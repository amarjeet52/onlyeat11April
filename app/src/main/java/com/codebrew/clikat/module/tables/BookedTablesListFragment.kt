package com.codebrew.clikat.module.tables

import android.Manifest
import android.content.IntentSender
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.core.app.ActivityCompat
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.codebrew.clikat.BR
import com.codebrew.clikat.R
import com.codebrew.clikat.app_utils.*
import com.codebrew.clikat.app_utils.extension.loadPlaceHolder
import com.codebrew.clikat.app_utils.extension.onSnackbar
import com.codebrew.clikat.base.BaseFragment
import com.codebrew.clikat.data.constants.AppConstants
import com.codebrew.clikat.data.constants.NetworkConstants
import com.codebrew.clikat.data.constants.PrefenceConstants
import com.codebrew.clikat.data.model.api.Currency
import com.codebrew.clikat.data.model.api.SuccessModel
import com.codebrew.clikat.data.model.others.GlobalTableDataHolder
import com.codebrew.clikat.data.preferences.PreferenceHelper
import com.codebrew.clikat.databinding.FragmentBookedTablesBinding
import com.codebrew.clikat.di.ViewModelProviderFactory
import com.codebrew.clikat.modal.BookedTableItem
import com.codebrew.clikat.modal.other.SettingModel
import com.codebrew.clikat.preferences.DataNames
import com.codebrew.clikat.retrofit.Config.Companion.DB_SECRET_KEY
import com.codebrew.clikat.utils.Utils
import com.codebrew.clikat.utils.configurations.Configurations
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import com.google.android.material.tabs.TabLayout
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import io.socket.client.Ack
import io.socket.emitter.Emitter
import kotlinx.android.synthetic.main.fragment_agent_list.*
import kotlinx.android.synthetic.main.fragment_booked_tables.*
import kotlinx.android.synthetic.main.fragment_booked_tables.noData
import kotlinx.android.synthetic.main.nothing_found.*
import kotlinx.android.synthetic.main.toolbar_subscription.*
import org.json.JSONObject
import pub.devrel.easypermissions.EasyPermissions
import timber.log.Timber
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject
import kotlin.collections.ArrayList


class BookedTablesListFragment : BaseFragment<FragmentBookedTablesBinding, TableBookingsViewModel>(),
        TableBookingListNavigator, TablesRecyclerActionListener, EasyPermissions.PermissionCallbacks,
        SwipeRefreshLayout.OnRefreshListener {

    @Inject
    lateinit var factory: ViewModelProviderFactory


    @Inject
    lateinit var permissionUtil: PermissionFile

    @Inject
    lateinit var mDataManager: PreferenceHelper

    private var selectedCurrency: Currency? = null
    private var clientInform: SettingModel.DataBean.SettingData? = null
    private var mBinding: FragmentBookedTablesBinding? = null
    private lateinit var viewModel: TableBookingsViewModel
    private var collection = ArrayList<BookedTableItem?>()
    private var recyclerAdapter = BookedTablesRecyclerAdapter()
    private var invitationList = 0
    private var currentLocation: Location? = null

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var locationCallback: LocationCallback? = null
    private var locationRequest: LocationRequest? = null

    private val socketManager
            by lazy {
                SocketManager.getInstance(mDataManager.getKeyValue(PrefenceConstants.USER_ID, PrefenceConstants.TYPE_STRING).toString(),
                        mDataManager.getKeyValue(PrefenceConstants.DB_SECRET, PrefenceConstants.TYPE_STRING).toString())
            }


    override fun getBindingVariable(): Int = BR.viewModel

    override fun getLayoutId(): Int = R.layout.fragment_booked_tables

    override fun getViewModel(): TableBookingsViewModel {
        viewModel = ViewModelProviders.of(this, factory).get(TableBookingsViewModel::class.java)
        return viewModel
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.navigator = this
        clientInform = mDataManager.getGsonValue(DataNames.SETTING_DATA, SettingModel.DataBean.SettingData::class.java)
        selectedCurrency = mDataManager.getGsonValue(PrefenceConstants.CURRENCY_INF, Currency::class.java)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mBinding = viewDataBinding
        mBinding?.color = Configurations.colors
        mBinding?.string = Configurations.strings


        tabLayout_tables?.newTab()?.setText("My requests")?.let { tabLayout_tables?.addTab(it) }
        tabLayout_tables?.newTab()?.setText("Invited requests")?.let { tabLayout_tables?.addTab(it) }

        setViewListener()
        setRecyclerAdapter()
        swipeRefresh.setOnRefreshListener(this)

        Utils.loadAppPlaceholder(clientInform?.table_booking_request ?: "")?.let {

            if (it.app?.isNotEmpty() == true)
            {
                ivPlaceholder.loadPlaceHolder(it.app ?: "")
            }

            if (it.message?.isNotEmpty() == true) {
                tvText.text = it.message
            }

        }

    }


    private fun setViewListener() {
        tb_back?.setOnClickListener {
            requireActivity().onBackPressed()
        }

        tb_name?.text = getString(R.string.table_booking)

        tabLayout_tables?.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabReselected(tab: TabLayout.Tab?) {}

            override fun onTabUnselected(tab: TabLayout.Tab?) {}

            override fun onTabSelected(tab: TabLayout.Tab?) {
                collection.clear()
                invitationList = tab?.position ?: 0
                recyclerAdapter.setDataType(invitationList)
                recyclerAdapter.notifyDataSetChanged()
                loadTablesList(true)
            }
        })

        onRecyclerViewScrolled()
    }

    override fun onResume() {
        super.onResume()
        reconnectToSocket()
        checkingLocationPermission()
        loadTablesList(true)
    }


    override fun onPause() {
        super.onPause()
        if(::fusedLocationClient.isInitialized)
          fusedLocationClient.removeLocationUpdates(locationCallback)
    }

    private fun setRecyclerAdapter() {
        recyclerAdapter.setListData(collection)
        recyclerAdapter.setDataType(invitationList)
        recyclerAdapter.setActionListener(this)
        recyclerAdapter.setSettingsData(clientInform,selectedCurrency)
        rvTables?.adapter = recyclerAdapter
    }

    private fun loadTablesList(loader: Boolean) {
        val requestHolder = hashMapOf(
                "user_id" to mDataManager.getKeyValue(PrefenceConstants.USER_ID, PrefenceConstants.TYPE_STRING),
                "offset" to collection.size,
                "limit" to 20,
                "invitation_list" to invitationList
        )
        viewModel.getListOfBookedTables(requestHolder, loader)
    }

    override fun onTableListReceived(list: List<BookedTableItem?>?) {
        val notifyData = collection.isNotEmpty()
        list?.let { collection.addAll(it) }

        if (collection.isNullOrEmpty()) {
            noData?.visibility = View.VISIBLE
            rvTables?.visibility = View.GONE
        } else {
            noData?.visibility = View.GONE
            rvTables?.visibility = View.VISIBLE
        }

        if(notifyData){
            recyclerAdapter.notifyDataSetChanged()
        } else{
            recyclerAdapter.setListData(collection)
        }


        recyclerAdapter.notifyDataSetChanged()
    }

    override fun onErrorOccur(message: String) {
        mBinding?.root?.onSnackbar(message)
    }

    override fun onSessionExpire() {
        openActivityOnTokenExpire()
    }

    override fun onInviteFriendClicked(itemModel: BookedTableItem?) {
        val userNameHolder = mDataManager.getKeyValue(PrefenceConstants.USER_NAME, PrefenceConstants.TYPE_STRING).toString()
        val hashMap = hashMapOf(
                "booking_id" to itemModel?.id.toString(),
                "table_id" to itemModel?.tableId.toString(),
                "capacity" to itemModel?.seatingCapacity.toString(),
                "table_number" to itemModel?.tableNumber.toString(),
                "table_name" to itemModel?.tableName.toString(),
                "user_name" to userNameHolder.toString(),
                "branch_name" to itemModel?.branchName.toString(),
                "date" to itemModel?.scheduleDate.toString(),
                "supplier_id" to itemModel?.supplierId.toString(),
                "branch_id" to itemModel?.branchId.toString()
        )
        val uriParams = DeeplinkingHelper.getNeededUri("table", hashMap)
        val x = DeeplinkingHelper.createDeeplink(uriParams)
        Log.i("TAG", "$x")
        DeeplinkingHelper.createShortLink(requireActivity(), x)

    }

    override fun onAddItemsInCartClicked(itemModel: BookedTableItem?) {
        val valuesHolder = GlobalTableDataHolder().apply {
            isInvitation = "0"
            isScanned = "0"
            invitationAccepted = "0"
            branch_id = itemModel?.branchId.toString()
            table_id = itemModel?.tableId.toString()
            capacity = itemModel?.seatingCapacity.toString()
            table_number = itemModel?.tableNumber.toString()
            table_name = itemModel?.tableName
            user_name = itemModel?.userName
            branch_name = itemModel?.branchName
            date = itemModel?.scheduleDate
            supplier_id = itemModel?.supplierId.toString()
            booking_id = itemModel?.id.toString()
        }

        mDataManager.setkeyValue(DataNames.INVITATTON_DATA, Gson().toJson(valuesHolder))

        val bundle = Bundle()
        bundle.putString("deliveryType", "dinein")


        if (clientInform?.app_selected_template != null
                && clientInform?.app_selected_template == "1") {
            navController(this).navigate(R.id.action_bookingList_to_RestaurantDetailNew, bundle)
        } else {
            navController(this).navigate(R.id.action_bookingList_to_RestaurantDetail, bundle)
        }
    }

    override fun onOnMyWayClicked(itemModel: BookedTableItem?) {
        connectAndEmmitSockets(itemModel)
        itemModel?.userOnTheWay = 1
        recyclerAdapter.notifyDataSetChanged()
    }

    private fun onRecyclerViewScrolled() {
        rvTables?.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val isPagingActive = collection.isNotEmpty() && collection.size % 20 == 0
                if (!recyclerView.canScrollVertically(1) && isPagingActive) {
                    loadTablesList(false)
                }
            }
        })
    }


    private fun connectAndEmmitSockets(itemModel: BookedTableItem?) {
        val cDate = Date()
        val formatter = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        formatter.timeZone = TimeZone.getDefault()
        val currentDate = formatter.format(cDate)

        reconnectToSocket()
        if (currentLocation == null) {
            return
        }

        mDataManager.getKeyValue(DataNames.REGISTRATION_ID, PrefenceConstants.TYPE_STRING).toString().isEmpty()

        val jsonObject = JSONObject()

        jsonObject.put("table_booking_id", itemModel?.id)
        jsonObject.put("secret_key", DB_SECRET_KEY)
        jsonObject.put("latitude", currentLocation?.latitude)
        jsonObject.put("longitude", currentLocation?.longitude)
        jsonObject.put("currentTime", currentDate)
        Log.i("okhttp", jsonObject.toString())
        socketManager.emit(
                SocketManager.SHARE_LOCATION,
                jsonObject,
                Ack {
                    val acknowledgement = it.firstOrNull()
                    if (acknowledgement != null && acknowledgement is JSONObject) {

                    }
                })
        socketManager.onErrorEvent()
    }

    private fun reconnectToSocket() {

        if (!socketManager.checkConnection()) {
            socketManager.connect(socketListener)
        }
    }


    private val socketListener = Emitter.Listener { args ->
        if (args.isNotEmpty() && args[0] is JSONObject) {
            val response = Gson().fromJson<SuccessModel>(
                    args[0].toString(),
                    object : TypeToken<SuccessModel>() {}.type
            )

            if (response?.success == NetworkConstants.AUTHFAILED) {
                mDataManager.logout()
                onSessionExpire()
            } else {
                Timber.e(response.message)
            }
        }
    }

    private fun checkingLocationPermission() {
        if (permissionUtil.hasLocation(requireActivity())) {
            createLocationRequest()
        } else {
            permissionUtil.locationTask(requireActivity())
        }
    }

    override fun onPermissionsDenied(requestCode: Int, perms: MutableList<String>) {

    }

    override fun onPermissionsGranted(requestCode: Int, perms: MutableList<String>) {
        if (requestCode == AppConstants.REQUEST_CODE_LOCATION) {
            if (CommonUtils.isNetworkConnected(requireActivity())) {
                createLocationRequest()
            }
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

        val client = LocationServices.getSettingsClient(requireActivity())
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
                            resolvable.startResolutionForResult(requireActivity(), AppConstants.RC_LOCATION_PERM)
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

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())

        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult?) {
                if (locationResult == null) {
                    return
                }
                for (location in locationResult.locations) {
                    currentLocation = location
                }


                val dataHolder = collection.filter {
                    it?.userOnTheWay == 1
                }

                if (dataHolder.isNotEmpty()) {
                    dataHolder.forEach {

                        connectAndEmmitSockets(it)
                    }
                }
            }
        }
        startLocationUpdates()
    }

    private fun startLocationUpdates() {


        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(requireActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return
        }
        fusedLocationClient.requestLocationUpdates(
                locationRequest,
                locationCallback,
                null /* Looper */
        )
    }

    override fun onRefresh() {
        swipeRefresh?.isRefreshing = false
        collection.clear()
        recyclerAdapter.setDataType(invitationList)
        recyclerAdapter.notifyDataSetChanged()
        loadTablesList(false)
    }
}