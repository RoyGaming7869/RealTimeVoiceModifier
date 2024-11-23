package com.real.time.voice.modifier

import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity
import com.real.time.voice.modifier.Utils.SharePrefsKey
import com.real.time.voice.modifier.Utils.SPHelper
import com.real.time.voice.modifier.databinding.ActivitySplashBinding
import java.util.Locale


class SplashActivity : AppCompatActivity() {
    lateinit var binding: ActivitySplashBinding
    var SPHelper: SPHelper?=null
    var selectedlangugae=""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)
        SPHelper=  SPHelper(this)

        selectedlangugae=SPHelper!!.getString(SharePrefsKey.langugae,"")


        Handler().postDelayed(Runnable {
            startMain()
        },3000)

    }


    fun startMain(){
        if (!selectedlangugae.equals("")){
            setLocale(selectedlangugae)
        }
        if (SPHelper!!.getBoolean(SharePrefsKey.login,false)){
            startActivity(Intent(this,MainActivity::class.java))
        }
        else{
            startActivity(Intent(this,OnBoardingActivity::class.java))

        }
        finish()

    }

    fun setLocale(locale: String){
        val locale = Locale(locale)
        Locale.setDefault(locale)
        val config: Configuration = baseContext.resources.configuration
        config.locale = locale
        baseContext.resources.updateConfiguration(
            config,
            baseContext.resources.displayMetrics
        )
    }

}