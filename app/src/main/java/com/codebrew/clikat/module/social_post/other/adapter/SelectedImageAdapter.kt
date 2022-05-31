package com.codebrew.clikat.module.social_post.other.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.codebrew.clikat.data.model.others.ImageListModel
import com.codebrew.clikat.databinding.ItemSocialImagesBinding
import com.codebrew.clikat.utils.configurations.Configurations
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class SelectedImageAdapter(val clickListener: ImageListener, val isDeleteBtn: Boolean): ListAdapter<SelectedImageItem, RecyclerView.ViewHolder>(ItemListDiffCallback()) {

    private val adapterScope = CoroutineScope(Dispatchers.Default)


    fun submitItemList(list: MutableList<ImageListModel?>?) {
        adapterScope.launch {
            val items = list?.map { SelectedImageItem.ImageItem(it) }

            withContext(Dispatchers.Main) {
                submitList(items)
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is VarientViewHolder -> {
                val nightItem = getItem(position) as SelectedImageItem.ImageItem
                holder.bind(position,nightItem.varientData,clickListener,isDeleteBtn)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return VarientViewHolder.from(parent)
    }
}


class VarientViewHolder private constructor(val binding: ItemSocialImagesBinding) :
        RecyclerView.ViewHolder(binding.root) {

    fun bind(position: Int, item: ImageListModel?, clickListener: ImageListener, deleteBtn: Boolean) {
        binding.color = Configurations.colors
        binding.dataItem =item
        binding.position =position
        binding.listener =clickListener
        binding.deleteBtn =deleteBtn
        binding.executePendingBindings()
    }

    companion object {
        fun from(parent: ViewGroup): VarientViewHolder {
            val layoutInflater = LayoutInflater.from(parent.context)
            val binding = ItemSocialImagesBinding.inflate(layoutInflater, parent, false)
            return VarientViewHolder(binding)
        }
    }
}


class ItemListDiffCallback : DiffUtil.ItemCallback<SelectedImageItem>() {
    override fun areItemsTheSame(oldItem: SelectedImageItem, newItem: SelectedImageItem): Boolean {
        return oldItem.varientData == newItem.varientData
    }

    @SuppressLint("DiffUtilEquals")
    override fun areContentsTheSame(oldItem: SelectedImageItem, newItem: SelectedImageItem): Boolean {
        return oldItem.varientData == newItem.varientData
    }
}

class ImageListener(val clickListener: (model: ImageListModel,pos:Int) -> Unit) {
    fun onImageSelect(model: ImageListModel,position: Int)=clickListener(model,position)
}


sealed class SelectedImageItem {
    data class ImageItem(val mVarientData: ImageListModel?) : SelectedImageItem() {
        override val varientData = mVarientData
    }

    abstract val varientData: ImageListModel?
}

