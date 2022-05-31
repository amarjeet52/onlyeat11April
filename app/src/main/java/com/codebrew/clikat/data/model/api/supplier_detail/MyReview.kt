package com.codebrew.clikat.data.model.api.supplier_detail

import android.os.Parcelable
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize
import javax.annotation.Generated
@Parcelize
data class MyReview (

    @SerializedName("comment")
    @Expose
    var comment: String? = null,

    @SerializedName("rating")
    @Expose
    var rating: Float? = null,

    @SerializedName("firstname")
    @Expose
    var firstname: String? = null,

    @SerializedName("user_image")
    @Expose
    var userImage: String? = null

):Parcelable