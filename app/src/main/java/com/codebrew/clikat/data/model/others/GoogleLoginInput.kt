package com.codebrew.clikat.data.model.others

data class GoogleLoginInput(
    var deviceToken: String?=null,
    var deviceType: Int?=null,
    var email: String?=null,
    var googleToken: String?=null,
    var image: String?=null,
    var latitude: Double?=null,
    var longitude: Double?=null,
    var name: String?=null
)