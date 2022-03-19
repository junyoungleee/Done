package com.palette.done.view.signin

import android.content.Intent
import android.content.res.Resources
import android.graphics.Point
import android.os.Build
import android.os.Bundle
import android.util.DisplayMetrics
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.NumberPicker
import android.widget.TimePicker
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.palette.done.data.remote.repository.MemberRepository
import com.palette.done.databinding.FragmentObAlarmBinding
import com.palette.done.view.adapter.ObAlarmRecyclerViewAdapter
import com.palette.done.view.decoration.RecyclerViewDecoration
import com.palette.done.view.main.MainActivity
import com.palette.done.view.util.Util
import com.palette.done.viewmodel.OnBoardingViewModel
import com.palette.done.viewmodel.OnBoardingViewModelFactory
import okhttp3.internal.wait
import java.text.DecimalFormat


class ObAlarmFragment : Fragment() {

    private var _binding: FragmentObAlarmBinding? = null
    private val binding get() = _binding!!

    private val onBoardingVM: OnBoardingViewModel by activityViewModels { OnBoardingViewModelFactory(
        MemberRepository()
    ) }

    private val INTERVAL = 5
    private val FORMATTER: DecimalFormat = DecimalFormat("00")

    private var minutePicker: NumberPicker? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentObAlarmBinding.inflate(inflater, container, false)

        onBoardingVM.alarmWeekday.observe(viewLifecycleOwner) {
            binding.btnNext.isEnabled = onBoardingVM.alarmWeekday.value!!.isNotEmpty()
        }

        setMinutePicker()

        setNextButton()
        setTimeTicker()
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

    fun setMinutePicker() {
        val numValues = 60 / INTERVAL
        val displayedValues = arrayOfNulls<String>(numValues)
        for (i in 0 until numValues) {
            displayedValues[i] = FORMATTER.format(i * INTERVAL)
        }
        val minute: View = binding.tpAlarmTime.findViewById(Resources.getSystem().getIdentifier("minute", "id", "android"))
        if (minute != null && minute is NumberPicker) {
            minutePicker = minute
            minutePicker!!.minValue = 0
            minutePicker!!.maxValue = numValues - 1
            minutePicker!!.displayedValues = displayedValues
        }
    }

    fun getMinute(): Int {
        return if (minutePicker != null) {
            minutePicker!!.value * INTERVAL
        } else {
            binding.tpAlarmTime.minute
        }
    }

    private fun setTimeTicker() {
        binding.tpAlarmTime.hour = 9
        binding.tpAlarmTime.minute = 0
        binding.tpAlarmTime.setOnTimeChangedListener { view, hourOfDay, minute ->
            onBoardingVM.alarmHour.value = hourOfDay
            onBoardingVM.alarmMin.value = minute*5
            Log.d("ob_time_changed", "${onBoardingVM.alarmHour.value} : ${onBoardingVM.alarmMin.value}")
        }
    }

    private fun setAlarmWeekBtn() {
        binding.rcWeekBtn.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        binding.rcWeekBtn.addItemDecoration(RecyclerViewDecoration(9))
        val util = Util()
        val display = requireActivity().windowManager.defaultDisplay
        val size = Point()
        display.getRealSize(size)
        val adapter = ObAlarmRecyclerViewAdapter(size.x - util.dpToPx(40+9*6))
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