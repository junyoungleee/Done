package com.palette.done.view.main.today

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.LinearLayout
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentManager
import com.palette.done.DoneApplication
import com.palette.done.data.remote.repository.DoneServerRepository
import com.palette.done.databinding.ActivityTodayRecordBinding
import com.palette.done.view.main.done.DoneCategoryFragment
import com.palette.done.viewmodel.TodayRecordViewModel
import com.palette.done.viewmodel.TodayRecordViewModelFactory

class TodayRecordActivity : AppCompatActivity() {

    private lateinit var binding: ActivityTodayRecordBinding

    private val todayVM: TodayRecordViewModel by viewModels() {
        TodayRecordViewModelFactory(DoneServerRepository(), DoneApplication().doneRepository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTodayRecordBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setBackButtonAction()
        setAddButtonsClick()
        setEditMode()

        closePopup()
    }

    private fun setBackButtonAction() {
        binding.btnBack.setOnClickListener {
            // 내용 저장
            finish()
        }
    }

    private fun setAddButtonsClick() {
        binding.llAddSticker.setOnClickListener {
            hideKeyboard()
            binding.flSticker.visibility = View.VISIBLE
            supportFragmentManager.beginTransaction().replace(binding.flSticker.id, TodayRecordFragment()).commit()
        }

        binding.llAddText.setOnClickListener {
            it.visibility = View.GONE
            binding.flSticker.visibility = View.GONE
            with(binding.etTodayText) {
                visibility = View.VISIBLE
                isFocusableInTouchMode = true
                requestFocus()
            }
            showKeyboard()
        }
    }

    private fun setEditMode() {
        // 스티커가 있으면 delete 버튼 보이기
        // 스티커가 없으면 다시 llAddSticker 버튼 보이기
        todayVM.stickerId.observe(this) { id ->
            if (id == 0) {
                binding.btnDeleteSticker.visibility = View.GONE
                binding.ivTodaySticker.visibility = View.GONE
                binding.llAddSticker.visibility = View.VISIBLE
            } else {
                binding.btnDeleteSticker.visibility = View.VISIBLE
                binding.ivTodaySticker.visibility = View.VISIBLE
//                val stickerId = resources.getIdentifier("sticker_$id", "drawable", this.packageName)
//                binding.ivTodaySticker.setImageDrawable(ContextCompat.getDrawable(this, stickerId))
                binding.llAddSticker.visibility = View.GONE
            }
        }
        // 스티커 삭제 버튼
        binding.btnDeleteSticker.setOnClickListener {
            it.visibility = View.GONE
            binding.ivTodaySticker.visibility = View.GONE
            binding.llAddSticker.visibility = View.VISIBLE
            todayVM.initStickerId()
        }

        // 텍스트가 있으면 llAddText 없애기
        // 텍스트가 없으면 llAddText 보이기
        binding.etTodayText.addTextChangedListener(todayVM.onTodayRecordTextWatcher())
        todayVM.todayText.observe(this) { text ->
            if (text == "") {
                binding.llAddText.visibility = View.VISIBLE
                binding.etTodayText.visibility = View.GONE
            } else {
                binding.llAddText.visibility = View.GONE
                binding.etTodayText.visibility = View.VISIBLE
            }
        }
        binding.etTodayText.setOnClickListener {
            binding.flSticker.visibility = View.GONE
        }
    }

    private fun closePopup() {
        with(binding) {
            root.setOnClickListener {
                binding.flSticker.visibility = View.GONE
                hideKeyboard()
            }
        }
    }

    private fun hideKeyboard() {
        val inputManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputManager.hideSoftInputFromWindow(currentFocus?.windowToken, 0)
    }

    private fun showKeyboard() {
        val inputManager = this.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputManager.showSoftInput(binding.etTodayText, 0)
    }

}