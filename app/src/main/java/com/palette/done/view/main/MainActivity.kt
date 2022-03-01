package com.palette.done.view.main

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.palette.done.R
import com.palette.done.databinding.ActivityMainBinding
import java.lang.StringBuilder

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setCalendarView()
    }

    private fun setCalendarView() {
        with(binding.calendarView) {
            setHeaderTypeface(resources.getFont(R.font.spoqa_han_sans_neo_bold))
            setTodayTypeface(resources.getFont(R.font.spoqa_han_sans_neo_bold))
            setTypeface(resources.getFont(R.font.spoqa_han_sans_neo_regular))
        }
    }
}