package com.codebrew.clikat.module.login

import com.codebrew.clikat.base.BaseViewModel
import com.codebrew.clikat.data.DataManager
import com.codebrew.clikat.data.constants.NetworkConstants
import com.codebrew.clikat.data.constants.PrefenceConstants
import com.codebrew.clikat.data.model.others.GoogleLoginInput
import com.codebrew.clikat.modal.ExampleCommon
import com.codebrew.clikat.modal.PojoSignUp
import com.codebrew.clikat.preferences.DataNames
import com.google.gson.Gson
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

class LoginViewModel(dataManager: DataManager) : BaseViewModel<LoginNavigator>(dataManager) {

    fun validateLogin(hashMap: HashMap<String, String>) {
        setIsLoading(true)
        dataManager.login(hashMap)
                ?.observeOn(AndroidSchedulers.mainThread())
                ?.subscribeOn(Schedulers.io())
                ?.subscribe({ this.codeResponse(it) }, { this.handleError(it) })?.let {
                    compositeDisposable.add(it)
                }
    }
    fun apiRegisterByPhone(hashMap: HashMap<String, String>) {
        setIsLoading(true)
        dataManager.apiRegisterByPhone(hashMap)
                ?.observeOn(AndroidSchedulers.mainThread())
                ?.subscribeOn(Schedulers.io())
                ?.subscribe({ this.registerByPhoneResponse(it) }, { this.handleError(it) })?.let {
                    compositeDisposable.add(it)
                }
    }

    fun validateForgotPswr(emailId: String) {
        setIsLoading(true)

        dataManager.forgotPassword(emailId)
                ?.observeOn(AndroidSchedulers.mainThread())
                ?.subscribeOn(Schedulers.io())
                ?.subscribe({ this.forgotResponse(it) }, { this.handleError(it) })?.let {
                    compositeDisposable.add(it)
                }
    }


    fun validateFb(hashMap: HashMap<String, String?>) {
        setIsLoading(true)

        dataManager.fbLogin(hashMap)
                ?.observeOn(AndroidSchedulers.mainThread())
                ?.subscribeOn(Schedulers.io())
                ?.subscribe({ this.fbResponse(it) }, { this.handleError(it) })?.let {
                    compositeDisposable.add(it)
                }
    }

    private fun fbResponse(it: PojoSignUp?) {
        setIsLoading(false)
        when (it?.status) {
            NetworkConstants.SUCCESS -> {

                // dataManager.addGsonValue(DataNames.USER_DATA,Gson().toJson(it))

                // dataManager.setkeyValue(PrefenceConstants.USER_LOGGED_IN,true)
                //  dataManager.setkeyValue(PrefenceConstants.ACCESS_TOKEN,it.data.access_token)
                //dataManager.setkeyValue(PrefenceConstants.USER_ID,it.data.id.toString())
                navigator.onSocialLogin(it,"facebook")
            }
            NetworkConstants.AUTHFAILED -> navigator.onSessionExpire()
            else -> navigator.onErrorOccur(it?.message ?: "")
        }
    }


    private fun forgotResponse(it: ExampleCommon?) {
        setIsLoading(false)
        when (it?.status) {
            NetworkConstants.SUCCESS -> {
                navigator.onForgotPswr(it.message)
            }
            NetworkConstants.AUTHFAILED -> navigator.onSessionExpire()
            else -> navigator.onErrorOccur(it?.message ?: "")
        }
    }

    private fun registerByPhoneResponse(it: PojoSignUp?) {
        setIsLoading(false)
        when (it?.status) {
            NetworkConstants.SUCCESS -> {
                it.data.user_created_id?.let {
                    dataManager.setkeyValue(PrefenceConstants.USER_CHAT_ID, it)
                }
                navigator.registerByPhone()
            }
            NetworkConstants.AUTHFAILED -> navigator.onSessionExpire()
            else -> navigator.onErrorOccur(it?.message ?: "")
        }
    }
    private fun codeResponse(it: PojoSignUp?) {
        setIsLoading(false)
        when (it?.status) {
            NetworkConstants.SUCCESS -> {

                // val signupDta=it.data
                val isVerified = it.data.otp_verified

                dataManager.setkeyValue(PrefenceConstants.IsVerified, it.data.otp_verified?:0)

                dataManager.addGsonValue(DataNames.USER_DATA, Gson().toJson(it))

                dataManager.setkeyValue(PrefenceConstants.USER_LOGGED_IN, true)

                it.data.user_created_id?.let {
                    dataManager.setkeyValue(PrefenceConstants.USER_CHAT_ID, it)
                }

                it.data.referral_id?.let {
                    dataManager.setkeyValue(PrefenceConstants.USER_REFERRAL_ID, it)
                }

                it.data.id_for_invoice?.let {
                    dataManager.setkeyValue(PrefenceConstants.ID_FOR_INVOICE,it)
                }

                it.data.customer_payment_id?.let {
                    dataManager.setkeyValue(PrefenceConstants.CUSTOMER_PAYMENT_ID, it)
                }

                dataManager.setkeyValue(PrefenceConstants.ACCESS_TOKEN, it.data.access_token?:"")
                dataManager.setkeyValue(PrefenceConstants.USER_ID, it.data.id.toString())

                if (isVerified == 0 || it.data.firstname?.isEmpty()==true) {
                    navigator.userNotVerified(it.data)

                } else {
                    navigator.onLogin(it.data)
                }
            }
            NetworkConstants.AUTHFAILED -> navigator.onSessionExpire()
            else -> navigator.onErrorOccur(it?.message ?: "")
        }
    }

    fun validateGoogle(param: GoogleLoginInput) {
        setIsLoading(true)

        dataManager.googleLogin(param)
                ?.observeOn(AndroidSchedulers.mainThread())
                ?.subscribeOn(Schedulers.io())
                ?.subscribe({ this.googleResponse(it) }, { this.handleError(it) })?.let {
                    compositeDisposable.add(it)
                }
    }

    private fun googleResponse(it: PojoSignUp?) {
        setIsLoading(false)
        when (it?.status) {
            NetworkConstants.SUCCESS -> {

                navigator.onSocialLogin(it,"google")
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
