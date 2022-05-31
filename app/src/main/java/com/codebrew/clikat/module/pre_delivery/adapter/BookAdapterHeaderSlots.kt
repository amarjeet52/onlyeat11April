package com.codebrew.clikat.module.pre_delivery.adapter

import android.annotation.SuppressLint
import android.app.Activity
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.beloo.widget.chipslayoutmanager.ChipsLayoutManager
import com.codebrew.clikat.R
import com.codebrew.clikat.modal.AppGlobal
import com.codebrew.clikat.module.pre_delivery.Model.ListItem
import com.squareup.picasso.Picasso

class BookAdapterHeaderSlots (internal var context: Activity, internal var header_list: ArrayList<ListItem>) :  RecyclerView.Adapter<BookAdapterHeaderSlots.ViewHolder>() {
var list_item:ArrayList<ListItem> = ArrayList()
    lateinit  var bookAdapterItems:BookAdapterItems
    override fun onCreateViewHolder(viewgroup: ViewGroup, viewType: Int): ViewHolder {

        val v = LayoutInflater.from(viewgroup.getContext())
                .inflate(R.layout.layout_book_table_item, viewgroup, false)

        return ViewHolder(v)
    }

    @SuppressLint("WrongConstant")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
      Picasso.get().load(header_list.get(position).image).into(holder.iv_image)
        holder.tv_item.setText(header_list.get(position).header_name)
        val chipsLayoutManager = ChipsLayoutManager.newBuilder(AppGlobal.context)
                .setChildGravity(Gravity.NO_GRAVITY)
                .setScrollingEnabled(true)
                .setGravityResolver { Gravity.NO_GRAVITY }
                .setOrientation(ChipsLayoutManager.HORIZONTAL)
                .setRowStrategy(ChipsLayoutManager.STRATEGY_DEFAULT)
                .build()
        if(header_list.get(position).header_name.equals("Morning"))
        {
            list_item.clear()
            var listItem:ListItem= ListItem("8:00 am")
            list_item.add(listItem)
            listItem= ListItem("9:00 am")
            list_item.add(listItem)
            listItem= ListItem("10:00 am")
            list_item.add(listItem)
            listItem= ListItem("11:00 am")
            list_item.add(listItem)
            bookAdapterItems = BookAdapterItems(context,list_item)

            holder.rv_items.layoutManager = chipsLayoutManager
            holder.rv_items.adapter = bookAdapterItems
        }else if(header_list.get(position).header_name.equals("Afternoon"))
        {
            list_item.clear()

           var listItem:ListItem= ListItem("12:00 pm")
            list_item.add(listItem)
            listItem= ListItem("1:00 pm")
            list_item.add(listItem)
            listItem= ListItem("2:00 pm")
            list_item.add(listItem)

            listItem= ListItem("3:00 pm")
            list_item.add(listItem)
            listItem= ListItem("4:00 pm")
            list_item.add(listItem)
            listItem= ListItem("5:00 pm")
            list_item.add(listItem)
            listItem= ListItem("6:00 pm")
            list_item.add(listItem)
            bookAdapterItems = BookAdapterItems(context,list_item)

            holder.rv_items.layoutManager = chipsLayoutManager
            holder.rv_items.adapter = bookAdapterItems

        }else{
            list_item.clear()
            var listItem:ListItem= ListItem("8:00 pm")
            list_item.add(listItem)
            listItem= ListItem("9:00 pm")
            list_item.add(listItem)
            listItem= ListItem("10:00 pm")
            list_item.add(listItem)
            listItem= ListItem("11:00 pm")
            list_item.add(listItem)
            bookAdapterItems = BookAdapterItems(context,list_item)

            holder.rv_items.layoutManager = chipsLayoutManager
            holder.rv_items.adapter = bookAdapterItems
        }
    }
    override fun getItemCount(): Int {
        return header_list.size
    }
    class ViewHolder(viewitem: View) : RecyclerView.ViewHolder(viewitem) {

        var iv_image: ImageView
var tv_item:TextView
var rv_items:RecyclerView
        init {
            iv_image = viewitem.findViewById(R.id.iv_image)
            tv_item = viewitem.findViewById(R.id.tv_item)
            rv_items = viewitem.findViewById(R.id.rv_items)

        }
    }


    interface Callback {
        fun onItemClicked(view: View)
    }
}