package com.codebrew.clikat.module.custom_home

import dagger.Module
import dagger.android.ContributesAndroidInjector


@Module
abstract class CusHomeProvider {

    @ContributesAndroidInjector
     abstract fun provideCusHomeFactory(): CustomHomeFrag

    @ContributesAndroidInjector
    abstract fun provideClikatHomeFactory(): ClikatHomeFragment
}
