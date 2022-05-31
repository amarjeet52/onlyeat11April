package com.codebrew.clikat.module.bottom_navigation

import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.content.res.Configuration
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.view.Window
import android.widget.TextView
import androidx.annotation.NonNull
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.codebrew.clikat.BR
import com.codebrew.clikat.BuildConfig
import com.codebrew.clikat.R
import com.codebrew.clikat.app_utils.*
import com.codebrew.clikat.app_utils.extension.launchActivity
import com.codebrew.clikat.app_utils.extension.onSnackbar
import com.codebrew.clikat.base.BaseActivity
import com.codebrew.clikat.base.BaseInterface
import com.codebrew.clikat.data.AppDataType
import com.codebrew.clikat.data.constants.AppConstants
import com.codebrew.clikat.data.constants.NetworkConstants
import com.codebrew.clikat.data.constants.PrefenceConstants
import com.codebrew.clikat.data.model.api.AddressBean
import com.codebrew.clikat.data.model.api.SuccessModel
import com.codebrew.clikat.data.model.api.orderDetail.Agent
import com.codebrew.clikat.data.model.api.orderDetail.OrderHistory
import com.codebrew.clikat.data.model.others.CommonEvent
import com.codebrew.clikat.data.model.others.EditProductsItem
import com.codebrew.clikat.data.model.others.GlobalTableDataHolder
import com.codebrew.clikat.data.model.others.PaymentEvent
import com.codebrew.clikat.data.preferences.PreferenceHelper
import com.codebrew.clikat.databinding.ActivityMainScreenBinding
import com.codebrew.clikat.di.ViewModelProviderFactory
import com.codebrew.clikat.dialog_flow.DialogChat
import com.codebrew.clikat.modal.other.SettingModel
import com.codebrew.clikat.module.cart.RAZOR_REQUEST
import com.codebrew.clikat.module.location.LocationActivity
import com.codebrew.clikat.module.main_screen.OnOrderEdited
import com.codebrew.clikat.module.order_detail.OrderDetailActivity
import com.codebrew.clikat.module.product.product_listing.DialogListener
import com.codebrew.clikat.module.tables.scanner.ScannerActivity
import com.codebrew.clikat.module.user_tracking.UserTracking
import com.codebrew.clikat.preferences.DataNames
import com.codebrew.clikat.user_chat.UserChatActivity
import com.codebrew.clikat.utils.GeneralFunctions
import com.codebrew.clikat.utils.StaticFunction
import com.codebrew.clikat.utils.StaticFunction.setStatusBarColor
import com.codebrew.clikat.utils.configurations.Configurations
import com.facebook.AccessToken
import com.facebook.login.LoginManager
import com.google.android.material.button.MaterialButton
import com.google.android.material.navigation.NavigationView
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
//import com.razorpay.PaymentData
//import com.razorpay.PaymentResultWithDataListener
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.HasAndroidInjector
import io.socket.emitter.Emitter
import kotlinx.android.synthetic.main.activity_main_screen.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import org.json.JSONObject
import pub.devrel.easypermissions.EasyPermissions
import timber.log.Timber
import zendesk.core.AnonymousIdentity
import zendesk.core.Identity
import zendesk.core.Zendesk
import zendesk.support.Support
import zendesk.support.guide.HelpCenterActivity
import java.lang.reflect.Type
import java.util.concurrent.Executors
import javax.inject.Inject


class MainScreenActivity : BaseActivity<ActivityMainScreenBinding, MainScreenViewModel>(), BaseInterface,
        HasAndroidInjector, OnOrderEdited,
        EasyPermissions.PermissionCallbacks, OnNavigationMenuClicked, NavigationView.OnNavigationItemSelectedListener, DialogListener {


    @Inject
    lateinit var factory: ViewModelProviderFactory

    @Inject
    lateinit var prefHelper: PreferenceHelper

    @Inject
    lateinit var androidInjector: DispatchingAndroidInjector<Any>

    private var mHomeViewModel: MainScreenViewModel? = null

    private var screenFlowBean: SettingModel.DataBean.ScreenFlowBean? = null

    var settingBean: SettingModel.DataBean.SettingData? = null

    var mScannerType = 0

    var isZendeskEnable = false

    @Inject
    lateinit var dialogsUtil: DialogsUtil

    @Inject
    lateinit var permissionFile: PermissionFile

    @Inject
    lateinit var appUtil: AppUtils

    @Inject
    lateinit var googleHelper: GoogleLoginHelper

    private var isFirst = true

    override fun getBindingVariable(): Int {
        return BR.viewModel
    }

    override fun getLayoutId(): Int {
        return R.layout.activity_main_screen
    }

    override fun getViewModel(): MainScreenViewModel {
        mHomeViewModel = ViewModelProviders.of(this, factory).get(MainScreenViewModel::class.java)
        return mHomeViewModel as MainScreenViewModel
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onMessageEvent(event: CommonEvent?) {
        if (event?.type == "login") {
            handleDgChatVisbility()
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        EventBus.getDefault().register(this)

        settingBean = prefHelper.getGsonValue(DataNames.SETTING_DATA, SettingModel.DataBean.SettingData::class.java)
        screenFlowBean = prefHelper.getGsonValue(DataNames.SCREEN_FLOW, SettingModel.DataBean.ScreenFlowBean::class.java)
        viewDataBinding?.appType = screenFlowBean?.app_type
        viewDataBinding?.color = Configurations.colors

        val statusColor = Color.parseColor(Configurations.colors.appBackground)
        setStatusBarColor(this, getColor(R.color.dark_blue))

        val navController = findNavController(R.id.nav_host_container)

        if (prefHelper.getCurrentUserLoggedIn()) {
            mHomeViewModel?.connectSocket(socketListener)
        }
        if (settingBean?.is_juman_flow_enable == "1") {
            nvView?.visibility = View.VISIBLE
            nav_view?.visibility = View.GONE
            nvView?.setNavigationItemSelectedListener(this)
            startDestination(null, R.id.jumaHomeFragment)
        } else if (intent?.hasExtra("isEditOrder") == true) {
            drawerLayout?.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)
            nav_view?.visibility = View.GONE
            fbChat?.visibility = View.GONE
            if (screenFlowBean?.app_type == AppDataType.Food.type) {
                val bundle = bundleOf(
                        "orderItem" to intent?.getParcelableExtra("orderItem"),
                        "selectedList" to intent?.getParcelableArrayListExtra<EditProductsItem>("selectedList")
                )
                // navController.navigate(R.id.restaurantDetailFragNew, bundle)
                startDestination(bundle, R.id.restaurantDetailFragNew)
            } else if (screenFlowBean?.app_type == AppDataType.HomeServ.type) {
                val bundle = bundleOf(
                        "orderItem" to intent?.getParcelableExtra("orderItem"),
                        "geofenceData" to intent?.getParcelableExtra("geofenceData"),
                        "isEditOrder" to true
                )
                startDestination(bundle, R.id.productTabListing)
            }
        } else {
            nvView?.visibility = View.GONE
            drawerLayout?.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)
            handleNotification(intent.extras)

            initZenDesk()

            when {
                screenFlowBean?.app_type ?: 0 > AppDataType.Custom.type -> {
                    nav_view.inflateMenu(R.menu.bottom_custom_menu)
                }
                else -> {
                    when (screenFlowBean?.app_type) {
                        AppDataType.CarRental.type ->
                        {

                            nav_view.inflateMenu(R.menu.bottom_rental_menu)

                        }
                        AppDataType.Ecom.type -> {

                            nav_view.inflateMenu(R.menu.bottom_ecom_menu)

                        }
                        else -> {
                            if (settingBean?.is_social_ecommerce == "1") {
                                nav_view.inflateMenu(R.menu.bottom_social_menu)
                            } else {
                                nav_view.inflateMenu(R.menu.bottom_nav_menu)
                            }
                        }
                    }
                }
            }
            nav_view.setupWithNavController(navController)

            if (nav_view.menu.size() > 4) {
                if (screenFlowBean?.app_type == AppDataType.Ecom.type) {
                    nav_view.menu.getItem(1).title = appUtil.loadAppConfig(0).strings?.categories
                }
                nav_view.menu.getItem(3).title = appUtil.loadAppConfig(0).strings?.orders
                nav_view.menu.getItem(4).title = appUtil.loadAppConfig(0).strings?.otherTab
                nav_view.menu.getItem(4).icon = if (BuildConfig.CLIENT_CODE == "foodydoo_0590" || BuildConfig.CLIENT_CODE == "readychef_0309" || settingBean?.is_pickup_primary == "1")
                    ContextCompat.getDrawable(this, R.drawable.navigation_profile_selector) else ContextCompat.getDrawable(this, R.drawable.navigation_other_selector)
            } else {
                nav_view.menu.getItem(1).title = appUtil.loadAppConfig(0).strings?.orders
                nav_view.menu.getItem(2).title = getString(R.string.cart)
                nav_view.menu.getItem(3).title = if (settingBean?.is_carveQatar_home_theme == "1") getString(R.string.others) else appUtil.loadAppConfig(0).strings?.otherTab
                nav_view.menu.getItem(3).icon = if (BuildConfig.CLIENT_CODE == "foodydoo_0590" || BuildConfig.CLIENT_CODE == "readychef_0309" || settingBean?.is_pickup_primary == "1")
                    ContextCompat.getDrawable(this, R.drawable.navigation_profile_selector) else ContextCompat.getDrawable(this, R.drawable.navigation_other_selector)
            }

        }

        // Whenever the selected controller changes, setup the action bar.


        if (settingBean?.show_ecom_v2_theme == "1" || settingBean?.is_skip_theme == "1" || settingBean?.is_juman_flow_enable == "1") {
            nav_view.visibility = View.GONE
        } else {
            navController.addOnDestinationChangedListener { _, destination, _ ->

                when (destination.label) {
                    "fragment_ecommerce" -> nav_view.visibility = View.VISIBLE
                    "fragment_home_menu_item" -> nav_view.visibility = View.GONE
                    "fragment_all_category" -> nav_view.visibility = View.VISIBLE
                    "fragment_resturant_home" -> {
                        //   AppConstants.DELIVERY_OPTIONS = DeliveryType.DeliveryOrder.type
                        if (prefHelper.getKeyValue(AppConstants.ROAD_MAP_TYPE, PrefenceConstants.TYPE_STRING) == AppConstants.ROAD_MAP_TYPE_SIGNATURE)
                            nav_view.visibility = View.GONE
                        else
                            nav_view.visibility = View.VISIBLE
                    }
                    "fragment_posts_home" -> nav_view.visibility = View.VISIBLE
                    "fragment_cart" -> nav_view.visibility = View.VISIBLE
                    "fragment_base_order" -> nav_view.visibility = View.VISIBLE
                    "fragment_more" -> nav_view.visibility = View.VISIBLE
                    "fragment_home_rental" -> nav_view.visibility = View.VISIBLE
                    "CustomHomeFrag" -> nav_view.visibility = View.VISIBLE
                    "ClikatHomeFragment" -> nav_view.visibility = View.VISIBLE
                    "homeFragment" -> nav_view.visibility = View.VISIBLE
                    "fragment_main" -> nav_view.visibility = View.VISIBLE
                    "frgment_crave_mania_new"->nav_view.visibility = View.VISIBLE
                    else -> {
                        nav_view.visibility = View.GONE
                    }

                }


                if (destination.label == "fragment_main" || destination.label == "fragment_resturant_home") {
                    if (settingBean?.is_table_booking == "1" && BuildConfig.CLIENT_CODE != "rushdelivery_0598") {
                        dfScanner?.visibility = View.VISIBLE
                    }

                    if (isZendeskEnable) {
                        fbChat?.visibility = View.VISIBLE
                    }

                } else {
                    dfScanner?.visibility = View.GONE
                    fbChat?.visibility = View.GONE
                }

                if (settingBean?.is_carveQatar_home_theme == "1")
                    dfScanner?.visibility = View.GONE
            }
        }

        handleDgChatVisbility()

        fbChat.setOnClickListener {

            HelpCenterActivity.builder()
                    .show(this)
        }

        dfChat.setOnClickListener {
            launchActivity<DialogChat>(AppConstants.REQUEST_DIALOG_CART)
        }

        dfScanner?.setOnClickListener {

            mScannerType = 1

            if (permissionFile.hasCameraPermissions(this)) {
                launchActivity<ScannerActivity>(AppConstants.REQUEST_CODE_SCANNER)
            } else {
                permissionFile.cameraAndGalleryActivity(this)
            }
        }

        prefHelper.getGsonValue(PrefenceConstants.ADRS_DATA, AddressBean::class.java)?.let {
            appUtil.setUserLocale(it)
        }

        val invitation = prefHelper.getCurrentTableData()
        if (invitation != null && prefHelper.getCurrentUserLoggedIn()) {
            if (invitation.invitationAccepted == "0" && invitation.isInvitation == "1") {
                showAcceptInvitationDialog(invitation)
            }
        }

        viewModel.invitationListener.observe(this, Observer {
            invitation?.invitationAccepted = "1"
            prefHelper.setkeyValue(DataNames.INVITATTON_DATA, Gson().toJson(invitation))
            findNavController(R.id.nav_host_container).navigate(R.id.bookedTableListingFragment, null)
        })
    }


    private fun showAcceptInvitationDialog(invitation: GlobalTableDataHolder) {
        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.layout_invitation_dialog)
        dialog.findViewById<TextView>(R.id.tvBranchName).text = getString(R.string.branch_name, invitation.branch_name)
        if ((invitation.table_name == null || invitation.table_name == "null") &&
                (invitation.table_number == null || invitation.table_number == "null") &&
                (invitation.capacity == null || invitation.capacity == "null")) {
            dialog.findViewById<TextView>(R.id.tvTableName).visibility = View.GONE
            dialog.findViewById<TextView>(R.id.tvTableNumber).visibility = View.GONE
            dialog.findViewById<TextView>(R.id.tvSeatingCapacity).visibility = View.GONE
        } else {
            dialog.findViewById<TextView>(R.id.tvTableName).text = getString(R.string.table_name, invitation.table_name)
            dialog.findViewById<TextView>(R.id.tvTableNumber).text = getString(R.string.table_number, invitation.table_number)
            dialog.findViewById<TextView>(R.id.tvSeatingCapacity).text = getString(R.string.seating_capacity, invitation.capacity)
        }

        dialog.findViewById<TextView>(R.id.tvUserName).text = getString(R.string.table_user, invitation.user_name)
        dialog.findViewById<TextView>(R.id.tvBookingDate).text = getString(R.string.table_date,
                invitation.date?.let {
                    appUtil.convertDateOneToAnother(it,
                            "yyyy-MM-dd'T'HH:mm:ss", "EEE, dd MMMM hh:mm aaa")
                })

        dialog.findViewById<MaterialButton>(R.id.btnReject).setOnClickListener {
            prefHelper.removeValue(DataNames.INVITATTON_DATA)
            dialog.dismiss()
        }
        dialog.findViewById<MaterialButton>(R.id.btnAccept).setOnClickListener {
            viewModel.acceptInvitation(invitation.booking_id.toString(),
                    prefHelper.getKeyValue(PrefenceConstants.USER_ID, PrefenceConstants.TYPE_STRING) as String)
            dialog.dismiss()
        }

        dialog.show()
    }

    override fun onBackPressed() {
        super.onBackPressed()
    }
    private fun handleNotification(bundle: Bundle?) {

        if (bundle == null && (bundle?.containsKey("type") == false || bundle?.containsKey("orderId") == false)) return

        if (bundle?.getString("orderId").isNullOrEmpty() && bundle?.getString("type") != "chat") return

        with(bundle)
        {
            if (bundle?.getString("type") == "chat") {
                val agentData = Agent(
                        name = this?.getString("sender_name") ?: "0",
                        image = this?.getString("sender_image"),
                        agent_created_id = this?.getString("agent_created_id")
                )
                val notificationItent = Intent(this@MainScreenActivity, UserChatActivity::class.java)
                notificationItent.putExtra("userData", agentData)
                notificationItent.putExtra("orderId", this?.getString("order_id") ?: "0")
                notificationItent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP)
                startActivity(notificationItent)
            } else {
                val orderId = this?.getString("orderId")
                val notificationIntent = Intent(this@MainScreenActivity, UserTracking::class.java)
                val arrayList = java.util.ArrayList<Int>()
                arrayList.add(orderId?.toInt() ?: 0)
                notificationIntent.putIntegerArrayListExtra("orderId", arrayList)
                startActivity(notificationIntent)
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()

        EventBus.getDefault().unregister(this)
    }

    private fun handleDgChatVisbility() {
        prefHelper.getKeyValue(DataNames.FEATURE_DATA, PrefenceConstants.TYPE_STRING).toString().let {
            val listType: Type = object : TypeToken<ArrayList<SettingModel.DataBean.FeatureData?>?>() {}.type
            val featureList: ArrayList<SettingModel.DataBean.FeatureData> = Gson().fromJson(it, listType)

            if (featureList.any { it.name == "Dialogflow" }) {
                val key_value_front = featureList.filter { it.name == "Dialogflow" }.firstOrNull()?.key_value_front

                if (key_value_front?.any { it?.value?.isNotEmpty() == true } == true && prefHelper.getCurrentUserLoggedIn()) {
                    dfChat.visibility = View.VISIBLE
                } else {
                    dfChat.visibility = View.GONE
                }
            }
        }
    }

    private fun startDestination(bundle: Bundle?, startDestination: Int) {
        val navHostFragment = (supportFragmentManager.findFragmentById(R.id.nav_host_container) as NavHostFragment)
        val inflater = navHostFragment.navController.navInflater
        val navGraph = inflater.inflate(R.navigation.home)
        navGraph.startDestination = startDestination
        navHostFragment.navController.setGraph(navGraph, bundle)
    }

    private fun initZenDesk() {

        prefHelper.getKeyValue(DataNames.FEATURE_DATA, PrefenceConstants.TYPE_STRING).toString().let {
            val listType: Type = object : TypeToken<ArrayList<SettingModel.DataBean.FeatureData?>?>() {}.type
            val featureList: ArrayList<SettingModel.DataBean.FeatureData> = Gson().fromJson(it, listType)

            if (featureList.any { it.name == "Zendesk" }) {
                val key_value_front = featureList.filter { it.name == "Zendesk" }.firstOrNull()?.key_value_front

                if (key_value_front?.any { it?.value?.isNotEmpty() == true } == true) {

                    isZendeskEnable = true

                    if (BuildConfig.CLIENT_CODE == "healthyboost_0559") {
                        fbChat.setImageResource(R.drawable.ic_support)
                    } else {
                        fbChat.setImageResource(R.drawable.ic_chat_updated)
                    }
                } else {
                    return@let
                }

                Zendesk.INSTANCE.init(this, key_value_front.find { it?.key == "zendeskUrl" }?.value
                        ?: "",
                        key_value_front.find { it?.key == "appId" }?.value ?: "",
                        key_value_front.find { it?.key == "clientId" }?.value ?: "")
                val identity: Identity = AnonymousIdentity()
                Zendesk.INSTANCE.setIdentity(identity)
                Support.INSTANCE.init(Zendesk.INSTANCE)
            }
        }


    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == AppConstants.REQUEST_DIALOG_CART && resultCode == Activity.RESULT_OK) {
            findNavController(R.id.nav_host_container).navigate(R.id.cart, data?.extras
                    ?: Bundle.EMPTY)
        } else if (requestCode == AppConstants.REQUEST_CODE_SCANNER && resultCode == Activity.RESULT_OK) {
            val responseHolder = try {
                convertToMap(data?.getStringExtra("rawResult").toString().replace("{", "").replace("}", ""))
            } catch (e: Exception) {
                viewDataBinding?.root?.onSnackbar(getString(R.string.invalid_qr_message))
                return
            }

            val dataHolder = GlobalTableDataHolder().apply {
                isInvitation = "0"
                isScanned = "1"
                supplier_id = responseHolder["s_id"].toString().trim()
                branch_id = responseHolder["b_id"].toString().trim()
                table_id = responseHolder["table_id"].toString().trim()
                table_name = responseHolder["table_name"].toString().trim()
                capacity = responseHolder["capacity"].toString().trim()
                table_number = responseHolder["table_number"].toString().trim()
            }
            prefHelper.setkeyValue(DataNames.INVITATTON_DATA, Gson().toJson(dataHolder))
            val bundle = Bundle()
            bundle.putString("deliveryType", "dineIn")

            if (settingBean?.app_selected_template != null && settingBean?.app_selected_template == "1") {
                findNavController(R.id.nav_host_container).navigate(R.id.restaurantDetailFragNew, bundle)
            } else {
                findNavController(R.id.nav_host_container).navigate(R.id.restaurantDetailFrag, bundle)
            }
        } else {
            val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_container)
            navHostFragment?.childFragmentManager?.fragments?.get(0)?.onActivityResult(requestCode, resultCode, data)
        }
    }

    override fun androidInjector(): AndroidInjector<Any> {
        return androidInjector
    }


    override fun onErrorOccur(message: String) {

    }

    override fun onSessionExpire() {
        openActivityOnTokenExpire()
    }

/*    override fun onPaymentError(errorCode: Int, errorMsg: String?, paymentDta: PaymentData) {
        EventBus.getDefault().post(PaymentEvent(errorMsg ?: "", errorCode, paymentDta.paymentId
                ?: ""))
    }

    override fun onPaymentSuccess(successMsg: String?, paymentDta: PaymentData) {
        EventBus.getDefault().post(PaymentEvent(successMsg
                ?: "", RAZOR_REQUEST, paymentDta.paymentId ?: ""))
    }*/

    override fun onRestDetailEdited(orderItem: OrderHistory?, selectedList: ArrayList<EditProductsItem>?) {
        val intent = Intent().putParcelableArrayListExtra("selectedList", selectedList).putExtra("orderItem", orderItem)
        setResult(Activity.RESULT_OK, intent)
        finish()
    }

    private val socketListener = Emitter.Listener { args ->
        if (args.isNotEmpty() && args[0] is JSONObject) {
            val response = Gson().fromJson<SuccessModel>(
                    args[0].toString(),
                    object : TypeToken<SuccessModel>() {}.type
            )

            if (response?.success == NetworkConstants.AUTHFAILED) {
                prefHelper.logout()
                onSessionExpire()
            } else {
                Timber.e(response.message)
            }
        }
    }

    override fun onPermissionsDenied(requestCode: Int, perms: MutableList<String>) {

    }

    override fun onPermissionsGranted(requestCode: Int, perms: MutableList<String>) {
        if (requestCode == AppConstants.CameraGalleryPicker && mScannerType == 1) {
            launchActivity<ScannerActivity>(AppConstants.REQUEST_CODE_SCANNER)
        }
    }

    private fun convertToMap(s: String?): HashMap<String, String> {
        val x = s?.split(",")?.associate {
            val (left, right) = it.split(":")
            left.trim() to right.trim()
        }
        return x as HashMap<String, String>
    }

    override fun onNavigationMenuChanged() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            nvView?.menu?.findItem(R.id.nav_logout)?.isVisible = prefHelper.getCurrentUserLoggedIn()
            drawerLayout.openDrawer(GravityCompat.START)
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.nav_home -> {

                startDestination(null, R.id.jumaHomeFragment)


            }
            R.id.nav_categories -> startDestination(null, R.id.categories)
            R.id.nav_cart -> startDestination(null, R.id.cart)
            R.id.nav_orders -> startDestination(null, R.id.orders)
            R.id.nav_profile -> startDestination(null, R.id.other)
            R.id.nav_share_app -> {
                val shareMsg = if (settingBean?.app_sharing_message?.isEmpty() == false) {
                    "${settingBean?.app_sharing_message} ${getString(R.string.app_link_share, getString(R.string.share_body, BuildConfig.APPLICATION_ID))}"
                } else {
                    getString(R.string.share_body, BuildConfig.APPLICATION_ID)
                }

                GeneralFunctions.shareApp(this, shareMsg)
            }
            R.id.nav_terms_condition -> {
                val bundle = bundleOf("terms" to 0)
                startDestination(bundle, R.id.webViewFragment)
            }
            R.id.nav_about_us -> {
                val termsCondition = prefHelper.getGsonValue(PrefenceConstants.TERMS_CONDITION, SettingModel.DataBean.TermCondition::class.java)
                if (termsCondition?.privacyPolicy != 0) {
                    val bundle = bundleOf("terms" to 1)
                    startDestination(bundle, R.id.webViewFragment)
                }
            }
            R.id.nav_privacy_policy -> {
                val bundle = bundleOf("terms" to 3)
                startDestination(bundle, R.id.webViewFragment)
            }
            R.id.nav_logout -> {
                if (prefHelper.getCurrentUserLoggedIn()) {
                    dialogsUtil.openAlertDialog(this, getString(R.string.log_out_msg), getString(R.string.ok), getString(R.string.cancel), this)
                }
            }
        }
        drawerLayout?.closeDrawer(GravityCompat.START)
        return true
    }

    override fun onSucessListner() {
        prefHelper.logout()

        if (AccessToken.getCurrentAccessToken() != null) {
            LoginManager.getInstance().logOut()
        }
        googleHelper.logoutGoogle {
            SocketManager.destroy()
            StaticFunction.clearCart(this)
            prefHelper.removeValue(DataNames.DISCOUNT_AMOUNT)
            StaticFunction.clearCart(this)
            launchActivity<LocationActivity>()
            finishAffinity()
        }
    }

    override fun onErrorListener() {

    }


}
