package com.codebrew.clikat.module.dialog_adress

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.PopupMenu
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView.ItemDecoration
import com.codebrew.clikat.BR
import com.codebrew.clikat.BuildConfig
import com.codebrew.clikat.R
import com.codebrew.clikat.app_utils.AppUtils
import com.codebrew.clikat.app_utils.CommonUtils
import com.codebrew.clikat.app_utils.CommonUtils.Companion.isNetworkConnected
import com.codebrew.clikat.app_utils.extension.afterTextChanged
import com.codebrew.clikat.base.BaseActivity
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
import com.codebrew.clikat.databinding.ActivityChooseAddressBinding
import com.codebrew.clikat.di.ViewModelProviderFactory
import com.codebrew.clikat.modal.DataCommon
import com.codebrew.clikat.modal.other.SettingModel
import com.codebrew.clikat.module.dialog_adress.adapter.AddressAdapter
import com.codebrew.clikat.module.dialog_adress.adapter.AddressListener
import com.codebrew.clikat.module.dialog_adress.adapter.PlacesAutoCompleteAdapter
import com.codebrew.clikat.module.dialog_adress.adapter.VehicleDEtailAdapter
import com.codebrew.clikat.module.dialog_adress.interfaces.AddressDialogListener
import com.codebrew.clikat.preferences.DataNames
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import kotlinx.android.synthetic.main.activity_choose_address.*
import kotlinx.android.synthetic.main.fragment_dialog_address.*
import kotlinx.android.synthetic.main.fragment_dialog_address.ed_search
import kotlinx.android.synthetic.main.fragment_dialog_address.rv_addressList
import kotlinx.android.synthetic.main.toolbar_app.*
import retrofit2.Retrofit
import javax.inject.Inject


private const val ARG_PARAM1 = "branchId"
private const val ARG_PARAM2 = "user_id"

class AddressDialogActivity : BaseActivity<ActivityChooseAddressBinding, AddressViewModel>(),
        PlacesAutoCompleteAdapter.ClickListener, AddressNavigator {

    private var mListener: AddressDialogListener? = null

    var tag1 = "1"
    private lateinit var addressAdapter: AddressAdapter
    private var addresslist = mutableListOf<AddressBean>()
    var screen_val = ""
    private var addressId = 0

    @Inject
    lateinit var factory: ViewModelProviderFactory

    @Inject
    lateinit var retrofit: Retrofit

    @Inject
    lateinit var prefHelper: PreferenceHelper
    var resturant_lat = 0.0
    var resturant_long = 0.0

    @Inject
    lateinit var appUtils: AppUtils

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


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//required-------------------------------------
        mSupplierId = intent.getIntExtra("branchId", 0)!!
        viewModel.navigator = this
        Places.initialize(this, appUtils.getMapKey())
        addressObserver()
        editAddressObserver()
        deleteAddressObserver()
        addAddressObserver()
        userVehicleObserver()
        deleteObserver()
        tb_back.setOnClickListener {
            finish()
        }
        tb_title.text = "Address"
        settingData = prefHelper.getGsonValue(
                DataNames.SETTING_DATA,
                SettingModel.DataBean.SettingData::class.java
        )
        if (intent.hasExtra("resturant_lat")) {
            resturant_lat = intent.getDoubleExtra("resturant_lat", 0.0)
        }
        if (intent.hasExtra("resturant_long")) {
            resturant_long = intent.getDoubleExtra("resturant_long", 0.0)
        }
        self_pickUp =
                if (prefHelper.getKeyValue(AppConstants.ROAD_MAP_TYPE, PrefenceConstants.TYPE_STRING)!!
                                .equals("Delivery")
                ) {
                    "8"
                } else {
                    "9"
                }
        if (self_pickUp.equals("8") || userId == 0) {
//            iv_add.visibility = View.GONE
//            ic_search.visibility = View.VISIBLE
//            ed_search.visibility = View.VISIBLE
            addressAdapter = AddressAdapter("1", settingData, AddressListener(
                    { model ->

                        if (!::addressBean.isInitialized) {
                            addressBean = AddressBean()
                            addressBean = model
                        } else {
                            addressBean = model
                        }
                        if (addressBean.isUnderZone!!.equals(1)) {
                            addressBean.user_service_charge = mAddressData?.user_service_charge
                            addressBean.preparation_time = mAddressData?.preparation_time
                            addressBean.min_order = mAddressData?.min_order
                            addressBean.base_delivery_charges = mAddressData?.base_delivery_charges

                            // loadMapScreen("select", addressBean)
//                    text_delivery_location.text =
//                        addressBean.customer_address + ", " + addressBean.address_line_1
                            mListener?.onAddressSelect(addressBean)
                            performResult(addressBean, null)
                        }
                    },
                    { adapterView, addressBean ->

                        val popup = PopupMenu(this, adapterView)
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
                        loadMapScreen("current", null)
                    }
                }
            })
            )


            rv_addressList.adapter = addressAdapter
            rv_addressList.scrollToPosition(0)

            val itemDecoration: ItemDecoration =
                    DividerItemDecoration(this, DividerItemDecoration.VERTICAL)
            rv_addressList.addItemDecoration(itemDecoration)


            val mAutoCompleteAdapter = PlacesAutoCompleteAdapter(this, settingData)
            mAutoCompleteAdapter.setClickListener(this)
            text_add.setOnClickListener {
//                val intent = Intent(this, newlocActivity::class.java)
//                intent.putExtra("user_id", 0)
//                intent.putExtra("branchId", 0)
                val intent = Intent(this, SelectlocActivity::class.java)
                intent.putExtra("is_new", 1)
                intent.putExtra("type", "add")
                startActivityForResult(intent, 157)
            }
            btn_save_adrs.setOnClickListener {
                Log.e("data_addfess", "datatatatat")
                finish()
            }
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

            param["latitude"] = resturant_lat.toString()
            param["longitude"] = resturant_long.toString()
            if (prefHelper.getCurrentUserLoggedIn()) {
                param["accessToken"] = prefHelper.getKeyValue(
                        PrefenceConstants.ACCESS_TOKEN,
                        PrefenceConstants.TYPE_STRING
                ).toString()
            }
            if (prefHelper.getCurrentUserLoggedIn()) {
                mAddressViewModel!!.getAddressJson(param, true)
            } else {
                mAddressViewModel!!.getAddressList(param, true)
            }


        } else {
//            iv_add.visibility = View.VISIBLE
//            ic_search.visibility = View.GONE
//            ed_search.visibility = View.GONE

            tv_title.setText(getString(R.string.choose_vehi))
            user_id =
                    prefHelper.getKeyValue(PrefenceConstants.USER_ID, PrefenceConstants.TYPE_STRING)
                            ?.toString()!!

            mAddressViewModel?.getUserVehicle(user_id)
        }
//        iv_add.setOnClickListener {
//            val intent = Intent(this, AddVehicleActivity::class.java)
//            startActivityForResult(intent, 158)
//        }
//        iv_close.setOnClickListener {
//
//        }

    }

    private fun performResult(addressBean: AddressBean?, vehicleInputParam: MapVehicleInputParam?) {

        val intent = Intent()

        if (addressBean != null) {
            intent.putExtra("addressBean", addressBean)
        }

        if (vehicleInputParam != null) {
            intent.putExtra("vehicleBean", vehicleInputParam)
        }

        setResult(Activity.RESULT_OK, intent)
        finish()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 157) {
            if (screen_val.equals("1")) {
                val param = java.util.HashMap<String, String>()
                param["languageId"] = "14"

                param["latitude"] = resturant_lat.toString()
                param["longitude"] = resturant_long.toString()
                if (prefHelper.getCurrentUserLoggedIn()) {
                    param["accessToken"] = prefHelper.getKeyValue(
                            PrefenceConstants.ACCESS_TOKEN,
                            PrefenceConstants.TYPE_STRING
                    ).toString()
                }
                if (prefHelper.getCurrentUserLoggedIn()) {
                    mAddressViewModel!!.getAddressJson(param, true)
                } else {
                    mAddressViewModel!!.getAddressList(param, true)
                }
            } else {
                if (resultCode == Activity.RESULT_OK) {
                    val mapInputParam = data?.getParcelableExtra<MapInputParam>("mapParam")

                    // tvArea.text = mapInputParam?.first_address.toString()

                    var hashMap: HashMap<String, String>? = null

                    when (mapInputParam?.requestType) {
                        "add", "edit", "current", "1" -> {

                            hashMap = HashMap()
                            // hashMap["name"] = prefHelper.getKeyValue(PrefenceConstants.USER_NAME, PrefenceConstants.TYPE_STRING).toString()
                            hashMap["latitude"] = mapInputParam.latitude
                            hashMap["longitude"] = mapInputParam.longitude
                            hashMap["addressLineFirst"] = mapInputParam.first_address
                            hashMap["zone_id"] = mapInputParam.zone_id!!
                            hashMap["customer_address"] = mapInputParam.second_address
                            hashMap["is_real_address"] = "0"
                            hashMap["address_type"] = mapInputParam.address_type ?: ""
                            hashMap["area"] = mapInputParam.area ?: ""
                            hashMap["street"] = mapInputParam.street ?: ""
                            hashMap["building"] = mapInputParam.building ?: ""
                            hashMap["floor"] = mapInputParam.floor ?: ""
                            hashMap["appartment"] = mapInputParam.appartment ?: ""
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


                    if (isNetworkConnected(applicationContext ?: this)) {

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
//                        text_delivery_location.text =
//                            addressBean.customer_address + ", " + addressBean.address_line_1
                            mListener?.onAddressSelect(addressBean)
                            performResult(addressBean, null)
                        }

                    }

                } else if (resultCode == Activity.RESULT_OK) {
                    mAddressViewModel?.getUserVehicle(user_id)

                    val vehicleInputParam =
                            data?.getParcelableExtra<MapVehicleInputParam>("vehicleParam")
                    mListener?.onVehicleSelect(
                            vehicleInputParam!!.id.toString(),
                            vehicleInputParam!!.modelNo.toString(),
                            vehicleInputParam!!.color.toString(),
                            vehicleInputParam!!.plateNo.toString()
                    )

                    performResult(null, vehicleInputParam)


                }
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

                            val vehicleInputParam = MapVehicleInputParam(
                                    vechicle_List.get(position)!!.id.toString(),
                                    vechicle_List.get(position)!!.name.toString(),
                                    vechicle_List.get(position)!!.color.toString(),
                                    vechicle_List.get(position)!!.number_plate.toString()
                            )

                            mListener?.onVehicleSelect(
                                    vechicle_List.get(position)!!.id.toString(),
                                    vechicle_List.get(position)!!.name.toString(),
                                    vechicle_List.get(position)!!.color.toString(),
                                    vechicle_List.get(position)!!.number_plate.toString()
                            )

                            performResult(null, vehicleInputParam)
                        }

                        override fun onDeleteClicked(id: Int) {
                            mAddressViewModel?.deleteUserVehicle(id.toString(), user_id)
                        }

                    })
            rv_addressList.layoutManager = LinearLayoutManager(this)
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


    override fun onStart() {
        super.onStart()

//        mListener = this as AddressDialogListener

    }


    override fun onDestroy() {
        super.onDestroy()
        mListener = null

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
//            text_delivery_location.text =
//                addresslist.get(0).customer_address + ", " + addresslist.get(0).address_line_1
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
            val param = java.util.HashMap<String, String>()
            param["languageId"] = "14"

            param["latitude"] = resturant_lat.toString()
            param["longitude"] = resturant_long.toString()
            if (prefHelper.getCurrentUserLoggedIn()) {
                param["accessToken"] = prefHelper.getKeyValue(
                        PrefenceConstants.ACCESS_TOKEN,
                        PrefenceConstants.TYPE_STRING
                ).toString()
            }
            if (prefHelper.getCurrentUserLoggedIn()) {
                mAddressViewModel!!.getAddressJson(param, true)
            } else {
                mAddressViewModel!!.getAddressList(param, true)
            }
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

            addresslist.add(resource)

            addresslist.sortByDescending { it.id }

            addressAdapter.addHeaderAndSubmitList(addresslist, prefHelper.getCurrentUserLoggedIn())

            val param = java.util.HashMap<String, String>()
            param["languageId"] = "14"

            param["latitude"] = resturant_lat.toString()
            param["longitude"] = resturant_long.toString()
            if (prefHelper.getCurrentUserLoggedIn()) {
                param["accessToken"] = prefHelper.getKeyValue(
                        PrefenceConstants.ACCESS_TOKEN,
                        PrefenceConstants.TYPE_STRING
                ).toString()
            }
            if (prefHelper.getCurrentUserLoggedIn()) {
                mAddressViewModel!!.getAddressJson(param, true)
            } else {
                mAddressViewModel!!.getAddressList(param, true)
            }
        }

        // Observe the LiveData, passing in this activity as the LifecycleOwner and the observer.
        mAddressViewModel?.addAdrsLiveData?.observe(this, catObserver)
    }


    private fun loadMapScreen(type: String, adrsData: AddressBean?) {

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
            screen_val = "1"
            val intent = Intent(this, newlocActivity::class.java)
            intent.putExtra("type", type)
            intent.putExtra("screen_val", screen_val)

            intent.putExtra("mapParam", mapParam)
            startActivityForResult(intent, LOCATION_PARAM)
        } else {

            screen_val = "1"
            val intent = Intent(this, SelectlocActivity::class.java)
            intent.putExtra("type", type)
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


    override fun getBindingVariable(): Int {
        return BR.viewModel
    }

    override fun getLayoutId(): Int {
        return R.layout.activity_choose_address
    }

    override fun getViewModel(): AddressViewModel {
        mAddressViewModel = ViewModelProviders.of(this, factory).get(AddressViewModel::class.java)
        return mAddressViewModel!!
    }

    override fun onZoneData(zoneInf: ZoneInformation?, isAddressUpdate: Boolean) {
        if (isNetworkConnected) {
            CommonUtils.setBaseUrl(BuildConfig.BASE_URL, retrofit)
            text_delivery_location.text = zoneInf!!.name.toString()!!
            mAddressViewModel?.getAddressListUnderZone(mSupplierId, zoneInfo = zoneInf!!.id.toString(), value = "")
        }


    }

}
