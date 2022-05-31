package com.codebrew.clikat.module.selectAgent

import com.codebrew.clikat.app_utils.SingleLiveEvent
import com.codebrew.clikat.base.BaseViewModel
import com.codebrew.clikat.data.DataManager
import com.codebrew.clikat.data.constants.NetworkConstants
import com.codebrew.clikat.data.constants.PrefenceConstants
import com.codebrew.clikat.modal.agent.AgentAvailabilityModel
import com.codebrew.clikat.modal.agent.AgentDataBean
import com.codebrew.clikat.modal.agent.AgentSlotsModel
import com.codebrew.clikat.preferences.DataNames
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers


class SelectAgentViewModel(dataManager: DataManager) : BaseViewModel<SelectAgentNavigator>(dataManager) {


    val agentAvailList by lazy { SingleLiveEvent<MutableList<AgentDataBean>>() }
    val agentSlotsData by lazy { SingleLiveEvent<List<String>>() }


    fun getAgentAvailability(param: HashMap<String, String>) {
        setIsLoading(true)

        val headerParam = HashMap<String, String>()
        headerParam["api_key"] = dataManager.getKeyValue(DataNames.AGENT_API_KEY, PrefenceConstants.TYPE_STRING).toString()
        headerParam["secret_key"] = dataManager.getKeyValue(DataNames.AGENT_DB_SECRET, PrefenceConstants.TYPE_STRING).toString()

        compositeDisposable.add(dataManager.getAgentAvailability(headerParam, param)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe({ this.validateAgentKey(it) }, { this.handleError(it) })
        )
    }

    fun validAgentSlot(hashMap: HashMap<String, String>) {
        setIsLoading(true)

        val headerParam = HashMap<String, String>()
        headerParam["api_key"] = dataManager.getKeyValue(DataNames.AGENT_API_KEY, PrefenceConstants.TYPE_STRING).toString()
        headerParam["secret_key"] = dataManager.getKeyValue(DataNames.AGENT_DB_SECRET, PrefenceConstants.TYPE_STRING).toString()

        compositeDisposable.add(dataManager.checkAgentTimeSlotAvail(headerParam, hashMap)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe({ this.validateAgentSlot(it) }, { this.handleError(it) })
        )
    }

    private fun validateAgentSlot(it: com.codebrew.clikat.data.model.api.SuccessModel?) {
        setIsLoading(false)
        if (it?.statusCode == NetworkConstants.SUCCESS) {
            navigator.onValidTimeSlot()
        } else {
            it?.message?.let { it1 -> navigator.onErrorOccur(it1) }
        }
    }


    fun getAgentSlotsList(param: HashMap<String, String>) {
        setIsLoading(true)

        val headerParam = HashMap<String, String>()
        headerParam["secret_key"] = dataManager.getKeyValue(DataNames.AGENT_DB_SECRET, PrefenceConstants.TYPE_STRING).toString()
        headerParam["api_key"] = dataManager.getKeyValue(DataNames.AGENT_API_KEY, PrefenceConstants.TYPE_STRING).toString()

        compositeDisposable.add(dataManager.getAvailabilitySlots(headerParam, param)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe({ this.validateAgentSlots(it) }, { this.handleError(it) })
        )
    }


    private fun validateAgentKey(it: AgentAvailabilityModel?) {

        setIsLoading(false)

        if (it?.statusCode == NetworkConstants.SUCCESS) {
            agentAvailList.value = it.data
        } else {
            it?.message?.let { it1 -> navigator.onErrorOccur(it1) }
        }
    }

    private fun validateAgentSlots(it: AgentSlotsModel?) {

        setIsLoading(false)

        if (it?.statusCode == NetworkConstants.SUCCESS) {
            agentSlotsData.value = it.data
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
