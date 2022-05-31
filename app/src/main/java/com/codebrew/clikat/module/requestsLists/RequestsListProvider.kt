package com.codebrew.clikat.module.requestsLists

import dagger.Module
import dagger.android.ContributesAndroidInjector


@Module
abstract class RequestsListProvider {

    @ContributesAndroidInjector
    abstract fun provideAgentListFactory(): RequestListFragment
}
