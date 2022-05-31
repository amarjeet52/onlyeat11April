package com.codebrew.clikat.data.model.others

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class MapVehicleInputParam
(var id:String?=null,var modelNo: String? = null,var color:String?=null, var plateNo: String? = null)
    : Parcelable