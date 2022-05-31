package com.codebrew.clikat.module.new_signup.otp_verify

import android.app.Activity
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.view.View
import android.widget.EditText
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.clevertap.android.sdk.CleverTapAPI
import com.codebrew.clikat.BR
import com.codebrew.clikat.R
import com.codebrew.clikat.app_utils.AppUtils
import com.codebrew.clikat.app_utils.extension.launchActivity
import com.codebrew.clikat.app_utils.extension.onSnackbar
import com.codebrew.clikat.app_utils.navController
import com.codebrew.clikat.base.BaseFragment
import com.codebrew.clikat.data.constants.PrefenceConstants
import com.codebrew.clikat.data.preferences.PreferenceHelper
import com.codebrew.clikat.databinding.OtpVerifyFragmentBinding
import com.codebrew.clikat.di.ViewModelProviderFactory
import com.codebrew.clikat.modal.PojoSignUp
import com.codebrew.clikat.modal.other.SettingModel
import com.codebrew.clikat.module.bottom_navigation.MainScreenActivity
import com.codebrew.clikat.preferences.DataNames
import com.codebrew.clikat.utils.configurations.Configurations
import com.mobsandgeeks.saripaar.ValidationError
import com.mobsandgeeks.saripaar.Validator
import com.mobsandgeeks.saripaar.annotation.NotEmpty
import com.mobsandgeeks.saripaar.annotation.Order
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import kotlinx.android.synthetic.main.otp_verify_fragment.*
import java.util.*
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class OtpVerifyFragment : BaseFragment<OtpVerifyFragmentBinding, OtpVerifyViewModel>(), OtpNavigator, Validator.ValidationListener {

    companion object {
        fun newInstance() = OtpVerifyFragment()
    }

    var cleverTapDefaultInstance: CleverTapAPI? = null
    private var settingBean: SettingModel.DataBean.SettingData? = null
    val args: OtpVerifyFragmentArgs by navArgs()

    @Inject
    lateinit var factory: ViewModelProviderFactory

    @Inject
    lateinit var prefHelper: PreferenceHelper

    @Inject
    lateinit var appUtils: AppUtils

    // Validationbutton_login
    private val validator = Validator(this)

    private lateinit var viewModel: OtpVerifyViewModel

    private var mBinding: OtpVerifyFragmentBinding? = null

    @NotEmpty(message = "Please enter Otp")
    @Order(1)
    private lateinit var otpEditText: EditText

    val compositeDisposable: CompositeDisposable = CompositeDisposable()

    var edit = emptyArray<EditText>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        cleverTapDefaultInstance = CleverTapAPI.getDefaultInstance(requireActivity())
        mBinding = viewDataBinding
        mBinding?.color = Configurations.colors
        mBinding?.strings = appUtils.loadAppConfig(0).strings
        Glide.with(this).asGif().load(R.raw.loading_gif).into(mBinding!!.imageLoader);

        viewModel.navigator = this
        settingBean = prefHelper.getGsonValue(DataNames.SETTING_DATA, SettingModel.DataBean.SettingData::class.java)
        mBinding?.settingData = settingBean
        // Validator
        validator.validationMode = Validator.Mode.IMMEDIATE
        validator.setValidationListener(this)

        otpEditText = view.findViewById(R.id.edOtp)

        compositeDisposable.add(disposable)



        btn_verify.setOnClickListener {
            validator.validate()
        }

        ivCancelCrave.setOnClickListener { activity?.onBackPressed() }

        tvResend.setOnClickListener {
            if (isNetworkConnected) {
                if (settingBean?.phone_registration_flag != null && settingBean?.phone_registration_flag == "1") {
                    val userCreatedId = prefHelper.getKeyValue(PrefenceConstants.USER_CHAT_ID, PrefenceConstants.TYPE_STRING)?.toString()
                            ?: ""
                    viewModel.resendRegisterByPhoneOtp(userCreatedId)
                } else {
                    viewModel.resendOtp(args.accessToken ?: "")

                }
            }
        }

        back.setOnClickListener {
            activity?.onBackPressed()
        }

        if (settingBean?.phone_registration_flag == "1")
            tvResend.isEnabled = true

        edit = arrayOf<EditText>(etOtp1, etOtp2, etOtp3, etOtp4)

        etOtp1.addTextChangedListener(GenericTextWatcher(etOtp1, edit))
        etOtp2.addTextChangedListener(GenericTextWatcher(etOtp2, edit))
        etOtp3.addTextChangedListener(GenericTextWatcher(etOtp3, edit))
        etOtp4.addTextChangedListener(GenericTextWatcher(etOtp4, edit))
    }

    override fun onDestroyView() {
        super.onDestroyView()

        compositeDisposable.dispose()
    }

    override fun getBindingVariable(): Int {
        return BR.viewModel
    }

    override fun getLayoutId(): Int {
        return R.layout.otp_verify_fragment
    }

    override fun getViewModel(): OtpVerifyViewModel {
        viewModel = ViewModelProviders.of(this, factory).get(OtpVerifyViewModel::class.java)
        return viewModel
    }

    override fun onOtpVerify() {
        val userInfo = prefHelper.getGsonValue(DataNames.USER_DATA, PojoSignUp::class.java)
        if (settingBean?.phone_registration_flag == "1" && userInfo?.data?.firstname?.isEmpty() == true) {
            navController(this).navigate(R.id.action_otpVerifyFragment_to_updateNameFrag)
        } else {
            appUtils.triggerEvent("login")
            prefHelper.setkeyValue(PrefenceConstants.USER_LOGGED_IN, true)

            val isGuest = settingBean?.login_template == "0"

            if (isGuest) {
                activity?.setResult(Activity.RESULT_OK)
            } else {
                val profileUpdate = HashMap<String, Any>()
                profileUpdate["Name"] = userInfo?.data?.email.toString() // String

                profileUpdate["Identity"] = userInfo?.data?.email.toString()// String or number

                profileUpdate["Email"] = userInfo?.data?.email.toString() // Email address of the user

                profileUpdate["Phone"] = userInfo?.data?.mobile_no.toString() // Phone (with the country code, starting with +)

                profileUpdate["Gender"] = "" // Can be either M or F

                profileUpdate["DOB"] = Date() // Date of Birth. Set the Date object to the appropriate value first

                cleverTapDefaultInstance?.onUserLogin(profileUpdate)
                activity?.launchActivity<MainScreenActivity>()
            }
        }

    }

    override fun onResendOtp(message: String) {
        tvResend.isEnabled = false
        compositeDisposable.add(disposable)
        mBinding?.root?.onSnackbar(message)
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

            val otp:StringBuilder=StringBuilder()
            edit.forEachIndexed { index, editText ->
                otp.append(editText.text.toString())
            }

            if(otp.trim().length<4)
            {
                mBinding?.root?.onSnackbar("Enter Valid Otp")
            }else
            {
                val hashMap = hashMapOf("otp" to otp.trim().toString(),
                        "languageId" to prefHelper.getLangCode())

                if (settingBean?.phone_registration_flag != null && settingBean?.phone_registration_flag == "1") {
                    hashMap["userCreatedId"] = prefHelper.getKeyValue(PrefenceConstants.USER_CHAT_ID, PrefenceConstants.TYPE_STRING)?.toString()
                            ?: ""
                    viewModel.validateRegisterByPhoneOtp(hashMap)
                } else {
                    hashMap["accessToken"] = args.accessToken.toString()
                    viewModel.validateOtp(hashMap)
                }
            }
        }


    }

    val disposable =
            Observable.interval(0, 1, TimeUnit.MINUTES)
                    .flatMap {
                        return@flatMap Observable.create<String> { emitter ->
                            // Log.d("IntervalExample", "Create")
                            //  emitter.onNext("MindOrks")
                            emitter.onComplete()
                        }
                    }
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe {
                        // Log.d("IntervalExample", it)
                        tvResend.isEnabled = true
                    }

}
