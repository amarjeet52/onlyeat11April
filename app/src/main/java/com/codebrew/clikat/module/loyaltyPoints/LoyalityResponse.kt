package com.codebrew.clikat.module.loyaltyPoints

data class LoyalityResponse(
    val `data`: List<DataLoyality>,
    val message: String,
    val status: Int
)
data class DataLoyality(
        val created_at: String,
        val id: Int,
        val image: String,
        val is_deleted: Int,
        val loyality_description_arabic: String,
        val loyality_description_english: String
)