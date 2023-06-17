package com.imams.searchnews.ui

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import androidx.paging.LoadState
import androidx.paging.PagingData
import androidx.recyclerview.widget.LinearLayoutManager
import com.imams.core.utils.visible
import com.imams.core.utils.wartaLog
import com.imams.newsapi.model.Source
import com.imams.searchnews.R
import com.imams.searchnews.databinding.ActivityNewsSourcesBinding
import com.imams.searchnews.ui.newssource.NewsSourceAdapter
import com.imams.searchnews.ui.newssource.NewsSourceVM
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class NewsSourcesActivity : AppCompatActivity() {

    private val binding by lazy { ActivityNewsSourcesBinding.inflate(layoutInflater) }
    private val viewModel: NewsSourceVM by viewModels()

    private val listAdapter: NewsSourceAdapter by lazy {
        NewsSourceAdapter(
            callback = { openNews(it.id) }
        )
    }

    private var tag = ""

    companion object {
        const val TAG_ID = "source_id"
    }

    override fun onStart() {
        super.onStart()
        intent?.getStringExtra(TAG_ID)?.let {
            tag = it
            viewModel.tag = tag
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        initViewAndListener()
        observeViewModel()
    }

    private fun observeViewModel() {
        lifecycleScope.launch {
            viewModel.fetchData().collectLatest {
                setAllSources(it)
            }
        }
    }

    private fun initViewAndListener() {
        with(binding) {
            swipeRefresh.setOnRefreshListener { listAdapter.refresh() }

            rvAllNews.layoutManager = LinearLayoutManager(this@NewsSourcesActivity,
                LinearLayoutManager.VERTICAL, false)
//            rvAllNews.adapter = listAdapter.withLoadStateFooter(LoadingStateAdapter())

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
        }
    }

    private fun setAllSources(list: PagingData<Source>) {
        binding.rvAllNews.visible()
        listAdapter.submitData(lifecycle, list)
    }


    private fun openNews(category: String) {
        log("click $category")
    }

    private fun log(msg: String) = wartaLog("NewsSourcePage: $msg")

}