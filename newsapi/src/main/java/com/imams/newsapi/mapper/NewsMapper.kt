package com.imams.newsapi.mapper

import android.os.Bundle
import com.imams.newsapi.model.Article
import com.imams.newsapi.model.Source
import com.imams.newsapi.source.remote.response.ArticleResponse
import com.imams.newsapi.source.remote.response.SourceResponse

object NewsMapper {

    fun ArticleResponse.toModel() = Article(
        title = title.orEmpty(),
        author = author.orEmpty(),
        source = source?.toModel() ?: emptySource(),
        description = description.orEmpty(),
        url =  url.orEmpty(),
        urlToImage = urlToImage.orEmpty(),
        publishedAt = publishedAt.orEmpty(),
        content = content.orEmpty(),
    )

    fun SourceResponse.toModel() = Source(
        id = id.orEmpty(),
        name =name.orEmpty(),
        description =description.orEmpty(),
        url = url.orEmpty(),
        category = category.orEmpty(),
        language = language.orEmpty(),
        country = country.orEmpty(),
    )

    private fun emptySource() = Source("","","", "","","", "")


    fun Bundle.toArticleDetail(): Article {
        return Article(
            author = this.getString("author").orEmpty(),
            title = this.getString("title").orEmpty(),
            source = emptySource().apply {
                id = this@toArticleDetail.getString("source").orEmpty()
                name = this@toArticleDetail.getString("source").orEmpty()
            },
            description = this.getString("description").orEmpty(),
            url =  this.getString("url").orEmpty(),
            urlToImage = this.getString("urlToImage").orEmpty(),
            publishedAt = this.getString("publishedAt").orEmpty(),
            content = this.getString("content").orEmpty(),
        )
    }

    fun Article.toBundle(): Bundle {
        return Bundle().apply {
            putString("author", this@toBundle.author)
            putString("title", this@toBundle.title)
            putString("source", this@toBundle.source.name)
            putString("description", this@toBundle.description)
            putString("url", this@toBundle.url)
            putString("urlToImage", this@toBundle.urlToImage)
            putString("publishedAt", this@toBundle.publishedAt)
            putString("content", this@toBundle.content)
        }
    }

}