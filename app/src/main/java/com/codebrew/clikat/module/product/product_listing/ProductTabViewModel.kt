package com.codebrew.clikat.module.product.product_listing

import androidx.databinding.ObservableBoolean
import androidx.lifecycle.MutableLiveData
import com.codebrew.clikat.app_utils.SingleLiveEvent
import com.codebrew.clikat.base.BaseViewModel
import com.codebrew.clikat.base.PagingResult
import com.codebrew.clikat.data.DataManager
import com.codebrew.clikat.data.constants.AppConstants
import com.codebrew.clikat.data.constants.NetworkConstants
import com.codebrew.clikat.data.model.api.ProductData
import com.codebrew.clikat.data.model.api.ProductListModel
import com.codebrew.clikat.data.model.api.orderDetail.OrderDetailModel
import com.codebrew.clikat.data.model.others.EditOrderRequest
import com.codebrew.clikat.data.model.others.FilterInputModel
import com.codebrew.clikat.modal.ExampleCommon
import com.codebrew.clikat.module.product.ProductNavigator
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import timber.log.Timber

class ProductTabViewModel(dataManager: DataManager) : BaseViewModel<ProductNavigator>(dataManager) {

    var isViewType = ObservableBoolean(true)

    var offset: Int? = 0
    private var isLastReceived = false
    fun validForPaging(): Boolean = !isLoading.get() && !isLastReceived


    val productLiveData by lazy {
        SingleLiveEvent<PagingResult<ProductData>>()
    }

    fun getProductList(param: FilterInputModel, firstPage: Boolean) {
        setIsLoading(true)

        if (firstPage) {
            offset = 0
            isLastReceived = false
        }

        param.offset=offset
        param.limit= AppConstants.LIMIT

        compositeDisposable.add(dataManager.getProductFilter(param)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe({ this.validateResponse(it,firstPage) }, { this.handleError(it) })
        )
    }

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


    private fun validateFavResponse(it: ExampleCommon?) {

        //setIsLoading(false)

        if (it?.status == NetworkConstants.SUCCESS) {
            navigator.onFavStatus()
        } else {
            it?.message?.let { it1 -> navigator.onErrorOccur(it1) }
        }
    }


    private fun validateResponse(it: ProductListModel?, firstPage: Boolean) {

        setIsLoading(false)

        when (it?.status) {
            NetworkConstants.SUCCESS -> {

                if (it.data?.product?.count()?:0< AppConstants.LIMIT) {
                    Timber.i("Last group is received")
                    isLastReceived = true
                } else {
                    Timber.i("Next page for topic groups is available")
                    offset = offset?.plus(AppConstants.LIMIT)
                }
                productLiveData.value = PagingResult(firstPage, it.data)
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

    fun setViewType(viewType: Boolean) {
        this.isViewType.set(viewType)
    }

    fun editOrderProducts(orderRequest: EditOrderRequest) {
        setIsLoading(true)
        compositeDisposable.add(
                dataManager.apiEditOrder(orderRequest)
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeOn(Schedulers.io())
                        .subscribe({ this.validateEditOrderResponse(it) },
                                { this.handleError(it) })
        )
    }

    private fun validateEditOrderResponse(it: OrderDetailModel?) {
        setIsLoading(false)

        if (it?.status == NetworkConstants.SUCCESS) {
            navigator.editOrderResponse(it.data)
        } else if (it?.status == NetworkConstants.AUTHFAILED) {
            dataManager.setUserAsLoggedOut()
            navigator.onSessionExpire()
        } else {
            it?.message?.let { it1 -> navigator.onErrorOccur(it1) }
        }
    }
}