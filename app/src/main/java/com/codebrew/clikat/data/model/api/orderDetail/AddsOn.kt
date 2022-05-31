package com.codebrew.clikat.data.model.api.orderDetail

import android.os.Parcelable
import androidx.annotation.Keep
import kotlinx.android.parcel.Parcelize

@Keep
@Parcelize
data class AddsOn(
        val adds_on_id: Int?=null,
        val adds_on_name: String?=null,
        val adds_on_type_jd: Int?=null,
        val adds_on_type_name: String?=null,
        val cart_id: Int?=null,
        val created_at: String?=null,
        val id: Int?=null,
        val price: Float?=null,
        val quantity: Int?=null,
        val serial_number: Int?=null,
        val updated_at: String?=null
):Parcelable