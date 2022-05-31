package com.codebrew.clikat.module.all_offers

import dagger.Module
import dagger.android.ContributesAndroidInjector


@Module
abstract class OfferProdProvider {

    @ContributesAndroidInjector
    abstract fun provideOfferprodFactory(): OfferProductListingFragment
}
