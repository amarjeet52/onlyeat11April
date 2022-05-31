package com.codebrew.clikat.modal.other

import android.os.Parcelable
import com.codebrew.clikat.data.model.api.AddsOn
import com.codebrew.clikat.data.model.api.QuestionList
import com.codebrew.clikat.data.model.others.AgentCustomParam
import com.codebrew.clikat.modal.HourlyPrice
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize
import kotlinx.android.parcel.RawValue

data class ProductDetailModel(


        /**
         * status : 200
         * message : Success
         * data : {"supplier_image":null,"supplier_id":1,"supplier_name":"apurva","ic_splash":"http://45.232.252.46:8081/clikat-buckettest/winter-workshop-9v8qbMs.jpg","fixed_price":"9.8","display_price":"10","bar_code":"0","sku":"1","is_package":0,"price_type":0,"product_desc":"ssss","name":"New Chai","price":"9.8","handling_admin":0,"handling_supplier":0,"urgent_value":90,"can_urgent":1,"urgent_type":1,"delivery_charges":0,"id":18,"measuring_unit":"gram","image_path":["http://192.168.102.52:8081/clikat-buckettest/sweetpotatohGbJz4.jpg","http://192.168.102.52:8081/clikat-buckettest/download(3)GmJA5e.jpg","http://192.168.102.52:8081/clikat-buckettest/lady-finger-500x500LFVCbn.png","http://192.168.102.52:8081/clikat-buckettest/download(3)yHKmeS.jpg"],"variants":[{"variant_name":"size","variant_values":[{"product_name":"testing bread","product_desc":"fdgfgsfg","measuring_unit":"gram","variant_value":"m","variant_name":"size","bar_code":"345","product_id":34},{"product_name":"testing bread","product_desc":"fdgfgsfg","measuring_unit":"gram","variant_value":"l","variant_name":"size","bar_code":"345","product_id":34},{"product_name":"testing bread","product_desc":"fdgfgsfg","measuring_unit":"gram","variant_value":"2xl","variant_name":"size","bar_code":"345","product_id":33}]},{"variant_name":"color","variant_values":[{"product_name":"testing bread","product_desc":"fdgfgsfg","measuring_unit":"gram","variant_value":"#ff0000","variant_name":"color","bar_code":"345","product_id":33}]}]}
         */

        var status: Int = 0,
        var message: String? = null,
        var data: ProductDataBean? = null
)

@Parcelize
data class ProductDataBean(
        var similarProduct: MutableList<ProductDataBean>? = null,
        var orderPos: Int? = null,
        var is_variant: Int? = null,
        var self_pickup: Int? = null,
        var is_scheduled: Int? = null,
        var isOpen: Boolean? = null,
        val base_delivery_charges: Float? = null,
        var product_id: Int? = null,
        var add_on_name: String? = null,
        var sbapaid: Int? = null,
        var supplier_logo: String? = null,
        var fixed_quantity: Float? = null,
        var quantity: Float? = null,
        var item_unavailable: String? = null,
        var purchased_quantity: Float? = null,
        var purchase_limit:Float?=null,
        var sub_category_name: String? = null,
        var category_flow: String? = null,
        var is_user_service_charge_flat :String? =null,
        var user_service_charge:Double?=null,
        var discount: Int? = null,
        var pricing_type: Int? = null,
        var availability: Int? = null,
        var house_cleaning_price: Int? = null,
        var beauty_saloon_price: Int? = null,
        var detailed_sub_category_id: Int? = null,
        var detailed_name: String? = null,
        var sub_category_id: Int? = null,
        var price1: Int? = null,
        var cate_name: String? = null,
        var is_appointment :String? = null,
        var category_name: String? = null,
        var min_order: Int? = null,
        var prod_quantity: Float? = 1f,
        var productAddonId: Long? = null,
        var branch_name: String? = null,
        var supplier_image: String? = null,
        var supplier_id: Int? = null,
        var delivery_max_time: Int? = null,
        var delivery_min_time: Int? = null,
        var supplier_branch_id: Int? = null,
        var supplier_name: String? = null,
        var offerValue: String?=null,
        var is_dine_in:Int?=null,
        var supplier_address: String? = null,
        var logo: String? = null,
        var brand_name: String? = null,
        var fixed_price: String? = null,
        var display_price: String? = null,
        var bar_code: String? = null,
        var sku: String? = null,
        var is_package: Int? = null,
        var price_type: Int? = null,
        var product_desc: String? = null,
        var product_name: String? = null,
        var product_owner_name:String?=null,
        var name: String? = null,
        var price: String? = null,
        var handling_admin: Float? = null,
        var handling_supplier: Float? = null,
        var order_price_id: String? = null,
        var urgent_value: Float? = null,
        var can_urgent: Int? = null,
        var urgent_type: Int? = null,
        var delivery_charges: Float? = null,
        var id: Int? = null,
        var agent_list: Int? = null,
        var measuring_unit: String? = null,
        var image_path: @RawValue Any? = null,
        var variants: List<VariantsBean>? = null,
        var rating: List<RatingBean>? = null,
        @SerializedName("hourly_price", alternate = ["hourlyPrice"])
        var hourly_price: List<HourlyPrice>? = null,
        var netPrice: Float? = null,
        var productSpecialInstructions :String? = null,
        var netDiscount: Float? = null,
        var total_rating: Int? = null,
        var total_reviews: Float? = null,
        var avg_rating: Float? = 0.0f,
        var sales_tax: Float? = 0.0f,
        var is_quantity: Int? = null,
        var is_agent: Int? = null,
        var is_product: Int? = null,
        var duration: Int? = null,
        var category_id: Int? = null,
        var is_favourite: Int? = null,
        var interval_flag: Int? = null,
        var interval_value: Int? = null,
        var required_day: Int? = null,
        var required_hour: Int? = null,
        var distance_value: Float? = null,
        var is_free_delivery:Int?=null,
        var minimum_order_amount_for_free_delivery:Int?=null,
        var radius_price: Float? = null,
        var latitude: Double? = null,
        var longitude: Double? = null,
        var cart_image_upload: Int? = 0,
        var order_instructions: Int? = 0,
        var order: Int? = null,
        var adds_on: List<AddsOn?>? = null,
        //self introduce variable
        var serviceDuration: Int? = null,
        val p_price: String? = null,
        val parent_id: Int? = null,
        var is_question: Int? = null,
        var type: Int? = null,
        //selected varient
        var prod_variants: List<VariantValuesBean>? = null,
        var selectQuestAns: MutableList<QuestionList>? = null,
        var isExpand: Boolean? = false,
        var payment_after_confirmation: Int? = null,
        val recipe_pdf: String? = null,
        val is_rated: Int? = null,
        val parent_category_id: Int? = null,
        var detailed_sub_name: String? = null,
        var return_data: ArrayList<RatingBean>? = null,
        var isEdit: Boolean? = true,
        var perProductLoyalityDiscount: Float? = null,
        var local_currency: String? = null,
        var currency_exchange_rate: Double? = null,
        var country_of_origin: String? = null,
        var size_chart_url: String? = null,
        var isSelected: Boolean? = false,
        var supplierBranchName:String?=null,
        var distance:Float?=null,
        var is_subscription_required:Int?=null,
        var product_reference_id:String?=null,
        var product_dimensions:String?=null,
        var product_upload_reciept:String?=null,
        var is_out_network:Int?=null,
        var agentDetail:AgentCustomParam?=null,
        var agentBufferPrice:Double?=null,
        var allergy_description:String?=null,
        var is_allergy_product:String?=null,
        var isSupplier:Boolean?=false,
        var table_booking_discount:Float?=null,
        var cate_image:String?=null,
        var isAddShow:Boolean?=null
) : Parcelable

@Parcelize
class VariantsBean(
        var variant_name: String? = null,
        val variant_type: Int? = null,
        var variant_values: List<VariantValuesBean>? = null
) : Parcelable

@Parcelize
data class VariantValuesBean(

        /**
         * product_name : testing bread
         * product_desc : fdgfgsfg
         * measuring_unit : gram
         * variant_value : m
         * variant_name : size
         * bar_code : 345
         * product_id : 34
         */


        var product_name: String? = null,
        var product_desc: String? = null,
        var measuring_unit: String? = null,
        var variant_value: String? = null,
        var variant_name: String? = null,
        var bar_code: String? = null,
        var filter_product_id: String? = null,
        var product_id: Int? = null,
        var variant_id: Int? = null,
        var filterstatus: Int? = 0,
        var variant_type: Int? = null,
        var isStatus: Boolean? = null,
        var unid: Int? = null,
        var isVisiblity_status: Boolean? = null) : Parcelable

@Parcelize
data class RatingBean(
        /**
         * id : 447
         * created_on : 26 March 2019
         * firstname : Ajit patra
         * lastname :
         * user_image : http://45.232.252.46:8081/clikat-buckettest/IMG_1552423231474_3001642941767089064ZpXJ8O.jpg
         * reviews : lkklmnas jasb
         * value : 1
         * title : ,llll
         */

        var id: Int? = null,
        var created_on: String? = null,
        var firstname: String? = null,
        var lastname: String? = null,
        var user_image: String? = null,
        @SerializedName("reviews", alternate = ["comment"])
        var reviews: String? = null,
        @SerializedName("value", alternate = ["rating"])
        var value: Int? = null,
        var title: String? = null,
        var reason: String? = null,
        var product_id: String? = null,
        var status: Int? = null
) : Parcelable

@Parcelize
data class HourlyPriceBean(
        /**
         * min_hour : 1
         * max_hour : 2
         * price_per_hour : 50
         */

        var min_hour: Int? = null,
        var max_hour: Int? = null,
        var price_per_hour: Float? = null
) : Parcelable
