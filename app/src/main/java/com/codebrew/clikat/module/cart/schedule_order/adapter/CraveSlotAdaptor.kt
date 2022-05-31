package com.codebrew.clikat.module.cart.schedule_order.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.RadioButton
import androidx.appcompat.widget.AppCompatTextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.codebrew.clikat.R
import com.codebrew.clikat.databinding.ItemCraveSlotBinding

class CraveSlotAdaptor(private val dataList: ArrayList<String>) : RecyclerView.Adapter<CraveSlotAdaptor.ViewHolder>() {

    private var selectedPos = 0


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = DataBindingUtil.inflate<ItemCraveSlotBinding>(LayoutInflater.from(parent.context),
                R.layout.item_crave_slot, parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return dataList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        holder.tvName.text = dataList[position]

        holder.rbSelect.isChecked = selectedPos == position

        holder.tvName.setOnClickListener {
            selectedPos = position
            notifyDataSetChanged()
        }
        holder.rbSelect.setOnClickListener {
            selectedPos = position
            notifyDataSetChanged()
        }

    }

    fun getSelectedPosition() = selectedPos

    class ViewHolder(itemView: ItemCraveSlotBinding) : RecyclerView.ViewHolder(itemView.root) {
var cl_main:ConstraintLayout=itemView.clMain
        val tvName: AppCompatTextView = itemView.tvSlot
        val rbSelect: RadioButton = itemView.rbSelect

    }

}