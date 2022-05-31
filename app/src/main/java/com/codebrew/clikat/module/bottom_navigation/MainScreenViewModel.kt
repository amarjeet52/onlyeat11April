package com.codebrew.clikat.module.bottom_navigation

import androidx.lifecycle.MutableLiveData
import com.codebrew.clikat.app_utils.SocketManager
import com.codebrew.clikat.base.BaseInterface
import com.codebrew.clikat.base.BaseViewModel
import com.codebrew.clikat.data.DataManager
import com.codebrew.clikat.data.constants.NetworkConstants
import com.codebrew.clikat.data.constants.PrefenceConstants
import com.codebrew.clikat.data.model.api.BraintreeTokenResoponse
import com.codebrew.clikat.data.model.api.orderDetail.OrderDetailModel
import com.codebrew.clikat.data.model.api.orderDetail.OrderHistory
import com.codebrew.clikat.modal.BookedTableResponseModel
import com.codebrew.clikat.modal.other.OrderDetailParam
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import io.socket.emitter.Emitter

class MainScreenViewModel(dataManager: DataManager) : BaseViewModel<BaseInterface>(dataManager) {

    val invitationListener by lazy { MutableLiveData<String?>() }

     val socketManager
            by lazy { SocketManager.getInstance(dataManager.getKeyValue(PrefenceConstants.USER_ID, PrefenceConstants.TYPE_STRING).toString(),
                    dataManager.getKeyValue(PrefenceConstants.DB_SECRET, PrefenceConstants.TYPE_STRING).toString()) }


     fun connectSocket(socketListener: Emitter.Listener){
        socketManager.connect(socketListener)
    }


    fun acceptInvitation(invitedTableId:String, currentUserId:String) {
        compositeDisposable.add(dataManager.acceptTableInvitation(invitedTableId,currentUserId)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe({ this.validateInvitationResponse(it) }, { this.handleError(it) })
        )
    }

    private fun validateInvitationResponse(it: BraintreeTokenResoponse?) {
        setIsLoading(false)
        when (it?.status) {
            NetworkConstants.SUCCESS -> {
                invitationListener.value = ""
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
