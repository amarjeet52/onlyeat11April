package com.codebrew.clikat.module.new_signup.signup

import com.codebrew.clikat.base.BaseInterface

interface RegisterNavigator : BaseInterface {
    fun onRegisterSuccess(accessToken: String,message: String)
    fun onOtpVerify()
}