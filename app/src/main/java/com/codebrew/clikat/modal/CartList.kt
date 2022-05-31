package com.codebrew.clikat.modal

/*
 * Created by cbl45 on 7/5/16.
 */
class CartList (
    var cartInfos: MutableList<CartInfo> ?=null,
    var supplierName:String?=null,
    var supplierImage :String?=null,
    var supplierId :String?=null,
    var supplierBranchId :String?=null,
    var categoryID: Int ?=null
){
    constructor() : this(mutableListOf(),"","","","",0)
}
