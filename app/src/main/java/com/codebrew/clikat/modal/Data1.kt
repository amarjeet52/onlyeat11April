package com.codebrew.clikat.modal

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

/*
 * Created by cbl80 on 26/4/16.
 */

@Parcelize
data class Data1(
        @SerializedName("id")
        var id: Int? = null,

        @SerializedName("access_token", alternate = ["accessToken"])
        var access_token: String? = null,
        var email: String? = null,
        var mobile_no: String? = null,
        @SerializedName("firstname", alternate = ["name"])
        var firstname: String? = "",
        var lastname: String? = "",
        @SerializedName("user_created_id", alternate = ["userCreatedId"])
        var user_created_id: String? = null,
        var otp: Int? = 0,
        var user_image: String? = "",
        var otp_verified: Int? = null,
        var notification_status: Int? = null,
        var notif_status: Boolean? = false,
        var fbId: String? = "",
        var referral_id: String? = "",
        var scheduleOrders: Int? = 0,
        var customer_payment_id: String? = "",
        var google_access_token: String? = "",
        var documents: String? = "",
        var message_id: String? = "",
        var country_code: String? = null,
        var isAlreadyRegistered: String? = "0",
        var abn_number: String? = null,
        var business_name: String? = null,
        var authnet_profile_id:String?=null,
        var is_subscribed:Int?=0,
        var id_for_invoice:String?=null
) : Parcelable

data class LoginCreds(
        var biometricLoginEmail: String?=null,
        var biometricPassword:String?=null

)