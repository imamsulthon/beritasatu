package com.imams.newsapi.source.local

import com.imams.newsapi.model.Category

fun newsCategories(): List<Category> = listOf(
    Category("business", "Business"),
    Category("entertainment", "Entertainment"),
    Category("general", "General"),
    Category("health", "Health"),
    Category("science", "Science"),
    Category("sports", "Sports"),
    Category("technology", "Technology"),
)