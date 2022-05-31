package com.codebrew.clikat.di.module

import android.app.Application
import android.content.Context
import com.codebrew.clikat.app_utils.*

import com.codebrew.clikat.data.AppDataManager
import com.codebrew.clikat.data.DataManager
import com.codebrew.clikat.data.constants.PrefenceConstants
import com.codebrew.clikat.data.preferences.AppPreferenceHelper
import com.codebrew.clikat.data.preferences.PreferenceHelper
import com.codebrew.clikat.di.ApplicationContext
import com.codebrew.clikat.di.PreferenceInfo
import dagger.Module
import dagger.Provides
import io.reactivex.disposables.CompositeDisposable
import javax.inject.Singleton


@Module
class AppModule {


    @Provides
    @Singleton
    internal fun provideContext(application: Application): Context = application

    @Provides
    @PreferenceInfo
    internal fun provideprefFileName(): String = PrefenceConstants.SharedPrefenceName


    @Provides
    @Singleton
    internal fun providePrefHelper(appPreferenceHelper: AppPreferenceHelper): PreferenceHelper = appPreferenceHelper


    @Provides
    @Singleton
    internal fun provideDataManager(appDataManager: AppDataManager): DataManager {
        return appDataManager
    }


    @Provides
    internal fun provideCompositeDisposable(): CompositeDisposable = CompositeDisposable()


    @Provides
    @Singleton
    internal fun provideDialogsUtil(): DialogsUtil {
        return DialogsUtil()
    }


    @Provides
    @Singleton
    internal fun provideDateTimeUtil(): DateTimeUtils {
        return DateTimeUtils()
    }


    @Provides
    @Singleton
    @ApplicationContext
    fun provideAppUtils(context: Context): AppUtils {
        return AppUtils(context)
    }


    @Provides
    @Singleton
    @ApplicationContext
    fun provideProdUtils(context: Context): ProdUtils {
        return ProdUtils(context)
    }

    @Provides
    @Singleton
    @ApplicationContext
    fun provideOrderUtils(context: Context): OrderUtils {
        return OrderUtils(context)
    }

    @Provides
    @Singleton
    @ApplicationContext
    fun provideCartUtils(context: Context): CartUtils {
        return CartUtils(context)
    }



    @Provides
    @Singleton
    @ApplicationContext
    fun provideImageUtil(context: Context): ImageUtility {
        return ImageUtility(context)
    }


    @Provides
    @Singleton
    @ApplicationContext
    fun providePaymentUtils(context: Context): PaymentUtil {
        return PaymentUtil(context)
    }


    @Provides
    @Singleton
    @ApplicationContext
    fun provideGoogleUtils(context: Context): GoogleLoginHelper {
        return GoogleLoginHelper(context)
    }

}