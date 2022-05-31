package com.codebrew.clikat.module.cart.v2

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
import android.widget.Toast
import androidx.annotation.NonNull
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.codebrew.clikat.BR
import com.codebrew.clikat.R
import com.codebrew.clikat.app_utils.AppUtils
import com.codebrew.clikat.app_utils.DateTimeUtils
import com.codebrew.clikat.app_utils.PermissionFile
import com.codebrew.clikat.base.BaseActivity
import com.codebrew.clikat.data.constants.AppConstants.Companion.REQUEST_CODE_LOCATION
import com.codebrew.clikat.data.constants.PrefenceConstants
import com.codebrew.clikat.data.model.api.AddressBean
import com.codebrew.clikat.data.model.api.DataBean
import com.codebrew.clikat.data.model.api.vehicleDetails.PostData
import com.codebrew.clikat.data.model.others.MapInputParam
import com.codebrew.clikat.data.model.others.MapVehicleInputParam
import com.codebrew.clikat.data.preferences.PreferenceHelper
import com.codebrew.clikat.databinding.ActivitySelectLocBinding
import com.codebrew.clikat.databinding.ActivityVehicleListingBinding
import com.codebrew.clikat.databinding.LayoutAddVehicleBinding
import com.codebrew.clikat.di.ViewModelProviderFactory
import com.codebrew.clikat.modal.AppGlobal.Companion.localeManager
import com.codebrew.clikat.modal.other.SettingModel
import com.codebrew.clikat.module.cart.CartViewModel
import com.codebrew.clikat.preferences.DataNames
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.hugmd.util.mapUtil.OnMapAndViewReadyListener
import dagger.android.AndroidInjection
import kotlinx.android.synthetic.main.activity_select_loc.*
import kotlinx.android.synthetic.main.layout_add_vehicle.*
import kotlinx.android.synthetic.main.toolbar_app.*
import pub.devrel.easypermissions.AppSettingsDialog
import pub.devrel.easypermissions.EasyPermissions
import java.io.IOException
import java.lang.Exception
import java.util.*
import javax.inject.Inject


open class DEtailVehicleActivity : BaseActivity<ActivityVehicleListingBinding, CartViewModel>()
{



    private lateinit var cartViewModel:CartViewModel
    @Inject
    lateinit var factory: ViewModelProviderFactory


    @Inject
    lateinit var appUtils: AppUtils
var user_id=""

    @Inject
    lateinit var prefHelper: PreferenceHelper

    private var clientInform: SettingModel.DataBean.SettingData? = null
    private var adminData: SettingModel.DataBean.AdminDetail? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        super.onCreate(savedInstanceState)
        tb_back.setOnClickListener{
            finish()
        }
         clientInform = prefHelper.getGsonValue(DataNames.SETTING_DATA, SettingModel.DataBean.SettingData::class.java)
        adminData = prefHelper.getGsonValue(PrefenceConstants.ADMIN_DETAILS, SettingModel.DataBean.AdminDetail::class.java)
        user_id = prefHelper.getKeyValue(PrefenceConstants.USER_ID, PrefenceConstants.TYPE_STRING)?.toString()!!



        tb_title.text=getString(R.string.add_vehicle)

        detailObserver()
        btn_submit.setOnClickListener {
            if(et_name.text.toString() == ""||et_carColor.text.toString() == ""||
                    et_carNumber.text.toString() == ""||et_carModel.text.toString() == ""||
                    et_mobile.text.toString() == ""  )
            {
                Toast.makeText(this,"Please all fields",Toast.LENGTH_SHORT).show()
            }else{
                cartViewModel.postUserVehicle(user_id,et_carModel.text.toString(),et_carColor.text.toString(),et_carNumber.text.toString(), et_mobile.text.toString(),"0")
            }
        }
    }
fun detailObserver()
{
    // Create the observer which updates the UI.
    val catObserver = Observer<List<PostData>> { resource ->
        val mapParam = MapVehicleInputParam(et_carModel.text.toString(),et_carColor.text.toString(),
                et_carNumber.text.toString())

        val intent = Intent()
        intent.putExtra("vehicleParam", mapParam)
        setResult(Activity.RESULT_OK, intent)
        finish()
    }

    // Observe the LiveData, passing in this activity as the LifecycleOwner and the observer.
   cartViewModel.vehicleresponseLiveData.observe(this, catObserver)
}



    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base?.let { localeManager?.setLocale(it) })
    }

    override fun getBindingVariable(): Int {
        return BR.viewModel
    }

    override fun getLayoutId(): Int {
        return R.layout.layout_add_vehicle
    }

    override fun getViewModel(): CartViewModel {
        cartViewModel = ViewModelProviders.of(this, factory).get(CartViewModel::class.java)
        return cartViewModel as CartViewModel
    }


}
