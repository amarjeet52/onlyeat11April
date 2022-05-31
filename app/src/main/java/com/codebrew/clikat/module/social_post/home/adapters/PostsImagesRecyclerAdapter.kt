package com.codebrew.clikat.module.social_post.home.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.codebrew.clikat.R
import com.codebrew.clikat.data.model.others.ImageListModel
import com.codebrew.clikat.databinding.ItemSocialImagesBinding
import com.codebrew.clikat.module.social_post.interfaces.RecyclerActionListener
import com.codebrew.clikat.utils.configurations.Configurations

class PostsImagesRecyclerAdapter : RecyclerView.Adapter<PostsImagesRecyclerAdapter.ViewHolder>() {
    private lateinit var actionListener: RecyclerActionListener
    private lateinit var collection: ArrayList<ImageListModel?>

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding: ItemSocialImagesBinding = DataBindingUtil.inflate(
                LayoutInflater.from(parent.context),
                R.layout.item_social_images,
                parent,
                false
        )

        binding.color = Configurations.colors
        return ViewHolder(binding)
    }


    override fun getItemCount(): Int = collection.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) =
            holder.onBind(collection[position])

    inner class ViewHolder(val binding: ItemSocialImagesBinding) : RecyclerView.ViewHolder(binding.root) {
        fun onBind(dataItem: ImageListModel?) = with(itemView) {
            binding.dataItem = dataItem

            binding.postImage.setOnClickListener {
                actionListener.onProdItemUpdate(collection[adapterPosition]?.image?:"",binding.postImage)
            }
        }
    }

    fun setOnActionListener(actionListener: RecyclerActionListener) {
        this.actionListener = actionListener
    }

    fun setListData(collection: ArrayList<ImageListModel?>) {
        this.collection = collection
    }
}