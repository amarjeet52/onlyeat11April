package com.codebrew.clikat.user_chat


import androidx.databinding.ObservableInt
import androidx.lifecycle.MutableLiveData
import com.codebrew.clikat.base.BaseViewModel
import com.codebrew.clikat.base.PagingResult
import com.codebrew.clikat.data.DataManager
import com.codebrew.clikat.data.ReceiverType
import com.codebrew.clikat.data.constants.AppConstants
import com.codebrew.clikat.data.constants.NetworkConstants
import com.codebrew.clikat.data.constants.PrefenceConstants
import com.codebrew.clikat.data.model.api.ChatMessageListing
import com.codebrew.clikat.data.model.api.LoyaltyResponse
import com.codebrew.clikat.data.model.api.orderDetail.Agent
import com.codebrew.clikat.data.network.ApiResponse
import com.codebrew.clikat.retrofit.RetrofitUtils
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import okhttp3.MultipartBody
import timber.log.Timber
import java.io.File
import java.util.*

class UserChatViewModel(
        dataManager: DataManager
) : BaseViewModel<UserChatNavigator>(dataManager) {


    val isListCount = ObservableInt(0)
    fun validForPaging(): Boolean = !isLoading.get() && !isLastReceived
    var skip: Int? = 0
    private var isLastReceived = false

    val userMessagelist: MutableLiveData<PagingResult<List<ChatMessageListing>>> by lazy {
        MutableLiveData<PagingResult<List<ChatMessageListing>>>()
    }


    fun fetchAllChat(orderId: String?, isFirst: Boolean, detail: Agent?, receiverType: String?) {
        if (isFirst) {
            skip = 0
            isLastReceived = false
        }

        setIsLoading(true)
        val hashMap = hashMapOf(
                "accessToken" to dataManager.getKeyValue(PrefenceConstants.ACCESS_TOKEN, PrefenceConstants.TYPE_STRING)?.toString(),
                "limit" to AppConstants.LIMIT.toString()/*if (isFirst) "500" else userMessagelist.value?.count()?.plus(50)?.toString()*/,
                "skip" to skip.toString()/*if (isFirst) "0" else userMessagelist.value?.count().toString()*/,
                "userType" to "2"//  1 for agent , 2 for user
        )

        if (!orderId.isNullOrEmpty())
            hashMap["order_id"] = orderId

        if (detail?.message_id?.isNotEmpty()==true) {
            hashMap["receiver_created_id"] = ""
            hashMap["message_id"] = detail.message_id
        }

        if(receiverType!=ReceiverType.ADMIN.type){
            if(receiverType==ReceiverType.SUPPLIER.type)
                hashMap["receiver_created_id"] = detail?.user_created_id
            else hashMap["receiver_created_id"] = detail?.agent_created_id
        }

        compositeDisposable.add(
                dataManager.getChatMessages(hashMap)
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeOn(Schedulers.io())
                        .subscribe({ this.handleChatResponse(it, isFirst) }, { this.handleError(it) })
        )
    }


    private fun handleChatResponse(it: ApiResponse<ArrayList<ChatMessageListing>>?, first: Boolean) {
        setIsLoading(false)
        if (first)
            setListCount(it?.data?.count() ?: 0)

        when (it?.status) {
            NetworkConstants.SUCCESS -> {
                val receivedGroups = it.data
                if ((receivedGroups?.size ?: 0) < AppConstants.LIMIT) {
                    Timber.i("Last group is received")
                    isLastReceived = true
                } else {
                    Timber.i("Next page for topic groups is available")
                    skip = skip?.plus(AppConstants.LIMIT)
                }

                userMessagelist.value = PagingResult(first, it.data)
            }
            else -> navigator.onErrorOccur(it?.msg.toString())
        }
    }

    fun apiUploadImage(chatMessage: ChatMessageListing) {
        setIsLoading(true)

        val body = MultipartBody.Part.createFormData("image",
                chatMessage.localFile?.absolutePath, RetrofitUtils.imageToRequestBody(chatMessage.localFile as File))

        compositeDisposable.add(
                dataManager.uploadImage(body)
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeOn(Schedulers.io())
                        .subscribe({ this.handleResponse(it, chatMessage) }, { this.handleError(it) })
        )
    }


    private fun handleResponse(it: ApiResponse<ChatMessageListing>?, chatMessage: ChatMessageListing) {
        setIsLoading(false)

        when (it?.status) {
            NetworkConstants.SUCCESS -> {
                chatMessage.image_url = it.data?.image
                navigator.onImageUploaded(chatMessage)
            }
            else -> navigator.onErrorOccur(it?.msg.toString())
        }
    }

    fun apiCheckMessageId(userType: String, user_created_id: String) {
        setIsLoading(true)

        compositeDisposable.add(
                dataManager.getChatMessageId(userType, user_created_id)
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeOn(Schedulers.io())
                        .subscribe({ this.handleMessageIdResponse(it) }, { this.handleError(it) })
        )
    }


    private fun handleMessageIdResponse(it: LoyaltyResponse) {
        setIsLoading(false)

        when (it.status) {
            NetworkConstants.SUCCESS -> {
                navigator.onChatMessageId(it.data?.message_id?:"")
            }
            else -> navigator.onErrorOccur(it.message.toString() ?: "")
        }
    }

    fun setListCount(count: Int) {
        this.isListCount.set(count)
    }


    private fun handleError(e: Throwable) {
        setIsLoading(false)
        handleErrorMsg(e).let {
            when (it) {
                NetworkConstants.AUTH_MSG -> {
                    navigator.onErrorOccur(NetworkConstants.AUTH_MSG)
                    dataManager.setUserAsLoggedOut()
                    navigator.onSessionExpire()
                }
                else -> navigator.onErrorOccur(it)
            }
        }
    }
}
