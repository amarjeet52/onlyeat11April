package com.codebrew.clikat.module.main_screen

import com.codebrew.clikat.data.model.api.orderDetail.OrderHistory
import com.codebrew.clikat.data.model.others.EditProductsItem

interface OnOrderEdited {
    fun onRestDetailEdited(orderItem:OrderHistory?,selectedList:ArrayList<EditProductsItem>?)
}