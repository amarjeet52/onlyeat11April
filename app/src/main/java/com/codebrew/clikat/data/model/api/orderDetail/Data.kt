package com.codebrew.clikat.data.model.api.orderDetail

import android.os.Parcelable
import androidx.annotation.Keep
import kotlinx.android.parcel.Parcelize

@Keep
@Parcelize
data class Data(
        val orderHistory: List<OrderHistory>?=null
):Parcelable