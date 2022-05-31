package com.codebrew.clikat.module.custom_home

import com.codebrew.clikat.BuildConfig
import com.codebrew.clikat.app_utils.SingleLiveEvent
import com.codebrew.clikat.base.BaseViewModel
import com.codebrew.clikat.data.DataManager
import com.codebrew.clikat.data.constants.NetworkConstants
import com.codebrew.clikat.data.constants.PrefenceConstants
import com.codebrew.clikat.modal.other.*
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

class CustomHomeViewModel(dataManager: DataManager) : BaseViewModel<CusHomeNavigator>(dataManager) {

    val homeDataLiveData by lazy { SingleLiveEvent<Data>() }
    val offersLiveData by lazy { SingleLiveEvent<OfferDataBean>() }
    val supplierLiveData by lazy { SingleLiveEvent<List<SupplierInArabicBean>>() }
    fun getCategories() {
        setIsLoading(true)

        val param = dataManager.updateUserInf()
        param["accessToken"] = dataManager.getKeyValue(PrefenceConstants.ACCESS_TOKEN, PrefenceConstants.TYPE_STRING).toString()
        compositeDisposable.add(dataManager.getAllCategoryNew(param)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe({ this.validateResponse(it) }, { this.handleError(it) })
        )
    }

    fun getOfferList() {
        setIsLoading(true)

        val param = dataManager.updateUserInf()
        param["accessToken"] = dataManager.getKeyValue(PrefenceConstants.ACCESS_TOKEN, PrefenceConstants.TYPE_STRING).toString()
        compositeDisposable.add(dataManager.getOfferList(param)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe({ this.handleOffers(it) }, { this.handleError(it) })
        )
    }

    fun getSupplierList(selfPick: String?, sort_by: String?) {

        setIsLoading(BuildConfig.CLIENT_CODE != "deliverfy_0574")

        val param = dataManager.updateUserInf()
        if (selfPick == "3") {
            /**to call dine in restaurants*/
            param["is_dine_in"] = "1"
        } else {
            param["self_pickup"] = selfPick ?: "0"
        }

        if (dataManager.getKeyValue(PrefenceConstants.CATEGORY_ID, PrefenceConstants.TYPE_STRING).toString().isNotEmpty()) {
            param["categoryId"] = dataManager.getKeyValue(PrefenceConstants.CATEGORY_ID, PrefenceConstants.TYPE_STRING).toString()
        }
        if (sort_by != null) {
            param["sort_by"] = sort_by.toString()
        }

        compositeDisposable.add(dataManager.getSupplierListClikat(param)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe({ this.handleSuppliers(it) }, { this.handleError(it) })
        )
    }

    private fun handleSuppliers(it: ResponseSuppliersList?) {
        setIsLoading(false)
        when (it?.status) {
            NetworkConstants.SUCCESS -> {
                supplierLiveData.value = it.data
            }
            NetworkConstants.AUTHFAILED -> {
                dataManager.setUserAsLoggedOut()
                navigator.onSessionExpire()
            }
            else -> {
                it?.message?.let { it1 -> navigator.onErrorOccur(it1.toString()) }
            }
        }
    }
    private fun handleOffers(it: OfferListModel?) {
        setIsLoading(false)
        when (it?.status) {
            NetworkConstants.SUCCESS -> offersLiveData.value = it.data
            NetworkConstants.AUTHFAILED -> {
                dataManager.setUserAsLoggedOut()
                navigator.onSessionExpire()
            }
            else -> it?.message?.let { it1 -> navigator.onErrorOccur(it1) }
        }
    }

    private fun validateResponse(it: CategoryListModel?) {
        setIsLoading(false)
        when (it?.status) {
            NetworkConstants.SUCCESS -> {
                homeDataLiveData.value = it.data
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
