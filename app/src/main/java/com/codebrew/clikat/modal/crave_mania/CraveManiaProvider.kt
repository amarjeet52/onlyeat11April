package com.codebrew.clikat.modal.crave_mania

import com.codebrew.clikat.module.restaurant_detail.CraveSignatureDetailFrag
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class CraveManiaProvider {
    @ContributesAndroidInjector
    abstract fun provideCraveManiaFragment(): CraveManiaFragment
}