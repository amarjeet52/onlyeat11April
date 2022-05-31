package com.codebrew.clikat.data.model.api

data class ReferralListing(
    val data: ReferralList,
    val message: String,
    val status: Int
)
data class ReferalData(
        val country_code: String,
        val email: String,
        val firstname: String,
        val given_price: Float,
        val lastname: String,
        val mobile_no: String,
        val receive_price: Float,
        val user_image: String
)

data class ReferralList(
        val referalData: MutableList<ReferalData>
)