package com.codebrew.clikat.module.banners

import dagger.Module
import dagger.android.ContributesAndroidInjector


@Module
abstract class BannersProvider {

    @ContributesAndroidInjector
    abstract fun bannersFactory(): BannersListingFragment
}
