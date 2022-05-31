package com.codebrew.clikat.module.rental.carDetail

import com.codebrew.clikat.app_utils.SingleLiveEvent
import com.codebrew.clikat.base.BaseViewModel
import com.codebrew.clikat.data.DataManager
import com.codebrew.clikat.data.constants.NetworkConstants
import com.codebrew.clikat.data.constants.PrefenceConstants
import com.codebrew.clikat.data.model.others.HomeRentalParam
import com.codebrew.clikat.data.model.others.UpdateCartParam
import com.codebrew.clikat.modal.CartInfoServer
import com.codebrew.clikat.modal.CartInfoServerArray
import com.codebrew.clikat.modal.ExampleCommon
import com.codebrew.clikat.modal.other.AddtoCartModel
import com.codebrew.clikat.modal.other.ProductDataBean
import com.codebrew.clikat.modal.other.ProductDetailModel
import com.codebrew.clikat.preferences.DataNames
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers


class CarDetailViewModel(dataManager: DataManager) : BaseViewModel<CarDetailNavigator>(dataManager) {

    val productDetailLiveData by lazy { SingleLiveEvent<ProductDataBean>() }

    fun getProductDetail(productId: String?, supplierBranchId: String?) {
        setIsLoading(true)

        val mParam = dataManager.updateUserInf()
        mParam["productId"] = productId ?: ""
        mParam["supplierBranchId"] = supplierBranchId ?: ""
        mParam["offer"] = ""
        if (dataManager.getCurrentUserLoggedIn()) {
            mParam["accessToken"] = dataManager.getKeyValue(PrefenceConstants.ACCESS_TOKEN, PrefenceConstants.TYPE_STRING).toString()
        }

        compositeDisposable.add(dataManager.getProductDetail(mParam)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe({ this.validateResponse(it) }, { this.handleError(it) })
        )
    }


    private fun validateResponse(it: ProductDetailModel?) {
        setIsLoading(false)
        if (it?.status == NetworkConstants.SUCCESS) {
            productDetailLiveData.value = it.data
        } else if (it?.status == NetworkConstants.AUTHFAILED) {
            dataManager.setUserAsLoggedOut()
            navigator.onSessionExpire()
        } else {
            it?.message?.let { it1 -> navigator.onErrorOccur(it1) }
        }
    }


    fun updateCartInfo(param: HomeRentalParam) {
        setIsLoading(true)

        val updateCart = UpdateCartParam()

        with(updateCart)
        {
            accessToken= dataManager.getKeyValue(PrefenceConstants.ACCESS_TOKEN, PrefenceConstants.TYPE_STRING).toString()
            cartId=dataManager.getKeyValue(DataNames.CART_ID, PrefenceConstants.TYPE_STRING).toString().toInt()
            deliveryType=DataNames.DELIVERY_TYPE_STANDARD.toString()
            deliveryId="0"
            handlingAdmin="0"
            handlingSupplier="0"
            urgentPrice="0"
            netAmount=param.totalAmt.toDoubleOrNull()
            currencyId="1"
            languageId= dataManager.getLangCode()

            val deliveryTime = param.booking_to_date.split(" ")
            deliveryDate = deliveryTime[0]
            this.deliveryTime = deliveryTime[1]
        }

        compositeDisposable.add(dataManager.updateCartInfo(updateCart)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe({ this.updateCart(it) }, { this.handleError(it) })
        )
    }


    private fun updateCart(it: ExampleCommon?) {

        setIsLoading(false)

        if (it?.status == NetworkConstants.SUCCESS) {
            navigator.updateCart()
        } else {
            it?.message?.let { it1 -> navigator.onErrorOccur(it1) }
        }
    }


    fun addCart(supplierBranchId: Int, productList: MutableList<CartInfoServer>) {
        setIsLoading(true)

        val cartInfoServerArray = CartInfoServerArray()
        cartInfoServerArray.productList = productList
        cartInfoServerArray.accessToken = dataManager.getKeyValue(PrefenceConstants.ACCESS_TOKEN, PrefenceConstants.TYPE_STRING).toString()

        cartInfoServerArray.cartId = "0"
        cartInfoServerArray.supplierBranchId = supplierBranchId

        dataManager.getAddToCart(cartInfoServerArray)
                ?.observeOn(AndroidSchedulers.mainThread())
                ?.subscribeOn(Schedulers.io())
                ?.subscribe({ this.addCartInfo(it) }, { this.handleError(it) })?.let {
                    compositeDisposable.add(it)
                }
    }


    fun markFavProduct(productId: String?, favStatus: String?) {
        setIsLoading(true)

        val mParam = HashMap<String?, String?>()
        mParam["product_id"] = productId
        mParam["status"] = favStatus
        dataManager.markWishList(mParam)
                ?.observeOn(AndroidSchedulers.mainThread())
                ?.subscribeOn(Schedulers.io())
                ?.subscribe({ this.validateFavResponse(it) }, { this.handleError(it) })?.let {
                    compositeDisposable.add(it)
                }
    }

    private fun validateFavResponse(it: ExampleCommon?) {

        setIsLoading(false)

        if (it?.status == NetworkConstants.SUCCESS) {
            navigator.onFavStatus()
        } else {
            it?.message?.let { it1 -> navigator.onErrorOccur(it1) }
        }
    }


    private fun addCartInfo(it: AddtoCartModel?) {

        setIsLoading(false)

        if (it?.status == NetworkConstants.SUCCESS) {
            dataManager.setkeyValue(DataNames.CART_ID, it.data?.cartId ?: "")

            it.data?.let { it1 -> navigator.addCart(it1) }
        } else {
            it?.message?.let { it1 -> navigator.onErrorOccur(it1) }
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
