package com.codebrew.clikat.modal

import com.codebrew.clikat.data.model.api.QuestionList
import com.codebrew.clikat.modal.other.VariantValuesBean
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

/*
 * Created by cbl45 on 7/5/16.
 */
class CartInfoServerArray {
    @SerializedName("accessToken")
    @Expose
    var accessToken: String? = null
    @SerializedName("supplierBranchId")
    @Expose
    var supplierBranchId = 0
    @SerializedName("remarks")
    @Expose
    var remarks = ""

    @SerializedName("is_schedule")
    @Expose
    var is_schedule = ""

    @SerializedName("productList")
    @Expose
    var productList: List<CartInfoServer>? = null

    @SerializedName("supplier_id")
    @Expose
    var supplier_id = 0
    @SerializedName("adds_on")
    @Expose
    var addons: List<ProductAddon?>? = null
    var order_day: String? = null
    var order_time: String? = null
    var cartId:String?=null
    var promoationType:String?=null
    var deviceId:String?=null
    var variants: List<VariantValuesBean?>? = null
    @SerializedName("addOn")
    var questionAddonPrice:Float?=null
    var questions:List<QuestionList>?=null

}