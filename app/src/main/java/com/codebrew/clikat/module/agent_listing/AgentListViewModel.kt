package com.codebrew.clikat.module.agent_listing

import androidx.databinding.ObservableInt
import com.codebrew.clikat.BuildConfig
import com.codebrew.clikat.app_utils.CommonUtils
import com.codebrew.clikat.app_utils.SingleLiveEvent
import com.codebrew.clikat.base.BaseViewModel
import com.codebrew.clikat.data.DataManager
import com.codebrew.clikat.data.constants.NetworkConstants
import com.codebrew.clikat.data.constants.PrefenceConstants
import com.codebrew.clikat.modal.agent.AgentListModel
import com.codebrew.clikat.modal.agent.DataBean
import com.codebrew.clikat.modal.agent.GetAgentListParam
import com.codebrew.clikat.modal.other.GetAgentListKey
import com.codebrew.clikat.preferences.DataNames
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers


class AgentListViewModel(dataManager: DataManager) : BaseViewModel<AgentListNavigator>(dataManager) {


    val agentList by lazy { SingleLiveEvent<List<DataBean>>() }
    val agentKeyData by lazy { SingleLiveEvent<List<GetAgentListKey.DataBean>>() }
    val isAgentList = ObservableInt(0)


    fun getAgentKeyList() {
        setIsLoading(true)

        CommonUtils.changebaseUrl(dataManager.getRetrofitUtl(), BuildConfig.BASE_URL)

        val headerParam = HashMap<String, String>()
        headerParam["agent_db_key"] = dataManager.getKeyValue(PrefenceConstants.AGENT_DB_SECRET, PrefenceConstants.TYPE_STRING).toString()

        compositeDisposable.add(dataManager.getAgentListKey(headerParam, "as")
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe({ this.validateAgentKey(it) }, { this.handleError(it) })
        )
    }

    private fun validateAgentKey(it: GetAgentListKey?) {

        setIsLoading(false)

        if (it?.status == NetworkConstants.SUCCESS) {
            agentKeyData.value = it.data
        } else {
            it?.message?.let { it1 -> navigator.onErrorOccur(it1) }
        }
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


    fun getAgentList(headers: HashMap<String, String>, param: GetAgentListParam) {
        setIsLoading(true)

        compositeDisposable.add(dataManager.getAgentListing(headers, param)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe({ this.validateAgentList(it) }, { this.handleError(it) })
        )
    }


    private fun validateAgentList(it: AgentListModel?) {

        setIsLoading(false)
        setIsAgentList(it?.data?.count() ?: 0)

        if (it?.statusCode == NetworkConstants.SUCCESS) {
            agentList.value = it.data
        } else {
            it?.message?.let { it1 -> navigator.onErrorOccur(it1) }
        }
    }

    fun setIsAgentList(listCount: Int) {
        this.isAgentList.set(listCount)
    }


    private fun handleError(e: Throwable) {
        setIsLoading(false)
        setIsAgentList(0)
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
