package com.codebrew.clikat.utils.configurations

import com.codebrew.clikat.BuildConfig
import com.codebrew.clikat.R
import com.codebrew.clikat.data.AppDataType
import com.codebrew.clikat.modal.AppGlobal
import com.codebrew.clikat.modal.other.SettingModel.DataBean.AppTerminology
import com.codebrew.clikat.modal.other.SettingModel.DataBean.Terminology
import com.codebrew.clikat.utils.ClikatConstants
import com.codebrew.clikat.utils.Utils.getStringByLocale
import com.google.gson.annotations.SerializedName
import java.util.*

class TextConfig(private val type: Int, terminology: Terminology?, languageId: String) {
    @kotlin.jvm.JvmField
    var recomended = R.string.recomended.toString()

    @kotlin.jvm.JvmField
    var promotions: String? = null

    @kotlin.jvm.JvmField
    var my_favorites: String? = null

    @kotlin.jvm.JvmField
    var order_history: String? = null

    @kotlin.jvm.JvmField
    var track_my_order: String? = null

    @kotlin.jvm.JvmField
    var rate_my_order: String? = null

    @kotlin.jvm.JvmField
    var upcoming_order: String? = null

    @kotlin.jvm.JvmField
    var loyalty_points: String? = null

    @kotlin.jvm.JvmField
    var share_app: String? = null

    @kotlin.jvm.JvmField
    var settings: String? = null

    @kotlin.jvm.JvmField
    var login: String? = null

    @kotlin.jvm.JvmField
    var forgot_password: String? = null

    @kotlin.jvm.JvmField
    var _continue: String? = null

    @kotlin.jvm.JvmField
    var signup_otp_msg: String? = null

    @kotlin.jvm.JvmField
    var send_otp: String? = null

    @kotlin.jvm.JvmField
    var tap_to_add_profile_pic: String? = null

    @kotlin.jvm.JvmField
    var finish: String? = null

    @kotlin.jvm.JvmField
    var oreder_scheduler: String? = null

    @kotlin.jvm.JvmField
    var delivery_address: String? = null

    @kotlin.jvm.JvmField
    var hint_email: String? = null

    @kotlin.jvm.JvmField
    var hint_password: String? = null

    @kotlin.jvm.JvmField
    var payment_method: String? = null

    @kotlin.jvm.JvmField
    var set_schedule: String? = null

    @kotlin.jvm.JvmField
    var net_total: String? = null

    @kotlin.jvm.JvmField
    var delivery_speed: String? = null

    @kotlin.jvm.JvmField
    var delivery_charges: String? = null

    @kotlin.jvm.JvmField
    var empty: String? = null

    @kotlin.jvm.JvmField
    var reviews: String? = null

    @kotlin.jvm.JvmField
    var NoCart: String? = null

    @kotlin.jvm.JvmField
    var save: String? = null

    @kotlin.jvm.JvmField
    var language: String? = null

    @kotlin.jvm.JvmField
    var address: String? = null

    @kotlin.jvm.JvmField
    var pickkup: String? = null

    @kotlin.jvm.JvmField
    var items: String? = null

    @kotlin.jvm.JvmField
    var net_payable: String? = null

    @kotlin.jvm.JvmField
    var additional_remarks: String? = null

    @kotlin.jvm.JvmField
    var update: String? = null

    @kotlin.jvm.JvmField
    var cancel: String? = null

    @kotlin.jvm.JvmField
    var open: String? = null

    @kotlin.jvm.JvmField
    var filter: String? = null

    @kotlin.jvm.JvmField
    var clearCart: String? = null

    @kotlin.jvm.JvmField
    var apply: String? = null

    @kotlin.jvm.JvmField
    var clear: String? = null

    @kotlin.jvm.JvmField
    var close: String? = null

    @kotlin.jvm.JvmField
    var both: String? = null

    @kotlin.jvm.JvmField
    var card: String? = null

    @kotlin.jvm.JvmField
    var cash: String? = null

    @kotlin.jvm.JvmField
    var rating: String? = null

    @kotlin.jvm.JvmField
    var busy: String? = null

    @kotlin.jvm.JvmField
    var logout: String? = null

    @kotlin.jvm.JvmField
    var points: String? = null

    @kotlin.jvm.JvmField
    var no_internet_connection_found: String? = null

    @kotlin.jvm.JvmField
    var retry: String? = null


    @kotlin.jvm.JvmField
    var nothing_found: String? = null

    @kotlin.jvm.JvmField
    var skip: String? = null

    @kotlin.jvm.JvmField
    var success: String? = null

    @kotlin.jvm.JvmField
    var status: String? = null

    @kotlin.jvm.JvmField
    var minimum_order: String? = null

    @kotlin.jvm.JvmField
    var delivery_time: String? = null

    @kotlin.jvm.JvmField
    var payment_options: String? = null

    @kotlin.jvm.JvmField
    var total: String? = null

    @kotlin.jvm.JvmField
    var others: String? = null

    @kotlin.jvm.JvmField
    var view_all = R.string.view_all.toString()

    @kotlin.jvm.JvmField
    var supplier_name: String? = null

    @kotlin.jvm.JvmField
    var terms: String? = null

    @kotlin.jvm.JvmField
    var hour: String? = null

    @kotlin.jvm.JvmField
    var day: String? = null

    @kotlin.jvm.JvmField
    var about_us: String? = null

    @kotlin.jvm.JvmField
    var add: String? = null

    @kotlin.jvm.JvmField
    var scheduled_order: String? = null

    @kotlin.jvm.JvmField
    var yes: String? = null

    @kotlin.jvm.JvmField
    var loyality_orders: String? = null

    @kotlin.jvm.JvmField
    var search: String? = null

    @kotlin.jvm.JvmField
    var discount: String? = null

    @kotlin.jvm.JvmField
    var error: String? = null


    //Constants
    @kotlin.jvm.JvmField
    var viewtype: String? = null

    @kotlin.jvm.JvmField
    var supplier: String? = null

    @kotlin.jvm.JvmField
    var product: String? = null

    @kotlin.jvm.JvmField
    var products: String? = null

    @kotlin.jvm.JvmField
    var order: String? = null

    @kotlin.jvm.JvmField
    var orders: String? = null

    @kotlin.jvm.JvmField
    var agent: String? = null

    @kotlin.jvm.JvmField
    var agents: String? = null

    @kotlin.jvm.JvmField
    var suppliers: String? = null

    @kotlin.jvm.JvmField
    var brand: String? = null

    @kotlin.jvm.JvmField
    var brands: String? = null

    @kotlin.jvm.JvmField
    var category: String? = null

    @kotlin.jvm.JvmField
    var categories: String? = null

    @kotlin.jvm.JvmField
    var catalogue: String? = null

    @kotlin.jvm.JvmField
    var otherTab: String? = null

    @kotlin.jvm.JvmField
    var wishlist: String? = null

    @kotlin.jvm.JvmField
    var tax: String? = null

    @kotlin.jvm.JvmField
    var order_now: String? = null

    @kotlin.jvm.JvmField
    var delivery_tab: String? = null

    @kotlin.jvm.JvmField
    var pickup_tab: String? = null

    @kotlin.jvm.JvmField
    var location_text: String? = null

    @kotlin.jvm.JvmField
    var razor_pay: String? = null

    @kotlin.jvm.JvmField
    var donate_to_someone: String? = null

    @kotlin.jvm.JvmField
    var wallet: String? = null

    @kotlin.jvm.JvmField
    var my_subscription: String? = null

    @kotlin.jvm.JvmField
    var supplier_service_fee: String? = null

    @kotlin.jvm.JvmField
    var suppliers_near_you: String? = null

    @kotlin.jvm.JvmField
    var no_cart_data: String? = null

    @kotlin.jvm.JvmField
    var choose_payment: String? = null

    @kotlin.jvm.JvmField
    var my_fatoorah: String? = null

    @kotlin.jvm.JvmField
    var unserviceable: String? = null

    @kotlin.jvm.JvmField
    var popularRestaurantes: String? = null

    @kotlin.jvm.JvmField
    var noOrderFound: String? = null

    @kotlin.jvm.JvmField
    var customisable: String? = null

    @kotlin.jvm.JvmField
    var paymentDetails: String? = null

    @kotlin.jvm.JvmField
    var email: String? = null

    @kotlin.jvm.JvmField
    var shareScreenshot: String? = null

    @kotlin.jvm.JvmField
    var choosePaymentReceipt: String? = null

    @kotlin.jvm.JvmField
    var orderNow: String? = null

    @kotlin.jvm.JvmField
    var cod: String? = null

    @kotlin.jvm.JvmField
    var braintree: String? = null

    @kotlin.jvm.JvmField
    var what_are_u_looking_for: String? = null

    @kotlin.jvm.JvmField
    var special_offers: String? = null

    @kotlin.jvm.JvmField
    var proceed: String? = null

    @kotlin.jvm.JvmField
    var recommended_supplier: String? = null

    @kotlin.jvm.JvmField
    var otp: String? = null

    @kotlin.jvm.JvmField
    var pending_orders: String? = null

    @kotlin.jvm.JvmField
    var completed_orders: String? = null

    @kotlin.jvm.JvmField
    var tags: String? = null

    private fun changeNaming(type: Int, terminology: Terminology?, languageId: String) {

        var appTerminology: AppTerminology? = null
        if (terminology != null) {
            appTerminology = if (languageId == ClikatConstants.ENGLISH_FULL || languageId == ClikatConstants.ENGLISH_SHORT) {
                terminology.english
            } else {
                terminology.other
            }
        }

        when (type) {
            AppDataType.Food.type -> {
                supplier = if (checkTerminolgy(appTerminology, appTerminology?.supplier)) appTerminology?.supplier else AppGlobal.context?.getStringByLocale(R.string.restaurant, Locale.getDefault())
                suppliers = if (checkTerminolgy(appTerminology, appTerminology?.suppliers)) appTerminology?.suppliers else AppGlobal.context?.getStringByLocale(R.string.restaurants, Locale.getDefault())
                product = if (checkTerminolgy(appTerminology, appTerminology?.product)) appTerminology?.product else "Food Item"
                products = if (checkTerminolgy(appTerminology, appTerminology?.products)) appTerminology?.products else "Food Items"
                order = if (checkTerminolgy(appTerminology, appTerminology?.order)) appTerminology?.order else AppGlobal.context?.getStringByLocale(R.string.orders_, Locale.getDefault())
                orders = if (checkTerminolgy(appTerminology, appTerminology?.orders)) appTerminology?.orders else AppGlobal.context?.getStringByLocale(R.string.orders, Locale.getDefault())
                agent = if (checkTerminolgy(appTerminology, appTerminology?.agent)) appTerminology?.agent else "Agent"
                agents = if (checkTerminolgy(appTerminology, appTerminology?.agents)) appTerminology?.agents else "Agents"
                category = if (checkTerminolgy(appTerminology, appTerminology?.category)) appTerminology?.category else "Category"
                categories = if (checkTerminolgy(appTerminology, appTerminology?.categories)) appTerminology?.categories else "Categories"
                brand = if (checkTerminolgy(appTerminology, appTerminology?.brand)) appTerminology?.brand else "Brand"
                brands = if (checkTerminolgy(appTerminology, appTerminology?.brands)) appTerminology?.brands else "Brands"
                catalogue = if (checkTerminolgy(appTerminology, appTerminology?.catalogue)) appTerminology?.catalogue else AppGlobal.context?.getStringByLocale(R.string.menu, Locale.getDefault())

                tax = if (checkTerminolgy(appTerminology, appTerminology?.tax)) appTerminology?.tax else AppGlobal.context?.getStringByLocale(R.string.tax, Locale.getDefault())
                order_now = if (checkTerminolgy(appTerminology, appTerminology?.order_now)) appTerminology?.order_now else AppGlobal.context?.getStringByLocale(R.string.order_now, Locale.getDefault())
                delivery_tab = if (checkTerminolgy(appTerminology, appTerminology?.delivery_tab)) appTerminology?.delivery_tab else AppGlobal.context?.getStringByLocale(R.string.delivery_txt, Locale.getDefault())
                location_text = if (checkTerminolgy(appTerminology, appTerminology?.location)) appTerminology?.location else
                    AppGlobal.context?.getStringByLocale(R.string.location, Locale.getDefault())
                pickup_tab = if (checkTerminolgy(appTerminology, appTerminology?.selfpickup)) appTerminology?.selfpickup else AppGlobal.context?.getStringByLocale(R.string.pickup, Locale.getDefault())
            }

            AppDataType.HomeServ.type -> {
                supplier = if (checkTerminolgy(appTerminology, appTerminology?.supplier)) appTerminology?.supplier else "Service Provider"
                suppliers = if (checkTerminolgy(appTerminology, appTerminology?.suppliers)) appTerminology?.suppliers else "Service Providers"
                product = if (checkTerminolgy(appTerminology, appTerminology?.product)) appTerminology?.product else "Service"
                products = if (checkTerminolgy(appTerminology, appTerminology?.products)) appTerminology?.products else "Services"
                order = if (checkTerminolgy(appTerminology, appTerminology?.order)) appTerminology?.order else "Booking"
                orders = if (checkTerminolgy(appTerminology, appTerminology?.orders)) appTerminology?.orders else "Bookings"
                agent = if (checkTerminolgy(appTerminology, appTerminology?.agent)) appTerminology?.agent else "Agent"
                agents = if (checkTerminolgy(appTerminology, appTerminology?.agents)) appTerminology?.agents else "Agents"
                category = if (checkTerminolgy(appTerminology, appTerminology?.category)) appTerminology?.category else "Category"
                categories = if (checkTerminolgy(appTerminology, appTerminology?.categories)) appTerminology?.categories else "Categories"
                brand = if (checkTerminolgy(appTerminology, appTerminology?.brand)) appTerminology?.brand else "Brand"
                brands = if (checkTerminolgy(appTerminology, appTerminology?.brands)) appTerminology?.brands else "Brands"
                catalogue = if (checkTerminolgy(appTerminology, appTerminology?.catalogue)) appTerminology?.catalogue else "Services"
                tax = if (checkTerminolgy(appTerminology, appTerminology?.tax)) appTerminology?.tax else "Tax"
                order_now = if (checkTerminolgy(appTerminology, appTerminology?.order_now)) appTerminology?.order_now else AppGlobal.context?.getStringByLocale(R.string.place_booking, Locale.getDefault())
                delivery_tab = if (checkTerminolgy(appTerminology, appTerminology?.delivery_tab)) appTerminology?.delivery_tab else AppGlobal.context?.getStringByLocale(R.string.delivery_txt, Locale.getDefault())
                location_text = if (checkTerminolgy(appTerminology, appTerminology?.location)) appTerminology?.location else
                    AppGlobal.context?.getStringByLocale(R.string.location, Locale.getDefault())
            }

            else -> {
                supplier = if (checkTerminolgy(appTerminology, appTerminology?.supplier)) appTerminology?.supplier else "Supplier"
                suppliers = if (checkTerminolgy(appTerminology, appTerminology?.suppliers)) appTerminology?.suppliers else "Suppliers"
                product = if (checkTerminolgy(appTerminology, appTerminology?.product)) appTerminology?.product else "Product"
                products = if (checkTerminolgy(appTerminology, appTerminology?.products)) appTerminology?.products else "Products"
                order = if (checkTerminolgy(appTerminology, appTerminology?.order)) appTerminology?.order else AppGlobal.context?.getStringByLocale(R.string.orders_, Locale.getDefault())
                orders = if (checkTerminolgy(appTerminology, appTerminology?.orders)) appTerminology?.orders else AppGlobal.context?.getStringByLocale(R.string.orders, Locale.getDefault())
                agent = if (checkTerminolgy(appTerminology, appTerminology?.agent)) appTerminology?.agent else "Agent"
                agents = if (checkTerminolgy(appTerminology, appTerminology?.agents)) appTerminology?.agents else "Agents"
                category = if (checkTerminolgy(appTerminology, appTerminology?.category)) appTerminology?.category else "Category"
                categories = if (checkTerminolgy(appTerminology, appTerminology?.categories)) appTerminology?.categories else "Categories"
                brand = if (checkTerminolgy(appTerminology, appTerminology?.brand)) appTerminology?.brand else "Brand"
                brands = if (checkTerminolgy(appTerminology, appTerminology?.brands)) appTerminology?.brands else "Brands"
                catalogue = if (checkTerminolgy(appTerminology, appTerminology?.catalogue)) appTerminology?.catalogue else "Catalogue"
                tax = if (checkTerminolgy(appTerminology, appTerminology?.tax)) appTerminology?.tax else "Tax"
                order_now = if (checkTerminolgy(appTerminology, appTerminology?.order_now)) appTerminology?.order_now else AppGlobal.context?.getString(R.string.place_order, order)
                delivery_tab = if (checkTerminolgy(appTerminology, appTerminology?.delivery_tab)) appTerminology?.delivery_tab else AppGlobal.context?.getStringByLocale(R.string.delivery_txt, Locale.getDefault())
                location_text = if (checkTerminolgy(appTerminology, appTerminology?.location)) appTerminology?.location else
                    AppGlobal.context?.getStringByLocale(R.string.location, Locale.getDefault())
            }
        }
        wishlist = if (checkTerminolgy(appTerminology, appTerminology?.wishlist)) appTerminology?.wishlist else AppGlobal.context?.getStringByLocale(R.string.wishlist, Locale.getDefault())
        otherTab = if (checkTerminolgy(appTerminology, appTerminology?.others_tab)) appTerminology?.others_tab else AppGlobal.context?.getStringByLocale(R.string.others, Locale.getDefault())
        wallet = if (checkTerminolgy(appTerminology, appTerminology?.wallet)) appTerminology?.wallet else AppGlobal.context?.getStringByLocale(R.string.wallet, Locale.getDefault())
        my_subscription = if (checkTerminolgy(appTerminology, appTerminology?.my_subscription)) appTerminology?.my_subscription else AppGlobal.context?.getStringByLocale(R.string.subscriptions, Locale.getDefault())
        loyalty_points = if (checkTerminolgy(appTerminology, appTerminology?.loyalty_points)) appTerminology?.loyalty_points else AppGlobal.context?.getStringByLocale(R.string.loyality_points, Locale.getDefault())
        supplier_service_fee = if (checkTerminolgy(appTerminology, appTerminology?.supplier_service_fee)) appTerminology?.supplier_service_fee
        else { if (BuildConfig.CLIENT_CODE == "skipp_0631") AppGlobal.context?.getStringByLocale(R.string.service_charge, Locale.getDefault(), "") else AppGlobal.context?.getStringByLocale(R.string.supplier_service_charge, Locale.getDefault(), supplier.toString()?:"")}
        suppliers_near_you = if (checkTerminolgy(appTerminology, appTerminology?.suppliers_near_you)) appTerminology?.suppliers_near_you else AppGlobal.context?.getStringByLocale(R.string.loyality_points, Locale.getDefault())
        no_cart_data = if (checkTerminolgy(appTerminology, appTerminology?.no_cart_data)) appTerminology?.no_cart_data else AppGlobal.context?.getStringByLocale(R.string.NoCart, Locale.getDefault())
        razor_pay = if (checkTerminolgy(appTerminology, appTerminology?.razor_pay)) appTerminology?.razor_pay else AppGlobal.context?.getStringByLocale(R.string.razor_pay, Locale.getDefault())
        choose_payment = if (checkTerminolgy(appTerminology, appTerminology?.choose_payment)) appTerminology?.choose_payment else AppGlobal.context?.getStringByLocale(R.string.choose_payment, Locale.getDefault())
        my_fatoorah = if (checkTerminolgy(appTerminology, appTerminology?.my_fatoorah)) appTerminology?.my_fatoorah else AppGlobal.context?.getStringByLocale(R.string.myFatoora, Locale.getDefault())
        unserviceable = if (checkTerminolgy(appTerminology, appTerminology?.unserviceable)) appTerminology?.unserviceable else AppGlobal.context?.getStringByLocale(R.string.un_service_able, Locale.getDefault())
        popularRestaurantes = if (checkTerminolgy(appTerminology, appTerminology?.popularRestaurantes)) appTerminology?.popularRestaurantes else AppGlobal.context?.getStringByLocale(R.string.popular_tag, Locale.getDefault(),supplier.toString()?:"")
        noOrderFound = if (checkTerminolgy(appTerminology, appTerminology?.noOrderFound)) appTerminology?.noOrderFound else AppGlobal.context?.getStringByLocale(R.string.no_order_found, Locale.getDefault(), order.toString()?:"")
        customisable = if (checkTerminolgy(appTerminology, appTerminology?.customisable)) appTerminology?.customisable else AppGlobal.context?.getStringByLocale(R.string.customizable, Locale.getDefault())
        paymentDetails = if (checkTerminolgy(appTerminology, appTerminology?.paymentDetails)) appTerminology?.paymentDetails else AppGlobal.context?.getStringByLocale(R.string.payment_details, Locale.getDefault())
        email = if (checkTerminolgy(appTerminology, appTerminology?.email)) appTerminology?.email else null
        shareScreenshot = if (checkTerminolgy(appTerminology, appTerminology?.shareScreenshot)) appTerminology?.shareScreenshot else null
        choosePaymentReceipt = if (checkTerminolgy(appTerminology, appTerminology?.choosePaymentReceipt)) appTerminology?.choosePaymentReceipt else AppGlobal.context?.getStringByLocale(R.string.choose_payment_receipt_to_upload, Locale.getDefault())
        orderNow = if (checkTerminolgy(appTerminology, appTerminology?.orderNow)) appTerminology?.orderNow else AppGlobal.context?.getStringByLocale(R.string.order_now, Locale.getDefault())
        what_are_u_looking_for=if (checkTerminolgy(appTerminology, appTerminology?.what_are_u_looking_for)) appTerminology?.what_are_u_looking_for else AppGlobal.context?.getStringByLocale(R.string.what_are_you_looking_for, Locale.getDefault())
        cod = if (checkTerminolgy(appTerminology, appTerminology?.cod)) appTerminology?.cod else AppGlobal.context?.getStringByLocale(R.string.payment_cash, Locale.getDefault())
        braintree = if (checkTerminolgy(appTerminology, appTerminology?.braintree)) appTerminology?.braintree else AppGlobal.context?.getStringByLocale(R.string.text_braintree, Locale.getDefault())
        special_offers = if (checkTerminolgy(appTerminology, appTerminology?.special_offers)) appTerminology?.special_offers else AppGlobal.context?.getStringByLocale(R.string.special_offers, Locale.getDefault())
        proceed = if (checkTerminolgy(appTerminology, appTerminology?.proceed)) appTerminology?.proceed else AppGlobal.context?.getStringByLocale(R.string.proceed, Locale.getDefault())
        recommended_supplier = if (checkTerminolgy(appTerminology, appTerminology?.recommended_supplier)) appTerminology?.recommended_supplier else AppGlobal.context?.getStringByLocale(R.string.recommed_supplier, Locale.getDefault(),supplier.toString()?:"")
        otp = if (checkTerminolgy(appTerminology, appTerminology?.otp)) appTerminology?.otp else AppGlobal.context?.getStringByLocale(R.string.otp_verification, Locale.getDefault())
        pending_orders = if (checkTerminolgy(appTerminology, appTerminology?.pending_orders)) appTerminology?.pending_orders else AppGlobal.context?.getStringByLocale(R.string.pending_orders, Locale.getDefault(), orders.toString())
        completed_orders = if (checkTerminolgy(appTerminology, appTerminology?.completed_orders)) appTerminology?.completed_orders else AppGlobal.context?.getStringByLocale(R.string.completed_orders, Locale.getDefault(), orders.toString())
        tags = if (checkTerminolgy(appTerminology, appTerminology?.tags)) appTerminology?.tags else AppGlobal.context?.getStringByLocale(R.string.tags, Locale.getDefault(),":")

    }

    private fun checkTerminolgy(appTerminology: AppTerminology?, naming: String?): Boolean {
        return appTerminology != null && naming?.isNotEmpty() == true
    }

    init {
        changeNaming(type, terminology, languageId)
    }
}