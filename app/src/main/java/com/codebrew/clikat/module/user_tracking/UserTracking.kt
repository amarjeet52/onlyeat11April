package com.codebrew.clikat.module.user_tracking


import android.Manifest
import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.util.DisplayMetrics
import android.view.View
import android.view.animation.LinearInterpolator
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.codebrew.clikat.BR
import com.codebrew.clikat.BuildConfig
import com.codebrew.clikat.R
import com.codebrew.clikat.app_utils.AppUtils
import com.codebrew.clikat.app_utils.CommonUtils
import com.codebrew.clikat.app_utils.PermissionFile
import com.codebrew.clikat.app_utils.extension.loadImage
import com.codebrew.clikat.app_utils.extension.onSnackbar
import com.codebrew.clikat.base.BaseActivity
import com.codebrew.clikat.data.AppDataType
import com.codebrew.clikat.data.DataManager
import com.codebrew.clikat.data.OrderStatus
import com.codebrew.clikat.data.constants.AppConstants
import com.codebrew.clikat.data.constants.PrefenceConstants
import com.codebrew.clikat.data.model.api.*
import com.codebrew.clikat.data.model.api.Currency
import com.codebrew.clikat.data.model.api.orderDetail.Data
import com.codebrew.clikat.data.model.api.orderDetail.OrderHistory
import com.codebrew.clikat.data.model.others.OrderEvent
import com.codebrew.clikat.data.model.others.OrderStatusModel
import com.codebrew.clikat.data.network.RestService
import com.codebrew.clikat.data.preferences.PreferenceHelper
import com.codebrew.clikat.databinding.FragmentUserTrackingBinding
import com.codebrew.clikat.di.ViewModelProviderFactory
import com.codebrew.clikat.modal.AppGlobal.Companion.localeManager
import com.codebrew.clikat.modal.DataZoom
import com.codebrew.clikat.modal.other.AddtoCartModel
import com.codebrew.clikat.modal.other.SettingModel
import com.codebrew.clikat.modal.socket.SocketResponseModel
import com.codebrew.clikat.module.help.HelpActivity
import com.codebrew.clikat.module.order_detail.OrderDetailNavigator
import com.codebrew.clikat.module.order_detail.OrderDetailViewModel
import com.codebrew.clikat.module.order_detail_new.adapter.StatusOrderAdapter
import com.codebrew.clikat.preferences.DataNames
import com.codebrew.clikat.utils.GeneralFunctions
import com.codebrew.clikat.utils.ProgressBarDialog
import com.codebrew.clikat.utils.Utils
import com.codebrew.clikat.utils.configurations.Configurations
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.google.android.material.appbar.AppBarLayout
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import dagger.android.AndroidInjection
import io.socket.client.IO
import io.socket.client.Socket
import io.socket.emitter.Emitter
import kotlinx.android.synthetic.main.content_scrolling.*
import kotlinx.android.synthetic.main.fragment_user_tracking.*
import okhttp3.ResponseBody
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import pub.devrel.easypermissions.AppSettingsDialog
import pub.devrel.easypermissions.EasyPermissions
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.IOException
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.text.ParseException
import java.util.*
import javax.inject.Inject
import kotlin.collections.ArrayList


/**
 * A simple [Fragment] subclass.
 */
class UserTracking : BaseActivity<FragmentUserTrackingBinding, OrderDetailViewModel>(),
    OrderDetailNavigator, OnMapReadyCallback, EasyPermissions.PermissionCallbacks {


    private var mMap: GoogleMap? = null
    private var v: Float = 0.toFloat()
    private var list = ArrayList<LatLng>()
    private var sourceLatLong: LatLng? = null
    private var destLatlng: LatLng? = null
    private var restLatlng: LatLng? = null
    private val orderId = ArrayList<Int>()


    var carMarker: Marker? = null
    var restMarker: Marker? = null
    var destMarker: Marker? = null

    private var line: Polyline? = null

    var valueAnimatorMove: ValueAnimator? = null

    var barDialog: ProgressBarDialog? = null

    private var mViewModel: OrderDetailViewModel? = null

    @Inject
    lateinit var factory: ViewModelProviderFactory
    @Inject
    lateinit var dataManager: DataManager
    @Inject
    lateinit var prefHelper: PreferenceHelper
    private val listTextHead = Color.parseColor(Configurations.colors.textListHead)

    @Inject
    lateinit var appUtils: AppUtils

    @Inject
    lateinit var permissionUtil: PermissionFile


    lateinit var binding: FragmentUserTrackingBinding

    private val textConfig by lazy { appUtils.loadAppConfig(0).strings }

    private var screenFlowBean: SettingModel.DataBean.ScreenFlowBean? = null


    private var mSocket: Socket? = null

    private var orderDetail: OrderHistory? = null
    private var selectedCurrency: Currency? = null
    private val decimalFormat: DecimalFormat =
        DecimalFormat("0.00", DecimalFormatSymbols.getInstance(Locale.ENGLISH))
    private var settingBean: SettingModel.DataBean.SettingData? = null
    private var mapFragment: SupportMapFragment? = null
    private var phoneNumber: String? = null
    private var contact_us: String? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        super.onCreate(savedInstanceState)

        binding = viewDataBinding
        binding.color = Configurations.colors
        binding.strings = textConfig
        Glide.with(this).asGif().load(R.raw.loading_gif).into(binding!!.imageLoader);
        mViewModel?.navigator = this

        EventBus.getDefault().register(this)

        resizeAppbarHeight()

        settingToolbar()

        mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment

        barDialog = ProgressBarDialog(this)

        selectedCurrency =
            prefHelper.getGsonValue(PrefenceConstants.CURRENCY_INF, Currency::class.java)
        settingBean = prefHelper.getGsonValue(
            DataNames.SETTING_DATA,
            SettingModel.DataBean.SettingData::class.java
        )
        screenFlowBean = prefHelper.getGsonValue(
            DataNames.SCREEN_FLOW,
            SettingModel.DataBean.ScreenFlowBean::class.java
        )
        binding!!.tvHelp.setOnClickListener {
            if (dataManager.getCurrentUserLoggedIn()) {

                var intent= Intent(this, HelpActivity::class.java)
                startActivity(intent)
            } else {
                appUtils.checkLoginFlow(this, DataNames.REQUEST_RESTAURANT_LOGIN)
            }
        }
        if (intent.hasExtra("orderId")) {
            orderId.addAll(intent.getIntegerArrayListExtra("orderId") ?: arrayListOf())
        }


        val userId =
            prefHelper.getKeyValue(PrefenceConstants.USER_ID, PrefenceConstants.TYPE_STRING)
                .toString()


        mSocket = IO.socket(
            BuildConfig.BASE_URL.plus(
                "?id=$userId&secretdbkey=" + prefHelper.getKeyValue(
                    PrefenceConstants.DB_SECRET,
                    PrefenceConstants.TYPE_STRING
                )!!.toString()
            )
        )


        orderDetailObserver()


        val params = app_bar.layoutParams as CoordinatorLayout.LayoutParams
        val behavior = AppBarLayout.Behavior()
        behavior.setDragCallback(object : AppBarLayout.Behavior.DragCallback() {
            override fun canDrag(appBarLayout: AppBarLayout): Boolean {
                return false
            }
        })
        params.behavior = behavior
    }

    private fun settingToolbar() {
        tvTitle?.text = getString(R.string.order_details, textConfig?.order)

        setSupportActionBar(toolbar)


        val drawable = ContextCompat.getDrawable(this, R.drawable.backnew)
        val bitmap = (drawable as BitmapDrawable).bitmap
        val newdrawable: Drawable =
            BitmapDrawable(resources, Bitmap.createScaledBitmap(bitmap, 70, 70, true))
        supportActionBar!!.setHomeAsUpIndicator(newdrawable)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowTitleEnabled(false)
    }

    private fun resizeAppbarHeight() {

        val displayMetrics = DisplayMetrics()
        (this as Activity)
            .windowManager
            .defaultDisplay
            .getMetrics(displayMetrics)
        val windowHeight = displayMetrics.heightPixels

        val addonHeight = windowHeight * 3 / 5

        app_bar.layoutParams?.height = addonHeight

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onMessageEvent(event: OrderEvent) {
        if (event.type == AppConstants.NOTIFICATION_EVENT) {
            if (isNetworkConnected) {
                viewModel.getOrderDetail(orderId)
            }
        }
    }

    private fun showBottomSheet() {

        handleBottomSheet(orderDetail)
    }

    @SuppressLint("SetTextI18n")
    private fun handleBottomSheet(orderDetail: OrderHistory?) {
        groupCar.visibility = View.GONE
        try {
            if (orderDetail?.car_details == null) {

            } else {
                groupCar.visibility = View.VISIBLE
                tvcarName.text = orderDetail?.car_details.name.toString()
                tvcarcolor.text = orderDetail?.car_details.color.toString()
                if (orderDetail?.car_details.mobile.toString() == null) {
                    tvCarModel.text = orderDetail?.car_details.number_plate.toString()
                } else {
                    tvCarModel.text =
                        orderDetail?.car_details.number_plate.toString() + "\n" + orderDetail?.car_details.mobile.toString()
                            ?: ""

                }

            }
        } catch (E: Exception) {

        }
        tvDeliveryPlace?.text =
            "${orderDetail?.delivery_address?.customer_address ?: ""} ,${orderDetail?.delivery_address?.address_line_1 ?: ""}"
        tvSupplierName?.text = orderDetail?.supplier_name
        tvSupplierAddress?.text = orderDetail?.branch_address
        var dd1 = ""
        if (orderDetail?.is_schedule.toString().equals("1")) {
            dd1 = orderDetail?.schedule_end_date?.replace("T", " ").toString()
        } else {
            dd1 = orderDetail?.service_date?.replace("T", " ").toString()
        }

        dd1 = dd1?.replace("Z", "")
        var deliveryDate1 = SpannableString.valueOf("")
        try {
            deliveryDate1 = setColor(
                getString(R.string.expected_delivered_time) + "\n"
                        + GeneralFunctions.getFormattedDateSide(dd1)
            )
        } catch (e: ParseException) {
            e.printStackTrace()
        }
        tvDeliveryTime?.text = deliveryDate1

        var dd = orderDetail?.created_on?.replace("T", " ")
        dd = dd?.replace("Z", "")
        var deliveryDate = SpannableString.valueOf("")
        try {
            deliveryDate = setColor(
                getString(R.string.placed_on) + "\n"
                        + GeneralFunctions.getFormattedDateSide(dd)
            )
        } catch (e: ParseException) {
            e.printStackTrace()
        }
        tvDelTime?.text = deliveryDate

        val items = StringBuilder()
        orderDetail?.product?.map {
            items.append("${it?.quantity}*${it?.name}")
            items.append("\n")
        }
        when (orderDetail?.status) {

            OrderStatus.Delivered.orderStatus -> {
                tvMessage.visibility = View.GONE
            }
            OrderStatus.Customer_Canceled.orderStatus -> {
                tvMessage.visibility = View.GONE
            }
            OrderStatus.Admin_Canceled.orderStatus -> {
                tvMessage.visibility = View.GONE
            }
            OrderStatus.Rejected.orderStatus -> {
                tvMessage.visibility = View.GONE
            }
        }
        ivCustomerSupport?.setOnClickListener {
            contact_us = "44072888"
            val intent = Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", contact_us, null))
            val messagesPermission2 = Manifest.permission.CALL_PHONE
            val hasaccesslocation2 = checkSelfPermission(messagesPermission2)
            if (hasaccesslocation2 != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(
                    arrayOf(
                        Manifest.permission.CALL_PHONE
                    ), 1
                )
            } else {
                Manifest.permission.CALL_PHONE
                startActivity(intent)
            }
            if (permissionUtil.hasCallPermissions(this)) {
                contact_us = "44072888"
                callPhone(contact_us ?: "")
            } else {
                permissionUtil.phoneCallTask(this)
            }
        }
        clSupplier?.setOnClickListener {

            val url =
                "https://www.google.com/maps/dir/?api=1&destination=" + orderDetail?.product?.get(0)?.latitude.toString() + "," + orderDetail?.product?.get(
                    0
                )?.longitude.toString() + "&travelmode=driving"
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
            startActivity(intent)


//            val gmmIntentUri = Uri.parse("google.streetview:cbll=" +orderDetail?.product?.get(0)?.latitude+","+orderDetail?.product?.get(0)?.longitude)
//
//            val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
//
//            mapIntent.setPackage("com.google.android.apps.maps")
//
//            startActivity(mapIntent)
        }


        tvMessage?.setOnClickListener {
            phoneNumber = orderDetail?.mobile_1
            val intent = Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", phoneNumber, null))
            val messagesPermission2 = Manifest.permission.CALL_PHONE
            val hasaccesslocation2 = checkSelfPermission(messagesPermission2)
            if (hasaccesslocation2 != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(
                    arrayOf(
                        Manifest.permission.CALL_PHONE
                    ), 1
                )
            } else {
                Manifest.permission.CALL_PHONE
                startActivity(intent)
            }
            if (permissionUtil.hasCallPermissions(this)) {
                phoneNumber = orderDetail?.mobile_1
                callPhone(phoneNumber ?: "")
            } else {
                permissionUtil.phoneCallTask(this)
            }
        }

        tvOrderPlaceHeader?.text = items.toString()
        ivSupplierImage?.loadImage(orderDetail?.logo ?: "")

        orderDetail?.agent?.let { agents ->
            tvDriverName?.text = agents.firstOrNull()?.name
            gpDriver?.visibility = View.VISIBLE

            tvDriverName?.setOnClickListener {
                if (tvDriverName?.text.toString().equals(getString(R.string.driver_not_assigned))) {

                } else {
                    if (permissionUtil.hasCallPermissions(this)) {
                        phoneNumber = agents.firstOrNull()?.phone_number ?: ""
                        callPhone(phoneNumber ?: "")
                    } else {
                        permissionUtil.phoneCallTask(this)
                    }
                }
            }

            ivCall.setOnClickListener {
                tvDriverName.performClick()
            }
        }

        if (orderDetail?.agent.isNullOrEmpty())
            tvDriverName?.text = getString(R.string.driver_not_assigned)

        tvOrderId?.text = orderDetail?.order_id?.toString()

        tvOrderAmount?.text = getString(
            R.string.currency_tag, AppConstants.CURRENCY_SYMBOL, Utils.getPriceFormat(
                decimalFormat.format(orderDetail?.net_amount)?.toFloatOrNull()
                    ?: 0f, settingBean, selectedCurrency
            )
        )

        tvPaymentMode?.text = if (orderDetail?.payment_type == DataNames.SKIP_PAYMENT)
            getString(R.string.out_of_app)
        else if (orderDetail?.payment_type == 3 && orderDetail.payment_status == 0) {
            "None"
        } else if (DataNames.DELIVERY_WALLET == orderDetail?.payment_type) {
            getString(R.string.wallet)
        } else if (DataNames.DELIVERY_CASH == orderDetail?.payment_type) {
            getString(R.string.cash)
        } else if (orderDetail?.payment_type == DataNames.DELIVERY_CARD) {
            getString(R.string.online_pay_tag, orderDetail?.payment_source)
        } else {
            when (orderDetail?.payment_source) {
                "zelle" -> {
                    getString(R.string.zelle)
                }
                "paypal" -> {
                    getString(R.string.pay_pal)
                }
                else -> {
                    getString(R.string.online_payment)
                }
            }
        }

        val mStatusList = mutableListOf<OrderStatusModel>()

        when (1) {
            AppDataType.Food.type -> {
                mStatusList.add(
                    OrderStatusModel(
                        convertDate(
                            orderDetail?.confirmed_on
                                ?: "", orderDetail?.status, OrderStatus.Approved.orderStatus
                        ), OrderStatus.Approved, orderDetail?.status
                            ?: 0.0
                    )
                )
                mStatusList.add(
                    OrderStatusModel(
                        convertDate(
                            orderDetail?.progress_on
                                ?: "", orderDetail?.status, OrderStatus.In_Kitchen.orderStatus
                        ), OrderStatus.In_Kitchen, orderDetail?.status
                            ?: 0.0
                    )
                )
                if (orderDetail?.self_pickup == 0) {
                    mStatusList.add(
                        OrderStatusModel(
                            convertDate(
                                orderDetail.shipped_on
                                    ?: "", orderDetail?.status, OrderStatus.On_The_Way.orderStatus
                            ), OrderStatus.On_The_Way, orderDetail.status
                                ?: 0.0
                        )
                    )
                } else {
                    mStatusList.add(
                        OrderStatusModel(
                            convertDate(
                                orderDetail?.shipped_on
                                    ?: "",
                                orderDetail?.status,
                                OrderStatus.Ready_to_be_picked.orderStatus
                            ), OrderStatus.Ready_to_be_picked, orderDetail?.status
                                ?: 0.0
                        )
                    )
                }
                if (orderDetail?.self_pickup == 0) {
                    mStatusList.add(
                        OrderStatusModel(
                            convertDate(
                                checkDeliverDate(orderDetail),
                                orderDetail.status,
                                OrderStatus.Delivered.orderStatus
                            ), OrderStatus.Delivered, orderDetail.status
                                ?: 0.0
                        )
                    )
                }
            }

            AppDataType.Ecom.type -> {
                mStatusList.add(
                    OrderStatusModel(
                        convertDate(
                            orderDetail?.confirmed_on
                                ?: "", orderDetail?.status, OrderStatus.Confirmed.orderStatus
                        ), OrderStatus.Confirmed, orderDetail?.status
                            ?: 0.0
                    )
                )
                mStatusList.add(
                    OrderStatusModel(
                        convertDate(
                            orderDetail?.progress_on
                                ?: "", orderDetail?.status, OrderStatus.Packed.orderStatus
                        ), OrderStatus.Packed, orderDetail?.status
                            ?: 0.0
                    )
                )
                mStatusList.add(
                    OrderStatusModel(
                        convertDate(
                            orderDetail?.near_on
                                ?: "", orderDetail?.status, OrderStatus.Shipped.orderStatus
                        ), OrderStatus.Shipped, orderDetail?.status
                            ?: 0.0
                    )
                )
                mStatusList.add(
                    OrderStatusModel(
                        convertDate(
                            orderDetail?.shipped_on
                                ?: "", orderDetail?.status, OrderStatus.On_The_Way.orderStatus
                        ), OrderStatus.On_The_Way, orderDetail?.status
                            ?: 0.0
                    )
                )
                mStatusList.add(
                    OrderStatusModel(
                        convertDate(
                            checkDeliverDate(orderDetail),
                            orderDetail?.status,
                            OrderStatus.Delivered.orderStatus
                        ), OrderStatus.Delivered, orderDetail?.status
                            ?: 0.0
                    )
                )
            }

            AppDataType.HomeServ.type -> {
                mStatusList.add(
                    OrderStatusModel(
                        convertDate(
                            orderDetail?.confirmed_on
                                ?: "", orderDetail?.status, OrderStatus.Confirmed.orderStatus
                        ), OrderStatus.Confirmed, orderDetail?.status
                            ?: 0.0
                    )
                )

                mStatusList.add(
                    OrderStatusModel(
                        convertDate(
                            orderDetail?.progress_on
                                ?: "", orderDetail?.status, OrderStatus.In_Kitchen.orderStatus
                        ), OrderStatus.In_Kitchen, orderDetail?.status
                            ?: 0.0
                    )
                )

                mStatusList.add(
                    OrderStatusModel(
                        convertDate(
                            orderDetail?.near_on
                                ?: "", orderDetail?.status, OrderStatus.Reached.orderStatus
                        ), OrderStatus.Reached, orderDetail?.status
                            ?: 0.0
                    )
                )

                mStatusList.add(
                    OrderStatusModel(
                        convertDate(
                            orderDetail?.shipped_on
                                ?: "", orderDetail?.status, OrderStatus.Started.orderStatus
                        ), OrderStatus.Started, orderDetail?.status
                            ?: 0.0
                    )
                )

                mStatusList.add(
                    OrderStatusModel(
                        convertDate(
                            checkDeliverDate(orderDetail),
                            orderDetail?.status,
                            OrderStatus.Ended.orderStatus
                        ), OrderStatus.Ended, orderDetail?.status
                            ?: 0.0
                    )
                )

            }
        }

        val mStatusPos = mStatusList.indexOfFirst {
            it.status.orderStatus == orderDetail?.status
        }

        rvStatus?.layoutManager = LinearLayoutManager(this, RecyclerView.HORIZONTAL, false)
        rvStatus?.adapter = StatusOrderAdapter(
            mStatusList, orderDetail?.type, orderDetail?.self_pickup, orderDetail?.terminology
                ?: ""
        )

        rvStatus?.layoutManager?.scrollToPosition(mStatusPos)


        val status = orderDetail?.status ?: 0.0


        val cancelHidden = status != OrderStatus.Pending.orderStatus

        tvCancelOrder?.visibility = if (!cancelHidden) {
            View.VISIBLE
        } else {
            View.GONE
        }

        tvCancelOrder?.setOnClickListener {
            if (orderDetail == null) return@setOnClickListener
            if (orderDetail?.payment_type == DataNames.DELIVERY_CARD && settingBean?.wallet_module == "1") {
                cancelOrder(orderDetail?.order_id.toString(), 1)
            } else {
                if (orderDetail?.status == OrderStatus.Scheduled.orderStatus) sweetDialog() else sweetDialog()
            }
        }

        if (orderDetail?.self_pickup == 1) {
            groupAddress?.visibility = View.GONE
            groupDriver?.visibility = View.GONE
            gpDriver?.visibility = View.GONE
            groupCar?.visibility = View.VISIBLE
        }
    }

    private fun setColor(string: String): SpannableString {
        val newString = SpannableString(string)
        val index = string.indexOf("\n") + 1
        newString.setSpan(
            ForegroundColorSpan(listTextHead), index, string.length,
            0
        )
        return newString
    }

    @SuppressLint("StringFormatInvalid")
    private fun sweetDialog() {

        val sweetAlertDialog = AlertDialog.Builder(this)
        sweetAlertDialog.setTitle(getString(R.string.cancel_order, textConfig?.order))
        sweetAlertDialog.setMessage(getString(R.string.doYouCancel, textConfig?.order))

        sweetAlertDialog.setPositiveButton(getString(R.string.ok)) { dialog, which ->
            if (CommonUtils.isNetworkConnected(this)) {
                val cancelWallet = if (settingBean?.wallet_module == "1") 1 else 0
                cancelOrder(orderDetail?.order_id.toString(), cancelWallet)
            }
            dialog.dismiss()
        }

        sweetAlertDialog.setNegativeButton(getString(R.string.cancel)) { dialog, which ->
            dialog.dismiss()
        }
        sweetAlertDialog.show()
    }

    override fun onResume() {
        super.onResume()
        if (isNetworkConnected) {
            viewModel.getOrderDetail(orderId)
        }
    }

    private fun orderDetailObserver() {
        // Create the observer which updates the UI.
        val catObserver = Observer<List<OrderHistory>> { resource ->

            resource?.firstOrNull()?.let {
                orderDetail = it

                mapFragment?.getMapAsync(this)
                showBottomSheet()

            }

        }
        // Observe the LiveData, passing in this activity as the LifecycleOwner and the observer.
        viewModel.orderDetailLiveData.observe(this, catObserver)
    }

    private fun settingData(orderDetail: OrderHistory?) {


        if (orderDetail?.delivery_address != null) {
            if (orderDetail.agent?.isNotEmpty() == true && orderDetail?.agent?.firstOrNull()?.latitude != null && (orderDetail?.status ?: 0.0 == OrderStatus.On_The_Way.orderStatus || orderDetail?.status ?: 0.0 == OrderStatus.Near_You.orderStatus)) {
                sourceLatLong = LatLng(
                    orderDetail?.agent?.firstOrNull()?.latitude ?: 0.0,
                    orderDetail?.agent?.firstOrNull()?.longitude
                        ?: 0.0
                )
            }

            restLatlng = if (orderDetail?.from_latitude != null) {
                LatLng(
                    orderDetail.from_latitude ?: 0.0, orderDetail.from_longitude
                        ?: 0.0
                )
            } else {
                null
            }

            destLatlng =
                if (orderDetail.status != OrderStatus.Delivered.orderStatus) {
                    LatLng(
                        orderDetail.to_latitude ?: 0.0, orderDetail.to_longitude
                            ?: 0.0
                    )
                } else {
                    null
                }

            showMarker(sourceLatLong, destLatlng, restLatlng)


        }

    }

    fun cancelOrder(orderId: String, cancelToWallet: Int) {

        viewModel.cancelOrder(orderId, cancelToWallet)

    }


    private fun currentLocation(lat: Double, lng: Double) {

        if (mMap == null) return

        destLatlng = LatLng(lat, lng)
        if (destLatlng?.latitude != null && destLatlng?.longitude != null) {
            val camPos = CameraPosition
                .builder(mMap?.cameraPosition) // current Camera
                .zoom(15f)
                .target(destLatlng ?: LatLng(0.0, 0.0))
                .build()
            mMap?.animateCamera(CameraUpdateFactory.newCameraPosition(camPos))
        }
    }

    private fun checkProductDetail() {

        mSocket?.on(SOCKET_EVENT, onNewMessage)
        mSocket?.connect()
    }


    private val onNewMessage = Emitter.Listener { args ->
        this@UserTracking.runOnUiThread {
            //3.0 on the way
            //4.0 near you

            if (orderDetail?.status == OrderStatus.Delivered.orderStatus) return@runOnUiThread

            val response = Gson().fromJson<SocketResponseModel>(
                args[0].toString(),
                object : TypeToken<SocketResponseModel>() {}.type
            )

            sourceLatLong =
                if (orderDetail?.status ?: 0.0 == OrderStatus.On_The_Way.orderStatus || orderDetail?.status ?: 0.0 == OrderStatus.Near_You.orderStatus) {
                    LatLng(response?.latitude ?: 0.0, response?.longitude ?: 0.0)
                } else {
                    null
                }


            showMarker(sourceLatLong, destLatlng, restLatlng)
            driverFocusMapCamera()
        }
    }


    override fun onDestroy() {
        super.onDestroy()

        EventBus.getDefault().unregister(this)

        mSocket?.disconnect()
        mSocket?.off(SOCKET_EVENT, onNewMessage)
    }


    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        if (orderDetail != null) {
            settingData(orderDetail)
        }

        checkProductDetail()
    }


    /**
     * Bearing between two LatLng pair
     *
     * @param begin First LatLng Pair
     * @param end Second LatLng Pair
     * @return The bearing or the angle at which the marker should rotate for going to `end` LAtLng.
     */
    private fun getBearing(begin: LatLng, end: LatLng): Float {
        val lat = Math.abs(begin.latitude - end.latitude)
        val lng = Math.abs(begin.longitude - end.longitude)

        if (begin.latitude < end.latitude && begin.longitude < end.longitude)
            return Math.toDegrees(Math.atan(lng / lat)).toFloat()
        else if (begin.latitude >= end.latitude && begin.longitude < end.longitude)
            return (90 - Math.toDegrees(Math.atan(lng / lat)) + 90).toFloat()
        else if (begin.latitude >= end.latitude && begin.longitude >= end.longitude)
            return (Math.toDegrees(Math.atan(lng / lat)) + 180).toFloat()
        else if (begin.latitude < end.latitude && begin.longitude >= end.longitude)
            return (90 - Math.toDegrees(Math.atan(lng / lat)) + 270).toFloat()
        return -1f
    }

    companion object {

        private val SOCKET_EVENT = "order_location"
    }


    fun drawPolyLine(
        sourceLat: Double,
        sourceLong: Double,
        destLat: Double,
        destLong: Double,
        language: String?
    ) {


        val BASE_URL_for_map = "https://maps.googleapis.com/maps/api/"
        val retrofit = Retrofit.Builder().baseUrl(BASE_URL_for_map)
            .addConverterFactory(GsonConverterFactory.create()).build()
        val api = retrofit.create(RestService::class.java)
        val service = api.getPolYLine(
            sourceLat.toString() + "," + sourceLong,
            destLat.toString() + "," + destLong,
            language,
            appUtils.getMapKey()
        )
        service.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.isSuccessful) {
                    try {
                        val responsePolyline = response.body()?.string()
                        val jsonRootObject = JSONObject(responsePolyline)
                        polyLine(jsonRootObject)
                    } catch (e: IOException) {
                        e.printStackTrace()

                    } catch (e: JSONException) {
                        e.printStackTrace()
                    }
                } else {
                    // handleApiError(errorModel.statusCode, errorModel.msg)
                }
            }

            override fun onFailure(call: Call<ResponseBody>?, t: Throwable?) {
                // getView()?.showLoader(false)
                //  getView()?.apiFailure()
            }
        })
    }

    fun handleApiError(code: Int?, error: String?) {


    }


    fun polyLine(jsonRootObject: JSONObject) {
        //  line?.remove()
        val routeArray = jsonRootObject.getJSONArray("routes")
        if (routeArray.length() == 0) {
            return
        }
        val routes: JSONObject?
        routes = routeArray.getJSONObject(0)
        val overviewPolylines = routes.getJSONObject("overview_polyline")
        val encodedString = overviewPolylines.getString("points")
        // pathArray.add(encodedString)

        line?.remove()
        list.clear()
        list.addAll(decodePoly(encodedString))



        //   val listSize = list.size
        //   sourceLatLong?.let { list.add(0, it) }
        //destLong?.let { list.add(listSize + 1, it) }
        line = mMap?.addPolyline(
            PolylineOptions()
                .addAll(list)
                .width(8f)
                .color(ContextCompat.getColor(this, R.color.text_dark))
                .geodesic(true)
        )

    }


    private fun decodePoly(encoded: String): java.util.ArrayList<LatLng> {

        val poly = java.util.ArrayList<LatLng>()
        var index = 0
        val len = encoded.length
        var lat = 0
        var lng = 0
        while (index < len) {
            var b: Int
            var shift = 0
            var result = 0
            do {
                b = encoded[index++].toInt() - 63
                result = result or (b and 0x1f shl shift)
                shift += 5
            } while (b >= 0x20)
            val dlat = if (result and 1 != 0) (result shr 1).inv() else result shr 1
            lat += dlat
            shift = 0
            result = 0
            do {
                b = encoded[index++].toInt() - 63
                result = result or (b and 0x1f shl shift)
                shift += 5
            } while (b >= 0x20)
            val dlng = if (result and 1 != 0) (result shr 1).inv() else result shr 1
            lng += dlng
            val p = LatLng(
                lat.toDouble() / 1E5,
                lng.toDouble() / 1E5
            )
            poly.add(p)
        }
        return poly
    }


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this)
    }

    override fun onPermissionsGranted(requestCode: Int, perms: MutableList<String>) {
        if (requestCode == AppConstants.REQUEST_CALL) {
            callPhone(phoneNumber ?: "")
        }
    }

    override fun onPermissionsDenied(requestCode: Int, perms: MutableList<String>) {
        if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
            AppSettingsDialog.Builder(this).build().show()
        }
    }


    private fun showMarker(sourceLatLong: LatLng?, destLong: LatLng?, restLatlng: LatLng?) {

        if (orderDetail?.status == OrderStatus.Admin_Canceled.orderStatus ||
            orderDetail?.status == OrderStatus.Delivered.orderStatus || orderDetail?.status == OrderStatus.Rating_Given.orderStatus || orderDetail?.status == OrderStatus.Pending.orderStatus
        ) {
            reFocusMapCamera()
            return
        } else if (orderDetail?.status == OrderStatus.Confirmed.orderStatus || orderDetail?.status == OrderStatus.In_Kitchen.orderStatus || orderDetail?.status == OrderStatus.In_Kitchen_CRAVE.orderStatus) {
            drawMarker(sourceLatLong, destLong, restLatlng)
            return
        }

        drawMarker(sourceLatLong, destLong, restLatlng)


        sourceLatLong?.latitude.let {
            sourceLatLong?.longitude.let { it1 ->
                destLong?.latitude.let { it2 ->
                    drawPolyLine(
                        sourceLat = it ?: 0.0,
                        sourceLong = it1 ?: 0.0,
                        destLat = it2 ?: 0.0,
                        destLong = destLong?.longitude ?: 0.0,
                        language = Locale.US.language
                    )
                }
            }
        }
    }

    private fun drawMarker(sourceLatLong: LatLng?, destLong: LatLng?, restLatlng: LatLng?) {
        if (destMarker != null) {
            destMarker?.remove()
        }

        if (carMarker != null) {
            carMarker?.remove()
        }

        if (restMarker != null) {
            restMarker?.remove()
        }

        if (restLatlng != null && arrayListOf(
                OrderStatus.Approved.orderStatus,
                OrderStatus.Pending.orderStatus,
                OrderStatus.In_Kitchen.orderStatus,
                OrderStatus.In_Kitchen_CRAVE.orderStatus,
                OrderStatus.READY_TO_BE_PICKED.orderStatus
            ).contains(orderDetail?.status ?: 0.0)
        ) {
            restMarker = mMap?.addMarker(
                MarkerOptions()
                    .position(restLatlng)
                    .icon(BitmapDescriptorFactory.fromBitmap(createBitmap(R.drawable.restaurantc)))
            )
        }

        if (sourceLatLong != null && (orderDetail?.status ?: 0.0 == OrderStatus.On_The_Way.orderStatus || orderDetail?.status ?: 0.0 == OrderStatus.Near_You.orderStatus)) {
            carMarker = mMap?.addMarker(
                MarkerOptions()
                    .position(sourceLatLong)
                    .icon(BitmapDescriptorFactory.fromBitmap(createBitmap(R.drawable.driver_c)))
            )
        }

        if (destLong != null) {
            destMarker = mMap?.addMarker(destLong.let { MarkerOptions().position(it) })
            destMarker?.setIcon((BitmapDescriptorFactory.fromBitmap(createBitmap(R.drawable.homec))))
        }


        val builder = LatLngBounds.Builder()

        carMarker?.let {
            builder.include(it?.position)
        }


        destMarker?.let {
            builder.include(it?.position)
        }

        restMarker?.let {
            builder.include(it?.position)
        }


        val cu = CameraUpdateFactory.newLatLngBounds(builder.build(), 50)

        mMap?.moveCamera(cu)
        mMap?.animateCamera(cu)
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private fun createBitmap(drawable: Int): Bitmap {
        val height = 150
        val width = 150
        val bitmapdraw = resources.getDrawable(drawable) as BitmapDrawable
        val b = bitmapdraw.bitmap
        return Bitmap.createScaledBitmap(b, width, height, false)
    }


    private fun reFocusMapCamera() {
        if (destLatlng == null) return

        val camPos = CameraPosition
            .builder(mMap?.cameraPosition) // current Camera
            .zoom(15f)
            .target(destLatlng)
            .build()
        mMap?.animateCamera(CameraUpdateFactory.newCameraPosition(camPos))

    }

    private fun driverFocusMapCamera() {
        if (sourceLatLong != null && (orderDetail?.status ?: 0.0 == OrderStatus.On_The_Way.orderStatus || orderDetail?.status ?: 0.0 == OrderStatus.Near_You.orderStatus)) {
            val camPos = CameraPosition
                .builder(mMap?.cameraPosition) // current Camera
                .zoom(15f)
                .target(sourceLatLong)
                .build()
            mMap?.animateCamera(CameraUpdateFactory.newCameraPosition(camPos))
        }
    }

    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base?.let { localeManager?.setLocale(it) })
    }


    override fun getBindingVariable(): Int {
        return BR.viewModel
    }

    override fun getLayoutId(): Int {
        return R.layout.fragment_user_tracking
    }

    override fun getViewModel(): OrderDetailViewModel {
        mViewModel = ViewModelProviders.of(this, factory).get(OrderDetailViewModel::class.java)
        return mViewModel as OrderDetailViewModel
    }

    override fun onCartAdded(cartdata: AddtoCartModel.CartdataBean?) {
    }

    override fun onCancelOrder() {
        Toast.makeText(this, getString(R.string.cancel_msg, textConfig?.order), Toast.LENGTH_SHORT)
            .show()
        finish()
    }

    override fun editOrderResponse(data: Data?) {

    }

    override fun onCompletePayment() {
    }

    override fun onTrackDhl(data: TrackDhl?) {
    }

    override fun onTrackShipRocket(data: TrackDhl?) {

    }

    override fun zoomAuth(data: TrackDhl?) {
    }

    override fun zoomCallLink(data: DataZoom?) {

    }

    override fun onChangeStatus(status: Double?) {
    }

    override fun onGeofencePayment(data: GeofenceData?, geofenceTax: Boolean) {
    }

    override fun getSaddedPaymentSuccess(data: AddCardResponseData?) {
    }

    override fun getMyFatoorahPaymentSuccess(data: AddCardResponseData?) {
    }

    override fun onSosSuccess() {
    }

    override fun onErrorOccur(message: String) {
        binding.root.onSnackbar(message)
    }

    override fun onSessionExpire() {
        openActivityOnTokenExpire()
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }


    private fun convertDate(
        dateToConvert: String,
        orderStatus: Double?,
        currentStatus: Double
    ): String {

        return appUtils.convertDateOneToAnother(
            replaceInvalid(dateToConvert).replace("T", " ").replace("+00:00", ""),
            "yyyy-MM-dd HH:mm:ss", "EEE, dd\nhh:mm aa"
        ) ?: ""
    }

    private fun checkDeliverDate(orderDetail: OrderHistory?): String {
        return if (orderDetail?.status != OrderStatus.Delivered.orderStatus) "" else orderDetail?.delivered_on
            ?: ""
    }

    private fun replaceInvalid(dateToConvert: String): String {
        return if (dateToConvert.contains("Invalid date")) {
            dateToConvert.replace("Invalid date", "")
        } else {
            dateToConvert
        }
    }

    private fun callPhone(number: String) {

        val intent = Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", number, null))
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.CALL_PHONE
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            Manifest.permission.CALL_PHONE
            startActivity(intent)
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(arrayOf(Manifest.permission.CALL_PHONE), 1)
            }
        }
    }

}
