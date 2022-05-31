package com.codebrew.clikat.module.cart.schedule_order

import com.codebrew.clikat.module.cart.tables.TableSelectionFragment
import com.codebrew.clikat.module.supplier_detail.v2.SupplierDetailFragmentV2
import dagger.Module
import dagger.android.ContributesAndroidInjector


@Module
abstract class TableSelectProvider {

    @ContributesAndroidInjector
    abstract fun provideSupplierFactory(): TableSelectionFragment
}
