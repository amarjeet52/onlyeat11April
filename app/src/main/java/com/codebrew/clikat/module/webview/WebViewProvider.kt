package com.codebrew.clikat.module.webview

import dagger.Module
import dagger.android.ContributesAndroidInjector


@Module
abstract class WebViewProvider {

    @ContributesAndroidInjector
    abstract fun provideFactory(): WebViewFragment

}
