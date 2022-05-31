package com.codebrew.clikat.module.cart.schedule_order.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.codebrew.clikat.data.model.others.ScheduleItemList
import com.codebrew.clikat.databinding.ItemScheduleSlotBinding
import com.codebrew.clikat.utils.configurations.Configurations
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class SlotAdapter(private val mCallback: ItemListener) : ListAdapter<SlotItem, RecyclerView.ViewHolder>(ItemListDiffCallback()) {

    private val adapterScope = CoroutineScope(Dispatchers.Default)


    fun submitItemList(list: List<ScheduleItemList>?) {
        adapterScope.launch {
            val items = list?.map { SlotItem.AddonItem(it) }

            withContext(Dispatchers.Main) {
                submitList(items)
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is SavedAddOnViewHolder -> {
                val nightItem = getItem(position) as SlotItem.AddonItem
                holder.bind(nightItem.mAddonData, mCallback,position)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return  SavedAddOnViewHolder.from(parent)
        }
    }


    class SavedAddOnViewHolder private constructor(val binding: ItemScheduleSlotBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: ScheduleItemList, clickListener: ItemListener, position: Int) {
            binding.listener=clickListener
            binding.itemData=item
            binding.position=position
            binding.color=Configurations.colors

            binding.executePendingBindings()
        }

        companion object {
            fun from(parent: ViewGroup): SavedAddOnViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ItemScheduleSlotBinding.inflate(layoutInflater, parent, false)
                return SavedAddOnViewHolder(binding)
            }
        }
    }


class ItemListDiffCallback : DiffUtil.ItemCallback<SlotItem>() {
    override fun areItemsTheSame(oldItem: SlotItem, newItem: SlotItem): Boolean {
        return oldItem.addonData == newItem.addonData
    }

    @SuppressLint("DiffUtilEquals")
    override fun areContentsTheSame(oldItem: SlotItem, newItem: SlotItem): Boolean {
        return oldItem.addonData.isStatus == newItem.addonData.isStatus
    }
}

class ItemListener(val clickListener: (model: ScheduleItemList,position:Int) -> Unit) {
    fun slotItem(slotBean: ScheduleItemList,pos:Int) = clickListener(slotBean,pos)
}


sealed class SlotItem {
    data class AddonItem(val mAddonData: ScheduleItemList) : SlotItem() {
        override val addonData = mAddonData
    }

    abstract val addonData: ScheduleItemList
}

