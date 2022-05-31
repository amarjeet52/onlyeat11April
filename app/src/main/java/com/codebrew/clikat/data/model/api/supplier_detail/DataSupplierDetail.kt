package com.codebrew.clikat.data.model.api.supplier_detail

import android.os.Parcelable
import com.codebrew.clikat.modal.other.SubCategoryData
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class DataSupplierDetail(

        @SerializedName("logo")
        @Expose
        var logo: String? = null,
        @SerializedName("status")
        @Expose
        var status: Int? = null,

        @SerializedName("email")
        @Expose
        var email: String? = null,
        @SerializedName("trade_license_no")
        @Expose
        var tradeLicenseNo: String? = null,
        @SerializedName("total_reviews")
        @Expose
        var totalReviews: Int? = null,

        @SerializedName("rating")
        @Expose
        var rating: Float? = null,

        @SerializedName("name")
        @Expose
        var name: String? = null,
        @SerializedName("payment_method")
        @Expose
        var paymentMethod: Int? = null,
        @SerializedName("description")
        @Expose
        var description: String? = null,

        @SerializedName("about")
        @Expose
        var about: String? = null,

        @SerializedName("terms_and_conditions")
        @Expose
        var termsAndConditions: String? = null,

        @SerializedName("address")
        @Expose
        var address: String? = null,

        @SerializedName("delivery_min_time")
        @Expose
        var deliveryMinTime: Int? = null,

        @SerializedName("delivery_max_time")
        @Expose
        var deliveryMaxTime: Int? = null,

        @SerializedName("min_order_delivery")
        @Expose
        var minOrderDelivery: Float? = null,

        @SerializedName("min_order_delivery_charge")
        @Expose
        var minOrderDeliveryCharge: Float? = null,
        @SerializedName("delivery_charges")
        @Expose
        var deliveryCharges: Float? = null,
        @SerializedName("business_start_date")
        @Expose
        var businessStartDate: String? = null,

        @SerializedName("total_order")
        @Expose
        var totalOrder: Int? = null,

        @SerializedName("review_list")
        @Expose
        var reviewList: List<ReviewList>? = null,

        @SerializedName("my_review")
        @Expose
        var myReview: MyReview? = null,

        @SerializedName("Favourite")
        @Expose
        var favourite: Int? = null,

        var order: Int? = null,
        var supplier_image: List<String>? = null,

        @SerializedName("open_time")
        @Expose
        var open_time: String? = null,
        @SerializedName("close_time")
        @Expose
        var closeTime: String? = null,

        @SerializedName("min_order")
        @Expose
        var minOrder: Float? = null,

        @SerializedName("commission_package")
        @Expose
        var commissionPackage: Int? = null,

        @SerializedName("supplier_id")
        @Expose
        var supplier_id: Int? = null,

        @SerializedName("category_id")
        @Expose
        var category_id: Int? = null,

        @SerializedName("branchId")
        @Expose
        var branchId: Int? = null,

        @SerializedName("id")
        @Expose
        var id: Int? = null,

        var facebook_link: String? = null,
        var linkedin_link: String? = null,
        var message_id: String? = null,
        var user_created_id: String? = null,

        var category: List<SubCategoryData>?=null


) : Parcelable