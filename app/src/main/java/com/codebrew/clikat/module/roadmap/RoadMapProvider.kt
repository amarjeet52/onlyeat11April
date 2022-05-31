package com.codebrew.clikat.module.roadmap

import com.codebrew.clikat.module.searchProduct.SearchFragment
import com.codebrew.clikat.module.searchProduct.UnifySearchFragment
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class RoadMapProvider {

    @ContributesAndroidInjector
    abstract fun provideSearchFactory(): RoadMapActivity

}
