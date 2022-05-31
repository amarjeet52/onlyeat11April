package com.codebrew.clikat.module.tables

import com.codebrew.clikat.modal.BookedTableItem

interface TablesRecyclerActionListener {

    fun onInviteFriendClicked(itemModel: BookedTableItem?)
    fun onAddItemsInCartClicked(itemModel: BookedTableItem?)
    fun onOnMyWayClicked(itemModel: BookedTableItem?)
}