package com.codebrew.clikat.data.model.api

data class RequestListModel(
    val data: RequestItem,
    val message: String,
    val status: Int
)

data class RequestData(
        var created_at: String,
        val id: Int,
        val name: String,
        val prescription: String,
        val prescription_image: String,
        val reasons: String,
        var status: Double,
        val supplier_branch_id: Int,
        val updated_at: String,
        var updated_status: String,
        var cancelBtn:Boolean,
        val user_id: Int
)

data class RequestItem(
        val data: ArrayList<RequestData>,
        val totalCount: Int
)