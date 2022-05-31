package com.codebrew.clikat.module.splash

import androidx.lifecycle.MutableLiveData
import com.codebrew.clikat.BuildConfig
import com.codebrew.clikat.base.BaseViewModel
import com.codebrew.clikat.data.DataManager
import com.codebrew.clikat.data.constants.AppConstants.Companion.CURRENCY_SYMBOL
import com.codebrew.clikat.data.constants.AppConstants.Companion.DEFAULT_CURRENCY_SYMBOL
import com.codebrew.clikat.data.constants.NetworkConstants
import com.codebrew.clikat.data.constants.PrefenceConstants
import com.codebrew.clikat.data.model.api.Currency
import com.codebrew.clikat.data.model.api.GetDbKeyModel
import com.codebrew.clikat.modal.DataCount
import com.codebrew.clikat.modal.PojoPendingOrders
import com.codebrew.clikat.modal.other.SettingModel
import com.codebrew.clikat.preferences.DataNames
import com.codebrew.clikat.retrofit.Config
import com.google.gson.Gson
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers


class SplashViewModel(dataManager: DataManager) : BaseViewModel<SplashNavigator>(dataManager) {


    val settingLiveData: MutableLiveData<SettingModel.DataBean> by lazy {
        MutableLiveData<SettingModel.DataBean>()
    }


    val orderCountLiveData: MutableLiveData<DataCount> by lazy {
        MutableLiveData<DataCount>()
    }

    val userCodeLiveData: MutableLiveData<GetDbKeyModel> by lazy {
        MutableLiveData<GetDbKeyModel>()
    }

    fun validateUserCode(code: String) {
        setIsLoading(true)

        compositeDisposable.add(dataManager.validateUserCode(code)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe({ this.codeResponse(it) }, { this.handleError(it) })
        )
    }


    fun fetchSetting() {
        setIsLoading(true)

        compositeDisposable.add(dataManager.getSetting()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe({
                    this.validateResponse(it) }, {
                    this.handleError(it) })
        )
    }


    fun fetchOrderCount(accessToken: String) {
        setIsLoading(true)

        compositeDisposable.add(dataManager.getCount(accessToken)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe({ this.orderCountResponse(it) }, { this.handleError(it) })
        )
    }

    private fun orderCountResponse(it: PojoPendingOrders?) {
        setIsLoading(false)

        when (it?.status) {
            NetworkConstants.SUCCESS -> orderCountLiveData.setValue(it.data)
            NetworkConstants.AUTHFAILED -> navigator.onSessionExpire()
            else -> navigator.onErrorOccur(it?.message ?: "")
        }
    }


    private fun validateResponse(it: SettingModel?) {

        setIsLoading(false)

        when (it?.status) {
            NetworkConstants.SUCCESS -> settingLiveData.setValue(it.data)
            NetworkConstants.AUTHFAILED -> navigator.onSessionExpire()
            else -> navigator.onErrorOccur(it?.message ?: "")
        }
    }


    private fun codeResponse(it: GetDbKeyModel?) {

        setIsLoading(false)

        when (it?.statusCode) {
            NetworkConstants.SUCCESS -> {


                if (it.data.data.isNotEmpty()) {

                    dataManager.logout()

                    dataManager.setkeyValue(DataNames.FEATURE_DATA, Gson().toJson(it.data.featureData
                            ?: ""))

                    when (BuildConfig.CLIENT_CODE) {
                        "thelocal_0694" -> {
                            Config.dbSecret = "f659625dc5b3c13128df7f4571f11ee4"
                            dataManager.setkeyValue(PrefenceConstants.DB_SECRET, "f659625dc5b3c13128df7f4571f11ee4")
                        }
                        "roadsideassistance_0649" -> {
                            Config.dbSecret = "c46dbcdba9a5c89726d8e52b47b01b0ea9d5da22ca8b58d3ee8f1c732b01c279"
                            dataManager.setkeyValue(PrefenceConstants.DB_SECRET, "c46dbcdba9a5c89726d8e52b47b01b0ea9d5da22ca8b58d3ee8f1c732b01c279")
                        }
                        else -> {
                            Config.dbSecret = it.data.data[0].cbl_customer_domains[0].db_secret_key
                            dataManager.setkeyValue(PrefenceConstants.DB_SECRET, it.data.data[0].cbl_customer_domains[0].db_secret_key)
                        }
                    }

                    dataManager.setkeyValue(PrefenceConstants.WEBSITE_LINK, it.data.data[0].cbl_customer_domains[0].site_domain)

                    dataManager.setkeyValue(PrefenceConstants.AGENT_DB_SECRET, it.data.data[0].cbl_customer_domains[0].agent_db_secret_key)

                    dataManager.setkeyValue(PrefenceConstants.APP_UNIQUE_CODE, it.data.data[0].uniqueId)

                    dataManager.addGsonValue(PrefenceConstants.DB_INFORMATION, Gson().toJson(it.data.data[0].cbl_customer_domains[0]))

                    val whatsAppNumber=it.data.data[0].whatsapp_country_code +" "+it.data.data[0].whatsapp_phone_number
                    dataManager.addGsonValue(PrefenceConstants.WHATSAPP_PHONE_NUMBER, whatsAppNumber)
                    userCodeLiveData.value = it
                }

                if (!it.data.currency.isNullOrEmpty()) {
                    val defaultCurrency: Currency?
                    defaultCurrency = it.data.currency.find { it.is_default==1 } ?: it.data.currency[0]


                    dataManager.addGsonValue(PrefenceConstants.CURRENCY_INF,
                            Gson().toJson(defaultCurrency))

                    CURRENCY_SYMBOL = defaultCurrency.currency_symbol ?: ""
                    DEFAULT_CURRENCY_SYMBOL= CURRENCY_SYMBOL

                    dataManager.addGsonValue(PrefenceConstants.DEFAULT_CURRENCY_INF,
                            Gson().toJson(defaultCurrency))

                    dataManager.addGsonValue(PrefenceConstants.CURRENCY_LIST,Gson().toJson(it.data.currency))
                }
            }
            NetworkConstants.AUTHFAILED -> navigator.onSessionExpire()
            else -> navigator.onErrorOccur(it?.message ?: "")
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
