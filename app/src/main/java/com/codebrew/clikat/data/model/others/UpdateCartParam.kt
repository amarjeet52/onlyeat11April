package com.codebrew.clikat.data.model.others

data class UpdateCartParam(
    var accessToken: String?=null,
    var addOn: Int?=null,
    var cartId: Int?=null,
    var currencyId: String?=null,
    var day: Int?=null,
    var deliveryCharges: String?=null,
    var deliveryDate: String?=null,
    var deliveryId: String?=null,
    var deliveryTime: String?=null,
    var deliveryType: String?=null,
    var delivery_max_time: String?=null,
    var handlingAdmin: String?=null,
    var handlingSupplier: String?=null,
    var order_day: String?=null,
    var order_time: String?=null,
    var languageId: String?=null,
    var minOrderDeliveryCrossed: Int?=null,
    var netAmount: Double?=null,
    var questions: List<Any>?=null,
    var urgentPrice: String?=null
)