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
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.codebrew.clikat.BR
import com.codebrew.clikat.R
import com.codebrew.clikat.app_utils.*
import com.codebrew.clikat.base.BaseActivity
import com.codebrew.clikat.data.constants.AppConstants.Companion.REQUEST_CODE_LOCATION
import com.codebrew.clikat.data.constants.PrefenceConstants
import com.codebrew.clikat.data.model.api.AddressBean
import com.codebrew.clikat.data.model.api.ZoneInformation
import com.codebrew.clikat.data.model.others.MapInputParam
import com.codebrew.clikat.data.preferences.PreferenceHelper
import com.codebrew.clikat.databinding.ActivityAddressNewBinding
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
import com.hugmd.util.mapUtil.OnMapAndViewReadyListener
import kotlinx.android.synthetic.main.activity_address_new.*
import kotlinx.android.synthetic.main.fragment_dialog_address.*
import kotlinx.android.synthetic.main.toolbar_app.*
import pub.devrel.easypermissions.AppSettingsDialog
import pub.devrel.easypermissions.EasyPermissions
import java.io.IOException
import java.util.*
import javax.inject.Inject


class newlocActivity : BaseActivity<ActivityAddressNewBinding, AddressViewModel>(),
        OnMapAndViewReadyListener.OnGlobalLayoutAndMapReadyListener, GoogleMap.OnCameraMoveListener,
        GoogleMap.OnCameraIdleListener, EasyPermissions.PermissionCallbacks, View.OnClickListener,
        AddressNavigator {
    var address_neww = ""
    var address_type = ""
    var tag_screen = ""
    var static_val=""
var type=""
    /** This is ok to be lateinit as it is initialised in onMapReady */
    private lateinit var map: GoogleMap

    private lateinit var mViewModel: AddressViewModel
    var screen_val=""
    @Inject
    lateinit var factory: ViewModelProviderFactory

    @Inject
    lateinit var permissionFile: PermissionFile

    @Inject
    lateinit var appUtils: AppUtils

    private lateinit var mLocationRequest: LocationRequest
    var mapInputParam: MapInputParam? = null
    private lateinit var locationCallback: LocationCallback

    @Inject
    lateinit var prefHelper: PreferenceHelper

    private lateinit var fusedLocationClient: FusedLocationProviderClient

    private var latlng: LatLng? = null

    private lateinit var mType: String

    private var mAddressId = ""
    var address_new = ""
    var zone_id = ""
    private lateinit var mBinding: ActivityAddressNewBinding
    private var clientInform: SettingModel.DataBean.SettingData? = null
    private var adminData: SettingModel.DataBean.AdminDetail? = null
    private var validNumber = false

    companion object{
        lateinit var address_baen_stat:AddressBean
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

//        mBinding == viewDataBinding  //DataBindingUtil.setContentView(this, R.layout.activity_select_loc) as ActivitySelectLocBinding
//        mBinding.color = Configurations.colors

        viewModel.navigator = this
        address_baen_stat= AddressBean()
        addAddressObserver()
        editAddressObserver()
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
        try {
            if(intent?.hasExtra("screen_val")!!)
            {
                screen_val=intent?.getStringExtra("screen_val")!!
            }
            if(intent?.hasExtra("static_val")!!)
            {
                static_val=intent?.getStringExtra("static_val")!!
            }
            mapInputParam = intent?.getParcelableExtra<MapInputParam>("mapParam")
            if(mapInputParam!!.street.equals(""))
            {
                ed_extra_adrs.setText(mapInputParam!!.first_address!!)
            }else {
                ed_extra_adrs.setText(mapInputParam!!.street!!)
            }
            if(mapInputParam!!.area.equals(""))
            {
                ed_area.setText(mapInputParam!!.second_address!!)
            }else {
                ed_area.setText(mapInputParam!!.area!!)
            }
                ed_address_type.setText(mapInputParam!!.address_type!!)
            ed_apartment.setText(mapInputParam!!.appartment!!)
            ed_floor.setText(mapInputParam!!.floor!!)
            ed_villa_adrss.setText(mapInputParam!!.building!!)

            if (intent?.hasExtra("tag_screen")!!) {
                tag_screen = intent?.getStringExtra("tag_screen")!!
            }
            if (intent?.hasExtra("type")!!) {
                type = intent?.getStringExtra("type")!!
            }
        } catch (e: Exception) {


        }
        tvUpdateLocation.setOnClickListener {
            val intent = Intent(this@newlocActivity, SelectlocActivity::class.java)
            intent.putExtra("mapParam", mapInputParam)
            intent.putExtra("is_new", 1)
            if (tag_screen.equals("cart")) {
                intent.putExtra("tag_screen", "cart")
            }
            if (type.equals("edit")||type.equals("add")) {
                intent.putExtra("type", type)
            }
            if(screen_val.equals("1"))
            {
                intent.putExtra("screen_val", screen_val)
            }
            startActivity(intent)
            finish()
        }
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
//                        currentLocation(location.latitude, location.longitude)

                        setMapLocation(LatLng(mapInputParam!!.latitude.toDouble(), mapInputParam!!.longitude.toDouble()))
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

//                tv_street_adrs.text = addressList[0].getAddressLine(0)
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
    }

    override fun onClick(p0: View) {
        when (p0) {
            tb_back -> finish()

            fb_current_loc -> {
                getLastLocation()
            }

            btn_save_adrs -> {
                if (!ed_villa_adrss.text.isBlank()) {

                    address_neww = ed_villa_adrss.text.toString()

                    if (!ed_floor.text.isBlank()) {
                        address_neww = address_neww + "," + ed_floor.text.toString()
                    }
                    if (!ed_apartment.text.isBlank()) {
                        address_neww = address_neww + "," + ed_apartment.text.toString()
                    }
                    if (!ed_extra_adrs.text.isBlank()) {
                        address_neww = address_neww + "," + ed_extra_adrs.text.toString()
                    }
                    if (!ed_area.text.isBlank()) {
                        address_neww = address_neww + "," + ed_area.text.toString()
                    }
                    if (!ed_address_type.text.isBlank()) {
                        address_type = ed_address_type.text.toString()
                    } else {
                        address_type = text_delivery_location.text.toString()
                    }

                } else {
                    AppToasty.error(this, "Building/Villa field can't be empty")

                }


                // tvArea.text = mapInputParam?.first_address.toString()

                var hashMap: HashMap<String, String>? = null

                hashMap = HashMap()
                // hashMap["name"] = prefHelper.getKeyValue(PrefenceConstants.USER_NAME, PrefenceConstants.TYPE_STRING).toString()
                hashMap["latitude"] = mapInputParam!!.latitude
                hashMap["longitude"] = mapInputParam!!.longitude
                hashMap["addressLineFirst"] = address_new
                hashMap["customer_address"] = address_type
                hashMap["zone_id"] = zone_id!!
                hashMap["address_type"] = ed_address_type.text.toString()?:""
                hashMap["area"] = ed_area.text.toString()?:""
                hashMap["street"] = ed_extra_adrs.text.toString()?:""
                hashMap["building"] = ed_villa_adrss.text.toString()?:""
                hashMap["floor"] = ed_floor.text.toString()?:""
                hashMap["appartment"] =ed_apartment.text.toString()?:""

                hashMap["is_real_address"] = "1"
                if (mapInputParam!!.name?.isNotEmpty() == true)
                    hashMap["name"] = mapInputParam!!.name ?: ""
                if (mapInputParam!!.phone_number?.isNotEmpty() == true) {
                    hashMap["phone_number"] = mapInputParam!!.phone_number ?: ""
                    hashMap["country_code"] = mapInputParam!!.country_code ?: ""
                }
                if (!mapInputParam!!.reference_address.isNullOrEmpty()) {
                    hashMap["reference_address"] = mapInputParam!!.reference_address ?: ""
                }



                if (CommonUtils.isNetworkConnected(applicationContext
                                ?: this)) {

                    if (prefHelper.getCurrentUserLoggedIn()) {
if(type.equals("edit"))
{
    if (!ed_villa_adrss.text.isBlank()) {

        address_neww = ed_villa_adrss.text.toString()

        if (!ed_floor.text.isBlank()) {
            address_neww = address_neww + "," + ed_floor.text.toString()
        }
        if (!ed_apartment.text.isBlank()) {
            address_neww = address_neww + "," + ed_apartment.text.toString()
        }
        if (!ed_extra_adrs.text.isBlank()) {
            address_neww = address_neww + "," + ed_extra_adrs.text.toString()
        }
        if (!ed_area.text.isBlank()) {
            address_neww = address_neww + "," + ed_area.text.toString()
        }
        if (!ed_address_type.text.isBlank()) {
            address_type = ed_address_type.text.toString()
        } else {
            address_type = text_delivery_location.text.toString()
        }

    } else {
        AppToasty.error(this, "Building/Villa field can't be empty")

    }
    var hashMap: HashMap<String, String>? = null

    hashMap = HashMap()
    // hashMap["name"] = prefHelper.getKeyValue(PrefenceConstants.USER_NAME, PrefenceConstants.TYPE_STRING).toString()
    hashMap["latitude"] = mapInputParam!!.latitude
    hashMap["longitude"] = mapInputParam!!.longitude
    hashMap["addressLineFirst"] = address_new
    hashMap["customer_address"] = address_type
    hashMap["zone_id"] = zone_id!!
    hashMap["address_type"] = ed_address_type.text.toString()?:""
    hashMap["area"] = ed_area.text.toString()?:""
    hashMap["street"] = ed_extra_adrs.text.toString()?:""
    hashMap["building"] = ed_villa_adrss.text.toString()?:""
    hashMap["floor"] = ed_floor.text.toString()?:""
    hashMap["appartment"] =ed_apartment.text.toString()?:""

    hashMap["is_real_address"] = "1"
    if (mapInputParam!!.name?.isNotEmpty() == true)
        hashMap["name"] = mapInputParam!!.name ?: ""
    if (mapInputParam!!.phone_number?.isNotEmpty() == true) {
        hashMap["phone_number"] = mapInputParam!!.phone_number ?: ""
        hashMap["country_code"] = mapInputParam!!.country_code ?: ""
    }
    if (!mapInputParam!!.reference_address.isNullOrEmpty()) {
        hashMap["reference_address"] = mapInputParam!!.reference_address ?: ""
    }



    hashMap["addressId"]= mapInputParam!!.addressId
    hashMap?.let {
        mViewModel?.editAddress(it)
    }
}else{
    hashMap?.let {
        mViewModel?.addAddress(it)
    }
}


                    }
                }
            }

            text_skip -> {
                if (latlng?.latitude != null && latlng?.longitude != null) {
                    val mapParam = MapInputParam(
                            latlng?.latitude.toString(),
                            latlng?.longitude.toString(),
                            address_new,
                            text_delivery_location.text.toString(),
                            mType,
                            mAddressId
                    )

                    val intent = Intent()
                    intent.putExtra("mapParam", mapParam)
                    setResult(Activity.RESULT_OK, intent)
                    finish()
                }
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

            setOnCameraIdleListener(this@newlocActivity)
            setOnCameraMoveListener(this@newlocActivity)
            map.getUiSettings().setAllGesturesEnabled(false);

            // Show Sydney on the map.

            setContentDescription("Map with lots of markers.")

            setMapLocation(LatLng(mapInputParam!!.latitude.toDouble(), mapInputParam!!.longitude.toDouble()))


            //moveCamera(com.google.android.gms.maps.CameraUpdateFactory.newLatLngBounds(builder.build(), 50))


        }
    }


    private fun setMapLocation(latLng: LatLng) {

        if (!::map.isInitialized) return
        map.clear()

        with(map) {
            moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 18f))
            mapType = GoogleMap.MAP_TYPE_NORMAL

        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == REQUEST_CODE_LOCATION && resultCode == Activity.RESULT_OK) {
            getLastLocation()
        }
    }
    private fun editAddressObserver() {
        // Create the observer which updates the UI.
        val catObserver = Observer<AddressBean> { resource ->

            if (tag_screen.equals("cart")) {

                val intent = Intent()
                if (resource!=null) {
                    intent.putExtra("addressBean", resource)
                }
                if(static_val.equals("1"))
                {
                    address_baen_stat=resource
                }

                intent.putExtra("is_real_address", "1")
                setResult(1, intent)
                finish()
            }else {

                val mapParam = MapInputParam(
                        resource?.latitude.toString(),
                        resource?.longitude.toString(),
                        address_new,
                        resource.area.toString(),
                        type,
                        resource?.id?.toString()!!,
                        zone_id=zone_id!!,
                        area=resource?.area!!,
                        floor=resource?.floor!!,
                        address_type=resource?.address_type!!,
                        appartment=resource?.appartment!!,
                        building=resource?.building!!,
                        street=resource?.street!!
                )
                mapParam.reference_address = "others"

                val intent = Intent()
                intent.putExtra("mapParam", mapParam)
                setResult(Activity.RESULT_OK, intent)
                finish()
            }

        }

        // Observe the LiveData, passing in this activity as the LifecycleOwner and the observer.
        mViewModel?.updateAdrsLiveData?.observe(this, catObserver)
    }
    private fun addAddressObserver() {
        // Create the observer which updates the UI.
        val catObserver = Observer<AddressBean> { resource ->
            if (tag_screen.equals("cart")) {

                val intent = Intent()
                if (resource!=null) {
                    intent.putExtra("addressBean", resource)
                }
                if(static_val.equals("1"))
                {
                    address_baen_stat=resource
                }

                intent.putExtra("is_real_address", "1")
                setResult(1, intent)
                finish()
            }else if(screen_val.equals("1"))
{
    val mapParam = MapInputParam(
            resource?.latitude.toString(),
            resource?.longitude.toString(),
            address_new,
            resource.area.toString(),
            type,
            resource?.id?.toString()!!,
            zone_id=zone_id!!,
            area=resource?.area!!,
            floor=resource?.floor!!,
            address_type=resource?.address_type!!,
            appartment=resource?.appartment!!,
            building=resource?.building!!,
            street=resource?.street!!
    )
    mapParam.reference_address = "others"

    val intent = Intent()
    intent.putExtra("mapParam", mapParam)
    setResult(Activity.RESULT_OK, intent)
    finish()
}else {
    finish()

}  }

        // Observe the LiveData, passing in this activity as the LifecycleOwner and the observer.
        mViewModel?.addAdrsLiveData?.observe(this, catObserver)
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
        return R.layout.activity_address_new
    }

    override fun getViewModel(): AddressViewModel {
        mViewModel = ViewModelProviders.of(this, factory).get(AddressViewModel::class.java)
        return mViewModel
    }

    override fun onZoneData(zoneInf: ZoneInformation?, isAddressUpdate: Boolean) {
        text_delivery_location.text = zoneInf?.name.toString()
        zone_id = zoneInf!!.id.toString()
        ed_area.text = zoneInf?.name.toString()
    }

    override fun onErrorOccur(message: String) {

    }

    override fun onSessionExpire() {
        openActivityOnTokenExpire()
    }


}
