package com.imams.topnews.ui.everything

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.imams.core.utils.visible
import com.imams.core.utils.wartaLog
import com.imams.newsapi.mapper.NewsMapper.toBundle
import com.imams.newsapi.model.Article
import com.imams.topnews.databinding.ActivityEverythingNewsBinding
import com.imams.topnews.ui.CategoryAdapter
import com.imams.topnews.ui.detail.DetailActivity
import com.imams.topnews.ui.home.SimpleNewsAdapter
import com.imams.topnews.ui.newssource.NewsSourcesActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class EverythingNewsActivity : AppCompatActivity() {

    private val binding by lazy { ActivityEverythingNewsBinding.inflate(layoutInflater) }
    private val viewModel: EverythingNewsVM by viewModels()

    private val categoryAdapter: CategoryAdapter by lazy {
        CategoryAdapter(
            listOf(),
            callback = { openNewsSourcePage(it.id) }
        )
    }


    private val listAdapter: SimpleNewsAdapter by lazy {
        SimpleNewsAdapter(callback = ::openDetail, list = listOf())
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        initViewAndListener()
        observeViewModel()
        fetchData()
    }

    private fun fetchData() {
        viewModel.fetchData()
        viewModel.fetchTopNews()
    }

    private fun observeViewModel() {
        lifecycleScope.launch {
            viewModel.categories.observe(this@EverythingNewsActivity) {
                it?.let {
                    categoryAdapter.submit(it)
                }
            }
            viewModel.topNews.observe(this@EverythingNewsActivity) {
                it?.let { setAllNews(it) }
            }
        }
    }

    private fun initViewAndListener() {
        with(binding) {
            rvCategory.adapter = categoryAdapter
            rvTopNews.adapter = listAdapter
        }
    }

    private fun openNewsSourcePage(category: String) {
        startActivity(Intent(this, NewsSourcesActivity::class.java).apply {
            putExtra(NewsSourcesActivity.TAG_ID, category)
        })
    }

    private fun setAllNews(list: List<Article>) {
        wartaLog("Everything $list")
        binding.tvTitleTopNews.visible()
        binding.rvTopNews.visible()
        listAdapter.submitData(list)
    }

    private fun openDetail(item: Article) {
        startActivity(Intent(this, DetailActivity::class.java).apply {
            putExtra(DetailActivity.DATA, item.toBundle())
        })
    }
}