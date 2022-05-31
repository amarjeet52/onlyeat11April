package com.codebrew.clikat.module.cart

import android.util.JsonReader
import android.util.Log
import android.widget.Toast
import androidx.databinding.ObservableBoolean
import androidx.databinding.ObservableInt
import androidx.lifecycle.MutableLiveData
import com.codebrew.clikat.BuildConfig
import com.codebrew.clikat.app_utils.AppUtils
import com.codebrew.clikat.app_utils.CommonUtils
import com.codebrew.clikat.app_utils.DateTimeUtils
import com.codebrew.clikat.app_utils.SingleLiveEvent
import com.codebrew.clikat.base.BaseViewModel
import com.codebrew.clikat.data.DataManager
import com.codebrew.clikat.data.FoodAppType

import com.codebrew.clikat.data.constants.AppConstants
import com.codebrew.clikat.data.constants.NetworkConstants
import com.codebrew.clikat.data.constants.PrefenceConstants
import com.codebrew.clikat.data.model.api.*
import com.codebrew.clikat.data.model.api.Currency
import com.codebrew.clikat.data.model.api.DataBean
import com.codebrew.clikat.data.model.api.SuccessModel
import com.codebrew.clikat.data.model.api.distance_matrix.DistanceMatrix
import com.codebrew.clikat.data.model.api.tap_payment.TapPaymentModel
import com.codebrew.clikat.data.model.api.vehicleDetails.*
import com.codebrew.clikat.data.model.dataModel.LocationModel
import com.codebrew.clikat.data.model.dataModel.NewData
import com.codebrew.clikat.data.model.others.*
import com.codebrew.clikat.data.network.ApiMsgResponse
import com.codebrew.clikat.data.network.ApiResponse
import com.codebrew.clikat.modal.*
import com.codebrew.clikat.modal.PaymentModel.PayModel
import com.codebrew.clikat.modal.other.*
import com.codebrew.clikat.modal.slots.SupplierSlots
import com.codebrew.clikat.modal.wallet.Data
import com.codebrew.clikat.modal.wallet.WalletTransactionsResonse
import com.codebrew.clikat.module.cart.schedule_order.CraveScheduleModel
import com.codebrew.clikat.preferences.DataNames
import com.google.android.gms.maps.model.LatLng
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import java.lang.reflect.Type
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.HashMap


class CartViewModel(dataManager: DataManager) : BaseViewModel<CartNavigator>(dataManager) {

    val liveUserDeatil: MutableLiveData<List<VehicleData>> by lazy { MutableLiveData<List<VehicleData>>() }

    val slotLiveData by lazy { SingleLiveEvent<List<String>>() }

    val savedCardLiveData by lazy { SingleLiveEvent<List<CardData>>() }

    val liveDataLoc by lazy { SingleLiveEvent<DataBean>() }
    val cancelLiveData by lazy { SingleLiveEvent<DataCommon>() }
    val wallet by lazy { SingleLiveEvent<Data>() }
    val showWalletLoader = ObservableBoolean(false)
    val addMoneyToWallet by lazy { MutableLiveData<Any>() }
    val imageLiveData: MutableLiveData<String> by lazy {
        MutableLiveData<String>()
    }

    val vehicleresponseLiveData: MutableLiveData<List<PostData>> by lazy { MutableLiveData<List<PostData>>() }
    val vehicleDeleteLiveData: MutableLiveData<DeleteData> by lazy { MutableLiveData<DeleteData>() }


    val isCartList = ObservableInt(0)
    val showMumyBeneLoader = ObservableBoolean(false)


    val tableNumberObserver by lazy {
        MutableLiveData<List<TableItem?>>()
    }

    var craveScheduleModel: CraveScheduleModel? = null


    fun generateToken(message: String) {
        setIsLoading(true)

        CommonUtils.changebaseUrl(dataManager.getRetrofitUtl(), BuildConfig.BASE_URL)
        dataManager.getDialogToken()
                .observeOn(AndroidSchedulers.mainThread())
                ?.subscribeOn(Schedulers.io())
                ?.subscribe({ this.tokenResponse(it, message) }, { this.handleError(it) })?.let {
                    compositeDisposable.add(it)
                }
    }

    private fun tokenResponse(it: ApiResponse<DialogTokenData>?, message: String) {
        setIsLoading(false)

        when (it?.status) {
            NetworkConstants.SUCCESS -> {
                navigator.onTokenGenerate(it.data?.token ?: "", message)
            }
            else -> {
                it?.msg?.let { it1 -> navigator.onErrorOccur(it1.toString()) }
            }
        }
    }

    fun getSaveCardNew() {
        setIsLoading(true)


        val hashMap = HashMap<String,String>()
        hashMap["accessToken"]=dataManager.getKeyValue(PrefenceConstants.ACCESS_TOKEN, PrefenceConstants.TYPE_STRING).toString()
        compositeDisposable.add(dataManager.getSavedCards(hashMap)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(
                        {
                            this.validatecardDAta(it)
                        },
                        { this.handleError(it) })
        )

    }
    private fun validatecardDAta(it: ApiResponse<List<CardData>>?) {

        setIsLoading(false)


        when (it?.status) {
            NetworkConstants.SUCCESS_CODE -> {

                savedCardLiveData.value = it.data

            }
            NetworkConstants.AUTHFAILED -> {
                navigator.onSessionExpire()
            }
            else -> {
                navigator.onErrorOccur(it?.msg.toString())
            }

        }
    }

    fun checkTimeSlots(time:String) {
        setIsLoading(true)
var param = HashMap<String,String>()
param["end_time"]=time?:""
        param["accessToken"]  = dataManager.getKeyValue(PrefenceConstants.ACCESS_TOKEN, PrefenceConstants.TYPE_STRING).toString()

        dataManager.getTimeSlots(param)
                .observeOn(AndroidSchedulers.mainThread())
                ?.subscribeOn(Schedulers.io())
                ?.subscribe({ this.slotsResponse(it) }, { this.handleSubError(it) })?.let {
                    compositeDisposable.add(it)
                }
    }

    private fun slotsResponse(it: TimeSlotModel) {
        setIsLoading(false)
        when (it?.status) {
            NetworkConstants.SUCCESS_CODE -> {
                slotLiveData.value = it.data
            }
            NetworkConstants.AUTHFAILED -> {
                dataManager.setUserAsLoggedOut()
                navigator.onSessionExpire()
            }
            else -> {

            }
        }
    }


    fun checkUserSubsc(param: UserSubInfoParam) {
        setIsLoading(true)

        CommonUtils.changebaseUrl(dataManager.getRetrofitUtl(), BuildConfig.ONBOARD_URL)
        dataManager.checkUserSubc(param)
                .observeOn(AndroidSchedulers.mainThread())
                ?.subscribeOn(Schedulers.io())
                ?.subscribe({ this.userSubsResponse(it) }, { this.handleSubError(it) })?.let {
                    compositeDisposable.add(it)
                }
    }

    private fun userSubsResponse(it: SuccessModel?) {
        setIsLoading(false)
        CommonUtils.changebaseUrl(dataManager.getRetrofitUtl(), BuildConfig.BASE_URL)

        when (it?.statusCode) {
            NetworkConstants.SUCCESS -> {
                dataManager.setkeyValue(PrefenceConstants.IS_BLOCK, false)
                navigator.userUnBlock()
            }
            NetworkConstants.AUTHFAILED -> {
                dataManager.setkeyValue(PrefenceConstants.IS_BLOCK, true)
                navigator.userBlocked()
            }
            else -> {
                it?.message?.let { it1 -> navigator.onErrorOccur(it1) }
            }
        }
    }


    fun refreshCart(param: CartReviewParam?, isLoginFromCart: Boolean = false) {
        setIsLoading(true)
        dataManager.checkProductList(param)
            ?.observeOn(AndroidSchedulers.mainThread())
            ?.subscribeOn(Schedulers.io())
            ?.subscribe(
                {

                    this.refreshCartResponse(it, isLoginFromCart)
                },
                {

                    this.handleError(it)
                })?.let {
                compositeDisposable.add(it)
            }
    }

    fun uploadImage(image: String) {
        setIsLoading(true)

        val file = File(image)
        val requestBody = file.asRequestBody("image/*".toMediaTypeOrNull())
        val partImage = MultipartBody.Part.createFormData("file", file.name, requestBody)


        compositeDisposable.add(
            dataManager.uploadFile(partImage)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe({ this.imageResponse(it) }, { this.handleError(it) })
        )
    }


    private fun refreshCartResponse(it: ApiMsgResponse<CartData>?, loginFromCart: Boolean) {
        setIsLoading(false)
        when (it?.status) {
            NetworkConstants.SUCCESS -> {
                if (it.data is CartData) {

                    navigator.onRefreshCart(it.data)
                }
            }
            NetworkConstants.AUTHFAILED -> {
                dataManager.setUserAsLoggedOut()
                navigator.onSessionExpire()
            }
            else -> {
                if (it?.msg is String) {
                    it.msg.let { it1 ->
                        navigator.onErrorOccur(it1)
                    }

                    navigator.onRefreshCartError()
                } else {
                    navigator.onRefreshCartError()
                }
            }
        }
    }

    fun editProfile(hashMap: HashMap<String, RequestBody>) {
        setIsLoading(true)
        dataManager.signUpFinish(hashMap)
            ?.observeOn(AndroidSchedulers.mainThread())
            ?.subscribeOn(Schedulers.io())
            ?.subscribe({ this.editProfile(it) }, { this.handleError(it) })?.let {
                compositeDisposable.add(
                    it
                )
            }
    }

    private fun editProfile(it: PojoSignUp?) {
        setIsLoading(false)

        when (it?.status) {
            NetworkConstants.SUCCESS -> {
                dataManager.addGsonValue(DataNames.USER_DATA, Gson().toJson(it))
                it.data.id_for_invoice?.let {
                    dataManager.setkeyValue(PrefenceConstants.ID_FOR_INVOICE, it)
                }
                navigator.onProfileUpdate()
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

    fun getWindCaveUrl(amount: String, currency: String, email: String, address: String?) {
        val hashMap = HashMap<String, String>()
        hashMap["success_url"] = "https://billing.royoapps.com/payment-success"
        hashMap["failure_url"] = "https://billing.royoapps.com/payment-error"
        hashMap["amount"] = amount
        hashMap["currency"] = currency
        hashMap["email"] = email
        if (address != null)
            hashMap["address"] = address
        setIsLoading(true)
        dataManager.getWindCaveUrl(hashMap)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribe({ this.paymentWindCaveResponse(it) }, { this.handleError(it) })?.let {
                compositeDisposable.add(it)
            }
    }

    fun getThawaniPaymentUrl(amount: String, currency: String, pojoSignUp: PojoSignUp?) {

        val hashMap = HashMap<String, String>()
        hashMap["amount"] = amount
        hashMap["email"] = pojoSignUp?.data?.email ?: ""
        hashMap["name"] = pojoSignUp?.data?.firstname ?: ""
        setIsLoading(true)
        dataManager.getThawaniPayUrl(hashMap)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribe({ this.paymentThawaniResponse(it) }, { this.handleError(it) })?.let {
                compositeDisposable.add(it)
            }
    }

    private fun paymentThawaniResponse(it: AddCardResponse?) {
        setIsLoading(false)
        when (it?.status) {
            NetworkConstants.SUCCESS -> {
                navigator.getThawaniPaymentSuccess(it.data)
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

    fun getTelrPaymentUrl(amount: String, currency: String) {

        val hashMap = HashMap<String, String>()
        hashMap["amount"] = amount
        hashMap["currency"] = currency
        hashMap["desc"] = "des"

        setIsLoading(true)
        dataManager.getTelrPayUrl(hashMap)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribe({ this.paymentTelrResponse(it) }, { this.handleError(it) })?.let {
                compositeDisposable.add(it)
            }
    }

    private fun paymentTelrResponse(it: AddCardResponse?) {
        setIsLoading(false)
        when (it?.status) {
            NetworkConstants.SUCCESS -> {
                navigator.getTelrPaymentSuccess(it.data)
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


    private fun paymentWindCaveResponse(it: WindCaveResponse?) {
        setIsLoading(false)
        when (it?.status) {
            NetworkConstants.SUCCESS -> {
                navigator.getWindCavePaymentSuccess(it.data)
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

    fun getMPaisaPaymentUrl(amount: String) {
        val hashMap = HashMap<String, String>()
        hashMap["checkout_url"] = "https://billing.royoapps.com/payment-success"
        hashMap["amount"] = amount
        hashMap["items_details"] = "items_details"
        setIsLoading(true)
        dataManager.getMpaisaUrl(hashMap)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribe({ this.paymentMPaisaResponse(it) }, { this.handleError(it) })?.let {
                compositeDisposable.add(it)
            }
    }

    private fun paymentMPaisaResponse(it: AddCardResponse?) {
        setIsLoading(false)
        when (it?.status) {
            NetworkConstants.SUCCESS -> {
                navigator.getmPaisaPaymentSuccess(it.data)
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

    fun getAamarPayUrl(hashMap: HashMap<String, String>) {
        setIsLoading(true)
        dataManager.getAamarPayUrl(hashMap)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribe({ this.paymentAamarPayResponse(it) }, { this.handleError(it) })?.let {
                compositeDisposable.add(it)
            }
    }

    private fun paymentAamarPayResponse(it: AddCardResponse?) {
        setIsLoading(false)
        when (it?.status) {
            NetworkConstants.SUCCESS -> {
                navigator.getAamarPayPaymentSuccess(it.data)
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

    fun getPaymayaUrl(hashMap: HashMap<String, String>) {
        setIsLoading(true)
        dataManager.getPaymayaUrl(hashMap)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribe({ this.paymentPaymayaResponse(it) }, { this.handleError(it) })?.let {
                compositeDisposable.add(it)
            }
    }

    private fun paymentPaymayaResponse(it: AddCardResponse?) {
        setIsLoading(false)
        when (it?.status) {
            NetworkConstants.SUCCESS -> {
                navigator.getPayMayaUrl(it.data)
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


    fun tapPayemntApi(hashMap: HashMap<String, String>) {
        setIsLoading(true)
        dataManager.tapPayment(hashMap)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribe({ this.tapPaymentResponse(it) },
                { this.handleError(it) })?.let {
                compositeDisposable.add(it)
            }
    }

    private fun tapPaymentResponse(it: TapPaymentModel?) {
        setIsLoading(false)
        when (it?.status) {
            NetworkConstants.SUCCESS -> {
                navigator.onTapPayment(it.data.transaction)
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


    fun evalonPayemntApi(amount: String) {
        setIsLoading(true)
        dataManager.evalonPayment(amount)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribe({ this.evalonPaymentResponse(it) },
                { this.handleError(it) })?.let {
                compositeDisposable.add(it)
            }
    }

    private fun evalonPaymentResponse(it: SuccessModel?) {
        setIsLoading(false)
        when (it?.statusCode) {
            NetworkConstants.SUCCESS -> {
                navigator.onEvalonPayment(it.data.toString())
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

    fun referralAmount() {
        setIsLoading(true)
        dataManager.getReferralAmount()
            .observeOn(AndroidSchedulers.mainThread())
            ?.subscribeOn(Schedulers.io())
            ?.subscribe({ this.referralResponse(it) }, { this.handleError(it) })?.let {
                compositeDisposable.add(it)
            }
    }

    private fun referralResponse(it: ApiResponse<ReferalAmt>?) {
        setIsLoading(false)
        when (it?.status) {
            NetworkConstants.SUCCESS -> {
                navigator.onReferralAmt(it.data?.referalAmount ?: 0.0f)
            }
            NetworkConstants.AUTHFAILED -> {
                dataManager.setUserAsLoggedOut()
                navigator.onSessionExpire()
            }
            else -> {
                it?.msg?.let { it1 -> navigator.onErrorOccur(it1.toString()) }
            }
        }
    }


    fun validatePromo(param: CheckPromoCodeParam?) {
        setIsLoading(true)
        dataManager.checkPromo(param)
            ?.observeOn(AndroidSchedulers.mainThread())
            ?.subscribeOn(Schedulers.io())
            ?.subscribe({ this.promoResponse(it, param) }, { this.handleError(it) })?.let {
                compositeDisposable.add(it)
            }
    }


    fun getDistance(source: LatLng, dest: LatLng, supplierId: Int?) {

        CommonUtils.changebaseUrl(dataManager.getRetrofitUtl(), BuildConfig.BASE_URL)

        val hashMap = hashMapOf(
            "source_latitude" to source.latitude.toString(),
            "source_longitude" to source.longitude.toString(),
            "dest_latitude" to dest.latitude.toString(),
            "dest_longitude" to dest.longitude.toString()
        )

        compositeDisposable.add(
            dataManager.getDistance(hashMap)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe({ this.distanceResponse(it, supplierId) }, { this.handleError(it) })
        )
    }

    private fun distanceResponse(it: ApiResponse<DistanceMatrix>?, supplierId: Int?) {
        //  setIsLoading(false)

        if (it?.status == NetworkConstants.SUCCESS) {
            navigator.onCalculateDistance(it.data, supplierId)
        } else {
            navigator.onCalculateDistance(DistanceMatrix(distance = 0.0, duration = ""), supplierId)
        }
    }

    private fun imageResponse(it: ApiResponse<Any>?) {
        setIsLoading(false)
        when (it?.status) {
            NetworkConstants.SUCCESS -> {
                imageLiveData.value = it.data.toString()
            }
            NetworkConstants.AUTHFAILED -> {
                dataManager.setUserAsLoggedOut()
                navigator.onSessionExpire()
            }
            else -> {
                it?.msg?.let { it1 -> navigator.onErrorOccur(it1.toString()) }
            }
        }
    }


    private fun promoResponse(it: PromoCodeModel?, param: CheckPromoCodeParam?) {
        setIsLoading(false)
        if (it?.status == NetworkConstants.SUCCESS) {
            navigator.onValidatePromo(it.data, param)
        } else if (it?.status == NetworkConstants.AUTHFAILED) {
            dataManager.setUserAsLoggedOut()
            navigator.onSessionExpire()
        } else {
            it?.message?.let { it1 -> navigator.onErrorOccur(it1) }
        }
    }


    fun generateOrder(
        Schedule_time: String? = null,
        user_car_id: String? = null,
        appUtils: AppUtils,
        mDeliveryType: Int?,
        mPaymentOption: Int,
        mAgentParam: AgentCustomParam?,
        payToken: String,
        uniqueId: String,
        redeemedAmt: Double,
        imageList: MutableList<ImageListModel>,
        instruction: String,
        mTipCharges: Float,
        restServiceTax: Double,
        mQuestionList: List<QuestionList>,
        questionAddonPrice: Float,
        appType: Int,
        mSelectedPayment: CustomPayModel?,
        have_pet: Int,
        cleaner_in: Int,
        parking_instructions: String,
        area_to_focus: String,
        paymentConfirm: Boolean,
        isDonate: Boolean,
        mobileNo: String?,
        scheduleDate: SupplierSlots?,
        wallet_discount_amount: Double?,
        isLoyaltyPointEnabled: Boolean? = null,
        clickAtCollectNo: String,
        have_coin_change: String? = null,
        is_dine_in_with_food: String?,
        selectedTableID: String?,
        tableLoadedFromScanner: String,
        subTotalCopy: Double,
        savedCard: SaveCardInputModel? = null,
        mDropOffTimeParam: AgentCustomParam? = null,
        order_delivery_type: Int? = null,
        vehicle_number: String? = null,
        isCutleryRequired: Boolean?,
        ipAddress: String,
        signature_menu_id: String,
        signature_plate_id: String,
        from_signature_menu: String,
        from_signature_plate: String,
        deliveryCompany: SuppliersDeliveryCompaniesItem? = null
    ) {

        val placeOrderInput = PlaceOrderInput()
        placeOrderInput.is_dine_in_with_food = is_dine_in_with_food
        placeOrderInput.is_dine_in_only = tableLoadedFromScanner
        placeOrderInput.is_dine_in = is_dine_in_with_food
        placeOrderInput.table_id = selectedTableID
        placeOrderInput.user_car_id = user_car_id ?: ""
        /*placeOrderInput.currency = "PEN"*/
        val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", DateTimeUtils.timeLocale)
        placeOrderInput.accessToken =
            dataManager.getKeyValue(PrefenceConstants.ACCESS_TOKEN, PrefenceConstants.TYPE_STRING)
                .toString()

        if (dataManager.getGsonValue(
                DataNames.DISCOUNT_AMOUNT,
                PromoCodeModel.DataBean::class.java
            ) != null
        ) {
            val promoData = dataManager.getGsonValue(
                DataNames.DISCOUNT_AMOUNT,
                PromoCodeModel.DataBean::class.java
            )
            placeOrderInput.promoCode = promoData?.promoCode
            placeOrderInput.promoId = promoData?.id ?: 0
            placeOrderInput.discountAmount =
                if (subTotalCopy < promoData?.discountPrice ?: 0f) subTotalCopy.toFloat() else promoData?.discountPrice
                    ?: 0.0f
        }
        if (Schedule_time.equals("")) {

        } else {
            placeOrderInput.schedule_end_date = Schedule_time
            placeOrderInput.is_schedule = 1
        }

        if (scheduleDate != null) {
            if (is_dine_in_with_food == "1") {
                placeOrderInput.is_schedule = 0
            } else {
                placeOrderInput.is_schedule = 1
            }

            if (craveScheduleModel != null) {
                placeOrderInput.schedule_date = craveScheduleModel?.scheduleDate
                placeOrderInput.schedule_end_date = craveScheduleModel?.endDate
                placeOrderInput.is_schedule = 1
                placeOrderInput.schedule_type = craveScheduleModel?.type

                craveScheduleModel = null
            } else {
                placeOrderInput.schedule_date = scheduleDate.startDateTime
                placeOrderInput.schedule_end_date = scheduleDate.endDateTime
            }


            placeOrderInput.slot_price =
                if (scheduleDate.selectedTable != null && scheduleDate.selectedTable?.calculated_table_price != null &&
                    scheduleDate.selectedTable?.calculated_table_price != 0f
                )
                    scheduleDate.selectedTable?.calculated_table_price.toString() else scheduleDate.supplierTimings?.firstOrNull()?.price.toString()
            placeOrderInput.slot_id = scheduleDate.supplierTimings?.firstOrNull()?.id.toString()
        }
        if (craveScheduleModel != null) {
            placeOrderInput.schedule_date = craveScheduleModel?.scheduleDate
            placeOrderInput.schedule_end_date = craveScheduleModel?.endDate
            placeOrderInput.is_schedule = 1
            placeOrderInput.schedule_type = craveScheduleModel?.type

            craveScheduleModel = null
        }
        if (order_delivery_type != null)
            placeOrderInput.order_delivery_type = order_delivery_type

        placeOrderInput.click_at_no = clickAtCollectNo
        placeOrderInput.have_pet = have_pet
        placeOrderInput.cleaner_in = cleaner_in
        placeOrderInput.parking_instructions = parking_instructions
        placeOrderInput.area_to_focus = area_to_focus

        placeOrderInput.self_pickup =
            if (is_dine_in_with_food == "1" && mDeliveryType == FoodAppType.DineIn.foodType) {
                3
            } else {
                if (mDeliveryType == 0 || mDeliveryType == 2) 0 else 1
            }

        placeOrderInput.offset =
            SimpleDateFormat("ZZZZZ", DateTimeUtils.timeLocale).format(System.currentTimeMillis())
        placeOrderInput.cartId =
            dataManager.getKeyValue(DataNames.CART_ID, PrefenceConstants.TYPE_STRING).toString()

        if (paymentConfirm) {
            placeOrderInput.payment_after_confirmation = 1
        }

        placeOrderInput.donate_to_someone = if (isDonate) 1 else 0


        if (mPaymentOption == 1) {

            /* if(mSelectedPayment?.customerId?.isNotEmpty()==true && mSelectedPayment.cardId?.isNotEmpty()==true)
             {*/

            /*   }else
               {*/
            placeOrderInput.gateway_unique_id = "Doha Bank"
//            if(BuildConfig.CLIENT_CODE=="ulagula_0488"){
//                placeOrderInput.payment_token = mSelectedPayment?.token
//            }else{
            placeOrderInput.payment_token = "wdwdwdwdwdw"
//            }


//            when (uniqueId) {
//                "authorize_net" -> {
//                    placeOrderInput.authnet_payment_profile_id = mSelectedPayment?.authnet_payment_profile_id
//                    placeOrderInput.authnet_profile_id = mSelectedPayment?.authnet_profile_id
//                }
//                "pago_facil" -> {
//                    placeOrderInput.payment_token = savedCard?.card_number
//                    placeOrderInput.cvt = savedCard?.cvc
//                    placeOrderInput.cp = savedCard?.zipCode
//                    placeOrderInput.expMonth = savedCard?.exp_month
//                    placeOrderInput.expYear = savedCard?.exp_year?.takeLast(2)
//
//                }
//                "safe2pay"->{
//                    placeOrderInput.payment_token = savedCard?.card_number
//                    placeOrderInput.cvc = savedCard?.cvc
//                    placeOrderInput.expMonth = savedCard?.exp_month
//                    placeOrderInput.expYear = savedCard?.exp_year
//                    placeOrderInput.cardHolderName=savedCard?.card_holder_name
//                }
//                else -> {
//                    placeOrderInput.customer_payment_id = mSelectedPayment?.customerId
//                    placeOrderInput.card_id = mSelectedPayment?.cardId
//                }
//            }
            // }
        }

        placeOrderInput.pres_description = instruction

        if (redeemedAmt > 0) {
            placeOrderInput.use_refferal = 1
        }

        if (mQuestionList.isNotEmpty()) {
            placeOrderInput.questions = mQuestionList
            placeOrderInput.addOn = questionAddonPrice
        }
        if (!vehicle_number.isNullOrEmpty())
            placeOrderInput.vehicle_number = vehicle_number

        placeOrderInput.type = appType
        placeOrderInput.paymentType = mPaymentOption
        placeOrderInput.languageId = dataManager.getLangCode().toInt()

        if (wallet_discount_amount != null)
            placeOrderInput.wallet_discount_amount = wallet_discount_amount

        val calendar = Calendar.getInstance()
        placeOrderInput.order_day = appUtils.getDayId(calendar.get(Calendar.DAY_OF_WEEK)) ?: ""
        placeOrderInput.order_time = appUtils.convertDateOneToAnother(
            calendar.time.toString(),
            "EEE MMM d HH:mm:ss Z yyyy",
            "HH:mm:ss"
        )
            ?: ""

        if (deliveryCompany != null)
            placeOrderInput.delivery_company_id = deliveryCompany.id.toString()

        if (mAgentParam != null) {
            if (mAgentParam.agentData != null)
                placeOrderInput.agentIds = listOf(mAgentParam.agentData?.id ?: 0)
            placeOrderInput.date_time = mAgentParam.serviceDate + " " + mAgentParam.serviceTime
            placeOrderInput.duration = mAgentParam.duration ?: 0
        } else {
            placeOrderInput.booking_date_time = dateFormat.format(calendar.time)
        }

        if (mDropOffTimeParam != null) {
            placeOrderInput.drop_off_date =
                mDropOffTimeParam.serviceDate + " " + mDropOffTimeParam.serviceTime
        }
        if (mTipCharges > 0) {
            placeOrderInput.tip_agent = mTipCharges
        }

        if (restServiceTax > 0) {
            placeOrderInput.user_service_charge = restServiceTax
        }


        if (!imageList.isNullOrEmpty()) {

            imageList.forEachIndexed { index, imageListModel ->

                when (index) {
                    0 -> placeOrderInput.pres_image1 = imageListModel.image ?: ""
                    1 -> placeOrderInput.pres_image2 = imageListModel.image ?: ""
                    2 -> placeOrderInput.pres_image3 = imageListModel.image ?: ""
                    3 -> placeOrderInput.pres_image4 = imageListModel.image ?: ""
                    4 -> placeOrderInput.pres_image5 = imageListModel.image ?: ""
                }
            }
        }
        if (isLoyaltyPointEnabled != null && isLoyaltyPointEnabled != false)
            placeOrderInput.use_loyality_point = "1"

        if (have_coin_change != null)
            placeOrderInput.have_coin_change = have_coin_change

        if (uniqueId == "mumybene" && !mobileNo.isNullOrEmpty()) {
            placeOrderInput.mobile_no = mobileNo
            placeOrderInput.service_provider = mSelectedPayment?.mumybenePay
            setIsLoading(false)
            showMumyBeneLoader.set(true)
        } else
            setIsLoading(true)

        val currency =
            dataManager.getGsonValue(PrefenceConstants.CURRENCY_INF, Currency::class.java)
        //placeOrderInput.currency = "PEN"

        placeOrderInput.currency = AppConstants.DEFAULT_CURRENCY_SYMBOL
        placeOrderInput.ip_address = ipAddress

        placeOrderInput.signature_menu_id = signature_menu_id
        placeOrderInput.signature_plate_id = signature_plate_id
        placeOrderInput.from_signature_menu = from_signature_menu
        placeOrderInput.from_signature_plate = from_signature_plate
        if (isCutleryRequired != null)

            placeOrderInput.is_cutlery_required = if (isCutleryRequired == true) "1" else "0"
        dataManager.generateOrder(placeOrderInput)
            ?.observeOn(AndroidSchedulers.mainThread())
            ?.subscribeOn(Schedulers.io())
            ?.subscribe({ this.validateResponse(it) }, { this.handleError(it) })?.let {
                compositeDisposable.add(it)
            }
    }

    fun final_order(
        order_id: String,
        transaction_id: String,
        type: String,
        status: String,
        cardEndingDigits: String,
        token: String,
        scheme: String,
        user_id: String
    ) {
        var param = FinalOrderInput()
        param.orderId = order_id
        param.transactionId = transaction_id
        param.type = type
        param.status = status

        param.cardEndingDigits = cardEndingDigits
        param.scheme = scheme
        param.token = token
        param.user_id = user_id

        dataManager.final_order(param)
            .observeOn(AndroidSchedulers.mainThread())
            ?.subscribeOn(Schedulers.io())
            ?.subscribe({ finalOrderResponse(order_id) }, { this.handleError(it) })?.let {
                compositeDisposable.add(it)
            }
    }

    private fun finalOrderResponse(order_id: String) {

        setIsLoading(false)

        navigator.onFinalResponse(order_id)
    }

    fun cancelGenerateOrder() {
        showMumyBeneLoader.set(false)
        compositeDisposable.clear()
    }

    private fun validateResponse(it: ApiResponse<Any>) {
        showMumyBeneLoader.set(false)
        setIsLoading(false)
        if (it.status == NetworkConstants.SUCCESS) {

            if (it.data is ArrayList<*>) {
                navigator.onOrderPlaced(it.data as ArrayList<Int>)
            }
        } else if (it.status == NetworkConstants.AUTHFAILED) {
            dataManager.setUserAsLoggedOut()
            navigator.onSessionExpire()
        } else {
            it.msg?.let { it1 -> navigator.onErrorOccur(if (it1 is String) it1.toString() else "Internal Server error") }
        }
    }


    fun updateCartInfo(
        cartList: MutableList<CartInfo>?, appUtils: AppUtils, mDeliveryType: Int?,
        mDeliveryCharge: Float, netTotal: Double,
        adminCharges: Float,
        deliveryMax: Int?,
        mQuestionList: List<QuestionList>,
        questionAddonPrice: Float
    ) {


        val calendar = Calendar.getInstance()

        val updateCart = UpdateCartParam()

        with(updateCart)
        {
            accessToken = dataManager.getKeyValue(
                PrefenceConstants.ACCESS_TOKEN,
                PrefenceConstants.TYPE_STRING
            ).toString()
            cartId =
                dataManager.getKeyValue(DataNames.CART_ID, PrefenceConstants.TYPE_STRING).toString()
                    .toInt()
            deliveryType = DataNames.DELIVERY_TYPE_STANDARD.toString()
            netAmount = netTotal
            currencyId = "1"
            languageId = dataManager.getLangCode()
            handlingAdmin = adminCharges.toString()
//            handlingSupplier = cartList?.maxByOrNull {
//                it.handlingSupplier
//            }?.handlingSupplier.toString()
            order_day = appUtils.getDayId(calendar.get(Calendar.DAY_OF_WEEK))
            order_time = appUtils.convertDateOneToAnother(
                calendar.time.toString(),
                "EEE MMM d HH:mm:ss Z yyyy",
                "HH:mm:ss"
            )

            if (mDeliveryType == FoodAppType.Pickup.foodType || mDeliveryType == FoodAppType.DineIn.foodType) {
                deliveryCharges = "0.0"
                deliveryId = "0"
            } else {
                deliveryId =
                    dataManager.getKeyValue(DataNames.PICKUP_ID, PrefenceConstants.TYPE_STRING)
                        .toString()
                deliveryCharges = mDeliveryCharge.toString()
            }

            if (mQuestionList.isNotEmpty()) {
                addOn = questionAddonPrice.toInt()
                questions = mQuestionList
            }

            calendar.add(Calendar.MINUTE, deliveryMax ?: 0)
            deliveryDate = appUtils.convertDateOneToAnother(
                calendar.time.toString(),
                "EEE MMM d HH:mm:ss Z yyyy",
                "yyyy-MM-dd"
            )
                ?: ""
            deliveryTime = appUtils.convertDateOneToAnother(
                calendar.time.toString(),
                "EEE MMM d HH:mm:ss Z yyyy",
                "HH:mm:ss"
            )
                ?: ""

            urgentPrice = "0"
        }

        setIsLoading(true)

        compositeDisposable.add(
            dataManager.updateCartInfo(updateCart)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe({ this.updateCart(it) }, { this.handleError(it) })
        )
    }


    private fun updateCart(it: ExampleCommon?) {

        setIsLoading(false)

        when (it?.status) {
            NetworkConstants.SUCCESS -> {
                navigator.onUpdateCart()
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


    fun addCart(
        cartList: MutableList<CartInfoServer>,
        appUtils: AppUtils,
        questionAddonPrice: Float,
        mQuestionList: List<QuestionList>,
        mDeliveryChargeArray: MutableList<SuplierDeliveryCharge>,
        decimalFormat: DecimalFormat?,
        is_schedule: String? = null
    ) {

        val cartInfoServerArray = CartInfoServerArray()
        val addonList = mutableListOf<ProductAddon?>()


        cartList.map { cart ->
            cart.add_ons?.map {
                it?.quantity = cart.quantity
            }

            cart.fixed_price = decimalFormat?.format(cart.fixed_price)?.toFloatOrNull() ?: 0f
        }


        cartList.filter { !it.add_ons.isNullOrEmpty() }.map { it.add_ons }.mapIndexed { pos, list ->

            list?.map {
                it?.serial_number = pos.inc()
            }
            list?.let { addonList.addAll(it) }
        }
        cartInfoServerArray.addons = addonList
        cartInfoServerArray.is_schedule = is_schedule!!
        cartInfoServerArray.variants = cartList.flatMap { it.variants ?: mutableListOf() }
        //remove duplicate value & sum count if contains addons of same product
        cartList.mapIndexed { index, cartInfoServer ->
            if (mDeliveryChargeArray.any { it.supplierId == cartInfoServer.supplier_id }) {
                val mDeliveryArray =
                    mDeliveryChargeArray.filter { it.supplierId == cartInfoServer.supplier_id }
                cartInfoServer.deliveryCharges = mDeliveryArray.firstOrNull()?.deliveryCharge
                cartInfoServer.handlingAdmin = mDeliveryArray.firstOrNull()?.tax
                cartInfoServer.tax = mDeliveryArray.firstOrNull()?.tax
            }

            cartInfoServer.price = cartInfoServer.fixed_price
            cartInfoServer.variants = null
        }
        val product = cartList.distinctBy {
            it.productId
        }

        product.map { cart ->
            cart.quantity = cartList.filter { it.productId == cart.productId }.sumByDouble {
                it.quantity?.toDouble() ?: 0.0
            }.toFloat()
        }

        cartInfoServerArray.productList = product
        //------------------------------------------------------------------//
        cartInfoServerArray.accessToken =
            dataManager.getKeyValue(PrefenceConstants.ACCESS_TOKEN, PrefenceConstants.TYPE_STRING)
                .toString()
        cartInfoServerArray.remarks = "0"
        cartInfoServerArray.cartId = "0"

        val calendar = Calendar.getInstance()
        calendar.timeZone = TimeZone.getDefault()
        cartInfoServerArray.order_day = appUtils.getDayId(calendar.get(Calendar.DAY_OF_WEEK)) ?: ""

        val sdf = SimpleDateFormat("HH:mm:ss", Locale("en"))
        cartInfoServerArray.order_time = sdf.format(calendar.time).toString()


        if (mQuestionList.isNotEmpty()) {
            cartInfoServerArray.questions = mQuestionList
            cartInfoServerArray.questionAddonPrice = questionAddonPrice
        }

        cartInfoServerArray.supplierBranchId = cartList.firstOrNull()?.supplier_branch_id ?: 0
        cartInfoServerArray.supplier_id = cartList.firstOrNull()?.supplier_id ?: 0


        setIsLoading(true)
        CommonUtils.changebaseUrl(dataManager.getRetrofitUtl(), BuildConfig.BASE_URL)
        dataManager.getAddToCart(cartInfoServerArray)
            ?.observeOn(AndroidSchedulers.mainThread())
            ?.subscribeOn(Schedulers.io())
            ?.subscribe({ this.addCartInfo(it) }, { this.handleError(it) })?.let {
                compositeDisposable.add(it)
            }
    }


    private fun addCartInfo(it: AddtoCartModel?) {

        setIsLoading(false)

        when (it?.status) {
            NetworkConstants.SUCCESS -> {
                dataManager.setkeyValue(DataNames.CART_ID, it.data?.cartId ?: "")
                navigator.onCartAdded(it.data)
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


    fun pay(amount: String, Order_id: String) {
        setIsLoading(true)

        val param = HashMap<String, String>()
        param["amount"] = amount
        param["order_id"] = Order_id
        compositeDisposable.add(
            dataManager.pay(param)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe({ this.payResponse(it) }, { this.handleError(it) })
        )
    }

    private fun payResponse(it: PayModel?) {
        setIsLoading(false)
        if (it?.statusCode == NetworkConstants.STATUS_CODE) {
            navigator.onPay(it.body.session.id, it.body.successIndicator)
        } else {
            //it?.message?.let { it1 -> navigator.onErrorOccur(it1) }
        }
    }

    fun getAddressList(supplierBranch: Int) {
        setIsLoading(true)

        val param = dataManager.updateUserInfnew()
        if (dataManager.getCurrentUserLoggedIn()) {
            param["accessToken"] = dataManager.getKeyValue(
                PrefenceConstants.ACCESS_TOKEN,
                PrefenceConstants.TYPE_STRING
            ).toString()
        }
        if (supplierBranch > 0) {
            param["supplierBranchId"] = supplierBranch.toString()
        }

        compositeDisposable.add(
            dataManager.getAllAddress(param)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe({ this.addressResponse(it) }, { this.handleError(it) })
        )
    }

    private fun addressResponse(it: CustomerAddressModel?) {
        setIsLoading(false)
        if (it?.status == NetworkConstants.SUCCESS) {
            navigator.onAddress(it.data)
        } else {
            //it?.message?.let { it1 -> navigator.onErrorOccur(it1) }
        }
    }

    //post vehicle details
    fun deleteUserVehicle(id: String, user_id: String) {
        setIsLoading(true)

        val param = HashMap<String, String>()

        param["user_id"] = user_id.toString()
        param["id"] = id.toString()

        compositeDisposable.add(
            dataManager.deleteUser(param)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe({ this.deleteDetailResponsenew(it) }, { this.handleError(it) })
        )
//        compositeDisposable.add(dataManager.deleteUserDEtails(id.toString(),user_id.toString())
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribeOn(Schedulers.io())
//                .subscribe({ this.deleteDetailResponsenew(it) }, { this.handleError(it) })
//        )
    }

    private fun deleteDetailResponsenew(it: DeleteVehicleDetails?) {
        setIsLoading(false)
        if (it?.status == NetworkConstants.SUCCESS) {

            vehicleDeleteLiveData.value = it.data

        }
    }

    //post vehicle details
    fun postUserVehicle(
        user_id: String,
        car_name: String,
        car_color: String,
        car_number_plate: String,
        mobile: String,
        is_default: String
    ) {
        setIsLoading(true)

        val param = HashMap<String, String>()

        param["user_id"] = user_id.toString()
        param["car_name"] = car_name.toString()
        param["car_color"] = car_color.toString()
        param["car_number_plate"] = car_number_plate.toString()
        param["mobile"] = mobile
        param["is_default"] = "0"

        compositeDisposable.add(
            dataManager.postUserDEtails(param)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe({ this.postDetailResponsenew(it) }, { this.handleError(it) })
        )
    }

    private fun postDetailResponsenew(it: PostVehicleDetails?) {
        setIsLoading(false)
        if (it?.status == NetworkConstants.SUCCESS) {

            vehicleresponseLiveData.value = it.data

        }
    }


    //user vehicle detail

    fun getUserVehicle(user_id: String) {
        setIsLoading(true)

        val param = dataManager.updateUserInfnew()

        param["user_id"] = user_id.toString()


        compositeDisposable.add(
            dataManager.getUserDEtails(param)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe({ this.userDetailResponsenew(it) }, { this.handleError(it) })
        )
    }


    private fun userDetailResponsenew(it: VehicalDetailsExample?) {
        setIsLoading(false)
        if (it?.status == NetworkConstants.SUCCESS) {

            liveUserDeatil.value = it.data

        }
    }

    fun setIsCartList(listCount: Int) {
        this.isCartList.set(listCount)
    }


    private fun handleError(e: Throwable) {
        CommonUtils.changebaseUrl(dataManager.getRetrofitUtl(), BuildConfig.BASE_URL)
        setIsLoading(false)
        handleErrorMsg(e).let {
            if (it == NetworkConstants.AUTH_MSG) {
                dataManager.setUserAsLoggedOut()
                navigator.onSessionExpire()
            } else {
                try {
                    navigator.onErrorOccur(it)
                } catch (e: Exception) {
                }
            }
        }
    }

    private fun handleSubError(e: Throwable) {
        CommonUtils.changebaseUrl(dataManager.getRetrofitUtl(), BuildConfig.BASE_URL)
        setIsLoading(false)
        handleErrorMsg(e).let {
            if (it == NetworkConstants.AUTH_MSG) {
                dataManager.setkeyValue(PrefenceConstants.IS_BLOCK, true)
                navigator.userBlocked()
            } else {
                navigator.onErrorOccur(it)
            }
        }
    }

    fun apiAddMoneyToWallet(
        mSelectedPayment: CustomPayModel?,
        amount: String,
        phoneNumber: String?,
        savedCard: SaveCardInputModel?
    ) {
        val currency =
            dataManager.getGsonValue(PrefenceConstants.CURRENCY_INF, Currency::class.java)
        val languageId =
            dataManager.getKeyValue(DataNames.SELECTED_LANGUAGE, PrefenceConstants.TYPE_STRING)
                .toString()
        val userId =
            dataManager.getKeyValue(PrefenceConstants.USER_ID, PrefenceConstants.TYPE_STRING)
                .toString()

        val mParam = HashMap<String, String>()
        mParam["gateway_unique_id"] = mSelectedPayment?.payment_token ?: ""

        if (!mSelectedPayment?.keyId.isNullOrEmpty())
            mParam["payment_token"] = mSelectedPayment?.keyId ?: ""

        mParam["amount"] = amount
        mParam["currency"] = currency?.currency_name ?: ""

        mParam["languageId"] = languageId
        mParam["user_id"] = userId

        if (mSelectedPayment?.payment_token == "mumybene" && !phoneNumber.isNullOrEmpty()) {
            mParam["phoneNumber"] = phoneNumber
        }

        if (mSelectedPayment?.payment_token == "authorize_net") {
            mParam["authnet_profile_id"] = mSelectedPayment.authnet_profile_id ?: ""
            mParam["authnet_payment_profile_id"] = mSelectedPayment.authnet_payment_profile_id ?: ""

        } else if (mSelectedPayment?.payment_token == "pago_facil") {
            mParam["payment_token"] = savedCard?.card_number ?: ""
            mParam["cvt"] = savedCard?.cvc ?: ""
            mParam["cp"] = savedCard?.zipCode ?: ""
            mParam["expMonth"] = savedCard?.exp_month ?: ""
            mParam["expYear"] = savedCard?.exp_year?.takeLast(2) ?: ""
        } else if (mSelectedPayment?.payment_token == "safe2pay") {
            mParam["payment_token"] = savedCard?.card_number ?: ""
            mParam["cvt"] = savedCard?.cvc ?: ""
            mParam["expMonth"] = savedCard?.exp_month ?: ""
            mParam["expYear"] = savedCard?.exp_year ?: ""
            mParam["cardHolderName"] = savedCard?.card_holder_name ?: ""
        } else if (!mSelectedPayment?.cardId.isNullOrEmpty() && !mSelectedPayment?.customerId.isNullOrEmpty()) {
            mParam["customer_payment_id"] = mSelectedPayment?.customerId ?: ""
            mParam["card_id"] = mSelectedPayment?.cardId ?: ""
        }

        setIsLoading(true)
        dataManager.apiAddMoneyToWallet(mParam)
            ?.observeOn(AndroidSchedulers.mainThread())
            ?.subscribeOn(Schedulers.io())
            ?.subscribe({ this.validateAddMoneyResponse(it) }, { this.handleError(it) })?.let {
                compositeDisposable.add(it)
            }
    }

    private fun validateAddMoneyResponse(it: SuccessModel?) {
        setIsLoading(false)
        when (it?.statusCode) {
            NetworkConstants.SUCCESS -> {
                addMoneyToWallet.value = it.data
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

    fun validateZelleImage(image: String) {
        setIsLoading(true)

        val file = File(image)
        val requestBody = file.asRequestBody("image/*".toMediaTypeOrNull())
        val partImage = MultipartBody.Part.createFormData("file", file.name, requestBody)

        compositeDisposable.add(
            dataManager.uploadFile(partImage)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe({ this.imageResponse(it) }, { this.handleError(it) })
        )
    }


    fun getBrainTreeClientToken() {
        setIsLoading(true)
        dataManager.getBrainTreeToken()
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribe({ this.tokenResponse(it) }, { this.handleError(it) })?.let {
                compositeDisposable.add(it)
            }
    }

    private fun tokenResponse(it: BraintreeTokenResoponse?) {
        setIsLoading(false)
        when (it?.status) {
            NetworkConstants.SUCCESS -> {
                navigator.onBrainTreeTokenSuccess(it.data?.client_token)
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


    fun cancelSubcription(model: SubcripModel) {
        setIsLoading(true)
        //{"subscription_plan_id":3,"action":1}

        dataManager.cancelSubscription(CancelSubscripInput(1, model.subscription_plan_id))
            .observeOn(AndroidSchedulers.mainThread())
            ?.subscribeOn(Schedulers.io())
            ?.subscribe({ this.validateSubcripList(it) }, { this.handleError(it) })?.let {
                compositeDisposable.add(it)
            }
    }

    private fun validateSubcripList(it: SuccessModel?) {
        setIsLoading(false)
        if (it?.statusCode == NetworkConstants.SUCCESS) {
            navigator.onCancelSubscription(it.message)
        } else {
            it?.message?.let { it1 -> navigator.onErrorOccur(it1) }
        }
    }

    fun getPaymentGeofence() {

        val mLocUser =
            dataManager.getGsonValue(PrefenceConstants.ADRS_DATA, AddressBean::class.java)
        val param = hashMapOf(
            "lat" to mLocUser?.latitude.toString(),
            "long" to mLocUser?.longitude.toString()
        )

        // setIsLoading(true)
        dataManager.geofenceGateway(param)
            .observeOn(AndroidSchedulers.mainThread())
            ?.subscribeOn(Schedulers.io())
            ?.subscribe({ this.geofencePayment(it) }, { this.handleError(it) })?.let {
                compositeDisposable.add(it)
            }
    }

    private fun geofencePayment(it: ApiResponse<GeofenceData>?) {

        //setIsLoading(false)
        when (it?.status) {
            NetworkConstants.SUCCESS -> {
                navigator.onGeofencePayment(it.data)
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

    fun buySubcription(model: BuySubcripInput) {
        setIsLoading(true)

        dataManager.buyRenewSubscrip(model)
            .observeOn(AndroidSchedulers.mainThread())
            ?.subscribeOn(Schedulers.io())
            ?.subscribe({ this.validateBuySubscription(it) }, { this.handleError(it) })?.let {
                compositeDisposable.add(it)
            }
    }

    private fun validateBuySubscription(it: BuySubscriptionModel?) {
        setIsLoading(false)
        if (it?.status == NetworkConstants.SUCCESS) {
            navigator.onBuySubscription(it.data)
        } else {
            it?.message?.let { it1 -> navigator.onErrorOccur(it1) }
        }
    }

    fun getSubscrpDetail() {
        setIsLoading(true)
        val hashMap = hashMapOf("limit" to "4", "skip" to "0")

        dataManager.getMySubscripList(hashMap)
            .observeOn(AndroidSchedulers.mainThread())
            ?.subscribeOn(Schedulers.io())
            ?.subscribe({ this.validateSubcripList(it) }, { this.handleError(it) })?.let {
                compositeDisposable.add(it)
            }
    }

    private fun validateSubcripList(it: SubcripListModel?) {
        setIsLoading(false)
        when (it?.status) {
            NetworkConstants.SUCCESS -> {
                if (it.data.list.isNotEmpty()) {
                    navigator.onSubscripDetailList(it.data.list)
                }
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

    fun verifyTableNumber(hashMap: HashMap<String, String?>) {
        dataManager.verifyTableNUmber(hashMap)
            .observeOn(AndroidSchedulers.mainThread())
            ?.subscribeOn(Schedulers.io())
            ?.subscribe({ this.validateTable(it) }, { this.handleError(it) })?.let {
                compositeDisposable.add(it)
            }
    }

    private fun validateTable(it: VerifyTableResponeModel?) {
        when (it?.status) {
            NetworkConstants.SUCCESS -> {
                tableNumberObserver.value = it.data
            }
            NetworkConstants.AUTHFAILED -> {
                dataManager.setUserAsLoggedOut()
                navigator.onSessionExpire()
            }
            else -> {
                tableNumberObserver.value = it?.data
            }
        }
    }

    fun getMultipleSupplierDistance(
        distanceArray: MutableList<SuplierDeliveryCharge>,
        soureceLat: Double,
        sourceLong: Double
    ) {

        val distance = MultipleSupplierDistance()

        val distanceList = mutableListOf<SupplierUserLatLong>()

        distanceArray.forEachIndexed { index, suplierDeliveryCharge ->
            distanceList.add(
                SupplierUserLatLong(
                    source_latitude = soureceLat, source_longitude = sourceLong,
                    supplierId = suplierDeliveryCharge.supplierId
                        ?: 0, dest_latitude = suplierDeliveryCharge.latitude ?: 0.0,
                    dest_longitude = suplierDeliveryCharge.longitude ?: 0.0
                )
            )
        }

        distance.supplierUserLatLongs = distanceList

        dataManager.getMultipleDistance(distance)
            .observeOn(AndroidSchedulers.mainThread())
            ?.subscribeOn(Schedulers.io())
            ?.subscribe({ this.validateMultipleSupplierArray(it) }, { this.handleError(it) })?.let {
                compositeDisposable.add(it)
            }
    }

    private fun validateMultipleSupplierArray(it: ApiResponse<List<DistanceMatrix>>?) {
        when (it?.status) {
            NetworkConstants.SUCCESS -> {
                if (it.data?.isNotEmpty() == true) {

                    it.data.forEachIndexed { index, distanceMatrix ->
                        navigator.onCalculateDistance(distanceMatrix, distanceMatrix.supplierId)
                    }

                } else {
                    navigator.onCalculateDistance(DistanceMatrix(distance = 0.0, duration = ""), 0)
                }
            }
            NetworkConstants.AUTHFAILED -> {
                dataManager.setUserAsLoggedOut()
                navigator.onSessionExpire()
            }
        }
    }

    fun getTransactionsList() {
        CommonUtils.changebaseUrl(dataManager.getRetrofitUtl(), BuildConfig.BASE_URL)
        showWalletLoader.set(true)
        compositeDisposable.add(
            dataManager.getAllWalletTransactions(0, AppConstants.LIMIT)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe({ this.validateResponse(it) }, { this.handleError(it) })
        )
    }

    private fun validateResponse(it: WalletTransactionsResonse) {
        showWalletLoader.set(false)
        when (it.status) {
            NetworkConstants.SUCCESS -> {
                val receivedGroups = it.data
                wallet.value = receivedGroups
            }
            NetworkConstants.AUTHFAILED -> {
                dataManager.setUserAsLoggedOut()
                navigator.onSessionExpire()
            }
            else -> {
                it.message?.let { it1 -> navigator.onErrorOccur(it1) }
            }
        }
    }
}
