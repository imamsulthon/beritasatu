package com.imams.searchnews.domain

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.bumptech.glide.load.HttpException
import com.imams.core.TheResult
import com.imams.newsapi.model.Source
import com.imams.newsapi.repository.NewsRepository
import java.io.IOException

class NewsSourcePagination(
    private val repository: NewsRepository,
    private val category: String,
    private val callback: (String, String) -> Unit,
) : PagingSource<Int, Source>() {

    override fun getRefreshKey(state: PagingState<Int, Source>): Int? {
        return state.anchorPosition?.let {
            state.closestPageToPosition(it)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(it)?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Source> {
        val page = params.key ?: 1
        return try {
            when (val response = repository.getSources()) {
                is TheResult.Success -> {
                    val list = response.data
                    LoadResult.Page(
                        list,
                        prevKey = if (page == 1) null else page - 1,
                        nextKey = if (list.isEmpty()) null else page + 1
                    )
                }
                is TheResult.Error -> {
                    callback.invoke(response.code, response.message)
                    LoadResult.Error(Throwable(message = response.code))
                }
            }
        } catch (exception: IOException) {
            callback.invoke("IOException", exception.message ?: "unknown")
            return LoadResult.Error(exception)
        } catch (exception: HttpException) {
            callback.invoke("HttpException", exception.message ?: "unknown")
            return LoadResult.Error(exception)
        }
    }

}