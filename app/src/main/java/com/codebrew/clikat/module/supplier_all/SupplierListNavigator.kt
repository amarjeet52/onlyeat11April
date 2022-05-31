package com.codebrew.clikat.module.supplier_all

import com.codebrew.clikat.modal.SupplierList

interface SupplierListNavigator {
    fun onErrorOccur(message: String)
    fun onSessionExpire()
    fun unFavSupplierResponse(data: SupplierList?)
    fun favSupplierResponse(supplierId: SupplierList?)
    fun onBranchList(supplierList: MutableList<SupplierList>)
}
