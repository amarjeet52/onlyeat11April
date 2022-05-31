package com.codebrew.clikat.module.dialog_adress

import com.codebrew.clikat.base.BaseInterface
import com.codebrew.clikat.data.model.api.ZoneInformation

interface AddressNavigator : BaseInterface
{
    fun onZoneData(zoneInf: ZoneInformation?, isAddressUpdate: Boolean){}
}
