package com.palette.done.view.signin

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.AdapterView.OnItemSelectedListener
import android.widget.ArrayAdapter
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.palette.done.R
import com.palette.done.databinding.FragmentObAlarmBinding
import com.palette.done.repository.MemberRepository
import com.palette.done.view.adapter.ObAlarmRecyclerViewAdapter
import com.palette.done.view.decoration.RecyclerViewDecoration
import com.palette.done.view.main.MainActivity
import com.palette.done.viewmodel.LoginViewModelFactory
import com.palette.done.viewmodel.OnBoardingViewModel
import com.palette.done.viewmodel.OnBoardingViewModelFactory
import java.text.DateFormatSymbols

class ObAlarmFragment : Fragment() {

    private var _binding: FragmentObAlarmBinding? = null
    private val binding get() = _binding!!

    private val onBoardingVM: OnBoardingViewModel by activityViewModels { OnBoardingViewModelFactory(MemberRepository()) }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FragmentObAlarmBinding.inflate(inflater, container, false)

        onBoardingVM.alarmWeekday.observe(viewLifecycleOwner) {
            binding.btnNext.isEnabled = onBoardingVM.alarmWeekday.value!!.isNotEmpty()
        }
        setNextButton()

        setAmPmSpinner()
        setHourSpinner()
        setMinSpinner()

        setAlarmWeekBtn()

        return binding.root
    }

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

    private fun setAmPmSpinner() {
        val ampm = resources.getStringArray(R.array.ampm)

        val adapter = ArrayAdapter(requireContext(), R.layout.support_simple_spinner_dropdown_item, ampm)
        binding.spAmPm.adapter = adapter
        binding.spAmPm.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                onBoardingVM.alarmAmPm.value = binding.spAmPm.getItemAtPosition(position).toString()
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {
                onBoardingVM.alarmAmPm.value = binding.spAmPm.getItemAtPosition(0).toString()
            }
        }
    }

    private fun setHourSpinner() {
        val hours = resources.getStringArray(R.array.hour)
        val adapter = ArrayAdapter(requireContext(), R.layout.support_simple_spinner_dropdown_item, hours)
        binding.spHour.adapter = adapter
        binding.spHour.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                onBoardingVM.alarmHour.value = binding.spHour.getItemAtPosition(position).toString().toInt()
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {
                onBoardingVM.alarmHour.value = binding.spHour.getItemAtPosition(0).toString().toInt()
            }
        }
    }

    private fun setMinSpinner() {
        val mins = resources.getStringArray(R.array.min)
        val adapter = ArrayAdapter(requireContext(), R.layout.support_simple_spinner_dropdown_item, mins)
        binding.spMin.adapter = adapter
        binding.spMin.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                onBoardingVM.alarmMin.value = binding.spMin.getItemAtPosition(position).toString().toInt()
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {
                onBoardingVM.alarmMin.value = binding.spMin.getItemAtPosition(0).toString().toInt()
            }
        }
    }

    private fun setAlarmWeekBtn() {
        binding.rcWeekBtn.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        binding.rcWeekBtn.addItemDecoration(RecyclerViewDecoration(9))

        val adapter = ObAlarmRecyclerViewAdapter()
        binding.rcWeekBtn.adapter = adapter
        adapter.setWeekItemClickListener(object : ObAlarmRecyclerViewAdapter.OnWeekItemClickListener {
            override fun onClick(v: View, position: Int) {
                v.isSelected = !v.isSelected
                onBoardingVM.setButtonSelectedAction(v.isSelected, position)
            }
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}