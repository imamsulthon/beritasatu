package com.imams.searchnews.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.imams.newsapi.model.Category
import com.imams.newsapi.repository.NewsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EverythingNewsVM @Inject constructor(
    private val repository: NewsRepository,
): ViewModel() {

    private val _categories = MutableLiveData<List<Category>>()
    val categories: LiveData<List<Category>> = _categories

    fun fetchData() {
        viewModelScope.launch {
            delay(1000)
            _categories.postValue(repository.getCategories())
        }
    }


}