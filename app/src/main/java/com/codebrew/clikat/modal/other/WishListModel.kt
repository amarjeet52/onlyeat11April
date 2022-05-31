package com.codebrew.clikat.modal.other

data class WishListModel(
    val data: MutableList<ProductDataBean>,
    val message: String,
    val status: Int
)