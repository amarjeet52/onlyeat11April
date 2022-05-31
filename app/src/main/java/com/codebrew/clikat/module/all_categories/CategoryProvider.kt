package com.codebrew.clikat.module.all_categories

import dagger.Module
import dagger.android.ContributesAndroidInjector


@Module
abstract class CategoryProvider {

    @ContributesAndroidInjector
    abstract fun provideCategoryFactory(): CategoryFragment
}
