package com.codebrew.clikat.module.dialog_adress.v2

import android.app.Activity
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.codebrew.clikat.BuildConfig
import com.codebrew.clikat.R
import com.codebrew.clikat.app_utils.AppUtils
import com.codebrew.clikat.app_utils.CommonUtils
import com.codebrew.clikat.app_utils.CommonUtils.Companion.isNetworkConnected
import com.codebrew.clikat.app_utils.extension.afterTextChanged
import com.codebrew.clikat.base.BaseDialog
import com.codebrew.clikat.data.model.api.AddressBean
import com.codebrew.clikat.data.model.api.DataBean
import com.codebrew.clikat.data.model.others.MapInputParam
import com.codebrew.clikat.data.preferences.PreferenceHelper
import com.codebrew.clikat.databinding.FragmentDialogAddressV2Binding
import com.codebrew.clikat.di.ViewModelProviderFactory
import com.codebrew.clikat.modal.DataCommon
import com.codebrew.clikat.modal.other.SettingModel
import com.codebrew.clikat.module.dialog_adress.AddressNavigator
import com.codebrew.clikat.module.dialog_adress.AddressViewModel
import com.codebrew.clikat.module.dialog_adress.adapter.AddressAdapter
import com.codebrew.clikat.module.dialog_adress.adapter.AddressListener
import com.codebrew.clikat.module.dialog_adress.adapter.PlacesAutoCompleteAdapter
import com.codebrew.clikat.module.dialog_adress.interfaces.AddressDialogListener
import com.codebrew.clikat.preferences.DataNames
import com.codebrew.clikat.utils.configurations.Configurations
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import dagger.android.support.AndroidSupportInjection
import kotlinx.android.synthetic.main.fragment_dialog_address_v2.*
import retrofit2.Retrofit
import javax.inject.Inject

const val LOCATION_PARAM = 157
private const val ARG_PARAM1 = "branchId"

class AddressDialogFragmentV2 : BaseDialog(), PlacesAutoCompleteAdapter.ClickListener, AddressNavigator {


    private var mListener: AddressDialogListener? = null

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

    lateinit var addressBean: AddressBean

    private var mAddressViewModel: AddressViewModel? = null

    private var mSupplierId = 0

    private var mAddressData: DataBean? = null

    var settingData: SettingModel.DataBean.SettingData? = null


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        val binding = DataBindingUtil.inflate<FragmentDialogAddressV2Binding>(inflater, R.layout.fragment_dialog_address_v2, container, false)
        AndroidSupportInjection.inject(this)
        mAddressViewModel = ViewModelProviders.of(this, factory).get(AddressViewModel::class.java)
        binding.viewModel = mAddressViewModel
        binding.color = Configurations.colors

        mAddressViewModel?.navigator = this

        return binding.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.MyBottomSheetDialogTheme)
        arguments?.let {
            mSupplierId = it.getInt(ARG_PARAM1)
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
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
                        hashMap["addressLineFirst"] = mapInputParam.first_address
                        hashMap["customer_address"] = mapInputParam.second_address
                    }
                }


                if (isNetworkConnected(activity?.applicationContext ?: requireContext())) {

                    if (prefHelper.getCurrentUserLoggedIn()) {
                        when (mapInputParam?.requestType) {
                            "add", "current" -> {
                                hashMap?.let { mAddressViewModel?.addAddress(it) }
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
                        addressBean.customer_address = """${mapInputParam?.second_address
                                ?: ""} , ${mapInputParam?.first_address ?: ""}"""
                        mListener?.onAddressSelect(addressBean)
                        dismiss()
                    }

                }

            }
        }

    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        Places.initialize(activity ?: requireContext(), appUtils.getMapKey())
        addressObserver()
        editAddressObserver()
        deleteAddressObserver()
        addAddressObserver()

        settingData = prefHelper.getGsonValue(DataNames.SETTING_DATA, SettingModel.DataBean.SettingData::class.java)


        addressAdapter = AddressAdapter("0",settingData, AddressListener(
                { model ->

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
                    loadMapScreen("current", null)
                }
            }
        }))


        rv_addressList.adapter = addressAdapter




        val mAutoCompleteAdapter = PlacesAutoCompleteAdapter(activity,settingData)
        mAutoCompleteAdapter.setClickListener(this)

        ed_search.afterTextChanged {
            if (it.isEmpty()) {
                rv_addressList.adapter = addressAdapter
            } else {
                if (rv_addressList.adapter == addressAdapter) {
                    rv_addressList.adapter = mAutoCompleteAdapter
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

        if (isNetworkConnected) {
            CommonUtils.setBaseUrl(BuildConfig.BASE_URL, retrofit)
            mAddressViewModel?.getAddressList(mSupplierId)
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
        }

        // Observe the LiveData, passing in this activity as the LifecycleOwner and the observer.
        mAddressViewModel?.addAdrsLiveData?.observe(this, catObserver)
    }


    private fun loadMapScreen(type: String, model: AddressBean?) {
        val intent = Intent(activity, SelectlocActivityV2::class.java)
        intent.putExtra("type", type)
        if (model != null) {
            intent.putExtra("addressData", model)
        }
        startActivityForResult(intent, LOCATION_PARAM)
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
        fun newInstance(supplierId: Int): AddressDialogFragmentV2 =
                AddressDialogFragmentV2().apply {
                    arguments = Bundle().apply {
                        putInt(ARG_PARAM1, supplierId)
                    }
                }
    }

    override fun onErrorOccur(message: String) {

    }

    override fun onSessionExpire() {
        openActivityOnTokenExpire()
    }
}
