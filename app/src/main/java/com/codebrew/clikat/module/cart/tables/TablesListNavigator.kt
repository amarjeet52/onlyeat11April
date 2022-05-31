package com.codebrew.clikat.module.cart.tables

import com.codebrew.clikat.base.BaseInterface
import com.codebrew.clikat.data.model.api.ListItem

interface TablesListNavigator : BaseInterface {

    fun onTableListReceived(list: List<ListItem?>?)
    fun onTableSuccessfullyBooked()
}