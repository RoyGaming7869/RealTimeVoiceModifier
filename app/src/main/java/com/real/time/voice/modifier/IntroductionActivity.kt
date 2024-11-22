package com.real.time.voice.modifier

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.viewpager.widget.ViewPager
import com.real.time.voice.modifier.Adapters.SliderAdapter
import com.real.time.voice.modifier.Utils.SharePrefsKey
import com.real.time.voice.modifier.Utils.SharedPreferencesHelper
import com.real.time.voice.modifier.databinding.ActivityIntroductionBinding
import com.zhpan.indicator.enums.IndicatorSlideMode
import com.zhpan.indicator.enums.IndicatorStyle

class IntroductionActivity : AppCompatActivity() {
    lateinit var binding: ActivityIntroductionBinding
    var sharedPreferencesHelper: SharedPreferencesHelper?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityIntroductionBinding.inflate(layoutInflater)
        setContentView(binding.root)
        sharedPreferencesHelper=  SharedPreferencesHelper(this)
        val adapter = SliderAdapter(supportFragmentManager)
        binding.viewpager.adapter = adapter

        binding.skiptxt.setOnClickListener {
            startActivity(Intent(this,MainActivity::class.java))
            finish()
        }

        binding.nexttxt.setOnClickListener {
            if (binding.viewpager.currentItem==2){
                sharedPreferencesHelper!!.putBoolean(SharePrefsKey.login,true)
                startActivity(Intent(this,MainActivity::class.java))
                finish()
            }
            else{
                binding.viewpager.setCurrentItem(binding.viewpager.currentItem+1)
            }

        }

        binding.indicatorView.apply {
            setSliderColor(getColor(R.color.white), getColor(R.color.setting_yellow))
            setSliderWidth(15f)
            setSliderHeight(10f)
            setSlideMode(IndicatorSlideMode.WORM)
            setIndicatorStyle(IndicatorStyle.ROUND_RECT)
            setPageSize(3)
            notifyDataChanged()
        }

        binding.viewpager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener{
            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {
                binding.indicatorView.onPageScrolled(position, positionOffset, positionOffsetPixels)
                if (position==2){
                    binding.nexttxt.text= getString(R.string.get_started)
                }
                else{
                    binding.nexttxt.text= getString(R.string.next)
                }
            }

            override fun onPageSelected(position: Int) {
                binding.indicatorView.onPageSelected(position)

            }

            override fun onPageScrollStateChanged(state: Int) {

            }
        })

    }
}