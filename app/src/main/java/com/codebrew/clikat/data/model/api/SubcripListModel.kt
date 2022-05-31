package com.codebrew.clikat.data.model.api

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

data class SubcripListModel(
        val data: SubcripData,
        val message: String,
        val status: Int
)

data class SubcripData(
        val count: Int,
        val list: MutableList<SubcripModel>
)

@Parcelize
data class SubcripModel(
        val benefitCount: Int?=null,
        val min_order_amount:Double?=null,
        val benefits: MutableList<Benefit>?=null,
        val created_at: String?=null,
        val description: String?=null,
        val id: Int?=null,
        val is_blocked: String?=null,
        val price: String?=null,
        val title: String?=null,
        val type: Int?=null,
        val is_subscribed: Int?=null,
        var benefitStatus:Boolean?=null,
        var subscription_plan:String?=null,
        var subscription_plan_short:String?=null,
        val updated_at: String?=null,
        var updated_start_date:String?=null,
        var update_end_date:String?=null,
        val benefit_type: String?=null,
        val end_date: String?=null,
        val image: String?=null,
        val is_cancelled: String?=null,
        val is_deleted: String?=null,
        val payment_id: String?=null,
        val payment_source: String?=null,
        val payment_status: String?=null,
        val renew_id: Int?=null,
        val start_date: String?=null,
        val status: String?=null,
        val subscription_id: String?=null,
        val subscription_plan_id: Int?=null,
        val transaction_id: String?=null,
        val user_id: Int?=null
):Parcelable

@Parcelize
data class Benefit(
        val benefit_id: String?=null,
        val benefit_type: String?=null,
        val created_at: String?=null,
        val description: String?=null,
        val id: Int?=null,
        val is_blocked: String?=null,
        val plan_id: Int?=null,
        val title: String?=null,
        val updated_at: String?=null
):Parcelable