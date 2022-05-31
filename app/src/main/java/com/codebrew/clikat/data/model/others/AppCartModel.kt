package com.codebrew.clikat.data.model.others

data class AppCartModel(var supplierName: String,var totalCount:Float,
                        var totalPrice:String,var cartAvail:Boolean){
    constructor() : this("",0f,"",false)
}