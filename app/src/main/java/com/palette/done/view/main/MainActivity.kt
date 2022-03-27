package com.palette.done.view.main

import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.DisplayMetrics
import android.util.Log
import android.view.View
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.startActivity
import androidx.core.view.doOnPreDraw
import androidx.core.view.isVisible
import androidx.recyclerview.widget.SimpleItemAnimator
import com.kizitonwose.calendarview.model.CalendarDay
import com.kizitonwose.calendarview.model.DayOwner
import com.kizitonwose.calendarview.ui.DayBinder
import com.kizitonwose.calendarview.ui.ViewContainer
import com.kizitonwose.calendarview.utils.Size
import com.kizitonwose.calendarview.utils.next
import com.kizitonwose.calendarview.utils.previous
import com.palette.done.DoneApplication
import com.palette.done.R
import com.palette.done.data.db.entity.DoneCount
import com.palette.done.data.remote.repository.DoneServerRepository
import com.palette.done.data.remote.repository.StickerServerRepository
import com.palette.done.databinding.ActivityMainBinding
import com.palette.done.databinding.CalendarDayLayoutBinding
import com.palette.done.view.decoration.DoneToast
import com.palette.done.view.my.MyActivity
import com.palette.done.view.my.sticker.StickerActivity
import com.palette.done.view.my.sticker.StickerInfoDialog
import com.palette.done.view.util.Util
import com.palette.done.viewmodel.*
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
    private lateinit var doneCountList: Map<String, Int>

    private val mainVM: MainViewModel by viewModels() {
        MainViewModelFactory(DoneServerRepository(), (application as DoneApplication).doneRepository)
    }
    private val stickerVM: StickerViewModel by viewModels() {
        StickerViewModelFactory(StickerServerRepository(), (application as DoneApplication).stickerRepository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        mainVM.doneCountList.observe(this) {
            doneCountList = mainVM.getDoneCountMap()
            setCalendarView()
        }
        setCalendarSubHeader()
        stickerVM.getAllSticker()

        setFirstLoginDialog()

        setButtonsDestination()
    }

    private fun setButtonsDestination() {
        binding.btnMy.setOnClickListener {
            val intent = Intent(this, MyActivity::class.java)
            startActivity(intent)
        }
        binding.btnSticker.setOnClickListener {
            val intent = Intent(this, StickerActivity::class.java)
            startActivity(intent)
        }
    }

    private fun setFirstLoginDialog() {
        // pref의 TodayFirst가 어제 날짜인 경우, 띄우기
        val visit = DoneApplication.pref.todayFirst  // 최근 방문 날짜
        val today = LocalDate.now().toString()  // 오늘 날짜

        if (visit != today && visit != "") {
            // 아예 앱을 처음 실행한 경우가 아니며 하루 중 첫 진입인 경우
            val dialog = FirstVisitDialog()
            dialog.show(this.supportFragmentManager, "FirstLoginDialog")
            DoneApplication.pref.todayFirst = today // 하루동안 안보이게 처리
        }
    }

    private fun setCalendarView() {
        setCalendarHeader()
        with(binding.calendarView) {
            itemAnimator = null  // 깜빡임 제거
            doOnPreDraw {
                daySize = Size(binding.calendarView.width/7, util.dpToPx(84))
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
                    // 날짜 클릭 이벤트
                    val intent = Intent(this@MainActivity, DoneActivity::class.java)
                    container.view.setOnClickListener {
                        val clickedDate = "${day.date.year}-${df.format(day.date.monthValue)}-${df.format(day.date.dayOfMonth)}"
                        intent.putExtra("clickedDate", clickedDate)
                        startActivity(intent)
                    }

                    // 날짜 설정
                    container.date.text = day.date.dayOfMonth.toString()

                    // 던리스트 & 오늘 설정
                    if (day.owner != DayOwner.THIS_MONTH) {
                        // 현재 날짜가 이번 달의 날짜가 아닌 경우
                        container.date.setTextColor(ContextCompat.getColor(context, R.color.calendarHiddenColor))
                        with(container.doneIcon) {
                            backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(context, R.color.calendarHiddenColor))
                            if (doneCountList.containsKey(day.date.toString())) {
                                visibility = View.VISIBLE
                                text = "${doneCountList[day.date.toString()]}"
                            } else {
                                visibility = View.INVISIBLE
                            }
                        }
                    } else {
                        // 이번 달의 날짜인 경우
                        // '오늘' 체크
                        when (day.date) {
                            today -> {
                                container.date.typeface = resources.getFont(R.font.spoqa_han_sans_neo_bold)
                                container.date.setTextColor(Color.BLACK)
                                container.today.visibility = View.VISIBLE
                            }
                            else -> {
                                container.date.typeface = resources.getFont(R.font.spoqa_han_sans_neo_regular)
                                container.date.setTextColor(ContextCompat.getColor(context, R.color.calendarColor))
                                container.today.visibility = View.INVISIBLE
                                if (day.date.isAfter(today)) {
                                    container.view.setOnClickListener {
                                        DoneToast.createToast(this@MainActivity, getString(R.string.toast_after_today), "")?.show()
                                    }
                                }
                            }
                        }
                        // 던아이콘 세팅
                        with(container.doneIcon) {
                            when (doneCountList.containsKey(day.date.toString())) {
                                true -> {
                                    visibility = View.VISIBLE
                                    text = doneCountList[day.date.toString()].toString()
                                    backgroundTintList = when(doneCountList[day.date.toString()]) {
                                        in(1..5) -> { ColorStateList.valueOf(ContextCompat.getColor(context, R.color.doneYellowColor)) }
                                        in(6..10) -> { ColorStateList.valueOf(ContextCompat.getColor(context, R.color.doneOrangeColor)) }
                                        else -> { ColorStateList.valueOf(ContextCompat.getColor(context, R.color.doneDarkOrangeColor)) }
                                    }
                                }
                                false -> {
                                    if (day.date != today) {
                                        intent.putExtra("is_empty", true)
                                    }
                                    visibility = View.INVISIBLE
                                }
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
                binding.tvCalendarMonth.text = getString(R.string.main_year_month, year, df.format(month.yearMonth.monthValue))
                // 현재 yyyy-MM 값
                mainVM._yearMonth.value = "${month.yearMonth.year}-${df.format(month.yearMonth.monthValue)}"
            }
        }
    }

    class DayViewContainer(view: View) : ViewContainer(view) {
        var date = CalendarDayLayoutBinding.bind(view).calendarDayText
        var today = CalendarDayLayoutBinding.bind(view).ivTodayDot
        var doneIcon = CalendarDayLayoutBinding.bind(view).doneIcon
    }

    private fun setCalendarSubHeader() {
        // 이번달 던리스트 개수
        mainVM.doneCount.observe(this) { cnt ->
            binding.tvDoneListCount.text = "$cnt"
        }
        // 랜덤 응원메세지
        val messages = resources.getStringArray(R.array.main_message)
        val idx = Random().nextInt(messages.size)
        binding.tvCheerUpMsg.text = messages[idx]
    }
}