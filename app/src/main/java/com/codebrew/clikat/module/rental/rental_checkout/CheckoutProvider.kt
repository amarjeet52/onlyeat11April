package com.codebrew.clikat.module.rental.rental_checkout

import dagger.Module
import dagger.android.ContributesAndroidInjector


@Module
abstract class CheckoutProvider {

    @ContributesAndroidInjector
    abstract fun provideRentalCheckFactory(): RentalCheckoutFrag
}
