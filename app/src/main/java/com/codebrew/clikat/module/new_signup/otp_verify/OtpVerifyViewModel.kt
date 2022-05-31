package com.codebrew.clikat.module.new_signup.otp_verify

import com.codebrew.clikat.base.BaseViewModel
import com.codebrew.clikat.data.DataManager
import com.codebrew.clikat.data.constants.NetworkConstants
import com.codebrew.clikat.data.constants.PrefenceConstants
import com.codebrew.clikat.modal.ExampleCommon
import com.codebrew.clikat.modal.PojoSignUp
import com.codebrew.clikat.preferences.DataNames
import com.google.gson.Gson
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

class OtpVerifyViewModel(dataManager: DataManager) : BaseViewModel<OtpNavigator>(dataManager) {

    fun validateOtp(hashMap: HashMap<String, String>) {
        setIsLoading(true)
        dataManager.verifyOtpNew(hashMap)
                ?.observeOn(AndroidSchedulers.mainThread())
                ?.subscribeOn(Schedulers.io())
                ?.subscribe({ this.codeResponse(it) }, { this.handleError(it) })?.let {
                    compositeDisposable.add(it)
                }
    }


    fun resendOtp(accessToken: String) {
        setIsLoading(true)
        dataManager.resendotp(accessToken)
                ?.observeOn(AndroidSchedulers.mainThread())
                ?.subscribeOn(Schedulers.io())
                ?.subscribe({ this.resendResponse(it) }, { this.handleError(it) })?.let {
                    compositeDisposable.add(it)
                }
    }

    private fun resendResponse(it: ExampleCommon?) {
        setIsLoading(false)
        when (it?.status) {
            NetworkConstants.SUCCESS -> {
                navigator.onResendOtp(it.message)
            }
            NetworkConstants.AUTHFAILED -> navigator.onSessionExpire()
            else -> navigator.onErrorOccur(it?.message ?: "")
        }
    }


    private fun codeResponse(it: PojoSignUp?) {
        setIsLoading(false)
        when (it?.status) {
            NetworkConstants.SUCCESS -> {
                it.data.otp_verified = 1

                dataManager.setkeyValue(PrefenceConstants.IsVerified, it.data.otp_verified?:0)

                dataManager.addGsonValue(DataNames.USER_DATA, Gson().toJson(it))

                it.data.user_created_id?.let {
                    dataManager.setkeyValue(PrefenceConstants.USER_CHAT_ID, it)
                }

                it.data.referral_id?.let {
                    dataManager.setkeyValue(PrefenceConstants.USER_REFERRAL_ID, it)
                }

                it.data.customer_payment_id?.let {
                    dataManager.setkeyValue(PrefenceConstants.CUSTOMER_PAYMENT_ID, it)
                }

                dataManager.setkeyValue(PrefenceConstants.ACCESS_TOKEN, it.data.access_token?:"")
                dataManager.setkeyValue(PrefenceConstants.USER_ID, it.data.id.toString())


                navigator.onOtpVerify()

            }
            NetworkConstants.AUTHFAILED -> navigator.onSessionExpire()
            else -> navigator.onErrorOccur(it?.message ?: "")
        }
    }

    fun validateRegisterByPhoneOtp(hashMap: HashMap<String, String>) {
        setIsLoading(true)
        dataManager.registerByPhoneOtpVerify(hashMap)
                ?.observeOn(AndroidSchedulers.mainThread())
                ?.subscribeOn(Schedulers.io())
                ?.subscribe({ this.codeResponse(it) }, { this.handleError(it) })?.let {
                    compositeDisposable.add(it)
                }
    }


    fun resendRegisterByPhoneOtp(userCreatedId: String) {
        setIsLoading(true)
        dataManager.resendOtpRegisterByPhone(userCreatedId)
                ?.observeOn(AndroidSchedulers.mainThread())
                ?.subscribeOn(Schedulers.io())
                ?.subscribe({ this.resendRegisterByPhoneResponse(it) }, { this.handleError(it) })?.let {
                    compositeDisposable.add(it)
                }
    }

    private fun resendRegisterByPhoneResponse(it: ExampleCommon?) {
        setIsLoading(false)
        when (it?.status) {
            NetworkConstants.SUCCESS -> {
                navigator.onResendOtp(it.message)
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
