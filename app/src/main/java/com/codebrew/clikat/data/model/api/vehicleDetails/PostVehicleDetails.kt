package com.codebrew.clikat.data.model.api.vehicleDetails

data class PostVehicleDetails(
    val `data`:  List<PostData>,
    val message: String,
    val status: Int
)

data class PostData(
        val id: Int,
        val user_id: Int,
        val name: String,
        val color: String,
        val number_plate: String,
        val is_default: Int,
        val is_deleted: Int,
        val mobile: String
)