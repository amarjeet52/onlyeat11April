package com.codebrew.clikat.data.model.others

data class CancelRequest(
        var id: String,
        var reason: String)



data class ViewProduct(
        var productId: Int)