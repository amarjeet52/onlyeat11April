package com.codebrew.clikat.data.model.others

data class BuySubcripInput(
    var action: Int?=null,
    val benefit_type: String?=null,
    val currency: String?=null,
    val gateway_unique_id: String?=null,
    val paymentType: Int?=null,
    var payment_token: String?=null,
    val price: Float?=null,
    val subscription_plan_id: Int?=null,
    val type: String?=null,
    var end_date: String?=null,
    var renew_id: Int?=null,
    var authnet_payment_profile_id: String? = null,
    var authnet_profile_id: String? = null,
     var cvt: String? = null,
    var cvc: String? = null,
    var cardHolderName: String? = null,
    var cp: String?= null,
    var expMonth: String?= null,
    var expYear: String? = null,
    var customer_payment_id: String? = null
)