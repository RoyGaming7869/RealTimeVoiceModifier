package com.real.time.voice.modifier.Adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.real.time.voice.modifier.Fragments.OnBoard1Fragment
import com.real.time.voice.modifier.Fragments.OnBoard2Fragment
import com.real.time.voice.modifier.Fragments.OnBoard3Fragment

class OnBoardingAdapter(fm: FragmentManager) : FragmentPagerAdapter(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {
    override fun getItem(position: Int): Fragment {
        return when (position) {
            0 -> OnBoard1Fragment()
            1 -> OnBoard2Fragment()
            2 -> OnBoard3Fragment()
            else -> throw IllegalArgumentException("Invalid position")
        }
    }

    override fun getCount(): Int {
        return 3
    }
}
