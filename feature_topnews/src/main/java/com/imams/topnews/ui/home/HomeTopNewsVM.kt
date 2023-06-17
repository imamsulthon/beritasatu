package com.imams.topnews.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.imams.newsapi.model.Article
import com.imams.newsapi.repository.NewsRepository
import com.imams.topnews.domain.TopNewsPagingSource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

@HiltViewModel
class HomeTopNewsVM @Inject constructor(
    private val repository: NewsRepository
): ViewModel() {

    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> = _errorMessage

    var country = "id"

    fun topNews(): Flow<PagingData<Article>> = getData().cachedIn(viewModelScope)

    private fun getData(): Flow<PagingData<Article>> {
        return Pager(
            config = PagingConfig(
                initialLoadSize = 3,
                pageSize = 10,
                enablePlaceholders = false
            ),
            pagingSourceFactory = {
                TopNewsPagingSource(repository,
                    country = country,
                    callback = {code, message ->
                        _errorMessage.postValue("code: $code \n $message")
                    }
                )
            }
        ).flow
    }

}