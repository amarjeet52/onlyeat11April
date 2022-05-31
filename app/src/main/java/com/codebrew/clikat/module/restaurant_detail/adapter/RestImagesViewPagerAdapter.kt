package com.codebrew.clikat.module.restaurant_detail.adapter

import android.app.Activity
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.viewpager.widget.PagerAdapter
import com.bumptech.glide.Glide
import com.codebrew.clikat.R
import com.codebrew.clikat.app_utils.extension.loadBannerImage
import com.codebrew.clikat.app_utils.extension.loadImage
import com.codebrew.clikat.utils.StaticFunction
import com.codebrew.clikat.utils.Utils
import kotlinx.android.synthetic.main.restaurant_pager_image.view.*


class RestImagesViewPagerAdapter(
        private val context: Context,

        private var onImageClicked: OnImageClicked?
) : PagerAdapter() {

    private val images = ArrayList<String>()

    override fun getCount(): Int {
        return images.size
    }

    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view === `object` as View
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {

        val view = LayoutInflater.from(context).inflate(R.layout.restaurant_pager_image, container, false)
        val isVideo = StaticFunction.isVideoFile(images[position])

        if (isVideo) {
            view?.ivPlay?.visibility = View.VISIBLE
            Glide.with(context).load(images[position]).into(view.sdvAds)
        } else {
            view?.ivPlay?.visibility = View.GONE
            view.sdvAds.loadBannerImage(images[position],R.drawable.bg_graphic)
           // StaticFunction.loadImage(images[position], view.sdvAds, false)
        }
        container.addView(view)

        view?.setOnClickListener {
            if (isVideo && images[position].isNotEmpty())
                onImageClicked?.onImageClicked(images[position])
        }
        return view
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        container.removeView(`object` as View)
    }

    override fun getItemPosition(`object`: Any): Int {
        return POSITION_NONE
    }

    interface OnImageClicked {
        fun onImageClicked(images: String)
    }

    fun addImages(list: List<String>) {
        images.clear()
        images.addAll(list)
        notifyDataSetChanged()
    }
}