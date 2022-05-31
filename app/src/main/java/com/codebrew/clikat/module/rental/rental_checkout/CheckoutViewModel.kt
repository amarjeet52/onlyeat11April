package com.codebrew.clikat.module.rental.rental_checkout

import com.codebrew.clikat.base.BaseViewModel
import com.codebrew.clikat.data.DataManager
import com.codebrew.clikat.data.constants.NetworkConstants
import com.codebrew.clikat.data.network.ApiResponse
import com.codebrew.clikat.modal.other.PlaceOrderInput
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers


class CheckoutViewModel(dataManager: DataManager) : BaseViewModel<CheckoutNavigator>(dataManager) {

    fun generateOrder(param: PlaceOrderInput?) {
        setIsLoading(true)
        dataManager.generateOrder(param)
                ?.observeOn(AndroidSchedulers.mainThread())
                ?.subscribeOn(Schedulers.io())
                ?.subscribe({ this.validateResponse(it) }, { this.handleError(it) })?.let {
                    compositeDisposable.add(it
                    )
                }
    }

    private fun validateResponse(it: ApiResponse<Any>) {
        setIsLoading(false)
        if (it.status == NetworkConstants.SUCCESS) {
            navigator.onOrderPlaced()
        } else {
            it.msg?.let { it1 -> navigator.onErrorOccur(it1.toString()) }
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
