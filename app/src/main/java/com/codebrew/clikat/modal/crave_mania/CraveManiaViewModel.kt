package com.codebrew.clikat.modal.crave_mania

import com.codebrew.clikat.base.BaseViewModel
import com.codebrew.clikat.data.DataManager
import com.codebrew.clikat.data.constants.NetworkConstants
import com.codebrew.clikat.data.constants.PrefenceConstants
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

class CraveManiaViewModel(dataManager: DataManager) : BaseViewModel<CraveManiaNavigator>(dataManager) {



    fun getCraveManiaRestaurant() {

            val param = dataManager.updateUserInf()
            param["limit"] = "20"
            param["skip"] = "0"
            param["sectionId"] = "13"
            param["accessToken"] = dataManager.getKeyValue(PrefenceConstants.ACCESS_TOKEN, PrefenceConstants.TYPE_STRING).toString()


            compositeDisposable.add(  dataManager.getCraveMania(param)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeOn(Schedulers.io())
                    .subscribe({ this.validateResponse(it) }, {
                        this.handleError(it)
                    })
            )
    }

    private fun validateResponse(it: CraveManiaResponseData?) {
        when (it?.status) {
            NetworkConstants.SUCCESS -> {
               navigator.onCraveManiaRestaurantResponse(it)
            }

            NetworkConstants.SUCCESS_CODE -> {
                navigator.onCraveManiaRestaurantResponse(it)
            }

            NetworkConstants.AUTHFAILED -> {
                dataManager.setUserAsLoggedOut()
                navigator.onSessionExpire()
            }
            else -> {
                it?.message?.let { it1 ->
                    navigator.onErrorOccur(it1)
                }
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
