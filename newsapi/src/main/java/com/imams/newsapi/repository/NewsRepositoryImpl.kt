package com.imams.newsapi.repository

import com.imams.core.TheResult
import com.imams.core.toError
import com.imams.core.utils.wartaLog
import com.imams.newsapi.mapper.NewsMapper.toModel
import com.imams.newsapi.model.Article
import com.imams.newsapi.model.Category
import com.imams.newsapi.model.Source
import com.imams.newsapi.source.local.newsCategories
import com.imams.newsapi.source.remote.service.NewsApiService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class NewsRepositoryImpl @Inject constructor(
    private val apiService: NewsApiService,
): NewsRepository {

    override suspend fun getNews(
        page: Int,
        category: String,
        source: String,
        query: String?
    ): TheResult<List<Article>> {
        return withContext(Dispatchers.IO) {
            try {
                wartaLog("Repos s $source q $query c $category")
                val response = apiService.getNews(page = page, sources = source, query = query)
                when {
                    response.status == "error" -> {
                        TheResult.Error(code = response.code.orEmpty(), message = response.message.orEmpty())
                    }
                    response.articles.isNullOrEmpty() -> {
                        TheResult.Success(emptyList())
                    }
                    else -> {
                        TheResult.Success(response.articles!!.map { it.toModel() })
                    }
                }
            } catch (e: Exception) {
                e.toError()
            }
        }
    }

    override suspend fun getTopNews(page: Int, country: String): TheResult<List<Article>> {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.getTopNews(page = page, country = country)
                when {
                    response.status.equals("error", true) -> {
                        TheResult.Error(
                            code = response.code.orEmpty(),
                            message = response.message.orEmpty()
                        )
                    }
                    response.articles.isNullOrEmpty() -> {
                        TheResult.Success(emptyList())
                    }
                    else -> {
                        TheResult.Success(response.articles!!.map { it.toModel() })
                    }
                }
            } catch (e: Exception) {
                e.toError()
            }
        }
    }

    override suspend fun getSources(category: String, country: String?): TheResult<List<Source>> {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.getNewsSource(category = category, country = country)
                when {
                    response.status.equals("error", true) -> {
                        TheResult.Error(
                            code = response.code.orEmpty(),
                            message = response.message.orEmpty()
                        )
                    }
                    response.sources.isNullOrEmpty() -> {
                        TheResult.Success(emptyList())
                    }
                    else -> {
                        TheResult.Success(response.sources!!.map { it.toModel() })
                    }
                }
            } catch (e: Exception) {
                e.toError()
            }
        }
    }

    override suspend fun getCategories(): List<Category> {
        return newsCategories()
    }

}