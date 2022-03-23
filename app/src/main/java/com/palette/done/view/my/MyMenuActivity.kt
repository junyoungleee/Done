package com.palette.done.view.my

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.palette.done.R
import com.palette.done.databinding.ActivityMyMenuBinding
import com.palette.done.view.my.menu.MyEditFragment
import com.palette.done.view.my.menu.MyGradeFragment
import com.palette.done.view.my.menu.MyPremiumFragment

class MyMenuActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMyMenuBinding
    private lateinit var mode: MyMode

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMyMenuBinding.inflate(layoutInflater)

        mode = MyMode.valueOf(intent!!.getStringExtra("mode")!!)
        setFragment()
        setContentView(binding.root)

        binding.btnBack.setOnClickListener {
            finish()
        }
    }

    private fun setFragment() {
        when(mode) {
            MyMode.PROFILE_EDIT -> {
                binding.tvMyEditTitle.text = getString(R.string.my_edit_title)
                supportFragmentManager.beginTransaction().replace(binding.flEdit.id, MyEditFragment()).commit()
            }
            MyMode.PREMIUM -> {
                binding.tvMyEditTitle.text = getString(R.string.premium)
                supportFragmentManager.beginTransaction().replace(binding.flEdit.id, MyPremiumFragment()).commit()
            }
            MyMode.GRADE -> {
                binding.tvMyEditTitle.text = getString(R.string.grade_title)
                supportFragmentManager.beginTransaction().replace(binding.flEdit.id, MyGradeFragment()).commit()
            }
        }
    }
}