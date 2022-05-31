package com.codebrew.clikat.module.supplier_detail

import com.codebrew.clikat.module.supplier_detail.v2.SupplierDetailFragmentV2
import dagger.Module
import dagger.android.ContributesAndroidInjector


@Module
abstract class SupplierDetailProvider {

    @ContributesAndroidInjector
    abstract fun provideSupplierFactory(): SupplierDetailFragment

    @ContributesAndroidInjector
    abstract fun provideSupplierV2Factory(): SupplierDetailFragmentV2
}
