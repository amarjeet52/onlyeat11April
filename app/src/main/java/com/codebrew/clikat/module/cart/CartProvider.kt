package com.codebrew.clikat.module.cart

import com.codebrew.clikat.module.cart.addproduct.AddProductDialog
import dagger.Module
import dagger.android.ContributesAndroidInjector


@Module
abstract class CartProvider {

    @ContributesAndroidInjector
    abstract fun provideCartFactory(): CartNew


    @ContributesAndroidInjector
    abstract fun provideCartCheckoutFactory(): CartCheckout


    @ContributesAndroidInjector
    abstract fun provideAddProductFactory(): AddProductDialog
}
