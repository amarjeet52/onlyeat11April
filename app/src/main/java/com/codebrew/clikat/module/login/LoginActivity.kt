package com.codebrew.clikat.module.login

import android.app.Activity
import android.app.AlertDialog
import android.app.admin.DevicePolicyManager
import android.content.ComponentCallbacks2
import android.content.Intent
import android.content.res.Resources
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.text.InputType
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.TextView
import androidx.annotation.NonNull
import androidx.annotation.RequiresApi
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricManager.Authenticators.*
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import androidx.core.os.ConfigurationCompat
import androidx.core.text.isDigitsOnly
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProviders
import com.clevertap.android.sdk.CleverTapAPI
import com.codebrew.clikat.BR
import com.codebrew.clikat.BuildConfig
import com.codebrew.clikat.R
import com.codebrew.clikat.app_utils.AppToasty
import com.codebrew.clikat.app_utils.AppUtils
import com.codebrew.clikat.app_utils.DialogsUtil
import com.codebrew.clikat.app_utils.GoogleLoginHelper
import com.codebrew.clikat.app_utils.extension.afterTextChanged
import com.codebrew.clikat.app_utils.extension.launchActivity
import com.codebrew.clikat.app_utils.extension.loadImage
import com.codebrew.clikat.app_utils.extension.onSnackbar
import com.codebrew.clikat.base.BaseActivity
import com.codebrew.clikat.data.AppDataType
import com.codebrew.clikat.data.DataManager
import com.codebrew.clikat.data.constants.AppConstants.Companion.ADMIN_SUPPLIER_DETAIL
import com.codebrew.clikat.data.constants.PrefenceConstants
import com.codebrew.clikat.data.model.api.AddressBean
import com.codebrew.clikat.data.model.api.CblCustomerDomain
import com.codebrew.clikat.data.preferences.PreferenceHelper
import com.codebrew.clikat.databinding.ActivityLoginBinding
import com.codebrew.clikat.databinding.DialougeForgotPassBinding
import com.codebrew.clikat.di.ViewModelProviderFactory
import com.codebrew.clikat.modal.AppGlobal
import com.codebrew.clikat.modal.Data1
import com.codebrew.clikat.modal.LoginCreds
import com.codebrew.clikat.modal.PojoSignUp
import com.codebrew.clikat.modal.other.SettingModel.DataBean.*
import com.codebrew.clikat.module.bottom_navigation.MainScreenActivity
import com.codebrew.clikat.module.new_signup.SigninActivity
import com.codebrew.clikat.module.signup.SignupActivity
import com.codebrew.clikat.preferences.DataNames
import com.codebrew.clikat.preferences.Prefs
import com.codebrew.clikat.utils.Dialogs.ForgotPasswordDialouge
import com.codebrew.clikat.utils.Dialogs.ForgotPasswordDialouge.OnOkClickListener
import com.codebrew.clikat.utils.GeneralFunctions
import com.codebrew.clikat.utils.StaticFunction
import com.codebrew.clikat.utils.StaticFunction.showNoInternetDialog
import com.codebrew.clikat.utils.StaticFunction.sweetDialogueSuccess
import com.codebrew.clikat.utils.Utils
import com.codebrew.clikat.utils.configurations.Configurations
import com.facebook.*
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.messaging.FirebaseMessaging
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_location.*
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.activity_login.clickatTextInputLayout
import kotlinx.android.synthetic.main.activity_login.etEmail
import kotlinx.android.synthetic.main.activity_login.etPassword
import kotlinx.android.synthetic.main.activity_login.layoutLogin
import kotlinx.android.synthetic.main.activity_login.linear_bg
import kotlinx.android.synthetic.main.activity_login.tvForgotPassword
import kotlinx.android.synthetic.main.activity_login.tvLogin
import kotlinx.android.synthetic.main.activity_login.tvSignup
import kotlinx.android.synthetic.main.activity_login.tvv
import kotlinx.android.synthetic.main.activity_login_new.*
import org.json.JSONArray
import org.json.JSONObject
import timber.log.Timber
import java.util.*
import javax.inject.Inject
import kotlin.collections.HashMap

/*
 * Created by Ankit Jindal on 21/4/16.
 */

const val GOOGLE_SIGN_IN = 784

class LoginActivity : BaseActivity<ActivityLoginBinding, LoginViewModel>(), LoginNavigator, View.OnClickListener {

    private var loginCreds: LoginCreds? = null

    @Inject
    lateinit var dataManager: DataManager

    @Inject
    lateinit var prefHelper: PreferenceHelper
    var cleverTapDefaultInstance: CleverTapAPI? = null
    @Inject
    lateinit var factory: ViewModelProviderFactory


    @Inject
    lateinit var dialogsUtil: DialogsUtil

    @Inject
    lateinit var appUtils: AppUtils

    private var mLoginViewModel: LoginViewModel? = null

    private lateinit var mBinding: ActivityLoginBinding

    private var fbId = ""

    private var callbackManager: CallbackManager? = null

    private var settingBean: SettingData? = null

    private var fbJson: JSONObject? = null
    private var validNumber = false

    @Inject
    lateinit var googleHelper: GoogleLoginHelper

    private var adminData: AdminDetail? = null

    private var clientData: CblCustomerDomain? = null

    private val textConfig by lazy { appUtils.loadAppConfig(0).strings }

    private var isFromBiometric: Boolean = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        cleverTapDefaultInstance = CleverTapAPI.getDefaultInstance(applicationContext)
        mBinding = viewDataBinding
        callbackManager = CallbackManager.Factory.create()
        viewModel.navigator = this

        mBinding.color = Configurations.colors
        mBinding.drawables = Configurations.drawables
        mBinding.strings = textConfig

        val statusColor = Color.parseColor(Configurations.colors.appBackground)
        StaticFunction.setStatusBarColor(this, getColor(R.color.dark_blue))
        loginCreds = dataManager.getGsonValue(PrefenceConstants.BIOMETRIC_DETAIL, LoginCreds::class.java)
        clientData = prefHelper.getGsonValue(PrefenceConstants.DB_INFORMATION, CblCustomerDomain::class.java)
        adminData = prefHelper.getGsonValue(PrefenceConstants.ADMIN_DETAILS, AdminDetail::class.java)
        settingBean = prefHelper.getGsonValue(DataNames.SETTING_DATA, SettingData::class.java)

        appUtils.checkFacebookLogin(this).let {
            if (it.isNotEmpty()) {
                tvFacebook.visibility = View.VISIBLE
            } else {
                tvFacebook.visibility = View.GONE
            }
        }

        settingBean?.is_vendor_registration?.let {
            if (it == "1") {
                tv_vendor_regis.text = getString(R.string.register_service_provider, textConfig?.supplier)

                tv_vendor_regis.visibility = View.VISIBLE
            }
        }

        settingBean?.auth_terms_check?.let {
            if (it == "1") {
                group_terms.visibility = View.VISIBLE
            }
        }

        appUtils.checkGoogleLogin(this).let {
            if (it.isNotEmpty()) {
                btnGoogle.visibility = View.VISIBLE
                //initialize google login
                googleHelper.initialize(this, it)
            } else {
                btnGoogle.visibility = View.GONE
            }
        }

        initialise()
        dynamicBackground()
        //  settingLayout(screenFlowBean)
        setlanguage()
        settypeface()
        clickListner()
        updateToken()
        setupUi()
    }

    fun setupUi() {
        if (settingBean?.is_craveQatar_login_theme.equals("1")) {
            groupLoginCrave.visibility = View.VISIBLE
            groupLoginOther.visibility = View.INVISIBLE
            iv_back.visibility = View.INVISIBLE
            layoutLogin.setBackgroundColor(Color.parseColor(Configurations.colors.splash_bg))
        }
    }

    private fun initialise() {

        val locale = ConfigurationCompat.getLocales(Resources.getSystem().configuration)[0]

        val countryCode = adminData?.iso.let {
            if (it?.isNotEmpty() == true) {
                it
            } else {
                locale.country
            }
        } ?: locale.country

        if (settingBean?.fixed_country_code == "1") {
            val jsonObj = JSONArray(settingBean?.countries_array ?: "")
            etCountryCode.setCustomMasterCountries(jsonObj.join(",").replace("\"", "").toUpperCase(Locale.getDefault()))
        } else if (settingBean?.enable_limit_country_codes == "1") {
            dataManager.getKeyValue(PrefenceConstants.COUNTRY_CODES, PrefenceConstants.TYPE_STRING).toString().let {
                val jsonObj = StaticFunction.getJsonCountries(it)
                etCountryCode.setCustomMasterCountries(jsonObj.join(",").replace("\"", "").toUpperCase(Locale.getDefault()))
            }
        }
        etCountryCode.setDefaultCountryUsingNameCode(countryCode)
        etCountryCode.resetToDefaultCountry()

        etCountryCode.registerCarrierNumberEditText(etEmail)
        etCountryCode.setNumberAutoFormattingEnabled(false)

        etCountryCode.setPhoneNumberValidityChangeListener { isValidNumber: Boolean -> validNumber = isValidNumber }


        if (settingBean?.phone_registration_flag == "1") {
            etEmail?.hint = getString(R.string.hint_digit_phone_number)
            etCountryCode?.visibility = View.VISIBLE
            tvv?.hint = getString(R.string.hint_digit_phone_number)
            etEmail?.inputType = InputType.TYPE_CLASS_NUMBER
            groupLoginWithOtp?.visibility = View.INVISIBLE
            etEmail?.imeOptions = EditorInfo.IME_ACTION_DONE
            login_text?.text = getString(R.string.login_signup)
        } else {
            groupLoginWithOtp?.visibility = View.VISIBLE
        }

        if (!(settingBean?.is_craveQatar_login_theme == "1"))
            checkBioMetricAvailability()

    }

    override fun onResume() {
        super.onResume()

        tvLoginByTouch?.visibility = if (settingBean?.enable_biometric_login == "1" && loginCreds != null && verifyBioMetricExistence()) View.VISIBLE else View.GONE

    }

    private fun registerFbCallback() {
        // Callback registration
        LoginManager.getInstance().logInWithReadPermissions(this, listOf("email", "public_profile"))
        LoginManager.getInstance().registerCallback(callbackManager, object : FacebookCallback<LoginResult?> {
            override fun onSuccess(loginResult: LoginResult?) { // App code
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

    private fun updateToken() {

        FirebaseMessaging.getInstance().token.addOnCompleteListener {
            if (it.isSuccessful) {
                val token  = it.result
                prefHelper.setkeyValue(DataNames.REGISTRATION_ID, token.toString())
            }

        }
    }

    private fun clickListner() {
        tvSignup.setOnClickListener(this)
        tvLogin.setOnClickListener(this)
        tvFacebook.setOnClickListener(this)
        btnGoogle.setOnClickListener(this)
        tvForgotPassword.setOnClickListener(this)
        tvForgotPassword2.setOnClickListener(this)
        iv_back.setOnClickListener(this)
        tvLoginByTouch?.setOnClickListener(this)
        tv_vendor_regis.setOnClickListener(this)
        etEmail?.afterTextChanged {
            etCountryCode?.visibility = if (it.isNotEmpty() && it.isDigitsOnly()) View.VISIBLE else View.GONE
        }
    }

    private fun settingLayout(screenFlowBean: ScreenFlowBean?) {
        when (screenFlowBean?.app_type) {
            AppDataType.Ecom.type -> linear_bg.setBackgroundResource(R.drawable.bg_ecomerce)
            AppDataType.Food.type -> linear_bg.setBackgroundResource(R.drawable.bg_foodserv)
            AppDataType.HomeServ.type -> linear_bg.setBackgroundResource(R.drawable.bg_homeserv)
            else -> linear_bg.setBackgroundResource(R.drawable.bg_homeserv)
        }
    }

    private fun dynamicBackground() {

        if (settingBean?.login_icon_url.toString().isNotEmpty()) {
            if (!(settingBean?.is_craveQatar_login_theme.equals("1")))
                linear_bg.loadImage(settingBean?.login_icon_url ?: "")
        }
    }

    override fun onStop() {
        super.onStop()
        onTrimMemory(ComponentCallbacks2.TRIM_MEMORY_UI_HIDDEN)
    }

    private fun settypeface() {
        etEmail.typeface = AppGlobal.regular
        etPassword.typeface = AppGlobal.regular
        tvForgotPassword.typeface = AppGlobal.regular
        tvLogin.typeface = AppGlobal.regular
        tvFacebook.typeface = AppGlobal.regular
        btnGoogle.typeface = AppGlobal.regular
        tvSignup.typeface = AppGlobal.semi_bold
        tv_vendor_regis.typeface = AppGlobal.semi_bold
        tvLoginByTouch.typeface = AppGlobal.regular
    }

    private fun setlanguage() {

        val selectedLang = prefHelper.getKeyValue(DataNames.SELECTED_LANGUAGE, PrefenceConstants.TYPE_STRING).toString()

        if (selectedLang == "arabic" || selectedLang == "ar") {
            GeneralFunctions.force_layout_to_RTL(this)
        } else {
            GeneralFunctions.force_layout_to_LTR(this)
        }
    }

    override fun getLayoutId(): Int {
        return R.layout.activity_login
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
        setResult(Activity.RESULT_CANCELED)
    }


    private fun forgotPassword() {
        val dialouge = ForgotPasswordDialouge(this, true, OnOkClickListener { emailId: String -> forgotPasswordApi(emailId) })
        dialouge.show()

        dialouge.setOnDismissListener {
            hideKeyboard()
        }

    }

    private fun forgotPasswordApi(emailId: String) {
        if (isNetworkConnected) {
            viewModel.validateForgotPswr(emailId)
        }
    }


    private fun apiRegisterByPhone() {
        val phone = etEmail.text.toString().trim()
        val hashMap = HashMap<String, String>()
        val locationUser = Prefs.with(this).getObject(PrefenceConstants.ADRS_DATA, AddressBean::class.java)
        hashMap["deviceToken"] = prefHelper.getKeyValue(DataNames.REGISTRATION_ID, PrefenceConstants.TYPE_STRING).toString()
        hashMap["deviceType"] = 0.toString()
        hashMap["languageId"] = prefHelper.getLangCode()
        hashMap["latitude"] = locationUser?.latitude.toString()
        hashMap["longitude"] = locationUser?.longitude.toString()
        hashMap["mobileNumber"] = phone
        hashMap["countryCode"] = etCountryCode?.selectedCountryCodeWithPlus.toString()

        if (isNetworkConnected) {
            mLoginViewModel?.apiRegisterByPhone(hashMap)
        }
    }

    private fun apiLogin(emailPhone: String, pass: String) {
        val hashMap = HashMap<String, String>()
        hashMap["deviceToken"] = prefHelper.getKeyValue(DataNames.REGISTRATION_ID, PrefenceConstants.TYPE_STRING).toString()
        hashMap["deviceType"] = 0.toString()
        hashMap["password"] = pass
        hashMap["languageId"] = prefHelper.getLangCode()

        if (emailPhone.isDigitsOnly()) {
            hashMap["phoneNumber"] = emailPhone
            hashMap["countryCode"] = etCountryCode?.selectedCountryCodeWithPlus.toString()
        } else
            hashMap["email"] = emailPhone

        if (isNetworkConnected) {
            mLoginViewModel?.validateLogin(hashMap)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        when (id) {
            android.R.id.home -> onBackPressed()
        }
        return super.onOptionsItemSelected(item)
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

    private fun loadPic(optJSONObject: JSONObject?): String? {
        return optJSONObject?.optJSONObject("data")?.optString("url", "")
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        callbackManager?.onActivityResult(requestCode, resultCode, data)
        super.onActivityResult(requestCode, resultCode, data)

        googleHelper.activityResult(requestCode, resultCode, data) {
            if (it != null) {
                hideLoading()
                googleHelper.logoutGoogle {

                }
                mBinding.root.onSnackbar(it)
            }
        }
        if (requestCode == 101 && resultCode == RESULT_OK) {
            Timber.d("$data")
        }
    }

    override fun onClick(p0: View?) {
        when (p0?.id) {
            R.id.tvSignup -> {
                launchActivity<SignupActivity> {
                    putExtra(DataNames.PHONE_VERIFIED, "0")
                }
                finish()
            }
            R.id.tvLogin -> {
                if (isNetworkConnected) {
                    hideKeyboard()
                    if (settingBean?.phone_registration_flag == "1") {
                        if (validatePhoneValidations())
                            apiRegisterByPhone()
                    } else if (checkBoxTerms?.isChecked == false && settingBean?.auth_terms_check == "1") {
                        tvv.error = null
                        clickatTextInputLayout.error = null
                        AppToasty.error(this, getString(R.string.plese_accept_terms_and_conditions))
                    } else {
                        if (validateValues())
                            apiLogin(etEmail.text.toString().trim(), etPassword.text.toString().trim())

                    }
                } else
                    showNoInternetDialog(this@LoginActivity)

            }
            R.id.tvLoginByTouch -> {
                if (settingBean?.enable_biometric_login == "1" && verifyBioMetricExistence() && !loginCreds?.biometricLoginEmail.isNullOrEmpty()
                        && !loginCreds?.biometricPassword.isNullOrEmpty()) {
                    isFromBiometric = true
                    apiLogin(loginCreds?.biometricLoginEmail ?: "", loginCreds?.biometricPassword
                            ?: "")
                }
            }
            R.id.btnGoogle -> {
                googleHelper.handleClick(mLoginViewModel)

            }
            R.id.tv_vendor_regis -> {
                if (BuildConfig.CLIENT_CODE == "bodyformula_0497" && !clientData?.supplier_domain.isNullOrEmpty()) {
                    Utils.openWebView(this, clientData?.supplier_domain.plus(ADMIN_SUPPLIER_DETAIL))
                } else if (!clientData?.admin_domain.isNullOrEmpty()) {
                    Utils.openWebView(this, clientData?.admin_domain.plus(ADMIN_SUPPLIER_DETAIL))
                }
            }
            R.id.tvFacebook -> if (isNetworkConnected) {
                registerFbCallback()
            } else {
                showNoInternetDialog(this@LoginActivity)
            }
            R.id.tvForgotPassword -> forgotPassword()
            R.id.tvForgotPassword2 -> forgotPassword()
            R.id.iv_back -> onBackPressed()
        }
    }

    private fun validatePhoneValidations(): Boolean {
        val emailText = etEmail.text.toString().trim()
        return when {
            emailText.isEmpty() -> {
                tvv.requestFocus()
                tvv.error = getString(R.string.empty_phone_number)
                false
            }
            !validNumber -> {
                tvv?.requestFocus()
                tvv?.error = getString(R.string.enter_valid_number)
                false
            }
            else -> {
                clickatTextInputLayout.error = null
                tvv.error = null
                true
            }
        }
    }

    private fun validateValues(): Boolean {
        val emailText = etEmail.text.toString().trim()
        return when {
            emailText.isEmpty() -> {
                tvv.requestFocus()
                tvv.error = getString(R.string.empty_email_phone)
                false
            }
            emailText.isDigitsOnly() -> {
                checkPhoneValidations()
            }
            !emailText.isDigitsOnly() -> {
                checkEmailValidations()
            }
            else -> true
        }

    }

    private fun checkPhoneValidations(): Boolean {
        return when {
            !validNumber -> {
                tvv?.requestFocus()
                tvv?.error = getString(R.string.enter_valid_number)
                false
            }
            etPassword.text.toString().trim().isEmpty() -> {
                clickatTextInputLayout.requestFocus()
                tvv.error = null
                clickatTextInputLayout.error = getString(R.string.empty_pwd)
                false
            }
            else -> {
                clickatTextInputLayout.error = null
                tvv.error = null
                true
            }

        }
    }

    private fun checkEmailValidations(): Boolean {
        return when {
            !GeneralFunctions.isValidEmail(etEmail.text.toString().trim()) -> {
                tvv.requestFocus()
                tvv.error = getString(R.string.invalid_email)
                false
            }
            etPassword.text.toString().trim().isEmpty() -> {
                clickatTextInputLayout.requestFocus()
                tvv.error = null
                clickatTextInputLayout.error = getString(R.string.empty_pwd)
                false
            }
            else -> {
                clickatTextInputLayout.error = null
                tvv.error = null
                true
            }
        }
    }

    override fun getBindingVariable(): Int {
        return BR.viewModel
    }

    override fun getViewModel(): LoginViewModel {
        mLoginViewModel = ViewModelProviders.of(this, factory).get(LoginViewModel::class.java)
        return mLoginViewModel as LoginViewModel
    }

    override fun onForgotPswr(message: String) {
        sweetDialogueSuccess(this@LoginActivity, resources.getString(R.string.success), message,
                false, 33, null, null)
    }

    override fun onLogin(userData: Data1) {
        val profileUpdate = HashMap<String, Any>()
        profileUpdate["Name"] = userData.email.toString() // String

        profileUpdate["Identity"] = userData.id.toString() // String or number

        profileUpdate["Email"] = userData.email.toString() // Email address of the user

        profileUpdate["Phone"] = userData.mobile_no.toString() // Phone (with the country code, starting with +)

        profileUpdate["Gender"] = "" // Can be either M or F

        profileUpdate["DOB"] = Date() // Date of Birth. Set the Date object to the appropriate value first


// optional fields. controls whether the user will be sent email, push etc.

// optional fields. controls whether the user will be sent email, push etc.
        profileUpdate["MSG-email"] = false // Disable email notifications

        profileUpdate["MSG-push"] = true // Enable push notifications

        profileUpdate["MSG-sms"] = false // Disable SMS notifications

        profileUpdate["MSG-whatsapp"] = true // Enable WhatsApp notifications




        cleverTapDefaultInstance?.onUserLogin(profileUpdate);
        afterLogin()
    }


    override fun userNotVerified(userData: Data1) {
        val prodViewedAction = mapOf(
                "Email" to userData.email,
                "phoneNumber" to userData.mobile_no,
                "Login_mode" to "Normal",
                "status" to "0")

        cleverTapDefaultInstance?.pushEvent("Login_event", prodViewedAction)
        notVerified(userData)
    }

    override fun registerByPhone() {
        launchActivity<SigninActivity> {}
        finish()
    }

    private fun afterLogin() {
        if (validateBioCreds()) {
            AlertDialog.Builder(this)
                    .setMessage(getString(R.string.enable_biometric_login))
                    .setPositiveButton(getString(R.string.yes)) { _, _ ->
                        initialiseBiometric()
                    }.setNegativeButton(getString(R.string.no)) { _, _ ->
                        loginSuccess()
                    }.show()
        } else if (settingBean?.enable_biometric_login == "1" && verifyBioMetricExistence() && loginCreds != null && isFromBiometric)
            initialiseBiometric()
        else
            loginSuccess()
    }

    private fun loginSuccess() {
        appUtils.triggerEvent("login")

        val isGuest = settingBean?.login_template.isNullOrEmpty() || settingBean?.login_template == "0"

        if (settingBean?.is_craveQatar_login_theme == "1") {
            launchActivity<MainScreenActivity>()
        } else {
            if (isGuest) {
                setResult(Activity.RESULT_OK)
            } else {
                val prodViewedAction = mapOf(
                        "Email" to etEmail.text.toString(),
                        "phoneNumber" to "",
                        "Login_mode" to "Normal",
                        "status" to "1")

                cleverTapDefaultInstance?.pushEvent("Login_event", prodViewedAction)
                launchActivity<MainScreenActivity>()
            }
        }
        isFromBiometric = false
        finish()
    }

    private fun notVerified(userData: Data1) {
        /*     val isGuest = settingBean?.login_template.isNullOrEmpty() || settingBean?.login_template == "0"

        if (isGuest) {
            setResult(Activity.RESULT_OK)
        } else {
            launchActivity<MainScreenActivity>()
        }*/

        launchActivity<SignupActivity> {
            putExtra("userData", userData)
        }

        finish()
    }

    private fun enterEmail() {

        val binding = DataBindingUtil.inflate<DialougeForgotPassBinding>(LayoutInflater.from(this), R.layout.dialouge_forgot_pass, null, false)
        binding.color = Configurations.colors

        val mDialog = dialogsUtil.showDialogFix(this, binding.root)
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

    override fun onSocialLogin(signup: PojoSignUp?, type: String) {

        if (type == "facebook") {
            signup?.data?.fbId = fbId
        }

        prefHelper.addGsonValue(DataNames.USER_DATA, Gson().toJson(signup))

        if (signup?.data?.otp_verified == 1) {
            //prefHelper.addGsonValue(DataNames.USER_DATA, Gson().toJson(signup))
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
            launchActivity<SignupActivity> {
                putExtra(DataNames.PHONE_VERIFIED, "1")

            }
            finish()
        }
    }


    override fun onErrorOccur(message: String) {
        isFromBiometric = false
        mBinding.root.onSnackbar(message)
    }

    override fun onSessionExpire() {

    }

    private fun initialiseBiometric() {
        val newExecutor = ContextCompat.getMainExecutor(this)
        val myBiometricPrompt = BiometricPrompt(this, newExecutor, object : BiometricPrompt.AuthenticationCallback() {

            override fun onAuthenticationError(errorCode: Int, @NonNull errString: CharSequence) {
                super.onAuthenticationError(errorCode, errString)
                AppToasty.error(this@LoginActivity, getString(R.string.authentication_error))
            }

            //onAuthenticationSucceeded is called when a fingerprint is matched successfully//
            override fun onAuthenticationSucceeded(@NonNull result: BiometricPrompt.AuthenticationResult) {
                super.onAuthenticationSucceeded(result)
                saveCreds()
                loginSuccess()
                AppToasty.success(this@LoginActivity, getString(R.string.authentication_success))
            }

            //onAuthenticationFailed is called when the fingerprint doesn’t match//
            override fun onAuthenticationFailed() {
                super.onAuthenticationFailed()
                AppToasty.error(this@LoginActivity, getString(R.string.authentication_failed))
            }
        })

        authenticateBiometric(myBiometricPrompt)
    }

    private fun saveCreds() {
        val creds = LoginCreds()
        creds.biometricLoginEmail = if (isFromBiometric && loginCreds != null) loginCreds?.biometricLoginEmail else etEmail?.text.toString().trim()
        creds.biometricPassword = if (isFromBiometric && loginCreds != null) loginCreds?.biometricPassword else etPassword?.text.toString().trim()

        dataManager.addGsonValue(PrefenceConstants.BIOMETRIC_DETAIL, Gson().toJson(creds))
    }


    private fun verifyBioMetricExistence(): Boolean {
        val biometricManager = BiometricManager.from(this)
        return when (biometricManager.canAuthenticate(BIOMETRIC_WEAK or BIOMETRIC_STRONG)) {
            BiometricManager.BIOMETRIC_SUCCESS ->
                true
            BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE ->
                false
            BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE ->
                false

            BiometricManager.BIOMETRIC_ERROR_SECURITY_UPDATE_REQUIRED -> {
                false
            }
            BiometricManager.BIOMETRIC_ERROR_UNSUPPORTED -> {
                false
            }
            BiometricManager.BIOMETRIC_STATUS_UNKNOWN -> {
                false
            }
            else -> false
        }
    }

    private fun checkBioMetricAvailability() {

        val biometricManager = BiometricManager.from(this)

        when (biometricManager.canAuthenticate(BIOMETRIC_WEAK or BIOMETRIC_STRONG)) {
            BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED -> {
                // Prompts the user to create credentials that your app accepts.
                askUserToAddBioMetric()

            }
            BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE -> {
                TODO()
            }
            BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE -> {
                TODO()
            }
            BiometricManager.BIOMETRIC_ERROR_SECURITY_UPDATE_REQUIRED -> {
                TODO()
            }
            BiometricManager.BIOMETRIC_ERROR_UNSUPPORTED -> {
                TODO()
            }
            BiometricManager.BIOMETRIC_STATUS_UNKNOWN -> {
                TODO()
            }
            BiometricManager.BIOMETRIC_SUCCESS -> {
                TODO()
            }
        }

    }


    private fun askUserToAddBioMetric() {

        AlertDialog.Builder(this)
                .setMessage(getString(R.string.biomentric_required))
                .setCancelable(false)
                .setPositiveButton(getString(R.string.proceed)) { _, _ ->

                    val enrollIntent = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                        Intent(Settings.ACTION_BIOMETRIC_ENROLL).apply {
                            putExtra(Settings.EXTRA_BIOMETRIC_AUTHENTICATORS_ALLOWED,
                                    BIOMETRIC_STRONG or BIOMETRIC_WEAK)
                        }
                    } else {
                        TODO("VERSION.SDK_INT < R")
                    }
                    startActivityForResult(enrollIntent, 101)

                }.setNegativeButton(getString(R.string.no_thanks)) { _, _ ->
                    {

                    }
                }
                .show()

    }


    private fun validateBioCreds(): Boolean {
        return (settingBean?.enable_biometric_login == "1" && verifyBioMetricExistence() && (loginCreds == null || loginCreds?.biometricLoginEmail
                != etEmail.text.toString().trim() || loginCreds?.biometricPassword != etPassword.text.toString().trim()) && !isFromBiometric)
    }

    private fun authenticateBiometric(myBiometricPrompt: BiometricPrompt) {
        val promptInfo: BiometricPrompt.PromptInfo = BiometricPrompt.PromptInfo.Builder()
                .setTitle(getString(R.string.authenticate_app, getString(R.string.app_name)))
                .setSubtitle(getString(R.string.authentication_text))
                .setDescription(getString(R.string.authenticate_app_des))
                .setNegativeButtonText(getString(R.string.cancel))
                .setConfirmationRequired(false)
                .setAllowedAuthenticators(BIOMETRIC_WEAK or BIOMETRIC_STRONG)
                .build()


        myBiometricPrompt.authenticate(promptInfo)
    }
}