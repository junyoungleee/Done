package com.palette.done.view.main.done

import android.content.Context
import android.os.Bundle
import android.text.InputFilter
import android.util.Log
import android.util.TypedValue
import android.view.*
import androidx.fragment.app.Fragment
import android.view.inputmethod.InputMethodManager
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.palette.done.DoneApplication
import com.palette.done.R
import com.palette.done.data.db.entity.Done
import com.palette.done.databinding.FragmentDoneBinding
import com.palette.done.data.remote.repository.DoneServerRepository
import com.palette.done.view.main.DoneActivity
import com.palette.done.view.main.DoneMode
import com.palette.done.view.main.PlanRoutineActivity
import com.palette.done.view.util.NetworkManager
import com.palette.done.view.util.Util
import com.palette.done.viewmodel.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class DoneFragment(mode: DoneMode) : Fragment() {

    private var _binding: FragmentDoneBinding? = null
    private val binding get() = _binding!!

    private val doneEditVM: DoneEditViewModel by activityViewModels() {
        DoneEditViewModelFactory(DoneServerRepository(), DoneApplication().doneRepository)
    }
    private val doneDateVM: DoneDateViewModel by activityViewModels() {
        DoneDateViewModelFactory(DoneApplication().doneRepository)
    }
    private val planVM: PlanViewModel by activityViewModels() {
        PlanViewModelFactory(DoneServerRepository(), DoneApplication().doneRepository)
    }
    private val routineVM: RoutineViewModel by activityViewModels() {
        RoutineViewModelFactory(DoneServerRepository(), DoneApplication().doneRepository)
    }
    private val categoryVM: CategoryViewModel by activityViewModels() {
        CategoryViewModelFactory(DoneApplication().doneRepository)
    }

    private var isEditPopupOpen: Boolean = false
    private var isCategoryOpen: Boolean = false
    private val editMode = mode

    private val util = Util()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentDoneBinding.inflate(inflater, container, false)

        binding.flWriteContainer.layoutParams =
            LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, DoneApplication.pref.keyboard)

        binding.flWriteContainer.visibility = View.GONE

        setTitle()
        setCategoryAction()
        setWriteButtons()
        setEditText()

        setTagClickListener()

        binding.etDone.clearFocus()
        binding.etDone.requestFocus()
        showKeyboard()

        return binding.root
    }

    private fun setTitle() {
        // edit ????????? title ??????
        when(editMode) {
            DoneMode.DONE -> {
                with(binding) {
                    tvDoneTitle1.visibility = View.VISIBLE
                    tvDoneTitle2.visibility = View.VISIBLE
                    val num = resources.getStringArray(R.array.korean_num)
                    doneDateVM.doneList.observe(viewLifecycleOwner) {
                        if (it.size < 19) { tvDoneIndex.text = num[it.size]
                        } else { tvDoneIndex.text = (it.size + 1).toString() }
                    }
                }
            }
            DoneMode.EDIT_DONE -> {
                with(binding) {
                    tvDoneTitle1.visibility = View.VISIBLE
                    tvDoneTitle2.visibility = View.VISIBLE
                    val num = resources.getStringArray(R.array.korean_num)
                    val index = doneEditVM.oldDoneIndex

                    tvDoneIndex.text = if (index < 19) {
                        num[index]
                    } else {
                        (index+1).toString()
                    }
                }
            }
            else -> {
                with(binding) {
                    tvDoneTitle1.visibility = View.GONE
                    tvDoneTitle2.visibility = View.GONE
                    tvDoneIndex.text = when (editMode) {
                        DoneMode.ADD_PLAN -> getString(R.string.plan_add)
                        DoneMode.EDIT_PLAN -> getString(R.string.plan_item_edit)
                        DoneMode.ADD_ROUTINE -> getString(R.string.routine_add)
                        DoneMode.EDIT_ROUTINE -> getString(R.string.routine_item_edit)
                        else -> ""
                    }
                }
            }
        }
    }

    private fun setWriteButtons() {
        // editText ?????? ?????? ?????? ??????
        binding.etDone.addTextChangedListener(doneEditVM.onDoneTextWatcher())
        doneEditVM.done.observe(viewLifecycleOwner) { done ->
            when(editMode) {
                // ???????????? ?????? / ??????
                DoneMode.DONE -> {
                    if (done == "") {
                        with(binding.btnWrite) {
                            text = getString(R.string.done_btn_hash_tag)
                            setTextSize(TypedValue.COMPLEX_UNIT_SP, 20f)
                            setPadding(util.dpToPx(0), util.dpToPx(0), util.dpToPx(0), util.dpToPx(0))
                            setOnClickListener {
                                binding.etDone.hint = getString(R.string.done_tag_hint)
                                isEditPopupOpen = true
                                setInputFrameLayout()
                                parentFragmentManager.beginTransaction().replace(binding.flWriteContainer.id, DoneTagFragment()).commit()
                                closeCategory()

                                (activity as DoneActivity).scrollingDown()
                            }
                        }
                    } else {
                        with(binding.btnWrite) {
                            text = getString(R.string.done_btn_write)
                            setTextSize(TypedValue.COMPLEX_UNIT_SP, 14f)
                            setPadding(util.dpToPx(12), util.dpToPx(0), util.dpToPx(12), util.dpToPx(0))
                            setOnClickListener {
                                if (NetworkManager.checkNetworkState(requireActivity())) {
                                    // ???????????? ?????? ??????
                                    val category = categoryVM.getSelectedCategoryAsDone()
                                    var tag = doneEditVM.getSelectedHashTagNo()
                                    var routine = doneEditVM.getSelectedRoutineTagNo()
                                    if (tag != null) {
                                        val selected = doneEditVM.selectedHashtag.value!!
                                        if (selected.name != done || selected.category_no != category) {
                                            tag = null
                                        }
                                    }
                                    if (routine != null) {
                                        val selected = doneEditVM.selectedRoutineTag.value!!
                                        if (selected.content != done || selected.categoryNo != category) {
                                            routine = null
                                        }
                                    }

                                    // recyclerview ??????
                                    val date = doneDateVM.getTitleDate()
                                    doneEditVM.addDoneList(binding.etDone.text.toString(), date, category, tag, routine)
                                    // Done ?????? ???, ?????????
                                    binding.etDone.text.clear()
                                    doneEditVM.initSelectedHashTag()
                                    doneEditVM.initSelectedRoutineTag()
                                    categoryVM.initSelectedCategory()
                                } else {
                                    NetworkManager.showRequireNetworkToast(requireActivity())
                                }
                            }
                        }
                    }
                }
                // ??????, ?????? ?????? / ??????
                else -> {
                    with(binding.btnWrite) {
                        setTextSize(TypedValue.COMPLEX_UNIT_SP, 14f)
                        setPadding(util.dpToPx(12), util.dpToPx(0), util.dpToPx(12), util.dpToPx(0))
                        text = when(editMode) {
                            DoneMode.EDIT_DONE -> getString(R.string.done_btn_write)
                            DoneMode.ADD_PLAN, DoneMode.ADD_ROUTINE -> getString(R.string.btn_add)
                            DoneMode.EDIT_PLAN, DoneMode.EDIT_ROUTINE -> getString(R.string.btn_item_edit)
                            else -> ""
                        }
                        setOnClickListener {
                            if (NetworkManager.checkNetworkState(requireActivity())) {
                                // ???????????? ?????? ??????
                                val category = categoryVM.getSelectedCategoryAsDone()
                                var tag = doneEditVM.getSelectedHashTagNo()
                                var routine = doneEditVM.getSelectedRoutineTagNo()

                                if (done != "") {
                                    when (editMode) {
                                        DoneMode.EDIT_DONE -> {
                                            val old = doneEditVM.oldDone
                                            // ?????????????????? ?????? -> ????????????/????????????
                                            if (old.tagNo != null) {
                                                if (old.tagNo == tag) {
                                                    // ??????????????? ???????????? ????????? ?????? -> ??? ?????? ????????? ??????
                                                    if (old.categoryNo != category || old.content != done) {
                                                        tag = null
                                                    }
                                                }
                                            }
                                            // ??????????????? ??????
                                            if (old.routineNo != null) {
                                                if (old.routineNo == routine) {
                                                    // ??????????????? ???????????? ????????? ?????? -> ??? ?????? ????????? ??????
                                                    if (old.categoryNo != category || old.content != done) {
                                                        routine = null
                                                    }
                                                }
                                            }
                                            val new = Done(old.doneId, old.date, done, category, tag, routine)
                                            doneEditVM.updateDoneList(new)
                                            (activity as DoneActivity).closeEditFrame()
                                        }
                                        DoneMode.ADD_PLAN -> {
                                            planVM.insertPlan(done, category)
                                            binding.etDone.text.clear()
                                            categoryVM.initSelectedCategory()
                                            (activity as PlanRoutineActivity).planScrollingDown()
                                        }
                                        DoneMode.EDIT_PLAN -> {
                                            planVM.updatePlan(
                                                planVM.selectedEditPlan.planNo, done, category)
                                            (activity as PlanRoutineActivity).closeEditFrame()
                                        }
                                        DoneMode.ADD_ROUTINE -> {
                                            routineVM.insertRoutine(done, category)
                                            binding.etDone.text.clear()
                                            categoryVM.initSelectedCategory()
                                            (activity as PlanRoutineActivity).routineScrollingDown()
                                        }
                                        DoneMode.EDIT_ROUTINE -> {
                                            routineVM.updateRoutine(
                                                routineVM.selectedEditRoutine.routineNo, done, category)
                                            (activity as PlanRoutineActivity).closeEditFrame()
                                        }
                                    }
                                }
                            } else {
                                NetworkManager.showRequireNetworkToast(requireActivity())
                            }
                        }
                    }
                }
            }
        }
    }

    private fun setTagClickListener() {
        if (editMode == DoneMode.DONE) {
            doneEditVM.selectedHashtag.observe(viewLifecycleOwner) {
                with(binding) {
                    etDone.setText(it.name)
                    categoryVM._selectedCategory.value = it.category_no
                    Log.d("tag_hash", "${categoryVM._selectedCategory.value}")
                }
            }
            doneEditVM.selectedRoutineTag.observe(viewLifecycleOwner) {
                with(binding) {
                    etDone.setText(it.content)
                    categoryVM._selectedCategory.value = it.categoryNo
                    Log.d("tag_routine", "${categoryVM._selectedCategory.value}")
                }
            }
        }
    }

    private fun categoryOpen() {
        isEditPopupOpen = true
        isCategoryOpen = true
        setInputFrameLayout()
        parentFragmentManager.beginTransaction().replace(binding.flWriteContainer.id, DoneCategoryFragment()).commit()
    }

    private fun closeCategory() {
        isCategoryOpen = false
        if (categoryVM.getSelectedCategoryAsDone() == null) {
            binding.btnCategory.setImageDrawable(ContextCompat.getDrawable(requireActivity(), R.drawable.ic_category))
        }
    }

    private fun setCategoryAction() {
        categoryVM.selectedCategory.observe(viewLifecycleOwner) { id ->
            when(id) {
                0, null -> {
                    // ??????????????? ?????? ??????
                    Log.d("category_id_0", "$id")
                    // ??????????????? ?????? ???, ????????? ????????? ????????? ???????????? ?????? ????????? ??? ?????????
                    binding.btnCategory.setOnClickListener {
                        categoryOpen()
                        if(editMode == DoneMode.DONE || editMode == DoneMode.EDIT_DONE) {
                            (activity as DoneActivity).scrollingDown()
                        } else if (editMode == DoneMode.ADD_PLAN || editMode == DoneMode.EDIT_PLAN) {
                            (activity as PlanRoutineActivity).planScrollingDown()
                        } else {
                            (activity as PlanRoutineActivity).routineScrollingDown()
                        }
                        binding.btnCategory.setImageDrawable(ContextCompat.getDrawable(requireActivity(), R.drawable.ic_empty_category))
                    }
                    // ??????????????? ?????? ???, ?????? ????????? ??? ????????? / ????????? ?????? ?????????
                    when(isCategoryOpen) {
                        true -> binding.btnCategory.setImageDrawable(ContextCompat.getDrawable(requireActivity(), R.drawable.ic_empty_category))
                        false -> binding.btnCategory.setImageDrawable(ContextCompat.getDrawable(requireActivity(), R.drawable.ic_category))
                    }
                }
                else -> {
                    // ??????????????? ???????????? ?????? ??????
                    Log.d("category_id", "$id")
                    val imgId = resources.getIdentifier("ic_category_$id", "drawable", requireActivity().packageName)
                    binding.btnCategory.setImageDrawable(ContextCompat.getDrawable(requireActivity(), imgId))
                    binding.btnCategory.setOnClickListener {
                        if(isCategoryOpen) {
                            categoryVM.initSelectedCategory()
                        } else {
                            categoryOpen()
                        }
                    }
                }
            }
        }
    }

    private fun setEditText() {
        // edittext ??????
        with(binding.etDone) {
            setOnClickListener {
                if (isEditPopupOpen) {
                    isEditPopupOpen = false
                }
                setInputFrameLayout()
                if (editMode == DoneMode.DONE && binding.etDone.text.isEmpty()) {
                    binding.etDone.hint = getString(R.string.done_list_write_hint)
                }
                if(editMode == DoneMode.DONE || editMode == DoneMode.EDIT_DONE) {
                    (activity as DoneActivity).scrollingDown()
                } else if (editMode == DoneMode.ADD_PLAN || editMode == DoneMode.EDIT_PLAN) {
                    (activity as PlanRoutineActivity).planScrollingDown()
                } else {
                    (activity as PlanRoutineActivity).routineScrollingDown()
                }
                closeCategory()
            }
            // EditText??? hint
            hint = when(editMode) {
                DoneMode.DONE -> getString(R.string.done_list_write_hint)
                DoneMode.ADD_PLAN -> getString(R.string.plan_hint_add)
                DoneMode.ADD_ROUTINE -> getString(R.string.routine_hint_add)
                else -> ""
            }
            // EditText??? text
            setText(when(editMode) {
                DoneMode.EDIT_DONE -> doneEditVM.oldDone.content
                DoneMode.EDIT_PLAN -> planVM.selectedEditPlan.content
                DoneMode.EDIT_ROUTINE -> routineVM.selectedEditRoutine.content
                else -> ""
            })
            // ?????? ????????? ??????
            val maxLength =  when(editMode) {
                DoneMode.DONE, DoneMode.EDIT_DONE -> 14
                DoneMode.ADD_PLAN, DoneMode.EDIT_PLAN -> 10
                DoneMode.ADD_ROUTINE, DoneMode.EDIT_ROUTINE -> 10
            }
            filters += InputFilter.LengthFilter(maxLength)
        }
    }

    private fun setInputFrameLayout() {
        Log.d("is_popup_open", "$isEditPopupOpen")
        lifecycleScope.launch {
            if (isEditPopupOpen) {
                // ????????????, ??????, ???????????? ???????????? ????????? ??????
                requireActivity().window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING)
                binding.flWriteContainer.visibility = View.VISIBLE
                hideKeyboard()
                delay(100)
                binding.etDone.requestFocus()
//                requireActivity().window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
            } else {
                // ????????????, ??????, ???????????? ???????????? ????????? ??????
//                requireActivity().window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING)
                showKeyboard()
                delay(100)
                // ?????? ????????? ??????
                requireActivity().window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
                binding.flWriteContainer.visibility = View.GONE
                delay(100)
                binding.etDone.requestFocus()
            }

        }
    }

    private fun hideKeyboard() {
        val inputManager = requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputManager.hideSoftInputFromWindow(requireActivity().currentFocus?.windowToken, 0)
        if(editMode == DoneMode.DONE || editMode == DoneMode.EDIT_DONE) {
            (activity as DoneActivity).makeScreenLong()
        } else {
            (activity as PlanRoutineActivity).makeScreenLong()
        }
    }

    private fun showKeyboard() {
        val inputManager = requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputManager.showSoftInput(binding.etDone, InputMethodManager.SHOW_IMPLICIT)
        if(editMode == DoneMode.DONE || editMode == DoneMode.EDIT_DONE) {
            (activity as DoneActivity).makeScreenLong()
        }else {
            (activity as PlanRoutineActivity).makeScreenLong()
        }
    }

    override fun onResume() {
        super.onResume()
        Log.d("fragment_oncreate", "true")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        Log.d("edit_destroy", "true")
        _binding = null
    }
}