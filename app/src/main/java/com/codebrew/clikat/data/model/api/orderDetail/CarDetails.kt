package com.codebrew.clikat.data.model.api.orderDetail

import android.os.Parcelable
import androidx.annotation.Keep
import kotlinx.android.parcel.Parcelize

@Keep
@Parcelize
class CarDetails (
    var id: Int? = 0,
  var user_id: Int? = 0,
    var name: String? = null,
   var color: String? = null,
   var number_plate: String? = null,
  var is_default: Int? = 0,
    var is_deleted: Int? = 0,
   var mobile : String ? = null
) : Parcelable