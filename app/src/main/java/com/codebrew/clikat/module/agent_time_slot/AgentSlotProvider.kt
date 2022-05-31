package com.codebrew.clikat.module.agent_time_slot

import dagger.Module
import dagger.android.ContributesAndroidInjector


@Module
abstract class AgentSlotProvider {

    @ContributesAndroidInjector
    abstract fun provideAgentSlotFactory(): AgentTimeSlotFragment
}
