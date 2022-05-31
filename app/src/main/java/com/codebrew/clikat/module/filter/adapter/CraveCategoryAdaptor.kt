package com.codebrew.clikat.module.filter.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.codebrew.clikat.R
import com.codebrew.clikat.databinding.ItemFilterSupplierBinding
import com.codebrew.clikat.modal.other.English

class CraveCategoryAdaptor(private val dataList: ArrayList<English>) : RecyclerView.Adapter<CraveCategoryAdaptor.ViewHolder>() {

    private var selectedPos = -1
    private var categorySelectListener: CategorySelectListener? = null


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = DataBindingUtil.inflate<ItemFilterSupplierBinding>(LayoutInflater.from(parent.context),
                R.layout.item_filter_supplier, parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return dataList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.tvName.text = dataList[position].name

        if (selectedPos == position) {
            holder.ivCheck.visibility = View.VISIBLE
        } else {
            holder.ivCheck.visibility = View.GONE
        }

        holder.parentPanel.setOnClickListener {
            selectedPos = position
            categorySelectListener?.onCategorySelected(dataList[position])
            notifyDataSetChanged()
        }

    }

    fun settingCallback(categorySelectListener: CategorySelectListener) {
        this.categorySelectListener = categorySelectListener
    }

    interface CategorySelectListener {
        fun onCategorySelected(english: English)
    }

    class ViewHolder(itemView: ItemFilterSupplierBinding) : RecyclerView.ViewHolder(itemView.root) {

        val tvName = itemView.tvName
        val ivCheck = itemView.ivCheck
        val parentPanel = itemView.parentPanel

    }

}