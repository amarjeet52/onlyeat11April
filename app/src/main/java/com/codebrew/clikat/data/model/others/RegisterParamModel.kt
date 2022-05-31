package com.codebrew.clikat.data.model.others

data class RegisterParamModel(var first_name: String, var last_name: String, var mobileNumber: Long,
                              var countryCode: String, var email: String, var deviceType: String, var deviceToken: String,
                              var password: String, var languageId: String, var longitude:Double,var latitude:Double)