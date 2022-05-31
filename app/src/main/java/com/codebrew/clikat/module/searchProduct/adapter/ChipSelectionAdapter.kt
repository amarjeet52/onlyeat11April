package com.codebrew.clikat.module.searchProduct.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.codebrew.clikat.R
import com.codebrew.clikat.databinding.ItemviewChipSelectedBinding
import com.codebrew.clikat.modal.other.English
import com.codebrew.clikat.module.searchProduct.ListManagerInterface

class ChipSelectionAdapter(var mContext: Context, var categoryList: MutableList<English>,var clickInterface: ListManagerInterface) : RecyclerView.Adapter<ChipSelectionAdapter.ViewHolder>(){


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemviewChipSelectedBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return categoryList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.onBind(categoryList[position])
    }

   inner class ViewHolder(var binding : ItemviewChipSelectedBinding) : RecyclerView.ViewHolder(binding.root) {
        fun onBind(english: English) {
            binding.category = english
            binding.chipType.isSelected = true
            binding.chipType.isCloseIconVisible = true
            binding.chipType.chipBackgroundColor = mContext.resources.getColorStateList(R.color.chip_selected)
            binding.chipType.setOnCloseIconClickListener {
                clickInterface.onLayoutClick(english)
            } }
    }

}