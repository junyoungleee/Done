package com.palette.done.view.main

import android.content.Context
import android.content.Intent
import android.graphics.Rect
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.activity.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.ListAdapter
import com.palette.done.DoneApplication
import com.palette.done.R
import com.palette.done.data.db.entity.Done
import com.palette.done.data.remote.repository.DoneServerRepository
import com.palette.done.databinding.ActivityDoneBinding
import com.palette.done.view.adapter.DoneAdapter
import com.palette.done.view.main.done.DoneFragment
import com.palette.done.viewmodel.*
import java.util.*

class DoneActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDoneBinding
    private val dateVM: DoneDateViewModel by viewModels() {
        DoneDateViewModelFactory((application as DoneApplication).doneRepository)
    }
    private val planVM: PlanViewModel by viewModels() {
        PlanViewModelFactory(DoneServerRepository(), DoneApplication().doneRepository)
    }
    private val routineVM: RoutineViewModel by viewModels() {
        RoutineViewModelFactory(DoneServerRepository(), DoneApplication().doneRepository)
    }


    private var rootHeight = -1
    private var keyboardHeight = -1

    private lateinit var doneAdapter: ListAdapter<Done, DoneAdapter.DoneViewHolder>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDoneBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val clickedDate = intent.getStringExtra("clickedDate")
        Log.d("date_clicked", "${clickedDate}")

        dateVM.setTitleDate(clickedDate!!)
        dateVM.transStringToCalendar(clickedDate!!)

        routineVM.initRoutine()

        setKeyboardHeight()
        setTitleDate()
        setButtonsDestination()

        initDoneListRecyclerView()

        popEditFrame()
        hideKeyboard()
    }

    private fun initDoneListRecyclerView() {
        doneAdapter = DoneAdapter()
        with(binding.rcDoneList) {
            adapter = doneAdapter
            layoutManager = LinearLayoutManager(context)
        }
        dateVM.doneList.observe(this) { doneLists ->
            Log.d("date_list_size", "${doneLists.size}")
            doneLists.let { doneAdapter.submitList(it) }
            if (doneLists.isEmpty()) {
                binding.tvDoneList.text = getString(R.string.done_list_hint)
            } else {
                binding.tvDoneList.text = ""
            }
        }
    }

    private fun setKeyboardHeight() {
        binding.root.viewTreeObserver.addOnGlobalLayoutListener {
            if (rootHeight == -1) rootHeight = binding.root.height
            val visibleFrameSize = Rect()
            binding.root.getWindowVisibleDisplayFrame(visibleFrameSize)
            val heightExceptKeyboard = visibleFrameSize.bottom - visibleFrameSize.top
            if (heightExceptKeyboard < rootHeight && DoneApplication.pref.keyboard == -1) {
                DoneApplication.pref.keyboard = rootHeight - heightExceptKeyboard
                Log.d("keyboard_height", "$keyboardHeight")
            }
        }
    }

    private fun setButtonsDestination() {
        with(binding) {
            btnCalendarBack.setOnClickListener {
                finish()
            }
            btnPlan.setOnClickListener {
                var intent = Intent(this@DoneActivity, PlanRoutineActivity::class.java)
                val date = dateVM.getTitleDate()
                intent.putExtra("mode", ItemMode.PLAN.name)
                intent.putExtra("date", date)  // 현재 던리스트의 날짜
                startActivity(intent)
            }
        }
    }

    private fun setTitleDate() {
        with(binding) {
            btnDayBefore.setOnClickListener {
                dateVM.clickedBackwardDay()
            }
            btnDayAfter.setOnClickListener {
                dateVM.clickedForwardDay()
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
        binding.rcDoneList.setOnClickListener {
            supportFragmentManager.beginTransaction().replace(binding.flDoneWrite.id, DoneFragment(DoneMode.DONE)).commit()
            binding.flDoneWrite.visibility = View.VISIBLE
        }
        binding.rootView.setOnClickListener {
            hideKeyboard()  // visibility보다 먼저 처리해야 함
            binding.flDoneWrite.visibility = View.GONE
        }
        binding.subRootView.setOnClickListener {
            hideKeyboard()  // visibility보다 먼저 처리해야 함
            binding.flDoneWrite.visibility = View.GONE
        }
    }

    private fun hideKeyboard() {
        val inputManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputManager.hideSoftInputFromWindow(currentFocus?.windowToken, 0)
    }


}