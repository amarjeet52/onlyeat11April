package com.codebrew.clikat.module.crave_mania

import com.codebrew.clikat.app_utils.DateTimeUtils
import com.codebrew.clikat.app_utils.SingleLiveEvent
import com.codebrew.clikat.base.BaseViewModel
import com.codebrew.clikat.base.PagingResult
import com.codebrew.clikat.data.DataManager
import com.codebrew.clikat.data.constants.NetworkConstants
import com.codebrew.clikat.data.constants.PrefenceConstants
import com.codebrew.clikat.modal.other.Data
import com.codebrew.clikat.modal.other.HomeSupplierPaginationModel
import com.codebrew.clikat.modal.other.SupplierDataBean
import com.codebrew.clikat.module.home_screen.HomeNavigator
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

class CraveManiaViewModelNew (dataManager: DataManager) : BaseViewModel<CraveManiaNavigator>(dataManager) {

    val homeDataLiveData by lazy { SingleLiveEvent<CraveManiaData>() }
    val homeDataAllLiveData by lazy { SingleLiveEvent<List<BannerData>>() }
    fun getSupplierList() {
        setIsLoading(true)
            compositeDisposable.add(dataManager.getCraveBanner()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe({
                    try {
                        this.BannerList(it)
                    }catch (e:Exception)
                    {

                    }},
                        {
                            this.handleError(it)
                        })
        )
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
    fun getSupplierListAll(type:String) {
        setIsLoading(true)
        val param = dataManager.updateUserInf()
        param["sectionId"] = "20"
        param["accessToken"] = dataManager.getKeyValue(PrefenceConstants.ACCESS_TOKEN, PrefenceConstants.TYPE_STRING).toString()

        param["limit"] = "20"

        param["skip"] = "0"

        param["type"] = type?:""


        compositeDisposable.add(dataManager.getCraveManiaNew(param)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe({
                    try {
                        this.supplierList(it)
                    }catch (e:Exception)
                    {

                    }},
                        {
                            this.handleError(it)
                        })
        )
    }
    private fun supplierList(it: CraveManiaModel) {
        setIsLoading(false)
        when (it?.status) {
            NetworkConstants.SUCCESS -> navigator.onSupplierList(it.data)
            NetworkConstants.SUCCESS_CODE -> homeDataLiveData.value=it.data
            NetworkConstants.AUTHFAILED -> {
                dataManager.setUserAsLoggedOut()
                navigator.onSessionExpire()
            }
            else -> it?.message?.let { it1 -> navigator.onErrorOccur(it1.toString()) }
        }
    }

    private fun BannerList(it: CraveManiaBannerModel) {
        setIsLoading(false)
        when (it?.status) {
            NetworkConstants.SUCCESS -> navigator.onBannerList(it.data)
            NetworkConstants.SUCCESS_CODE -> homeDataAllLiveData.value=it.data
            NetworkConstants.AUTHFAILED -> {
                dataManager.setUserAsLoggedOut()
                navigator.onSessionExpire()
            }
            else -> it?.message?.let { it1 -> navigator.onErrorOccur(it1.toString()) }
        }
    }
}
