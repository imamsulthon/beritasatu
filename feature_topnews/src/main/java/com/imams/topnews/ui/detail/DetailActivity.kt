package com.imams.topnews.ui.detail

import android.annotation.SuppressLint
import android.os.Bundle
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.imams.core.utils.gone
import com.imams.core.utils.setOrGone
import com.imams.core.utils.simpleFormattedDate
import com.imams.core.utils.visible
import com.imams.newsapi.mapper.NewsMapper.toArticleDetail
import com.imams.newsapi.model.Article
import com.imams.topnews.R
import com.imams.topnews.databinding.ActivityDetailBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DetailActivity : AppCompatActivity() {

    private val binding by lazy { ActivityDetailBinding.inflate(layoutInflater) }

    private var article: Article? = null

    companion object {
        const val DATA = "date"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
    }

    override fun onStart() {
        super.onStart()
        intent?.getBundleExtra(DATA)?.let {
            article = it.toArticleDetail()
        }
        article?.let { setData(it) }
    }

    private fun setData(item: Article) {
        with(binding) {
            swipeRefresh.isEnabled = false
            if (item.urlToImage.isEmpty()) {
                ivBanner.gone()
            } else {
                ivBanner.visible()
                try {
                    Glide.with(binding.root)
                        .load(item.urlToImage)
                        .error(R.drawable.ic_newspaper)
                        .into(ivBanner)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

            tvTitle.setOrGone(item.title)
            tvSource.setOrGone(item.authorAndSource())
            tvDate.setOrGone(item.publishedAt.simpleFormattedDate())
            tvContent.setOrGone(item.content)
            tvDescriptions.setOrGone(item.description)
            loadUrl(item.url)
        }
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun loadUrl(newsUrl: String) {
        with(binding) {
            webview.settings.javaScriptEnabled = true
            webview.webViewClient = object : WebViewClient() {
                override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?): Boolean {
                    return false
                }
            }
            webview.loadUrl(newsUrl)
        }
    }

}