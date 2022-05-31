package com.codebrew.clikat.di.component

import android.app.Application
import com.codebrew.clikat.di.module.AppModule
import com.codebrew.clikat.data.network.NetModule
import com.codebrew.clikat.data.network.RestService
import com.codebrew.clikat.di.ApplicationScope
import com.codebrew.clikat.di.builder.ActivityBuilder
import com.codebrew.clikat.modal.AppGlobal
import dagger.BindsInstance
import dagger.Component
import dagger.android.AndroidInjectionModule
import javax.inject.Singleton


@Singleton
@ApplicationScope
@Component(modules = [(AndroidInjectionModule::class), (AppModule::class) ,(ActivityBuilder::class),(NetModule::class)])
interface AppComponent {


    fun getApiInterface(): RestService


    @Component.Builder
    interface Builder {

        @BindsInstance
        fun application(application: Application): Builder

        fun build(): AppComponent
    }

    fun inject(app: AppGlobal)

}