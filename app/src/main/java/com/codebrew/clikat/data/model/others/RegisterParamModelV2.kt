package com.codebrew.clikat.data.model.others

data class RegisterParamModelV2(
        var email: String?,
        var deviceToken: String?,
        var deviceType: Int,
        var areaId: Int,
        var password: String?,
        var languageId: Int)