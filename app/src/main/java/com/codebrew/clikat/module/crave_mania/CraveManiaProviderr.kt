package com.codebrew.clikat.module.crave_mania

import com.codebrew.clikat.module.restaurant_detail.CraveSignatureDetailFrag
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class CraveManiaProviderr {
    @ContributesAndroidInjector
    abstract fun provideCraveManiaFragment(): FragmentCraveMania
}