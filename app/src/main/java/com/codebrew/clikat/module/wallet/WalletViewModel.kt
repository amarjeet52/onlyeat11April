package com.codebrew.clikat.module.wallet

import androidx.databinding.ObservableInt
import com.codebrew.clikat.app_utils.SingleLiveEvent
import com.codebrew.clikat.base.BaseViewModel
import com.codebrew.clikat.base.PagingResult
import com.codebrew.clikat.data.DataManager
import com.codebrew.clikat.data.constants.AppConstants
import com.codebrew.clikat.data.constants.NetworkConstants
import com.codebrew.clikat.data.model.api.SuccessModel
import com.codebrew.clikat.modal.wallet.WalletTransactionsResonse
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import timber.log.Timber


class WalletViewModel(dataManager: DataManager) : BaseViewModel<WalletNavigator>(dataManager) {

    val transactions by lazy { SingleLiveEvent<PagingResult<WalletTransactionsResonse>>() }
    val isTransactionCount = ObservableInt(0)
    var skip: Int? = 0
    private var isLastReceived = false

    fun validForPaging(): Boolean = !isLoading.get() && !isLastReceived

    fun getTransactionsList(isFirstPage: Boolean) {
        if (isFirstPage) {
            skip = 0
            isLastReceived = false
            setIsLoading(true)
        }
        compositeDisposable.add(dataManager.getAllWalletTransactions(skip ?: 0, AppConstants.LIMIT)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe({ this.validateResponse(isFirstPage, it) }, { this.handleError(it) })
        )
    }

    private fun validateResponse(isFirstPage: Boolean, it: WalletTransactionsResonse) {
        setIsLoading(false)
        if (isFirstPage)
            setIsTransaction(it.data?.transactions?.count() ?: 0)
        when (it.status) {
            NetworkConstants.SUCCESS -> {
                val receivedGroups = it.data?.transactions
                if ((receivedGroups?.size ?: 0) < AppConstants.LIMIT) {
                    Timber.i("Last group is received")
                    isLastReceived = true
                } else {
                    Timber.i("Next page for topic groups is available")
                    skip = skip?.plus(AppConstants.LIMIT)
                }
                transactions.value = PagingResult(isFirstPage, it)
            }
            NetworkConstants.AUTHFAILED -> {
                dataManager.setUserAsLoggedOut()
                navigator.onSessionExpire()
            }
            else -> {
                it.message?.let { it1 -> navigator.onErrorOccur(it1) }
            }
        }
    }

    fun apiSendMoney(amount: String, countryCode: String?, emailPhone: String, comment: String) {
        val mParam = HashMap<String, String>()
        mParam["amount"] = amount
        if (countryCode == null)
            mParam["user_email"] = emailPhone
        else {
            mParam["countryCode"] = countryCode
            mParam["phone_number"] = emailPhone
        }
        mParam["comment"] = comment
        setIsLoading(true)
        dataManager.apiSendMoney(mParam)
                ?.observeOn(AndroidSchedulers.mainThread())
                ?.subscribeOn(Schedulers.io())
                ?.subscribe({ this.validateShareMoneyResponse(it) }, { this.handleError(it) })?.let {
                    compositeDisposable.add(it)
                }
    }

    private fun validateShareMoneyResponse(it: SuccessModel?) {
        setIsLoading(false)
        when (it?.statusCode) {
            NetworkConstants.SUCCESS -> {
                navigator.onMoneySent()
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
        setIsTransaction(0)
        handleErrorMsg(e).let {
            if (it == NetworkConstants.AUTH_MSG) {
                dataManager.setUserAsLoggedOut()
                navigator.onSessionExpire()
            } else {
                navigator.onErrorOccur(it)
            }
        }
    }

    private fun setIsTransaction(listCount: Int) {
        this.isTransactionCount.set(listCount)
    }
}
