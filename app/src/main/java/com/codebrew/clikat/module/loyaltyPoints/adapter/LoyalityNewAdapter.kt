package com.codebrew.clikat.module.loyaltyPoints.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.codebrew.clikat.R
import com.codebrew.clikat.data.model.api.LoyalityDataItem
import com.codebrew.clikat.databinding.ItemLoyalityPointsDescBinding
import kotlinx.android.synthetic.main.item_loyality_points_desc.view.*

class LoyalityNewAdapter(private val loyalityList: List<LoyalityDataItem>)  : RecyclerView.Adapter<LoyalityNewAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = DataBindingUtil.inflate<ItemLoyalityPointsDescBinding>(LayoutInflater.from(parent.context),
                R.layout.item_loyality_points_desc, parent, false)

        return ViewHolder(binding.root)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val  mLoyalityData= loyalityList[holder.adapterPosition]
        holder.onBind(mLoyalityData)
    }

    override fun getItemCount(): Int {
        return loyalityList.count()
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun onBind(earnedDataItem: LoyalityDataItem?) = with(itemView){
            txt_loyality_point.text=earnedDataItem?.title
            Glide.with(context).load( earnedDataItem?.drawable).into(iv_trophy)
            btn_points.text=earnedDataItem?.rewards
            rv_loyality_desc?.adapter=LoyalityDescAdapter(earnedDataItem?.rewardsPoint?.toMutableList()?: mutableListOf())

        }
    }

}