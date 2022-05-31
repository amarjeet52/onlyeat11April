package com.codebrew.clikat.module.cart.adapter

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.codebrew.clikat.R
import com.codebrew.clikat.app_utils.AppUtils
import com.codebrew.clikat.data.VendorAppType
import com.codebrew.clikat.data.constants.AppConstants
import com.codebrew.clikat.data.model.api.Currency
import com.codebrew.clikat.databinding.ItemTipsBinding
import com.codebrew.clikat.modal.other.SettingModel
import com.codebrew.clikat.utils.Utils
import com.codebrew.clikat.utils.configurations.Configurations
import kotlinx.android.synthetic.main.item_tips.view.*

const val TIP_PERCEN = 1
const val TIP_FIXED = 2

class TipAdapter(private val mContext: Context, val tipList: ArrayList<Int>,
                 private val screenFlow: SettingModel.DataBean.ScreenFlowBean?,
                 private val appUtils: AppUtils,
private val settingsData:SettingModel.DataBean.SettingData?,val selectedCurrency:Currency?)
    : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var mCallback: TipCallback? = null

    private var tipType: Int? = null
    private var adapterPosition=-1

    private var colorConfig = Configurations.colors

    fun tipCallback(mCallback: TipCallback?) {
        this.mCallback = mCallback
    }

    fun tipType(tipType: Int?) {
        this.tipType = tipType
    }

    fun sekectedTip(adapterPosition: Int) {
        this.adapterPosition = adapterPosition
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val binding: ItemTipsBinding = ItemTipsBinding.inflate(LayoutInflater.from(mContext), parent, false)
        binding.color = colorConfig
        binding.drawables = Configurations.drawables
        binding.strings = appUtils.loadAppConfig(0).strings
        binding.singleVndorType = screenFlow?.is_single_vendor == VendorAppType.Single.appType
        return HeaderViewHolder(binding.root)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is HeaderViewHolder) {
            val holder2 = holder
            holder2.tvTip.text = if (tipType == TIP_FIXED) {
                mContext.getString(R.string.currency_tag, AppConstants.CURRENCY_SYMBOL, Utils.getPriceFormat(tipList.get(position).toFloat(),settingsData,selectedCurrency))
            } else {
                "${tipList.get(position)} %"
            }

            if (adapterPosition ==position) {
                holder2.tvTip.setTextColor(Color.parseColor(colorConfig.appBackground))
                holder2.tvTip.setBackgroundColor(Color.parseColor(colorConfig.primaryColor))
            } else {
                holder2.tvTip.setTextColor(Color.parseColor(colorConfig.textListHead))
                holder2.tvTip.setBackgroundColor(Color.parseColor(colorConfig.appBackground))
            }

        }
    }

    override fun getItemCount(): Int {
        return tipList.size
    }

    inner class HeaderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var tvTip = itemView.tvTip

        init {
            tvTip.setOnClickListener { v: View? -> mCallback?.onTipSelected(adapterPosition) }
        }
    }

    interface TipCallback {
        fun onTipSelected(position: Int)
    }
}