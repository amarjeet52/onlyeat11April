package com.codebrew.clikat.module.filter.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.codebrew.clikat.R
import com.codebrew.clikat.databinding.ItemSelectedSupplierBinding
import com.codebrew.clikat.modal.other.English

class SelectedCategoryAdaptor(private val dataList: ArrayList<English>) : RecyclerView.Adapter<SelectedCategoryAdaptor.ViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = DataBindingUtil.inflate<ItemSelectedSupplierBinding>(LayoutInflater.from(parent.context),
                R.layout.item_selected_supplier, parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return dataList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.tvName.text = dataList[position].name

        holder.ivCross.setOnClickListener {
            dataList.removeAt(holder.adapterPosition)
            notifyDataSetChanged()
        }

    }

    fun addCategory(english: English) {
       val existed  = dataList.any { it.id == english.id }
        if(!existed){
            dataList.add(english)
            notifyDataSetChanged()
        }
    }

    fun getSelectedCategories() = dataList

    class ViewHolder(itemView: ItemSelectedSupplierBinding) : RecyclerView.ViewHolder(itemView.root) {

        val tvName = itemView.tvName
        val ivCross = itemView.ivRateCross


    }

}