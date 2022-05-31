package com.codebrew.clikat.data.model.others


data class ReturnProductModel(
        var order_price_id: String,
        var product_id: String,
        var reason: String,
        var refund_to_wallet:Int
)