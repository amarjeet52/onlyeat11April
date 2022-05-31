package com.codebrew.clikat.module.new_signup.name

import android.app.Activity
import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import com.codebrew.clikat.BR
import com.codebrew.clikat.R
import com.codebrew.clikat.app_utils.AppUtils
import com.codebrew.clikat.app_utils.extension.launchActivity
import com.codebrew.clikat.app_utils.extension.onSnackbar
import com.codebrew.clikat.base.BaseFragment
import com.codebrew.clikat.data.constants.PrefenceConstants
import com.codebrew.clikat.data.preferences.PreferenceHelper
import com.codebrew.clikat.databinding.FragmentUpdateNameBinding
import com.codebrew.clikat.di.ViewModelProviderFactory
import com.codebrew.clikat.modal.PojoSignUp
import com.codebrew.clikat.modal.other.SettingModel
import com.codebrew.clikat.module.bottom_navigation.MainScreenActivity
import com.codebrew.clikat.module.new_signup.enter_phone.EnterPhoneViewModel
import com.codebrew.clikat.module.new_signup.enter_phone.PhoneNaviagtor
import com.codebrew.clikat.preferences.DataNames
import com.codebrew.clikat.utils.configurations.Configurations
import com.google.gson.Gson
import kotlinx.android.synthetic.main.fragment_update_name.*
import javax.inject.Inject

class EnterNameFragment : BaseFragment<FragmentUpdateNameBinding, EnterPhoneViewModel>(), PhoneNaviagtor {

    companion object {
        fun newInstance() = EnterNameFragment()
    }

    @Inject
    lateinit var factory: ViewModelProviderFactory

    @Inject
    lateinit var prefHelper: PreferenceHelper

    @Inject
    lateinit var appUtils: AppUtils

    private lateinit var viewModel: EnterPhoneViewModel

    private var mBinding: FragmentUpdateNameBinding? = null

    var settingData: SettingModel.DataBean.SettingData? = null


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


        tvSubmit.setOnClickListener {
            if (isNetworkConnected)
            {
                hideKeyboard()
                validateValues()
            }
        }

        back.setOnClickListener {
            findNavController().popBackStack()
        }
    }

    private fun validateValues() {
        if (etName.text.toString().trim() == "") {
            inputLayout.requestFocus()
            inputLayout.error = getString(R.string.enter_name)
        } else {
            inputLayout.error = null
            if (isNetworkConnected) {
                val hashMap = hashMapOf("userCreatedId" to prefHelper.getKeyValue(PrefenceConstants.USER_CHAT_ID, PrefenceConstants.TYPE_STRING).toString(),
                        "name" to etName?.text.toString().trim())
                viewModel.updateName(hashMap)
            }
        }
    }


    override fun getBindingVariable(): Int {
        return BR.viewModel
    }

    override fun getLayoutId(): Int {
        return R.layout.fragment_update_name
    }

    override fun getViewModel(): EnterPhoneViewModel {
        viewModel = ViewModelProviders.of(this, factory).get(EnterPhoneViewModel::class.java)
        return viewModel
    }

    override fun onPhoneVerify(accessToken: String) {
        //do nothing
    }

    override fun onOtpVerify() {
        //do nothing
    }

    override fun updatePhone(it: PojoSignUp) {
        val userInfo = prefHelper.getGsonValue(DataNames.USER_DATA, PojoSignUp::class.java)
        userInfo?.data?.firstname = it.data?.firstname
        prefHelper.addGsonValue(DataNames.USER_DATA, Gson().toJson(userInfo))
        prefHelper.setkeyValue(PrefenceConstants.USER_LOGGED_IN, true)
        appUtils.triggerEvent("login")

        val isGuest = settingData?.login_template.isNullOrEmpty() || settingData?.login_template == "0"

        if (isGuest) {
            activity?.setResult(Activity.RESULT_OK)
        } else {
            activity?.launchActivity<MainScreenActivity>()
        }
        activity?.finish()
    }

    override fun onErrorOccur(message: String) {
        mBinding?.root?.onSnackbar(message)
    }

    override fun onSessionExpire() {
        openActivityOnTokenExpire()
    }

}
