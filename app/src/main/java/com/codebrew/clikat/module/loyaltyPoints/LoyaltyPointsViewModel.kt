package com.codebrew.clikat.module.loyaltyPoints

import android.util.Log
import com.codebrew.clikat.base.BaseViewModel
import com.codebrew.clikat.data.DataManager
import com.codebrew.clikat.data.constants.NetworkConstants
import com.codebrew.clikat.data.constants.PrefenceConstants
import com.codebrew.clikat.data.model.api.LoyaltyResponse
import com.google.gson.JsonObject
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import org.json.JSONObject


class LoyaltyPointsViewModel(dataManager: DataManager) : BaseViewModel<LoyaltyPointsNavigator>(dataManager) {


    fun apiGetLoyaltyPoints() {
        setIsLoading(true)
        compositeDisposable.add(dataManager.getLoyaltyPointsData()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe({ this.validateResponse(it) }, { this.handleError(it) })
        )
    }

    private fun validateResponse(it: LoyaltyResponse?) {
        setIsLoading(false)

        when (it?.status) {
            NetworkConstants.SUCCESS -> {

                navigator.loyaltyPointsSuccess(it.data)

            }
            NetworkConstants.AUTHFAILED -> {
                dataManager.setUserAsLoggedOut()
                navigator.onSessionExpire()
            }
            else -> {
                it?.message?.let { it1 ->
                    navigator.onErrorOccur(it1.toString()) }
            }
        }
    }

    fun apiGetLoyalty() {
        val param = hashMapOf(
                "accessToken" to dataManager.getKeyValue(
                        PrefenceConstants.ACCESS_TOKEN,
                        PrefenceConstants.TYPE_STRING
                ).toString())

        setIsLoading(true)
        compositeDisposable.add(dataManager.getLoyaltyPoints(param)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe({ this.validateResponseLoyality(it) }, { this.handleError(it) })
        )
    }

    private fun validateResponseLoyality(it: LoyalityResponse?) {
        setIsLoading(false)

        when (it?.status) {
            NetworkConstants.SUCCESS_CODE -> {

                navigator.loyaltyPointsSuccessNew(it.data)

            }
            NetworkConstants.AUTHFAILED -> {
                dataManager.setUserAsLoggedOut()
                navigator.onSessionExpire()
            }
            else -> {
                it?.message?.let { it1 ->
                    navigator.onErrorOccur(it1.toString()) }
            }
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
