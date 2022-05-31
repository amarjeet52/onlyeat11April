package com.codebrew.clikat.module.signature

import com.codebrew.clikat.app_utils.DateTimeUtils
import com.codebrew.clikat.base.BaseViewModel
import com.codebrew.clikat.data.DataManager
import com.codebrew.clikat.data.constants.NetworkConstants
import com.codebrew.clikat.modal.other.HomeSupplierPaginationModel
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

class SignatureViewModel(dataManager: DataManager) : BaseViewModel<SignatureNavigator>(dataManager) {


    fun getSupplierList(selfPick: String?, type: String) {
        setIsLoading(true)
        val param = dataManager.updateUserInf()
        param["self_pickup"] = selfPick ?: "0"
        param["offset"] = DateTimeUtils.timeOffset
        param["limit"] = "20"
        param["search"] = ""
        if (!type.isNullOrEmpty()) {
            param["type"] = type}
            param["skip"] = "0"



            compositeDisposable.add(dataManager.getSupplierListV2(param)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeOn(Schedulers.io())
                    .subscribe({ this.supplierList(it) },
                            {
                                this.handleError(it)
                            })
            )
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

        private fun supplierList(it: HomeSupplierPaginationModel) {
            setIsLoading(false)
            when (it?.status) {
                NetworkConstants.SUCCESS -> navigator.onSupplierList(it.data?.list)
                NetworkConstants.AUTHFAILED -> {
                    dataManager.setUserAsLoggedOut()
                    navigator.onSessionExpire()
                }
                else -> it?.message?.let { it1 -> navigator.onErrorOccur(it1.toString()) }
            }
        }
    }