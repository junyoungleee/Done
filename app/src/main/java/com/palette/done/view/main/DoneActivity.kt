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
import com.palette.done.DoneApplication
import com.palette.done.databinding.ActivityDoneBinding
import com.palette.done.view.main.done.DoneFragment
import com.palette.done.viewmodel.DoneDateViewModel
import com.palette.done.viewmodel.DoneEditViewModel
import java.util.*

class DoneActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDoneBinding
    private val dateVM: DoneDateViewModel by viewModels()
    private val doneEditVM: DoneEditViewModel by viewModels()

    private var rootHeight = -1
    private var keyboardHeight = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDoneBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val clickedDate = intent.getStringExtra("clickedDate")
        Log.d("date_clicked", "${clickedDate}")

        dateVM.setTitleDate(clickedDate!!)
        dateVM.transStringToCalendar(clickedDate!!)


        setKeyboardHeight()
        setTitleDate()

        setTitleDate()
        setButtonsDestination()

        popEditFrame()
        hideKeyboard()
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
                var intent = Intent(this@DoneActivity, PlanActivity::class.java)
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
            supportFragmentManager.beginTransaction().replace(binding.flDoneWrite.id, DoneFragment()).commit()
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