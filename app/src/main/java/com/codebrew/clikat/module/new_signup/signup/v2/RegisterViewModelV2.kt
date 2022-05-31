package com.codebrew.clikat.module.new_signup.signup.v2

import com.codebrew.clikat.base.BaseViewModel
import com.codebrew.clikat.data.DataManager
import com.codebrew.clikat.data.constants.NetworkConstants
import com.codebrew.clikat.data.constants.PrefenceConstants
import com.codebrew.clikat.data.model.others.RegisterParamModelV2
import com.codebrew.clikat.modal.PojoSignUp
import com.codebrew.clikat.module.new_signup.signup.RegisterNavigator
import com.codebrew.clikat.preferences.DataNames
import com.google.gson.Gson
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

class RegisterViewModelV2(dataManager: DataManager) : BaseViewModel<RegisterNavigator>(dataManager) {

    fun validateSignup(param: RegisterParamModelV2) {
        setIsLoading(true)
        dataManager.signup_step_first(param)
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

                dataManager.addGsonValue(DataNames.USER_DATA, Gson().toJson(it))

                navigator.onRegisterSuccess(it?.data?.access_token?:"","")
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
