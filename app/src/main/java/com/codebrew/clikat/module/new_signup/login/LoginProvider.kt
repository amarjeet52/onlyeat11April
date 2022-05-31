package com.codebrew.clikat.module.new_signup.login

import com.codebrew.clikat.module.new_signup.login.v2.LoginFragmentV2
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class LoginProvider {

    @ContributesAndroidInjector
    abstract fun provideLoginFactory(): LoginFragment


    @ContributesAndroidInjector
    abstract fun provideLoginV2Factory(): LoginFragmentV2
}
