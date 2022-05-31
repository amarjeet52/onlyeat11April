package com.codebrew.clikat.module.essentialHome


import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.codebrew.clikat.R
import com.codebrew.clikat.databinding.ItemServiceListBinding
import com.codebrew.clikat.modal.other.English
import com.codebrew.clikat.utils.configurations.Configurations
import com.makeramen.roundedimageview.RoundedImageView
import kotlinx.android.synthetic.main.item_service_list.view.*

class ServiceViewAdapter(
        private val mValues: List<English>,
        private val mListener: ServiceHeaderViewAdapter.OnListFragmentInteractionListener?)
    : RecyclerView.Adapter<ServiceViewAdapter.ViewHolder>() {

    private val mOnClickListener: View.OnClickListener

    init {
        mOnClickListener = View.OnClickListener { v ->
            val item = v.tag as English
            // Notify the active callbacks interface (the activity, if the fragment is attached to
            // one) that an item has been selected.
            mListener?.onListFragmentInteraction(item)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        val binding: ItemServiceListBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.item_service_list, parent, false)
        binding.color = Configurations.colors
        binding.strings = Configurations.strings
        return ViewHolder(binding.root)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = mValues[position]
        // holder.mIdView.text = item.id

        if(!item.categoryName.isNullOrEmpty())
        {
            Glide.with(holder.itemView.context).load(item.imageId).into(holder.mIdView)
            holder.mContentView.text = item.categoryName
        }
        else
        {
            Glide.with(holder.itemView.context).load(item.image).into(holder.mIdView)
            holder.mContentView.text = item.name
        }



        with(holder.mView) {
            tag = item
            setOnClickListener(mOnClickListener)
        }
    }

    override fun getItemCount(): Int = mValues.size

    inner class ViewHolder(val mView: View) : RecyclerView.ViewHolder(mView) {
        val mIdView: RoundedImageView = mView.iv_category
        val mContentView: TextView = mView.tv_category

        override fun toString(): String {
            return super.toString() + " '" + mContentView.text + "'"
        }
    }

}
