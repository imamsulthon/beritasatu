package com.imams.newsapi.source.remote.response

import com.google.gson.annotations.SerializedName
import com.imams.newsapi.source.remote.service.BaseResponse

data class NewsResponse(
    @SerializedName("totalResult") var totalResult: Int? = null,
    @SerializedName("articles") var articles: List<ArticleResponse>? = null,
): BaseResponse()
