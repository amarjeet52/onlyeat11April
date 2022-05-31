package com.codebrew.clikat.module.signup

import dagger.Module
import dagger.android.ContributesAndroidInjector


@Module
abstract class SignUpProvider {

    @ContributesAndroidInjector
    abstract fun provideSignUpFrag1Factory(): SignupFragment1

    @ContributesAndroidInjector
    abstract fun provideSignUpFrag3Factory(): SignupFragment3


    @ContributesAndroidInjector
    abstract fun provideSignUpFrag4Factory(): SignupFragment4
}
