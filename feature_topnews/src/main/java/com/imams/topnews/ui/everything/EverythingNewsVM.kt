package com.imams.topnews.ui.everything

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.imams.core.TheResult
import com.imams.core.utils.wartaLog
import com.imams.newsapi.model.Article
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

    private val _topNews = MutableLiveData<List<Article>>()
    val topNews: LiveData<List<Article>> = _topNews

    fun fetchData() {
        viewModelScope.launch {
            delay(500)
            _categories.postValue(repository.getCategories())
        }
    }

    fun fetchTopNews() {
        viewModelScope.launch {
            when (val result = repository.getTopNews(1, "us")) {
                is TheResult.Success -> {
                    val list = result.data.take(10)
                    _topNews.postValue(list)
                }
                is TheResult.Error -> {}
            }
        }
    }

}