package com.imams.newsapi.source.remote.service

import com.imams.newsapi.source.remote.response.NewsResponse
import com.imams.newsapi.source.remote.response.NewsSourcesResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface NewsApiService {

    @GET("everything")
    suspend fun getNews(
        @Query("page") page: Int? = null,
        @Query("pageSize") pageSize: Int? = 10,
        @Query("q") query: String? = null,
        @Query("sources") sources: String? = null,
    ): NewsResponse

    @GET("top-headlines")
    suspend fun getTopNews(
        @Query("page") page: Int? = null,
        @Query("pageSize") pageSize: Int? = 10,
        @Query("country") country: String? = null,
        @Query("sources") source: String? = null,
        @Query("category") category: String? = null,
    ): NewsResponse

    @GET("top-headlines/sources")
    suspend fun getNewsSource(
        @Query("category") category: String? = null,
        @Query("country") country: String? = null,
    ): NewsSourcesResponse

}