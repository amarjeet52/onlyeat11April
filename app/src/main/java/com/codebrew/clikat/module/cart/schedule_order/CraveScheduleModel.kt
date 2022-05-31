package com.codebrew.clikat.module.cart.schedule_order

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class CraveScheduleModel(val scheduleDate: String,
                              val endDate: String,
                              val type: String) : Parcelable