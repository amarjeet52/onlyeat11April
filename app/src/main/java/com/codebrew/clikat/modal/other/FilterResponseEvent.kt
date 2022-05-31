package com.codebrew.clikat.modal.other


data class FilterResponseEvent(var filterModel:FilterVarientData, var productlist: ArrayList<ProductDataBean>, var status: String)
{
    constructor() : this(FilterVarientData(),arrayListOf(),"cancel")
}