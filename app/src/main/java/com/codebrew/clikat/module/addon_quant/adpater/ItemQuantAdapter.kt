package com.codebrew.clikat.module.addon_quant.adpater

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.codebrew.clikat.R
import com.codebrew.clikat.data.constants.AppConstants
import com.codebrew.clikat.databinding.ItemSavedAddonBinding
import com.codebrew.clikat.modal.CartInfo
import com.codebrew.clikat.modal.other.SettingModel
import com.codebrew.clikat.utils.configurations.Configurations
import kotlinx.android.synthetic.main.item_saved_addon.view.*
import kotlinx.android.synthetic.main.layout_prod_stepper.view.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.*


class ItemQuantAdapter(val settingsData: SettingModel.DataBean.SettingData?) : ListAdapter<ProductItem, RecyclerView.ViewHolder>(ItemListDiffCallback()) {

    private val adapterScope = CoroutineScope(Dispatchers.Default)

    private lateinit var mCallback: ItemListener

    fun settingCallback(mCallback: ItemListener) {
        this.mCallback = mCallback
    }

    fun submitItemList(list: List<CartInfo>?) {
        adapterScope.launch {
            val items = list?.map { ProductItem.AddonItem(it) }

            withContext(Dispatchers.Main) {
                submitList(items)
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is SavedAddOnViewHolder -> {
                val nightItem = getItem(position) as ProductItem.AddonItem
                holder.bind(nightItem.mAddonData, mCallback,settingsData)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return SavedAddOnViewHolder.from(parent)
    }
}


class SavedAddOnViewHolder private constructor(val binding: ItemSavedAddonBinding) :
        RecyclerView.ViewHolder(binding.root) {

    fun bind(item: CartInfo, clickListener: ItemListener, settingsData: SettingModel.DataBean.SettingData?) {
        binding.listener = clickListener
        binding.itemData = item
        binding.currency = AppConstants.CURRENCY_SYMBOL
        binding.color = Configurations.colors

        val price= String.format(Locale("en"),"%.2f", (item.price * item.quantity))
        itemView.tv_addon_price?.text= itemView.context.getString(R.string.currency_tag,AppConstants.CURRENCY_SYMBOL,
                price)

        itemView.tv_count?.text=if (settingsData?.is_decimal_quantity_allowed == "1")
            String.format(Locale("en"),"%.2f", item.quantity)
        else (item.quantity.toInt()).toString()
        binding.executePendingBindings()
    }

    companion object {
        fun from(parent: ViewGroup): SavedAddOnViewHolder {
            val layoutInflater = LayoutInflater.from(parent.context)
            val binding = ItemSavedAddonBinding.inflate(layoutInflater, parent, false)
            return SavedAddOnViewHolder(binding)
        }
    }
}


class ItemListDiffCallback : DiffUtil.ItemCallback<ProductItem>() {
    override fun areItemsTheSame(oldItem: ProductItem, newItem: ProductItem): Boolean {
        return oldItem.addonData == newItem.addonData
    }

    @SuppressLint("DiffUtilEquals")
    override fun areContentsTheSame(oldItem: ProductItem, newItem: ProductItem): Boolean {
        return oldItem.addonData.quantity == newItem.addonData.quantity
    }
}

class ItemListener(val clickListener: (model: CartInfo) -> Unit,
                   val minuslistener: (model: CartInfo) -> Unit) {
    fun addItem(addonBean: CartInfo) = clickListener(addonBean)
    fun minusItem(addonBean: CartInfo) = minuslistener(addonBean)
}


sealed class ProductItem {
    data class AddonItem(val mAddonData: CartInfo) : ProductItem() {
        override val addonData = mAddonData
    }

    abstract val addonData: CartInfo
}

