package com.codebrew.clikat.modal.other

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import java.util.*

@Parcelize
data class FilterVarientData (

    var catId: Int,

    var minPrice: Int,

    var maxPrice: Int,

    var sortBy: String,

    var areadId: Int,

    var isAvailability: Boolean,

    var isDiscount: Boolean,

    var subCatId: MutableSet<Int>,

    var catNames:ArrayList<String>,

    var supplierId :ArrayList<String>,

    var varientID: MutableSet<Int> ,

    var brandId: MutableSet<Int>,

    var isFilterApply:Boolean

): Parcelable
{
    constructor() : this(0,0,0,"",0,false,false, mutableSetOf(), arrayListOf()
    , arrayListOf(), mutableSetOf(), mutableSetOf(),false)
}
