package com.codebrew.clikat.module.wallet

import com.codebrew.clikat.module.wallet.sendMoney.WalletSendMoneyFragment
import dagger.Module
import dagger.android.ContributesAndroidInjector


@Module
abstract class WalletProvider {

    @ContributesAndroidInjector
    abstract fun walletFactory(): WalletMainFragment

    @ContributesAndroidInjector
    abstract fun sendMoneyWalletFactory(): WalletSendMoneyFragment

}
