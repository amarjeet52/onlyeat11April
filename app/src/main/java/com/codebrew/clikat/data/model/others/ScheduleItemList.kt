package com.codebrew.clikat.data.model.others

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class ScheduleItemList (
        var  startDate:String?=null,
        var  endDate:String?=null,
        var  isStatus:Boolean?=null,
        var startTime:String?=null,
        var endTime:String?=null
):Parcelable