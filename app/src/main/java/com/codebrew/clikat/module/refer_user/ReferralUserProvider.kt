package com.codebrew.clikat.module.refer_user

import dagger.Module
import dagger.android.ContributesAndroidInjector


@Module
abstract class ReferralUserProvider {

    @ContributesAndroidInjector
    abstract fun provideReferralUserFactory(): ReferralUser
}
