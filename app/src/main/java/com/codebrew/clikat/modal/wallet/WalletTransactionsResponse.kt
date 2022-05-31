package com.codebrew.clikat.modal.wallet

import com.google.gson.annotations.SerializedName

data class WalletTransactionsResonse(

		@field:SerializedName("data")
		val data: Data? = null,

		@field:SerializedName("message")
		val message: String? = null,

		@field:SerializedName("status")
		val status: Int? = null
)

data class UserDetails(

		@field:SerializedName("country_code")
		val countryCode: String? = null,

		@field:SerializedName("phone_no")
		val phoneNo: String? = null,

		@field:SerializedName("firstname")
		val firstname: String? = null,

		@field:SerializedName("mobile_no")
		val mobileNo: String? = null,

		@field:SerializedName("id")
		val id: Int? = null,

		@field:SerializedName("email")
		val email: String? = null,

		@field:SerializedName("lastname")
		val lastname: String? = null,

		@field:SerializedName("wallet_amount")
		val walletAmount: Float? = null
)

data class Data(

		@field:SerializedName("count")
		val count: Int? = null,

		@field:SerializedName("transactions")
		val transactions: ArrayList<TransactionsItem>? = null,

		@field:SerializedName("userDetails")
		val userDetails: UserDetails? = null
)

data class TransactionsItem(

		@field:SerializedName("by_admin")
		val byAdmin: Int? = null,

		@field:SerializedName("shareDate")
		val shareDate: String? = null,

		@field:SerializedName("card_payment_id")
		val cardPaymentId: String? = null,

		@field:SerializedName("is_add")
		val isAdd: Int? = null,

		@field:SerializedName("amount")
		val amount: Float? = null,

		@field:SerializedName("share_mobile_no")
		val shareMobileNo: String? = null,

		@field:SerializedName("share_user_email")
		val shareUserEmail: String? = null,

		@field:SerializedName("created_at")
		val createdAt: String? = null,

		@field:SerializedName("user_wallet_share_id")
		val userWalletShareId: Int? = null,

		@field:SerializedName("added_deduct_through")
		val addedDeductThrough: Int? = null,

		@field:SerializedName("user_id")
		val userId: Int? = null,

		@field:SerializedName("id")
		val id: Int? = null,

		@field:SerializedName("payment_source")
		val paymentSource: String? = null,

		@field:SerializedName("share_amount")
		val shareAmount: Int? = null,

		@field:SerializedName("share_through")
		val shareThrough: String? = null,

		@field:SerializedName("share_user_name")
		val shareUserName: String? = null,

		@field:SerializedName("comment")
		val comment: String? = null,

		var formattedDate:String?=null
)
