package com.codebrew.clikat.module.filter

import dagger.Module
import dagger.android.ContributesAndroidInjector


@Module
abstract class BottomSheetProvider {

    @ContributesAndroidInjector
    internal abstract fun provideBottomSheetFactory(): BottomSheetFragment

    @ContributesAndroidInjector
    internal abstract fun provideCraveBottomSheetFactory(): CraveFilterBottomSheet

}
