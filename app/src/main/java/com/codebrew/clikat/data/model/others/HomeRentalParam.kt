package com.codebrew.clikat.data.model.others

import android.os.Parcelable
import androidx.annotation.Keep
import kotlinx.android.parcel.Parcelize

@Parcelize
@Keep
data class HomeRentalParam(var from_latitude: Double, var from_longitude: Double,var to_latitude: Double, var to_longitude: Double, var booking_from_date: String,
                           var booking_to_date: String, var driveType: Int, var from_address:String, var startEnd:String
                           , var cartId:String, var totalAmt:String, var to_address:String) : Parcelable
{
    constructor() : this(0.0,0.0,0.0,0.0,"","",0,"",""
    ,"","","")
}