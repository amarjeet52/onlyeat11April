package com.codebrew.clikat.module.signup

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.codebrew.clikat.R
import kotlinx.android.synthetic.main.item_img_document.view.*

class DocumentsListAdapter(val onItemClicked: OnItemClicked) :
        RecyclerView.Adapter<DocumentsListAdapter.ViewHolder>() {

    private var imageList = ArrayList<String>()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
                LayoutInflater.from(parent.context).inflate(
                        R.layout.item_img_document,
                        parent,
                        false
                )
        )
    }

    override fun getItemCount(): Int = imageList.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.onBind(imageList[position])
    }


    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        init {
            itemView.ivAdd?.setOnClickListener {
                onItemClicked.onItemClicked()
            }
            itemView.ivCross?.setOnClickListener {
                onItemClicked.onItemRemoved(adapterPosition)
            }

        }

        fun onBind(item: String) = with(itemView) {
            val requestOptions = RequestOptions()
                    .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                    .placeholder(R.drawable.bg_ecomerce)
                    .error(R.drawable.bg_ecomerce)

            ivDocument?.visibility = if (adapterPosition != (imageList.size - 1)) View.VISIBLE else View.GONE
            ivCross?.visibility = if (adapterPosition != (imageList.size - 1)) View.VISIBLE else View.GONE

            Glide.with(itemView.context).load(item).apply(requestOptions).into(ivDocument)
            ivAdd?.visibility =
                    if (adapterPosition == (imageList.size - 1) && imageList.size <= 4) View.VISIBLE else View.GONE

        }
    }

    fun addImage(item: String) {
        imageList.add(0, item)
        notifyDataSetChanged()
    }

    fun removeItem(position: Int) {
        imageList.removeAt(position)
        notifyDataSetChanged()
    }

    fun getList(): ArrayList<String> {
        return imageList
    }

    interface OnItemClicked {
        fun onItemClicked()
        fun onItemRemoved(adapterPosition: Int)
    }
}