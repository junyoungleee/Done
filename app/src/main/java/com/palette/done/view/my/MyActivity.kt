package com.palette.done.view.my

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.palette.done.DoneApplication
import com.palette.done.R
import com.palette.done.databinding.ActivityMyBinding

class MyActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMyBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMyBinding.inflate(layoutInflater)

        setButtonsDestination()

        setProfile()
        setLevel()
        setReport()

        setPremium()

        setContentView(binding.root)
    }

    private fun setPremium() {
        binding.layoutMyProfile.ivPremiumBadge.visibility = if (DoneApplication.pref.premium) {
            View.VISIBLE
        } else {
            View.INVISIBLE
        }
    }

    private fun setProfile() {
        val data = DoneApplication.pref
        with(binding.layoutMyProfile) {
            tvType.text = LevelType.type.getValue(data.type!!)
            tvNickname.text = data.nickname!!
            val lv = data.level
            tvLevelWithName.text = "${LevelType.getLevel(lv)} LV.$lv"
        }
    }

    private fun setLevel() {
        val data = DoneApplication.pref
        with(binding.layoutMyLevel) {
            val level = data.level
            if(level == 10) {
                tvLevelProgressStart.text = "LV.$level"
                tvLevelProgressEnd.text = ""
            } else {
                tvLevelProgressStart.text = "LV.$level"
                tvLevelProgressEnd.text = "LV.${level+1}"
            }

            tvLevelLeftDetail.text = getString(R.string.my_level_left)
//            pbLevel.progress = 50
        }
    }

    private fun setReport() {
        with(binding.layoutMyReport) {
            tvReport1.text = "??개"
            tvReport2.text = "??%"
            tvReport3.text = "??개"
        }
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