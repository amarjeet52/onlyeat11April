package com.codebrew.clikat.data.model.api.vehicleDetails

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize


data class VehicalDetailsExample(
    val `data`: List<VehicleData>,
    val message: String,
    val status: Int
)

data class VehicleData(
        val color: String,
        val id: Int,
        val is_default: Int,
        val is_deleted: Int,
        val name: String,
        val number_plate: String,
        val user_id: Int
)

