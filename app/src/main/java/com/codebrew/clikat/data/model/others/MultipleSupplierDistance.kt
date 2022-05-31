package com.codebrew.clikat.data.model.others

data class MultipleSupplierDistance(
        var supplierUserLatLongs: List<SupplierUserLatLong>?=null
)

data class SupplierUserLatLong(
        val dest_latitude: Double,
        val dest_longitude: Double,
        val source_latitude: Double,
        val source_longitude: Double,
        val supplierId: Int
)