package com.codebrew.clikat.module.banners

import com.codebrew.clikat.base.BaseInterface
import com.codebrew.clikat.data.model.api.supplier_detail.DataSupplierDetail

interface BannersListNavigator : BaseInterface {

    fun supplierDetailSuccess(data: DataSupplierDetail)
}
