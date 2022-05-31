package com.codebrew.clikat.module.restaurant_detail

import androidx.databinding.ObservableBoolean
import androidx.databinding.ObservableInt
import com.codebrew.clikat.app_utils.CommonUtils
import com.codebrew.clikat.app_utils.SingleLiveEvent
import com.codebrew.clikat.base.BaseViewModel
import com.codebrew.clikat.base.PagingResult
import com.codebrew.clikat.data.DataManager
import com.codebrew.clikat.data.constants.AppConstants
import com.codebrew.clikat.data.constants.NetworkConstants
import com.codebrew.clikat.data.constants.PrefenceConstants
import com.codebrew.clikat.data.model.others.CustomPayModel
import com.codebrew.clikat.data.model.others.SaveCardInputModel
import com.codebrew.clikat.data.network.ApiResponse
import com.codebrew.clikat.modal.ExampleCommon
import com.codebrew.clikat.modal.other.*
import com.codebrew.clikat.module.restaurant_detail.model.AddOnsResponse
import com.codebrew.clikat.module.restaurant_detail.model.Dataa
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import timber.log.Timber
import java.io.File


class RestDetailViewModel(dataManager: DataManager) : BaseViewModel<RestDetailNavigator>(dataManager) {

    val supplierLiveData by lazy { SingleLiveEvent<PagingResult<SuplierProdListModel>>() }
    val subCatLiveData by lazy { SingleLiveEvent<SubCatData>() }
    val addOnsLiveData by lazy { SingleLiveEvent<List<Dataa>>() }
    val tableCapacityLiveData by lazy { SingleLiveEvent<ArrayList<String>>() }
    val categoryLiveData by lazy { SingleLiveEvent<ArrayList<ProductBean>>() }

    val prescLiveData by lazy { SingleLiveEvent<String>() }

    val isSupplierProdCount = ObservableInt(0)
    val isCategoryId = ObservableInt(0)
    var skip: Int? = 0
    private var isLastReceived = false
    var showWhiteScreen = ObservableBoolean(true)
    fun validForPaging(): Boolean = !isLoading.get() && !isLastReceived

    val isContentProgressBarLoading = ObservableBoolean(false)

    fun setIsContentProgressBarLoading(isLoading: Boolean) {
        this.isContentProgressBarLoading.set(isLoading)
    }


    fun getAddOns(param: HashMap<String, String>) {
        setIsLoading(true)

        compositeDisposable.add(dataManager.getProductAddOns(param)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe({ this.validateAddOns(it) }, { this.handleError(it) })
        )
    }


    private fun validateAddOns(it: AddOnsResponse?) {
        setIsLoading(false)
        when (it?.status) {
            NetworkConstants.SUCCESS -> {
                addOnsLiveData.value = it.data
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


    fun getSubCategory(param: HashMap<String, String>) {
        setIsLoading(true)

        compositeDisposable.add(dataManager.getSubCategory(param)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe({ this.validateSubCategory(it) }, { this.handleError(it) })
        )
    }


    private fun validateSubCategory(it: SubCategoryListModel?) {
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

    fun getManiaProductList(supplierId: String, isFirstPage: Boolean, settingBean: SettingModel.DataBean.SettingData?, categoryId: String?, supplier_branch_id: Int? = 0, is_non_veg: String? = null, showShimmerLoading: Boolean = true) {
        if (isFirstPage) {
            skip = 0
            isLastReceived = false

            // if (showShimmerLoading)
             setIsLoading(true)
        }

        val param = dataManager.updateUserInf()
        param["supplier_id"] = supplierId
        param["supplier_branch_id"] = supplier_branch_id.toString()


        /*    if (categoryId != null)
                param["category_id"] = categoryId*/


        if (is_non_veg != null)
            param["is_non_veg"] = is_non_veg

        /*    val api = if (settingBean?.enable_rest_pagination_category_wise == "1" || settingBean?.is_juman_flow_enable == "1")
                dataManager.getSuppliersProductLst(param)
            else
                dataManager.getProductLst(param)*/

        compositeDisposable.add(dataManager.getManiaProductLst(param)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribe({ this.validateResponse(it, isFirstPage) }, {
                this.handleError(it)
            })
        )
    }


    fun getProductList(supplierId: String, isFirstPage: Boolean, settingBean: SettingModel.DataBean.SettingData?, categoryId: String?, supplier_branch_id: Int? = 0, is_non_veg: String? = null, showShimmerLoading: Boolean = true,type:String?=null) {
        if (isFirstPage) {
            skip = 0
            isLastReceived = false

            // if (showShimmerLoading)
            setIsLoading(true)
        }

        val param = dataManager.updateUserInf()
        param["supplier_id"] = supplierId
        param["supplier_branch_id"] = supplier_branch_id.toString()
if(type==null||type.equals(""))
{

}else{
    param["type"] = type!!
}

        /*    if (categoryId != null)
                param["category_id"] = categoryId*/


        if (is_non_veg != null)
            param["is_non_veg"] = is_non_veg

        /*    val api = if (settingBean?.enable_rest_pagination_category_wise == "1" || settingBean?.is_juman_flow_enable == "1")
                dataManager.getSuppliersProductLst(param)
            else
                dataManager.getProductLst(param)*/

        compositeDisposable.add(dataManager.getProductLst(param)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe({ this.validateResponse(it, isFirstPage) }, {
                    this.handleError(it)
                })
        )
    }

    fun getBranchProductList(supplierId: String, supplierBranchId: String, isFirstPage: Boolean) {


        if (isFirstPage) {
            skip = 0
            isLastReceived = false
            setIsLoading(true)
        }

        val param = dataManager.updateUserInf()
        param["supplier_id"] = supplierId

        if (supplierBranchId != "0") {
            param["supplier_branch_id"] = supplierBranchId
        }
/*        param["limit"] = AppConstants.LIMIT.toString()
        param["offset"] = (skip ?: 0).toString()*/
        compositeDisposable.add(dataManager.getBranchProductLst(param)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe({ this.validateResponse(it, isFirstPage) }, {
                    this.handleError(it)
                })
        )
    }


    fun uploadPresImage(image: String, supplierId: String, adrsId: Int?, appType: String) {
        // setImageLoading(true)

        val file = File(image)
        val requestBody = file.asRequestBody("image/*".toMediaTypeOrNull())
        val partImage = MultipartBody.Part.createFormData("file", file.name, requestBody)

        dataManager.uploadPres(CommonUtils.convrtReqBdy(""), CommonUtils.convrtReqBdy(supplierId), CommonUtils.convrtReqBdy(adrsId.toString()),
                CommonUtils.convrtReqBdy(appType), partImage)
                .observeOn(AndroidSchedulers.mainThread())
                ?.subscribeOn(Schedulers.io())
                ?.subscribe({ this.validatePresc(it) }, {
                    this.handleError(it)
                })?.let {
                    compositeDisposable.add(it)
                }

    }

    private fun validatePresc(it: ApiResponse<Any>?) {
        // setIsLoading(false)
        when (it?.status) {
            NetworkConstants.SUCCESS -> {
                prescLiveData.value = it.msg.toString()
            }
            NetworkConstants.AUTHFAILED -> {
                dataManager.setUserAsLoggedOut()
                navigator.onSessionExpire()
            }
            else -> {
                it?.msg?.let { it1 -> navigator.onErrorOccur(it1.toString()) }
            }
        }
    }


    fun markFavSupplier(supplierId: String) {


        val param = dataManager.updateUserInf()
        param["supplierId"] = supplierId
        param["accessToken"] = dataManager.getKeyValue(PrefenceConstants.ACCESS_TOKEN, PrefenceConstants.TYPE_STRING).toString()
        compositeDisposable.add(dataManager.favouriteSupplier(param)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe({ this.favResponse(it) }, {
                    this.handleError(it)
                })
        )
    }

    fun unFavSupplier(supplierId: String) {


        val param = dataManager.updateUserInf()
        param["supplierId"] = supplierId
        param["accessToken"] = dataManager.getKeyValue(PrefenceConstants.ACCESS_TOKEN, PrefenceConstants.TYPE_STRING).toString()
        compositeDisposable.add(dataManager.unfavSupplier(param)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe({ this.unfavResponse(it) }, {
                    this.handleError(it)
                })
        )
    }

    private fun unfavResponse(it: ExampleCommon?) {
        if (it?.status == NetworkConstants.SUCCESS) {
            navigator.unFavResponse()
        } else if (it?.status == NetworkConstants.AUTHFAILED) {
            dataManager.setUserAsLoggedOut()
            navigator.onSessionExpire()
        } else {
            it?.message?.let { it1 -> navigator.onErrorOccur(it1) }
        }
    }


    private fun favResponse(it: ExampleCommon?) {

        when (it?.status) {
            NetworkConstants.SUCCESS -> {
                navigator.favResponse()
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

    fun getSupplierCategoryList(supplierId: String) {
        setIsLoading(true)
        val param = HashMap<String, String>()
        param["supplierId"] = supplierId
        param["langauge_id"] = dataManager.getLangCode()

        compositeDisposable.add(dataManager.getAllSuppliersCategories(param)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe({ this.validateResponse(it) }, {
                    this.handleError(it)
                })
        )
    }

    private fun validateResponse(it: SupplierCategoryModel?) {
//        setIsLoading(false)

        when (it?.status) {
            NetworkConstants.SUCCESS -> {
                categoryLiveData.value = it.data
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


    private fun validateResponse(it: SuplierProdListModel?, firstPage: Boolean) {
        setIsLoading(false)
        setIsContentProgressBarLoading(false)
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

                supplierLiveData.value = PagingResult(firstPage, it)
            }
            NetworkConstants.AUTHFAILED -> {
                dataManager.setUserAsLoggedOut()
                navigator.onSessionExpire()
            }
            else -> {
                it?.message?.let { it1 ->
                    navigator.onErrorOccur(it1.toString() ?: "")
                }
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

    fun makeBookingAccordingToSlot(mParam: HashMap<String, String?>, mSelectedPayment: CustomPayModel?, savedCard: SaveCardInputModel?) {
        setIsLoading(true)

        if (mSelectedPayment != null) {
            mParam["gateway_unique_id"] = mSelectedPayment.payment_token ?: ""

            if (!mSelectedPayment.keyId.isNullOrEmpty())
                mParam["payment_token"] = mSelectedPayment.keyId ?: ""

            /* if (mSelectedPayment.payment_token == "mumybene" && !phoneNumber.isNullOrEmpty()) {
                 mParam["phoneNumber"] = phoneNumber
             }*/

            if (mSelectedPayment.payment_token == "authorize_net") {
                mParam["authnet_profile_id"] = mSelectedPayment.authnet_profile_id ?: ""
                mParam["authnet_payment_profile_id"] = mSelectedPayment.authnet_payment_profile_id
                        ?: ""

            } else if (mSelectedPayment.payment_token == "pago_facil") {
                mParam["payment_token"] = savedCard?.card_number ?: ""
                mParam["cvt"] = savedCard?.cvc ?: ""
                mParam["cp"] = savedCard?.zipCode ?: ""
                mParam["expMonth"] = savedCard?.exp_month ?: ""
                mParam["expYear"] = savedCard?.exp_year?.takeLast(2) ?: ""
            } else if (mSelectedPayment.payment_token == "safe2pay") {
                mParam["payment_token"] = savedCard?.card_number ?: ""
                mParam["cvt"] = savedCard?.cvc ?: ""
                mParam["expMonth"] = savedCard?.exp_month ?: ""
                mParam["expYear"] = savedCard?.exp_year ?: ""
                mParam["cardHolderName"] = savedCard?.card_holder_name ?: ""
            } else if (!mSelectedPayment.cardId.isNullOrEmpty() && !mSelectedPayment.customerId.isNullOrEmpty()) {
                mParam["customer_payment_id"] = mSelectedPayment.customerId ?: ""
                mParam["card_id"] = mSelectedPayment.cardId ?: ""
            }
        }

        compositeDisposable.add(dataManager.makeTableBookingRequest(mParam)
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

    fun setIsCategory(catId: Int) {
        this.isCategoryId.set(catId)
    }


}
