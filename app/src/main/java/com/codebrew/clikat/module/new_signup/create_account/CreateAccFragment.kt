package com.codebrew.clikat.module.new_signup.create_account

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.CompoundButton
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProviders
import com.clevertap.android.sdk.CleverTapAPI
import com.codebrew.clikat.BR
import com.codebrew.clikat.R
import com.codebrew.clikat.app_utils.*
import com.codebrew.clikat.app_utils.extension.launchActivity
import com.codebrew.clikat.app_utils.extension.onSnackbar
import com.codebrew.clikat.app_utils.navController
import com.codebrew.clikat.base.BaseFragment
import com.codebrew.clikat.data.constants.AppConstants
import com.codebrew.clikat.data.constants.PrefenceConstants
import com.codebrew.clikat.data.preferences.PreferenceHelper
import com.codebrew.clikat.databinding.CreateAccFragmentBinding
import com.codebrew.clikat.databinding.DialougeForgotPassBinding
import com.codebrew.clikat.di.ViewModelProviderFactory
import com.codebrew.clikat.modal.Data1
import com.codebrew.clikat.modal.PojoSignUp
import com.codebrew.clikat.modal.other.SettingModel
import com.codebrew.clikat.module.bottom_navigation.MainScreenActivity
import com.codebrew.clikat.module.login.LoginActivityNew
import com.codebrew.clikat.module.login.LoginNavigator
import com.codebrew.clikat.module.login.LoginViewModel
import com.codebrew.clikat.module.roadmap.RoadMapActivity
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
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.create_acc_fragment.*
import kotlinx.android.synthetic.main.create_acc_fragment.tvLogin
import kotlinx.android.synthetic.main.fragment_more_setting.*
import kotlinx.android.synthetic.main.login_fragment.*
import org.json.JSONObject
import java.util.*
import javax.inject.Inject

class CreateAccFragment : BaseFragment<CreateAccFragmentBinding, LoginViewModel>(), LoginNavigator,
        CompoundButton.OnCheckedChangeListener {
    @Inject
    lateinit var permissionUtil: PermissionFile
    companion object {
        fun newInstance() = CreateAccFragment()
    }

    var cleverTapDefaultInstance: CleverTapAPI? = null
    private var isTermsAccepted = false

    private var mBinding: CreateAccFragmentBinding? = null

    @Inject
    lateinit var factory: ViewModelProviderFactory

    @Inject
    lateinit var prefHelper: PreferenceHelper

    @Inject
    lateinit var appUtils: AppUtils

    private lateinit var viewModel: LoginViewModel

    private var settingBean: SettingModel.DataBean.SettingData? = null

    @Inject
    lateinit var dialogsUtil: DialogsUtil

    @Inject
    lateinit var googleHelper: GoogleLoginHelper

    private var fbId = ""

    private var callbackManager: CallbackManager? = null
    private var fbJson: JSONObject? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        callbackManager = CallbackManager.Factory.create()

        settingBean = prefHelper.getGsonValue(DataNames.SETTING_DATA, SettingModel.DataBean.SettingData::class.java)

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        cleverTapDefaultInstance = CleverTapAPI.getDefaultInstance(requireActivity())
        mBinding = viewDataBinding
        mBinding?.color = Configurations.colors
        mBinding?.strings = appUtils.loadAppConfig(0).strings
        mBinding?.settingData = settingBean

        viewModel.navigator = this
        googleHelper.initialize(requireActivity(), activity?.getString(R.string.default_web_client_id)?:"")
        updateToken()

        //facebookCallback()


        appUtils.checkGoogleLogin(requireActivity()).let {
            if (it.isNotEmpty()) {
                lytGoogle.visibility = View.VISIBLE
                //initialize google login
                googleHelper.initialize(requireActivity(), it)
            } else {
                lytGoogle.visibility = View.GONE
            }
        }

        appUtils.checkFacebookLogin(requireActivity()).let {
            if (it.isNotEmpty()) {
                lytFacebook.visibility = View.VISIBLE
            } else {
                lytFacebook.visibility = View.GONE
            }
        }

        tvTitleGuest.setOnClickListener {
            activity?.launchActivity<MainScreenActivity>()
        }


        lytGoogle.setOnClickListener {
            googleHelper.handleClick(viewModel)
        }

        btnGoogleCrave.setOnClickListener {
            googleHelper.handleClick(viewModel)
        }

        tvSignWithEmail.setOnClickListener {
            validateValues("signup")
        }

        lytFacebook.setOnClickListener {
            //validateValues("facebook")

            facebookCallback()
        }

//        btnFacebookCrave.setOnClickListener {
//            validateValues("facebook")
//        }

        tvTitleGuest.setOnClickListener {
            activity?.launchActivity<RoadMapActivity>()
            activity?.finish()
        }
        tvLogin.setOnClickListener {
            activity?.launchActivity<LoginActivityNew>(AppConstants.REQUEST_USER_PROFILE)
        }

        tvcontact.setOnClickListener {
            if (permissionUtil.hasCallPermissions(requireContext())) {
                callPhone("44072888")
            } else {
                permissionUtil.phoneCallTask(requireContext())
            }
        }
    }
    private fun callPhone(number: String) {
        val intent = Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", number, null))
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED) {
            Manifest.permission.CALL_PHONE
            startActivity(intent)
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(arrayOf(Manifest.permission.CALL_PHONE), 1)
            }
        }
    }
    override fun onCheckedChanged(buttonView: CompoundButton?, isChecked: Boolean) {
        if (buttonView?.isPressed == true)
            isTermsAccepted = isChecked
    }


    private fun validateValues(param: String) {
        if (!isTermsAccepted && !(settingBean?.is_craveQatar_login_theme == "1")) {
            mBinding?.root?.onSnackbar(getString(R.string.plese_accept_terms_and_conditions))
        } else {
            if (param == "signup") {
                navController(this@CreateAccFragment).navigate(R.id.action_createAccFragment_to_registerFragment)
            } else {
               // login_button.callOnClick()
            }
        }
    }

    override fun getBindingVariable(): Int {
        return BR.viewModel
    }

    override fun getLayoutId(): Int {
        return R.layout.create_acc_fragment
    }

    override fun getViewModel(): LoginViewModel {
        viewModel = ViewModelProviders.of(this, factory).get(LoginViewModel::class.java)
        return viewModel
    }

    override fun onForgotPswr(message: String) {

    }

    override fun onLogin(userData: Data1) {
        afterLogin1()
    }

    override fun onSocialLogin(signup: PojoSignUp?, type: String) {
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

            signup.data.customer_payment_id?.let {
                prefHelper.setkeyValue(PrefenceConstants.CUSTOMER_PAYMENT_ID, it)
            }

            signup.data?.referral_id?.let {
                prefHelper.setkeyValue(PrefenceConstants.USER_REFERRAL_ID, it)
            }

            afterLogin(signup)
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

    override fun onErrorOccur(message: String) {
        mBinding?.root?.onSnackbar(message)
    }

    override fun onSessionExpire() {
        openActivityOnTokenExpire()
    }


    private fun facebookCallback() {
      //  login_button.setPermissions(listOf("email", "public_profile"))
        LoginManager.getInstance().logInWithReadPermissions(this, listOf("email","public_profile"));
        // Callback registration
        LoginManager.getInstance().registerCallback(callbackManager, object : FacebookCallback<LoginResult?> {
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
                googleHelper.logoutGoogle {

                }
                mBinding?.root?.onSnackbar(it)
            }
        }
    }

    private fun afterLogin1() {
        val isGuest = settingBean?.login_template.isNullOrEmpty() || settingBean?.login_template == "0"

        if (isGuest) {
            activity?.setResult(Activity.RESULT_OK)
        } else {
            activity?.launchActivity<MainScreenActivity>()
        }
        activity?.finish()
    }
    private fun afterLogin(signup: PojoSignUp?) {
        val isGuest = settingBean?.login_template.isNullOrEmpty() || settingBean?.login_template == "0"

        if (isGuest) {
            activity?.setResult(Activity.RESULT_OK)
        } else {
            val profileUpdate = HashMap<String, Any>()
            profileUpdate["Name"] = signup?.data?.email.toString() // String

            profileUpdate["Identity"] =  signup?.data?.email.toString()// String or number

            profileUpdate["Email"] = signup?.data?.email.toString() // Email address of the user

            profileUpdate["Phone"] = signup?.data?.mobile_no.toString() // Phone (with the country code, starting with +)

            profileUpdate["Gender"] = "" // Can be either M or F

            profileUpdate["DOB"] = Date() // Date of Birth. Set the Date object to the appropriate value first

            cleverTapDefaultInstance?.onUserLogin(profileUpdate)
            activity?.launchActivity<MainScreenActivity>()
        }
        activity?.finish()
    }

    private fun notVerified(userData: Data1?) {

        val action = CreateAccFragmentDirections.actionCreateAccFragmentToEnterPhoneFrag(userData?.access_token)
        navController(this@CreateAccFragment).navigate(action)
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


}
