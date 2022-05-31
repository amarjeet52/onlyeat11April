package com.codebrew.clikat.module.product

import com.codebrew.clikat.module.product.product_listing.ProductTabListing
import dagger.Module
import dagger.android.ContributesAndroidInjector


@Module
abstract class ProdListProvider {

    @ContributesAndroidInjector
    abstract fun provideProdListFactory(): ProductTabListing
}
