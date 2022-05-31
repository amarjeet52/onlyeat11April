package com.codebrew.clikat.module.new_signup.enter_phone.v2

import android.app.Activity
import android.content.res.Resources
import android.os.Bundle
import android.view.View
import androidx.core.os.ConfigurationCompat
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.codebrew.clikat.BR
import com.codebrew.clikat.R
import com.codebrew.clikat.app_utils.AppUtils
import com.codebrew.clikat.app_utils.extension.launchActivity
import com.codebrew.clikat.app_utils.extension.onSnackbar
import com.codebrew.clikat.app_utils.navController
import com.codebrew.clikat.base.BaseFragment
import com.codebrew.clikat.data.preferences.PreferenceHelper
import com.codebrew.clikat.databinding.EnterPhoneFragmentBinding
import com.codebrew.clikat.databinding.EnterPhoneFragmentV2Binding
import com.codebrew.clikat.di.ViewModelProviderFactory
import com.codebrew.clikat.modal.PojoSignUp
import com.codebrew.clikat.modal.other.SettingModel
import com.codebrew.clikat.module.bottom_navigation.MainScreenActivity
import com.codebrew.clikat.module.new_signup.enter_phone.EnterPhoneFragArgs
import com.codebrew.clikat.module.new_signup.enter_phone.EnterPhoneFragDirections
import com.codebrew.clikat.module.new_signup.enter_phone.EnterPhoneViewModel
import com.codebrew.clikat.module.new_signup.enter_phone.PhoneNaviagtor
import com.codebrew.clikat.preferences.DataNames
import com.codebrew.clikat.utils.configurations.Configurations
import kotlinx.android.synthetic.main.enter_phone_fragment.*
import javax.inject.Inject

class EnterPhoneFragV2 : BaseFragment<EnterPhoneFragmentV2Binding, EnterPhoneViewModel>(), PhoneNaviagtor {

    companion object {
        fun newInstance() = EnterPhoneFragV2()
    }

    @Inject
    lateinit var factory: ViewModelProviderFactory

    @Inject
    lateinit var prefHelper: PreferenceHelper

    @Inject
    lateinit var appUtils: AppUtils

    private lateinit var viewModel: EnterPhoneViewModel

    private var mBinding: EnterPhoneFragmentV2Binding? = null

    var settingData: SettingModel.DataBean.SettingData? = null

    private var validNumber = false

    val args: EnterPhoneFragArgs by navArgs()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        settingData = prefHelper.getGsonValue(DataNames.SETTING_DATA, SettingModel.DataBean.SettingData::class.java)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mBinding = viewDataBinding
        mBinding?.color = Configurations.colors
        mBinding?.strings = appUtils.loadAppConfig(0).strings

        viewModel.navigator = this

        settingData?.referral_feature?.let {
            if (it == "1") {
                inputLayout_referal.visibility = View.VISIBLE
            }
        }

        etCountryCode.registerCarrierNumberEditText(etPhoneNumber)
        etCountryCode.setNumberAutoFormattingEnabled(false)
        settingData?.cutom_country_code?.let {
            if (it == "1") {
                etCountryCode.setDefaultCountryUsingNameCode("VE")
                etCountryCode.setCustomMasterCountries("VE,US")
                etCountryCode.resetToDefaultCountry()
            } else {
                val locale = ConfigurationCompat.getLocales(Resources.getSystem().configuration)[0]
                etCountryCode.setDefaultCountryUsingNameCode(locale.country)
            }
        }
        etCountryCode.setPhoneNumberValidityChangeListener { isValidNumber: Boolean -> validNumber = isValidNumber }


        tvSendOtp.setOnClickListener {
            if (isNetworkConnected) {
                validate_values()
            }
        }

        if(settingData?.bypass_otp == "1"){
            tvSendOtp?.text = getString(R.string.submit)
        }

        back.setOnClickListener {
            findNavController().popBackStack()
        }

    }


    private fun validate_values() {
        if (etPhoneNumber.text.toString().trim() == "") {
            inputLayout.requestFocus()
            inputLayout.error = getString(R.string.empty_phone_number)
        } else {
            inputLayout.error = null

            if (isNetworkConnected) {
                val hashMap = hashMapOf("accessToken" to args.accessToken,
                        "countryCode" to etCountryCode?.selectedCountryCodeWithPlus,
                        "mobileNumber" to etPhoneNumber.text.toString())


                if (etReferral.text?.trim()?.isNotEmpty() == true) {
                    hashMap["referralCode"] = etReferral.text.toString().trim()
                }


                viewModel.validatePhone(hashMap)
            }
        }
    }


    override fun getBindingVariable(): Int {
        return BR.viewModel
    }

    override fun getLayoutId(): Int {
        return R.layout.enter_phone_fragment_v2
    }

    override fun getViewModel(): EnterPhoneViewModel {
        viewModel = ViewModelProviders.of(this, factory).get(EnterPhoneViewModel::class.java)
        return viewModel
    }

    override fun onPhoneVerify(accessToken: String) {
        if(settingData?.bypass_otp =="1"){
            if (isNetworkConnected) {

                val hashMap = hashMapOf("accessToken" to accessToken,
                        "otp" to "12345",
                        "languageId" to prefHelper.getLangCode())

                viewModel.validateOtp(hashMap)
            }
            return
        }

        val action = EnterPhoneFragV2Directions.actionEnterPhoneFrag2ToOtpVerifyFragmentV2(accessToken)
        navController(this@EnterPhoneFragV2).navigate(action)
    }

    override fun onOtpVerify() {
        val settingBean = prefHelper.getGsonValue(DataNames.SETTING_DATA, SettingModel.DataBean.SettingData::class.java)
        val isGuest = settingBean?.login_template.isNullOrEmpty() || settingBean?.login_template == "0"

        if (isGuest) {
            activity?.setResult(Activity.RESULT_OK)
        } else {
            activity?.launchActivity<MainScreenActivity>()
        }

        activity?.finish()
    }

    override fun updatePhone(it: PojoSignUp) {
        //do nothing
    }

    override fun onErrorOccur(message: String) {
        mBinding?.root?.onSnackbar(message)
    }

    override fun onSessionExpire() {
        openActivityOnTokenExpire()
    }

}
