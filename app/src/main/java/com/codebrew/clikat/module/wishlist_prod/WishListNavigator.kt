package com.codebrew.clikat.module.wishlist_prod

interface WishListNavigator {

    fun onErrorOccur(message: String)

    fun onSessionExpire()

    fun onFavStatus(position: Int?)

}
