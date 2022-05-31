package com.codebrew.clikat.data.model.api.distance_matrix


import androidx.annotation.Keep

@Keep
data class DistanceMatrix(
    val distance: Double,
    var duration:String,
    val supplierId: Int?=null
)