package com.codebrew.clikat.data.model.api.tap_payment

data class Data(
    val amount: Double,
    val api_version: String,
    val card_threeDSecure: Boolean,
    val currency: String,
    val customer: Customer,
    val description: String,
    val id: String,
    val live_mode: Boolean,
    val merchant_id: String,
    val metadata: Metadata,
    val method: String,
    val `object`: String,
    val post: Post,
    val receipt: Receipt,
    val redirect: Redirect,
    val reference: Reference,
    val response: Response,
    val save_card: Boolean,
    val source: Source,
    val statement_descriptor: String,
    val status: String,
    val threeDSecure: Boolean,
    val transaction: Transaction
)