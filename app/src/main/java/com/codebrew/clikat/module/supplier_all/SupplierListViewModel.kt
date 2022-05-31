package com.codebrew.clikat.module.supplier_all

import androidx.databinding.ObservableInt
import androidx.lifecycle.MutableLiveData
import com.codebrew.clikat.BuildConfig
import com.codebrew.clikat.app_utils.SingleLiveEvent
import com.codebrew.clikat.base.BaseViewModel
import com.codebrew.clikat.base.PagingResult
import com.codebrew.clikat.data.DataManager
import com.codebrew.clikat.data.SortBy
import com.codebrew.clikat.data.constants.NetworkConstants
import com.codebrew.clikat.data.constants.PrefenceConstants
import com.codebrew.clikat.modal.ExampleAllSupplier
import com.codebrew.clikat.modal.ExampleAllSupplierNew
import com.codebrew.clikat.modal.ExampleCommon
import com.codebrew.clikat.modal.SupplierList
import com.codebrew.clikat.modal.other.*
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers


class SupplierListViewModel(dataManager: DataManager) : BaseViewModel<SupplierListNavigator>(dataManager) {

    val offersLiveData by lazy { SingleLiveEvent<OfferDataBean>() }
    val supplierLiveData: MutableLiveData<MutableList<SupplierList>> by lazy {
        MutableLiveData<MutableList<SupplierList>>()
    }

    val branchLiveDataby by lazy { SingleLiveEvent<PagingResult<List<SupplierDataBean>>>() }

    val nearBySupplierLiveData by lazy { SingleLiveEvent<List<SupplierDataBean>>() }
    val isSupplierList = ObservableInt(0)
    val tableCapacityLiveData by lazy { SingleLiveEvent<ArrayList<String>>() }

    fun getTableCapacity(supplierId: String, branchId: String) {
        setIsLoading(true)
        val hashMap = HashMap<String, String>()
        hashMap["supplier_id"] = supplierId
        hashMap["branch_id"] = branchId
        compositeDisposable.add(dataManager.getTableCapacity(hashMap)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe({ this.validateTableCapacity(it) }, { this.handleError(it) })
        )
    }
    private fun validateBranchData(it: HomeSupplierModel?) {
        setIsLoading(false)
        when (it?.status) {
            NetworkConstants.SUCCESS -> {
                branchLiveDataby.value =  PagingResult(true, it.data)
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

    private fun validateTableCapacity(it: TableCapacityModel?) {
        setIsLoading(false)
        when (it?.status) {
            NetworkConstants.SUCCESS -> {
                tableCapacityLiveData.value = it.data
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
    fun branchList(param: HashMap<String, String>, fromBranch: Boolean) {
        setIsLoading(true)

        compositeDisposable.add(dataManager.getbranchList(param)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe({ this.validateBranchData(it) }, { this.handleError(it) })
        )

    }

    fun supplierList(param: HashMap<String, String>, fromBranch: Boolean,self_pickup:String?="") {
        setIsLoading(true)


        val apiHelper = if (param.containsKey("tag_id"))
            dataManager.getAllSuppliersTagList(param)
        else if (fromBranch) {
            dataManager.getAllSuppliersBranchList(param)
        } else {
//            dataManager.getAllSuppliersNew(param)
    if(self_pickup.equals("8")) {
        dataManager.getSupplierListV2Dummy(param)
    }else{
        dataManager.getSupplierList_V7(param)
    }
        }

        apiHelper.observeOn(AndroidSchedulers.mainThread())
                ?.subscribeOn(Schedulers.io())
                ?.subscribe({ this.validateSuplResponse(it, fromBranch) }, { this.handleError(it) })?.let {
                    compositeDisposable.add(it)
                }
    }

    private fun validateSuplResponse(it: ExampleAllSupplier, fromBranch: Boolean) {
        setIsLoading(false)

        if (it.status == NetworkConstants.SUCCESS) {
//            if (it.data.supplierList?.sumBy { it.branch_list?.count() ?: 0 } == 0) return

            if (fromBranch) {
                navigator.onBranchList(it.data?.supplierList ?: mutableListOf())
            } else {

//                val filterList = mutableListOf<SupplierList>()
//
//                if (it.data?.supplierList?.sumBy { it.branch_list?.count() ?: 0 } ?: 0 > 0) {
//                    it.data?.supplierList?.mapIndexed { index, supplierList ->
//                        filterList.addAll(supplierList?.branch_list ?: mutableListOf())
//                    }
//                }
                supplierLiveData.value = it.data.supplierList
            }

        } else {
            it.message?.let { it1 -> navigator.onErrorOccur(it1) }
        }
    }

    fun getSupplierList() {

        setIsLoading(true)

        val param = dataManager.updateUserInf()
        param["self_pickup"] = "1"

        if (dataManager.getKeyValue(PrefenceConstants.CATEGORY_ID, PrefenceConstants.TYPE_STRING).toString().isNotEmpty()) {
            param["categoryId"] = dataManager.getKeyValue(PrefenceConstants.CATEGORY_ID, PrefenceConstants.TYPE_STRING).toString()
        }
        param["sort_by"] = SortBy.SortByDistance.sortBy.toString()


        compositeDisposable.add(dataManager.getSupplierList(param)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe({ this.handleSuppliers(it) }, { this.handleError(it) })
        )
    }

    fun getAllSupplierList() {

        setIsLoading(true)

        val param = dataManager.updateUserInf()
        param["self_pickup"] = "1"

        if (dataManager.getKeyValue(PrefenceConstants.CATEGORY_ID, PrefenceConstants.TYPE_STRING).toString().isNotEmpty()) {
            param["categoryId"] = dataManager.getKeyValue(PrefenceConstants.CATEGORY_ID, PrefenceConstants.TYPE_STRING).toString()
        }
        param["sort_by"] = SortBy.SortByDistance.sortBy.toString()


        compositeDisposable.add(dataManager.getAllSupplierList(param)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe({ this.handleAllSuppliers(it) }, { this.handleError(it) })
        )
    }

    private fun handleAllSuppliers(it: ExampleAllSupplierNew?) {
        setIsLoading(false)
        when (it?.status) {
            NetworkConstants.SUCCESS -> {
                setSupplierList(it.data?.count() ?: 0)
                supplierLiveData.value = it.data
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

    private fun handleSuppliers(it: HomeSupplierModel?) {
        setIsLoading(false)
        when (it?.status) {
            NetworkConstants.SUCCESS -> {
                nearBySupplierLiveData.value = it.data
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

    fun markFavSupplier(supplierId: SupplierList?) {
        val param = dataManager.updateUserInf()
        param["supplierId"] = supplierId?.id.toString()
        param["accessToken"] = dataManager.getKeyValue(PrefenceConstants.ACCESS_TOKEN, PrefenceConstants.TYPE_STRING).toString()
        compositeDisposable.add(dataManager.favouriteSupplier(param)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe({ this.favResponse(it, supplierId) }, { this.handleError(it) })
        )
    }

    fun unFavSupplier(supplierId: SupplierList?) {
        val param = dataManager.updateUserInf()
        param["supplierId"] = supplierId?.id.toString()
        param["accessToken"] = dataManager.getKeyValue(PrefenceConstants.ACCESS_TOKEN, PrefenceConstants.TYPE_STRING).toString()
        compositeDisposable.add(dataManager.unfavSupplier(param)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe({ this.unfavResponse(it, supplierId) }, { this.handleError(it) })
        )
    }

    private fun unfavResponse(it: ExampleCommon?, supplierId: SupplierList?) {
        if (it?.status == NetworkConstants.SUCCESS) {
            navigator.unFavSupplierResponse(supplierId)
        } else if (it?.status == NetworkConstants.AUTHFAILED) {
            dataManager.setUserAsLoggedOut()
            navigator.onSessionExpire()
        } else {
            it?.message?.let { it1 -> navigator.onErrorOccur(it1) }
        }
    }


    private fun favResponse(it: ExampleCommon?, supplierId: SupplierList?) {

        if (it?.status == NetworkConstants.SUCCESS) {
            navigator.favSupplierResponse(supplierId)
        } else if (it?.status == NetworkConstants.AUTHFAILED) {
            dataManager.setUserAsLoggedOut()
            navigator.onSessionExpire()
        } else {
            it?.message?.let { it1 -> navigator.onErrorOccur(it1) }
        }
    }

    fun getOfferList(catId: Int, selected_template: String?, orderBy: String?) {
        setIsLoading(true)

        val param = dataManager.updateUserInf()
        param["accessToken"] = dataManager.getKeyValue(PrefenceConstants.ACCESS_TOKEN, PrefenceConstants.TYPE_STRING).toString()

        if (catId > 0) {
            param["categoryId"] = catId.toString()
        }

        if (orderBy != null)
            param["order_by"] = orderBy.toString()

        val api = if (selected_template == "3" || selected_template == "1")
            dataManager.getOfferListV1(param)
        else
            dataManager.getOfferList(param)

        compositeDisposable.add(api
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe({ this.handleOffers(it) }, { this.handleError(it) })
        )
    }

    private fun handleOffers(it: OfferListModel?) {
        setIsLoading(false)
        when (it?.status) {
            NetworkConstants.SUCCESS -> offersLiveData.value = it.data
            NetworkConstants.AUTHFAILED -> {
                dataManager.setUserAsLoggedOut()
                navigator.onSessionExpire()
            }
            else -> it?.message?.let { it1 -> navigator.onErrorOccur(it1) }
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

    fun setSupplierList(count: Int) {
        this.isSupplierList.set(count)
    }


}
