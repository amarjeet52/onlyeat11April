package com.codebrew.clikat.module.tables

import com.codebrew.clikat.base.BaseInterface
import com.codebrew.clikat.modal.BookedTableItem

interface TableBookingListNavigator  : BaseInterface {
    fun onTableListReceived(list: List<BookedTableItem?>?)
}