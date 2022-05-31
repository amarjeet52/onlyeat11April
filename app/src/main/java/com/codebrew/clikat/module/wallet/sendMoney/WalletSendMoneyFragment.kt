package com.codebrew.clikat.module.wallet.sendMoney

import android.content.res.Resources
import android.os.Bundle
import android.text.InputType
import android.view.View
import android.view.WindowManager
import androidx.core.os.ConfigurationCompat
import androidx.core.os.bundleOf
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.NavOptions
import androidx.navigation.Navigation
import com.codebrew.clikat.BR
import com.codebrew.clikat.R
import com.codebrew.clikat.app_utils.AppToasty
import com.codebrew.clikat.app_utils.extension.onSnackbar
import com.codebrew.clikat.app_utils.navController
import com.codebrew.clikat.base.BaseFragment
import com.codebrew.clikat.data.preferences.PreferenceHelper
import com.codebrew.clikat.databinding.FragmentSendMoneyBinding
import com.codebrew.clikat.di.ViewModelProviderFactory
import com.codebrew.clikat.modal.other.SettingModel
import com.codebrew.clikat.module.wallet.WalletNavigator
import com.codebrew.clikat.module.wallet.WalletViewModel
import com.codebrew.clikat.preferences.DataNames
import com.codebrew.clikat.utils.GeneralFunctions
import com.codebrew.clikat.utils.configurations.Configurations
import kotlinx.android.synthetic.main.fragment_send_money.*
import javax.inject.Inject

class WalletSendMoneyFragment : BaseFragment<FragmentSendMoneyBinding, WalletViewModel>(), WalletNavigator, View.OnClickListener {

    @Inject
    lateinit var factory: ViewModelProviderFactory

    private var mBinding: FragmentSendMoneyBinding? = null

    private lateinit var viewModel: WalletViewModel
    private var settingBean: SettingModel.DataBean.SettingData? = null

    @Inject
    lateinit var prefHelper: PreferenceHelper

    private var validNumber = false

    override fun getBindingVariable(): Int {
        return BR.viewModel
    }

    override fun getViewModel(): WalletViewModel {
        viewModel = ViewModelProviders.of(this, factory).get(WalletViewModel::class.java)
        return viewModel
    }

    override fun getLayoutId(): Int {
        return R.layout.fragment_send_money
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.navigator = this
        activity?.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN)

        settingBean = prefHelper.getGsonValue(DataNames.SETTING_DATA, SettingModel.DataBean.SettingData::class.java)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mBinding = viewDataBinding
        viewDataBinding.color = Configurations.colors
        viewDataBinding.strings = Configurations.strings

        initialise()
        listeners()
        checkRadioType()
    }

    private fun listeners() {
        ivBack?.setOnClickListener(this)
        tvSendMoney?.setOnClickListener(this)
    }


    private fun initialise() {
        ccp.registerCarrierNumberEditText(etEmailPhone)
        ccp.setNumberAutoFormattingEnabled(false)
        settingBean?.cutom_country_code?.let {
            if (it == "1") {
                ccp.setDefaultCountryUsingNameCode("VE")
                ccp.setCustomMasterCountries("VE,US")
                ccp.resetToDefaultCountry()
            } else {
                val locale = ConfigurationCompat.getLocales(Resources.getSystem().configuration)[0]
                ccp.setDefaultCountryUsingNameCode(locale.country)
            }
        }
        ccp.setPhoneNumberValidityChangeListener { isValidNumber: Boolean -> validNumber = isValidNumber }
    }

    private fun checkRadioType() {
        rbGroup?.setOnCheckedChangeListener { _, i ->
            when (i) {
                R.id.rbEmail -> {
                    etEmailPhone?.inputType = InputType.TYPE_TEXT_VARIATION_WEB_EMAIL_ADDRESS
                    etEmailPhone?.hint = getString(R.string.enter_email)
                    etEmailPhone?.setText("")
                    ccp?.visibility = View.GONE
                }
                R.id.rbPhone -> {
                    etEmailPhone?.setText("")
                    etEmailPhone?.hint = getString(R.string.enter_phone_number)
                    etEmailPhone?.inputType = InputType.TYPE_CLASS_PHONE
                    ccp?.visibility = View.VISIBLE
                }
            }
        }
    }

    private fun hiSendMoneyApi(comment: String) {
        if (isNetworkConnected) {
            val amtTxt = etAmount.text.toString()
            val amount = String.format("%.2f", amtTxt.toFloat())
            val ccp = if (rbPhone?.isChecked == true) ccp?.selectedCountryCodeWithPlus else null
            viewModel.apiSendMoney(amount, ccp, etEmailPhone?.text.toString().trim(),comment)
        }
    }

    override fun onMoneySent() {
        AppToasty.success(requireContext(), getString(R.string.money_sent_success))
        val navOptions=NavOptions.Builder()
                .setPopUpTo(R.id.walletFragment, true)
                .build()
        //navController(this@WalletSendMoneyFragment).popBackStack()
        val bundle= bundleOf("isRefresh" to true)
        Navigation.findNavController(requireView()).navigate(R.id.walletFragment, bundle, navOptions)

    }

    override fun onAddMoneyToWallet() {
    }

    override fun onErrorOccur(message: String) {
        mBinding?.root?.onSnackbar(message)
    }

    override fun onSessionExpire() {
        openActivityOnTokenExpire()
    }

    override fun onClick(p0: View?) {
        when (p0?.id) {
            R.id.ivBack -> navController(this@WalletSendMoneyFragment).popBackStack()
            R.id.tvSendMoney -> {
                hideKeyboard()
                val amount = etAmount?.text.toString().trim()
                val comment = etComment?.text.toString().trim()
                val emailPhone = etEmailPhone?.text.toString().trim()
                if (validations(amount, emailPhone, comment))
                    hiSendMoneyApi(comment)
            }
        }
    }

    private fun validations(amount: String, emailPhone: String, comment: String): Boolean {
        return when {
            amount.isEmpty() -> {
                mBinding?.root?.onSnackbar(getString(R.string.enter_amount))
                false
            }
            emailPhone.isEmpty() -> {
                val message = if (rbEmail?.isChecked == true) getString(R.string.enter_email) else getString(R.string.enter_phone_number)
                mBinding?.root?.onSnackbar(message)
                false
            }
            (rbEmail?.isChecked == true && !GeneralFunctions.isValidEmail(emailPhone)) -> {
                mBinding?.root?.onSnackbar(getString(R.string.invalid_email))
                false
            }

            (rbPhone?.isChecked == true && !validNumber) -> {
                mBinding?.root?.onSnackbar(getString(R.string.enter_valid_number))
                false
            }
            comment.isEmpty() -> {
                mBinding?.root?.onSnackbar(getString(R.string.enter_comment))
                false
            }
            else -> true
        }
    }
}