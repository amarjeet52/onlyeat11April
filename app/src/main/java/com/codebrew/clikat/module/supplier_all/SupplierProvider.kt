package com.codebrew.clikat.module.supplier_all

import com.codebrew.clikat.module.home_screen.BranchResturant
import com.codebrew.clikat.module.supplier_all.newBranches.NewBranchesFragment
import dagger.Module
import dagger.android.ContributesAndroidInjector


@Module
abstract class SupplierProvider {

    @ContributesAndroidInjector
    abstract fun provideSupplierFactory(): SupplierAll

    @ContributesAndroidInjector
    abstract fun providebranchListFactory(): BranchResturant

    @ContributesAndroidInjector
    abstract fun provideBranches(): NewBranchesFragment
}
