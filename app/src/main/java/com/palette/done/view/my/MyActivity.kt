package com.palette.done.view.my

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.palette.done.R
import com.palette.done.databinding.ActivityMyBinding

class MyActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMyBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMyBinding.inflate(layoutInflater)

        setButtonsDestination()

        setContentView(binding.root)
    }

    private fun setButtonsDestination() {
        with(binding) {
            layoutMyProfile.btnEditProfile.setOnClickListener {
                val intent = Intent(this@MyActivity, MyEditActivity::class.java)
                intent.putExtra("mode", MyMode.PROFILE_EDIT.name)
                startActivity(intent)
            }
            cvLevel.setOnClickListener {

            }
        }
        with(binding.layoutMyMenu) {
            btnMenuPremium.setOnClickListener {

            }
            btnMenuNotice.setOnClickListener {

            }
            btnMenuInquiry.setOnClickListener {

            }
            btnMenuUse.setOnClickListener {

            }
            btnMenuPrivacy.setOnClickListener {

            }
            btnMenuVersion.setOnClickListener {

            }
        }
    }
}