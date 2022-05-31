package com.codebrew.clikat.data.constants

import com.codebrew.clikat.data.DeliveryType
import com.codebrew.clikat.data.SearchType
import com.google.android.gms.maps.model.LatLng

interface AppConstants {
    companion object {
        const val LIMIT = 20
        const val LIMIT_SUPPLIERS = 1000
        const val CameraGalleryPicker = 123
        const val CameraPicker = 124
        const val GalleyPicker = 125
        const val REQUEST_CODE_PDF=126
        const val REQUEST_CODE_LOCATION = 1002
        const val REQUEST_CALL = 323
        const val REQUEST_AUDIO = 324
        const val CANCELREQUEST = 5
        const val MULTI_CAM_STOR = 1254
        const val PIC_CROP = 457
        const val PLACE_PICKER_REQUEST = 482
        const val PERMISSION_REQUEST = 786
        const val RC_LOCATION_PERM = 787
        const val REQUEST_PRODUCT_FAV = 789
        const val REQUEST_ADDRESS_ADD = 790
        const val REQUEST_USER_PROFILE = 791
        const val REQUEST_WISH_PROD = 792
        const val REQUEST_WISH_LIST = 793
        const val REQUEST_AGENT_DETAIL = 794
        const val REQUEST_CART_LOGIN_BOOKING = 795
        const val REQUEST_PRODUCT_FAVOURITE = 796
        const val REQUEST_PAYMENT_DEBIT_CARD = 798
        const val REQUEST_PAYMENT_OPTION = 797
        const val REQUEST_CARD_ADD = 799
        const val REQUEST_SQUARE_PAY = 800
        const val REQUEST_SQUARE_LOGIN = 801
        const val REQUEST_PRES_UPLOAD = 802
        const val REQUEST_ADD_MONEY = 803
        const val PEACH_PAYMENT = 804
        const val REQUEST_DIALOG_CART = 80
        const val REQUEST_CODE_SCANNER = 81
        const val REQ_CODE_GALLERY_IMAGE = 1
        const val REQ_CODE_CAMERA_IMAGE = 2
        var isChatOpen = false
        var currentOrderId = "0"
        val UPLOAD_DOCUMENTS = 1005
        val PROFILE = 1006

        //Filter Event constant
        const val CATEGORY_SELECT = "category_select"
        const val SUBCATEGORY_ADD = "subcategory_add"
        const val SUBCATEGORY_REMOVE = "subcategory_remove"
        const val SUBCATEGORY_DETAIL = "subcategory_detail"
        const val SUBCATEGORY_BACKPRESS = "backpressed"
        const val SUBCATEGORY_CATEGORY = "subcat_cat"
        const val BANNER_PROMO_BEAN = "bannerPromoBean"


        const val ROAD_MAP_TYPE ="RoadMapType"
        const val CRAVE_MANIA ="RoadMapType"


        const val ROAD_MAP_TYPE_DELIVERY ="Delivery"
        const val ROAD_MAP_TYPE_BOOK_TABLE ="Book Table"
        const val ROAD_MAP_TYPE_PICK_UP ="Pick Up"
        const val ROAD_MAP_TYPE_SIGNATURE ="Signature"
        const val ROAD_MAP_TYPE_CRAVE_MANIA ="Crave Mania"


        const val CANCEL_EVENT = "cancel"
        const val NOTIFICATION_EVENT = "orderType"

        var DELIVERY_OPTIONS = DeliveryType.DeliveryOrder.type
        var SEARCH_OPTION = SearchType.TYPE_PROD.type
        var CURRENCY_SYMBOL = "QAR"
        var DEFAULT_CURRENCY_SYMBOL = "QAR"
        var CONVERSION_RATE = 1f
        var COUNTRY_ISO = "US"
        var USER_LATLNG = LatLng(0.0, 0.0)
        var APP_SUB_TYPE = -1
        var APP_SAVED_SUB_TYPE = -1
        var LANG_CODE = "en"

        const val MESSAGE_TYPE_TEXT = "text"
        const val MESSAGE_TYPE_IMAGE = "image"
        const val DECIMAL_INTERVAL = 0.15f
        var isOnlyAuth: Boolean = false

        val ADMIN_SUPPLIER_DETAIL="/#!/supplier-registration"
        val MILES_TO_KM=0.621371
    }
}