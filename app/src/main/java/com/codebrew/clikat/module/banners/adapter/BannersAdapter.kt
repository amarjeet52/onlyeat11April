package com.codebrew.clikat.module.banners.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.codebrew.clikat.R
import com.codebrew.clikat.app_utils.extension.loadBannerImage
import com.codebrew.clikat.databinding.ItemBannersBinding
import com.codebrew.clikat.modal.other.TopBanner
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class BannersAdapter :
        ListAdapter<BannerItem, RecyclerView.ViewHolder>(ItemListDiffCallback()) {

    private val adapterScope = CoroutineScope(Dispatchers.Default)
    private lateinit var mCallback: ItemListener

    fun settingCallback(mCallback: ItemListener) {
        this.mCallback = mCallback
    }

    fun submitItemList(list: List<TopBanner>?) {
        adapterScope.launch {
            val items = list?.map { BannerItem.BannersItem(it) }

            withContext(Dispatchers.Main) {
                submitList(items)
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is BannersViewHolder -> {
                val nightItem = getItem(position) as BannerItem.BannersItem
                holder.bind(nightItem.mBannerData, mCallback)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return BannersViewHolder.from(parent)
    }
}


class BannersViewHolder private constructor(val binding: ItemBannersBinding) :
        RecyclerView.ViewHolder(binding.root) {

    fun bind(mBannerData: TopBanner, mCallback: ItemListener) {
        binding.ivBanner.loadBannerImage(mBannerData.phone_image
                ?: "", R.drawable.ic_banner_placeholder)
        binding.ivBanner.setOnClickListener {
            mCallback.bannerClick(mBannerData)
        }
    }

    companion object {
        fun from(parent: ViewGroup): BannersViewHolder {
            val layoutInflater = LayoutInflater.from(parent.context)
            val binding = ItemBannersBinding.inflate(layoutInflater, parent, false)
            return BannersViewHolder(binding)
        }
    }
}

class ItemListener(val clickListener: (item: TopBanner) -> Unit) {
    fun bannerClick(clickListener: TopBanner) = clickListener(clickListener)
}


class ItemListDiffCallback : DiffUtil.ItemCallback<BannerItem>() {
    override fun areItemsTheSame(oldItem: BannerItem, newItem: BannerItem): Boolean {
        return oldItem.bannerData == newItem.bannerData
    }

    @SuppressLint("DiffUtilEquals")
    override fun areContentsTheSame(oldItem: BannerItem, newItem: BannerItem): Boolean {
        return oldItem == newItem
    }
}


sealed class BannerItem {
    data class BannersItem(val mBannerData: TopBanner) : BannerItem() {
        override val bannerData = mBannerData
    }

    abstract val bannerData: TopBanner
}

