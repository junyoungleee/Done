package com.palette.done.view

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.palette.done.data.PreferenceManager
import com.palette.done.databinding.ActivityLandingBinding
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

class LandingActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLandingBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLandingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnStart.setOnClickListener {
            var intent = Intent(this, StartActivity::class.java)
            startActivity(intent)
        }
    }
}