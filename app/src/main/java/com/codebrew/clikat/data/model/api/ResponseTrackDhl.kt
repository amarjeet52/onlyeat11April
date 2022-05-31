package com.codebrew.clikat.data.model.api

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

data class ResponseTrackDhl(

        @field:SerializedName("data")
        val data: TrackDhl? = null,

        @field:SerializedName("message")
        val message: String? = null,

        @field:SerializedName("status")
        val status: Int? = null
)

@Parcelize
data class ShipperReference(

        @field:SerializedName("ReferenceID")
        val referenceID: String? = null
) : Parcelable


@Parcelize
data class TrackDhl(

        @field:SerializedName("Status")
        val status: Status? = null,

        @field:SerializedName("ShipmentInfo")
        val shipmentInfo: ShipmentInfo? = null,

        @field:SerializedName("AWBNumber")
        val aWBNumber: String? = null,

        @field:SerializedName("tracking_data")
        val tracking_data: ShipmentInfo? = null,

        @field:SerializedName("token")
        val token:String?=null,

        @field:SerializedName("zoom_email")
        val zoom_email:String?=null,

        @field:SerializedName("order_id")
        var order_id:String?=null
) : Parcelable


@Parcelize
data class Shipper(

        @field:SerializedName("PostalCode")
        val postalCode: String? = null,

        @field:SerializedName("City")
        val city: String? = null,

        @field:SerializedName("CountryCode")
        val countryCode: String? = null
) : Parcelable


@Parcelize
data class ServiceArea(

        @field:SerializedName("Description")
        val description: String? = null,

        @field:SerializedName("ServiceAreaCode")
        val serviceAreaCode: String? = null
) : Parcelable


@Parcelize
data class Consignee(

        @field:SerializedName("PostalCode")
        val postalCode: String? = null,

        @field:SerializedName("City")
        val city: String? = null,

        @field:SerializedName("CountryCode")
        val countryCode: String? = null
) : Parcelable


@Parcelize
data class ShipmentEvent(

        @field:SerializedName("ServiceArea")
        val serviceArea: ServiceArea? = null,

        @field:SerializedName("ServiceEvent")
        val serviceEvent: ServiceEvent? = null,

        @field:SerializedName("Signatory")
        val signatory: String? = null,

        @field:SerializedName("Time")
        val time: String? = null,

        @field:SerializedName("Date")
        val date: String? = null
) : Parcelable


@Parcelize
data class DestinationServiceArea(

        @field:SerializedName("Description")
        val description: String? = null,

        @field:SerializedName("ServiceAreaCode")
        val serviceAreaCode: String? = null
) : Parcelable


@Parcelize
data class OriginServiceArea(

        @field:SerializedName("Description")
        val description: String? = null,

        @field:SerializedName("ServiceAreaCode")
        val serviceAreaCode: String? = null
) : Parcelable


@Parcelize
data class Status(

        @field:SerializedName("ActionStatus")
        val actionStatus: String? = null
) : Parcelable


@Parcelize
data class ShipmentInfo(

        @field:SerializedName("OriginServiceArea")
        val originServiceArea: OriginServiceArea? = null,

        @field:SerializedName("DestinationServiceArea")
        val destinationServiceArea: DestinationServiceArea? = null,

        @field:SerializedName("ShipperName")
        val shipperName: String? = null,

        @field:SerializedName("ShipperAccountNumber")
        val shipperAccountNumber: String? = null,

        @field:SerializedName("ShipperReference")
        val shipperReference: ShipperReference? = null,

        @field:SerializedName("WeightUnit")
        val weightUnit: String? = null,

        @field:SerializedName("Weight")
        val weight: String? = null,

        @field:SerializedName("DlvyNotificationFlag")
        val dlvyNotificationFlag: String? = null,

        @field:SerializedName("Consignee")
        val consignee: Consignee? = null,

        @field:SerializedName("ShipmentEvent")
        val shipmentEvent: ShipmentEvent? = null,

        @field:SerializedName("ConsigneeName")
        val consigneeName: String? = null,

        @field:SerializedName("ShipmentDesc")
        val shipmentDesc: String? = null,

        @field:SerializedName("ShipmentDate")
        val shipmentDate: String? = null,

        @field:SerializedName("GlobalProductCode")
        val globalProductCode: String? = null,

        @field:SerializedName("Shipper")
        val shipper: Shipper? = null,

        @field:SerializedName("Pieces")
        val pieces: String? = null,

        @field:SerializedName("shipment_track")
        val shipmentTrack: List<ShipmentTrackItem?>? = null,

        @field:SerializedName("shipment_track_activities")
        val shipmentTrackActivities: List<ShipmentTrackActivitiesItem?>? = null,

        @field:SerializedName("track_status")
        val trackStatus: Int? = null,

        @field:SerializedName("track_url")
        val trackUrl: String? = null,

        @field:SerializedName("shipment_status")
        val shipmentStatus: Int? = null
) : Parcelable


@Parcelize
data class ServiceEvent(

        @field:SerializedName("Description")
        val description: String? = null,

        @field:SerializedName("EventCode")
        val eventCode: String? = null
) : Parcelable


@Parcelize
data class ShipmentTrackItem(

        @field:SerializedName("courier_company_id")
        val courierCompanyId: Int? = null,


        @field:SerializedName("delivered_to")
        val deliveredTo: String? = null,

        @field:SerializedName("consignee_name")
        val consigneeName: String? = null,

        @field:SerializedName("origin")
        val origin: String? = null,

        @field:SerializedName("destination")
        val destination: String? = null,

        @field:SerializedName("weight")
        val weight: String? = null,

        @field:SerializedName("packages")
        val packages: Int? = null,

        @field:SerializedName("current_status")
        val currentStatus: String? = null,

        @field:SerializedName("id")
        val id: Int? = null,

        @field:SerializedName("awb_code")
        val awbCode: String? = null,

        @field:SerializedName("order_id")
        val orderId: Int? = null
):Parcelable

@Parcelize
data class ShipmentTrackActivitiesItem(

        @field:SerializedName("date")
        val date: String? = null,

        @field:SerializedName("activity")
        val activity: String? = null,

        @field:SerializedName("location")
        val location: String? = null
):Parcelable
