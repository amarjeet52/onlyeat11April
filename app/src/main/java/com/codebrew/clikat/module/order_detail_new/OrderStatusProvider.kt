package com.codebrew.clikat.module.order_detail_new

import com.codebrew.clikat.data.OrderStatus
import dagger.Module
import dagger.android.ContributesAndroidInjector


@Module
abstract class OrderStatusProvider {

    @ContributesAndroidInjector
    abstract fun provideOrderStatusFactory(): OrderStatusDialog
}
