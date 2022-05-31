package com.codebrew.clikat.data.model.others

data class SendMsgModel(
        val data: Data,
        val message: Any,
        val status: Int
)


data class Detail(
        val chat_type: String,
        val order_id: String,
        val receiver_created_id: String,
        val sent_at: String,
        val text: String,
        val type: String
)

data class Data(
        val detail: Detail
)