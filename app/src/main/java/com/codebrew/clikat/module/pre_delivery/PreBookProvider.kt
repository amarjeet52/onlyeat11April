package com.codebrew.clikat.module.pre_delivery

import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class PreBookMenuProvider {

    @ContributesAndroidInjector
    abstract fun provideSearchFactory(): PreBookMenuFragment

}
