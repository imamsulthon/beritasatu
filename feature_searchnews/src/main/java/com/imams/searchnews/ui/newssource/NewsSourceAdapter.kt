package com.imams.searchnews.ui.newssource

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.LoadState
import androidx.paging.LoadStateAdapter
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.imams.core.utils.gone
import com.imams.core.utils.visible
import com.imams.newsapi.model.Source
import com.imams.searchnews.R
import com.imams.searchnews.databinding.ItemNewsSourceBinding
import com.imams.searchnews.databinding.LoadingBinding

class NewsSourceAdapter(
    private val callback: ((Source) -> Unit)?,
) : PagingDataAdapter<Source, NewsSourceVH>(DIFF_CALLBACK) {

    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<Source>() {

            override fun areItemsTheSame(oldItem: Source, newItem: Source): Boolean =
                oldItem == newItem

            override fun areContentsTheSame(oldItem: Source, newItem: Source): Boolean =
                oldItem.id == newItem.id
        }
    }

    override fun onBindViewHolder(holder: NewsSourceVH, position: Int) {
        getItem(position)?.let { source ->
            holder.bind(source)
            holder.itemView.setOnClickListener {
                callback?.invoke(source)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NewsSourceVH {
        return NewsSourceVH(
            ItemNewsSourceBinding.bind(
                LayoutInflater.from(parent.context).inflate(
                    R.layout.item_news_source, parent, false
                )
            )
        )
    }

}

class NewsSourceVH(
    private val binding: ItemNewsSourceBinding,
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(item: Source) {
        with(binding) {
            tvTitle.text = item.name
            tvSubTitle.text = item.url
            tvDesc.text = item.description
            tvCountry.text = item.country
            tvLanguage.text = item.language
        }
    }
}

class LoadingStateAdapter : LoadStateAdapter<LoadingStateAdapter.ViewHolder>() {

    class ViewHolder(private val binding: LoadingBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(loadState: LoadState) {
            with(binding) {
                when (loadState) {
                    is LoadState.Loading -> {
                        tvLoading.visible()
                        tvMessage.gone()
                    }
                    is LoadState.Error -> {
                        tvLoading.gone()
                        tvMessage.visible()
                        tvMessage.text = "Error\n${loadState.error.message}"
                    }
                    is LoadState.NotLoading -> {
                        tvLoading.gone()
                        tvMessage.gone()
                    }
                }
            }
        }
    }

    override fun onBindViewHolder(holder: ViewHolder, loadState: LoadState) {
        holder.bind(loadState)
    }

    override fun onCreateViewHolder(parent: ViewGroup, loadState: LoadState): ViewHolder {
        return ViewHolder(
            LoadingBinding.bind(
                LayoutInflater.from(parent.context)
                    .inflate(R.layout.loading, parent, false)
            )
        )
    }

}
