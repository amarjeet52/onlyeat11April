package com.codebrew.clikat.data.model.others

import android.os.Parcel
import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
class SaveCardInputModel (
        var  user_id:String?=null,
        var  card_type:String?=null,
        var  card_number:String?=null,
        var  exp_month:String?=null,
        var  exp_year:String?=null,
        var  card_token:String?=null,
        var  gateway_unique_id:String?=null,
        var  cvc:String?=null,
        var  card_nonce:String?=null,
        var card_holder_name:String?=null,
        var zipCode:String?=null
) : Parcelable



