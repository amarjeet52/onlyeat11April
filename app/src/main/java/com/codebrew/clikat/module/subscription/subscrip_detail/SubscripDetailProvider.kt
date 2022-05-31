package com.codebrew.clikat.module.subscription.subscrip_detail

import dagger.Module
import dagger.android.ContributesAndroidInjector


@Module
abstract class SubscripDetailProvider {

    @ContributesAndroidInjector
    abstract fun provideSubscripDetailFactory(): SubscripDetailFrag

}
