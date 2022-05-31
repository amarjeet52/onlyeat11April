package com.codebrew.clikat.module.rental

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.codebrew.clikat.data.model.others.RentalDayModel
import com.codebrew.clikat.databinding.ItemAvailDateBinding
import com.codebrew.clikat.utils.configurations.Configurations
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class DaysAdapter : ListAdapter<DayItem, RecyclerView.ViewHolder>(ItemListDiffCallback()) {

    private val adapterScope = CoroutineScope(Dispatchers.Default)

    private lateinit var mCallback: ItemListener

    fun settingCallback(mCallback: ItemListener) {
        this.mCallback = mCallback
    }

    fun submitItemList(list: List<RentalDayModel>?) {
        adapterScope.launch {
            val items = list?.map { DayItem.AddonItem(it) }

            withContext(Dispatchers.Main) {
                submitList(items)
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is DayViewHolder -> {
                val modelItem = getItem(position) as DayItem.AddonItem
                holder.bind(modelItem.mDayData, mCallback)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return DayViewHolder.from(parent)
    }
}


class DayViewHolder private constructor(val binding: ItemAvailDateBinding) :
        RecyclerView.ViewHolder(binding.root) {

    fun bind(item: RentalDayModel, listener: ItemListener) {

        binding.dayBean = item
        binding.clickListener = listener
        binding.color = Configurations.colors

        binding.executePendingBindings()
    }

    companion object {
        fun from(parent: ViewGroup): DayViewHolder {
            val layoutInflater = LayoutInflater.from(parent.context)
            val binding = ItemAvailDateBinding.inflate(layoutInflater, parent, false)
            return DayViewHolder(binding)
        }
    }
}


class ItemListDiffCallback : DiffUtil.ItemCallback<DayItem>() {
    override fun areItemsTheSame(oldItem: DayItem, newItem: DayItem): Boolean {
        return oldItem.dayData == newItem.dayData
    }

    @SuppressLint("DiffUtilEquals")
    override fun areContentsTheSame(oldItem: DayItem, newItem: DayItem): Boolean {
        return oldItem.dayData.dayName == newItem.dayData.dayName
    }
}

class ItemListener(val clickListener: (rentalModel: RentalDayModel) -> Unit) {
    fun dayClick(rental: RentalDayModel) = clickListener(rental)
}


sealed class DayItem {
    data class AddonItem(val mDayData: RentalDayModel) : DayItem() {
        override val dayData = mDayData
    }

    abstract val dayData: RentalDayModel
}

