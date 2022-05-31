package com.codebrew.clikat.module.dialog_adress.interfaces

import com.codebrew.clikat.data.model.api.AddressBean

interface AddressDialogListener {
    fun onAddressSelect(adrsBean: AddressBean) {}
    fun onDestroyDialog()
    fun onVehicleSelect(id: String, model: String, color: String, plate_no: String)
}