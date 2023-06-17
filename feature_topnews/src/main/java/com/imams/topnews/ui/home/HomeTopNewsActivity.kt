package com.imams.topnews.ui.home

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import androidx.paging.LoadState
import androidx.paging.PagingData
import androidx.recyclerview.widget.LinearLayoutManager
import com.imams.core.utils.countryLabels
import com.imams.core.utils.getCountryCode
import com.imams.core.utils.visible
import com.imams.newsapi.mapper.NewsMapper.toBundle
import com.imams.newsapi.model.Article
import com.imams.topnews.R
import com.imams.topnews.databinding.ActivityHomeBinding
import com.imams.topnews.ui.detail.DetailActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class HomeTopNewsActivity : AppCompatActivity() {

    private val binding by lazy { ActivityHomeBinding.inflate(layoutInflater) }
    private val viewModel: HomeTopNewsVM by viewModels()

    private val allNewsAdapter: PagingNewsAdapter by lazy {
        PagingNewsAdapter(
            callback = ::openDetail
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        initViewAndListener()
        observeViewModel()
    }

    private fun observeViewModel() {
        lifecycleScope.launch {
            viewModel.topNews().collectLatest {
                setAllNews(it)
            }
            viewModel.errorMessage.observe(this@HomeTopNewsActivity) {
                it?.let { showToast(it) }
            }
        }
    }

    private fun initViewAndListener() {
        with(binding) {
            swipeRefresh.setOnRefreshListener { allNewsAdapter.refresh() }

            rvAllNews.layoutManager = LinearLayoutManager(this@HomeTopNewsActivity,
                LinearLayoutManager.VERTICAL, false)
            rvAllNews.adapter = allNewsAdapter.withLoadStateFooter(LoadingStateAdapter())

            allNewsAdapter.addLoadStateListener {
                binding.swipeRefresh.isRefreshing = it.refresh is LoadState.Loading

                val isErrorOrEmpty = it.refresh is LoadState.Error ||
                        (it.refresh is LoadState.NotLoading && allNewsAdapter.itemCount == 0)
                tvLoadState.isVisible = isErrorOrEmpty
                if (it.refresh is LoadState.Error) {
                    tvLoadState.text = getString(R.string.label_error_data)
                }

                if (it.refresh is LoadState.NotLoading && allNewsAdapter.itemCount == 0) {
                    tvLoadState.text = getString(R.string.label_empty_data)
                }
                rvAllNews.isVisible = !isErrorOrEmpty
            }
            setupCountryAdapter()
        }
    }

    private fun setupCountryAdapter() {
        with(binding) {
            val countryLabels = countryLabels()
            optionalCountry.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                    if (countryLabels[p2].getCountryCode() == viewModel.country) return
                    viewModel.country = countryLabels[p2].getCountryCode()
                    lifecycleScope.launch {
                        delay(500)
                        allNewsAdapter.refresh()
                    }
                }
                override fun onNothingSelected(p0: AdapterView<*>?) {}
            }
            ArrayAdapter(
                this@HomeTopNewsActivity,
                android.R.layout.simple_spinner_dropdown_item,
                countryLabels
            ).also { arrayAdapter ->
                arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                optionalCountry.adapter = arrayAdapter
                optionalCountry.setSelection(countryLabels.indexOf("Indonesia"))
            }
        }

    }

    private fun setAllNews(list: PagingData<Article>) {
        binding.rvAllNews.visible()
        allNewsAdapter.submitData(lifecycle, list)
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