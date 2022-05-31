package com.codebrew.clikat.data.model.api.tap_payment

data class Transaction(
    val amount: Double,
    val asynchronous: Boolean,
    val created: String,
    val currency: String,
    val expiry: Expiry,
    val timezone: String,
    val url: String
)