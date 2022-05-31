package com.codebrew.clikat.module.payment_gateway.savedcards

import androidx.databinding.ObservableInt
import com.codebrew.clikat.app_utils.SingleLiveEvent
import com.codebrew.clikat.base.BaseViewModel
import com.codebrew.clikat.data.DataManager
import com.codebrew.clikat.data.constants.NetworkConstants
import com.codebrew.clikat.data.constants.PrefenceConstants
import com.codebrew.clikat.data.model.api.*
import com.codebrew.clikat.data.model.others.SaveCardInputModel
import com.codebrew.clikat.data.network.ApiResponse
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import org.json.JSONArray
import org.json.JSONObject
import java.lang.reflect.Type


class SavedCardsViewModel(dataManager: DataManager) : BaseViewModel<SavedCardsNavigator>(dataManager) {

    val savedCardLiveData by lazy { SingleLiveEvent<List<SavedCardList>>() }
    val savedCardListData by lazy { SingleLiveEvent<Data1>() }

    val isCardCount = ObservableInt(0)

    val addCardLiveData by lazy { SingleLiveEvent<AddCardResponseData>() }

    fun saveCard(param: SaveCardInputModel?) {
        setIsLoading(true)

        compositeDisposable.add(dataManager.saveCard(param)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe({ this.saveCardResp(it) }, { this.handleError(it) })
        )
    }


    private fun saveCardResp(it: ApiResponse<AddCardResponseData>) {

        setIsLoading(false)

        if (it.status == NetworkConstants.SUCCESS) {
            addCardLiveData.value = it.data
        } else if (it.status == NetworkConstants.AUTHFAILED) {
            navigator.onSessionExpire()
        } else {
            navigator.onErrorOccur(it.msg.toString())
        }
    }



    fun getSaveCardList(gateway_unique_id: String) {
        setIsLoading(true)


        val hashMap = hashMapOf("gateway_unique_id" to gateway_unique_id)

        if (dataManager.getKeyValue(PrefenceConstants.CUSTOMER_PAYMENT_ID, PrefenceConstants.TYPE_STRING).toString().isNotEmpty()) {
            hashMap["customer_payment_id"] = dataManager.getKeyValue(PrefenceConstants.CUSTOMER_PAYMENT_ID, PrefenceConstants.TYPE_STRING).toString()
        }

        if (gateway_unique_id == "squareup") {
            compositeDisposable.add(dataManager.getSquareCardList(hashMap)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeOn(Schedulers.io())
                    .subscribe({ this.validateSquare(it) }, { this.handleError(it) })
            )
        } else if(gateway_unique_id=="payuLatam"){
            compositeDisposable.add(dataManager.getPayULatamCardList(hashMap)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeOn(Schedulers.io())
                    .subscribe({ this.validatePayULatam(it) }, { this.handleError(it) })
            )
        }else {
            compositeDisposable.add(dataManager.getStripeCardList(hashMap)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeOn(Schedulers.io())
                    .subscribe({ this.validateStripe(it) }, { this.handleError(it) })
            )
        }
    }

    private fun validatePayULatam(it: SavedCardData) {
        setIsLoading(false)
        setCardCount(0)

        when (it.status) {
            NetworkConstants.SUCCESS -> {
                setCardCount(it.data.creditCards?.count()?:0)
                savedCardLiveData.value = it.data.creditCards?: mutableListOf()
            }
            NetworkConstants.AUTHFAILED -> {
                navigator.onSessionExpire()
            }
            else -> {
                navigator.onErrorOccur(it.message ?: "")
            }

        }
    }

    private fun validateStripe(it: ApiResponse<SavedData>?) {

        setIsLoading(false)
        setCardCount(0)

        when (it?.status) {
            NetworkConstants.SUCCESS -> {
                if (!(it.data?.data.isNullOrEmpty())) {
                    setCardCount(it.data?.data?.count() ?: 0)
                    savedCardLiveData.value = it.data?.data
                }
            }
            NetworkConstants.AUTHFAILED -> {
                navigator.onSessionExpire()
            }
            else -> {
                navigator.onErrorOccur(it?.msg.toString())
            }

        }
    }




    private fun validateSquare(it: ApiResponse<List<SavedCardList>>?) {
        setIsLoading(false)
        setCardCount(0)

        when (it?.status) {
            NetworkConstants.SUCCESS -> {

                setCardCount(it.data?.count() ?: 0)
                savedCardLiveData.value = it.data
            }
            NetworkConstants.AUTHFAILED -> {
                navigator.onSessionExpire()
            }
            else -> {
                navigator.onErrorOccur(it?.msg.toString())
            }
        }
    }


    fun deleteSavedCard(card_id: String, gateway_unique_id: String) {
        setIsLoading(true)

        val hashMap = hashMapOf("customer_payment_id" to dataManager.getKeyValue(PrefenceConstants.CUSTOMER_PAYMENT_ID, PrefenceConstants.TYPE_STRING).toString(),
                "card_id" to card_id, "gateway_unique_id" to gateway_unique_id
        )

        compositeDisposable.add(dataManager.deleteSavedCard(hashMap)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe({ this.validateDeleteCard(it) }, { this.handleError(it) })
        )
    }


    private fun validateResponse(it: ApiResponse<Any>) {

        setIsLoading(false)
        setCardCount(0)

        when (it.status) {
            NetworkConstants.SUCCESS -> {
                when (it.data) {
                    is List<*> -> {
                        val listType: Type = object : TypeToken<List<SavedCardList?>?>() {}.type
                        val jsonObj = JSONArray(it.data)
                        val cardLIst: List<SavedCardList> = Gson().fromJson(jsonObj.toString(), listType)

                        setCardCount(cardLIst.count())
                        savedCardLiveData.value = cardLIst
                    }
                    else -> {
                        // setCardCount(0)
                        val listType: Type = object : TypeToken<List<SavedCardList?>?>() {}.type
                        val jsonObj = JSONObject(it.data.toString())
                        val cardLIst: List<SavedCardList> = Gson().fromJson(jsonObj.getJSONArray("data").toString(), listType)

                        setCardCount(cardLIst.count())
                        savedCardLiveData.value = cardLIst
                    }
                }
            }
            NetworkConstants.AUTHFAILED -> {
                navigator.onSessionExpire()
            }
            else -> {
                navigator.onErrorOccur(it.msg.toString())
            }
        }
    }

    private fun validateDeleteCard(it: ApiResponse<Any>) {

        setIsLoading(false)

        when (it.status) {
            NetworkConstants.SUCCESS -> {
                navigator.onDeleteCard()
            }
            NetworkConstants.AUTHFAILED -> {
                navigator.onSessionExpire()
            }
            else -> {
                navigator.onErrorOccur(it.msg.toString())
            }
        }
    }

    private fun setCardCount(count: Int) {
        this.isCardCount.set(count)
    }


    private fun handleError(e: Throwable) {
        setCardCount(0)
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