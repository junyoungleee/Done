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
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.palette.done.DoneApplication
import com.palette.done.R
import com.palette.done.data.db.entity.Plan
import com.palette.done.databinding.ActivityPlanBinding
import com.palette.done.repository.DoneServerRepository
import com.palette.done.view.adapter.PlanAdapter
import com.palette.done.view.main.done.DoneFragment
import com.palette.done.viewmodel.PlanViewModel
import com.palette.done.viewmodel.PlanViewModelFactory

class PlanActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPlanBinding

    private val planVM: PlanViewModel by viewModels() {
        PlanViewModelFactory(DoneServerRepository(), DoneApplication().doneRepository)
    }

    private var isEditMode: Boolean = false  // 편집 모드
    private lateinit var date: String  // Plan을 던리스트로 저장할 날짜
    private var planAdapter = PlanAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPlanBinding.inflate(layoutInflater)
        setContentView(binding.root)

        date = intent.getStringExtra("date").toString()

        initPlanRecyclerView()
        binding.btnPlanEdit.setOnClickListener {
            isEditMode = !isEditMode
            setEditMode()
        }

        setAddButton()
        setBackButton()

        popEditFrame()

        setDetailTextSpan()
    }

    private fun initPlanRecyclerView() {
        with(binding.rcPlan) {
            adapter = planAdapter
            layoutManager = LinearLayoutManager(context)
        }
        planVM.planList.observe(this) { planList ->
            Log.d("plan_list_size", "${planList.size}")
            planList.let { planAdapter.submitList(it) }
            if (planList.isEmpty()) {
                binding.llAddPlan.visibility = View.VISIBLE
            } else {
                if (isEditMode) {
                    binding.llAddPlan.visibility = View.VISIBLE
                } else {
                    binding.llAddPlan.visibility = View.GONE
                }
                binding.btnPlanEdit.visibility = View.VISIBLE
            }
        }
        planAdapter.setPlanItemClickListener(object : PlanAdapter.OnPlanItemClickListener {
            override fun onDoneButtonClick(v: View, plan: Plan) {
                // 플랜 Done -> 리스트에서 사라지고 던리스트에 추가

            }

            override fun onEditButtonClick(v: View, plan: Plan) {
                // 플랜 수정
                planVM.selectedEditPlan = plan
                supportFragmentManager.beginTransaction().replace(binding.flPlanWrite.id, DoneFragment(DoneMode.EDIT_PLAN)).commit()
                binding.flPlanWrite.visibility = View.VISIBLE
            }

            override fun onDeleteButtonClick(v: View, plan: Plan) {
                // 플랜 삭제
                planVM.deletePlan(plan.planNo)
            }

        })
    }

    private fun setEditMode() {
        if (isEditMode) {
            with(binding) {
                btnBack.visibility = View.GONE
                tvPlanTitle.text = getString(R.string.plan_edit_title)
                llAddPlan.visibility = View.VISIBLE
                with(btnPlanEdit) {
                    background = ContextCompat.getDrawable(context, R.drawable.all_btn_round_selector)
                    setTextColor(ContextCompat.getColor(context, R.color.white))
                    text = getString(R.string.plan_btn_edit_finish)
                }
            }
        } else {
            with(binding) {
                btnBack.visibility = View.VISIBLE
                tvPlanTitle.text = getString(R.string.plan_title)
                llAddPlan.visibility = View.GONE
                with(btnPlanEdit) {
                    background = ContextCompat.getDrawable(context, R.drawable.all_btn_round_stroke)
                    setTextColor(ContextCompat.getColor(context, R.color.black))
                    text = getString(R.string.plan_btn_edit_mode)
                }
            }
        }
        // recyclerview item buttons' visibility update
        with(planAdapter) {
            setEditMode(isEditMode)
            notifyDataSetChanged()
        }
    }

    private fun setAddButton() {
        binding.llAddPlan.setOnClickListener {
            supportFragmentManager.beginTransaction().replace(binding.flPlanWrite.id, DoneFragment(DoneMode.ADD_PLAN)).commit()
            binding.flPlanWrite.visibility = View.VISIBLE
        }
    }

    private fun popEditFrame() {
        binding.root.setOnClickListener {
            hideKeyboard()
            binding.flPlanWrite.visibility = View.GONE
        }
    }

    private fun hideKeyboard() {
        val inputManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputManager.hideSoftInputFromWindow(currentFocus?.windowToken, 0)
    }

    private fun setDetailTextSpan() {
        SpannableStringBuilder(getString(R.string.plan_detail)).apply {
            setSpan(
                StyleSpan(Typeface.BOLD), 0, 19, Spannable.SPAN_INCLUSIVE_INCLUSIVE
            )
            binding.tvDetailPlan.text = this
        }
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