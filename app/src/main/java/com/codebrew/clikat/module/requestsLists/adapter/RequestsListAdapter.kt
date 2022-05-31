package com.codebrew.clikat.module.requestsLists.adapter


import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.codebrew.clikat.R
import com.codebrew.clikat.data.model.api.RequestData
import com.codebrew.clikat.databinding.ItemRequestsListBinding
import com.codebrew.clikat.utils.configurations.Configurations

class RequestsListAdapter(private var onItemClicked: OnRequestClicked)
    : RecyclerView.Adapter<RequestsListAdapter.ViewHolder>() {

    private var list = ArrayList<RequestData>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        val binding = DataBindingUtil.inflate<ItemRequestsListBinding>(LayoutInflater.from(parent.context),
                R.layout.item_requests_list, parent, false)
        binding.color = Configurations.colors
        binding.strings = Configurations.strings
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = list[position]
        holder.onBind(item)
    }

    override fun getItemCount(): Int = list.size

    inner class ViewHolder(val mView: ItemRequestsListBinding) : RecyclerView.ViewHolder(mView.root) {

        init {
            mView.tvCancel.setOnClickListener {
                onItemClicked.onItemCancelled(list[adapterPosition], adapterPosition)
            }
        }

        fun onBind(item: RequestData) {
            mView.requestModel = item
        }
    }

    fun addList(isFirstPage: Boolean, list: ArrayList<RequestData>) {
        if (isFirstPage)
            this.list.clear()
        this.list.addAll(list)
        notifyDataSetChanged()
    }

    fun changeStatus(position: Int) {
        if (position != -1) {
            list[position].status = 4.0
            notifyItemChanged(position)
        }
    }

    interface OnRequestClicked {
        fun onItemCancelled(item: RequestData?, adapterPos: Int)
    }
}
