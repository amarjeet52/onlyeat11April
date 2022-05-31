package com.codebrew.clikat.module.home_screen.suppliers

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.IntentSender
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.annotation.NonNull
import androidx.core.app.ActivityCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.codebrew.clikat.BR
import com.codebrew.clikat.R
import com.codebrew.clikat.app_utils.PermissionFile
import com.codebrew.clikat.app_utils.extension.onSnackbar
import com.codebrew.clikat.base.BaseActivity
import com.codebrew.clikat.data.constants.AppConstants
import com.codebrew.clikat.databinding.FragmentMapSuppliersBinding
import com.codebrew.clikat.di.ViewModelProviderFactory
import com.codebrew.clikat.modal.SupplierList
import com.codebrew.clikat.modal.other.SupplierDataBean
import com.codebrew.clikat.module.supplier_all.SupplierListNavigator
import com.codebrew.clikat.module.supplier_all.SupplierListViewModel
import com.codebrew.clikat.utils.MapUtils
import com.codebrew.clikat.utils.configurations.Configurations
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.hugmd.util.mapUtil.OnMapAndViewReadyListener
import kotlinx.android.synthetic.main.fragment_map_suppliers.*
import org.greenrobot.eventbus.EventBus
import pub.devrel.easypermissions.AppSettingsDialog
import pub.devrel.easypermissions.EasyPermissions
import javax.inject.Inject

class SuppliersMapFragment : BaseActivity<FragmentMapSuppliersBinding, SupplierListViewModel>(), SupplierListNavigator,
        OnMapAndViewReadyListener.OnGlobalLayoutAndMapReadyListener,
        EasyPermissions.PermissionCallbacks {

    @Inject
    lateinit var permissionUtil: PermissionFile

    private lateinit var mLocationRequest: LocationRequest

    @Inject
    lateinit var factory: ViewModelProviderFactory

    private lateinit var viewModel: SupplierListViewModel

    private lateinit var fusedLocationClient: FusedLocationProviderClient

    private var mBinding: FragmentMapSuppliersBinding? = null
    private lateinit var locationCallback: LocationCallback
    private lateinit var mMap: GoogleMap
    private var latlng: LatLng? = null
    private var currentLocMarker: Marker? = null

    private var currentBearing = 0f

    override fun getBindingVariable(): Int {
        return BR.viewModel
    }

    override fun getLayoutId(): Int {
        return R.layout.fragment_map_suppliers
    }

    override fun getViewModel(): SupplierListViewModel {
        viewModel = ViewModelProviders.of(this, factory).get(SupplierListViewModel::class.java)
        return viewModel
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = viewDataBinding
        mBinding?.color = Configurations.colors

        initialise()
        intialiseLocation()
        listeners()
        if (permissionUtil.locationTask(this)) {
            createLocationRequest()
        }
        supplierObserver()
        hitApi()
    }

    private fun hitApi() {
        if (isNetworkConnected)
            viewModel.getSupplierList()
    }

    private fun listeners() {
        ivBack?.setOnClickListener {
            onBackPressed()
        }
        fb_current_loc?.setOnClickListener {
            updateCameraWithBearing()
        }
    }

    private fun initialise() {
        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment

        OnMapAndViewReadyListener(mapFragment, this@SuppliersMapFragment)
    }

    private fun supplierObserver() {
        // Create the observer which updates the UI.
        val supplierObserver = Observer<List<SupplierDataBean>> { resource ->
            addGoogleMarkers(resource)
        }

        // Observe the LiveData, passing in this activity as the LifecycleOwner and the observer.
        viewModel.nearBySupplierLiveData.observe(this, supplierObserver)

    }

    private fun addGoogleMarkers(resource: List<SupplierDataBean>?) {
        val array=ArrayList<SupplierDataBean>()
        array.addAll(resource?: arrayListOf())
        val minvalue=0.0
        val maxValue=0.0
        resource?.forEach {
            mMap.addMarker(MarkerOptions().position(LatLng(it.latitude?:0.0, it.longitude?:0.0)).title(it.name))
        }
        currentBearing = MapUtils.bearingBetweenLocations(
                LatLng(0.0,0.0),  LatLng(0.0,0.0)
        )
        updateCameraWithBearing()
    }

    //intialise Location location request
    private fun intialiseLocation() {
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
                        latlng = LatLng(location.latitude, location.longitude)
                        showCurrentMarker()
                    }
                    break
                }

                fusedLocationClient.removeLocationUpdates(locationCallback)
            }
        }
    }

    private fun setMapLocation() {

        if (!::mMap.isInitialized) return
        mMap.clear()

        with(mMap) {

            updateCameraWithBearing()
            mapType = GoogleMap.MAP_TYPE_NORMAL
        }
    }
    private fun updateCameraWithBearing() {
        val camPos = CameraPosition
                .builder(mMap.cameraPosition) // current Camera
                .bearing(currentBearing)
                .zoom(8f)
                .target(latlng ?: LatLng(0.0, 0.0))
                .build()
        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(camPos))
    }

    override fun onMapReady(googleMap: GoogleMap?) {
        mMap = googleMap ?: return

        with(mMap) {
            // Hide the zoom controls as the button panel will cover it.
            uiSettings.isZoomControlsEnabled = false
            uiSettings.isMyLocationButtonEnabled = true

            // Setting an info window adapter allows us to change the both the contents and
            // look of the info window.


            mapType = GoogleMap.MAP_TYPE_NORMAL
            setPadding(0, 10, 0, 0)
            uiSettings.isMapToolbarEnabled = true

            // Set listeners for marker events.  See the bottom of this class for their behavior.


            // Override the default content description on the view, for accessibility mode.
            // Ideally this string would be localised.
            setContentDescription("Map with lots of markers.")

             latlng?.let { setMapLocation() }
        }
    }


    private fun getLastLocation() {
        if (permissionUtil.locationTask(this)) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) !=
                    PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return
            }
            fusedLocationClient.requestLocationUpdates(mLocationRequest,
                    locationCallback,
                    null /* Looper */)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == AppConstants.REQUEST_CODE_LOCATION && resultCode == Activity.RESULT_OK) {
            getLastLocation()
        }
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
            AppConstants.REQUEST_CODE_LOCATION -> createLocationRequest()
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
                            AppConstants.REQUEST_CODE_LOCATION)
                } catch (sendEx: IntentSender.SendIntentException) {
                    // Ignore the error.
                }
            }
        }

    }
    private fun showCurrentMarker() {
        if (!::mMap.isInitialized) return

        if (currentLocMarker == null && latlng!=null) {
            currentLocMarker = mMap.addMarker(MarkerOptions().anchor(0.5f, 0.5f).position(latlng?:LatLng(0.0,0.0)).title("Your Current Location"))
            currentLocMarker?.setIcon((BitmapDescriptorFactory.fromResource(R.drawable.ic_nearby)))
        }
    }

    override fun onErrorOccur(message: String) {
        mBinding?.root?.onSnackbar(message)
    }

    override fun onSessionExpire() {
        openActivityOnTokenExpire()
    }

    override fun unFavSupplierResponse(data: SupplierList?) {

    }

    override fun favSupplierResponse(supplierId: SupplierList?) {
    }

    override fun onBranchList(supplierList: MutableList<SupplierList>) {
        //do nothing
    }

    override fun onDestroy() {
        super.onDestroy()
        fusedLocationClient.removeLocationUpdates(locationCallback)
    }



}