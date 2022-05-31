package com.codebrew.clikat.module.home_screen.resturant_home.pickup

import com.codebrew.clikat.module.home_screen.resturant_home.dineIn.DineInResturantFrag
import dagger.Module
import dagger.android.ContributesAndroidInjector


@Module
abstract class PickUpProvider {

    @ContributesAndroidInjector
    abstract fun providePickUpFactory(): PickupResturantFrag

    @ContributesAndroidInjector
    abstract fun provideDineInFactory(): DineInResturantFrag
}
