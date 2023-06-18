package com.imams.searchnews.ui

import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.core.widget.doAfterTextChanged
import androidx.lifecycle.lifecycleScope
import androidx.paging.LoadState
import androidx.paging.PagingData
import androidx.recyclerview.widget.GridLayoutManager
import com.imams.core.utils.visible
import com.imams.core.utils.wartaLog
import com.imams.newsapi.model.Source
import com.imams.searchnews.R
import com.imams.searchnews.databinding.ActivityNewsSourcesBinding
import com.imams.searchnews.ui.newssource.LoadingStateAdapter
import com.imams.searchnews.ui.newssource.NewsSourceAdapter
import com.imams.searchnews.ui.newssource.NewsSourceVM
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class NewsSourcesActivity : AppCompatActivity() {

    private val binding by lazy { ActivityNewsSourcesBinding.inflate(layoutInflater) }
    private val viewModel: NewsSourceVM by viewModels()

    private var searchJob: Job? = null

    private val listAdapter: NewsSourceAdapter by lazy {
        NewsSourceAdapter(
            callback = { openNews(it.id) }
        )
    }

    private var tag = ""

    companion object {
        const val TAG_ID = "source_id"
    }

    private fun getIntentData() {
        intent?.getStringExtra(TAG_ID)?.let {
            tag = it
            viewModel.tag = tag
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
            viewModel.paginationFlow().collectLatest {
                setAllSources(it)
            }
            viewModel.errorMessage.observe(this@NewsSourcesActivity) {
                it.let {
                    Toast.makeText(this@NewsSourcesActivity, it, Toast.LENGTH_LONG).show()
                }
            }
            viewModel.stateEnableSearch.observe(this@NewsSourcesActivity) {
                it?.let {
                    binding.searchView.isEnabled = it
                }
            }
        }
        viewModel.fetchData()
    }

    private fun initViewAndListener() {
        with(binding) {
            swipeRefresh.setOnRefreshListener { doRefresh() }

            rvAllNews.layoutManager = GridLayoutManager(this@NewsSourcesActivity, 2)
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
                log("afterTextChanged ${it.toString()}")
                doSearch(it.toString())
            }
        }
    }

    private fun doSearch(query: String) {
        searchJob?.cancel()
        searchJob = lifecycleScope.launch {
            delay(500)
            log("doSearch")
            viewModel.search(query)
            listAdapter.refresh()
        }
        searchJob?.start()
    }

    private fun doRefresh() {
        lifecycleScope.launch {
            log("doRefresh")
            binding.searchView.text = null
            viewModel.doRefresh()
            listAdapter.refresh()
            delay(500)
            binding.swipeRefresh.isRefreshing = false
        }
    }

    private fun setAllSources(list: PagingData<Source>) {
        log("setAllSource")
        binding.rvAllNews.visible()
        listAdapter.submitData(lifecycle, list)
    }

    private fun openNews(category: String) {
        log("click $category")
    }

    private fun log(msg: String) {
        wartaLog("NewsSourcePage: $msg")
    }

}