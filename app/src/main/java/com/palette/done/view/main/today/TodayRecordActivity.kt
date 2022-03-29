package com.palette.done.view.main.today

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import com.palette.done.DoneApplication
import com.palette.done.data.remote.repository.DoneServerRepository
import com.palette.done.databinding.ActivityTodayRecordBinding
import com.palette.done.viewmodel.TodayRecordViewModel
import com.palette.done.viewmodel.TodayRecordViewModelFactory
import com.palette.done.viewmodel.TodayStickerViewModel
import com.palette.done.viewmodel.TodayStickerViewModelFactory

class TodayRecordActivity : AppCompatActivity() {

    private lateinit var binding: ActivityTodayRecordBinding

    private lateinit var todayVM: TodayRecordViewModel
    private lateinit var todayVMFactory: TodayRecordViewModelFactory

    private val stickerVM : TodayStickerViewModel by viewModels() {
        TodayStickerViewModelFactory(DoneApplication().stickerRepository)
    }

    private var first = false // 처음 저장인지, 수정인지 확인
    private lateinit var date: String  // 오늘한마디를 저장할 날짜


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTodayRecordBinding.inflate(layoutInflater)
        setContentView(binding.root)

        date = intent.getStringExtra("date").toString()
        first = intent.getBooleanExtra("empty", false)
        Log.d("today_date", date)

        todayVMFactory = TodayRecordViewModelFactory(date, DoneServerRepository(), DoneApplication().doneRepository)
        todayVM = ViewModelProvider(this, todayVMFactory).get(TodayRecordViewModel::class.java)

        showData()
        setBackButtonAction()
        setAddButtonsClick()
        setEditMode()

        closePopup()
    }

    private fun setAddButtonsClick() {
        binding.llAddSticker.setOnClickListener {
            hideKeyboard()
            binding.flTodayEdit.visibility = View.VISIBLE
            supportFragmentManager.beginTransaction().replace(binding.flTodayEdit.id, TodayStickerFragment()).commit()
        }

        binding.llAddText.setOnClickListener {
            it.visibility = View.GONE
            binding.flTodayEdit.visibility = View.GONE
            with(binding.etTodayText) {
                visibility = View.VISIBLE
                isFocusableInTouchMode = true
                requestFocus()
            }
            showKeyboard()
        }

        binding.flTodayEdit.setOnClickListener { }
    }

    private fun showData() {
        todayVM.todayRecord.observe(this) { tr ->
            Log.d("today_ob", "$tr")
            if (tr != null) {
                if (tr.todaySticker != null) {
                    binding.llAddSticker.visibility = View.GONE
                    binding.ivTodaySticker.visibility = View.VISIBLE
                    val stickerId = resources.getIdentifier("sticker_${tr.todaySticker}", "drawable", this.packageName)
                    binding.ivTodaySticker.setImageDrawable(ContextCompat.getDrawable(this, stickerId))
                    binding.btnDeleteSticker.visibility = View.VISIBLE
                    stickerVM.setTodaySticker(tr.todaySticker)
                }
                if (tr.todayWord != null) {
                    binding.llAddText.visibility = View.GONE
                    binding.etTodayText.visibility = View.VISIBLE
                    binding.etTodayText.setText(tr.todayWord)
                }
            }
        }
    }

    private fun setEditMode() {
        // 스티커가 있으면 delete 버튼 보이기
        // 스티커가 없으면 다시 llAddSticker 버튼 보이기
        stickerVM.stickerId.observe(this) { id ->
            if (id == 0 || id == null) {
                binding.btnDeleteSticker.visibility = View.GONE
                binding.ivTodaySticker.visibility = View.GONE
                binding.llAddSticker.visibility = View.VISIBLE
            } else {
                binding.btnDeleteSticker.visibility = View.VISIBLE
                binding.ivTodaySticker.visibility = View.VISIBLE
                val stickerId = resources.getIdentifier("sticker_$id", "drawable", this.packageName)
                binding.ivTodaySticker.setImageDrawable(ContextCompat.getDrawable(this, stickerId))
                binding.llAddSticker.visibility = View.GONE
            }
        }
        // 스티커 삭제 버튼
        binding.btnDeleteSticker.setOnClickListener {
            it.visibility = View.GONE
            binding.ivTodaySticker.visibility = View.GONE
            binding.llAddSticker.visibility = View.VISIBLE
            stickerVM.initStickerId()
        }

        // 텍스트가 있으면 llAddText 없애기
        binding.etTodayText.addTextChangedListener(todayVM.onTodayRecordTextWatcher())
        todayVM.todayText.observe(this) { text ->
            if (text != "") {
                binding.llAddText.visibility = View.GONE
                binding.etTodayText.visibility = View.VISIBLE
            }
        }
        binding.etTodayText.setOnClickListener {
            binding.flTodayEdit.visibility = View.GONE
        }
    }

    private fun closePopup() {
        with(binding) {
            root.setOnClickListener {
                binding.flTodayEdit.visibility = View.GONE
                hideKeyboard()
            }
            toolbar.setOnClickListener {
                binding.flTodayEdit.visibility = View.GONE
                hideKeyboard()
            }
        }
    }

    private fun setBackButtonAction() {
        binding.btnBack.setOnClickListener {
            // 내용 저장
            saveAndUpdateTodayRecord()
        }
    }

    override fun onBackPressed() {
        // 내용 저장
        saveAndUpdateTodayRecord()
    }

    private fun saveAndUpdateTodayRecord() {
        val sticker = stickerVM.getTodaySticker()
        val text = todayVM.getTodayText()
        if (first) {
            // 처음 저장하는 경우
            if (sticker == null && text == null) {
                // 아무것도 작성하지 않은 경우
                finish()
            } else {
                // 처음 저장하는 경우
                todayVM.postTodayRecord(date, text, sticker)
                todayVM.isSaved.observe(this) { result ->
                    if (result) {
                        finish()
                    }
                }
            }
        } else {
            // 내용이 있는 경우
            if (todayVM.isExistingEdited(stickerVM.getTodaySticker())) {
                // 바뀐 내용이 있다면 수정 내용 저장
                Log.d("today_edit_check", "${todayVM.isExistingEdited(stickerVM.getTodaySticker())}")
                todayVM.patchTodayRecord(date, todayVM.getTodayRecordId()!!, text, sticker)
                todayVM.isSaved.observe(this) { result ->
                    if (result) {
                        finish()
                    }
                }
            } else {
                // 바뀐 내용이 없는 경우
                Log.d("today_edit_check", "${todayVM.isExistingEdited(stickerVM.getTodaySticker())}")
                finish()
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