package com.codebrew.clikat.data.model.api

import com.google.gson.annotations.SerializedName

data class RoadPoints(var snappedPoints: ArrayList<RoadItem> = ArrayList())


data class RoadItem(

        @field:SerializedName("placeId")
        val placeId: String? = null,

        @field:SerializedName("originalIndex")
        val originalIndex: Int? = null,

        @field:SerializedName("location")
        val location: Location? = null
)


data class Location(

        @field:SerializedName("latitude")
        val latitude: Double? = null,

        @field:SerializedName("longitude")
        val longitude: Double? = null
)