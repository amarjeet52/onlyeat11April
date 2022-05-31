package com.codebrew.clikat.module.dialog_adress

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.IntentSender
import android.location.Address
import android.location.Geocoder
import android.os.Bundle
import android.os.Handler
import android.view.View
import androidx.annotation.NonNull
import androidx.lifecycle.ViewModelProviders
import com.bumptech.glide.Glide
import com.codebrew.clikat.BR
import com.codebrew.clikat.R
import com.codebrew.clikat.app_utils.AppUtils
import com.codebrew.clikat.app_utils.DateTimeUtils
import com.codebrew.clikat.app_utils.PermissionFile
import com.codebrew.clikat.base.BaseActivity
import com.codebrew.clikat.data.constants.AppConstants.Companion.REQUEST_CODE_LOCATION
import com.codebrew.clikat.data.constants.PrefenceConstants
import com.codebrew.clikat.data.model.api.AddressBean
import com.codebrew.clikat.data.model.api.ZoneInformation
import com.codebrew.clikat.data.model.others.MapInputParam
import com.codebrew.clikat.data.preferences.PreferenceHelper
import com.codebrew.clikat.databinding.ActivitySelectLocBinding
import com.codebrew.clikat.di.ViewModelProviderFactory
import com.codebrew.clikat.modal.AppGlobal.Companion.localeManager
import com.codebrew.clikat.modal.other.SettingModel
import com.codebrew.clikat.preferences.DataNames
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.widget.Autocomplete
import com.google.android.libraries.places.widget.AutocompleteActivity
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode
import com.hugmd.util.mapUtil.OnMapAndViewReadyListener
import kotlinx.android.synthetic.main.activity_address_new.*
import kotlinx.android.synthetic.main.activity_select_loc.*
import kotlinx.android.synthetic.main.activity_select_loc.btn_save_adrs
import kotlinx.android.synthetic.main.activity_select_loc.ed_extra_adrs
import kotlinx.android.synthetic.main.activity_select_loc.fb_current_loc
import kotlinx.android.synthetic.main.activity_select_loc.iv_loc_marker
import kotlinx.android.synthetic.main.activity_select_loc.loc_container
import kotlinx.android.synthetic.main.activity_select_loc.shimmer_view_container
import kotlinx.android.synthetic.main.activity_select_loc.text_delivery_location
import kotlinx.android.synthetic.main.activity_select_loc.text_skip
import kotlinx.android.synthetic.main.activity_select_loc.text_street_number
import kotlinx.android.synthetic.main.toolbar_app.*
import pub.devrel.easypermissions.AppSettingsDialog
import pub.devrel.easypermissions.EasyPermissions
import java.io.IOException
import java.util.*
import javax.inject.Inject


class SelectlocActivity : BaseActivity<ActivitySelectLocBinding, AddressViewModel>(),
        OnMapAndViewReadyListener.OnGlobalLayoutAndMapReadyListener, GoogleMap.OnCameraMoveListener,
        GoogleMap.OnCameraIdleListener, EasyPermissions.PermissionCallbacks, View.OnClickListener,
        AddressNavigator {


    /** This is ok to be lateinit as it is initialised in onMapReady */
    private lateinit var map: GoogleMap

    private lateinit var mViewModel: AddressViewModel

    var address_new = ""

    @Inject
    lateinit var factory: ViewModelProviderFactory
    var tag_screen = ""
    var screen_val = ""

    @Inject
    lateinit var permissionFile: PermissionFile

    @Inject
    lateinit var appUtils: AppUtils

    private lateinit var mLocationRequest: LocationRequest

    private lateinit var locationCallback: LocationCallback

    @Inject
    lateinit var prefHelper: PreferenceHelper

    private lateinit var fusedLocationClient: FusedLocationProviderClient

    private var latlng: LatLng? = null

    var mType = ""

    private var mAddressId = ""
    var is_new = 0

    var mapInputParam: MapInputParam? = null
    private lateinit var mBinding: ActivitySelectLocBinding
    private var clientInform: SettingModel.DataBean.SettingData? = null
    private var adminData: SettingModel.DataBean.AdminDetail? = null
    private var validNumber = false

    private val AUTOCOMPLETE_REQUEST_CODE = 1
    val fields = listOf(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

//        mBinding == viewDataBinding  //DataBindingUtil.setContentView(this, R.layout.activity_select_loc) as ActivitySelectLocBinding
//        mBinding.color = Configurations.colors
        viewModel.navigator = this
        try {
            is_new = intent.getIntExtra("is_new", 0)
        } catch (e: Exception) {

        }
        try {
            if (intent?.hasExtra("mapParam")!!) {
                mapInputParam = intent?.getParcelableExtra<MapInputParam>("mapParam")

            }
            if (intent?.hasExtra("tag_screen")!!) {
                tag_screen = intent?.getStringExtra("tag_screen")!!
            }
            if (intent?.hasExtra("screen_val")!!) {
                screen_val = intent?.getStringExtra("screen_val")!!
            }
        } catch (e: Exception) {


        }
        Glide.with(this).asGif().load(R.raw.loading_gif).into(imageLoader);
        imageLoader.visibility = View.GONE
        clientInform = prefHelper.getGsonValue(
                DataNames.SETTING_DATA,
                SettingModel.DataBean.SettingData::class.java
        )
        adminData = prefHelper.getGsonValue(
                PrefenceConstants.ADMIN_DETAILS,
                SettingModel.DataBean.AdminDetail::class.java
        )
        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        OnMapAndViewReadyListener(mapFragment, this)


        tb_title.text = "Set A Delivery Address"

        setClick()
        intializeLocation()
        settingLayout()
    }

    //intialize locatin request
    private fun intializeLocation() {
        mLocationRequest = LocationRequest()
        mLocationRequest.interval = 10000
        mLocationRequest.fastestInterval = 5000
        mLocationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)


        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult?) {
                locationResult ?: return
                for (location in locationResult.locations) {
                    if (location != null) {
                        if (mapInputParam != null) {
                            if (mapInputParam!!.latitude != null && mapInputParam!!.longitude != null) {
                                setMapLocation(LatLng(location.latitude, location.longitude))
                            }
                        } else {
                            currentLocation(location.latitude, location.longitude)

                            setMapLocation(LatLng(location.latitude, location.longitude))
                        }

                    }
                    break
                }

                fusedLocationClient.removeLocationUpdates(locationCallback)
            }
        }

    }


    private fun createLocationRequest() {

        val builder = LocationSettingsRequest.Builder()
                .addLocationRequest(mLocationRequest)

        val client = LocationServices.getSettingsClient(this)
        val task = client.checkLocationSettings(builder.build())


        task.addOnSuccessListener { locationSettingsResponse ->
            // All location settings are satisfied. The client can initialize
            // location requests here.
            getLastLocation()
        }

        task.addOnFailureListener { exception ->
            if (exception is ResolvableApiException) {
                // Location settings are not satisfied, but this can be fixed
                // by showing the user a dialog.
                try {
                    // Show the dialog by calling startResolutionForResult(),
                    // and check the result in onActivityResult().
                    exception.startResolutionForResult(
                            this,
                            REQUEST_CODE_LOCATION
                    )
                } catch (sendEx: IntentSender.SendIntentException) {
                    // Ignore the error.
                }
            }
        }

    }

    @SuppressLint("MissingPermission")
    private fun getLastLocation() {

        if (permissionFile.locationTask(this)) {

            fusedLocationClient.requestLocationUpdates(
                    mLocationRequest,
                    locationCallback,
                    null /* Looper */
            )

            /*fusedLocationClient.lastLocation
                    .addOnSuccessListener { location : Location? ->
                    }
*/
        }
    }


    private fun currentLocation(lat: Double, lng: Double) {
        val geocoder = Geocoder(this, DateTimeUtils.timeLocale)

        latlng = LatLng(lat, lng)

        try {
            val addressList: List<Address> = geocoder.getFromLocation(lat, lng, 1)

            if (addressList.isNotEmpty()) {

                tv_street_adrs.text = addressList[0].getAddressLine(0)
                address_new = addressList[0].getAddressLine(0)
            }
        } catch (e: IOException) {

        }
    }


    private fun settingLayout() {

        val calender = Calendar.getInstance()
        val timeZone = calender.timeZone


        if (intent != null) {
            if (intent.hasExtra("addressData")) {
                val addressModel = intent.getParcelableExtra<AddressBean>("addressData")

                mAddressId = addressModel?.id.toString()

                if (addressModel?.latitude?.isNotBlank()!! && addressModel.longitude?.isNotBlank()!!) {
                    latlng = LatLng(
                            addressModel.latitude?.toDouble()!!,
                            addressModel.longitude?.toDouble()!!
                    )
                }


                tv_street_adrs.text = addressModel.address_line_1
                address_new = addressModel.address_line_1!!

                if (addressModel.customer_address != null) {
                    ed_extra_adrs.setText(addressModel.customer_address)
                }
            }

            if (intent.hasExtra("type")) {
                mType = intent.getStringExtra("type") ?: ""
                when (mType) {
                    "select" -> {
                    }
                    "edit" -> {
                        tb_title.text = getString(R.string.edit_address)
                        btn_save_adrs.text = getString(R.string.update_address)
                    }
                    else -> {
                        if (permissionFile.locationTask(this) && !intent.hasExtra("addressData")) {
                            createLocationRequest()
                        }
                    }
                }
            }
        }

    }

    private fun setClick() {
        tb_back.setOnClickListener(this)
        fb_current_loc.setOnClickListener(this)
        btn_save_adrs.setOnClickListener(this)
        text_skip.setOnClickListener(this)
        tv_search.setOnClickListener(this)
    }

    override fun onClick(p0: View) {
        when (p0) {
            tb_back -> finish()

            fb_current_loc -> {
                getLastLocation()
            }

            btn_save_adrs -> {


                val param = HashMap<String, String>()
                param["languageId"] = "14"

                param["latitude"] = latlng?.latitude.toString()
                param["longitude"] = latlng?.longitude.toString()
                if (prefHelper.getCurrentUserLoggedIn()) {
                    param["accessToken"] = prefHelper.getKeyValue(
                            PrefenceConstants.ACCESS_TOKEN,
                            PrefenceConstants.TYPE_STRING
                    ).toString()
                }

                if (prefHelper.getCurrentUserLoggedIn()) {
                    mViewModel.getAddressJson(param, false)
                } else {
                    mViewModel.getAddressList(param, false)
                }

            }

            text_skip -> {
                if (latlng?.latitude != null && latlng?.longitude != null) {
                    val mapParam = MapInputParam(
                            latlng?.latitude.toString(),
                            latlng?.longitude.toString(),
                            address_new,
                            "",
                            mType,
                            mAddressId
                    )

                    val intent = Intent()
                    intent.putExtra("mapParam", mapParam)
                    setResult(Activity.RESULT_OK, intent)
                    finish()
                }
            }

            tv_search -> {
                val intent = Autocomplete.IntentBuilder(AutocompleteActivityMode.FULLSCREEN, fields)
                        .setCountry("QA")
                        .build(this)
                startActivityForResult(intent, AUTOCOMPLETE_REQUEST_CODE)
            }
        }
    }


    override fun onMapReady(googleMap: GoogleMap?) {
        map = googleMap ?: return

        with(map) {
            // Hide the zoom controls as the button panel will cover it.
            uiSettings.isZoomControlsEnabled = false
            uiSettings.isMyLocationButtonEnabled = true

            // Setting an info window adapter allows us to change the both the contents and
            // look of the info window.


            mapType = GoogleMap.MAP_TYPE_NORMAL
            setPadding(0, 10, 0, 0)
            uiSettings.isMapToolbarEnabled = true

            // Set listeners for marker events.  See the bottom of this class for their behavior.

            setOnCameraIdleListener(this@SelectlocActivity)
            setOnCameraMoveListener(this@SelectlocActivity)
            // Override the default content description on the view, for accessibility mode.
            // Ideally this string would be localised.
            setContentDescription("Map with lots of markers.")
            try {
                if (mapInputParam != null) {
                    if (mapInputParam!!.latitude != null && mapInputParam!!.longitude != null) {
                        setMapLocation(LatLng(mapInputParam!!.latitude!!.toDouble(), mapInputParam!!.longitude!!.toDouble()))
                    } else {

                    }
                } else {
                    latlng?.let { setMapLocation(it) }
                }
            } catch (e: Exception) {

            }
            //moveCamera(com.google.android.gms.maps.CameraUpdateFactory.newLatLngBounds(builder.build(), 50))


        }
    }


    private fun setMapLocation(latLng: LatLng) {

        if (!::map.isInitialized) return
        map.clear()

        with(map) {
            moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 18f))
            mapType = GoogleMap.MAP_TYPE_NORMAL
            try {
                Handler().postDelayed({

                    val param = HashMap<String, String>()
                    param["languageId"] = "14"

                    param["latitude"] = latLng.latitude.toString()
                    param["longitude"] = latLng.longitude.toString()
                    if (prefHelper.getCurrentUserLoggedIn()) {
                        param["accessToken"] = prefHelper.getKeyValue(
                                PrefenceConstants.ACCESS_TOKEN,
                                PrefenceConstants.TYPE_STRING
                        ).toString()
                    }

                    if (prefHelper.getCurrentUserLoggedIn()) {
                        mViewModel.getAddressJson(param, true)
                    } else {
                        mViewModel.getAddressList(param, true)
                    }

                }, 3000)
            } catch (e: Exception) {
            }
        }


    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

        if (requestCode == AUTOCOMPLETE_REQUEST_CODE) {
            when (resultCode) {
                Activity.RESULT_OK -> {
                    data?.let {
                        val place = Autocomplete.getPlaceFromIntent(data)

                        place.latLng?.let { it1 -> setMapLocation(it1) }
                    }
                }
                AutocompleteActivity.RESULT_ERROR -> {
                    // TODO: Handle the error.
                    data?.let {
                        val status = Autocomplete.getStatusFromIntent(data)
                        // Log.e("AutoComplete", status.statusMessage.toString())
                    }
                }
                Activity.RESULT_CANCELED -> {
                    // The user canceled the operation.
                }
            }
            return
        } else if (requestCode == REQUEST_CODE_LOCATION && resultCode == Activity.RESULT_OK) {
            getLastLocation()
        }

        super.onActivityResult(requestCode, resultCode, data)
    }


    override fun onCameraMove() {

//        iv_loc_marker.visibility = View.GONE
//        shimmer_view_container.visibility = View.VISIBLE
//        loc_container.visibility = View.INVISIBLE
    }

    override fun onCameraIdle() {

        iv_loc_marker.visibility = View.VISIBLE
        shimmer_view_container.visibility = View.GONE
        loc_container.visibility = View.VISIBLE

        val center: LatLng = map.cameraPosition.target
        imageLoader.visibility = View.VISIBLE
        try {
            Handler().postDelayed({

                val param = HashMap<String, String>()
                param["languageId"] = "14"

                param["latitude"] = center.latitude.toString()
                param["longitude"] = center.longitude.toString()
                if (prefHelper.getCurrentUserLoggedIn()) {
                    param["accessToken"] = prefHelper.getKeyValue(
                            PrefenceConstants.ACCESS_TOKEN,
                            PrefenceConstants.TYPE_STRING
                    ).toString()
                }

                if (prefHelper.getCurrentUserLoggedIn()) {
                    mViewModel.getAddressJson(param, true)
                } else {
                    mViewModel.getAddressList(param, true)
                }

            }, 3000)
        } catch (e: Exception) {
        }


        currentLocation(center.latitude, center.longitude)
    }


    override fun onRequestPermissionsResult(
            requestCode: Int,
            @NonNull permissions: Array<String>,
            @NonNull grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        // EasyPermissions handles the request result.
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this)
    }

    override fun onPermissionsDenied(requestCode: Int, perms: MutableList<String>) {
        if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
            AppSettingsDialog.Builder(this).build().show()
        }
    }

    override fun onPermissionsGranted(requestCode: Int, perms: MutableList<String>) {
        when (requestCode) {
            REQUEST_CODE_LOCATION -> createLocationRequest()
        }
    }

    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base?.let { localeManager?.setLocale(it) })
    }

    override fun getBindingVariable(): Int {
        return BR.viewModel
    }

    override fun getLayoutId(): Int {
        return R.layout.activity_select_loc
    }

    override fun getViewModel(): AddressViewModel {
        mViewModel = ViewModelProviders.of(this, factory).get(AddressViewModel::class.java)
        return mViewModel
    }

    override fun onZoneData(zoneInf: ZoneInformation?, isAddressUpdate: Boolean) {
        imageLoader.visibility = View.GONE
        if (isAddressUpdate) {

            text_delivery_location.text = zoneInf?.name.toString()
        } else {
            if (is_new == 1) {
                if (latlng?.latitude != null && latlng?.longitude != null) {
                    val mapParam = MapInputParam(
                            latlng?.latitude.toString(),
                            latlng?.longitude.toString(),
                            address_new,
                            zoneInf?.name.toString(),
                            "",
                            "",
                            area = "",
                            floor = "",
                            address_type = "",
                            appartment = "",
                            building = "",
                            street = ""
                    )
                    mapParam.reference_address = "others"

                    val intent = Intent(this@SelectlocActivity, newlocActivity::class.java)
                    intent.putExtra("mapParam", mapParam)
                    if (tag_screen.equals("cart")) {
                        intent.putExtra("tag_screen", "cart")
                    }
                    intent.putExtra("static_val", "1")
                    if (mType.equals("edit") || mType.equals("add")) {
                        intent.putExtra("type", mType)
                    }
                    intent.putExtra("screen_val", "1")
                    startActivity(intent)
                    finish()
                } else {
                    val address = "${getString(R.string.please_add)} ${
                        text_street_number?.text.toString().toLowerCase()
                    }"
                }
            } else {
                if (mType.equals("edit") || screen_val.equals("1")) {
                    if (latlng?.latitude != null && latlng?.longitude != null) {
                        val mapParam = MapInputParam(
                                latlng?.latitude.toString(),
                                latlng?.longitude.toString(),
                                address_new,
                                zoneInf?.name.toString(),
                                "",
                                mAddressId
                        )
                        mapParam.reference_address = "others"

                        val intent = Intent(this@SelectlocActivity, newlocActivity::class.java)
                        intent.putExtra("mapParam", mapParam)
                        if (mType.equals("edit") || mType.equals("add")) {
                            intent.putExtra("type", mType)
                        }
                        if (tag_screen.equals("cart")) {
                            intent.putExtra("tag_screen", "cart")
                        }
                        if (screen_val.equals("1")) {
                            intent.putExtra("screen_val", screen_val)
                        }
                        startActivity(intent)
                        finish()
                    }
                } else {
                    if (latlng?.latitude != null && latlng?.longitude != null) {
                        val mapParam = MapInputParam(
                                latlng?.latitude.toString(),
                                latlng?.longitude.toString(),
                                address_new,
                                zoneInf?.name.toString(),
                                mType,
                                mAddressId, zone_id = zoneInf?.id.toString()
                        )
                        mapParam.reference_address = "others"

                        val intent = Intent()
                        intent.putExtra("mapParam", mapParam)
                        setResult(Activity.RESULT_OK, intent)
                        finish()
                    } else {
                        val address = "${getString(R.string.please_add)} ${
                            text_street_number?.text.toString().toLowerCase()
                        }"
                    }
                }
            }
        }


    }

    override fun onErrorOccur(message: String) {

    }

    override fun onSessionExpire() {
        openActivityOnTokenExpire()
    }


}
