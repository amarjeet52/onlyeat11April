package com.codebrew.clikat.module.base_orders

import androidx.databinding.ObservableInt
import com.codebrew.clikat.app_utils.SingleLiveEvent
import com.codebrew.clikat.base.BaseViewModel
import com.codebrew.clikat.base.PagingResult
import com.codebrew.clikat.data.DataManager
import com.codebrew.clikat.data.constants.AppConstants
import com.codebrew.clikat.data.constants.NetworkConstants
import com.codebrew.clikat.data.constants.PrefenceConstants
import com.codebrew.clikat.data.model.api.OrderListModel
import com.codebrew.clikat.data.model.api.orderDetail.OrderHistory
import com.codebrew.clikat.modal.DataCommon
import com.codebrew.clikat.modal.ExampleCommon
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import timber.log.Timber


class BaseOrderViewModel(dataManager: DataManager) : BaseViewModel<BaseOrderNavigator>(dataManager) {

    val historyLiveData by lazy { SingleLiveEvent<PagingResult<MutableList<OrderHistory>>>() }
    val pendingLiveData by lazy { SingleLiveEvent<PagingResult<MutableList<OrderHistory>>>() }
    val cancelLiveData by lazy { SingleLiveEvent<DataCommon>() }

    var offset: Int? = 0
    private var isLastReceived = false
    fun validForPaging(): Boolean = !isLoading.get() && !isLastReceived

    val isOrderList = ObservableInt(0)

    fun getOrderHistory(firstPage: Boolean) {
        setIsLoading(true)

        if (firstPage) {
            offset = 0
            isLastReceived = false
        }

        val param = hashMapOf("accessToken" to dataManager.getKeyValue(PrefenceConstants.ACCESS_TOKEN, PrefenceConstants.TYPE_STRING).toString(),
                "languageId" to dataManager.getLangCode(),
                "offset" to offset.toString(), "limit" to AppConstants.LIMIT.toString())

        dataManager.orderHistory(param)
                ?.observeOn(AndroidSchedulers.mainThread())
                ?.subscribeOn(Schedulers.io())
                ?.subscribe({ this.validateResponse(firstPage, it) }, { this.handleError(it) })?.let {
                    compositeDisposable.add(it)
                }
    }


    fun getUpcomingList(firstPage: Boolean) {
        setIsLoading(true)
        // "offset":6,"limit":6}

        if (firstPage) {
            offset = 0
            isLastReceived = false
        }

        val param = hashMapOf("accessToken" to dataManager.getKeyValue(PrefenceConstants.ACCESS_TOKEN, PrefenceConstants.TYPE_STRING).toString(),
                "languageId" to dataManager.getLangCode(),
                "offset" to offset.toString(), "limit" to AppConstants.LIMIT.toString())

        dataManager.upcomingOrder(param)
                ?.observeOn(AndroidSchedulers.mainThread())
                ?.subscribeOn(Schedulers.io())
                ?.subscribe({ this.upcomingResponse(firstPage, it) }, { this.handleError(it) })?.let {
                    compositeDisposable.add(it)
                }
    }


    fun cancelOrder(orderId: String,cancelToWallet:Int) {
        setIsLoading(true)

        val param = hashMapOf("accessToken" to dataManager.getKeyValue(PrefenceConstants.ACCESS_TOKEN, PrefenceConstants.TYPE_STRING).toString(),
                "languageId" to dataManager.getLangCode(),
                "cancel_to_wallet" to cancelToWallet.toString(),
                "orderId" to orderId, "isScheduled" to "0")

        dataManager.cancelOrder(param)
                ?.observeOn(AndroidSchedulers.mainThread())
                ?.subscribeOn(Schedulers.io())
                ?.subscribe({ this.cancelResponse(it) }, { this.handleError(it) })?.let {
                    compositeDisposable.add(it)
                }
    }

    private fun cancelResponse(it: ExampleCommon?) {
        setIsLoading(false)
        when (it?.status) {
            NetworkConstants.SUCCESS -> {
                cancelLiveData.value = it.data
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


    private fun validateResponse(firstPage: Boolean, it: OrderListModel?) {
        setIsLoading(false)
        if (firstPage)
            setOrderList(it?.data?.orderHistory?.size ?: 0)

        when (it?.status) {
            NetworkConstants.SUCCESS -> {
                if (isOrderList.get() < AppConstants.LIMIT) {
                    Timber.i("Last group is received")
                    isLastReceived = true
                } else {
                    Timber.i("Next page for topic groups is available")
                    offset = offset?.plus(AppConstants.LIMIT)
                }
                historyLiveData.value = PagingResult(firstPage, it.data.orderHistory)
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


    private fun upcomingResponse(firstPage: Boolean, it: OrderListModel?) {
        setIsLoading(false)
        if (firstPage)
            setOrderList(it?.data?.orderHistory?.size ?: 0)

        when (it?.status) {
            NetworkConstants.SUCCESS -> {
                if (isOrderList.get() < AppConstants.LIMIT) {
                    Timber.i("Last group is received")
                    isLastReceived = true
                } else {
                    Timber.i("Next page for topic groups is available")
                    offset = offset?.plus(AppConstants.LIMIT)
                }
                pendingLiveData.value = PagingResult(firstPage, it.data.orderHistory)
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
        setOrderList(0)
        handleErrorMsg(e).let {
            if (it == NetworkConstants.AUTH_MSG) {
                dataManager.setUserAsLoggedOut()
                navigator.onSessionExpire()
            } else {
                navigator.onErrorOccur(it)
            }
        }
    }

    fun setOrderList(offerCount: Int) {
        this.isOrderList.set(offerCount)
    }

}
