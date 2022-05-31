package com.codebrew.clikat.module.dialog_adress.v2

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.IntentSender
import android.location.Address
import android.location.Geocoder
import android.os.Bundle
import android.view.View
import androidx.annotation.NonNull
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.codebrew.clikat.BuildConfig
import com.codebrew.clikat.R
import com.codebrew.clikat.app_utils.PermissionFile
import com.codebrew.clikat.app_utils.extension.onSnackbar
import com.codebrew.clikat.data.constants.AppConstants.Companion.REQUEST_CODE_LOCATION
import com.codebrew.clikat.data.model.api.AddressBean
import com.codebrew.clikat.data.model.others.MapInputParam
import com.codebrew.clikat.databinding.ActivitySelectLocV2Binding
import com.codebrew.clikat.modal.AppGlobal.Companion.localeManager
import com.codebrew.clikat.utils.configurations.Configurations
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.hugmd.util.mapUtil.OnMapAndViewReadyListener
import dagger.android.AndroidInjection
import kotlinx.android.synthetic.main.activity_select_loc.*
import kotlinx.android.synthetic.main.toolbar_app.*
import pub.devrel.easypermissions.AppSettingsDialog
import pub.devrel.easypermissions.EasyPermissions
import java.io.IOException
import java.util.*
import javax.inject.Inject


open class SelectlocActivityV2 : AppCompatActivity(),
        OnMapAndViewReadyListener.OnGlobalLayoutAndMapReadyListener, GoogleMap.OnCameraMoveListener,
        GoogleMap.OnCameraIdleListener, EasyPermissions.PermissionCallbacks, View.OnClickListener {


    /** This is ok to be lateinit as it is initialised in onMapReady */
    private lateinit var map: GoogleMap

    @Inject
    lateinit var permissionFile: PermissionFile

    private lateinit var mLocationRequest: LocationRequest

    private lateinit var locationCallback: LocationCallback

    private lateinit var fusedLocationClient: FusedLocationProviderClient

    private var latlng: LatLng? = null

    private lateinit var mType: String

    private var mAddressId = ""

    private lateinit var mBinding: ActivitySelectLocV2Binding


    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        super.onCreate(savedInstanceState)

        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_select_loc_v2) as ActivitySelectLocV2Binding
        mBinding.color = Configurations.colors

        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        OnMapAndViewReadyListener(mapFragment, this)

        setClick()

        intializeLocation()

        settingLayout()

        setAnimation()

    }

    private fun setAnimation() {


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
                        currentLocation(location.latitude, location.longitude)

                        setMapLocation(LatLng(location.latitude, location.longitude))
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
                            REQUEST_CODE_LOCATION)
                } catch (sendEx: IntentSender.SendIntentException) {
                    // Ignore the error.
                }
            }
        }

    }

    @SuppressLint("MissingPermission")
    private fun getLastLocation() {

        if (permissionFile.locationTask(this)) {


            fusedLocationClient.requestLocationUpdates(mLocationRequest,
                    locationCallback,
                    null /* Looper */)

            /*fusedLocationClient.lastLocation
                    .addOnSuccessListener { location : Location? ->
                    }
*/
        }
    }


    private fun currentLocation(lat: Double, lng: Double) {
        val geocoder = Geocoder(this, Locale.getDefault())

        latlng = LatLng(lat, lng)

        try {
            val addressList: List<Address> = geocoder.getFromLocation(lat, lng, 1)

            if (addressList.isNotEmpty()) {

                tv_street_adrs.text = addressList[0].getAddressLine(0)

            }
        } catch (e: IOException) {

        }
    }


    private fun settingLayout() {


        if (intent != null) {

            if (intent.hasExtra("addressData")) {
                val addressModel = intent.getParcelableExtra<AddressBean>("addressData")

                mAddressId = addressModel?.id.toString()

                if (addressModel?.latitude?.isNotBlank()!! && addressModel.longitude?.isNotBlank()!!) {
                    latlng = LatLng(addressModel.latitude?.toDouble()!!, addressModel.longitude?.toDouble()!!)
                }


                tv_street_adrs.text = addressModel.address_line_1


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
    }

    override fun onClick(p0: View) {

        when (p0) {
            tb_back -> finish()

            fb_current_loc -> {
                getLastLocation()
            }

            btn_save_adrs -> {
                    if (ed_extra_adrs.text.trim().isNotEmpty() || BuildConfig.CLIENT_CODE=="betternoms_0820") {
                        val mapParam = MapInputParam(latlng?.latitude.toString(), latlng?.longitude.toString(), ed_extra_adrs.text.toString(), ed_extra_adrs.text.trim().toString(), mType, mAddressId)

                        val intent = Intent()
                        intent.putExtra("mapParam", mapParam)
                        setResult(Activity.RESULT_OK, intent)
                        finish()
                    } else {
                        val address = "${getString(R.string.please_add)} ${text_street_number?.text.toString().toLowerCase()}"
                        mBinding.root.onSnackbar(address)
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

            setOnCameraIdleListener(this@SelectlocActivityV2)
            setOnCameraMoveListener(this@SelectlocActivityV2)
            // Override the default content description on the view, for accessibility mode.
            // Ideally this string would be localised.
            setContentDescription("Map with lots of markers.")

            latlng?.let { setMapLocation(it) }

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


    override fun onCameraMove() {

        iv_loc_marker.visibility = View.GONE
        shimmer_view_container.visibility = View.VISIBLE
        loc_container.visibility = View.INVISIBLE
    }

    override fun onCameraIdle() {

        iv_loc_marker.visibility = View.VISIBLE
        shimmer_view_container.visibility = View.GONE
        loc_container.visibility = View.VISIBLE

        val center: LatLng = map.cameraPosition.target
        currentLocation(center.latitude, center.longitude)
    }


    override fun onRequestPermissionsResult(requestCode: Int,
                                            @NonNull permissions: Array<String>,
                                            @NonNull grantResults: IntArray) {
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

}
