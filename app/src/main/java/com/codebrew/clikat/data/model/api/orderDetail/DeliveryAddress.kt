package com.codebrew.clikat.data.model.api.orderDetail

import android.os.Parcelable
import androidx.annotation.Keep
import kotlinx.android.parcel.Parcelize

@Keep
@Parcelize
class DeliveryAddress(
        var address_line_1: String? = null,
        var address_line_2: String? = null,
        var pincode: String? = null,
        var city: String? = null,
        var landmark: String? = null,
        var customer_address: String? = null,
        var address_link: String? = null,
        var reference_address: String? = null,
        var user_name: String? = null,
        var phone_number: String? = null,
        var country_code:String?=null

) : Parcelable