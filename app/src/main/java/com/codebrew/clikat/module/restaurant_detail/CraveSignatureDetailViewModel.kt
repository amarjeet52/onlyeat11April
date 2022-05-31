package com.codebrew.clikat.module.restaurant_detail

import androidx.databinding.ObservableBoolean
import androidx.databinding.ObservableInt
import com.codebrew.clikat.app_utils.SingleLiveEvent
import com.codebrew.clikat.base.BaseViewModel
import com.codebrew.clikat.base.PagingResult
import com.codebrew.clikat.data.DataManager
import com.codebrew.clikat.data.constants.AppConstants
import com.codebrew.clikat.data.constants.NetworkConstants
import com.codebrew.clikat.data.constants.NetworkConstants.Companion.SUCCESS_CODE
import com.codebrew.clikat.data.constants.PrefenceConstants
import com.codebrew.clikat.modal.other.*
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import timber.log.Timber


class CraveSignatureDetailViewModel(dataManager: DataManager) : BaseViewModel<RestDetailNavigator>(dataManager) {

    val supplierLiveData by lazy { SingleLiveEvent<PagingResult<SuplierProdListModel>>() }
    val subCatLiveData by lazy { SingleLiveEvent<SubCatData>() }
    val tableCapacityLiveData by lazy { SingleLiveEvent<ArrayList<String>>() }
    val categoryLiveData by lazy { SingleLiveEvent<ArrayList<ProductBean>>() }

    val prescLiveData by lazy { SingleLiveEvent<String>() }

    val isSupplierProdCount = ObservableInt(0)
    val isCategoryId = ObservableInt(0)
    var skip: Int? = 0
    private var isLastReceived = false
    var showWhiteScreen = ObservableBoolean(true)
    fun validForPaging(): Boolean = !isLoading.get() && !isLastReceived

    val isContentProgressBarLoading = ObservableBoolean(false)

    fun setIsContentProgressBarLoading(isLoading: Boolean) {
        this.isContentProgressBarLoading.set(isLoading)
    }

    fun getProductList(supplierId: String, isFirstPage: Boolean, settingBean: SettingModel.DataBean.SettingData?, categoryId: String?,
                       is_non_veg: String? = null, showShimmerLoading: Boolean = true, type: String) {
        if (isFirstPage) {
            skip = 0
            isLastReceived = false
        }

        val param = dataManager.updateUserInf()
        param["supplier_id"] = supplierId
      //  param["type"] = type

        compositeDisposable.add(  dataManager.getProductLst(param)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe({ this.validateResponse(it, isFirstPage) }, {
                    this.handleError(it)
                })
        )
    }

    fun getSignaturePlates(supplierId: String,sectionId:String,accessToken:String) {

        val param = dataManager.updateUserInf()
        param["supplier_id"] = supplierId
        param["sectionId"] = sectionId
        param["accessToken"] = accessToken


        compositeDisposable.add(  dataManager.getSignaturePlates(param)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe({ this.validateResponse(it) }, {
                    this.handleError(it)
                })
        )
    }

    private fun validateResponse(it: SuplierProdListModel?) {
        when (it?.status) {
            NetworkConstants.SUCCESS -> {
                supplierLiveData.value =  PagingResult(true, it)
            }

            SUCCESS_CODE -> {
                supplierLiveData.value =  PagingResult(true, it)
            }
            NetworkConstants.AUTHFAILED -> {
                dataManager.setUserAsLoggedOut()
                navigator.onSessionExpire()
            }
            else -> {
                it?.message?.let { it1 ->
                    navigator.onErrorOccur(it1.toString()?:"")
                }
            }
        }
    }

    fun getSignatureMenu(supplierId: String,sectionId:String,accessToken:String) {

        val param = dataManager.updateUserInf()
        param["supplier_id"] = supplierId
        param["sectionId"] = "2"
        param["accessToken"] = dataManager.getKeyValue(PrefenceConstants.ACCESS_TOKEN, PrefenceConstants.TYPE_STRING).toString()



        compositeDisposable.add(  dataManager.getSignatureMenu(param)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe({ this.validateResponse(it) }, {
                    this.handleError(it)
                })
        )
    }

    private fun validateResponse(it: SuplierProdListModel?, firstPage: Boolean) {
        setIsLoading(false)
        setIsContentProgressBarLoading(false)
        if (firstPage)
            isSupplierProdCount.set(it?.data?.product?.count() ?: 0)
        when (it?.status) {
            NetworkConstants.SUCCESS -> {
                val receivedGroups = it.data?.product?.sumBy { it.value?.count() ?: 0 }
                if ((receivedGroups ?: 0) < AppConstants.LIMIT) {
                    Timber.i("Last group is received")
                    isLastReceived = true
                } else {
                    Timber.i("Next page for topic groups is available")
                    skip = skip?.plus(AppConstants.LIMIT)
                }

                supplierLiveData.value = PagingResult(firstPage, it)
            }
            NetworkConstants.AUTHFAILED -> {
                dataManager.setUserAsLoggedOut()
                navigator.onSessionExpire()
            }
            else -> {
                it?.message?.let { it1 ->
                    navigator.onErrorOccur(it1.toString()?:"")
                }
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
