package com.codebrew.clikat.module.main_screen

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.codebrew.clikat.BuildConfig
import com.codebrew.clikat.R
import com.codebrew.clikat.app_utils.navController
import com.codebrew.clikat.data.AppDataType
import com.codebrew.clikat.data.DeliveryType
import com.codebrew.clikat.data.FoodAppType
import com.codebrew.clikat.data.VendorAppType
import com.codebrew.clikat.data.constants.AppConstants
import com.codebrew.clikat.data.constants.PrefenceConstants
import com.codebrew.clikat.data.preferences.PreferenceHelper
import com.codebrew.clikat.modal.other.SettingModel
import com.codebrew.clikat.preferences.DataNames
import dagger.android.support.AndroidSupportInjection
import javax.inject.Inject

class MainFragment : Fragment() {


    private var screenFlowBean: SettingModel.DataBean.ScreenFlowBean? = null
    private var bookingFlowBean: SettingModel.DataBean.BookingFlowBean? = null

    @Inject
    lateinit var prefHelper: PreferenceHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidSupportInjection.inject(this)
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_main, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        screenFlowBean = prefHelper.getGsonValue(DataNames.SCREEN_FLOW, SettingModel.DataBean.ScreenFlowBean::class.java)
        val settingFlow = prefHelper.getGsonValue(DataNames.SETTING_DATA, SettingModel.DataBean.SettingData::class.java)

        bookingFlowBean = prefHelper.getGsonValue(DataNames.BOOKING_FLOW, SettingModel.DataBean.BookingFlowBean::class.java)
        if (screenFlowBean?.app_type ?: 0 > AppDataType.Custom.type || AppConstants.APP_SUB_TYPE > AppDataType.Custom.type) {

            if (settingFlow?.clikat_theme != null && settingFlow.clikat_theme == "1")
                navController(this@MainFragment).navigate(R.id.action_mainFragment_to_clikatHomeFragment)
            else
                navController(this@MainFragment).navigate(R.id.action_mainFragment_to_customHomeFrag)
        } /*else if (BuildConfig.CLIENT_CODE == "craveqatar123_0896") {
            navController(this@MainFragment).navigate(R.id.action_mainFragment_to_roadMap)
        }*/
        else {
            when (screenFlowBean?.app_type ?: 0) {
                AppDataType.Food.type -> {
                    if (bookingFlowBean?.is_pickup_order == FoodAppType.Both.foodType && screenFlowBean?.is_single_vendor == VendorAppType.Multiple.appType) {
                        AppConstants.DELIVERY_OPTIONS = DeliveryType.DeliveryOrder.type
                     //   navController(this@MainFragment).navigate(R.id.action_mainFragment_to_resturantHomeFrag)

                            navController(this@MainFragment).navigate(R.id.action_mainFragment_to_resturantHomeFrag)
                    } else {
                        //navController(this@MainFragment).navigate(R.id.action_mainFragment_to_homeFragment)
                      //  navController(this@MainFragment).navigate(R.id.action_mainFragment_to_homeMenuItemFragment)

                            navController(this@MainFragment).navigate(R.id.action_mainFragment_to_resturantHomeFrag)

                    }
                }
                AppDataType.CarRental.type -> {
                    navController(this@MainFragment).navigate(R.id.action_mainFragment_to_homeRental)
                }
                else -> {
                    navController(this@MainFragment).navigate(R.id.action_mainFragment_to_homeFragment)
                }
            }
        }

    }
}
