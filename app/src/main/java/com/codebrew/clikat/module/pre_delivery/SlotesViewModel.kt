package com.codebrew.clikat.module.pre_delivery

import androidx.lifecycle.MutableLiveData
import com.codebrew.clikat.base.BaseInterface
import com.codebrew.clikat.base.BaseViewModel
import com.codebrew.clikat.data.DataManager
import com.codebrew.clikat.data.constants.NetworkConstants
import com.codebrew.clikat.data.model.api.QuestionList
import com.codebrew.clikat.data.model.api.QuestionResponse
import com.codebrew.clikat.module.pre_delivery.Model.SlotsModel
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

class SlotesViewModel(dataManager: DataManager) : BaseViewModel<BaseInterface>(dataManager) {

    val slotsLiveData by lazy { MutableLiveData<List<String>>() }


    fun getAvailableSlots(categoryId: String) {
        val param = dataManager.updateUserInf()
        param["supplier_id"] = "1"

        param["date_order_type"] = "3"
        param["date"] = "3"


        setIsLoading(true)
        dataManager.getSlots(param)
                ?.observeOn(AndroidSchedulers.mainThread())
                ?.subscribeOn(Schedulers.io())
                ?.subscribe({ this.codeResponse(it) }, { this.handleError(it) })?.let {
                    compositeDisposable.add(it)
                }
    }


    private fun codeResponse(it: SlotsModel?) {
        setIsLoading(false)
        when (it?.status) {
            NetworkConstants.SUCCESS, 4 -> {

                slotsLiveData.value = it.data
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