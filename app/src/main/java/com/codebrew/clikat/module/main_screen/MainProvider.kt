package com.codebrew.clikat.module.main_screen

import dagger.Module
import dagger.android.ContributesAndroidInjector


@Module
abstract class MainProvider {

    @ContributesAndroidInjector
    abstract fun provideMainFactory(): MainFragment


}
