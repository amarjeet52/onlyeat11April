package com.codebrew.clikat.module.signup

import android.app.Activity
import android.content.Intent
import android.content.res.Resources
import android.graphics.Color
import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.ConfigurationCompat
import androidx.core.text.isDigitsOnly
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.codebrew.clikat.BuildConfig
import com.codebrew.clikat.R
import com.codebrew.clikat.app_utils.AppToasty
import com.codebrew.clikat.app_utils.AppUtils
import com.codebrew.clikat.app_utils.GoogleLoginHelper
import com.codebrew.clikat.app_utils.extension.afterTextChanged
import com.codebrew.clikat.app_utils.extension.launchActivity
import com.codebrew.clikat.app_utils.extension.onSnackbar
import com.codebrew.clikat.app_utils.navController
import com.codebrew.clikat.data.AppDataType
import com.codebrew.clikat.data.DataManager
import com.codebrew.clikat.data.constants.AppConstants.Companion.ADMIN_SUPPLIER_DETAIL
import com.codebrew.clikat.data.constants.PrefenceConstants
import com.codebrew.clikat.data.model.api.AddressBean
import com.codebrew.clikat.data.model.api.CblCustomerDomain
import com.codebrew.clikat.data.model.others.GoogleLoginInput
import com.codebrew.clikat.data.preferences.PreferenceHelper
import com.codebrew.clikat.databinding.FragmentSignup1Binding
import com.codebrew.clikat.modal.AppGlobal
import com.codebrew.clikat.modal.PojoSignUp
import com.codebrew.clikat.modal.other.SettingModel
import com.codebrew.clikat.modal.other.SettingModel.DataBean.ScreenFlowBean
import com.codebrew.clikat.module.bottom_navigation.MainScreenActivity
import com.codebrew.clikat.module.login.LoginActivity
import com.codebrew.clikat.module.login.LoginActivityNew
import com.codebrew.clikat.module.webview.WebViewActivity
import com.codebrew.clikat.preferences.DataNames
import com.codebrew.clikat.preferences.Prefs
import com.codebrew.clikat.retrofit.RestClient
import com.codebrew.clikat.utils.*
import com.codebrew.clikat.utils.StaticFunction.getLanguage
import com.codebrew.clikat.utils.StaticFunction.isInternetConnected
import com.codebrew.clikat.utils.StaticFunction.showNoInternetDialog
import com.codebrew.clikat.utils.configurations.Configurations
import com.google.gson.Gson
import dagger.android.support.AndroidSupportInjection
import kotlinx.android.synthetic.main.fragment_signup_1.*
import org.json.JSONArray
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*
import javax.inject.Inject
import kotlin.collections.HashMap

/*
 * Created by cbl80 on 25/4/16.
 */
class SignupFragment1 : Fragment(), View.OnClickListener {

    private var termsCondition: SettingModel.DataBean.TermCondition? = null

    @Inject
    lateinit var googleHelper: GoogleLoginHelper

    @Inject
    lateinit var appUtils: AppUtils

    @Inject
    lateinit var dataManager: DataManager

    @Inject
    lateinit var prefHelper: PreferenceHelper

    private var settingBean: SettingModel.DataBean.SettingData? = null

    private var connectionDetector: ConnectionDetector? = null

    private var clientData: CblCustomerDomain? = null
    private val textConfig by lazy { appUtils.loadAppConfig(0).strings }
    private val colorConfig by lazy { Configurations.colors }
    private var adminData: SettingModel.DataBean.AdminDetail? = null
    private var validNumber = false

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidSupportInjection.inject(this)

        settingBean = prefHelper.getGsonValue(DataNames.SETTING_DATA, SettingModel.DataBean.SettingData::class.java)
        clientData = prefHelper.getGsonValue(PrefenceConstants.DB_INFORMATION, CblCustomerDomain::class.java)
        termsCondition = prefHelper.getGsonValue(PrefenceConstants.TERMS_CONDITION, SettingModel.DataBean.TermCondition::class.java)
        adminData = prefHelper.getGsonValue(PrefenceConstants.ADMIN_DETAILS, SettingModel.DataBean.AdminDetail::class.java)

        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val binding: FragmentSignup1Binding = DataBindingUtil.inflate(inflater, R.layout.fragment_signup_1, container, false)
        binding.color = Configurations.colors
        binding.drawables = Configurations.drawables
        binding.strings = textConfig
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        appUtils.checkFacebookLogin(activity ?: requireActivity()).let {
            if (it.isNotEmpty()) {
                tvFacebook.visibility = View.VISIBLE
            } else {
                tvFacebook.visibility = View.GONE
            }
        }


        settingBean?.auth_terms_check?.let {
            if (it == "1") {
                group_terms.visibility = View.VISIBLE
            }
        }

        settingBean?.is_vendor_registration?.let {
            if (it == "1") {
                tv_vendor_regis.text = getString(R.string.register_service_provider, textConfig?.supplier)

                tv_vendor_regis.visibility = View.VISIBLE
            }
        }


        appUtils.checkGoogleLogin(requireActivity()).let {
            if (it.isNotEmpty()) {
                btnGoogle.visibility = View.VISIBLE
                //initialize google login
                activity?.let { it1 -> googleHelper.initialize(it1, it) }
            } else {
                btnGoogle.visibility = View.GONE
            }
        }

        btnGoogle.setOnClickListener {
            googleHelper.handleClick(null) {
                googleLogin(it)
            }
        }

        initialise()
        setypeface()
        clickListner()
        spannableText()
        connectionDetector = ConnectionDetector(activity)
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
        }else if(settingBean?.enable_limit_country_codes=="1"){
            dataManager.getKeyValue(PrefenceConstants.COUNTRY_CODES, PrefenceConstants.TYPE_STRING).toString().let {
                val jsonObj=StaticFunction.getJsonCountries(it)
                etCountryCode.setCustomMasterCountries(jsonObj.join(",").replace("\"", "").toUpperCase(Locale.getDefault()))
            }
        }

        etCountryCode.setDefaultCountryUsingNameCode(countryCode)
        etCountryCode.resetToDefaultCountry()

        etCountryCode.registerCarrierNumberEditText(etEmail)
        etCountryCode.setNumberAutoFormattingEnabled(false)

        etCountryCode.setPhoneNumberValidityChangeListener { isValidNumber: Boolean -> validNumber = isValidNumber }

        if (settingBean?.enable_signup_phone_only == "1") {
            tvv?.hint = getString(R.string.hint_email_phone)
            etEmail?.afterTextChanged {
                if (settingBean?.enable_signup_phone_only == "1")
                    groupPhoneNumber?.visibility = if (it.isNotEmpty() && it.isDigitsOnly()) View.VISIBLE else View.GONE
            }
        }
    }

    private fun clickListner() {
        tvSignup.setOnClickListener(this)
        iv_back.setOnClickListener(this)
        tvFacebook.setOnClickListener(this)
        tvText.setOnClickListener(this)
        tv_vendor_regis.setOnClickListener(this)
    }

    private fun setypeface() {
        etEmail.typeface = AppGlobal.regular
        etPassword.typeface = AppGlobal.regular
        tvSignup.typeface = AppGlobal.semi_bold
        tvText.typeface = AppGlobal.semi_bold
        tvFacebook.typeface = AppGlobal.semi_bold
        btnGoogle.typeface = AppGlobal.semi_bold
        tv_vendor_regis.typeface = AppGlobal.semi_bold

    }

    private fun spannableText() {
        val ss = SpannableString(getString(R.string.i_accept_terms_and_conditions))

        val clickableSpan1 = object : ClickableSpan() {
            override fun onClick(widget: View) {
                if (termsCondition?.termsAndConditions == 0) return
                activity?.launchActivity<WebViewActivity> {
                    putExtra("terms", 0)
                }
            }


            //This is in order to change the default appereance of the link
            override fun updateDrawState(ds: TextPaint) {
                super.updateDrawState(ds)
                ds.color = Color.parseColor(colorConfig.primaryColor)
                ds.isUnderlineText = true
            }
        }
        //here you set the starting and ending char of the link in the string
        ss.setSpan(clickableSpan1, 14, ss.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        tvTerms.text = ss
        tvTerms.movementMethod = LinkMovementMethod.getInstance()
    }

    private fun settingLayout(screenFlowBean: ScreenFlowBean) {
        when (screenFlowBean.app_type) {
            AppDataType.Ecom.type -> linear_bg.setBackgroundResource(R.drawable.bg_ecomerce)
            AppDataType.Food.type -> linear_bg.setBackgroundResource(R.drawable.bg_foodserv)
            AppDataType.HomeServ.type -> linear_bg.setBackgroundResource(R.drawable.bg_homeserv)
            else -> linear_bg.setBackgroundResource(R.drawable.bg_homeserv)
        }
    }


    override fun onClick(view: View) {
        when (view.id) {
            R.id.tvSignup -> {
                if (settingBean?.enable_signup_phone_only == "1") {
                    if (validateValues()) {
                        if (connectionDetector?.isConnectingToInternet == true) {
                            apiHit()

                        } else connectionDetector?.showNoInternetDialog()
                    }
                } else
                    validate_values()
            }
            R.id.iv_back -> activity?.finish()
            R.id.tvText -> {
                val intent = Intent(activity, LoginActivityNew::class.java)
                startActivity(intent)
                requireActivity().finish()
            }
            R.id.tv_vendor_regis -> {
                if (BuildConfig.CLIENT_CODE == "bodyformula_0497" && !clientData?.supplier_domain.isNullOrEmpty()) {
                    Utils.openWebView(activity
                            ?: requireContext(), clientData?.supplier_domain.plus(ADMIN_SUPPLIER_DETAIL))
                } else if (!clientData?.admin_domain.isNullOrEmpty()) {
                    Utils.openWebView(activity
                            ?: requireContext(), clientData?.admin_domain.plus(ADMIN_SUPPLIER_DETAIL))
                }
            }

            R.id.tvFacebook -> if (isInternetConnected(activity)) {
                (activity as SignupActivity?)?.performLogin()
            } else {
                showNoInternetDialog(activity)
            }
        }
    }

    private fun validate_values() {
        if (etEmail.text.toString().trim() == "") {
            tvv.requestFocus()
            tvv.error = getString(R.string.empty_email)
        } else if (!GeneralFunctions.isValidEmail(etEmail.text.toString().trim())) {
            tvv.requestFocus()
            tvv.error = getString(R.string.invalid_email)
        } else if (checkOthersValidations()) {
            clickatTextInputLayout.error = null
            if (connectionDetector?.isConnectingToInternet == true) {
                apiHit()
            } else connectionDetector?.showNoInternetDialog()
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
            !checkOthersValidations() -> {
                false
            }
            else -> {
                clickatTextInputLayout.error = null
                tvv.error = null
                true
            }

        }
    }

    private fun checkOthersValidations(): Boolean {
        return when {
            etPassword.text.toString().trim() == "" -> {
                tvv.error = null
                clickatTextInputLayout.requestFocus()
                clickatTextInputLayout.error = getString(R.string.empty_pwd)
                false
            }
            etPassword.text.toString().trim().length < 6 -> {
                tvv.error = null
                clickatTextInputLayout.requestFocus()
                clickatTextInputLayout.error = getString(R.string.passwrd_lenght)
                false
            }
            checkBoxTerms?.isChecked == false && settingBean?.auth_terms_check == "1" -> {
                tvv.error = null
                clickatTextInputLayout.error = null
                AppToasty.error(requireContext(), getString(R.string.plese_accept_terms_and_conditions))
                false
            }
            else -> true
        }
    }

    private fun checkEmailValidations(): Boolean {
        return when {
            !GeneralFunctions.isValidEmail(etEmail.text.toString().trim()) -> {
                tvv.requestFocus()
                tvv.error = getString(R.string.invalid_email)
                false
            }
            !checkOthersValidations() -> {
                false
            }
            else -> {
                clickatTextInputLayout.error = null
                tvv.error = null
                true
            }
        }
    }

    private fun apiHit() {
        val barDialog = ProgressBarDialog(activity)
        barDialog.show()
        val hashMap=HashMap<String,String>()
        hashMap["deviceToken"]= Prefs.with(activity).getString(DataNames.REGISTRATION_ID, "")
        hashMap["deviceType"]="0"
        hashMap["areaId"]= "0"
        hashMap["password"]= etPassword.text.toString().trim { it <= ' ' }
        hashMap["languageId"]= getLanguage(activity).toString()

        if(settingBean?.enable_signup_phone_only=="1" &&  etEmail.text?.isDigitsOnly()==true){
            hashMap["mobileNumber"]= etEmail.text.toString().trim()
            hashMap["countryCode"]= etCountryCode?.selectedCountryCodeWithPlus.toString()
        }else
            hashMap["email"]= etEmail.text.toString().trim()

        if(!etReferralCode.text.isNullOrEmpty())
            hashMap["referralCode"]= etReferralCode.text.toString().trim()

        val user = Prefs.with(activity).getObject(PrefenceConstants.ADRS_DATA, AddressBean::class.java)
        val call = RestClient.getModalApiService(activity).signup_step_first(hashMap)
        call.enqueue(object : Callback<PojoSignUp?> {
            override fun onResponse(call: Call<PojoSignUp?>, response: Response<PojoSignUp?>) {
                barDialog.dismiss()
                if (response.code() == 200) {
                    val pojoSignUp = response.body()
                    if (pojoSignUp?.status == ClikatConstants.STATUS_SUCCESS) {
                        pojoSignUp.data?.otp_verified = 0
                        Prefs.with(activity).save(DataNames.USER_DATA, pojoSignUp)
                        Prefs.with(activity).save(PrefenceConstants.USER_TEMP_PASSWORD, etPassword.text.toString().trim())

                        if(settingBean?.enable_signup_phone_only=="1" && etEmail.text?.isDigitsOnly()==true){
                            if (settingBean?.bypass_otp == "1") {
                                verifyOtp()
                            } else {
                                findNavController().navigate(SignupFragment1Directions.actionSignupFragment1ToSignupFragment3(pojoSignUp.data.otp
                                        ?: 0, etCountryCode.selectedCountryCodeWithPlus + " " + etEmail.text.toString().trim(), "0"))
                            }
                        }else
                            findNavController().navigate(SignupFragment1Directions.actionSignupFragment1ToSignupFragment2())
//                        navController(this@SignupFragment1).navigate(R.id.signupFragment2,null)

                        // GeneralFunctions.addFragment(fragmentManager, SignupFragment2(), null, R.id.flContainer)
                    } else {
                        GeneralFunctions.showSnackBar(view, pojoSignUp?.message, activity)
                    }
                }
            }

            override fun onFailure(call: Call<PojoSignUp?>, t: Throwable) {
                barDialog.dismiss()
                GeneralFunctions.showSnackBar(view, t.message, activity)
            }
        })
    }
    private fun verifyOtp() {
        val barDialog = ProgressBarDialog(activity)
        barDialog.show()
        val pojoSignUp = Prefs.with(activity).getObject(DataNames.USER_DATA, PojoSignUp::class.java)
        val call = RestClient.getModalApiService(activity).verify_otp(pojoSignUp.data.access_token, "12345", getLanguage(activity))
        call.enqueue(object : Callback<PojoSignUp?> {
            override fun onResponse(call: Call<PojoSignUp?>, response: Response<PojoSignUp?>) {
                barDialog.dismiss()
                if (response.code() == 200) {
                    val pojoSign = response.body()
                    if (pojoSign?.status == ClikatConstants.STATUS_SUCCESS) { /*         if (getArguments().getString(DataNames.PHONE_VERIFIED, "1").equals("1")) {
                            AdjustEvent event = new AdjustEvent("lqdpiu");
                            Adjust.trackEvent(event);
                            pojoSignUp.data.otp_verified = 1;
                            Prefs.with(getActivity()).save(DataNames.USER_DATA, pojoSignUp);
                            getActivity().finish();
                        } else {*/
                        pojoSignUp.data.otp_verified = 1
                        Prefs.with(activity).save(DataNames.USER_DATA, pojoSignUp)
                        findNavController().navigate(R.id.action_signupFragment1_to_signupFragment4)
                        // GeneralFunctions.addFragment(fragmentManager, SignupFragment4(), null, R.id.flContainer)
                        //  }
                    } else if (pojoSign?.status == ClikatConstants.STATUS_INVALID_TOKEN) {
                        // connectionDetector?.loginExpiredDialog()
                    } else {
                        GeneralFunctions.showSnackBar(view, pojoSign?.message, activity)
                    }
                }
            }

            override fun onFailure(call: Call<PojoSignUp?>, t: Throwable) {
                barDialog.dismiss()
                GeneralFunctions.showSnackBar(view, t.message, activity)
            }
        })
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        googleHelper.activityResult(requestCode, resultCode, data) {
            if (it != null) {
                googleHelper.logoutGoogle {

                }
                linear_bg.onSnackbar(it)
            }
        }
    }

    private fun googleLogin(param: GoogleLoginInput) {
        val barDialog = ProgressBarDialog(activity)
        barDialog.show()
        val call = RestClient.getModalApiService(activity).googleLogin(param)
        call.enqueue(object : Callback<PojoSignUp?> {
            override fun onResponse(call: Call<PojoSignUp?>, response: Response<PojoSignUp?>) {
                barDialog.dismiss()
                if (response.code() == 200) {
                    val pojoSignUp = response.body()
                    if (pojoSignUp!!.status == ClikatConstants.STATUS_SUCCESS) {
                        prefHelper.setkeyValue(PrefenceConstants.USER_LOGGED_IN, true)
                        prefHelper.setkeyValue(PrefenceConstants.ACCESS_TOKEN, pojoSignUp.data.access_token
                                ?: "")
                        prefHelper.setkeyValue(PrefenceConstants.USER_ID, pojoSignUp.data.id.toString())

                        prefHelper.addGsonValue(DataNames.USER_DATA, Gson().toJson(pojoSignUp))
                        if (pojoSignUp.data.otp_verified == 1 && pojoSignUp.data.firstname?.isNotEmpty() == true) {

                            pojoSignUp.data?.user_created_id?.let {
                                prefHelper.setkeyValue(PrefenceConstants.USER_CHAT_ID, it)
                            }

                            pojoSignUp.data?.customer_payment_id?.let {
                                prefHelper.setkeyValue(PrefenceConstants.CUSTOMER_PAYMENT_ID, it)
                            }


                            pojoSignUp.data?.referral_id?.let {
                                prefHelper.setkeyValue(PrefenceConstants.USER_REFERRAL_ID, it)
                            }

                            prefHelper.getGsonValue(DataNames.SETTING_DATA, SettingModel.DataBean.SettingData::class.java)

                            val settingBean = prefHelper.getGsonValue(DataNames.SETTING_DATA, SettingModel.DataBean.SettingData::class.java)
                            if (settingBean?.login_template == null) {
                                activity?.setResult(Activity.RESULT_OK)
                            } else {
                                startActivity(Intent(activity, MainScreenActivity::class.java))
                            }
                            activity?.finish()
                        } else {
                            if (pojoSignUp.data?.otp_verified == 0) {
                                val bundle = Bundle()
                                bundle.putString(DataNames.PHONE_VERIFIED, "1")
                                navController(this@SignupFragment1).navigate(R.id.signupFragment2, bundle)
                            } else if (pojoSignUp.data?.firstname.isNullOrEmpty()) {
                                navController(this@SignupFragment1).navigate(R.id.signupFragment4)
                            }
                        }
                    } else {
                        GeneralFunctions.showSnackBar(linear_bg, pojoSignUp.message, activity)
                    }
                }
            }

            override fun onFailure(call: Call<PojoSignUp?>, t: Throwable) {
                barDialog.dismiss()
                GeneralFunctions.showSnackBar(view, t.message, activity)
            }
        })
    }


}