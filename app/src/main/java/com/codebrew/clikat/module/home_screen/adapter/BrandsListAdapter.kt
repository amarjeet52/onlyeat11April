package com.codebrew.clikat.module.home_screen.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.codebrew.clikat.R
import com.codebrew.clikat.app_utils.extension.loadImage
import com.codebrew.clikat.databinding.ItemHomeBrandBinding
import com.codebrew.clikat.modal.other.Brand
import com.codebrew.clikat.utils.configurations.Configurations
import kotlinx.android.synthetic.main.item_home_brand.view.*

class BrandsListAdapter(private val brandlist: List<Brand>) : RecyclerView.Adapter<BrandsListAdapter.ViewHolder>() {

    var mCallback: BrandCallback? = null

    fun settingCallback(mCallback: BrandCallback) {
        this.mCallback = mCallback
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = DataBindingUtil.inflate<ItemHomeBrandBinding>(LayoutInflater.from(parent.context),
                R.layout.item_home_brand, parent, false)
        binding.color = Configurations.colors
        return ViewHolder(binding.root)
    }

    override fun getItemCount(): Int {
        return brandlist.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        holder.onBind(brandlist[holder.adapterPosition])

        holder.itemView.setOnClickListener {

            mCallback?.onBrandSelect(brandlist[holder.adapterPosition])
        }
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val ivBrand = itemView.iv_product
        val tvBrand = itemView.tv_brand

        fun onBind(brandsBean: Brand) {
            tvBrand.text = brandsBean.name
            brandsBean.image?.let { ivBrand.loadImage(it) }

        }
    }

    interface BrandCallback {
        fun onBrandSelect(brandsBean: Brand)
    }
}