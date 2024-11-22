package com.real.time.voice.modifier.Adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.real.time.voice.modifier.Fragments.Intro1Fragment
import com.real.time.voice.modifier.Fragments.Intro2Fragment
import com.real.time.voice.modifier.Fragments.Intro3Fragment

class SliderAdapter(fm: FragmentManager) : FragmentPagerAdapter(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {
    override fun getItem(position: Int): Fragment {
        return when (position) {
            0 -> Intro1Fragment()
            1 -> Intro2Fragment()
            2 -> Intro3Fragment()
            else -> throw IllegalArgumentException("Invalid position")
        }
    }

    override fun getCount(): Int {
        return 3
    }
}
