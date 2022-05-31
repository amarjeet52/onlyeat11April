package com.codebrew.clikat.data.model.api.vehicleDetails

data class DeleteVehicleDetails(
    val `data`:  DeleteData,
    val message: String,
    val status: Int
)

data class DeleteData(
        val id: Int,
        val user_id: Int,
        val name: String,
        val color: String,
        val number_plate: String,
        val is_default: Int,
        val is_deleted: Int,
        val mobile: String
)