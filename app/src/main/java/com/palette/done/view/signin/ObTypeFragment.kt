package com.palette.done.view.signin


import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.activityViewModels
import androidx.viewpager2.widget.ViewPager2
import com.palette.done.DoneApplication
import com.palette.done.R
import com.palette.done.databinding.FragmentObTypeBinding
import com.palette.done.data.remote.repository.MemberRepository
import com.palette.done.view.main.MainActivity
import com.palette.done.view.my.MyEditActivity
import com.palette.done.viewmodel.MyEditViewModel
import com.palette.done.viewmodel.MyEditViewModelFactory
import com.palette.done.viewmodel.OnBoardingViewModel
import com.palette.done.viewmodel.OnBoardingViewModelFactory

class ObTypeFragment(edit: Boolean = false) : Fragment() {

    private var _binding: FragmentObTypeBinding? = null
    private val binding get() = _binding!!

    private val editMode: Boolean = edit

    private val myEditVM: MyEditViewModel by activityViewModels { MyEditViewModelFactory(
        MemberRepository()
    ) }
    private val onBoardingVM: OnBoardingViewModel by activityViewModels { OnBoardingViewModelFactory(
        MemberRepository()
    ) }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentObTypeBinding.inflate(inflater, container, false)

        if (!editMode) {
            // 온보딩 화면인 경우
            onBoardingVM.nickname.observe(viewLifecycleOwner) {
                binding.tvNickname.text = onBoardingVM.nickname.value.toString()
            }
            setFirstTypeButton()
            setNextButton()
        } else {
            // 사용자 정보 수정인 경우
            initTypeEditFragment()
            setEditTypeButton()
            setEditButton()
        }

        return binding.root
    }

    private fun setJTypeButtonClick() {
        binding.btnNext.isEnabled = true
        binding.llTypeP.isSelected = false
        binding.llTypeJ.isSelected = true
    }

    private fun setPTypeButtonClick() {
        binding.btnNext.isEnabled = true
        binding.llTypeP.isSelected = true
        binding.llTypeJ.isSelected = false
    }

    // 온보딩인 경우 ---------------------------------------------------------------------------------
    private fun setFirstTypeButton() {
        binding.llTypeJ.setOnClickListener {
            onBoardingVM.userType.value = "j"
            setJTypeButtonClick()
        }
        binding.llTypeP.setOnClickListener {
            onBoardingVM.userType.value = "p"
            setPTypeButtonClick()
        }
    }

//    private fun setNextButton() {
//        val viewPager = activity?.findViewById<ViewPager2>(R.id.view_pager_on_boarding)
//        binding.btnNext.setOnClickListener {
//            viewPager?.currentItem = 2
//            setIndicator() // 다음 버튼 클릭 시 indicator 수정
//        }
//    }

    private fun setNextButton() {
        // 기존 스택 없애고 메인 화면이 루트
        binding.btnNext.setOnClickListener {
            onBoardingVM.patchMemberProfile()
            onBoardingVM.patchSuccess.observe(viewLifecycleOwner) {
                if (it) {
                    val intent = Intent(requireContext(), MainActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    startActivity(intent)
                }
            }
        }
    }

    private fun setIndicator() {
        val indicator2 = activity?.findViewById<ImageView>(R.id.iv_indicator_second)
        val indicator3 = activity?.findViewById<ImageView>(R.id.iv_indicator_third)

        indicator2!!.setImageResource(R.drawable.ic_indicator_now)
        indicator3!!.setImageResource(R.drawable.ic_indicator_now)
    }

    // 사용자 정보 수정인 경우 -------------------------------------------------------------------------
    private fun initTypeEditFragment() {
        with(binding) {
            llTitle.visibility = View.GONE
            tvTypeDetail.visibility = View.GONE
            marginBottom.visibility = View.VISIBLE
            btnNext.isEnabled = false
            btnNext.text = getString(R.string.my_edit_type_btn)
        }

        val old = DoneApplication.pref.type
        if (old == "j") {
            setJTypeButtonClick()
            myEditVM.type = "j"
        } else if (old == "p") {
            setPTypeButtonClick()
            myEditVM.type = "p"
        }
    }

    private fun setEditTypeButton() {
        binding.llTypeJ.setOnClickListener {
            setJTypeButtonClick()
            myEditVM.checkType("j")
        }
        binding.llTypeP.setOnClickListener {
            setPTypeButtonClick()
            myEditVM.checkType("p")
        }
    }

    private fun setEditButton() {
        // 타입 변경
        val type = DoneApplication.pref.type
        myEditVM.typeResult.observe(viewLifecycleOwner) { result ->
            binding.btnNext.isEnabled = result
        }
        binding.btnNext.setOnClickListener {
            if (type != myEditVM.type) {
                myEditVM.patchType()
                myEditVM.isResponse.observe(viewLifecycleOwner) { response ->
                    if (response) {
                        myEditVM.initResponseValue()
                        (activity as MyEditActivity).finish()
                    }
                }
            } else {
                (activity as MyEditActivity).finish()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}