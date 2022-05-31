package com.codebrew.clikat.data.model.api

import com.google.gson.annotations.SerializedName

data class SavedData(
        val status: Int,
        val message: String,
        val data: ArrayList<SavedCardList>,
        val has_more: Boolean,
        val url: String
)

data class SavedObj(
        val data: SavedData,
        val message: String,
        val status: Int
)

data class SavedCardList(
        val token: String,
        val customerId: String,
        val number: String,
        val type: String,
        val document: String,
        val address: Any,
        val address_city: Any,
        val address_country: Any,
        val address_line1: Any,
        val address_line1_check: Any,
        val address_line2: Any,
        val address_state: Any,
        val address_zip: Any,
        val address_zip_check: Any,
        val brand: String,
        val country: String,
        val customer: String,
        val cvc_check: String,
        val dynamic_last4: Any,
        val exp_month: Int,
        val exp_year: Int,
        val fingerprint: String,
        val funding: String,
        val id: String,
        val expirationDate: String,
        val authnet_payment_profile_id: String,
        val authnet_profile_id: String,
        @SerializedName("last4", alternate = ["last_4"])
        val last4: String,
        val name: String,
        val tokenization_method: Any,
        @SerializedName("card_number", alternate = ["cardNumber"])
        val card_number: String
)