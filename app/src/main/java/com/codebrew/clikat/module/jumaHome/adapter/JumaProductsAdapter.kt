package com.codebrew.clikat.module.jumaHome.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.codebrew.clikat.R
import com.codebrew.clikat.databinding.ItemJumaProductTextBinding
import com.codebrew.clikat.modal.other.ProductDataBean
import com.codebrew.clikat.utils.configurations.Configurations


class JumaProductsAdapter(private val productsList: MutableList<ProductDataBean>?,
                          private val callback: JumaProductsMainAdapter.OnProceedClicked) : RecyclerView.Adapter<JumaProductsAdapter.ViewHolder>() {

    override fun getItemCount(): Int {
        return productsList?.size ?: 0
    }


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(productsList?.get(position))
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = ItemJumaProductTextBinding.inflate(layoutInflater, parent, false)
        binding.color = Configurations.colors
        return ViewHolder(binding.root)
    }


    inner class ViewHolder(itemView: View?) : RecyclerView.ViewHolder(itemView as View) {
        private val tvProductName = itemView?.findViewById<TextView>(R.id.tvProductName)
        private val ivSelect = itemView?.findViewById<ImageView>(R.id.ivSelect)

        init {
            ivSelect?.setOnClickListener {
                productsList?.get(adapterPosition)?.isSelected=!(productsList?.get(adapterPosition)?.isSelected?:false)
                notifyItemChanged(adapterPosition)
            }
        }

        fun bind(productDataBean: ProductDataBean?) = with(itemView) {
            tvProductName?.text = productDataBean?.name
            if(productDataBean?.isSelected==true)
                ivSelect?.setImageResource(R.drawable.ic_unselected_checkbox)
            else
              ivSelect?.setImageResource(R.drawable.ic_selected_checkbox)

        }
    }


}

