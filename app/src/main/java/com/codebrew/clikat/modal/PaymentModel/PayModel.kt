package com.codebrew.clikat.modal.PaymentModel

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class PayModel(
    val body: Body,
//    val headers: Headers,
//    val request: Request,
    val statusCode: Int
):Parcelable
@Parcelize
 class Body(
        val merchant: String,
        val result: String,
        val session: Session,
        val successIndicator: String
):Parcelable
@Parcelize
 class Session(
        val id: String,
        val updateStatus: String,
        val version: String
):Parcelable