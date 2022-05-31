package com.codebrew.clikat.module.requestsLists

import androidx.databinding.ObservableInt
import com.codebrew.clikat.app_utils.SingleLiveEvent
import com.codebrew.clikat.base.BaseViewModel
import com.codebrew.clikat.base.PagingResult
import com.codebrew.clikat.data.DataManager
import com.codebrew.clikat.data.constants.AppConstants.Companion.LIMIT
import com.codebrew.clikat.data.constants.NetworkConstants
import com.codebrew.clikat.data.model.api.RequestItem
import com.codebrew.clikat.data.model.api.RequestListModel
import com.codebrew.clikat.data.model.api.SuccessModel
import com.codebrew.clikat.data.model.others.CancelRequest
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import timber.log.Timber


class RequestsViewModel(dataManager: DataManager) : BaseViewModel<RequestsNavigator>(dataManager) {

    val requestsList by lazy { SingleLiveEvent<PagingResult<RequestItem>>() }
    val cancelRequest by lazy { SingleLiveEvent<Int>() }
    val isRequestsCount = ObservableInt(0)
    var offset: Int? = 0
    private var isLastReceived = false

    fun validForPaging(): Boolean = !isLoading.get() && !isLastReceived

    fun getRequestsList(firstPage: Boolean) {
        if (firstPage) {
            offset = 0
            isLastReceived = false
        }
        setIsLoading(true)
        compositeDisposable.add(dataManager.getRequestsList(offset, LIMIT)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe({ this.validateResponse(firstPage, it) }, { this.handleError(it) })
        )
    }

    fun cancelRequest(id: String, reason: String, adapterPos: Int) {
        setIsLoading(true)
        compositeDisposable.add(dataManager.apiCancelRequest(CancelRequest(id = id, reason = ""))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe({ this.validateCancelResponse(it, adapterPos) }, { this.handleError(it) })
        )
    }

    private fun validateResponse(firstPage: Boolean, it: RequestListModel?) {
        setIsLoading(false)
        if (firstPage)
            setIsRequests(it?.data?.data?.count() ?: 0)

        when (it?.status) {
            NetworkConstants.SUCCESS -> {
                val receivedGroups = it.data.data
                if (receivedGroups.size < LIMIT) {
                    Timber.i("Last group is received")
                    isLastReceived = true
                } else {
                    Timber.i("Next page for topic groups is available")
                    offset = offset?.plus(LIMIT)
                }
                requestsList.value = PagingResult(firstPage, it.data)
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

    private fun validateCancelResponse(it: SuccessModel?, adapterPos: Int) {
        setIsLoading(false)
        when (it?.statusCode) {
            NetworkConstants.SUCCESS -> {
                cancelRequest.value = adapterPos
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
        setIsRequests(0)
        handleErrorMsg(e).let {
            if (it == NetworkConstants.AUTH_MSG) {
                dataManager.setUserAsLoggedOut()
                navigator.onSessionExpire()
            } else {
                navigator.onErrorOccur(it)
            }
        }
    }

    private fun setIsRequests(listCount: Int) {
        this.isRequestsCount.set(listCount)
    }
}
