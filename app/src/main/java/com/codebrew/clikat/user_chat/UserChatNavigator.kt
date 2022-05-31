package com.codebrew.clikat.user_chat

import com.codebrew.clikat.base.BaseInterface
import com.codebrew.clikat.data.model.api.ChatMessageListing


interface UserChatNavigator: BaseInterface {
    fun onImageUploaded(newMessage: ChatMessageListing?)
    fun onChatMessageId(messageId:String)
}