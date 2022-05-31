package com.codebrew.clikat.modal.crave_mania

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.codebrew.clikat.R
import com.codebrew.clikat.module.restaurant_detail.Final
import kotlinx.android.synthetic.main.item_supplier_crave_new_layout.view.*

class CraveManiaAdapter(activity: Activity, private var itemList: ArrayList<Final>) :  RecyclerView.Adapter<CraveManiaAdapter.ViewHolder>() {

    override fun onCreateViewHolder(viewgroup: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(viewgroup.context).inflate(R.layout.item_supplier_crave_new_layout, viewgroup, false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.onBind(itemList[position])
    }
    override fun getItemCount(): Int {
        return itemList.size
    }
    class ViewHolder(viewItem: View) : RecyclerView.ViewHolder(viewItem) {
        fun onBind(data: Final) = with(itemView){
            tv_pre_order.visibility=View.GONE
            rl_close.visibility=View.GONE
            rl_close_e.visibility=View.GONE
            tvName.text = data.supplierDetail?.branchName.toString()
            tvSupplierloc.text = data.supplierDetail?.branchName.toString()
            Glide.with(itemView).load(data.supplierDetail?.logo).into(sdvImage)
        }

    }



}