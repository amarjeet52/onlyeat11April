package com.codebrew.clikat.data.model.api

import com.codebrew.clikat.modal.Address
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class SavedCardData(
        val status: Int,
        val message: String,
        val data: Data1
)

data class Data1(
        val id: String,
        val fullName: String,
        val email: String,
        val creditCards: MutableList<SavedCardList> ?=null
)
