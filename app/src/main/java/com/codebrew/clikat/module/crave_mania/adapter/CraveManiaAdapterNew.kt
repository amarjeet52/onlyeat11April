package com.codebrew.clikat.module.crave_mania.adapter

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.codebrew.clikat.R

import com.codebrew.clikat.module.crave_mania.FdArray
import com.codebrew.clikat.module.crave_mania.SectionDataModel


class CraveManiaAdapterNew(internal var activity: Activity, internal var item_list: List<SectionDataModel>) : RecyclerView.Adapter<CraveManiaAdapterNew.ViewHolder>() {
    lateinit var craveManiaItemAdapter: CraveManiaItemAdapter
    private var mCallback: CraveListCallback? = null
    override fun onCreateViewHolder(viewgroup: ViewGroup, viewType: Int): ViewHolder {

        val v = LayoutInflater.from(viewgroup.getContext())
                .inflate(R.layout.layout_crave_mania_list, viewgroup, false)

        return ViewHolder(v)
    }

    fun settingCallback(mCallback: CraveListCallback) {
        this.mCallback = mCallback
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.tv_title.setText(item_list.get(position).getHeaderTitle())
        val linearLayoutManager =
                LinearLayoutManager(activity)
        holder.rv_homeItem.layoutManager = linearLayoutManager
        holder.tv_viewAll.setOnClickListener {
            if (item_list.get(position).getHeaderTitle().toString().equals(activity.getString(R.string.free_delivery))) {
                mCallback?.onViewAllClick("free_delivery")
            } else if (item_list.get(position).getHeaderTitle().toString().equals(activity.getString(R.string.discount))) {
                mCallback?.onViewAllClick("is_discount")
            } else if (item_list.get(position).getHeaderTitle().toString().equals(activity.getString(R.string.combo))) {
                mCallback?.onViewAllClick("is_combo")
            } else if (item_list.get(position).getHeaderTitle().toString().equals(activity.getString(R.string.buy1))) {
                mCallback?.onViewAllClick("buy_one_get_one")
            }

        }
        if (item_list.get(position).getAllItemsInSection()?.get(position)?.is_selection == 0) {
            holder.iv_imageView.visibility = View.GONE
            holder.tv_title.visibility = View.GONE
            holder.iv_view.visibility = View.GONE
            Glide.with(activity).load(R.drawable.iv_placeholder).into(holder.iv_imageView)

        } else {
            holder.iv_view.visibility = View.VISIBLE
            holder.tv_title.visibility = View.VISIBLE
            holder.iv_imageView.visibility = View.VISIBLE
            Glide.with(activity).load(item_list.get(position).getAllItemsInSection()?.get(position)?.value).into(holder.iv_imageView)

        }
        holder.iv_imageView.setOnClickListener {
            if (item_list.get(position).getHeaderTitle().toString().equals(activity.getString(R.string.free_delivery))) {
                mCallback?.onViewAllClick("Freedelivery")
            } else if (item_list.get(position).getHeaderTitle().toString().equals(activity.getString(R.string.discount))) {
                mCallback?.onViewAllClick("Discount")
            } else if (item_list.get(position).getHeaderTitle().toString().equals(activity.getString(R.string.combo))) {
                mCallback?.onViewAllClick("Combo")
            } else if (item_list.get(position).getHeaderTitle().toString().equals(activity.getString(R.string.buy1))) {
                mCallback?.onViewAllClick("Buy1get1")
            }
        }


    }

    override fun getItemCount(): Int {
        return item_list.size
    }

    class ViewHolder(viewitem: View) : RecyclerView.ViewHolder(viewitem) {

        var tv_title: TextView
        var tv_viewAll: TextView
        var rv_homeItem: RecyclerView
        var iv_imageView: ImageView
        var iv_view: ImageView

        init {
            tv_title = viewitem.findViewById(R.id.tv_title)
            tv_viewAll = viewitem.findViewById(R.id.tv_viewAll)
            rv_homeItem = viewitem.findViewById(R.id.rv_homeItem)
            iv_imageView = viewitem.findViewById(R.id.iv_image)
            iv_view = viewitem.findViewById(R.id.iv_view)

        }
    }

    interface CraveListCallback {
        fun onCraveDetail(item_list: List<FdArray>, pos: Int)
        fun onViewAllClick(type: String)
    }


}