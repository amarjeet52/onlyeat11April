package com.codebrew.clikat.module.payment_gateway

import androidx.databinding.ObservableBoolean
import androidx.lifecycle.MutableLiveData
import com.codebrew.clikat.BuildConfig
import com.codebrew.clikat.app_utils.CommonUtils
import com.codebrew.clikat.app_utils.SingleLiveEvent
import com.codebrew.clikat.base.BaseInterface
import com.codebrew.clikat.base.BaseViewModel
import com.codebrew.clikat.base.PagingResult
import com.codebrew.clikat.data.DataManager
import com.codebrew.clikat.data.constants.AppConstants
import com.codebrew.clikat.data.constants.NetworkConstants
import com.codebrew.clikat.data.constants.PrefenceConstants
import com.codebrew.clikat.data.network.ApiResponse
import com.codebrew.clikat.modal.wallet.Data
import com.codebrew.clikat.modal.wallet.WalletTransactionsResonse
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import timber.log.Timber
import java.io.File

class PaymentListViewModel(dataManager: DataManager) : BaseViewModel<BaseInterface>(dataManager) {
    val wallet by lazy { SingleLiveEvent<Data>() }
    val showWalletLoader = ObservableBoolean(false)

    val settingLiveData: MutableLiveData<String> by lazy {
        MutableLiveData<String>()
    }

    fun validateZelleImage(image: String) {
        CommonUtils.changebaseUrl(dataManager.getRetrofitUtl(), BuildConfig.BASE_URL)
        setIsLoading(true)

        val file = File(image)
        val requestBody = file.asRequestBody("image/*".toMediaTypeOrNull())
        val partImage = MultipartBody.Part.createFormData("file", file.name, requestBody)

        compositeDisposable.add(dataManager.uploadFile(partImage)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe({ this.imageResponse(it) }, { this.handleError(it) })
        )
    }

    private fun handleError(e: Throwable) {
        setIsLoading(false)
        setImageLoading(false)
        handleErrorMsg(e).let {
            if (it == NetworkConstants.AUTH_MSG) {
                dataManager.setUserAsLoggedOut()
                navigator.onSessionExpire()
            } else {
                navigator.onErrorOccur(it)
            }
        }
    }

    private fun imageResponse(it: ApiResponse<Any>?) {
        setIsLoading(false)
        when (it?.status) {
            NetworkConstants.SUCCESS -> {
                settingLiveData.value = it.data.toString()
            }
            NetworkConstants.AUTHFAILED -> {
                dataManager.setUserAsLoggedOut()
                navigator.onSessionExpire()
            }
            else -> {
                it?.msg?.let { it1 -> navigator.onErrorOccur(it1.toString()) }
            }
        }
    }

    fun getTransactionsList() {
        CommonUtils.changebaseUrl(dataManager.getRetrofitUtl(), BuildConfig.BASE_URL)
        showWalletLoader.set(true)
        compositeDisposable.add(dataManager.getAllWalletTransactions(0, AppConstants.LIMIT)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe({ this.validateResponse(it) }, { this.handleError(it) })
        )
    }

    private fun validateResponse(it: WalletTransactionsResonse) {
        showWalletLoader.set(false)
        when (it.status) {
            NetworkConstants.SUCCESS -> {
                val receivedGroups = it.data
                wallet.value = receivedGroups
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

}
