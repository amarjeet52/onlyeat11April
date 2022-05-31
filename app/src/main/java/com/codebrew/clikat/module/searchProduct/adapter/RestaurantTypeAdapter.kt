package com.codebrew.clikat.module.searchProduct.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.codebrew.clikat.databinding.ItemviewTypeCraveBinding
import com.codebrew.clikat.modal.other.English
import com.codebrew.clikat.module.searchProduct.ListManagerInterface

class RestaurantTypeAdapter(var mContext: Context, var categoryList: MutableList<English>,var clickInterface: ListManagerInterface) : RecyclerView.Adapter<RestaurantTypeAdapter.ViewHolder>(){



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemviewTypeCraveBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)

    }

    override fun getItemCount(): Int {
        return categoryList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.onBind(categoryList[position])



    }

    inner class ViewHolder(var binding : ItemviewTypeCraveBinding) : RecyclerView.ViewHolder(binding.root) {
       fun onBind(english: English) {
           binding.category = english

           if(english.isSelected){
               binding.ivCheck.visibility = View.VISIBLE
           }
           else{
               binding.ivCheck.visibility = View.GONE
           }

           binding.clCategory.setOnClickListener {
               if(english.isSelected){
                   english.isSelected = false
                   binding.ivCheck.visibility = View.GONE
               }
               else{
                   english.isSelected = true
                   binding.ivCheck.visibility = View.VISIBLE
               }
               clickInterface.onLayoutClick(english)
           }
       }

    }

}