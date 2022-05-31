package com.codebrew.clikat.module.addon_quant

import dagger.Module
import dagger.android.ContributesAndroidInjector


@Module
abstract class SavedAddonProvider {

    @ContributesAndroidInjector
    abstract fun provideSavedAddonFactory(): SavedAddon
}
