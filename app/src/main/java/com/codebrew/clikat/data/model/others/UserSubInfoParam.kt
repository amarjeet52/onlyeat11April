package com.codebrew.clikat.data.model.others

data class UserSubInfoParam(
        var db_secret_key: String?=null,
        var key_value: KeyValue?=null,
        var user_id: Int?=null,
        var platform: String?=null
)

data class KeyValue(
        var bundleId: String?=null,
        var deviceType: String?=null
)