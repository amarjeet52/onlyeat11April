package com.codebrew.clikat.module.product_addon

import dagger.Module
import dagger.android.ContributesAndroidInjector


@Module
abstract class AddonProvider {

    @ContributesAndroidInjector
    abstract fun provideAddonFactory(): AddonFragment
}
