package com.codebrew.clikat.user_chat.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.codebrew.clikat.data.MessageStatus
import com.codebrew.clikat.data.model.api.ChatMessageListing
import com.codebrew.clikat.databinding.ItemChatHeaderBinding
import com.codebrew.clikat.databinding.ItemChatTextLeftBinding
import com.codebrew.clikat.databinding.ItemChatTextRightBinding
import com.codebrew.clikat.user_chat.viewHolder.ImageLeftViewHolder
import com.codebrew.clikat.user_chat.viewHolder.ImageRightViewHolder


private const val TEXT_LEFT_VIEW_TYPE = 1
private const val TEXT_RIGHT_VIEW_TYPE = 2
private const val IMAGE_LEFT_VIEW_TYPE = 4
private const val IMAGE_RIGHT_VIEW_TYPE = 5

private const val Date_VIEW_TYPE = 3

private const val MESSAGE_TYPE = "text"
private const val MESSAGE_IMAGE_TYPE = "image"
private const val TIME_TYPE = "time"

class ChatAdapter(val clickListener: ChatListener) :
        RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val messages = mutableListOf<ChatMessageListing>()

    /*private val adapterScope = CoroutineScope(Dispatchers.Default)

    fun addItmSubmitList(list: List<ChatMessageListing>?) {
        adapterScope.launch {
            val items = list?.map {
                when (it.chat_type) {
                    MESSAGE_TYPE -> {
                        if (it.ownMessage == true) {
                            DataItem.TextRightItem(it)
                        } else {
                            DataItem.TextLeftItem(it)
                        }
                    }
                    MESSAGE_IMAGE_TYPE -> {
                        if (it.ownMessage == true) {
                            DataItem.ImageRightItem(it)
                        } else {
                            DataItem.ImageLeftItem(it)
                        }
                    }
                    TIME_TYPE -> {
                        DataItem.DateItem(it)
                    }

                    else -> DataItem.ChatItem(it)
                }
            }
            withContext(Dispatchers.Main) {
                submitList(items)
            }
        }
    }*/
    fun addItmSubmitList(list: List<ChatMessageListing>?) {
        messages.addAll(0, list ?: emptyList())
        notifyItemRangeInserted(0, list?.size ?: 0)
    }


    fun addNewMessage(message: ChatMessageListing) {
        messages.add(message)
        notifyItemInserted(itemCount - 1)
    }

    fun updateMessageStatus(localId: String, messageStatus: MessageStatus) {
        val totalMessages = messages.size
        if (totalMessages == 0) return
        // Find the message from the last and set update message status
        for (index in totalMessages - 1 downTo 0) {
            if (messages[index].localId == localId) {
                messages[index].messageStatus = messageStatus
                notifyItemChanged(index)
                break
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val dataItem = messages[position]
        when (holder) {
            is TextLeftViewHolder -> {
                holder.bind(messages[position], clickListener)
            }
            is TextRightViewHolder -> {
                holder.bind(dataItem, clickListener)
            }

            is ImageLeftViewHolder -> {
                holder.bind(dataItem)
            }

            is ImageRightViewHolder -> {
                holder.bind(dataItem)
            }
            is TextHeaderViewHolder -> {
                holder.bind(dataItem)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            TEXT_LEFT_VIEW_TYPE -> TextLeftViewHolder.from(parent)
            TEXT_RIGHT_VIEW_TYPE -> TextRightViewHolder.from(parent)
            Date_VIEW_TYPE -> TextHeaderViewHolder.from(parent)
            IMAGE_LEFT_VIEW_TYPE -> ImageLeftViewHolder.from(parent, clickListener)
            IMAGE_RIGHT_VIEW_TYPE -> ImageRightViewHolder.from(parent, clickListener)
            else -> throw ClassCastException("Unknown viewType $viewType")
        }
    }

    override fun getItemCount() = messages.size

    override fun getItemViewType(position: Int): Int {
        val message = messages[position]
        val ownMessage = message.ownMessage == true

        return when (message.chat_type) {
            MESSAGE_TYPE -> {
                if (ownMessage) {
                    TEXT_RIGHT_VIEW_TYPE
                } else {
                    TEXT_LEFT_VIEW_TYPE
                }
            }

            MESSAGE_IMAGE_TYPE -> {
                if (ownMessage) {
                    IMAGE_RIGHT_VIEW_TYPE
                } else {
                    IMAGE_LEFT_VIEW_TYPE
                }
            }

            else -> Date_VIEW_TYPE
        }
    }
/*    override fun getItemViewType(position: Int): Int {
        return when (getItem(position)) {
            is DataItem.TextLeftItem -> TEXT_LEFT_VIEW_TYPE
            is DataItem.TextRightItem -> TEXT_RIGHT_VIEW_TYPE
            is DataItem.ImageLeftItem -> IMAGE_LEFT_VIEW_TYPE
            is DataItem.ImageRightItem -> IMAGE_RIGHT_VIEW_TYPE
            is DataItem.DateItem -> Date_VIEW_TYPE
            else -> -1
        }
    }*/


    class TextLeftViewHolder private constructor(val binding: ItemChatTextLeftBinding) :
            RecyclerView.ViewHolder(binding.root) {

        fun bind(item: ChatMessageListing, listener: ChatListener) {
            binding.clickListener = listener
            binding.chatData = item
            binding.executePendingBindings()
        }

        companion object {
            fun from(parent: ViewGroup): TextLeftViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ItemChatTextLeftBinding.inflate(layoutInflater, parent, false)
                return TextLeftViewHolder(binding)
            }
        }
    }


    class TextRightViewHolder private constructor(val binding: ItemChatTextRightBinding) :
            RecyclerView.ViewHolder(binding.root) {

        fun bind(item: ChatMessageListing, listener: ChatListener) {
            binding.clickListener = listener
            binding.chatData = item
            binding.executePendingBindings()
        }

        companion object {
            fun from(parent: ViewGroup): TextRightViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ItemChatTextRightBinding.inflate(layoutInflater, parent, false)
                return TextRightViewHolder(binding)
            }
        }
    }

    class TextHeaderViewHolder private constructor(val binding: ItemChatHeaderBinding) :
            RecyclerView.ViewHolder(binding.root) {

        fun bind(item: ChatMessageListing) {
            binding.chatData = item
            binding.executePendingBindings()
        }

        companion object {
            fun from(parent: ViewGroup): TextHeaderViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ItemChatHeaderBinding.inflate(layoutInflater, parent, false)
                return TextHeaderViewHolder(binding)
            }
        }
    }

}

/*class SleepNightDiffCallback : DiffUtil.ItemCallback<DataItem>() {
    override fun areItemsTheSame(oldItem: DataItem, newItem: DataItem): Boolean {
        return oldItem.chatData.c_id == newItem.chatData.c_id
    }

    @SuppressLint("DiffUtilEquals")
    override fun areContentsTheSame(oldItem: DataItem, newItem: DataItem): Boolean {
        return oldItem == newItem
    }
}*/


class ChatListener(val clickListener: (model: ChatMessageListing) -> Unit, val resendImageListener: (chatMessage: ChatMessageListing) -> Unit
        , val imgClickListener: (chatMessage: ChatMessageListing,image:ImageView) -> Unit) {
    fun onClick(chatBean: ChatMessageListing) = clickListener(chatBean)
    fun onResendMessageClicked(chatMessage: ChatMessageListing) = resendImageListener(chatMessage)
    fun onImageClick(chatMessage: ChatMessageListing,image:ImageView) = imgClickListener(chatMessage,image)
}


/*
sealed class DataItem {
    data class TextLeftItem(val messageData: ChatMessageListing) : DataItem() {
        override val chatData = messageData
    }

    data class TextRightItem(val messageData: ChatMessageListing) : DataItem() {
        override val chatData = messageData
    }

    data class ImageLeftItem(val messageData: ChatMessageListing) : DataItem() {
        override val chatData = messageData
    }

    data class ImageRightItem(val messageData: ChatMessageListing) : DataItem() {
        override val chatData = messageData
    }

    data class DateItem(val messageData: ChatMessageListing) : DataItem() {
        override val chatData = messageData
    }


    data class ChatItem(val messageData: ChatMessageListing) : DataItem() {
        override val chatData = messageData
    }

    abstract val chatData: ChatMessageListing
}
*/

