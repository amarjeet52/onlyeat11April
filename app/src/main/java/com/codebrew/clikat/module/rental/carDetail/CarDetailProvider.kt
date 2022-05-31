package com.codebrew.clikat.module.rental.carDetail

import dagger.Module
import dagger.android.ContributesAndroidInjector


@Module
abstract class CarDetailProvider {

    @ContributesAndroidInjector
    abstract fun provideCarDetailFactory(): CarDetailFrag
}
