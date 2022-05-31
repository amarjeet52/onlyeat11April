package com.codebrew.clikat.data.model.api.supplier_detail

import android.os.Parcelable
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class ReviewList(

        @SerializedName("comment")
        var comment: String? = null,

        @SerializedName("rating")
        var rating: Float? = null,

        @SerializedName("firstname")
        var firstname: String? = null,
        @SerializedName("user_image")
        var userImage: String? = null

) : Parcelable