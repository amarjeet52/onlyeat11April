package com.codebrew.clikat.data.model.dataModel

data class LocationModel(
    val `data`: NewData,
    val message: String,
    val status: Int
)
data class NewData(
    val zoneInformation: MutableList<ZoneInformation>,
    val abn_number: String,
    val address: List<Addres>,
    val business_name: String,
    val id_for_invoice: Any,
    val notification_language: Int,
    val notification_status: Int,
    val user_id: Int,
    val wallet_amount: Int

)
data class ZoneInformation(
    val id: Int,
    val name: String
)
data class Addres(
    val address_line_1: String,
    val address_line_2: String,
    val address_link: String,
    val area_id: Int,
    val city: String,
    val country_code: String,
    val customer_address: String,
    val directions_for_delivery: String,
    val id: Int,
    val is_default: Int,
    val is_deleted: Int,
    val iso: Any,
    val landmark: String,
    val latitude: Double,
    val longitude: Double,
    val name: String,
    val phone_number: String,
    val pincode: String,
    val reference_address: String,
    val user_id: Int
)