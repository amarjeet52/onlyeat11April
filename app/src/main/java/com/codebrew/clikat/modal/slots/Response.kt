package com.codebrew.clikat.modal.slots

import android.os.Parcelable
import com.codebrew.clikat.data.model.api.ListItem
import com.codebrew.clikat.data.model.api.orderDetail.TableDetails
import com.codebrew.clikat.modal.BookedTableItem
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize
import java.util.*

data class SuppliersSlotsResponse(

        @field:SerializedName("data")
        val data: SupplierSlots? = null,

        @field:SerializedName("message")
        val message: String? = null,

        @field:SerializedName("status")
        val status: Int? = null
)

data class SuppliersTableSlotsResponse(

        @field:SerializedName("data")
        val data: TableSlotsItem? = null,

        @field:SerializedName("message")
        val message: String? = null,

        @field:SerializedName("status")
        val status: Int? = null
)

@Parcelize
data class SupplierAvailableDatesItem(

        @field:SerializedName("date")
        var date: Date? = null,

        @field:SerializedName("date_order_type")
        val dateOrderType: Int? = null,

        @field:SerializedName("from_date")
        var fromDate: String? = null,

        @field:SerializedName("to_date")
        val toDate: String? = null,

        @field:SerializedName("id")
        val id: Int? = null,

        @field:SerializedName("supplier_id")
        val supplierId: Int? = null,

        @field:SerializedName("supplier_location_id")
        val supplierLocationId: Int? = null,

        @field:SerializedName("status")
        val status: Int? = null,

        @field:SerializedName("day_id")
        val dayId: Int? = null,

        var isWeek: Boolean? = false

) : Parcelable, Comparable<SupplierAvailableDatesItem> {
    override fun compareTo(other: SupplierAvailableDatesItem): Int {
        return date?.compareTo(other.date) ?: 0
    }
}

@Parcelize
data class TableSlotsItem(

        @field:SerializedName("availableTables")
        var availableTables: ArrayList<ListItem?>? = null,

        @field:SerializedName("slots")
        var slots: ArrayList<String>? = null


) : Parcelable


@Parcelize
data class SupplierSlots(

        @field:SerializedName("weeks_data")
        val weeksData: ArrayList<SupplierAvailableDatesItem>? = null,

        @field:SerializedName("phone")
        val phone: String? = null,

        @field:SerializedName("supplier_available_dates")
        val supplierAvailableDates: ArrayList<SupplierAvailableDatesItem>? = null,

        @field:SerializedName("name")
        val name: String? = null,

        @field:SerializedName("supplier_timings")
        var supplierTimings: ArrayList<SupplierTimingsItem?>? = null,

        @field:SerializedName("supplier_slots_interval")
        var supplierSlotsInterval: Int? = null,


        @field:SerializedName("email")
        val email: String? = null,

        @field:SerializedName("startDateTime")
        var startDateTime: String? = null,

        @field:SerializedName("endDateTime")
        var endDateTime: String? = null,

        @field:SerializedName("selectedTable")
        var selectedTable: ListItem? = null
) : Parcelable

@Parcelize
data class SupplierTimingsItem(

        @field:SerializedName("start_time")
        val startTime: String? = null,

        @field:SerializedName("quantity")
        val quantity: Int? = null,

        @field:SerializedName("offset")
        val offset: String? = null,

        @field:SerializedName("date_order_type")
        val dateOrderType: Int? = null,

        @field:SerializedName("price")
        val price: Float? = null,

        @field:SerializedName("end_time")
        val endTime: String? = null,

        @field:SerializedName("id")
        val id: Int? = null,

        @field:SerializedName("date_id")
        val dateId: Int? = null,

        @field:SerializedName("supplier_id")
        val supplierId: Int? = null,

        @field:SerializedName("day_id")
        val dayId: Int? = null,

        @field:SerializedName("supplier_location_id")
        val supplierLocationId: Int? = null,

        @field:SerializedName("status")
        val status: Int? = null
) : Parcelable

