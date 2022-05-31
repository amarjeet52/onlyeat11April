package com.codebrew.clikat.module.rental.carList

import com.codebrew.clikat.app_utils.SingleLiveEvent
import com.codebrew.clikat.base.BaseViewModel
import com.codebrew.clikat.data.DataManager
import com.codebrew.clikat.data.constants.NetworkConstants
import com.codebrew.clikat.data.model.api.ProductListModel
import com.codebrew.clikat.modal.other.FilterInputNew
import com.codebrew.clikat.modal.other.ProductDataBean
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

class ProductListViewModel(dataManager: DataManager) : BaseViewModel<ProductListNavigator>(dataManager) {
    val rentalDataLiveData by lazy { SingleLiveEvent<MutableList<ProductDataBean>>() }

    fun getRentalList(param: FilterInputNew) {
        setIsLoading(true)

        compositeDisposable.add(dataManager.getRentalFilter(param)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe({ this.validateResponse(it) }, { this.handleError(it) })
        )
    }


    private fun validateResponse(it: ProductListModel?) {
        setIsLoading(false)
        setIsList(0)
        when (it?.status) {
            NetworkConstants.SUCCESS -> {

                setIsList(it.data?.product?.size ?: 0)
                rentalDataLiveData.value = it.data?.product
            }
            NetworkConstants.AUTHFAILED -> {
                dataManager.setUserAsLoggedOut()
                navigator.onSessionExpire()
            }
            else -> {
                it?.message?.let { it1 -> navigator.onErrorOccur(it1) }
            }
        }
    }


    private fun handleError(e: Throwable) {
        setIsLoading(false)
        setIsList(0)
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
