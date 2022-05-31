package com.codebrew.clikat.data.model.others

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class ImageCustomParam(var pres_image1:String,var pres_image2:String,var pres_image3:String,
var pres_image4:String,var pres_image5:String): Parcelable {
    constructor() : this("", "","","","")
}