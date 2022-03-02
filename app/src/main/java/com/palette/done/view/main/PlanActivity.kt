package com.palette.done.view.main

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.palette.done.databinding.ActivityPlanBinding

class PlanActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPlanBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPlanBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}