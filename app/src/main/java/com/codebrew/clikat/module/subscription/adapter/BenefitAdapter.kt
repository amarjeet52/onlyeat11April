package com.codebrew.clikat.module.subscription.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.codebrew.clikat.data.model.api.Benefit
import com.codebrew.clikat.databinding.ItemBenefitSubcriptionBinding
import com.codebrew.clikat.utils.configurations.Configurations
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class BenefitAdapter : ListAdapter<BenefitItem, RecyclerView.ViewHolder>(ItemListDiffCallback()) {

    private val adapterScope = CoroutineScope(Dispatchers.Default)


    fun submitItemList(list: MutableList<Benefit>?) {
        adapterScope.launch {
            val items = list?.map { BenefitItem.AddonItem(it) }

            withContext(Dispatchers.Main) {
                submitList(items)
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is VarientViewHolder -> {
                val nightItem = getItem(position) as BenefitItem.AddonItem
                holder.bind(nightItem.varientData)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return VarientViewHolder.from(parent)
    }
}


class VarientViewHolder private constructor(val binding: ItemBenefitSubcriptionBinding) :
        RecyclerView.ViewHolder(binding.root) {

    fun bind(item: Benefit?) {
        binding.color = Configurations.colors
        binding.benefitBean =item
        binding.executePendingBindings()
    }

    companion object {
        fun from(parent: ViewGroup): VarientViewHolder {
            val layoutInflater = LayoutInflater.from(parent.context)
            val binding = ItemBenefitSubcriptionBinding.inflate(layoutInflater, parent, false)
            return VarientViewHolder(binding)
        }
    }
}


class ItemListDiffCallback : DiffUtil.ItemCallback<BenefitItem>() {
    override fun areItemsTheSame(oldItem: BenefitItem, newItem: BenefitItem): Boolean {
        return oldItem.varientData == newItem.varientData
    }

    @SuppressLint("DiffUtilEquals")
    override fun areContentsTheSame(oldItem: BenefitItem, newItem: BenefitItem): Boolean {
        return oldItem.varientData?.title == newItem.varientData?.title
    }
}


sealed class BenefitItem {
    data class AddonItem(val mVarientData: Benefit?) : BenefitItem() {
        override val varientData = mVarientData
    }

    abstract val varientData: Benefit?
}

