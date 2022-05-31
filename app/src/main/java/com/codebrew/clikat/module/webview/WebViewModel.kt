package com.codebrew.clikat.module.webview

import androidx.lifecycle.MutableLiveData
import com.codebrew.clikat.base.BaseInterface
import com.codebrew.clikat.base.BaseViewModel
import com.codebrew.clikat.data.DataManager
import com.codebrew.clikat.data.constants.NetworkConstants
import com.codebrew.clikat.data.model.api.Data
import com.codebrew.clikat.data.model.api.TermsConditionModel
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

class WebViewModel(dataManager: DataManager) : BaseViewModel<BaseInterface>(dataManager) {


    val webListData: MutableLiveData<List<Data?>?> by lazy {
        MutableLiveData<List<Data?>?>()
    }


    fun getTermsCondition() {
        setIsLoading(true)


        dataManager.getTermsCondition()
                ?.observeOn(AndroidSchedulers.mainThread())
                ?.subscribeOn(Schedulers.io())
                ?.subscribe({ this.validateResponse(it) }, { this.handleError(it) })?.let {
                    compositeDisposable.add(it)
                }
    }


    private fun validateResponse(it: TermsConditionModel?) {


        setIsLoading(false)
        when (it?.status) {
            NetworkConstants.SUCCESS, 4 -> {
                webListData.setValue(it.data)
            }
            NetworkConstants.AUTHFAILED -> {
                navigator.onSessionExpire()
            }
            else -> {
                navigator.onErrorOccur(it?.message ?: "")
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
