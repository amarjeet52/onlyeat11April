package com.codebrew.clikat.module.rental.carDetail

import com.codebrew.clikat.base.BaseInterface
import com.codebrew.clikat.modal.other.AddtoCartModel

interface CarDetailNavigator : BaseInterface {

    fun updateCart()

    fun addCart(cartdata: AddtoCartModel.CartdataBean)

    fun onFavStatus()
}
