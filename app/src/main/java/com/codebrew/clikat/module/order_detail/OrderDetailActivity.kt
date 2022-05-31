package com.codebrew.clikat.module.order_detail

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.content.ActivityNotFoundException
import android.content.Intent
import android.content.IntentSender
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.PorterDuff
import android.location.Location
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.viewpager2.widget.ViewPager2
import com.codebrew.clikat.BR
import com.codebrew.clikat.BuildConfig
import com.codebrew.clikat.R
import com.codebrew.clikat.app_utils.*
import com.codebrew.clikat.app_utils.extension.launchActivity
import com.codebrew.clikat.app_utils.extension.onSnackbar
import com.codebrew.clikat.app_utils.extension.setColorScale
import com.codebrew.clikat.app_utils.extension.setGreyScale
import com.codebrew.clikat.base.BaseActivity
import com.codebrew.clikat.data.*
import com.codebrew.clikat.data.constants.AppConstants
import com.codebrew.clikat.data.constants.PrefenceConstants
import com.codebrew.clikat.data.model.api.*
import com.codebrew.clikat.data.model.api.Currency
import com.codebrew.clikat.data.model.api.orderDetail.Data
import com.codebrew.clikat.data.model.api.orderDetail.OrderHistory
import com.codebrew.clikat.data.model.others.*
import com.codebrew.clikat.data.preferences.PreferenceHelper
import com.codebrew.clikat.databinding.ActivityOrderDetailsBinding
import com.codebrew.clikat.di.ViewModelProviderFactory
import com.codebrew.clikat.modal.*
import com.codebrew.clikat.modal.other.AddtoCartModel
import com.codebrew.clikat.modal.other.ProductDataBean
import com.codebrew.clikat.modal.other.RatingBean
import com.codebrew.clikat.modal.other.SettingModel
import com.codebrew.clikat.modal.other.SettingModel.DataBean.BookingFlowBean
import com.codebrew.clikat.modal.other.SettingModel.DataBean.ScreenFlowBean
import com.codebrew.clikat.module.bottom_navigation.MainScreenActivity
import com.codebrew.clikat.module.cart.MY_FATOORAH_PAYMENT_REQUEST
import com.codebrew.clikat.module.cart.SADDED_PAYMENT_REQUEST
import com.codebrew.clikat.module.help.HelpActivity
import com.codebrew.clikat.module.order_detail.adapter.OrderDetailProductAdapter
import com.codebrew.clikat.module.order_detail.adapter.OrderPagerAdapter
import com.codebrew.clikat.module.order_detail.rate_product.RateProductActivity
import com.codebrew.clikat.module.payment_gateway.PaymentListActivity
import com.codebrew.clikat.module.payment_gateway.PaymentWebViewActivity
import com.codebrew.clikat.module.payment_gateway.dialog_card.CardDialogFrag
import com.codebrew.clikat.module.product.product_listing.DialogListener
import com.codebrew.clikat.module.restaurant_detail.VideoPlayer
import com.codebrew.clikat.module.webview.WebViewActivity
import com.codebrew.clikat.preferences.DataNames
import com.codebrew.clikat.preferences.Prefs
import com.codebrew.clikat.user_chat.UserChatActivity
import com.codebrew.clikat.utils.*
import com.codebrew.clikat.utils.StaticFunction.isAppInstalled
import com.codebrew.clikat.utils.StaticFunction.setStatusBarColor
import com.codebrew.clikat.utils.configurations.Configurations
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
//import com.razorpay.Checkout
//import com.razorpay.PaymentData
//import com.razorpay.PaymentResultWithDataListener
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.HasAndroidInjector
import kotlinx.android.synthetic.main.activity_order_details.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import org.json.JSONObject
import pub.devrel.easypermissions.AppSettingsDialog
import pub.devrel.easypermissions.EasyPermissions
import java.lang.reflect.Type
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.*
import javax.inject.Inject
import kotlin.collections.ArrayList

/*
 * Created by cbl80 on 26/4/16.
 */

val EDIT_ORDER = 1001

class OrderDetailActivity : BaseActivity<ActivityOrderDetailsBinding, OrderDetailViewModel>(), OrderDetailNavigator,
        OrderDetailProductAdapter.OnReturnClicked, CardDialogFrag.onPaymentListener, HasAndroidInjector,
        EasyPermissions.PermissionCallbacks, DialogIntrface, DialogListener {

    @Inject
    lateinit var androidInjector: DispatchingAndroidInjector<Any>

    private var pagerAdapter: OrderPagerAdapter? = null
    private val appBackground by lazy { Color.parseColor(Configurations.colors.appBackground) }
    private var bookingFlowBean: BookingFlowBean? = null
    var settingData: SettingModel.DataBean.SettingData? = null
    private val textConfig by lazy { appUtil.loadAppConfig(0).strings }
    private var screenFlowBean: ScreenFlowBean? = null

    @Inject
    lateinit var mDataManager: PreferenceHelper

    private val orderHistoryBeans by lazy { mutableListOf<OrderHistory>() }

    lateinit var orderDetail: OrderHistory

    @Inject
    lateinit var dataManager: DataManager

    @Inject
    lateinit var appUtil: AppUtils

    @Inject
    lateinit var orderUtils: OrderUtils

    @Inject
    lateinit var permissionUtil: PermissionFile

    @Inject
    lateinit var mDialogsUtil: DialogsUtil

    var phoneNum: String? = null

    @Inject
    lateinit var factory: ViewModelProviderFactory

    private var mViewModel: OrderDetailViewModel? = null

    private lateinit var mBinding: ActivityOrderDetailsBinding

    private var mDeliveryType = 0

    private val orderId = ArrayList<Int>()

    lateinit var mSelectedPayment: CustomPayModel

    lateinit var orderData: OrderHistory

    var paymentSelec = 0

    var netAmt = 0.0f

    var mGson = Gson()
    private val decimalFormat: DecimalFormat = DecimalFormat("0.00", DecimalFormatSymbols.getInstance(Locale.ENGLISH))
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var locationCallback: LocationCallback? = null
    private var locationRequest: LocationRequest? = null
    private var geoFenceItem: GeofenceData? = null
    private var isPayNowClicked: Boolean? = false
    private var isRedirectToMap: Boolean? = false
    private var socketInputParam: SocketInputParam? = null
    private var dhlStatusResponse: TrackDhl? = null
    private var selectedCurrency: Currency? = null
    private var isAlreadyShown:Boolean?=false
    override fun getBindingVariable(): Int {
        return BR.viewModel
    }

    override fun getLayoutId(): Int {
        return R.layout.activity_order_details
    }

    override fun getViewModel(): OrderDetailViewModel {
        mViewModel = ViewModelProviders.of(this, factory).get(OrderDetailViewModel::class.java)
        return mViewModel as OrderDetailViewModel
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mBinding = viewDataBinding

        mViewModel?.navigator = this

        mBinding.color = Configurations.colors
        // mBinding.strings = appUtil.loadAppConfig(0).strings


        setStatusBarColor(this, getColor(R.color.dark_blue))
        settingData = dataManager.getGsonValue(DataNames.SETTING_DATA, SettingModel.DataBean.SettingData::class.java)
        bookingFlowBean = Prefs.with(this).getObject(DataNames.BOOKING_FLOW, BookingFlowBean::class.java)
        screenFlowBean = Prefs.with(this).getObject(DataNames.SCREEN_FLOW, ScreenFlowBean::class.java)
        selectedCurrency = dataManager.getGsonValue(PrefenceConstants.CURRENCY_INF, Currency::class.java)
        Prefs.with(this).save(DataNames.DB_AUTH, true)

        setlanguage()
        settoolbar()
        settypeface()
        clickListner()
        initialseSocket()

        EventBus.getDefault().register(this)

        pagerAdapter = OrderPagerAdapter(this, orderHistoryBeans, appUtil, this, orderUtils, decimalFormat, selectedCurrency)

        pagerAdapter?.setUserId(dataManager.getKeyValue(PrefenceConstants.USER_ID, PrefenceConstants.TYPE_STRING).toString(), bookingFlowBean, screenFlowBean, settingData)

        pagerAdapter?.settingCallback(OrderPagerAdapter.OrderListener({

            if (permissionUtil.hasCallPermissions(this)) {
                if (settingData?.is_number_masking_enable == "1") {
                    this.phoneNum = it.country_code ?: "" + it.proxy_phone_number ?: ""
                    callPhone((it.country_code ?: "") + (it.proxy_phone_number ?: ""))
                } else {
                    this.phoneNum = it.country_code ?: "" + it.phone_number
                    callPhone((it.country_code ?: "") + (it.phone_number ?: ""))
                }
            } else {
                permissionUtil.phoneCallTask(this)
            }

        }, {
            if (orderHistoryBeans.isEmpty() && orderId.isEmpty()) return@OrderListener

            if (orderHistoryBeans[vp_product_item.currentItem].agent?.isEmpty() == true) return@OrderListener

            val currentAgent = orderHistoryBeans[vp_product_item.currentItem].agent?.first()
                    ?: return@OrderListener

            launchActivity<UserChatActivity> {
                putExtra("orderId", orderId[vp_product_item.currentItem].toString())
                putExtra("userData", currentAgent)
            }
        }, {
            //rate supplier
            launchActivity<RateProductActivity> {
                putParcelableArrayListExtra("rateProducts", rateAgentSup(it, "Supplier"))
                putExtra("type", "Supplier")
            }
        }, {
            //rate agent
            launchActivity<RateProductActivity> {
                putParcelableArrayListExtra("rateProducts", rateAgentSup(it, "Agent"))
                putExtra("type", "Agent")
            }
        }, {
            //redirect to maps
            isRedirectToMap = true
            checkingLocationPermission()
        }, {
            //track Dhl status
            showDhlTrackStatus()
        }, {
            // hit SOS api
            hitSosApi(it)
        }, {
            hitShipRocketStatusApi(it)

        }, {
            launchActivity<WebViewActivity> {
                putExtra("terms", 5)
            }
        }, {
            checkZoomCallLink(it)
        }, {
            viewAgentBio(it)
        }
        ))

        vp_product_item.orientation = ViewPager2.ORIENTATION_HORIZONTAL
        vp_product_item.adapter = pagerAdapter

        /*      vp_product_item.offscreenPageLimit = 3

              val pageMargin: Float = resources.getDimensionPixelOffset(R.dimen.pageMargin).toFloat()
              val pageOffset: Float = resources.getDimensionPixelOffset(R.dimen.offset).toFloat()*/


        /*    vp_product_item.setPageTransformer { page, position ->
                val myOffset: Float = position * -(2 * pageOffset + pageMargin)

                when {
                    position < -1 -> {
                        page.translationX = -myOffset
                    }
                    position <= 1 -> {
                        val scaleFactor = 0.7f.coerceAtLeast(1 - abs(position - 0.14285715f))
                        page.translationX = myOffset
                        page.scaleY = scaleFactor
                        page.alpha = scaleFactor
                    }
                    else -> {
                        page.alpha = 0.0f
                        page.translationX = myOffset
                    }
                }
            }*/



        vp_product_item.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
                super.onPageScrolled(position, positionOffset, positionOffsetPixels)

                //  AppConstants.APP_SUB_TYPE= orderHistoryBeans[position].type?:0

                if (orderHistoryBeans.count() > 1) {

                    btn_previous.visibility = if(position != 0) View.VISIBLE else View.INVISIBLE
                    btn_next.visibility = if(orderHistoryBeans.count() - 1 != position) View.VISIBLE else View.INVISIBLE

                } else {
                    btn_previous.visibility = View.GONE
                    btn_next.visibility = View.GONE
                }

                if (orderHistoryBeans[position].type == 0) {
                    orderHistoryBeans[position].type = screenFlowBean?.app_type
                }

                mBinding.strings = textConfig

                if (orderHistoryBeans[position].status == 11.0) {
                    orderHistoryBeans[position].status = OrderStatus.In_Kitchen.orderStatus
                }

                setAppTerminology(orderHistoryBeans[position])

                statusOrder(orderHistoryBeans[position].status ?: 0.0, orderHistoryBeans[position])
                orderDetail = orderHistoryBeans[position]
                mDeliveryType = orderDetail.self_pickup ?: 0

                if (::fusedLocationClient.isInitialized)
                    fusedLocationClient.removeLocationUpdates(locationCallback)
                checkingLocationPermission()
            }
        })

        btn_previous.setOnClickListener {
            vp_product_item.currentItem = vp_product_item.currentItem-1
        }

        btn_next.setOnClickListener {
            vp_product_item.currentItem = vp_product_item.currentItem+1
        }

        if (intent.hasExtra("orderId")) {
            orderId.addAll(intent.getIntegerArrayListExtra("orderId") ?: arrayListOf())
        } /* else {
            orderId.addAll(getIntent().getIntegerArrayListExtra("orderId"));
        }*/

        orderDetailObserver()
        orderReturnProduct()
    }

    private fun viewAgentBio(it: String) {
        startActivity(Intent(this, VideoPlayer::class.java).putExtra("link", it))
    }

    private fun checkZoomCallLink(it: OrderHistory) {
        if (!it.zoom_call_start_url.isNullOrEmpty())
            StaticFunction.openUrl(this, it.zoom_call_start_url ?: "")
        else {
            if (isNetworkConnected)
                viewModel.apiCheckZoomAuth(it)
        }
    }

    override fun zoomAuth(data: TrackDhl?) {
        if (isNetworkConnected)
            viewModel.apiCreateZoomLink(data)
    }

    override fun zoomCallLink(data: DataZoom?) {
        if (!data?.startUrl.isNullOrEmpty())
            StaticFunction.openUrl(this, data?.startUrl ?: "")
    }

    private fun hitShipRocketStatusApi(orderId: String) {
        if (isNetworkConnected)
            viewModel.apiTrackShipRocket(orderId)
    }

    private fun hitSosApi(orderId: String) {
        if (isNetworkConnected)
            viewModel.apiSos(orderId)
    }

    override fun onSosSuccess() {
        AppToasty.success(this, getString(R.string.request_submitted))
    }

    private fun showDhlTrackStatus() {
        val dialogDhl = Dialog(this)
        dialogDhl.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialogDhl.setContentView(R.layout.dialog_track_dhl)
        dialogDhl.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        dialogDhl.setCancelable(false)
        val ivCross = dialogDhl.findViewById(R.id.ivCross) as ImageView
        val tvReferenceId = dialogDhl.findViewById(R.id.tvReferenceId) as TextView
        val tvServiceArea = dialogDhl.findViewById(R.id.tvServiceArea) as TextView
        val tvServiceStatus = dialogDhl.findViewById(R.id.tvShipingEvent) as TextView
        tvReferenceId.text = getString(R.string.reference_id, dhlStatusResponse?.shipmentInfo?.shipperReference?.referenceID)
        tvServiceArea.text = getString(R.string.service_area, dhlStatusResponse?.shipmentInfo?.shipmentEvent?.serviceArea?.description)
        tvServiceStatus.text = getString(R.string.service_event, dhlStatusResponse?.shipmentInfo?.shipmentEvent?.serviceEvent?.description)

        ivCross.setOnClickListener {
            dialogDhl.dismiss()
        }
        dialogDhl.show()
    }

    private fun initialseSocket() {
        socketInputParam = SocketInputParam()

        socketInputParam?.secret_key = dataManager.getKeyValue(
                PrefenceConstants.DB_SECRET,
                PrefenceConstants.TYPE_STRING
        ).toString()

        socketInputParam?.user_id =
                dataManager.getKeyValue(
                        PrefenceConstants.USER_ID,
                        PrefenceConstants.TYPE_STRING
                )
                        .toString()

        if (isNetworkConnected) {
            mViewModel?.connectToSocket()
        }
    }


    private fun hitGeoFenceTaxApi(orderHist: OrderHistory?) {
        if (isNetworkConnected) {
            mViewModel?.getGeofenceTax(orderHist?.latitude ?: 0.0, orderHist?.longitude
                    ?: 0.0, orderHist?.supplier_branch_id.toString())
        }
    }


    private fun rateAgentSup(orderHistory: OrderHistory?, type: String): ArrayList<RateProductListModel> {
        val name = if (type == "Agent") orderHistory?.agent?.firstOrNull()?.name else checkSupplierBranchName(orderHistory?.product?.firstOrNull())
        val image = if (type == "Agent") orderHistory?.agent?.firstOrNull()?.image else orderHistory?.logo

        val rateProductListModels = ArrayList<RateProductListModel>()
        rateProductListModels.add(RateProductListModel(name, checkSupplierBranchName(orderHistory?.product?.firstOrNull()), image,
                supplier_id = checkSupplierBranchId(orderHistory?.product?.firstOrNull()).toString(), order_id = orderDetail.order_id.toString()))
        return rateProductListModels
    }

    //*********************Location Request Or Permission Enable*************************
    private fun checkingLocationPermission() {
        if (permissionUtil.hasLocation(this)) {
            createLocationRequest()
        } else {
            permissionUtil.locationTask(this)
        }
    }

    private fun createLocationRequest() {
        locationRequest = LocationRequest.create()?.apply {
            interval = 10000
            fastestInterval = 5000
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        }

        val builder = LocationSettingsRequest.Builder()
                .addLocationRequest(locationRequest!!)

        val client = LocationServices.getSettingsClient(this)
        val task = client.checkLocationSettings(builder.build())

        task.addOnCompleteListener { task1 ->
            try {
                val response = task1.getResult(ApiException::class.java)
                // All location settings are satisfied. The client can initialize location
                getCurrentLocation()
            } catch (exception: ApiException) {
                when (exception.statusCode) {
                    LocationSettingsStatusCodes.RESOLUTION_REQUIRED ->
                        // Location settings are not satisfied. But could be fixed by showing the
                        // user a dialog.
                        try {
                            // Cast to a resolvable exception.
                            val resolvable = exception as ResolvableApiException
                            // Show the dialog by calling startResolutionForResult(),
                            // and check the result in onActivityResult().
                            resolvable.startResolutionForResult(
                                    this,
                                    AppConstants.RC_LOCATION_PERM
                            )
                        } catch (e: IntentSender.SendIntentException) {
                            // Ignore the error.
                        } catch (e: ClassCastException) {
                            // Ignore, should be an impossible error.
                        }

                    LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE ->
                        // Location settings are not satisfied. However, we have no way to fix the
                        // settings so we won't show the dialog.
                        Log.e("Data", "SETTINGS_CHANGE_UNAVAILABLE")
                }
            }
        }
    }

    private fun getCurrentLocation() {

        fusedLocationClient =
                LocationServices.getFusedLocationProviderClient(this)

        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult?) {
                if (locationResult == null) {
                    return
                }
                for (location in locationResult.locations) {
                    /*hit api only when button is clicked*/

                    if (isPayNowClicked == true)
                        hitPaymentGatewaysApi(location)
                    else if (isRedirectToMap == true)
                    {
                        if(isAppInstalled(this@OrderDetailActivity,"com.waze"))
                            redirectToWazeMap(location)
                        else
                          redirectToGoogleMap(location)
                    }

                    /*check for 100 meter: restaurant location in pickup flow mark status arrived if within 100m*/
                    /*if normal order than stop location update*/
                    if (::orderDetail.isInitialized && orderDetail.self_pickup != null &&
                            orderDetail.self_pickup == FoodAppType.Pickup.foodType &&
                            orderDetail.status != OrderStatus.Arrived.orderStatus &&
                            orderDetail.status != OrderStatus.Delivered.orderStatus &&
                            orderDetail.status != OrderStatus.PickUp.orderStatus &&
                            orderDetail.status != OrderStatus.Pending.orderStatus
                    ) {
                        //AppToasty.success(this@OrderDetailActivity, "location updateee")
                        compareLocation(location)
                    } else {
                        //AppToasty.error(this@OrderDetailActivity, "location canel")
                        fusedLocationClient.removeLocationUpdates(locationCallback)
                        break
                    }


                }
            }


        }
        startLocationUpdates()
    }

    private fun compareLocation(userLocation: Location?) {

        if (isNetworkConnected) {
            mViewModel?.connectToSocket()
        }

        socketInputParam?.latitude = userLocation?.latitude.toString()
        socketInputParam?.longitude = userLocation?.longitude.toString()
        socketInputParam?.order_id = orderDetail.order_id.toString()

        socketInputParam?.let {
            mViewModel?.sendCurrentLoc(it)
        }

        /* val restaurantLocation = Location("")
         restaurantLocation.latitude = orderDetail.latitude ?: 0.0
         restaurantLocation.longitude = orderDetail.longitude ?: 0.0

         val distance = userLocation?.distanceTo(restaurantLocation)
         if (distance ?: 0f < 100f) {
             if (isNetworkConnected)
                 mViewModel?.apiChangeOrderStatus(orderDetail.order_id.toString(), OrderStatus.Arrived.orderStatus, null, "1")
         }*/

    }


    private fun hitPaymentGatewaysApi(location: Location) {
        isPayNowClicked = false
        if (isNetworkConnected)
            viewModel.getPaymentGateways(location.latitude, location.longitude)
    }

    private fun redirectToGoogleMap(location: Location) {
        isRedirectToMap = false
        if (!::orderDetail.isInitialized) return
        val uri = "http://maps.google.com/maps?saddr=" + location.latitude.toString() + "," + location.longitude.toString() + "&daddr=" +
                orderDetail.latitude.toString() + "," + orderDetail.longitude
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(uri))
        startActivity(intent)
    }

    private fun redirectToWazeMap(location: Location) {
        isRedirectToMap = false
        if (!::orderDetail.isInitialized) return
        try {
            // Launch Waze
            val url = "https://waze.com/ul?ll="+ orderDetail.latitude + "," + orderDetail.longitude+"&navigate=yes&zoom=17"
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
            startActivity(intent)
        } catch (ex: ActivityNotFoundException) {
            redirectToGoogleMap(location)
        }
    }

    private fun startLocationUpdates() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                    checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
            ) {
                // TODO: Consider calling
                //    Activity#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for Activity#requestPermissions for more details.
                return
            }
        }
        fusedLocationClient.requestLocationUpdates(
                locationRequest,
                locationCallback,
                null /* Looper */
        )
    }

    //***********************************************************************************


    private fun setAppTerminology(orderHistory: OrderHistory) {

        if (pagerAdapter == null && orderHistory.terminology.isNullOrEmpty()) return

        val terminologyBean = mGson.fromJson(orderHistory.terminology, SettingModel.DataBean.Terminology::class.java)
        val languageId = dataManager.getLangCode()

        var appTerminology: SettingModel.DataBean.AppTerminology? = null
        if (orderHistory.terminology != null) {
            appTerminology = if (languageId == ClikatConstants.ENGLISH_SHORT || languageId == ClikatConstants.ENGLISH_FULL) {
                terminologyBean.english
            } else {
                terminologyBean.other
            }
        }

        pagerAdapter?.settingTerminology(appTerminology, orderHistory.type ?: 0)
    }


    override fun onResume() {
        super.onResume()
        if (!::orderDetail.isInitialized || (::orderDetail.isInitialized && orderDetail.editOrder == false)) {
            if (isNetworkConnected) {
                viewModel.getOrderDetail(orderId)
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onMessageEvent(event: OrderEvent) {
        if (event.type == AppConstants.NOTIFICATION_EVENT) {
            if (isNetworkConnected) {
                //  viewModel.orderDetailLiveData.value=null
                viewModel.getOrderDetail(orderId)
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        // AppToasty.error(this@OrderDetailActivity, "location destroy")

        if (::fusedLocationClient.isInitialized)
            fusedLocationClient.removeLocationUpdates(locationCallback)
        if (::orderDetail.isInitialized)
            orderDetail.self_pickup = null
        /* mViewModel?.destroySocket()*/
        EventBus.getDefault().unregister(this)

    }


    private fun orderDetailObserver() {
        // Create the observer which updates the UI.
        val catObserver = Observer<List<OrderHistory>> { resource ->

            resource?.firstOrNull()?.let {
                orderDetail = it
            }

            if (isNetworkConnected && resource?.firstOrNull()?.dhlData?.isNotEmpty() == true && !::orderDetail.isInitialized)
                mViewModel?.apiTrackDhl(resource.firstOrNull()?.order_id.toString())

            if (geoFenceItem == null)
                hitGeoFenceTaxApi(resource?.firstOrNull())

            initailize(resource)
        }
        // Observe the LiveData, passing in this activity as the LifecycleOwner and the observer.
        viewModel.orderDetailLiveData.observe(this, catObserver)
    }


    private fun clickListner() {
        btnCancel.setOnClickListener {
            if (!::orderDetail.isInitialized) return@setOnClickListener
            if (orderDetail.payment_type == DataNames.DELIVERY_CARD && settingData?.wallet_module == "1") {
                cancelOrderWallet()
            } else {
                if (orderDetail.status == OrderStatus.Scheduled.orderStatus) sweetDialog(1) else sweetDialog(0)
            }
        }


        btnReorder.setOnClickListener {
            if (!::orderDetail.isInitialized) return@setOnClickListener
            if (isNetworkConnected) {

                if (bookingFlowBean?.vendor_status == 0 && appUtil.checkVendorStatus(orderDetail.supplier_id, vendorBranchId = orderDetail.supplier_branch_id, branchFlow = settingData?.branch_flow)) {
                    mDialogsUtil.openAlertDialog(this, getString(R.string.clearCart, textConfig?.supplier
                            ?: "", textConfig?.proceed ?: ""), "Yes", "No", this)
                } else {
                    addToCart()
                }
            }
        }

        btnEditOrder?.setOnClickListener {
            if (screenFlowBean?.app_type == AppDataType.Food.type) {
                if (orderDetail.editOrder == true) {
                    val list = ArrayList<EditProductsItem>()
                    list.addAll(selectedProductsList?.map { it.copy() } ?: arrayListOf())
                    val intent = Intent(this, MainScreenActivity::class.java)
                    intent.putExtra("orderItem", orderDetail)
                    intent.putExtra("isEditOrder", true)
                    intent.putExtra("selectedList", list)
                    startActivityForResult(intent, EDIT_ORDER)
                } else {
                    orderDetail.editOrder = true
                    btnSave.visibility = View.VISIBLE
                    btnEditOrder.text = getString(R.string.add_items)
                    orderDetail.product?.map { it?.isEdit = true }
                    pagerAdapter?.notifyDataSetChanged()
                }
            } else if (screenFlowBean?.app_type == AppDataType.HomeServ.type) {
                val intent = Intent(this, MainScreenActivity::class.java)
                intent.putExtra("orderItem", orderDetail)
                intent.putExtra("geofenceData", geoFenceItem)
                intent.putExtra("isEditOrder", true)
                startActivityForResult(intent, EDIT_ORDER)
            }
        }

        tvNewAddedItems?.setOnClickListener {
            val list = ArrayList<EditProductsItem>()
            list.addAll(selectedProductsList?.map { it.copy() } ?: arrayListOf())
            val intent = Intent(this, MainScreenActivity::class.java)
            intent.putExtra("orderItem", orderDetail)
            intent.putExtra("selectedList", list)
            intent.putExtra("isEditOrder", true)
            startActivityForResult(intent, EDIT_ORDER)
        }

        btnSave?.setOnClickListener {
            if (::orderDetail.isInitialized) {
                val removalItems = addListInSelectedItem()
                if (selectedProductsList?.isNotEmpty() == true)
                    editOrder(removalItems)
                else
                    mBinding.root.onSnackbar(getString(R.string.select_atleast_one))
            }
        }
        btnChangeUserStatus?.setOnClickListener {
            if (!::orderDetail.isInitialized) return@setOnClickListener
            /*if next status to changeis arrived  : user needs to enter parking locations*/
            if (orderDetail.nextStatusToChange == OrderStatus.Arrived.orderStatus) {
                if (BuildConfig.CLIENT_CODE == "skipp_0631") {
                    if (isNetworkConnected)
                        mViewModel?.apiChangeOrderStatus(orderDetail.order_id.toString(), orderDetail.nextStatusToChange, "", null)
                } else
                    addParkingInstructionsDialog()
            } else {
                if (isNetworkConnected)
                    mViewModel?.apiChangeOrderStatus(orderDetail.order_id.toString(), orderDetail.nextStatusToChange, null, null)
            }
        }
    }

    private fun addToCart() {
        if (dataManager.getCurrentUserLoggedIn()) {
            val productList = covertCartToArray(orderDetail.product)


            val cartInfoServerArray = CartInfoServerArray()

            val calendar = Calendar.getInstance()
            cartInfoServerArray.order_day = appUtil.getDayId(calendar.get(Calendar.DAY_OF_WEEK))
                    ?: ""
            cartInfoServerArray.order_time = appUtil.convertDateOneToAnother(calendar.time.toString(), "EEE MMM d HH:mm:ss Z yyyy", "HH:mm:ss")
                    ?: ""

            cartInfoServerArray.addons = orderDetail.product?.map {
                it?.adds_on?.associateBy { it?.insertData() }
            }?.flatMap { it?.keys ?: emptyList<ProductAddon>() }

            cartInfoServerArray.productList = productList

            cartInfoServerArray.variants = productList.flatMap {
                it?.variants ?: mutableListOf()
            }

            cartInfoServerArray.supplierBranchId = orderDetail.supplier_branch_id ?: 0

            viewModel.addCart(cartInfoServerArray)
        }
    }

    fun AddsOn.insertData() = ProductAddon(
            id = adds_on_id.toString(),
            name = adds_on_name,
            price = price,
            type_id = adds_on_type_jd.toString(),
            type_name = adds_on_type_name,
            quantity = quantity,
            adds_on_type_quantity = adds_on_type_quantity?.toInt(),
            serial_number = serial_number
    )


    private fun settypeface() {
        btnCancel?.typeface = AppGlobal.regular
        btnReorder?.typeface = AppGlobal.regular
        tvTitle?.typeface = AppGlobal.regular
    }

    private fun initailize(orderHistoryBeanList: List<OrderHistory>) {
        if (settingData?.can_user_edit != null && settingData?.can_user_edit == "1" &&
                orderHistoryBeanList.firstOrNull()?.is_edit == 0 &&
                (orderHistoryBeanList.firstOrNull()?.status == OrderStatus.Confirmed.orderStatus ||
                        orderHistoryBeanList.firstOrNull()?.status == OrderStatus.Pending.orderStatus)) {
            lytEditOrder?.visibility = View.VISIBLE
            if (screenFlowBean?.app_type != AppDataType.Food.type) {
                btnSave?.visibility = View.GONE
            }
        } else
            lytEditOrder?.visibility = View.GONE

        if (orderHistoryBeanList[0].type == 0) {
            orderHistoryBeanList[0].type = screenFlowBean?.app_type
        }

        mBinding.strings = appUtil.loadAppConfig(orderHistoryBeanList.firstOrNull()?.type
                ?: 0).strings
        tvTitle?.text = getString(R.string.order_details, mBinding.strings?.order)
        btnEditOrder?.text = getString(R.string.edit_order_detail, mBinding.strings?.order)

        if (orderHistoryBeanList[0].status == 11.0) {
            orderHistoryBeanList[0].status = OrderStatus.In_Kitchen.orderStatus
        }

        if (orderHistoryBeanList[0].status == 10.0) {
            orderHistoryBeanList[0].status = OrderStatus.Ready_to_be_picked.orderStatus
        }

        setAppTerminology(orderHistoryBeanList[0])

        orderHistoryBeanList[0].status?.let {
            statusOrder(it, orderHistoryBeanList[0])
        }

        if (orderHistoryBeanList.firstOrNull()?.self_pickup == FoodAppType.Pickup.foodType && settingData?.hide_pickup_status != "1") {
            btnChangeUserStatus?.visibility = when (orderHistoryBeanList.firstOrNull()?.status) {
                OrderStatus.Pending.orderStatus, OrderStatus.Approved.orderStatus, OrderStatus.PickUp.orderStatus,
                OrderStatus.Customer_Canceled.orderStatus, OrderStatus.Rejected.orderStatus, OrderStatus.Packed.orderStatus -> View.GONE
                else -> View.VISIBLE
            }

            val status = appUtil.statusToChange(orderHistoryBeanList.firstOrNull()?.status
                    ?: 0.0, orderHistoryBeanList.firstOrNull()?.user_on_the_way)
            orderHistoryBeanList.firstOrNull()?.nextStatusToChange = status

            if (BuildConfig.CLIENT_CODE == "skipp_0631" && OrderStatus.PickUp.orderStatus == status)
                btnChangeUserStatus?.visibility = View.GONE
            else
                btnChangeUserStatus?.text = StaticFunction.getStatusText(status, this)
        } else
            btnChangeUserStatus?.visibility = View.GONE
        //AppConstants.APP_SUB_TYPE= orderHistoryBeanList[0].type?:0

        orderHistoryBeans.clear()
        orderHistoryBeans.addAll(orderHistoryBeanList)
        pagerAdapter?.notifyDataSetChanged()

        if(isAlreadyShown != true && !orderHistoryBeanList.isNullOrEmpty() &&
            orderHistoryBeanList.firstOrNull()?.status == OrderStatus.Delivered.orderStatus
                && settingData?.is_agent_rating == "1" && !(orderHistoryBeanList.firstOrNull()?.agent.isNullOrEmpty())){

            isAlreadyShown=true
            rateAgentDialog(orderHistoryBeanList.firstOrNull())
        }
    }

    private fun statusOrder(status: Double, orderHist: OrderHistory) {

        btnPayNow.visibility = View.GONE

        when (status) {
            OrderStatus.Pending.orderStatus -> {
                lyt_action_btn.visibility = View.VISIBLE
                btnReorder.visibility = View.GONE
                btnReorder.visibility = View.GONE
            }

            OrderStatus.Confirmed.orderStatus, OrderStatus.On_The_Way.orderStatus,
            OrderStatus.Near_You.orderStatus,
            OrderStatus.Ready_to_be_picked.orderStatus, OrderStatus.Reached.orderStatus,
            OrderStatus.In_Kitchen.orderStatus -> {
                btnReorder.visibility = View.GONE

                lyt_action_btn.visibility = View.GONE
            }


            OrderStatus.Delivered.orderStatus, OrderStatus.Rejected.orderStatus,
            OrderStatus.Customer_Canceled.orderStatus, OrderStatus.Rating_Given.orderStatus -> {
                btnReorder.visibility = if (orderHist.type ?: 0 != AppDataType.HomeServ.type) {
                    View.VISIBLE
                } else {
                    View.GONE
                }

                lyt_action_btn.visibility = View.VISIBLE
            }
        }

        if (orderHist.type ?: 0 == AppDataType.HomeServ.type && status == OrderStatus.Customer_Canceled.orderStatus &&
                status == OrderStatus.Delivered.orderStatus) {
            btnReorder.visibility = View.GONE
        }


        val cancelHidden = (listOf(OrderStatus.Rejected.orderStatus, OrderStatus.Rating_Given.orderStatus,
                OrderStatus.Customer_Canceled.orderStatus, OrderStatus.Delivered.orderStatus).contains(status))
                || settingData?.disable_order_cancel ?: "0" == "0"
                && (status != OrderStatus.Pending.orderStatus || settingData?.disbale_user_cancel_pending_order ?: "0" == "0")
                || (settingData?.disable_user_cancel_after_confirm == "1" && status != OrderStatus.Pending.orderStatus)

        btnCancel.visibility = if (!cancelHidden) {
            lyt_action_btn.visibility = View.VISIBLE
            View.VISIBLE
        } else {
            View.GONE
        }

        val paymentFlow = orderUtils.checkPaymtFlow(orderHist)
        if (paymentFlow > 0)
            enablePayNow(orderHist, paymentFlow)
    }

    private fun enablePayNow(orderHist: OrderHistory, paymentFlow: Int) {

        with(orderHist)
        {
            when (paymentFlow) {
                OrderPayment.ReceiptOrder.payment, OrderPayment.PaymentAfterConfirm.payment -> {
                    orderData = this
                    paymentSelec = OrderPayment.ReceiptOrder.payment
                    netAmt = net_amount ?: 0.0f

                    lyt_action_btn.visibility = View.VISIBLE
                    btnPayNow.visibility = View.VISIBLE
                    btnCancel.visibility = View.VISIBLE
                    btnPayNow.text = getString(R.string.pay_now, AppConstants.CURRENCY_SYMBOL, Utils.getPriceFormat(net_amount
                            ?: 0.0f, settingData, selectedCurrency))

                }
                OrderPayment.EditOrder.payment -> {
                    orderData = this
                    paymentSelec = OrderPayment.EditOrder.payment

                    netAmt = remaining_amount ?: 0.0f
                    lyt_action_btn.visibility = View.VISIBLE
                    btnPayNow.visibility = View.VISIBLE
                    btnPayNow.text = getString(R.string.pay_now, AppConstants.CURRENCY_SYMBOL, Utils.getPriceFormat(netAmt, settingData, selectedCurrency))
                }
            }

            btnPayNow.setOnClickListener {
                isPayNowClicked = true
                checkingLocationPermission()

            }
        }
    }

    private fun settoolbar() {
        setSupportActionBar(toolbar)
        val icon = resources.getDrawable(R.drawable.ic_back)
        icon.setColorFilter(resources.getColor(R.color.black), PorterDuff.Mode.SRC_IN)
        //tvTitle?.text = getString(R.string.order_details, appUtil.loadAppConfig(0).strings?.order)
        supportActionBar?.setHomeAsUpIndicator(icon)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowTitleEnabled(false)
    }

    private fun setlanguage() {
        val selectedLang = dataManager.getKeyValue(DataNames.SELECTED_LANGUAGE, PrefenceConstants.TYPE_STRING).toString()

        if (selectedLang == "arabic" || selectedLang == "ar") {
            GeneralFunctions.force_layout_to_RTL(this)
        } else {
            GeneralFunctions.force_layout_to_LTR(this)
        }
    }


    override fun onBackPressed() {
        setResult(Activity.RESULT_CANCELED)
        finish()
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
    }


    private fun sweetDialog(isScheduled: Int) {

        val sweetAlertDialog = AlertDialog.Builder(this@OrderDetailActivity)
        sweetAlertDialog.setTitle(getString(R.string.cancel_order, textConfig?.order))
        sweetAlertDialog.setMessage(getString(R.string.doYouCancel, textConfig?.order))

        sweetAlertDialog.setPositiveButton(getString(R.string.ok)) { dialog, which ->
            if (isNetworkConnected) {
                val cancelWallet = if (settingData?.wallet_module == "1") 1 else 0
                viewModel.cancelOrder(orderDetail.order_id.toString(), cancelWallet)
            }
            dialog.dismiss()
        }

        sweetAlertDialog.setNegativeButton(getString(R.string.cancel)) { dialog, which ->
            dialog.dismiss()
        }
        sweetAlertDialog.show()
    }

    private fun cancelOrderWallet() {
        val cancelDialog = Dialog(this)
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
            if (isNetworkConnected) {
                viewModel.cancelOrder(orderDetail.order_id.toString(), cancelToWallet)
            }
            cancelDialog.dismiss()
        }
        cancelDialog.show()
    }

    private fun rateAgentDialog(orderHist: OrderHistory?){
        android.app.AlertDialog.Builder(this)
                .setMessage(getString(R.string.do_you_want_to_rate_order))
                .setPositiveButton(getString(R.string.yes)) { _, _->
                    launchActivity<RateProductActivity> {
                        putParcelableArrayListExtra("rateProducts", rateAgentSup(orderHist, "Agent"))
                        putExtra("type", "Agent")
                    }
                }
                .setNegativeButton(getString(R.string.no),null)
                .show()
    }

    private fun addParkingInstructionsDialog() {
        val parkingDialog = Dialog(this)
        parkingDialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        parkingDialog.setContentView(R.layout.dialog_parking_instructions)
        parkingDialog.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        parkingDialog.setCancelable(false)
        val ivCross = parkingDialog.findViewById(R.id.ivCross) as ImageView
        val etParkingInstruction = parkingDialog.findViewById(R.id.etParkingInstruction) as EditText
        val btnSubmit = parkingDialog.findViewById(R.id.btnSubmit) as Button
        ivCross.setOnClickListener {
            parkingDialog.dismiss()
        }

        btnSubmit.setOnClickListener {
            val instructions = etParkingInstruction.text.toString().trim()
            if (instructions.isEmpty())
                AppToasty.error(this, getString(R.string.enter_instruction_error))
            else {
                hideKeyboard()
                if (isNetworkConnected)
                    mViewModel?.apiChangeOrderStatus(orderDetail.order_id.toString(), orderDetail.nextStatusToChange, instructions, null)
                parkingDialog.dismiss()
            }
        }
        parkingDialog.show()
    }

    private fun sweetDialogSu() {
        val sweetAlertDialog = AlertDialog.Builder(this@OrderDetailActivity)
        sweetAlertDialog.setTitle(getString(R.string.success))
        sweetAlertDialog.setMessage(getString(R.string.cancel_msg, textConfig?.order))
        sweetAlertDialog.setPositiveButton(getString(R.string.ok)) { dialog, which ->
            dialog.dismiss()
            setResult(Activity.RESULT_OK)
            finish()
        }
        sweetAlertDialog.show()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> onBackPressed()
        }
        return super.onOptionsItemSelected(item)
    }

    private fun covertCartToArray(product: List<ProductDataBean?>?): MutableList<CartInfoServer> {
        val listCartInfoServers: MutableList<CartInfoServer> = ArrayList()
        if (product != null) {
            for (i in product.indices) {
                val cartInfoServer = CartInfoServer()
                cartInfoServer.quantity = product[i]?.prod_quantity
                cartInfoServer.productId = product[i]?.product_id.toString()
                cartInfoServer.handlingAdmin = product[i]?.handling_admin
                cartInfoServer.supplier_branch_id = product[i]?.supplier_branch_id
                cartInfoServer.handlingSupplier = product[i]?.handling_supplier
                cartInfoServer.supplier_id = product[i]?.supplier_id
                cartInfoServer.deliveryCharges = product[i]?.delivery_charges
                cartInfoServer.pricetype = 0
                cartInfoServer.appType = AppConstants.APP_SUB_TYPE
                cartInfoServer.name = product[i]?.name
                cartInfoServer.fixed_price = product[i]?.fixed_price?.toFloatOrNull()
                cartInfoServer.variants = product[i]?.prod_variants
                cartInfoServer.price = product[i]?.price?.toFloatOrNull()
                cartInfoServer.category_id = product[i]?.category_id
                cartInfoServer.handlingSupplier = product[i]?.handling_supplier
                listCartInfoServers.add(cartInfoServer)
            }
        }
        return listCartInfoServers
    }

    private fun addtoCartLocal(product: ProductDataBean?) {


        appUtil.addProductDb(this, orderHistoryBeans[vp_product_item.currentItem].type
                ?: 0, product)
    }


    override fun onCartAdded(cartdata: AddtoCartModel.CartdataBean?) {

        if (!::orderDetail.isInitialized) return

        dataManager.setkeyValue(DataNames.CART_ID, cartdata?.cartId ?: "")

        /*      val bundle = bundleOf(DataNames.SUPPLIER_BRANCH_ID to supplierBranchId)
              val deliveryFragment = DeliveryFragment()
              deliveryFragment.arguments = bundle*/

        addCartLocal2(orderDetail)

        Toast.makeText(this@OrderDetailActivity, getString(R.string.cart_added), Toast.LENGTH_SHORT).show()
        /*  val intent = Intent()
          intent.putExtra(DataNames.SUPPLIER_BRANCH_ID, supplierBranchId)
          setResult(Activity.RESULT_OK, intent)*/
        finish()
    }

    private fun addCartLocal2(orderDetail: OrderHistory?) {

        val prodList = arrayListOf<ProductDataBean?>()

        var cartinfo: CartInfo

        orderDetail?.product?.mapIndexed { index, product ->

            if (product?.adds_on.isNullOrEmpty()) {
                product?.prod_quantity = product?.quantity
                product?.netPrice = product?.fixed_price?.toFloatOrNull()
                product?.supplier_name = checkSupplierBranchName(product)
                prodList += product?.copy()
            } else {
                product?.adds_on?.groupBy {
                    it?.serial_number
                }?.mapValues {
                    product.adds_on = it.value
                    product.add_on_name = it.value.map { it?.adds_on_type_name }.joinToString()
                    product.quantity = it.value[0]?.quantity ?: 0f
                    product.prod_quantity = it.value[0]?.quantity ?: 0f
                    product.supplier_name = checkSupplierBranchName(product)

                    //product.supplier_image=orderDetail.upp
                    // product.netPrice= product.fixed_price
                    prodList += product.copy()
                }
            }

        }


        prodList.mapIndexed { index, productDataBean ->

            if (productDataBean?.adds_on?.isEmpty() == true) {
                if (appUtil.checkProdExistance(productDataBean.product_id)) {

                    var quantity = appUtil.getCartList().cartInfos?.find { it.productId == productDataBean.product_id }?.quantity
                    quantity = quantity?.plus(productDataBean.quantity ?: 0f)

                    StaticFunction.updateCart(this, productDataBean.product_id, quantity, productDataBean.netPrice
                            ?: 0.0f)
                } else {
                    addCartDB(productDataBean, mutableListOf(), orderDetail?.type ?: 0)
                }
            } else {
                cartinfo = appUtil.checkProductAddon(productDataBean?.adds_on?.map {
                    it?.insertData()
                }?.toMutableList() ?: mutableListOf())

                if (cartinfo.productId != 0) {
                    productDataBean?.productAddonId = cartinfo.productAddonId
                    updateCartDB(productDataBean, cartinfo)
                } else {
                    addCartDB(productDataBean, productDataBean?.adds_on?.asSequence()?.map { it?.insertData() }?.toMutableList()
                            ?: mutableListOf(), orderDetail?.type ?: 0)
                }
            }
        }

    }

    private fun updateCartDB(productModel: ProductDataBean?, cartinfo: CartInfo) {
        val quantity = cartinfo.quantity
        quantity.plus(productModel?.quantity ?: 0f)
        productModel?.prod_quantity = quantity
        productModel?.fixed_quantity = quantity

        appUtil.updateItem(productModel)
    }


    private fun addCartDB(productModel: ProductDataBean?, productAddon: MutableList<ProductAddon?>, type: Int) {
        val cartInfo = CartInfo()
        cartInfo.quantity = productModel?.quantity ?: 0f
        cartInfo.productName = productModel?.name
        cartInfo.productDesc = productModel?.product_desc
        //  cartInfo.supplierAddress=productModel.suppl
        cartInfo.productId = productModel?.product_id ?: 0
        cartInfo.imagePath = productModel?.image_path.toString()
        cartInfo.fixed_price = productModel?.fixed_price?.toFloatOrNull()
        cartInfo.supplierName = checkSupplierBranchName(productModel)
        cartInfo.suplierBranchId = productModel?.supplier_branch_id ?: 0
        cartInfo.measuringUnit = productModel?.measuring_unit
        cartInfo.deliveryCharges = productModel?.delivery_charges ?: 0.0f
        cartInfo.supplierId = productModel?.supplier_id ?: 0
        cartInfo.urgent_type = productModel?.urgent_type ?: 0
        cartInfo.isUrgent = productModel?.can_urgent ?: 0
        cartInfo.latitude = productModel?.latitude
        cartInfo.longitude = productModel?.longitude
        // cartInfo.setUrgentValue(productModel.getUrgent_value());
        cartInfo.categoryId = productModel?.category_id ?: 0

        cartInfo.add_ons?.addAll(productAddon)

        cartInfo.add_on_name = productAddon.joinToString { "${it?.type_name ?: ""} * ${it?.adds_on_type_quantity}" }

        cartInfo.price = if (productAddon.isNotEmpty()) {
            productAddon.sumBy {
                it?.price?.toInt() ?: 0
            }.toFloat().plus(productModel?.price?.toFloatOrNull() ?: 0.0f)
        } else {
            productModel?.netPrice ?: 0.0f
        }

        cartInfo.deliveryType = mDeliveryType
        cartInfo.productAddonId = Calendar.getInstance().timeInMillis

        cartInfo.handlingAdmin = productModel?.handling_admin ?: 0.0f
        cartInfo.handlingSupplier = productModel?.handling_supplier ?: 0.0f
        cartInfo.appType = type

        // cartInfo.handlingCharges = productModel?.handling_admin ?.plus(productModel.handling_supplier?: 0.0f)?: 0.0f
        appUtil.addItem(cartInfo)

        // productModel?.prod_quantity = productModel?.prod_quantity
    }


    override fun onCancelOrder() {
        sweetDialogSu()
    }

    override fun editOrderResponse(data: Data?) {
        AppToasty.success(this, getString(R.string.order_edited_success))
        orderDetail.editOrder = false
        selectedProductsList?.clear()
        tvNewAddedItems?.visibility = View.GONE

        if (isNetworkConnected) {
            viewModel.getOrderDetail(orderId)
        }
    }

    override fun onCompletePayment() {
        mBinding.root.onSnackbar("Payment done successfully.")

        if (isNetworkConnected) {
            viewModel.getOrderDetail(orderId)
        }
    }

    override fun onTrackDhl(data: TrackDhl?) {
        dhlStatusResponse = data
    }

    override fun onTrackShipRocket(data: TrackDhl?) {
        if (!data?.tracking_data?.trackUrl.isNullOrEmpty())
            StaticFunction.openCustomChrome(this, data?.tracking_data?.trackUrl ?: "")
    }


    override fun onChangeStatus(status: Double?) {
        AppToasty.success(this, getString(R.string.status_changed_successfully))
        orderDetail.status = status


        if (orderDetail.status == OrderStatus.PickUp.orderStatus || (BuildConfig.CLIENT_CODE == "skipp_0631" && OrderStatus.Arrived.orderStatus == status)) {
            btnChangeUserStatus?.visibility = View.GONE
        } else {
            btnChangeUserStatus?.visibility = View.VISIBLE
            orderDetail.nextStatusToChange = appUtil.statusToChange(status
                    ?: 0.0, orderDetail.user_on_the_way)
            btnChangeUserStatus?.text = StaticFunction.getStatusText(orderDetail.nextStatusToChange, this)
        }
        pagerAdapter?.notifyDataSetChanged()

        orderDetail.status?.let {
            statusOrder(it, orderDetail)
        }

        /*if (isNetworkConnected) {
            viewModel.getOrderDetail(orderId)
        }*/
    }


    override fun onGeofencePayment(data: GeofenceData?, geofenceTax: Boolean) {
        if (geofenceTax)
            geoFenceItem = data
        else {
            if (::orderData.isInitialized) {
                paymentDetail(orderData, data?.gateways as ArrayList<String>?)
            }
        }
    }

    override fun onErrorOccur(message: String) {
        mBinding.root.onSnackbar(message)
    }

    override fun onSessionExpire() {
        openActivityOnTokenExpire()
    }


    private fun callPhone(number: String) {

        val intent = Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", number, null))
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED) {
            Manifest.permission.CALL_PHONE
            startActivity(intent)
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(arrayOf(Manifest.permission.CALL_PHONE), 1)
            }
        }
    }

    override fun onReturnProductClicked(item: ProductDataBean?) {
        showReturnDialog(item)
    }

    override fun onRateProd(item: ProductDataBean?) {
        launchActivity<RateProductActivity> {
            putParcelableArrayListExtra("rateProducts", calculateRateProduct(item))
            putExtra("type", "Prod")
        }
    }

    override fun onClickProdDescListener(productDesc: String) {

        val desc = Utils.getHtmlData(productDesc).toString()
        if (desc.isEmpty()) return

        StaticFunction.sweetDialogueSuccess11(this, getString(R.string.description), desc, false, 1001, this)
    }

    override fun onIncrementClicked(item: ProductDataBean?, adapterPosition: Int, parentPosition: Int) {
        item?.quantity = if (settingData?.is_decimal_quantity_allowed == "1")
            decimalFormat.format(item?.quantity?.plus(AppConstants.DECIMAL_INTERVAL)).toFloat()
        else
            decimalFormat.format(item?.quantity?.plus(1)).toFloat()

        pagerAdapter?.addRemoveItem(item, adapterPosition, parentPosition, selectedProductsList, geoFenceItem)
    }

    override fun onDecrementClicked(item: ProductDataBean?, adapterPosition: Int, parentPosition: Int) {
        if (item?.quantity ?: 0f >= 1f) {
            item?.quantity = if (settingData?.is_decimal_quantity_allowed == "1")
                decimalFormat.format(item?.quantity?.minus(AppConstants.DECIMAL_INTERVAL)).toFloat()
            else
                decimalFormat.format(item?.quantity?.minus(1)).toFloat()
            pagerAdapter?.addRemoveItem(item, adapterPosition, parentPosition, selectedProductsList, geoFenceItem)
        }
    }


    private fun calculateRateProduct(product: ProductDataBean?): ArrayList<RateProductListModel> {
        val rateProductListModels = ArrayList<RateProductListModel>()
        rateProductListModels.add(RateProductListModel(product?.name, checkSupplierBranchName(product), product?.image_path.toString(),
                product?.product_id.toString(), supplier_id = checkSupplierBranchId(product).toString(), order_id = orderDetail.order_id.toString()))
        return rateProductListModels
    }


    private fun showReturnDialog(item: ProductDataBean?) {
        val returnDialog = Dialog(this)
        returnDialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        returnDialog.setContentView(R.layout.layout_return_product)
        returnDialog.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        returnDialog.setCancelable(false)

        val ivCross = returnDialog.findViewById(R.id.ivCross) as ImageView
        val etReason = returnDialog.findViewById(R.id.etReason) as EditText
        val btnReturnProd = returnDialog.findViewById(R.id.btnReturnProd) as Button
        val rbWallet = returnDialog.findViewById(R.id.rbWallet) as RadioButton
        val rbOthers = returnDialog.findViewById(R.id.rbOthers) as RadioButton
        val rgGroup = returnDialog.findViewById(R.id.rgGroup) as RadioGroup
        rgGroup.visibility = if (settingData?.wallet_module != "1") View.GONE else View.VISIBLE

        ivCross.setOnClickListener {
            returnDialog.dismiss()
        }

        btnReturnProd.setOnClickListener {
            val reason = etReason.text.toString().trim()
            if (reason.isEmpty()) {
                AppToasty.error(this, getString(R.string.enter_reason_to_return_product))
            } else {
                if (isNetworkConnected) {
                    val refundToWallet = if (rbWallet.isChecked && settingData?.wallet_module == "1") 1 else 0
                    returnDialog.dismiss()
                    viewModel.apiReturnProduct(item, reason, refundToWallet)
                }
            }
        }
        returnDialog.show()
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun orderReturnProduct() {
        // Create the observer which updates the UI.
        val returnProduct = Observer<ProductDataBean> { resource ->
            val returnData = RatingBean()
            returnData.status = 0
            resource.return_data?.add(returnData)
            pagerAdapter?.notifyDataSetChanged()

            mBinding.root.onSnackbar(getString(R.string.return_request_msg))

//            if(resource.orderPos!=null)
//            {
//                val pos=orderHistoryBeans[resource.orderPos?:0].product?.indexOfFirst { it?.product_id==resource?.product_id }
//                if(pos!=null && pos!=-1)
//                {
//                    orderHistoryBeans[resource.orderPos?:0].product?.get(pos)?.return_data= arrayListOf(returnData)
//                }
//                pagerAdapter?.notifyItemChanged(resource?.orderPos?:0)
//            }

        }

        // Observe the LiveData, passing in this activity as the LifecycleOwner and the observer.
        viewModel.returnLiveData.observe(this, returnProduct)
    }

    private fun paymentDetail(orderHist: OrderHistory, payment_gateways: ArrayList<String>?) {

        dataManager.getKeyValue(DataNames.FEATURE_DATA, PrefenceConstants.TYPE_STRING).toString().let {
            val listType: Type = object : TypeToken<ArrayList<SettingModel.DataBean.FeatureData?>?>() {}.type
            val featureList: ArrayList<SettingModel.DataBean.FeatureData> = Gson().fromJson(it, listType)

            launchActivity<PaymentListActivity>(AppConstants.REQUEST_PAYMENT_OPTION) {
                putParcelableArrayListExtra("feature_data", featureList)
                putExtra("orderData", orderHist)
                putExtra("mSelectPayment", payment_gateways)
                putExtra("mTotalAmt", netAmt)
            }

        }
    }


    @SuppressLint("StringFormatInvalid")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == AppConstants.REQUEST_PAYMENT_OPTION && resultCode == Activity.RESULT_OK) {

            mSelectedPayment = data?.getParcelableExtra("payItem") ?: CustomPayModel()

            when {
                mSelectedPayment.keyId == DataNames.PAYMENT_CASH.toString() -> {
                    callApi(mSelectedPayment)
                }
                mSelectedPayment.payName == getString(R.string.razor_pay) -> {
//                    initRazorPay(mSelectedPayment)
                }
                mSelectedPayment.payName == getString(R.string.zelle) -> {
                    pagerAdapter?.setZelleDoc(mSelectedPayment)
                    callApi(mSelectedPayment)
                }

                mSelectedPayment.payName == getString(R.string.saded) -> {
                    getSadedPayment()
                }
                mSelectedPayment.payName == textConfig?.my_fatoorah -> {
                    initMyFatooraGateway()
                }
                mSelectedPayment.payName == getString(R.string.wallet) -> {
                    callApi(mSelectedPayment)
                }
                else -> {
                    if (mSelectedPayment.addCard == true) {
                        callApi(mSelectedPayment)
                    } else {
                        CardDialogFrag.newInstance(mSelectedPayment, netAmt).show(supportFragmentManager, "paymentDialog")
                    }
                }
            }

        } else if (resultCode == Activity.RESULT_OK && requestCode == AppConstants.RC_LOCATION_PERM) {
            getCurrentLocation()
        } else if (requestCode == SADDED_PAYMENT_REQUEST) {
            when (resultCode) {
                Activity.RESULT_OK -> {
                    mBinding.root.onSnackbar(getString(R.string.payment_done_successful))
                    callApi(mSelectedPayment)
                    // use the result to update your UI and send the payment method nonce to your server
                }
                else -> mBinding.root.onSnackbar(getString(R.string.payment_unsuccessful))

            }
        } else if (requestCode == MY_FATOORAH_PAYMENT_REQUEST) {
            when (resultCode) {
                Activity.RESULT_OK -> {
                    mBinding.root.onSnackbar(getString(R.string.payment_done_successful))
                    if (data != null && data.hasExtra("paymentId"))
                        mSelectedPayment.keyId = data.getStringExtra("paymentId")

                    callApi(mSelectedPayment)
                    // use the result to update your UI and send the payment method nonce to your server
                }
                else -> mBinding.root.onSnackbar(getString(R.string.payment_unsuccessful))
            }
        } else if (requestCode == EDIT_ORDER && resultCode == Activity.RESULT_OK) {
            if (data != null && data.hasExtra("selectedList")) {
                var quantity: Float? = 0f
                val list = data.getParcelableArrayListExtra<EditProductsItem>("selectedList")

                selectedProductsList?.clear()
                selectedProductsList?.addAll(list ?: arrayListOf())

                list?.map {
                    quantity = quantity?.plus(it.quantity ?: 0f)
                }

                tvNewAddedItems?.visibility = View.VISIBLE
                tvNewAddedItems?.text = getString(R.string.new_added_items, quantity?.toString())
                pagerAdapter?.addRemoveItem(selectedProductsList, geoFenceItem)
            }
        }
    }


    private fun callApi(mSelectedPayment: CustomPayModel) {


        val currency = dataManager.getGsonValue(PrefenceConstants.CURRENCY_INF, Currency::class.java)

        when (paymentSelec) {
            OrderPayment.ReceiptOrder.payment, OrderPayment.PaymentAfterConfirm.payment -> {
                with(this.mSelectedPayment)
                {

                    val param: MakePaymentInput? = when (mSelectedPayment.payName) {
                        textConfig?.cod ->
                            MakePaymentInput(currency = currency?.currency_name ?: "",
                                    gateway_unique_id = payment_token
                                            ?: "", languageId = dataManager.getLangCode().toInt(),
                                    order_id = orderData.order_id.toString(), payment_token = "", payment_type = DataNames.PAYMENT_CASH)

                        getString(R.string.zelle), getString(R.string.razor_pay),
                        textConfig?.my_fatoorah, getString(R.string.saded) ->
                            MakePaymentInput(card_id = "", currency = currency?.currency_name
                                    ?: "", customer_payment_id = "",
                                    gateway_unique_id = payment_token
                                            ?: "", languageId = dataManager.getLangCode().toInt(),
                                    order_id = orderData.order_id.toString(), payment_token = keyId
                                    ?: "", payment_type = DataNames.PAYMENT_CARD)


                        "paystack" ->
                            MakePaymentInput(card_id = "", currency = currency?.currency_name
                                    ?: "", customer_payment_id = "",
                                    gateway_unique_id = payment_token
                                            ?: "", languageId = dataManager.getLangCode().toInt(),
                                    order_id = orderData.order_id.toString(), payment_token = keyId
                                    ?: "", payment_type = DataNames.PAYMENT_CARD)

                        getString(R.string.wallet) -> {
                            MakePaymentInput(card_id = "", currency = currency?.currency_name
                                    ?: "", customer_payment_id = "",
                                    gateway_unique_id = "", languageId = dataManager.getLangCode().toInt(),
                                    order_id = orderData.order_id.toString(), payment_token = "", payment_type = DataNames.PAYMENT_WALLET)
                        }

                        else ->
                            MakePaymentInput(card_id = cardId
                                    ?: "", currency = currency?.currency_name
                                    ?: "", customer_payment_id = customerId ?: "",
                                    gateway_unique_id = payment_token
                                            ?: "", languageId = dataManager.getLangCode().toInt(),
                                    order_id = orderData.order_id.toString(), payment_token = keyId
                                    ?: "", payment_type = DataNames.PAYMENT_CARD)

                    }


                    param?.let { mViewModel?.makePayment(it) }
                }
            }

            OrderPayment.EditOrder.payment -> {
                with(this.mSelectedPayment)
                {
                    val param: MakePaymentInput = when (mSelectedPayment.payName) {
                        textConfig?.my_fatoorah, getString(R.string.saded) -> {
                            MakePaymentInput(card_id = "", currency = currency?.currency_name
                                    ?: "", customer_payment_id = "",
                                    gateway_unique_id = payment_token
                                            ?: "", languageId = dataManager.getLangCode().toInt(),
                                    order_id = orderData.order_id.toString(), payment_token = keyId
                                    ?: "")
                        }
                        getString(R.string.wallet) -> {
                            MakePaymentInput(card_id = "", currency = currency?.currency_name
                                    ?: "", customer_payment_id = "",
                                    gateway_unique_id = "", languageId = dataManager.getLangCode().toInt(),
                                    order_id = orderData.order_id.toString(), payment_token = "", payment_type = DataNames.PAYMENT_WALLET)
                        }

                        else -> {
                            MakePaymentInput(card_id = cardId
                                    ?: "", currency = currency?.currency_name
                                    ?: "", customer_payment_id = customerId ?: "",
                                    gateway_unique_id = payment_token
                                            ?: "", languageId = dataManager.getLangCode().toInt(),
                                    order_id = orderData.order_id.toString(), payment_token = keyId
                                    ?: "")
                        }
                    }
                    mViewModel?.remainingPayment(param)
                }
            }

        }
    }

    private fun getSadedPayment() {
        val userInfo = dataManager.getGsonValue(DataNames.USER_DATA, PojoSignUp::class.java)
        if (isNetworkConnected) {
            mViewModel?.getSaddedPaymentUrl(userInfo?.data?.email ?: "", userInfo?.data?.firstname
                    ?: "", netAmt.toString())
        }
    }

    private fun initMyFatooraGateway() {
        val currency = dataManager.getGsonValue(PrefenceConstants.CURRENCY_INF, Currency::class.java)
        if (isNetworkConnected) {
            mViewModel?.getMyFatoorahPaymentUrl(currency?.currency_name ?: "", netAmt.toString())
        }
    }

    override fun getSaddedPaymentSuccess(data: AddCardResponseData?) {
        mSelectedPayment.keyId = data?.transaction_reference
        AppToasty.success(this, "success")
        val intent = Intent(this, PaymentWebViewActivity::class.java).putExtra("paymentData", data)
        startActivityForResult(intent, SADDED_PAYMENT_REQUEST)
    }

    override fun getMyFatoorahPaymentSuccess(data: AddCardResponseData?) {
        AppToasty.success(this, "success")
        val intent = Intent(this, PaymentWebViewActivity::class.java).putExtra("paymentData", data)
                .putExtra("payment_gateway", textConfig?.my_fatoorah)
        startActivityForResult(intent, MY_FATOORAH_PAYMENT_REQUEST)
    }

//    private fun initRazorPay(mSelectedPayment: CustomPayModel?) {
//
//        val userInfo = dataManager.getGsonValue(DataNames.USER_DATA, PojoSignUp::class.java)
//
//        Checkout.preload(this)
//        val co = Checkout()
//
//        co.setKeyID(mSelectedPayment?.keyId)
//        co.setImage(R.mipmap.ic_launcher_new)
//
//        try {
//            val options = JSONObject()
//            // options.put("name", "JnJ's Cafe")
//            //  options.put("description", "Food Order")
//            //You can omit the image option to fetch the image from dashboard
//            // options.put("image", "https://cafejj-api.royoapps.com/clikat-buckettest/jnj.png")
//            options.put("currency", "INR")
//            options.put("amount", netAmt.times(100))
//            options.put("payment_capture", true)
//            // options.put("order_id", orderId)
//
//            val preFill = JSONObject()
//            preFill.put("email", userInfo?.data?.email)
//            preFill.put("contact", userInfo?.data?.mobile_no)
//
//            options.put("prefill", preFill)
//
//            co.open(this, options)
//
//        } catch (e: Exception) {
//            e.printStackTrace()
//        }
//    }

    override fun paymentToken(token: String, paymentMethod: String, savedCard: SaveCardInputModel?) {

        if (isNetworkConnected) {
            if (mSelectedPayment.payment_token == "paystack") {
                mSelectedPayment.keyId = token
            }

            callApi(mSelectedPayment)
        }
    }

    override fun androidInjector(): AndroidInjector<Any> {
        return androidInjector
    }

    override fun onPermissionsDenied(requestCode: Int, perms: MutableList<String>) {
        if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
            AppSettingsDialog.Builder(this).build().show()
        }
    }

    override fun onPermissionsGranted(requestCode: Int, perms: MutableList<String>) {
        if (requestCode == AppConstants.REQUEST_CODE_LOCATION) {
            if (CommonUtils.isNetworkConnected(applicationContext)) {
                createLocationRequest()
            }
        }
    }

//    override fun onPaymentError(p0: Int, p1: String?, p2: PaymentData?) {
//        mBinding.root.onSnackbar(p1 ?: "")
//    }
//
//    override fun onPaymentSuccess(p0: String?, paymentData: PaymentData?) {
//
//        mSelectedPayment.keyId = paymentData?.paymentId
//        mSelectedPayment.payment_token = "razorpay"
//        callApi(mSelectedPayment)
//    }

    override fun onSuccessListener() {

    }

    override fun onSucessListner() {
        appUtil.clearCart().run {
            btnReorder.callOnClick()
        }
    }


    override fun onErrorListener() {

    }

    private fun checkSupplierBranchId(product: ProductDataBean?): Int {
        /*return if (product?.supplier_branch_id ?: 0 > 0) {
            product?.supplier_branch_id ?: 0
        } else {*/
        return product?.supplier_id ?: 0
        //  }
    }

    private fun checkSupplierBranchName(product: ProductDataBean?): String {

        return if (product?.branch_name ?: "".isNotEmpty() == true) {
            product?.branch_name ?: ""
        } else {
            product?.supplier_name ?: ""
        }
    }

    private var selectedProductsList: ArrayList<EditProductsItem>? = arrayListOf()
    private fun addListInSelectedItem(): ArrayList<String?> {
        val removalItems = ArrayList<String?>()
        if (::orderDetail.isInitialized) {
            orderDetail.product?.forEach {
                if (it?.quantity != 0f) {
                    val item = EditProductsItem()
                    item.productId = it?.product_id
                    item.imagePath = it?.image_path?.toString()
                    item.productDesc = it?.product_desc
                    item.quantity = it?.quantity
                    item.price = it?.price?.toFloatOrNull()
                    item.productName = it?.product_name
                    item.handling_admin = it?.handling_admin?.toDouble()
                    item.orderPriceId = it?.order_price_id
                    item.branchId = orderDetail.supplier_branch_id
                    selectedProductsList?.add(item)
                } else {
                    removalItems.add(it.order_price_id ?: "")
                }
            }
        }
        return removalItems
    }

    private fun editOrder(removalItems: ArrayList<String?>?) {
        if (::orderDetail.isInitialized) {
            val orderRequest = EditOrderRequest()
            orderRequest.orderId = orderDetail.order_id.toString()
            orderRequest.items = selectedProductsList
            getHandlingAdminCharges(orderRequest)
            /*any number need to remove from backend*/
            orderRequest.sectionId = 0
            orderRequest.removalItems = removalItems
            if (isNetworkConnected)
                mViewModel?.editOrderProducts(orderRequest)
        }
    }

    private fun getHandlingAdminCharges(orderRequest: EditOrderRequest) {
        var handlingAdmin: Double? = 0.0
        var totalPrice: Float? = 0f
        selectedProductsList?.forEach {
            val taxCharges =
                    geoFenceItem?.taxData?.firstOrNull()?.tax?.toDouble() ?: (it.handling_admin
                            ?: 0.0)
            handlingAdmin = handlingAdmin?.plus(
                    ((it.quantity?.toDouble() ?: 1.0).times(it.price?.toDouble()
                            ?: 0.0).times(taxCharges.div(100)))
            )
            it.handling_admin = null
            totalPrice = totalPrice?.plus((it.price ?: 0f) * (it.quantity ?: 0f))
        }
        orderRequest.handlingAdmin = handlingAdmin
      //  orderRequest.handling_supplier = orderDetail.handling_supplier
        orderRequest.userServiceCharge =
                ((totalPrice ?: 0f) * (orderDetail.user_service_charge?.toFloat() ?: 0f)).div(100)
    }


}
