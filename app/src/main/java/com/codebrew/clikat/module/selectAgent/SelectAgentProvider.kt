package com.codebrew.clikat.module.selectAgent

import dagger.Module
import dagger.android.ContributesAndroidInjector


@Module
abstract class SelectAgentProvider {

    @ContributesAndroidInjector
    abstract fun provideSelectAgentFactory(): SelectAgent
}
