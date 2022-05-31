package com.codebrew.clikat.dialog_flow.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.codebrew.clikat.data.model.api.AddressBean
import com.codebrew.clikat.data.model.others.RentalDayModel
import com.codebrew.clikat.databinding.DgflowItemAddressBinding
import com.codebrew.clikat.databinding.ItemAvailDateBinding
import com.codebrew.clikat.utils.configurations.Configurations
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class DialogAddressAdapter : ListAdapter<AddressItem, RecyclerView.ViewHolder>(AdrsListDiffCallback()) {

    private val adapterScope = CoroutineScope(Dispatchers.Default)

    private lateinit var mCallback: AddressListener

    fun settingCallback(mCallback: AddressListener) {
        this.mCallback = mCallback
    }

    fun submitItemList(list: List<AddressBean>?) {
        adapterScope.launch {
            val items = list?.map { AddressItem.AddonItem(it) }

            withContext(Dispatchers.Main) {
                submitList(items)
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is AddressViewHolder -> {
                val modelItem = getItem(position) as AddressItem.AddonItem
                holder.bind(modelItem.mDayData, mCallback)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return AddressViewHolder.from(parent)
    }
}


class AddressViewHolder private constructor(val binding: DgflowItemAddressBinding) :
        RecyclerView.ViewHolder(binding.root) {

    fun bind(item: AddressBean, listener: AddressListener) {

        binding.addressItem = item
        binding.listener = listener
        binding.color = Configurations.colors

        binding.executePendingBindings()
    }

    companion object {
        fun from(parent: ViewGroup): AddressViewHolder {
            val layoutInflater = LayoutInflater.from(parent.context)
            val binding = DgflowItemAddressBinding.inflate(layoutInflater, parent, false)
            return AddressViewHolder(binding)
        }
    }
}


class AdrsListDiffCallback : DiffUtil.ItemCallback<AddressItem>() {
    override fun areItemsTheSame(oldItem: AddressItem, newItem: AddressItem): Boolean {
        return oldItem.dayData == newItem.dayData
    }

    @SuppressLint("DiffUtilEquals")
    override fun areContentsTheSame(oldItem: AddressItem, newItem: AddressItem): Boolean {
        return oldItem.dayData.id == newItem.dayData.id
    }
}

class AddressListener(val clickListener: (rentalModel: AddressBean) -> Unit) {
    fun adrslick(address: AddressBean) = clickListener(address)
}


sealed class AddressItem {
    data class AddonItem(val mDayData: AddressBean) : AddressItem() {
        override val dayData = mDayData
    }

    abstract val dayData: AddressBean
}

