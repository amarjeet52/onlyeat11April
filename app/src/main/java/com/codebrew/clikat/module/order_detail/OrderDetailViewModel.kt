package com.codebrew.clikat.module.order_detail

import androidx.lifecycle.MutableLiveData
import com.codebrew.clikat.app_utils.DateTimeUtils
import com.codebrew.clikat.app_utils.SocketManager
import com.codebrew.clikat.base.BaseViewModel
import com.codebrew.clikat.data.DataManager
import com.codebrew.clikat.data.constants.NetworkConstants
import com.codebrew.clikat.data.constants.PrefenceConstants
import com.codebrew.clikat.data.model.api.*
import com.codebrew.clikat.data.model.api.orderDetail.OrderDetailModel
import com.codebrew.clikat.data.model.api.orderDetail.OrderHistory
import com.codebrew.clikat.data.model.others.ChangeOrderStatus
import com.codebrew.clikat.data.model.others.EditOrderRequest
import com.codebrew.clikat.data.model.others.ReturnProductModel
import com.codebrew.clikat.data.network.ApiResponse
import com.codebrew.clikat.modal.CartInfoServerArray
import com.codebrew.clikat.modal.ExampleCommon
import com.codebrew.clikat.modal.ResponseZoomCall
import com.codebrew.clikat.modal.SocketInputParam
import com.codebrew.clikat.modal.other.AddtoCartModel
import com.codebrew.clikat.modal.other.OrderDetailParam
import com.codebrew.clikat.modal.other.ProductDataBean
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import io.socket.client.Ack
import io.socket.emitter.Emitter
import org.json.JSONObject
import timber.log.Timber
import java.text.SimpleDateFormat

class OrderDetailViewModel(dataManager: DataManager) : BaseViewModel<OrderDetailNavigator>(dataManager) {

    val orderDetailLiveData by lazy { MutableLiveData<List<OrderHistory>>() }
    val returnLiveData by lazy { MutableLiveData<ProductDataBean>() }


    private val socketManager
            by lazy {
                SocketManager.getInstance(dataManager.getKeyValue(PrefenceConstants.USER_ID, PrefenceConstants.TYPE_STRING).toString(),
                        dataManager.getKeyValue(PrefenceConstants.DB_SECRET, PrefenceConstants.TYPE_STRING).toString())
            }


    fun connectToSocket() {
        socketManager.connect(socketListener)
    }


    private val socketListener = Emitter.Listener { args ->
        if (args.isNotEmpty() && args[0] is JSONObject) {
            val response = Gson().fromJson<SuccessModel>(
                    args[0].toString(),
                    object : TypeToken<SuccessModel>() {}.type
            )

            if (response?.success == NetworkConstants.AUTHFAILED) {
                dataManager.logout()
                navigator.onSessionExpire()
            } else {
                Timber.e(response.message)
            }
        }
    }

    fun sendCurrentLoc(socketInputParam: SocketInputParam) {
        val request = JSONObject(Gson().toJson(socketInputParam))
        socketManager.emit(
                SocketManager.ON_LOCATION_UPDATE, request,
                Ack {
                    val acknowledgement = it.firstOrNull()
                    if (acknowledgement != null && acknowledgement is JSONObject) {

                        val response = Gson().fromJson<SuccessModel>(
                                acknowledgement.toString(),
                                object : TypeToken<SuccessModel>() {}.type
                        )
                        // AppLogger.e("location", "" +response.message)
                    }
                })
    }

    fun getOrderDetail(orderIds: List<Int>) {
        setIsLoading(true)

        val orderInput = OrderDetailParam(
                dataManager.getLangCode(),
                dataManager.getKeyValue(PrefenceConstants.ACCESS_TOKEN, PrefenceConstants.TYPE_STRING).toString(), orderIds)

        dataManager.orderDetails(orderInput)
                ?.observeOn(AndroidSchedulers.mainThread())
                ?.subscribeOn(Schedulers.io())
                ?.subscribe({ this.validateResponse(it) }, { this.handleError(it) })?.let {
                    compositeDisposable.add(it)
                }
    }

    fun makePayment(param: MakePaymentInput) {
        setIsLoading(true)
        dataManager.makePayment(param)
                .observeOn(AndroidSchedulers.mainThread())
                ?.subscribeOn(Schedulers.io())
                ?.subscribe({ this.makePaymentResponse(it) }, { this.handleError(it) })?.let {
                    compositeDisposable.add(it)
                }
    }

    fun getGeofenceTax(latitude: Double, longitude: Double, branchId: String) {
        // setIsLoading(true)
        dataManager.apiGetGeofencingTax(latitude, longitude, branchId)
                .observeOn(AndroidSchedulers.mainThread())
                ?.subscribeOn(Schedulers.io())
                ?.subscribe({ this.geofencePayment(it, true) }, { this.handleError(it) })?.let {
                    compositeDisposable.add(it)
                }
    }

    fun getPaymentGateways(latitude: Double, longitude: Double) {
        val param = hashMapOf("lat" to latitude.toString(),
                "long" to longitude.toString())

        // setIsLoading(true)
        dataManager.geofenceGateway(param)
                .observeOn(AndroidSchedulers.mainThread())
                ?.subscribeOn(Schedulers.io())
                ?.subscribe({ this.geofencePayment(it, false) }, { this.handleError(it) })?.let {
                    compositeDisposable.add(it)
                }
    }

    fun apiChangeOrderStatus(order_id: String, status: Double?, instructions: String?, is_automatic: String?) {
        val orderStatus = ChangeOrderStatus()
        orderStatus.order_id= order_id
        orderStatus.status = status.toString()
        orderStatus.offset = SimpleDateFormat("ZZZZZ", DateTimeUtils.timeLocale).format(System.currentTimeMillis())
        if (!instructions.isNullOrEmpty())
            orderStatus.parking_instructions = instructions
        if (!is_automatic.isNullOrEmpty())
            orderStatus.is_automatic = is_automatic
        setIsLoading(true)
        dataManager.changeOrderStatus(orderStatus)
                .observeOn(AndroidSchedulers.mainThread())
                ?.subscribeOn(Schedulers.io())
                ?.subscribe({ this.validateResponse(it, status) }, { this.handleError(it) })?.let {
                    compositeDisposable.add(it)
                }
    }

    private fun validateResponse(it: SuccessModel?, status: Double?) {

        setIsLoading(false)
        when (it?.statusCode) {
            NetworkConstants.SUCCESS -> {
                navigator.onChangeStatus(status)
            }
            NetworkConstants.AUTHFAILED -> {
                dataManager.setUserAsLoggedOut()
                navigator.onSessionExpire()
            }
            else -> {
                it?.message.let { it1 -> navigator.onErrorOccur(it1 ?: "") }
            }
        }
    }

    private fun geofencePayment(it: ApiResponse<GeofenceData>?, isGeofenceTax: Boolean) {

        //setIsLoading(false)
        when (it?.status) {
            NetworkConstants.SUCCESS -> {
                navigator.onGeofencePayment(it.data, isGeofenceTax)
            }
            NetworkConstants.AUTHFAILED -> {
                dataManager.setUserAsLoggedOut()
                navigator.onSessionExpire()
            }
            else -> {
                it?.msg.let { it1 -> navigator.onErrorOccur(it1.toString()) }
            }
        }
    }


    fun remainingPayment(param: MakePaymentInput) {
        setIsLoading(true)
        dataManager.remainingPayment(param)
                .observeOn(AndroidSchedulers.mainThread())
                ?.subscribeOn(Schedulers.io())
                ?.subscribe({ this.remainingResponse(it) }, { this.handleError(it) })?.let {
                    compositeDisposable.add(it)
                }
    }

    fun apiTrackDhl(orderId: String) {
        setIsLoading(true)
        dataManager.trackDhl(orderId)
                .observeOn(AndroidSchedulers.mainThread())
                ?.subscribeOn(Schedulers.io())
                ?.subscribe({ this.responseTrackDhl(it) }, { this.handleError(it) })?.let {
                    compositeDisposable.add(it)
                }
    }

    private fun responseTrackDhl(it: ResponseTrackDhl) {
        setIsLoading(false)
        when (it.status) {
            NetworkConstants.SUCCESS -> {
                navigator.onTrackDhl(it.data)
            }
            NetworkConstants.AUTHFAILED -> {
                dataManager.setUserAsLoggedOut()
                navigator.onSessionExpire()
            }
            else -> {
                it.message.let { it1 -> navigator.onErrorOccur(it1 ?: "") }
            }
        }
    }

    fun apiTrackShipRocket(orderId: String) {
        setIsLoading(true)
        dataManager.apiTrackShipRocket(orderId)
                .observeOn(AndroidSchedulers.mainThread())
                ?.subscribeOn(Schedulers.io())
                ?.subscribe({ this.responseTrackShipRocket(it) }, { this.handleError(it) })?.let {
                    compositeDisposable.add(it)
                }
    }

    private fun responseTrackShipRocket(it: ResponseTrackDhl) {
        setIsLoading(false)
        when (it.status) {
            NetworkConstants.SUCCESS -> {
                navigator.onTrackShipRocket(it.data)
            }
            NetworkConstants.AUTHFAILED -> {
                dataManager.setUserAsLoggedOut()
                navigator.onSessionExpire()
            }
            else -> {
                it.message.let { it1 -> navigator.onErrorOccur(it1 ?: "") }
            }
        }
    }

    fun apiCheckZoomAuth(orderHistory: OrderHistory) {
        setIsLoading(true)
        dataManager.checkZoomAuth()
                .observeOn(AndroidSchedulers.mainThread())
                ?.subscribeOn(Schedulers.io())
                ?.subscribe({ this.zoomAuthResponse(it, orderHistory) }, { this.handleError(it) })?.let {
                    compositeDisposable.add(it)
                }
    }

    private fun zoomAuthResponse(it: ResponseTrackDhl, orderHistory: OrderHistory) {
        setIsLoading(false)
        when (it.status) {
            NetworkConstants.SUCCESS -> {
                it.data?.order_id = orderHistory.order_id.toString()
                navigator.zoomAuth(it.data)
            }
            NetworkConstants.AUTHFAILED -> {
                dataManager.setUserAsLoggedOut()
                navigator.onSessionExpire()
            }
            else -> {
                it.message.let { it1 -> navigator.onErrorOccur(it1 ?: "") }
            }
        }
    }


    fun apiCreateZoomLink(data: TrackDhl?) {
        setIsLoading(true)
        val hashMap = HashMap<String, String>()
        hashMap["token"] = data?.token ?: ""
        hashMap["zoom_email"] = data?.zoom_email ?: ""
        hashMap["order_id"]=data?.order_id?:""
        dataManager.createZoomLink(hashMap)
                .observeOn(AndroidSchedulers.mainThread())
                ?.subscribeOn(Schedulers.io())
                ?.subscribe({ this.zoomLinkResponse(it) }, { this.handleError(it) })?.let {
                    compositeDisposable.add(it)
                }
    }

    private fun zoomLinkResponse(it: ResponseZoomCall) {
        setIsLoading(false)
        when (it.status) {
            NetworkConstants.SUCCESS -> {
                navigator.zoomCallLink(it.data)
            }
            NetworkConstants.AUTHFAILED -> {
                dataManager.setUserAsLoggedOut()
                navigator.onSessionExpire()
            }
            else -> {
                it.message.let { it1 -> navigator.onErrorOccur(it1 ?: "") }
            }
        }
    }

    fun addCart(cartInfo: CartInfoServerArray) {
        setIsLoading(true)

        cartInfo.accessToken = dataManager.getKeyValue(PrefenceConstants.ACCESS_TOKEN, PrefenceConstants.TYPE_STRING).toString()
        cartInfo.cartId = "0"
        cartInfo.remarks = "0"

        dataManager.getAddToCart(cartInfo)
                ?.observeOn(AndroidSchedulers.mainThread())
                ?.subscribeOn(Schedulers.io())
                ?.subscribe({ this.addCartInfo(it) }, { this.handleError(it) })?.let {
                    compositeDisposable.add(it)
                }
    }

    fun cancelOrder(orderId: String, cancelToWallet: Int) {
        setIsLoading(true)

        val hashMap = hashMapOf("orderId" to orderId,
                "languageId" to dataManager.getLangCode(),
                "cancel_to_wallet" to cancelToWallet.toString(),
                "accessToken" to dataManager.getKeyValue(PrefenceConstants.ACCESS_TOKEN, PrefenceConstants.TYPE_STRING).toString())

        dataManager.cancelOrder(hashMap)
                ?.observeOn(AndroidSchedulers.mainThread())
                ?.subscribeOn(Schedulers.io())
                ?.subscribe({ this.cancelResponse(it) }, { this.handleError(it) })?.let {
                    compositeDisposable.add(it)
                }
    }

    fun apiReturnProduct(item: ProductDataBean?, reason: String, refundToWallet: Int) {

        val body = ReturnProductModel(order_price_id = item?.order_price_id.toString(),
                product_id = item?.product_id.toString(),
                reason = reason, refund_to_wallet = refundToWallet)

        setIsLoading(true)
        dataManager.returnProduct(body)
                ?.observeOn(AndroidSchedulers.mainThread())
                ?.subscribeOn(Schedulers.io())
                ?.subscribe({ this.validateReturnResponse(it, item) }, { this.handleError(it) })?.let {
                    compositeDisposable.add(it)
                }
    }


    private fun makePaymentResponse(it: SuccessModel) {
        setIsLoading(false)
        when (it.statusCode) {
            NetworkConstants.SUCCESS -> {
                navigator.onCompletePayment()
            }
            NetworkConstants.AUTHFAILED -> {
                dataManager.setUserAsLoggedOut()
                navigator.onSessionExpire()
            }
            else -> {
                it.message.let { it1 -> navigator.onErrorOccur(it1) }
            }
        }
    }

    fun getSaddedPaymentUrl(email: String, name: String, amount: String) {
        setIsLoading(true)
        dataManager.getSaddedPaymentUrl(email, name, amount)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe({ this.paymentSadedResponse(it) }, { this.handleError(it) })?.let {
                    compositeDisposable.add(it)
                }
    }

    private fun paymentSadedResponse(it: AddCardResponse?) {
        setIsLoading(false)
        when (it?.status) {
            NetworkConstants.SUCCESS -> {
                navigator.getSaddedPaymentSuccess(it.data)
            }
            NetworkConstants.AUTHFAILED -> {
                dataManager.setUserAsLoggedOut()
                navigator.onSessionExpire()
            }
            else -> {
                it?.message?.let { it1 -> navigator.onErrorOccur(it1) }
            }
        }
    }

    fun getMyFatoorahPaymentUrl(currency: String, amount: String) {
        setIsLoading(true)
        dataManager.getMyFatoorahPaymentUrl(currency, amount)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe({ this.paymentMyFatoorahResponse(it) },
                        { this.handleError(it) })?.let {
                    compositeDisposable.add(it)
                }
    }

    private fun paymentMyFatoorahResponse(it: AddCardResponse?) {
        setIsLoading(false)
        when (it?.status) {
            NetworkConstants.SUCCESS -> {
                navigator.getMyFatoorahPaymentSuccess(it.data)
            }
            NetworkConstants.AUTHFAILED -> {
                dataManager.setUserAsLoggedOut()
                navigator.onSessionExpire()
            }
            else -> {
                it?.message?.let { it1 -> navigator.onErrorOccur(it1) }
            }
        }
    }

    private fun remainingResponse(it: SuccessModel) {
        setIsLoading(false)
        when (it.statusCode) {
            NetworkConstants.SUCCESS -> {
                navigator.onCompletePayment()
            }
            NetworkConstants.AUTHFAILED -> {
                dataManager.setUserAsLoggedOut()
                navigator.onSessionExpire()
            }
            else -> {
                it.message.let { it1 -> navigator.onErrorOccur(it1) }
            }
        }
    }


    private fun cancelResponse(it: ExampleCommon?) {

        setIsLoading(false)

        if (it?.status == NetworkConstants.SUCCESS) {
            navigator.onCancelOrder()
        } else {
            it?.message?.let { it1 -> navigator.onErrorOccur(it1) }
        }
    }


    private fun addCartInfo(it: AddtoCartModel?) {

        setIsLoading(false)

        if (it?.status == NetworkConstants.SUCCESS) {
            //    dataManager.setkeyValue(DataNames.CART_ID,it.cartdata.cartId)
            navigator.onCartAdded(it.data)
        } else {
            it?.message?.let { it1 -> navigator.onErrorOccur(it1) }
        }

    }


    private fun validateResponse(it: OrderDetailModel?) {
        setIsLoading(false)
        if (it?.status == NetworkConstants.SUCCESS) {
            if (it.data?.orderHistory?.isNotEmpty() == true) {
                orderDetailLiveData.value = it.data.orderHistory
            }
        } else if (it?.status == NetworkConstants.AUTHFAILED) {
            dataManager.setUserAsLoggedOut()
            navigator.onSessionExpire()
        } else {
            it?.message?.let { it1 -> navigator.onErrorOccur(it1) }
        }
    }

    private fun validateReturnResponse(it: OrderDetailModel?, item: ProductDataBean?) {
        setIsLoading(false)
        if (it?.status == NetworkConstants.SUCCESS) {
            returnLiveData.value = item
        } else if (it?.status == NetworkConstants.AUTHFAILED) {
            dataManager.setUserAsLoggedOut()
            navigator.onSessionExpire()
        } else {
            it?.message?.let { it1 -> navigator.onErrorOccur(it1) }
        }
    }

    fun editOrderProducts(orderRequest: EditOrderRequest) {
        setIsLoading(true)
        compositeDisposable.add(
                dataManager.apiEditOrder(orderRequest)
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeOn(Schedulers.io())
                        .subscribe({ this.validateEditOrderResponse(it) },
                                { this.handleError(it) })
        )
    }

    private fun validateEditOrderResponse(it: OrderDetailModel?) {
        setIsLoading(false)

        if (it?.status == NetworkConstants.SUCCESS) {
            navigator.editOrderResponse(it.data)
        } else if (it?.status == NetworkConstants.AUTHFAILED) {
            dataManager.setUserAsLoggedOut()
            navigator.onSessionExpire()
        } else {
            it?.message?.let { it1 -> navigator.onErrorOccur(it1) }
        }
    }

    fun apiSos(orderId: String?) {
        val address = dataManager.getGsonValue(PrefenceConstants.ADRS_DATA, AddressBean::class.java)
        setIsLoading(true)
        val mParam = HashMap<String?, String?>()
        mParam["user_id"] = dataManager.getKeyValue(PrefenceConstants.USER_ID, PrefenceConstants.TYPE_STRING).toString()
        mParam["device_type"] = "0" //for android 0
        mParam["latitude"] = address?.latitude ?: "0.0"
        mParam["longitude"] = address?.longitude ?: "0.0"
        mParam["address"] = if (!address?.address_line_1.isNullOrEmpty()) address?.address_line_1 else ""
        mParam["order_id"] = orderId

        dataManager.apiSos(mParam)
                ?.observeOn(AndroidSchedulers.mainThread())
                ?.subscribeOn(Schedulers.io())
                ?.subscribe({ this.validateResponse(it) }, { this.handleError(it) })?.let {
                    compositeDisposable.add(it)
                }
    }

    private fun validateResponse(it: ExampleCommon?) {
        setIsLoading(false)
        if (it?.status == NetworkConstants.SUCCESS) {
            navigator.onSosSuccess()
        } else {
            it?.message?.let { it1 -> navigator.onErrorOccur(it1) }
        }
    }

    private fun handleError(e: Throwable) {
        setIsLoading(false)
        handleErrorMsg(e).let {
            if (it == NetworkConstants.AUTH_MSG) {
                dataManager.setUserAsLoggedOut()
                navigator.onSessionExpire()
            } else {
                navigator.onErrorOccur(it)
            }
        }
    }

}
