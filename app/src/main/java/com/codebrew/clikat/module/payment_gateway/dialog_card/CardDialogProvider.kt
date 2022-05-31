package com.codebrew.clikat.module.payment_gateway.dialog_card

import dagger.Module
import dagger.android.ContributesAndroidInjector


@Module
abstract class CardDialogProvider {

    @ContributesAndroidInjector
    abstract fun provideCardDialogFactory(): CardDialogFrag
}
