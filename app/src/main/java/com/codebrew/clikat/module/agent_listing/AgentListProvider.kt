package com.codebrew.clikat.module.agent_listing

import dagger.Module
import dagger.android.ContributesAndroidInjector


@Module
abstract class AgentListProvider {

    @ContributesAndroidInjector
    abstract fun provideAgentListFactory(): AgentListFragment
}
