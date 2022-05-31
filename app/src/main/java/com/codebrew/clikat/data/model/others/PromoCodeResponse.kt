package com.codebrew.clikat.data.model.others

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

data class PromoCodeResponse(

	@field:SerializedName("data")
	val data: PromoCodeData? = null,

	@field:SerializedName("message")
	val message: String? = null,

	@field:SerializedName("status")
	val status: Int? = null
)

@Parcelize
data class PromoCodeItem(

	@field:SerializedName("firstTime")
	val firstTime: Int? = null,

	@field:SerializedName("endDate")
	val endDate: String? = null,

	@field:SerializedName("promoDesc")
	val promoDesc: String? = null,

	@field:SerializedName("name")
	val name: String? = null,

	@field:SerializedName("discountPrice")
	val discountPrice: Int? = null,

	@field:SerializedName("promoCode")
	val promoCode: String? = null,

	@field:SerializedName("promo_user_subscription_type")
	val promoUserSubscriptionType: String? = null,

	@field:SerializedName("startDate")
	val startDate: String? = null
):Parcelable

data class PromoCodeData(

	@field:SerializedName("count")
	val count: Int? = null,

	@field:SerializedName("list")
	val list: ArrayList<PromoCodeItem>? = null
)
