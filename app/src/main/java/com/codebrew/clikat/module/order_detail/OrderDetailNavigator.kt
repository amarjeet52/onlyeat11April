package com.codebrew.clikat.module.order_detail

import com.codebrew.clikat.base.BaseInterface
import com.codebrew.clikat.data.model.api.AddCardResponseData
import com.codebrew.clikat.data.model.api.GeofenceData
import com.codebrew.clikat.data.model.api.TrackDhl
import com.codebrew.clikat.data.model.api.orderDetail.Data
import com.codebrew.clikat.modal.DataZoom
import com.codebrew.clikat.modal.other.AddtoCartModel

interface OrderDetailNavigator : BaseInterface {

    fun onCartAdded(cartdata: AddtoCartModel.CartdataBean?)
    fun onCancelOrder()
    fun editOrderResponse(data: Data?)
    fun onCompletePayment()
    fun onTrackDhl(data: TrackDhl?)
    fun onTrackShipRocket(data: TrackDhl?)
    fun zoomAuth(data: TrackDhl?)
    fun zoomCallLink(data: DataZoom?)
    fun onChangeStatus(status: Double?)
    fun onGeofencePayment(data: GeofenceData?, geofenceTax: Boolean)
    fun getSaddedPaymentSuccess(data: AddCardResponseData?)
    fun getMyFatoorahPaymentSuccess(data: AddCardResponseData?)
    fun onSosSuccess()
}