package com.codebrew.clikat.module.referral_list

import dagger.Module
import dagger.android.ContributesAndroidInjector


@Module
abstract class ReferralProvider {

    @ContributesAndroidInjector
    abstract fun provideReferralListFactory(): ReferralListFragment
}
