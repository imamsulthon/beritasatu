package com.imams.searchnews.ui.newssource

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.imams.newsapi.model.Source
import com.imams.searchnews.R
import com.imams.searchnews.databinding.ItemNewsSourceBinding

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
//            try {
//                com.bumptech.glide.Glide.with(itemView.context)
//                    .load(item.urlToImage)
//                    .error(R.drawable.ic_newspaper)
//                    .into(viewLeft)
//            } catch (e: Exception) {
//                e.printStackTrace()
//            }
            tvTitle.text = item.name
//            tvSubTitle.text = item.publishedAt.simpleFormattedDate()
//            tvDesc.text = item.authorAndSource()
        }
    }
}