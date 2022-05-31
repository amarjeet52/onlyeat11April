package com.codebrew.clikat.module.wishlist_prod.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView
import com.codebrew.clikat.databinding.ItemHomeSupplierBinding
import com.codebrew.clikat.databinding.ItemSupplierVerticalBinding
import com.codebrew.clikat.modal.other.SettingModel
import com.codebrew.clikat.modal.other.SupplierDataBean
import com.codebrew.clikat.utils.configurations.Configurations


class SuppliersWishListAdapter(val clickListener: OnItemClicked, val clientInform: SettingModel.DataBean.SettingData?) : RecyclerView.Adapter<SuppliersWishListAdapter.ViewHolder>() {

    private var suppliersList = ArrayList<SupplierDataBean>()


    override fun getItemCount(): Int {
        return suppliersList.size
    }


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(suppliersList[position])
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return if (viewType != SKIP_THEME) {
            val binding = ItemHomeSupplierBinding.inflate(layoutInflater, parent, false)
            binding.color = Configurations.colors
            binding.drawables = Configurations.drawables
            binding.strings = Configurations.strings
            ViewHolder(binding)
        } else {
            val binding = ItemSupplierVerticalBinding.inflate(layoutInflater, parent, false)
            binding.color = Configurations.colors
            ViewHolder(binding)
        }

    }


    inner class ViewHolder(val binding: ViewDataBinding) : RecyclerView.ViewHolder(binding.root) {

        init {
            itemView.setOnClickListener {
                clickListener.onItemClicked(suppliersList[adapterPosition])
            }
        }

        fun bind(item: SupplierDataBean) {
            when (binding) {
                is ItemHomeSupplierBinding -> {
                    binding.supplierData = item
                    // binding.suppplierListener = clickListener
                    binding.ivWishlist.visibility = View.VISIBLE
                    binding.executePendingBindings()

                    binding.ivWishlist.setOnClickListener {
                        clickListener.removeProduct(suppliersList[adapterPosition], adapterPosition)
                    }
                }
                is ItemSupplierVerticalBinding -> {
                    binding.supplierData = item
                    binding.executePendingBindings()

                    binding.ivWishlist.setOnClickListener {
                        clickListener.removeProduct(suppliersList[adapterPosition], adapterPosition)
                    }
                }
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (clientInform?.is_skip_theme == "1")
            SKIP_THEME
        else SUPPLIERS_LIST

    }

    fun addList(list: MutableList<SupplierDataBean>) {
        suppliersList.clear()
        suppliersList.addAll(list)
        notifyDataSetChanged()
    }

    fun removedProduct(position: Int?) {
        if (position != null) {
            suppliersList.removeAt(position)
            notifyItemRemoved(position)
        }
    }

    fun getList(): MutableList<SupplierDataBean>? {
        return suppliersList
    }

    companion object {
        const val SKIP_THEME = 1
        const val SUPPLIERS_LIST = 2
    }
}


interface OnItemClicked {
    fun onItemClicked(dataItem: SupplierDataBean)
    fun removeProduct(dataItem: SupplierDataBean, position: Int)
}

