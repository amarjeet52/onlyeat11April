package com.codebrew.clikat.data.model.api

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize
@Parcelize
data class BraintreeTokenResoponse (
        @field:SerializedName("status")
        val status: Int? = null,


        @field:SerializedName("message")
        val message: String? = null,

        @field:SerializedName("data")
        val data: TokenData? = null
    ) : Parcelable


@Parcelize
data class TokenData(
        @field:SerializedName("client_token")
        val client_token: String? = null
): Parcelable