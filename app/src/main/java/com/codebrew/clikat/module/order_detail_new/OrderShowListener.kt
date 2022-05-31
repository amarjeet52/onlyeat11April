package com.codebrew.clikat.module.order_detail_new

interface OrderShowListener {
    fun onShowDialog()
    fun onDismissDialog()
    fun onCallSupplier(phoneNumber:String)
}