package com.codebrew.clikat.module.jumaHome

import com.codebrew.clikat.module.home_screen.suppliers.SupplierListingFragment
import com.codebrew.clikat.module.home_screen.suppliers.SuppliersMapFragment
import com.codebrew.clikat.module.home_screen.viewAll.ViewAllCategoriesFragment
import com.codebrew.clikat.module.home_screen.viewAll.ViewAllSuppliersFragment
import com.codebrew.clikat.module.jumaHome.JumaHomeLaundryFrag
import dagger.Module
import dagger.android.ContributesAndroidInjector


@Module
abstract class JumaHomeProvider {

    @ContributesAndroidInjector
    abstract fun provideJumaFactory(): JumaProductsFragment

    @ContributesAndroidInjector
    abstract fun provideJumaHomeFactory(): JumaHomeLaundryFrag
}
