package com.palette.done.view.main.done

import android.content.Intent
import android.graphics.Typeface
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.StyleSpan
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.google.android.flexbox.*
import com.palette.done.DoneApplication
import com.palette.done.R
import com.palette.done.data.db.entity.Routine
import com.palette.done.databinding.FragmentDoneRoutineBinding
import com.palette.done.data.remote.repository.DoneServerRepository
import com.palette.done.view.adapter.RoutineAdapter
import com.palette.done.view.adapter.RoutineTagAdapter
import com.palette.done.view.main.ItemMode
import com.palette.done.view.main.PlanRoutineActivity
import com.palette.done.viewmodel.DoneEditViewModel
import com.palette.done.viewmodel.DoneEditViewModelFactory
import com.palette.done.viewmodel.RoutineViewModel
import com.palette.done.viewmodel.RoutineViewModelFactory

class DoneRoutineFragment : Fragment() {

    private var _binding: FragmentDoneRoutineBinding? = null
    private val binding get() = _binding!!

    private val routineVM: RoutineViewModel by activityViewModels() {
        RoutineViewModelFactory(DoneServerRepository(), DoneApplication().doneRepository)
    }
    private val doneVM: DoneEditViewModel by activityViewModels() {
        DoneEditViewModelFactory(DoneServerRepository(), DoneApplication().doneRepository)
    }

    private val tagAdapter = RoutineTagAdapter()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FragmentDoneRoutineBinding.inflate(inflater, container, false)

        setEditAndAddButton()
        setRoutineDetailTextSpan()

        initRoutineTagRecyclerView()

        return binding.root
    }

    private fun initRoutineTagRecyclerView() {
        val tagLayoutManager = FlexboxLayoutManager(context)
        tagLayoutManager.flexDirection = FlexDirection.ROW
        tagLayoutManager.justifyContent = JustifyContent.CENTER

        val tagDecoration = FlexboxItemDecoration(context)
        tagDecoration.setDrawable(ContextCompat.getDrawable(requireActivity(), R.drawable.tag_divider))

        with(binding.rcRoutine) {
            adapter = tagAdapter
            layoutManager = tagLayoutManager
            overScrollMode = View.OVER_SCROLL_NEVER
            addItemDecoration(tagDecoration)
        }
        routineVM.routineList.observe(viewLifecycleOwner) { routineList ->
            routineList.let { tagAdapter.submitList(it) }
        }
        tagAdapter.setTagItemClickListener(object : RoutineTagAdapter.OnTagItemClickListener {
            override fun onTagClick(v: View, routine: Routine) {
                // 루틴 태그 클릭 리스너
                Log.d("tag_routine_fragment", "${routine.content}")
                doneVM._selectedRoutineTag.value = routine
                tagAdapter.notifyDataSetChanged()
            }
        })
        doneVM.selectedRoutineTag.observe(viewLifecycleOwner) {
            if (it.routineNo == 0) {
                tagAdapter.initClickedPosition()
                tagAdapter.notifyDataSetChanged()
            }
        }
    }

    private fun setEditAndAddButton() {
        routineVM.routineList.observe(viewLifecycleOwner) { routines ->
            with(binding) {
                if (routines.isEmpty()) {
                    btnRoutineEdit.visibility = View.INVISIBLE
                    llAddRoutine.visibility = View.VISIBLE
                    llAddRoutineButton.setOnClickListener {
                        val intent = Intent(requireActivity(), PlanRoutineActivity::class.java)
                        intent.putExtra("mode", ItemMode.ROUTINE.name)
                        startActivity(intent)
                    }
                } else {
                    btnRoutineEdit.visibility = View.VISIBLE
                    btnRoutineEdit.setOnClickListener {
                        val intent = Intent(requireActivity(), PlanRoutineActivity::class.java)
                        intent.putExtra("mode", ItemMode.ROUTINE.name)
                        startActivity(intent)
                    }
                    llAddRoutine.visibility = View.GONE
                }
            }
        }
    }

    private fun setRoutineDetailTextSpan() {
        SpannableStringBuilder(getString(R.string.routine_detail)).apply {
            setSpan(
                StyleSpan(Typeface.BOLD), 0, 23, Spannable.SPAN_INCLUSIVE_INCLUSIVE
            )
            binding.tvRoutineDetail.text = this
        }
    }
    

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}