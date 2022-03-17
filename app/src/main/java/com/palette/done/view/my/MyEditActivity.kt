package com.palette.done.view.my

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.FragmentManager
import com.palette.done.R
import com.palette.done.databinding.ActivityMyEditBinding
import com.palette.done.view.my.edit.MyEditFragment

class MyEditActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMyEditBinding
    private lateinit var mode: MyMode

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMyEditBinding.inflate(layoutInflater)

        mode = MyMode.valueOf(intent!!.getStringExtra("mode")!!)
        setFragment()
        setContentView(binding.root)
    }

    private fun setFragment() {
        when(mode) {
            MyMode.PROFILE_EDIT -> {
                supportFragmentManager.beginTransaction().replace(binding.flEdit.id, MyEditFragment()).commit()
            }
        }
    }
}