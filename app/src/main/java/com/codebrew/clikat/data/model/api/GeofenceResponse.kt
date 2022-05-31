package com.codebrew.clikat.data.model.api

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

data class GeofenceResponse(

    val data: GeofenceItem,
    val message: String,
    val status: Int,
    val success: Int
)
@Parcelize
data class GeofenceItem(
        val gateways: ArrayList<String>? = null,
        val taxData: ArrayList<DataItem>? = null
):Parcelable

@Parcelize
data class DataItem(
        val id: String? = null,
        var status:Boolean?=null,
        val tax: String? = null,
        val is_multiple: String? = null,
        val max_adds_on: String? = null,
        val min_adds_on: String? = null,
        val name: String? = null,
        val price: Float? = null,
        val type_id: String? = null,
        val serial_number: Int?=null,
        val type_name: String? = null,
        var payment_gateways:ArrayList<String>?=null
) : Parcelable

