package com.codebrew.clikat.module.signature

import com.codebrew.clikat.base.BaseInterface
import com.codebrew.clikat.modal.other.SupplierDataBean

interface SignatureNavigator: BaseInterface {
    fun onSupplierList(data: List<SupplierDataBean>?)
}