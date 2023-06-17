package com.imams.searchnews.ui.newssource

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.imams.newsapi.model.Source
import com.imams.newsapi.repository.NewsRepository
import com.imams.searchnews.domain.NewsSourcePagination
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

@HiltViewModel
class NewsSourceVM @Inject constructor(
    private val repository: NewsRepository,
): ViewModel() {

    var tag = ""

    fun fetchData(): Flow<PagingData<Source>> = getData().cachedIn(viewModelScope)

    private fun getData(): Flow<PagingData<Source>> {
        return Pager(
            config = PagingConfig(
                initialLoadSize = 3,
                pageSize = 10,
                enablePlaceholders = false
            ),
            pagingSourceFactory = {
                NewsSourcePagination(
                    repository,
                    category = tag,
//                    country = country,
                    callback = {code, message ->
//                        _errorMessage.postValue("code: $code \n $message")
                    }
                )
            }
        ).flow
    }
}