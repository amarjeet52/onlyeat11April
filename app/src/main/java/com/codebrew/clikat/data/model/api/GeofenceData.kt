package com.codebrew.clikat.data.model.api

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class GeofenceData(
    val gateways: List<String>,
    val taxData: ArrayList<DataItem>? = null
):Parcelable