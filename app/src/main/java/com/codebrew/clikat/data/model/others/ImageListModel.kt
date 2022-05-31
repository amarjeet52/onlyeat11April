package com.codebrew.clikat.data.model.others

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class ImageListModel(
        var is_imageLoad: Boolean?=false,
        var _id: String?=null,
        var image: String?=null,
        var isSelected: Boolean?=null,
        var isDeleteImage: Boolean? = null
): Parcelable {
    constructor() : this(false, "","",false,false)
}


