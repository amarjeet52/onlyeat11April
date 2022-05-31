package com.codebrew.clikat.module.all_offers

import androidx.databinding.ObservableInt
import com.codebrew.clikat.app_utils.SingleLiveEvent
import com.codebrew.clikat.base.BaseViewModel
import com.codebrew.clikat.data.DataManager
import com.codebrew.clikat.data.constants.NetworkConstants
import com.codebrew.clikat.modal.ExampleCommon
import com.codebrew.clikat.modal.other.ProductDataBean
import com.codebrew.clikat.modal.other.ViewAllOfferListModel
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers


class OfferProdListViewModel(dataManager: DataManager) : BaseViewModel<OfferProdListNavigator>(dataManager) {

    val offerLiveData by lazy { SingleLiveEvent<MutableList<ProductDataBean>>() }

    val isOfferList = ObservableInt(0)


    fun markFavProduct(productId: Int?, favStatus: Int?) {
        //setIsLoading(true)

        val mParam = HashMap<String?, String?>()
        mParam["product_id"] = productId.toString()
        mParam["status"] = favStatus.toString()
        dataManager.markWishList(mParam)
                ?.observeOn(AndroidSchedulers.mainThread())
                ?.subscribeOn(Schedulers.io())
                ?.subscribe({ this.validateFavResponse(it) }, { this.handleError(it) })?.let {
                    compositeDisposable.add(it)
                }
    }

    fun getOfferList(catId: Int) {
        setIsLoading(true)

        val param = dataManager.updateUserInf()
        if (catId > 0) {
            param["categoryId"] = catId.toString()
        }
        dataManager.getAllOffer(param)
                ?.observeOn(AndroidSchedulers.mainThread())
                ?.subscribeOn(Schedulers.io())
                ?.subscribe({ this.validateResponse(it) }, { this.handleError(it) })?.let {
                    compositeDisposable.add(it)
                }
    }

    private fun validateFavResponse(it: ExampleCommon?) {

        //setIsLoading(false)

        if (it?.status == NetworkConstants.SUCCESS) {
            navigator.onFavStatus()
        } else {
            it?.message?.let { it1 -> navigator.onErrorOccur(it1) }
        }
    }


    private fun validateResponse(it: ViewAllOfferListModel?) {
        setOfferList(it?.data?.list?.size ?: 0)
        setIsLoading(false)
        when (it?.status) {
            NetworkConstants.SUCCESS -> {
                offerLiveData.value = it.data.list
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
        this.isOfferList.set(offerCount)
    }

}
