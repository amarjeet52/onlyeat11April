package com.codebrew.clikat.modal.crave_mania

import com.codebrew.clikat.module.restaurant_detail.Final
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class CraveManiaResponseData {
        @SerializedName("status")
        @Expose
        var status:Int?=null
        @SerializedName("message")
        @Expose
        var message:String?=null
        @SerializedName("data")
        @Expose
        var data: Data?=null

}

class Data {
    @SerializedName("final")
    @Expose
    var final:ArrayList<Final>? = ArrayList()
}