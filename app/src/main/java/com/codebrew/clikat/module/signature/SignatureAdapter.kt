package com.codebrew.clikat.module.signature

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.codebrew.clikat.R
import com.codebrew.clikat.databinding.ItemSignatureBinding
import com.codebrew.clikat.modal.other.SupplierDataBean
import com.codebrew.clikat.utils.configurations.Configurations

class SignatureAdapter internal constructor(private val data: List<SupplierDataBean>,
                                            private val type: String) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var mContext: Context? = null
    var onItemClick: OnItemClick? = null

    fun settingCallback(mCallback: OnItemClick?) {
        onItemClick = mCallback;
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): RecyclerView.ViewHolder {
        mContext = viewGroup.context
        val binding: ItemSignatureBinding = DataBindingUtil.inflate(LayoutInflater.from(viewGroup.context),
                R.layout.item_signature, viewGroup, false)
        binding.color = Configurations.colors
        return ViewHolder(binding.root)
    }

    override fun onBindViewHolder(viewHolder: RecyclerView.ViewHolder, i: Int) {
        val pos = viewHolder.adapterPosition
        val holder = viewHolder as ViewHolder
        holder.tvName.text = data[pos].name
        holder.tvDesc.text = data[pos].description
        Glide.with(holder.itemView).load(data[pos].logo).into(holder.ivUser)

        holder.itemView.setOnClickListener {
            onItemClick?.onClick(data[pos])
        }

        if (type == "2") {
            holder.tvPriceOnSel.text = " Visit Boutique "
        } else if (type == "3") {
            holder.tvPriceOnSel.text = " View Plates "
        } else {
            holder.tvPriceOnSel.text = " See catering menu "
        }
    }

    override fun getItemCount(): Int {
        return data.size
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var tvName: TextView = itemView.findViewById(R.id.tvTitle)
        var tvDesc: TextView = itemView.findViewById(R.id.tvDesc)
        var tvPriceOnSel: TextView = itemView.findViewById(R.id.tvPriceOnSel)
        var ivUser: ImageView = itemView.findViewById(R.id.ivUser)
    }

    interface OnItemClick {
        fun onClick(supplierDataBean: SupplierDataBean)
    }

}