package com.codebrew.clikat.module.referral_list

import androidx.databinding.ObservableInt
import androidx.lifecycle.MutableLiveData
import com.codebrew.clikat.base.BaseInterface
import com.codebrew.clikat.base.BaseViewModel
import com.codebrew.clikat.data.DataManager
import com.codebrew.clikat.data.constants.NetworkConstants
import com.codebrew.clikat.data.model.api.ReferalData
import com.codebrew.clikat.data.model.api.ReferralList
import com.codebrew.clikat.data.network.ApiResponse
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

class ReferralListViewModel(dataManager: DataManager) : BaseViewModel<BaseInterface>(dataManager) {


    val referralListLiveData: MutableLiveData<MutableList<ReferalData>> by lazy {
        MutableLiveData<MutableList<ReferalData>>()
    }


    val isReferralList = ObservableInt(0)

    fun getReferralList() {
        setIsLoading(true)


        compositeDisposable.add(dataManager.getReferralList()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe({ this.validateResponse(it) }, { this.handleError(it) })
        )
    }

    private fun validateResponse(it: ApiResponse<ReferralList>?) {


        setIsLoading(false)
        setRefferalList(it?.data?.referalData?.count() ?: 0)
        when (it?.status) {
            NetworkConstants.SUCCESS -> {
                referralListLiveData.value = it.data?.referalData
            }
            NetworkConstants.AUTHFAILED -> {
                navigator.onSessionExpire()
            }
            else -> {
                navigator.onErrorOccur(it?.msg.toString())
            }
        }
    }


    private fun handleError(e: Throwable) {
        setIsLoading(false)
        setRefferalList(0)
        handleErrorMsg(e).let {
            if (it == NetworkConstants.AUTH_MSG) {
                dataManager.setUserAsLoggedOut()
                navigator.onSessionExpire()
            } else {
                navigator.onErrorOccur(it)
            }
        }
    }


    fun setRefferalList(wishCount: Int) {
        this.isReferralList.set(wishCount)
    }
}
