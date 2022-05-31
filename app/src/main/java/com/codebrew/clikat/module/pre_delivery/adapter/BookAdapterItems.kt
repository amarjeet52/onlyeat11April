package com.codebrew.clikat.module.pre_delivery.adapter

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.codebrew.clikat.R
import com.codebrew.clikat.module.pre_delivery.Model.ListItem
import com.squareup.picasso.Picasso

class BookAdapterItems (chatActivity: Activity,internal var item_list: ArrayList<ListItem>) :  RecyclerView.Adapter<BookAdapterItems.ViewHolder>() {

    override fun onCreateViewHolder(viewgroup: ViewGroup, viewType: Int): ViewHolder {

        val v = LayoutInflater.from(viewgroup.getContext())
                .inflate(R.layout.layout_book_tablerecycler_item, viewgroup, false)

        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        holder.tv_item.setText(item_list.get(position).name)
    }
    override fun getItemCount(): Int {
        return item_list.size
    }
    class ViewHolder(viewitem: View) : RecyclerView.ViewHolder(viewitem) {

var tv_item:TextView

        init {

            tv_item = viewitem.findViewById(R.id.tv_item1)


        }
    }


    interface Callback {
        fun onItemClicked(view: View)
    }
}