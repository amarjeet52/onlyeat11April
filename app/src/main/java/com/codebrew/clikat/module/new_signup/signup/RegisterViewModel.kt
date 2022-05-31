package com.codebrew.clikat.module.new_signup.signup

import com.codebrew.clikat.BuildConfig
import com.codebrew.clikat.app_utils.CommonUtils
import com.codebrew.clikat.base.BaseViewModel
import com.codebrew.clikat.data.DataManager
import com.codebrew.clikat.data.constants.NetworkConstants
import com.codebrew.clikat.data.constants.PrefenceConstants
import com.codebrew.clikat.data.model.api.SuccessModel
import com.codebrew.clikat.data.model.others.PandaDocResponse
import com.codebrew.clikat.modal.PojoSignUp
import com.codebrew.clikat.preferences.DataNames
import com.google.gson.Gson
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import okhttp3.MultipartBody
import okhttp3.RequestBody

class RegisterViewModel(dataManager: DataManager) : BaseViewModel<RegisterNavigator>(dataManager) {


    fun validateSignup(partMap: HashMap<String, RequestBody>?, body: ArrayList<MultipartBody.Part>?) {
        setIsLoading(true)
        dataManager.registerUserNew(partMap, body)
                ?.observeOn(AndroidSchedulers.mainThread())
                ?.subscribeOn(Schedulers.io())
                ?.subscribe({ this.codeResponse(it) }, { this.handleError(it) })?.let {
                    compositeDisposable.add(it)
                }
    }


    private fun codeResponse(it: PojoSignUp?) {
        setIsLoading(false)
        when (it?.status) {
            NetworkConstants.SUCCESS -> {
                it.data.otp_verified = 0
if(it.message.toString().equals("Email already exist"))
{
    navigator.onErrorOccur(it?.message ?: "")
}else {

    dataManager.addGsonValue(DataNames.USER_DATA, Gson().toJson(it))
    navigator.onRegisterSuccess(it.data.access_token ?: "",it.message.toString())
}
            }
            NetworkConstants.AUTHFAILED -> navigator.onSessionExpire()
            else -> navigator.onErrorOccur(it?.message ?: "")
        }
    }

    fun validateOtp(hashMap: HashMap<String, String>) {
        setIsLoading(true)
        dataManager.verifyOtpNew(hashMap)
                ?.observeOn(AndroidSchedulers.mainThread())
                ?.subscribeOn(Schedulers.io())
                ?.subscribe({ this.otpResponse(it) }, { this.handleError(it) })?.let {
                    compositeDisposable.add(it)
                }
    }



    private fun otpResponse(it: PojoSignUp?) {
        setIsLoading(false)
        when (it?.status) {
            NetworkConstants.SUCCESS -> {
                it.data.otp_verified = 1

                dataManager.setkeyValue(PrefenceConstants.IsVerified, it.data.otp_verified ?: 0)

                dataManager.addGsonValue(DataNames.USER_DATA, Gson().toJson(it))

                dataManager.setkeyValue(PrefenceConstants.USER_LOGGED_IN, true)

                it.data.user_created_id?.let {
                    dataManager.setkeyValue(PrefenceConstants.USER_CHAT_ID, it)
                }

                it.data.referral_id?.let {
                    dataManager.setkeyValue(PrefenceConstants.USER_REFERRAL_ID, it)
                }

                it.data.customer_payment_id?.let {
                    dataManager.setkeyValue(PrefenceConstants.CUSTOMER_PAYMENT_ID, it)
                }

                dataManager.setkeyValue(PrefenceConstants.ACCESS_TOKEN, it.data.access_token ?: "")
                dataManager.setkeyValue(PrefenceConstants.USER_ID, it.data.id.toString())


                navigator.onOtpVerify()

            }
            NetworkConstants.AUTHFAILED -> navigator.onSessionExpire()
            else -> navigator.onErrorOccur(it?.message ?: "")
        }
    }


    private fun handleError(e: Throwable) {
        setIsLoading(false)

        handleErrorMsg(e).let {
            if (it == NetworkConstants.AUTH_MSG) {
                dataManager.setUserAsLoggedOut()
                navigator.onSessionExpire()
            } else {
                navigator.onErrorOccur(it)
            }
        }
    }


}
