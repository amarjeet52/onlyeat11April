package com.codebrew.clikat.module.feedback

import androidx.databinding.ObservableBoolean
import com.codebrew.clikat.base.BaseViewModel
import com.codebrew.clikat.data.DataManager
import com.codebrew.clikat.data.constants.NetworkConstants
import com.codebrew.clikat.data.model.api.SuccessModel
import com.codebrew.clikat.data.model.api.SuggestionResponse
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers


class FeedbackViewModel(dataManager: DataManager) : BaseViewModel<FeedbackNavigator>(dataManager) {
    val showMainScreen= ObservableBoolean(true)
    fun apiAddFeedback(hashMap: HashMap<String, String>) {
        setIsLoading(true)
        showMainScreen.set(true)
        compositeDisposable.add(dataManager.apiAddFeedback(hashMap)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe({ this.validateResponse(it) }, { this.handleError(it) })
        )
    }

    private fun validateResponse(it: SuccessModel?) {
        setIsLoading(false)

        when (it?.statusCode) {
            NetworkConstants.SUCCESS -> {
                navigator.feedbackSuccess()
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

    fun apiGetSuggestions() {
        setIsLoading(true)
        showMainScreen.set(false)
        compositeDisposable.add(dataManager.getSuggestionsApi(0,100)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe({ this.validateSuggestionsResponse(it) }, { this.handleError(it) })
        )
    }

    private fun validateSuggestionsResponse(it: SuggestionResponse?) {
        setIsLoading(false)
        showMainScreen.set(true)
        when (it?.status) {
            NetworkConstants.SUCCESS -> {
                navigator.suggestionListSuccess(it.data)
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
