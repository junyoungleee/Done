package com.palette.done.view.main.done

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.tabs.TabLayoutMediator
import com.palette.done.databinding.FragmentDoneRoutineBinding
import com.palette.done.databinding.FragmentDoneTagBinding
import com.palette.done.view.adapter.ViewPagerAdapter

class DoneTagFragment : Fragment() {

    private var _binding: FragmentDoneTagBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FragmentDoneTagBinding.inflate(inflater, container, false)

        setTabBarLayout()

        return binding.root
    }

    private fun setTabBarLayout() {
        val tabTitle = arrayOf("해시태그", "루틴")
        val fragments = arrayListOf(DoneHashtagFragment(), DoneRoutineFragment())

        binding.vpTabBarLayout.adapter = ViewPagerAdapter(requireActivity(), fragments)
        TabLayoutMediator(binding.tabBar, binding.vpTabBarLayout) { tab, position ->
            tab.text = tabTitle[position]
        }.attach()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}