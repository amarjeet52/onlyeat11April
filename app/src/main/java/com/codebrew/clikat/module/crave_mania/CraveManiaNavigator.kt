package com.codebrew.clikat.module.crave_mania

import com.codebrew.clikat.base.BaseInterface
import com.codebrew.clikat.modal.other.SupplierDataBean

interface CraveManiaNavigator : BaseInterface {
    fun onSupplierList(data: CraveManiaData)
    fun onBannerList(data: List<BannerData>)
}