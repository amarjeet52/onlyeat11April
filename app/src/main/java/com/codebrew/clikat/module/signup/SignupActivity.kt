package com.codebrew.clikat.module.signup

import android.app.Activity
import android.content.ComponentCallbacks2
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import com.codebrew.clikat.R
import com.codebrew.clikat.activities.FacebookLoginActivity
import com.codebrew.clikat.app_utils.DialogsUtil
import com.codebrew.clikat.data.constants.PrefenceConstants
import com.codebrew.clikat.data.model.api.AddressBean
import com.codebrew.clikat.data.preferences.PreferenceHelper
import com.codebrew.clikat.databinding.DialougeForgotPassBinding
import com.codebrew.clikat.modal.Data1
import com.codebrew.clikat.modal.PojoSignUp
import com.codebrew.clikat.modal.other.SettingModel.DataBean.SettingData
import com.codebrew.clikat.module.bottom_navigation.MainScreenActivity
import com.codebrew.clikat.preferences.DataNames
import com.codebrew.clikat.preferences.Prefs
import com.codebrew.clikat.retrofit.RestClient
import com.codebrew.clikat.utils.ClikatConstants
import com.codebrew.clikat.utils.GeneralFunctions
import com.codebrew.clikat.utils.ProgressBarDialog
import com.codebrew.clikat.utils.StaticFunction
import com.codebrew.clikat.utils.configurations.Configurations
import com.facebook.FacebookException
import com.facebook.FacebookSdk
import com.facebook.GraphResponse
import com.facebook.login.LoginResult
import com.google.android.gms.tasks.Task
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.messaging.FirebaseMessaging
import dagger.android.AndroidInjection
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.HasAndroidInjector
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import javax.inject.Inject

/*
 * Created by cbl80 on 25/4/16.
 */
class SignupActivity : FacebookLoginActivity(), HasAndroidInjector {
    private var settingsData: SettingData? = null

    @Inject
    lateinit var prefHelper: PreferenceHelper

    @Inject
    lateinit var androidInjector: DispatchingAndroidInjector<Any>

    private var fbJson: JSONObject? = null

    @Inject
    lateinit var dialogsUtil: DialogsUtil

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.layout_frame)

        val statusColor = Color.parseColor(Configurations.colors.appBackground)
        StaticFunction.setStatusBarColor(this, getColor(R.color.dark_blue))

        setlanguage()
        initialise()

        if (intent.hasExtra("userData")) {
            val userData = intent.getParcelableExtra<Data1>("userData")

            if (userData?.otp_verified == 0) {
                findNavController(R.id.flContainer).navigate(R.id.signupFragment2)
            } else if (userData?.firstname.isNullOrEmpty()) {
                findNavController(R.id.flContainer).navigate(R.id.signupFragment4)
            }
        }
        else if (intent.getStringExtra(DataNames.PHONE_VERIFIED) == "1") {
            // val signupFragment2 = SignupFragment2()
            val bundle = Bundle()
            bundle.putString(DataNames.PHONE_VERIFIED, "1")
            //signupFragment2.arguments = bundle
            startDestinationToFragment2(bundle)
            //findNavController().navigate(SignupFragment2Directions.actionSignupFragment2ToSignupFragment3())
            // GeneralFunctions.replaceFragment(getSupportFragmentManager(), signupFragment2, null, R.id.flContainer);
        } else  // GeneralFunctions.replaceFragment(getSupportFragmentManager(), new SignupFragment1(), null, R.id.flContainer);
        {
            startDestinationToFragment1()
        }

        updateToken()

        printHashKey(this)
    }

    private fun initialise() {
        settingsData = prefHelper.getGsonValue(DataNames.SETTING_DATA, SettingData::class.java)
    }


/*        if (intent.getStringExtra(DataNames.PHONE_VERIFIED) == "1") {
            val signupFragment2 = SignupFragment2()
            val bundle = Bundle()
            bundle.putString(DataNames.PHONE_VERIFIED, "1")
            signupFragment2.arguments = bundle
            //findNavController().navigate(SignupFragment2Directions.actionSignupFragment2ToSignupFragment3())
            GeneralFunctions.replaceFragment(supportFragmentManager, signupFragment2, null, R.id.flContainer)
        } else GeneralFunctions.replaceFragment(supportFragmentManager, SignupFragment1(), null, R.id.flContainer)*/
/*        updateToken()
        printHashKey(this)
    }*/

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

    override fun getLayoutId(): Int {
        return R.layout.layout_frame
    }

    override fun loginSuccess(result: LoginResult) {
        getUserProfile()
    }

    override fun onStop() {
        super.onStop()
        onTrimMemory(ComponentCallbacks2.TRIM_MEMORY_UI_HIDDEN)
    }

    override fun OnSuccess(fbObject: JSONObject, response: GraphResponse) {
        try {

            fbJson = fbObject

            if (fbObject.has("email")) {
                fbloginapi(fbObject)
            } else {
                enterEmail()
            }
        } catch (e: JSONException) {
            e.printStackTrace()
        }
    }

    override fun loginCancelled() {}
    override fun loginError(exception: FacebookException) {
    }

    private fun setlanguage() {

        val selectedLang = prefHelper.getKeyValue(DataNames.SELECTED_LANGUAGE, PrefenceConstants.TYPE_STRING).toString()

        if (selectedLang == "arabic" || selectedLang == "ar") {
            GeneralFunctions.force_layout_to_RTL(this)
        } else {
            GeneralFunctions.force_layout_to_LTR(this)
        }
    }

    override fun onBackPressed() {
        if (supportFragmentManager.backStackEntryCount > 1) {
            supportFragmentManager.popBackStackImmediate()
        } else {
            finish()
        }
    }

    @Throws(JSONException::class)
    private fun fbloginapi(`object`: JSONObject?) {
        val locationUser = Prefs.with(this@SignupActivity).getObject(PrefenceConstants.ADRS_DATA, AddressBean::class.java)
        val regID = Prefs.with(this).getString(DataNames.REGISTRATION_ID, "") ?: ""
        val fbId = `object`?.getString("id") ?: ""
        val hashMap = HashMap<String, String>()
        hashMap["facebookToken"] = `object`?.getString("id") ?: ""
        hashMap["name"] = `object`?.getString("name") ?: ""

        hashMap["email"] = `object`?.getString("email") ?: ""

        hashMap["image"] = "http://graph.facebook.com/$fbId/picture?type=large"
        hashMap["deviceToken"] = "" + regID
        //  hashMap.put("areaId", "" + locationUser.getAreaID());
        hashMap["deviceType"] = "" + 0
        val barDialog = ProgressBarDialog(this)
        barDialog.show()
        val fbLogin = RestClient.getModalApiService(this).fbLogin(hashMap)
        fbLogin.enqueue(object : Callback<PojoSignUp?> {
            override fun onResponse(call: Call<PojoSignUp?>, response: Response<PojoSignUp?>) {
                barDialog.dismiss()
                if (response.code() == 200) {
                    val pojoSignUp = response.body()
                    if (pojoSignUp!!.status == ClikatConstants.STATUS_SUCCESS) {
                        pojoSignUp.data.fbId = fbId
                        prefHelper.setkeyValue(PrefenceConstants.USER_LOGGED_IN, true)
                        prefHelper.setkeyValue(PrefenceConstants.ACCESS_TOKEN, pojoSignUp.data.access_token
                                ?: "")
                        prefHelper.setkeyValue(PrefenceConstants.USER_ID, pojoSignUp.data.id.toString())
                        Prefs.with(this@SignupActivity).save(DataNames.USER_DATA, pojoSignUp)
                        if (pojoSignUp.data.otp_verified == 1) {

                            pojoSignUp.data?.user_created_id?.let {
                                prefHelper.setkeyValue(PrefenceConstants.USER_CHAT_ID, it)
                            }

                            pojoSignUp.data?.customer_payment_id?.let {
                                prefHelper.setkeyValue(PrefenceConstants.CUSTOMER_PAYMENT_ID, it)
                            }


                            pojoSignUp.data?.referral_id?.let {
                                prefHelper.setkeyValue(PrefenceConstants.USER_REFERRAL_ID, it)
                            }

                            val settingBean = Prefs.with(this@SignupActivity).getObject(DataNames.SETTING_DATA, SettingData::class.java)
                            if (settingBean.login_template == null) {
                                setResult(Activity.RESULT_OK)
                            } else {
                                startActivity(Intent(this@SignupActivity, MainScreenActivity::class.java))
                            }
                            finish()
                        } else {
                            val bundle = Bundle()
                            bundle.putString(DataNames.PHONE_VERIFIED, "1")
                            startDestinationToFragment2(bundle)
//                            startActivity(Intent(this@SignupActivity, SignupActivity::class.java)
//                                    .putExtra(DataNames.PHONE_VERIFIED, "1").addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP))
                        }
                    } else {
                        GeneralFunctions.showSnackBar(currentFocus, pojoSignUp.message, this@SignupActivity)
                    }
                }
            }

            override fun onFailure(call: Call<PojoSignUp?>, t: Throwable) {
                barDialog.dismiss()
            }
        })
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
                fbJson?.putOpt("email", edEmail.text.toString().trim())
                fbloginapi(fbJson)
            }

        }

    }

    private fun startDestinationToFragment2(bundle: Bundle) {
        val navHostFragment = (supportFragmentManager.findFragmentById(R.id.flContainer) as NavHostFragment?)!!
        val inflater = navHostFragment.navController.navInflater
        val navGraph = inflater.inflate(R.navigation.signup)
        navGraph.startDestination = R.id.signupFragment2
        navHostFragment.navController.graph = navGraph

        navHostFragment.navController.setGraph(navGraph, bundle)
    }


    private fun startDestinationToFragment1() {
        val navHostFragment = (supportFragmentManager.findFragmentById(R.id.flContainer) as NavHostFragment?)!!
        val inflater = navHostFragment.navController.navInflater
        val navGraph = inflater.inflate(R.navigation.signup)
        navGraph.startDestination = R.id.signupFragment1
        navHostFragment.navController.graph = navGraph
    }

    private fun startDestinationToFragment3() {
        val navHostFragment = (supportFragmentManager.findFragmentById(R.id.flContainer) as NavHostFragment?)!!
        val inflater = navHostFragment.navController.navInflater
        val navGraph = inflater.inflate(R.navigation.signup)
        navGraph.startDestination = R.id.signupFragment3
        navHostFragment.navController.graph = navGraph
    }


    override fun androidInjector(): AndroidInjector<Any> {
        return androidInjector
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        val navHostFragment = supportFragmentManager.findFragmentById(R.id.flContainer)
        navHostFragment?.childFragmentManager?.fragments?.get(0)?.onActivityResult(requestCode, resultCode, data)
    }

}