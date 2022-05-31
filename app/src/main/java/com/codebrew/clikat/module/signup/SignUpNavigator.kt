package com.codebrew.clikat.module.signup

import com.codebrew.clikat.base.BaseInterface
import com.codebrew.clikat.modal.Data1

interface SignUpNavigator : BaseInterface {

    fun onSignUpFirst(data: Data1)

    fun onSignUpPhone()
}
