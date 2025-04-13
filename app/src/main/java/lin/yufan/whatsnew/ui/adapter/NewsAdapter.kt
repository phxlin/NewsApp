package lin.yufan.whatsnew.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import lin.yufan.whatsnew.data.model.Article
import lin.yufan.whatsnew.databinding.ItemArticlePreviewBinding
import lin.yufan.whatsnew.util.getProgressDrawable
import lin.yufan.whatsnew.util.loadImage

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
            ItemArticlePreviewBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )

    override fun onBindViewHolder(holder: ArticleViewHolder, position: Int) {
        holder.bind(diff.currentList[position])
    }

    override fun getItemCount() = diff.currentList.size

    fun setOnItemClickListener(listener: (Article) -> Unit) {
        onItemClickListener = listener
    }

    inner class ArticleViewHolder(private val binding: ItemArticlePreviewBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(article: Article) = with(binding) {
            ivArticleImage.loadImage(
                article.urlToImage,
                getProgressDrawable(itemView.context)
            )
            tvTitle.text = article.title
            tvDescription.text = article.description
            tvSource.text = article.source?.name
            tvPublishedAt.text = article.publishedAt

            itemView.setOnClickListener {
                onItemClickListener?.let {
                    it(article)
                }
            }
        }
    }
}