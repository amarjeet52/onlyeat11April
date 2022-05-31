package com.codebrew.clikat.modal

import com.codebrew.clikat.modal.other.ProductDataBean
import com.google.gson.annotations.SerializedName

data class SeachProductsResponseModel(

	@field:SerializedName("data")
	val data: Data? = null,

	@field:SerializedName("message")
	val message: String? = null,

	@field:SerializedName("status")
	val status: Int? = null
)

data class Data(

	@field:SerializedName("list")
	val list: List<SearchProductListItem?>? = null
)

data class SearchProductListItem(

	@field:SerializedName("handling_supplier")
	val handlingSupplier: Int? = null,

	@field:SerializedName("urgent_value")
	val urgentValue: Int? = null,

	@field:SerializedName("cart_image_upload")
	val cartImageUpload: Int? = null,

	@field:SerializedName("language_id")
	val languageId: Int? = null,

	@field:SerializedName("interval_value")
	val intervalValue: Int? = null,

	@field:SerializedName("product_desc")
	val productDesc: String? = null,

	@field:SerializedName("product_tags")
	val productTags: String? = null,

	@field:SerializedName("urgent_type")
	val urgentType: Int? = null,

	@field:SerializedName("max_hour")
	val maxHour: Int? = null,

	@field:SerializedName("price")
	var price: Float? = null,

	@field:SerializedName("product_id")
	val productId: Int? = null,

	@field:SerializedName("total_rating")
	val totalRating: Int? = null,

	@field:SerializedName("id")
	val id: Int? = null,

	@field:SerializedName("measuring_unit")
	val measuringUnit: String? = null,

	@field:SerializedName("sku")
	val sku: String? = null,

	@field:SerializedName("is_product")
	val isProduct: Int? = null,

	@field:SerializedName("total_review")
	val totalReview: Int? = null,

	@field:SerializedName("country_of_origin")
	val countryOfOrigin: String? = null,

	@field:SerializedName("gst_price")
	val gstPrice: Any? = null,

	@field:SerializedName("created_by")
	val createdBy: Int? = null,

	@field:SerializedName("imageOrder")
	val imageOrder: Int? = null,

	@field:SerializedName("offer_name")
	val offerName: String? = null,

	@field:SerializedName("brand_id")
	val brandId: Int? = null,

	@field:SerializedName("pricing_type")
	val pricingType: Int? = null,

	@field:SerializedName("per_hour_price")
	val perHourPrice: Int? = null,

	@field:SerializedName("can_urgent")
	val canUrgent: Int? = null,

	@field:SerializedName("scheduling_possible")
	val schedulingPossible: Int? = null,

	@field:SerializedName("parent_id")
	val parentId: Int? = null,

	@field:SerializedName("pos_product_id")
	val posProductId: Int? = null,

	@field:SerializedName("name")
	val name: String? = null,

	@field:SerializedName("price_type")
	val priceType: Int? = null,

	@field:SerializedName("default_image")
	val defaultImage: Int? = null,

	@field:SerializedName("end_date")
	val endDate: String? = null,

	@field:SerializedName("interval_flag")
	val intervalFlag: Int? = null,

	@field:SerializedName("customTabDescription1")
	val customTabDescription1: Any? = null,

	@field:SerializedName("customTabDescription2")
	val customTabDescription2: Any? = null,

	@field:SerializedName("is_global")
	val isGlobal: Int? = null,

	@field:SerializedName("created_at")
	val createdAt: String? = null,

	@field:SerializedName("beauty_saloon_price")
	val beautySaloonPrice: Int? = null,

	@field:SerializedName("commission_package")
	val commissionPackage: Int? = null,

	@field:SerializedName("purchased_quantity")
	val purchasedQuantity: Int? = null,

	@field:SerializedName("duration")
	val duration: Int? = null,

	@field:SerializedName("is_deleted")
	val isDeleted: Int? = null,

	@field:SerializedName("category_id")
	val categoryId: Int? = null,

	@field:SerializedName("videoUrl")
	val videoUrl: Any? = null,

	@field:SerializedName("delivery_charges")
	val deliveryCharges: Int? = null,

	@field:SerializedName("added_by")
	val addedBy: Int? = null,

	@field:SerializedName("sub_category_id")
	val subCategoryId: Int? = null,

	@field:SerializedName("avg_rating")
	val avgRating: Int? = null,

	@field:SerializedName("commission")
	val commission: Int? = null,

	@field:SerializedName("user_type_id")
	val userTypeId: Int? = null,

	@field:SerializedName("making_price")
	val makingPrice: String? = null,

	@field:SerializedName("detailed_sub_category_id")
	val detailedSubCategoryId: Int? = null,

	@field:SerializedName("commission_type")
	val commissionType: Int? = null,

	@field:SerializedName("house_cleaning_price")
	val houseCleaningPrice: Int? = null,

	@field:SerializedName("min_hour")
	val minHour: Int? = null,

	@field:SerializedName("start_date")
	val startDate: String? = null,

	@field:SerializedName("Size_chart_url")
	val sizeChartUrl: String? = null,

	@field:SerializedName("is_prescribed")
	val isPrescribed: Int? = null,

	@field:SerializedName("is_package")
	val isPackage: Int? = null,

	@field:SerializedName("quantity")
	val quantity: Int? = null,

	@field:SerializedName("display_price")
	var displayPrice: Float? = null,

	@field:SerializedName("recurring_possible")
	val recurringPossible: Int? = null,

	@field:SerializedName("item_unavailable")
	val itemUnavailable: Int? = null,

	@field:SerializedName("is_non_veg")
	val isNonVeg: Int? = null,

	@field:SerializedName("is_available")
	val isAvailable: Int? = null,

	@field:SerializedName("urgent_price")
	val urgentPrice: Int? = null,

	@field:SerializedName("price_unit")
	val priceUnit: String? = null,

	@field:SerializedName("approved_by_admin")
	val approvedByAdmin: Int? = null,

	@field:SerializedName("image_path")
	val imagePath: String? = null,

	@field:SerializedName("bar_code")
	val barCode: String? = null,

	@field:SerializedName("is_live")
	val isLive: Int? = null,

	@field:SerializedName("payment_after_confirmation")
	val paymentAfterConfirmation: Int? = null,

	@field:SerializedName("approved_by_supplier")
	val approvedBySupplier: Int? = null,

	@field:SerializedName("handling")
	val handling: Int? = null
)
