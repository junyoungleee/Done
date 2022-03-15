package com.palette.done.viewmodel

import androidx.lifecycle.*
import com.palette.done.data.db.datasource.DoneRepository
import com.palette.done.data.db.entity.Category
import com.palette.done.data.remote.repository.DoneServerRepository

class CategoryViewModel(val dbRepo: DoneRepository) : ViewModel() {

    var _selectedCategory: MutableLiveData<Int> = MutableLiveData(0)
    val selectedCategory get() = _selectedCategory

    var category: LiveData<List<Category>> = dbRepo.getCategory().asLiveData()

    fun initSelectedCategory() {
        _selectedCategory.value = 0
    }

    fun getSelectedCategory(): Int? {
        return if(selectedCategory.value != 0) {
            selectedCategory.value
        } else {
            null
        }
    }
}

class CategoryViewModelFactory(private val dbRepo: DoneRepository
): ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CategoryViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return CategoryViewModel(dbRepo) as T
        }
        throw IllegalArgumentException("Unknown ViewModel Class")
    }
}