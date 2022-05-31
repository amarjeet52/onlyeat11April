package com.codebrew.clikat.module.referral_list.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.codebrew.clikat.data.constants.AppConstants
import com.codebrew.clikat.data.model.api.ReferalData
import com.codebrew.clikat.databinding.ItemReferredUserBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class ReferrListAdapter :
        ListAdapter<DataItem, RecyclerView.ViewHolder>(MessageListDiffCallback()) {

    private val adapterScope = CoroutineScope(Dispatchers.Default)

    fun submitMessageList(list: List<ReferalData>?) {
        adapterScope.launch {
            val items = list?.map { DataItem.MessageItem(it) }

            withContext(Dispatchers.Main) {
                submitList(items)
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is ViewHolder -> {
                val nightItem = getItem(position) as DataItem.MessageItem
                holder.bind(nightItem.messageData)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return ViewHolder.from(parent)
    }
}


class ViewHolder private constructor(val binding: ItemReferredUserBinding) :
        RecyclerView.ViewHolder(binding.root) {

    fun bind(item: ReferalData) {
        binding.item = item
        binding.currency = AppConstants.CURRENCY_SYMBOL
        binding.executePendingBindings()
    }

    companion object {
        fun from(parent: ViewGroup): ViewHolder {
            val layoutInflater = LayoutInflater.from(parent.context)
            val binding = ItemReferredUserBinding.inflate(layoutInflater, parent, false)
            return ViewHolder(binding)
        }
    }
}


class MessageListDiffCallback : DiffUtil.ItemCallback<DataItem>() {
    override fun areItemsTheSame(oldItem: DataItem, newItem: DataItem): Boolean {
        return oldItem.referralData == newItem.referralData
    }

    @SuppressLint("DiffUtilEquals")
    override fun areContentsTheSame(oldItem: DataItem, newItem: DataItem): Boolean {
        return oldItem == newItem
    }
}


sealed class DataItem {
    data class MessageItem(val messageData: ReferalData) : DataItem() {
        override val referralData = messageData
    }

    abstract val referralData: ReferalData
}

