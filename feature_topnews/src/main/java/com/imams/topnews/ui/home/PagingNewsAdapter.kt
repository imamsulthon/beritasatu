package com.imams.topnews.ui.home

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.LoadState
import androidx.paging.LoadStateAdapter
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.imams.core.utils.gone
import com.imams.core.utils.simpleFormattedDate
import com.imams.core.utils.visible
import com.imams.newsapi.model.Article
import com.imams.topnews.R
import com.imams.topnews.databinding.ItemNewsLandscapeBinding
import com.imams.topnews.databinding.LoadingBinding

class PagingNewsAdapter(
    private val callback: ((Article) -> Unit)?
) : PagingDataAdapter<Article, LandscapeNewsVH>(DIFF_CALLBACK) {

    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<Article>() {
            override fun areItemsTheSame(oldItem: Article, newItem: Article): Boolean =
                oldItem == newItem

            override fun areContentsTheSame(oldItem: Article, newItem: Article): Boolean =
                oldItem.title == newItem.title
        }
    }

    override fun onBindViewHolder(holder: LandscapeNewsVH, position: Int) {
        getItem(position)?.let { news ->
            holder.bind(news)
            holder.itemView.setOnClickListener {
                callback?.invoke(news)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LandscapeNewsVH {
        return LandscapeNewsVH(
            ItemNewsLandscapeBinding.bind(
                LayoutInflater.from(parent.context).inflate(
                    R.layout.item_news_landscape, parent, false
                )
            )
        )
    }

}

class LandscapeNewsVH(
    private val binding: ItemNewsLandscapeBinding,
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(item: Article) {
        with(binding) {
            try {
                Glide.with(itemView.context)
                    .load(item.urlToImage)
                    .error(R.drawable.ic_newspaper)
                    .into(viewLeft)
            } catch (e: Exception) {
                e.printStackTrace()
            }
            tvTitle.text = item.title
            tvSubTitle.text = item.publishedAt.simpleFormattedDate()
            tvDesc.text = item.authorAndSource()
        }
    }
}

class LoadingStateAdapter : LoadStateAdapter<LoadingStateAdapter.ViewHolder>() {

    class ViewHolder(private val binding: LoadingBinding) : RecyclerView.ViewHolder(binding.root) {

        @SuppressLint("SetTextI18n")
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

class SimpleNewsAdapter(
    private var list: List<Article>,
    private val callback: ((Article) -> Unit)?
) : RecyclerView.Adapter<LandscapeNewsVH>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LandscapeNewsVH {
        return LandscapeNewsVH(
            ItemNewsLandscapeBinding.bind(
                LayoutInflater.from(parent.context).inflate(
                    R.layout.item_news_landscape, parent, false
                )
            )
        )
    }

    override fun getItemCount(): Int = list.size
    override fun onBindViewHolder(holder: LandscapeNewsVH, position: Int) {
        val article = list[position]
        holder.bind(article)
        holder.itemView.setOnClickListener {
            callback?.invoke(article)
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    fun submitData(list: List<Article>) {
        this.list = list
        notifyDataSetChanged()
    }

}
