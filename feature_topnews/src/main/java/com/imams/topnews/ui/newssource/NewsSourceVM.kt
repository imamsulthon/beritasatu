package com.imams.topnews.ui.newssource

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asFlow
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.imams.core.TheResult
import com.imams.core.utils.wartaLog
import com.imams.newsapi.model.Source
import com.imams.newsapi.repository.NewsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NewsSourceVM @Inject constructor(
    private val repository: NewsRepository,
): ViewModel() {

    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> = _errorMessage
    private val _stateEnableSearch = MutableLiveData(false)
    val stateEnableSearch: LiveData<Boolean> = _stateEnableSearch

    var country: String? = null
    var tag = "health"
    var query = ""

    private val _result: MutableList<Source> = mutableListOf()
    private val _dataSource: MutableLiveData<PagingData<Source>> = MutableLiveData()
    fun paginationFlow(): Flow<PagingData<Source>> = _dataSource.asFlow().cachedIn(viewModelScope)

    fun fetchData() {
        viewModelScope.launch {
            when (val result = repository.getSources(tag, country)) {
                is TheResult.Success -> {
                    _result.clear()
                    _result.addAll(result.data)
                    _stateEnableSearch.postValue(_result.isNotEmpty())
                    proceed(_result, query)
                }
                is TheResult.Error -> {
                    _errorMessage.postValue(result.code)
                    _stateEnableSearch.postValue(false)
                }
            }
        }
    }

    fun search(q: String) {
        log("search before $query ${_result.isEmpty()}")
        query = q
        if (_result.isEmpty()) return
        log("search after $query")
        proceed(_result, query)
    }

    fun doRefresh() {
        query = ""
        fetchData()
    }

    private fun proceed(list: List<Source>, query: String) {
        viewModelScope.launch {
            val l = if (query.isEmpty()) list else list.filter { it.name.equals(query, true) }
            log("proceed l size ${l.size} q $query")
            _dataSource.postValue(PagingData.from(l))
        }
    }

    private fun log(msg: String) = wartaLog("NewsSourceVM: $msg")

}