package com.codebrew.clikat.module.login

import com.codebrew.clikat.base.BaseInterface
import com.codebrew.clikat.modal.Data1
import com.codebrew.clikat.modal.PojoSignUp

interface LoginNavigator : BaseInterface {
    fun onForgotPswr(message: String)
    fun onLogin(userData: Data1)
    fun onSocialLogin(signup: PojoSignUp?,type:String)
    fun userNotVerified(userData: Data1)
    fun registerByPhone()
}