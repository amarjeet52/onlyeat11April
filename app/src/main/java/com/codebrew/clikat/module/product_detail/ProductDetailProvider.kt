package com.codebrew.clikat.module.product_detail

import com.codebrew.clikat.module.product_detail.v2.ProductDetailsV2
import dagger.Module
import dagger.android.ContributesAndroidInjector


@Module
abstract class ProductDetailProvider {

    @ContributesAndroidInjector
    abstract fun provideProdDetailFactory(): ProductDetails

    @ContributesAndroidInjector
    abstract fun provideProdDetailV2Factory(): ProductDetailsV2


}
