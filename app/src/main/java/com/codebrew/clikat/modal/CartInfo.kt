package com.codebrew.clikat.modal

import android.os.Parcelable
import com.codebrew.clikat.data.VendorAppType
import com.codebrew.clikat.data.model.api.QuestionList
import com.codebrew.clikat.data.model.others.AgentCustomParam
import com.codebrew.clikat.modal.other.VariantValuesBean
import kotlinx.android.parcel.Parcelize


@Parcelize
data class CartInfo(

        var productName: String? = null,
        var productDesc: String? = null,
        val currency: String? = null,
        var measuringUnit: String? = null,
        var imagePath: String? = null,
        var price: Float = 0.toFloat(),
        var discount:Int=0,
        var display_price:Float=0.toFloat(),
        var supplierName: String? = null,
        var supplierAddress: String? = null,
        var quantity: Float = 0f,
        var supplierId: Int = 0,
        var categoryId: Int = 0,
        var productSpecialInstructions: String? = null,
        var handlingSupplier: Float = 0.toFloat(),
        var handlingCharges: Float = 0.toFloat(),
        var suplierBranchId: Int = 0,
        var productId: Int = 0,
        var handlingAdmin: Float = 0.toFloat(),
        var isUrgent: Int = 0,
        var urgentValue: Float = 0f,
        var priceType: Int = 0,
        var urgent_type: Int = 0,
        var packageType: Int = 1,
        var is_appointment :String? = null,

        var agentType: Int = 0,

        var agentList: Int = 0,

        // 0 for delivery 1 for pickup
        var deliveryType: Int = 0,

        // 0 for serviceType 1 for productType
        var serviceType: Int = 0,

        var serviceDuration: Int = 0,

        var serviceDurationSum: Float = 0f,

        var deliveryCharges: Float = 0.toFloat(),

        var subCategoryName: String = "",
        var isQuant: Int = 1,
        var hourlyPrice: List<HourlyPrice> = emptyList(),
        var purchasedQuant: Float? = null,
        var prodQuant: Float? = null,
        var avgRating: Float? = 0f,
        var isQuantity: Int? = null,
        var isDiscount: Int? = null,
        var latitude: Double? = null,
        var longitude: Double? = null,
        var radius_price: Float? = null,
        var distance_value: Float? = null,
        var appType: Int? = null,
        //question list
        var question_list: MutableList<QuestionList>? = mutableListOf(),
        //addon data price
        var add_ons: MutableList<ProductAddon?>? = mutableListOf(),
        var add_on_name: String? = null,
        var fixed_price: Float? = null,
        var productAddonId: Long = 0,
        var isStock: Boolean? = true,
        var deliveryMax: Int = 0,
        var isPaymentConfirm: Int = 0,
        var sales_tax: Float? = null,
        var cart_image_upload: Int = 0,
        var order_instructions: Int = 0,
        var perProductLoyalityDiscount:Float?=null,
        var duration:Int?=null,

        // varient list
        var varients: MutableList<VariantValuesBean?>? = mutableListOf(),
        var local_currency:String?=null,
        var is_user_service_charge_flat :String? =null,
        var user_service_charge:Double?=null,
        var currency_exchange_rate:Double?=null,
        var purchase_limit:Float?=null,
        var product_owner_name:String?=null,
        var product_reference_id:String?=null,
        var product_dimensions:String?=null,
        var product_upload_reciept:String?=null,
        var is_out_network:Int?=null,
        var serviceAgentDetail: AgentCustomParam?=null,
        var agentBufferPrice:Double?=null,
        var is_free_delivery:Int?=null,
        var isOpen:Boolean?=null,
        var table_booking_discount:Float?=null
) : Parcelable


@Parcelize
data class ProductAddon(var id: String? = null, var name: String? = null, var price: Float? = null,
                        var type_id: String? = null, var type_name: String? = null, var quantity: Float? = null,
                        var serial_number: Int? = null, var adds_on_type_quantity: Int? = null) : Parcelable
