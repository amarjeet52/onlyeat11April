package com.codebrew.clikat.module.change_language

import dagger.Module
import dagger.android.ContributesAndroidInjector


@Module
abstract class ChangeLangProvider {

    @ContributesAndroidInjector
    abstract fun provideChangeLagFactory(): ChangeLanguage

    @ContributesAndroidInjector
    abstract fun provideChangeSkipLagFactory(): SkipChangeLanguage

}
