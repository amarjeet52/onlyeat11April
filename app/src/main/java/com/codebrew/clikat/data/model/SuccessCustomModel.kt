package com.codebrew.clikat.data.model

import com.google.gson.annotations.SerializedName

data class SuccessCustomModel(
    val message: Any,
    val data: Any,
    @SerializedName("status",alternate = ["statusCode"])
    val statusCode: Int,
    val success: Int
)

