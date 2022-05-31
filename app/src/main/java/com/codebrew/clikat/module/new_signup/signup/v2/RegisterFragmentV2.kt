package com.codebrew.clikat.module.new_signup.signup.v2

import android.os.Bundle
import android.view.View
import android.widget.EditText
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import com.codebrew.clikat.BR
import com.codebrew.clikat.R
import com.codebrew.clikat.app_utils.AppUtils
import com.codebrew.clikat.app_utils.extension.onSnackbar
import com.codebrew.clikat.app_utils.navController
import com.codebrew.clikat.base.BaseFragment
import com.codebrew.clikat.data.constants.PrefenceConstants
import com.codebrew.clikat.data.model.others.RegisterParamModelV2
import com.codebrew.clikat.data.preferences.PreferenceHelper
import com.codebrew.clikat.databinding.RegisterFragmentV2Binding
import com.codebrew.clikat.di.ViewModelProviderFactory
import com.codebrew.clikat.modal.other.SettingModel
import com.codebrew.clikat.module.new_signup.signup.RegisterNavigator
import com.codebrew.clikat.preferences.DataNames
import com.codebrew.clikat.preferences.Prefs
import com.codebrew.clikat.utils.StaticFunction
import com.codebrew.clikat.utils.configurations.Configurations
import com.google.firebase.messaging.FirebaseMessaging
import com.mobsandgeeks.saripaar.ValidationError
import com.mobsandgeeks.saripaar.Validator
import com.mobsandgeeks.saripaar.annotation.*
import kotlinx.android.synthetic.main.register_fragment_v2.*
import javax.inject.Inject

class RegisterFragmentV2 : BaseFragment<RegisterFragmentV2Binding, RegisterViewModelV2>(),
        RegisterNavigator, Validator.ValidationListener {

    companion object {
        fun newInstance() = RegisterFragmentV2()
    }

    private lateinit var viewModel: RegisterViewModelV2

    private var mBinding: RegisterFragmentV2Binding? = null

    @Inject
    lateinit var factory: ViewModelProviderFactory

    @Inject
    lateinit var appUtils: AppUtils

    @Inject
    lateinit var prefHelper: PreferenceHelper

    // Validationbutton_login
    private val validator = Validator(this)

    var settingData: SettingModel.DataBean.SettingData? = null

    @NotEmpty(message = "Please enter Email")
    @Email
    @Order(1)
    private lateinit var emailEditText: EditText

    @NotEmpty
    @Password(scheme = Password.Scheme.ANY)
    @Order(2)
    @Length(min = 4, message = "Password must be between 4 and 12 characters")
    private lateinit var passwordEditText: EditText


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

        // Validator
        validator.validationMode = Validator.Mode.BURST
        validator.setValidationListener(this)

        emailEditText = view.findViewById(R.id.edEmail)
        passwordEditText = view.findViewById(R.id.etPassword)

        updateToken()

        btn_signup.setOnClickListener {
            validator.validate()
        }

        iv_back.setOnClickListener {
            findNavController().popBackStack()
        }

        tvText.setOnClickListener {
            navController(this@RegisterFragmentV2).navigate(R.id.action_registerFragmentV2_to_loginFragmentV2)
        }
    }

    private fun updateToken() {

        if (prefHelper.getKeyValue(DataNames.REGISTRATION_ID, PrefenceConstants.TYPE_STRING).toString().isEmpty()) {
            FirebaseMessaging.getInstance().token.addOnCompleteListener {
                if (it.isSuccessful) {
                    val token  = it.result
                    prefHelper.setkeyValue(DataNames.REGISTRATION_ID, token.toString())
                }

            }
        }
    }


    override fun getBindingVariable(): Int {
        return BR.viewModel
    }

    override fun getLayoutId(): Int {
        return R.layout.register_fragment_v2
    }

    override fun getViewModel(): RegisterViewModelV2 {
        viewModel = ViewModelProviders.of(this, factory).get(RegisterViewModelV2::class.java)
        return viewModel
    }

    override fun onRegisterSuccess(accessToken: String,message:String) {
        val action = RegisterFragmentV2Directions.actionRegisterFragmentV2ToEnterPhoneFragV2(accessToken)
        navController(this@RegisterFragmentV2).navigate(action)
    }



    override fun onOtpVerify() {

    }


    override fun onErrorOccur(message: String) {
        mBinding?.root?.onSnackbar(message)
    }

    override fun onSessionExpire() {
        openActivityOnTokenExpire()
    }

    override fun onValidationFailed(errors: MutableList<ValidationError>?) {
        val error = errors?.get(0)
        val message = error?.getCollatedErrorMessage(activity)
        val editText = error?.view as EditText
        editText.error = message
        editText.requestFocus()
    }

    override fun onValidationSucceeded() {
        hideKeyboard()
        if (isNetworkConnected) {

            if (prefHelper.getKeyValue(DataNames.REGISTRATION_ID, PrefenceConstants.TYPE_STRING).toString().isEmpty()) {
                updateToken()
            } else {

                val registerParam = RegisterParamModelV2(
                        emailEditText.text.toString().trim(),
                        Prefs.with(activity).getString(DataNames.REGISTRATION_ID, ""),
                        1,
                        0,
                        etPassword.text.toString().trim(),
                        StaticFunction.getLanguage(activity)
                )
                viewModel.validateSignup(registerParam)
            }
        }
    }

}
