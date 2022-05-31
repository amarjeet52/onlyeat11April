package com.codebrew.clikat.module.cart.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.codebrew.clikat.R
import com.codebrew.clikat.data.constants.AppConstants
import com.codebrew.clikat.data.model.api.AnswersData
import com.codebrew.clikat.data.model.api.Currency
import com.codebrew.clikat.modal.other.SettingModel
import com.codebrew.clikat.utils.Utils
import kotlinx.android.synthetic.main.item_select_ans.view.*

class SelectAnsAdapter(private val mContext: Context, private val list: List<AnswersData>, private val settingData: SettingModel.DataBean.SettingData?,
                       private val selectedCurrency: Currency?) : RecyclerView.Adapter<SelectAnsAdapter.SelectAnsViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SelectAnsViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_select_ans, parent, false)
        return SelectAnsViewHolder(view)
    }

    override fun getItemCount(): Int {
        return list.size
    }


    override fun onBindViewHolder(holder: SelectAnsViewHolder, position: Int) {

        val answerData = list[position]

        holder.tvAnswer.text = ". " + answerData.optionLabel

        val totalAddonChargeByPercent = (answerData.productPrice.times(answerData.percentageValue)).div(100)

        val priceTag = if (answerData.flatValue > 0) {
            holder.itemView.context.getString(R.string.currency_tag, AppConstants.CURRENCY_SYMBOL, Utils.getPriceFormat(answerData.flatValue,settingData,selectedCurrency))
        } else {
            holder.itemView.context.getString(R.string.currency_tag, AppConstants.CURRENCY_SYMBOL, Utils.getPriceFormat(totalAddonChargeByPercent,settingData,selectedCurrency))
        }

        holder.tv_price.text = priceTag

    }


    inner class SelectAnsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        internal var tvAnswer: TextView = itemView.tvAnswer
        internal var tv_price: TextView = itemView.tvPrice
    }


}