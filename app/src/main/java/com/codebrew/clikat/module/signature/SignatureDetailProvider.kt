package com.codebrew.clikat.module.signature

import com.codebrew.clikat.module.restaurant_detail.CraveSignatureDetailFrag
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class SignatureDetailProvider {
    @ContributesAndroidInjector
    abstract fun provideSubCatFactory(): CraveSignatureDetailFrag
}