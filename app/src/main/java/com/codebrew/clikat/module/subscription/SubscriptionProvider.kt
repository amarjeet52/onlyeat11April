package com.codebrew.clikat.module.subscription

import dagger.Module
import dagger.android.ContributesAndroidInjector


@Module
abstract class SubscriptionProvider {

    @ContributesAndroidInjector
    abstract fun provideSubscriptionFactory(): SubscriptionFrag

}
