package com.codebrew.clikat.module.dialog_adress

import com.codebrew.clikat.module.dialog_adress.v2.AddressDialogFragmentV2
import dagger.Module
import dagger.android.ContributesAndroidInjector


@Module
abstract class AddressDialogProvider {

    @ContributesAndroidInjector
    abstract fun provideAddressDialogFactory(): AddressDialogFragment

    @ContributesAndroidInjector
    abstract fun provideAddressDialogV2Factory(): AddressDialogFragmentV2


}
