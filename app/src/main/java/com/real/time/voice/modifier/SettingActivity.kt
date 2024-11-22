package com.real.time.voice.modifier

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.real.time.voice.modifier.databinding.ActivitySettingBinding

class SettingActivity : AppCompatActivity() {
    lateinit var binding: ActivitySettingBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivitySettingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.languagelinear.setOnClickListener {
            startActivity(Intent(this,LanguageActivity::class.java))
        }

        binding.sharelinear.setOnClickListener {
            shareApp()
        }

        binding.ratelinear.setOnClickListener {
            rateApp()
        }

        binding.morelinear.setOnClickListener {
            moreApps()
        }

        binding.backrelative.setOnClickListener {
            finish()
        }

        binding.privacylinear.setOnClickListener {
            startActivity(Intent(this@SettingActivity,PrivacyPolicyActivity::class.java))
        }

    }

    private fun shareApp() {
        val shareIntent = Intent().apply {
            action = Intent.ACTION_SEND
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, "Check out this amazing app!")
        }
        startActivity(Intent.createChooser(shareIntent, "Share via"))
    }

    private fun rateApp() {
        try {
            startActivity(
                Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse("market://details?id=$packageName")
                )
            )
        } catch (e: android.content.ActivityNotFoundException) {
            startActivity(
                Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse("https://play.google.com/store/apps/details?id=$packageName")
                )
            )
        }
    }

    private fun moreApps() {
        startActivity(
            Intent(
                Intent.ACTION_VIEW,
                Uri.parse("https://play.google.com/store/apps/developer?id=Yiligisun")
            )
        )
    }
}