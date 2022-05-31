package com.codebrew.clikat.module.wishlist_prod

import dagger.Module
import dagger.android.ContributesAndroidInjector


@Module
abstract class WishListProvider {

    @ContributesAndroidInjector
    abstract fun provideStandardFactory(): WishListFrag

    @ContributesAndroidInjector
    abstract fun provideFactory(): WishListMainFragment

    @ContributesAndroidInjector
    abstract fun provideSuppliersFactory(): WishlistSuppliersFrag
}
