package com.codebrew.clikat.module.filter

import dagger.Module
import dagger.android.ContributesAndroidInjector


@Module
abstract class FilterProvider {

    @ContributesAndroidInjector
    abstract fun provideFilterFactory(): FilterFragment
}
