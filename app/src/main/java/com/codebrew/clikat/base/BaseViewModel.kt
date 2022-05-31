package com.codebrew.clikat.base

import androidx.databinding.ObservableBoolean
import androidx.databinding.ObservableInt
import androidx.lifecycle.ViewModel
import com.codebrew.clikat.BuildConfig
import com.codebrew.clikat.data.DataManager
import com.codebrew.clikat.data.constants.NetworkConstants
import io.reactivex.disposables.CompositeDisposable
import okhttp3.ResponseBody
import org.json.JSONObject
import retrofit2.HttpException
import java.lang.ref.WeakReference

abstract class BaseViewModel<N>(val dataManager: DataManager) : ViewModel() {

    val isLoading = ObservableBoolean(false)

    val isImageUpload = ObservableBoolean(false)

    val isList = ObservableInt(0)

    val compositeDisposable: CompositeDisposable = CompositeDisposable()

    private var mNavigator: WeakReference<N>? = null


    var navigator: N
        get() = mNavigator?.get()!!
        set(navigator) {
            this.mNavigator = WeakReference(navigator)
        }

    override fun onCleared() {
        compositeDisposable.dispose()
        super.onCleared()
    }

    fun setIsLoading(isLoading: Boolean) {
        this.isLoading.set(isLoading)
    }


    fun setImageLoading(isImage: Boolean) {
        this.isImageUpload.set(isImage)
    }

    fun setIsList(isList: Int) {
        this.isList.set(isList)
    }


    fun handleErrorMsg(e: Throwable): String {

        return if (e is HttpException) {
            val responseBody = e.response()?.errorBody()
            getErrorMessage(responseBody)
        } else {
            val errorMessage=e.localizedMessage
                    ?: ""
            if (errorMessage.contains("royoapps.com/")) {
                "Connection error"
            } else errorMessage
        }
    }


    private fun getErrorMessage(responseBody: ResponseBody?): String {
        return try {
            val jsonObject = JSONObject(responseBody?.string() ?: "")
            when {
                jsonObject.getInt("statusCode") == NetworkConstants.AUTHFAILED -> {
                    NetworkConstants.AUTH_MSG
                }
                else ->
                    jsonObject.getString("message")

            }
        } catch (e: Exception) {
            "Internal Server error"
        }
    }


}
