package com.real.time.voice.modifier

//import com.bullhead.equalizer.EqualizerFragment
//import com.bullhead.equalizer.EqualizerFragment

import android.Manifest
import android.app.ActivityManager
import android.app.AlertDialog
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.content.pm.PackageManager
import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioManager
import android.media.AudioRecord
import android.media.AudioTrack
import android.media.MediaRecorder
import android.os.Bundle
import android.os.Handler
import android.os.IBinder
import android.util.Log
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.SeekBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.real.time.voice.modifier.Interface.MyServiceCallback
import com.real.time.voice.modifier.Service.MyAudioService
import com.chibde.visualizer.LineBarVisualizer


class MainActivity : AppCompatActivity(), MyServiceCallback {
    private val REQUEST_RECORD_AUDIO_PERMISSION_CODE = 200
    private var isPermissionToRecordAccepted = true
    private val audioPermissions = arrayOf(Manifest.permission.RECORD_AUDIO)
    private var isSettingsPanelOpen = false

    private var audioRecorder: AudioRecord? = null
    companion object{
        public var audioPlayer: AudioTrack? = null
    }
    private var shouldUseEarphoneMic = true  // Variable to track user's choice

    var isAudioRecording=false
    var balanceSeekBar: SeekBar?=null
    var volumeSeekBar: SeekBar?=null
    var equalizerContainer: FrameLayout?=null
    var equalizerIcon: ImageView?= null
    var micSwitchIcon: ImageView?= null
    var settingsIcon: ImageView?= null
    var isServiceStopped= false
    private var audioServiceInstance: MyAudioService? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
//        helloid=findViewById(R.id.helloid)
        equalizerContainer=findViewById(R.id.eqFrame)
        equalizerIcon=findViewById(R.id.equalizer_icon)
        micSwitchIcon=findViewById(R.id.switch_icon)
        settingsIcon=findViewById(R.id.setting_icon)
        isSettingsPanelOpen=true
        // Request runtime permissions
        ActivityCompat.requestPermissions(this, audioPermissions, REQUEST_RECORD_AUDIO_PERMISSION_CODE)

        if (isServiceRunning(MyAudioService::class.java)){
                micSwitchIcon!!.setImageResource(R.drawable.switch_on_ic)
            isAudioRecording=true
            var visualizer= findViewById<LineBarVisualizer>(R.id.visualizer)
            visualizer.setColor(ContextCompat.getColor(this,R.color.white))
            visualizer.setDensity(160f)
            visualizer.setPlayer(MyAudioService.audioTrack!!.audioSessionId)
            audioServiceInstance?.setCallback(this@MainActivity)
            isServiceBound=true
        }
        else{
            micSwitchIcon!!.setImageResource(R.drawable.switch_off_ic)
        }
      //  startAudio()
        balanceSeekBar=findViewById(R.id.balanceseek)
        volumeSeekBar=findViewById(R.id.volumeseek)

        settingsIcon!!.setOnClickListener {
            startActivity(Intent(this@MainActivity,SettingActivity::class.java))
        }

        micSwitchIcon!!.setOnClickListener {
            if (isAudioRecording){
                micSwitchIcon!!.setImageResource(R.drawable.switch_off_ic)
                Log.d("isServiceBoundonSwitch", "isServiceBound: "+isServiceBound)
                if (isServiceBound) {
                    Log.d("isServiceBoundonSwitch", "audioService null: "+(audioServiceInstance!=null).toString())
                    if (audioServiceInstance!=null) {
                        audioServiceInstance?.stopAudio()
                    }
                    else{
                        val stopServiceIntent = Intent(this, MyAudioService::class.java)
                        stopService(stopServiceIntent)
                        isAudioRecording = false
                        isServiceBound=false
                    }
                }
//                    stopAudioService()
            }
            else{
                if (isPermissionToRecordAccepted) {
                    if (checkConnectedAudioDevice()) {
                        startAudioService()
                        micSwitchIcon!!.setImageResource(R.drawable.switch_on_ic)
                    }
                    else{
                        showNoHeadphoneDialog()
                    }
                }
                else{
                    ActivityCompat.requestPermissions(this, audioPermissions, REQUEST_RECORD_AUDIO_PERMISSION_CODE)
                }
//                startAudio()
            }
        }

        balanceSeekBar!!.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener{
            override fun onProgressChanged(p0: SeekBar?, p1: Int, p2: Boolean) {
                setBalance(p1.toFloat())
            }

            override fun onStartTrackingTouch(p0: SeekBar?) {

            }

            override fun onStopTrackingTouch(p0: SeekBar?) {

            }

        })

        volumeSeekBar!!.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener{
            override fun onProgressChanged(p0: SeekBar?, p1: Int, p2: Boolean) {
                setVolume(p1.toFloat())
            }

            override fun onStartTrackingTouch(p0: SeekBar?) {

            }

            override fun onStopTrackingTouch(p0: SeekBar?) {

            }

        })


        equalizerIcon!!.setOnClickListener {
            if (isServiceRunning(MyAudioService::class.java)) {
                startActivity(Intent(this@MainActivity, EqualizeAudioActivity::class.java))
            }
            else{
                Toast.makeText(this@MainActivity,
                    getString(R.string.microphone_is_off), Toast.LENGTH_SHORT).show()
            }
//            eqFrame!!.visibility=View.VISIBLE
//            val sessionId= audioTrack?.audioSessionId
//            val fragment = DialogEqualizerFragment.newBuilder()
//                .setAudioSessionId(sessionId!!)
//                .themeColor(ContextCompat.getColor(this,R.color.setting_yellow))
//                .textColor(ContextCompat.getColor(this, R.color.white))
//                .accentAlpha(ContextCompat.getColor(this, R.color.setting_yellow))
//                .darkColor(ContextCompat.getColor(this, R.color.mainbg))
//                .setAccentColor(ContextCompat.getColor(this, R.color.mainbg))
//                .build()
//            fragment.show(supportFragmentManager, "eq")
        }

    }

    private fun showNoHeadphoneDialog() {
        val builder= AlertDialog.Builder(this)
        val inflater= layoutInflater.inflate(R.layout.no_headphone_layout,null)
        val canceltxt= inflater.findViewById<TextView>(R.id.canceltxt)
        val retrycard= inflater.findViewById<CardView>(R.id.retrycard)
        builder.setView(inflater)
        val dialog= builder.create()
        dialog.window!!.setBackgroundDrawableResource(android.R.color.transparent)
        canceltxt.setOnClickListener {
            dialog.dismiss()
        }

        retrycard.setOnClickListener {
            if (checkConnectedAudioDevice()){
                dialog.dismiss()
                startAudioService()
                micSwitchIcon!!.setImageResource(R.drawable.switch_on_ic)
            }
            else{
                Toast.makeText(this@MainActivity,
                    getString(R.string.not_connected_yet), Toast.LENGTH_SHORT).show()
            }
        }

        dialog.show()
    }

    private fun setVolume(volume: Float) {
        audioPlayer?.setVolume(volume)
    }

    // Function to adjust the balance (left/right audio channel ratio)
    private fun setBalance(balance: Float) {
        val pan = if (balance > 0) 1.0f - balance else 1.0f
        val volumeLeft = 1.0f
        val volumeRight = if (balance > 0) 1.0f else 1.0f + balance

        audioPlayer?.setStereoVolume(volumeLeft * pan, volumeRight * (1.0f - pan))

    }
    private fun startAudio() {
        if (isPermissionToRecordAccepted) {
            val bufferSize =
                AudioRecord.getMinBufferSize(44100, AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT)

            if (ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.RECORD_AUDIO
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                return
            }

            val audioSource = if (shouldUseEarphoneMic) {
                MediaRecorder.AudioSource.MIC
            } else {
                MediaRecorder.AudioSource.MIC
            }
                audioRecorder = AudioRecord(
                    audioSource,
                    44100,
                    AudioFormat.CHANNEL_IN_MONO,
                    AudioFormat.ENCODING_PCM_16BIT,
                    bufferSize
                )

                audioPlayer = AudioTrack.Builder()
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

                audioRecorder?.startRecording()
                audioPlayer?.play()

//                isRecording = true

                // Start a thread to read and write audio in real-time
//                Thread {
//                    val buffer = ByteArray(bufferSize)
//                    while (isRecording) {
//                        val bytesRead = audioRecord?.read(buffer, 0, bufferSize) ?: 0
//                        if (bytesRead > 0) {
//                            audioTrack?.write(buffer, 0, bytesRead)
//                        }
//                    }
//                }.start()




        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        when (requestCode) {
            REQUEST_RECORD_AUDIO_PERMISSION_CODE -> {
                if (grantResults!=null && grantResults.size>0) {
                    isPermissionToRecordAccepted =
                        grantResults[0] == PackageManager.PERMISSION_GRANTED
                }
            }

        }

        if (!isPermissionToRecordAccepted) finish()
        else{
            // Check if the notification permission is granted
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                != PackageManager.PERMISSION_GRANTED) {
                // Permission is not granted, request it
                ActivityCompat.requestPermissions(this,
                    arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                    11);
            } else {

            }

        }
    }


    private fun stopAudio() {
//        isRecording = false
        audioRecorder?.stop()
        audioRecorder?.release()
        audioPlayer?.stop()
        audioPlayer?.release()
    }

    private fun startAudioService() {
        isServiceStopped=false
        if (isServiceRunning(MyAudioService::class.java)){
            Log.d("Track_Service", "startAudioService: Service already running trying to start audio")
            if (isServiceBound) {
                audioServiceInstance?.startAudio()
                Log.d("Track_Service", "startAudioService: isService bound true")
            }
            else{
                Log.d("Track_Service", "startAudioService: Service bound false")
                isAudioRecording=true
                val serviceIntent = Intent(this, MyAudioService::class.java)
                bindService(serviceIntent, serviceConnection, Context.BIND_AUTO_CREATE)

                ContextCompat.startForegroundService(this, serviceIntent)

            }
        }
        else{
            Log.d("Track_Service", "startAudioService: Service not in running")

            if (!isServiceBound) {
                Log.d("Track_Service", "startAudioService: Service bound false")
                isAudioRecording=true
                val serviceIntent = Intent(this, MyAudioService::class.java)
                bindService(serviceIntent, serviceConnection, Context.BIND_AUTO_CREATE)

                ContextCompat.startForegroundService(this, serviceIntent)

            }
        }


        Handler().postDelayed(Runnable { isServiceStopped=true },3000)
    }

    private var isServiceBound = false
    private val serviceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            val binder = service as MyAudioService.LocalBinder
            audioServiceInstance = binder.getService()
            audioServiceInstance?.setCallback(this@MainActivity)
            // Update isServiceBound flag
            isServiceBound = true
        }
        override fun onServiceDisconnected(name: ComponentName?) {
            // Handle service disconnection if needed
            isServiceBound = false
        }
    }
    private fun stopAudioService() {
        if (isServiceStopped) {
            if (isServiceBound) {
                // Unbind the service
                unbindService(serviceConnection)
                isServiceBound = false

                // Delay before stopping the service
                Handler().postDelayed({
                    // Stop the service only if it is still bound
                    if (!isServiceBound) {
                        val stopServiceIntent = Intent(this, MyAudioService::class.java)
                        stopService(stopServiceIntent)
                        isAudioRecording = false
                    }
                }, 1000) // Adjust the delay as needed
            }
        }
    }


    /* private fun stopAudioService() {
         isRecording=false
         val serviceIntent = Intent(this, AudioService::class.java)
         stopService(serviceIntent)
     }*/


    private fun isServiceRunning(serviceClass: Class<*>): Boolean {
        val manager = getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        for (service in manager.getRunningServices(Int.MAX_VALUE)) {
            if (serviceClass.name == service.service.className) {
                return true
            }
        }
        return false
    }

    override fun onRecordingStarted() {
        var visualizer= findViewById<LineBarVisualizer>(R.id.visualizer)
        visualizer.setColor(ContextCompat.getColor(this,R.color.white))
        visualizer.setDensity(160f)
        visualizer.setPlayer(MyAudioService.audioTrack!!.audioSessionId)

    }

    override fun onRecordingStopped() {
        if (isSettingsPanelOpen) {
            if (isServiceBound) {

                isServiceBound = false
            }
            isAudioRecording = false
            micSwitchIcon!!.setImageResource(R.drawable.switch_off_ic)
        }
    }

    private fun checkConnectedAudioDevice(): Boolean {
        var isconnected= false
        val audioManager = getSystemService(Context.AUDIO_SERVICE) as AudioManager

        // Check if a wired headset is connected
        val isWiredHeadsetConnected = audioManager.isWiredHeadsetOn

        // Check if a Bluetooth device is connected
        val isBluetoothConnected = audioManager.isBluetoothA2dpOn

        if (isWiredHeadsetConnected) {
            // Wired headset is connected
            isconnected=true
        }

        if (isBluetoothConnected) {
            // Bluetooth device is connected
            isconnected=true
        }
        return isconnected
    }

    override fun onDestroy() {
        super.onDestroy()
        isSettingsPanelOpen=false
    }

    override fun onResume() {
        super.onResume()

    }
}