package com.codebrew.clikat.module.filter.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.codebrew.clikat.R
import com.codebrew.clikat.databinding.ItemSortbyCraveBinding
import com.codebrew.clikat.module.searchProduct.CraveSortByModel

class CraveFilterSortAdaptor(private val dataList: ArrayList<CraveSortByModel>) : RecyclerView.Adapter<CraveFilterSortAdaptor.ViewHolder>() {

    private var selectedPos = -1


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = DataBindingUtil.inflate<ItemSortbyCraveBinding>(LayoutInflater.from(parent.context),
                R.layout.item_sortby_crave, parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return dataList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.tvName.text = dataList[position].name

        if (selectedPos == position) {
            holder.tvName.isSelected = true
            holder.ivCross.visibility = View.VISIBLE
        } else {
            holder.tvName.isSelected = false
            holder.ivCross.visibility = View.GONE
        }

        holder.tvName.setOnClickListener {
            selectedPos = position
            notifyDataSetChanged()
        }

    }


    class ViewHolder(itemView: ItemSortbyCraveBinding) : RecyclerView.ViewHolder(itemView.root) {

        val tvName = itemView.tvName
        val ivCross = itemView.ivCross

    }

}