package com.codebrew.clikat.data


import android.content.Context
import android.util.Log
import com.codebrew.clikat.BuildConfig
import com.codebrew.clikat.data.constants.PrefenceConstants
import com.codebrew.clikat.data.model.SuccessCustomModel
import com.codebrew.clikat.data.model.api.*
import com.codebrew.clikat.data.model.api.SuccessModel
import com.codebrew.clikat.data.model.api.distance_matrix.DistanceMatrix
import com.codebrew.clikat.data.model.api.orderDetail.OrderDetailModel
import com.codebrew.clikat.data.model.api.tap_payment.TapPaymentModel
import com.codebrew.clikat.data.model.api.vehicleDetails.DeleteVehicleDetails
import com.codebrew.clikat.data.model.api.vehicleDetails.PostVehicleDetails
import com.codebrew.clikat.data.model.api.vehicleDetails.VehicalDetailsExample
import com.codebrew.clikat.data.model.others.*
import com.codebrew.clikat.data.network.ApiMsgResponse
import com.codebrew.clikat.data.network.ApiResponse
import com.codebrew.clikat.data.network.RestService
import com.codebrew.clikat.data.preferences.PreferenceHelper
import com.codebrew.clikat.modal.*
import com.codebrew.clikat.modal.PaymentModel.PayModel
import com.codebrew.clikat.modal.agent.AgentAvailabilityModel
import com.codebrew.clikat.modal.agent.AgentListModel
import com.codebrew.clikat.modal.agent.AgentSlotsModel
import com.codebrew.clikat.modal.agent.GetAgentListParam
import com.codebrew.clikat.modal.other.*
import com.codebrew.clikat.modal.slots.SuppliersSlotsResponse
import com.codebrew.clikat.modal.slots.SuppliersTableSlotsResponse
import com.codebrew.clikat.modal.wallet.WalletTransactionsResonse
import com.codebrew.clikat.module.crave_mania.CraveManiaModel
import com.codebrew.clikat.module.pre_delivery.Model.SlotsModel
import com.codebrew.clikat.modal.crave_mania.CraveManiaResponseData
import com.codebrew.clikat.module.cart.CardData
import com.codebrew.clikat.module.cart.SavedCardModel
import com.codebrew.clikat.module.cart.TimeSlotModel
import com.codebrew.clikat.module.crave_mania.CraveManiaBannerModel
import com.codebrew.clikat.module.loyaltyPoints.LoyalityResponse
import com.codebrew.clikat.module.rating.RatingModel
import com.codebrew.clikat.module.restaurant_detail.model.AddOnsResponse
import com.codebrew.clikat.preferences.DataNames
import io.reactivex.Observable
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.http.Body
import retrofit2.http.HeaderMap
import retrofit2.http.QueryMap
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.collections.ArrayList
import kotlin.collections.HashMap
import kotlin.collections.set


@Singleton

class AppDataManager @Inject
constructor(
        private val mContext: Context,
        private val mPreferencesHelper: PreferenceHelper,
        private val mApiHelper: RestService,
        private val retrofit: Retrofit
) : DataManager {

    override fun getMpaisaUrl(hashMap: java.util.HashMap<String, String>): Observable<AddCardResponse> {
        return mApiHelper.getMpaisaUrl(hashMap)
    }

    override fun getAamarPayUrl(hashMap: HashMap<String, String>): Observable<AddCardResponse> {
        return mApiHelper.getAamarPayUrl(hashMap)
    }

    override fun getThawaniPayUrl(hashMap: java.util.HashMap<String, String>): Observable<AddCardResponse> {
        return mApiHelper.getThawaniPayUrl(hashMap)
    }

    override fun getHyperPayUrl(amount: String, currency: String): Observable<AddCardResponse> {
        return mApiHelper.getHyperPayUrl(amount,currency)
    }

    override fun getPaymayaUrl(hashMap: java.util.HashMap<String, String>): Observable<AddCardResponse> {
        return mApiHelper.getPaymayaUrl(hashMap)
    }

    override fun getTelrPayUrl(hashMap: java.util.HashMap<String, String>): Observable<AddCardResponse> {
        return mApiHelper.getTelrPayUrl(hashMap)
    }

    override fun apiEditOrder(orderRequest: EditOrderRequest): Observable<OrderDetailModel> {
        return mApiHelper.apiEditOrder(orderRequest)
    }

    override fun checkUserSubc(userSubInfoParam: UserSubInfoParam): Observable<SuccessModel> {
        return mApiHelper.checkUserSubc(userSubInfoParam)
    }

    override fun getTimeSlots(params: HashMap<String, String>): Observable<TimeSlotModel> {
        return mApiHelper.getTimeSlots(params)
    }

    override fun getSubscriptionList(params: java.util.HashMap<String, String>): Observable<SubcripListModel> {
        return mApiHelper.getSubscriptionList(params)
    }

    override fun cancelSubscription(params: CancelSubscripInput): Observable<SuccessModel> {
        return mApiHelper.cancelSubscription(params)
    }

    override fun getMySubscripList(params: java.util.HashMap<String, String>): Observable<SubcripListModel> {
        return mApiHelper.getMySubscripList(params)
    }

    override fun buyRenewSubscrip(params: BuySubcripInput): Observable<BuySubscriptionModel> {
        return mApiHelper.buyRenewSubscrip(params)
    }

    override fun getDialogToken(): Observable<ApiResponse<DialogTokenData>> {
        return mApiHelper.getDialogToken()
    }

    override fun googleLogin(params: GoogleLoginInput): Observable<PojoSignUp?>? {
        return mApiHelper.googleLogin(params)
    }

    override fun makeTableBookingRequest(hashMap: java.util.HashMap<String, String?>): Observable<SuplierProdListModel> {
        return mApiHelper.makeTableBookingRequest(hashMap)
    }

    override fun getListOfTablesOfSupplier(params: java.util.HashMap<String, Any?>): Observable<TableListResponse> {
        return mApiHelper.getListOfTablesOfSupplier(params)
    }

    override fun getListOfTablesOfSuppliers(params: java.util.HashMap<String, Int?>): Observable<TableListResponse> {
        return mApiHelper.getListOfTablesOfSuppliers(params)
    }

    override fun getListOfBookedTables(params: java.util.HashMap<String, Any?>): Observable<BookedTableResponseModel> {
        return mApiHelper.getListOfBookedTables(params)
    }

    override fun acceptTableInvitation(tableId: String, user_id: String): Observable<BraintreeTokenResoponse> {
        return mApiHelper.acceptTableInvitation(tableId, user_id)
    }


    override fun getUserPostsList(hashMap: HashMap<String, String>): Observable<PostsResponseModel> {
        return mApiHelper.getUserPostsList(hashMap)
    }

    override fun signUpFinish(hashMap: HashMap<String, RequestBody>?): Observable<PojoSignUp?>? {
        return mApiHelper.signUpFinish(hashMap)
    }

    override fun createPost(params: CreatePostInput): Observable<SuccessModel> {
        return mApiHelper.createPost(params)
    }

    override fun updatePost(params: CreatePostInput): Observable<SuccessModel> {
        return mApiHelper.updatePost(params)
    }

    override fun getSocialComments(params: HashMap<String, String>): Observable<SocialCommentLikes> {
        return mApiHelper.getSocialComments(params)
    }

    override fun getSocialLikes(params: HashMap<String, String>): Observable<SocialCommentLikes> {
        return mApiHelper.getSocialLikes(params)
    }

    override fun addComment(id: String, comment: String): Observable<SuccessModel> {
        return mApiHelper.addComment(id, comment)
    }

    override fun addLike(id: String): Observable<SuccessModel> {
        return mApiHelper.addLike(id)
    }

    override fun removeLike(user_id: String, post_id: String): Observable<SuccessModel> {
        return mApiHelper.removeLike(user_id, post_id)
    }

    override fun postReport(hashMap: HashMap<String, String>): Observable<SuccessModel> {
        return mApiHelper.postReport(hashMap)
    }

    override fun blockUser(hashMap: java.util.HashMap<String, String>): Observable<SuccessModel> {
        return mApiHelper.blockUser(hashMap)
    }

    override fun logoutUser(headers: java.util.HashMap<String, String>): Observable<SuccessModel> {
        return mApiHelper.logoutUser(headers)
    }

    override fun deletePost(id: String?): Observable<SuccessModel> {
        return mApiHelper.deletePost(id)
    }

    override fun postDetails(id: String?): Observable<ApiResponse<List<PostItem>>> {
        return mApiHelper.postDetails(id)
    }

    override fun apiTrackShipRocket(order_id: String): Observable<ResponseTrackDhl> {
        return mApiHelper.apiTrackShipRocket(order_id)
    }

    override fun getPromoCodes(skip: Int, limit: Int,supplierIds:String): Observable<PromoCodeResponse> {
        return mApiHelper.getPromoCodes(skip, limit,supplierIds)
    }

    override fun checkZoomAuth(): Observable<ResponseTrackDhl> {
        return mApiHelper.checkZoomAuth()
    }

    override fun createZoomLink(hashMap: java.util.HashMap<String, String>): Observable<ResponseZoomCall> {
        return mApiHelper.createZoomLink(hashMap)
    }

    override fun searchCategories(hashMap: java.util.HashMap<String, String>): Observable<HomeSupplierModel> {
        return mApiHelper.searchCategories(hashMap)
    }

    override fun searchProductsList(hashMap: java.util.HashMap<String, String>): Observable<SeachProductsResponseModel> {
        return mApiHelper.searchProductsList(hashMap)
    }


    override fun verifyTableNUmber(hashMap: HashMap<String, String?>): Observable<VerifyTableResponeModel> {
        return mApiHelper.verifyTableNUmber(hashMap)
    }

    override fun getChatMessageId(userType: String, user_created_id: String): Observable<LoyaltyResponse> {
        return mApiHelper.getChatMessageId(userType, user_created_id)
    }

    override fun trackDhl(orderId: String): Observable<ResponseTrackDhl> {
        return mApiHelper.trackDhl(orderId)
    }

    override fun viewProduct(body: ViewProduct): Observable<SuccessCustomModel> {
        return mApiHelper.viewProduct(body)
    }

    override fun getSuggestionsApi(skip: Int, limit: Int): Observable<SuggestionResponse> {
        return mApiHelper.getSuggestionsApi(skip, limit)
    }

    override fun apiAddFeedback(hashMap: java.util.HashMap<String, String>): Observable<SuccessModel> {
        return mApiHelper.apiAddFeedback(hashMap)
    }

    override fun getLoyaltyPointsData(): Observable<LoyaltyResponse> {
        return mApiHelper.getLoyaltyPointsData()
    }

    override fun getLoyaltyPoints(hashMap: HashMap<String, String>?): Observable<LoyalityResponse> {
        return mApiHelper.getLoyaltyPoints(hashMap)
    }

    override fun getBrainTreeToken(): Observable<BraintreeTokenResoponse> {
        return mApiHelper.getBrainTreeToken()
    }

    override fun verify_otp(dataMap: HashMap<String, String>): Observable<PojoSignUp?>? {
        return mApiHelper.verify_otp(dataMap)
    }

    override fun getWindCaveUrl(hashMap: java.util.HashMap<String, String>): Observable<WindCaveResponse> {
        return mApiHelper.getWindCaveUrl(hashMap)
    }

    override fun getMyFatoorahPaymentUrl(currency: String, amount: String): Observable<AddCardResponse> {
        return mApiHelper.getMyFatoorahPaymentUrl(currency, amount)
    }

    override fun changeOrderStatus(orderStatus: ChangeOrderStatus): Observable<SuccessModel> {
        return mApiHelper.changeOrderStatus(orderStatus)
    }

    override fun getAllSuppliersBranchList(params: java.util.HashMap<String, String>): Observable<ExampleAllSupplier> {
        return mApiHelper.getAllSuppliersBranchList(params)
    }

    override fun uploadAudio(partFile: RequestBody): Observable<SuccessModel> {
        return mApiHelper.uploadAudio(partFile)
    }

    override fun getBranchProductLst(hashMap: java.util.HashMap<String, String>): Observable<SuplierProdListModel> {
        return mApiHelper.getBranchProductLst(hashMap)
    }

    override fun tapPayment(params: java.util.HashMap<String, String>): Observable<TapPaymentModel> {
        return mApiHelper.tapPayment(params)
    }

    override fun evalonPayment(amount: String): Observable<SuccessModel> {
        return mApiHelper.evalonPayment(amount)
    }

    override fun apiGetGeofencingTax(
            latitude: Double,
            longitude: Double,
            branchId: String
    ): Observable<ApiResponse<GeofenceData>> {
        return mApiHelper.apiGetGeofencingTax(latitude, longitude, branchId)
    }

    override fun getSaddedPaymentUrl(email: String, name: String, amount: String): Observable<AddCardResponse> {
        return mApiHelper.getSaddedPaymentUrl(email, name, amount)
    }

    override fun getSupplierListClikat(hashMap: HashMap<String, String>): Observable<ResponseSuppliersList> {
        return mApiHelper.getSupplierListClikat(hashMap)
    }

    override fun gwtSuppliersWishList(param: java.util.HashMap<String, String>): Observable<WishListSuppliersModel> {
        return mApiHelper.gwtSuppliersWishList(param)
    }


    override fun apiCancelRequest(body: CancelRequest): Observable<SuccessModel> {
        return mApiHelper.apiCancelRequest(body)
    }

    override fun getRequestsList(offset: Int?, limit: Int?): Observable<RequestListModel> {
        return mApiHelper.getRequestsList(offset, limit)
    }

    override fun getAllNotifications(accessToken: String, skip: Int, limit: Int): Observable<PojoNotification> {
        return mApiHelper.getAllNotifications(accessToken, skip, limit)
    }

    override fun getAllWalletTransactions(skip: Int, limit: Int): Observable<WalletTransactionsResonse> {
        return mApiHelper.getAllWalletTransactions(skip, limit)
    }

    override fun returnProduct(param: ReturnProductModel?): Observable<OrderDetailModel?>? {
        return mApiHelper.returnProduct(param)
    }


    override fun getAllSuppliersNew(params: java.util.HashMap<String, String>): Observable<ExampleAllSupplier> {
        return mApiHelper.getAllSuppliersNew(params)
    }

    override fun getAllSuppliersCategories(params: java.util.HashMap<String, String>): Observable<SupplierCategoryModel> {
        return mApiHelper.getAllSuppliersCategories(params)
    }

    override fun getAllSuppliersTagList(params: HashMap<String, String>): Observable<ExampleAllSupplier> {
        return mApiHelper.getAllSuppliersTagList(params)
    }

    override fun favouriteSupplier(params: HashMap<String, String>): Observable<ExampleCommon> {
        return mApiHelper.favouriteSupplier(params)
    }

    override fun unfavSupplier(hashMap: HashMap<String, String>): Observable<ExampleCommon> {
        return mApiHelper.unfavSupplier(hashMap)
    }

    override fun markWishList(params: HashMap<String?, String?>?): Observable<ExampleCommon?>? {
        return mApiHelper.markWishList(params)
    }

    override fun apiSos(params: java.util.HashMap<String?, String?>?): Observable<ExampleCommon?>? {
        return mApiHelper.apiSos(params)
    }

    override fun getAddToCart(cartInfoServerArray: CartInfoServerArray?): Observable<AddtoCartModel?>? {
        cartInfoServerArray?.deviceId = mPreferencesHelper.getKeyValue(DataNames.REGISTRATION_ID, PrefenceConstants.TYPE_STRING).toString()
        return mApiHelper.getAddToCart(cartInfoServerArray)
    }

    override fun generateOrder(params: PlaceOrderInput?): Observable<ApiResponse<Any>>? {
        return mApiHelper.generateOrder(params)
    }

    override fun genOrder(params: PlaceOrderInput?): Observable<PlaceOrderModel?>? {
        return mApiHelper.genOrder(params)
    }

    override fun checkPromo(promoCodeParam: CheckPromoCodeParam?): Observable<PromoCodeModel?>? {
        return mApiHelper.checkPromo(promoCodeParam)
    }

    override fun orderDetails(param: OrderDetailParam?): Observable<OrderDetailModel?>? {
        return mApiHelper.orderDetails(param)
    }

    override fun cancelOrder(hashMap: HashMap<String, String>): Observable<ExampleCommon?>? {
        return mApiHelper.cancelOrder(hashMap)
    }

    override fun changeNotifStatus(hashMap: HashMap<String?, String?>?): Observable<ExampleCommon?>? {
        return mApiHelper.changeNotifStatus(hashMap)
    }

    override fun uploadSingleImage(accesstoken: RequestBody, image: MultipartBody.Part?): Observable<ExampleCommon?>? {
        return mApiHelper.uploadSingleImage(accesstoken, image)
    }

    override fun notiLanguage(hashMap: HashMap<String?, String?>?): Observable<ExampleCommon?>? {
        return mApiHelper.notiLanguage(hashMap)
    }

    override fun checkProductList(params: CartReviewParam?): Observable<ApiMsgResponse<CartData>>? {
        return mApiHelper.checkProductList(params)
    }

    override fun forgotPassword(emailId: String?): Observable<ExampleCommon?>? {
        return mApiHelper.forgotPassword(emailId)
    }

    override fun login(hashMap: HashMap<String, String>?): Observable<PojoSignUp?>? {
        return mApiHelper.login(hashMap)
    }

    override fun apiRegisterByPhone(hashMap: java.util.HashMap<String, String>?): Observable<PojoSignUp?>? {
        return mApiHelper.apiRegisterByPhone(hashMap)
    }


    override fun fbLogin(hashMap: HashMap<String, String?>?): Observable<PojoSignUp?>? {
        return mApiHelper.fbLogin(hashMap)
    }

    override fun getDistance(hashMap: HashMap<String, String>): Observable<ApiResponse<DistanceMatrix>> {
        return mApiHelper.getDistance(hashMap)
    }

    override fun getMultipleDistance(params: MultipleSupplierDistance?): Observable<ApiResponse<List<DistanceMatrix>>> {
        return mApiHelper.getMultipleDistance(params)
    }

    override fun getAllOffer(hashMap: HashMap<String, String>?): Observable<ViewAllOfferListModel?>? {
        return mApiHelper.getAllOffer(hashMap)
    }

    override fun getAllBannersList(hashMap: java.util.HashMap<String, String>?): Observable<BannersListModel>? {
        return mApiHelper.getAllBannersList(hashMap)
    }

    override fun orderHistory(hashMap: HashMap<String, String>?): Observable<OrderListModel?>? {
        return mApiHelper.orderHistory(hashMap)
    }

    override fun upcomingOrder(hashMap: HashMap<String, String>?): Observable<OrderListModel?>? {
        return mApiHelper.upcomingOrder(hashMap)
    }

    override fun getPopularProd(hashMap: HashMap<String, String>?): Observable<ProductListModel?>? {
        return mApiHelper.getPopularProd(hashMap)
    }

    override fun getPopularProdV1(hashMap: java.util.HashMap<String, String>?): Observable<ProductListModel?>? {
        return mApiHelper.getPopularProdV1(hashMap)
    }

    override fun getTermsCondition(): Observable<TermsConditionModel?>? {
        return mApiHelper.getTermsCondition()
    }

    override fun getCategoryVarient(category_id: String?): Observable<FilterVarientCatModel?>? {
        return mApiHelper.getCategoryVarient(category_id)
    }

    override fun getListOfQuestions(hashMap: java.util.HashMap<String, String>?): Observable<QuestionResponse?>? {
        return mApiHelper.getListOfQuestions(hashMap)
    }

    override fun getChatMessages(hashMap: HashMap<String, String?>): Observable<ApiResponse<ArrayList<ChatMessageListing>>> {
        return mApiHelper.getChatMessages(hashMap)
    }

    override fun getReferralAmount(): Observable<ApiResponse<ReferalAmt>> {
        return mApiHelper.getReferralAmount()
    }

    override fun getReferralList(): Observable<ApiResponse<ReferralList>> {
        return mApiHelper.getReferralList()
    }

    override fun uploadFile(image: MultipartBody.Part?): Observable<ApiResponse<Any>> {
        return mApiHelper.uploadFile(image)
    }

    override fun registerUserNew(params: HashMap<String, RequestBody>?, body: ArrayList<MultipartBody.Part>?): Observable<PojoSignUp?>? {
        return mApiHelper.registerUserNew(params, body)
    }


    override fun verifyOtpNew(hashMap: java.util.HashMap<String, String>): Observable<PojoSignUp?>? {
        return mApiHelper.verifyOtpNew(hashMap)
    }

    override fun registerByPhoneOtpVerify(hashMap: java.util.HashMap<String, String>): Observable<PojoSignUp?>? {
        return mApiHelper.registerByPhoneOtpVerify(hashMap)
    }

    override fun resendotp(accesstoken: String?): Observable<ExampleCommon?>? {
        return mApiHelper.resendotp(accesstoken)
    }

    override fun resendOtpRegisterByPhone(userCreatedId: String?): Observable<ExampleCommon?>? {
        return mApiHelper.resendOtpRegisterByPhone(userCreatedId)
    }

    override fun signup_phone_2(hashMap: java.util.HashMap<String, String?>): Observable<PojoSignUp?>? {
        return mApiHelper.signup_phone_2(hashMap)
    }

    override fun apiUpdateName(hashMap: java.util.HashMap<String, String>): Observable<PojoSignUp?>? {
        return mApiHelper.apiUpdateName(hashMap)
    }

    override fun saveCard(params: SaveCardInputModel?): Observable<ApiResponse<AddCardResponseData>> {
        return mApiHelper.saveCard(params)
    }

    override fun getSquareCardList(hashMap: java.util.HashMap<String, String>): Observable<ApiResponse<List<SavedCardList>>> {
        return mApiHelper.getSquareCardList(hashMap)
    }

    override fun getSavedCards(hashMap: HashMap<String, String>): Observable<ApiResponse<List<CardData>>> {
        return mApiHelper.getSavedCards(hashMap)
    }

    override fun getStripeCardList(hashMap: java.util.HashMap<String, String>): Observable<ApiResponse<SavedData>> {
        return mApiHelper.getStripeCardList(hashMap)
    }

    override fun getPayULatamCardList(hashMap: java.util.HashMap<String, String>): Observable<SavedCardData> {
        return mApiHelper.getPayULatamCardList(hashMap)
    }


    override fun deleteSavedCard(hashMap: java.util.HashMap<String, String>): Observable<ApiResponse<Any>> {
        return mApiHelper.deleteSavedCard(hashMap)
    }

    override fun uploadPres(prescription: RequestBody, supplier_branch_id: RequestBody, deliveryId: RequestBody, service_type: RequestBody, file: MultipartBody.Part?): Observable<ApiResponse<Any>> {
        return mApiHelper.uploadPres(prescription, supplier_branch_id, deliveryId, service_type, file)
    }

    override fun uploadImage(file: MultipartBody.Part?): Observable<ApiResponse<ChatMessageListing>> {
        return mApiHelper.uploadImage(file)
    }


    override fun makePayment(param: MakePaymentInput): Observable<SuccessModel> {
        return mApiHelper.makePayment(param)
    }

    override fun remainingPayment(param: MakePaymentInput): Observable<SuccessModel> {
        return mApiHelper.remainingPayment(param)
    }

    override fun getBraintreeToken(): Observable<SuccessModel> {
        return mApiHelper.getBraintreeToken()
    }

    override fun checkAgentTimeSlotAvail(@HeaderMap headers: java.util.HashMap<String, String>, @QueryMap hashMap: java.util.HashMap<String, String>): Observable<SuccessModel> {
        return mApiHelper.checkAgentTimeSlotAvail(headers, hashMap)
    }

    override fun geofenceGateway(hashMap: java.util.HashMap<String, String>): Observable<ApiResponse<GeofenceData>> {
        return mApiHelper.geofenceGateway(hashMap)
    }


    override fun rateProduct(hashMap: java.util.HashMap<String, String>): Observable<SuccessModel?>? {
        return mApiHelper.rateProduct(hashMap)
    }

    override fun supplierRating(hashMap: RateInputModel): Observable<SuccessModel?>? {
        return mApiHelper.supplierRating(hashMap)
    }

    override fun rateOrder(hashMap: java.util.HashMap<String, String>): Observable<RatingModel?>? {
        return mApiHelper.rateOrder(hashMap)
    }

    override fun agentRating(hashMap: java.util.HashMap<String, String>): Observable<SuccessModel?>? {
        return mApiHelper.agentRating(hashMap)
    }

    override fun apiSendMoney(hashMap: java.util.HashMap<String, String>): Observable<SuccessModel?>? {
        return mApiHelper.apiSendMoney(hashMap)
    }

    override fun apiAddMoneyToWallet(hashMap: java.util.HashMap<String, String>): Observable<SuccessModel?>? {
        return mApiHelper.apiAddMoneyToWallet(hashMap)
    }

    override fun getAvailabilitySlots(headers: HashMap<String, String>, hashMap: java.util.HashMap<String, String>): Observable<AgentSlotsModel> {
        return mApiHelper.getAvailabilitySlots(headers, hashMap)
    }

    override fun getAgentAvailability(@HeaderMap headers: HashMap<String, String>, @QueryMap hashMap: java.util.HashMap<String, String>): Observable<AgentAvailabilityModel> {
        return mApiHelper.getAgentAvailability(headers, hashMap)
    }

    override fun getAgentListing(@HeaderMap headers: HashMap<String, String>, @Body params: GetAgentListParam): Observable<AgentListModel> {
        return mApiHelper.getAgentListing(headers, params)
    }


    override fun updateCartInfo(updateCart: UpdateCartParam?): Observable<ExampleCommon> {
        return mApiHelper.updateCartInfo(updateCart)
    }

    override fun getProductLst(hashMap: java.util.HashMap<String, String>): Observable<SuplierProdListModel> {
        return mApiHelper.getProductLst(hashMap)
    }

    override fun getManiaProductLst(hashMap: HashMap<String, String>): Observable<SuplierProdListModel> {
        return mApiHelper.getManiaProductLst(hashMap)
    }

    override fun getSuppliersProductLst(hashMap: java.util.HashMap<String, String>): Observable<SuplierProdListModel> {
        return mApiHelper.getSuppliersProductLst(hashMap)
    }

    override fun getAgentListKey(@HeaderMap headers: HashMap<String, String>, param: String): Observable<GetAgentListKey> {
        return mApiHelper.getAgentListKey(headers, param)
    }

    override fun getAvailabilitySlot(@HeaderMap headers: HashMap<String, String>, hashMap: java.util.HashMap<String, String>): Observable<AgentSlotsModel> {
        return mApiHelper.getAvailabilitySlot(headers, hashMap)
    }

    override fun getSupplierSlots(hashMap: java.util.HashMap<String, String>): Observable<AgentSlotsModel> {
        return mApiHelper.getSupplierSlots(hashMap)
    }

    override fun getSupplierTableSlots(hashMap: java.util.HashMap<String, String>): Observable<SuppliersTableSlotsResponse> {
        return mApiHelper.getSupplierTableSlots(hashMap)
    }

    override fun apiHoldSlot(hashMap: java.util.HashMap<String, String>): Observable<SuccessModel> {
        return mApiHelper.apiHoldSlot(hashMap)
    }

    override fun getSupplierAvailabilities(hashMap: HashMap<String, String>): Observable<SuppliersSlotsResponse> {
        return mApiHelper.getSupplierAvailabilities(hashMap)
    }

    override fun getProductDetail(params: java.util.HashMap<String, String>): Observable<ProductDetailModel> {
        return mApiHelper.getProductDetail(params)
    }

    override fun getProductAddOns(params: HashMap<String, String>): Observable<AddOnsResponse> {
        return mApiHelper.getProductAddOns(params)
    }

    override fun getProdDetail(params: java.util.HashMap<String, String>): Observable<ProductDetailModel> {
        return mApiHelper.getProdDetail(params)
    }

    override fun getSubCategory(params: java.util.HashMap<String, String>): Observable<SubCategoryListModel> {
        return mApiHelper.getSubCategory(params)
    }

    override fun getTableCapacity(params: java.util.HashMap<String, String>): Observable<TableCapacityModel> {
        return mApiHelper.getTableCapacity(params)
    }

    override fun getProductFilter(param: FilterInputModel): Observable<ProductListModel> {
        return mApiHelper.getProductFilter(param)
    }

    override fun getRentalFilter(param: FilterInputNew): Observable<ProductListModel> {
        return mApiHelper.getRentalFilter(param)
    }

    override fun validateUserCode(key: String): Observable<GetDbKeyModel> {
        return mApiHelper.validateUserCode(key)
    }

    override fun deleteAddress(hashMap: java.util.HashMap<String, String>): Observable<ExampleCommon> {
        return mApiHelper.deleteAddress(hashMap)
    }

    override fun addAddress(hashMap: java.util.HashMap<String, String>): Observable<AddAddressModel> {
        return mApiHelper.addAddress(hashMap)
    }

    override fun updateAddress(hashMap: java.util.HashMap<String, String>): Observable<AddAddressModel> {
        return mApiHelper.updateAddress(hashMap)
    }

    override fun getAllAddress(params: HashMap<String, String>): Observable<CustomerAddressModel> {
        return mApiHelper.getAllAddress(params)
    }

    override fun getAllAddressnew(params: HashMap<String, String>): Observable<CustomerAddressModel> {
        return mApiHelper.getAllAddressnew(params)
    }

    override fun getZoneList(params: HashMap<String, String>): Observable<CustomerAddressListModel> {
        return mApiHelper.getZoneList(params)
    }

    override fun updateUserInfnew(): HashMap<String, String> {
        val param = HashMap<String, String>()

        param["languageId"] = "14"
        val mLocUser = mPreferencesHelper.getGsonValue(PrefenceConstants.ADRS_DATA, AddressBean::class.java)
        if (mLocUser != null) {
            param["latitude"] = mLocUser.latitude ?: ""
            param["longitude"] = mLocUser.longitude ?: ""

        }
        return param
    }
    override fun updateUserInf(): HashMap<String, String> {
        val param = HashMap<String, String>()

        param["languageId"] = mPreferencesHelper.getLangCode()
        val mLocUser = mPreferencesHelper.getGsonValue(PrefenceConstants.ADRS_DATA, AddressBean::class.java)
        if (mLocUser != null) {
            param["latitude"] = mLocUser.latitude ?: ""
            param["longitude"] = mLocUser.longitude ?: ""
        }
        return param
    }


    override fun getSupplierList(hashMap: HashMap<String, String>): Observable<HomeSupplierModel> {
        return mApiHelper.getSupplierList(hashMap)
    }

    override fun getSupplierList6(hashMap: java.util.HashMap<String, String>): Observable<SearchSupplierModel> {
        return mApiHelper.getSupplierList6(hashMap)
    }

    override fun getSupplierList7(hashMap: java.util.HashMap<String, String>): Observable<SearchSupplierModel> {
        return mApiHelper.getSupplierList7(hashMap)
    }

    override fun getSlots(hashMap: HashMap<String, String>): Observable<SlotsModel> {
        return mApiHelper.getSlots(hashMap)
    }

    override fun getAllSupplierList(hashMap: HashMap<String, String>): Observable<ExampleAllSupplierNew> {
        return mApiHelper.getAllSupplierList(hashMap)
    }

    override fun getSupplierListV1(hashMap: java.util.HashMap<String, String>): Observable<HomeSupplierModel> {
        return mApiHelper.getSupplierListV1(hashMap)
    }

    override fun getbranchList(hashMap: java.util.HashMap<String, String>): Observable<HomeSupplierModel> {
        return mApiHelper.getbranchList(hashMap)
    }

    override fun getSupplierListV7(hashMap: java.util.HashMap<String, String>): Observable<HomeSupplierPaginationModel> {
        return mApiHelper.getSupplierListV7(hashMap)
    }

    override fun getSupplierList_V7(hashMap: java.util.HashMap<String, String>): Observable<ExampleAllSupplier> {
        return mApiHelper.getSupplierList_V7(hashMap)
    }

    override fun getSupplierListV6(hashMap: java.util.HashMap<String, String>): Observable<HomeSupplierPaginationModel> {
        return mApiHelper.getSupplierListV6(hashMap)
    }

    override fun getCraveManiaNew(hashMap: java.util.HashMap<String, String>): Observable<CraveManiaModel> {
        return mApiHelper.getCraveManiaNew(hashMap)
    }
    override fun getCraveBanner(): Observable<CraveManiaBannerModel> {
        return mApiHelper.getCraveBanner()
    }
    override fun getSupplierListV2(hashMap: java.util.HashMap<String, String>): Observable<HomeSupplierPaginationModel> {
        return mApiHelper.getSupplierListV2(hashMap)
    }


    override fun getSupplierListV2Dummy(hashMap: java.util.HashMap<String, String>): Observable<ExampleAllSupplier> {
        return mApiHelper.getSupplierListV2Dummy(hashMap)
    }


    override fun getSignaturePlates(hashMap: java.util.HashMap<String, String>): Observable<SuplierProdListModel> {
        return mApiHelper.getSignaturePlates(hashMap)
    }

    override fun getSignatureMenu(hashMap: java.util.HashMap<String, String>): Observable<SuplierProdListModel> {
        return mApiHelper.getSignatureMenu(hashMap)
    }

    override fun getCraveMania(hashMap: java.util.HashMap<String, String>): Observable<CraveManiaResponseData> {
        return  mApiHelper.getCraveMania(hashMap)
    }

    override fun getOfferList(hashMap: HashMap<String, String>): Observable<OfferListModel> {
        return mApiHelper.getOfferList(hashMap)
    }

    override fun getCategoryWiseSuppliers(hashMap: java.util.HashMap<String, String>): Observable<ResponseCategorySuppliersList> {
        return mApiHelper.getCategoryWiseSuppliers(hashMap)
    }

    override fun getOfferListV1(hashMap: java.util.HashMap<String, String>): Observable<OfferListModel> {
        return mApiHelper.getOfferListV1(hashMap)
    }

    override fun pay(hashMap: java.util.HashMap<String, String>): Observable<PayModel> {
        return mApiHelper.pay(hashMap)
    }

    override fun getAllCategoryNew(hashMap: HashMap<String, String>): Observable<CategoryListModel> {
        return mApiHelper.getAllCategoryNew(hashMap)
    }

    override fun getUserDEtails(hashMap: java.util.HashMap<String, String>): Observable<VehicalDetailsExample> {
        return mApiHelper.getUserDEtails(hashMap)
    }


    override fun deleteUser(id: HashMap<String, String>): Observable<DeleteVehicleDetails> {
        return mApiHelper.deleteUser(id)
    }

    override fun postUserDEtails(hashMap: java.util.HashMap<String, String>): Observable<PostVehicleDetails> {
        return mApiHelper.postUserDEtails(hashMap)
    }

    override fun getSetting(): Observable<SettingModel> {
        return mApiHelper.getSetting()
    }


    override fun getCount(accessToken: String): Observable<PojoPendingOrders> {
        return mApiHelper.getCount(accessToken)
    }


    override fun markSupplierFav(params: HashMap<String, String>): Observable<ExampleCommon> {
        return mApiHelper.markSupplierFav(params)
    }

    override fun markSupplierUnFav(params: HashMap<String, String>): Observable<ExampleCommon> {
        return mApiHelper.markSupplierUnFav(params)
    }

    override fun getSupplierDetails(params: HashMap<String, String>): Observable<ExampleSupplierDetail> {
        return mApiHelper.getSupplierDetails(params)
    }

    override fun getWishlist(param: HashMap<String, String>): Observable<WishListModel> {
        return mApiHelper.getWishlist(param)
    }

    override fun final_order(params: FinalOrderInput): Observable<FinalOrderPojo?> {
        return mApiHelper.final_order(params)
    }

    override fun getPolYLine(origin: String, destination: String, language: String?, key: String?): Call<ResponseBody> {
        return mApiHelper.getPolYLine(origin, destination, language, key)
    }

    override fun createDocument(body: PandaDocResponse): Observable<SuccessModel?> {
        return mApiHelper.createDocument(body)
    }

    override fun createAccessToken(): Observable<SuccessModel?> {
        return mApiHelper.createAccessToken()
    }

    override fun getRoadPoints(points: String, key: String): Call<RoadPoints> {
        return mApiHelper.getRoadPoints(points, key)
    }


    override fun updateApiHeader(userId: Long?, accessToken: String) {

    }

    override fun getRetrofitUtl(): Retrofit {
        return retrofit
    }

    override fun getUserSubscData(): UserSubInfoParam {

        val userSubsc = UserSubInfoParam().apply {
            db_secret_key = getKeyValue(PrefenceConstants.DB_SECRET, PrefenceConstants.TYPE_STRING).toString()
            user_id = getKeyValue(PrefenceConstants.USER_ID, PrefenceConstants.TYPE_STRING).toString().toInt()
            platform = "ANDROID"
            key_value = KeyValue().apply {
                deviceType = "Android"
                bundleId = BuildConfig.APPLICATION_ID
            }
        }

        return userSubsc
    }


    override fun addGsonValue(key: String, value: String) {
        return mPreferencesHelper.addGsonValue(key, value)
    }

    override fun <T> getGsonValue(key: String, type: Class<T>): T? {
        return mPreferencesHelper.getGsonValue(key, type)
    }


    override fun setUserAsLoggedOut() {
        mPreferencesHelper.logout()
    }


    override fun setkeyValue(key: String, value: Any) {
        return mPreferencesHelper.setkeyValue(key, value)
    }

    override fun getKeyValue(key: String, type: String): Any? {
        return mPreferencesHelper.getKeyValue(key, type)
    }

    override fun logout() {

        //Toast.makeText(mContext,"Sorry, your account has been logged in other device! Please login again to continue.",Toast.LENGTH_SHORT).show()
        mPreferencesHelper.logout()
    }

    override fun onClear() {
        mPreferencesHelper.onClear()
    }

    override fun onCartClear() {
        mPreferencesHelper.onCartClear()
    }

    override fun removeValue(key: String) {
        mPreferencesHelper.removeValue(key)
    }

    override fun getCurrentUserLoggedIn(): Boolean {
        return mPreferencesHelper.getCurrentUserLoggedIn()
    }

    override fun getLangCode(): String {
        return mPreferencesHelper.getLangCode()
    }

    override fun isBranchFlow(): Boolean {
        return mPreferencesHelper.isBranchFlow()
    }

    override fun isSubcriptionEnded(): Boolean {
        return mPreferencesHelper.isSubcriptionEnded()
    }

    override fun getCurrentTableData(): GlobalTableDataHolder? {
        return mPreferencesHelper.getCurrentTableData()
    }

    override fun signup_step_first(params: RegisterParamModelV2?): Observable<PojoSignUp?>? {
        return mApiHelper.signup_step_first(params)
    }
}