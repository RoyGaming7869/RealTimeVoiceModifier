package com.real.time.voice.modifier.Service

import android.Manifest
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioManager
import android.media.AudioRecord
import android.media.AudioTrack
import android.media.MediaRecorder
import android.os.Binder
import android.os.Build
import android.os.Handler
import android.os.IBinder
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import com.real.time.voice.modifier.Interface.MyServiceCallback
import com.real.time.voice.modifier.MainActivity
import com.real.time.voice.modifier.R

class MyAudioService: Service() {
    private val binder: IBinder = LocalBinder()
    companion object{
        private var isRecording = false
        public var audioTrack: AudioTrack? = null
    }
    private var audioRecord: AudioRecord? = null
    private var callback: MyServiceCallback? = null

    inner class LocalBinder : Binder() {
        fun getService(): MyAudioService {
            return this@MyAudioService
        }
    }

    private var audioReceiver: AudioReceiver? = null

    override fun onCreate() {
        super.onCreate()
        audioReceiver = AudioReceiver()
        val filter = IntentFilter()
        filter.addAction(AudioManager.ACTION_AUDIO_BECOMING_NOISY)
        registerReceiver(audioReceiver, filter)
    }

    override fun onBind(intent: Intent): IBinder {
        return binder
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        startForeground(1, createNotification())
        Handler().postDelayed(Runnable { startAudio() },2000)
        return START_STICKY
    }

    private fun createNotification(): Notification {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channelId = "channel_id"
            val channelName = "Channel Name"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(channelId, channelName, importance)

            val notificationManager = getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(channel)
        }

        val notificationIntent = Intent(this, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            this, 0, notificationIntent, PendingIntent.FLAG_IMMUTABLE
        )

        return NotificationCompat.Builder(this, "channel_id")
            .setContentTitle("AudioService")
            .setContentText("Keeping audio active")
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentIntent(pendingIntent)
            .build()
    }


    fun startAudio() {
//        if (permissionToRecordAccepted) {
            val bufferSize =
                AudioRecord.getMinBufferSize(44100, AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT)

            if (ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.RECORD_AUDIO
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                return
            }

            val audioSource = MediaRecorder.AudioSource.MIC

        audioRecord = AudioRecord(
            audioSource,
            44100,
            AudioFormat.CHANNEL_IN_MONO,
            AudioFormat.ENCODING_PCM_16BIT,
            bufferSize
        )

        audioTrack = AudioTrack.Builder()
            .setAudioAttributes(
                AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_MEDIA)
                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                    .build()
            )
            .setAudioFormat(
                AudioFormat.Builder()
                    .setSampleRate(44100)
                    .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                    .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                    .build()
            )
            .setBufferSizeInBytes(bufferSize)
            .build()

        if (audioRecord?.state != AudioRecord.STATE_INITIALIZED || audioTrack?.state != AudioTrack.STATE_INITIALIZED) {
            // Handle initialization error
            return
        }

        audioRecord?.startRecording()
            audioTrack?.play()

            isRecording = true

        // Start a thread to read and write audio in real-time
        Thread {
            val buffer = ByteArray(bufferSize)
            while (isRecording) {
                val bytesRead = audioRecord?.read(buffer, 0, bufferSize) ?: 0
                if (bytesRead > 0) {
                    synchronized(audioTrack!!) {
                        audioTrack?.write(buffer, 0, bytesRead)
                    }
                }
            }
        }.start()

        if (callback!=null) {
            callback!!.onRecordingStarted()
        }


//        }
    }
    fun stopAudio() {
        isRecording = false

        if (audioRecord != null && audioRecord?.state == AudioRecord.STATE_INITIALIZED) {
            Log.e("AudioService", "AudioRecord properly initialized and not released")

            audioRecord?.stop()
            audioRecord?.release()
        } else {
            Log.e("AudioService", "AudioRecord not properly initialized or already released")
        }

        if (audioTrack != null && audioTrack?.state == AudioTrack.STATE_INITIALIZED) {
            Log.e("AudioService", "AudioTrack properly initialized and not released")

            audioTrack?.stop()
            audioTrack?.release()
        } else {
            Log.e("AudioService", "AudioTrack not properly initialized or already released")
        }

        Log.e("AudioService", "Stop forground")

        stopForeground(true)

        callback?.onRecordingStopped()
    }


    // ... other methods

    override fun onDestroy() {
        super.onDestroy()
        // Stop audio processing and release resources
        stopAudio()
        stopSelf()
        audioReceiver?.let {
            unregisterReceiver(it)
            audioReceiver = null
        }
    }


    fun setCallback(callback: MyServiceCallback) {
        this.callback = callback
    }


    inner class AudioReceiver : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            if (intent?.action == AudioManager.ACTION_AUDIO_BECOMING_NOISY) {
                stopAudio()
                stopSelf()
                // Headphones disconnected
//                stopService(Intent(context, AudioService::class.java))
            }
        }
    }
}