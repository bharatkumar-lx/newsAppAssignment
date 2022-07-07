package com.bharat.newsappassignment.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bharat.newsappassignment.databinding.ArticalPreviewBinding
import com.bharat.newsappassignment.model.Article
import com.bharat.newsappassignment.utils.Utilities
import com.bumptech.glide.Glide



class NewsAdapter : RecyclerView.Adapter<NewsAdapter.ArticleViewHolder>(){
    inner class ArticleViewHolder(val binding: ArticalPreviewBinding): RecyclerView.ViewHolder(binding.root)

    //    DiffUtil only update the changed  list
//    it compare 2 lists and only update the changed one
    private val differCallBack = object :DiffUtil.ItemCallback<Article>(){
        override fun areItemsTheSame(oldItem: Article, newItem: Article): Boolean {
            return oldItem.url == oldItem.url
        }

        override fun areContentsTheSame(oldItem: Article, newItem: Article): Boolean {
            return oldItem == newItem
        }
    }


    val differ = AsyncListDiffer(this,differCallBack)


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ArticleViewHolder {
        val binding = ArticalPreviewBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ArticleViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ArticleViewHolder, position: Int) {
        val article = differ.currentList[position]
        val binding = holder.binding
        holder.binding.apply {
            Glide.with(binding.previewImageView).load(article.urlToImage).into(binding.previewImageView)
            binding.newsTitle.text = article.title
            binding.newsDescription.text = article.description
            binding.publishTime.text =
                article.publishedAt?.let { Utilities.getPublishTime(it,article?.source?.name) }
        }

    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }


    private var onItemClickListener :((Article) -> Unit)? = null

    fun setOnItemClickListener(listener:(Article) -> Unit){
        onItemClickListener = listener

    }

}