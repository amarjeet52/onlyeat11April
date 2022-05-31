package com.codebrew.clikat.retrofit;

import com.codebrew.clikat.data.model.others.GoogleLoginInput;
import com.codebrew.clikat.modal.ExampleCommon;
import com.codebrew.clikat.modal.PojoPendingOrders;
import com.codebrew.clikat.modal.PojoSignUp;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.PartMap;


public interface Api {
/*    *//*1*//*
    @GET("/get_all_category_new")
    Call<ExampleAllCategories> getAllCategory(@QueryMap HashMap<String, String> hashMap);


    @FormUrlEncoded
    @POST("/package_product")
    Call<PackageProductListModel> getAllPackages
    (@FieldMap HashMap<String, String> params);*/

    /*10*/
    @FormUrlEncoded
    @POST("/add_to_favourite")
    Call<ExampleCommon> favourite
    (@FieldMap HashMap<String, String> params);

    //productId //languageId
     /*11*/
    @FormUrlEncoded
    @POST("/facebook_login")
    Call<PojoSignUp> fbLogin(@FieldMap HashMap<String, String> hashMap);

    /*12*/
    @FormUrlEncoded
    @POST("/customer_register_step_first")
    Call<PojoSignUp> signup_step_first(@FieldMap HashMap<String,String> hashMap);

    /*13*/
    @FormUrlEncoded
    @POST("/customer_register_step_second")
    Call<PojoSignUp> sognup_phone_2(@FieldMap HashMap<String,String> hashMap);

    /*14*/
    @FormUrlEncoded
    @POST("/check_otp")
    Call<PojoSignUp> verify_otp(@Field("accessToken") String accesstoken,
                                @Field("otp") String otp,
                                @Field("languageId") int languageId);


    @FormUrlEncoded
    @POST("/user/contact_us")
    Call<PojoSignUp> apiContactUs(@FieldMap HashMap<String,String> hashMap);
    /*15*/


    /*16*/


/*
    */
/*17*//*

    @FormUrlEncoded
    @POST("/schedule_orders")
    Call<ScheduleListModel> scheduledOrder(@FieldMap HashMap<String, String> hashMap);

    */
/*18*//*

    @FormUrlEncoded
    @POST("/track_order_list")
    Call<TrackOrderList> trackOrderList(@FieldMap HashMap<String, String> hashMap);
*/


    @Multipart
    @POST("/customer_register_step_third")
    Call<PojoSignUp> signUpFinish( @PartMap Map<String, RequestBody> hashMap);

    @Multipart
    @POST("/user/upload/document")
    Call<PojoSignUp> uploadDocuments(@Part ArrayList<MultipartBody.Part> body);

 /*   *//*21*//*
    @POST("/add_to_cart")
    Call<AddtoCartModel> getAddToCart1(@Body CartInfoServerArray cartInfoServerArray);

    *//*22*//*
    @FormUrlEncoded
    @POST("/update_cart_info")
    Call<ExampleCommon> updateCartInfo
    (@FieldMap HashMap<String, String> params);

    *//*23*//*
    @FormUrlEncoded
    @POST("/package_category")
    Call<ExamplePackagesSupplier> packageCategory
    (@FieldMap HashMap<String, String> params);

    *//*24*//*
    @POST("/genrate_order")
    Call<PlaceOrderModel> generateOrder
    (@Body PlaceOrderInput params);

    *//*25*//*
    @FormUrlEncoded
    @POST("/add_new_address")
    Call<ExampleCommon> addAddress(@FieldMap HashMap<String, String> hashMap,
                                   @Field("areaId") int areaId);

    *//*26*//*
    @FormUrlEncoded
    @POST("/get_all_customer_address")
    Call<CustomerAddressModel> getAllAddress(@FieldMap HashMap<String, String> params);

    *//*27*//*
    @FormUrlEncoded
    @POST("/edit_address")
    Call<PojoSuccess1> updateAddress(@FieldMap HashMap<String, String> hashMap,
                                     @Field("areaId") int areaId, @Field("addressId") int addressId);

    *//*28*//*
    @FormUrlEncoded
    @POST("/multi_search")
    Call<PojoMultiSearch> multisearch(@FieldMap HashMap<String, String> hashMap,
                                      @Field("languageId") int languageId);
*/
    /*29*/
    @FormUrlEncoded
    @POST("/resend_otp")
    Call<ExampleCommon> resendotp(@Field("accessToken") String accesstoken);

    /*30*/
    @FormUrlEncoded
    @POST("/change_password")
    Call<ExampleCommon> changePassword(@FieldMap HashMap<String, String> hashMap);

    /*31*/


    /*32*/


    /*33*/

/*

    */
/*34*//*

    @FormUrlEncoded
    @POST("/get_loyality_product")
    Call<ExampleLoyalityPoints> apiLoyalityPoints(@FieldMap HashMap<String, String> hashMap);

    */
/*35*//*

    @FormUrlEncoded
    @POST("/get_promoation_product")
    Call<PromotionListModel> apiGetPromotion(@FieldMap HashMap<String, String> hashMap);

    */
/*36*//*



    */
/*37*//*

    @FormUrlEncoded
    @POST("/get_my_favourite")
    Call<FavouriteListModel> favList(@FieldMap HashMap<String, String> hashMap);

    */
/*38*//*

    @FormUrlEncoded
    @POST("/rate_my_order_list")
    Call<RateMyProductModel> rateMyOrder(@FieldMap HashMap<String, String> hashMap);

    */
/*39*//*

    @FormUrlEncoded
    @POST("/bar_code")
    Call<PojoBarcode> barCode(@FieldMap HashMap<String, String> hashMap);

    */
/*40*//*



    */
/*41*//*

    @FormUrlEncoded
    @POST("/order_track")
    Call<ExampleCommon> trackOrder(@FieldMap HashMap<String, String> hashMap);
*/

    /*42*/
    @FormUrlEncoded
    @POST("/supplier_rating")
    Call<ExampleCommon> supplierRating(@FieldMap HashMap<String, String> hashMap);

/*    *//*43*//*
    @FormUrlEncoded
    @POST("/user_rate_order")
    Call<ExampleCommon> orderRating(@FieldMap HashMap<String, String> hashMap);

    *//*44*//*
    @POST("/schedule_order_new")
    Call<ScheduleOrderModel> orderSchedule(@Body SchedulerSent schedulerSent);*/

/*
    @FormUrlEncoded
    @POST("/get_laundry_product")
    Call<PojoLaundry> getLaundryProducts(@FieldMap HashMap<String, String> hashMap);*/

/*
    */
/*46*//*

    @FormUrlEncoded
    @POST("/laundary_supplier_list")
    Call<ExampleAllSupplier> laundrySupplier(@FieldMap HashMap<String, String> hashMap);

    */
/*47*//*

    @FormUrlEncoded
    @POST("/un_favourite")
    Call<ExampleCommon> unfavSupplier(@FieldMap HashMap<String, String> hashMap);

    */
/*48*//*

    @FormUrlEncoded
    @POST("/confirm_order")
    Call<PlaceOrderModel> confirmOrder(@FieldMap HashMap<String, String> hashMap);

    */
/*49*//*

    @POST("/loyality_order")
    Call<ExampleCommon> loyalityOrder(@Body LoyalityCartServer loyalityPoints);

    */
/*50*//*

    @FormUrlEncoded
    @POST("/get_all_notification")
    Call<PojoNotification> callNotification(@FieldMap HashMap<String, String> hashMap);

    */
/*51*//*

    @FormUrlEncoded
    @POST("/clear_notification")
    Call<ExampleCommon> clearOneNotification(@FieldMap HashMap<String, String> hashMap);

    */
/*52*//*

    @FormUrlEncoded
    @POST("/clear_all_notification")
    Call<ExampleCommon> clearAllNotification(@FieldMap HashMap<String, String> hashMap);

    */
/*53*//*



    */
/*54*//*

    @FormUrlEncoded
    @POST("/delete_customer_address")
    Call<ExampleCommon> deleteAddress(@FieldMap HashMap<String, String> hashMap);

    */
/*57*//*

    @FormUrlEncoded
    @POST("/product_acco_to_area")
    Call<SearchProductModel> compareProducts(@FieldMap HashMap<String, String> hashMap);

    */
/*58*//*

    @FormUrlEncoded
    @POST("/compare_product")
    Call<PojoCompareResultSupplier> compareResults(@FieldMap HashMap<String, String> hashMap);
*/

/*    *//*59*//*
    @Headers({
            "Content-Type: application/json"
    })
    @POST("https://paymentservices.payfort.com/FortAPI/paymentApi")
    Call<PayFortSdkToken> paymentSdk(@Body SdkTokenRequest tokenRequest);*/

    /*62*/
    @FormUrlEncoded
    @POST("/get_total_pending_schedule")
    Call<PojoPendingOrders> getCount(@FieldMap HashMap<String, String> hashMap);

/*

    */
/*65*//*

    @FormUrlEncoded
    @POST("/check_supplier")
    Call<PojoSuccess1> checkSupplier(@Field("supplierId") String supplierId, @Field("areaId") String areaId, @Field("langId") int langId);

    */
/*66*//*

    @FormUrlEncoded
    @POST("/check_branch")
    Call<PojoSuccess1> checkBranch(@Field("supplierBranchId") String supplierId, @Field("areaId") String areaId, @Field("langId") int langId);

    */
/*69*//*

    @GET("/get_all_offer_list")
    Call<OfferListModel> getOfferList(@QueryMap HashMap<String, String> hashMap);

    */
/*70*//*

    @FormUrlEncoded
    @POST("/getSettings")
    Call<SettingModel> getSetting(@Field("val") String supplierId);
*/


/*    @FormUrlEncoded
    @POST("")
    Call<SubCategoryListModel> getSubCategory
            (@FieldMap HashMap<String, String> params);*/

    /*73*/


    /*4*/


//
//    /*69*/
//    @GET("/agent/slots")
//    Call<AgentSlotsModel> getAgentSlots(@QueryMap HashMap<String, String> hashMap);
//
//    /*70*/
//    @GET("/home/supplier_list")
//    Call<HomeSupplierModel> getSupplierList(@QueryMap HashMap<String, String> hashMap);
//
//    /*80*/
//
//    @FormUrlEncoded
//    @POST("/product_mark_fav_unfav")
//    Call<ExampleCommon> markWishList(@FieldMap HashMap<String, String> params);


    @POST("/google_login")
    Call<PojoSignUp> googleLogin(@Body GoogleLoginInput param);


}