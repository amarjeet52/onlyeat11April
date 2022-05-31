package com.codebrew.clikat.di.builder

import com.codebrew.clikat.module.cart.schedule_order.BookTableSlots
import dagger.Module
import dagger.android.ContributesAndroidInjector


@Module
abstract class BookTableDetailProvider {
    @ContributesAndroidInjector
    abstract fun viewBookTableSlots(): BookTableSlots
}
