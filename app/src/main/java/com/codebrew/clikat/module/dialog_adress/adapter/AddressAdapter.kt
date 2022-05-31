package com.codebrew.clikat.module.dialog_adress.adapter

import android.annotation.SuppressLint
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.codebrew.clikat.R
import com.codebrew.clikat.data.model.api.AddressBean
import com.codebrew.clikat.databinding.ListSearchFooterBinding
import com.codebrew.clikat.databinding.ListSearchHeaderBinding
import com.codebrew.clikat.databinding.ListSearchItemBinding
import com.codebrew.clikat.modal.other.SettingModel
import com.codebrew.clikat.utils.configurations.Configurations
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


private const val ITEM_VIEW_TYPE_HEADER = 0
private const val ITEM_VIEW_TYPE_ITEM = 1
private const val ITEM_VIEW_TYPE_FOOTER = 2

class AddressAdapter(internal var screen_tag:String?=null,private val settingModel: SettingModel.DataBean.SettingData?, private val clickListener: AddressListener) :
        ListAdapter<DataItem, RecyclerView.ViewHolder>(SleepNightDiffCallback()) {

    private val adapterScope = CoroutineScope(Dispatchers.Default)

    fun addHeaderAndSubmitList(list: List<AddressBean>?, loginStatus: Boolean) {
        adapterScope.launch {


            val items: List<DataItem> = if (loginStatus) {
                when (list) {
                    null -> listOf(DataItem.Header) + listOf(DataItem.Footer)
                    else -> list.map { DataItem.SleepNightItem(it) }+listOf(DataItem.Header) + listOf(DataItem.Footer)
                }
            } else {
                when (list) {
                    null -> listOf(DataItem.Header)+ listOf(DataItem.Footer)
                    else ->  list.map { DataItem.SleepNightItem(it) }+listOf(DataItem.Header)
                }
            }

            withContext(Dispatchers.Main) {
                submitList(items)
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is ViewHolder -> {
                val nightItem = getItem(position) as DataItem.SleepNightItem
                holder.bind(nightItem.address, clickListener,settingModel,screen_tag)
            }

            is TextViewFooter -> {
                holder.bind(clickListener)
            }

            is TextViewHeader -> {
                holder.bind(clickListener)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            ITEM_VIEW_TYPE_ITEM -> ViewHolder.from(parent)
            ITEM_VIEW_TYPE_HEADER -> TextViewHeader.from(parent)
            ITEM_VIEW_TYPE_FOOTER -> TextViewFooter.from(parent)

            else -> throw ClassCastException("Unknown viewType $viewType")
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when (getItem(position)) {
            is DataItem.SleepNightItem -> ITEM_VIEW_TYPE_ITEM
            is DataItem.Header -> ITEM_VIEW_TYPE_HEADER
            is DataItem.Footer -> ITEM_VIEW_TYPE_FOOTER

        }
    }

    class TextViewHeader private constructor(val binding: ListSearchHeaderBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(clickListener: AddressListener) {
            binding.clickListener = clickListener
            binding.executePendingBindings()
            binding.color = Configurations.colors

            binding.textview.compoundDrawablesRelative.getOrNull(0)?.setTint(Color.parseColor(Configurations.colors.primaryColor))
        }

        companion object {
            fun from(parent: ViewGroup): TextViewHeader {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ListSearchHeaderBinding.inflate(layoutInflater, parent, false)
                return TextViewHeader(binding)
            }
        }
    }


    class TextViewFooter private constructor(val binding: ListSearchFooterBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(clickListener: AddressListener) {
            binding.clickListener = clickListener
            binding.color = Configurations.colors
            binding.executePendingBindings()
        }

        companion object {
            fun from(parent: ViewGroup): TextViewFooter {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ListSearchFooterBinding.inflate(layoutInflater, parent, false)
                return TextViewFooter(binding)
            }
        }
    }


    class ViewHolder private constructor(val binding: ListSearchItemBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: AddressBean, clickListener: AddressListener, settingModel: SettingModel.DataBean.SettingData?, screen_tag: String?) {

            if(settingModel?.show_ecom_v2_theme=="1")
            {
                binding.root.setBackgroundResource(R.drawable.et_radius_back_ground)
            }
            if(screen_tag.equals("1"))
            {
            if(item.isUnderZone==1)
            {
                binding.tvNoLocale.visibility=View.GONE
                binding.imageView4.visibility=View.VISIBLE
            }else{
                binding.tvNoLocale.visibility=View.VISIBLE
                binding.imageView4.visibility=View.VISIBLE
            }}else{
                binding.tvNoLocale.visibility=View.GONE
                binding.imageView4.visibility=View.VISIBLE
            }
            if(item.address_type.equals(""))
            {
                binding.tvCustomerAdrs.text=item.area
            }else{
                binding.tvCustomerAdrs.text=item.address_type
            }
var address=""
            if(!item.building.equals(""))
            {
               address=item.building!!
            }
            if(!item.street.equals(""))
            {
                address=address+","+item.street!!
            }
            if(!item.area.equals(""))
            {
                address=address+","+item.area!!
            }
            if(address.equals(""))
            {
                binding.tvAdrsLineFirst.text=item.customer_address
            }else{
                binding.tvAdrsLineFirst.text=address
            }
            binding.addressBean = item
            binding.clickListener = clickListener
            binding.executePendingBindings()
        }

        companion object {
            fun from(parent: ViewGroup): ViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ListSearchItemBinding.inflate(layoutInflater, parent, false)
                return ViewHolder(binding)
            }
        }
    }
}

class SleepNightDiffCallback : DiffUtil.ItemCallback<DataItem>() {
    override fun areItemsTheSame(oldItem: DataItem, newItem: DataItem): Boolean {
        return oldItem.address == newItem.address
    }

    @SuppressLint("DiffUtilEquals")
    override fun areContentsTheSame(oldItem: DataItem, newItem: DataItem): Boolean {
        return oldItem == newItem
    }
}

class AddressListener(val clickListener: (addressData: AddressBean) -> Unit,
                      val editDellistener: (view: View, addressBean: AddressBean) -> Unit, val typelistener: (mType: String) -> Unit) {

    fun onClick(model: AddressBean) = clickListener(model)

    fun onEditDelete(view: View, addressBean: AddressBean) = editDellistener(view, addressBean)

    fun onTypeClick(mType: String) = typelistener(mType)

}


sealed class DataItem {
    data class SleepNightItem(val addressBean: AddressBean) : DataItem() {
        override val address = addressBean
    }

    object Header : DataItem() {
        override val address = AddressBean()
    }

    object Footer : DataItem() {
        override val address = AddressBean()
    }

    abstract val address: AddressBean
}

