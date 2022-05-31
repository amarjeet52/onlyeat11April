package com.codebrew.clikat.user_chat.viewHolder

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.codebrew.clikat.R
import com.codebrew.clikat.app_utils.CommonUtils.Companion.hide
import com.codebrew.clikat.app_utils.CommonUtils.Companion.isNetworkConnected
import com.codebrew.clikat.app_utils.CommonUtils.Companion.visible
import com.codebrew.clikat.app_utils.extension.loadImage
import com.codebrew.clikat.app_utils.extension.loadUserImage
import com.codebrew.clikat.data.MessageStatus
import com.codebrew.clikat.data.model.api.ChatMessageListing
import com.codebrew.clikat.databinding.ItemChatImageLeftBinding
import com.codebrew.clikat.user_chat.adapter.ChatListener
import kotlinx.android.synthetic.main.item_chat_image_left.view.*
import kotlinx.android.synthetic.main.item_chat_image_left.view.btnResend
import kotlinx.android.synthetic.main.item_chat_image_left.view.ivImage
import kotlinx.android.synthetic.main.item_chat_image_left.view.progressBar
import kotlinx.android.synthetic.main.item_chat_image_right.view.*

class ImageLeftViewHolder private constructor(val binding: ItemChatImageLeftBinding, val callback: ChatListener) :
        RecyclerView.ViewHolder(binding.root) {

    companion object {
        fun from(parent: ViewGroup, callback: ChatListener): ImageLeftViewHolder {
            val layoutInflater = LayoutInflater.from(parent.context)
            val binding = ItemChatImageLeftBinding.inflate(layoutInflater, parent, false)
            return ImageLeftViewHolder(binding, callback)
        }
    }

    init {

        itemView.btnResend.setOnClickListener {
            if (adapterPosition == RecyclerView.NO_POSITION) {
                return@setOnClickListener
            }
            if (isNetworkConnected(itemView.context)) {
                message.messageStatus = MessageStatus.SENDING
                updateMessageStatus(message.messageStatus)
                callback.onResendMessageClicked(message)
            }
        }

        itemView.ivImage.setOnClickListener {
            if (adapterPosition == RecyclerView.NO_POSITION) {
                return@setOnClickListener
            }
            callback.imgClickListener(message,itemView.ivImage)
        }
    }

    private lateinit var message: ChatMessageListing

    fun bind(message: ChatMessageListing) {
        binding.chatData = message
        binding.executePendingBindings()

        this.message = message
        val image: Any? = if (message.localFile != null) {
            message.localFile
        } else {
            message.image_url
        }

        itemView.ivImage.loadUserImage(image.toString())

        updateMessageStatus(message.messageStatus)
    }

    private fun updateMessageStatus(status: MessageStatus) {
        when (status) {
            MessageStatus.SENDING -> {
                itemView.progressBar.visible()
                itemView.btnResend.isEnabled = false
                itemView.btnResend.hide()
            }

            MessageStatus.SENT -> {
                itemView.progressBar.hide()
                itemView.btnResend.isEnabled = false
                itemView.btnResend.hide()
            }

            MessageStatus.ERROR -> {
                itemView.progressBar.hide()
                itemView.btnResend.isEnabled = true
                itemView.btnResend.visible()
            }
        }
    }


}