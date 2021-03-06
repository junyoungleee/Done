package com.palette.done.view.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.palette.done.R
import com.palette.done.data.db.entity.Category
import com.palette.done.data.remote.model.dones.Tags
import com.palette.done.databinding.ItemCategoryBinding
import com.palette.done.databinding.ItemTagBinding
import kotlin.coroutines.coroutineContext

class CategoryAdapter(val context: Context) : ListAdapter<Category, CategoryAdapter.CategoryViewHolder>(CategoryComparator()){
    private lateinit var categoryClickListener: OnCategoryClickListener

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
        val binding = ItemCategoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CategoryViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {
        val category = getItem(position)
        with(holder.binding) {
            val id = category.categoryNo
            val name = "ic_category_$id"
            Log.d("category_re", name)
            val imgId = context.resources.getIdentifier(name, "drawable", context.packageName)
            ivCategory.setImageDrawable(ContextCompat.getDrawable(context, imgId))
            ivCategory.setOnClickListener {
                categoryClickListener.onCategoryClick(it, category)
            }
        }
    }

    class CategoryViewHolder(val binding: ItemCategoryBinding): RecyclerView.ViewHolder(binding.root) { }

    private class CategoryComparator: DiffUtil.ItemCallback<Category>() {
        override fun areItemsTheSame(oldItem: Category, newItem: Category): Boolean {
            return oldItem === newItem
        }

        override fun areContentsTheSame(oldItem: Category, newItem: Category): Boolean {
            return oldItem.categoryNo == newItem.categoryNo
        }
    }

    interface OnCategoryClickListener {
        fun onCategoryClick(v: View, category: Category)
    }

    fun setCategoryClickListener(onRoutineItemClickListener: OnCategoryClickListener) {
        this.categoryClickListener = onRoutineItemClickListener
    }
}