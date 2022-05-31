package com.codebrew.clikat.module.cart

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.codebrew.clikat.BR
import com.codebrew.clikat.R
import com.codebrew.clikat.app_utils.AppUtils

import com.codebrew.clikat.base.BaseActivity
import com.codebrew.clikat.data.constants.PrefenceConstants
import com.codebrew.clikat.data.model.api.vehicleDetails.DeleteData
import com.codebrew.clikat.data.model.api.vehicleDetails.PostData
import com.codebrew.clikat.data.model.api.vehicleDetails.VehicleData
import com.codebrew.clikat.data.preferences.PreferenceHelper
import com.codebrew.clikat.databinding.ActivityVehicleListingBinding
import com.codebrew.clikat.di.ViewModelProviderFactory
import com.codebrew.clikat.module.dialog_adress.VEHICLE_PARAM


import com.codebrew.clikat.module.dialog_adress.adapter.VehicleDEtailAdapter
import dagger.android.AndroidInjection
import kotlinx.android.synthetic.main.activity_vehicle_listing.*
import kotlinx.android.synthetic.main.activity_vehicle_listing.rv_addressList
import kotlinx.android.synthetic.main.fragment_dialog_address.*
import kotlinx.android.synthetic.main.toolbar_app.*
import javax.inject.Inject


open class VehicleListingActivity :  BaseActivity<ActivityVehicleListingBinding, CartViewModel>(){
    val VEHICLE_PARAM1 = 159
    private lateinit var cartViewModel:CartViewModel
    @Inject
    lateinit var factory: ViewModelProviderFactory


    @Inject
    lateinit var appUtils: AppUtils
    var user_id=""

    @Inject
    lateinit var prefHelper: PreferenceHelper


    lateinit var vehicledEtailAdapter: VehicleDEtailAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        super.onCreate(savedInstanceState)
        user_id = prefHelper.getKeyValue(PrefenceConstants.USER_ID, PrefenceConstants.TYPE_STRING)?.toString()!!

  tb_back.setOnClickListener{
            finish()
        }
        tb_add.visibility= View.VISIBLE
        tb_title.text=getString(R.string.your_vehicle)
        userVehicleObserver()
        deleteObserver()
        cartViewModel.getUserVehicle(user_id)
        tb_add.setOnClickListener {
            val intent = Intent(this, AddVehicleActivity::class.java)
            startActivityForResult(intent, VEHICLE_PARAM1)
        }
    }

    private fun userVehicleObserver() {

        // Create the observer which updates the UI.
        val vehicleObserver = Observer<List<VehicleData>> { resource ->
             var vechicle_List: List<VehicleData> = ArrayList()
            vechicle_List = resource
            vehicledEtailAdapter = VehicleDEtailAdapter(
                    vechicle_List,
                    object : VehicleDEtailAdapter.Callback {
                        override fun onItemClicked(position: Int) {
                               }

                        override fun onDeleteClicked(id: Int) {
                            cartViewModel?.deleteUserVehicle(id.toString(),user_id)
                        }

                    })
            rv_addressList.layoutManager = LinearLayoutManager(this)
            rv_addressList.adapter = vehicledEtailAdapter

        }


        cartViewModel?.liveUserDeatil?.observe(this, vehicleObserver)
    }

    fun deleteObserver()
    {
        // Create the observer which updates the UI.
        val catObserver = Observer<DeleteData> { resource ->
            cartViewModel?.getUserVehicle(user_id)
        }

        // Observe the LiveData, passing in this activity as the LifecycleOwner and the observer.
        cartViewModel?.vehicleDeleteLiveData?.observe(this, catObserver)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == VEHICLE_PARAM1)  {
            if (resultCode == Activity.RESULT_OK) {
                cartViewModel?.getUserVehicle(user_id)
          }
        }

    }




    override fun getBindingVariable(): Int {
        return BR.viewModel
    }

    override fun getLayoutId(): Int {
        return R.layout.activity_vehicle_listing
    }

    override fun getViewModel(): CartViewModel {
        cartViewModel = ViewModelProviders.of(this, factory).get(CartViewModel::class.java)
        return cartViewModel as CartViewModel
    }
}