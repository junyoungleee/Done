package com.palette.done.view.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.palette.done.view.signin.ObAlarmFragment
import com.palette.done.view.signin.ObNicknameFragment
import com.palette.done.view.signin.ObTypeFragment

class ViewPagerAdapter(fa: FragmentActivity, list: ArrayList<Fragment>) : FragmentStateAdapter(fa) {

    val fragmentList = list

    override fun getItemCount(): Int {
        return fragmentList.size
    }

    override fun createFragment(position: Int): Fragment {
        return fragmentList[position]
    }
}