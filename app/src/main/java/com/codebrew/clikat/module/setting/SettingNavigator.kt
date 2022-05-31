package com.codebrew.clikat.module.setting

import com.codebrew.clikat.base.BaseInterface

interface SettingNavigator : BaseInterface {

    fun onNotifiChange(message: String){}
    fun pandaDocSuccess(){}
    fun onNotiLangChange(message: String, langCode: String)
    fun onProfileUpdate(){}
    fun onUploadPic(message: String, image: String){}
}