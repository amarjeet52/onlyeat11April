package com.codebrew.clikat.base

import com.codebrew.clikat.data.model.api.ListItem

interface BaseInterface {

    fun onErrorOccur(message: String)

    fun onSessionExpire()
}