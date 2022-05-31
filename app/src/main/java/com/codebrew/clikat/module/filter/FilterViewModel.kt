package com.codebrew.clikat.module.filter

import androidx.lifecycle.MutableLiveData
import com.codebrew.clikat.app_utils.SingleLiveEvent
import com.codebrew.clikat.base.BaseViewModel
import com.codebrew.clikat.data.DataManager
import com.codebrew.clikat.data.constants.NetworkConstants
import com.codebrew.clikat.data.constants.PrefenceConstants
import com.codebrew.clikat.data.model.api.ProductData
import com.codebrew.clikat.data.model.api.ProductListModel
import com.codebrew.clikat.data.model.others.FilterInputModel
import com.codebrew.clikat.modal.other.*
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

class FilterViewModel(dataManager: DataManager) : BaseViewModel<FilterNavigator>(dataManager) {


    val productLiveData: MutableLiveData<ProductData> by lazy {
        MutableLiveData<ProductData>()
    }

    val categoryLiveData by lazy { SingleLiveEvent<MutableList<English>>() }

    val varientCatLiveData by lazy { SingleLiveEvent<FilterVarientCatModel>() }

    val subCatLiveData by lazy { SingleLiveEvent<SubCatData>() }


    fun getProductList(inputModel: FilterInputModel) {
        setIsLoading(true)

        dataManager.getProductFilter(inputModel)
                .observeOn(AndroidSchedulers.mainThread())
                ?.subscribeOn(Schedulers.io())
                ?.subscribe({ this.validateResponse(it) }, { this.handleError(it) })?.let {
                    compositeDisposable.add(it)
                }
    }

    fun getCategories() {
        setIsLoading(true)
        val param = dataManager.updateUserInf()
        param["accessToken"] = dataManager.getKeyValue(PrefenceConstants.ACCESS_TOKEN, PrefenceConstants.TYPE_STRING).toString()
        compositeDisposable.add(dataManager.getAllCategoryNew(param)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe({ this.categoryResponse(it) }, { this.handleError(it) })
        )
    }


    fun getVarientByCategory(catId: String) {
        setIsLoading(true)

        dataManager.getCategoryVarient(catId)
                ?.observeOn(AndroidSchedulers.mainThread())
                ?.subscribeOn(Schedulers.io())
                ?.subscribe({ this.varientCatResponse(it) }, { this.handleError(it) })?.let {
                    compositeDisposable.add(it)
                }
    }


    fun getSubCategory(param: HashMap<String, String>) {
        setIsLoading(true)

        compositeDisposable.add(dataManager.getSubCategory(param)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe({ this.subCategoryResponse(it) }, { this.handleError(it) })
        )
    }


    private fun subCategoryResponse(it: SubCategoryListModel?) {
        setIsLoading(false)
        when (it?.status) {
            NetworkConstants.SUCCESS -> {
                subCatLiveData.value = it.data
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


    private fun varientCatResponse(it: FilterVarientCatModel?) {
        setIsLoading(false)
        when (it?.status) {
            NetworkConstants.SUCCESS -> {
                varientCatLiveData.value = it
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


    private fun categoryResponse(it: CategoryListModel?) {
        setIsLoading(false)
        when (it?.status) {
            NetworkConstants.SUCCESS -> {
                categoryLiveData.value = it.data?.english ?: mutableListOf()
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


    private fun validateResponse(it: ProductListModel?) {

        setIsLoading(false)

        when (it?.status) {
            NetworkConstants.SUCCESS -> {
                productLiveData.value = it.data
            }
            NetworkConstants.AUTHFAILED -> {
                navigator.onSessionExpire()
            }
            else -> {
                navigator.onErrorOccur(it?.message ?: "")
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