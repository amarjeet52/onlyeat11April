package com.codebrew.clikat.module.loyaltyPoints

import com.codebrew.clikat.base.BaseInterface
import com.codebrew.clikat.data.model.api.LoyaltyData

interface LoyaltyPointsNavigator : BaseInterface {
  fun loyaltyPointsSuccess(data: LoyaltyData?)
  fun loyaltyPointsSuccessNew(data: List<DataLoyality?>)
}
