package com.codebrew.clikat.module.rental

import android.Manifest
import android.app.Activity
import android.app.TimePickerDialog
import android.app.TimePickerDialog.OnTimeSetListener
import android.content.Intent
import android.content.IntentSender
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TimePicker
import androidx.core.os.bundleOf
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.codebrew.clikat.BR
import com.codebrew.clikat.R
import com.codebrew.clikat.app_utils.AppUtils
import com.codebrew.clikat.app_utils.DateTimeUtils
import com.codebrew.clikat.app_utils.PermissionFile
import com.codebrew.clikat.app_utils.extension.onSnackbar
import com.codebrew.clikat.app_utils.navController
import com.codebrew.clikat.base.BaseFragment
import com.codebrew.clikat.data.constants.AppConstants
import com.codebrew.clikat.data.constants.AppConstants.Companion.REQUEST_CODE_LOCATION
import com.codebrew.clikat.data.model.others.HomeRentalParam
import com.codebrew.clikat.data.model.others.RentalDayModel
import com.codebrew.clikat.databinding.FragmentHomeRentalBinding
import com.codebrew.clikat.di.ViewModelProviderFactory
import com.codebrew.clikat.utils.configurations.Configurations
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import com.google.android.gms.maps.model.LatLng
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place.Field
import com.google.android.libraries.places.widget.Autocomplete
import com.google.android.libraries.places.widget.Autocomplete.IntentBuilder
import com.google.android.libraries.places.widget.AutocompleteActivity
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode
import kotlinx.android.synthetic.main.fragment_home_rental.*
import pub.devrel.easypermissions.AppSettingsDialog
import pub.devrel.easypermissions.EasyPermissions
import java.util.*
import javax.inject.Inject


private const val PLACE_PICKER_REQUEST = 1234

class HomeRental : BaseFragment<FragmentHomeRentalBinding, HomeRentalViewModel>(), OnTimeSetListener
        , HomeRentalNavigator,
        EasyPermissions.PermissionCallbacks {

    lateinit var mAdapter: DaysAdapter

    var daysList: MutableList<RentalDayModel>? = mutableListOf()

    var startDayName = ""
    var endDayName = ""

    val startDate by lazy { Calendar.getInstance(DateTimeUtils.timeLocale) }
    val endDate by lazy { Calendar.getInstance(DateTimeUtils.timeLocale) }

    var mType = "pickUp"

    private var pickUptHour = 0
    private var pickUpMinute = 0
    private var dropOffHour = 0
    private var dropOffMinute = 0


    var startLatLng = LatLng(0.0, 0.0)
    var endLatLng = LatLng(0.0, 0.0)


    @Inject
    lateinit var factory: ViewModelProviderFactory

    @Inject
    lateinit var appUtils: AppUtils

    @Inject
    lateinit var permissionUtil: PermissionFile

    private lateinit var viewModel: HomeRentalViewModel

    private var mBinding: FragmentHomeRentalBinding? = null

    private lateinit var timePicker: TimePickerDialog

    // Set the fields to specify which types of place data to return.
    private val fields = listOf(Field.ID, Field.NAME, Field.LAT_LNG)


    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var locationCallback: LocationCallback? = null
    private var locationRequest: LocationRequest? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel.navigator = this
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mBinding = viewDataBinding

        viewDataBinding.color = Configurations.colors

        settingDataLyt()

        calculateDayItem()

        intializePlace()

        tv_pickup.setOnClickListener {
            mType = "pickUp"
            openTimePicker(tv_pickup.text.toString())
        }

        tv_dropup.setOnClickListener {
            mType = "dropUp"
            openTimePicker(tv_dropup.text.toString())
        }

        materialCardView.setOnClickListener {
            openPlacePicker("pickUp")
        }

        drop_cardView.setOnClickListener {
            openPlacePicker("dropUp")
        }

        checkingLocationPermission()
    }

    override fun onDestroyView() {
        super.onDestroyView()

        if (!::fusedLocationClient.isInitialized) return

        fusedLocationClient.removeLocationUpdates(locationCallback)
    }

    private fun openPlacePicker(type: String) {
        mType = type
        // Start the autocomplete intent.
        val intent = IntentBuilder(
                AutocompleteActivityMode.FULLSCREEN, fields)
                .build(activity ?: requireActivity())
        startActivityForResult(intent, PLACE_PICKER_REQUEST)
    }


    //*********************Location Request Or Permission Enable*************************
    private fun checkingLocationPermission() {
        if (permissionUtil.hasLocation(activity ?: requireActivity())) {
            createLocationRequest()
        } else {
            permissionUtil.locationTaskFrag(this@HomeRental)
        }
    }
    //***********************************************************************************

    private fun createLocationRequest() {
        locationRequest = LocationRequest.create()?.apply {
            interval = 10000
            fastestInterval = 5000
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        }

        val builder = LocationSettingsRequest.Builder()
                .addLocationRequest(locationRequest!!)

        val client = LocationServices.getSettingsClient(activity ?: requireActivity())
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
                                    activity ?: requireActivity(),
                                    AppConstants.RC_LOCATION_PERM
                            )
                            Log.e("Data", "REQUEST_CHECK_SETTINGS")
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
                LocationServices.getFusedLocationProviderClient(activity ?: requireActivity())

        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult?) {
                if (locationResult == null) {
                    return
                }
                for (location in locationResult.locations) {
                    if (mType == "pickUp") {
                        progressBar?.visibility = View.VISIBLE

                        startLatLng = LatLng(location?.latitude ?: 0.0, location?.longitude ?: 0.0)

                        appUtils.getAddress(location?.latitude ?: 0.0, location?.longitude
                                ?: 0.0)?.getAddressLine(0).let {
                            progressBar.visibility = View.GONE
                            tvPickup.text = it
                        }
                    }
                    fusedLocationClient.removeLocationUpdates(locationCallback)
                    break
                }
            }
        }


        startLocationUpdates()
    }

    private fun startLocationUpdates() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (activity?.checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                    activity?.checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
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

    private fun intializePlace() {
        if (!Places.isInitialized()) {
            Places.initialize(activity?.applicationContext
                    ?: requireActivity(), appUtils.getMapKey())
        }
    }


    private fun openTimePicker(time: String) {

        val calendar = appUtils.getCalendarFormat(time, "hh:mm aa")

        timePicker.updateTime(calendar?.get(Calendar.HOUR_OF_DAY)
                ?: 11, calendar?.get(Calendar.MINUTE) ?: 12)
        timePicker.show()
    }

    private fun calculateDayItem() {
        if (!::mAdapter.isInitialized) return
        daysList?.clear()

        val cal = Calendar.getInstance()
        pickUptHour = cal.get(Calendar.HOUR_OF_DAY)
        pickUpMinute = cal.get(Calendar.MINUTE)
        tv_pickup.text = appUtils.convertDateOneToAnother(cal.time.toString(), "EEE MMM d HH:mm:ss Z yyyy", "hh:mm aa")


        for (i in 0..12) {
            cal.add(Calendar.DATE, 1)
            val rental = RentalDayModel()
            rental.dayId = i
            rental.dayName = appUtils.convertDateOneToAnother(cal.time.toString(), "EEE MMM d HH:mm:ss Z yyyy", "dd\nMMM")
                    ?: ""
            rental.dayFormt = appUtils.getCalendarFormat(cal.time.toString(), "EEE MMM d HH:mm:ss Z yyyy")
                    ?: Calendar.getInstance()
            daysList?.add(rental)
        }

        cal.add(Calendar.HOUR_OF_DAY, 1)
        dropOffHour = cal.get(Calendar.HOUR_OF_DAY)
        dropOffMinute = cal.get(Calendar.MINUTE)
        tv_dropup.text = appUtils.convertDateOneToAnother(cal.time.toString(), "EEE MMM d HH:mm:ss Z yyyy", "hh:mm aa")

        mAdapter.submitItemList(daysList)
    }

    private fun settingDataLyt() {
        mAdapter = DaysAdapter()

        timePicker = TimePickerDialog(activity, this, 14, 0, false)
        var dayRefStat: Boolean


        mAdapter.settingCallback(ItemListener {

            if (endDayName.isNotEmpty()) {
                endDayName = ""
                startDayName = ""

                dayRefStat = false

                daysList?.forEachIndexed { index, rentalDayModel ->
                    rentalDayModel.startDate = ""
                    rentalDayModel.dayStatus = false
                    rentalDayModel.endDate = ""
                }
            } else {
                dayRefStat = true
            }


            if (startDayName.isNotEmpty()) {
                endDate.time = it.dayFormt.time
                if (startDate.before(endDate)) {
                    it.dayStatus = !it.dayStatus
                    endDayName = it.dayName
                    it.endDate = "End Date"
                } else if (startDate == endDate) {
                    endDayName = it.dayName
                    it.startDate = "Start Date\nEnd Date"
                }
            }

            if (dayRefStat && startDayName.isEmpty()) {
                startDate.time = it.dayFormt.time
                it.dayStatus = !it.dayStatus
                startDayName = it.dayName
                it.startDate = "Start Date"
            } else {
                dayRefStat = true
            }


            if (startDayName.isNotEmpty() && endDayName.isNotEmpty()) {
                for (x in daysList?.indexOfFirst { it.startDate.isNotEmpty() }!! until daysList?.indexOfFirst { it.endDate.isNotEmpty() }!!) {
                    val dayModel = daysList!![x]
                    dayModel.dayStatus = true
                    daysList?.set(x, dayModel)
                }
            }
            mAdapter.notifyDataSetChanged()

        })

        recyclerView.layoutManager = LinearLayoutManager(activity, RecyclerView.HORIZONTAL, false)
        recyclerView.adapter = mAdapter
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == PLACE_PICKER_REQUEST && resultCode == Activity.RESULT_OK) {
            val place = Autocomplete.getPlaceFromIntent(data!!)
            if (place.latLng != null) {
                val lat = place.latLng?.latitude
                val lng = place.latLng?.longitude




                if (mType == "pickUp") {

                    startLatLng = LatLng(lat ?: 0.0, lng ?: 0.0)

                    progressBar.visibility = View.VISIBLE
                    appUtils.getAddress(lat ?: 0.0, lng ?: 0.0)?.getAddressLine(0).let {
                        progressBar.visibility = View.GONE
                        tvPickup.text = it
                    }
                } else {
                    endLatLng = LatLng(lat ?: 0.0, lng ?: 0.0)
                    pbDropup.visibility = View.VISIBLE

                    appUtils.getAddress(lat ?: 0.0, lng ?: 0.0)?.getAddressLine(0).let {
                        pbDropup.visibility = View.GONE
                        tvDropup.text = it
                    }
                }
            }
        } else if (resultCode == AutocompleteActivity.RESULT_ERROR) {
            val status = Autocomplete.getStatusFromIntent(data!!)
            //  Log.i(TAG, status.getStatusMessage());
        } else if (resultCode == AutocompleteActivity.RESULT_CANCELED) { // The user canceled the operation.

        } else if (requestCode == AppConstants.RC_LOCATION_PERM) {
            getCurrentLocation()
        }
    }

    override fun onTimeSet(p0: TimePicker?, p1: Int, p2: Int) {


        if (mType == "pickUp") {
            pickUptHour = p1
            pickUpMinute = p2
            tv_pickup.text = appUtils.convertDateOneToAnother("$p1:$p2", "HH:mm", "hh:mm aa")
            dropOffHour = p1.plus(1)
            dropOffMinute = p2
            tv_dropup.text = appUtils.convertDateOneToAnother("$p1:$p2", "HH:mm", "hh:mm aa")
        } else {
            dropOffHour = p1
            dropOffMinute = p2
            tv_dropup.text = appUtils.convertDateOneToAnother("$p1:$p2", "HH:mm", "hh:mm aa")
        }
    }


    override fun getBindingVariable(): Int {
        return BR.viewModel
    }

    override fun getLayoutId(): Int {
        return R.layout.fragment_home_rental
    }

    override fun getViewModel(): HomeRentalViewModel {
        viewModel = ViewModelProviders.of(this, factory).get(HomeRentalViewModel::class.java)
        return viewModel
    }

    override fun onHomeRental() {
        if (isNetworkConnected && validateHomeData(startDate, endDate, startDayName, endDayName)) {

            val mHomeParam = HomeRentalParam(startLatLng.latitude, startLatLng.longitude, endLatLng.latitude, endLatLng.longitude,
                    appUtils.convertDateOneToAnother(startDate.time.toString(), "EEE MMM d HH:mm:ss Z yyyy", "yyyy-MM-dd HH:mm:ss")
                            ?: "",
                    appUtils.convertDateOneToAnother(endDate.time.toString(), "EEE MMM d HH:mm:ss Z yyyy", "yyyy-MM-dd HH:mm:ss")
                            ?: "",
                    if (radioButton?.isChecked == true) 1 else 0, tvPickup.text.toString(),
                    appUtils.convertDateOneToAnother(startDate.time.toString(), "EEE MMM d HH:mm:ss Z yyyy", "MMM dd").plus(" - ")
                            .plus(appUtils.convertDateOneToAnother(endDate.time.toString(), "EEE MMM d HH:mm:ss Z yyyy", "MMM dd")
                                    ?: "")
                    , "", "", if (tvDropup.text.toString().equals("Choose your Drop-off location")) "" else tvDropup.text.toString())

            val bundle = bundleOf("intputData" to mHomeParam)
            navController(this@HomeRental).navigate(R.id.action_homeRental_to_productListFrag, bundle)
        }
    }

    override fun onErrorOccur(message: String) {
        mBinding?.root?.onSnackbar(message)
    }

    override fun onSessionExpire() {
        openActivityOnTokenExpire()
    }


    override fun onPermissionsDenied(requestCode: Int, perms: MutableList<String>) {
        if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
            AppSettingsDialog.Builder(this).build().show()
        }
    }


    override fun onRequestPermissionsResult(
            requestCode: Int,
            permissions: Array<String>,
            grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        // EasyPermissions handles the request result.
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this)
    }

    override fun onPermissionsGranted(requestCode: Int, perms: MutableList<String>) {

        if (requestCode == REQUEST_CODE_LOCATION) {
            if (isNetworkConnected) {
                createLocationRequest()
            }
        }
    }

    private fun validateHomeData(startDate: Calendar?, endDate: Calendar?, startDayName: String, endDayName: String): Boolean {

        if (startDayName.isNotEmpty()) {
            startDate?.set(Calendar.HOUR_OF_DAY, pickUptHour)
            startDate?.set(Calendar.MINUTE, pickUpMinute)
        }

        if (endDayName.isNotEmpty()) {
            endDate?.set(Calendar.HOUR_OF_DAY, dropOffHour)
            endDate?.set(Calendar.MINUTE, dropOffMinute)
        }

        return when {
            startDayName.isEmpty() -> {
                mBinding?.root?.onSnackbar("Select Start Date")
                false
            }
            endDayName.isEmpty() -> {
                mBinding?.root?.onSnackbar("Select End Date")
                false
            }
            startDate?.before(endDate) == false -> {
                mBinding?.root?.onSnackbar("Select Valid Date")
                false
            }
            else -> {
                true
            }
        }
    }


}
