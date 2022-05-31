package com.codebrew.clikat.module.cart.schedule_order

import com.codebrew.clikat.app_utils.SingleLiveEvent
import com.codebrew.clikat.base.BaseInterface
import com.codebrew.clikat.base.BaseViewModel
import com.codebrew.clikat.data.DataManager
import com.codebrew.clikat.data.constants.NetworkConstants
import com.codebrew.clikat.data.constants.PrefenceConstants
import com.codebrew.clikat.modal.agent.AgentSlotsModel
import com.codebrew.clikat.modal.other.GetAgentListKey
import com.codebrew.clikat.preferences.DataNames
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers


class ScheduleOrderViewModel(dataManager: DataManager) : BaseViewModel<BaseInterface>(dataManager) {

    val slotsData by lazy { SingleLiveEvent<List<String>>() }

    fun getSlotsList(param: HashMap<String, String>) {
        setIsLoading(true)

        compositeDisposable.add(dataManager.getSupplierSlots(param)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe({ this.validateAgentSlots(it) }, { this.handleError(it) })
        )
    }


    private fun validateAgentSlots(it: AgentSlotsModel?) {

        setIsLoading(false)

        if (it?.statusCode == NetworkConstants.SUCCESS) {
            slotsData.value = it.data
        } else {
            it?.message?.let { it1 -> navigator.onErrorOccur(it1) }
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
