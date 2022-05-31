package com.codebrew.clikat.module.home_screen.listeners

import com.codebrew.clikat.modal.other.FiltersSupplierList

interface OnSortByListenerClicked {
    fun onItemSelected(type:Int)
    fun onPrepTimeSelected(filters: FiltersSupplierList)
}