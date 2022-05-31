package com.codebrew.clikat.module.searchProduct

import dagger.Module
import dagger.android.ContributesAndroidInjector


@Module
abstract class SearchProvider {

    @ContributesAndroidInjector
    abstract fun provideSearchFactory(): SearchFragment

    @ContributesAndroidInjector
    abstract fun provideUnifySearchFactory(): UnifySearchFragment
}
