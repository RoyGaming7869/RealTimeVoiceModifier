package com.real.time.voice.modifier

import android.media.MediaPlayer
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.real.time.voice.modifier.Service.MyAudioService
import com.bullhead.equalizer.EqualizerFragment

//import com.bullhead.equalizer.EqualizerFragment

class EqualizeAudioActivity : AppCompatActivity() {
    private var mediaPlayer: MediaPlayer? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_equalizer)
//
//        // Initialize MediaPlayer with the audio file from the raw folder
//        mediaPlayer = MediaPlayer.create(this, R.raw.samplemusin)
//
//        // If you need to handle completion or errors, set listeners
//        mediaPlayer?.setOnCompletionListener {
//            // Handle completion
//        }
//
//        mediaPlayer?.setOnErrorListener { mp, what, extra ->
//            // Handle errors
//            false
//        }
//        mediaPlayer?.setLooping(true);
//
////        mediaPlayer?.prepare()
//       //t  mediaPlayer?.start()
//
//        val sessionId = MainActivity.audioTrack!!.getAudioSessionId()
//
//        val equalizerFragment= EqualizerFragment.Builder()
//            .setAccentColor(Color.parseColor("#4caf50"))
//            .setAudioSessionId(sessionId)
//            .build()
//
//        supportFragmentManager.beginTransaction().replace(R.id.eqFrame,equalizerFragment).commit()

        val sessionId = MyAudioService.audioTrack!!.getAudioSessionId()

        val equalizerFragment= EqualizerFragment.Builder()
            .setAccentColor(ContextCompat.getColor(this,R.color.seek_main))
            .setAudioSessionId(sessionId)
            .build()
        supportFragmentManager.beginTransaction().replace(R.id.eqFrame,equalizerFragment).commit()



    }
}