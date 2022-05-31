package com.codebrew.clikat.modal.crave_mania

interface CraveManiaNavigator {
    fun onSessionExpire()
   fun onErrorOccur(it: String)
    fun onCraveManiaRestaurantResponse(it: CraveManiaResponseData?)
}
