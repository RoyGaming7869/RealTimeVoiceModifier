package com.real.time.voice.modifier

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
import com.real.time.voice.modifier.Utils.SharePrefsKey
import com.real.time.voice.modifier.Utils.SPHelper
import com.real.time.voice.modifier.databinding.ActivityLanguageBinding
import com.google.android.play.core.splitinstall.SplitInstallManagerFactory
import java.util.Locale


class SelectLanguageActivity : AppCompatActivity() {
    lateinit var binding: ActivityLanguageBinding
    var selectedlangugae=""
    var SPHelper: SPHelper?=null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding= ActivityLanguageBinding.inflate(layoutInflater)
        setContentView(binding.root)
        SPHelper= SPHelper(this)

        binding.backicon.setOnClickListener {
            finish()
        }
        selectedlangugae=SPHelper!!.getString(SharePrefsKey.langugae,"")
        if (selectedlangugae.equals("ar")){
            binding.arabiccheck.setImageResource(R.drawable.baseline_radio_button_checked_24)
        }
        else if (selectedlangugae.equals("es")){
            binding.spanishcheck.setImageResource(R.drawable.baseline_radio_button_checked_24)
        }
        else if (selectedlangugae.equals("fr")){
            binding.frenchcheck.setImageResource(R.drawable.baseline_radio_button_checked_24)
        }
        else if (selectedlangugae.equals("pt")){
            binding.portaguescheck.setImageResource(R.drawable.baseline_radio_button_checked_24)
        }
        else if (selectedlangugae.equals("hi")){
            binding.hindicheck.setImageResource(R.drawable.baseline_radio_button_checked_24)
        }
        else {
            binding.englishcheck.setImageResource(R.drawable.baseline_radio_button_checked_24)
        }

        binding.arabiclinear.setOnClickListener {
            selectedlangugae="ar"
            binding.arabiccheck.setImageResource(R.drawable.baseline_radio_button_checked_24)
            binding.englishcheck.setImageResource(R.drawable.baseline_radio_button_unchecked_24)
            binding.hindicheck.setImageResource(R.drawable.baseline_radio_button_unchecked_24)
            binding.portaguescheck.setImageResource(R.drawable.baseline_radio_button_unchecked_24)
            binding.frenchcheck.setImageResource(R.drawable.baseline_radio_button_unchecked_24)
            binding.spanishcheck.setImageResource(R.drawable.baseline_radio_button_unchecked_24)
        }


        binding.englishlinear.setOnClickListener {
            selectedlangugae="en"
            binding.arabiccheck.setImageResource(R.drawable.baseline_radio_button_unchecked_24)
            binding.englishcheck.setImageResource(R.drawable.baseline_radio_button_checked_24)
            binding.hindicheck.setImageResource(R.drawable.baseline_radio_button_unchecked_24)
            binding.portaguescheck.setImageResource(R.drawable.baseline_radio_button_unchecked_24)
            binding.frenchcheck.setImageResource(R.drawable.baseline_radio_button_unchecked_24)
            binding.spanishcheck.setImageResource(R.drawable.baseline_radio_button_unchecked_24)
        }


        binding.hindilinear.setOnClickListener {
            selectedlangugae="hi"
            binding.arabiccheck.setImageResource(R.drawable.baseline_radio_button_unchecked_24)
            binding.englishcheck.setImageResource(R.drawable.baseline_radio_button_unchecked_24)
            binding.hindicheck.setImageResource(R.drawable.baseline_radio_button_checked_24)
            binding.portaguescheck.setImageResource(R.drawable.baseline_radio_button_unchecked_24)
            binding.frenchcheck.setImageResource(R.drawable.baseline_radio_button_unchecked_24)
            binding.spanishcheck.setImageResource(R.drawable.baseline_radio_button_unchecked_24)
        }


        binding.portaguallinear.setOnClickListener {
            selectedlangugae="pt"
            binding.arabiccheck.setImageResource(R.drawable.baseline_radio_button_unchecked_24)
            binding.englishcheck.setImageResource(R.drawable.baseline_radio_button_unchecked_24)
            binding.hindicheck.setImageResource(R.drawable.baseline_radio_button_unchecked_24)
            binding.portaguescheck.setImageResource(R.drawable.baseline_radio_button_checked_24)
            binding.frenchcheck.setImageResource(R.drawable.baseline_radio_button_unchecked_24)
            binding.spanishcheck.setImageResource(R.drawable.baseline_radio_button_unchecked_24)
        }



        binding.frenchlinear.setOnClickListener {
            selectedlangugae="fr"
            binding.arabiccheck.setImageResource(R.drawable.baseline_radio_button_unchecked_24)
            binding.englishcheck.setImageResource(R.drawable.baseline_radio_button_unchecked_24)
            binding.hindicheck.setImageResource(R.drawable.baseline_radio_button_unchecked_24)
            binding.portaguescheck.setImageResource(R.drawable.baseline_radio_button_unchecked_24)
            binding.frenchcheck.setImageResource(R.drawable.baseline_radio_button_checked_24)
            binding.spanishcheck.setImageResource(R.drawable.baseline_radio_button_unchecked_24)
        }



        binding.spanishlinear.setOnClickListener {
            selectedlangugae="es"
            binding.arabiccheck.setImageResource(R.drawable.baseline_radio_button_unchecked_24)
            binding.englishcheck.setImageResource(R.drawable.baseline_radio_button_unchecked_24)
            binding.hindicheck.setImageResource(R.drawable.baseline_radio_button_unchecked_24)
            binding.portaguescheck.setImageResource(R.drawable.baseline_radio_button_unchecked_24)
            binding.frenchcheck.setImageResource(R.drawable.baseline_radio_button_unchecked_24)
            binding.spanishcheck.setImageResource(R.drawable.baseline_radio_button_checked_24)
        }


        binding.savecard.setOnClickListener {
            SPHelper!!.putString(SharePrefsKey.langugae,selectedlangugae)
            downloadSelectedLanguage(selectedlangugae)
            val appLocale: LocaleListCompat = LocaleListCompat.forLanguageTags(selectedlangugae)
            AppCompatDelegate.setApplicationLocales(appLocale)
//            startActivity(Intent(this,MainActivity::class.java))
//            finish()
        }


    }
    fun downloadSelectedLanguage(lan: String) {
        val splitInstallManager = SplitInstallManagerFactory.create(this)
        splitInstallManager.deferredLanguageInstall(listOf(Locale.forLanguageTag(lan)))
    }


}