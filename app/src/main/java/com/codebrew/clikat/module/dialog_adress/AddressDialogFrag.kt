package com.codebrew.clikat.module.dialog_adress

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.IntentSender
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.PopupMenu
import androidx.annotation.NonNull
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView.ItemDecoration
import com.codebrew.clikat.BuildConfig
import com.codebrew.clikat.R
import com.codebrew.clikat.app_utils.AppUtils
import com.codebrew.clikat.app_utils.CommonUtils
import com.codebrew.clikat.app_utils.CommonUtils.Companion.isNetworkConnected
import com.codebrew.clikat.app_utils.DateTimeUtils
import com.codebrew.clikat.app_utils.PermissionFile
import com.codebrew.clikat.app_utils.extension.afterTextChanged
import com.codebrew.clikat.base.BaseDialog
import com.codebrew.clikat.data.constants.AppConstants
import com.codebrew.clikat.data.constants.PrefenceConstants
import com.codebrew.clikat.data.model.api.AddressBean
import com.codebrew.clikat.data.model.api.DataBean
import com.codebrew.clikat.data.model.api.ZoneInformation
import com.codebrew.clikat.data.model.api.vehicleDetails.DeleteData
import com.codebrew.clikat.data.model.api.vehicleDetails.VehicleData
import com.codebrew.clikat.data.model.others.MapInputParam
import com.codebrew.clikat.data.model.others.MapVehicleInputParam
import com.codebrew.clikat.data.preferences.PreferenceHelper
import com.codebrew.clikat.databinding.FragmentDialogAddressBinding
import com.codebrew.clikat.di.ViewModelProviderFactory
import com.codebrew.clikat.modal.DataCommon
import com.codebrew.clikat.modal.other.SettingModel
import com.codebrew.clikat.module.cart.AddVehicleActivity
import com.codebrew.clikat.module.dialog_adress.adapter.AddressAdapter
import com.codebrew.clikat.module.dialog_adress.adapter.AddressListener
import com.codebrew.clikat.module.dialog_adress.adapter.PlacesAutoCompleteAdapter
import com.codebrew.clikat.module.dialog_adress.adapter.VehicleDEtailAdapter
import com.codebrew.clikat.module.dialog_adress.interfaces.AddressDialogListener
import com.codebrew.clikat.preferences.DataNames
import com.codebrew.clikat.utils.configurations.Configurations
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import dagger.android.support.AndroidSupportInjection
import kotlinx.android.synthetic.main.activity_address_new.*
import kotlinx.android.synthetic.main.activity_select_loc.*
import kotlinx.android.synthetic.main.activity_select_loc.ed_extra_adrs
import kotlinx.android.synthetic.main.fragment_cart_new.*
import kotlinx.android.synthetic.main.fragment_dialog_address.*
import kotlinx.android.synthetic.main.layout_add_vehicle.*
import pub.devrel.easypermissions.AppSettingsDialog
import pub.devrel.easypermissions.EasyPermissions
import retrofit2.Retrofit
import java.io.IOException
import javax.inject.Inject

const val LOCATION_PARAM = 157
const val VEHICLE_PARAM = 158
private const val ARG_PARAM1 = "branchId"
private const val ARG_PARAM2 = "user_id"

class AddressDialogFragment : BaseDialog(), PlacesAutoCompleteAdapter.ClickListener,
    AddressNavigator, EasyPermissions.PermissionCallbacks {
  var  screen_val = "1"
    private var mListener: AddressDialogListener? = null

    var tag1 = "1"
    private lateinit var addressAdapter: AddressAdapter
    private var addresslist = mutableListOf<AddressBean>()

    private var addressId = 0

    @Inject
    lateinit var factory: ViewModelProviderFactory

    @Inject
    lateinit var retrofit: Retrofit

    @Inject
    lateinit var prefHelper: PreferenceHelper

    @Inject
    lateinit var appUtils: AppUtils

    @Inject
    lateinit var permissionFile: PermissionFile

    lateinit var addressBean: AddressBean

    private var mAddressViewModel: AddressViewModel? = null

    private var vechicle_List: List<VehicleData> = ArrayList()
    lateinit var vehicledEtailAdapter: VehicleDEtailAdapter
    private var mSupplierId = 0

    private var userId = 0

    private var self_pickUp = ""
    private var user_id = ""
    private var mAddressData: DataBean? = null

    var settingData: SettingModel.DataBean.SettingData? = null

    private lateinit var mLocationRequest: LocationRequest
    private lateinit var locationCallback: LocationCallback
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    private var mCurrentLocation: Location? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val binding = DataBindingUtil.inflate<FragmentDialogAddressBinding>(
            inflater,
            R.layout.fragment_dialog_address,
            container,
            false
        )
        AndroidSupportInjection.inject(this)
        mAddressViewModel = ViewModelProviders.of(this, factory).get(AddressViewModel::class.java)
        binding.viewModel = mAddressViewModel
        binding.color = Configurations.colors

        mAddressViewModel?.navigator = this

        //set to adjust screen height automatically, when soft keyboard appears on screen
        dialog?.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE or WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

        return binding.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.let {
            mSupplierId = it.getInt(ARG_PARAM1)
            userId = it.getInt(ARG_PARAM2)
        }

        intializeLocation()

    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (screen_val.equals("0")) {
            val mLocUser = prefHelper.getGsonValue(PrefenceConstants.ADRS_DATA, AddressBean::class.java)

            val param = java.util.HashMap<String, String>()
            param["languageId"] = "14"

            param["latitude"] = mLocUser!!.latitude.toString()
            param["longitude"] = mLocUser!!.longitude.toString()
            if (prefHelper.getCurrentUserLoggedIn()) {
                param["accessToken"] = prefHelper.getKeyValue(
                        PrefenceConstants.ACCESS_TOKEN,
                        PrefenceConstants.TYPE_STRING
                ).toString()
            }
            if (isNetworkConnected) {
                CommonUtils.setBaseUrl(BuildConfig.BASE_URL, retrofit)
                mAddressViewModel?.getAddressList(mSupplierId)
            }

        }
        if (requestCode == LOCATION_PARAM) {

            if (resultCode == Activity.RESULT_OK) {
                val mapInputParam = data?.getParcelableExtra<MapInputParam>("mapParam")

                // tvArea.text = mapInputParam?.first_address.toString()

                var hashMap: HashMap<String, String>? = null

                when (mapInputParam?.requestType) {
                    "add", "edit", "current" -> {

                        hashMap = HashMap()
                        // hashMap["name"] = prefHelper.getKeyValue(PrefenceConstants.USER_NAME, PrefenceConstants.TYPE_STRING).toString()
                        hashMap["latitude"] = mapInputParam.latitude
                        hashMap["longitude"] = mapInputParam.longitude
                        hashMap["zone_id"] = mapInputParam.zone_id!!
                        hashMap["addressLineFirst"] = mapInputParam.first_address
                        hashMap["customer_address"] = mapInputParam.second_address
                        hashMap["is_real_address"]="0"
                        hashMap["address_type"] =""
                        hashMap["area"] = mapInputParam.area?:""
                        hashMap["street"] =mapInputParam.street?:""
                        hashMap["building"] =mapInputParam.building?:""
                        hashMap["floor"] =mapInputParam.floor?:""
                        hashMap["appartment"] =mapInputParam.appartment?:""
                        if (mapInputParam.name?.isNotEmpty() == true)
                            hashMap["name"] = mapInputParam.name ?: ""
                        if (mapInputParam.phone_number?.isNotEmpty() == true) {
                            hashMap["phone_number"] = mapInputParam.phone_number ?: ""
                            hashMap["country_code"] = mapInputParam.country_code ?: ""
                        }
                        if (!mapInputParam.reference_address.isNullOrEmpty()) {
                            hashMap["reference_address"] = mapInputParam.reference_address ?: ""
                        }
                    }
                }


                if (isNetworkConnected(activity?.applicationContext ?: requireContext())) {

                    if (prefHelper.getCurrentUserLoggedIn()) {
                        when (mapInputParam?.requestType) {
                            "add", "current" -> {
                                hashMap?.let {
                                    mAddressViewModel?.addAddress(it)
                                }
                            }
                            "edit" -> {
                                hashMap?.set("addressId", mapInputParam.addressId)
                                hashMap?.let { mAddressViewModel?.editAddress(it) }
                            }
                        }
                    } else {
                        addressBean = AddressBean()
                        addressBean.latitude = mapInputParam?.latitude ?: ""
                        addressBean.longitude = mapInputParam?.longitude ?: ""
                        addressBean.customer_address = """${
                            mapInputParam?.second_address
                                ?: ""
                        } , ${mapInputParam?.first_address ?: ""}"""
                        mListener?.onAddressSelect(addressBean)
                        dismiss()
                    }

                }

            }
        } else {
            if (resultCode == Activity.RESULT_OK) {
                mAddressViewModel?.getUserVehicle(user_id)

                val vehicleInputParam =
                    data?.getParcelableExtra<MapVehicleInputParam>("vehicleParam")
                mListener?.onVehicleSelect(
                    vehicleInputParam?.id.toString(),
                    vehicleInputParam?.modelNo.toString(),
                    vehicleInputParam?.color.toString(),
                    vehicleInputParam?.plateNo.toString()
                )

            }
        }

    }

    private fun userVehicleObserver() {

        // Create the observer which updates the UI.
        val vehicleObserver = Observer<List<VehicleData>> { resource ->

            vechicle_List = resource
            vehicledEtailAdapter = VehicleDEtailAdapter(
                vechicle_List,
                object : VehicleDEtailAdapter.Callback {
                    override fun onItemClicked(position: Int) {
                        mListener?.onVehicleSelect(
                            vechicle_List.get(position)?.id.toString(),
                            vechicle_List.get(position)?.name.toString(),
                            vechicle_List.get(position)?.color.toString(),
                            vechicle_List.get(position)?.number_plate.toString()
                        )
                        dismiss()
                    }

                    override fun onDeleteClicked(id: Int) {
                        mAddressViewModel?.deleteUserVehicle(id.toString(), user_id)
                    }

                })
            rv_addressList.layoutManager = LinearLayoutManager(activity)
            rv_addressList.scrollToPosition(0)
            rv_addressList.adapter = vehicledEtailAdapter

        }


        mAddressViewModel?.liveUserDeatil?.observe(this, vehicleObserver)
    }

    fun deleteObserver() {
        // Create the observer which updates the UI.
        val catObserver = Observer<DeleteData> { resource ->
            mAddressViewModel?.getUserVehicle(user_id)
        }

        // Observe the LiveData, passing in this activity as the LifecycleOwner and the observer.
        mAddressViewModel?.vehicleDeleteLiveData?.observe(this, catObserver)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        Places.initialize(activity ?: requireContext(), appUtils.getMapKey())
        addressObserver()
        editAddressObserver()
        deleteAddressObserver()
        addAddressObserver()
        userVehicleObserver()
        deleteObserver()
        settingData = prefHelper.getGsonValue(
            DataNames.SETTING_DATA,
            SettingModel.DataBean.SettingData::class.java
        )

        self_pickUp =
            if (prefHelper.getKeyValue(AppConstants.ROAD_MAP_TYPE, PrefenceConstants.TYPE_STRING)
                    .toString()
                    .equals("Delivery")
            ) {
                "8"
            } else {
                "9"
            }

        cn_search.visibility = if (prefHelper.getCurrentUserLoggedIn()) {
            View.VISIBLE
        } else {
            View.GONE
        }

        if (permissionFile.locationTask(activity?:requireActivity())) {
            createLocationRequest()
        }

        if (self_pickUp.equals("8") || userId == 0) {
            iv_add.visibility = View.GONE
            ic_search.visibility = View.VISIBLE
            ed_search.visibility = View.VISIBLE
            addressAdapter = AddressAdapter("0",settingData, AddressListener(
                    { model ->
//                        if (model.isUnderZone == 1) {
                            if (!::addressBean.isInitialized) {
                                addressBean = AddressBean()
                                addressBean = model
                            } else {
                                addressBean = model
                            }

                            addressBean.user_service_charge = mAddressData?.user_service_charge
                            addressBean.preparation_time = mAddressData?.preparation_time
                            addressBean.min_order = mAddressData?.min_order
                            addressBean.base_delivery_charges = mAddressData?.base_delivery_charges

                            // loadMapScreen("select", addressBean)
                            mListener?.onAddressSelect(addressBean)
                            dismissDialog("dialog")
//                        }
                    },
                { adapterView, addressBean ->

                    val popup = PopupMenu(activity, adapterView)
                    //inflating menu from xml resource
                    popup.inflate(R.menu.popup_address)
                    //adding click listener


                    popup.setOnMenuItemClickListener { menuItem ->

                        when (menuItem.itemId) {
                            R.id.menu_edit -> {
                                loadMapScreen("edit", addressBean)
                            }

                            R.id.menu_delete -> {
                                if (isNetworkConnected) {

                                    addressId = addressBean.id

                                    mAddressViewModel?.deleteAddress(addressBean.id.toString())
                                }
                            }
                        }
                        true
                    }

                    //displaying the popup
                    popup.show()
                }, { mType ->
                    when (mType) {
                        "footer" -> {
                            loadMapScreen("add", null)
                        }
                        "header" -> {
                            if (mCurrentLocation == null) {
                                getLastLocation()
                                return@AddressListener
                            }

                            addressBean = AddressBean()
                            addressBean.latitude = mCurrentLocation?.latitude.toString() ?: ""
                            addressBean.longitude = mCurrentLocation?.longitude.toString() ?: ""
                            addressBean.customer_address = currentLocation(
                                mCurrentLocation?.latitude ?: 0.0,
                                mCurrentLocation?.longitude ?: 0.0
                            )
                            mListener?.onAddressSelect(addressBean)
                            dismissDialog("dialog")
                            // loadMapScreen("current", null)
                        }
                    }
                })
            )


            rv_addressList.adapter = addressAdapter
            rv_addressList.scrollToPosition(0)

            val itemDecoration: ItemDecoration =
                DividerItemDecoration(activity, DividerItemDecoration.VERTICAL)
            rv_addressList.addItemDecoration(itemDecoration)


            val mAutoCompleteAdapter = PlacesAutoCompleteAdapter(activity, settingData)
            mAutoCompleteAdapter.setClickListener(this)

            ed_search.afterTextChanged {
                if (it.isEmpty()) {
                    rv_addressList.adapter = addressAdapter
                } else {
                    if (rv_addressList.adapter == addressAdapter) {
                        rv_addressList.adapter = mAutoCompleteAdapter
                        rv_addressList.scrollToPosition(0)
                    }

                    mAutoCompleteAdapter.filter.filter(it)
                }

            }

            /*  ed_search.setOnFocusChangeListener { v, hasFocus ->
              if (v == ed_search) {
                  if (!hasFocus) {
                      hideKeyboard()
                  }
              }
          }*/


            addressAdapter.addHeaderAndSubmitList(null, prefHelper.getCurrentUserLoggedIn())
            val mLocUser = prefHelper.getGsonValue(PrefenceConstants.ADRS_DATA, AddressBean::class.java)

            val param = java.util.HashMap<String, String>()
            param["languageId"] = "14"

            param["latitude"] = mLocUser!!.latitude.toString()
            param["longitude"] = mLocUser!!.longitude.toString()
            if (prefHelper.getCurrentUserLoggedIn()) {
                param["accessToken"] = prefHelper.getKeyValue(
                        PrefenceConstants.ACCESS_TOKEN,
                        PrefenceConstants.TYPE_STRING
                ).toString()
            }
//            if (prefHelper.getCurrentUserLoggedIn()) {
//                mAddressViewModel!!.getAddressJson(param,true)
//            } else {
//                mAddressViewModel!!.getAddressList(param,true)
//            }

            if (isNetworkConnected) {
                CommonUtils.setBaseUrl(BuildConfig.BASE_URL, retrofit)
                mAddressViewModel?.getAddressList(mSupplierId)
            }

        } else {
            iv_add.visibility = View.VISIBLE
            ic_search.visibility = View.GONE
            ed_search.visibility = View.GONE

            tv_title.setText(getString(R.string.choose_vehi))
            user_id =
                prefHelper.getKeyValue(PrefenceConstants.USER_ID, PrefenceConstants.TYPE_STRING)
                    ?.toString() ?: ""

            mAddressViewModel?.getUserVehicle(user_id)
        }
        iv_add.setOnClickListener {
            val intent = Intent(activity, AddVehicleActivity::class.java)
            startActivityForResult(intent, VEHICLE_PARAM)
        }
        iv_close.setOnClickListener {
            dismiss()
        }

    }


    override fun onAttach(context: Context) {
        super.onAttach(context)
        val parent = parentFragment
        mListener = if (parent != null) {
            parent as AddressDialogListener
        } else {
            context as AddressDialogListener
        }
    }

    override fun onDetach() {
        mListener = null
        super.onDetach()
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)

        if (!::addressBean.isInitialized) {
            mListener?.onDestroyDialog()
        }
    }


    private fun addressObserver() {
        // Create the observer which updates the UI.
        val catObserver = Observer<DataBean> { resource ->

            mAddressData = resource

            addresslist.clear()

            addresslist.addAll(resource.address!!)

            addresslist.sortByDescending { it.id }

            addressAdapter.addHeaderAndSubmitList(addresslist, prefHelper.getCurrentUserLoggedIn())
        }

        // Observe the LiveData, passing in this activity as the LifecycleOwner and the observer.
        mAddressViewModel?.addressLiveData?.observe(this, catObserver)
    }

    private fun editAddressObserver() {
        // Create the observer which updates the UI.
        val catObserver = Observer<AddressBean> { resource ->

            addresslist.mapIndexed { index, addressBean ->

                if (resource.id == addressBean.id) {
                    addresslist[index] = resource
                }
            }
            addressAdapter.addHeaderAndSubmitList(addresslist, prefHelper.getCurrentUserLoggedIn())
            mListener?.onAddressSelect(addressBean)
            dismissDialog("dialog")
        }

        // Observe the LiveData, passing in this activity as the LifecycleOwner and the observer.
        mAddressViewModel?.updateAdrsLiveData?.observe(this, catObserver)
    }


    private fun deleteAddressObserver() {
        // Create the observer which updates the UI.
        val catObserver = Observer<DataCommon> { resource ->


            val updated_list = addresslist.filter { it.id != addressId }

            addresslist.clear()

            addresslist.addAll(updated_list)

            addressAdapter.addHeaderAndSubmitList(addresslist, prefHelper.getCurrentUserLoggedIn())
        }

        // Observe the LiveData, passing in this activity as the LifecycleOwner and the observer.
        mAddressViewModel?.deleteAdrsData?.observe(this, catObserver)
    }


    private fun addAddressObserver() {
        // Create the observer which updates the UI.
        val catObserver = Observer<AddressBean> { resource ->

            ed_search.setText("")

            // addresslist.add(resource)

            // addresslist.sortByDescending { it.id }

            //  addressAdapter.addHeaderAndSubmitList(addresslist, prefHelper.getCurrentUserLoggedIn())


            if (!::addressBean.isInitialized) {
                addressBean = AddressBean()
                addressBean = resource
            } else {
                addressBean = resource
            }

            addressBean.user_service_charge = mAddressData?.user_service_charge
            addressBean.preparation_time = mAddressData?.preparation_time
            addressBean.min_order = mAddressData?.min_order
            addressBean.base_delivery_charges = mAddressData?.base_delivery_charges

            // loadMapScreen("select", addressBean)
            mListener?.onAddressSelect(addressBean)
            dismissDialog("dialog")
        }

        // Observe the LiveData, passing in this activity as the LifecycleOwner and the observer.
        mAddressViewModel?.addAdrsLiveData?.observe(this, catObserver)
    }


    private fun loadMapScreen(type: String, adrsData: AddressBean?) {
if(!type.equals("add"))
{
        if (adrsData != null) {
            val mapParam = MapInputParam(
                    adrsData?.latitude!!.toString(),
                    adrsData?.longitude!!.toString(),
                    adrsData?.address_line_1!!,
                    "",
                    "",
                    adrsData?.id.toString(),
                    area = adrsData?.area ?: "",
                    floor = adrsData?.floor ?: "",
                    address_type = adrsData?.address_type ?: "",
                    appartment = adrsData?.appartment ?: "",
                    building = adrsData?.building ?: "",
                    street = adrsData?.street ?: ""

            )
            mapParam.reference_address = "others"
            screen_val = "0"
            val intent = Intent(activity, newlocActivity::class.java)
            intent.putExtra("type", type)
            intent.putExtra("screen_val", screen_val)

            intent.putExtra("mapParam", mapParam)
            startActivityForResult(intent, LOCATION_PARAM)
        }       }else{
            screen_val = "0"


            val intent = Intent(activity, SelectlocActivity::class.java)
            intent.putExtra("type", type)
    if (adrsData != null) {
        intent.putExtra("addressData", adrsData)
    }
            intent.putExtra("screen_val", screen_val)
            startActivityForResult(intent, LOCATION_PARAM)
        }

    }

    override fun click(place: Place?) {

        if (!::addressBean.isInitialized) {
            addressBean = AddressBean()
        }
        addressBean.latitude = place?.latLng?.latitude?.toString()
        addressBean.longitude = place?.latLng?.longitude?.toString()
        addressBean.address_line_1 = place?.address

        loadMapScreen("add", addressBean)
    }

    companion object {
        fun newInstance(supplierId: Int, user_id: Int): AddressDialogFragment =
            AddressDialogFragment().apply {
                arguments = Bundle().apply {
                    putInt(ARG_PARAM1, supplierId)
                    putInt(ARG_PARAM2, user_id)
                }
            }


    }

    override fun onErrorOccur(message: String) {

    }

    override fun onSessionExpire() {
        openActivityOnTokenExpire()
    }

    //intialize locatin request
    private fun intializeLocation() {
        mLocationRequest = LocationRequest()
        mLocationRequest.interval = 10000
        mLocationRequest.fastestInterval = 5000
        mLocationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY

        fusedLocationClient =
            LocationServices.getFusedLocationProviderClient(activity ?: requireActivity())


        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult?) {
                locationResult ?: return
                for (location in locationResult.locations) {
                    if (location != null) {
                        mCurrentLocation = location
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

        val client = activity?.let { LocationServices.getSettingsClient(it) }
        val task = client?.checkLocationSettings(builder.build())


        task?.addOnSuccessListener { locationSettingsResponse ->
            // All location settings are satisfied. The client can initialize
            // location requests here.
            getLastLocation()
        }

        task?.addOnFailureListener { exception ->
            if (exception is ResolvableApiException) {
                // Location settings are not satisfied, but this can be fixed
                // by showing the user a dialog.
                try {
                    // Show the dialog by calling startResolutionForResult(),
                    // and check the result in onActivityResult().
                    exception.startResolutionForResult(
                        activity,
                        AppConstants.REQUEST_CODE_LOCATION
                    )
                } catch (sendEx: IntentSender.SendIntentException) {
                    // Ignore the error.
                }
            }
        }

    }

    @SuppressLint("MissingPermission")
    private fun getLastLocation() {

        if (permissionFile.locationTask(activity ?: requireActivity())) {

            fusedLocationClient.requestLocationUpdates(
                mLocationRequest,
                locationCallback,
                null /* Looper */
            )
        }
    }

    private fun currentLocation(lat: Double, lng: Double): String {
        val geocoder = Geocoder(activity, DateTimeUtils.timeLocale)

        try {
            val addressList: List<Address> = geocoder.getFromLocation(lat, lng, 1)
            if (addressList.isNotEmpty()) {
                return addressList[0].getAddressLine(0)
            }
        } catch (e: IOException) {
            return ""
        }

        return ""
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
            AppConstants.REQUEST_CODE_LOCATION -> createLocationRequest()
        }
    }

    override fun onZoneData(zoneInf: ZoneInformation?, isAddressUpdate: Boolean) {
        if (isNetworkConnected) {
            CommonUtils.setBaseUrl(BuildConfig.BASE_URL, retrofit)
            mAddressViewModel?.getAddressList1(mSupplierId,zoneInfo=zoneInf!!.id.toString(),value="")
        }



    }




}
