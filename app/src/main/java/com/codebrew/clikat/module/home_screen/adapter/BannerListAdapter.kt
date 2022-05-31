package com.codebrew.clikat.module.home_screen.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.codebrew.clikat.R
import com.codebrew.clikat.app_utils.extension.loadBannerImage
import com.codebrew.clikat.data.VendorAppType
import com.codebrew.clikat.databinding.ItemFullBannerBinding
import com.codebrew.clikat.databinding.ItemHalfBannerBinding
import com.codebrew.clikat.modal.other.TopBanner
import com.codebrew.clikat.utils.StaticFunction.loadImage
import com.codebrew.clikat.utils.configurations.Configurations
import com.makeramen.roundedimageview.RoundedImageView

class BannerListAdapter(private val bannerList: List<TopBanner>, private val mBannerWidth: Int,private val mSingleVendor: Int) : RecyclerView.Adapter<BannerListAdapter.ViewHolder>() {
    private var callback: BannerCallback? = null

    fun settingCallback(mCallabck: BannerCallback?) {
        callback = mCallabck
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return if (viewType == ITEM_BANNER_FULL) {
            val binding: ItemFullBannerBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.item_full_banner, parent, false)
            binding.color = Configurations.colors
            ViewHolder(binding.root)
        } else {
            val binding: ItemHalfBannerBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.item_half_banner, parent, false)
            binding.color = Configurations.colors
            ViewHolder(binding.root)
        }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        /*   if (bannerList.get(position).getName() != null) {
            holder.bannerName.setText(bannerList.get(position).getName());
        }*/
        if (bannerList[position].phone_image != null) {
            if (bannerList[position].isEnabled) holder.onBind()
            holder.bannerImage.loadBannerImage(bannerList[position].phone_image?:"",R.drawable.bg_graphic)
        } else {
            val requestOptions = RequestOptions()
                    .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                    .placeholder(R.drawable.bg_graphic)
                    .error(R.drawable.bg_graphic)
            if (bannerList[0].pickupImage?.isNotEmpty() == true){
                Glide.with(holder.bannerImage.context).load(bannerList[position].pickupImage).apply(requestOptions).into(holder.bannerImage)
            } else {
                Glide.with(holder.bannerImage.context).load(bannerList[position].bannerImage).apply(requestOptions).into(holder.bannerImage)
            }
        }
    }

    override fun getItemCount(): Int {
        return bannerList.size
    }

    override fun getItemViewType(position: Int): Int {
        return if (mBannerWidth==0)  ITEM_BANNER_FULL
        else  TYPE_BANNER_HALF
    }

    interface BannerCallback {
        fun onBannerDetail(bannerBean: TopBanner?)
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var bannerImage: RoundedImageView = itemView.findViewById(R.id.iv_banner)
        var bannerName: TextView = itemView.findViewById(R.id.tv_bannerName)
        init {
            itemView.setOnClickListener { v: View? ->
//                callback?.onBannerDetail(bannerList[adapterPosition])
            }
        }
        fun onBind() {

        }

    }

    companion object {
        private const val ITEM_BANNER_FULL = 0
        private const val TYPE_BANNER_HALF = 1
    }


}