package com.codebrew.clikat.modal

import android.os.Parcel
import android.os.Parcelable
import android.os.Parcelable.Creator
import com.codebrew.clikat.data.model.api.QuestionList
import com.codebrew.clikat.modal.other.VariantValuesBean
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

/*
 * Created by cbl45 on 7/5/16.
 */
@Parcelize
class CartInfoServer (

    @SerializedName("productId")
    @Expose
    var productId: String? = null,

    @SerializedName("quantity")
    @Expose
    var quantity: Float? = null,
    @SerializedName("handling_admin")
    @Expose
    var handlingAdmin: Float? = null,
    var tax: Float? = null,

    @SerializedName("delivery_charge")
    var deliveryCharges:Float?=null,
    var name: String? = null,
    var product_desc: String? = null,
    var price:Float?=null,
    var fixed_price:Float?=null,
    var subCatName: String? = null,
    @SerializedName("handling_supplier")
    @Expose
    var handlingSupplier: Float? = null,
    @SerializedName("price_type")
    @Expose
    var pricetype :Int?=null,
    @SerializedName("supplier_branch_id")
    @Expose
    var supplier_branch_id :Int?=null,
    @SerializedName("supplier_id")
    @Expose
    var supplier_id:Int?=0,
    var agent_type :Int?=null,
    var agent_list:Int?=null,
    var category_id:Int?=null,
    var deliveryType:Int?=null,
    @SerializedName("add_ons")
    @Expose
    var add_ons: List<ProductAddon?>? = null,
    var variants: List<VariantValuesBean?>? = null,
    var question_list: List<QuestionList?>? = null,
    @SerializedName("type")
    @Expose
    var appType:Int?=null,
    var duration:Int?=null,
    var isPaymentConfirm:Int?=null,
    var perProductLoyalityDiscount:Float?=null,
    var product_owner_name:String?=null,

    @SerializedName("special_instructions")
    var special_instructions: String? = null,


    var product_reference_id:String?=null,
    var product_dimensions:String?=null,
    var product_upload_reciept:String?=null,
    var agentBufferPrice:Double?=null

):Parcelable