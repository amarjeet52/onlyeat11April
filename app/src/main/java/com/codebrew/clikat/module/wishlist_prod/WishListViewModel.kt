package com.codebrew.clikat.module.wishlist_prod

import androidx.databinding.ObservableInt
import androidx.lifecycle.MutableLiveData
import com.codebrew.clikat.base.BaseViewModel
import com.codebrew.clikat.data.DataManager
import com.codebrew.clikat.data.constants.NetworkConstants
import com.codebrew.clikat.data.constants.PrefenceConstants
import com.codebrew.clikat.modal.ExampleCommon
import com.codebrew.clikat.modal.WishListSuppliersModel
import com.codebrew.clikat.modal.other.ProductDataBean
import com.codebrew.clikat.modal.other.SupplierDataBean
import com.codebrew.clikat.modal.other.WishListModel
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers


class WishListViewModel(dataManager: DataManager) : BaseViewModel<WishListNavigator>(dataManager) {


    val wishListLiveData: MutableLiveData<MutableList<ProductDataBean>> by lazy {
        MutableLiveData<MutableList<ProductDataBean>>()
    }

    val supplierWishList: MutableLiveData<MutableList<SupplierDataBean>> by lazy {
        MutableLiveData<MutableList<SupplierDataBean>>()
    }

    val isWishList = ObservableInt(0)


    fun markFavProduct(productId: Int?, favStatus: Int?, position: Int) {
        val mParam = HashMap<String?, String?>()
        mParam["product_id"] = productId.toString()
        mParam["status"] = favStatus.toString()
        dataManager.markWishList(mParam)
                ?.observeOn(AndroidSchedulers.mainThread())
                ?.subscribeOn(Schedulers.io())
                ?.subscribe({ this.validateFavResponse(it, position) }, { this.handleError(it) })?.let {
                    compositeDisposable.add(it)
                }
    }

    private fun validateFavResponse(it: ExampleCommon?, position: Int) {

        if (it?.status == NetworkConstants.SUCCESS) {
            navigator.onFavStatus(position)
        } else {
            it?.message?.let { it1 -> navigator.onErrorOccur(it1) }
        }
    }


    fun getWishlist() {
        setIsLoading(true)

        val hashMap = dataManager.updateUserInf()
        hashMap.remove("languageId")

        hashMap["language_id"] = dataManager.getLangCode()

        compositeDisposable.add(dataManager.getWishlist(hashMap)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe({ this.validateResponse(it) }, { this.handleError(it) })
        )
    }

    fun getSuppliersWishList() {
        setIsLoading(true)

        val hashMap = dataManager.updateUserInf()
        hashMap.remove("languageId")

        hashMap["languageId"] = dataManager.getLangCode()
        hashMap["accessToken"] = dataManager.getKeyValue(PrefenceConstants.ACCESS_TOKEN, PrefenceConstants.TYPE_STRING).toString()

        compositeDisposable.add(dataManager.gwtSuppliersWishList(hashMap)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe({ this.validateSuppliersResponse(it) }, { this.handleError(it) })
        )
    }

    private fun validateResponse(it: WishListModel?) {
        setIsLoading(false)
        setWishList(it?.data?.size ?: 0)
        when (it?.status) {
            NetworkConstants.SUCCESS -> {
                wishListLiveData.setValue(it.data)
            }
            NetworkConstants.AUTHFAILED -> {
                navigator.onSessionExpire()
            }
            else -> {
                navigator.onErrorOccur(it?.message ?: "")
            }
        }
    }

    private fun validateSuppliersResponse(it: WishListSuppliersModel?) {
        setIsLoading(false)
        setWishList(it?.data?.favourites?.size ?: 0)
        when (it?.status) {
            NetworkConstants.SUCCESS -> {
                supplierWishList.setValue(it.data?.favourites)
            }
            NetworkConstants.AUTHFAILED -> {
                navigator.onSessionExpire()
            }
            else -> {
                navigator.onErrorOccur(it?.message ?: "")
            }
        }
    }

    fun unFavSupplier(supplierId: String, position: Int) {
        val param = dataManager.updateUserInf()
        param["supplierId"] = supplierId
        param["accessToken"] = dataManager.getKeyValue(PrefenceConstants.ACCESS_TOKEN, PrefenceConstants.TYPE_STRING).toString()
        compositeDisposable.add(dataManager.unfavSupplier(param)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe({ this.unfavResponse(it, position) }, { this.handleError(it) })
        )
    }

    private fun unfavResponse(it: ExampleCommon?, position: Int) {
        if (it?.status == NetworkConstants.SUCCESS) {
            navigator.onFavStatus(position)
        } else if (it?.status == NetworkConstants.AUTHFAILED) {
            dataManager.setUserAsLoggedOut()
            navigator.onSessionExpire()
        } else {
            it?.message?.let { it1 -> navigator.onErrorOccur(it1) }
        }
    }

    private fun handleError(e: Throwable) {
        setIsLoading(false)
        setWishList(0)
        handleErrorMsg(e).let {
            if (it == NetworkConstants.AUTH_MSG) {
                dataManager.setUserAsLoggedOut()
                navigator.onSessionExpire()
            } else {
                navigator.onErrorOccur(it)
            }
        }
    }


    fun setWishList(wishCount: Int) {
        this.isWishList.set(wishCount)
    }
}
