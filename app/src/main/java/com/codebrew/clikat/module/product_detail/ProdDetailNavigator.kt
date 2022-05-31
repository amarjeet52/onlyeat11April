package com.codebrew.clikat.module.product_detail

import com.codebrew.clikat.base.BaseInterface
import com.codebrew.clikat.modal.CartInfoServer
import com.codebrew.clikat.modal.other.AddtoCartModel
import java.util.*

interface ProdDetailNavigator : BaseInterface {

    fun onCartAdded(cartdata: AddtoCartModel.CartdataBean?, productList: ArrayList<CartInfoServer>, message: String)

    fun onFavStatus(prodStatus: Int?)
}
