package com.codebrew.clikat.module.product

import com.codebrew.clikat.base.BaseInterface
import com.codebrew.clikat.data.model.api.orderDetail.Data

interface ProductNavigator : BaseInterface {

    fun onFavStatus()
    fun editOrderResponse(data: Data?)
}