package com.codebrew.clikat.data.model.api

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

data class SocialCommentLikes(
        val data: CommentData,
        val message: String,
        val status: Int
)

data class CommentData(
        val count: Int,
        val list: MutableList<CommentBean>
)

@Parcelize
data class CommentBean(
        var comment: String,
        var id: Int,
        var user_image: String?=null,
        var post_id: Int?=null,
        var user_id: Int,
        var user_name: String
):Parcelable