package com.codebrew.clikat.module.new_signup.otp_verify

import com.codebrew.clikat.base.BaseInterface

interface OtpNavigator : BaseInterface {

    fun onOtpVerify()

    fun onResendOtp(message: String)
}