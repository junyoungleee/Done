package com.palette.done.view.main

import android.content.Context
import android.content.Intent
import android.graphics.Rect
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.MotionEvent
import android.view.View
import android.view.ViewTreeObserver
import android.view.inputmethod.InputMethodManager
import android.widget.ScrollView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.palette.done.DoneApplication
import com.palette.done.R
import com.palette.done.data.db.entity.Done
import com.palette.done.data.remote.repository.DoneServerRepository
import com.palette.done.databinding.ActivityDoneBinding
import com.palette.done.view.adapter.DoneAdapter
import com.palette.done.view.decoration.DoneToast
import com.palette.done.view.main.done.DoneFragment
import com.palette.done.view.main.today.TodayRecordActivity
import com.palette.done.view.util.Util
import com.palette.done.viewmodel.*
import com.skydoves.powermenu.MenuAnimation
import com.skydoves.powermenu.PowerMenu
import com.skydoves.powermenu.PowerMenuItem
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.util.*


class DoneActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDoneBinding
    private val dateVM: DoneDateViewModel by viewModels() {
        DoneDateViewModelFactory((application as DoneApplication).doneRepository)
    }
    private val doneVM: DoneEditViewModel by viewModels() {
        DoneEditViewModelFactory(DoneServerRepository(), DoneApplication().doneRepository)
    }
    private val planVM: PlanViewModel by viewModels() {
        PlanViewModelFactory(DoneServerRepository(), DoneApplication().doneRepository)
    }
    private val routineVM: RoutineViewModel by viewModels() {
        RoutineViewModelFactory(DoneServerRepository(), DoneApplication().doneRepository)
    }
    private val categoryVM: CategoryViewModel by viewModels() {
        CategoryViewModelFactory(DoneApplication().doneRepository)
    }

    val PREMIUM_DONELIST_SIZE = 50
    val NORMAL_DONELIST_SIZE = 8

    private var doneAdapter = DoneAdapter(this)
    private lateinit var popup: PowerMenu

    private var rootHeight: Int = -1

    private val util = Util()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDoneBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setKeyboardHeight()

        val clickedDate = intent.getStringExtra("clickedDate")
        Log.d("date_clicked", "${clickedDate}")

        dateVM.setTitleDate(clickedDate!!)
        dateVM.transStringToCalendar(clickedDate)

        routineVM.initRoutine()
        planVM.initPlan()

        showEmptyLayout()

        setTitleDate()
        setButtonsDestination()
        setPremium()

        initDoneListRecyclerView()

        popEditFrame()
        hideKeyboard()
    }

    private fun setButtonsDestination() {
        with(binding) {
            btnCalendarBack.setOnClickListener {
                finish()
            }
            btnPlan.setOnClickListener {
                val intent = Intent(this@DoneActivity, PlanRoutineActivity::class.java)
                val date = dateVM.getTitleDate()
                intent.putExtra("mode", ItemMode.PLAN.name)
                intent.putExtra("date", date)  // 현재 던리스트의 날짜
                startActivity(intent)
            }
            llTodayRecord.setOnClickListener {
                val intent = Intent(this@DoneActivity, TodayRecordActivity::class.java)
                startActivity(intent)
            }
        }
    }

    private fun showEmptyLayout() {
        dateVM.doneList.observe(this) {
            with(binding) {
                if (it.isEmpty()) {
                    if (!dateVM.isDateToday()) {
                        // 리스트가 비어있는데 이전 날인 경우
                        rcDoneList.isClickable = false
                        tvDoneList.visibility = View.GONE
                        layoutEmpty.root.visibility = View.VISIBLE
                        layoutEmpty.btnWrite.setOnClickListener {
                            rcDoneList.isClickable = true
                            tvDoneList.visibility = View.VISIBLE
                            layoutEmpty.root.visibility = View.GONE
                        }
                    } else {
                        // 리스트가 비어있는데 오늘인 경우
                        rcDoneList.isClickable = true
                        tvDoneList.visibility = View.VISIBLE
                        layoutEmpty.root.visibility = View.GONE
                    }
                } else {
                    // 리스트가 있는 경우
                    rcDoneList.isClickable = true
                    tvDoneList.visibility = View.VISIBLE
                    layoutEmpty.root.visibility = View.GONE
                }
            }
        }
    }

    private fun initDoneListRecyclerView() {
        with(binding.rcDoneList) {
            adapter = doneAdapter
            layoutManager = LinearLayoutManager(context)
            itemAnimator = null
        }
        dateVM.doneList.observe(this) { doneLists ->
            Log.d("date_list_size", "${doneLists.size}")
            doneLists.let { doneAdapter.submitList(it) }
            if (doneLists.isEmpty()) {
                binding.tvDoneListCount.visibility = View.INVISIBLE
                binding.tvDoneList.text = getString(R.string.done_list_hint)
            } else {
                binding.tvDoneListCount.visibility = View.VISIBLE
                binding.tvDoneListCount.text = "${doneLists.size}"
                binding.tvDoneList.text = ""
            }
        }

        doneAdapter.setDoneClickListener(object : DoneAdapter.OnDoneClickListener{
            // 던리스트 수정/삭제 메뉴
            override fun onDoneMenuClick(v: View, done: Done, position: Int) {
                Log.d("done_click", "ok")
                popup = PowerMenu.Builder(this@DoneActivity)
                    .addItem(PowerMenuItem("수정"))
                    .addItem(PowerMenuItem("삭제"))
                    .setMenuRadius(util.dpToPx(8).toFloat())
                    .setTextColor(ContextCompat.getColor(this@DoneActivity, R.color.black))
                    .setTextSize(14)
                    .setTextGravity(Gravity.CENTER)
                    .setMenuColor(ContextCompat.getColor(this@DoneActivity, R.color.white))
                    .setDivider(ContextCompat.getDrawable(this@DoneActivity, R.drawable.popup_divider))
                    .setDividerHeight(util.dpToPx(1))
                    .setPadding(9)
                    .setWidth(util.dpToPx(108))
                    .setAnimation(MenuAnimation.SHOWUP_TOP_RIGHT)
                    .setSelectedMenuColor(ContextCompat.getColor(this@DoneActivity, R.color.doneDetailColor))
                    .setOnMenuItemClickListener { menu, item ->
                        when(menu) {
                            0 -> {
                                // 수정
                                popup.dismiss()
                                doneVM.oldDone = done
                                doneVM.oldDoneIndex = position
                                categoryVM._selectedCategory.value = done.categoryNo
                                supportFragmentManager.beginTransaction().replace(binding.flDoneWrite.id, DoneFragment(DoneMode.EDIT_DONE)).commit()
                                binding.flDoneWrite.visibility = View.VISIBLE
                            }
                            1 -> {
                                // 삭제
                                doneVM.deleteDoneList(done.doneId)
                                popup.dismiss()
                            }
                        }
                    }
                    .build()
                popup.showAsDropDown(v, v.measuredWidth/2-popup.contentViewWidth+util.dpToPx(14), -v.measuredHeight/2-util.dpToPx(6))
            }
        })
    }

    private fun setTitleDate() {
        with(binding) {
            btnDayBefore.setOnClickListener {
                closeEditFrame()
                dateVM.clickedBackwardDay()
            }
            btnDayAfter.setOnClickListener {
                closeEditFrame()
                if (dateVM.isDateToday()) {
                    DoneToast.createToast(this@DoneActivity, getString(R.string.toast_after_today), "")?.show()
                } else {
                    dateVM.clickedForwardDay()
                }
            }
        }
        dateVM.titleDate.observe(this) {
            binding.tvDate.text = dateVM.titleDate.value
        }
    }


    private fun popEditFrame() {
        binding.tvDoneList.setOnClickListener {
            supportFragmentManager.beginTransaction().replace(binding.flDoneWrite.id, DoneFragment(DoneMode.DONE)).commit()
            binding.flDoneWrite.visibility = View.VISIBLE
        }
        binding.rcDoneList.setOnTouchListener { v, event ->
            if (event.action == MotionEvent.ACTION_UP)
                v.performClick()
            else
                false
        }
        binding.rcDoneList.setOnClickListener {
            if (it.isClickable) {
                supportFragmentManager.beginTransaction().replace(binding.flDoneWrite.id, DoneFragment(DoneMode.DONE)).commit()
                binding.flDoneWrite.visibility = View.VISIBLE
            }
        }
        binding.rootView.setOnClickListener {
            closeEditFrame()
            binding.scrollView.post {
                binding.scrollView.fullScroll(ScrollView.FOCUS_UP)
            }
        }
        binding.subRootView.setOnClickListener {
            closeEditFrame()
            binding.scrollView.post {
                binding.scrollView.fullScroll(ScrollView.FOCUS_UP)
            }
        }
    }

    fun scrollingDown() {
        binding.scrollView.post {
            binding.scrollView.fullScroll(ScrollView.FOCUS_DOWN)
        }
    }

    fun closeEditFrame() {
        hideKeyboard() // visibility보다 먼저 처리해야 함
        binding.flDoneWrite.visibility = View.GONE
    }

    private fun setPremium() {
        dateVM.doneList.observe(this) { list ->
            if (DoneApplication.pref.premium) {
                if(list.size == PREMIUM_DONELIST_SIZE) {
                    binding.rcDoneList.isClickable = false
                    closeEditFrame() // editFrame 열려있으면 닫아야 함
                } else {
                    binding.rcDoneList.isClickable = true
                }
            } else {
                if(list.size == NORMAL_DONELIST_SIZE) {
                    binding.rcDoneList.isClickable = false
                    closeEditFrame() // editFrame 열려있으면 닫아야 함
                } else {
                    binding.rcDoneList.isClickable = true
                }
            }
        }
    }

    private fun hideKeyboard() {
        val inputManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputManager.hideSoftInputFromWindow(currentFocus?.windowToken, 0)
    }

    private fun setKeyboardHeight() {
        binding.root.viewTreeObserver.addOnGlobalLayoutListener(object: ViewTreeObserver.OnGlobalLayoutListener{
            override fun onGlobalLayout() {
                if (rootHeight == -1) rootHeight = binding.root.height
                val visibleFrameSize = Rect()
                binding.root.getWindowVisibleDisplayFrame(visibleFrameSize)
                val heightExceptKeyboard = visibleFrameSize.bottom - visibleFrameSize.top
                val keyboard = rootHeight - heightExceptKeyboard
                Log.d("keyboard", "$keyboard")
                if (DoneApplication.pref.keyboard != keyboard && keyboard != 0) {
                    DoneApplication.pref.keyboard = keyboard
                    Log.d("keyboard_pref", "${DoneApplication.pref.keyboard}")
                    binding.root.viewTreeObserver.removeOnGlobalLayoutListener(this)
                } else {
                    if (keyboard != 0) {
                        binding.root.viewTreeObserver.removeOnGlobalLayoutListener(this)
                    }
                }
            }
        })
    }



}

