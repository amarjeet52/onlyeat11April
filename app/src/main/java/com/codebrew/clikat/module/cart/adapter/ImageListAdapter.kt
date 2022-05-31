package com.codebrew.clikat.module.cart.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.codebrew.clikat.R
import com.codebrew.clikat.app_utils.extension.loadImage
import com.codebrew.clikat.data.model.others.ImageListModel
import com.codebrew.clikat.databinding.FooterPrescriptionImageBinding
import com.codebrew.clikat.databinding.HeaderPrescriptionImageBinding
import com.codebrew.clikat.databinding.ItemPrescriptionImageBinding
import com.codebrew.clikat.utils.configurations.Configurations
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

private const val ITEM_VIEW_TYPE_HEADER = 0
private const val ITEM_VIEW_TYPE_ITEM = 1
private const val ITEM_VIEW_TYPE_FOOTER = 2

class ImageListAdapter(val clickListener: UserChatListener, val isSocial: Boolean) :
        ListAdapter<ImageListAdapter.DataItem, RecyclerView.ViewHolder>(MessageListDiffCallback()) {

    private val adapterScope = CoroutineScope(Dispatchers.Default)

    private var mType = ""

    fun submitMessageList(list: List<ImageListModel?>?, type: String) {

        mType = type

        adapterScope.launch {

            val items: List<DataItem> = (if (type == "order") {
                list?.map { DataItem.MessageItem(it) }
            } else {
                when (list) {
                    null -> listOf(DataItem.Header)
                    else -> listOf(DataItem.Header) + list.map { DataItem.MessageItem(it) }
                }
            }) as List<DataItem>


            withContext(Dispatchers.Main) {
                submitList(items)
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is ViewHolder -> {
                val nightItem = getItem(position) as DataItem.MessageItem
                holder.bind(position, nightItem.messageData, clickListener, mType,isSocial)
            }

            is ImageViewHeader -> {
                holder.bind(clickListener,isSocial)
            }

            is ImageViewFooter -> {
                holder.bind(clickListener, currentList.count())
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            ITEM_VIEW_TYPE_HEADER -> ImageViewHeader.from(parent)
            ITEM_VIEW_TYPE_FOOTER -> ImageViewFooter.from(parent)
            ITEM_VIEW_TYPE_ITEM -> ViewHolder.from(parent)
            else -> throw ClassCastException("Unknown viewType $viewType")
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when (getItem(position)) {
            is DataItem.Header -> ITEM_VIEW_TYPE_HEADER
            is DataItem.Footer -> ITEM_VIEW_TYPE_FOOTER
            is DataItem.MessageItem -> ITEM_VIEW_TYPE_ITEM
        }
    }


    class ViewHolder private constructor(val binding: ItemPrescriptionImageBinding) :
            RecyclerView.ViewHolder(binding.root) {

        fun bind(position: Int, item: ImageListModel?, clickListener: UserChatListener, mType: String, social: Boolean) {
            binding.imageData = item
            binding.pos = position - 1
            binding.clickListener = clickListener
            binding.color = Configurations.colors
            binding.type = mType
            binding.isSocial = social

            binding.executePendingBindings()

            if(!item?.image.isNullOrEmpty() && item?.image?.endsWith(".pdf")==true)
                binding.imageView.setImageResource(R.drawable.pdf)
            else
               binding.imageView.loadImage(item?.image?:"")
        }

        companion object {
            fun from(parent: ViewGroup): ViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ItemPrescriptionImageBinding.inflate(layoutInflater, parent, false)
                return ViewHolder(binding)
            }
        }
    }

    class ImageViewHeader private constructor(val binding: HeaderPrescriptionImageBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(clickListener: UserChatListener, social: Boolean) {
            binding.clickListener = clickListener
            binding.imageModel = ImageListModel()
            binding.isSocial = social
            binding.executePendingBindings()
        }

        companion object {
            fun from(parent: ViewGroup): ImageViewHeader {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = HeaderPrescriptionImageBinding.inflate(layoutInflater, parent, false)
                return ImageViewHeader(binding)
            }
        }
    }


    class ImageViewFooter private constructor(val binding: FooterPrescriptionImageBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(clickListener: UserChatListener, count: Int) {
            binding.clickListener = clickListener
            binding.color = Configurations.colors
            binding.count = count.minus(4).toString()

            binding.executePendingBindings()
        }

        companion object {
            fun from(parent: ViewGroup): ImageViewFooter {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = FooterPrescriptionImageBinding.inflate(layoutInflater, parent, false)
                return ImageViewFooter(binding)
            }
        }
    }


    class MessageListDiffCallback : DiffUtil.ItemCallback<DataItem>() {
        override fun areItemsTheSame(oldItem: DataItem, newItem: DataItem): Boolean {
            return oldItem.chatData == newItem.chatData
        }

        @SuppressLint("DiffUtilEquals")
        override fun areContentsTheSame(oldItem: DataItem, newItem: DataItem): Boolean {
            return oldItem == newItem
        }
    }


    class UserChatListener(val clickListener: (model: ImageListModel) -> Unit, val deletelistener: (mType: Int) -> Unit,
                           val selectlistener: (model: ImageListModel,position: Int) -> Unit) {
        fun onClick(addressBean: ImageListModel) = clickListener(addressBean)
        fun onDeleteClick(mType: Int) = deletelistener(mType)
        fun onImageSelect(model: ImageListModel,position: Int)=selectlistener(model,position)
    }

    sealed class DataItem {
        data class MessageItem(val messageData: ImageListModel?) : DataItem() {
            override val chatData = messageData
        }

        object Header : DataItem() {
            override val chatData = ImageListModel()
        }

        object Footer : DataItem() {
            override val chatData = ImageListModel()
        }

        abstract val chatData: ImageListModel?
    }

}

