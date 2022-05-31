package com.codebrew.clikat.data.model.others

data class RateInputModel(
    val accessToken: String,
    val comment: String,
    val orderId: String,
    val rating: Int,
    val supplierId: String
)