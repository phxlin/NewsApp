package lin.yufan.newsapp.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import lin.yufan.newsapp.R
import lin.yufan.newsapp.data.model.Article
import lin.yufan.newsapp.util.getProgressDrawable
import lin.yufan.newsapp.util.loadImage

class NewsAdapter : RecyclerView.Adapter<NewsAdapter.ArticleViewHolder>() {

    val diff = AsyncListDiffer(this, object : DiffUtil.ItemCallback<Article>() {
        override fun areItemsTheSame(oldItem: Article, newItem: Article) =
            oldItem.url == newItem.url

        override fun areContentsTheSame(oldItem: Article, newItem: Article) =
            oldItem == newItem
    })

    private var onItemClickListener: ((Article) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        ArticleViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.item_article_preview,
                parent,
                false
            )
        )

    override fun getItemCount() = diff.currentList.size

    override fun onBindViewHolder(holder: ArticleViewHolder, position: Int) {
        val currentArticle = diff.currentList[position]

        holder.itemView.apply {
            val ivArticleImage = findViewById<ImageView>(R.id.ivArticleImage)
            val tvTitle = findViewById<TextView>(R.id.tvTitle)
            val tvDescription = findViewById<TextView>(R.id.tvDescription)
            val tvSource = findViewById<TextView>(R.id.tvSource)
            val tvPublishedAt = findViewById<TextView>(R.id.tvPublishedAt)

            ivArticleImage.loadImage(
                currentArticle.urlToImage,
                getProgressDrawable(context)
            )
            tvTitle.text = currentArticle.title
            tvDescription.text = currentArticle.description
            tvSource.text = currentArticle.source?.name
            tvPublishedAt.text = currentArticle.publishedAt

            setOnClickListener {
                onItemClickListener?.let {
                    it(currentArticle)
                }
            }
        }
    }

    fun setOnItemClickListener(listener: (Article) -> Unit) {
        onItemClickListener = listener
    }

    inner class ArticleViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
}