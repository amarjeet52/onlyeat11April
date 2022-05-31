package com.codebrew.clikat.app_utils


import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.annotation.SuppressLint
import android.annotation.TargetApi
import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.location.Address
import android.location.Geocoder
import android.location.LocationManager
import android.media.Image
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.view.View
import android.view.ViewAnimationUtils
import android.view.Window
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.Group
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import ch.datatrans.payment.Payment
import ch.datatrans.payment.PaymentMethod
import ch.datatrans.payment.PaymentMethodType
import ch.datatrans.payment.android.DisplayContext
import ch.datatrans.payment.android.IPaymentProcessStateListener
import ch.datatrans.payment.android.PaymentProcessAndroid
import ch.datatrans.payment.android.ResourceProvider
import com.codebrew.clikat.R
import com.codebrew.clikat.app_utils.extension.launchActivity
import com.codebrew.clikat.data.AppDataType
import com.codebrew.clikat.data.DataManager
import com.codebrew.clikat.data.OrderStatus
import com.codebrew.clikat.data.constants.AppConstants
import com.codebrew.clikat.data.constants.AppConstants.Companion.CURRENCY_SYMBOL
import com.codebrew.clikat.data.constants.PrefenceConstants
import com.codebrew.clikat.data.model.api.AddressBean
import com.codebrew.clikat.data.model.api.Currency
import com.codebrew.clikat.data.model.others.AppCartModel
import com.codebrew.clikat.data.model.others.CommonEvent
import com.codebrew.clikat.data.model.others.CustomPayModel
import com.codebrew.clikat.data.preferences.PreferenceHelper
import com.codebrew.clikat.modal.CartInfo
import com.codebrew.clikat.modal.CartList
import com.codebrew.clikat.modal.PojoSignUp
import com.codebrew.clikat.modal.ProductAddon
import com.codebrew.clikat.modal.other.*
import com.codebrew.clikat.module.bottom_navigation.MainScreenActivity
import com.codebrew.clikat.module.essentialHome.ServiceListActivity
import com.codebrew.clikat.module.home_screen.listeners.OnSortByListenerClicked
import com.codebrew.clikat.module.login.LoginActivity
import com.codebrew.clikat.module.login.LoginActivityNew
import com.codebrew.clikat.module.new_signup.SigninActivity
import com.codebrew.clikat.module.new_signup.create_account.CreateAccActivity
import com.codebrew.clikat.module.new_signup.signup.SignInActivityNew
import com.codebrew.clikat.module.product.product_listing.DialogListener
import com.codebrew.clikat.module.roadmap.RoadMapActivity
import com.codebrew.clikat.preferences.DataNames
import com.codebrew.clikat.utils.ClikatConstants
import com.codebrew.clikat.utils.StaticFunction
import com.codebrew.clikat.utils.Utils
import com.codebrew.clikat.utils.configurations.AppConfiguration
import com.codebrew.clikat.utils.configurations.Configurations
import com.codebrew.clikat.utils.configurations.TextConfig
import com.crystal.crystalrangeseekbar.widgets.CrystalRangeSeekbar
import com.google.android.gms.maps.model.LatLng
import com.google.android.material.button.MaterialButton
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputEditText
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.hbb20.CountryCodePicker
import com.paytabs.paytabs_sdk.payment.ui.activities.PayTabActivity
import com.paytabs.paytabs_sdk.utils.PaymentParams
import org.greenrobot.eventbus.EventBus
import retrofit2.HttpException
import java.io.IOException
import java.lang.reflect.Type
import java.math.RoundingMode
import java.net.ConnectException
import java.net.UnknownHostException
import java.text.*
import java.util.*
import java.util.regex.Pattern
import javax.inject.Inject
import kotlin.collections.ArrayList
import kotlin.math.hypot

/**
 *
 *
 * Contains commonly used methods in an Android App
 */
class AppUtils @Inject constructor(private val mContext: Context) {


    @Inject
    lateinit var mPreferenceHelper: PreferenceHelper

    @Inject
    lateinit var dataManager: DataManager

    @Inject
    lateinit var mDialogsUtil: DialogsUtil


/*    private val mContext: Context? = null

    fun ImageUtility(context: Context): ??? {
        this.mContext = context
    }*/
    /**
     * check if user has enabled Gps of device
     *
     * @return true or false depending upon device Gps status
     */
    val isGpsEnabled: Boolean
        get() {
            val manager = mContext.getSystemService(Context.LOCATION_SERVICE) as LocationManager
            return manager.isProviderEnabled(LocationManager.GPS_PROVIDER)
        }

    fun checkEmail(email: String): Boolean {
        return EMAIL_ADDRESS_PATTERN.matcher(email.trim { it <= ' ' }).matches()
    }

    fun isValidEmail(target: CharSequence?): Boolean {
        return if (target == null) {
            false
        } else {
            android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches()
        }
    }

    /**
     * Description : Hide the soft keyboard
     *
     * @param view : Pass the current view
     */
    fun hideSoftKeyboard(view: View) {
        val inputMethodManager = mContext.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
    }

    fun hideSoftKeyboardOut(activity: Activity) {
        val inputMethodManager = activity.getSystemService(
                Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(
                activity.currentFocus!!.windowToken, 0)
    }

    fun getCurrentTableData() = dataManager.getCurrentTableData()

    /*
*getting name from int
* */
    fun getMonth(month: Int): String {
        return DateFormatSymbols().months[month]
    }

    /**
     * Show snackbar
     *
     * @param view view clicked
     * @param text text to be displayed on snackbar
     */
    fun showSnackBar(view: View, text: String) {
        Snackbar.make(view, text, Snackbar.LENGTH_LONG).show()
    }


    /**
     * Show snackbar
     *
     * @param text text to be displayed on Toast
     */
    fun showToast(text: String) {
        Toast.makeText(mContext, text, Toast.LENGTH_SHORT).show()
    }


    //return error message from webservice error code
    private fun getErrorMessage(throwable: Throwable): String {
        val errorMessage: String
        if (throwable is HttpException || throwable is UnknownHostException
                || throwable is ConnectException) {
            errorMessage = "Something went wrong"
        } else {
            errorMessage = "Unfortunately an error has occurred!"
        }
        return errorMessage
    }

    /**
     * Redirect user to enable GPS
     */
    fun goToGpsSettings() {
        val callGPSSettingIntent = Intent(
                Settings.ACTION_LOCATION_SOURCE_SETTINGS)
        mContext.startActivity(callGPSSettingIntent)
    }

    fun checkTwillioAuthFeature(): Boolean {

        dataManager.getKeyValue(DataNames.FEATURE_DATA, PrefenceConstants.TYPE_STRING).toString().let {
            val listType: Type = object : TypeToken<java.util.ArrayList<SettingModel.DataBean.FeatureData?>?>() {}.type
            val featureList: java.util.ArrayList<SettingModel.DataBean.FeatureData> = Gson().fromJson(it, listType)

            return if (featureList.any { it.name == "twilio_authy" }) {

                val isActive = featureList.firstOrNull { it.name == "twilio_authy" }?.is_active ?: 0

                isActive == 1

            } else
                false
        }

    }

    /**
     * check if user has permissions for the asked permissions
     */
    fun hasPermissions(context: Context?, vararg permissions: String): Boolean {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && context != null) {
            for (permission in permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false
                }
            }
        }
        return true
    }


    fun round(datavalue: Double, places: Int): Double {
        var value = datavalue
        if (places < 0) throw IllegalArgumentException()

        val factor = Math.pow(10.0, places.toDouble()).toLong()
        value = value * factor
        val tmp = Math.round(value)
        return tmp.toDouble() / factor
    }


    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    fun changeStatusBarColor(activity: Activity) {
        val window = activity.window
        // clear FLAG_TRANSLUCENT_STATUS flag:
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        // add FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS flag to the window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        // finally change the color
        window.statusBarColor = ContextCompat.getColor(activity, R.color.colorPrimary)
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    fun changeStatusBarTranparent(activity: Activity) {
        val window = activity.window
        // clear FLAG_TRANSLUCENT_STATUS flag:
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        // add FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS flag to the window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        // finally change the color
        window.statusBarColor = ContextCompat.getColor(activity, android.R.color.transparent)
    }

    fun formatPrice(price: String): String {
        val formatter = DecimalFormat("#,###,###")
        return formatter.format(java.lang.Double.valueOf(price))
    }


    /**
     * Convert date from one format to another
     *
     * @param dateToConvert date to be converted
     * @param formatFrom    the format of the date to be converted
     * @param formatTo      the format of date you want the output
     * @return date in string as per the entered formats
     */
    @SuppressLint("SimpleDateFormat")
    fun convertDateOneToAnother(dateToConvert: String, formatFrom: String, formatTo: String): String? {
        var outputDateStr: String? = null
        val inputFormat = SimpleDateFormat(formatFrom, Locale(if (AppConstants.LANG_CODE == "ar") "en" else AppConstants.LANG_CODE))
        // inputFormat.timeZone = TimeZone.getDefault()
        val outputFormat = SimpleDateFormat(formatTo, Locale(if (AppConstants.LANG_CODE == "ar") "en" else AppConstants.LANG_CODE))
        outputFormat.timeZone = TimeZone.getDefault()
        val date: Date
        try {
            date = inputFormat.parse(dateToConvert) ?: Date()
            outputDateStr = outputFormat.format(date)
        } catch (e: ParseException) {
            e.printStackTrace()
        }

        return outputDateStr
    }


    @SuppressLint("SimpleDateFormat")
    @RequiresApi(Build.VERSION_CODES.O)
    fun convertTimeInMiliSeconds(scheduleDate: String): Long? {
        val simple = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss")
        return try {
            val x = simple.parse(scheduleDate)
            x.time
        } catch (e: Exception) {
            0.toLong()
        }
    }


    /**
     * Convert date from one format to another
     *
     * @param dateToConvert date to be converted
     * @param formatFrom    the format of the date to be converted
     * @param formatTo      the format of date you want the output
     * @return date in string as per the entered formats
     */
    @SuppressLint("SimpleDateFormat")
    fun addMinutesToDate(dateToConvert: String, formatFrom: String, formatTo: String, minute: Int): String? {
        var outputDateStr: String? = null
        val inputFormat = SimpleDateFormat(formatFrom, Locale.ENGLISH)
        // inputFormat.timeZone = TimeZone.getDefault()
        val outputFormat = SimpleDateFormat(formatTo, Locale.ENGLISH)
        outputFormat.timeZone = TimeZone.getDefault()
        val date: Date
        try {
            val calendar = Calendar.getInstance(Locale.getDefault())
            date = inputFormat.parse(dateToConvert) ?: Date()
            calendar.time = date
            calendar.add(Calendar.MINUTE, minute)
            outputDateStr = outputFormat.format(date)
        } catch (e: ParseException) {
            e.printStackTrace()
        }

        return outputDateStr
    }


    /**
     * Convert date from one format to another
     *
     * @param dateToConvert date to be converted
     * @param formatFrom    the format of the date to be converted
     * @param formatTo      the format of date you want the output
     * @return date in string as per the entered formats
     */
    @SuppressLint("SimpleDateFormat")
    fun convertDateToAddDate(dateToConvert: String, formatFrom: String, formatTo: String, duration: Int): String? {
        var outputDateStr: String? = null
        val inputFormat = SimpleDateFormat(formatFrom, DateTimeUtils.timeLocale)
        // inputFormat.timeZone = TimeZone.getDefault()
        val outputFormat = SimpleDateFormat(formatTo, DateTimeUtils.timeLocale)
        outputFormat.timeZone = TimeZone.getDefault()
        try {

            val cal = Calendar.getInstance()
            cal.time = inputFormat.parse(dateToConvert) ?: Date()
            cal.add(Calendar.MINUTE, duration)
            outputDateStr = outputFormat.format(cal.time)
        } catch (e: ParseException) {
            e.printStackTrace()
        }

        return outputDateStr
    }


    fun getCalendarFormat(dateToConvert: String, dateFormat: String): Calendar? {
        val inputFormat = SimpleDateFormat(dateFormat, DateTimeUtils.timeLocale)
        var date: Date? = null
        try {
            date = inputFormat.parse(dateToConvert)
        } catch (e: ParseException) {
            e.printStackTrace()
        }

        val calendar = Calendar.getInstance(Locale.getDefault())
        calendar.time = date ?: Date()
        return calendar
    }

    fun getAddress(lat: Double, lng: Double): Address? {
        var addresses: List<Address?>? = null
        val geocoder: Geocoder = Geocoder(mContext, Locale.getDefault())
        try {
            addresses = geocoder.getFromLocation(lat, lng, 1) // Here 1 represent max location result to returned, by documents it recommended 1 to 5
        } catch (e: IOException) {
            e.printStackTrace()
        }
        /*  String city = addresses.get(0).getLocality();
        String state = addresses.get(0).getAdminArea();
        String country = addresses.get(0).getCountryName();
        String postalCode = addresses.get(0).getPostalCode();
        String knownName = addresses.get(0).getFeatureName();*/

        return if (addresses?.isNotEmpty() == true) addresses.get(0) else Address(Locale.getDefault())
    }


    fun getCartData(clientInform: SettingModel.DataBean.SettingData?, selectedCurrency: Currency?): AppCartModel {
        val cartModel = AppCartModel()
        val cartList: CartList? = getCartList()

        val count = cartList?.cartInfos?.size ?: 0
        if (count > 0) {

            cartModel.cartAvail = true
            var price = 0f
            val cartInfos = cartList?.cartInfos ?: mutableListOf()
            val supplierIds = ArrayList<Int>()
            var totalItem = 0f


            for (i in cartInfos.indices) {
                if (cartInfos[i].hourlyPrice.isNullOrEmpty()) {
                    price += cartInfos[i].price * cartInfos[i].quantity
                } else {
                    price += cartInfos[i].price
                }

                totalItem += cartInfos[i].quantity

                if (!supplierIds.contains(cartInfos[i].supplierId)) {
                    supplierIds.add(cartInfos[i].supplierId)
                }
            }

            cartModel.totalPrice = Utils.getPriceFormat(price, clientInform, selectedCurrency)
            cartModel.totalCount = totalItem

            if (supplierIds.size < 2) {
                cartModel.supplierName = cartInfos[0].supplierName ?: ""
            } else {
                cartModel.supplierName == ""
            }

        }

        return cartModel

    }

    fun checkVendorStatus(vendorId: Int?, vendorBranchId: Int?, branchFlow: String?): Boolean {
        val cartList: CartList? = getCartList()

        var isForSingleVendorSingleBranch: Boolean

        isForSingleVendorSingleBranch = cartList?.cartInfos?.any {
            it.supplierId != vendorId
        } ?: false

        if (mPreferenceHelper.isBranchFlow()) {
            if (branchFlow == "1") {
                isForSingleVendorSingleBranch = cartList?.cartInfos?.any {
                    it.supplierId != vendorId
                } ?: false
            }
            return isForSingleVendorSingleBranch
        } else {
            return isForSingleVendorSingleBranch
        }
    }

    fun checkAppType(cartList: CartList?): Boolean {

        return if (cartList?.cartInfos?.count() ?: 0 == 0) {
            false
        } else {
            cartList?.cartInfos?.count() ?: 0 != cartList?.cartInfos?.filter { it.appType == cartList.cartInfos?.first()?.appType ?: 0 }?.count()
        }

    }


    fun checkBookingFlow(mContext: Context, productId: Int?, listener: DialogListener?): Boolean {
        var bookingStatus = false


        val cartList: CartList? = getCartList()

        val cartCount = cartList?.cartInfos ?: mutableListOf()


        val bookingFlowBean = mPreferenceHelper.getGsonValue(DataNames.BOOKING_FLOW, SettingModel.DataBean.BookingFlowBean::class.java)

        if (cartCount.size > 0) {

            when (bookingFlowBean?.cart_flow) {
                0 -> {

                    if (cartCount.any { it.productId == productId }) {
                        mDialogsUtil.openAlertDialog(mContext, mContext.getString(R.string.clearCart_multiprod_quant, loadAppConfig(0).strings?.proceed), "Yes", "No", listener)
                    } else {
                        mDialogsUtil.openAlertDialog(mContext, mContext.getString(R.string.clearCart_product, loadAppConfig(0).strings?.proceed), "Yes", "No", listener)
                    }
                }
                2 ->
                    //check multiple product have single fixed_quantity

                    if (cartCount.any { it.productId == productId && it.quantity >= 1 }) {
                        mDialogsUtil.openAlertDialog(mContext, mContext.getString(R.string.clearCart_multiprod_quant, loadAppConfig(0).strings?.proceed), "Yes", "No", listener)
                    } else {
                        bookingStatus = true
                    }

                1 ->
                    //check single product have multiple fixed_quantity

                    if (cartCount.any { it.productId == productId }) {
                        bookingStatus = true
                    } else {
                        mDialogsUtil.openAlertDialog(mContext, mContext.getString(R.string.clearCart_multiprod_quant, loadAppConfig(0).strings?.proceed), "Yes", "No", listener)
                    }

                3 -> bookingStatus = true


            }
        } else {
            bookingStatus = true
        }

        return bookingStatus
    }

    fun checkBookingStatus(mContext: Context, list: List<ProductDataBean?>, productId: Int?, listener: DialogListener): Boolean {
        var bookingStatus = false
        val bookingFlowBean = mPreferenceHelper.getGsonValue(DataNames.BOOKING_FLOW, SettingModel.DataBean.BookingFlowBean::class.java)
        val cartCount = list.filter { it?.prod_quantity ?: 0f > 0f }
        if (cartCount.isNotEmpty()) {

            when (bookingFlowBean?.cart_flow) {
                0 -> {

                    if (cartCount.any { it?.product_id == productId }) {
                        mDialogsUtil.openAlertDialog(mContext, mContext.getString(R.string.clearCart_multiprod_quant, loadAppConfig(0).strings?.proceed), "Yes", "No", listener)
                    } else {
                        mDialogsUtil.openAlertDialog(mContext, mContext.getString(R.string.clearCart_product, loadAppConfig(0).strings?.proceed), "Yes", "No", listener)
                    }
                }
                2 ->
                    //check multiple product have single fixed_quantity

                    if (cartCount.any { it?.product_id == productId && it?.quantity ?: 0f >= 1f }) {
                        mDialogsUtil.openAlertDialog(mContext, mContext.getString(R.string.clearCart_multiprod_quant, loadAppConfig(0).strings?.proceed), "Yes", "No", listener)
                    } else {
                        bookingStatus = true
                    }

                1 ->
                    //check single product have multiple fixed_quantity

                    if (cartCount.any { it?.product_id == productId }) {
                        bookingStatus = true
                    } else {
                        mDialogsUtil.openAlertDialog(mContext, mContext.getString(R.string.clearCart_multiprod_quant, loadAppConfig(0).strings?.proceed), "Yes", "No", listener)
                    }

                3 -> bookingStatus = true


            }
        } else {
            bookingStatus = true
        }

        return bookingStatus
    }

    fun checkProdExistance(mProdId: Int?): Boolean {
        val cartList: CartList? = getCartList()

        return cartList?.cartInfos?.any { it.productId == mProdId } ?: false
    }


    fun checkProductAddon(mAddonList: MutableList<ProductAddon?>?): CartInfo {


        val cartList: CartList? = getCartList()

        cartList?.cartInfos?.forEachIndexed { _, cartInfo ->

            if (!cartInfo.add_ons.isNullOrEmpty() && mAddonList?.size == cartInfo.add_ons?.size) {

                val status = mAddonList?.zip(cartInfo.add_ons
                        ?: mutableListOf())?.map { it.second?.type_id == it.first?.type_id }

                if (!status.isNullOrEmpty() && !status.any { !it }) {
                    return cartInfo
                }
            }
        }

        return CartInfo()
    }

    fun clearCart() {
        var mPrefs: SharedPreferences.Editor = mContext.getSharedPreferences(DataNames.Pref_Cart_Quantity, Context.MODE_PRIVATE).edit().clear()
        mPrefs.apply()

        mPrefs = mContext.getSharedPreferences("netTotalLaundry", Context.MODE_PRIVATE).edit().clear()
        mPrefs.apply()


        val cartList: CartList? = mPreferenceHelper.getGsonValue(DataNames.CART, CartList::class.java)
        cartList?.cartInfos?.clear()
        mPreferenceHelper.addGsonValue(DataNames.CART, Gson().toJson(cartList))
    }


    fun addItem(cartInfo: CartInfo) {
        val cartList: CartList? = getCartList()

        if (checkProductAddon(cartInfo.add_ons ?: mutableListOf()).productId != 0) {
            val index = cartList?.cartInfos?.indexOfFirst { it.add_ons == cartInfo.add_ons }
                    ?: -1 // -1 if not found
            //if (index != null) {
            if (index >= 0) {
                cartList?.cartInfos?.set(index, cartInfo)
            }
            //  }
        } else {
            cartList?.cartInfos?.add(cartInfo)
        }

        mPreferenceHelper.addGsonValue(DataNames.CART, Gson().toJson(cartList))


        updateTotalPrice(cartList!!)
        updateProductQuant(cartInfo.productId.toString())

    }


    fun updateItem(productModel: ProductDataBean?) {
        val cartList: CartList? = getCartList()

        val index = cartList?.cartInfos?.indexOfFirst { it.productAddonId == productModel?.productAddonId } // -1 if not found
        if (index != null) {
            if (index >= 0) {

                val cartInfo = cartList.cartInfos?.elementAt(index)
                cartInfo?.quantity = (productModel?.fixed_quantity ?: 0f).toFloat()

                /*   cartInfo?.add_ons?.mapIndexed { _, productAddon ->
                       productAddon?.quantity = productModel?.fixed_quantity ?: 1
                   }*/

                cartInfo?.let { cartList.cartInfos?.set(index, it) }

                mPreferenceHelper.addGsonValue(DataNames.CART, Gson().toJson(cartList))

                updateProductQuant(cartInfo?.productId.toString())

                updateTotalPrice(cartList)
            }
        }
    }

    fun updateCartItem(cartItem: CartInfo, position: Int) {
        val cartList: CartList? = getCartList()
        cartList?.cartInfos?.set(position, cartItem)
        mPreferenceHelper.addGsonValue(DataNames.CART, Gson().toJson(cartList))

        updateProductQuant(cartItem.productId.toString())
        cartList?.let { updateTotalPrice(it) }
    }

    fun calculateCartTotal(list: MutableList<CartInfo>?): Double {

        var total = 0.0

        val decimalFormat = DecimalFormat("0.00", DecimalFormatSymbols.getInstance(Locale.ENGLISH))

        val cartList: CartList? = getCartList()

        decimalFormat.roundingMode = RoundingMode.HALF_DOWN

        cartList?.cartInfos?.forEachIndexed { index, cartInfo ->

            total += if (cartInfo.hourlyPrice.isNullOrEmpty()) {
                cartInfo.price.times(cartInfo.quantity)
            } else if (!cartInfo.add_ons.isNullOrEmpty()) {
                with(cartInfo)
                {
                    (add_ons?.sumByDouble {
                        it?.price?.toDouble()?.times(it.quantity?.toDouble() ?: 0.0) ?: 0.0
                    }?.toFloat()?.plus(fixed_price ?: 0f) ?: 0.0f).times(quantity)
                }
            } else {
                //cartInfo.price
                cartInfo.price.times(cartInfo.quantity)
            }

        }
        return decimalFormat.format(total).toDouble()
    }

    private fun updateTotalPrice(cartList: CartList) {

        var totalAmount = 0.0f

        cartList.cartInfos?.mapIndexed { _, cartItem ->
            totalAmount += cartItem.price.times(cartItem.quantity)
        }
        mPreferenceHelper.setkeyValue(PrefenceConstants.NET_TOTAL, totalAmount)
    }

    fun removeCartItem(productModel: ProductDataBean) {
        val cartList: CartList? = getCartList()

        val index = cartList?.cartInfos?.indexOfFirst { it.productAddonId == productModel.productAddonId }
                ?: -1
        if (index >= 0) {
            cartList?.cartInfos?.removeAt(index)
            removeProductQuant(productId = productModel.product_id.toString())
        }

        mPreferenceHelper.addGsonValue(DataNames.CART, Gson().toJson(cartList))

        updateTotalPrice(cartList!!)

    }


    fun getCartList(): CartList {
        return mPreferenceHelper.getGsonValue(DataNames.CART, CartList::class.java) ?: CartList()
    }


    private fun updateProductQuant(productId: String) {

        val cartList: CartList? = getCartList()
        val quantity = (cartList?.cartInfos?.filter { productId.toInt() == it.productId }?.sumByDouble { it.quantity.toDouble() }
                ?: 0.0).toFloat()

        val editor = mContext.getSharedPreferences(DataNames.Pref_Cart_Quantity, 0).edit()
        editor.putFloat(productId, quantity)
        editor.apply()
    }

    private fun removeProductQuant(productId: String) {
        val editor = mContext.getSharedPreferences(DataNames.Pref_Cart_Quantity, 0).edit()
        editor.remove(productId)
        editor.apply()
    }

    fun addProductDb(context: Context?, appType: Int, productModel: ProductDataBean?) {
        val cartInfo = CartInfo()
        cartInfo.quantity = productModel?.prod_quantity ?: 0f
        cartInfo.productName = productModel?.name
        cartInfo.productDesc = productModel?.product_desc
        //  cartInfo.supplierAddress=productModel.suppl
        cartInfo.productId = productModel?.product_id ?: 0
        cartInfo.imagePath = productModel?.image_path.toString()
        cartInfo.is_appointment = productModel?.is_appointment
        cartInfo.price = productModel?.netPrice ?: 0.0f
        cartInfo.fixed_price = productModel?.fixed_price?.toFloatOrNull() ?: 0.0f
        cartInfo.supplierName = productModel?.supplier_name
        cartInfo.suplierBranchId = productModel?.supplier_branch_id ?: 0
        cartInfo.measuringUnit = productModel?.measuring_unit
        cartInfo.deliveryCharges = productModel?.delivery_charges ?: 0.0f
        cartInfo.supplierId = productModel?.supplier_id ?: 0
        cartInfo.urgent_type = productModel?.urgent_type ?: 0
        cartInfo.isUrgent = productModel?.can_urgent ?: 0
        cartInfo.isPaymentConfirm = productModel?.payment_after_confirmation ?: 0
        cartInfo.avgRating = productModel?.avg_rating ?: 0.0f
        cartInfo.cart_image_upload = productModel?.cart_image_upload ?: 0
        cartInfo.order_instructions = productModel?.order_instructions ?: 0
        cartInfo.sales_tax = productModel?.sales_tax
        if (productModel?.prod_variants?.isNotEmpty() == true) {
            cartInfo.varients = (productModel.prod_variants)?.toMutableList()
        }


        // cartInfo.setUrgentValue(productModel.getUrgent_value());
        cartInfo.categoryId = productModel?.category_id ?: 0
        cartInfo.isDiscount = productModel?.discount

        if (!productModel?.hourly_price.isNullOrEmpty()) {

            cartInfo.hourlyPrice = productModel?.hourly_price ?: emptyList()
            cartInfo.fixed_price = productModel?.hourly_price?.get(0)?.discount_price
        }


        if (appType == AppDataType.HomeServ.type) {
            val bookingFlowBean = mPreferenceHelper.getGsonValue(DataNames.BOOKING_FLOW, SettingModel.DataBean.BookingFlowBean::class.java)

            cartInfo.isQuant = productModel?.is_quantity ?: 0
            //set manually service Type 0 for service 1 for product
            cartInfo.serviceType = productModel?.is_product ?: 0
            cartInfo.agentType = if (productModel?.is_agent == 1 && productModel.is_product == 0) 1 else 0
            cartInfo.agentList = productModel?.agent_list ?: 0
            //set service duration amount as per duration or interval
            //  if (productModel?.is_product == 0) {

            cartInfo.serviceDuration = productModel?.duration ?: 0
            cartInfo.serviceDurationSum = productModel?.duration?.times(productModel.prod_quantity
                    ?: 0f) ?: 0f
            // productModel.serviceDuration =productModel.duration ?: 0
            //}
            cartInfo.priceType = productModel?.price_type ?: 0
        } else {
            cartInfo.serviceType = 1
        }

        cartInfo.latitude = productModel?.latitude
        cartInfo.longitude = productModel?.longitude
        cartInfo.radius_price = productModel?.radius_price
        cartInfo.deliveryMax = productModel?.delivery_max_time ?: 0
        cartInfo.purchasedQuant = productModel?.purchased_quantity
        cartInfo.prodQuant = productModel?.quantity

        cartInfo.productSpecialInstructions = productModel?.productSpecialInstructions

        cartInfo.purchase_limit = productModel?.purchase_limit

        cartInfo.deliveryType = productModel?.self_pickup ?: 0
        cartInfo.duration = productModel?.duration ?: 0

        cartInfo.handlingAdmin = productModel?.handling_admin ?: 0.0f
        cartInfo.handlingSupplier = productModel?.handling_supplier ?: 0.0f

        cartInfo.question_list = productModel?.selectQuestAns ?: mutableListOf()
        cartInfo.appType = if (appType > AppDataType.Custom.type) productModel?.type else appType

        cartInfo.handlingCharges = (productModel?.handling_admin?.plus(productModel.handling_supplier
                ?: 0.0f)) ?: 0.0f

        cartInfo.product_owner_name = productModel?.product_owner_name
        cartInfo.isOpen = productModel?.isOpen ?: true
        cartInfo.product_reference_id = productModel?.product_reference_id
        cartInfo.product_upload_reciept = productModel?.product_upload_reciept
        cartInfo.product_dimensions = productModel?.product_dimensions
        cartInfo.is_out_network = productModel?.is_out_network
        cartInfo.serviceAgentDetail = productModel?.agentDetail
        cartInfo.agentBufferPrice = productModel?.agentBufferPrice
        StaticFunction.addToCart(context, cartInfo)
    }


    fun getDayId(dayId: Int): String? {
        return when (dayId) {
            Calendar.SUNDAY -> "6"
            Calendar.MONDAY -> "0"
            Calendar.TUESDAY -> "1"
            Calendar.WEDNESDAY -> "2"
            Calendar.THURSDAY -> "3"
            Calendar.FRIDAY -> "4"
            Calendar.SATURDAY -> "5"
            else -> "-1"
        }
    }

    fun checkDayId(dayId: Int): Int? {
        return when (dayId) {
            Calendar.SUNDAY -> 0
            Calendar.MONDAY -> 1
            Calendar.TUESDAY -> 2
            Calendar.WEDNESDAY -> 3
            Calendar.THURSDAY -> 4
            Calendar.FRIDAY -> 5
            Calendar.SATURDAY -> 6
            else -> -1
        }
    }

    fun checkResturntTiming(timing: List<TimeDataBean>?): Boolean {
        // val calendar = Calendar.getInstance()
        val startDate = Calendar.getInstance(Locale.getDefault())
        val endDate = Calendar.getInstance(Locale.getDefault())
        val currentDate = Calendar.getInstance(Locale.getDefault())

        var isCheckStatus = false

        val sdf = SimpleDateFormat("HH:mm:ss", Locale.getDefault())

        run loop@{
            timing?.forEach {
                if (it.week_id == (getDayId(currentDate.get(Calendar.DAY_OF_WEEK))
                        ?: "-1").toInt()) {
                    isCheckStatus = it.is_open == 1

                    startDate.time = sdf.parse(it.start_time ?: "")!!
                    startDate.set(Calendar.YEAR, currentDate.get(Calendar.YEAR))
                    startDate.set(Calendar.MONTH, currentDate.get(Calendar.MONTH))
                    startDate.set(Calendar.DAY_OF_MONTH, currentDate.get(Calendar.DAY_OF_MONTH))

                    endDate.time = sdf.parse(it.end_time ?: "")!!
                    endDate.set(Calendar.YEAR, currentDate.get(Calendar.YEAR))
                    endDate.set(Calendar.MONTH, currentDate.get(Calendar.MONTH))
                    endDate.set(Calendar.DAY_OF_MONTH, currentDate.get(Calendar.DAY_OF_MONTH))

                    if (currentDate.time.after(startDate.time) && currentDate.time.before(endDate.time)) {
                        return@loop
                    }
                }
            }
        }

        return if (isCheckStatus) {
            currentDate.time.after(startDate.time) && currentDate.time.before(endDate.time)
        } else {
            isCheckStatus
        }
    }




    fun revealShow(dialogView: View?, fab: View, b: Boolean, popView: PopupWindow?) {
        val view = dialogView?.findViewById<ConstraintLayout>(R.id.dialog)

        val width = view?.width?.toDouble()
        val height = view?.height?.toDouble()

        val endRadius = hypot(width ?: 0.0, height ?: 0.0).toFloat()

        val cx = (fab.x + (fab.width / 2)).toInt()
        val cy = ((fab.y) + fab.height + 56).toInt()


        if (b) {
            val revealAnimator = ViewAnimationUtils.createCircularReveal(view, cx, cy, 0f, endRadius)

            view?.visibility = View.VISIBLE
            revealAnimator.duration = 700
            revealAnimator.start()

        } else {

            val anim = ViewAnimationUtils.createCircularReveal(view, cx, cy, endRadius, 0f)


            anim.addListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator?) {
                    super.onAnimationEnd(animation)
                    popView?.dismiss()
                    view?.visibility = View.INVISIBLE
                }
            })
            anim.duration = 700
            anim.start()
        }

    }

    fun getCurrencySymbol(): String {

        val currency = mPreferenceHelper.getGsonValue(PrefenceConstants.CURRENCY_INF, Currency::class.java)

        return if (currency == null) {
            CURRENCY_SYMBOL
        } else {
            CURRENCY_SYMBOL = currency.currency_symbol ?: ""
            currency.currency_symbol ?: ""
        }
    }

    fun checkLoginActivity(): Class<*>? {
        val settingFlow = mPreferenceHelper.getGsonValue(DataNames.SETTING_DATA, SettingModel.DataBean.SettingData::class.java)

        return if (settingFlow?.user_register_flow != null && settingFlow.user_register_flow == "1" ||
                settingFlow?.show_ecom_v2_theme == "1") {
            SigninActivity::class.java
        } else {
            LoginActivityNew::class.java

        }
    }

    fun checkLoginFlow(mContext: Activity, resultCode: Int) {
        val settingFlow = mPreferenceHelper.getGsonValue(DataNames.SETTING_DATA, SettingModel.DataBean.SettingData::class.java)

        return if (settingFlow?.user_register_flow != null && settingFlow.user_register_flow == "1" ||
                settingFlow?.show_ecom_v2_theme == "1") {
            mContext.launchActivity<SigninActivity>(resultCode)
        } else {
            mContext.launchActivity<SignInActivityNew>(resultCode)
        }
    }


    fun loadAppConfig(appType: Int): AppConfiguration {
        val screenFlowBean = mPreferenceHelper.getGsonValue(DataNames.SCREEN_FLOW, SettingModel.DataBean.ScreenFlowBean::class.java)
        val terminologyBean = mPreferenceHelper.getGsonValue(PrefenceConstants.APP_TERMINOLOGY, SettingModel.DataBean.Terminology::class.java)
        if (appType > 0) {
            screenFlowBean?.app_type = appType
        }

        if (appType > 0) {
            screenFlowBean?.app_type = appType
        }

        val stringConfig = TextConfig(screenFlowBean?.app_type
                ?: 0, terminologyBean, mPreferenceHelper.getKeyValue(DataNames.SELECTED_LANGUAGE, PrefenceConstants.TYPE_STRING) as String)

        val appConfig = AppConfiguration

        appConfig.strings = stringConfig

        return appConfig
    }

    fun getLangCode(languageParam: String?): Int {
        val selectedLang = languageParam
                ?: mPreferenceHelper.getKeyValue(DataNames.SELECTED_LANGUAGE, PrefenceConstants.TYPE_STRING).toString()
        return if (selectedLang == ClikatConstants.ENGLISH_SHORT || selectedLang == ClikatConstants.ENGLISH_FULL) {
            ClikatConstants.LANGUAGE_ENGLISH
        } else {
            ClikatConstants.LANGUAGE_OTHER
        }
    }

    fun getMapKey(): String {

        val settingData = mPreferenceHelper.getGsonValue(DataNames.SETTING_DATA, SettingModel.DataBean.SettingData::class.java)

        return if (!settingData?.google_map_key_android.isNullOrEmpty() && settingData?.google_map_key_android?.length ?: 0 > 20) {
            settingData?.google_map_key_android ?: "AIzaSyAO1Or-BIWXfjE2NlL-z6lzcCWRnznwJ0g"
        } else if (!settingData?.google_map_key.isNullOrEmpty() && settingData?.google_map_key?.contains("https://www.google.com/maps") == false) {
            settingData.google_map_key
        } else {
            "AIzaSyAO1Or-BIWXfjE2NlL-z6lzcCWRnznwJ0g"
        }

    }

    fun showBillingData(context: Activity, mSelectedPayment: CustomPayModel?, totalAmt: Double?) {
        val dialog = Dialog(context, android.R.style.Theme_Material_Dialog_Alert)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.dialog_add_billing)
        dialog.setCancelable(false)
        dialog.window?.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT)

        val ivCross = dialog.findViewById<ImageView>(R.id.ivCross)

        val etAddressDetail = dialog.findViewById<TextInputEditText>(R.id.etAddressDetail)
        val etCity = dialog.findViewById<TextInputEditText>(R.id.etCity)
        val etStateDetail = dialog.findViewById<TextInputEditText>(R.id.etStateDetail)
        val ccp = dialog.findViewById<CountryCodePicker>(R.id.ccp)
        val etPinCodeDetail = dialog.findViewById<TextInputEditText>(R.id.etPinCodeDetail)

        val etShippingAddressDetail = dialog.findViewById<TextInputEditText>(R.id.etShippingAddressDetail)
        val etShippingCity = dialog.findViewById<TextInputEditText>(R.id.etShippingCity)
        val etShippingStateDetail = dialog.findViewById<TextInputEditText>(R.id.etShippingStateDetail)
        val ccpShipping = dialog.findViewById<CountryCodePicker>(R.id.shippingCcp)
        val etShippingPinCodeDetail = dialog.findViewById<TextInputEditText>(R.id.etShippingPinCodeDetail)
        val grpShippingAddress = dialog.findViewById<Group>(R.id.grpShippingAddress)
        val checkBox = dialog.findViewById<CheckBox>(R.id.checkBox)
        val tvSubmit = dialog.findViewById<MaterialButton>(R.id.tvSubmit)

        checkBox?.setOnCheckedChangeListener { compoundButton, b ->
            grpShippingAddress?.visibility = if (b) View.GONE else View.VISIBLE
        }


        tvSubmit?.setOnClickListener {
            hideSoftKeyboard(tvSubmit)
            val data = BillingAddress()
            val locale = Locale("en", ccp?.selectedCountryNameCode.toString())
            val localeShipping = Locale("en", ccpShipping?.selectedCountryNameCode.toString())

            data.address = etAddressDetail?.text.toString().trim()
            data.city = etCity?.text.toString().trim()
            data.state = etStateDetail?.text.toString().trim()
            data.country = locale.isO3Country
            data.pinCode = etPinCodeDetail?.text.toString().trim()

            if (checkBox.isChecked) {
                data.shippingAddress = data.address
                data.shippingCity = data.city
                data.shippingCountry = data.country
                data.shippingState = data.state
                data.shippingPinCode = data.pinCode
            } else {
                data.shippingAddress = etShippingAddressDetail?.text.toString().trim()
                data.shippingCity = etShippingCity?.text.toString().trim()
                data.shippingCountry = localeShipping.isO3Country
                data.shippingState = etShippingStateDetail?.text.toString().trim()
                data.shippingPinCode = etShippingPinCodeDetail?.text.toString().trim()
            }

            when {
                data.address.isNullOrEmpty() -> AppToasty.error(context, context.getString(R.string.enter_address))
                data.city.isNullOrEmpty() -> AppToasty.error(context, context.getString(R.string.enter_city))
                data.state.isNullOrEmpty() -> AppToasty.error(context, context.getString(R.string.enter_state))
                data.country.isNullOrEmpty() -> AppToasty.error(context, context.getString(R.string.enter_country))
                data.pinCode.isNullOrEmpty() -> AppToasty.error(context, context.getString(R.string.enter_pincode))
                !checkBox.isChecked -> {
                    when {
                        data.shippingAddress.isNullOrEmpty() -> AppToasty.error(context, context.getString(R.string.enter_shipping_address))
                        data.shippingCity.isNullOrEmpty() -> AppToasty.error(context, context.getString(R.string.enter_shipping_city))
                        data.shippingState.isNullOrEmpty() -> AppToasty.error(context, context.getString(R.string.enter_shipping_state))
                        data.shippingCountry.isNullOrEmpty() -> AppToasty.error(context, context.getString(R.string.enter_shipping_country))
                        data.shippingPinCode.isNullOrEmpty() -> AppToasty.error(context, context.getString(R.string.enter_shipping_pincode))
                        else -> {
                            dialog.dismiss()
                            payTabsPayment(context, mSelectedPayment, totalAmt, data)
                        }
                    }
                }
                else -> {
                    dialog.dismiss()
                    payTabsPayment(context, mSelectedPayment, totalAmt, data)
                }
            }
        }
        ivCross?.setOnClickListener {
            dialog.dismiss()
        }
        dialog.show()
    }

    fun showAllergiesDialog(context: Context, allergies: String) {
        val dialog = Dialog(context, android.R.style.Theme_Material_Dialog_Alert)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.dialog_track_dhl)
        dialog.setCancelable(false)
        dialog.window?.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT)
        val title = dialog.findViewById<TextView>(R.id.tvAddInst)
        val ivCross = dialog.findViewById<ImageView>(R.id.ivCross)
        val subTitle = dialog.findViewById<TextView>(R.id.tvShipmentInfoTag)
        val groupOthrs = dialog.findViewById<Group>(R.id.gpOther)
        groupOthrs?.visibility = View.GONE
        title?.text = context.getString(R.string.allergies)
        subTitle?.text = allergies

        ivCross?.setOnClickListener {
            dialog.dismiss()
        }
        dialog.show()
    }

    fun showPrepTimeDialog(context: Context, onSortByListenerClicked: OnSortByListenerClicked) {
        val dialog = Dialog(context, android.R.style.Theme_Material_Dialog_Alert)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.dialog_prep_time)
        dialog.setCancelable(false)
        dialog.window?.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT)

        val seekBar = dialog.findViewById<CrystalRangeSeekbar>(R.id.rangeSeekbar)
        val ivCross = dialog.findViewById<ImageView>(R.id.ivCross)
        val tvMinValue = dialog.findViewById<TextView>(R.id.tvMinValue)
        val tvMaxValue = dialog.findViewById<TextView>(R.id.tvMaxValue)
        val btnDone = dialog.findViewById<MaterialButton>(R.id.btnDone)
        val cbPrepTime = dialog.findViewById<CheckBox>(R.id.cbPrepTime)
        val cbFreeDelivery = dialog.findViewById<CheckBox>(R.id.cbFreeDelivery)
        val groupPrepTime = dialog.findViewById<Group>(R.id.groupPrepTime)
        seekBar.setOnRangeSeekbarChangeListener { minValue, maxValue ->
            tvMinValue.text = context.getString(R.string.mins_value, minValue.toString())
            tvMaxValue.text = context.getString(R.string.mins_value, maxValue.toString())
        }

        cbPrepTime?.setOnCheckedChangeListener { _, b ->
            groupPrepTime.visibility = if (b) View.VISIBLE else View.GONE
        }
        ivCross?.setOnClickListener {
            dialog.dismiss()
        }

        btnDone?.setOnClickListener {
            val filters = FiltersSupplierList()
            filters.minValue = seekBar?.selectedMinValue.toString()
            filters.maxValue = seekBar?.selectedMaxValue.toString()
            filters.is_free_delivery = if (cbFreeDelivery.isChecked) 1 else 0
            filters.is_preparation_time = if (cbPrepTime.isChecked) 1 else 0

            if (filters.is_preparation_time == 0 && filters.is_free_delivery == 0) {
                AppToasty.error(context, context.getString(R.string.select_filters))
            } else {
                onSortByListenerClicked.onPrepTimeSelected(filters)
                dialog.dismiss()
            }
        }

        dialog.show()
    }

    private var onHyperPayChanges: OnHyperPayChanges? = null
    fun setListener(onHyperPayChanges: OnHyperPayChanges) {
        this.onHyperPayChanges = onHyperPayChanges
    }

    interface OnHyperPayChanges {
        fun onHyperPayChanges(data: BillingAddress)
    }

    fun payTabsPayment(context: Activity?, mSelectedPayment: CustomPayModel?, totalAmt: Double?, data: BillingAddress) {

        val userInfo = dataManager.getGsonValue(DataNames.USER_DATA, PojoSignUp::class.java)
        val currency = dataManager.getGsonValue(PrefenceConstants.CURRENCY_INF, Currency::class.java)
        val intent = Intent(context, PayTabActivity::class.java)

        //Add your Secret Key Here

        mSelectedPayment?.payement_front?.forEachIndexed { index, keyValueFront ->
            if (keyValueFront?.key == "paytabs_secret_key") {
                intent.putExtra(PaymentParams.SECRET_KEY, keyValueFront.value)
            } else if (keyValueFront?.key == "merchant_email") {
                intent.putExtra(PaymentParams.MERCHANT_EMAIL, keyValueFront.value)
            }
        }

        intent.putExtra(PaymentParams.LANGUAGE, PaymentParams.ENGLISH)
        intent.putExtra(PaymentParams.TRANSACTION_TITLE, "Payment")
        intent.putExtra(PaymentParams.AMOUNT, totalAmt)

        intent.putExtra(PaymentParams.CURRENCY_CODE, currency?.currency_name)
        intent.putExtra(PaymentParams.CUSTOMER_PHONE_NUMBER, userInfo?.data?.mobile_no)
        intent.putExtra(PaymentParams.CUSTOMER_EMAIL, userInfo?.data?.email)
        intent.putExtra(PaymentParams.ORDER_ID, System.currentTimeMillis().toString())
        intent.putExtra(PaymentParams.PRODUCT_NAME, "Product 1, Product 2")

        //Billing Address
        intent.putExtra(PaymentParams.ADDRESS_BILLING, data.address)
        intent.putExtra(PaymentParams.CITY_BILLING, data.city)
        intent.putExtra(PaymentParams.STATE_BILLING, data.state)
        intent.putExtra(PaymentParams.COUNTRY_BILLING, data.country)
        intent.putExtra(
                PaymentParams.POSTAL_CODE_BILLING,
                data.pinCode
        ) //Put Country Phone code if Postal code not available '00973'

        //Shipping Address
        intent.putExtra(PaymentParams.ADDRESS_SHIPPING, data.shippingAddress)
        intent.putExtra(PaymentParams.CITY_SHIPPING, data.shippingCity)
        intent.putExtra(PaymentParams.STATE_SHIPPING, data.shippingState)
        intent.putExtra(PaymentParams.COUNTRY_SHIPPING, data.shippingCountry)
        intent.putExtra(
                PaymentParams.POSTAL_CODE_SHIPPING,
                data.shippingPinCode
        ) //Put Country Phone code if Postal code not available '00973'

        //Payment Page Style
        intent.putExtra(PaymentParams.PAY_BUTTON_COLOR, Configurations.colors.primaryColor)

        //Tokenization
        intent.putExtra(PaymentParams.IS_TOKENIZATION, true)
        context?.startActivityForResult(intent, PaymentParams.PAYMENT_REQUEST_CODE)
    }

    internal interface DefaultPaymentInformation {
        companion object {
            const val SIGN = "200731095424588455"
        }
    }

    fun startTransaction(mTotalAmt: Double, paymentProcessStateListener: IPaymentProcessStateListener, requireActivity: FragmentActivity) {
        val settingData = mPreferenceHelper.getGsonValue(DataNames.SETTING_DATA, SettingModel.DataBean.SettingData::class.java)
        val currency = dataManager.getGsonValue(PrefenceConstants.CURRENCY_INF, Currency::class.java)
        val payment = Payment(settingData?.datatrans_app_merchant_id, System.currentTimeMillis().toString(),
                currency?.currency_name, (mTotalAmt.toInt().times(100)), DefaultPaymentInformation.SIGN)

        val dc = DisplayContext(ResourceProvider(), requireActivity)
        val paymentMethods: MutableList<PaymentMethod> = ArrayList()
        paymentMethods.add(PaymentMethod.createMethod(PaymentMethodType.VISA))
        paymentMethods.add(PaymentMethod.createMethod(PaymentMethodType.MASTERCARD))
        paymentMethods.add(PaymentMethod.createMethod(PaymentMethodType.AMEX))
        paymentMethods.add(PaymentMethod.createMethod(PaymentMethodType.PFCARD))
        paymentMethods.add(PaymentMethod.createMethod(PaymentMethodType.PAYPAL))

        // normal payment
        val ppa = PaymentProcessAndroid(dc, payment, paymentMethods)

        // payment with alias request for registrations only
        //AliasRequest ar = new AliasRequest(transactionDetails.getMerchantId(), "CHF", paymentMethods);

        //PaymentProcessAndroid ppa = new PaymentProcessAndroid(dc, ar);

        // payment with aliasCC
        //PaymentProcessAndroid ppa = new PaymentProcessAndroid(dc, payment, new AliasPaymentMethodCreditCard(PaymentMethodType.VISA, "70119122433810042", "", 2018, 12, "DME"));

        // useAlias
        // https://docs.datatrans.ch/docs/payment-process-alias
        ppa.paymentOptions.isRecurringPayment = true
        // CAA
        ppa.paymentOptions.isAutoSettlement = true

        // this invokes the 'BEFORE_COMPLETION' callback which allows the user to show
        // a custom confirmation screen/dialog
        // ppa.setManualCompletionEnabled(true);

        // activate split mode. use transactionId from callback to complete transaction
        // https://docs.datatrans.ch/docs/integrations-split-mode#section-finalize-the-authorization
        // ppa.getPaymentOptions().setSkipAuthorizationCompletion(true);

        // used to ensure a proper switch back to the app
        ppa.paymentOptions.appCallbackScheme = "ch.datatrans.android.sample"
        //        ppa.getPaymentOptions().setTWINTEnvironment(TwintEnvironment.INT);
        ppa.setTestingEnabled(true)
        ppa.paymentOptions.isCertificatePinning = true
        ppa.addStateListener(paymentProcessStateListener)
        ppa.start()
    }


    fun checkHomeActivity(mContext: Activity, bundle: Bundle) {
        val settingFlow = mPreferenceHelper.getGsonValue(DataNames.SETTING_DATA, SettingModel.DataBean.SettingData::class.java)

        return if (settingFlow?.show_food_groc != null && settingFlow.show_food_groc == "1") {
            mContext.launchActivity<ServiceListActivity>()
        } else {
            if (dataManager.getCurrentUserLoggedIn()) {
                mContext.launchActivity<RoadMapActivity> {
                    putExtras(bundle)
                }
            } else {
                mContext.launchActivity<SignInActivityNew> {
                    putExtras(bundle)
                }
            }

        }
    }

    fun setUserLocale(addressBean: AddressBean?) {
        addressBean.let {
            AppConstants.USER_LATLNG = LatLng((it?.latitude ?: "0.0").toDouble(), (it?.longitude
                    ?: "0.0").toDouble())
            AppConstants.COUNTRY_ISO = getAddress((it?.latitude ?: "0.0").toDouble(), (it?.longitude
                    ?: "0.0").toDouble())?.countryCode ?: ""
        }
    }

    fun getAddressFormat(addressBean: AddressBean?): String? {
        return if (addressBean?.address_line_1 != null) {
            "${addressBean.customer_address ?: ""} , ${addressBean.address_line_1 ?: ""}"
        } else {
            addressBean?.customer_address ?: ""
        }
    }

    fun statusToChange(status: Double, user_on_the_way: String?): Double {
        return when {
            status == OrderStatus.In_Kitchen.orderStatus -> OrderStatus.On_The_Way.orderStatus
            status == OrderStatus.Ready_to_be_picked.orderStatus && user_on_the_way != "1" -> OrderStatus.On_The_Way.orderStatus
            status == OrderStatus.Ready_to_be_picked.orderStatus && user_on_the_way == "1" -> OrderStatus.Arrived.orderStatus
            status == OrderStatus.On_The_Way.orderStatus -> OrderStatus.Arrived.orderStatus
            status == OrderStatus.Arrived.orderStatus -> OrderStatus.PickUp.orderStatus
            else -> OrderStatus.On_The_Way.orderStatus
        }
    }

    fun triggerEvent(event: String) {

        EventBus.getDefault().post(CommonEvent(event))
    }

    fun checkGoogleLogin(mActivity: Activity): String {
        val settingBean = dataManager.getGsonValue(DataNames.SETTING_DATA, SettingModel.DataBean.SettingData::class.java)

        if (settingBean?.enable_google_login == "0") return ""

        dataManager.getKeyValue(DataNames.FEATURE_DATA, PrefenceConstants.TYPE_STRING).toString().let {
            val listType: Type = object : TypeToken<ArrayList<SettingModel.DataBean.FeatureData?>?>() {}.type
            val featureList: ArrayList<SettingModel.DataBean.FeatureData> = Gson().fromJson(it, listType)

            if (featureList.any { it.name == "Google" }) {

                val key_value_front = featureList.firstOrNull { it.name == "Google" }?.key_value_front

                if (key_value_front?.firstOrNull()?.key == "client_id") {
                    // return key_value_front.firstOrNull()?.value?:""
                    return mActivity.getString(R.string.default_web_client_id)
                }

            }

        }

        return ""
    }


    fun checkFacebookLogin(mActivity: Activity): String {
        val settingBean = dataManager.getGsonValue(DataNames.SETTING_DATA, SettingModel.DataBean.SettingData::class.java)

        if (settingBean?.enable_google_login == "0") return ""

        dataManager.getKeyValue(DataNames.FEATURE_DATA, PrefenceConstants.TYPE_STRING).toString().let {
            val listType: Type = object : TypeToken<ArrayList<SettingModel.DataBean.FeatureData?>?>() {}.type
            val featureList: ArrayList<SettingModel.DataBean.FeatureData> = Gson().fromJson(it, listType)

            if (featureList.any { it.name == "Facebook" }) {

                val key_value_front = featureList.firstOrNull { it.name == "Facebook" }?.key_value_front

                if (key_value_front?.firstOrNull()?.key == "app_id") {
                    return key_value_front.firstOrNull()?.value ?: ""
                }
            }

            return ""
        }
    }


    companion object {

        /*    public AppUtils(Context context) {
        this.mContext = context;
    }*/

        /**
         * Description : Check if user is online or not
         *
         * @return true if online else false
         */


        private val EMAIL_PATTERN = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@" + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$"

        val EMAIL_ADDRESS_PATTERN = Pattern.compile(
                EMAIL_PATTERN
        )
    }


}