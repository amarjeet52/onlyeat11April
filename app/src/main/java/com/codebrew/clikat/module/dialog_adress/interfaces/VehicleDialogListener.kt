package com.codebrew.clikat.module.dialog_adress.interfaces

import com.codebrew.clikat.data.model.api.AddressBean
import com.codebrew.clikat.data.model.api.vehicleDetails.VehicleData

interface VehicleDialogListener {

    fun onVehicleSelect(model: String,color:String,plate_no:String)


}