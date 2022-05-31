package com.codebrew.clikat.data.model.api

import android.os.Parcelable
import com.codebrew.clikat.data.model.others.ImageListModel
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class PostsResponseModel(

        @field:SerializedName("data")
        val data: ResponseData? = null,

        @field:SerializedName("message")
        val message: String? = null,

        @field:SerializedName("status")
        val status: Int? = null
) : Parcelable


@Parcelize
data class ResponseData(

        @field:SerializedName("count")
        val count: Int? = null,

        @field:SerializedName("list")
        val list: List<PostItem?>? = null
) : Parcelable

@Parcelize
data class PostItem(

        @field:SerializedName("total_comments")
        var totalComments: Int? = null,

        @field:SerializedName("post_images")
        val postImages: MutableList<ImageListModel?>? = null,

        @field:SerializedName("user_email")
        val userEmail: String? = null,

        @field:SerializedName("heading")
        val heading: String? = null,

        @field:SerializedName("user_name")
        val userName: String? = null,

        @field:SerializedName("description")
        val description: String? = null,

        @field:SerializedName("total_likes")
        var totalLikes: Int? = null,

        @field:SerializedName("product_name")
        val productName: String? = null,

        @field:SerializedName("branch_email")
        val branchEmail: String? = null,

        @field:SerializedName("branch_id")
        val branchId: Int? = null,

        @field:SerializedName("already_like")
        var alreadyLike: Int? = null,

        @field:SerializedName("product_id")
        val productId: Int? = null,

        @field:SerializedName("branch_name")
        val branchName: String? = null,

        @field:SerializedName("supplier_email")
        val supplierEmail: String? = null,

        @field:SerializedName("id")
        val id: Int? = null,

        @field:SerializedName("supplier_name")
        val supplierName: String? = null,

        @field:SerializedName("supplier_id")
        val supplierId: Int? = null,

        @field:SerializedName("created_at")
        val createdAt: String? = null,

        @field:SerializedName("user_image")
        val userImage: String? = null,

        @field:SerializedName("user_id")
        val userId: String? = null,

        var formattedDate: String? = null


) : Parcelable
