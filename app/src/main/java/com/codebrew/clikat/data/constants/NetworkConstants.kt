package com.codebrew.clikat.data.constants


interface NetworkConstants {

    companion object {
        const val SUCCESS = 200
        const val AUTHFAILED = 401
        const val SUCCESS_CODE = 4
        const val SUCCESS_CODEN = "4"
        const val STATUS_CODE = 201
        const val AUTH_MSG = "AuthError"

        const val EVALON_TEST_LINK ="https://api.demo.convergepay.com/hosted-payments?ssl_txn_auth_token="
        const val EVALON_PROD_LINK ="https://api.convergepay.com/hosted-payments?ssl_txn_auth_token="

        const val VERIFY_USER_CODE="/v1/common/agent/boot"

        const val WISHLIST = "/favourite_product"
        const val SUPPLIERS_WISH_LIST = "/get_my_favourite"
        const val SUPPLIER_DETAIL = "/supplier_details"
        const val ADD_TO_FAVOURITE_SUPL = "/add_to_favourite"
        const val UN_FAVOURITE_SUPL = "/un_favourite"
        const val GET_SETTING = "/getSettings"
        const val GET_ALL_COUNTRY = "/get_all_country"
        const val GET_ALL_CITY = "/get_all_city"
        const val GET_ALL_AREA = "/get_all_area"
        const val GET_COMPLETE_ORDER_STATUS = "/get_total_pending_schedule"
        const val GET_ALL_CATEGORY_NEW = "/get_all_category_new"
        const val GET_SUPPLIER_LIST = "/home/supplier_list"
        const val GET_SUPPLIER_LIST_V1 = "/home/supplier_list/V1"
        const val GET_SUPPLIER_LIST_V6 = "/home/supplier_list/V6"
        const val GET_SUPPLIER_LIST_V7 = "/home/supplier_list/V7"
        const val GET_SUPPLIER_LIST_V2 = "/home/supplier_list/V2"
        const val GET_SIGNATURE_PLATES = "/get_signature_plates"
        const val GET_SIGNATURE_MENUS = "/get_signature_menu"
        const val GET_CRAVE_MANIA = "/get_all_branches_for_mania_app_version"
        const val CRAVE_MANIA_BANNER = "/get_banner_images"
        const val GET_ALL_OFFER_LIST="/get_all_offer_list"
        const val GET_ALL_OFFERS_LIST_V1="/get_all_offer_list/v1"
        const val GET_ALL_CUSTOMER_ADRS="/get_all_customer_address_V1"//"/get_all_customer_address"
        const val GET_ALL_CUSTOMER_ADRS1="/get_all_customer_address"

        const val ADD_CUSTOMER_ADRS="/add_new_address"
        const val EDIT_CUSTOMER_ADRS="/edit_address"
        const val DELETE_CUSTOMER_ADRS="/delete_customer_address"
        const val PRODUCT_FILTERATION="/v1/product_filteration"
        const val CHAT_LISTING = "/getChat"
        const val REFERRAL_AMT = "/user/referralAmount"
        const val REFERRAL_LIST = "/user/myReferral"
        const val UPLOAD_DOC = "/user/order/addReceipt"
        const val DIALOG_TOKEN = "/common/dialog/token"
        const val CATEGORY_WISE_SUPPLIERS_LIST="/user/category_wise_suppliers"
        const val GET_BRANCH_LIST = "/get_branch_list"
        const val GET_USERDETAIL = "/user/car_details"
    }
}