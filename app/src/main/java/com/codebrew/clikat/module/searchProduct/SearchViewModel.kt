package com.codebrew.clikat.module.searchProduct

import androidx.databinding.ObservableBoolean
import androidx.databinding.ObservableInt
import com.codebrew.clikat.app_utils.DateTimeUtils
import com.codebrew.clikat.app_utils.SingleLiveEvent
import com.codebrew.clikat.base.BaseViewModel
import com.codebrew.clikat.base.PagingResult
import com.codebrew.clikat.data.DataManager
import com.codebrew.clikat.data.constants.AppConstants
import com.codebrew.clikat.data.constants.NetworkConstants
import com.codebrew.clikat.data.constants.PrefenceConstants
import com.codebrew.clikat.data.model.api.ProductData
import com.codebrew.clikat.data.model.api.ProductListModel
import com.codebrew.clikat.data.model.others.FilterInputModel
import com.codebrew.clikat.modal.ExampleCommon
import com.codebrew.clikat.modal.other.*
import com.codebrew.clikat.utils.ClikatConstants
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import timber.log.Timber


class SearchViewModel(dataManager: DataManager) : BaseViewModel<SearchNavigator>(dataManager) {

    val homeDataLiveData by lazy { SingleLiveEvent<Data>() }
    val isSearchHist = ObservableBoolean(false)
    val supplierCount = ObservableInt(0)
    val productCount = ObservableInt(0)
    //true for rest, false for prod

    val supplierLiveData by lazy { SingleLiveEvent<List<SupplierDataBean>>() }

    val productLiveData by lazy {
        SingleLiveEvent<PagingResult<ProductData>>()
    }

    var offset: Int? = 0
    private var isLastReceived = false
    fun validForPaging(): Boolean = !isLoading.get() && !isLastReceived

    fun setIsSearchHist(param: Boolean) {
        this.isSearchHist.set(param)
    }


    fun getProductList(filterInput: FilterInputModel, firstPage: Boolean) {

        if (firstPage) {
            offset = 0
            isLastReceived = false
            setIsLoading(true)
        }

        filterInput.offset = offset
        filterInput.limit = AppConstants.LIMIT

        compositeDisposable.add(dataManager.getProductFilter(filterInput)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe({ this.validateResponse(it, firstPage) }, { this.handleError(it) })
        )
    }

    fun getCategories(catId: Int) {
        setIsLoading(true)

        val param = dataManager.updateUserInf()
        param["accessToken"] = dataManager.getKeyValue(PrefenceConstants.ACCESS_TOKEN, PrefenceConstants.TYPE_STRING).toString()
        if (catId > 0) {
            param["categoryId"] = catId.toString()
        }

        compositeDisposable.add(dataManager.getAllCategoryNew(param)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe({ this.validateCategoryResponse(it) },
                        { this.handleError(it) })
        )
    }


    fun getSupplierList(resturant: String?, categoryId: String? = null,self_pickup:String?="") {
        setIsLoading(true)

        val param = dataManager.updateUserInf()
//        if (dataManager.getLangCode().isNotBlank() && dataManager.getLangCode() == ClikatConstants.LANGUAGE_ENGLISH.toString()) {
            param["search"] = resturant ?: ""
//        }
        param["skip"] = "0"
        param["limit"] = "500"
        param["self_pickup"] = "8" ?: ""
        param["offset"] = DateTimeUtils.timeOffset?: ""

        param["access_token"] = dataManager.getKeyValue(PrefenceConstants.ACCESS_TOKEN, PrefenceConstants.TYPE_STRING).toString()

//        if (!categoryId.isNullOrEmpty() && categoryId != "0")
            param["categoryId"] = categoryId?:""

        val apiHelper =  if(self_pickup.equals("8")) {
                dataManager.getSupplierList6(param)
            }else{
                dataManager.getSupplierList7(param)
            }


        apiHelper.observeOn(AndroidSchedulers.mainThread())
                ?.subscribeOn(Schedulers.io())
                ?.subscribe({ this.handleSuppliers(it, resturant ?: "") }, { this.handleError(it) })?.let {
                    compositeDisposable.add(it)
                }
    }





    private fun handleSuppliers(it: SearchSupplierModel?, keyword: String) {
        setIsLoading(false)
        setIsList(it?.data?.list?.size ?: 0)
        setSupplierCount(it?.data?.list?.size ?: 0)
        when (it?.status) {
            NetworkConstants.SUCCESS -> {
//                val filterList = if (dataManager.getLangCode().isNotBlank() && dataManager.getLangCode() == ClikatConstants.LANGUAGE_OTHER.toString()) {
//                    it.data?.list?.filter { keyword.contains(it.name_arabic ?: "") }
//                } else {
//                    it.data?.list
//                }

                supplierLiveData.value =  it.data?.list
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

        if (it?.status == NetworkConstants.SUCCESS) {
            navigator.onFavStatus()
        } else {
            it?.message?.let { it1 -> navigator.onErrorOccur(it1) }
        }
    }


    private fun validateResponse(it: ProductListModel?, firstPage: Boolean) {
        setIsLoading(false)
        setIsList(0)
        setProductCount(it?.data?.product?.count() ?: 0)
        when (it?.status) {
            NetworkConstants.SUCCESS -> {
                if (it.data?.product?.count() ?: 0 < AppConstants.LIMIT) {
                    Timber.i("Last group is received")
                    isLastReceived = true
                } else {
                    Timber.i("Next page for topic groups is available")
                    offset = offset?.plus(AppConstants.LIMIT)
                }
                setIsList(it.data?.product?.count() ?: 0)
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
        setIsList(0)
        handleErrorMsg(e).let {
            if (it == NetworkConstants.AUTH_MSG) {
                dataManager.setUserAsLoggedOut()
                navigator.onSessionExpire()
            } else {
                navigator.onErrorOccur(it)
            }
        }


    }

    private fun validateCategoryResponse(it: CategoryListModel?) {
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


    fun setSupplierCount(isList: Int) {
        this.supplierCount.set(isList)
    }

    fun setProductCount(isList: Int) {
        this.productCount.set(isList)
    }
}
