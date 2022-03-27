package com.palette.done.view.my

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import com.palette.done.DoneApplication
import com.palette.done.R
import com.palette.done.data.remote.repository.DoneServerRepository
import com.palette.done.data.remote.repository.MemberRepository
import com.palette.done.databinding.ActivityMyBinding
import com.palette.done.viewmodel.*

class MyActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMyBinding

    private val myVM: MyViewModel by viewModels() {
        MyViewModelFactory(MemberRepository(), (application as DoneApplication).doneRepository)
    }

    private var leftMessage: String = ""
    private var percent: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMyBinding.inflate(layoutInflater)

        setButtonsDestination()

        myVM.getUserProfile()

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
            val level = data.level
            val gradeName = GradeType.getGradeName(level)
            tvLevelWithName.text = getString(R.string.my_grade_level, gradeName, level)
        }
    }

    private fun setLevel() {
        val data = DoneApplication.pref
        with(binding.layoutMyLevel) {
            tvGradeName.visibility = View.GONE
            val level = data.level
            val nextLevel = level+1 // 암 온더 넥ㅅㅌ 레벨
            if(level == 10) {
                tvLevelProgressStart.text = getString(R.string.level, level)
                tvLevelProgressEnd.text = ""
            } else {
                tvLevelProgressStart.text = getString(R.string.level, level)
                tvLevelProgressEnd.text = getString(R.string.level, nextLevel)
            }
            val grade = GradeType.getGrade(level)
            val imgId = resources.getIdentifier("img_grade_${grade}", "drawable", this@MyActivity.packageName)
            ivGradeDonedone.setImageDrawable(ContextCompat.getDrawable(this@MyActivity, imgId))

            myVM.leftMessage.observe(this@MyActivity) { msg ->
                tvLevelLeftDetail.text = msg
                leftMessage = msg
            }

            myVM.cumulatedData.observe(this@MyActivity) { data ->
                pbLevel.progress = LevelType.getProgressPercent(level, data)
                percent = data
            }
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
                intent.putExtra("msg", leftMessage)
                intent.putExtra("percent", percent)
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