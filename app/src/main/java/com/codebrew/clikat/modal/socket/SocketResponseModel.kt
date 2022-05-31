package com.codebrew.clikat.modal.socket

data class SocketResponseModel (
    val address: List<CblUserOrderAddress>,
    val client_secret_key: String,
    val latitude: Double,
    val bearing: Float,
    val longitude: Double,
    val order_id: String,
    val status: Int,
    val user_id: String,
    var estimatedTimeInMinutes : String?=null)


data class CblUserOrderAddress (
        val address_line_1: String,
        val address_line_2: String,
        val address_link: String,
        val city: String,
        val customer_address: String,
        val directions_for_delivery: String,
        val id: Int,
        val is_deleted: Int,
        val landmark: String,
        val latitude: Double,
        val longitude: Double,
        val name: String,
        val order_id: Int,
        val pincode: String,
        val type: Int

)