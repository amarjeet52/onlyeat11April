package com.codebrew.clikat.module.supplier_detail

import androidx.lifecycle.MutableLiveData
import com.codebrew.clikat.app_utils.SingleLiveEvent
import com.codebrew.clikat.base.BaseViewModel
import com.codebrew.clikat.data.DataManager
import com.codebrew.clikat.data.constants.NetworkConstants
import com.codebrew.clikat.data.constants.PrefenceConstants
import com.codebrew.clikat.data.model.api.supplier_detail.DataSupplierDetail
import com.codebrew.clikat.modal.ExampleCommon
import com.codebrew.clikat.modal.ExampleSupplierDetail
import com.codebrew.clikat.modal.other.SubCatData
import com.codebrew.clikat.modal.other.SubCategoryListModel
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import java.util.*

class SupplierDetailViewModel(dataManager: DataManager) : BaseViewModel<SupplierDetailNavigator>(dataManager) {


    val supllierLiveData: MutableLiveData<DataSupplierDetail> by lazy {
        MutableLiveData<DataSupplierDetail>()
    }

    val subCatLiveData by lazy { SingleLiveEvent<SubCatData>() }


    /*  val supllierLiveData: MutableLiveData<DataSupplierDetail> by lazy {
          MutableLiveData<DataSupplierDetail>()
      }
  */

    fun getSubCategory(param: HashMap<String, String>) {
      //  setIsLoading(true)

        compositeDisposable.add(dataManager.getSubCategory(param)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe({ this.validateResponse(it) }, { this.handleError(it) })
        )
    }

    fun fetchSupplierInf(supplierId: String, branchId: String, categoryId: String) {
        setIsLoading(true)
        val hashMap = dataManager.updateUserInf()

        if (dataManager.getCurrentUserLoggedIn()) {
            hashMap["accessToken"] = dataManager.getKeyValue(PrefenceConstants.ACCESS_TOKEN, PrefenceConstants.TYPE_STRING).toString()
        }

        hashMap["supplierId"] = supplierId
        hashMap["branchId"] = branchId
        hashMap["categoryId"] = categoryId


        compositeDisposable.add(dataManager.getSupplierDetails(hashMap)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe({ this.validateResponse(it) }, { this.handleError(it) })
        )
    }


    fun markFavouriteSupplier(supplierId: String) {
        //setIsLoading(true)

        val hashMap = HashMap<String, String>()

        if (dataManager.getCurrentUserLoggedIn()) {
            hashMap["accessToken"] = dataManager.getKeyValue(PrefenceConstants.ACCESS_TOKEN, PrefenceConstants.TYPE_STRING).toString()
        }
        hashMap["supplierId"] = supplierId



        compositeDisposable.add(dataManager.markSupplierFav(hashMap)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe({ this.favResponse(it) }, { this.handleError(it) })
        )
    }


    fun unFavouriteSupplier(supplierId: String) {
       // setIsLoading(true)

        val hashMap = HashMap<String, String>()

        if (dataManager.getCurrentUserLoggedIn()) {
            hashMap["accessToken"] = dataManager.getKeyValue(PrefenceConstants.ACCESS_TOKEN, PrefenceConstants.TYPE_STRING).toString()
        }
        hashMap["supplierId"] = supplierId

        compositeDisposable.add(dataManager.markSupplierUnFav(hashMap)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe({ this.unfavResponse(it) }, { this.handleError(it) })
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


    private fun unfavResponse(it: ExampleCommon?) {

      //  setIsLoading(false)

        when (it?.status) {
            NetworkConstants.SUCCESS -> navigator.onFavouriteStatus(it.message)
            NetworkConstants.AUTHFAILED -> navigator.onSessionExpire()
            else -> navigator.onErrorOccur(it?.message ?: "")
        }
    }


    private fun validateResponse(it: ExampleSupplierDetail?) {

        setIsLoading(false)

        when (it?.status) {
            NetworkConstants.SUCCESS -> supllierLiveData.setValue(it.data)
            NetworkConstants.AUTHFAILED -> navigator.onSessionExpire()
            else -> navigator.onErrorOccur(it?.message ?: "")
        }
    }


    private fun favResponse(it: ExampleCommon?) {

       // setIsLoading(false)

        when (it?.status) {
            NetworkConstants.SUCCESS -> navigator.onFavouriteStatus(it.message)
            NetworkConstants.AUTHFAILED -> navigator.onSessionExpire()
            else -> navigator.onErrorOccur(it?.message ?: "")
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
