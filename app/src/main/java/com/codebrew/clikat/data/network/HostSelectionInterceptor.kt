package com.codebrew.clikat.data.network

import com.codebrew.clikat.data.constants.AppConstants
import com.codebrew.clikat.data.constants.PrefenceConstants
import com.codebrew.clikat.data.preferences.PreferenceHelper
import com.codebrew.clikat.di.ApplicationScope
import dagger.Module
import dagger.Provides
import okhttp3.Interceptor
import okhttp3.Response

@Module(includes = [NetModule::class])
class HostSelectionInterceptor(private val preferenceHelper: PreferenceHelper) : Interceptor {


    @Volatile
    var secret_key: String? = null

    @Provides
    @ApplicationScope
    fun setSecret(secret_key: String): String? {
        this.secret_key = secret_key
        return this.secret_key
    }

    @Provides
    @ApplicationScope
    override fun intercept(chain: Interceptor.Chain): Response {

        val requestBuilder = chain.request().newBuilder()

        val request = if (preferenceHelper.getCurrentUserLoggedIn()) {
            if (AppConstants.isOnlyAuth == true)
                requestBuilder
                        .addHeader("Authorization", "Bearer "+preferenceHelper.getKeyValue(PrefenceConstants.ACCESS_TOKEN, PrefenceConstants.TYPE_STRING).toString())
                        .addHeader("Accept-Language", AppConstants.LANG_CODE)
                        .build()
            else
                requestBuilder
                        .addHeader("secretdbkey", secret_key ?: "")
                        .addHeader("Accept-Language", AppConstants.LANG_CODE)
                        .addHeader("Authorization", preferenceHelper.getKeyValue(PrefenceConstants.ACCESS_TOKEN, PrefenceConstants.TYPE_STRING).toString())
                        .build()
        } else {
            requestBuilder
                    .addHeader("secretdbkey", secret_key ?: "")
                    .addHeader("Accept-Language", AppConstants.LANG_CODE)
                    .build()
        }

        if (secret_key == null || secret_key?.isEmpty() == true) {
            requestBuilder.removeHeader("secretdbkey")
        }

        return chain.proceed(request)
    }
}

