package com.codebrew.clikat.module.cart.tables

import com.codebrew.clikat.base.BaseViewModel
import com.codebrew.clikat.data.DataManager
import com.codebrew.clikat.data.constants.NetworkConstants
import com.codebrew.clikat.data.model.api.TableListResponse
import com.codebrew.clikat.modal.other.SuplierProdListModel
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers


class TablesViewModel(dataManager: DataManager) : BaseViewModel<TablesListNavigator>(dataManager) {


    fun getListOfTablesAccordingToSlot(param: HashMap<String, Any?>, loader:Boolean) {
        if(loader) {
            setIsLoading(true)
        }

        compositeDisposable.add(dataManager.getListOfTablesOfSupplier(param)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe({ this.validateTableListingResponse(it) }, { this.handleError(it) })
        )
    }

    private fun validateTableListingResponse(it: TableListResponse?) {
        setIsLoading(false)
        when (it?.status) {
            NetworkConstants.SUCCESS -> {
                navigator.onTableListReceived(it.data?.list)
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
        handleErrorMsg(e).let {
            if (it == NetworkConstants.AUTH_MSG) {
                dataManager.setUserAsLoggedOut()
                navigator.onSessionExpire()
            } else {
                navigator.onErrorOccur(it)
            }
        }
    }

    fun makeBookingAccordingToSlot(param: HashMap<String, String?>) {
        setIsLoading(true)

        compositeDisposable.add(dataManager.makeTableBookingRequest(param)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe({ this.validateTableBookingResponse(it) }, {
                    this.handleError(it)
                })
        )
    }

    private fun validateTableBookingResponse(it: SuplierProdListModel?) {
        setIsLoading(false)
        when (it?.status) {
            NetworkConstants.SUCCESS -> {
                navigator.onTableSuccessfullyBooked()
            }
            NetworkConstants.AUTHFAILED -> {
                dataManager.setUserAsLoggedOut()
                navigator.onSessionExpire()
            }
            else -> {
                it?.message?.let { it1 -> navigator.onErrorOccur(it1.toString() ?: "") }
            }
        }
    }

}