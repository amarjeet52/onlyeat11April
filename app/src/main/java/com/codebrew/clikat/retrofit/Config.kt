package com.codebrew.clikat.retrofit


interface Config {

    companion object {

        var DB_SECRET_KEY = ""

        val senderNumber: String
            get() = "51465"

        var dbSecret: String = DB_SECRET_KEY
        set(value) {
            DB_SECRET_KEY=value
            field = value
        }

    }


}