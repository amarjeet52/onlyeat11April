package com.codebrew.clikat.module.subcategory

import androidx.databinding.ObservableInt
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.MutableLiveData
import com.codebrew.clikat.app_utils.DateTimeUtils
import com.codebrew.clikat.app_utils.SingleLiveEvent
import com.codebrew.clikat.base.BaseViewModel
import com.codebrew.clikat.data.DataManager
import com.codebrew.clikat.data.constants.NetworkConstants
import com.codebrew.clikat.data.constants.PrefenceConstants
import com.codebrew.clikat.data.model.api.AddressBean
import com.codebrew.clikat.data.model.api.ProductData
import com.codebrew.clikat.data.model.api.ProductListModel
import com.codebrew.clikat.data.model.api.supplier_detail.DataSupplierDetail
import com.codebrew.clikat.data.model.others.FilterInputModel
import com.codebrew.clikat.modal.ExampleAllSupplier
import com.codebrew.clikat.modal.ExampleSupplierDetail
import com.codebrew.clikat.modal.other.*
import com.codebrew.clikat.utils.StaticFunction
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import java.text.SimpleDateFormat


class SubCategoryViewModel(dataManager: DataManager) : BaseViewModel<SubCategoryNavigator>(dataManager) {

    val subCatLiveData by lazy { SingleLiveEvent<SubCatData>() }
    val suppliersList by lazy { SingleLiveEvent<ExampleAllSupplier>() }
    val offersLiveData by lazy { SingleLiveEvent<OfferDataBean>() }
    val isSubCat = ObservableInt(0)
    val supplierViaGetLiveData by lazy { SingleLiveEvent<List<SupplierDataBean>>() }
    val categoriesSearchLiveData by lazy { SingleLiveEvent<List<SupplierDataBean>>() }
    val productLiveData: MutableLiveData<ProductData> by lazy {
        MutableLiveData<ProductData>()
    }

    val supllierLiveData: MutableLiveData<DataSupplierDetail> by lazy {
        MutableLiveData<DataSupplierDetail>()
    }

    fun getSubCategory(param: HashMap<String, String>) {
        setIsLoading(true)

        compositeDisposable.add(dataManager.getSubCategory(param)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe({ this.validateResponse(it) }, { this.handleError(it) })
        )
    }

    fun fetchSuplierDetail(branch_id: Int, catId: Int, supplier_id: Int?) {
        setIsLoading(true)
        val hashMap = dataManager.updateUserInf()
        hashMap["supplierId"] = supplier_id.toString()
        hashMap["branchId"] = branch_id.toString()
        hashMap["categoryId"] = catId.toString()

        compositeDisposable.add(dataManager.getSupplierDetails(hashMap)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe({ this.validateSupplierResponse(it) }, { this.handleError(it) })
        )
    }

    fun getSuppliers(param: HashMap<String, String>) {
        setIsLoading(true)
        compositeDisposable.add(dataManager.getAllSuppliersNew(param)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe({ this.validateResponseSuppliersList(it) },
                        { this.handleError(it) })
        )
    }

    private fun validateResponse(it: SubCategoryListModel?) {
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

    private fun validateSupplierResponse(it: ExampleSupplierDetail?) {
        setIsLoading(false)
        when (it?.status) {
            NetworkConstants.SUCCESS -> supllierLiveData.setValue(it.data)

            else ->
                it?.message?.let { it1 -> navigator.onErrorOccur(it1) }

        }
    }

    private fun validateResponseSuppliersList(it: ExampleAllSupplier?) {
        setIsLoading(false)
        when (it?.status) {
            NetworkConstants.SUCCESS -> {
                suppliersList.value = it
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
        setSubCat(0)
        handleErrorMsg(e).let {
            if (it == NetworkConstants.AUTH_MSG) {
                dataManager.setUserAsLoggedOut()
                navigator.onSessionExpire()
            } else {
                navigator.onErrorOccur(it)
            }
        }
    }

    fun setSubCat(count: Int) {
        this.isSubCat.set(count)
    }

    fun getSupplierListViaGet(searchParam: String, categoryId: Int) {

        setIsLoading(true)

        val hashMap = dataManager.updateUserInf()
        hashMap["self_pickup"] = "0"
        hashMap["offset"] = SimpleDateFormat("ZZZZZ", DateTimeUtils.timeLocale).format(System.currentTimeMillis())
        hashMap["languageId"] = dataManager.getLangCode()
        hashMap["search"] = searchParam
        hashMap["categoryId"] = categoryId.toString()



        compositeDisposable.add(dataManager.getSupplierList(hashMap)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe({ this.handleSuppliers(it) }, { this.handleError(it) })
        )
    }

    private fun handleSuppliers(it: HomeSupplierModel?) {
        setIsLoading(false)
        when (it?.status) {
            NetworkConstants.SUCCESS -> {
                supplierViaGetLiveData.value = it.data
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

    fun getCategoriesSearch(searchParam: String) {
        setIsLoading(true)

        val hashMap = dataManager.updateUserInf()
        hashMap["languageId"] = dataManager.getLangCode()
        hashMap["searchText"] = searchParam
        hashMap["accessToken"] = dataManager.getKeyValue(PrefenceConstants.ACCESS_TOKEN, PrefenceConstants.TYPE_STRING).toString()

        compositeDisposable.add(dataManager.searchCategories(hashMap)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe({ this.handleCategoriesList(it) }, { this.handleError(it) })
        )
    }


    private fun handleCategoriesList(it: HomeSupplierModel?) {
        setIsLoading(false)
        when (it?.status) {
            NetworkConstants.SUCCESS -> {
                categoriesSearchLiveData.value = it.data
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


    fun getProductList(keyWord: String, requireActivity: FragmentActivity, categoryId: Int,subCategoryIdList: ArrayList<Int>) {
        val inputModel = FilterInputModel()
        inputModel.languageId = StaticFunction.getLanguage(requireActivity).toString()

        inputModel.is_availability = 0.toString()
        inputModel.is_discount = 0.toString()
        inputModel.max_price_range = 100000.toString()
        inputModel.min_price_range = 0.toString()
//      inputModel.categoryId = categoryId
        inputModel.subCategoryId = subCategoryIdList


        val mLocUser = dataManager.getGsonValue(PrefenceConstants.ADRS_DATA, AddressBean::class.java)

        if (mLocUser != null) {
            inputModel.latitude = mLocUser.latitude ?: ""
            inputModel.longitude = mLocUser.longitude ?: ""
        }
        inputModel.low_to_high = "1"
        inputModel.is_popularity = 0
        if (keyWord.isNotEmpty()) {
            inputModel.product_name = keyWord
        }

        setIsLoading(true)

        compositeDisposable.add(dataManager.getProductFilter(inputModel)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe({ this.validateResponse(it) }, { this.handleError(it) })
        )
    }


    private fun validateResponse(it: ProductListModel?) {
        setIsLoading(false)
        when (it?.status) {
            NetworkConstants.SUCCESS -> {
                setIsList(it.data?.product?.size ?: 0)
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


}
