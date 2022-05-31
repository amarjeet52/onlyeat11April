package com.codebrew.clikat.module.essentialHome


import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.codebrew.clikat.BuildConfig
import com.codebrew.clikat.R
import com.codebrew.clikat.databinding.ItemServiceHeaderBinding
import com.codebrew.clikat.databinding.ItemServiceListBinding
import com.codebrew.clikat.modal.other.English
import kotlinx.android.synthetic.main.item_service_header.view.*

class ServiceHeaderViewAdapter(
        private val mValues: HashMap<String, List<English>>,
        private val mListener: OnListFragmentInteractionListener?)
    : RecyclerView.Adapter<ServiceHeaderViewAdapter.ViewHolder>() {



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding: ItemServiceHeaderBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.item_service_header, parent, false)
        return ViewHolder(binding.root)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val keys = mValues.keys.toList()

        val headerName = keys[position]

        holder.tvHeaderName.text = headerName

        val mValues = this.mValues[keys[position]]

        val mAdapter: ServiceViewAdapter? = ServiceViewAdapter(mValues ?: emptyList(), mListener)

        val count = if (BuildConfig.CLIENT_CODE == "lastminute_0382") 1 else 2
        holder.rvCategories.layoutManager = GridLayoutManager(holder.rvCategories.context, count)
        holder.rvCategories.adapter = mAdapter

    }

    override fun getItemCount(): Int = mValues.size

    inner class ViewHolder(val mView: View) : RecyclerView.ViewHolder(mView) {
        val tvHeaderName: TextView = mView.tv_header_name
        val rvCategories: RecyclerView = mView.rv_categories
    }

    interface OnListFragmentInteractionListener {
        fun onListFragmentInteraction(item: English?)
    }

}
