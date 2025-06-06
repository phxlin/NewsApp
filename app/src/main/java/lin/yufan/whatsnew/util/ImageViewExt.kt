package lin.yufan.whatsnew.util

import android.content.Context
import android.widget.ImageView
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import lin.yufan.whatsnew.R

fun getProgressDrawable(context: Context): CircularProgressDrawable =
    CircularProgressDrawable(context).apply {
        strokeWidth = 10f
        centerRadius = 50f
        start()
    }

fun ImageView.loadImage(
    uri: String?,
    progressDrawable: CircularProgressDrawable
) {
    val options = RequestOptions()
        .placeholder(progressDrawable)
        .error(R.drawable.ic_image_removed)

    Glide.with(this.context)
        .setDefaultRequestOptions(options)
        .load(uri)
        .into(this)
}