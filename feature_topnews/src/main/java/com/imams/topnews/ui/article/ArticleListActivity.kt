package com.imams.topnews.ui.article

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.core.widget.doAfterTextChanged
import androidx.lifecycle.lifecycleScope
import androidx.paging.LoadState
import androidx.paging.PagingData
import androidx.recyclerview.widget.LinearLayoutManager
import com.imams.core.utils.visible
import com.imams.newsapi.mapper.NewsMapper.toBundle
import com.imams.newsapi.model.Article
import com.imams.topnews.R
import com.imams.topnews.databinding.ActivityArticleListBinding
import com.imams.topnews.ui.detail.DetailActivity
import com.imams.topnews.ui.home.LoadingStateAdapter
import com.imams.topnews.ui.home.PagingNewsAdapter
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ArticleListActivity : AppCompatActivity() {

    private val binding by lazy { ActivityArticleListBinding.inflate(layoutInflater) }
    private val viewModel: ArticleListByCategoryVM by viewModels()
    private var searchJob: Job? = null

    private val listAdapter: PagingNewsAdapter by lazy {
        PagingNewsAdapter(
            callback = ::openDetail
        )
    }

    companion object {
        const val TAG_DATA = "tag_data"
        const val TAG_CATEGORY = "tag_category"
        const val TAG_SOURCE = "tag_source"
    }

    private fun getIntentData() {
        intent.getBundleExtra(TAG_DATA)?.let {
            val category = it.getString(TAG_CATEGORY).orEmpty()
            val source = it.getString(TAG_SOURCE).orEmpty()
            viewModel.setupParams(category, source)
            val topTitle = "By $source"
            binding.tvPageTitle.text = topTitle
            binding.tvCategory.text = category
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        getIntentData()
        initViewAndListener()
        observeViewModel()
    }

    private fun observeViewModel() {
        lifecycleScope.launch {
            viewModel.articleBySourceAndCategory().collectLatest {
                setAllNews(it)
            }
            viewModel.errorMessage.observe(this@ArticleListActivity) {
                it?.let { showToast(it) }
            }
        }
    }

    private fun initViewAndListener() {
        with(binding) {
            swipeRefresh.setOnRefreshListener { listAdapter.refresh() }

            rvAllNews.layoutManager = LinearLayoutManager(this@ArticleListActivity,
                LinearLayoutManager.VERTICAL, false)
            rvAllNews.adapter = listAdapter.withLoadStateFooter(LoadingStateAdapter())

            listAdapter.addLoadStateListener {
                binding.swipeRefresh.isRefreshing = it.refresh is LoadState.Loading

                val isErrorOrEmpty = it.refresh is LoadState.Error ||
                        (it.refresh is LoadState.NotLoading && listAdapter.itemCount == 0)
                tvLoadState.isVisible = isErrorOrEmpty
                if (it.refresh is LoadState.Error) {
                    tvLoadState.text = getString(R.string.label_error_data)
                }

                if (it.refresh is LoadState.NotLoading && listAdapter.itemCount == 0) {
                    tvLoadState.text = getString(R.string.label_empty_data)
                }
                rvAllNews.isVisible = !isErrorOrEmpty
            }

            searchView.doAfterTextChanged {
                it?.let { e -> doSearch(e.toString()) }
            }
        }
    }

    private fun doSearch(query: String?) {
        searchJob?.cancel()
        searchJob = lifecycleScope.launch {
            delay(800)
            viewModel.query = query
            listAdapter.refresh()
        }
        searchJob?.start()
    }

    private fun setAllNews(list: PagingData<Article>) {
        binding.rvAllNews.visible()
        listAdapter.submitData(lifecycle, list)
    }

    private fun openDetail(item: Article) {
        startActivity(Intent(this, DetailActivity::class.java).apply {
            putExtra(DetailActivity.DATA, item.toBundle())
        })
    }

    private fun showToast(msg: String) {
        Toast.makeText(this, msg, Toast.LENGTH_LONG).show()
    }
}