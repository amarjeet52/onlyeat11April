package com.codebrew.clikat.module.tables

import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class BookedTableListProvider {
    @ContributesAndroidInjector
    abstract fun provideBookedTableFragmentFactory(): BookedTablesListFragment
}