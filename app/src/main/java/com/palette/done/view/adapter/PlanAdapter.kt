package com.palette.done.view.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.palette.done.data.db.entity.Done
import com.palette.done.data.db.entity.Plan
import com.palette.done.databinding.ItemDoneBinding
import com.palette.done.databinding.ItemPlanRoutineBinding

class PlanAdapter(val context: Context) : ListAdapter<Plan, PlanAdapter.PlanViewHolder>(PlanComparator())  {

    private lateinit var planItemClickListener: OnPlanItemClickListener
    private var editMode = false

    fun setEditMode(edit: Boolean) {
        editMode = edit
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlanViewHolder {
        val binding = ItemPlanRoutineBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PlanViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PlanViewHolder, position: Int) {
        val plan = getItem(position)
        with(holder.binding) {
            tvContent.text = plan.content
            if (editMode) {
                btnDone.visibility = View.GONE
                btnEdit.visibility = View.VISIBLE
                btnDelete.visibility = View.VISIBLE
            } else {
                btnDone.visibility = View.VISIBLE
                btnEdit.visibility = View.GONE
                btnDelete.visibility = View.GONE
            }

            val cId = plan.categoryNo
            if (cId != null) {
                val name = "ic_category_$cId"
                val imgId = context.resources.getIdentifier(name, "drawable", context.packageName)
                ivCategory.setImageDrawable(ContextCompat.getDrawable(context, imgId))
            }

            btnDone.setOnClickListener {
                planItemClickListener.onDoneButtonClick(it, plan)
            }
            btnEdit.setOnClickListener {
                planItemClickListener.onEditButtonClick(it, plan)
            }
            btnDelete.setOnClickListener {
                planItemClickListener.onDeleteButtonClick(it, plan)
            }
        }
    }

    class PlanViewHolder(val binding: ItemPlanRoutineBinding): RecyclerView.ViewHolder(binding.root) { }

    private class PlanComparator: DiffUtil.ItemCallback<Plan>() {
        override fun areItemsTheSame(oldItem: Plan, newItem: Plan): Boolean {
            return oldItem === newItem
        }

        override fun areContentsTheSame(oldItem: Plan, newItem: Plan): Boolean {
            return oldItem.planNo == newItem.planNo
        }

    }

    interface OnPlanItemClickListener {
        fun onDoneButtonClick(v: View, plan: Plan)
        fun onEditButtonClick(v: View, plan: Plan)
        fun onDeleteButtonClick(v: View, plan: Plan)
    }

    fun setPlanItemClickListener(onPlanItemClickListener: OnPlanItemClickListener) {
        this.planItemClickListener = onPlanItemClickListener
    }
}