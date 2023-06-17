package com.imams.newsapi.repository

import com.imams.core.TheResult
import com.imams.newsapi.model.Article
import com.imams.newsapi.model.Category
import com.imams.newsapi.model.Source

interface NewsRepository {

    suspend fun getNews(page: Int): TheResult<List<Article>>

    suspend fun getTopNews(page: Int, country: String): TheResult<List<Article>>

    suspend fun getSources(): TheResult<List<Source>>

    suspend fun getCategories(): List<Category>

}