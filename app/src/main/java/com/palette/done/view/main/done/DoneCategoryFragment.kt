package com.palette.done.view.main.done

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.activityViewModels
import com.google.android.flexbox.FlexDirection
import com.google.android.flexbox.FlexboxItemDecoration
import com.google.android.flexbox.FlexboxLayoutManager
import com.google.android.flexbox.JustifyContent
import com.palette.done.DoneApplication
import com.palette.done.R
import com.palette.done.data.db.entity.Category
import com.palette.done.data.remote.repository.DoneServerRepository
import com.palette.done.databinding.FragmentDoneCategoryBinding
import com.palette.done.view.adapter.CategoryAdapter
import com.palette.done.viewmodel.CategoryViewModel
import com.palette.done.viewmodel.CategoryViewModelFactory
import com.palette.done.viewmodel.DoneEditViewModel
import com.palette.done.viewmodel.DoneEditViewModelFactory

class DoneCategoryFragment : Fragment() {

    private var _binding: FragmentDoneCategoryBinding? = null
    private val binding get() = _binding!!

    private val categoryVM: CategoryViewModel by activityViewModels() {
        CategoryViewModelFactory((requireActivity().application as DoneApplication).doneRepository)
    }

    private lateinit var categoryAdapter : CategoryAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentDoneCategoryBinding.inflate(inflater, container, false)

        categoryAdapter = CategoryAdapter(requireActivity())
        initCategoryRecyclerView()

        return binding.root
    }

    private fun initCategoryRecyclerView() {
        val categoryLayoutManager = FlexboxLayoutManager(context)
        categoryLayoutManager.flexDirection = FlexDirection.ROW
        categoryLayoutManager.justifyContent = JustifyContent.CENTER

        val decoration = FlexboxItemDecoration(context)
        decoration.setDrawable(ContextCompat.getDrawable(requireActivity(), R.drawable.tag_divider))

        with(binding.rcCategory) {
            adapter = categoryAdapter
            layoutManager = categoryLayoutManager
            overScrollMode = View.OVER_SCROLL_NEVER
            addItemDecoration(decoration)
        }
        categoryVM.category.observe(viewLifecycleOwner) {
            categoryAdapter.submitList(it)
        }

        categoryAdapter.setCategoryClickListener(object : CategoryAdapter.OnCategoryClickListener{
            override fun onCategoryClick(v: View, category: Category) {
                categoryVM._selectedCategory.value = category.categoryNo
            }
        })
    }
}