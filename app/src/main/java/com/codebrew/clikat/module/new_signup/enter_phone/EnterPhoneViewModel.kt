package com.codebrew.clikat.module.new_signup.enter_phone

import com.codebrew.clikat.base.BaseViewModel
import com.codebrew.clikat.data.DataManager
import com.codebrew.clikat.data.constants.NetworkConstants
import com.codebrew.clikat.data.constants.PrefenceConstants
import com.codebrew.clikat.modal.PojoSignUp
import com.codebrew.clikat.preferences.DataNames
import com.google.gson.Gson
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

class EnterPhoneViewModel(dataManager: DataManager) : BaseViewModel<PhoneNaviagtor>(dataManager) {

    fun validatePhone(hashMap: HashMap<String, String?>) {
        setIsLoading(true)
        dataManager.signup_phone_2(hashMap)
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
                navigator.onPhoneVerify(it.data.access_token ?: "")
            }
            NetworkConstants.AUTHFAILED -> navigator.onSessionExpire()
            else -> navigator.onErrorOccur(it?.message ?: "")
        }
    }

    fun updateName(hashMap: HashMap<String, String>) {
        setIsLoading(true)
        dataManager.apiUpdateName(hashMap)
                ?.observeOn(AndroidSchedulers.mainThread())
                ?.subscribeOn(Schedulers.io())
                ?.subscribe({ this.codeUpdateNameResponse(it) }, { this.handleError(it) })?.let {
                    compositeDisposable.add(it)
                }
    }

    private fun codeUpdateNameResponse(it: PojoSignUp?) {
        setIsLoading(false)
        when (it?.status) {
            NetworkConstants.SUCCESS -> {
                navigator.updatePhone(it)

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


}
