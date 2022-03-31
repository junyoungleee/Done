package com.palette.done.view.my.menu

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import com.palette.done.DoneApplication
import com.palette.done.R
import com.palette.done.databinding.FragmentMyGradeBinding
import com.palette.done.view.my.GradeType
import com.palette.done.view.my.LevelType

class MyGradeFragment : Fragment() {

    private var _binding: FragmentMyGradeBinding? = null
    private val binding get() = _binding!!

    private val message by lazy {requireArguments().getString("msg", "")}
    private val percent by lazy {requireArguments().getInt("percent", 0)}

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentMyGradeBinding.inflate(inflater, container, false)

        setMyGrade(message, percent)

        return binding.root
    }

    private fun setMyGrade(left: String, percent: Int) {
        val level = DoneApplication.pref.level
        val nextLevel = level+1
        with(binding.layoutGrade) {
            tvGradeName.visibility = View.VISIBLE
            tvGradeName.text = GradeType.getGradeName(level)
            if(level == 10) {
                tvLevelProgressStart.text = getString(R.string.level, level)
                tvLevelProgressEnd.text = ""
            } else {
                tvLevelProgressStart.text = getString(R.string.level, level)
                tvLevelProgressEnd.text = getString(R.string.level, nextLevel)
            }
            val grade = GradeType.getGrade(level)
            val imgId = resources.getIdentifier("img_grade_${grade}", "drawable", requireActivity().packageName)
            ivGradeDonedone.setImageDrawable(ContextCompat.getDrawable(requireActivity(), imgId))

            tvLevelLeftDetail.text = left
            pbLevel.progress = LevelType.getProgressPercent(level, percent)
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}

