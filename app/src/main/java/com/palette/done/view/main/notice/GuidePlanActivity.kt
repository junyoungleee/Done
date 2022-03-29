package com.palette.done.view.main.notice

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.palette.done.databinding.ActivityGuidePlanBinding

class GuidePlanActivity : AppCompatActivity() {

    private lateinit var binding: ActivityGuidePlanBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGuidePlanBinding.inflate(layoutInflater)

        binding.btnBack.setOnClickListener {
            finish()
        }
        binding.ivBack.setOnClickListener {
            finish()
        }

        setContentView(binding.root)
    }
}