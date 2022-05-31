package com.codebrew.clikat.module.crave_mania.adapter

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.codebrew.clikat.R
import com.codebrew.clikat.module.crave_mania.FdArray

class CraveManiaItemAdapter(internal var activity: Activity, internal var item_list: List<FdArray>, internal var mCallback: CraveListCallback) : RecyclerView.Adapter<CraveManiaItemAdapter.ViewHolder>() {

    override fun onCreateViewHolder(viewgroup: ViewGroup, viewType: Int): ViewHolder {

        val v = LayoutInflater.from(viewgroup.getContext())
                .inflate(R.layout.item_supplier_crave_new_layout, viewgroup, false)

        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.tvName.setText(item_list.get(position).supplier_detail.name)
        Glide.with(activity).load(item_list.get(position).supplier_detail.logo).into(holder.sdvImage)
holder.viewLayout.setOnClickListener {
    mCallback!!.onCraveListDetail(item_list,position)
}
    }

    override fun getItemCount(): Int {
        return item_list.size
    }

    class ViewHolder(viewitem: View) : RecyclerView.ViewHolder(viewitem) {

        var tvName: TextView
        var tvSupplierloc: TextView
        var sdvImage: ImageView
        var tv_pre_order: TextView
var rl_close:RelativeLayout
        var rl_close_e:RelativeLayout
        var viewLayout:ConstraintLayout
        init {
            viewLayout=viewitem.findViewById(R.id.viewLayout)
            tv_pre_order = viewitem.findViewById(R.id.tv_pre_order)
            rl_close = viewitem.findViewById(R.id.rl_close)
            rl_close_e = viewitem.findViewById(R.id.rl_close_e)
            tvName = viewitem.findViewById(R.id.tvName)
            tvSupplierloc = viewitem.findViewById(R.id.tvSupplierloc)
            sdvImage = viewitem.findViewById(R.id.sdvImage)
            tv_pre_order.visibility=View.GONE
            rl_close.visibility=View.GONE
            rl_close_e.visibility=View.GONE
         //   ratingBar = viewitem.findViewById(R.id.ratingBar)
        }
    }
    interface CraveListCallback {
        fun onCraveListDetail(item_list:List<FdArray>,pos: Int)
    }


}