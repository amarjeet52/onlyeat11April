package com.codebrew.clikat.module.essentialHome

import com.codebrew.clikat.app_utils.SingleLiveEvent
import com.codebrew.clikat.base.BaseInterface
import com.codebrew.clikat.base.BaseViewModel
import com.codebrew.clikat.data.DataManager
import com.codebrew.clikat.data.constants.NetworkConstants
import com.codebrew.clikat.data.constants.PrefenceConstants
import com.codebrew.clikat.modal.other.*
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers


class ServiceViewModel(dataManager: DataManager) : BaseViewModel<BaseInterface>(dataManager) {

    val homeDataLiveData by lazy { SingleLiveEvent<Data>() }


    fun getCategories() {
        setIsLoading(true)

        val param = dataManager.updateUserInf()
        param["accessToken"] = dataManager.getKeyValue(PrefenceConstants.ACCESS_TOKEN, PrefenceConstants.TYPE_STRING).toString()
        compositeDisposable.add(dataManager.getAllCategoryNew(param)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe({ this.validateResponse(it) }, { this.handleError(it) })
        )
    }


    private fun validateResponse(it: CategoryListModel?) {
        setIsLoading(false)
        when (it?.status) {
            NetworkConstants.SUCCESS -> {
                homeDataLiveData.value = it.data
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
}
