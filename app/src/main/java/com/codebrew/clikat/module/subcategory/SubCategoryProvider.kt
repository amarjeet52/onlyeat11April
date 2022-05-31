package com.codebrew.clikat.module.subcategory

import dagger.Module
import dagger.android.ContributesAndroidInjector


@Module
abstract class SubCategoryProvider {

    @ContributesAndroidInjector
    abstract fun provideSubCatFactory(): SubCategory
}
