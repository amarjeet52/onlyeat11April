package com.codebrew.clikat.module.notification

import dagger.Module
import dagger.android.ContributesAndroidInjector


@Module
abstract class NotificationListProvider {

    @ContributesAndroidInjector
    abstract fun provideAgentListFactory(): NotificationListFragment
}
