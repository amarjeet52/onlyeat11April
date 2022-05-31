package com.codebrew.clikat.data.model.others

data class SuplierDeliveryCharge(
        var deliveryCharge: Float? = null,
        var tax: Float? = null,
        var handling_admin: Float? = null,
        var supplierId: Int? = null,
        var latitude: Double? = null,
        var longitude: Double? = null
)
