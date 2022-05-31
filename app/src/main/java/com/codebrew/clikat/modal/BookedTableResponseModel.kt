package com.codebrew.clikat.modal

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

data class BookedTableResponseModel(

        @field:SerializedName("data")
        val data: TableResponseData? = null,

        @field:SerializedName("message")
        val message: String? = null,

        @field:SerializedName("status")
        val status: Int? = null
)
@Parcelize
data class BookedTableItem(

        @field:SerializedName("user_email")
        val userEmail: String? = null,

        @field:SerializedName("branch_id")
        val branchId: Int? = null,

        @field:SerializedName("seating_capacity")
        val seatingCapacity: Int? = null,

        @field:SerializedName("user_name")
        val userName: String? = null,

        @field:SerializedName("branch_name")
        val branchName: String? = null,

        @field:SerializedName("table_number")
        val tableNumber: Int? = null,

        @field:SerializedName("id")
        val id: Int? = null,

        @field:SerializedName("table_id")
        val tableId: Int? = null,

        @field:SerializedName("schedule_date")
        val scheduleDate: String? = null,

        @field:SerializedName("table_name")
        val tableName: String? = null,

        @field:SerializedName("schedule_end_date")
        val scheduleEndDate: String? = null,

        @field:SerializedName("status")
        val status: Int? = null,

        @field:SerializedName("order_id")
        val orderId: Int? = null,

		@field:SerializedName("supplier_id")
        val supplierId: Int? = null,

        @field:SerializedName("user_on_the_way")
        var userOnTheWay: Int? = null,

        @field:SerializedName("user_in_range")
        val userInRange: Int? = null,

        @field:SerializedName("table_booking_price")
        val table_booking_price:Float?=null,

        @field:SerializedName("payment_source")
        val payment_source:String?=null,

        @field:SerializedName("payment_type")
        val payment_type:Int?=null,


        @field:SerializedName("amount")
        val amount:Float?=null
):Parcelable

data class TableResponseData(

        @field:SerializedName("count")
        val count: Int? = null,

        @field:SerializedName("list")
        val list: List<BookedTableItem?>? = null
)
