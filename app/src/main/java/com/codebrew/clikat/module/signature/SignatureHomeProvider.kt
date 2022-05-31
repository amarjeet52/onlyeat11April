package com.codebrew.clikat.module.signature

import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class SignatureHomeProvider {
    @ContributesAndroidInjector
    abstract fun provideSubCatFactory(): SignatureHome
}