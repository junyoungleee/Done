package com.palette.done.view.main

import android.content.Context
import android.graphics.Typeface
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.StyleSpan
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.ScrollView
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.SimpleItemAnimator
import com.palette.done.DoneApplication
import com.palette.done.R
import com.palette.done.data.db.entity.Plan
import com.palette.done.data.db.entity.Routine
import com.palette.done.data.remote.repository.DoneServerRepository
import com.palette.done.databinding.ActivityPlanRoutineBinding
import com.palette.done.view.adapter.PlanAdapter
import com.palette.done.view.adapter.RoutineAdapter
import com.palette.done.view.decoration.DoneToast
import com.palette.done.view.main.done.DoneFragment
import com.palette.done.view.util.Util
import com.palette.done.viewmodel.*
import java.util.*

class PlanRoutineActivity() : AppCompatActivity() {

    private lateinit var binding: ActivityPlanRoutineBinding

    private val planVM: PlanViewModel by viewModels() {
        PlanViewModelFactory(DoneServerRepository(), DoneApplication().doneRepository)
    }
    private val routineVM: RoutineViewModel by viewModels() {
        RoutineViewModelFactory(DoneServerRepository(), DoneApplication().doneRepository)
    }
    private val categoryVM: CategoryViewModel by viewModels() {
        CategoryViewModelFactory(DoneApplication().doneRepository)
    }

    private lateinit var itemMode: ItemMode  // 플랜, 루틴 모드
    private lateinit var date: String  // Plan을 던리스트로 저장할 날짜
    private var isEditMode: Boolean = false  // 편집 모드

    private var util = Util()

    private lateinit var planAdapter: PlanAdapter
    private lateinit var routineAdapter: RoutineAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPlanRoutineBinding.inflate(layoutInflater)
        setContentView(binding.root)

        itemMode = ItemMode.valueOf(intent!!.getStringExtra("mode")!!)
        date = intent.getStringExtra("date").toString()

        routineVM.initRoutine()
        planVM.initPlan()

        setTitle()

        when(itemMode) {
            ItemMode.PLAN -> {
                binding.tvActivityDetail.text = getText(R.string.plan_detail)
                planAdapter = PlanAdapter(this)
                initPlanRecyclerView()
                setPlanAddButton()
                setPlanAddButtonVisibility()
                binding.tvAddItem.text = getString(R.string.plan_add)
            }
            ItemMode.ROUTINE -> {
                binding.tvActivityDetail.text = getText(R.string.routine_detail)
                routineAdapter = RoutineAdapter(this)
                initRoutineRecyclerView()
                setRoutineAddButton()
                setRoutineAddButtonVisibility()
                binding.tvAddItem.text = getString(R.string.routine_add)
            }
        }

        // 편집 모드 설정
        binding.btnEditMode.setOnClickListener {
            isEditMode = !isEditMode
            setEditMode()
            hideKeyboard()
            makeScreenOriginal()
            binding.flItemWrite.visibility = View.GONE
        }

        setBackButton()
        popEditFrame()
    }

    private fun initPlanRecyclerView() {
        with(binding.rcItem) {
            adapter = planAdapter
            layoutManager = LinearLayoutManager(context)
            itemAnimator = null
        }
        planVM.planList.observe(this) { planList ->
            Log.d("plan_list_size", "${planList.size}")
            planList.let { planAdapter.submitList(it) }
        }
        planAdapter.setPlanItemClickListener(object : PlanAdapter.OnPlanItemClickListener {
            override fun onDoneButtonClick(v: View, plan: Plan) {
                // 플랜 Done -> 리스트에서 사라지고 던리스트에 추가
                planVM.donePlan(plan, date)
                val idx = Random().nextInt(3)
                val messages = resources.getStringArray(R.array.plan_message)
                DoneToast.createToast(this@PlanRoutineActivity, plan.content, messages.get(idx))?.show()
            }

            override fun onEditButtonClick(v: View, plan: Plan) {
                // 플랜 수정
                hideKeyboard()
                scrollingUp()
                planVM.selectedEditPlan = plan
                categoryVM._selectedCategory.value = plan.categoryNo
                supportFragmentManager.beginTransaction().replace(binding.flItemWrite.id, DoneFragment(DoneMode.EDIT_PLAN)).commit()
                binding.flItemWrite.visibility = View.VISIBLE
            }

            override fun onDeleteButtonClick(v: View, plan: Plan) {
                // 플랜 삭제
                planVM.deletePlan(plan.planNo)
            }
        })
    }

    private fun initRoutineRecyclerView() {
        with(binding.rcItem) {
            adapter = routineAdapter
            layoutManager = LinearLayoutManager(context)
            itemAnimator = null
        }
        routineVM.routineList.observe(this) { routineList ->
            Log.d("routine_list_size", "${routineList.size}")
            routineList.let { routineAdapter.submitList(it) }
        }
        routineAdapter.setRoutineItemClickListener(object : RoutineAdapter.OnRoutineItemClickListener {
            override fun onEditButtonClick(v: View, routine: Routine) {
                // 루틴 수정
                hideKeyboard()
                scrollingUp()
                routineVM.selectedEditRoutine = routine
                categoryVM._selectedCategory.value = routine.categoryNo
                supportFragmentManager.beginTransaction().replace(binding.flItemWrite.id, DoneFragment(DoneMode.EDIT_ROUTINE)).commit()
                binding.flItemWrite.visibility = View.VISIBLE
            }

            override fun onDeleteButtonClick(v: View, routine: Routine) {
                // 루틴 삭제
                Log.d("routine", "${routine.routineNo}")
                routineVM.deleteRoutine(routine.routineNo)
            }
        })
    }

    private fun setTitle() {
        binding.tvPlanTitle.text = when(isEditMode) {
            true -> {
                when (itemMode) {
                    ItemMode.ROUTINE -> { getString(R.string.routine_edit_title)}
                    ItemMode.PLAN -> {getString(R.string.plan_edit_title)}
                }
            }
            false -> {
                when (itemMode) {
                    ItemMode.ROUTINE -> { getString(R.string.routine_title)}
                    ItemMode.PLAN -> {getString(R.string.plan_title)}
                }
            }
        }

    }

    private fun setEditMode() {
        setTitle()
        if (isEditMode) {
            // 편집모드
            with(binding) {
                btnBack.visibility = View.GONE
                llAddItem.visibility = View.VISIBLE
                with(btnEditMode) {
                    visibility = View.VISIBLE
                    background = ContextCompat.getDrawable(context, R.drawable.all_btn_round_selector)
                    setTextColor(ContextCompat.getColor(context, R.color.white))
                    text = getString(R.string.btn_edit_finish)
                }
            }
        } else {
            // 편집모드 X
            with(binding) {
                btnBack.visibility = View.VISIBLE
                tvPlanTitle.text = when (itemMode) {
                    ItemMode.ROUTINE -> { getString(R.string.routine_title)}
                    ItemMode.PLAN -> {getString(R.string.plan_title)}
                }
                when (itemMode) {
                    ItemMode.PLAN -> setPlanAddButtonVisibility()
                    ItemMode.ROUTINE -> setRoutineAddButtonVisibility()
                }
                with(btnEditMode) {
                    background = ContextCompat.getDrawable(context, R.drawable.all_btn_round_stroke)
                    setTextColor(ContextCompat.getColor(context, R.color.black))
                    text = getString(R.string.btn_edit_mode)
                }
            }
        }
        when (itemMode) {
            ItemMode.PLAN -> setPlanItemEditMode()
            ItemMode.ROUTINE -> setRoutineItemEditMode()
        }
    }

    private fun setPlanAddButtonVisibility() {
        planVM.planList.observe(this) { planList ->
            if(!isEditMode) {
                if (planList.isEmpty()) {
                    binding.llAddItem.visibility = View.VISIBLE
                    binding.btnEditMode.visibility = View.GONE
                } else {
                    binding.llAddItem.visibility = View.GONE
                    binding.btnEditMode.visibility = View.VISIBLE
                }
            }
        }
    }

    private fun setRoutineAddButtonVisibility() {
        routineVM.routineList.observe(this) { routineList ->
            if(!isEditMode) {
                if (routineList.isEmpty()) {
                    binding.llAddItem.visibility = View.VISIBLE
                    binding.btnEditMode.visibility = View.GONE
                } else {
                    binding.llAddItem.visibility = View.GONE
                    binding.btnEditMode.visibility = View.VISIBLE
                }
            }
        }
    }

    private fun setPlanItemEditMode() {
        with(planAdapter) {
            setEditMode(isEditMode)
            notifyDataSetChanged()
        }
    }

    private fun setRoutineItemEditMode() {
        with(routineAdapter) {
            setEditMode(isEditMode)
            notifyDataSetChanged()
        }
    }

    private fun setPlanAddButton() {
        binding.llAddItem.setOnClickListener {
            supportFragmentManager.beginTransaction().replace(binding.flItemWrite.id, DoneFragment(DoneMode.ADD_PLAN)).commit()
            binding.flItemWrite.visibility = View.VISIBLE
            categoryVM.initSelectedCategory()
            hideKeyboard()
            makeScreenLong()
        }
    }

    private fun setRoutineAddButton() {
        binding.llAddItem.setOnClickListener {
            supportFragmentManager.beginTransaction().replace(binding.flItemWrite.id, DoneFragment(DoneMode.ADD_ROUTINE)).commit()
            binding.flItemWrite.visibility = View.VISIBLE
            categoryVM.initSelectedCategory()
            hideKeyboard()
            makeScreenLong()
        }
    }

    private fun popEditFrame() {
        binding.root.setOnClickListener {
            hideKeyboard()
            binding.flItemWrite.visibility = View.GONE
            makeScreenOriginal()
        }
        binding.scrollView.getChildAt(0).setOnClickListener {
            hideKeyboard()
            binding.flItemWrite.visibility = View.GONE
            makeScreenOriginal()
        }
        binding.toolbar.setOnClickListener {
            hideKeyboard()
            binding.flItemWrite.visibility = View.GONE
            makeScreenOriginal()
        }
    }

    fun makeScreenLong() {
        binding.llSubRoot.setPadding(0, 0, 0, DoneApplication.pref.keyboard+util.dpToPx(110))
    }

    private fun makeScreenOriginal() {
        binding.llSubRoot.setPadding(0, 0, 0, util.dpToPx(10))
    }

    fun planScrollingDown() {
        val height = resources.getDimension(R.dimen.plan_routine_item_height)
        planVM.planList.observe(this) { list ->
            val dp = if (list.size <= 4) {
                0
            } else {
                (height*(list.size - 4)).toInt()
            }
            binding.scrollView.smoothScrollTo(0, binding.rcItem.top+dp)
        }
    }

    fun routineScrollingDown() {
        val height = resources.getDimension(R.dimen.plan_routine_item_height)
        routineVM.routineList.observe(this) { list ->
            val dp = if (list.size <= 4) {
                0
            } else {
                (height*(list.size - 4)).toInt()
            }
            binding.scrollView.smoothScrollTo(0, binding.rcItem.top+dp)
        }
    }

    private fun scrollingUp() {
        binding.scrollView.fullScroll(ScrollView.FOCUS_UP)
    }

    private fun hideKeyboard() {
        val inputManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputManager.hideSoftInputFromWindow(currentFocus?.windowToken, 0)
    }

    fun closeEditFrame() {
        hideKeyboard()
        binding.flItemWrite.visibility = View.GONE
        makeScreenOriginal()
    }

    private fun setBackButton() {
        binding.btnBack.setOnClickListener {
            finish()
        }
    }

    override fun onBackPressed() {
        if (!isEditMode) {
            super.onBackPressed()
        }
    }

}