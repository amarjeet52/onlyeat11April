package com.codebrew.clikat.module.product_detail

import com.codebrew.clikat.app_utils.SingleLiveEvent
import com.codebrew.clikat.base.BaseViewModel
import com.codebrew.clikat.data.DataManager
import com.codebrew.clikat.data.constants.NetworkConstants
import com.codebrew.clikat.data.model.SuccessCustomModel
import com.codebrew.clikat.data.model.api.SuccessModel
import com.codebrew.clikat.data.model.others.ViewProduct
import com.codebrew.clikat.modal.CartInfoServer
import com.codebrew.clikat.modal.CartInfoServerArray
import com.codebrew.clikat.modal.ExampleCommon
import com.codebrew.clikat.modal.other.AddtoCartModel
import com.codebrew.clikat.modal.other.ProductDataBean
import com.codebrew.clikat.modal.other.ProductDetailModel
import com.codebrew.clikat.preferences.DataNames
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import java.util.*
import kotlin.collections.HashMap
import kotlin.collections.set


class ProdDetailViewModel(dataManager: DataManager) : BaseViewModel<ProdDetailNavigator>(dataManager) {

    val productLiveData by lazy { SingleLiveEvent<ProductDataBean>() }

    var prodStatus: Int? = null

    fun viewProduct(productId: Int?) {

        val model= ViewProduct(productId?:0)

        compositeDisposable.add(dataManager.viewProduct(model)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe({ this.modelResponse(it) }, { this.handleError(it) })
        )
    }

    private fun modelResponse(it: SuccessCustomModel?) {
        setIsLoading(false)
        when (it?.statusCode) {
            NetworkConstants.SUCCESS -> {
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


    fun getProductDetail(param: HashMap<String, String>) {
        setIsLoading(true)

        param.putAll(dataManager.updateUserInf())
        compositeDisposable.add(dataManager.getProdDetail(param)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe({ this.validateResponse(it) }, { this.handleError(it) })
        )
    }

    fun addCart(cartInfo: CartInfoServerArray, productList: ArrayList<CartInfoServer>) {
        setIsLoading(true)
        dataManager.getAddToCart(cartInfo)
                ?.observeOn(AndroidSchedulers.mainThread())
                ?.subscribeOn(Schedulers.io())
                ?.subscribe({ this.addCartInfo(it, productList) }, { this.handleError(it) })?.let {
                    compositeDisposable.add(it)
                }
    }


    fun markFavProduct(productId: Int?, favStatus: Int?) {

        prodStatus = favStatus

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

        if (it?.status == NetworkConstants.SUCCESS) {
            navigator.onFavStatus(prodStatus)
        } else {
            it?.message?.let { it1 -> navigator.onErrorOccur(it1) }
        }
    }


    private fun validateResponse(it: ProductDetailModel?) {

        when (it?.status) {
            NetworkConstants.SUCCESS -> {
                productLiveData.value = it.data
            }
            NetworkConstants.AUTHFAILED -> {
                setIsLoading(false)
                dataManager.setUserAsLoggedOut()
                navigator.onSessionExpire()
            }
            else -> {
                setIsLoading(false)
                it?.message?.let { it1 -> navigator.onErrorOccur(it1) }
            }
        }
    }


    private fun addCartInfo(it: AddtoCartModel?, productList: ArrayList<CartInfoServer>) {

        setIsLoading(false)

        when (it?.status) {
            NetworkConstants.SUCCESS -> {
                dataManager.setkeyValue(DataNames.CART_ID, it.data?.cartId ?: "")
                navigator.onCartAdded(it.data, productList, it.message ?: "")
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
