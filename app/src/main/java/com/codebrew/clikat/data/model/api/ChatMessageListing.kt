package com.codebrew.clikat.data.model.api

import android.os.Parcelable
import com.codebrew.clikat.data.MessageStatus
import com.codebrew.clikat.data.model.api.chat.Image
import kotlinx.android.parcel.Parcelize
import java.io.File

@Parcelize
data class ChatMessageListing(
        var c_id: String?=null,
        var message_id: String?=null,
        var order_id: String?=null,
        var send_to: String?=null,
        var send_by: String?=null,
        var text: String?=null,
        var thumbnail: String?=null,
        var sent_at: String?=null,
        var chat_type: String?=null,
        var isSent: Boolean?=null,
        var original: String?=null,
        var name: String?=null,
        var ownMessage:Boolean?=null,
        var profile_pic: String?=null,
        var oppositionId: String?=null,
        var receiver_created_id: String?=null,
        var sender_created_id:String?=null,
        var type: String? = "1",
        var isFailed: Boolean? = false,
        var userType:String?=null,
        var messageStatus: MessageStatus = MessageStatus.SENT,
        var localId: String? = null,
        var localFile: File? = null,
        var localFileThumbnail: File? = null,
        var image: String? = null,
        var image_url: String? = null,
        var sent_by:String?=null
):Parcelable
{
    constructor() : this("","","","","","","","","",false,"","",
            false,"","","")
}