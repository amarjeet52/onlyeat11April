package com.codebrew.clikat.app_utils.extension

import android.graphics.ColorMatrix
import android.graphics.ColorMatrixColorFilter
import android.graphics.drawable.Drawable
import android.util.Log
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.RequestOptions
import com.codebrew.clikat.BuildConfig
import com.codebrew.clikat.R
import com.codebrew.clikat.data.AppDataType
import com.codebrew.clikat.data.constants.AppConstants
import com.codebrew.clikat.utils.Utils
import jp.wasabeef.glide.transformations.RoundedCornersTransformation


internal fun ImageView.loadImage(url: String, imageHeight: Int? = null, imageWidth: Int? = null) {


    val glide = Glide.with(this.context)


    val requestOptions = RequestOptions
            .bitmapTransform(RoundedCornersTransformation(8, 0,
                    RoundedCornersTransformation.CornerType.ALL))
            .diskCacheStrategy(DiskCacheStrategy.ALL)
            .placeholder(if (AppConstants.APP_SUB_TYPE == AppDataType.Food.type) R.drawable.iv_placeholder else R.drawable.white_placeholder)
            .error(if (AppConstants.APP_SUB_TYPE == AppDataType.Food.type) R.drawable.iv_placeholder else R.drawable.white_placeholder)

    glide.load(url)
            .apply(requestOptions)
            .listener(object : RequestListener<Drawable> {
                override fun onLoadFailed(e: GlideException?, model: Any?, target: com.bumptech.glide.request.target.Target<Drawable>?, isFirstResource: Boolean): Boolean {
                    return false // important to return false so the error placeholder can be placed
                }

                override fun onResourceReady(resource: Drawable?, model: Any?, target: com.bumptech.glide.request.target.Target<Drawable>?, dataSource: DataSource?, isFirstResource: Boolean): Boolean {
                    return false
                }
            }).into(this)


}


internal fun ImageView.loadUserImage(url: String) {

    val glide = Glide.with(this.context)

    val requestOptions = RequestOptions
            .bitmapTransform(RoundedCornersTransformation(8, 0,
                    RoundedCornersTransformation.CornerType.ALL))
            .diskCacheStrategy(DiskCacheStrategy.ALL)
            .placeholder(R.drawable.ic_user_placeholder)
            .error(R.drawable.ic_user_placeholder)


    glide.load(url).apply(requestOptions).into(this)

}


internal fun ImageView.loadBannerImage(url: String, placeholder: Int) {

    val glide = Glide.with(this.context)

    val requestOptions = RequestOptions
            .bitmapTransform(RoundedCornersTransformation(8, 0,
                    RoundedCornersTransformation.CornerType.ALL))
            .diskCacheStrategy(DiskCacheStrategy.ALL)
            .placeholder(placeholder)
            .error(placeholder)


    glide.load(url).apply(requestOptions).into(this)

}

internal fun ImageView.loadPlaceHolder(url: String) {

    val glide = Glide.with(this.context)

    val requestOptions = RequestOptions
            .bitmapTransform(RoundedCornersTransformation(8, 0,
                    RoundedCornersTransformation.CornerType.ALL))
            .diskCacheStrategy(DiskCacheStrategy.ALL)
            .placeholder(R.drawable.img_nothing_found)
            .error(R.drawable.img_nothing_found)


    glide.load(url).apply(requestOptions).into(this)

}

fun ImageView.setGreyScale() {
    val matrix = ColorMatrix()
    matrix.setSaturation(0f) //0 means grayscale
    val cf = ColorMatrixColorFilter(matrix)
    colorFilter = cf
}

fun ImageView.setColorScale() {
    colorFilter = null
}