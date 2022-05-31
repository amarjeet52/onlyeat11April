package com.codebrew.clikat.module.home_screen.resturant_home

import dagger.Module
import dagger.android.ContributesAndroidInjector


@Module
abstract class ResHomeProvider {

    @ContributesAndroidInjector
    abstract fun provideResHomeFactory(): ResturantHomeFrag
}
