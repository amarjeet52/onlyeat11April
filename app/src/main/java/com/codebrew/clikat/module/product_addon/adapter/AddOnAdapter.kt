package com.codebrew.clikat.module.product_addon.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.codebrew.clikat.R
import com.codebrew.clikat.data.model.api.AddsOn
import com.codebrew.clikat.data.model.api.Value
import com.codebrew.clikat.databinding.ItemProdAddonBinding
import com.codebrew.clikat.modal.other.SettingModel
import es.dmoral.toasty.Toasty
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class AddOnAdapter(private val settingBean: SettingModel.DataBean.SettingData?) : ListAdapter<DataItem, RecyclerView.ViewHolder>(AddonListDiffCallback()) {

    private val adapterScope = CoroutineScope(Dispatchers.Default)

    private var mContext: Context? = null

    fun submitAddonList(list: List<AddsOn?>?) {
        adapterScope.launch {
            val items = list?.map { DataItem.AddonItem(it) }

            withContext(Dispatchers.Main) {
                submitList(items)
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

        mContext = holder.itemView.context

        when (holder) {
            is ViewHolder -> {
                val nightItem = getItem(position) as DataItem.AddonItem
                holder.bind(mContext, nightItem.mAddonData, currentList,settingBean)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return ViewHolder.from(parent)
    }
}


class ViewHolder private constructor(val binding: ItemProdAddonBinding) :
        RecyclerView.ViewHolder(binding.root) {

    fun bind(mContext: Context?, item: AddsOn?, currentList: MutableList<DataItem>, settingBean: SettingModel.DataBean.SettingData?) {
        binding.addonData = item

        val adapter = AddOnItemAdapter(settingBean,ItemListener({


            val filterList = currentList.filter { it1 -> it.name == it1.addonData?.name }

            if (it.is_multiple == "0") {

                if (filterList.isNotEmpty()) {
                    filterList[0].addonData?.value?.mapIndexed { index, value ->

                        if (value.status == true) {
                            value.status = false
                        }
                    }
                }

                it.status = true

                binding.rvChildAddon.adapter?.notifyDataSetChanged()
            } else {
                if ((it.max_adds_on ?: 0 > 0 && it.status == false &&
                                filterList[0].addonData?.value?.filter { it1 -> it1.status == true }?.sumBy { it.selectedQuantity?:0 }
                                        ?.minus(filterList[0].addonData?.value?.filter { it1 -> it1.is_default == "1" }?.count()
                                                ?: 0)
                                ?: 0 >= filterList[0].addonData?.value?.firstOrNull()?.max_adds_on ?: 0) ||
                        filterList[0].addonData?.value?.filter { it.status == true }?.count()?.minus(filterList[0].addonData?.value?.filter { it1 -> it1.is_default == "1" }?.count()
                                ?: 0) ?: 0 >= filterList[0].addonData?.addon_limit ?: 0 &&
                        it.status == false && filterList[0].addonData?.addon_limit ?: 0 > 0) {
                    mContext?.let { it1 -> Toasty.error(it1, "Addon Limit exceed").show() }
                    //   it.status=false
                    binding.rvChildAddon.adapter?.notifyDataSetChanged()
                } else {
                    it.status = it.status != true
                }
            }
            binding.rvChildAddon.adapter?.notifyDataSetChanged()

        }, {

            val addonDta = currentList.get(adapterPosition).addonData

            if(addonDta?.value?.firstOrNull()?.max_adds_on ?: 0 > 0 &&
                    addonDta?.value?.filter { it.status == true }?.sumBy {
                        it.selectedQuantity ?: 0
                    }?.minus(addonDta.value.count { it.is_default == "1" }) ?: 0>=
                    addonDta?.value?.firstOrNull()?.max_adds_on ?: 0)
            {
                it.selectedQuantity = it.selectedQuantity
            }else
            {
                it.selectedQuantity  =it.selectedQuantity?.plus(1)
            }
            binding.rvChildAddon.adapter?.notifyDataSetChanged()
        }, {
            if ((it.selectedQuantity ?: 1) > 1) {
                it.selectedQuantity = it.selectedQuantity?.minus(1)
            } else {
                it.selectedQuantity = it.selectedQuantity
            }
            binding.rvChildAddon.adapter?.notifyDataSetChanged()
        }))

//        binding.selectAddon.text = mContext?.getString(R.string.addon_selction, if (item?.value?.any { it.is_multiple == "0" } == true) "single" else "multiple")
        binding.selectAddon.text = "${mContext?.getString(R.string.addon_selction, if (item?.value?.any { it.is_multiple == "0" } == true) mContext.getString(R.string.text_single) else mContext.getString(R.string.text_multiple))} ${if (item?.value?.any { it.is_multiple == "0" } == true) "" else "(${mContext?.getString(R.string.minm)} ${item?.value?.get(0)?.min_adds_on} ${mContext?.getString(R.string.max)} ${item?.value?.get(0)?.max_adds_on ?: item?.value?.get(0)?.addon_limit})"}"

        binding.rvChildAddon.adapter = adapter
        adapter.submitItemList(item?.value)

        binding.executePendingBindings()
    }

    companion object {
        fun from(parent: ViewGroup): ViewHolder {
            val layoutInflater = LayoutInflater.from(parent.context)
            val binding = ItemProdAddonBinding.inflate(layoutInflater, parent, false)
            return ViewHolder(binding)
        }
    }
}


class AddonListDiffCallback : DiffUtil.ItemCallback<DataItem>() {
    override fun areItemsTheSame(oldItem: DataItem, newItem: DataItem): Boolean {
        return oldItem.addonData == newItem.addonData
    }

    @SuppressLint("DiffUtilEquals")
    override fun areContentsTheSame(oldItem: DataItem, newItem: DataItem): Boolean {
        return oldItem == newItem
    }
}

class ItemListener(val clickListener: (model: Value) -> Unit, val clickListenerInc: (model: Value) -> Unit,
                   val clickListenerDec: (model: Value) -> Unit) {
    fun onClick(addonBean: Value) = clickListener(addonBean)

    fun onClickInc(addonBean: Value) = clickListenerInc(addonBean)

    fun onClickDec(addonBean: Value) = clickListenerDec(addonBean)
}


sealed class DataItem {
    data class AddonItem(val mAddonData: AddsOn?) : DataItem() {
        override val addonData = mAddonData
    }

    abstract val addonData: AddsOn?
}

