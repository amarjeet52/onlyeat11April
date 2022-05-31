package com.codebrew.clikat.module.cart

data class SavedCardModel(
    val data: List<CardData>,
    val message: String,
    val status: Int
)
data class CardData(
        val card_brand: String,
        val card_id: Int,
        val card_number: String,
        val card_payment_id: Int,
        val card_source: String,
        val card_token: String,
        val card_type: String,
        val created_at: String,
        val customer_payment_id: Any,
        val cvc: String,
        val exp_month: Int,
        val exp_year: Int,
        val id: Int,
        val is_default: Int,
        val is_deleted: Int,
        val updated_at: String,
        val user_id: Int
)