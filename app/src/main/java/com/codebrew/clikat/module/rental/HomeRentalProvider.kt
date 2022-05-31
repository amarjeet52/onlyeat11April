package com.codebrew.clikat.module.rental

import dagger.Module
import dagger.android.ContributesAndroidInjector


@Module
abstract class HomeRentalProvider {

    @ContributesAndroidInjector
    abstract fun provideHomeFactory(): HomeRental
}
