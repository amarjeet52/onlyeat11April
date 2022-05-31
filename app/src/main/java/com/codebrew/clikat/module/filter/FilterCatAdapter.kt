package com.codebrew.clikat.module.filter

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.codebrew.clikat.R
import com.codebrew.clikat.databinding.ItemFilterTabBinding
import com.codebrew.clikat.utils.configurations.Configurations
import kotlinx.android.synthetic.main.item_filter_tab.view.*

class FilterCatAdapter(private var mContext: Context?) : RecyclerView.Adapter<FilterCatAdapter.ViewHolder>() {

    private var categorylist = mutableListOf<String>()

    private lateinit var mCallback: FilterCallback

    private var selectedPos = 0


    init {
        categorylist = mContext?.resources?.getStringArray(R.array.filter_array)?.toMutableList()
                ?: mutableListOf()
    }

    val mSelectedColor: String by lazy {
        Configurations.colors.search_background
    }

    val mWhiteColor: String by lazy {
        Configurations.colors.appBackground
    }

    fun settingCallback(mCallback: FilterCallback) {
        this.mCallback = mCallback
    }

    fun addCategory(category: String) {
        categorylist.add(category)
        notifyDataSetChanged()
    }

    fun removeCategory(category: String) {
        categorylist.removeAt(categorylist.indexOf(category))
        notifyDataSetChanged()
    }

    fun refreshAdapter() {
        selectedPos = 0
        notifyDataSetChanged()
    }

    fun isCatgyExit(category: String): Boolean {
        return categorylist.any {
            it == category
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = DataBindingUtil.inflate<ItemFilterTabBinding>(LayoutInflater.from(parent.context),
                R.layout.item_filter_tab, parent, false)
        binding.color = Configurations.colors
        return ViewHolder(binding.root)
    }

    override fun getItemCount(): Int {
        return categorylist.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        holder.onBind(categorylist[holder.adapterPosition])

        if (selectedPos == holder.adapterPosition) {
            holder.filter_text.setBackgroundColor(Color.parseColor(mWhiteColor))
        } else {
            holder.filter_text.setBackgroundColor(Color.parseColor(mSelectedColor))
        }


        holder.filter_text.setOnClickListener {
            selectedPos=holder.adapterPosition
            mCallback.onfilterSelect(selectedPos)
            notifyDataSetChanged()
        }
    }


    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val filter_text = itemView.tv_name

        fun onBind(name: String) {
            filter_text.text = name
        }
    }

    interface FilterCallback {
        fun onfilterSelect(position: Int)
    }
}