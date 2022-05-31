package com.codebrew.clikat.data.model.others

import com.codebrew.clikat.data.OrderStatus

data class OrderStatusModel(
        var statusTime: String,
        var status: OrderStatus,
        var orderStatus: Double
)