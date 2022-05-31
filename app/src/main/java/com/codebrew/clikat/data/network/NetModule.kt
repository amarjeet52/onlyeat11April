package com.codebrew.clikat.data.network

import com.codebrew.clikat.BuildConfig
import com.codebrew.clikat.data.preferences.PreferenceHelper
import com.codebrew.clikat.di.ApplicationScope
import com.codebrew.clikat.modal.AppGlobal
import dagger.Module
import dagger.Provides
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.jetbrains.annotations.NotNull
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit


/*@Module
class NetModule(internal var context: Context) {*/

@Module
 class  NetModule {



    @Provides
    @NotNull
    @ApplicationScope
    internal fun getApiInterface(retroFit: Retrofit): RestService {
        return retroFit.create(RestService::class.java)
    }


    @Provides
    @ApplicationScope
    internal fun getRetrofit(
            hostInterceptor: HostSelectionInterceptor,
            loggingInterceptor: HttpLoggingInterceptor): Retrofit {

        return Retrofit.Builder()
                .baseUrl(BuildConfig.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .client(okHttpClient(hostInterceptor, loggingInterceptor))
                .build()
    }


    @Provides
    @ApplicationScope
    fun hostSelectionInterceptor(preferenceHelper: PreferenceHelper): HostSelectionInterceptor {
        return HostSelectionInterceptor(preferenceHelper)
    }

    @Provides
    @ApplicationScope
    
    fun loggingInterceptor(): HttpLoggingInterceptor {
        val interceptor = HttpLoggingInterceptor(HttpLoggingInterceptor.Logger.DEFAULT)
        interceptor.level = HttpLoggingInterceptor.Level.HEADERS
        interceptor.level = HttpLoggingInterceptor.Level.BODY
        return interceptor
    }


    @Provides
    @ApplicationScope
    fun okHttpClient(
            hostInterceptor: HostSelectionInterceptor,
            loggingInterceptor: HttpLoggingInterceptor
    ): OkHttpClient {
        return OkHttpClient.Builder()
                .addInterceptor(hostInterceptor)
                .addInterceptor(loggingInterceptor)
                .addInterceptor(NetworkConnectionInterceptor(AppGlobal.context))
                .connectTimeout(60, TimeUnit.SECONDS)
                .writeTimeout(60, TimeUnit.SECONDS)
                .readTimeout(60, TimeUnit.SECONDS)
                .build()
    }


}