package com.codebrew.clikat.dialog_flow.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.codebrew.clikat.data.constants.AppConstants
import com.codebrew.clikat.data.model.api.Currency
import com.codebrew.clikat.databinding.DgflowItemProductBinding
import com.codebrew.clikat.modal.other.ProductDataBean
import com.codebrew.clikat.modal.other.SettingModel
import com.codebrew.clikat.utils.Utils
import com.codebrew.clikat.utils.configurations.Configurations
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class DialogProdAdapter : ListAdapter<ProdItem, RecyclerView.ViewHolder>(ItemListDiffCallback()) {

    private  var selectedCurrency: Currency?=null
    private val adapterScope = CoroutineScope(Dispatchers.Default)

    private lateinit var mCallback: ProdListener

    private var settingModel: SettingModel.DataBean.SettingData ?=null

    fun settingCallback(clientInf: SettingModel.DataBean.SettingData?,currency:Currency?, mCallback: ProdListener) {
        this.mCallback = mCallback
        settingModel=clientInf
        selectedCurrency=currency
    }

    fun submitItemList(list: List<ProductDataBean>?) {
        adapterScope.launch {
            val items = list?.map { ProdItem.AddonItem(it) }

            withContext(Dispatchers.Main) {
                submitList(items)
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is DayViewHolder -> {
                val modelItem = getItem(position) as ProdItem.AddonItem
                holder.bind(modelItem.mDayData, mCallback,settingModel,selectedCurrency)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return DayViewHolder.from(parent)
    }
}


class DayViewHolder private constructor(val binding: DgflowItemProductBinding) :
        RecyclerView.ViewHolder(binding.root) {

    fun bind(item: ProductDataBean, listener: ProdListener, settingModel: SettingModel.DataBean.SettingData?,selectedCurrency:Currency?) {

        item.price= Utils.getPriceFormat(item.price?.toFloat()?:0f,settingModel,selectedCurrency)
        item.display_price= Utils.getPriceFormat(item.display_price?.toFloat()?:0f,settingModel,selectedCurrency)

        binding.productItem = item
        binding.isWeightVisible=settingModel?.is_product_weight =="1"
        binding.currency = AppConstants.CURRENCY_SYMBOL
        binding.prodlistener = listener
        binding.color = Configurations.colors
        binding.strings = Configurations.strings

        binding.executePendingBindings()
    }

    companion object {
        fun from(parent: ViewGroup): DayViewHolder {
            val layoutInflater = LayoutInflater.from(parent.context)
            val binding = DgflowItemProductBinding.inflate(layoutInflater, parent, false)
            return DayViewHolder(binding)
        }
    }
}


class ItemListDiffCallback : DiffUtil.ItemCallback<ProdItem>() {
    override fun areItemsTheSame(oldItem: ProdItem, newItem: ProdItem): Boolean {
        return oldItem.dayData == newItem.dayData
    }

    @SuppressLint("DiffUtilEquals")
    override fun areContentsTheSame(oldItem: ProdItem, newItem: ProdItem): Boolean {
        return oldItem.dayData.id == newItem.dayData.id
    }
}

class ProdListener(val clickListener: (rentalModel: ProductDataBean) -> Unit) {
    fun prodClick(product: ProductDataBean) = clickListener(product)
}


sealed class ProdItem {
    data class AddonItem(val mDayData: ProductDataBean) : ProdItem() {
        override val dayData = mDayData
    }

    abstract val dayData: ProductDataBean
}

