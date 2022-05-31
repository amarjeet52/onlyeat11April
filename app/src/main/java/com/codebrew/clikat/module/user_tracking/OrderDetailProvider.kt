package com.codebrew.clikat.module.user_tracking

import com.codebrew.clikat.module.new_signup.login.LoginFragment
import com.codebrew.clikat.module.new_signup.login.v2.LoginFragmentV2
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class OrderDetailProvider {

    @ContributesAndroidInjector
    abstract fun provideOrderDetailBottomSheetFactory(): OrderBottomSheetFragment

}
