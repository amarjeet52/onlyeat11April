package com.codebrew.clikat.module.searchProduct.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.codebrew.clikat.app_utils.AppUtils
import com.codebrew.clikat.databinding.ItemAllSupplierCraveBinding
import com.codebrew.clikat.databinding.ItemHomeSupplierBinding
import com.codebrew.clikat.modal.other.SupplierDataBean
import com.codebrew.clikat.modal.other.TimeDataBean
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.lang.reflect.Type


class ResturantListAdapter(val clickListener: ResturantListener,internal var appUtils: AppUtils, var lang_code: String? = "") :
        ListAdapter<DataItem, RecyclerView.ViewHolder>(MessageListDiffCallback()) {
    public var lang_code1 = ""
    private val adapterScope = CoroutineScope(Dispatchers.Default)

    fun submitMessageList(list: List<SupplierDataBean>?) {
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

                if (nightItem.supplierData.timings?.isNotEmpty() == true) {
                    val listType: Type = object : TypeToken<ArrayList<TimeDataBean?>?>() {}.type
                    nightItem.supplierData.timing = Gson().fromJson(nightItem.supplierData.timings, listType)
                }

                nightItem.supplierData.isOpen = appUtils.checkResturntTiming(nightItem.supplierData.timing)
                this.lang_code1 = lang_code!!
                holder.bind(nightItem.supplierData, clickListener, lang_code1,appUtils)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        this.lang_code1 = lang_code!!
        return ViewHolder.from(parent)
    }
}


class ViewHolder private constructor(val binding: ItemHomeSupplierBinding) :
        RecyclerView.ViewHolder(binding.root) {

    fun bind(item: SupplierDataBean, clickListener: ResturantListener, lang_code1: String, appUtils: AppUtils) {

        if (lang_code1.equals("15")) {
            binding.tvName.text = item.name_arabic
        } else {
            binding.tvName.text = item.name
        }

        val type = object : TypeToken<ArrayList<TimeDataBean>>() {}.type
        val listOfMap: ArrayList<TimeDataBean> = Gson().fromJson(item?.timings, type)
        item.isOpen = appUtils.checkResturntTiming(listOfMap)
        binding.supplierData = item
//        if(!item!!.isOpen&&item!!.is_scheduled==0)
//        {
//            binding.ivStatus.visibility= View.VISIBLE
//        }else{
//            binding.ivStatus.visibility= View.GONE
//        }
        binding.tvRating.text=item.rating.toString()
        binding.root.setOnClickListener {
            clickListener.onClick(item)
        }

        binding.executePendingBindings()
    }

    companion object {
        fun from(parent: ViewGroup): ViewHolder {
            val layoutInflater = LayoutInflater.from(parent.context)
            val binding = ItemHomeSupplierBinding.inflate(layoutInflater, parent, false)
            return ViewHolder(binding)
        }
    }
}


class MessageListDiffCallback : DiffUtil.ItemCallback<DataItem>() {
    override fun areItemsTheSame(oldItem: DataItem, newItem: DataItem): Boolean {
        return oldItem.chatData == newItem.chatData
    }

    @SuppressLint("DiffUtilEquals")
    override fun areContentsTheSame(oldItem: DataItem, newItem: DataItem): Boolean {
        return oldItem == newItem
    }
}


class ResturantListener(val clickListener: (model: SupplierDataBean) -> Unit) {
    fun onClick(addressBean: SupplierDataBean) = clickListener(addressBean)
}

sealed class DataItem {
    data class MessageItem(val supplierData: SupplierDataBean) : DataItem() {
        override val chatData = supplierData
    }

    abstract val chatData: SupplierDataBean
}

