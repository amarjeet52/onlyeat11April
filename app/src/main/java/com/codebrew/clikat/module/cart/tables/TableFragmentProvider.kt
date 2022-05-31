package com.codebrew.clikat.module.cart.tables

import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class TableFragmentProvider {

    @ContributesAndroidInjector
    abstract fun provideTableFragmentFactory(): TableSelectionFragment

}