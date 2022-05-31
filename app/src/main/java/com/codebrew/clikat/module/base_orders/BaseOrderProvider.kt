package com.codebrew.clikat.module.base_orders

import com.codebrew.clikat.module.completed_order.OrderHistoryFargment
import com.codebrew.clikat.module.pending_orders.UpcomingOrdersFargment
import dagger.Module
import dagger.android.ContributesAndroidInjector


@Module
abstract class BaseOrderProvider {

    @ContributesAndroidInjector
    abstract fun provideBaseOrderFactory(): BaseOrderFragment

    @ContributesAndroidInjector
    abstract fun provideOrderHistFactory(): OrderHistoryFargment


    @ContributesAndroidInjector
    abstract fun provideUpcomingOrderFactory(): UpcomingOrdersFargment

    @ContributesAndroidInjector
    abstract fun provideSkipOrderFactory(): SkipOrdersFragment

}
