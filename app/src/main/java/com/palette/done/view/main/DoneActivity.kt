package com.palette.done.view.main

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import com.palette.done.databinding.ActivityDoneBinding
import com.palette.done.viewmodel.DoneViewModel
import java.text.SimpleDateFormat
import java.util.*

class DoneActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDoneBinding
    private val doneVM: DoneViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDoneBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val clickedDate = intent.getStringExtra("clickedDate")
        Log.d("date_clicked", "${clickedDate}")

        doneVM.setTitleDate(clickedDate!!)
        doneVM.transStringToCalendar(clickedDate!!)

        setTitleDate()

        setTitleDate()
        setButtonsDestination()
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
                doneVM.clickedBackwardDay()
            }
            btnDayAfter.setOnClickListener {
                doneVM.clickedForwardDay()
            }
        }
        doneVM.titleDate.observe(this) {
            binding.tvDate.text = doneVM.titleDate.value
        }
    }


}