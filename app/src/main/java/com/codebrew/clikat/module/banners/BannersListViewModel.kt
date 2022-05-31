package com.codebrew.clikat.module.banners

import androidx.databinding.ObservableInt
import com.codebrew.clikat.app_utils.SingleLiveEvent
import com.codebrew.clikat.base.BaseViewModel
import com.codebrew.clikat.data.DataManager
import com.codebrew.clikat.data.constants.NetworkConstants
import com.codebrew.clikat.modal.ExampleSupplierDetail
import com.codebrew.clikat.modal.other.*
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers


class BannersListViewModel(dataManager: DataManager) : BaseViewModel<BannersListNavigator>(dataManager) {

    val bannersLiveData by lazy { SingleLiveEvent<List<TopBanner>>() }

    val isBannerList = ObservableInt(0)


    fun getBannersList() {
        setIsLoading(true)

        val param = dataManager.updateUserInf()

        dataManager.getAllBannersList(param)
                ?.observeOn(AndroidSchedulers.mainThread())
                ?.subscribeOn(Schedulers.io())
                ?.subscribe({ this.validateResponse(it) }, { this.handleError(it) })?.let {
                    compositeDisposable.add(it)
                }
    }


    private fun validateResponse(it: BannersListModel?) {
        setOfferList(it?.data?.size ?: 0)
        setIsLoading(false)
        when (it?.status) {
            NetworkConstants.SUCCESS -> {
                bannersLiveData.value = it.data
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


    fun fetchSupplierDetail(branch_id: Int, catId: Int, supplier_id: Int?) {
        setIsLoading(true)

        val hashMap = dataManager.updateUserInf()


        hashMap["supplierId"] = supplier_id.toString()
        hashMap["branchId"] = branch_id.toString()
        hashMap["categoryId"] = catId.toString()


        compositeDisposable.add(dataManager.getSupplierDetails(hashMap)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe({ this.validateSupplierResponse(it, supplier_id, catId, branch_id) }, { this.handleError(it) })
        )
    }

    private fun validateSupplierResponse(it: ExampleSupplierDetail?, supplierId: Int?, catId: Int, branchId: Int) {
        setIsLoading(false)

        when (it?.status) {
            NetworkConstants.SUCCESS -> {
                val data = it.data
                data.branchId = branchId
                data.supplier_id = supplierId
                data.category_id = catId
                navigator.supplierDetailSuccess(it.data)
            }

            else ->
                it?.message?.let { it1 -> navigator.onErrorOccur(it1) }

        }
    }
    private fun handleError(e: Throwable) {
        setOfferList(0)
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


    fun setOfferList(offerCount: Int) {
        this.isBannerList.set(offerCount)
    }

}
