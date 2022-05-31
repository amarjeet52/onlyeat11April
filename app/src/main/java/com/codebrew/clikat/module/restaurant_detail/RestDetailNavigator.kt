package com.codebrew.clikat.module.restaurant_detail

import com.codebrew.clikat.base.BaseInterface

interface RestDetailNavigator : BaseInterface {

    fun favResponse(){}

    fun unFavResponse(){}

    fun onTableSuccessfullyBooked(){}


}
