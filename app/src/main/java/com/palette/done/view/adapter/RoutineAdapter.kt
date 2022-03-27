package com.palette.done.view.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.palette.done.data.db.entity.Plan
import com.palette.done.data.db.entity.Routine
import com.palette.done.databinding.ItemPlanRoutineBinding

class RoutineAdapter(val context: Context) : ListAdapter<Routine, RoutineAdapter.RoutineViewHolder>(RoutineComparator()){

    private lateinit var routineItemClickListener: OnRoutineItemClickListener
    private var editMode = false

    fun setEditMode(edit: Boolean) {
        editMode = edit
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RoutineViewHolder {
        val binding = ItemPlanRoutineBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return RoutineAdapter.RoutineViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RoutineViewHolder, position: Int) {
        val routine = getItem(position)
        with(holder.binding) {
            tvContent.text = routine.content
            if (editMode) {
                btnEdit.visibility = View.VISIBLE
                btnDelete.visibility = View.VISIBLE
            } else {
                btnEdit.visibility = View.GONE
                btnDelete.visibility = View.GONE
            }

            val cId = routine.categoryNo
            if (cId != null) {
                val name = "ic_category_$cId"
                val imgId = context.resources.getIdentifier(name, "drawable", context.packageName)
                ivCategory.setImageDrawable(ContextCompat.getDrawable(context, imgId))
            }

            btnDone.visibility = View.GONE
            btnEdit.setOnClickListener {
                routineItemClickListener.onEditButtonClick(it, routine)
            }
            btnDelete.setOnClickListener {
                routineItemClickListener.onDeleteButtonClick(it, routine)
            }
        }
    }

    class RoutineViewHolder(val binding: ItemPlanRoutineBinding): RecyclerView.ViewHolder(binding.root) { }

    private class RoutineComparator: DiffUtil.ItemCallback<Routine>() {
        override fun areItemsTheSame(oldItem: Routine, newItem: Routine): Boolean {
            return oldItem === newItem
        }

        override fun areContentsTheSame(oldItem: Routine, newItem: Routine): Boolean {
            return oldItem.routineNo == newItem.routineNo
        }

    }

    interface OnRoutineItemClickListener {
        fun onEditButtonClick(v: View, routine: Routine)
        fun onDeleteButtonClick(v: View, routine: Routine)
    }

    fun setRoutineItemClickListener(onRoutineItemClickListener: OnRoutineItemClickListener) {
        this.routineItemClickListener = onRoutineItemClickListener
    }
}


