package com.codebrew.clikat.module.notification

import androidx.databinding.ObservableInt
import com.codebrew.clikat.app_utils.SingleLiveEvent
import com.codebrew.clikat.base.BaseViewModel
import com.codebrew.clikat.base.PagingResult
import com.codebrew.clikat.data.DataManager
import com.codebrew.clikat.data.constants.AppConstants.Companion.LIMIT
import com.codebrew.clikat.data.constants.NetworkConstants
import com.codebrew.clikat.data.constants.PrefenceConstants
import com.codebrew.clikat.modal.PojoNotification
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import timber.log.Timber


class NotificationViewModel(dataManager: DataManager) : BaseViewModel<NotificationNavigator>(dataManager) {

    val notification by lazy { SingleLiveEvent<PagingResult<PojoNotification>>() }
    val isNotificationCount = ObservableInt(0)
    var skip: Int? = 0
    private var isLastReceived = false

    fun validForPaging(): Boolean = !isLoading.get() && !isLastReceived

    fun getNotificationList(isFirstPage: Boolean) {
        if (isFirstPage) {
            skip = 0
            isLastReceived = false
        }
        setIsLoading(true)
        val accessToken = dataManager.getKeyValue(PrefenceConstants.ACCESS_TOKEN, PrefenceConstants.TYPE_STRING).toString()

        compositeDisposable.add(dataManager.getAllNotifications(accessToken, skip ?: 0, LIMIT)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe({ this.validateResponse(isFirstPage, it) }, { this.handleError(it) })
        )
    }

    private fun validateResponse(isFirstPage: Boolean, it: PojoNotification?) {
        setIsLoading(false)
        if (isFirstPage)
            setIsNotification(it?.data?.notification?.count() ?: 0)
        when (it?.status) {
            NetworkConstants.SUCCESS -> {
                val receivedGroups = it.data?.notification
                if ((receivedGroups?.size ?: 0) < LIMIT) {
                    Timber.i("Last group is received")
                    isLastReceived = true
                } else {
                    Timber.i("Next page for topic groups is available")
                    skip = skip?.plus(LIMIT)
                }

                notification.value = PagingResult(isFirstPage, it)
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
        setIsNotification(0)
        handleErrorMsg(e).let {
            if (it == NetworkConstants.AUTH_MSG) {
                dataManager.setUserAsLoggedOut()
                navigator.onSessionExpire()
            } else {
                navigator.onErrorOccur(it)
            }
        }
    }

    private fun setIsNotification(listCount: Int) {
        this.isNotificationCount.set(listCount)
    }
}
