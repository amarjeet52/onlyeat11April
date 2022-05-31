package com.codebrew.clikat.module.manage_order

import dagger.Module
import dagger.android.ContributesAndroidInjector


@Module
abstract class ManageOrderProvider {

    @ContributesAndroidInjector
    abstract fun provideManageOrderFactory(): ManageOrderFrag

}
