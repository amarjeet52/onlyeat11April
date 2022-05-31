package com.codebrew.clikat.module.recording

import com.codebrew.clikat.base.BaseInterface
import com.codebrew.clikat.data.model.api.AddCardResponseData
import com.codebrew.clikat.data.model.api.GeofenceData
import com.codebrew.clikat.data.model.api.TrackDhl
import com.codebrew.clikat.data.model.api.orderDetail.Data
import com.codebrew.clikat.modal.DataZoom
import com.codebrew.clikat.modal.other.AddtoCartModel

interface RecordAudioNavigator  : BaseInterface {


    fun onSosSuccess()

}