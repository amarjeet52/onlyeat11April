package com.codebrew.clikat.module.subscription

import androidx.databinding.ObservableInt
import androidx.lifecycle.viewModelScope
import com.codebrew.clikat.app_utils.SingleLiveEvent
import com.codebrew.clikat.base.BaseInterface
import com.codebrew.clikat.base.BaseViewModel
import com.codebrew.clikat.base.PagingResult
import com.codebrew.clikat.data.DataManager
import com.codebrew.clikat.data.constants.AppConstants.Companion.LIMIT
import com.codebrew.clikat.data.constants.NetworkConstants
import com.codebrew.clikat.data.model.api.SubcripListModel
import com.codebrew.clikat.data.model.api.SubcripModel
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import timber.log.Timber


class SubscriptionViewModel(dataManager: DataManager) : BaseViewModel<BaseInterface>(dataManager) {


    val subscripLiveData by lazy { SingleLiveEvent<PagingResult<MutableList<SubcripModel>>>() }

    val isSubscripCount = ObservableInt(0)

    var skip: Int? = 0
    private var isLastReceived = false

    fun validForPaging(): Boolean =  !isLastReceived

    fun getSubscrpList(isFirstPage: Boolean) {
        if (isFirstPage) {
            skip = 0
            isLastReceived = false
            setIsLoading(true)
        }
        val  hashMap= hashMapOf("limit" to LIMIT.toString(),"skip" to (skip ?: 0).toString())

        dataManager.getSubscriptionList(hashMap)
                .observeOn(AndroidSchedulers.mainThread())
                ?.subscribeOn(Schedulers.io())
                ?.subscribe({ this.validateSubcripList(isFirstPage,it) }, { this.handleError(it) })?.let {
                    compositeDisposable.add(it)
                }
    }

    private fun validateSubcripList(isFirstPage: Boolean, it: SubcripListModel?) {

        setIsLoading(false)
        if (isFirstPage)
            isSubscripCount.set(it?.data?.list?.count()?:0)
        when (it?.status) {
            NetworkConstants.SUCCESS -> {
                val receivedGroups = it.data.list
                if ((receivedGroups.size ?: 0) < LIMIT) {
                    Timber.i("Last group is received")
                    isLastReceived = true
                } else {
                    Timber.i("Next page for topic groups is available")
                    skip = skip?.plus(LIMIT)
                }

                subscripLiveData.value = PagingResult(isFirstPage, it.data.list)
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
        isSubscripCount.set(0)
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
