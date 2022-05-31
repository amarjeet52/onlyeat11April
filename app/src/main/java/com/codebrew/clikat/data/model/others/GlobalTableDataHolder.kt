package com.codebrew.clikat.data.model.others

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class GlobalTableDataHolder (
        var isInvitation:String? = null,
        var isScanned:String? = null,
        var invitationAccepted:String? = null,
        var branch_id:String? = null,
        var user_id:String? = null,
        var table_id:String? = null,
        var capacity:String? = null,
        var table_number:String? = null,
        var table_name:String? = null,
        var user_name:String? = null,
        var branch_name:String? = null,
        var date:String? = null,
        var supplier_id:String? = null,
        var booking_id:String? = null
):Parcelable