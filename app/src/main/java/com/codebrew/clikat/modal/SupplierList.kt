package com.codebrew.clikat.modal

import android.annotation.SuppressLint
import android.os.Parcelable
import com.codebrew.clikat.modal.other.Branch
import com.codebrew.clikat.modal.other.Category
import com.codebrew.clikat.modal.other.TimeDataBean
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize
import javax.annotation.Generated

@Parcelize
class SupplierList : Parcelable {
    /**
     * @return The supplierBranchId
     */
    /**
     * @param supplierBranchId The supplier_branch_id
     */
    @SerializedName("supplier_branch_id")
    @Expose
    var supplierBranchId: Int? = null
    /**
     * @return The handlingFees
     */
    /**
     * @param handlingFees The handling_fees
     */
    @SerializedName("handling_fees")
    @Expose
    var handlingFees: Float? = null
    /**
     * @return The commissionType
     */
    /**
     * @param commissionType The commission_type
     */
    @SerializedName("commission_type")
    @Expose
    var commissionType: Int? = null
    /**
     * @return The commission
     */
    /**
     * @param commission The commission
     */
    @SerializedName("commission")
    @Expose
    var commission: Int? = null
    /**
     * @return The deliveryMinTime
     */
    /**
     * @param deliveryMinTime The delivery_min_time
     */
    @SerializedName("delivery_min_time")
    @Expose
    var deliveryMinTime: Int? = null
    /**
     * @return The deliveryMaxTime
     */
    /**
     * @param deliveryMaxTime The delivery_max_time
     */
    @SerializedName("delivery_max_time")
    @Expose
    var deliveryMaxTime: Int? = null
    /**
     * @return The deliveryStartTime
     */
    /**
     * @param deliveryStartTime The delivery_start_time
     */
    @SerializedName("delivery_start_time")
    @Expose
    var deliveryStartTime: String? = null
    /**
     * @return The deliveryEndTime
     */
    /**
     * @param deliveryEndTime The delivery_end_time
     */
    @SerializedName("delivery_end_time")
    @Expose
    var deliveryEndTime: String? = null
    /**
     * @return The workingStartTime
     */
    /**
     * @param workingStartTime The working_start_time
     */
    @SerializedName("working_start_time")
    @Expose
    var workingStartTime: String? = null
    /**
     * @return The workingEndTime
     */
    /**
     * @param workingEndTime The working_end_time
     */
    @SerializedName("working_end_time")
    @Expose
    var workingEndTime: String? = null

    /**
     * @return The minOrder
     */
    @SerializedName("categories")
    @Expose
    var categories: String? = null

    @SerializedName("name_arabic")
    @Expose
    var name_arabic: String? = null

    @SerializedName("min_order")
    @Expose
    var minOrder = 0f
        private set
    /**
     * @return The deliveryCharges
     */
    /**
     * @param deliveryCharges The delivery_charges
     */
    @SerializedName("delivery_charges")
    @Expose
    var deliveryCharges: Int? = null
    /**
     * @return The name
     */
    /**
     * @param name The name
     */
    @SerializedName("name")
    @Expose
    var name: String? = null
    /**
     * @return The logo
     */
    /**
     * @param ic_splash The logo
     */
    @SerializedName("logo")
    @Expose
    var logo: String? = null

    @SerializedName("supplier_image")
    @Expose
    var supplierImage: String? = null

    /**
     * @return The id
     */
    /**
     * @param id The id
     */
    @SerializedName("id")
    @Expose
    var id: Int? = null
    /**
     * @return The status
     */
    /**
     * @param status The status
     */
    @SerializedName("status")
    @Expose
    var status: Int? = null
    /**
     * @return The handlingAdmin
     */
    /**
     * @param handlingAdmin The handling_admin
     */
    @SerializedName("handling_admin")
    @Expose
    var handlingAdmin: Float? = null


    @SerializedName("count_supplier_branch")
    @Expose
    var count_supplier_branch: Int? = null
    /**
     * @return The totalReviews
     */
    /**
     * @param totalReviews The total_reviews
     */
    @SerializedName("total_reviews")
    @Expose
    var totalReviews: Int? = null
    /**
     * @return The rating
     */
    /**
     * @param rating The rating
     */
    @SerializedName("rating")
    @Expose
    var rating: Float? = null
    /**
     * @return The paymentMethod
     */
    /**
     * @param paymentMethod The payment_method
     */
    @SerializedName("is_on_discount")
    @Expose
    val is_on_discount: Int? = null

    @SerializedName("is_free_delivery")
    @Expose
    val is_free_delivery: Int? = null

    @SerializedName("payment_method")
    @Expose
    var paymentMethod: Int? = null
    /**
     * @return The commissionPackage
     */
    /**
     * @param commissionPackage The commission_package
     */
    @SerializedName("commission_package")
    @Expose
    var commissionPackage: Int? = null

    @SerializedName("is_sponsor")
    @Expose
    var isSponsor = 0

    @SerializedName("address")
    @Expose
    val address: String? = null

    @SerializedName("supplier_id")
    @Expose
    val supplier_id: Int? = null

    @SerializedName("business_start_date")
    @Expose
    val business_start_date: String? = null

    /**
     * @param minOrder The min_order
     */
    fun setMinOrder(minOrder: Int) {
        this.minOrder = minOrder.toFloat()
    }

    @SerializedName("supplier_branch_name")
    @Expose
    var supplierBranchName: String? = null

    var is_multi_branch: Int? = null

    var supplierPhoneNumber: String? = null

    var isOpen: Boolean = false

    var is_scheduled:Int=0

    @SerializedName("timings")
    @Expose
    val timings: String? = null

    var timing: List<TimeDataBean>? = null

    var distance:Float?=null

    var Favourite:Int?= 0

    var position:Int?=0

    var is_dine_in:Int?=0


    val category: List<Category>? = null
}