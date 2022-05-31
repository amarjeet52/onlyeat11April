package com.codebrew.clikat.module.agent_time_slot

import com.codebrew.clikat.BuildConfig
import com.codebrew.clikat.app_utils.CommonUtils
import com.codebrew.clikat.app_utils.SingleLiveEvent
import com.codebrew.clikat.base.BaseInterface
import com.codebrew.clikat.base.BaseViewModel
import com.codebrew.clikat.data.DataManager
import com.codebrew.clikat.data.constants.NetworkConstants
import com.codebrew.clikat.data.constants.PrefenceConstants
import com.codebrew.clikat.data.model.api.SuccessModel
import com.codebrew.clikat.data.model.api.TableListResponse
import com.codebrew.clikat.modal.agent.AgentSlotsModel
import com.codebrew.clikat.modal.other.GetAgentListKey
import com.codebrew.clikat.modal.slots.SupplierSlots
import com.codebrew.clikat.modal.slots.SuppliersSlotsResponse
import com.codebrew.clikat.modal.slots.SuppliersTableSlotsResponse
import com.codebrew.clikat.modal.slots.TableSlotsItem
import com.codebrew.clikat.preferences.DataNames
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers


class AgentViewModel(dataManager: DataManager) : BaseViewModel<BaseInterface>(dataManager) {

    val agentKeyData by lazy { SingleLiveEvent<List<GetAgentListKey.DataBean>>() }
    val agentSlotsData by lazy { SingleLiveEvent<List<String>>() }
    val slotsData by lazy { SingleLiveEvent<List<String>>() }
    val tableSlotsData by lazy { SingleLiveEvent<TableSlotsItem>() }
    val tableListSlotsData by lazy { SingleLiveEvent<TableListResponse>() }
    val holdSlotsLiveData by lazy { SingleLiveEvent<Any>() }
    val supplierAvailabilities by lazy { SingleLiveEvent<SupplierSlots>() }

    fun getAgentSlotsList(param: HashMap<String, String>) {
        setIsLoading(true)

        val headerParam = HashMap<String, String>()
        headerParam["secret_key"] = dataManager.getKeyValue(DataNames.AGENT_DB_SECRET, PrefenceConstants.TYPE_STRING).toString()
        headerParam["api_key"] = dataManager.getKeyValue(DataNames.AGENT_API_KEY, PrefenceConstants.TYPE_STRING).toString()

        compositeDisposable.add(dataManager.getAvailabilitySlot(headerParam, param)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe({ this.validateAgentSlots(it) }, { this.handleError(it) })
        )
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



    fun getSlotsList(param: HashMap<String, String>) {
        CommonUtils.changebaseUrl(dataManager.getRetrofitUtl(), BuildConfig.BASE_URL)
        setIsLoading(true)

        compositeDisposable.add(dataManager.getSupplierSlots(param)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe({
                    this.validateSlots(it) }, { this.handleError(it) })
        )
    }

    private fun validateSlots(it: AgentSlotsModel?) {
        setIsLoading(false)
        when (it?.status) {
            NetworkConstants.SUCCESS , NetworkConstants.SUCCESS_CODE -> {
                slotsData.value = it.data
            }
            NetworkConstants.AUTHFAILED -> {
                navigator.onSessionExpire()
            }
            else -> {
                navigator.onErrorOccur(it?.message ?: "")
            }
        }
    }

    fun getTableSlotsList(param: HashMap<String, String>) {
        CommonUtils.changebaseUrl(dataManager.getRetrofitUtl(), BuildConfig.BASE_URL)
        setIsLoading(true)

        compositeDisposable.add(dataManager.getSupplierTableSlots(param)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe({
                    this.validateTableSlots(it) }, { this.handleError(it) })
        )
    }

    private fun validateTableSlots(it: SuppliersTableSlotsResponse?) {
        setIsLoading(false)
        when (it?.status) {
            NetworkConstants.SUCCESS , NetworkConstants.SUCCESS_CODE -> {
                tableSlotsData.value = it.data
            }
            NetworkConstants.AUTHFAILED -> {
                navigator.onSessionExpire()
            }
            else -> {
                navigator.onErrorOccur(it?.message ?: "")
            }
        }
    }
    fun getListOfTablesAccordingToSlot(param: HashMap<String, Int?>, loader:Boolean) {
        if(loader) {
            setIsLoading(true)
        }

        compositeDisposable.add(dataManager.getListOfTablesOfSuppliers(param)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe({ this.validateTableListingResponse(it) }, { this.handleError(it) })
        )
    }


    private fun validateTableListingResponse(it: TableListResponse?) {
        setIsLoading(false)
        when (it?.status) {
            NetworkConstants.SUCCESS -> {
                tableListSlotsData.value = (it)
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

    fun holdSlotApi(param: HashMap<String, String>) {
        CommonUtils.changebaseUrl(dataManager.getRetrofitUtl(), BuildConfig.BASE_URL)
        setIsLoading(true)

        compositeDisposable.add(dataManager.apiHoldSlot(param)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe({
                    this.validateHoldSlots(it) }, { this.handleError(it) })
        )
    }

    private fun validateHoldSlots(it: SuccessModel?) {
        setIsLoading(false)
        when (it?.statusCode) {
            NetworkConstants.SUCCESS , NetworkConstants.SUCCESS_CODE -> {
                holdSlotsLiveData.value=it.data
            }
            NetworkConstants.AUTHFAILED -> {
                navigator.onSessionExpire()
            }
            else -> {
                navigator.onErrorOccur(it?.message ?: "")
            }
        }
    }
    fun getSupplierAvailabilities(param: HashMap<String, String>) {
        CommonUtils.changebaseUrl(dataManager.getRetrofitUtl(), BuildConfig.BASE_URL)
        setIsLoading(true)

        compositeDisposable.add(dataManager.getSupplierAvailabilities(param)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe({
                    this.validateSuppliersAvailabilities(it) }, { this.handleError(it) })
        )
    }


    private fun validateSuppliersAvailabilities(it: SuppliersSlotsResponse?) {
        setIsLoading(false)
        when (it?.status) {
            NetworkConstants.SUCCESS , NetworkConstants.SUCCESS_CODE -> {
                supplierAvailabilities.value = it.data
            }
            NetworkConstants.AUTHFAILED -> {
                navigator.onSessionExpire()
            }
            else -> {
                navigator.onErrorOccur(it?.message ?: "")
            }
        }
    }
}
