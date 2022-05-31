package com.codebrew.clikat.module.home_screen

import androidx.databinding.ObservableBoolean
import androidx.databinding.ObservableInt
import androidx.lifecycle.MutableLiveData
import com.codebrew.clikat.BuildConfig
import com.codebrew.clikat.app_utils.DateTimeUtils
import com.codebrew.clikat.app_utils.SingleLiveEvent
import com.codebrew.clikat.base.BaseViewModel
import com.codebrew.clikat.base.PagingResult
import com.codebrew.clikat.data.DataManager
import com.codebrew.clikat.data.constants.AppConstants
import com.codebrew.clikat.data.constants.NetworkConstants
import com.codebrew.clikat.data.constants.PrefenceConstants
import com.codebrew.clikat.data.model.api.OrderListModel
import com.codebrew.clikat.data.model.api.ProductListModel
import com.codebrew.clikat.data.model.api.orderDetail.OrderHistory
import com.codebrew.clikat.modal.ExampleAllSupplier
import com.codebrew.clikat.modal.ExampleCommon
import com.codebrew.clikat.modal.ExampleSupplierDetail
import com.codebrew.clikat.modal.SupplierList
import com.codebrew.clikat.modal.other.*
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import timber.log.Timber


class HomeViewModel(dataManager: DataManager) : BaseViewModel<HomeNavigator>(dataManager) {

    val homeDataLiveData by lazy { SingleLiveEvent<Data>() }
    val supplierLiveData by lazy { SingleLiveEvent<PagingResult<List<SupplierDataBean>>>() }
    val highestRatingSupplierLiveData by lazy { SingleLiveEvent<PagingResult<List<SupplierDataBean>>>() }
    val newRestaurantSupplierLiveData by lazy { SingleLiveEvent<PagingResult<List<SupplierDataBean>>>() }
    val categoryWiseSupplierLiveData by lazy { SingleLiveEvent<ArrayList<CategoryWiseSuppliers>>() }
    val historyLiveData by lazy { SingleLiveEvent<PagingResult<MutableList<OrderHistory>>>() }
    val tableCapacityLiveData by lazy { SingleLiveEvent<ArrayList<String>>() }
    val offersLiveData by lazy { SingleLiveEvent<OfferDataBean>() }

    val productLiveData by lazy { SingleLiveEvent<PagingResult<SuplierProdListModel>>() }
    val popularLiveData by lazy { SingleLiveEvent<List<ProductDataBean>>() }
    val showCircleLoader = ObservableBoolean(false)

    val branchListData: MutableLiveData<MutableList<SupplierList>> by lazy {
        MutableLiveData<MutableList<SupplierList>>()
    }

    val isSupplierProdCount = ObservableInt(0)
    var skip: Int? = 0
    var skipSupplierList: Int? = 0
    private var isLastReceived = false
    private var isSuppliersLastReceived = false

    var supplierListCount = ObservableInt(0)
    fun validForPaging(): Boolean = !isLastReceived

    fun validForSuppliersPaging(): Boolean = !(isLoading.get()) && !isSuppliersLastReceived

    fun supplierBranchList(param: HashMap<String, String>) {
        dataManager.getAllSuppliersBranchList(param)
            .observeOn(AndroidSchedulers.mainThread())
            ?.subscribeOn(Schedulers.io())
            ?.subscribe({ this.validateSuplResponse(it) }, { this.handleError(it) })?.let {
                compositeDisposable.add(it)
            }
    }

    private fun validateSuplResponse(it: ExampleAllSupplier) {
        if (it.status == NetworkConstants.SUCCESS) {

            if (it.data.supplierList.isNotEmpty()) {
                branchListData.value = it.data?.supplierList
            }

        } else {
            it.message?.let { it1 -> navigator.onErrorOccur(it1) }
        }
    }

    fun getTableCapacity(supplierId: String, branchId: String) {
        setIsLoading(true)
        val hashMap = HashMap<String, String>()
        hashMap["supplier_id"] = supplierId
        hashMap["branch_id"] = branchId
        compositeDisposable.add(
            dataManager.getTableCapacity(hashMap)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe({ this.validateTableCapacity(it) }, { this.handleError(it) })
        )
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

    fun getOrderHistory() {
        setIsLoading(true)

        val param = hashMapOf(
            "accessToken" to dataManager.getKeyValue(
                PrefenceConstants.ACCESS_TOKEN,
                PrefenceConstants.TYPE_STRING
            ).toString(),
            "languageId" to dataManager.getLangCode(),
            "offset" to "0", "limit" to "5"
        )

        dataManager.orderHistory(param)
            ?.observeOn(AndroidSchedulers.mainThread())
            ?.subscribeOn(Schedulers.io())
            ?.subscribe({ this.validateOrdersResponse(it) }, { this.handleError(it) })?.let {
                compositeDisposable.add(it)
            }
    }

    private fun validateOrdersResponse(it: OrderListModel?) {
        setIsLoading(false)

        when (it?.status) {
            NetworkConstants.SUCCESS -> {
                historyLiveData.value = PagingResult(true, it.data.orderHistory)
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

    fun getCategories(catId: Int) {
        setIsLoading(true)

        val param = dataManager.updateUserInf()
        param["accessToken"] =
            dataManager.getKeyValue(PrefenceConstants.ACCESS_TOKEN, PrefenceConstants.TYPE_STRING)
                .toString()
        if (catId > 0) {
            param["categoryId"] = catId.toString()
        }

        compositeDisposable.add(
            dataManager.getAllCategoryNew(param)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe({ this.validateResponse(it) },
                    { this.handleError(it) })
        )
    }

    fun getProductList(supplierId: String, isFirstPage: Boolean) {
        //setIsLoading(true)

        if (isFirstPage) {
            skip = 0
            isLastReceived = false
        }

        val param = dataManager.updateUserInf()

        if (dataManager.getKeyValue(PrefenceConstants.CATEGORY_ID, PrefenceConstants.TYPE_STRING)
                .toString().isNotEmpty()
        ) {
            param["categoryId"] = dataManager.getKeyValue(
                PrefenceConstants.CATEGORY_ID,
                PrefenceConstants.TYPE_STRING
            ).toString()
        }
        param["supplier_id"] = supplierId
        param["limit"] = AppConstants.LIMIT.toString()
        param["offset"] = (skip ?: 0).toString()

        compositeDisposable.add(
            dataManager.getProductLst(param)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe({ this.handleProductList(it, isFirstPage) }, { this.handleError(it) })
        )
    }


    fun getPopularProduct(catId: Int, clientInform: SettingModel.DataBean.SettingData?) {
        //  setIsLoading(true)

        val param = dataManager.updateUserInf()
        param["offset"] = "0"
        param["limit"] = "10"

        if (catId > 0) {
            param["categoryId"] = catId.toString()
        }

        val api =
            if (clientInform?.app_selected_theme == "3" || clientInform?.app_selected_theme == "1" ||
                clientInform?.dynamic_home_screen_sections == "1"
            )
                dataManager.getPopularProdV1(param)
            else
                dataManager.getPopularProd(param)

        api?.observeOn(AndroidSchedulers.mainThread())
            ?.subscribeOn(Schedulers.io())
            ?.subscribe({ this.handlePopularList(it) }, { this.handleError(it) })
            ?.let { compositeDisposable.add(it) }
    }


    fun getSupplierList(
        selfPick: String?,
        sort_by: String?,
        clientInform: SettingModel.DataBean.SettingData?,
        isFirstPage: Boolean? = true,
        otherSuppliers: String? = "0",
        categoryId: String? = null,
        filters: FiltersSupplierList? = null
    ) {

        if (isFirstPage == true)
            setIsLoading(BuildConfig.CLIENT_CODE != "deliverfy_0574")

        //https://api-crave.doortodine.com/home/supplier_list/V2?skip=0&limit=20&languageId=14&
        // latitude=30.70067467835862&longitude=76.76414776593447&self_pickup=8&search=&offset=+05:30&categoryId=0&sort_by=2

        val param = dataManager.updateUserInf()
        if (selfPick == "3") {
            /**to call dine in restaurants*/
            param["is_dine_in"] = "1"
        } else {
            // param["self_pickup"] = selfPick ?: "0"
        }
        param["offset"] = DateTimeUtils.timeOffset
        //   param["categoryId"] = "0"


        if (filters != null) {
            param["is_free_delivery"] = filters.is_free_delivery.toString()
            if (filters.is_preparation_time == 1) {
                param["min_preparation_time"] = filters.minValue ?: "1"
                param["max_preparation_time"] = filters.maxValue ?: "59"
            }
        }

        if (sort_by != null) {
            param["sort_by"] = sort_by.toString()
        }

        val api = if (selfPick.equals("8")) {
            if (clientInform?.enable_rest_pagination_category_wise == "1") {
                if (isFirstPage == true) {
                    skipSupplierList = 0
                    isSuppliersLastReceived = false
                }
                param["limit"] = AppConstants.LIMIT_SUPPLIERS.toString()
                param["skip"] = (skipSupplierList ?: 0).toString()
                if (selfPick.equals("8")) {
                    param["self_pickup"] = ""
                } else {
                    param["self_pickup"] = ""
                }                      //  param["sort_by"] = "2"

                dataManager.getSupplierListV6(param)
            } else if (clientInform?.app_selected_theme == "3" || clientInform?.app_selected_theme == "1")
                dataManager.getSupplierListV1(param)
            else {
                dataManager.getSupplierList(param)
            }
        } else {

            if (isFirstPage == true) {
                skipSupplierList = 0
                isSuppliersLastReceived = false
            }
            param["limit"] = AppConstants.LIMIT_SUPPLIERS.toString()
            param["skip"] = (skipSupplierList ?: 0).toString()

            param["self_pickup"] = selfPick!!
            dataManager.getSupplierListV7(param)
        }

        compositeDisposable.add(
            api
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe({
                    if ((sort_by == "2" || sort_by == "0") && otherSuppliers != "0" && clientInform?.enable_rest_pagination_category_wise == "1" && it is HomeSupplierPaginationModel)
                        handleDynamicSupplierData(it as HomeSupplierPaginationModel, otherSuppliers)
                    else if (clientInform?.enable_rest_pagination_category_wise == "1" && it is HomeSupplierPaginationModel)
                        this.handleSuppliersPagination(
                            isFirstPage
                                ?: true, it as HomeSupplierPaginationModel
                        )
                    else
                        this.handleSuppliers(it as HomeSupplierModel)
                }, {
                    this.handleError(it)
                })
        )
    }

    fun getOfferList(catId: Int, clientInform: SettingModel.DataBean.SettingData?, filterBy: Int) {
        setIsLoading(true)

        val param = dataManager.updateUserInf()
        param["accessToken"] =
            dataManager.getKeyValue(PrefenceConstants.ACCESS_TOKEN, PrefenceConstants.TYPE_STRING)
                .toString()

        if (catId > 0) {
            param["categoryId"] = catId.toString()
        }
        param["filter_by"] = filterBy.toString()

        if (clientInform?.enable_rating_wise_sorting == "1")
            param["order_by"] = "1"

        val api =
            if (clientInform?.app_selected_theme == "3" || clientInform?.app_selected_theme == "1")
                dataManager.getOfferListV1(param)
            else
                dataManager.getOfferList(param)

        compositeDisposable.add(
            api
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe({ this.handleOffers(it) }, { this.handleError(it) })
        )
    }

    fun getCategoryViseSupplier(categoryIds: ArrayList<Int>) {
        setIsLoading(true)

        val param = dataManager.updateUserInf()
        param["categoryIds"] = categoryIds.toString()

        compositeDisposable.add(
            dataManager.getCategoryWiseSuppliers(param)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe({ this.categoryWiseSuppliers(it) },
                    {
                        this.handleError(it)
                    })
        )
    }


    fun makeBookingAccordingToSlot(param: HashMap<String, String?>) {
        setIsLoading(true)

        compositeDisposable.add(
            dataManager.makeTableBookingRequest(param)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe({ this.validateTableBookingResponse(it) }, {
                    this.handleError(it)
                })
        )
    }


    private fun validateTableBookingResponse(it: SuplierProdListModel?) {
        setIsLoading(false)
        when (it?.status) {
            NetworkConstants.SUCCESS -> {
                navigator.onTableSuccessfullyBooked()
            }
            NetworkConstants.AUTHFAILED -> {
                dataManager.setUserAsLoggedOut()
                navigator.onSessionExpire()
            }
            else -> {
                it?.message?.let { it1 -> navigator.onErrorOccur(it1.toString() ?: "") }
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

    private fun handlePopularList(it: ProductListModel?) {
        //  setIsLoading(false)
        if (it?.status == NetworkConstants.SUCCESS) {
            popularLiveData.value = it.data?.product
        } else if (it?.status == NetworkConstants.AUTHFAILED) {
            dataManager.setUserAsLoggedOut()
            navigator.onSessionExpire()
        } else {
            it?.message?.let { it1 -> navigator.onErrorOccur(it1) }
        }
    }

    fun markFavSupplier(supplierId: SupplierInArabicBean?) {
        val param = dataManager.updateUserInf()
        param["supplierId"] = supplierId?.id.toString()
        param["accessToken"] =
            dataManager.getKeyValue(PrefenceConstants.ACCESS_TOKEN, PrefenceConstants.TYPE_STRING)
                .toString()
        compositeDisposable.add(
            dataManager.favouriteSupplier(param)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe({ this.favResponse(it, supplierId) }, { this.handleError(it) })
        )
    }

    fun unFavSupplier(supplierId: SupplierInArabicBean?) {
        val param = dataManager.updateUserInf()
        param["supplierId"] = supplierId?.id.toString()
        param["accessToken"] =
            dataManager.getKeyValue(PrefenceConstants.ACCESS_TOKEN, PrefenceConstants.TYPE_STRING)
                .toString()
        compositeDisposable.add(
            dataManager.unfavSupplier(param)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe({ this.unfavResponse(it, supplierId) }, { this.handleError(it) })
        )
    }

    private fun unfavResponse(it: ExampleCommon?, supplierId: SupplierInArabicBean?) {
        if (it?.status == NetworkConstants.SUCCESS) {
            navigator.unFavSupplierResponse(supplierId)
        } else if (it?.status == NetworkConstants.AUTHFAILED) {
            dataManager.setUserAsLoggedOut()
            navigator.onSessionExpire()
        } else {
            it?.message?.let { it1 -> navigator.onErrorOccur(it1) }
        }
    }


    private fun favResponse(it: ExampleCommon?, supplierId: SupplierInArabicBean?) {

        when (it?.status) {
            NetworkConstants.SUCCESS -> {
                navigator.favSupplierResponse(supplierId)
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


    private fun handleProductList(it: SuplierProdListModel?, firstPage: Boolean) {

        setIsLoading(false)
        if (firstPage)
            isSupplierProdCount.set(it?.data?.product?.count() ?: 0)
        when (it?.status) {
            NetworkConstants.SUCCESS -> {
                val receivedGroups = it.data?.product?.sumBy { it.value?.count() ?: 0 }
                if ((receivedGroups ?: 0) < AppConstants.LIMIT) {
                    Timber.i("Last group is received")
                    isLastReceived = true
                } else {
                    Timber.i("Next page for topic groups is available")
                    skip = skip?.plus(AppConstants.LIMIT)
                }

                productLiveData.value = PagingResult(firstPage, it)
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

    private fun categoryWiseSuppliers(it: ResponseCategorySuppliersList?) {
        setIsLoading(false)
        when (it?.status) {
            NetworkConstants.SUCCESS -> {
                categoryWiseSupplierLiveData.value = it.data
            }
            NetworkConstants.AUTHFAILED -> {
                dataManager.setUserAsLoggedOut()
                navigator.onSessionExpire()
            }
            else -> it?.message?.let { it1 ->
                navigator.onErrorOccur(it1)
            }
        }
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

    override fun onCleared() {
        super.onCleared()
    }

    private fun handleSuppliersPagination(isFirstPage: Boolean, it: HomeSupplierPaginationModel?) {
        setIsLoading(false)
        when (it?.status) {
            NetworkConstants.SUCCESS -> {

                val supplierlist = mutableListOf<SupplierDataBean>()

                it.data?.list?.forEachIndexed { index, supplierDataBean ->
                    if (supplierDataBean.branch_list?.size == 0) {

                    } else {
                        supplierlist.add(supplierDataBean)
                    }
                }

                val receivedGroups = supplierlist.size
                if ((receivedGroups ?: 0) < AppConstants.LIMIT_SUPPLIERS) {
                    Timber.i("Last group is received")
                    isSuppliersLastReceived = true
                } else {
                    Timber.i("Next page for topic groups is available")
                    skipSupplierList = skipSupplierList?.plus(AppConstants.LIMIT_SUPPLIERS)
                }

                if (isFirstPage)
                    supplierListCount.set(supplierlist.count() ?: 0)
                supplierLiveData.value = PagingResult(isFirstPage, supplierlist)
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
                supplierListCount.set(it.data?.size ?: 0)
                supplierLiveData.value = PagingResult(true, it.data)
            }
            NetworkConstants.AUTHFAILED -> {
                dataManager.setUserAsLoggedOut()
                navigator.onSessionExpire()
            }
            else -> {
                it?.message?.let { it1 ->
                    navigator.onErrorOccur(it1.toString())
                }
            }
        }
    }

    private fun handleDynamicSupplierData(
        it: HomeSupplierPaginationModel?,
        otherSuppliers: String?
    ) {
        setIsLoading(false)
        when (it?.status) {
            NetworkConstants.SUCCESS -> {
                when (otherSuppliers) {
                    "1" -> highestRatingSupplierLiveData.value = PagingResult(true, it.data?.list)
                    "2" -> newRestaurantSupplierLiveData.value = PagingResult(true, it.data?.list)
                }
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


    private fun validateResponse(it: CategoryListModel?) {
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

    private fun validateFavResponse(it: ExampleCommon?) {

        //setIsLoading(false)
        if (it?.status == NetworkConstants.SUCCESS) {
            navigator.onFavStatus()
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

    fun fetchSupplierDetail(branch_id: Int, catId: Int, supplier_id: Int?) {
        setIsLoading(false)
        showCircleLoader.set(true)
        val hashMap = dataManager.updateUserInf()

        hashMap["supplierId"] = supplier_id.toString()
        hashMap["branchId"] = branch_id.toString()
        hashMap["categoryId"] = catId.toString()


        compositeDisposable.add(
            dataManager.getSupplierDetails(hashMap)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(
                    { this.validateSupplierResponse(it, supplier_id, catId, branch_id) },
                    { this.handleError(it) })
        )
    }

    private fun validateSupplierResponse(
        it: ExampleSupplierDetail?,
        supplierId: Int?,
        catId: Int,
        branchId: Int
    ) {
        setIsLoading(false)
        showCircleLoader.set(false)
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
}


