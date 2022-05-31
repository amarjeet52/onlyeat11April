package com.codebrew.clikat.modal.other.more_list

data class MoreListModel(var name: String, var image: Int,val large_image:Int?=null) {
    constructor() : this("", 0)
}