package com.codebrew.clikat.module.cart.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.codebrew.clikat.databinding.ItemVarientCartBinding
import com.codebrew.clikat.modal.other.VariantValuesBean
import com.codebrew.clikat.utils.configurations.Configurations
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers


class VarientItemAdapter : ListAdapter<VarientItem, RecyclerView.ViewHolder>(ItemListDiffCallback()) {

    private val adapterScope = CoroutineScope(Dispatchers.Default)


    fun submitItemList(list: MutableList<VariantValuesBean?>?) {
        //  adapterScope.launch {
        val items = list?.map { VarientItem.AddonItem(it) }

        //  withContext(Dispatchers.Main) {
        submitList(items)
        //  }
        //  }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is VarientViewHolder -> {
                val nightItem = getItem(position) as VarientItem.AddonItem
                holder.bind(nightItem.varientData)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return VarientViewHolder.from(parent)
    }
}


class VarientViewHolder private constructor(val binding: ItemVarientCartBinding) :
        RecyclerView.ViewHolder(binding.root) {

    fun bind(item: VariantValuesBean?) {
        binding.color = Configurations.colors
        binding.varientBean = item
        binding.executePendingBindings()
    }

    companion object {
        fun from(parent: ViewGroup): VarientViewHolder {
            val layoutInflater = LayoutInflater.from(parent.context)
            val binding = ItemVarientCartBinding.inflate(layoutInflater, parent, false)
            return VarientViewHolder(binding)
        }
    }
}


class ItemListDiffCallback : DiffUtil.ItemCallback<VarientItem>() {
    override fun areItemsTheSame(oldItem: VarientItem, newItem: VarientItem): Boolean {
        return oldItem.varientData == newItem.varientData
    }

    @SuppressLint("DiffUtilEquals")
    override fun areContentsTheSame(oldItem: VarientItem, newItem: VarientItem): Boolean {
        return oldItem.varientData?.product_id == newItem.varientData?.product_id
    }
}


sealed class VarientItem {
    data class AddonItem(val mVarientData: VariantValuesBean?) : VarientItem() {
        override val varientData = mVarientData
    }

    abstract val varientData: VariantValuesBean?
}

