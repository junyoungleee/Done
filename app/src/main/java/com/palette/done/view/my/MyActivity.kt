package com.palette.done.view.my

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
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
            tvType.text = GradeType.type.getValue(data.type!!)
            tvNickname.text = data.nickname!!
            val lv = data.level
            tvLevelWithName.text = "${GradeType.getGradeName(lv)} LV.$lv"
        }
    }

    private fun setLevel() {
        val data = DoneApplication.pref
        with(binding.layoutMyLevel) {
            tvGradeName.visibility = View.GONE
            val level = data.level
            if(level == 10) {
                tvLevelProgressStart.text = "LV.$level"
                tvLevelProgressEnd.text = ""
            } else {
                tvLevelProgressStart.text = "LV.$level"
                tvLevelProgressEnd.text = "LV.${level+1}"
            }
            val grade = GradeType.getGrade(level)
            val imgId = resources.getIdentifier("img_grade_${grade}", "drawable", this@MyActivity.packageName)
            ivGradeDonedone.setImageDrawable(ContextCompat.getDrawable(this@MyActivity, imgId))

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
            btnBack.setOnClickListener {
                finish()
            }
            layoutMyProfile.btnEditProfile.setOnClickListener {
                val intent = Intent(this@MyActivity, MyMenuActivity::class.java)
                intent.putExtra("mode", MyMode.PROFILE_EDIT.name)
                startActivity(intent)
            }
            cvLevel.setOnClickListener {
                val intent = Intent(this@MyActivity, MyMenuActivity::class.java)
                intent.putExtra("mode", MyMode.GRADE.name)
                startActivity(intent)
            }
        }
        with(binding.layoutMyMenu) {
            btnMenuPremium.setOnClickListener {
                // 프리미엄
                val intent = Intent(this@MyActivity, MyMenuActivity::class.java)
                intent.putExtra("mode", MyMode.PREMIUM.name)
                startActivity(intent)
            }
            btnMenuNotice.setOnClickListener {
                // 공지사항
                val intentUrl = Intent(Intent.ACTION_VIEW,
                    Uri.parse("https://spark-myth-3c9.notion.site/f6a726345199494cbd2225ea8dfdd898"))
                startActivity(intentUrl)
            }
            btnMenuInquiry.setOnClickListener {
                // 문의하기
                val intentUrl = Intent(Intent.ACTION_VIEW,
                    Uri.parse("https://spark-myth-3c9.notion.site/59aa9d72a8104a4eb73daa31a77cdb82"))
                startActivity(intentUrl)
            }
            btnMenuUse.setOnClickListener {
                // 이용약관
                val intentUrl = Intent(Intent.ACTION_VIEW,
                    Uri.parse("https://spark-myth-3c9.notion.site/f7c405f9a9a644a8964ad6046530d08f"))
                startActivity(intentUrl)
            }
            btnMenuPrivacy.setOnClickListener {
                // 개인정보 보호약관
                val intentUrl = Intent(Intent.ACTION_VIEW,
                    Uri.parse("https://spark-myth-3c9.notion.site/95812922d5cc4f9aa47f32936f5e9919"))
                startActivity(intentUrl)
            }

        }
    }

    override fun onResume() {
        super.onResume()
        setProfile()
    }
}