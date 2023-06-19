package com.imams.topnews.ui.newssource

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.imams.newsapi.model.Source
import com.imams.topnews.R
import com.imams.topnews.databinding.ItemNewsSourceBinding

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