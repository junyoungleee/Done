package com.palette.done.view.signin

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.palette.done.databinding.ActivityOnBoardingBinding
import com.palette.done.view.adapter.ViewPagerAdapter

class OnBoardingActivity : AppCompatActivity() {

    private lateinit var binding: ActivityOnBoardingBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOnBoardingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setViewPager()
    }

    private fun setViewPager() {
        val fragments = arrayListOf<Fragment>(ObNicknameFragment(), ObTypeFragment(), ObAlarmFragment())
        val adapter = ViewPagerAdapter(this, fragments)
        binding.viewPagerOnBoarding.adapter = adapter
        binding.viewPagerOnBoarding.isUserInputEnabled = false  // swipe action 제거
    }

}