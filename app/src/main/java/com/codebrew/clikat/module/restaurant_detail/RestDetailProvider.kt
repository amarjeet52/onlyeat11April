package com.codebrew.clikat.module.restaurant_detail

import dagger.Module
import dagger.android.ContributesAndroidInjector


@Module
abstract class RestDetailProvider {

    @ContributesAndroidInjector
    abstract fun provideRestDetailFactory(): RestaurantDetailFrag
}
