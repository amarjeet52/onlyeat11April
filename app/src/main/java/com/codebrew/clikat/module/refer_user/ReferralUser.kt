package com.codebrew.clikat.module.refer_user

import android.R.attr.label
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProviders
import com.codebrew.clikat.BR
import com.codebrew.clikat.R
import com.codebrew.clikat.app_utils.AppToasty
import com.codebrew.clikat.app_utils.extension.onSnackbar
import com.codebrew.clikat.base.BaseFragment
import com.codebrew.clikat.base.BaseInterface
import com.codebrew.clikat.data.constants.AppConstants
import com.codebrew.clikat.data.constants.PrefenceConstants
import com.codebrew.clikat.data.preferences.PreferenceHelper
import com.codebrew.clikat.databinding.ReferralUserFragmentBinding
import com.codebrew.clikat.di.ViewModelProviderFactory
import com.codebrew.clikat.modal.other.SettingModel
import com.codebrew.clikat.module.referral_list.ReferralListViewModel
import com.codebrew.clikat.preferences.DataNames
import com.codebrew.clikat.utils.GeneralFunctions
import com.codebrew.clikat.utils.configurations.Configurations
import kotlinx.android.synthetic.main.referral_user_fragment.*
import javax.inject.Inject


class ReferralUser : BaseFragment<ReferralUserFragmentBinding, ReferralListViewModel>(), BaseInterface {

    companion object {
        fun newInstance() = ReferralUser()
    }

    private lateinit var viewModel: ReferralListViewModel


    @Inject
    lateinit var factory: ViewModelProviderFactory

    private var mBinding: ReferralUserFragmentBinding? = null

    private var settingFlowBean: SettingModel.DataBean.SettingData? = null

    @Inject
    lateinit var perferenceHelper: PreferenceHelper


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.navigator = this

        settingFlowBean = perferenceHelper.getGsonValue(DataNames.SETTING_DATA, SettingModel.DataBean.SettingData::class.java)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mBinding = viewDataBinding
        mBinding?.color = Configurations.colors


        tv_referral_code.text = perferenceHelper.getKeyValue(PrefenceConstants.USER_REFERRAL_ID, PrefenceConstants.TYPE_STRING).toString()

        textView4.text = getString(R.string.referral_text_tag, AppConstants.CURRENCY_SYMBOL + (settingFlowBean?.referral_receive_price
                ?: "0.0"), AppConstants.CURRENCY_SYMBOL + (settingFlowBean?.referral_given_price
                ?: "0.0"))

        btn_share_referral.setOnClickListener {
            GeneralFunctions.shareApp(activity, getString(R.string.share_referral, perferenceHelper.getKeyValue(PrefenceConstants.USER_REFERRAL_ID, PrefenceConstants.TYPE_STRING).toString(), AppConstants.CURRENCY_SYMBOL + (settingFlowBean?.referral_receive_price
                    ?: "0.0"), AppConstants.CURRENCY_SYMBOL + (settingFlowBean?.referral_given_price
                    ?: "0.0")))
        }

        btn_referral_copy.setOnClickListener {
            val clipboard: ClipboardManager = activity?.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            val clip = ClipData.newPlainText(getString(R.string.referral_code_copied), tv_referral_code.text.toString())
            clipboard.setPrimaryClip(clip)

            AppToasty.success(activity?:requireContext(),getString(R.string.referral_code_copied))
        }

    }

    override fun getBindingVariable(): Int {
        return BR.viewModel
    }

    override fun getLayoutId(): Int {
        return R.layout.referral_user_fragment
    }

    override fun getViewModel(): ReferralListViewModel {
        viewModel = ViewModelProviders.of(this, factory).get(ReferralListViewModel::class.java)
        return viewModel
    }

    override fun onErrorOccur(message: String) {
        mBinding?.root?.onSnackbar(message)
    }

    override fun onSessionExpire() {
        openActivityOnTokenExpire()
    }

}
