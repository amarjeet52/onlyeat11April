package com.codebrew.clikat.data.model.api

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class AddCardResponseData(
        @field:SerializedName("transaction-reference")
        val transaction_reference: String? = null,

        @field:SerializedName("notification-mode")
        val notification_mode: String? = null,

        @SerializedName("PaymentURL")
        val PaymentURL: String? = null,

        @SerializedName("payment_id")
        val payment_id: String? = null,

        @SerializedName("payment-url")
        val payment_url: String? = null,

        @field:SerializedName("status")
        val status: Int? = null,

        @field:SerializedName("error-code")
        val error_code: Int? = null,

        @field:SerializedName("error-message")
        val error_message: String? = null,

        @field:SerializedName("customer_payment_id")
        val customer_payment_id: String? = null,

        @field:SerializedName("InvoiceId")
        val InvoiceId: String? = null,

        @field:SerializedName("URI" )
        val url:String?=null,

        @field:SerializedName("rID")
        val rID:String?=null,

        @field:SerializedName("finalUrl")
        val finalUrl:String?=null,

        @field:SerializedName("method")
        val method:String?=null,

        @field:SerializedName("trace")
        val trace:String?=null,

        @field:SerializedName("order")
        val order:CardsItem?=null,

        @field:SerializedName("destinationurl")
        val destinationurl:String?=null,

        @field:SerializedName("cards")
        val cards: ArrayList<CardsItem>? = null,

        @field:SerializedName("track")
        val track: String? = null,

        @field:SerializedName("baseUrl")
        val baseUrl: String? = null,

        @field:SerializedName("storeinfo")
        val storeinfo: Storeinfo? = null,

        @field:SerializedName("returnurl")
        val returnUrl:String?=null,

        @field:SerializedName("redirectUrl")
        val redirectUrl:String?=null

):Parcelable

@Parcelize
data class CardsItem(

        @field:SerializedName("optiontype")
        val optiontype: String? = null,

        @field:SerializedName("img_medium")
        val imgMedium: String? = null,

        @field:SerializedName("card_type")
        val cardType: String? = null,

        @field:SerializedName("card_id")
        val cardId: String? = null,

        @field:SerializedName("url")
        val url: String? = null,

        @field:SerializedName("ref")
        val ref: String? = null,

        @field:SerializedName("sdk_text")
        val sdkText: String? = null
):Parcelable

@Parcelize
data class Storeinfo(

        @field:SerializedName("storeid")
        val storeid: String? = null,

        @field:SerializedName("title")
        val title: String? = null,

        @field:SerializedName("ipn_alert")
        val ipnAlert: String? = null,

        @field:SerializedName("auto_divert")
        val autoDivert: String? = null,

        @field:SerializedName("domain")
        val domain: String? = null,

        @field:SerializedName("logo")
        val logo: String? = null,

        @field:SerializedName("bkash_option")
        val bkashOption: String? = null,

        @field:SerializedName("ipn_url")
        val ipnUrl: String? = null
):Parcelable

data class AddCardResponse(
        val data: AddCardResponseData? = null,
        val message: String? = null,
        val status: Int? = null
)

data class WindCaveResponse(
        val data: Result? = null,
        val message: String? = null,
        val status: Int? = null
)
data class Result(
        val Request: AddCardResponseData? = null
)