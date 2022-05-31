package com.codebrew.clikat.module.recording

import com.codebrew.clikat.BuildConfig
import com.codebrew.clikat.app_utils.CommonUtils
import com.codebrew.clikat.app_utils.SingleLiveEvent
import com.codebrew.clikat.base.BaseViewModel
import com.codebrew.clikat.data.DataManager
import com.codebrew.clikat.data.constants.NetworkConstants
import com.codebrew.clikat.data.constants.PrefenceConstants
import com.codebrew.clikat.data.model.api.SuccessModel
import com.codebrew.clikat.modal.ExampleCommon
import com.codebrew.clikat.modal.PojoSignUp
import com.codebrew.clikat.modal.other.Data
import com.codebrew.clikat.preferences.DataNames
import com.codebrew.clikat.preferences.Prefs
import com.google.gson.Gson
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File

class RecordingViewModel(dataManager: DataManager) : BaseViewModel<RecordAudioNavigator>(dataManager) {
    val recordAudioLiveData by lazy { SingleLiveEvent<String>() }


    fun uploadAudio(requestBody:RequestBody) {
        setImageLoading(true)


        dataManager.uploadAudio(requestBody)
            ?.observeOn(AndroidSchedulers.mainThread())
            ?.subscribeOn(Schedulers.io())
            ?.subscribe({ this.uploadAudioResponse(it) },
                {
                    this.handleError(it) })?.let {
                compositeDisposable.add(it)
            }

    }
    private fun uploadAudioResponse(it: SuccessModel?) {
        setImageLoading(false)

        when (it?.statusCode) {
            NetworkConstants.SUCCESS_CODE -> {
                recordAudioLiveData.value=it.message.toString()
            }
            NetworkConstants.AUTHFAILED -> {
                dataManager.setUserAsLoggedOut()
              //  navigator.onSessionExpire()
            }
            else -> {
                it?.message?.let { it1 -> //navigator.onErrorOccur(it1)
                }
            }
        }
    }

    private fun handleError(e: Throwable) {
        CommonUtils.changebaseUrl(dataManager.getRetrofitUtl(), BuildConfig.BASE_URL)
        setIsLoading(false)
        setImageLoading(false)
        handleErrorMsg(e).let {
            if (it == NetworkConstants.AUTH_MSG) {
                dataManager.setUserAsLoggedOut()
//                navigator.onSessionExpire()
            } else {
//                navigator.onErrorOccur(it)
            }
        }
    }

}
