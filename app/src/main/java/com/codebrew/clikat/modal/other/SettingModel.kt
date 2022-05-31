package com.codebrew.clikat.modal.other

import android.os.Parcelable
import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

class SettingModel {

    var status = 0
    var message: String? = null
    var data: DataBean? = null

    class DataBean {
        var dialog_token: String? = null
        val supplier_branch_id = 0
        val supplier_id = 0
        val latitude: Double? = null
        val longitude: Double? = null
        val supplierName: String? = null
        val self_pickup: String? = null
        val key_value: SettingData? = null
        var screenFlow: List<ScreenFlowBean>? = null
        var bookingFlow: List<BookingFlowBean>? = null
        var termsAndConditions: List<TermCondition>? = null
        val default_address: List<DefaultAddres>? = null
        val adminDetails: List<AdminDetail>? = null
        var featureData: List<FeatureData>? = null
        var user_app_version: ArrayList<UserAppVersion>? = null
        var countryCodes: ArrayList<CountryCodes>? = null

        class ScreenFlowBean {
            var is_multiple_branch = 0
            var is_single_vendor = 0
            var app_type = 0
            var sub_app_type = 0
        }
        class CountryCodes (
            var id:Int? = null,
            var country_code:String? = null,
            var iso:String? = null,
            var flag_image:String? = null,
            var country_name:String? = null
        )
        class BookingFlowBean {
            var vendor_status = 0
            var cart_flow = 0
            var is_scheduled = 0
            var schedule_time = 0
            var admin_order_priority = 0
            var is_pickup_order = 0
            var interval = 0
            var branch_flow = 0
            var booking_track_status = 0  // 0 for single supplier --> single branch, 1 for sigle supplier --> multiple branch
        }

        class TermCondition(
                val termsAndConditions: Int?,
                val privacyPolicy: Int?,
                val language_id: Int)

        data class AdminDetail(
                val country_code: String,
                val email: String,
                val iso: String,
                val phone_number: String
        )

        data class UserAppVersion(
                val version_ios: String? = null,
                val version_android: String? = null,
                val is_update_ios: String? = null,
                val is_update_android: String? = null
        )

        data class DefaultAddres(
                val address: String,
                val latitude: Double,
                val longitude: Double
        )

        @Parcelize
        class SettingData(
                val banner_four: String? = null,
                val banner_one: String? = null,
                val banner_three: String? = null,
                val banner_two: String? = null,
                val login_template: String? = null,
                val banner_url: String? = null,
                val element_color: String? = null,
                val logo_url: String? = null,
                val theme_color: String? = null,
                var header_color: String? = null,
                val header_text_color: String? = null,
                var terminology: String? = null,
                var three_decimal: String? = null,
                var payment_after_confirmation: String? = null,
                val payment_method: String? = null,
                val referral_feature: String? = null,
                val chat_enable: String? = null,
                val referral_receive_price: String? = null,
                val referral_given_price: String? = null,
                val email: String? = null,
                var from_email: String? = null,
                var from_number: String? = null,
                val app_banner_width: String? = null,
                val pickup_url_one: String? = null,
                val pickup_url_two: String? = null,
                val pickup_url_three: String? = null,


                //order_instructions
                //0: disabled
                //1: enabled
                //
                //cart_image_upload
                //0: disabled
                //1: enabled
                var cart_image_upload: String? = null,
                var order_instructions: String? = null,
                var disable_tax: String? = null,
                val cutom_country_code: String? = null,
                val delivery_charge_type: String? = null,
                val user_service_fee: String? = null,
                var secondary_language: String? = null,
                var app_selected_template: String? = null,
                @SerializedName("user_register_flow", alternate = ["user_register_flow "])
                var user_register_flow: String? = null,

                val app_sharing_message: String? = null,
                var bypass_otp: String? = null,
                val google_map_key: String? = null,
                val is_return_request: String? = null,
                val is_supplier_detail: String? = null,
                val phone_number: String? = null,
                val product_pdf_upload: String,
                val app_selected_theme: String? = null,
                val enable_rating_wise_sorting: String? = null,
                val enable_supplier_in_special_offer: String? = null,
                val payhere_merchantID: String? = null,
                val extra_instructions: String? = null,
                //cannadash
                //1 to show
                //0 to hide
                val extra_functionality: String? = null,
                val things_to_remember: String? = null,
                var min_order: Double? = null,
                @SerializedName("disable_order_cancel", alternate = ["disable_user_cancel_order"])
                val disable_order_cancel: String? = null,
                val search_by: String? = null,
                val is_product_wishlist: String? = null,
                val is_supplier_wishlist: String? = null,
                val show_prescription_requests: String? = null,
                val show_donate_popup: String? = null,
                val product_detail: String? = null,
                val is_agent_rating: String? = null,
                var is_supplier_rating: String? = null,
                val is_product_rating: String? = null,
                var clikat_theme: String? = null,
                var skip_suppliers_list: String? = null,
                var is_skip_theme: String? = null,
                var is_juman_flow_enable:String?=null,
                var ecom_agent_module: String? = null,
                val is_hunger_app: String? = null,
                var zipTheme: String? = null,
                var yummyTheme: String? = null,
                var isProductDetailCheck: String? = null,
                @SerializedName("show_expected_delivery_between ", alternate = ["show_expected_delivery_between"])
                var show_expected_delivery_between: String? = null,
                val is_restaurant_sort: String? = null,
                //zipeats
                var wallet_module: String? = null,
                var show_supplier_detail: String? = null,
                var payment_through_wallet_discount: String? = null,
                var is_number_masking_enable :String? = null,
                var zipDesc: String? = null,
                var addonHeight: String? = null,
                var savedCard: String? = null,
                var supplierImage: String? = null,
                var isTawk: String? = null,
                var is_app_sharing_message: String? = null,
                var is_schdule_order: String? = null,
                var show_filters: String? = "1",
                var show_social_links: String? = "1",
                var fackbook_link: String? = null,
                var twitter_link: String? = null,
                var instagram_link: String? = null,
                var google_link: String? = null,
                var linkedin_link: String? = null,
                var product_prescription: String? = null,
                var isCash: String? = null,
                var isCallSupplier: String? = null,
                var default_language: String? = null,
                var can_user_edit: String? = null,
                var hideAgentList: String? = null,
                var show_ecom_v2_theme: String? = "0",
                var user_id_proof: String? = null,
                var show_food_groc: String? = "0",
                var laundary_service_flow: String? = null,
                val is_laundry_theme: String? = null,
                val is_lubanah_theme: String? = null,
                var is_user_subscription: String? = null,
                var google_map_key_android: String? = null,
                var dialog_project_id: String? = null,
                var search_user_locale: String? = null,
                var addon_type_quantity: String? = null,
                var enable_google_login: String? = null,
                var enable_facebook_login: String? = null,
                var agentTipPercentage: String? = null,
                var is_decimal_quantity_allowed: String? = "1",
                var is_loyality_enable: String? = "1",
                var enable_whatsapp_contact_us: String? = null,
                var enable_id_for_invoice_in_profile:String?=null,
                var is_table_booking: String? = null,
                val by_pass_tables_selection: String? = null,
                var table_book_mac_theme:String?=null,
                var show_filter_on_home:String?=null,
                var is_feedback_form_enabled: String? = null,
                var is_product_weight: String? = null,
                var admin_to_user_chat: String? = null,
                var supplier_to_user_chat: String? = "1",
                var branch_flow: String? = null,
                var phone_registration_flag: String? = null,
                var enable_signup_phone_only:String?=null,
                var is_unify_search: String? = null,
                var is_coin_exchange: String? = null,
                var custom_rest_prod: String? = null,
                var is_abn_business: String? = null,
                var datatrans_app_merchant_id: String? = null,
                var is_currency_exchange_rate: String? = null,
                @SerializedName("is_sos_allow", alternate = ["is_sos_enable"])
                var is_sos_enable: String? = null,
                var full_view_supplier_theme:String = "1",
                var is_table_invite_allowed: String? = null,
                var table_booking_add_food_allow: String? = null,
                var is_social_ecommerce: String? = null,
                var fixed_country_code: String? = null,
                var countries_array: String? = null,
                var instruction_screen_image: String? = null,
                var is_pickup_primary: String? = null,
                var is_found: String? = "0",
                var ecom_theme_dark: String? = null,
                var show_wallet_on_home: String? = null,
                var is_decimal_fixed_interval: String? = null,
                var client_key_sandbox: String? = null,
                var login_id_sandbox: String? = null,
                var is_new_feedback_theme: String? = null,
                var is_vendor_registration: String? = null,
                @SerializedName("Admin Domain")
                var admin_domain: String? = null,
                var agent_verification_code_enable: String? = null,
                val disable_phone_name_in_address: String? = null,
                val tutorial_screens: String? = null,
                val dropoff_buffer: String? = "0",
                val enable_size_chart_in_product: String? = null,
                var enable_country_of_origin_in_product: String? = null,
                var is_compare_products: String? = null,
                var food_flow_after_home: String? = null,
                var hide_supplier_address: String? = null,
                var delivery_distance_unit: String? = null,
                var show_platform_versions: String? = null,
                var is_custom_category_template: String? = null,
                var signup_declaration: String? = null,
                var hide_agent_tip: String? = null,
                var is_expactor: String? = null,
                var disbale_user_cancel_pending_order: String? = null,
                var disable_user_cancel_after_confirm: String? = null,

                // image placeholder
                var no_data_image: String? = null,
                var pickup_url: String? = null,
                var user_location: String? = null,
                var empty_cart: String? = null,
                var login_icon_url: String? = null,

                var is_tutorial_screen_enable: String? = null,
                var enable_best_sellers: String? = null,
                var is_new_menu_theme: String? = null,
                var enable_promo_code_list: String? = null,
                var show_supplier_phone: String? = "1",
                var show_supplier_email: String? = "1",
                var show_supplier_delivery_timing: String? = "1",
                var show_supplier_open_close: String? = "1",
                var show_supplier_nationality: String? = "1",
                var show_supplier_speciality: String? = "1",
                var show_supplier_brand_name: String? = "1",
                var show_supplier_info_settings: String? = null,
                var enable_min_order_distance_wise: String? = null,
                var show_tags_for_suppliers: String? = null,
                var is_zoom_call_enabled: String? = null,
                var enable_tax_on_total_amt: String? = "1",
                var is_near_by_supplier_enable: String? = null,
                var auth_terms_check: String? = null,
                var hide_pickup_status: String? = null,
                var rest_detail_pagin: String? = null,
                var is_round_off_disable: String? = null,
                var is_delivery_charge_weight_wise_enable: String? = null,
                var is_enable_delivery_type: String? = null,
                var is_enable_orderwise_gateways: String? = null,
                var enable_user_vehicle_number: String? = null,
                var dynamic_order_type_client_wise: String? = null,
                var enable_rest_pagination_category_wise: String? = null,
                var enable_item_purchase_limit: String? = null,
                var dynamic_order_type_client_wise_delivery: String? = null,
                var dynamic_order_type_client_wise_pickup: String? = null,
                var dynamic_order_type_client_wise_dinein: String? = null,
                var enable_cutlery_option: String? = null,
                var enable_referral_bal_limit: String? = null,
                var referral_bal_limit_per_order: String? = null,
                var enable_contact_us: String? = null,
                var enable_freelancer_flow: String? = null,
                var enable_audio_video: String? = null,
                var enable_date_of_birth: String? = null,
                var home_screen_sections: String? = null,
                var dynamic_home_screen_sections: String? = null,
                var is_order_types_screen_dynamic: String? = null,
                var order_type_sections: String? = null,
                //placeholder

                var home: String?= null,
                var table_booking_request: String?= null,
                var wallet_history: String?= null,
                var cart: String?= null,
                var product_listing: String?= null,
                var supplier_reviews: String?= null,
                var order_history_listing: String?= null,
                var favourite_product_listing: String?= null,
                var agent_slots_listing: String?= null,
                var feature_suggestion_list: String?= null,
                var supplier_listing: String?= null,
                var notifications: String?= null,
                var promo_code_listing: String?= null,
                var favourite_suppliers: String?= null,
                var select_agent_lsiting: String?= null,
                var loyalty_points_listing: String?= null,
                var prescription_requests_listing: String?= null,
                var table_selection: String?= null,
                var enable_supplier_promo_list:String?=null,
                var enable_supplier_review_list:String?=null,
                var enable_product_allergy:String?=null,
                var is_multicurrency_enable:String?=null,
                var cleanfax_home_title:String?=null,
                var cleanfax_home_heading:String?=null,
                var cleanfax_home_image_url:String?=null,
                val clean_fax_phone_number:String?=null,
                val is_service_single_selection:String?=null,
                val enable_non_veg_filter:String?=null,
                val enable_min_loyality_points:String?=null,
                val min_loyalty_points_to_redeem:Float?=null,
                val enable_limit_country_codes:String?=null,
                val hide_filter_on_search:String?=null,
                val show_supplier_categories:String?=null,
                val enable_payment_confirmed_status:String?=null,
                val enable_delivery_companies:String?=null,
                val loyality_discount_on_product_listing:String?=null,
                val skip_payment_option:String?=null,
                val enable_biometric_login:String?=null,
                val is_carveQatar_home_theme : String ?= null,
                val is_craveQatar_login_theme : String ?= null
        ) : Parcelable

        @Keep
        data class TutorialItem(
                val tutorial_image: String?,
                val tutorial_text: String?,
                val tutorial_title: String
        )

        @Keep
        data class Terminology(
                val english: AppTerminology?,
                val other: AppTerminology?
        )

        @Keep
        data class AppTerminology(
                val agent: String?,
                val agents: String?,
                val brand: String?,
                val brands: String?,
                val categories: String?,
                val category: String?,
                val order: String?,
                val orders: String?,
                val product: String?,
                val products: String?,
                val status: Status,
                val supplier: String?,
                val suppliers: String?,
                val catalogue: String?,
                @SerializedName("others_tab", alternate = ["otherTab"])
                val others_tab: String?,
                val choose_payment: String,
                val order_now: String,
                val wishlist: String,
                val tax: String,
                val subCategories: String,
                val payment: String,
                val cash: String,
                val total_revenue: String,
                val prescription_value: String,
                val instruction: String,
                val promotions: String,
                val promo_code: String,
                val product_file_upload: String,
                val delivery_timing: String,
                val donate_to_someone: String,
                val delivery_tab: String,
                val location: String,
                val razor_pay: String,
                val selfpickup: String,
                val my_subscription: String,
                val wallet: String,
                val loyalty_points: String,
                val suppliers_near_you: String,
                val supplier_service_fee: String,
                val no_cart_data: String,
                @SerializedName("cod", alternate = ["COD"])
                val cod: String,
                val braintree: String,
                val pending_orders: String,
                val completed_orders: String,

                val my_fatoorah: String,
                @SerializedName("Unserviceable", alternate = ["unserviceable"])
                val unserviceable: String,
                @SerializedName("Popular Restaurantes")
                val popularRestaurantes: String,
                @SerializedName("No Orden found")
                val noOrderFound: String,
                @SerializedName("Customisable", alternate = ["customize", "customizable"])
                val customisable: String,
                @SerializedName("Payment Details")
                val paymentDetails: String,
                @SerializedName("Email")
                val email: String,
                @SerializedName("Share screenshot")
                val shareScreenshot: String?,
                @SerializedName("CHOOSE PAYMENT RECEIPT")
                val choosePaymentReceipt: String?,
                val orderNow: String?,

                val pickup_self: String?,
                val proceed: String?,
                val revenue: String?,
                val revenue_detail: String?,
                val special_offers: String?,
                val supplier_backgound: String?,
                val recommended_supplier: String?,
                val what_are_u_looking_for: String,
                val tags: String,
                val otp: String
        )


        @Keep
        data class AppIcon(
                val app: String?=null,
                val web: String?=null,
                val message:String?=null
        )


        @Keep
        data class Status(
                @SerializedName("0")
                val PENDING: String?,
                @SerializedName("1")
                val ACCEPTED: String?,
                @SerializedName("2")
                val REJECTED: String?,
                @SerializedName("3")
                val ON_THE_WAY: String?,
                @SerializedName("4")
                val NEAR_YOU: String?,
                @SerializedName("5")
                val DELIVERED: String?,
                @SerializedName("6")
                val RATE_GIVEN: String?,
                @SerializedName("7")
                val TRACK_ORDER: String?,
                @SerializedName("8")
                val CUSTOMER_CANCEL: String?,
                @SerializedName("9")
                val SCHEDULED: String?,
                @SerializedName("10")
                val SHIPPED: String?,
                @SerializedName("11", alternate = ["2.5"])
                var PACKED: String?,
                @SerializedName("12")
                val ADMIN_CANCEL: String?
        )

        @Keep
        @Parcelize
        data class FeatureData(
                val customer_feature_id: Int? = null,
                val id: Int? = null,
                val is_active: Int? = null,
                val key_value: List<KeyValue?>? = null,
                val key_value_front: List<KeyValueFront?>? = null,
                val name: String? = null,
                val type_id: Int? = null,
                val type_name: String? = null
        ) : Parcelable

        @Keep
        @Parcelize
        data class KeyValue(
                val for_front: Int? = null,
                val key: String? = null,
                val value: String? = null
        ) : Parcelable

        @Keep
        @Parcelize
        data class KeyValueFront(
                val created_at: String? = null,
                val customer_feature_id: Int? = null,
                val for_front: Int? = null,
                val id: Int? = null,
                val key: String? = null,
                val updated_at: String? = null,
                val value: String? = null
        ) : Parcelable


        @Keep
        @Parcelize
        data class DynamicScreenSections(
                val id: String? = null,
                val code: String? = null,
                val section_name: String? = null,
                val section_place: Int? = null,
                val is_active: Int? = null
        ) : Parcelable, Comparable<DynamicScreenSections> {
            override fun compareTo(other: DynamicScreenSections): Int {
                return section_place?.compareTo(other.section_place ?: 0) ?: 0
            }

        }


        @Keep
        @Parcelize
        data class DynamicSectionsData(
                var list: ArrayList<DynamicScreenSections>? = null
        ) : Parcelable
    }
}