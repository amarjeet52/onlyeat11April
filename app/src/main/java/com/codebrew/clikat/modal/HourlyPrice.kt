package com.codebrew.clikat.modal

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class HourlyPrice (
    var min_hour: Int?=null,
    var max_hour: Int?=null,
    var price_per_hour: Float?=null,
    var discount_price:Float?=null
):Parcelable