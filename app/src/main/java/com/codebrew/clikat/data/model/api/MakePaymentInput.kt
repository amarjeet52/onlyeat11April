package com.codebrew.clikat.data.model.api

data class MakePaymentInput(
    val card_id: String?=null,
    val currency: String?=null,
    val customer_payment_id: String?=null,
    val gateway_unique_id: String?=null,
    val languageId: Int?=null,
    val order_id: String?=null,
    val payment_token: String?=null,
    val payment_type: Int?=null
)