package com.codebrew.clikat.module.jumaHome.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.codebrew.clikat.R
import com.codebrew.clikat.databinding.ItemJumaProductsBinding
import com.codebrew.clikat.modal.other.ProductBean
import com.codebrew.clikat.modal.other.ProductDataBean
import com.codebrew.clikat.utils.configurations.Configurations


class JumaProductsMainAdapter(val callback:OnProceedClicked) : RecyclerView.Adapter<JumaProductsMainAdapter.ViewHolder>() {
    private var list = ArrayList<ProductBean>()

    override fun getItemCount(): Int {
        return list.size
    }


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(list[position])
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = ItemJumaProductsBinding.inflate(layoutInflater, parent, false)
        binding.color=Configurations.colors
        return ViewHolder(binding.root)
    }


    inner class ViewHolder(itemView: View?) : RecyclerView.ViewHolder(itemView as View) {

        var tvTitle= itemView?.findViewById<TextView>(R.id.tvTitle)
        var tvProceed= itemView?.findViewById<TextView>(R.id.tvProceed)
        var rvProducts= itemView?.findViewById<RecyclerView>(R.id.rvProducts)

        init {
            tvProceed?.setOnClickListener {
                val list= list[adapterPosition].value?.filter { it.isSelected==true }
                callback.onProceedButtonClicked(list)
            }
        }
        fun bind(productBean: ProductBean) = with(itemView) {
            tvTitle?.text=productBean.sub_cat_name
            rvProducts?.adapter=JumaProductsAdapter(productBean.value,callback)
        }
    }

    fun addList(list: MutableList<ProductBean>) {
        this.list.clear()
        this.list.addAll(list)
        notifyDataSetChanged()
    }

    interface OnProceedClicked{
        fun onProceedButtonClicked(list: List<ProductDataBean>?)
        fun onProductClicked(index: Int)
    }
}

