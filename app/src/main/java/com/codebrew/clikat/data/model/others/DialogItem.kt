package com.codebrew.clikat.data.model.others

import com.codebrew.clikat.data.model.api.AddressBean
import com.codebrew.clikat.modal.other.ProductDataBean

data class DialogItem(
        val items: List<ProductDataBean>
)


data class DialogDataItem(val dialogType: Int, val items: List<ProductDataBean>,
                          val address: List<AddressBean>, val popUpItem: PopUpItem, val message: String)


data class PopUpItem(val msg: String? = null, val acceptMsg: String? = null, val rejectMsg: String? = null, val isBillingAmt: Boolean? = null,
                     val totalPrice: Float? = null, val totalTax: Float? = null, val totalDelivery: Float? = null, val type: Int? = null)