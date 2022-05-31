package com.codebrew.clikat.data.model.api.orderDetail

import android.os.Parcelable
import androidx.annotation.Keep
import kotlinx.android.parcel.Parcelize

@Keep
@Parcelize
data class OrderDetailModel(
        val data: Data?=null,
        val message: String?=null,
        val status: Int?=null
):Parcelable