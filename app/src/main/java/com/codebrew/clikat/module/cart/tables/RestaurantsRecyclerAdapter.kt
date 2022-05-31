package com.codebrew.clikat.module.cart.tables

import android.annotation.SuppressLint
import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.codebrew.clikat.R
import com.codebrew.clikat.data.model.api.ListItem
import com.codebrew.clikat.databinding.ItemFilterChecksizeBinding
import com.codebrew.clikat.utils.configurations.Configurations
import kotlinx.android.synthetic.main.item_filter_checksize.view.*

class RestaurantsRecyclerAdapter: RecyclerView.Adapter<RestaurantsRecyclerAdapter.ItemViewHolder>() {
    private var listCollection = mutableListOf<ListItem?>()
    var selectedPosition = -1

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val binding: ItemFilterChecksizeBinding = DataBindingUtil.inflate(
                LayoutInflater.from(parent.context),
                R.layout.item_filter_checksize,
                parent,
                false
        )
        binding.color = Configurations.colors
        return ItemViewHolder(binding)
    }

    override fun getItemCount(): Int = listCollection.size

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        holder.onBind(listCollection[position])
    }

    fun setListData(listCollection: List<ListItem?>?) {
        this.listCollection = listCollection as MutableList<ListItem?>
    }


    inner class ItemViewHolder(binding: ItemFilterChecksizeBinding) :
            RecyclerView.ViewHolder(binding.root) {

        init {
            val params = itemView.tv_name?.layoutParams as RecyclerView.LayoutParams
            params.width = RecyclerView.LayoutParams.MATCH_PARENT
            itemView.tv_name?.layoutParams = params

        }
        @SuppressLint("SetTextI18n")
        fun onBind(itemModel: ListItem?) {
            itemView.tv_name?.text = "${itemModel?.tableName} \n ${
            itemView.context.getString(R.string.seating_capacity, itemModel?.seatingCapacity?.toString())}"

            itemView.setOnClickListener {
                selectedPosition = adapterPosition
                notifyDataSetChanged()
            }

            if (adapterPosition == selectedPosition) {
                itemView.tv_name.setBackgroundColor(Color.parseColor(Configurations.colors.primaryColor))
                itemView.tv_name.setTextColor(Color.parseColor(Configurations.colors.appBackground))
            } else {
                itemView.tv_name.setBackgroundColor(Color.parseColor(Configurations.colors.appBackground))
                itemView.tv_name.setTextColor(Color.parseColor(Configurations.colors.textHead))
            }
        }
    }

}

