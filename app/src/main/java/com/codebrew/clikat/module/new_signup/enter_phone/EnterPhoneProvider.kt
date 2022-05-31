package com.codebrew.clikat.module.new_signup.enter_phone

import com.codebrew.clikat.module.new_signup.enter_phone.v2.EnterPhoneFragV2
import com.codebrew.clikat.module.new_signup.name.EnterNameFragment
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class EnterPhoneProvider {

    @ContributesAndroidInjector
    abstract fun providePhoneFactory(): EnterPhoneFrag

    @ContributesAndroidInjector
    abstract fun providePhoneV2Factory(): EnterPhoneFragV2

    @ContributesAndroidInjector
    abstract fun provideNameFactory(): EnterNameFragment


}
