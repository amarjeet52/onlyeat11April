package com.codebrew.clikat.utils

import android.app.Activity
import android.app.AlarmManager
import android.app.Dialog
import android.app.PendingIntent
import android.content.*
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.net.Uri
import android.os.Build
import android.telephony.PhoneNumberUtils
import android.text.Editable
import android.text.TextWatcher
import android.util.DisplayMetrics
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.browser.customtabs.CustomTabsIntent
import androidx.core.content.ContextCompat
import androidx.core.text.HtmlCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.RequestOptions
import com.codebrew.clikat.BuildConfig
import com.codebrew.clikat.R
import com.codebrew.clikat.activities.NoInternetActivity
import com.codebrew.clikat.app_utils.AppToasty
import com.codebrew.clikat.app_utils.DateTimeUtils
import com.codebrew.clikat.data.*
import com.codebrew.clikat.data.constants.AppConstants
import com.codebrew.clikat.data.constants.PrefenceConstants
import com.codebrew.clikat.data.model.others.OrderEvent
import com.codebrew.clikat.modal.*
import com.codebrew.clikat.modal.other.SettingModel
import com.codebrew.clikat.module.home_screen.adapter.HomeItemAdapter
import com.codebrew.clikat.module.restaurant_detail.OnTableCapacityListener
import com.codebrew.clikat.module.splash.SplashActivity
import com.codebrew.clikat.preferences.DataNames
import com.codebrew.clikat.preferences.Prefs
import com.codebrew.clikat.services.SchedulerReciever
import com.google.android.material.textview.MaterialTextView
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import jp.wasabeef.glide.transformations.RoundedCornersTransformation
import org.greenrobot.eventbus.EventBus
import org.json.JSONArray
import java.lang.reflect.Type
import java.net.URLConnection
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import java.util.regex.Pattern
import kotlin.collections.ArrayList

/*
 * Created by cbl45 on 7/5/16.
 */
object StaticFunction {

    private var cartlist: CartList? = null

    fun EditText.onChange(cb: (String) -> Unit) {
        this.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                cb(s.toString())
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })
    }



    val accessToken: String
        get() {
            val signUp = Prefs.getPrefs().getObject(DataNames.USER_DATA, PojoSignUp::class.java)
            return if (signUp != null && signUp.data != null && signUp.data.access_token != null)
                signUp.data?.access_token ?: ""
            else
                ""
        }

    fun addToCart(context: Context?, cartInfo: CartInfo) {
        cartlist = Prefs.with(context).getObject(DataNames.CART, CartList::class.java)
        // Prefs.with(context).save(DataNames.SUPPLIER_LOGO_CART, supplierImage)
        // Prefs.with(context).save(DataNames.SUPPLIER_LOGO_NAME_CART, "" + supplierName)
        if (cartlist == null) {
            cartlist = CartList()
        }

        val position = checkIfProductExist(cartInfo.productId)

        if (position != -1) {
            cartInfo.quantity = 1f
            cartlist!!.cartInfos!![position] = cartInfo
        } else {
            cartlist!!.cartInfos!!.add(cartInfo)
        }

        saveCartQuantity(context, cartInfo.productId, cartInfo.quantity)
        Prefs.with(context).save(DataNames.CART, cartlist)


        updateNetTotal(cartlist, context)
        alarmFunction(context)

    }

    private fun alarmFunction(context: Context?) {
        val rightNow = Calendar.getInstance()
        val myIntent = Intent(context, SchedulerReciever::class.java)
        val pendingIntent = PendingIntent.getBroadcast(context, 0, myIntent, 0)
        val alarmManager = context?.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmManager.set(AlarmManager.RTC, rightNow.timeInMillis + 900000, pendingIntent)
    }


    fun clearCartLaundry(context: Context?) {
        cartlist = Prefs.with(context).getObject(DataNames.CART_LAUNDRY, CartList::class.java)

        if (cartlist == null) {
            cartlist = CartList()
        } else {
            cartlist!!.cartInfos!!.clear()
            Prefs.with(context).save(DataNames.CART_LAUNDRY, cartlist)
        }
        removeAllCartLaundry(context)

    }

    fun clearCart(context: Context?) {
        cartlist = Prefs.with(context).getObject(DataNames.CART, CartList::class.java)

        if (cartlist == null) {
            cartlist = CartList()
        } else {
            cartlist!!.cartInfos!!.clear()
            Prefs.with(context).save(DataNames.CART, cartlist)
        }
        cartlist = Prefs.with(context).getObject(DataNames.CART_SALOON_PLACE, CartList::class.java)

        if (cartlist == null) {
            cartlist = CartList()
        } else {
            cartlist!!.cartInfos!!.clear()
            Prefs.with(context).save(DataNames.CART_SALOON_PLACE, cartlist)
        }
        cartlist = Prefs.with(context).getObject(DataNames.CART_SALOON_HOME, CartList::class.java)

        if (cartlist == null) {
            cartlist = CartList()
        } else {
            cartlist!!.cartInfos!!.clear()
            Prefs.with(context).save(DataNames.CART_SALOON_HOME, cartlist)
        }
        cartlist = Prefs.with(context).getObject(DataNames.CART_SALOON_PLACE, CartList::class.java)

        if (cartlist == null) {
            cartlist = CartList()
        } else {
            cartlist!!.cartInfos!!.clear()
            Prefs.with(context).save(DataNames.CART_SALOON_PLACE, cartlist)
        }
        cartlist = Prefs.with(context).getObject(DataNames.CART_LAUNDRY, CartList::class.java)

        if (cartlist == null) {
            cartlist = CartList()
        } else {
            cartlist!!.cartInfos!!.clear()
            Prefs.with(context).save(DataNames.CART_LAUNDRY, cartlist)
        }
        Prefs.with(context).save(DataNames.SUPPLIERBRANCHID, "")
        //Prefs.with(context).save(DataNames.CATEGORY_ID, "")
        removeAllCart(context)

    }

    fun updateNetTotal(cartList: CartList?, context: Context?) {
        if (cartList != null && cartList.cartInfos != null) {
            var netTotal = 0f
            for (i in 0 until cartList.cartInfos!!.size) {
                netTotal = netTotal + cartList.cartInfos!![i].price * cartList.cartInfos!![i].quantity
            }
            Prefs.with(context).save("netTotal", netTotal)
        }
        Prefs.with(context).remove(DataNames.DISCOUNT_AMOUNT)
    }


    private fun checkIfProductExist(productId: Int): Int {
        for (i in 0 until cartlist!!.cartInfos!!.size) {
            if (productId == cartlist!!.cartInfos!![i].productId) {
                return i
            }
        }
        return -1
    }


    fun removeFromCart(context: Context?, productId: Int?, addonProdId: Long) {

        cartlist = Prefs.with(context).getObject(DataNames.CART, CartList::class.java)

        val pos: Int = if (addonProdId > 0) {
            cartlist?.cartInfos?.indexOfFirst { it.productAddonId == addonProdId } ?: -1
        } else {
            // removeCart(context, productId)
            cartlist?.cartInfos?.indexOfFirst { it.productId == productId } ?: -1
        }

        if (cartlist?.cartInfos?.any { it.productId == productId } == true || cartlist == null) {
            removeCart(context, productId)
        }

        if (pos != -1) cartlist?.cartInfos?.removeAt(pos)

        /*if (cartlist!!.cartInfos!!.size == 0) {
            Prefs.with(context).save(DataNames.SUPPLIERBRANCHID, "")
            Prefs.with(context).save(DataNames.CATEGORY_ID, "")
        }*/

        if (cartlist != null) {
            Prefs.with(context).save(DataNames.CART, cartlist)
        }


        updateNetTotal(cartlist, context)
    }


    fun updateCart(context: Context?, productId: Int?, quantity: Float?, netPrice: Float) {
        cartlist = Prefs.with(context).getObject(DataNames.CART, CartList::class.java)
        for (i in 0 until cartlist!!.cartInfos!!.size) {
            if (productId == cartlist!!.cartInfos!![i].productId) {
                cartlist!!.cartInfos!![i].quantity = quantity ?: 0f
                cartlist!!.cartInfos!![i].price = netPrice
                saveCartQuantity(context, cartlist?.cartInfos?.get(i)?.productId, cartlist?.cartInfos?.get(i)?.quantity
                        ?: 0f)
            }
        }


        Prefs.with(context).save(DataNames.CART, cartlist)


        updateNetTotal(cartlist, context)
        alarmFunction(context)
    }


    fun updateCartInstructions(context: Context?, productId: Int?, specialInstructions: String?) {
        cartlist = Prefs.with(context).getObject(DataNames.CART, CartList::class.java)
        for (i in 0 until cartlist!!.cartInfos!!.size) {
            if (productId == cartlist!!.cartInfos!![i].productId) {
                cartlist!!.cartInfos!![i].productSpecialInstructions = specialInstructions
                saveSpecialInstructions(context, cartlist?.cartInfos?.get(i)?.productId, cartlist?.cartInfos?.get(i)?.productSpecialInstructions
                        ?: "")
            }
        }

        Prefs.with(context).save(DataNames.CART, cartlist)
    }




    fun cartCount(context: Context?, flow: Int): Float {

        when (flow) {
            DataNames.FLOW_BEAUTY_SALOON -> cartlist = Prefs.with(context).getObject(DataNames.CART_SALOON_HOME, CartList::class.java)
            DataNames.FLOW_BEAUTY_SALOON_PLACE -> cartlist = Prefs.with(context).getObject(DataNames.CART_SALOON_PLACE, CartList::class.java)
            DataNames.FLOW_LAUNDRY -> cartlist = Prefs.with(context).getObject(DataNames.CART_LAUNDRY, CartList::class.java)
            else -> cartlist = Prefs.with(context).getObject(DataNames.CART, CartList::class.java)
        }
        var size = 0f
        if (cartlist != null) {
            for (i in 0 until cartlist!!.cartInfos!!.size) {
                size = size + cartlist!!.cartInfos!![i].quantity
            }
        }
        return size
    }

    fun cartCount(context: Context?): Float {

        cartlist = CartList()
        cartlist = Prefs.with(context).getObject(DataNames.CART_SALOON_HOME, CartList::class.java)
        var size = 0f
        if (cartlist != null) {
            for (i in 0 until cartlist!!.cartInfos!!.size) {
                size = size + cartlist!!.cartInfos!![i].quantity
            }
        }
        cartlist = Prefs.with(context).getObject(DataNames.CART_SALOON_PLACE, CartList::class.java)
        if (cartlist != null) {
            for (i in 0 until cartlist!!.cartInfos!!.size) {
                size = size + cartlist!!.cartInfos!![i].quantity
            }
        }
        cartlist = Prefs.with(context).getObject(DataNames.CART, CartList::class.java)
        if (cartlist != null) {
            for (i in 0 until cartlist!!.cartInfos!!.size) {
                size = size + cartlist!!.cartInfos!![i].quantity
            }
        }

        return size
    }

    fun cartCountLaundry(context: Context?): Float {

        cartlist = Prefs.with(context).getObject(DataNames.CART_LAUNDRY, CartList::class.java)
        var size = 0f
        if (cartlist != null) {
            for (i in 0 until cartlist!!.cartInfos!!.size) {
                size += cartlist!!.cartInfos!![i].quantity
            }
        }
        return size
    }

    fun covertCartToArray(context: Context?): List<CartInfoServer> {

        cartlist = Prefs.with(context).getObject(DataNames.CART, CartList::class.java)

        val listCartInfoServers = ArrayList<CartInfoServer>()
        if (cartlist != null) {
            for (i in 0 until cartlist!!.cartInfos!!.size) {
                val cartInfoServer = CartInfoServer()
                Prefs.with(context).save(DataNames.FLOW_PA, cartlist!!.cartInfos!![0].packageType)

                if (cartlist?.cartInfos?.get(i)?.hourlyPrice.isNullOrEmpty()) {
                    cartInfoServer.quantity = cartlist?.cartInfos?.get(i)?.quantity
                } else {
                    cartInfoServer.quantity = 1f
                }

                cartInfoServer.pricetype = cartlist!!.cartInfos!![i].priceType
                cartInfoServer.productId = cartlist!!.cartInfos!![i].productId.toString()
                cartInfoServer.category_id = cartlist!!.cartInfos!![i].categoryId
                cartInfoServer.handlingAdmin = cartlist!!.cartInfos!![i].handlingAdmin
                cartInfoServer.supplier_branch_id = cartlist!!.cartInfos!![i].suplierBranchId
                cartInfoServer.handlingSupplier = cartlist!!.cartInfos!![i].handlingSupplier
                cartInfoServer.supplier_id = cartlist!!.cartInfos!![i].supplierId
                cartInfoServer.agent_type = cartlist!!.cartInfos!![i].agentType
                cartInfoServer.agent_list = cartlist!!.cartInfos!![i].agentList
                cartInfoServer.deliveryType = cartlist!!.cartInfos!![i].deliveryType
                cartInfoServer.fixed_price = cartlist!!.cartInfos!![i].fixed_price
                cartInfoServer.name = cartlist!!.cartInfos!![i].productName
                cartInfoServer.subCatName = cartlist!!.cartInfos!![i].subCategoryName
                cartInfoServer.deliveryCharges = cartlist!!.cartInfos!![i].deliveryCharges
                cartInfoServer.add_ons = cartlist?.cartInfos?.get(i)?.add_ons
                cartInfoServer.price = cartlist!!.cartInfos!![i].price
                cartInfoServer.variants = cartlist?.cartInfos?.get(i)?.varients
                cartInfoServer.question_list = cartlist?.cartInfos?.get(i)?.question_list
                cartInfoServer.isPaymentConfirm = cartlist?.cartInfos?.get(i)?.isPaymentConfirm
                cartInfoServer.appType = cartlist?.cartInfos?.get(i)?.appType
                cartInfoServer.duration = cartlist?.cartInfos?.get(i)?.duration
                cartInfoServer.special_instructions = cartlist?.cartInfos?.get(i)?.productSpecialInstructions
                cartInfoServer.product_owner_name= cartlist?.cartInfos?.get(i)?.product_owner_name
                cartInfoServer.product_reference_id= cartlist?.cartInfos?.get(i)?.product_reference_id
                cartInfoServer.product_upload_reciept= cartlist?.cartInfos?.get(i)?.product_upload_reciept
                cartInfoServer.product_dimensions= cartlist?.cartInfos?.get(i)?.product_dimensions
                cartInfoServer.agentBufferPrice= cartlist?.cartInfos?.get(i)?.agentBufferPrice

                listCartInfoServers.add(cartInfoServer)
            }
        }
        return listCartInfoServers
    }


    fun saveCartQuantity(context: Context?, productId: Int?, quantity: Float) {
        val editor = context?.getSharedPreferences(DataNames.Pref_Cart_Quantity, 0)?.edit()
        editor?.putFloat("" + productId, quantity)
        editor?.apply()
    }

    fun saveSpecialInstructions(context: Context?, productId: Int?, specialInstructions: String) {
        val editor = context?.getSharedPreferences(DataNames.Pref_Cart_Instructions, 0)?.edit()
        editor?.putString("" + productId, specialInstructions)
        editor?.apply()
    }


    fun getCartQuantity(context: Context?, productId: Int?): Float {
        val prefs = context?.getSharedPreferences(DataNames.Pref_Cart_Quantity, 0)
        return prefs?.getFloat("" + productId, 0f) ?: 0f
    }


    fun getCartSpecialInstructions(context: Context?, productId: Int?): String {
        val prefs = context?.getSharedPreferences(DataNames.Pref_Cart_Instructions, 0)
        return prefs?.getString("" + productId, "") ?: ""
    }


    fun removeCart(context: Context?, productId: Int?) {
        val editor = context?.getSharedPreferences(DataNames.Pref_Cart_Quantity, 0)?.edit()
        editor?.remove("" + productId)
        editor?.apply()
    }

    fun removeAllCartLaundry(context: Context?) {
        var editor: SharedPreferences.Editor? = context?.getSharedPreferences(DataNames.Pref_Cart_Quantity_laundry, 0)?.edit()
        editor?.clear()
        editor?.apply()
        editor = context?.getSharedPreferences("netTotalLaundry", 0)?.edit()
        editor?.clear()
        editor?.apply()

    }

    fun removeAllCart(context: Context?) {
        var editor: SharedPreferences.Editor? = context?.getSharedPreferences(DataNames.Pref_Cart_Quantity, 0)?.edit()
        editor?.clear()
        editor?.apply()
        editor = context?.getSharedPreferences(DataNames.Pref_Cart_Quantity_Sallon_Home, 0)?.edit()
        editor?.clear()
        editor?.apply()
        editor = context?.getSharedPreferences(DataNames.Pref_Cart_Quantity_Sallon_Place, 0)?.edit()
        editor?.clear()
        editor?.apply()
        editor = context?.getSharedPreferences(DataNames.Pref_Cart_Quantity_laundry, 0)?.edit()
        editor?.clear()
        editor?.apply()
        editor = context?.getSharedPreferences("netTotalLaundry", 0)?.edit()
        editor?.clear()
        editor?.apply()
        editor = context?.getSharedPreferences(DataNames.CATEGORY_ID_TEST, 0)?.edit()
        editor?.clear()
        editor?.apply()

    }

    fun getCurrency(context: Context?): String {
        return Prefs.with(context).getString(DataNames.CURRENCY, "QAR")
    }


    fun dialogue(context: Context?, message: String,
                 title: String, negativeButton: Boolean, dialogIntrface: DialogIntrface) {
        val builder = AlertDialog.Builder(context!!)
        if (title.isEmpty()) {
            builder.setTitle(context.getString(R.string.update))
        } else
            builder.setTitle(title)
        builder.setMessage(message)
        builder.setPositiveButton(context.getString(R.string.Ok)) { dialog, which ->

            dialogIntrface.onSuccessListener()

            dialog.cancel()

        }

        if (negativeButton) {
            builder.setNegativeButton(context.getString(R.string.cancel)) { dialog, which ->
                dialogIntrface.onErrorListener()
                dialog.cancel()
            }
        }
        builder.setCancelable(false)
        builder.show()


    }


    fun getLanguage(context: Context?): Int {

        val selectedLang = Prefs.with(context).getString(DataNames.SELECTED_LANGUAGE, ClikatConstants.ENGLISH_FULL)

        return when (selectedLang) {
            ClikatConstants.ENGLISH_FULL, ClikatConstants.ENGLISH_SHORT -> ClikatConstants.LANGUAGE_ENGLISH
            else -> ClikatConstants.LANGUAGE_OTHER
        }
    }


    fun getAccesstoken(context: Context?): String {
        val signUp = Prefs.with(context).getObject(DataNames.USER_DATA, PojoSignUp::class.java)
        return if (signUp?.data != null && signUp.data.access_token != null)
            signUp.data?.access_token ?: ""
        else
            ""
    }


    fun isLoginProperly(activity: Context?): PojoSignUp {
        var pojoSignUp = Prefs.with(activity).getObject(DataNames.USER_DATA, PojoSignUp::class.java)

        if (pojoSignUp?.data != null &&
                pojoSignUp.data.otp_verified != null && pojoSignUp.data.otp_verified == 1) {
            Log.d("", "")
        } else {
            pojoSignUp = PojoSignUp()
        }
        Prefs.with(activity).save(DataNames.USER_DATA, pojoSignUp)

        return pojoSignUp
    }

    fun isInternetConnected(context: Context?): Boolean {
        val detector = ConnectionDetector(context)
        return detector.isConnectingToInternet
    }

    fun showNoInternetDialog(context: Context?) {
        context?.startActivity(Intent(context, NoInternetActivity::class.java))
    }


    fun colorStatusProduct(tvOrder: TextView?, status: Double?, mContext: Context?, isRec: Boolean) {

        when (status) {

            OrderStatus.Pending.orderStatus,OrderStatus.Admin_Canceled.orderStatus, OrderStatus.Rejected.orderStatus, OrderStatus.Rating_Given.orderStatus, OrderStatus.Customer_Canceled.orderStatus -> {
                Log.e("API ERROr in status", "API error in status")
                if (isRec)
                    tvOrder?.setBackgroundResource(R.drawable.red_rec)
                tvOrder?.setTextColor(ContextCompat.getColor(mContext!!, R.color.red))
            }
            OrderStatus.Approved.orderStatus -> {
                if (isRec)
                    tvOrder?.setBackgroundResource(R.drawable.brown_rec)
                tvOrder?.setTextColor(ContextCompat.getColor(mContext!!, R.color.brown))
            }

            OrderStatus.On_The_Way.orderStatus, OrderStatus.Near_You.orderStatus, OrderStatus.Track.orderStatus, OrderStatus.Packed.orderStatus -> {
                if (isRec)
                    tvOrder?.setBackgroundResource(R.drawable.yellow_rec)
                tvOrder?.setTextColor(ContextCompat.getColor(mContext!!, R.color.yellow))
            }


            OrderStatus.Reached.orderStatus, OrderStatus.Ended.orderStatus, OrderStatus.Scheduled.orderStatus -> {
                if (isRec)
                    tvOrder?.setBackgroundResource(R.drawable.green_rec)
                tvOrder?.setTextColor(ContextCompat.getColor(mContext!!, R.color.light_green))
            }
        }

    }


    fun getStatusText(status: Double?, context: Context?): String? {
        return when (status) {
            OrderStatus.On_The_Way.orderStatus -> context?.getString(R.string.on_the_way)
            OrderStatus.PickUp.orderStatus -> context?.getString(R.string.picked_up_tag)
            OrderStatus.Arrived.orderStatus -> context?.getString(R.string.arrived)
            else -> ""
        }
    }

    fun statusProduct1(status: Double?, appType: Int, selfPickup: Int?, context: Context?, orderTerminology: String): String? {

        val terminologyBean = if (orderTerminology.isNotEmpty()) {
            Gson().fromJson(orderTerminology, SettingModel.DataBean.Terminology::class.java)
        } else {
            Prefs.getPrefs().getObject(PrefenceConstants.APP_TERMINOLOGY, SettingModel.DataBean.Terminology::class.java)
        }

        val languageId = Prefs.getPrefs().getString(DataNames.SELECTED_LANGUAGE, ClikatConstants.ENGLISH_FULL)

        var appTerminology: SettingModel.DataBean.AppTerminology? = null
        if (terminologyBean != null) {
            appTerminology = if (languageId == ClikatConstants.ENGLISH_FULL || languageId == ClikatConstants.ENGLISH_SHORT) {
                terminologyBean.english
            } else {
                terminologyBean.other
            }
        }

        when (status) {
            OrderStatus.Pending.orderStatus -> return if (appTerminology != null && appTerminology.status.PENDING?.isNotEmpty() == true)
                appTerminology.status.PENDING else when (appType) {
                AppDataType.Food.type -> context?.resources?.getString(R.string.placed)
                else -> context?.resources?.getString(R.string.pending)
            }
            OrderStatus.Approved.orderStatus, OrderStatus.Confirmed.orderStatus -> return if (appTerminology != null &&
                    appTerminology.status.ACCEPTED?.isNotEmpty() == true)
                appTerminology.status.ACCEPTED else when (appType) {
                AppDataType.Ecom.type -> context?.resources?.getString(R.string.approved)
                else -> context?.resources?.getString(R.string.confirmed)
            }

            OrderStatus.Rejected.orderStatus -> return if (appTerminology != null && appTerminology.status.REJECTED?.isNotEmpty() == true)
                appTerminology.status.REJECTED else context?.resources?.getString(R.string.reject)

            OrderStatus.In_Kitchen.orderStatus -> return  context?.resources?.getString(R.string.cooking)

            OrderStatus.On_The_Way.orderStatus, OrderStatus.Started.orderStatus -> {
                return if (appTerminology != null && appTerminology.status.ON_THE_WAY?.isNotEmpty() == true)
                    appTerminology.status.ON_THE_WAY else when (appType) {
                    AppDataType.HomeServ.type -> context?.resources?.getString(R.string.started)
                    AppDataType.Ecom.type -> context?.resources?.getString(R.string.out_delivery)
                    else -> context?.resources?.getString(R.string.on_the_way)
                }
            }

            OrderStatus.Near_You.orderStatus ->
                return if (appTerminology != null && appTerminology.status.NEAR_YOU?.isNotEmpty() == true)
                    appTerminology.status.NEAR_YOU
                else context?.resources?.getString(R.string.near_you)

            OrderStatus.Ended.orderStatus, OrderStatus.Delivered.orderStatus -> {
                return if (appTerminology != null && appTerminology.status.DELIVERED?.isNotEmpty() == true)
                    appTerminology.status.DELIVERED else when (appType) {
                    AppDataType.HomeServ.type -> context?.resources?.getString(R.string.ended)
                    AppDataType.Food.type -> {
                        when (selfPickup) {
                            FoodAppType.Pickup.foodType -> context?.resources?.getString(R.string.picked_up)
                            FoodAppType.DineIn.foodType -> context?.resources?.getString(R.string.served)
                            else -> context?.resources?.getString(R.string.driver_at_location)
                        }
                    }
                    else -> context?.resources?.getString(R.string.driver_at_location)
                }
            }

            OrderStatus.Reached.orderStatus, OrderStatus.Shipped.orderStatus, OrderStatus.Ready_to_be_picked.orderStatus -> {
                return if (selfPickup == FoodAppType.DineIn.foodType) {
                    context?.resources?.getString(R.string.ready_to_serve)
                } else if (appTerminology != null && appTerminology.status.SHIPPED?.isNotEmpty() == true) {
                    appTerminology.status.SHIPPED
                } else {
                    when (appType) {
                        AppDataType.HomeServ.type -> context?.resources?.getString(R.string.reached)
                        AppDataType.Food.type -> context?.resources?.getString(R.string.ready_to_pick)
                        else -> context?.resources?.getString(R.string.ready_to_pick)
                    }
                }
            }
            OrderStatus.Admin_Canceled.orderStatus -> return if (appTerminology != null && appTerminology.status.ADMIN_CANCEL?.isNotEmpty() == true)
                appTerminology.status.ADMIN_CANCEL else context?.resources?.getString(R.string.admin_camncel)

            OrderStatus.Rating_Given.orderStatus -> return if (appTerminology != null && appTerminology.status.RATE_GIVEN?.isNotEmpty() == true)
                appTerminology.status.RATE_GIVEN else context?.resources?.getString(R.string.rating_given)

            OrderStatus.Customer_Canceled.orderStatus -> return if (appTerminology != null && appTerminology.status.CUSTOMER_CANCEL?.isNotEmpty() == true)
                appTerminology.status.CUSTOMER_CANCEL else context?.resources?.getString(R.string.customer_cancelled)


            OrderStatus.Packed.orderStatus, OrderStatus.In_Kitchen.orderStatus -> {
                return if (appTerminology != null && appTerminology.status.PACKED?.isNotEmpty() == true)
                    appTerminology.status.PACKED else when (appType) {
                    AppDataType.Ecom.type -> context?.resources?.getString(R.string.packed)
                    AppDataType.Food.type -> context?.resources?.getString(R.string.in_kitchen)
                    else -> context?.resources?.getString(R.string.on_the_way)
                }
            }
        }

        return ""
    }


    fun statusProduct(status: Double?, appType: Int, selfPickup: Int?, context: Context?, orderTerminology: String): String? {

        val terminologyBean = if (orderTerminology.isNotEmpty()) {
            Gson().fromJson(orderTerminology, SettingModel.DataBean.Terminology::class.java)
        } else {
            Prefs.getPrefs().getObject(PrefenceConstants.APP_TERMINOLOGY, SettingModel.DataBean.Terminology::class.java)
        }

        val languageId = Prefs.getPrefs().getString(DataNames.SELECTED_LANGUAGE, ClikatConstants.ENGLISH_FULL)

        var appTerminology: SettingModel.DataBean.AppTerminology? = null
        if (terminologyBean != null) {
            appTerminology = if (languageId == ClikatConstants.ENGLISH_FULL || languageId == ClikatConstants.ENGLISH_SHORT) {
                terminologyBean.english
            } else {
                terminologyBean.other
            }
        }

        when (status) {
            OrderStatus.Pending.orderStatus -> return if (appTerminology != null && appTerminology.status.PENDING?.isNotEmpty() == true)
                appTerminology.status.PENDING else when (appType) {
                AppDataType.Food.type -> context?.resources?.getString(R.string.placed)
                else -> context?.resources?.getString(R.string.pending)
            }
            OrderStatus.Approved.orderStatus, OrderStatus.Confirmed.orderStatus -> return if (appTerminology != null &&
                    appTerminology.status.ACCEPTED?.isNotEmpty() == true)
                appTerminology.status.ACCEPTED else when (appType) {
                AppDataType.Ecom.type -> context?.resources?.getString(R.string.approved)
                else -> context?.resources?.getString(R.string.confirmed)
            }

            OrderStatus.Rejected.orderStatus -> return if (appTerminology != null && appTerminology.status.REJECTED?.isNotEmpty() == true)
                appTerminology.status.REJECTED else context?.resources?.getString(R.string.reject)

            OrderStatus.In_Kitchen.orderStatus -> return  context?.resources?.getString(R.string.cooking)

            OrderStatus.On_The_Way.orderStatus, OrderStatus.Started.orderStatus -> {
                return if (appTerminology != null && appTerminology.status.ON_THE_WAY?.isNotEmpty() == true)
                    appTerminology.status.ON_THE_WAY else when (appType) {
                    AppDataType.HomeServ.type -> context?.resources?.getString(R.string.started)
                    AppDataType.Ecom.type -> context?.resources?.getString(R.string.out_delivery)
                    else -> context?.resources?.getString(R.string.on_the_way)
                }
            }

            OrderStatus.Near_You.orderStatus ->
                return if (appTerminology != null && appTerminology.status.NEAR_YOU?.isNotEmpty() == true)
                    appTerminology.status.NEAR_YOU
                else context?.resources?.getString(R.string.near_you)

            OrderStatus.Ended.orderStatus, OrderStatus.Delivered.orderStatus -> {
                return if (appTerminology != null && appTerminology.status.DELIVERED?.isNotEmpty() == true)
                    appTerminology.status.DELIVERED else when (appType) {
                    AppDataType.HomeServ.type -> context?.resources?.getString(R.string.ended)
                    AppDataType.Food.type -> {
                        when (selfPickup) {
                            FoodAppType.Pickup.foodType -> context?.resources?.getString(R.string.picked_up)
                            FoodAppType.DineIn.foodType -> context?.resources?.getString(R.string.served)
                            else -> context?.resources?.getString(R.string.delivered)
                        }
                    }
                    else -> context?.resources?.getString(R.string.delivered)
                }
            }

            OrderStatus.Reached.orderStatus, OrderStatus.Shipped.orderStatus, OrderStatus.Ready_to_be_picked.orderStatus -> {
                return if (selfPickup == FoodAppType.DineIn.foodType) {
                    context?.resources?.getString(R.string.ready_to_serve)
                } else if (appTerminology != null && appTerminology.status.SHIPPED?.isNotEmpty() == true) {
                    appTerminology.status.SHIPPED
                } else {
                    when (appType) {
                        AppDataType.HomeServ.type -> context?.resources?.getString(R.string.reached)
                        AppDataType.Food.type -> context?.resources?.getString(R.string.ready_to_pick)
                        else -> context?.resources?.getString(R.string.ready_to_pick)
                    }
                }
            }
            OrderStatus.Admin_Canceled.orderStatus -> return if (appTerminology != null && appTerminology.status.ADMIN_CANCEL?.isNotEmpty() == true)
                appTerminology.status.ADMIN_CANCEL else context?.resources?.getString(R.string.admin_camncel)

            OrderStatus.Rating_Given.orderStatus -> return if (appTerminology != null && appTerminology.status.RATE_GIVEN?.isNotEmpty() == true)
                appTerminology.status.RATE_GIVEN else context?.resources?.getString(R.string.rating_given)

            OrderStatus.Customer_Canceled.orderStatus -> return if (appTerminology != null && appTerminology.status.CUSTOMER_CANCEL?.isNotEmpty() == true)
                appTerminology.status.CUSTOMER_CANCEL else context?.resources?.getString(R.string.customer_cancelled)


            OrderStatus.Packed.orderStatus, OrderStatus.In_Kitchen.orderStatus -> {
                return if (appTerminology != null && appTerminology.status.PACKED?.isNotEmpty() == true)
                    appTerminology.status.PACKED else when (appType) {
                    AppDataType.Ecom.type -> context?.resources?.getString(R.string.packed)
                    AppDataType.Food.type -> context?.resources?.getString(R.string.in_kitchen)
                    else -> context?.resources?.getString(R.string.on_the_way)
                }
            }
        }

        return ""
    }

    fun sweetDialogueSuccess11(context: Context?, title: String, message: String,
                               negativeButton: Boolean, flag: Int, clickListener: DialogIntrface, isCancelable: Boolean = false) {


        val sweetAlertDialog = AlertDialog.Builder(context!!)
        sweetAlertDialog.setTitle(HtmlCompat.fromHtml(title, HtmlCompat.FROM_HTML_MODE_LEGACY))
        sweetAlertDialog.setMessage(message)
        if (isCancelable)
            sweetAlertDialog.setCancelable(false)
        if (negativeButton) {
            sweetAlertDialog.setNegativeButton(context.getString(R.string.cancel)) { dialog, which ->
                dialog.dismiss()
            }
        }
        sweetAlertDialog.setPositiveButton(context.getString(R.string.ok)) { dialog, which ->

            when (flag) {
                1001 -> {
                    clickListener.onSuccessListener()
                }

            }

            dialog.dismiss()
        }
        sweetAlertDialog.setOnDismissListener { clearCart(context) }
        sweetAlertDialog.setCancelable(false)
        sweetAlertDialog.show()

    }

    fun tableCapacityDialog(context: Context,clickListener: OnTableCapacityListener) {

        val dialog = Dialog(context)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.dialog_select_table)
        dialog.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        dialog.setCancelable(false)

        val ivCross = dialog.findViewById(R.id.ivCross) as ImageView
        val etCapacity = dialog.findViewById(R.id.etTableCapacity) as EditText
        val tvConfirm = dialog.findViewById(R.id.tvContinue) as MaterialTextView

      /*  val tableBookingList=ArrayList<String>()
        tableBookingList.clear()
        tableCapacity.add(0,context.getString(R.string.select_table_capacity))*/

        //val spinnerCapacity=dialog.findViewById<Spinner>(R.id.spinnerCapacity)
      /*  val dataAdapter: ArrayAdapter<String> = ArrayAdapter<String>(context, android.R.layout.simple_spinner_item, tableCapacity)
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerCapacity.adapter = dataAdapter*/
/*
        spinnerCapacity.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                tvCapacity.text = spinnerCapacity?.selectedItem?.toString()
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                //do nothing
            }
        }*/

        ivCross.setOnClickListener {
            dialog.dismiss()
        }

        /*tvCapacity.setOnClickListener {
            spinnerCapacity?.performClick()
        }*/

        tvConfirm.setOnClickListener {
            if(etCapacity.text.toString().trim().isEmpty())
                AppToasty.error(context,context.getString(R.string.enter_person_quantity))
            else
            {
                clickListener.onTableCapacitySelected(etCapacity.text.toString().trim().toInt())
                dialog.dismiss()
            }
        }
        dialog.show()

    }

    fun sweetDialogueSuccess(context: Context?, title: String, message: String,
                             negativeButton: Boolean, flag: Int, history2: OrderHistory2?, cartIds: ArrayList<Int>?) {

        val sweetAlertDialog = AlertDialog.Builder(context!!)
        sweetAlertDialog.setTitle(title)
        sweetAlertDialog.setMessage(message)
        if (negativeButton) {
            sweetAlertDialog.setNegativeButton(context.getString(R.string.cancel)) { dialog, which ->
                dialog.dismiss()
            }
        }

        sweetAlertDialog.setPositiveButton(context.getString(R.string.ok)) { dialog, which ->
            dialog.dismiss()

            when (flag) {

                501 -> {
                    removeAllCart(context)
                    Prefs.with(context).removeAll()
                    context.startActivity(Intent(context, SplashActivity::class.java))
                    (context as Activity).finishAffinity()
                }
            }

        }
        sweetAlertDialog.show()

    }


    fun sweetDialogueFailure(context: Context?, title: String, message: String,
                             negativeButton: Boolean, flag: Int, wallet_module: String) {

        val sweetAlertDialog = AlertDialog.Builder(context!!)
        sweetAlertDialog.setTitle(title)
        sweetAlertDialog.setMessage(message)

        if (negativeButton) {
            sweetAlertDialog.setNegativeButton(context.getString(R.string.cancel), null)
        }

        sweetAlertDialog.setPositiveButton(context.getString(R.string.ok)) { dialog, which ->
            if (flag == 500) {
                sweetDialogueSuccess(context, context.getString(R.string.success), context.getString(R.string.success_logout), false, 501, null, null)
            } else if (flag == 101) {
                val cancelWallet = if (wallet_module == "1") 1 else 0
                EventBus.getDefault().post(OrderEvent(AppConstants.CANCEL_EVENT, cancelWallet))
            }
            dialog.dismiss()
        }

        sweetAlertDialog.show()
    }

    fun cancelOrderWallet(context: Context, text: String) {
        val cancelDialog = Dialog(context)
        cancelDialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        cancelDialog.setContentView(R.layout.layout_cancel_order)
        cancelDialog.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        cancelDialog.setCancelable(false)

        val ivCross = cancelDialog.findViewById(R.id.ivCross) as ImageView
        val btnCancelOrder = cancelDialog.findViewById(R.id.btnReturnProd) as Button
        val rbWallet = cancelDialog.findViewById(R.id.rbWallet) as RadioButton

        ivCross.setOnClickListener {
            cancelDialog.dismiss()
        }

        btnCancelOrder.setOnClickListener {
            val cancelToWallet = if (rbWallet.isChecked) 1 else 0

            EventBus.getDefault().post(OrderEvent(AppConstants.CANCEL_EVENT, cancelToWallet))
            cancelDialog.dismiss()
        }
        cancelDialog.show()
    }

    fun pxToDp(px: Float, context: Context?): Float {
        return px / ((context?.resources?.displayMetrics?.densityDpi?.toFloat()
                ?: 0f) / DisplayMetrics.DENSITY_DEFAULT)
    }

    fun dpFromPx(px: Int, context: Context?): Int {
        return (px / (context?.resources?.displayMetrics?.density ?: 0f)).toInt()
    }

    fun pxFromDp(dp: Int, context: Context?): Int {
        return (dp * (context?.resources?.displayMetrics?.density ?: 0f)).toInt()
    }

    fun isValidColorHex(color: String?): Boolean {
        if (color == null) return false
        val colorPattern = Pattern.compile("#([0-9a-f]{3}|[0-9a-f]{6}|[0-9a-f]{8})")
        val m = colorPattern.matcher(color.toLowerCase())
        return m.matches()
    }


    fun setStatusBarColor(activity: AppCompatActivity, color: Int) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val window = activity.window
            if (color == Color.BLACK && window.navigationBarColor == Color.BLACK) {
                window.clearFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            } else {
                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            }
            window.statusBarColor = color
        }
    }

    fun changeStrokeColor(color: String): GradientDrawable {
        val gradient = GradientDrawable()
        gradient.shape = GradientDrawable.RECTANGLE
        gradient.setStroke(1, Color.parseColor(color))
        gradient.cornerRadii = floatArrayOf(8f, 8f, 8f, 8f, 8f, 8f, 8f, 8f)
        return gradient
    }


    fun changeBorderColor(color: String, strokeColor: String, shape: Int): GradientDrawable {
        val gradient = GradientDrawable()
        gradient.shape = shape
        if (strokeColor.isEmpty()) {
            gradient.setColor(Color.parseColor(color))
            gradient.setStroke(3, Color.parseColor(color))
        } else
            gradient.setStroke(2, Color.parseColor(strokeColor))


        if (shape == GradientDrawable.RADIAL_GRADIENT) {
            gradient.cornerRadius = 20f
            // gradient.setCornerRadii(new float[]{3, 3, 3, 3, 0, 0, 0, 0});
        } else {
            gradient.cornerRadii = floatArrayOf(8f, 8f, 8f, 8f, 8f, 8f, 8f, 8f)
        }
        return gradient
    }


    fun varientColor(color: String, strokeColor: String, shape: Int): GradientDrawable {
        val gradient = GradientDrawable()
        gradient.shape = shape

        gradient.setColor(Color.parseColor(color))
        gradient.setStroke(3, Color.parseColor(strokeColor))

        if (shape == GradientDrawable.RADIAL_GRADIENT) {
            gradient.cornerRadius = 20f
        } else {
            gradient.cornerRadii = floatArrayOf(8f, 8f, 8f, 8f, 8f, 8f, 8f, 8f)
        }


        return gradient
    }


    fun changeBorderTextColor(color: String, shape: Int): GradientDrawable {
        val gradient = GradientDrawable()
        gradient.shape = shape

        gradient.setColor(Color.parseColor(color))
        gradient.setStroke(3, Color.parseColor(color))

        gradient.cornerRadii = floatArrayOf(8f, 8f, 8f, 8f, 8f, 8f, 8f, 8f)

        return gradient
    }


    fun changeLocationStroke(color: String, type: String): GradientDrawable {

        val strokeWidth: Int
        if (type == "rate")
            strokeWidth = 30
        else
            strokeWidth = 20

        val gradient = GradientDrawable()
        gradient.shape = GradientDrawable.RECTANGLE
        gradient.cornerRadius = strokeWidth.toFloat()
        gradient.setColor(Color.parseColor(color))

        return gradient
    }


    fun changeCategoryColor(): GradientDrawable {
        val ButtonColors = intArrayOf(Color.parseColor("#00FFFFFF"), Color.parseColor("#A8000000"))

        val gradientDrawable = GradientDrawable(
                GradientDrawable.Orientation.TOP_BOTTOM, ButtonColors)
        gradientDrawable.shape = GradientDrawable.LINEAR_GRADIENT
        gradientDrawable.cornerRadius = 10f

        return gradientDrawable
    }


    fun changeGradientColor(): GradientDrawable {
        val ButtonColors = intArrayOf(Color.parseColor("#1A000000"), Color.parseColor("#1A000000"))

        return GradientDrawable(
                GradientDrawable.Orientation.TOP_BOTTOM, ButtonColors)
    }

    fun hideKeyboard(activity: Activity) {
        val imm = activity.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        //Find the currently focused view, so we can grab the correct window token from it.
        var view = activity.currentFocus
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = View(activity)
        }
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }

    fun convertDateOneToAnother(dateToConvert: String, toDate: String, fromDate: String): String {
        var outputDateStr = ""
        val inputFormat = SimpleDateFormat(toDate, DateTimeUtils.timeLocale)
        val outputFormat = SimpleDateFormat(fromDate, DateTimeUtils.timeLocale)
        val date: Date?
        try {
            date = inputFormat.parse(dateToConvert)
            outputDateStr = outputFormat.format(date!!)
        } catch (e: ParseException) {
            e.printStackTrace()
        }

        return outputDateStr
    }


    fun isVideoFile(path: String): Boolean {
        val mimeType = URLConnection.guessContentTypeFromName(path)
        return mimeType != null && mimeType.startsWith("video")
    }

    fun loadImage(url: String?, imageView: ImageView, roundedShape: Boolean, imageHeight: Int? = null, imageWidth: Int? = null) {

        val glide = Glide.with(imageView.context)


        val requestOptions = RequestOptions
                .bitmapTransform(RoundedCornersTransformation(8, 0,
                        RoundedCornersTransformation.CornerType.ALL))
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .placeholder(if (AppConstants.APP_SUB_TYPE == AppDataType.Food.type) R.drawable.iv_placeholder else R.drawable.white_placeholder)
                .error(if (AppConstants.APP_SUB_TYPE == AppDataType.Food.type) R.drawable.iv_placeholder else R.drawable.white_placeholder)


        glide.load(url)
                .apply(requestOptions).into(imageView)

    }

    fun loadImageUrl(url: String,imageView:ImageView) {


        val glide = Glide.with(imageView.context)


        val requestOptions = RequestOptions
                .bitmapTransform(RoundedCornersTransformation(8, 0,
                        RoundedCornersTransformation.CornerType.ALL))
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .placeholder(if (AppConstants.APP_SUB_TYPE == AppDataType.Food.type) R.drawable.iv_placeholder else R.drawable.white_placeholder)
                .error(if (AppConstants.APP_SUB_TYPE == AppDataType.Food.type) R.drawable.iv_placeholder else R.drawable.white_placeholder)

        glide.load(url)
                .apply(requestOptions)
                .listener(object : RequestListener<Drawable> {
                    override fun onLoadFailed(e: GlideException?, model: Any?, target: com.bumptech.glide.request.target.Target<Drawable>?, isFirstResource: Boolean): Boolean {
                        return false // important to return false so the error placeholder can be placed
                    }

                    override fun onResourceReady(resource: Drawable?, model: Any?, target: com.bumptech.glide.request.target.Target<Drawable>?, dataSource: DataSource?, isFirstResource: Boolean): Boolean {
                        return false
                    }
                }).into(imageView)


    }

    fun loadUserImage(url: String?, imageView: ImageView, roundedShape: Boolean) {
        var thumbUrl = ""

        /*if (url != null) {
            thumbUrl = url.substring(0, url.lastIndexOf("/") + 1) + "thumb_" + url.substring(url.lastIndexOf("/") + 1)
        }*/

        val glide = Glide.with(imageView.context)


        val requestOptions = RequestOptions
                .bitmapTransform(RoundedCornersTransformation(8, 0,
                        RoundedCornersTransformation.CornerType.ALL))
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .placeholder(R.drawable.ic_user_placeholder)
                .error(R.drawable.ic_user_placeholder)


        glide.load(url)
                //.thumbnail(Glide.with(imageView.context).load(thumbUrl))
                .apply(requestOptions).into(imageView)

    }


    fun openCustomChrome(activity: Context, url: String) {
        try {
            var uri = url
            if (!uri.startsWith("https://") && !uri.startsWith("http://")) {
                uri = "http://$uri"
            }

            val builder = CustomTabsIntent.Builder().build()
            builder.launchUrl(activity, Uri.parse(uri))
        } catch (e: ActivityNotFoundException) {
            e.printStackTrace()
        }
    }


    fun openUrl(activity: Context, url: String) {
        try {
            val myIntent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
            activity.startActivity(myIntent)
        } catch (e: ActivityNotFoundException) {

            e.printStackTrace()
        }
    }

    fun getStatus(context: Context?, status: String): String? {
        return when (status) {
            "awaiting_shipment" -> context?.getString(R.string.awaiting_payment)
            "cancelled" -> context?.getString(R.string.cancelled)
            "shipped" -> context?.getString(R.string.shipped)
            "onhold" -> context?.getString(R.string.onhold)
            else -> status
        }
    }

    fun convertFromUtcFormat(format: String, time: String): String {
        return if (time != "") {
            val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", DateTimeUtils.timeLocale)
            val mDate = sdf.parse(time) ?: Date()
            val cal = Calendar.getInstance()
            if (cal.timeZone.inDaylightTime(mDate)) {
                cal.timeInMillis = mDate.time + cal.timeZone.rawOffset + cal.timeZone.dstSavings
            } else
                cal.timeInMillis = mDate.time + cal.timeZone.rawOffset
            val dateFormat = SimpleDateFormat(format, DateTimeUtils.timeLocale)
            dateFormat.format(cal.time)
        } else {
            ""
        }
    }

    fun getSectionsName(sectionName:String):String{
        return when(sectionName){
            "trending_rest"->HomeItemAdapter.HORIZONTAL_SUPPLIERS
            "special_offer"->HomeItemAdapter.SPL_PROD_TYPE
            "best_seller"->HomeItemAdapter.BEST_SELLERS
            "top_selling"->HomeItemAdapter.POPULAR_TYPE
            "fastest_del"->HomeItemAdapter.FASTEST_DELIVERY_SUPPLIERS
            "recomm_items"->HomeItemAdapter.RECOMMENDED_FOOD
            "recomm_supplier"->HomeItemAdapter.RECOMEND_TYPE
            "near_you"->HomeItemAdapter.CLIKAT_THEME_SUPPLIERS
            "highest_rating_resturant" -> HomeItemAdapter.HIGEST_RATING_SUPPLIERS
            "new_resturant" -> HomeItemAdapter.NEW_RESTUARENT_SUPPLIERS
            "category_wise_rest" -> HomeItemAdapter.CATEGORY_WISE_SUPPLIERS
            "show_categories"->HomeItemAdapter.CATEGORY_TYPE
            "recent_orders"->HomeItemAdapter.RECENT_ORDERS
            else->HomeItemAdapter.CLIKAT_THEME_SUPPLIERS
        }
    }
    fun getFilterByType(sectionName:String):Int{
        return when(sectionName){
            "pickup"->FilterByData.Pickup.type
            "dinein"->FilterByData.DineIn.type
            else->FilterByData.Delivery.type
        }
    }

    fun getDeliveryType(sectionName:String):Int{
        return when(sectionName){
            "pickup"->FoodAppType.Pickup.foodType
            "dinein"->FoodAppType.DineIn.foodType
            else->FoodAppType.Delivery.foodType
        }
    }

    fun getTabIcon(sectionName:String):Int{
        return when(sectionName){
            "pickup"->R.drawable.ic_pickup
            "dinein"->R.drawable.ic_dine
            else->R.drawable.ic_delivery
        }
    }

    fun getSelfPickup(sectionName:String):Int{
        return when(sectionName){
            "pickup"->FoodAppType.Pickup.foodType
            "dinein"->FoodAppType.DineIn.foodType
            else->FoodAppType.Delivery.foodType
        }
    }
    fun getSortByValue(sectionName:String):String{
        return when(sectionName){
            "Distance"-> SortBy.SortByDistance.sortBy.toString()
            "Rating"->SortBy.SortByRating.sortBy.toString()
            "New"->SortBy.SortByNew.sortBy.toString()
            "Alphabetically"->SortBy.SortByATZ.sortBy.toString()
            "Open"->SortBy.SortByOpen.sortBy.toString()
            "Closed"->SortBy.SortByClose.sortBy.toString()
            else->SortBy.SortByDistance.sortBy.toString()
        }
    }

    fun redirectToPlayStore(context: Activity) {
        val appPackageName: String = context.packageName
        try {
            context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=$appPackageName")))
        } catch (anfe: ActivityNotFoundException) {
            context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=$appPackageName")))
        }
    }

    fun Context.sendEmail(email: String?) {
        try {
            if (!email.isNullOrEmpty()) {
                val emailIntent = Intent(Intent.ACTION_SENDTO)
                emailIntent.data = Uri.parse("mailto:$email")
                startActivity(emailIntent)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }
    fun Context.isNightModeEnabled(): Boolean? {
        return (resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK)== Configuration.UI_MODE_NIGHT_YES
    }

    fun isAppInstalled(context: Context, packageName: String): Boolean {
        return try {
            context.packageManager.getApplicationInfo(packageName, 0)
            true
        } catch (e: PackageManager.NameNotFoundException) {
            false
        }
    }
    fun Context.callPhone(number: String) {
        val intent = Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", number, null))
        startActivity(intent)
    }
    fun Context.redirectToWhatsApp(phoneNumber:String,message: String){
        val packageManager: PackageManager = packageManager
        val i = Intent(Intent.ACTION_VIEW)

        try {
            val url = "https://api.whatsapp.com/send?phone=$phoneNumber" /*+ "&text=" + URLEncoder.encode(message, "UTF-8")*/
            i.setPackage("com.whatsapp")
            i.data = Uri.parse(url)
            if (i.resolveActivity(packageManager) != null) {
                startActivity(i)
            }
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }

    fun Context.openWhatsApp(phoneNumber:String) {

        val isWhatsappInstalled = whatsappInstalledOrNot()
        if (isWhatsappInstalled) {
            val sendIntent = Intent("android.intent.action.MAIN")
            sendIntent.component = ComponentName("com.whatsapp", "com.whatsapp.Conversation")
            sendIntent.putExtra("jid", PhoneNumberUtils.stripSeparators(phoneNumber).toString() + "@s.whatsapp.net") //phone number without "+" prefix
            startActivity(sendIntent)
        } else {
            try {
                val url = "https://api.whatsapp.com/send?phone=${PhoneNumberUtils.stripSeparators(phoneNumber)}"
                val i = Intent(Intent.ACTION_VIEW)
                i.data = Uri.parse(url)
                startActivity(i)
            } catch (ex: Exception) {
                ex.printStackTrace()
            }
        }
    }

    private fun Context.whatsappInstalledOrNot(): Boolean {
        val pm: PackageManager = packageManager
        val app_installed: Boolean
        app_installed = try {
            pm.getPackageInfo("com.whatsapp", PackageManager.GET_ACTIVITIES)
            true
        } catch (e: PackageManager.NameNotFoundException) {
            false
        }
        return app_installed
    }

    fun getJsonCountries(it: String): JSONArray {
        val listType: Type = object : TypeToken<ArrayList<SettingModel.DataBean.CountryCodes?>?>() {}.type
        val countryCodes: ArrayList<SettingModel.DataBean.CountryCodes> = Gson().fromJson(it, listType)
        val arrayList=ArrayList<String>()
        countryCodes.forEach {country->
            arrayList.add(country.iso?:"")
        }
        return JSONArray(arrayList)
    }

    fun checkVisibility(
            keyValue: String?,
            infoType: String?): Boolean {
        /*return true if view is visible*/
        if (infoType.isNullOrEmpty() || infoType == "0") return true  /*check default case no need to check views basis of keys*/

        return keyValue != null && keyValue != "0"
    }

    fun EditText.focus() {
        requestFocus()
        setSelection(length())
    }
}