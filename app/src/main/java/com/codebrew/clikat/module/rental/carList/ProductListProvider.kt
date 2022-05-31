package com.codebrew.clikat.module.rental.carList

import dagger.Module
import dagger.android.ContributesAndroidInjector


@Module
abstract class ProductListProvider {

    @ContributesAndroidInjector
    abstract fun provideHomeFactory(): ProductListFrag
}
