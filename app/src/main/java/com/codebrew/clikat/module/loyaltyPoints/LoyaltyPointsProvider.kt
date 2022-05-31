package com.codebrew.clikat.module.loyaltyPoints

import com.codebrew.clikat.module.help.HelpActivity
import com.codebrew.clikat.module.recording.RecordingFragment
import dagger.Module
import dagger.android.ContributesAndroidInjector


@Module
abstract class LoyaltyPointsProvider {

    @ContributesAndroidInjector
    abstract fun loyaltyPointsFactory(): LoyaltyPointsFragment

    @ContributesAndroidInjector
    abstract fun loyaltyPointsNewFactory(): LoyaltyPointsFragmentNew

    @ContributesAndroidInjector
    abstract fun recordingFactory(): RecordingFragment


}

