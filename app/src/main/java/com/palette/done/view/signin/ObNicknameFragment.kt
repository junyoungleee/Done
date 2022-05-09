package com.palette.done.view.signin

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.ImageView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.activityViewModels
import androidx.viewpager2.widget.ViewPager2
import com.palette.done.DoneApplication
import com.palette.done.R
import com.palette.done.databinding.FragmentObNicknameBinding
import com.palette.done.data.remote.repository.MemberRepository
import com.palette.done.view.decoration.DoneToast
import com.palette.done.view.my.MyEditActivity
import com.palette.done.view.util.NetworkManager
import com.palette.done.view.util.Util
import com.palette.done.viewmodel.MyEditViewModel
import com.palette.done.viewmodel.MyEditViewModelFactory
import com.palette.done.viewmodel.OnBoardingViewModel
import com.palette.done.viewmodel.OnBoardingViewModelFactory

class ObNicknameFragment(edit: Boolean = false) : Fragment() {

    private var _binding: FragmentObNicknameBinding? = null
    private val binding get() = _binding!!

    private val editMode: Boolean = edit  // 수정모드인지 확인
    private val util = Util()

    private val myEditVM: MyEditViewModel by activityViewModels { MyEditViewModelFactory(
        MemberRepository()
    ) }
    private val onBoardingVM: OnBoardingViewModel by activityViewModels { OnBoardingViewModelFactory(
        MemberRepository()
    )}

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FragmentObNicknameBinding.inflate(inflater, container, false)

        if (!editMode) {
            setNextButton()
        } else {
            setEditMode()
            checkEditedNickName()
            setEditButton()
        }

        checkNetworkState()
        binding.root.setOnClickListener {
            hideKeyboard()
        }

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun checkNetworkState() {
        if (!NetworkManager.checkNetworkState(requireActivity())) {
            DoneToast.createToast(requireActivity(), text = getString(R.string.network_error))?.show()
        }
    }

    // 온보딩 ---------------------------------------------------------------------------------------
    private fun setNextButton() {
        binding.etNickname.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                binding.btnNext.isEnabled = s.toString().isNotEmpty()
            }
            override fun afterTextChanged(s: Editable?) {
                binding.btnNext.isEnabled = s.toString().isNotEmpty()
            }
        })
        val viewPager = activity?.findViewById<ViewPager2>(R.id.view_pager_on_boarding)
        binding.btnNext.setOnClickListener {
            onBoardingVM.nickname.value = binding.etNickname.text.toString()
            viewPager?.currentItem = 1
            setIndicator()  // 다음 버튼 클릭 시 indicator 수정
        }
    }

    private fun setIndicator() {
        val indicator2 = activity?.findViewById<ImageView>(R.id.iv_indicator_second)
        val indicator3 = activity?.findViewById<ImageView>(R.id.iv_indicator_third)

        indicator2!!.setImageResource(R.drawable.ic_indicator_now)
        indicator3!!.setImageResource(R.drawable.ic_indicator_left)
    }

    // 닉네임 수정 -----------------------------------------------------------------------------------
    private fun setEditMode() {
        with(binding) {
            tvTitle.visibility = View.GONE
            tvEditTitle.visibility = View.VISIBLE
            tvNicknameWrong.visibility = View.VISIBLE
            etNickname.setText(DoneApplication.pref.nickname)
            btnNext.text = getString(R.string.login_btn_next)
            btnNext.isEnabled = false
        }

        var layoutParam = ConstraintLayout.LayoutParams(
            ConstraintLayout.LayoutParams.MATCH_PARENT, binding.btnNext.height
        )
        layoutParam.topToBottom = binding.tvNicknameWrong.id
        layoutParam.startToStart = ConstraintLayout.LayoutParams.PARENT_ID
        layoutParam.endToEnd = ConstraintLayout.LayoutParams.PARENT_ID
        layoutParam.topMargin = util.dpToPx(77)
        binding.btnNext.layoutParams = layoutParam
    }

    private fun checkEditedNickName() {
        // 닉네임 체크 입력

        // 1.2 업데이트
        val old = DoneApplication.pref.nickname
        binding.etNickname.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                var result = old != s.toString() && s.toString().isNotEmpty()
                binding.btnNext.isEnabled = result
                afterCheckEditedNickName(result)
            }
            override fun afterTextChanged(s: Editable?) {
                var result = old != s.toString() && s.toString().isNotEmpty()
                binding.btnNext.isEnabled = result
                afterCheckEditedNickName(result)
            }
        })
    }

    private fun afterCheckEditedNickName(result: Boolean) {
        with(binding) {
            if (!result) {
                if (binding.etNickname.text.toString() == "") {
                    tvNicknameWrong.text = ""
                } else {
                    tvNicknameWrong.text = getString(R.string.my_nickname_wrong_same)
                }
                btnNext.isEnabled = false
            }
            else {
                tvNicknameWrong.text = ""
                btnNext.isEnabled = true
            }
        }
    }

    private fun setEditButton() {
        // 닉네임 변경
        binding.btnNext.setOnClickListener {
            if (NetworkManager.checkNetworkState(requireActivity())) {
                myEditVM.patchNickname(binding.etNickname.text.toString())
                myEditVM.isResponse.observe(viewLifecycleOwner) { response ->
                    if (response) {
                        myEditVM.initResponseValue()
                        (activity as MyEditActivity).finish()
                    }
                }
            } else {
                NetworkManager.showRequireNetworkToast(requireActivity())
            }
        }
    }

    // ---------------------------------------------------------------------------------------------
    private fun hideKeyboard() {
        if (activity != null && requireActivity().currentFocus != null) {
            val inputManager: InputMethodManager = requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            inputManager.hideSoftInputFromWindow(requireActivity().currentFocus?.windowToken, InputMethodManager.HIDE_NOT_ALWAYS)
        }
    }

}