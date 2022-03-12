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

    private var isEditPopupOpen: Boolean = false
    private val editMode = mode

    private val util = Util()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FragmentDoneBinding.inflate(inflater, container, false)

        binding.flWriteContainer.layoutParams =
            LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, DoneApplication.pref.keyboard)

        binding.etDone.requestFocus()

        setTitle()

        setCategoryButton()
        setWriteButtons()
        setEditText()

        return binding.root
    }

    private fun setTitle() {
        // edit 상자의 title 세팅
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
        // editText 옆의 입력 버튼 세팅
        binding.etDone.addTextChangedListener(doneEditVM.onDoneTextWatcher())
        doneEditVM.done.observe(viewLifecycleOwner) { done ->
            when(editMode) {
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
                            }
                        }
                    } else {
                        with(binding.btnWrite) {
                            text = getString(R.string.done_btn_write)
                            setTextSize(TypedValue.COMPLEX_UNIT_SP, 14f)
                            setPadding(util.dpToPx(12), util.dpToPx(0), util.dpToPx(12), util.dpToPx(0))
                            setOnClickListener {
                                val category: Int? = null
                                val tag: Int? = null
                                val routine: Int? = null

                                // recyclerview 추가
                                val date = doneDateVM.getTitleDate()
                                Log.d("date_save", "$date")
                                doneEditVM.addDoneList(date, binding.etDone.text.toString(), null, null, null)
                                binding.etDone.text.clear()
                            }
                        }
                    }
                }
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
                            val category: Int? = null
                            val tag: Int? = null
                            val routine: Int? = null

                            when (editMode) {
                                DoneMode.EDIT_DONE -> {
                                    val old = doneEditVM.oldDone
                                    if (done != old.content || category != old.categoryNo || tag != old.tagNo || routine != old.routineNo) {
                                        val new = Done(old.doneId, old.date, done, category, tag, routine)
                                        Log.d("done_edit", "id = ${old.doneId}")
//                                        doneEditVM.updateDoneList(new)
                                    }
                                    hideKeyboard()
                                    (activity as DoneActivity).closeEditFrame()
                                }
                                DoneMode.ADD_PLAN -> {
                                    if (done != "") {
                                        planVM.insertPlan(done, null)
                                    }
                                    binding.etDone.text.clear()
                                }
                                DoneMode.EDIT_PLAN -> {
                                    val category: Int? = null  // 새로 선택한 카테고리 받아오기
                                    if (done != planVM.selectedEditPlan.content || category != planVM.selectedEditPlan.categoryNo) {
                                        // db, server에 plan 수정
                                        planVM.updatePlan(planVM.selectedEditPlan.planNo, done, category)
                                    }
                                    hideKeyboard()
                                    (activity as DoneActivity).closeEditFrame()
                                }
                                DoneMode.ADD_ROUTINE -> {
                                    if (done != "") {
                                        routineVM.insertRoutine(done, null)
                                    }
                                    binding.etDone.text.clear()
                                }
                                DoneMode.EDIT_ROUTINE -> {
                                    val category: Int? = null
                                    if (done != routineVM.selectedEditRoutine.content || category != routineVM.selectedEditRoutine.categoryNo) {
                                        routineVM.updateRoutine(routineVM.selectedEditRoutine.routineNo, done, category)
                                    }
                                    hideKeyboard()
                                    (activity as DoneActivity).closeEditFrame()
                                }
                            }

                        }
                    }
                }
            }
        }
    }

    private fun setCategoryButton() {
        binding.btnCategory.setOnClickListener {
            if (!isEditPopupOpen) {
                isEditPopupOpen = !isEditPopupOpen
            }
            setInputFrameLayout()
//            binding.btnCategory.background = ContextCompat.getDrawable(requireActivity(), R.drawable.ic_empty_category)
            parentFragmentManager.beginTransaction().replace(binding.flWriteContainer.id, DoneCategoryFragment()).commit()
        }
    }

    private fun setEditText() {
        // edittext 설정
        with(binding.etDone) {
            setOnClickListener {
                if (isEditPopupOpen) {
                    isEditPopupOpen = false
                    setInputFrameLayout()
                }
                if (editMode == DoneMode.DONE && binding.etDone.text.isEmpty()) {
                    binding.etDone.hint = getString(R.string.done_list_write_hint)
                }
            }
            // EditText의 hint
            hint = when(editMode) {
                DoneMode.DONE -> getString(R.string.done_list_write_hint)
                DoneMode.ADD_PLAN -> getString(R.string.plan_hint_add)
                DoneMode.ADD_ROUTINE -> getString(R.string.routine_hint_add)
                else -> ""
            }
            // EditText의 text
            setText(when(editMode) {
                DoneMode.EDIT_DONE -> doneEditVM.oldDone.content
                DoneMode.EDIT_PLAN -> planVM.selectedEditPlan.content
                DoneMode.EDIT_ROUTINE -> routineVM.selectedEditRoutine.content
                else -> ""
            })
            // 입력 글자수 제한
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