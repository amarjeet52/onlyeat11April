package com.codebrew.clikat.data.model.api

data class BuySubscriptionModel(
    val data: BuySubscription,
    val message: String,
    val status: Int
)

data class BuySubscription(
    val affectedRows: Int,
    val changedRows: Int,
    val fieldCount: Int,
    val insertId: Int,
    val message: String,
    val protocol41: Boolean,
    val serverStatus: Int,
    val warningCount: Int
)