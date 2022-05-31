package com.codebrew.clikat.module.loyaltyPoints.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.codebrew.clikat.R
import com.codebrew.clikat.data.model.api.LoyalityPointItem
import com.codebrew.clikat.databinding.ItemLoyalityDescBinding
import kotlinx.android.synthetic.main.item_loyality_desc.view.*

class LoyalityDescAdapter( private val loyalityDescList: MutableList<String>)  : RecyclerView.Adapter<LoyalityDescAdapter.ViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = DataBindingUtil.inflate<ItemLoyalityDescBinding>(LayoutInflater.from(parent.context),
                R.layout.item_loyality_desc, parent, false)

        return ViewHolder(binding.root)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        loyalityDescList[holder.adapterPosition].let {
            holder.onBind(it,(position+1))
        }
    }

    override fun getItemCount(): Int {
        return loyalityDescList.count()
    }

    class ViewHolder (itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun onBind(descItem: String?, position: Int) = with(itemView){
            tv_point.text=descItem
            tv_serial.text=position.toString()
        }
    }
}