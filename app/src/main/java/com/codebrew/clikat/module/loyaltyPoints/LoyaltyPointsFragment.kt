package com.codebrew.clikat.module.loyaltyPoints

import android.os.Bundle
import android.view.View
import android.view.WindowManager
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.Navigation
import com.bumptech.glide.Glide
import com.codebrew.clikat.BR
import com.codebrew.clikat.BuildConfig
import com.codebrew.clikat.R
import com.codebrew.clikat.app_utils.AppUtils
import com.codebrew.clikat.app_utils.extension.loadPlaceHolder
import com.codebrew.clikat.app_utils.extension.onSnackbar
import com.codebrew.clikat.base.BaseFragment
import com.codebrew.clikat.data.constants.AppConstants
import com.codebrew.clikat.data.constants.PrefenceConstants
import com.codebrew.clikat.data.model.api.Currency
import com.codebrew.clikat.data.model.api.LoyaltyData
import com.codebrew.clikat.data.preferences.PreferenceHelper
import com.codebrew.clikat.databinding.FragmentLoyalityPointsBinding
import com.codebrew.clikat.di.ViewModelProviderFactory
import com.codebrew.clikat.modal.other.SettingModel
import com.codebrew.clikat.module.loyaltyPoints.adapter.LoyaltyAdapter
import com.codebrew.clikat.preferences.DataNames
import com.codebrew.clikat.utils.GeneralFunctions
import com.codebrew.clikat.utils.Utils
import com.codebrew.clikat.utils.configurations.Configurations
import kotlinx.android.synthetic.main.fragment_loyality_points.*
import kotlinx.android.synthetic.main.nothing_found.*
import kotlinx.android.synthetic.main.toolbar_subscription.*
import javax.inject.Inject


class LoyaltyPointsFragment : BaseFragment<FragmentLoyalityPointsBinding, LoyaltyPointsViewModel>(), LoyaltyPointsNavigator {

    @Inject
    lateinit var factory: ViewModelProviderFactory

    @Inject
    lateinit var mDataManager: PreferenceHelper

    @Inject
    lateinit var appUtils: AppUtils
    private var selectedCurrency: Currency? = null
    private lateinit var viewModel: LoyaltyPointsViewModel
    private var adapter: LoyaltyAdapter? = null
    private var mBinding: FragmentLoyalityPointsBinding? = null
    private var settingFlowBean: SettingModel.DataBean.SettingData? = null
    var settingData: SettingModel.DataBean.SettingData? = null

    private val textConfig by lazy { appUtils.loadAppConfig(0).strings }

    override fun getViewModel(): LoyaltyPointsViewModel {
        viewModel = ViewModelProviders.of(this, factory).get(LoyaltyPointsViewModel::class.java)
        return viewModel
    }

    override fun getBindingVariable(): Int {
        return BR.viewModel
    }
    override fun loyaltyPointsSuccessNew(data: List<DataLoyality?>) {

    }
    override fun getLayoutId(): Int {
        return R.layout.fragment_loyality_points
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
        Glide.with(this).asGif().load(R.raw.loading_gif).into(mBinding!!.imageLoader);
        viewDataBinding.color = Configurations.colors
        initialise()
        listeners()
        setAdapter()
        hitApi()
    }

    private fun initialise() {
        activity?.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN)
        settingFlowBean = mDataManager.getGsonValue(DataNames.SETTING_DATA, SettingModel.DataBean.SettingData::class.java)


        group_invite.visibility = if (BuildConfig.CLIENT_CODE == "yayeen_0550") {
            View.GONE
        } else {
            View.VISIBLE
        }

        Utils.loadAppPlaceholder(settingData?.loyalty_points_listing ?: "")?.let {

            if (it.app?.isNotEmpty() == true)
            {
                ivPlaceholder.loadPlaceHolder(it.app ?: "")
            }

            if (it.message?.isNotEmpty() == true) {
                tvText.text = it.message
            }

        }
    }

    private fun setAdapter() {
        adapter = LoyaltyAdapter()
        rvEarnings.adapter = adapter
    }

    private fun hitApi() {
        if (isNetworkConnected)
            viewModel.apiGetLoyaltyPoints()
    }

    private fun listeners() {
        tb_name?.text = textConfig?.loyalty_points ?: getString(R.string.loyality_points)
        tb_back?.setOnClickListener {
            Navigation.findNavController(requireView()).popBackStack()
        }

        btnInvite?.setOnClickListener {
            GeneralFunctions.shareApp(activity, getString(R.string.share_referral, mDataManager.getKeyValue(PrefenceConstants.USER_REFERRAL_ID, PrefenceConstants.TYPE_STRING).toString(), AppConstants.CURRENCY_SYMBOL + (settingFlowBean?.referral_receive_price
                    ?: "0.0"), AppConstants.CURRENCY_SYMBOL + (settingFlowBean?.referral_given_price
                    ?: "0.0")))
        }
    }

    override fun loyaltyPointsSuccess(data: LoyaltyData?) {
        if (data != null) {
            tvLevelDescription?.text = data.loyalityLevel?.firstOrNull()?.description
            tvInviteFriendsDes?.text = getString(R.string.share_referral, mDataManager.getKeyValue(PrefenceConstants.USER_REFERRAL_ID, PrefenceConstants.TYPE_STRING).toString(), AppConstants.CURRENCY_SYMBOL + (settingFlowBean?.referral_receive_price
                    ?: "0.0"), AppConstants.CURRENCY_SYMBOL + (settingFlowBean?.referral_given_price
                    ?: "0.0"))
            tvTotalPoints?.text = data.totalEarningPoint.toString()
            tvLevel?.text = data.loyalityLevel?.firstOrNull()?.name
            tvAmountLeft?.text = getString(R.string.currency_tag, AppConstants.CURRENCY_SYMBOL, Utils.getPriceFormat(data.leftPointAmount?:0f,settingData,selectedCurrency))
            tvTotalAmount?.text = getString(R.string.currency_tag, AppConstants.CURRENCY_SYMBOL, Utils.getPriceFormat(data.totalPointAmountEarned?:0f,settingData,selectedCurrency))
            Glide.with(requireContext()).load(data.loyalityLevel?.firstOrNull()?.image)
                    .into(ivIcon)
            adapter?.addList(data.earnedData ?: arrayListOf())

            if (data.earnedData?.isEmpty() == true) {
                noData.visibility = View.VISIBLE
                rvEarnings.visibility = View.GONE
            } else {
                noData.visibility = View.GONE
                rvEarnings.visibility = View.VISIBLE
            }
        }
    }

    override fun onErrorOccur(message: String) {
        mBinding?.root?.onSnackbar(message)
    }

    override fun onSessionExpire() {
        openActivityOnTokenExpire()
    }

}