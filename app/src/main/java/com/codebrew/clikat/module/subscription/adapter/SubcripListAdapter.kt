package com.codebrew.clikat.module.subscription.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.codebrew.clikat.data.constants.AppConstants
import com.codebrew.clikat.data.model.api.SubcripModel
import com.codebrew.clikat.databinding.ItemSubcriptonBinding
import com.codebrew.clikat.modal.other.SettingModel
import com.codebrew.clikat.utils.configurations.Configurations
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class SubcripListAdapter(private val clientInform: SettingModel.DataBean.SettingData?) : ListAdapter<SubscipItem, RecyclerView.ViewHolder>(SubscripListDiffCallback()) {

    private val adapterScope = CoroutineScope(Dispatchers.Default)

    private lateinit var mCallback: SubscripListener

    fun settingCallback(mCallback: SubscripListener) {
        this.mCallback = mCallback
    }


    fun submitItemList(list: MutableList<SubcripModel>?) {
        adapterScope.launch {
            val items = list?.map { SubscipItem.AddonItem(it) }

            withContext(Dispatchers.Main) {
                submitList(items)
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is SubcripViewHolder -> {
                val nightItem = getItem(position) as SubscipItem.AddonItem
                holder.bind(nightItem.subcripData, position, mCallback)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return SubcripViewHolder.from(parent,clientInform)
    }
}


class SubcripViewHolder private constructor(val binding: ItemSubcriptonBinding, val clientInform: SettingModel.DataBean.SettingData?) :
        RecyclerView.ViewHolder(binding.root) {

    fun bind(item: SubcripModel?, position: Int, mCallback: SubscripListener) {
        binding.color = Configurations.colors
        binding.listener = mCallback
        binding.subcripBean = item
        binding.position = position
        binding.currency = AppConstants.CURRENCY_SYMBOL
        binding.showTime = clientInform?.full_view_supplier_theme == "1"
        binding.executePendingBindings()
    }

    companion object {
        fun from(parent: ViewGroup, clientInform: SettingModel.DataBean.SettingData?): SubcripViewHolder {
            val layoutInflater = LayoutInflater.from(parent.context)
            val binding = ItemSubcriptonBinding.inflate(layoutInflater, parent, false)
            return SubcripViewHolder(binding,clientInform)
        }
    }
}


class SubscripListDiffCallback : DiffUtil.ItemCallback<SubscipItem>() {
    override fun areItemsTheSame(oldItem: SubscipItem, newItem: SubscipItem): Boolean {
        return oldItem.subcripData?.benefitStatus == newItem.subcripData?.benefitStatus
    }

    @SuppressLint("DiffUtilEquals")
    override fun areContentsTheSame(oldItem: SubscipItem, newItem: SubscipItem): Boolean {
        return oldItem.subcripData?.benefitStatus == newItem.subcripData?.benefitStatus
    }
}

class SubscripListener(val clickListener: (model: SubcripModel, pos: Int) -> Unit) {
    fun subscripListener(item: SubcripModel, position: Int) = clickListener(item, position)
}


sealed class SubscipItem {
    data class AddonItem(val mSubcripData: SubcripModel?) : SubscipItem() {
        override val subcripData = mSubcripData
    }

    abstract val subcripData: SubcripModel?
}

