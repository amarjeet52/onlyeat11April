package com.codebrew.clikat.module.splash

import android.app.Activity
import android.app.Dialog
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.*
import android.util.Base64
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.Window
import android.widget.EditText
import android.widget.ImageView
import android.widget.VideoView
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.Target
import com.clevertap.android.sdk.CleverTapAPI
import com.codebrew.clikat.BR
import com.codebrew.clikat.BuildConfig
import com.codebrew.clikat.R
import com.codebrew.clikat.app_utils.AppUtils
import com.codebrew.clikat.app_utils.CommonUtils
import com.codebrew.clikat.app_utils.CommonUtils.Companion.getaddress
import com.codebrew.clikat.app_utils.DialogsUtil
import com.codebrew.clikat.app_utils.PermissionFile
import com.codebrew.clikat.app_utils.extension.launchActivity
import com.codebrew.clikat.app_utils.extension.onSnackbar
import com.codebrew.clikat.base.BaseActivity
import com.codebrew.clikat.data.AppVersionUpdate
import com.codebrew.clikat.data.VendorAppType
import com.codebrew.clikat.data.constants.AppConstants
import com.codebrew.clikat.data.constants.PrefenceConstants
import com.codebrew.clikat.data.model.api.GetDbKeyModel
import com.codebrew.clikat.data.model.others.GlobalTableDataHolder
import com.codebrew.clikat.data.network.HostSelectionInterceptor
import com.codebrew.clikat.data.preferences.PreferenceHelper
import com.codebrew.clikat.databinding.ActivitySplashBinding
import com.codebrew.clikat.databinding.DialogEnterCodeBinding
import com.codebrew.clikat.di.ViewModelProviderFactory
import com.codebrew.clikat.modal.AppGlobal
import com.codebrew.clikat.modal.DataCount
import com.codebrew.clikat.modal.LocationUser
import com.codebrew.clikat.modal.other.SettingModel
import com.codebrew.clikat.module.instruction_page.InstructionActivity
import com.codebrew.clikat.module.location.LocationActivity
import com.codebrew.clikat.preferences.DataNames
import com.codebrew.clikat.retrofit.Config
import com.codebrew.clikat.utils.GeneralFunctions
import com.codebrew.clikat.utils.StaticFunction
import com.google.android.material.button.MaterialButton
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

import kotlinx.android.synthetic.main.activity_splash.*
import kotlinx.android.synthetic.main.activity_splash.ivSplashLogo
import kotlinx.android.synthetic.main.activity_splash.rvContainer
import kotlinx.android.synthetic.main.activity_splash.tvComingSoon
import pub.devrel.easypermissions.AppSettingsDialog
import pub.devrel.easypermissions.EasyPermissions
import retrofit2.Retrofit
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException
import javax.inject.Inject


/*
 * Created by Ankit Jindal on 18/4/16.
 */

const val TAG = "DeepLink"

class SplashActivity : BaseActivity<ActivitySplashBinding, SplashViewModel>(), SplashNavigator, EasyPermissions.PermissionCallbacks {


    @Inject
    lateinit var factory: ViewModelProviderFactory
    private var settingLat: SettingModel.DataBean.DefaultAddres? = null

    @Inject
    lateinit var prefHelper: PreferenceHelper

    @Inject
    lateinit var permissionUtil: PermissionFile

    @Inject
    lateinit var retrofit: Retrofit

    @Inject
    lateinit var interceptor: HostSelectionInterceptor

    @Inject
    lateinit var mDialogUtil: DialogsUtil

    @Inject
    lateinit var appUtils: AppUtils

lateinit var imageView: ImageView
    private var dialog: Dialog? = null

    lateinit var vibrator: Vibrator
    private var mSplashViewModel: SplashViewModel? = null

    private lateinit var mGson: Gson
    override fun getBindingVariable(): Int {
        return BR.viewModel
    }

    override fun getLayoutId(): Int {
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        return R.layout.activity_splash
    }

    override fun getViewModel(): SplashViewModel {
        mSplashViewModel = ViewModelProviders.of(this, factory).get(SplashViewModel::class.java)
        return mSplashViewModel as SplashViewModel
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        vibrator  = getSystemService(VIBRATOR_SERVICE) as Vibrator
        CleverTapAPI.createNotificationChannel(getApplicationContext(), "crave_quater", "Crave Quater", "Notification from CleverTap", NotificationManager.IMPORTANCE_MAX, "102", true)

        viewModel.navigator = this
        settingLat = prefHelper.getGsonValue(DataNames.LOc_DATA, SettingModel.DataBean.DefaultAddres::class.java)

        mGson = Gson()
        StaticFunction.setStatusBarColor(this, getColor(R.color.yellow_new))
        window.setNavigationBarColor(getColor(R.color.yellow_new))
        if (intent.getBooleanExtra("isFromRoadMap", false)) {
            rvContainer.visibility = View.GONE
            tvComingSoon.visibility = View.VISIBLE
        } else {
            settingObserver()
            orderObserver()
            userCodeObserver()
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vibrator.vibrate(VibrationEffect.createOneShot(500, VibrationEffect.EFFECT_HEAVY_CLICK));
            } else {
                //deprecated in API 26
                vibrator.vibrate(500);
            }
//            try {
//
//                val video = Uri.parse("android.resource://" + packageName + "/" + R.raw.craveintro)
//                videoViewSplash.setVideoURI(video)
//                videoViewSplash.setOnCompletionListener {  handleDynamicLink() }
//                videoViewSplash.start()
//            } catch (ex: java.lang.Exception) {
//                handleDynamicLink()
//            }
//            imageView=findViewById(R.id.imageLoader)
//            Glide.with(this).asGif().load(R.raw.loading_gif).into(imageView);
            Handler(Looper.getMainLooper()).postDelayed({
//                ivSplashLogo.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.fast_food))
                handleDynamicLink()
            }, 2000)
            printHashKey(this)
        }
    }


    private fun handleDynamicLink() {

        FirebaseDynamicLinks.getInstance()
                .getDynamicLink(intent)
                .addOnSuccessListener(this) { pendingDynamicLinkData ->
                    // Get deep link from result (may be null if no link is found)
                    val deepLink: Uri?
                    if (pendingDynamicLinkData != null) {
                        deepLink = pendingDynamicLinkData.link

                        if (!deepLink?.getQueryParameter("table_id").isNullOrBlank()) {

                            val valuesHolder = GlobalTableDataHolder().apply {
                                isInvitation = "1"
                                isScanned = "0"
                                invitationAccepted = "0"
                                branch_id = deepLink?.getQueryParameter("branch_id")
                                user_id = deepLink?.getQueryParameter("user_id")
                                table_id = deepLink?.getQueryParameter("table_id")
                                capacity = deepLink?.getQueryParameter("capacity")
                                table_number = deepLink?.getQueryParameter("table_number")
                                table_name = deepLink?.getQueryParameter("table_name")
                                user_name = deepLink?.getQueryParameter("user_name")
                                branch_name = deepLink?.getQueryParameter("branch_name")
                                date = deepLink?.getQueryParameter("date")
                                supplier_id = deepLink?.getQueryParameter("supplier_id")
                                booking_id = deepLink?.getQueryParameter("booking_id")
                            }

                            prefHelper.setkeyValue(DataNames.INVITATTON_DATA, Gson().toJson(valuesHolder))
                        }

                        if (!deepLink?.getQueryParameter("uniqueId").isNullOrBlank()) {

                            val uniqueCode = deepLink?.getQueryParameter("uniqueId")

                            if (uniqueCode == prefHelper.getKeyValue(PrefenceConstants.APP_UNIQUE_CODE, PrefenceConstants.TYPE_STRING)) {
                                checkAppDBKey(prefHelper.getKeyValue(
                                        PrefenceConstants.DB_SECRET,
                                        PrefenceConstants.TYPE_STRING)!!.toString())
                                fetchSetting()
                            } else {
                                CommonUtils.setBaseUrl(retrofit = retrofit, baseUrl = BuildConfig.ONBOARD_URL)

                                if (isNetworkConnected) {
                                    viewModel.validateUserCode(deepLink?.getQueryParameter("uniqueId")
                                            ?: "")
                                }
                            }
                        } else {
                            validateSecretCode()
                        }

                    } else {
                        validateSecretCode()
                    }
                }
                .addOnFailureListener(this) { e -> Log.w(TAG, "getDynamicLink:onFailure", e) }
    }

    //parul_0267

    private fun validateSecretCode() {

        val mClientCode = BuildConfig.CLIENT_CODE

        if (prefHelper.getKeyValue(PrefenceConstants.DB_SECRET, PrefenceConstants.TYPE_STRING)?.toString()?.isEmpty() == true
                || mClientCode != prefHelper.getKeyValue(PrefenceConstants.APP_UNIQUE_CODE, PrefenceConstants.TYPE_STRING)?.toString()) {
            CommonUtils.setBaseUrl(retrofit = retrofit, baseUrl = BuildConfig.ONBOARD_URL)
            //      showDialog()
            if (isNetworkConnected) {
                prefHelper.onClear()
                viewModel.validateUserCode(mClientCode)
            }
        } else {
            Config.dbSecret = prefHelper.getKeyValue(PrefenceConstants.DB_SECRET, PrefenceConstants.TYPE_STRING).toString()
//            Config.dbSecret = "4aa6114820b1f242cee23600898183cd"
            checkAppDBKey(prefHelper.getKeyValue(
                    PrefenceConstants.DB_SECRET,
                    PrefenceConstants.TYPE_STRING)!!.toString())
            fetchSetting()
        }
    }


    private fun checkAppDBKey(dBKey: String) {

        if (dBKey.isNotEmpty()) {
            if (interceptor.secret_key == null || interceptor.secret_key
                    !== dBKey) {
                interceptor.secret_key = dBKey
            }
            CommonUtils.setBaseUrl(BuildConfig.BASE_URL, retrofit)
        }
    }


    private fun showDialog() {

        val binding = DataBindingUtil.inflate<DialogEnterCodeBinding>(LayoutInflater.from(this), R.layout.dialog_enter_code, null, false)
        // binding.appData= ColorConfig()
        dialog = mDialogUtil.showDialogFix(this, binding.root)


        val button = dialog?.findViewById<MaterialButton>(R.id.btn_submit)
        val editText = dialog?.findViewById<EditText>(R.id.edit_code)


        button?.setOnClickListener {
            if (editText?.text?.toString()?.isNotEmpty() == true) {
                if (isNetworkConnected) {
                    viewModel.validateUserCode(editText.text.toString())
                }
            } else {
                rvContainer.onSnackbar(getString(R.string.enter_supplier_code))
            }
        }

        dialog?.show()
    }

    private fun fetchSetting() {
        if (isNetworkConnected) {
            viewModel.fetchSetting()
        }
    }


    private fun fetchOrderCount(accessToken: String) {
        if (isNetworkConnected) {
            viewModel.fetchOrderCount(accessToken)
        }
    }


    private fun settingObserver() {
        // Create the observer which updates the UI.
        val catObserver = Observer<SettingModel.DataBean> { resource ->
            checkAppVersion(this, resource)
        }

        // Observe the LiveData, passing in this activity as the LifecycleOwner and the observer.
        viewModel.settingLiveData.observe(this, catObserver)
    }


    private fun orderObserver() {
        // Create the observer which updates the UI.
        val catObserver = Observer<DataCount> { resource ->

            updateOrderCount(resource)
        }

        // Observe the LiveData, passing in this activity as the LifecycleOwner and the observer.
        viewModel.orderCountLiveData.observe(this, catObserver)

    }

    private fun userCodeObserver() {
        // Create the observer which updates the UI.
        val catObserver = Observer<GetDbKeyModel> { resource ->
            when (BuildConfig.CLIENT_CODE) {
                "thelocal_0694" -> {
                    checkAppDBKey("f659625dc5b3c13128df7f4571f11ee4")
                }
                "roadsideassistance_0649" -> {
                    checkAppDBKey("c46dbcdba9a5c89726d8e52b47b01b0ea9d5da22ca8b58d3ee8f1c732b01c279")
                }
                else -> {
                    checkAppDBKey(resource?.data?.data?.get(0)?.cbl_customer_domains?.get(0)?.db_secret_key
                            ?: "")
                }
            }
            dialog?.dismiss()
            fetchSetting()
        }

        // Observe the LiveData, passing in this activity as the LifecycleOwner and the observer.
        viewModel.userCodeLiveData.observe(this, catObserver)

    }

    private fun updateOrderCount(resource: DataCount?) {


        mGson.toJson(resource)?.let { prefHelper.setkeyValue(DataNames.ORDERS_COUNT, it) }

        runNormal11()
    }


    private fun updateSettingData(resource: SettingModel.DataBean?) {

        resource?.key_value?.show_food_groc = "0"
        resource?.key_value?.rest_detail_pagin = "0"
        //resource?.key_value?.enable_rest_pagination_category_wise = "0"
        resource?.key_value?.is_juman_flow_enable = "0"


        if (resource?.default_address?.isNotEmpty() == true) {
            prefHelper.setkeyValue(DataNames.DEFAULT_ADRS, mGson.toJson(resource.default_address.first()))
        }

        prefHelper.setkeyValue(DataNames.SETTING_DATA, mGson.toJson(resource?.key_value))

        when (BuildConfig.CLIENT_CODE) {
            "urbanfix_0328" -> {
                resource?.key_value?.three_decimal = "1"
            }
            "yummy_0122" -> {
                resource?.key_value?.yummyTheme = "1"
            }
            "zipeats", "zipeats_0738", "ziptaste_0005" -> {
                //zipeats
                resource?.key_value?.zipDesc = "1"
                resource?.key_value?.addonHeight = "1"
                resource?.key_value?.header_color = "#ffffff"
                resource?.key_value?.savedCard = "0"
                resource?.key_value?.supplierImage = "1"
                resource?.key_value?.isTawk = "1"
                resource?.key_value?.zipTheme = "1"
                resource?.key_value?.addon_type_quantity = "1"
                resource?.key_value?.isProductDetailCheck = "1"
                resource?.key_value?.show_expected_delivery_between = "1"
            }
            "royobeauty_0254" -> {
                resource?.bookingFlow?.get(0)?.vendor_status = 0
                resource?.bookingFlow?.get(0)?.cart_flow = 1
            }

            "faindy_0224" -> {
                resource?.key_value?.isCallSupplier = "1"
                resource?.key_value?.isCash = "1"
            }
            "homerr_0625" -> {
                resource?.key_value?.isCallSupplier = "1"
            }
            "lastminute_0382" -> {
                resource?.key_value?.show_food_groc = "1"
                //  resource?.key_value?.search_user_locale = "1"
            }
            "lettta_0293" -> {
                resource?.key_value?.search_user_locale = "1"
            }

            "beezi_0750" -> {
                resource?.key_value?.show_ecom_v2_theme = "1"
            }
            "riversidemarket_0415" -> {
                resource?.key_value?.custom_rest_prod = "1"
            }
            "yammfood_0599" -> {
                resource?.key_value?.bypass_otp = "0"
                resource?.key_value?.phone_registration_flag = "0"
            }
            "foundlifestyle_0437" -> {
                resource?.key_value?.is_found = "1"
            }
            "expactor_0485" -> {
                resource?.key_value?.is_expactor = "1"
                resource?.key_value?.show_supplier_detail = "1"
            }
            "alkhaliligroup_0825" -> {
                resource?.key_value?.ecom_theme_dark = "1"
            }
            "craveqatar123_0896" -> {
                resource?.key_value?.enable_google_login = "0"
                resource?.key_value?.enable_facebook_login = "0"
            }
        }

        with(resource?.key_value)
        {
            this?.no_data_image = assignAppImage((this?.no_data_image ?: "").toString())
            this?.pickup_url = assignAppImage((this?.pickup_url ?: "").toString())
            this?.user_location = assignAppImage((this?.user_location ?: "").toString())
            this?.empty_cart = assignAppImage((this?.empty_cart ?: "").toString())
            this?.login_icon_url = assignAppImage((this?.login_icon_url ?: "").toString())
        }

        //           this?.no_data_image=assignAppImage((this?.no_data_image?:"").toString(),R.drawable.img_nothing_found)
        //            this?.pickup_url=assignAppImage((this?.pickup_url?:"").toString(),R.drawable.ic_pickup_banner_1)
        //            this?.user_location=assignAppImage((this?.user_location?:"").toString(),R.drawable.ic_loc)
        //            this?.empty_cart=assignAppImage((this?.empty_cart?:"").toString(),R.drawable.ic_cart_grey)
        //            this?.login_icon_url=assignAppImage((this?.login_icon_url?:"").toString(),R.drawable.bg_foodserv)


        AppConstants.APP_SUB_TYPE = resource?.screenFlow?.first()?.app_type ?: 0
        AppConstants.APP_SAVED_SUB_TYPE = resource?.screenFlow?.first()?.app_type ?: 0

        resource?.dialog_token?.let { prefHelper.setkeyValue(DataNames.DIALOG_TOKEN, it) }


        resource?.screenFlow?.get(0)?.app_type?.let { prefHelper.setkeyValue(DataNames.APP_TYPE, it) }


        if (resource?.bookingFlow?.isNotEmpty() == true) {
            prefHelper.setkeyValue(DataNames.BOOKING_FLOW, mGson.toJson(resource.bookingFlow?.get(0)))
        }

        viewModel.userCodeLiveData.value?.data?.settingsData?.let {
            resource?.key_value?.from_email = it.from_email
        }

        if (resource?.key_value?.dynamic_order_type_client_wise == "1")
            resource.key_value.is_table_booking = resource.key_value.dynamic_order_type_client_wise_dinein

        prefHelper.setkeyValue(DataNames.SETTING_DATA, mGson.toJson(resource?.key_value))

        if (resource?.adminDetails?.isNotEmpty() == true) {
            prefHelper.setkeyValue(PrefenceConstants.ADMIN_DETAILS, mGson.toJson(resource.adminDetails.firstOrNull()))
        }

        if (!resource?.key_value?.home_screen_sections.isNullOrEmpty()) {
            val data = mGson.fromJson<ArrayList<SettingModel.DataBean.DynamicScreenSections>>(resource?.key_value?.home_screen_sections.toString(),
                    object : TypeToken<List<SettingModel.DataBean.DynamicScreenSections?>?>() {}.type)

            val arrayList = ArrayList<SettingModel.DataBean.DynamicScreenSections>()
            arrayList.addAll(data?.filter { it.is_active == 1 } ?: arrayListOf())

            val homeData = SettingModel.DataBean.DynamicSectionsData()
            homeData.list = arrayList
            prefHelper.setkeyValue(DataNames.HOME_DYNAMIC_SECTIONS, mGson.toJson(homeData))
        }

        if (!resource?.key_value?.order_type_sections.isNullOrEmpty()) {
            val data = mGson.fromJson<ArrayList<SettingModel.DataBean.DynamicScreenSections>>(resource?.key_value?.order_type_sections.toString(),
                    object : TypeToken<List<SettingModel.DataBean.DynamicScreenSections?>?>() {}.type)

            val arrayList = ArrayList<SettingModel.DataBean.DynamicScreenSections>()
            arrayList.addAll(data)

            val orderSectionData = SettingModel.DataBean.DynamicSectionsData()
            orderSectionData.list = arrayList
            prefHelper.setkeyValue(DataNames.ORDER_TYPE_DYNAMIC_SECTIONS, mGson.toJson(orderSectionData))
        }

        if (resource?.key_value?.terminology?.isNotEmpty() == true) {
            prefHelper.addGsonValue(PrefenceConstants.APP_TERMINOLOGY, resource.key_value.terminology
                    ?: "")
        }

        prefHelper.setkeyValue(PrefenceConstants.GENRIC_SUPPLIERID, resource?.supplier_id ?: 0)

        if (resource?.termsAndConditions?.isNotEmpty() == true) {
            prefHelper.addGsonValue(PrefenceConstants.TERMS_CONDITION, Gson().toJson(resource.termsAndConditions?.get(0)))
        }


        prefHelper.setkeyValue(PrefenceConstants.GENERIC_SPL_BRANCHID, resource?.supplier_branch_id
                ?: 0)

        if (resource?.screenFlow?.isNotEmpty() == true) {

            if (resource.screenFlow?.get(0)?.is_single_vendor == VendorAppType.Single.appType) {
                val mResturantDetail = LocationUser(resource.latitude.toString(), resource.longitude.toString(),
                        "${resource.supplierName ?: ""} , ${
                            getaddress(this, resource.latitude
                                    ?: 0.0, resource.longitude ?: 0.0)
                        }")
                prefHelper.addGsonValue(PrefenceConstants.RESTAURANT_INF, Gson().toJson(mResturantDetail))
            }

            prefHelper.setkeyValue(DataNames.SCREEN_FLOW, mGson.toJson(resource.screenFlow?.get(0)))
            prefHelper.setkeyValue(PrefenceConstants.SELF_PICKUP, resource.self_pickup ?: "")
        }

        if (prefHelper.getCurrentUserLoggedIn()) {
            fetchOrderCount(prefHelper.getKeyValue(PrefenceConstants.ACCESS_TOKEN, PrefenceConstants.TYPE_STRING).toString())
        } else {
            runNormal11()
        }
    }

    private fun assignAppImage(image: String): String {
        if (image.isBlank() || image == "[object Object]") return ""

        val imageModel: SettingModel.DataBean.AppIcon = mGson.fromJson(image, SettingModel.DataBean.AppIcon::class.java)
        return if (imageModel.app?.isNotEmpty() == true) imageModel.app else ""
    }


    private fun selectLanguage(language: String) {

        val lang = if (language.isEmpty()) {
            "en"
        } else {
            language
        }

        AppGlobal.localeManager?.setNewLocale(this, lang)



        when (language) {
            "arabic", "ar" -> {
                GeneralFunctions.force_layout_to_RTL(this)
            }
            "spanish", "español", "es" -> {
                GeneralFunctions.force_layout_to_LTR(this)
            }
            else -> {
                GeneralFunctions.force_layout_to_LTR(this)
            }
        }
    }

    private fun runNormal11() {
        //selectLanguageId()
        if (prefHelper.getKeyValue(DataNames.SELECTED_LANGUAGE, PrefenceConstants.TYPE_STRING).toString().isEmpty() && viewModel.settingLiveData.value?.key_value?.default_language == "1") {
            selectLanguage(viewModel.settingLiveData.value?.key_value?.secondary_language ?: "")
        } else {
            selectLanguage(prefHelper.getKeyValue(DataNames.SELECTED_LANGUAGE, PrefenceConstants.TYPE_STRING) as String)
        }

        appUtils.getCurrencySymbol()

        val isGuest = viewModel.settingLiveData.value?.key_value?.login_template.isNullOrEmpty() || viewModel.settingLiveData.value?.key_value?.login_template == "0"

        if (prefHelper.getKeyValue(DataNames.FIRST_TIME, PrefenceConstants.TYPE_BOOLEAN) as Boolean) {
            //after instruction screen
            //launchActivity<OrderDetailNew>()
            checkLocationAndHomeFlow(isGuest)
        } else {
            launchActivity<InstructionActivity>()
        }
        finish()
    }

    private fun checkLocationAndHomeFlow(isGuest: Boolean) {
        if (!permissionUtil.hasLocation(this)) {
            launchActivity<LocationActivity>()
        } else {
            if (isGuest || prefHelper.getCurrentUserLoggedIn()) {
                appUtils.checkHomeActivity(this, intent.extras ?: Bundle.EMPTY)
            } else {
                appUtils.checkLoginFlow(this@SplashActivity, -1)
            }
        }
    }

    fun printHashKey(pContext: Context) {
        try {
            val info: PackageInfo = pContext.packageManager.getPackageInfo(pContext.packageName, PackageManager.GET_SIGNATURES)
            for (signature in info.signatures) {
                val md = MessageDigest.getInstance("SHA")
                md.update(signature.toByteArray())
                val hashKey = String(Base64.encode(md.digest(), 0))
                Log.i(TAG, "printHashKey() Hash Key: $hashKey")
            }
        } catch (e: NoSuchAlgorithmException) {
            Log.e(TAG, "printHashKey()", e)
        } catch (e: Exception) {
            Log.e(TAG, "printHashKey()", e)
        }
    }


    override fun onErrorOccur(message: String) {

        rvContainer.onSnackbar(message)
    }

    override fun onSessionExpire() {
        openActivityOnTokenExpire()
    }

    override fun onRequestPermissionsResult(
            requestCode: Int,
            permissions: Array<String>,
            grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this)
    }

    override fun onPermissionsDenied(requestCode: Int, perms: MutableList<String>) {
        if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
            AppSettingsDialog.Builder(this).build().show()
        }
    }

    override fun onPermissionsGranted(requestCode: Int, perms: MutableList<String>) {
        if (requestCode == AppConstants.REQUEST_CODE_LOCATION) {
            if (isNetworkConnected) {
                handleDynamicLink()
            }
        }
    }

    private fun checkAppVersion(context: Activity?, resource: SettingModel.DataBean) {
        try {

            if (!resource.user_app_version.isNullOrEmpty() && resource.user_app_version?.firstOrNull()?.version_android ?: "1.0" > BuildConfig.VERSION_NAME) {
//                when (resource.user_app_version?.firstOrNull()?.is_update_android) {
//                    AppVersionUpdate.ForceUpdate.update ->
                showUpdateAppPopup(false, resource)
//                    AppVersionUpdate.NormalUpdate.update ->
//                showUpdateAppPopup(true, resource)
//                    else -> updateSettingData(resource)
//                }
            } else
                updateSettingData(resource)


        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
        }
    }

    private fun showUpdateAppPopup(showCancel: Boolean, resource: SettingModel.DataBean) {
        val ad = AlertDialog.Builder(this)
        ad.setTitle(getString(R.string.app_update_available))
        ad.setMessage(getString(R.string.app_update_available_message))
        ad.setCancelable(false)
        ad.setPositiveButton(getString(R.string.update)) { _, _ ->
            StaticFunction.redirectToPlayStore(this@SplashActivity)
        }
        if (showCancel)
            ad.setNegativeButton(getString(R.string.cancel)) { dialog, _ ->
                dialog.cancel()
                updateSettingData(resource)
            }
        ad.show()

    }


}