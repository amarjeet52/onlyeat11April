package com.codebrew.clikat.module.home_screen

import com.codebrew.clikat.module.cart.schedule_order.BookTableSlots
import com.codebrew.clikat.module.home_screen.suppliers.SupplierListingFragment
import com.codebrew.clikat.module.home_screen.suppliers.SuppliersMapFragment
import com.codebrew.clikat.module.home_screen.viewAll.ViewAllCategoriesFragment
import com.codebrew.clikat.module.home_screen.viewAll.ViewAllSuppliersFragment
import com.codebrew.clikat.module.jumaHome.JumaHomeLaundryFrag
import com.codebrew.clikat.module.pre_delivery.BookTableDetailFragment
import dagger.Module
import dagger.android.ContributesAndroidInjector


@Module
abstract class HomeProvider {

    @ContributesAndroidInjector
    abstract fun provideHomeFactory(): HomeFragment

    @ContributesAndroidInjector
    abstract fun provideHomeMenuItemFragmentFactory(): HomeMenuItemFragment


    @ContributesAndroidInjector
    abstract fun supplierListFactory():SupplierListingFragment

    @ContributesAndroidInjector
    abstract fun viewAllSuppliersList():ViewAllSuppliersFragment

    @ContributesAndroidInjector
    abstract fun viewAllCategoryList():ViewAllCategoriesFragment

    @ContributesAndroidInjector
    abstract fun viewBookTableDetailFragment(): BookTableDetailFragment

}
