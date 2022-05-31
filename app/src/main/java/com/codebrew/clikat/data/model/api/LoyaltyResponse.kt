package com.codebrew.clikat.data.model.api

import com.google.gson.annotations.SerializedName

data class LoyaltyResponse(

        @field:SerializedName("data")
        val data: LoyaltyData? = null,

        @field:SerializedName("message")
        val message: Any? = null,

        @field:SerializedName("status")
        val status: Int? = null
)

data class LoyalityLevelItem(

        @field:SerializedName("is_for_all_category")
        val isForAllCategory: Int? = null,

        @field:SerializedName("image")
        val image: String? = null,

        @field:SerializedName("per_point_amount_type")
        val perPointAmountType: Int? = null,

        @field:SerializedName("per_point_order_amount")
        val perPointOrderAmount: Double? = null,

        @field:SerializedName("name")
        val name: String? = null,

        @field:SerializedName("description")
        val description: String? = null,

        @field:SerializedName("per_point_amount")
        val perPointAmount: Double? = null,

        @field:SerializedName("id")
        val id: Int? = null,

        @field:SerializedName("total_loyality_points")
        val totalLoyalityPoints: Double? = null
)

data class LoyaltyData(

        @field:SerializedName("earnedData")
        val earnedData: ArrayList<EarnedDataItem>? = null,

        @field:SerializedName("loyalityLevel")
        val loyalityLevel: ArrayList<LoyalityLevelItem>? = null,

        @field:SerializedName("totalPointAmountEarned")
        val totalPointAmountEarned: Float? = null,

        @field:SerializedName("totalEarningPoint")
        val totalEarningPoint: Float? = null,

        @field:SerializedName("leftPointAmount")
        val leftPointAmount: Float? = null,

        var message_id: String? = ""
)


data class EarnedDataItem(

        @field:SerializedName("loyality_point_discount")
        val loyalityPointDiscount: Double? = null,

        @field:SerializedName("have_pet")
        val havePet: Int? = null,

        @field:SerializedName("supplier_stripe_transfer_id")
        val supplierStripeTransferId: String? = null,

        @field:SerializedName("earned_points")
        val earned_points: String? = null,

        @field:SerializedName("handling_admin")
        val handlingAdmin: Double? = null,

        @field:SerializedName("promo_code")
        val promoCode: String? = null,

        @field:SerializedName("type")
        val type: Int? = null,

        @field:SerializedName("pickup_date")
        val pickupDate: String? = null,

        @field:SerializedName("referral_amount")
        val referralAmount: Float? = null,

        @field:SerializedName("id")
        val id: Int? = null,

        @field:SerializedName("payment_source")
        val paymentSource: String? = null,

        @field:SerializedName("self_pickup")
        val selfPickup: Int? = null,

        @field:SerializedName("transaction_id")
        val transactionId: String? = null,

        @field:SerializedName("card_payment_id")
        val cardPaymentId: String? = null,

        @field:SerializedName("delivery_date_time")
        val deliveryDateTime: String? = null,

        @field:SerializedName("user_service_charge")
        val userServiceCharge: Float? = null,

        @field:SerializedName("to_address")
        val toAddress: String? = null,

        @field:SerializedName("from_latitude")
        val fromLatitude: Double? = null,

        @field:SerializedName("CommentApprove")
        val commentApprove: Int? = null,

        @field:SerializedName("approve_rejection_reason")
        val approveRejectionReason: String? = null,

        @field:SerializedName("user_id")
        val userId: Int? = null,

        @field:SerializedName("created_on")
        val createdOn: String? = null,

        @field:SerializedName("Server_Date")
        val serverDate: String? = null,

        @field:SerializedName("donate_to_someone")
        val donateToSomeone: Int? = null,

        @field:SerializedName("pickup_time")
        val pickupTime: Any? = null,

        @field:SerializedName("promo_discount")
        val promoDiscount: Double? = null,

        @field:SerializedName("order_id")
        val orderId: Int? = null,

        @field:SerializedName("was_postponed")
        val wasPostponed: Int? = null,

        @field:SerializedName("status")
        val status: Int? = null,

        @field:SerializedName("progress_on")
        val progressOn: String? = null,

        @field:SerializedName("rated_on")
        val ratedOn: String? = null,

        @field:SerializedName("created_at")
        val createdAt: String? = null,

        @field:SerializedName("schedule_order")
        val scheduleOrder: Int? = null,

        @field:SerializedName("restaurantFloor")
        val restaurantFloor: Any? = null,

        @field:SerializedName("updated_at")
        val updatedAt: String? = null,

        @field:SerializedName("delivery_charges")
        val deliveryCharges: Double? = null,

        @field:SerializedName("refund_amount")
        val refundAmount: Double? = null,

        @field:SerializedName("urgent")
        val urgent: Int? = null,

        @field:SerializedName("schedule_end_date")
        val scheduleEndDate: String? = null,

        @field:SerializedName("is_recurring")
        val isRecurring: Int? = null,

        @field:SerializedName("delivered_by")
        val deliveredBy: Int? = null,

        @field:SerializedName("user_delivery_address")
        val userDeliveryAddress: Int? = null,

        @field:SerializedName("is_package")
        val isPackage: Int? = null,

        @field:SerializedName("pres_description")
        val presDescription: String? = null,

        @field:SerializedName("preparation_pickup_date_time")
        val preparationPickupDateTime: String? = null,

        @field:SerializedName("zelle_receipt_url")
        val zelleReceiptUrl: String? = null,

        @field:SerializedName("area_to_focus")
        val areaToFocus: String? = null,

        @field:SerializedName("updated_on")
        val updatedOn: String? = null,

        @field:SerializedName("handling_supplier")
        val handlingSupplier: Float? = null,

        @field:SerializedName("buffer_time")
        val bufferTime: Int? = null,

        @field:SerializedName("near_on")
        val nearOn: String? = null,

        @field:SerializedName("slot_price")
        val slotPrice: Float? = null,

        @field:SerializedName("waiting_charges")
        val waitingCharges: Float? = null,

        @field:SerializedName("rating")
        val rating: Float? = null,

        @field:SerializedName("questions")
        val questions: Any? = null,

        @field:SerializedName("delivery_time")
        val deliveryTime: Any? = null,

        @field:SerializedName("is_read")
        val isRead: Int? = null,

        @field:SerializedName("cart_id")
        val cartId: Int? = null,

        @field:SerializedName("pres_image1")
        val presImage1: String? = null,

        @field:SerializedName("pres_image2")
        val presImage2: String? = null,

        @field:SerializedName("pres_image3")
        val presImage3: String? = null,

        @field:SerializedName("service_date")
        val serviceDate: String? = null,

        @field:SerializedName("is_schedule")
        val isSchedule: Int? = null,

        @field:SerializedName("pres_image4")
        val presImage4: String? = null,

        @field:SerializedName("cleaner_in")
        val cleanerIn: Int? = null,

        @field:SerializedName("from_address")
        val fromAddress: String? = null,

        @field:SerializedName("pres_image5")
        val presImage5: String? = null,

        @field:SerializedName("min_order_delivery_crossed")
        val minOrderDeliveryCrossed: Int? = null,

        @field:SerializedName("info")
        val info: Int? = null,

        @field:SerializedName("supplier_branch_id")
        val supplierBranchId: Int? = null,

        @field:SerializedName("user_pickup_address")
        val userPickupAddress: Any? = null,

        @field:SerializedName("parking_instructions")
        val parkingInstructions: String? = null,

        @field:SerializedName("earned_amount")
        val earnedAmount: Double? = null,

        @field:SerializedName("gst_charges")
        val gstCharges: Double? = null,

        @field:SerializedName("remarks_images_array")
        val remarksImagesArray: String? = null,

        @field:SerializedName("schedule_date")
        val scheduleDate: String? = null,

        @field:SerializedName("created_by")
        val createdBy: Int? = null,

        @field:SerializedName("wallet_discount_amount")
        val walletDiscountAmount: Double? = null,

        @field:SerializedName("payment_type")
        val paymentType: Int? = null,

        @field:SerializedName("push_sent")
        val pushSent: Int? = null,

        @field:SerializedName("loyalty_points")
        val loyaltyPoints: Double? = null,

        @field:SerializedName("user_address_id")
        val userAddressId: Int? = null,

        @field:SerializedName("to_latitude")
        val toLatitude: Double? = null,

        @field:SerializedName("read_by")
        val readBy: Int? = null,

        @field:SerializedName("remaining_amount")
        val remainingAmount: Double? = null,

        @field:SerializedName("supplier_commision")
        val supplierCommision: Double? = null,

        @field:SerializedName("is_automatic")
        val isAutomatic: Int? = null,

        @field:SerializedName("redeem_promo")
        val redeemPromo: Int? = null,

        @field:SerializedName("duration")
        val duration: Int? = null,

        @field:SerializedName("id_edit")
        val idEdit: Int? = null,

        @field:SerializedName("from_longitude")
        val fromLongitude: Double? = null,

        @field:SerializedName("preparation_time")
        val preparationTime: String? = null,

        @field:SerializedName("is_ready_for_use")
        val isReadyForUse: Int? = null,

        @field:SerializedName("tip_agent")
        val tipAgent: Double? = null,

        @field:SerializedName("is_agent")
        val isAgent: Double? = null,

        @field:SerializedName("edit_by")
        val editBy: String? = null,

        @field:SerializedName("gift_amount")
        val giftAmount: Double? = null,

        @field:SerializedName("payment_status")
        val paymentStatus: Int? = null,

        @field:SerializedName("delivered_on")
        val deliveredOn: String? = null,

        @field:SerializedName("is_edit")
        val isEdit: Int? = null,

        @field:SerializedName("urgent_price")
        val urgentPrice: Float? = null,

        @field:SerializedName("confirmed_on")
        val confirmedOn: String? = null,

        @field:SerializedName("is_acknowledged")
        val isAcknowledged: Int? = null,

        @field:SerializedName("to_longitude")
        val toLongitude: Double? = null,

        @field:SerializedName("pickup_address_id")
        val pickupAddressId: Int? = null,

        @field:SerializedName("shipped_on")
        val shippedOn: String? = null,

        @field:SerializedName("order_source")
        val orderSource: Int? = null,

        @field:SerializedName("apply_promo")
        val applyPromo: Int? = null,

        @field:SerializedName("payment_after_confirmation")
        val paymentAfterConfirmation: Int? = null,

        @field:SerializedName("comment")
        val comment: String? = null,

        @field:SerializedName("net_amount")
        val netAmount: Double? = null,

        @field:SerializedName("used_loyality_point_amount")
        val used_loyality_point_amount: Double? = null,

        @field:SerializedName("partial_prescription")
        val partialPrescription: Int? = null,

        @field:SerializedName("request_id")
        val requestId: Int? = null,

        @field:SerializedName("currency_id")
        val currencyId: Int? = null,

        @field:SerializedName("remarks")
        val remarks: String? = null
)

data class LoyalityDataItem(val drawable: String? = null, val title: String? = null, val rewards: String? = null, val rewardsPoint: List<String>?=null)

data class LoyalityPointItem(val loyalityDesc:String?=null)
