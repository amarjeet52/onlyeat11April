package com.codebrew.clikat.data.network

import com.google.gson.annotations.SerializedName

class ApiMsgResponse<T> {
    val success: Int? = null
    val status: Int? = null
    @SerializedName("message",alternate = ["msg"])
    val msg: Any? = null
    val data: T? = null
}