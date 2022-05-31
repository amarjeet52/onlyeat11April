package com.codebrew.clikat.module.new_signup.enter_phone

import com.codebrew.clikat.base.BaseInterface
import com.codebrew.clikat.modal.PojoSignUp

interface PhoneNaviagtor : BaseInterface {

    fun onPhoneVerify(accessToken: String)
    fun onOtpVerify()
    fun updatePhone(it: PojoSignUp)
}