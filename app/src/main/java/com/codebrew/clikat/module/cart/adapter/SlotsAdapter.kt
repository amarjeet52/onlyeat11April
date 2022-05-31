package com.codebrew.clikat.module.cart.adapter

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.codebrew.clikat.R
import com.codebrew.clikat.module.cart.SlotModel


class SlotsAdapter(internal var activity: Activity, internal var Slot_list:List<String>, internal var mListener: Callback?)
        : RecyclerView.Adapter<SlotsAdapter.ViewHolder>() {
var selectedPosition=0
    override fun getItemViewType(position: Int): Int {
        return position
    }


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.tv_item.setText(Slot_list.get(position))
        if(selectedPosition==position)
        {
            holder.tv_item.setTextColor(ContextCompat.getColor(activity,R.color.white))
            holder.tv_item.setBackgroundResource(R.drawable.back_blue)
        }else{
            holder.tv_item.setTextColor(ContextCompat.getColor(activity,R.color.black))
            holder.tv_item.setBackgroundResource(R.drawable.back_grey1)
        }

        holder.tv_item.setOnClickListener {
            selectedPosition=holder.adapterPosition
            mListener?.onItemClicked(Slot_list.get(position))
notifyDataSetChanged()

        }
    }



    override fun getItemCount(): Int {
        return Slot_list.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.getContext())
            .inflate(R.layout.layout_book_tablerecycler_item, parent, false)

        return ViewHolder(v)
    }

class ViewHolder(viewitem: View) : RecyclerView.ViewHolder(viewitem) {

    var tv_item:TextView

    init {

        tv_item = viewitem.findViewById(R.id.tv_item1)


    }
}


interface Callback {
    fun onItemClicked( time:String)

}
}


