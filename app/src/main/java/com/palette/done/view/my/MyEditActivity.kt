package com.palette.done.view.my

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.palette.done.R
import com.palette.done.databinding.ActivityMyEditBinding
import com.palette.done.view.decoration.DoneToast
import com.palette.done.view.login.LoginPwdFragment
import com.palette.done.view.my.MyEditMode
import com.palette.done.view.my.MyMode
import com.palette.done.view.signin.ObNicknameFragment
import com.palette.done.view.signin.ObTypeFragment
import com.palette.done.view.util.NetworkManager

class MyEditActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMyEditBinding
    private lateinit var mode: MyEditMode

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMyEditBinding.inflate(layoutInflater)

        mode = MyEditMode.valueOf(intent!!.getStringExtra("mode")!!)
        checkNetworkState()

        setFragment()
        setContentView(binding.root)

        binding.btnBack.setOnClickListener {
            finish()
        }

    }

    private fun checkNetworkState() {
        if (!NetworkManager.checkNetworkState(this)) {
            NetworkManager.showRequireNetworkToast(this)
        }
    }

    private fun setFragment() {
        val editMode = true
        when(mode) {
            MyEditMode.NICKNAME -> {
                binding.tvMyEditTitle.text = getString(R.string.my_edit_nickname_title)
                supportFragmentManager.beginTransaction().replace(binding.flEdit.id, ObNicknameFragment(editMode)).commit()
            }
            MyEditMode.TPYE -> {
                binding.tvMyEditTitle.text = getString(R.string.my_edit_type_title)
                supportFragmentManager.beginTransaction().replace(binding.flEdit.id, ObTypeFragment(editMode)).commit()
            }
            MyEditMode.PWD -> {
                binding.tvMyEditTitle.text = getString(R.string.my_edit_pwd_title)
                supportFragmentManager.beginTransaction().replace(binding.flEdit.id, LoginPwdFragment(editMode)).commit()
            }
            MyEditMode.ALARM -> {

            }
        }
    }

}