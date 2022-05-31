package com.codebrew.clikat.modal.other

data class UpdateFilterEvent(var filterPos:Int,var clearType:Boolean)
{
    constructor() : this(0,false)
}