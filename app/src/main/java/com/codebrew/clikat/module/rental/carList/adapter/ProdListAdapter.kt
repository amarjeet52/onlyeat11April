package com.codebrew.clikat.module.rental.carList.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.codebrew.clikat.databinding.ItemProductRentalBinding
import com.codebrew.clikat.modal.other.ProductDataBean
import com.codebrew.clikat.utils.configurations.Configurations
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ProdListAdapter : ListAdapter<ProductItem, RecyclerView.ViewHolder>(ItemListDiffCallback()) {

    private val adapterScope = CoroutineScope(Dispatchers.Default)

    private lateinit var mCallback: ProdListener

    fun settingCallback(mCallback: ProdListener) {
        this.mCallback = mCallback
    }

    fun submitItemList(list: List<ProductDataBean>?) {
        adapterScope.launch {
            val items = list?.map { ProductItem.ProductDataItem(it) }

            withContext(Dispatchers.Main) {
                submitList(items)
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is DayViewHolder -> {
                val modelItem = getItem(position) as ProductItem.ProductDataItem
                holder.bind(modelItem.mProdData, mCallback)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return DayViewHolder.from(parent)
    }
}


class DayViewHolder private constructor(val binding: ItemProductRentalBinding) :
        RecyclerView.ViewHolder(binding.root) {

    fun bind(item: ProductDataBean, listener: ProdListener) {
        binding.color = Configurations.colors
        binding.model = item
        binding.prodlistener = listener
        binding.executePendingBindings()
    }

    companion object {
        fun from(parent: ViewGroup): DayViewHolder {
            val layoutInflater = LayoutInflater.from(parent.context)
            val binding = ItemProductRentalBinding.inflate(layoutInflater, parent, false)
            return DayViewHolder(binding)
        }
    }
}


class ItemListDiffCallback : DiffUtil.ItemCallback<ProductItem>() {
    override fun areItemsTheSame(oldItem: ProductItem, newItem: ProductItem): Boolean {
        return oldItem.dayData == newItem.dayData
    }

    @SuppressLint("DiffUtilEquals")
    override fun areContentsTheSame(oldItem: ProductItem, newItem: ProductItem): Boolean {
        return oldItem.dayData == newItem.dayData
    }
}

class ProdListener(val clickListener: (rentalModel: ProductDataBean) -> Unit) {
    fun productClick(rental: ProductDataBean) = clickListener(rental)
}


sealed class ProductItem {
    data class ProductDataItem(val mProdData: ProductDataBean) : ProductItem() {
        override val dayData = mProdData
    }

    abstract val dayData: ProductDataBean
}

