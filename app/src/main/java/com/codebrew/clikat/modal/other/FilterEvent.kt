package com.codebrew.clikat.modal.other

data class FilterEvent(var type: String, var catId: Int, var name: String, var position: Int,var subcatList: List<SubCategory>? = null) {
    constructor() : this("", 0, "", 0, emptyList())
}