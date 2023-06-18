package com.imams.searchnews.domain

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.imams.core.utils.wartaLog
import com.imams.newsapi.model.Source
import java.io.IOException

class NewsSourcePagination(
    private val list: List<Source> = mutableListOf(),
    private val callback: (String, String) -> Unit,
) : PagingSource<Int, Source>() {

    private var _list = list

    override fun getRefreshKey(state: PagingState<Int, Source>): Int? {
        return state.anchorPosition?.let {
            state.closestPageToPosition(it)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(it)?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Source> {
        val page = params.key ?: 1
        return try {
//            if (page == 1) {
//                when (val response = repository.getSources(category, country)) {
//                    is TheResult.Success -> {
//                        list = response.data
//                        log("size ${list.size}")
//                    }
//                    is TheResult.Error -> {
//                        log("error ${response.message}")
//                        callback.invoke(response.code, response.message)
//                        return LoadResult.Error(Throwable(message = "error"))
//                    }
//                }
//            }
            log("list: ${_list.size} loadSize: ${params.loadSize} key: ${params.key} page: $page")
            when {
                _list.isNotEmpty() -> {
                    val toLoad = _list.take(params.loadSize)
                    _list = _list.drop(params.loadSize)
                    LoadResult.Page(
                        toLoad,
                        prevKey = if (page == 1) null else page - 1,
                        nextKey = if (list.isEmpty()) null else page + 1
                    )
                }
                 else -> {
                     LoadResult.Page(
                         emptyList(),
                         prevKey = null,
                         nextKey = null,
                     )
                 }
            }
        } catch (exception: IOException) {
            callback.invoke("IOException", exception.message ?: "unknown")
            return LoadResult.Error(exception)
        }
    }

    private fun log(msg: String) = wartaLog("NewsSourcePagination: $msg")

}