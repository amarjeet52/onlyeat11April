package com.codebrew.clikat.data.network

import com.codebrew.clikat.data.constants.NetworkConstants
import com.codebrew.clikat.data.model.SuccessCustomModel
import com.codebrew.clikat.data.model.api.*
import com.codebrew.clikat.data.model.api.distance_matrix.DistanceMatrix
import com.codebrew.clikat.data.model.api.orderDetail.OrderDetailModel
import com.codebrew.clikat.data.model.api.tap_payment.TapPaymentModel
import com.codebrew.clikat.data.model.others.*
import com.codebrew.clikat.modal.*
import com.codebrew.clikat.modal.PaymentModel.PayModel
import com.codebrew.clikat.modal.agent.AgentAvailabilityModel
import com.codebrew.clikat.modal.agent.AgentListModel
import com.codebrew.clikat.modal.agent.AgentSlotsModel
import com.codebrew.clikat.modal.agent.GetAgentListParam
import com.codebrew.clikat.modal.crave_mania.CraveManiaResponseData
import com.codebrew.clikat.modal.other.*
import com.codebrew.clikat.modal.slots.SuppliersSlotsResponse
import com.codebrew.clikat.modal.slots.SuppliersTableSlotsResponse
import com.codebrew.clikat.modal.wallet.WalletTransactionsResonse
import com.codebrew.clikat.module.crave_mania.CraveManiaModel
import com.codebrew.clikat.module.pre_delivery.Model.SlotsModel
import io.reactivex.Observable
import com.codebrew.clikat.data.model.api.SuccessModel
import com.codebrew.clikat.data.model.api.vehicleDetails.DeleteVehicleDetails
import com.codebrew.clikat.data.model.api.vehicleDetails.PostVehicleDetails
import com.codebrew.clikat.data.model.api.vehicleDetails.VehicalDetailsExample
import com.codebrew.clikat.data.model.dataModel.LocationModel
import com.codebrew.clikat.module.cart.CardData
import com.codebrew.clikat.module.cart.SavedCardModel
import com.codebrew.clikat.module.cart.TimeSlotModel
import com.codebrew.clikat.module.crave_mania.CraveManiaBannerModel
import com.codebrew.clikat.module.loyaltyPoints.LoyalityResponse
import com.codebrew.clikat.module.rating.RatingModel
import com.codebrew.clikat.module.restaurant_detail.model.AddOnsResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.*
import java.util.*
import kotlin.collections.HashMap


interface RestService {


/*    Required :  email,password
    Optional  : latitude,longitude,device_token,device_type*/


    // @Headers("No-Authentication: true")
    @GET("directions/json?sensor=false&mode=driving&alternatives=true&units=metric")
    fun getPolYLine(@Query("destination") destination: String,
                    @Query("origin") origin: String,
                    @Query("language") language: String?,
                    @Query("key") key: String?): Call<ResponseBody>


    @POST("/public/v1/documents")
    fun createDocument(@Body body: PandaDocResponse): Observable<SuccessModel?>

    @POST("/oauth2/access_token")
    fun createAccessToken(): Observable<SuccessModel?>

    @GET("https://roads.googleapis.com/v1/nearestRoads?")
    fun getRoadPoints(@Query("points") points: String,
                      @Query("key") key: String): Call<RoadPoints>


    @GET(NetworkConstants.WISHLIST)
    fun getWishlist(@QueryMap param: HashMap<String, String>): Observable<WishListModel>


    @POST("/q_pay_final_order")
    fun final_order(@Body params:FinalOrderInput): Observable<FinalOrderPojo?>

    @FormUrlEncoded
    @POST(NetworkConstants.SUPPLIERS_WISH_LIST)
    fun gwtSuppliersWishList(@FieldMap param: HashMap<String, String>): Observable<WishListSuppliersModel>

    @FormUrlEncoded
    @POST(NetworkConstants.SUPPLIER_DETAIL)
    fun getSupplierDetails(@FieldMap params: HashMap<String, String>): Observable<ExampleSupplierDetail>


    @FormUrlEncoded
    @POST(NetworkConstants.ADD_TO_FAVOURITE_SUPL)
    fun markSupplierFav(@FieldMap params: HashMap<String, String>): Observable<ExampleCommon>


    @FormUrlEncoded
    @POST(NetworkConstants.UN_FAVOURITE_SUPL)
    fun markSupplierUnFav(@FieldMap params: HashMap<String, String>): Observable<ExampleCommon>

    @GET(NetworkConstants.GET_SETTING)
    fun getSetting(): Observable<SettingModel>


    @FormUrlEncoded
    @POST(NetworkConstants.GET_COMPLETE_ORDER_STATUS)
    fun getCount(@Field("accessToken") accessToken: String): Observable<PojoPendingOrders>

    @GET(NetworkConstants.GET_ALL_CATEGORY_NEW)
    fun getAllCategoryNew(@QueryMap hashMap: HashMap<String, String>): Observable<CategoryListModel>

    @GET(NetworkConstants.GET_USERDETAIL)
    fun getUserDEtails(@QueryMap hashMap: HashMap<String, String>): Observable<VehicalDetailsExample>

    @DELETE("/user/car_details")
    fun deleteUser(@QueryMap id:HashMap<String,String>): Observable<DeleteVehicleDetails>

    @FormUrlEncoded
    @POST(NetworkConstants.GET_USERDETAIL)
    fun postUserDEtails(@FieldMap hashMap: HashMap<String, String>): Observable<PostVehicleDetails>


    @GET(NetworkConstants.GET_SUPPLIER_LIST)
    fun getSupplierList(@QueryMap hashMap: HashMap<String, String>): Observable<HomeSupplierModel>

    @GET(NetworkConstants.GET_SUPPLIER_LIST_V6)
    fun getSupplierList6(@QueryMap hashMap: HashMap<String, String>): Observable<SearchSupplierModel>

    @GET(NetworkConstants.GET_SUPPLIER_LIST_V7)
    fun getSupplierList7(@QueryMap hashMap: HashMap<String, String>): Observable<SearchSupplierModel>

    @GET("user/list_supplier_slots")
    fun getSlots(@QueryMap hashMap: HashMap<String, String>): Observable<SlotsModel>


    @GET(NetworkConstants.GET_SUPPLIER_LIST_V2)
    fun getAllSupplierList(@QueryMap hashMap: HashMap<String, String>): Observable<ExampleAllSupplierNew>

    @GET(NetworkConstants.GET_SUPPLIER_LIST_V1)
    fun getSupplierListV1(@QueryMap hashMap: HashMap<String, String>): Observable<HomeSupplierModel>

    @GET(NetworkConstants.GET_BRANCH_LIST)
    fun getbranchList(@QueryMap hashMap: HashMap<String, String>): Observable<HomeSupplierModel>

    @GET(NetworkConstants.GET_SUPPLIER_LIST_V7)
    fun getSupplierListV7(@QueryMap hashMap: HashMap<String, String>): Observable<HomeSupplierPaginationModel>

    @GET(NetworkConstants.GET_SUPPLIER_LIST_V7)
    fun getSupplierList_V7(@QueryMap hashMap: HashMap<String, String>): Observable<ExampleAllSupplier>

    @GET(NetworkConstants.GET_SUPPLIER_LIST_V6)
    fun getSupplierListV6(@QueryMap hashMap: HashMap<String, String>): Observable<HomeSupplierPaginationModel>

    @GET(NetworkConstants.GET_SUPPLIER_LIST_V2)
    fun getSupplierListV2(@QueryMap hashMap: HashMap<String, String>): Observable<HomeSupplierPaginationModel>


    @GET(NetworkConstants.GET_SUPPLIER_LIST_V6)
    fun getSupplierListV2Dummy(@QueryMap hashMap: HashMap<String, String>): Observable<ExampleAllSupplier>



    @GET(NetworkConstants.GET_SIGNATURE_PLATES)
    fun getSignaturePlates(@QueryMap hashMap: HashMap<String, String>): Observable<SuplierProdListModel>

    @GET(NetworkConstants.GET_SIGNATURE_MENUS)
    fun getSignatureMenu(@QueryMap hashMap: HashMap<String, String>): Observable<SuplierProdListModel>

    @GET(NetworkConstants.GET_CRAVE_MANIA)
    fun getCraveMania(@QueryMap hashMap: HashMap<String, String>): Observable<CraveManiaResponseData>

    @GET(NetworkConstants.GET_CRAVE_MANIA)
    fun getCraveManiaNew(@QueryMap hashMap: HashMap<String, String>): Observable<CraveManiaModel>

    @GET(NetworkConstants.CRAVE_MANIA_BANNER)
    fun getCraveBanner(): Observable<CraveManiaBannerModel>

    @GET(NetworkConstants.GET_SUPPLIER_LIST)
    fun getSupplierListClikat(@QueryMap hashMap: HashMap<String, String>): Observable<ResponseSuppliersList>

    @GET(NetworkConstants.GET_ALL_OFFER_LIST)
    fun getOfferList(@QueryMap hashMap: HashMap<String, String>): Observable<OfferListModel>

    @FormUrlEncoded
    @POST(NetworkConstants.CATEGORY_WISE_SUPPLIERS_LIST)
    fun getCategoryWiseSuppliers(@FieldMap hashMap: HashMap<String, String>): Observable<ResponseCategorySuppliersList>

    @GET(NetworkConstants.GET_ALL_OFFERS_LIST_V1)
    fun getOfferListV1(@QueryMap hashMap: HashMap<String, String>): Observable<OfferListModel>

    @GET("/pay")
    fun pay(@QueryMap hashMap: HashMap<String, String>): Observable<PayModel>

    @FormUrlEncoded
    @POST(NetworkConstants.GET_ALL_CUSTOMER_ADRS1)
    fun getAllAddress(@FieldMap params: HashMap<String, String>): Observable<CustomerAddressModel>

    @FormUrlEncoded
    @POST(NetworkConstants.GET_ALL_CUSTOMER_ADRS)
    fun getAllAddressnew(@FieldMap params: HashMap<String, String>): Observable<CustomerAddressModel>

    @FormUrlEncoded
    @POST(NetworkConstants.GET_ALL_CUSTOMER_ADRS)
    fun getZoneList(@FieldMap params: HashMap<String, String>): Observable<CustomerAddressListModel>


    @FormUrlEncoded
    @POST(NetworkConstants.DELETE_CUSTOMER_ADRS)
    fun deleteAddress(@FieldMap hashMap: HashMap<String, String>): Observable<ExampleCommon>

    @FormUrlEncoded
    @POST(NetworkConstants.ADD_CUSTOMER_ADRS)
    fun addAddress(@FieldMap hashMap: HashMap<String, String>): Observable<AddAddressModel>

    @FormUrlEncoded
    @POST(NetworkConstants.EDIT_CUSTOMER_ADRS)
    fun updateAddress(@FieldMap hashMap: HashMap<String, String>): Observable<AddAddressModel>

    @GET(NetworkConstants.VERIFY_USER_CODE)
    fun validateUserCode(@Query("uniqueId") key: String): Observable<GetDbKeyModel>

    @POST(NetworkConstants.PRODUCT_FILTERATION)
    fun getProductFilter(@Body param: FilterInputModel): Observable<ProductListModel>


    @POST(NetworkConstants.PRODUCT_FILTERATION)
    fun getRentalFilter(@Body param: FilterInputNew): Observable<ProductListModel>

    @FormUrlEncoded
    @POST("/get_supplier_list")
    fun getAllSuppliersNew(@FieldMap params: HashMap<String, String>): Observable<ExampleAllSupplier>


    @GET("/user/list_supplier_categories")
    fun getAllSuppliersCategories(@QueryMap params: HashMap<String, String>): Observable<SupplierCategoryModel>

    @GET("/user/getSupplierListByTagId")
    fun getAllSuppliersTagList(@QueryMap params: HashMap<String, String>): Observable<ExampleAllSupplier>

    @GET("/home/subcategory_listing_v1")
    fun getSubCategory(@QueryMap params: HashMap<String, String>): Observable<SubCategoryListModel>

    @GET("/user/list_tables_seating_capacities")
    fun getTableCapacity(@QueryMap params: HashMap<String, String>): Observable<TableCapacityModel>

    @FormUrlEncoded
    @POST("/get_product_details")
    fun getProductDetail(@FieldMap params: HashMap<String, String>): Observable<ProductDetailModel>


    @GET("/supplier_branch/get_product_add_ons")
    fun getProductAddOns(@QueryMap params: HashMap<String, String>): Observable<AddOnsResponse>

    @FormUrlEncoded
    @POST("/get_product_details")
    fun getProdDetail(@FieldMap params: HashMap<String, String>): Observable<ProductDetailModel>


    @GET("/agent/available/slots")
    fun getAvailabilitySlot(@HeaderMap headers: HashMap<String, String>, @QueryMap hashMap: HashMap<String, String>): Observable<AgentSlotsModel>


    @GET("/user/list_supplier_slots")
    fun getSupplierSlots(@QueryMap hashMap: HashMap<String, String>): Observable<AgentSlotsModel>

    @GET("user/list_supplier_slots/v1")
    fun getSupplierTableSlots(@QueryMap hashMap: HashMap<String, String>): Observable<SuppliersTableSlotsResponse>


    @FormUrlEncoded
    @POST("/user/hold_supplier_slots")
    fun apiHoldSlot(@FieldMap hashMap: HashMap<String, String>):Observable<SuccessModel>


    @GET("/user/supplier_availabilities")
    fun getSupplierAvailabilities(@QueryMap hashMap: HashMap<String, String>): Observable<SuppliersSlotsResponse>


    @GET("/agent/slots")
    fun getAvailabilitySlots(@HeaderMap headers: HashMap<String, String>, @QueryMap hashMap: HashMap<String, String>): Observable<AgentSlotsModel>


    @FormUrlEncoded
    @POST("/agent/get_agent_keys")
    fun getAgentListKey(@HeaderMap headers: HashMap<String, String>, @Field("val") param: String): Observable<GetAgentListKey>


//    @GET("/supplier/product_list")
//    fun getProductLst(@QueryMap hashMap: HashMap<String, String>): Observable<SuplierProdListModel>

    @GET("/supplier_branch/product_list")
    fun getProductLst(@QueryMap hashMap: HashMap<String, String>): Observable<SuplierProdListModel>

    @GET("/supplier_branch/mania/product_list")
    fun getManiaProductLst(@QueryMap hashMap: HashMap<String, String>): Observable<SuplierProdListModel>


    @GET("/v1/supplier/product_list")
    fun getSuppliersProductLst(@QueryMap hashMap: HashMap<String, String>): Observable<SuplierProdListModel>

    @POST("/update_cart_info")
    fun updateCartInfo(@Body updateCart: UpdateCartParam?): Observable<ExampleCommon>


    @POST("/sevice/agent/list")
    fun getAgentListing(@HeaderMap headers: HashMap<String, String>,
                        @Body params: GetAgentListParam): Observable<AgentListModel>

    @GET("/agent/availability")
    fun getAgentAvailability(@HeaderMap headers: HashMap<String, String>,
                             @QueryMap hashMap: HashMap<String, String>): Observable<AgentAvailabilityModel>


    @FormUrlEncoded
    @POST("/add_to_favourite")
    fun favouriteSupplier(@FieldMap params: HashMap<String, String>): Observable<ExampleCommon>


    @FormUrlEncoded
    @POST("/un_favourite")
    fun unfavSupplier(@FieldMap hashMap: HashMap<String, String>): Observable<ExampleCommon>


    @FormUrlEncoded
    @POST("/product_mark_fav_unfav")
    fun markWishList(@FieldMap params: HashMap<String?, String?>?): Observable<ExampleCommon?>?


    @FormUrlEncoded
    @POST("/user/sos_alert_notification")
    fun apiSos(@FieldMap params: HashMap<String?, String?>?): Observable<ExampleCommon?>?


    @POST("/v1/add_to_cart")
    fun getAddToCart(@Body cartInfoServerArray: CartInfoServerArray?): Observable<AddtoCartModel?>?

    @POST("/v2/genrate_order")
    fun generateOrder(@Body params: PlaceOrderInput?): Observable<ApiResponse<Any>>?

    @POST("/genrate_order")
    fun genOrder(@Body params: PlaceOrderInput?): Observable<PlaceOrderModel?>?


    @POST("/checkPromoV1")
    fun checkPromo(@Body promoCodeParam: CheckPromoCodeParam?): Observable<PromoCodeModel?>?


    @POST("/v2/user_order_details")
    fun orderDetails(@Body param: OrderDetailParam?): Observable<OrderDetailModel?>?


    @POST("/user/order/return_request")
    fun returnProduct(@Body param: ReturnProductModel?): Observable<OrderDetailModel?>?

    @FormUrlEncoded
    @POST("/cancel_order")
    fun cancelOrder(@FieldMap hashMap: HashMap<String, String>): Observable<ExampleCommon?>?


    @FormUrlEncoded
    @POST("/on_off_notification")
    fun changeNotifStatus(@FieldMap hashMap: HashMap<String?, String?>?): Observable<ExampleCommon?>?


    @Multipart
    @POST("/change_profile")
    fun uploadSingleImage(@Part("accessToken") accesstoken: @JvmSuppressWildcards RequestBody,
                          @Part image: MultipartBody.Part?): Observable<ExampleCommon?>?

    @GET("/get_all_notification")
    fun getAllNotifications(@Query("accessToken") accessToken: String,
                            @Query("skip") skip: Int,
                            @Query("limit") limit: Int): Observable<PojoNotification>

    @GET("/user/wallet/transactions")
    fun getAllWalletTransactions(@Query("skip") skip: Int,
                                 @Query("limit") limit: Int): Observable<WalletTransactionsResonse>

    @GET("/user/order/requestList")
    fun getRequestsList(@Query("offset") offset: Int?,
                        @Query("limit") limit: Int?): Observable<RequestListModel>

    @FormUrlEncoded
    @POST("/notification_language")
    fun notiLanguage(@FieldMap hashMap: HashMap<String?, String?>?): Observable<ExampleCommon?>?

    @POST("/v1/check_product_list")
    fun checkProductList(@Body params: CartReviewParam?): Observable<ApiMsgResponse<CartData>>?

    @FormUrlEncoded
    @POST("/forget_password")
    fun forgotPassword(@Field("emailId") emailId: String?): Observable<ExampleCommon?>?

    @FormUrlEncoded
    @POST("/login")
    fun login(@FieldMap hashMap: HashMap<String, String>?): Observable<PojoSignUp?>?

    @FormUrlEncoded
    @POST("/user/register/byPhone")
    fun apiRegisterByPhone(@FieldMap hashMap: HashMap<String, String>?): Observable<PojoSignUp?>?

    @FormUrlEncoded
    @POST("/facebook_login")
    fun fbLogin(@FieldMap hashMap: HashMap<String, String?>?): Observable<PojoSignUp?>?


    @GET("/user/google/matrix")
    fun getDistance(@QueryMap hashMap: HashMap<String, String>): Observable<ApiResponse<DistanceMatrix>>

    @POST("/user/google/matrix/V1")
    fun getMultipleDistance(@Body params: MultipleSupplierDistance?): Observable<ApiResponse<List<DistanceMatrix>>>

    @FormUrlEncoded
    @POST("/view_all_offer")
    fun getAllOffer(@FieldMap hashMap: HashMap<String, String>?): Observable<ViewAllOfferListModel?>?


    @GET("/common/getAllBanners")
    fun getAllBannersList(@QueryMap hashMap: HashMap<String, String>?): Observable<BannersListModel>?

    @FormUrlEncoded
    @POST("/v2/history_order")
    fun orderHistory(@FieldMap hashMap: HashMap<String, String>?): Observable<OrderListModel?>?

    /*15*/
    @FormUrlEncoded
    @POST("/v2/upcoming_order")
    fun upcomingOrder(@FieldMap hashMap: HashMap<String, String>?): Observable<OrderListModel?>?


    @PUT("/user/order/request_reject")
    fun apiCancelRequest(@Body body: CancelRequest): Observable<SuccessModel>

    @GET("/popular/product?")
    fun getPopularProd(@QueryMap hashMap: HashMap<String, String>?): Observable<ProductListModel?>?

    @GET("/popular/product/V1")
    fun getPopularProdV1(@QueryMap hashMap: HashMap<String, String>?): Observable<ProductListModel?>?

    @GET("/list_termsConditions")
    fun getTermsCondition(): Observable<TermsConditionModel?>?


    @FormUrlEncoded
    @POST("/common/variant_list")
    fun getCategoryVarient(@Field("category_id") category_id: String?): Observable<FilterVarientCatModel?>?

    @GET("/getQuestionsByCategoryId")
    fun getListOfQuestions(@QueryMap hashMap: HashMap<String, String>?): Observable<QuestionResponse?>?


    @GET(NetworkConstants.CHAT_LISTING)
    fun getChatMessages(@QueryMap hashMap: HashMap<String, String?>): Observable<ApiResponse<ArrayList<ChatMessageListing>>>

    @GET(NetworkConstants.REFERRAL_AMT)
    fun getReferralAmount(): Observable<ApiResponse<ReferalAmt>>

    @GET(NetworkConstants.REFERRAL_LIST)
    fun getReferralList(): Observable<ApiResponse<ReferralList>>

    @Multipart
    @POST(NetworkConstants.UPLOAD_DOC)
    fun uploadFile(@Part image: MultipartBody.Part?): Observable<ApiResponse<Any>>


    @Multipart
    @POST("/v1/user/registration")
    fun registerUserNew(@PartMap params: HashMap<String, RequestBody>?, @Part body: ArrayList<MultipartBody.Part>?): Observable<PojoSignUp?>?

    @Multipart
    @POST("/customer_register_step_third")
    fun signUpFinish(@PartMap hashMap: HashMap<String, RequestBody>?): Observable<PojoSignUp?>?


    @POST("/customer_register_step_first")
    fun signup_step_first(@Body params: RegisterParamModelV2?): Observable<PojoSignUp?>?


    @FormUrlEncoded
    @POST("/check_otp_new")
    fun verifyOtpNew(@FieldMap hashMap: HashMap<String, String>): Observable<PojoSignUp?>?


    @FormUrlEncoded
    @POST("/user/otp/verify")
    fun registerByPhoneOtpVerify(@FieldMap hashMap: HashMap<String, String>): Observable<PojoSignUp?>?

    @FormUrlEncoded
    @POST("/resend_otp")
    fun resendotp(@Field("accessToken") accesstoken: String?): Observable<ExampleCommon?>?


    @FormUrlEncoded
    @POST("/user/resend/otp")
    fun resendOtpRegisterByPhone(@Field("userCreatedId") userCreatedId: String?): Observable<ExampleCommon?>?

    @FormUrlEncoded
    @POST("/customer_register_step_second")
    fun signup_phone_2(@FieldMap hashMap: HashMap<String, String?>): Observable<PojoSignUp?>?

    @FormUrlEncoded
    @POST("/user/update/name")
    fun apiUpdateName(@FieldMap hashMap: HashMap<String, String>): Observable<PojoSignUp?>?

    @POST("/customer/add_card")
    fun saveCard(@Body params: SaveCardInputModel?): Observable<ApiResponse<AddCardResponseData>>

    @GET("/customer/get_cards")
    fun getSquareCardList(@QueryMap hashMap: HashMap<String, String>): Observable<ApiResponse<List<SavedCardList>>>

    @GET("/user_get_saved_cards")
    fun getSavedCards(@QueryMap hashMap: HashMap<String, String>): Observable<ApiResponse<List<CardData>>>


    @GET("/customer/get_cards")
    fun getStripeCardList(@QueryMap hashMap: HashMap<String, String>): Observable<ApiResponse<SavedData>>

    @GET("/customer/get_cards")
    fun getPayULatamCardList(@QueryMap hashMap: HashMap<String, String>): Observable<SavedCardData>

    @FormUrlEncoded
    @POST("/customer/delete_card")
    fun deleteSavedCard(@FieldMap hashMap: HashMap<String, String>): Observable<ApiResponse<Any>>

    @Multipart
    @POST("/user/order/request")
    fun uploadPres(@Part("prescription") prescription: @JvmSuppressWildcards RequestBody,
                   @Part("supplier_branch_id") supplier_branch_id: @JvmSuppressWildcards RequestBody,
                   @Part("deliveryId") deliveryId: @JvmSuppressWildcards RequestBody,
                   @Part("service_type") service_type: @JvmSuppressWildcards RequestBody,
                   @Part file: MultipartBody.Part?): Observable<ApiResponse<Any>>


    @Multipart
    @POST("/upload_chat_image")
    fun uploadImage(@Part file: MultipartBody.Part?): Observable<ApiResponse<ChatMessageListing>>

    @POST("/user/order/make_payment")
    fun makePayment(@Body param: MakePaymentInput): Observable<SuccessModel>

    @POST("/user/order/remaining_payment")
    fun remainingPayment(@Body param: MakePaymentInput): Observable<SuccessModel>


    @GET("/braintree/client-token")
    fun getBraintreeToken(): Observable<SuccessModel>


    @GET("agent/checkAgentSlotsAvailability")
    fun checkAgentTimeSlotAvail(@HeaderMap headers: HashMap<String, String>, @QueryMap hashMap: HashMap<String, String>):
            Observable<SuccessModel>


    @GET("/common/geofencing_gateways")
    fun geofenceGateway(@QueryMap hashMap: HashMap<String, String>): Observable<ApiResponse<GeofenceData>>


    /*    @FormUrlEncoded
    @POST("")
    Call<SubCategoryListModel> getSubCategory
            (@FieldMap HashMap<String, String> params);*/


    /*73*/
    @FormUrlEncoded
    @POST("/rate_product")
    fun rateProduct(@FieldMap hashMap: HashMap<String, String>): Observable<SuccessModel?>?

    @FormUrlEncoded
    @POST("/add_order_rating")
    fun rateOrder(@FieldMap hashMap: HashMap<String, String>): Observable<RatingModel?>?


    @POST("/supplier_rating")
    fun supplierRating(@Body param: RateInputModel): Observable<SuccessModel?>?


    @FormUrlEncoded
    @POST("/user_rate_order")
    fun agentRating(@FieldMap hashMap: HashMap<String, String>): Observable<SuccessModel?>?

    @FormUrlEncoded
    @POST("/user/wallet/share")
    fun apiSendMoney(@FieldMap hashMap: HashMap<String, String>): Observable<SuccessModel?>?

    @FormUrlEncoded
    @POST("/user/wallet/add_money")
    fun apiAddMoneyToWallet(@FieldMap hashMap: HashMap<String, String>): Observable<SuccessModel?>?

    //https://maps.googleapis.com/maps/api/distancematrix/json?units=imperial&origins=40.6655101,-73.89188969999998&destinations=40.6905615%2C-73.9976592%7C40.6905615%2C-73.9976592%7C40.6905615%2C-73.9976592%7C40.6905615%2C-73.9976592%7C40.6905615%2C-73.9976592%7C40.6905615%2C-73.9976592%7C40.659569%2C-73.933783%7C40.729029%2C-73.851524%7C40.6860072%2C-73.6334271%7C40.598566%2C-73.7527626%7C40.659569%2C-73.933783%7C40.729029%2C-73.851524%7C40.6860072%2C-73.6334271%7C40.598566%2C-73.7527626&key=YOUR_API_KEY

    @GET("/common/geofencing_tax")
    fun apiGetGeofencingTax(@Query("lat") latitude: Double,
                            @Query("long") longitude: Double,
                            @Query("branchId") branchId: String): Observable<ApiResponse<GeofenceData>>


    @GET("/user/Sadded/getPaymentUrl")
    fun getSaddedPaymentUrl(@Query("email") email: String, @Query("name") name: String,
                            @Query("amount") amount: String): Observable<AddCardResponse>

    @FormUrlEncoded
    @POST("get_myfatoorah_payment_url")
    fun getMyFatoorahPaymentUrl(@Field("currency") currency: String,
                                @Field("amount") amount: String): Observable<AddCardResponse>

    @POST("/user/change_order_status")
    fun changeOrderStatus(@Body orderStatus: ChangeOrderStatus): Observable<SuccessModel>

    @FormUrlEncoded
    @POST("/get_supplier_branch_list")
    fun getAllSuppliersBranchList(@FieldMap params: HashMap<String, String>): Observable<ExampleAllSupplier>



    @POST("/add_audio_order")
    fun uploadAudio(
        @Body partFile: RequestBody
    ): Observable<SuccessModel>

    @GET("/supplier_branch/product_list")
    fun getBranchProductLst(@QueryMap hashMap: HashMap<String, String>): Observable<SuplierProdListModel>

    @GET("/user/tap/getPaymentUrl")
    fun tapPayment(@QueryMap params: HashMap<String, String>): Observable<TapPaymentModel>

    // TODO: Requires converse payment response
    // Currently use dummy model class

    @FormUrlEncoded
    @POST("get_converge_payment_token")
    fun evalonPayment(@Field("amount") amount: String): Observable<SuccessModel>


    @FormUrlEncoded
    @POST("/user/windcave/getUrl")
    fun getWindCaveUrl(@FieldMap hashMap: HashMap<String, String>): Observable<WindCaveResponse>

    @FormUrlEncoded
    @POST("/user/mPaisa/getUrl")
    fun getMpaisaUrl(@FieldMap hashMap: HashMap<String, String>): Observable<AddCardResponse>


    @GET("/user/aamarpay/getPaymentUrl")
    fun getAamarPayUrl(@QueryMap hashMap: HashMap<String, String>): Observable<AddCardResponse>

    @GET("/user/thawani/getPaymentUrl")
    fun getThawaniPayUrl(@QueryMap hashMap: HashMap<String, String>): Observable<AddCardResponse>

    @FormUrlEncoded
    @POST("/user/hyperpay/getPaymentUrlId")
    fun getHyperPayUrl(@Field("amount") amount: String, @Field("currency") currency: String): Observable<AddCardResponse>


    @FormUrlEncoded
    @POST("/user/get_paymaya_url")
    fun getPaymayaUrl(@FieldMap hashMap: HashMap<String, String>): Observable<AddCardResponse>

    @GET("/user/telr/getPaymentUrl")
    fun getTelrPayUrl(@QueryMap hashMap: HashMap<String, String>): Observable<AddCardResponse>

    @GET("/braintree/client-token")
    fun getBrainTreeToken(): Observable<BraintreeTokenResoponse>

    @POST("/user/order/add_items")
    fun apiEditOrder(@Body orderRequest: EditOrderRequest): Observable<OrderDetailModel>

    @FormUrlEncoded
    @POST("/check_otp")
    fun verify_otp(@FieldMap dataMap: HashMap<String, String>): Observable<PojoSignUp?>?


    @POST("/client/user/track/info")
    fun checkUserSubc(@Body userSubInfoParam: UserSubInfoParam): Observable<SuccessModel>


    @GET("/get_timing_slot")
    fun getTimeSlots(@QueryMap params: HashMap<String, String>): Observable<TimeSlotModel>


    @GET("/get_user_subscriptions_list")
    fun getSubscriptionList(@QueryMap params: HashMap<String, String>): Observable<SubcripListModel>

    @POST("cancel_delete_user_subscription")
    fun cancelSubscription(@Body params: CancelSubscripInput): Observable<SuccessModel>


    @GET("/get_my_subscriptions_list")
    fun getMySubscripList(@QueryMap params: HashMap<String, String>): Observable<SubcripListModel>

    @POST("/buy_user_subscription")
    fun buyRenewSubscrip(@Body params: BuySubcripInput): Observable<BuySubscriptionModel>

    @GET(NetworkConstants.DIALOG_TOKEN)
    fun getDialogToken(): Observable<ApiResponse<DialogTokenData>>

    @GET("/get_user_suggestions")
    fun getSuggestionsApi(@Query("offset") offset: Int,
                          @Query("limit") limit: Int): Observable<SuggestionResponse>

    @FormUrlEncoded
    @POST("/add_feedback")
    fun apiAddFeedback(@FieldMap hashMap: HashMap<String, String>): Observable<SuccessModel>

    @GET("/user/loyality")
    fun getLoyaltyPointsData(): Observable<LoyaltyResponse>

    @FormUrlEncoded
    @POST("/get_loyality_setting")
    fun getLoyaltyPoints(@FieldMap hashMap: HashMap<String, String>?): Observable<LoyalityResponse>


    @POST("/google_login")
    fun googleLogin(@Body params: GoogleLoginInput): Observable<PojoSignUp?>?

    @GET("/getChatMessageId")
    fun getChatMessageId(@Query("userType") userType: String, @Query("user_created_id") user_created_id: String): Observable<LoyaltyResponse>

    @FormUrlEncoded
    @POST("/user/dhl/shipment/track")
    fun trackDhl(@Field("orderId") orderId: String): Observable<ResponseTrackDhl>

    @PUT("/view/product")
    fun viewProduct(@Body body: ViewProduct): Observable<SuccessCustomModel>

    @FormUrlEncoded
    @POST("/user/make_table_booking_requests")
    fun makeTableBookingRequest(@FieldMap hashMap: HashMap<String, String?>): Observable<SuplierProdListModel>

    @GET("/user/list_supplier_tables")
    fun getListOfTablesOfSupplier(@QueryMap params: HashMap<String, Any?>): Observable<TableListResponse>

    @GET("/user/list_supplier_tables")
    fun getListOfTablesOfSuppliers(@QueryMap params: HashMap<String, Int?>): Observable<TableListResponse>

    @GET("/user/list_booking_requests")
    fun getListOfBookedTables(@QueryMap params: HashMap<String, Any?>): Observable<BookedTableResponseModel>

    @FormUrlEncoded
    @POST("/user/accept_invitation")
    fun acceptTableInvitation(@Field("table_booking_id") tableId: String,
                              @Field("user_id") user_id: String): Observable<BraintreeTokenResoponse>


    @GET("/user/get_posts")
    fun getUserPostsList(@QueryMap hashMap: HashMap<String, String>): Observable<PostsResponseModel>

    @POST("/user/create_post")
    fun createPost(@Body params: CreatePostInput): Observable<SuccessModel>

    @PUT("/user/update_post")
    fun updatePost(@Body params: CreatePostInput): Observable<SuccessModel>

    @GET("/user/get_post_comments")
    fun getSocialComments(@QueryMap params: HashMap<String, String>): Observable<SocialCommentLikes>

    @GET("/user/get_post_likes_users_list")
    fun getSocialLikes(@QueryMap params: HashMap<String, String>): Observable<SocialCommentLikes>

    @FormUrlEncoded
    @POST("/user/add_comment")
    fun addComment(@Field("id") id: String, @Field("comment") comment: String): Observable<SuccessModel>

    @FormUrlEncoded
    @POST("/user/verify_table_number")
    fun verifyTableNUmber(@FieldMap hashMap: HashMap<String, String?>): Observable<VerifyTableResponeModel>

    @FormUrlEncoded
    @POST("/user/add_like")
    fun addLike(@Field("id") id: String): Observable<SuccessModel>

    @FormUrlEncoded
    @POST("/user/remove_like")
    fun removeLike(@Field("user_id") user_id: String, @Field("post_id") post_id: String): Observable<SuccessModel>

    @FormUrlEncoded
    @POST("/user/post_report")
    fun postReport(@FieldMap hashMap: HashMap<String, String>): Observable<SuccessModel>

    @FormUrlEncoded
    @POST("/user/block_unblock_user")
    fun blockUser(@FieldMap hashMap: HashMap<String, String>): Observable<SuccessModel>

    @FormUrlEncoded
    @POST("/user_logout")
    fun logoutUser(@FieldMap headers: HashMap<String, String>): Observable<SuccessModel>

    @DELETE("/user/post/delete/{id}")
    fun deletePost(@Path("id") id: String?): Observable<SuccessModel>

    @GET("/user/post_details")
    fun postDetails(@Query("id") id: String?): Observable<ApiResponse<List<PostItem>>>

    @FormUrlEncoded
    @POST("/user/ship_rocket/shipment/track")
    fun apiTrackShipRocket(@Field("orderId") order_id: String): Observable<ResponseTrackDhl>

    @GET("/user/promo_codes")
    fun getPromoCodes(@Query("skip") skip: Int, @Query("limit") limit: Int, @Query("supplierIds") supplierIds: String): Observable<PromoCodeResponse>

    @GET("zoom_auth")
    fun checkZoomAuth(): Observable<ResponseTrackDhl>

    @FormUrlEncoded
    @POST("zoom_create_meeting")
    fun createZoomLink(@FieldMap hashMap: HashMap<String, String>): Observable<ResponseZoomCall>

    @GET("/search_category_new")
    fun searchCategories(@QueryMap hashMap: HashMap<String, String>): Observable<HomeSupplierModel>

    @GET("/product_search")
    fun searchProductsList(@QueryMap hashMap: HashMap<String, String>): Observable<SeachProductsResponseModel>


}
