package com.codebrew.clikat.module.signup

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.text.InputFilter
import android.os.CountDownTimer
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import androidx.navigation.fragment.findNavController
import com.codebrew.clikat.R
import com.codebrew.clikat.app_utils.AppToasty
import com.codebrew.clikat.app_utils.AppUtils
import com.codebrew.clikat.data.DataManager
import com.codebrew.clikat.data.constants.PrefenceConstants
import com.codebrew.clikat.databinding.FragmentSignup3Binding
import com.codebrew.clikat.modal.AppGlobal
import com.codebrew.clikat.modal.ExampleCommon
import com.codebrew.clikat.modal.PojoSignUp
import com.codebrew.clikat.modal.other.SettingModel
import com.codebrew.clikat.preferences.DataNames
import com.codebrew.clikat.preferences.Prefs
import com.codebrew.clikat.retrofit.RestClient
import com.codebrew.clikat.utils.ClikatConstants
import com.codebrew.clikat.utils.ConnectionDetector
import com.codebrew.clikat.utils.GeneralFunctions
import com.codebrew.clikat.utils.ProgressBarDialog
import com.codebrew.clikat.utils.StaticFunction.getLanguage
import com.codebrew.clikat.utils.StaticFunction.isInternetConnected
import com.codebrew.clikat.utils.StaticFunction.showNoInternetDialog
import com.codebrew.clikat.utils.configurations.Configurations
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import dagger.android.support.AndroidSupportInjection
import kotlinx.android.synthetic.main.fragment_signup_3.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.lang.reflect.Type
import javax.inject.Inject

/*
 * Created by cbl80 on 25/4/16.
 */
class SignupFragment3 : Fragment(), View.OnClickListener {

    var settingData: SettingModel.DataBean.SettingData? = null

    private var cd: ConnectionDetector? = null

    var accessToken = ""

    @Inject
    lateinit var appUtils: AppUtils

    var countDownTimer: CountDownTimer? = null

    val stringConfig by lazy { Configurations.strings }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AndroidSupportInjection.inject(this)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val binding: FragmentSignup3Binding = DataBindingUtil.inflate(inflater, R.layout.fragment_signup_3, container, false)
        binding.color = Configurations.colors
        binding.drawables = Configurations.drawables
        binding.strings = stringConfig
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        settingData = Prefs.with(activity).getObject(DataNames.SETTING_DATA, SettingModel.DataBean.SettingData::class.java)

        settypeface()
        clickListner()


        if (settingData?.is_skip_theme == "1")
            etOtp.filters = arrayOf(InputFilter.LengthFilter(5))

        if (appUtils.checkTwillioAuthFeature())
            etOtp.filters = arrayOf(InputFilter.LengthFilter(7))

        val bundle = arguments
        if (bundle != null) {
            if (bundle.containsKey("phone")) {
                tv_sender_phone?.text = getString(R.string.phone_tag, stringConfig.otp, bundle.getString("phone"))
            }
            if (bundle.containsKey("access_token")) {
                accessToken = bundle.getString("access_token", "")

            }
        }
        cd = ConnectionDetector(activity)

        countDownTimer = object : CountDownTimer(30000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                timer_text.visibility = View.VISIBLE
                timer_text.animateText("${getString(R.string.time_left)} ${millisUntilFinished / 1000}")
            }

            override fun onFinish() {
                if (activity == null) return
                tvText.visibility = View.VISIBLE
                timer_text.visibility = View.INVISIBLE
                tvResend.visibility = View.VISIBLE
                tvResend.isEnabled = true
                initialise()
            }
        }.start()
    }


    private fun initialise() {
        groupContact?.visibility = if (settingData?.enable_contact_us == "1") View.VISIBLE else View.GONE
    }

    override fun onDestroyView() {
        super.onDestroyView()

        countDownTimer?.cancel()
    }

    private fun clickListner() {
        tvSubmit.setOnClickListener(this)
        iv_back.setOnClickListener(this)
        tvResend.setOnClickListener(this)
        tvContactUs?.setOnClickListener(this)

    }

    private fun settypeface() {
        etOtp?.typeface = AppGlobal.regular
        tvText?.typeface = AppGlobal.regular
        tvResend?.typeface = AppGlobal.semi_bold
        tvSubmit?.typeface = AppGlobal.semi_bold
    }


    override fun onClick(view: View) {
        when (view.id) {
            R.id.tvSubmit -> validate_values()
            R.id.iv_back -> activity?.onBackPressed()
            R.id.tvResend -> if (isInternetConnected(activity)) {
                ressendotpApi()
            } else {
                showNoInternetDialog(activity)
            }
            R.id.tvContactUs -> {
                if (isInternetConnected(activity)) {
                    contactUsApi()
                } else {
                    showNoInternetDialog(activity)
                }

            }
        }
    }

    private fun ressendotpApi() {
        val barDialog = ProgressBarDialog(activity)
        barDialog.show()
        val pojoSignUp = Prefs.with(activity).getObject(DataNames.USER_DATA, PojoSignUp::class.java)
        val call = RestClient.getModalApiService(activity).resendotp(pojoSignUp?.data?.access_token)
        call.enqueue(object : Callback<ExampleCommon?> {
            override fun onResponse(call: Call<ExampleCommon?>, response: Response<ExampleCommon?>) {
                barDialog.dismiss()
                if (response.code() == 200) {
                    val pojoSignUp = response.body()
                    when {
                        pojoSignUp?.status == ClikatConstants.STATUS_SUCCESS -> {
                            countDownTimer?.start()
                            GeneralFunctions.showSnackBar(view, pojoSignUp.message, activity)
                        }
                        else -> {
                            GeneralFunctions.showSnackBar(view, pojoSignUp?.message, activity)
                        }
                    }
                }
            }

            override fun onFailure(call: Call<ExampleCommon?>, t: Throwable) {
                barDialog.dismiss()
                GeneralFunctions.showSnackBar(view, t.message, activity)
            }
        })
    }

    private fun validate_values() {
        if (etOtp?.text.toString().trim { it <= ' ' } == "") {
            inputLayout.requestFocus()
            inputLayout.error = getString(R.string.empty_otp, stringConfig.otp)
        } else {
            if (cd?.isConnectingToInternet == true) api_hit() else cd?.showNoInternetDialog()
        }
    }

    private inner class OtpReciever : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val otp = intent.getStringExtra("OtpMessage")
            etOtp?.setText(otp)
            api_hit()
        }
    }

    private fun api_hit() {
        val barDialog = ProgressBarDialog(activity)
        barDialog.show()
        val s = etOtp?.text.toString().trim { it <= ' ' }
        val pojoSignUp = Prefs.with(activity).getObject(DataNames.USER_DATA, PojoSignUp::class.java)
        val call = RestClient.getModalApiService(activity).verify_otp(pojoSignUp?.data?.access_token, s, getLanguage(activity))
        call.enqueue(object : Callback<PojoSignUp?> {
            override fun onResponse(call: Call<PojoSignUp?>, response: Response<PojoSignUp?>) {
                barDialog.dismiss()
                if (response.code() == 200) {
                    val pojoSign = response.body()
                    when (pojoSign?.status) {
                        ClikatConstants.STATUS_SUCCESS -> {
                            countDownTimer?.cancel()
                            pojoSignUp.data.otp_verified = 1
                            Prefs.with(activity).save(DataNames.USER_DATA, pojoSignUp)
                            findNavController().navigate(SignupFragment3Directions.actionSignupFragment3ToSignupFragment4())
                        }
                        else -> {
                            GeneralFunctions.showSnackBar(view, pojoSign?.message, activity)
                        }
                    }
                }
            }

            override fun onFailure(call: Call<PojoSignUp?>, t: Throwable) {
                barDialog.dismiss()
                GeneralFunctions.showSnackBar(view, t.message, activity)
            }
        })
    }

    private fun contactUsApi() {
        val barDialog = ProgressBarDialog(activity)
        barDialog.show()
        val pojoSignUp = Prefs.with(activity).getObject(DataNames.USER_DATA, PojoSignUp::class.java)
        val phoneNumber = arguments?.getString("phone")?.split(" ")

        val hashMap = HashMap<String, String>()
        hashMap["emailId"] = pojoSignUp?.data?.email ?: ""
        hashMap["countryCode"] = if (!phoneNumber.isNullOrEmpty()) phoneNumber.firstOrNull()
                ?: "" else ""
        hashMap["phoneNumber"] = if (!phoneNumber.isNullOrEmpty() && phoneNumber.size > 1) phoneNumber[1] else ""

        val call = RestClient.getModalApiService(activity).apiContactUs(hashMap)
        call.enqueue(object : Callback<PojoSignUp?> {
            override fun onResponse(call: Call<PojoSignUp?>, response: Response<PojoSignUp?>) {
                barDialog.dismiss()
                if (response.code() == 200) {
                    val pojoSign = response.body()
                    when (pojoSign?.status) {
                        ClikatConstants.STATUS_SUCCESS -> {
                            AppToasty.success(requireContext(), getString(R.string.message_sent_to_admin))
                        }
                        else -> {
                            GeneralFunctions.showSnackBar(view, pojoSign?.message, activity)
                        }
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