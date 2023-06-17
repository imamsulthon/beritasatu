package com.imams.topnews.domain

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.bumptech.glide.load.HttpException
import com.imams.core.TheResult
import com.imams.newsapi.model.Article
import com.imams.newsapi.repository.NewsRepository
import java.io.IOException

class TopNewsPagingSource(
    private val repository: NewsRepository,
    private val country: String? = null,
    private val callback: (String, String) -> Unit,
): PagingSource<Int, Article>() {

    override fun getRefreshKey(state: PagingState<Int, Article>): Int? {
        return state.anchorPosition?.let {
            state.closestPageToPosition(it)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(it)?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Article> {
        val page = params.key ?: 1
        return try {
            when (val response = repository.getTopNews(page, country?: "us")) {
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