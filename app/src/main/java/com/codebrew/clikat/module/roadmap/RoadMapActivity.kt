package com.codebrew.clikat.module.roadmap

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.Window
import android.view.WindowManager
import androidx.lifecycle.ViewModelProviders
import com.codebrew.clikat.BR
import com.codebrew.clikat.R
import com.codebrew.clikat.app_utils.extension.launchActivity
import com.codebrew.clikat.base.BaseActivity
import com.codebrew.clikat.data.constants.AppConstants
import com.codebrew.clikat.data.constants.AppConstants.Companion.ROAD_MAP_TYPE_BOOK_TABLE
import com.codebrew.clikat.data.constants.AppConstants.Companion.ROAD_MAP_TYPE_CRAVE_MANIA
import com.codebrew.clikat.data.constants.AppConstants.Companion.ROAD_MAP_TYPE_DELIVERY
import com.codebrew.clikat.data.constants.AppConstants.Companion.ROAD_MAP_TYPE_PICK_UP
import com.codebrew.clikat.data.constants.AppConstants.Companion.ROAD_MAP_TYPE_SIGNATURE
import com.codebrew.clikat.data.preferences.PreferenceHelper
import com.codebrew.clikat.databinding.ActivityMainScreenBinding
import com.codebrew.clikat.databinding.FragmentPreBookMenuBinding
import com.codebrew.clikat.databinding.FragmentRoadMapBinding
import com.codebrew.clikat.di.ViewModelProviderFactory
import com.codebrew.clikat.modal.other.English
import com.codebrew.clikat.modal.other.SettingModel
import com.codebrew.clikat.module.bottom_navigation.MainScreenActivity
import com.codebrew.clikat.module.crave_mania.FragmentCraveMania
import com.codebrew.clikat.module.pre_delivery.PreBookMenuFragment
import com.codebrew.clikat.module.searchProduct.SearchViewModel
import com.codebrew.clikat.module.splash.SplashActivity
import com.codebrew.clikat.preferences.DataNames
import com.google.gson.Gson
import kotlinx.android.synthetic.main.fragment_road_map.*
import javax.inject.Inject

class RoadMapActivity : BaseActivity<ActivityMainScreenBinding, SearchViewModel>() {

    @Inject
    lateinit var factory: ViewModelProviderFactory
    private val sharedPrefFile = "kotlinsharedpreference"
    lateinit var editor: SharedPreferences.Editor
    lateinit var sharedPreferences: SharedPreferences
    private lateinit var viewModel: SearchViewModel

    private lateinit var mBinding: FragmentRoadMapBinding

    @Inject
    lateinit var preferenceHelper: PreferenceHelper

    private var clientInform: SettingModel.DataBean.SettingData? = null
    private var clientInform_new: SettingModel.DataBean? = null

    @SuppressLint("CommitPrefEdits")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedPreferences = this.getSharedPreferences(sharedPrefFile, Context.MODE_PRIVATE)
        editor = sharedPreferences.edit()
        clientInform = preferenceHelper.getGsonValue(DataNames.SETTING_DATA, SettingModel.DataBean.SettingData::class.java)
        clientInform_new?.user_app_version?.get(0)?.version_android
        setClickListeners()

    }

    private fun setClickListeners() {
        group_delivery?.setOnClickListener {
            editor.putString("is_table", "0")
            editor.apply()
            editor.commit()
            goToNextActivity(ROAD_MAP_TYPE_DELIVERY)
        }

        group_book_table?.setOnClickListener {
//            editor.putString("is_table", "1")
//            editor.apply()
//            editor.commit()
//            goToNextActivity(ROAD_MAP_TYPE_BOOK_TABLE)
//            var intent = Intent(this,SplashActivity::class.java)
//            intent.putExtra("isFromRoadMap",true)
//            startActivity(intent)
        }

        group_pickup.setOnClickListener {
            editor.putString("is_table", "0")
            editor.apply()
            editor.commit()
            goToNextActivity(ROAD_MAP_TYPE_PICK_UP)

        }

        group_sign.setOnClickListener {
//            editor.putString("is_table", "0")
//            editor.apply()
//            editor.commit()
//            goToNextActivity(ROAD_MAP_TYPE_SIGNATURE)
//            var intent = Intent(this,SplashActivity::class.java)
//            intent.putExtra("isFromRoadMap",true)
//            startActivity(intent)

        }

        group_crave.setOnClickListener {
            editor.putString("is_table", "0")
            editor.apply()
            editor.commit()
            goToNextActivity(ROAD_MAP_TYPE_CRAVE_MANIA)

        }
    }


    private fun goToNextActivity(s: String) {
        when (s) {
            ROAD_MAP_TYPE_DELIVERY -> {
                clientInform?.is_table_booking = "0"

            }
            ROAD_MAP_TYPE_PICK_UP -> {
                clientInform?.is_table_booking = "2"
            }
            ROAD_MAP_TYPE_CRAVE_MANIA -> {
                clientInform?.is_table_booking = "3"
            }
            else -> {
                clientInform?.is_table_booking = "1"
            }
        }
        preferenceHelper.setkeyValue(DataNames.SETTING_DATA, Gson().toJson(clientInform))
        preferenceHelper.setkeyValue(AppConstants.ROAD_MAP_TYPE, s)
        when (s) {
            ROAD_MAP_TYPE_BOOK_TABLE -> {
                startActivity(Intent(this, PreBookMenuFragment::class.java))

            }
            ROAD_MAP_TYPE_CRAVE_MANIA -> {
                startActivity(Intent(this, MainScreenActivity::class.java))
            }
            else -> {
                startActivity(Intent(this, MainScreenActivity::class.java))
            }
        }


    }

    override fun getBindingVariable(): Int {
        return BR.viewModel
    }

    override fun getLayoutId(): Int {
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        this.window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);

        return R.layout.fragment_road_map
    }

    override fun getViewModel(): SearchViewModel {
        viewModel = ViewModelProviders.of(this, factory).get(SearchViewModel::class.java)
        return viewModel
    }
}