package lin.yufan.whatsnew.data.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity(tableName = "articles")
data class Article(
    @ColumnInfo(name = "article_author")
    val author: String?,
    @ColumnInfo(name = "article_content")
    val content: String?,
    @ColumnInfo(name = "article_description")
    val description: String?,
    @ColumnInfo(name = "article_publishedAt")
    val publishedAt: String?,
    @ColumnInfo(name = "article_source")
    val source: Source?,
    @ColumnInfo(name = "article_title")
    val title: String?,
    @ColumnInfo(name = "article_url")
    val url: String?,
    @ColumnInfo(name = "article_image")
    val urlToImage: String?
) : Serializable {
    @PrimaryKey(autoGenerate = true)
    var id: Int? = null
}