package com.codebrew.clikat.data.model.api

data class ReferralAmountModel(
    val data: ReferalAmt,
    val message: String,
    val status: Int
)

data class ReferalAmt(
        val referalAmount: Float
)