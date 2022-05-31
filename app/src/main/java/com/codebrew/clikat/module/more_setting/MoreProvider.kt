package com.codebrew.clikat.module.more_setting

import dagger.Module
import dagger.android.ContributesAndroidInjector


@Module
abstract class MoreProvider {

    @ContributesAndroidInjector
    abstract fun providMoreFactory(): MoreSettingFragment

    @ContributesAndroidInjector
    abstract fun provideSkipFactory(): SkipMoreSettingFragment
}
