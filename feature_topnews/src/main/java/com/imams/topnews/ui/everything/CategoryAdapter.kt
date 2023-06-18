package com.imams.topnews.ui

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.imams.newsapi.model.Category
import com.imams.topnews.R
import com.imams.topnews.databinding.ItemCategoryNewsBinding

class CategoryAdapter(
    private var list: List<Category>,
    private val callback: ((Category) -> Unit)?
): RecyclerView.Adapter<CategoryVH>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryVH {
        return CategoryVH(
            ItemCategoryNewsBinding.bind(
            LayoutInflater.from(parent.context).inflate(
                R.layout.item_category_news, parent, false
            )
        ))
    }

    override fun getItemCount(): Int = list.size

    override fun onBindViewHolder(holder: CategoryVH, position: Int) {
        holder.bind(list[position])
        holder.itemView.setOnClickListener {
            callback?.invoke(list[position])
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    fun submit(list: List<Category>) {
        this.list = list
        notifyDataSetChanged()
    }

}

class CategoryVH(
    private val binding: ItemCategoryNewsBinding,
): RecyclerView.ViewHolder(binding.root) {

    fun bind(item: Category) {
        with(binding) {
            tvTitle.text = item.label
        }
    }
}