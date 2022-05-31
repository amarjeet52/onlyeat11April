package com.codebrew.clikat.modal.other

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class CategoryListModel(
        val data: Data? = null,
        val message: String? = null,
        val status: Int? = null
) : Parcelable


@Parcelize
data class BannersListModel(
        val data: List<TopBanner>? = null,
        val message: String? = null,
        val status: Int? = null
) : Parcelable

@Parcelize
data class Data(
        val brands: List<Brand>? = null,
        val english: ArrayList<English>? = null,
        val supplier_tags: ArrayList<English>? = null,
        val languageList: List<Language>? = null,
        val recentActivity: MutableList<ProductDataBean>? = null,
        val topBanner: List<TopBanner>? = null,
        val user_wallet_amount:String?=null
) : Parcelable

@Parcelize
data class Brand(
        val id: Int? = null,
        val image: String? = null,
        val name: String? = null

) : Parcelable


@Parcelize
data class English(
        val agent_list: Int? = null,
        val category_flow: String? = null,
        val description: String? = null,
        val distance: Double? = null,
        val icon: String? = null,
        var id: Int? = null,
        var categoryName:String?=null,
        var imageId:Int?=null,
        val image: String? = null,
        val tag_image:String?=null,
        val is_agent: Int? = null,
        val is_quantity: Int? = null,
        val latitude: Double? = null,
        val longitude: Double? = null,
        var name: String? = null,
        val new_order: Int? = null,
        val order: Int? = null,
        var screenType: String? = null,
        val sub_category: List<SubCategory>? = null,
        var supplier_branch_id: Int? = null,
        val supplier_placement_level: Int? = null,
        var type: Int? = null,
        var supplier_id: Int? = null,
        var cart_image_upload: Int? = null,
        var is_dine: Int? = null,
        var is_assign: Int? = null,
        var is_question: Int? = null,
        var supplierName: String? = null,
        var order_instructions: Int? = null,
        val parent_id: Int? = null,
        var payment_after_confirmation: Int? = null,
        val tax: Float? = null,
        var terminology: String? = null,
        var menu_type:String?=null,
        var isSelected : Boolean = false

) : Parcelable

@Parcelize
data class TopBanner(
        val banner_type: Int? = null,
        val branch_id: Int? = null,
        val category_flow: String? = null,
        val category_id: Int? = null,
        val category_name: String? = null,
        val category_order: Int? = null,
        val distance: Double? = null,
        val id: Int? = null,
        val type: Int? = null,
        val name: String? = null,
        val supplier_name: String? = null,
        var bannerImage: Int? = null,
        var pickupImage: String? = null,
        var phone_image: String? = null,
        var isEnabled: Boolean = true,
        val supplier_id: Int? = null,
        val is_subcategory: Int? = null,
        val supplier_placement_level: Int? = null,
        val flow_banner_type: String? = null,
        val website_image: String? = null,
        val cart_image_upload: Int? = null,
        val is_assign: Int? = null,
        val is_question: Int? = null,
        val order_instructions: Int? = null,
        val parent_id: Int? = null,
        val payment_after_confirmation: Int? = null,
        val tax: Float? = null,
        val terminology: String? = null,
        var isWebsite: Boolean? = null
) : Parcelable

@Parcelize
data class SubCategory(val is_assign: Int? = null,
                       val is_question: Int? = null,
                       val image: String? = null,
                       val icon: String? = null,
                       val parent_id: Int? = null,
                       val id: Int? = null,
                       var status: Boolean? = false,
                       val name: String? = null,
                       val sub_category: List<SubCategory>? = null
) : Parcelable


@Parcelize
data class Language(
        val country_code: String? = null,
        val id: Int? = null,
        val language_code: String? = null,
        val language_name: String? = null,
        val rtl: Int? = null
) : Parcelable


@Parcelize
data class BillingAddress(
        var address: String? = null,
        var city: String? = null,
        var state: String? = null,
        var country: String? = null,
        var pinCode: String? = null,

        var shippingAddress: String? = null,
        var shippingCity: String? = null,
        var shippingState: String? = null,
        var shippingCountry: String? = null,
        var shippingPinCode: String? = null
) : Parcelable

data class SortByData(
        val filterName:String?=null,
        var icon:Int?=null,
        val type: Int?=null
)
