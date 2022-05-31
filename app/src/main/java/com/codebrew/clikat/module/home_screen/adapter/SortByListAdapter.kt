package com.codebrew.clikat.module.home_screen.adapter


import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.codebrew.clikat.R
import com.codebrew.clikat.databinding.ItemSortByBinding
import com.codebrew.clikat.modal.other.SortByData
import com.codebrew.clikat.module.home_screen.listeners.OnSortByListenerClicked
import com.codebrew.clikat.utils.configurations.Configurations

class SortByListAdapter
    : RecyclerView.Adapter<SortByListAdapter.ViewHolder>() {

    private var list = ArrayList<SortByData>()
    private var mCallback: OnSortByListenerClicked? = null

    fun settingCallback(mCallback: OnSortByListenerClicked) {
        this.mCallback = mCallback
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        val binding = DataBindingUtil.inflate<ItemSortByBinding>(LayoutInflater.from(parent.context),
                R.layout.item_sort_by, parent, false)
        binding.color = Configurations.colors
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = list[position]
        holder.onBind(item)
    }

    override fun getItemCount(): Int = list.size

    inner class ViewHolder(val mView: ItemSortByBinding) : RecyclerView.ViewHolder(mView.root) {

        init {
            itemView.setOnClickListener {
                mCallback?.onItemSelected(adapterPosition)
            }
        }

        fun onBind(item: SortByData) {
            mView.btnRecomended.text = item.filterName
            mView.ivIcon.setImageResource(item.icon ?: 0)
        }
    }

    fun addList(list: ArrayList<SortByData>) {
        this.list.clear()
        this.list.addAll(list)
        notifyDataSetChanged()
    }


}
