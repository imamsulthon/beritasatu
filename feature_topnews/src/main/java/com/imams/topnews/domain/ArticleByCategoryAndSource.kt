package com.imams.topnews.domain

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.imams.core.TheResult
import com.imams.newsapi.model.Article
import com.imams.newsapi.repository.NewsRepository
import java.io.IOException

class ArticleByCategoryAndSource(
    private val repository: NewsRepository,
    private val myParams: MyParams,
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
            when (
                val response = repository.getNews(
                    page = page, category = myParams.category, source = myParams.source, query = myParams.query
                )
            ) {
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
        }
    }
}

data class MyParams(
    val category: String,
    val source: String,
    var query: String? = null
)