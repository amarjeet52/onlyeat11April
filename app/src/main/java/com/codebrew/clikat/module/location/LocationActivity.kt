package com.codebrew.clikat.module.location

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.IntentSender
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.Window
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.codebrew.clikat.BuildConfig
import com.codebrew.clikat.R
import com.codebrew.clikat.app_utils.AppUtils
import com.codebrew.clikat.app_utils.CommonUtils
import com.codebrew.clikat.app_utils.PermissionFile
import com.codebrew.clikat.data.DataManager
import com.codebrew.clikat.data.constants.AppConstants
import com.codebrew.clikat.data.constants.PrefenceConstants
import com.codebrew.clikat.data.model.api.AddressBean
import com.codebrew.clikat.data.preferences.PreferenceHelper
import com.codebrew.clikat.databinding.ActivityLocationBinding
import com.codebrew.clikat.modal.AppGlobal.Companion.localeManager
import com.codebrew.clikat.modal.other.SettingModel
import com.codebrew.clikat.module.dialog_adress.AddressDialogFragment
import com.codebrew.clikat.module.dialog_adress.interfaces.AddressDialogListener
import com.codebrew.clikat.module.dialog_adress.v2.AddressDialogFragmentV2
import com.codebrew.clikat.preferences.DataNames
import com.codebrew.clikat.utils.configurations.Configurations
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import com.google.gson.Gson
import dagger.android.AndroidInjection
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.HasAndroidInjector
import kotlinx.android.synthetic.main.activity_location.*
import kotlinx.android.synthetic.main.activity_splash.*
import pub.devrel.easypermissions.AppSettingsDialog
import pub.devrel.easypermissions.EasyPermissions
import javax.inject.Inject

class LocationActivity : AppCompatActivity(), EasyPermissions.PermissionCallbacks, AddressDialogListener, HasAndroidInjector {

    @Inject
    lateinit var permissionUtil: PermissionFile

    @Inject
    lateinit var prefHelper: PreferenceHelper
    @Inject
    lateinit var dataManager: DataManager

    @Inject
    lateinit var appUtils: AppUtils

    @Inject
    lateinit var androidInjector: DispatchingAndroidInjector<Any>


    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var locationCallback: LocationCallback? = null
    private var locationRequest: LocationRequest? = null
    var settingData: SettingModel.DataBean.SettingData? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)

        val binding: ActivityLocationBinding = ActivityLocationBinding.inflate(layoutInflater)
        binding.color = Configurations.colors
        setContentView(binding.root)

        settingData = dataManager.getGsonValue(DataNames.SETTING_DATA, SettingModel.DataBean.SettingData::class.java)
        btn_manual_loc.setOnClickListener {
            checkingLocationPermission()
        }

        tvNotNow.setOnClickListener {

            val defaultAdrs=prefHelper.getGsonValue(DataNames.DEFAULT_ADRS,SettingModel.DataBean.DefaultAddres::class.java)

            appUtils.getAddress(defaultAdrs?.latitude ?: 0.0, defaultAdrs?.longitude
                    ?: 0.0)?.getAddressLine(0).let {
                val locUser = AddressBean(latitude = (defaultAdrs?.latitude
                        ?: 0.0).toString(), longitude = (defaultAdrs?.longitude
                        ?: 0.0).toString(), customer_address = it)

                appUtils.setUserLocale(locUser)
                prefHelper.addGsonValue(PrefenceConstants.ADRS_DATA, Gson().toJson(locUser))

                onAdrressValidate()
            }
        }
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

                    appUtils.getAddress(location?.latitude ?: 0.0, location?.longitude
                            ?: 0.0)?.getAddressLine(0).let {
                        val locUser = AddressBean(latitude = (location?.latitude
                                ?: 0.0).toString(), longitude = (location?.longitude
                                ?: 0.0).toString(), customer_address = it)

                        appUtils.setUserLocale(locUser)
                        prefHelper.addGsonValue(PrefenceConstants.ADRS_DATA, Gson().toJson(locUser))

                        Log.e("loc_data",Gson().toJson(locUser).toString())
                    }

                    fusedLocationClient.removeLocationUpdates(locationCallback)
                    break
                }

                onAdrressValidate()

            }
        }


        startLocationUpdates()
    }


    private fun onAdrressValidate() {
        val settingBean = prefHelper.getGsonValue(DataNames.SETTING_DATA, SettingModel.DataBean.SettingData::class.java)
        val isGuest = settingBean?.login_template.isNullOrEmpty() || settingBean?.login_template == "0"
        if (isGuest) {
            appUtils.checkHomeActivity(this, intent.extras ?: Bundle.EMPTY)
        } else {
            appUtils.checkLoginFlow(this@LocationActivity, -1)
        }
        finish()
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
            if (CommonUtils.isNetworkConnected(applicationContext)) {
                createLocationRequest()
            }
        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Activity.RESULT_OK && requestCode == AppConstants.RC_LOCATION_PERM) {
            getCurrentLocation()
        }
    }

    override fun onAddressSelect(adrsBean: AddressBean) {

        prefHelper.addGsonValue(PrefenceConstants.ADRS_DATA, Gson().toJson(adrsBean))

        onAdrressValidate()
    }

    override fun onDestroyDialog() {

    }
    override fun onVehicleSelect(id:String,model: String, color: String, plate_no: String) {
        TODO("Not yet implemented")
    }
    override fun androidInjector(): AndroidInjector<Any> {
        return androidInjector
    }

    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base?.let { localeManager?.setLocale(it) })
    }
}
