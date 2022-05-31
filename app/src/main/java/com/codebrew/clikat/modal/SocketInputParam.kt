package com.codebrew.clikat.modal


data class SocketInputParam(
    var latitude: String?=null,
    var longitude: String?=null,
    var order_id: String?=null,
    var user_id: String?=null,
    var secret_key: String?=null)
{
    constructor() : this("","","","" ,"")
}

