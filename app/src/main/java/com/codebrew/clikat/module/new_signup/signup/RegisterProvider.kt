package com.codebrew.clikat.module.new_signup.signup

import com.codebrew.clikat.module.new_signup.signup.v2.RegisterFragmentV2
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class RegisterProvider {

    @ContributesAndroidInjector
    abstract fun provideRegisterFactory(): RegisterFragment


    @ContributesAndroidInjector
    abstract fun provideRegisterV2Factory(): RegisterFragmentV2


}
