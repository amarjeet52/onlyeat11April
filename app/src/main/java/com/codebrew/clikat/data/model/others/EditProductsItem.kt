package com.codebrew.clikat.data.model.others

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class EditProductsItem(
    var price: Float? = null,
    var quantity: Float? = null,
    var productName: String? = null,
    var productId: Int? = null,
    var branchId: Int? = null,
    var productDesc: String? = null,
    var imagePath:String?=null,
    var orderPriceId:String?=null,
    var handling_admin:Double?=null
):Parcelable
