package com.codebrew.clikat.data.model.api

import com.codebrew.clikat.data.model.api.orderDetail.OrderHistory

data class OrderListModel(
        val data: OrderData,
        val message: String,
        val status: Int
)

data class OrderData(
        val count: Int,
        val orderHistory: MutableList<OrderHistory>
)

data class DeliveryAddress(
        val address_line_1: String,
        val address_line_2: String,
        val city: String,
        val landmark: String,
        val pincode: String
)