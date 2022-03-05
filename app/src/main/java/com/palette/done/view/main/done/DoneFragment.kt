package com.palette.done.view.main.done

import android.content.Context
import android.graphics.Rect
import android.os.Bundle
import android.util.Log
import android.util.TypedValue
import android.view.*
import androidx.fragment.app.Fragment
import android.view.inputmethod.InputMethodManager
import android.widget.FrameLayout
import android.widget.LinearLayout
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.palette.done.DoneApplication
import com.palette.done.R
import com.palette.done.databinding.FragmentDoneBinding
import com.palette.done.databinding.FragmentLoginEmailBinding
import com.palette.done.view.util.Util
import com.palette.done.viewmodel.DoneEditViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class DoneFragment : Fragment() {

    private var _binding: FragmentDoneBinding? = null
    private val binding get() = _binding!!

    private val doneVM: DoneEditViewModel by activityViewModels()

    private var isEditPopupOpen: Boolean = false

    private val util = Util()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FragmentDoneBinding.inflate(inflater, container, false)

        binding.flWriteContainer.layoutParams =
            LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, DoneApplication.pref.keyboard)

        binding.etDone.requestFocus()

        setCategoryButton()
        setWriteButtons()
        setEditText()

        return binding.root
    }

    private fun setWriteButtons() {
        binding.etDone.addTextChangedListener(doneVM.onDoneTextWatcher())
        doneVM.done.observe(viewLifecycleOwner) { done ->
            if (done == "") {
                with(binding.btnWrite) {
                    text = getString(R.string.done_btn_hash_tag)
                    setTextSize(TypedValue.COMPLEX_UNIT_SP, 20f)
                    setPadding(util.dpToPx(0), util.dpToPx(0), util.dpToPx(0), util.dpToPx(0))
                    setOnClickListener {
                        isEditPopupOpen = true
                        setInputFrameLayout()
                        parentFragmentManager.beginTransaction().replace(binding.flWriteContainer.id, DoneTagFragment()).commit()
                    }
                }
            } else {
                with(binding.btnWrite) {
                    text = getString(R.string.done_btn_write)
                    setTextSize(TypedValue.COMPLEX_UNIT_SP, 14f)
                    setPadding(util.dpToPx(12), util.dpToPx(0), util.dpToPx(12), util.dpToPx(0))
                }
            }
        }
    }

    private fun setCategoryButton() {
        binding.btnCategory.setOnClickListener {
            isEditPopupOpen = !isEditPopupOpen
            setInputFrameLayout()
            parentFragmentManager.beginTransaction().replace(binding.flWriteContainer.id, DoneCategoryFragment()).commit()
        }
    }

    private fun setEditText() {
        binding.etDone.setOnClickListener {
            if (isEditPopupOpen) {
                isEditPopupOpen = false
                setInputFrameLayout()
            }
        }
    }

    private fun setInputFrameLayout() {
        Log.d("is_popup_open", "$isEditPopupOpen")
        lifecycleScope.launch {
            if (isEditPopupOpen) {
                // 카테고리, 루틴, 해시태그 입력창이 열리게 하기
                hideKeyboard()
                requireActivity().window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING)
                binding.flWriteContainer.visibility = View.VISIBLE
                delay(100)
                binding.etDone.requestFocus()
                requireActivity().window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
            } else {
                // 카테고리, 루틴, 해시태그 입력창이 닫히게 하기
                requireActivity().window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING)
                showKeyboard()
                binding.etDone.requestFocus()
                delay(100)
                binding.flWriteContainer.visibility = View.GONE
                requireActivity().window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
            }
        }
    }

    private fun hideKeyboard() {
        val inputManager = requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputManager.hideSoftInputFromWindow(requireActivity().currentFocus?.windowToken, 0)
    }

    private fun showKeyboard() {
        val inputManager = requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputManager.showSoftInput(view, 0)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}