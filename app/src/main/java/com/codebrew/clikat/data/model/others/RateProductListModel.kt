package com.codebrew.clikat.data.model.others

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
class RateProductListModel (
    var prodName: String?=null,
    var supplierName: String?=null,
    var prodImage: String?=null,
    var product_id: String?=null,
    var supplier_id: String?=null,
    var order_id: String?=null,
    var isRateStatus:Boolean = false,
    var value: String? = null,
    var title: String? = null,
    var reviews: String? = null
): Parcelable