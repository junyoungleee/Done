package com.palette.done.view.main

import android.content.Context
import android.content.Intent
import android.graphics.Rect
import android.os.Bundle
import android.os.Handler
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
import androidx.core.view.marginBottom
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.palette.done.DoneApplication
import com.palette.done.R
import com.palette.done.data.db.entity.Done
import com.palette.done.data.remote.repository.DoneServerRepository
import com.palette.done.databinding.ActivityDoneBinding
import com.palette.done.databinding.LayoutDoneEmptyBinding
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
        binding.flDoneWrite.visibility = View.GONE

        val clickedDate = intent.getStringExtra("clickedDate")
        Log.d("date_clicked", "${clickedDate}")

        dateVM.setTitleDate(clickedDate!!)
        dateVM.transStringToCalendar(clickedDate)

        routineVM.initRoutine()
        planVM.initPlan()

        setTitleDate()
        setButtonsDestination()
        checkEmptyAndPremium()

        initDoneListRecyclerView()

        showTodayRecord()

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
                val date = dateVM.getTitleDate()
                val today = dateVM.todayRecord.value
                val empty = today == null
                Log.d("empty", "${empty}")
                intent.putExtra("date", date)
                intent.putExtra("empty", empty)
                startActivity(intent)
            }
        }
    }


    private fun checkEmptyAndPremium() {
        dateVM.doneList.observe(this) {
            if (it.isEmpty()) {
                if (!dateVM.isDateToday()) {
                    // 리스트가 비어있는데 이전 날인 경우
                    with(binding) {
                        rcDoneList.isClickable = false
                        tvDoneList.visibility = View.GONE
                        layoutEmpty.root.visibility = View.VISIBLE
                        layoutEmpty.btnWrite.setOnClickListener {
                            binding.rcDoneList.isClickable = true
                            binding.tvDoneList.visibility = View.VISIBLE
                            layoutEmpty.root.visibility = View.GONE
                        }
                        Log.d("rc_clickable", "${rcDoneList.isClickable}")
                    }
                } else {
                    // 리스트가 비어있는데 오늘인 경우
                    with(binding) {
                        rcDoneList.isClickable = true
                        tvDoneList.visibility = View.VISIBLE
                        layoutEmpty.root.visibility = View.GONE
                    }
                }
            } else {
                // 리스트가 있는 경우
                with(binding) {
                    tvDoneList.visibility = View.VISIBLE
                    layoutEmpty.root.visibility = View.GONE
                }
                // 프리미엄 체크
                if (DoneApplication.pref.premium) {
                    if(it.size == PREMIUM_DONELIST_SIZE) {
                        binding.rcDoneList.isClickable = false
                        binding.tvDoneList.isClickable = false
                        closeEditFrame() // editFrame 열려있으면 닫아야 함
                        makeScreenOriginal()
                    } else {
                        binding.rcDoneList.isClickable = true
                        binding.tvDoneList.isClickable = true
                    }
                } else {
                    if(it.size == NORMAL_DONELIST_SIZE) {
                        binding.rcDoneList.isClickable = false
                        binding.tvDoneList.isClickable = false
                        closeEditFrame() // editFrame 열려있으면 닫아야 함
                        makeScreenOriginal()
                    } else {
                        binding.rcDoneList.isClickable = true
                        binding.tvDoneList.isClickable = true
                    }
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
                    .setLifecycleOwner(this@DoneActivity)
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
                                hideKeyboard()
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

    // 오늘한마디 ------------------------------------------------------------------------------------
    private fun showTodayRecord() {
        dateVM.todayRecord.observe(this) { tr ->
            with(binding) {
                if (tr == null) {
                    // 오늘의 한마디가 없는 경우
                    ivTodayRecordSticker.visibility = View.GONE
                    viewTodayStickerTopMargin.visibility = View.GONE
                    viewTodayStickerBottomMargin.visibility = View.GONE

                    tvTodayRecordText.text = getString(R.string.done_today_hint)
                    tvTodayRecordText.visibility = View.VISIBLE
                } else {
                    // 텍스트만 있는 경우
                    if (tr.todaySticker == null && tr.todayWord != null) {
                        ivTodayRecordSticker.visibility = View.GONE
                        viewTodayStickerTopMargin.visibility = View.GONE
                        viewTodayStickerBottomMargin.visibility = View.GONE

                        tvTodayRecordText.visibility = View.VISIBLE
                        tvTodayRecordText.text = tr.todayWord
                    }else if (tr.todaySticker != null && tr.todayWord == null ) {
                        // 스티커만 있는 경우
                        ivTodayRecordSticker.visibility = View.VISIBLE
                        viewTodayStickerTopMargin.visibility = View.VISIBLE
                        viewTodayStickerBottomMargin.visibility = View.VISIBLE

                        val stickerName = "img_sticker_${tr.todaySticker}"
                        val stickerId = resources.getIdentifier(stickerName, "drawable", this@DoneActivity.packageName)
                        ivTodayRecordSticker.setImageDrawable(ContextCompat.getDrawable(this@DoneActivity, stickerId))

                        tvTodayRecordText.visibility = View.GONE
                    } else if (tr.todaySticker == null && tr.todayWord == null) {
                        // 저장했었다가 사용자가 내용을 다 삭제한 경우
                        ivTodayRecordSticker.visibility = View.GONE
                        viewTodayStickerTopMargin.visibility = View.GONE
                        viewTodayStickerBottomMargin.visibility = View.GONE

                        tvTodayRecordText.text = getString(R.string.done_today_hint)
                        tvTodayRecordText.visibility = View.VISIBLE
                    } else {
                        // 둘 다 있는 경우
                        ivTodayRecordSticker.visibility = View.VISIBLE
                        viewTodayStickerTopMargin.visibility = View.GONE
                        viewTodayStickerBottomMargin.visibility = View.GONE

//                        val stickerName = "img_sticker_${tr.todaySticker}"
//                        val stickerId = resources.getIdentifier(stickerName, "drawable", this@DoneActivity.packageName)
//                        ivTodayRecordSticker.setImageDrawable(ContextCompat.getDrawable(this@DoneActivity, stickerId))

                        tvTodayRecordText.visibility = View.VISIBLE
                        tvTodayRecordText.text = tr.todayWord
                    }
                }
            }
        }
    }

    // 전체 -----------------------------------------------------------------------------------------
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
            hideKeyboard()
            scrollingUp()
        }
        binding.rcDoneList.setOnTouchListener { v, event ->
            if (event.action == MotionEvent.ACTION_UP)
                v.performClick()
            else
                false
        }
        binding.rcDoneList.setOnClickListener {
            if (it.isClickable) {
                Log.d("rc_click", "true")
                supportFragmentManager.beginTransaction().replace(binding.flDoneWrite.id, DoneFragment(DoneMode.DONE)).commit()
                binding.flDoneWrite.visibility = View.VISIBLE
                hideKeyboard()
                scrollingUp()
            }
        }
        binding.rootView.setOnClickListener {
            closeEditFrame()
            makeScreenOriginal()
            binding.scrollView.post {
                binding.scrollView.fullScroll(ScrollView.FOCUS_UP)
            }
        }
        binding.subRootView.setOnClickListener {
            closeEditFrame()
            makeScreenOriginal()
            binding.scrollView.post {
                binding.scrollView.fullScroll(ScrollView.FOCUS_UP)
            }
        }
        binding.flDoneWrite.isClickable = false
        binding.flDoneWrite.setOnClickListener {

        }
    }

    fun makeScreenLong() {
        binding.subRootView.setPadding(0, 0, 0, DoneApplication.pref.keyboard+util.dpToPx(20))
    }

    fun makeScreenOriginal() {
        binding.subRootView.setPadding(0, 0, 0, util.dpToPx(20))
    }

    fun scrollingDown() {
        val height = resources.getDimension(R.dimen.done_item_height)
        dateVM.doneList.observe(this) { list ->
            var dp = if (list.size <= 4) {
                0
            } else {
                (height*(list.size - 4)).toInt()
            }
            binding.scrollView.smoothScrollTo(0, binding.rcDoneList.top+dp)
        }
    }

    private fun scrollingUp() {
        binding.scrollView.fullScroll(ScrollView.FOCUS_UP)
    }

    fun closeEditFrame() {
        hideKeyboard() // visibility보다 먼저 처리해야 함
        binding.flDoneWrite.visibility = View.GONE
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

