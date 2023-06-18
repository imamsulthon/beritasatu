package com.imams.searchnews

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.imams.searchnews.databinding.ActivityEverythingNewsBinding
import com.imams.searchnews.ui.CategoryAdapter
import com.imams.searchnews.ui.EverythingNewsVM
import com.imams.searchnews.ui.NewsSourcesActivity
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        initViewAndListener()
        observeViewModel()
        fetchData()
    }

    private fun fetchData() {
        viewModel.fetchData()
    }

    private fun observeViewModel() {
        lifecycleScope.launch {
            viewModel.categories.observe(this@EverythingNewsActivity) {
                it?.let {
                    categoryAdapter.submit(it)
                }
            }
        }
    }

    private fun initViewAndListener() {
        with(binding) {
//            rvCategory.layoutManager = StaggeredGridLayoutManager(4, StaggeredGridLayoutManager.VERTICAL)
            rvCategory.adapter = categoryAdapter
        }
    }

    private fun openNewsSourcePage(category: String) {
        startActivity(Intent(this, NewsSourcesActivity::class.java).apply {
            putExtra(NewsSourcesActivity.TAG_ID, category)
        })
    }

}