package com.codebrew.clikat.module.setting

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

class SettingViewModel(dataManager: DataManager) : BaseViewModel<SettingNavigator>(dataManager) {
    val recordAudioLiveData by lazy { SingleLiveEvent<SuccessModel>() }


    fun changeNotification(status: Int) {
        setIsLoading(true)


        val params = hashMapOf<String?, String?>(
                "accessToken" to dataManager.getKeyValue(PrefenceConstants.ACCESS_TOKEN, PrefenceConstants.TYPE_STRING).toString(),
                "status" to status.toString(),
                "languageId" to dataManager.getLangCode())


        dataManager.changeNotifStatus(params)
                ?.observeOn(AndroidSchedulers.mainThread())
                ?.subscribeOn(Schedulers.io())
                ?.subscribe({ this.validateResponse(it) }, { this.handleError(it) })?.let {
                    compositeDisposable.add(it)
                }
    }


    fun changeNotiLang(languageId: Int, langCode: String) {
        setIsLoading(true)
        val param = hashMapOf<String?, String?>(
                "accessToken" to dataManager.getKeyValue(PrefenceConstants.ACCESS_TOKEN, PrefenceConstants.TYPE_STRING).toString(),
                "languageId" to languageId.toString())

        dataManager.notiLanguage(param)
                ?.observeOn(AndroidSchedulers.mainThread())
                ?.subscribeOn(Schedulers.io())
                ?.subscribe({ this.notiLangResponse(it,langCode) }, { this.handleError(it) })?.let {
                    compositeDisposable.add(it)
                }
    }

    fun uploadAudio(requestBody:RequestBody) {
        setImageLoading(true)


        dataManager.uploadAudio(requestBody)
            ?.observeOn(AndroidSchedulers.mainThread())
            ?.subscribeOn(Schedulers.io())
            ?.subscribe({ this.uploadAudioResponse(it) }, { this.handleError(it) })?.let {
                compositeDisposable.add(it)
            }

    }
    private fun uploadAudioResponse(it: SuccessModel?) {
        setImageLoading(false)

        when (it?.statusCode) {
            NetworkConstants.SUCCESS -> {
                recordAudioLiveData.value=it
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


    fun uploadProfImage(image: String) {
        setImageLoading(true)

        val file = File(image)
        val requestBody = file.asRequestBody("image/*".toMediaTypeOrNull())
        val partImage = MultipartBody.Part.createFormData("profilePic", file.name, requestBody)

        dataManager.uploadSingleImage(CommonUtils.convrtReqBdy(dataManager.getKeyValue(PrefenceConstants.ACCESS_TOKEN, PrefenceConstants.TYPE_STRING).toString())
                , partImage)
                ?.observeOn(AndroidSchedulers.mainThread())
                ?.subscribeOn(Schedulers.io())
                ?.subscribe({ this.changePicResponse(it) }, { this.handleError(it) })?.let {
                    compositeDisposable.add(it)
                }

    }

    private fun changePicResponse(it: ExampleCommon?) {
        setImageLoading(false)

        when (it?.status) {
            NetworkConstants.SUCCESS -> {
                navigator.onUploadPic(it.message, it.data.image)
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

    fun editProfile(hashMap: HashMap<String, RequestBody>) {
        setIsLoading(true)
        dataManager.signUpFinish(hashMap)
                ?.observeOn(AndroidSchedulers.mainThread())
                ?.subscribeOn(Schedulers.io())
                ?.subscribe({ this.editProfile(it) }, { this.handleError(it) })?.let {
                    compositeDisposable.add(it
                    )
                }
    }

    private fun editProfile(it: PojoSignUp?) {
        setIsLoading(false)

        when (it?.status) {
            NetworkConstants.SUCCESS -> {
                dataManager.addGsonValue(DataNames.USER_DATA, Gson().toJson(it))
                it.data.id_for_invoice?.let {
                    dataManager.setkeyValue(PrefenceConstants.ID_FOR_INVOICE,it)
                }
                navigator.onProfileUpdate()
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


    private fun notiLangResponse(it: ExampleCommon?, langCode: String) {
        setIsLoading(false)
        when (it?.status) {
            NetworkConstants.SUCCESS -> {
                navigator.onNotiLangChange(it.message,langCode)
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



    private fun validateResponse(it: ExampleCommon?) {
        setIsLoading(false)
        if (it?.status == NetworkConstants.SUCCESS) {
            navigator.onNotifiChange(it.message)
        } else if (it?.status == NetworkConstants.AUTHFAILED) {
            dataManager.setUserAsLoggedOut()
            navigator.onSessionExpire()
        } else {
            it?.message?.let { it1 -> navigator.onErrorOccur(it1) }
        }
    }


    private fun handleError(e: Throwable) {
        CommonUtils.changebaseUrl(dataManager.getRetrofitUtl(), BuildConfig.BASE_URL)
        setIsLoading(false)
        setImageLoading(false)
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
