package com.codebrew.clikat.module.loyaltyPoints.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.codebrew.clikat.R
import com.codebrew.clikat.data.constants.AppConstants
import com.codebrew.clikat.data.model.api.EarnedDataItem
import com.codebrew.clikat.databinding.ItemLoyaltyPointsBinding
import kotlinx.android.synthetic.main.item_loyalty_points.view.*

class LoyaltyAdapter : RecyclerView.Adapter<LoyaltyAdapter.ViewHolder>() {
    private var earnedList: ArrayList<EarnedDataItem>? = arrayListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = DataBindingUtil.inflate<ItemLoyaltyPointsBinding>(LayoutInflater.from(parent.context),
                R.layout.item_loyalty_points, parent, false)

        return ViewHolder(binding.root)
    }

    override fun getItemCount(): Int = earnedList?.size?:0

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.onBind(earnedList?.get(position))
    }

    fun addList(list: ArrayList<EarnedDataItem>) {
        earnedList?.addAll(list)
        notifyDataSetChanged()
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun onBind(earnedDataItem: EarnedDataItem?) = with(itemView){
            /*always 1 point earn for one order*/

            tvOrderId?.text=context?.getString(R.string.order_id,earnedDataItem?.orderId.toString())
             if(earnedDataItem?.used_loyality_point_amount?:0.0>0)
             {
                 tvEarning?.text=context.getString(R.string.you_spent_loyalty_discount)
                 tvEarningAmount?.text=context.getString(R.string.used_loyalty_point,AppConstants.CURRENCY_SYMBOL,earnedDataItem?.used_loyality_point_amount.toString())
             }
            else
             {
                 tvEarning?.text=context.getString(R.string.you_earn_points,earnedDataItem?.earned_points?:"1")
                 tvEarningAmount?.text= context.getString(R.string.earned_amount,AppConstants.CURRENCY_SYMBOL,earnedDataItem?.earnedAmount.toString())
             }
        }
    }
}