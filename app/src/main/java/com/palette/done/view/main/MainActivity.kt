package com.palette.done.view.main

import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.DisplayMetrics
import android.util.Log
import android.view.View
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.startActivity
import androidx.core.view.doOnPreDraw
import com.kizitonwose.calendarview.model.CalendarDay
import com.kizitonwose.calendarview.model.DayOwner
import com.kizitonwose.calendarview.ui.DayBinder
import com.kizitonwose.calendarview.ui.ViewContainer
import com.kizitonwose.calendarview.utils.Size
import com.kizitonwose.calendarview.utils.next
import com.kizitonwose.calendarview.utils.previous
import com.palette.done.R
import com.palette.done.databinding.ActivityMainBinding
import com.palette.done.databinding.CalendarDayLayoutBinding
import com.palette.done.view.util.Util
import java.text.DecimalFormat
import java.time.LocalDate
import java.time.YearMonth
import java.time.temporal.WeekFields
import java.util.*

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val util = Util()
    private val df = DecimalFormat("00")

    private var today = LocalDate.now()
    private var doneDates = mutableSetOf<LocalDate>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        setCalendarView()
    }

    private fun setCalendarView() {
        setCalendarHeader()
        with(binding.calendarView) {
            doOnPreDraw {
                daySize = Size(binding.calendarView.width/7, util.dpToPx(86))
            }

            val currentMonth = YearMonth.now()
            val firstMonth = currentMonth.minusMonths(240)
            val lastMonth = currentMonth.plusMonths(240)
            val firstDayOfWeek = WeekFields.of(Locale.getDefault()).firstDayOfWeek
            setup(firstMonth, lastMonth, firstDayOfWeek)
            scrollToMonth(currentMonth)

            dayBinder = object : DayBinder<DayViewContainer> {
                override fun create(view: View) = DayViewContainer(view)
                override fun bind(container: DayViewContainer, day: CalendarDay) {
                    container.view.setOnClickListener {
                        val clickedDate = "${day.date.year}-${df.format(day.date.monthValue)}-${df.format(day.date.dayOfMonth)}"
                        val intent = Intent(this@MainActivity, DoneActivity::class.java)
                        intent.putExtra("clickedDate", clickedDate)
                        startActivity(intent)
                    }
                    container.date.text = day.date.dayOfMonth.toString()
                    if (day.owner != DayOwner.THIS_MONTH) {
                        // 현재 날짜가 이번 달의 날짜가 아닌 경우
                        container.date.setTextColor(ContextCompat.getColor(context, R.color.calendarHiddenColor))
                        container.doneIcon.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(context, R.color.calendarHiddenColor))
                    } else {
                        when {
                            today == day.date -> {
                                // 오늘
                                Log.d("today", "$today")
                                container.date.typeface = resources.getFont(R.font.spoqa_han_sans_neo_bold)
                                container.date.setTextColor(Color.BLACK)
                                container.today.setBackgroundResource(R.drawable.ic_calendar_dot)
                            }
                            else -> {
                                container.date.typeface = resources.getFont(R.font.spoqa_han_sans_neo_regular)
                                container.date.setTextColor(ContextCompat.getColor(context, R.color.calendarColor))
                            }
                        }
                    }
                }
            }
        }
    }

    private fun setCalendarHeader() {
        with(binding) {
            btnPreviousMonth.setOnClickListener {
                binding.calendarView.findFirstVisibleMonth()?.let {
                    binding.calendarView.smoothScrollToMonth(it.yearMonth.previous)
                }
            }
            btnNextMonth.setOnClickListener {
                binding.calendarView.findFirstVisibleMonth()?.let {
                    binding.calendarView.smoothScrollToMonth(it.yearMonth.next)
                }
            }
            calendarView.monthScrollListener = { month ->
                val year= month.yearMonth.year.toString().substring(2)
                binding.tvCalendarMonth.text = "${year}.${df.format(month.yearMonth.monthValue)}"
            }
        }
    }

    class DayViewContainer(view: View) : ViewContainer(view) {
        var date = CalendarDayLayoutBinding.bind(view).calendarDayText
        var today = CalendarDayLayoutBinding.bind(view).ivTodayDot
        var doneIcon = CalendarDayLayoutBinding.bind(view).doneIcon
    }
}