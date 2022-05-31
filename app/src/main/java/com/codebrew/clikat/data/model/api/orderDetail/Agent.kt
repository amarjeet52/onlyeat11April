package com.codebrew.clikat.data.model.api.orderDetail

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Agent(
        var image: String? = null,
        var agent_created_id: String? = null,
        var country: String? = null,
        var occupation: String? = null,
        var city: String? = null,
        var name: String? = null,
        var phone_number: String? = null,
        var country_code: String? = null,
        var proxy_phone_number :String? =null,
        var state: String? = null,
        var experience: Int? = null,
        var longitude: Double? = null,
        var latitude: Double? = null,
        var email: String? = null,
        var total_review: Float? = null,
        var avg_rating: Float? = null,
        var message_id: String? = null,
        var user_created_id: String? = null,
        var assign_id:String?=null,
        var agent_bio_url:String?=null) : Parcelable