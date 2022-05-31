package com.codebrew.clikat.module.setting

import com.codebrew.clikat.module.change_language.ChangeLanguage
import com.codebrew.clikat.module.setting.v2.SettingFragmentV2
import dagger.Module
import dagger.android.ContributesAndroidInjector


@Module
abstract class SettingProvider {

    @ContributesAndroidInjector
    abstract fun provideSettingFactory(): SettingFragment

    @ContributesAndroidInjector
    abstract fun provideSettingV2Factory(): SettingFragmentV2

}
