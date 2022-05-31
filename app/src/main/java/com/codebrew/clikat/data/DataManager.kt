package com.codebrew.clikat.data

import com.codebrew.clikat.data.model.others.UserSubInfoParam
import com.codebrew.clikat.data.network.RestService
import com.codebrew.clikat.data.preferences.PreferenceHelper
import retrofit2.Retrofit


interface DataManager : PreferenceHelper, RestService {

    fun setUserAsLoggedOut()

    fun updateUserInf():HashMap<String,String>

fun updateUserInfnew():HashMap<String,String>

fun updateApiHeader(userId: Long?, accessToken: String)

    fun getRetrofitUtl():Retrofit

    fun getUserSubscData(): UserSubInfoParam
}
