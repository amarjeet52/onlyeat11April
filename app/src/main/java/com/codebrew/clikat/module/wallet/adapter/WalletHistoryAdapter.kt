package com.codebrew.clikat.module.wallet.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.codebrew.clikat.R
import com.codebrew.clikat.app_utils.DateTimeUtils
import com.codebrew.clikat.data.WalletAmountStatus
import com.codebrew.clikat.data.constants.AppConstants
import com.codebrew.clikat.data.model.api.Currency
import com.codebrew.clikat.databinding.ItemWalletHistroyBinding
import com.codebrew.clikat.modal.other.SettingModel
import com.codebrew.clikat.modal.wallet.TransactionsItem
import com.codebrew.clikat.utils.StaticFunction
import com.codebrew.clikat.utils.Utils
import com.codebrew.clikat.utils.configurations.Configurations
import kotlinx.android.synthetic.main.item_wallet_histroy.view.*


class WalletHistoryAdapter(private val settingData: SettingModel.DataBean.SettingData?,
                           private val selectedCurrency:Currency?,
                           private val dateTimeUtils: DateTimeUtils) : RecyclerView.Adapter<WalletHistoryAdapter.ViewHolder>() {

    private var list = ArrayList<TransactionsItem>()

    override fun getItemCount(): Int {
        return list.size
    }


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(list[position])
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = ItemWalletHistroyBinding.inflate(layoutInflater, parent, false)
        binding.color = Configurations.colors
        binding.strings = Configurations.strings
        return ViewHolder(binding.root)
    }


    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bind(item: TransactionsItem) = with(itemView) {
            tvAmount?.text = context.getString(R.string.currency_tag, AppConstants.CURRENCY_SYMBOL, Utils.getPriceFormat(item.amount
                    ?: 0f, settingData,selectedCurrency))


            val date=item?.formattedDate?:""

            when (item.addedDeductThrough) {
                WalletAmountStatus.ByAccount.added_deduct_through -> {
                    tvAmount?.setTextColor(ContextCompat.getColor(context, R.color.username4))
                    tvAmountAddedTime?.text = context.getString(R.string.amount_added, date)
                }
                WalletAmountStatus.ByShare.added_deduct_through -> {
                    if (item.isAdd == 1) {
                        tvAmount?.setTextColor(ContextCompat.getColor(context, R.color.username4))
                        tvAmountAddedTime?.text = context.getString(R.string.amount_received,  item.shareUserName) + " - " + date
                    } else {
                        tvAmount?.setTextColor(ContextCompat.getColor(context, R.color.red))
                        tvAmountAddedTime?.text = context.getString(R.string.amount_sent, item.shareUserName) + " - " + date
                    }
                }
                WalletAmountStatus.ByRefundAdded.added_deduct_through -> {
                    tvAmount?.setTextColor(ContextCompat.getColor(context, R.color.username4))
                    tvAmountAddedTime?.text = context.getString(R.string.refund_added, date)
                }
                WalletAmountStatus.ByOrderPlaced.added_deduct_through -> {
                    tvAmount?.setTextColor(ContextCompat.getColor(context, R.color.red))
                    tvAmountAddedTime?.text = context.getString(R.string.order_placed, date)
                }
                else -> {
                    tvAmount?.setTextColor(ContextCompat.getColor(context, R.color.username4))

                    tvAmountAddedTime?.text = context.getString(R.string.amount_added, date)
                }
            }

        }
    }

    fun addList(isFirstPage: Boolean?, list: ArrayList<TransactionsItem>) {
        if (isFirstPage == true)
            this.list.clear()
        this.list.addAll(list)
        notifyDataSetChanged()
    }

}

