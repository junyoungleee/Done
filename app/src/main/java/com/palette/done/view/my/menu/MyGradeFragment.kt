package com.palette.done.view.my.menu

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import com.palette.done.DoneApplication
import com.palette.done.R
import com.palette.done.databinding.FragmentMyGradeBinding
import com.palette.done.view.my.GradeType

class MyGradeFragment : Fragment() {

    private var _binding: FragmentMyGradeBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentMyGradeBinding.inflate(inflater, container, false)

        setMyGrade("", 0)

        return binding.root
    }

    private fun setMyGrade(left: String, percent: Int) {
        val level = DoneApplication.pref.level
        with(binding.layoutGrade) {
            tvGradeName.visibility = View.VISIBLE
            tvGradeName.text = GradeType.getGradeName(level)
            if(level == 10) {
                tvLevelProgressStart.text = "LV.$level"
                tvLevelProgressEnd.text = ""
            } else {
                tvLevelProgressStart.text = "LV.$level"
                tvLevelProgressEnd.text = "LV.${level+1}"
            }
            val grade = GradeType.getGrade(level)
            val imgId = resources.getIdentifier("img_grade_${grade}", "drawable", requireActivity().packageName)
            ivGradeDonedone.setImageDrawable(ContextCompat.getDrawable(requireActivity(), imgId))

//            tvLevelLeftDetail.text = left
//            pbLevel.progress = percent
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}

