package com.codebrew.clikat.data.model.api

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class TableListResponse(

        @field:SerializedName("data")
        val data: TableData? = null,

        @field:SerializedName("message")
        val message: String? = null,

        @field:SerializedName("status")
        val status: Int? = null
) : Parcelable

@Parcelize
data class ListItem(

        @field:SerializedName("seating_capacity")
        var seatingCapacity: Int? = null,

        @field:SerializedName("table_number")
        var tableNumber: Int? = null,

        @field:SerializedName("qr_code")
        val qrCode: String? = null,

        @field:SerializedName("id")
        var id: Int? = null,

        @field:SerializedName("supplier_id")
        val supplierId: Int? = null,

        @field:SerializedName("table_name")
        var tableName: String? = null,

		@field:SerializedName("table_booking_price")
		val table_booking_price:Float?=null,

        @field:SerializedName("calculatedPrice")
        var calculated_table_price:Float?=null,

        var requestedFrom: String? = null // for purpose to send data and avoid crash
) : Parcelable

@Parcelize
data class TableData(

        @field:SerializedName("count")
        val count: Int? = null,

        @field:SerializedName("list")
        val list: List<ListItem?>? = null
) : Parcelable
