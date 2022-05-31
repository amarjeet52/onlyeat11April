package com.codebrew.clikat.modal.other

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

data class OfferListModel(
        var status: Int? = null,
        var message: String? = null,
        var data: OfferDataBean? = null)

data class ResponseSuppliersList(
        var status: Int? = null,
        var message: String? = null,
        var data: List<SupplierInArabicBean>? = null)


data class ResponseCategorySuppliersList(
        var status: Int? = null,
        var message: String? = null,
        var data: ArrayList<CategoryWiseSuppliers>? = null
)

data class OfferDataBean(
        var offerEnglish: List<ProductDataBean>? = null,
        var recentViewHistory: List<ProductDataBean>? = null,
        var offersSuppliers: List<ProductDataBean>? = null,
        @SerializedName("SupplierInArabic")
        var supplierInArabic: List<SupplierInArabicBean>? = null,
        var bestSellersSuppliers: List<SupplierInArabicBean>? = null,
        var fastestDeliverySuppliers:List<SupplierInArabicBean>?=null,
        val getOfferByCategory: List<GetOfferByCategory>?=null)

@Parcelize
data class CategoryWiseSuppliers(
        val category_name: String,
        val suppliers: List<SupplierDataBean>?=null

):Parcelable

data class GetOfferByCategory(
        val name: String,
        val value: MutableList<ProductDataBean>
)

@Parcelize
data class SupplierInArabicBean(
        var id : Int? = null,
        var category_id : Int? = null,
        var supplier_branch_id: Int? = null,
        var supplier_image: String? = null,
        var is_subcategory: Int? = null,
        var logo: String? = null,
        var status : Int? = null,
        var payment_method : Int? = null,
        var is_dine_in: Int? = null,
        var rating: Float? = null,
        var total_reviews : Float? = null,
        var name: String? = null,
        val delivery_radius: Float?=null,
        val distance: Double?=null,
        var description: String? = null,
        var uniqueness: String? = null,
        var Favourite:Int?= 0,
        var supplierPhoneNumber:String?=null,
        var terms_and_conditions: String? = null,
        var category: MutableList<SubCategoryData>? = null,
        var address: String? = null,
        val type: Int?=null,
        val is_multi_branch: Int?=null,
        val is_subscribed: String?=null,
        val is_multi_branchs: Int?=null,
        var parentPosition:Int?= null,
        var delivery_min_time: Int = 0,
        var delivery_max_time: Int = 0
      ):Parcelable


data class FiltersSupplierList(
        var is_free_delivery:Int?=null,
        var is_preparation_time:Int?=null,
        var minValue:String?=null,
        var maxValue:String?=null
)