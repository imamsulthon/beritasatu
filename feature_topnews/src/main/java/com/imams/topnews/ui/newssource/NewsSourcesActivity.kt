package com.imams.topnews.ui.newssource

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.core.widget.doAfterTextChanged
import androidx.lifecycle.lifecycleScope
import androidx.paging.LoadState
import androidx.paging.PagingData
import androidx.recyclerview.widget.GridLayoutManager
import com.imams.core.utils.countryLabels
import com.imams.core.utils.getCountryCode
import com.imams.newsapi.model.Source
import com.imams.topnews.R
import com.imams.topnews.databinding.ActivityNewsSourcesBinding
import com.imams.topnews.ui.article.ArticleListActivity
import com.imams.topnews.ui.home.LoadingStateAdapter
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
        const val TAG_ID = "category_id"
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
                errorState.root.isVisible = isErrorOrEmpty
                if (it.refresh is LoadState.Error) {
                    errorState.tvLoadState.text = getString(R.string.label_error_data)
                }

                if (it.refresh is LoadState.NotLoading && listAdapter.itemCount == 0) {
                    errorState.tvLoadState.text = getString(R.string.label_empty_data)
                }
                rvAllNews.isVisible = listAdapter.itemCount >= 0
            }

            setupCountryAdapter()

            searchView.doAfterTextChanged {
                doSearch(it.toString())
            }
        }
    }

    private fun doSearch(query: String) {
        searchJob?.cancel()
        searchJob = lifecycleScope.launch {
            delay(500)
            viewModel.search(query)
            listAdapter.refresh()
        }
        searchJob?.start()
    }

    private fun doRefresh() {
        lifecycleScope.launch {
            binding.searchView.text = null
            viewModel.doRefresh()
            listAdapter.refresh()
            delay(500)
            binding.swipeRefresh.isRefreshing = false
        }
    }

    private fun setAllSources(list: PagingData<Source>) {
        listAdapter.submitData(lifecycle, list)
    }

    private fun setupCountryAdapter() {
        with(binding) {
            val countryLabels = countryLabels()
            optionalCountry.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                    if (countryLabels[p2].getCountryCode() == viewModel.country) return
                    viewModel.country = countryLabels[p2].getCountryCode()
                    doRefresh()
                }
                override fun onNothingSelected(p0: AdapterView<*>?) {}
            }
            ArrayAdapter(
                this@NewsSourcesActivity,
                android.R.layout.simple_spinner_dropdown_item,
                countryLabels
            ).also { arrayAdapter ->
                arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                optionalCountry.adapter = arrayAdapter
                optionalCountry.setSelection(countryLabels.indexOf("United States"))
            }
        }

    }

    private fun openNews(sourceId: String) {
        startActivity(Intent(this, ArticleListActivity::class.java).apply {
            putExtra(ArticleListActivity.TAG_DATA, Bundle().apply {
                putString(ArticleListActivity.TAG_CATEGORY, tag)
                putString(ArticleListActivity.TAG_SOURCE, sourceId)
            })
        })
    }

}