package com.codebrew.clikat.module.new_signup.login.v2

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.EditText
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import com.codebrew.clikat.BR
import com.codebrew.clikat.R
import com.codebrew.clikat.app_utils.AppUtils
import com.codebrew.clikat.app_utils.DialogsUtil
import com.codebrew.clikat.app_utils.GoogleLoginHelper
import com.codebrew.clikat.app_utils.extension.launchActivity
import com.codebrew.clikat.app_utils.extension.onSnackbar
import com.codebrew.clikat.app_utils.navController
import com.codebrew.clikat.base.BaseFragment
import com.codebrew.clikat.data.constants.PrefenceConstants
import com.codebrew.clikat.data.preferences.PreferenceHelper
import com.codebrew.clikat.databinding.DialougeForgotPassBinding
import com.codebrew.clikat.databinding.LoginFragmentV2Binding
import com.codebrew.clikat.di.ViewModelProviderFactory
import com.codebrew.clikat.modal.Data1
import com.codebrew.clikat.modal.PojoSignUp
import com.codebrew.clikat.modal.other.SettingModel
import com.codebrew.clikat.module.bottom_navigation.MainScreenActivity
import com.codebrew.clikat.module.login.LoginNavigator
import com.codebrew.clikat.module.login.LoginViewModel
import com.codebrew.clikat.module.new_signup.login.LoginFragmentDirections
import com.codebrew.clikat.preferences.DataNames
import com.codebrew.clikat.utils.GeneralFunctions
import com.codebrew.clikat.utils.configurations.Configurations
import com.facebook.*
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.messaging.FirebaseMessaging
import com.google.gson.Gson
import com.mobsandgeeks.saripaar.ValidationError
import com.mobsandgeeks.saripaar.Validator
import com.mobsandgeeks.saripaar.annotation.Email
import com.mobsandgeeks.saripaar.annotation.NotEmpty
import com.mobsandgeeks.saripaar.annotation.Order
import com.mobsandgeeks.saripaar.annotation.Password
import kotlinx.android.synthetic.main.login_fragment.*
import kotlinx.android.synthetic.main.login_fragment_v2.*
import kotlinx.android.synthetic.main.login_fragment_v2.btnGoogle
import kotlinx.android.synthetic.main.login_fragment_v2.btn_login
import kotlinx.android.synthetic.main.login_fragment_v2.btn_social
import kotlinx.android.synthetic.main.login_fragment_v2.etPassword
import kotlinx.android.synthetic.main.login_fragment_v2.login_button
import kotlinx.android.synthetic.main.login_fragment_v2.tvSignup
import org.json.JSONObject
import javax.inject.Inject


class LoginFragmentV2 : BaseFragment<LoginFragmentV2Binding, LoginViewModel>(), Validator.ValidationListener, LoginNavigator {

    companion object {
        fun newInstance() = LoginFragmentV2()
    }

    @Inject
    lateinit var factory: ViewModelProviderFactory

    @Inject
    lateinit var prefHelper: PreferenceHelper


    @Inject
    lateinit var appUtils: AppUtils


    private var mBinding: LoginFragmentV2Binding? = null

    private lateinit var viewModel: LoginViewModel

    @NotEmpty(message = "Please enter Email")
    @Email
    @Order(1)
    private lateinit var emailEditText: EditText

    @NotEmpty(message = "Please enter password")
    @Password
    @Order(2)
    private lateinit var pswEditText: EditText

    // Validation
    val validator = Validator(this)

    private var settingBean: SettingModel.DataBean.SettingData? = null

    @Inject
    lateinit var dialogsUtil: DialogsUtil

    private var fbId = ""

    private var callbackManager: CallbackManager? = null
    private var fbJson: JSONObject? = null

    @Inject
    lateinit var googleHelper: GoogleLoginHelper


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        callbackManager = CallbackManager.Factory.create()

        settingBean = prefHelper.getGsonValue(DataNames.SETTING_DATA, SettingModel.DataBean.SettingData::class.java)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mBinding = viewDataBinding
        mBinding?.color = Configurations.colors
        mBinding?.strings = appUtils.loadAppConfig(0).strings

        viewModel.navigator = this

        emailEditText = view.findViewById(R.id.edEmail)
        pswEditText = view.findViewById(R.id.etPassword)


        // Validator
        validator.validationMode = Validator.Mode.IMMEDIATE
        // Listeners
        validator.setValidationListener(this)

        facebookCallback()

        updateToken()

        appUtils.checkGoogleLogin(requireActivity()).let {
            if(it.isNotEmpty())
            {
                btnGoogle.visibility = View.VISIBLE
                //initialize google login
                googleHelper.initialize(requireActivity(),it)
            }else
            {
                btnGoogle.visibility = View.GONE
            }
        }

        btnGoogle.setOnClickListener {
            googleHelper.handleClick(viewModel)
        }

        btn_login.setOnClickListener {
            validator.validate()
        }

        btn_social.setOnClickListener {

            if (isNetworkConnected) {
                login_button.callOnClick()
            }

        }

        tvSignup.setOnClickListener {
            navController(this@LoginFragmentV2).navigate(R.id.action_loginFragmentV2_to_registerFragmentV2)
        }


        iv_back.setOnClickListener {
            if(!findNavController().popBackStack()){
                requireActivity().finish()
            }

        }
    }

    private fun afterLogin() {

        appUtils.triggerEvent("login")

        val isGuest = settingBean?.login_template.isNullOrEmpty() || settingBean?.login_template == "0"

        if (isGuest) {
            activity?.setResult(Activity.RESULT_OK)
        } else {
            activity?.launchActivity<MainScreenActivity>()
        }
        activity?.finish()
    }


    override fun getBindingVariable(): Int {
        return BR.viewModel
    }

    override fun getLayoutId(): Int {
        return R.layout.login_fragment_v2
    }

    override fun getViewModel(): LoginViewModel {
        viewModel = ViewModelProviders.of(this, factory).get(LoginViewModel::class.java)
        return viewModel
    }

    override fun onForgotPswr(message: String) {

    }

    override fun onLogin(userData: Data1) {
        afterLogin()
    }

    private fun facebookCallback() {
        login_button.setPermissions(listOf("email", "public_profile"))
        // Callback registration
        login_button.registerCallback(callbackManager, object : FacebookCallback<LoginResult?> {
            override fun onSuccess(loginResult: LoginResult?) {
                fbSuccess(loginResult)
            }

            override fun onCancel() {
                if (AccessToken.getCurrentAccessToken() != null) {
                    LoginManager.getInstance().logOut()
                }
            }

            override fun onError(exception: FacebookException) {
                if (exception is FacebookAuthorizationException) {
                    if (AccessToken.getCurrentAccessToken() != null) {
                        LoginManager.getInstance().logOut()
                    }
                }
            }
        })
    }

    private fun fbSuccess(loginResult: LoginResult?) {

        val request = GraphRequest.newMeRequest(
                loginResult?.accessToken
        ) { fbObject: JSONObject?, response: GraphResponse? ->

            fbJson = fbObject

            if (fbObject?.has("email") == true) {
                fbloginapi(fbObject)
            } else {
                enterEmail()
            }
        }

        val parameters = Bundle()
        parameters.putString("fields", "id,name,email,picture")
        request.parameters = parameters
        request.executeAsync()
    }

    private fun fbloginapi(jsonObj: JSONObject?) {
        fbId = jsonObj?.optString("id", "") ?: ""
        val hashMap = hashMapOf("facebookToken" to jsonObj?.optString("id", ""),
                "name" to jsonObj?.optString("name", ""),
                "email" to jsonObj?.optString("email", ""),
                "image" to loadPic(jsonObj?.optJSONObject("picture")),
                "deviceToken" to prefHelper.getKeyValue(DataNames.REGISTRATION_ID, PrefenceConstants.TYPE_STRING).toString(),
                "deviceType" to "0")

        if (isNetworkConnected) {
            viewModel.validateFb(hashMap)
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


    private fun loadPic(optJSONObject: JSONObject?): String? {
        return optJSONObject?.optJSONObject("data")?.optString("url", "")
    }

    private fun enterEmail() {

        val binding = DataBindingUtil.inflate<DialougeForgotPassBinding>(LayoutInflater.from(activity), R.layout.dialouge_forgot_pass, null, false)
        binding.color = Configurations.colors

        val mDialog = dialogsUtil.showDialogFix(activity ?: requireContext(), binding.root)
        mDialog.show()


        val edEmail = mDialog.findViewById<TextInputEditText>(R.id.etSearch)
        val tlInput = mDialog.findViewById<TextInputLayout>(R.id.tlForgot)
        val tvTitle = mDialog.findViewById<TextView>(R.id.tvTitle)
        val tvEnter = mDialog.findViewById<TextView>(R.id.tvGo)

        tvTitle.text = getString(R.string.enter_email)

        tvEnter.setOnClickListener {

            if (edEmail.text.toString().trim().isEmpty()) {
                tlInput.requestFocus()
                tlInput.error = getString(R.string.empty_email)
            } else if (!GeneralFunctions.isValidEmail(edEmail.text.toString().trim())) {
                tlInput.requestFocus()
                tlInput.error = getString(R.string.invalid_email)
            } else {
                if (isNetworkConnected) {
                    fbJson?.putOpt("email", edEmail.text.toString().trim())
                    fbloginapi(fbJson)
                }
            }

        }

    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        callbackManager?.onActivityResult(requestCode, resultCode, data)
        super.onActivityResult(requestCode, resultCode, data)

        googleHelper.activityResult(requestCode, resultCode, data) {
            if (it != null) {
                hideLoading()
                googleHelper.logoutGoogle{

                }
                mBinding?.root?.onSnackbar(it)
            }
        }
    }


    override fun onSocialLogin(signup: PojoSignUp?,type:String) {
        if (type == "facebook") {
            signup?.data?.fbId = fbId
        }

        prefHelper.addGsonValue(DataNames.USER_DATA, Gson().toJson(signup))

        if (signup?.data?.otp_verified == 1) {
            prefHelper.addGsonValue(DataNames.USER_DATA, Gson().toJson(signup))
            prefHelper.setkeyValue(PrefenceConstants.USER_LOGGED_IN, true)
            prefHelper.setkeyValue(PrefenceConstants.ACCESS_TOKEN, signup.data?.access_token ?: "")
            prefHelper.setkeyValue(PrefenceConstants.USER_ID, signup.data?.id.toString())

            signup.data?.user_created_id?.let {
                prefHelper.setkeyValue(PrefenceConstants.USER_CHAT_ID, it)
            }

            signup.data?.referral_id?.let {
                prefHelper.setkeyValue(PrefenceConstants.USER_REFERRAL_ID, it)
            }

            afterLogin()
        } else {
            notVerified(signup?.data)
        }
    }

    override fun userNotVerified(userData: Data1) {
        notVerified(userData)
    }

    override fun registerByPhone() {
        //do nothing
    }

    private fun notVerified(userData: Data1?) {

        val action = LoginFragmentDirections.actionLoginFragmentToEnterPhoneFrag(userData?.access_token)
        navController(this@LoginFragmentV2).navigate(action)
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
            val hashMap = hashMapOf("email" to emailEditText.text.toString().trim(),
                    "deviceToken" to prefHelper.getKeyValue(DataNames.REGISTRATION_ID, PrefenceConstants.TYPE_STRING).toString(),
                    "deviceType" to 0.toString(),
                    "password" to etPassword.text.toString().trim(),
                    "languageId" to prefHelper.getLangCode())

            if (isNetworkConnected) {
                viewModel.validateLogin(hashMap)
            }
        }
    }

}
