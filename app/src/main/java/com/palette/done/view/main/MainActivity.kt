package com.palette.done.view.main

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.applandeo.materialcalendarview.EventDay
import com.applandeo.materialcalendarview.listeners.OnDayClickListener
import com.palette.done.R
import com.palette.done.databinding.ActivityMainBinding
import java.lang.StringBuilder
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setCalendarView()
        setCalendarClickEvent()
    }

    private fun setCalendarView() {
        with(binding.calendarView) {
            setHeaderTypeface(resources.getFont(R.font.spoqa_han_sans_neo_bold))
            setTodayTypeface(resources.getFont(R.font.spoqa_han_sans_neo_bold))
            setTypeface(resources.getFont(R.font.spoqa_han_sans_neo_regular))
        }
    }

    private fun setCalendarClickEvent() {
        binding.calendarView.setOnDayClickListener(object : OnDayClickListener {
            override fun onDayClick(eventDay: EventDay) {
                var clickedDay = eventDay.calendar.time
                val df = SimpleDateFormat("yyyy-MM-dd")
                val dateStr = df.format(clickedDay)
                Log.d("clicked_day", "$dateStr")

                var intent = Intent(this@MainActivity, DoneActivity::class.java)
                intent.putExtra("clickedDate", dateStr)
                startActivity(intent)
            }
        })
    }
}