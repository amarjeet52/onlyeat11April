package com.codebrew.clikat.data.model.others

data class EditOrderRequest(
    var sectionId: Int? = null,
    var orderId: String? = null,
    var duration: Int? = null,
    var pricing_type:Int?=null,
    var userServiceCharge:Float?=null,
    var handlingAdmin:Double?=null,
  //  var handling_supplier:Float?=null,
    var items: ArrayList<EditProductsItem>? = null,
    var removalItems: ArrayList<String?>? = null
)
