package com.imams.newsapi.model

data class Article(
    var source: Source,
    var author: String,
    var title: String,
    var description: String,
    var url: String,
    var urlToImage: String,
    var publishedAt: String,
    var content: String,
) {
    fun authorAndSource(): String = when {
        this.author.isNotEmpty() && this.source.name.isNotEmpty() -> "${this.author} (${this.source.name})"
        this.author.isEmpty() -> this.source.name
        this.source.name.isEmpty() -> this.author
        else -> ""
    }
}
