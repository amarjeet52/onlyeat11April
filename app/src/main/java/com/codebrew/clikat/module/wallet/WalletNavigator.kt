package com.codebrew.clikat.module.wallet

import com.codebrew.clikat.base.BaseInterface
import com.codebrew.clikat.base.PagingResult
import com.codebrew.clikat.data.model.api.AddCardResponseData
import com.codebrew.clikat.data.model.api.Result
import com.codebrew.clikat.data.model.api.tap_payment.Transaction
import com.codebrew.clikat.modal.PojoNotification

interface WalletNavigator : BaseInterface {
    fun onMoneySent()
    fun onAddMoneyToWallet()
}
