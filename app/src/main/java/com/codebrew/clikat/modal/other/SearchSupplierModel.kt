package com.codebrew.clikat.modal.other


data class SearchSupplierModel(

        var status: Int = 0,
        var message: Any? = null,
        var data: SearchSupplierData? = null
)
data class SearchSupplierData(
        var list: List<SupplierDataBean>? = null
)

