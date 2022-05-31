package com.codebrew.clikat.data.preferences

import com.codebrew.clikat.data.model.others.GlobalTableDataHolder

interface PreferenceHelper {

    fun setkeyValue(key: String, value: Any)

    fun getKeyValue(key: String, type: String): Any?

    fun addGsonValue(key: String,value: String)

    fun <T>getGsonValue(key: String, type: Class<T>): T?

    fun logout()

    fun onClear()

    fun onCartClear()

    fun removeValue(key:String)

    fun getCurrentUserLoggedIn(): Boolean

    fun getLangCode():String

    fun isBranchFlow():Boolean

    fun isSubcriptionEnded():Boolean

    fun getCurrentTableData(): GlobalTableDataHolder?

}