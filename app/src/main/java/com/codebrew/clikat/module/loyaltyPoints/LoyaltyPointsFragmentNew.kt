package com.codebrew.clikat.module.loyaltyPoints

import android.os.Bundle
import android.view.View
import android.view.WindowManager
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.Navigation
import com.bumptech.glide.Glide
import com.codebrew.clikat.BR
import com.codebrew.clikat.R
import com.codebrew.clikat.app_utils.AppUtils
import com.codebrew.clikat.app_utils.extension.onSnackbar
import com.codebrew.clikat.base.BaseFragment
import com.codebrew.clikat.data.constants.AppConstants
import com.codebrew.clikat.data.constants.PrefenceConstants
import com.codebrew.clikat.data.model.api.Currency
import com.codebrew.clikat.data.model.api.LoyalityDataItem
import com.codebrew.clikat.data.model.api.LoyaltyData
import com.codebrew.clikat.data.preferences.PreferenceHelper
import com.codebrew.clikat.databinding.FragmentLoyalityPointsNewBinding
import com.codebrew.clikat.di.ViewModelProviderFactory
import com.codebrew.clikat.modal.other.SettingModel
import com.codebrew.clikat.module.loyaltyPoints.adapter.LoyalityNewAdapter
import com.codebrew.clikat.module.loyaltyPoints.adapter.LoyaltyAdapter
import com.codebrew.clikat.preferences.DataNames
import com.codebrew.clikat.utils.Utils
import com.codebrew.clikat.utils.configurations.Configurations
import kotlinx.android.synthetic.main.fragment_loyality_points_new.*
import kotlinx.android.synthetic.main.toolbar_app.*
import javax.inject.Inject


class LoyaltyPointsFragmentNew : BaseFragment<FragmentLoyalityPointsNewBinding, LoyaltyPointsViewModel>(), LoyaltyPointsNavigator {

    @Inject
    lateinit var factory: ViewModelProviderFactory

    @Inject
    lateinit var mDataManager: PreferenceHelper

    @Inject
    lateinit var appUtils: AppUtils
    private var selectedCurrency: Currency? = null
    private lateinit var viewModel: LoyaltyPointsViewModel
    private var adapter: LoyalityNewAdapter? = null
    private var mBinding: FragmentLoyalityPointsNewBinding? = null
    var settingData: SettingModel.DataBean.SettingData? = null
    var mLoyalityList = mutableListOf<LoyalityDataItem>()
    var loyality_points=""
    override fun getViewModel(): LoyaltyPointsViewModel {
        viewModel = ViewModelProviders.of(this, factory).get(LoyaltyPointsViewModel::class.java)
        return viewModel
    }

    override fun getBindingVariable(): Int {
        return BR.viewModel
    }

    override fun getLayoutId(): Int {
        return R.layout.fragment_loyality_points_new
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        selectedCurrency = mDataManager.getGsonValue(PrefenceConstants.CURRENCY_INF, Currency::class.java)
        settingData = mDataManager.getGsonValue(DataNames.SETTING_DATA, SettingModel.DataBean.SettingData::class.java)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.navigator = this

        mBinding = viewDataBinding
        viewDataBinding.color = Configurations.colors
        Glide.with(this).asGif().load(R.raw.loading_gif).into(mBinding!!.imageLoader);
        settingToolbar()

        hideKeyboard()
        setAdapter()
        hitLoalityPoint()

    }

    private fun settingToolbar() {

        tb_back.setOnClickListener {
            Navigation.findNavController(requireView()).popBackStack()
        }

        tb_title.text = getString(R.string.reward)
    }

    private fun setAdapter() {
        adapter = LoyalityNewAdapter(mLoyalityList)
        rvEarnings.adapter = adapter
    }
    private fun hitLoalityPoint()
    {
        if (isNetworkConnected)
            viewModel.apiGetLoyaltyPoints()
    }

    private fun hitApi() {
        if (isNetworkConnected)
            viewModel.apiGetLoyalty()
    }


    override fun loyaltyPointsSuccess(data: LoyaltyData?) {
        if (data != null) {
            loyality_points= data.totalEarningPoint.toString()
            hitApi()
        }
    }

    override fun loyaltyPointsSuccessNew(data: List<DataLoyality?>) {
        if (data != null) {

            if(mDataManager.getLangCode().equals("14")) {
                mLoyalityList.add(LoyalityDataItem(data.get(0)?.image, data.get(0)?.loyality_description_english, getString(R.string.loyality_totl_amt,loyality_points), mutableListOf(getString(R.string.order_food), getString(R.string.get_50), getString(R.string.reward_conversion))))

                mLoyalityList.add(LoyalityDataItem(data.get(1)?.image, data.get(1)?.loyality_description_english, getString(R.string.gift_title), mutableListOf(getString(R.string.just_order), getString(R.string.our_defi), getString(R.string.every_order))))
            }
            else{
                mLoyalityList.add(LoyalityDataItem(data.get(0)?.image, data.get(0)?.loyality_description_arabic, getString(R.string.loyality_totl_amt,loyality_points), mutableListOf(getString(R.string.order_food), getString(R.string.get_50), getString(R.string.reward_conversion))))

                mLoyalityList.add(LoyalityDataItem(data.get(1)?.image, data.get(1)?.loyality_description_arabic, getString(R.string.gift_title), mutableListOf(getString(R.string.just_order), getString(R.string.our_defi), getString(R.string.every_order))))

            }
            adapter?.notifyDataSetChanged()
        }
    }

    override fun onErrorOccur(message: String) {
        mBinding?.root?.onSnackbar(message)
    }

    override fun onSessionExpire() {
        openActivityOnTokenExpire()
    }

}