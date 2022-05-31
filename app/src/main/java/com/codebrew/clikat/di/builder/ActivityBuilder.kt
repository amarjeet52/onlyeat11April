package com.codebrew.clikat.di.builder


import com.codebrew.clikat.dialog_flow.DialogChat
import com.codebrew.clikat.modal.crave_mania.CraveManiaFragment
import com.codebrew.clikat.modal.crave_mania.CraveManiaProvider
import com.codebrew.clikat.module.addon_quant.SavedAddonProvider
import com.codebrew.clikat.module.agent_listing.AgentListProvider
import com.codebrew.clikat.module.agent_time_slot.AgentSlotProvider
import com.codebrew.clikat.module.all_categories.CategoryProvider
import com.codebrew.clikat.module.all_offers.OfferProdProvider
import com.codebrew.clikat.module.banners.BannersProvider
import com.codebrew.clikat.module.base_orders.BaseOrderProvider
import com.codebrew.clikat.module.bottom_navigation.MainScreenActivity
import com.codebrew.clikat.module.cart.AddVehicleActivity
import com.codebrew.clikat.module.cart.CartProvider
import com.codebrew.clikat.module.cart.SelectSlots
import com.codebrew.clikat.module.cart.VehicleListingActivity
import com.codebrew.clikat.module.cart.promocode.PromoCodeListActivity
import com.codebrew.clikat.module.cart.schedule_order.*
import com.codebrew.clikat.module.cart.tables.TableFragmentProvider
import com.codebrew.clikat.module.change_language.ChangeLangProvider
import com.codebrew.clikat.module.crave_mania.CraveManiaProviderr
import com.codebrew.clikat.module.crave_mania.FragmentCraveMania
import com.codebrew.clikat.module.crave_mania.FragmentCraveManiaAll1
import com.codebrew.clikat.module.custom_home.CusHomeProvider
import com.codebrew.clikat.module.dialog_adress.AddressDialogActivity
import com.codebrew.clikat.module.dialog_adress.AddressDialogProvider
import com.codebrew.clikat.module.dialog_adress.SelectlocActivity
import com.codebrew.clikat.module.dialog_adress.newlocActivity
import com.codebrew.clikat.module.dialog_adress.v2.SelectlocActivityV2
import com.codebrew.clikat.module.essentialHome.ServiceListActivity
import com.codebrew.clikat.module.feedback.FeedbackProvider
import com.codebrew.clikat.module.filter.BottomSheetProvider
import com.codebrew.clikat.module.filter.FilterProvider
import com.codebrew.clikat.module.help.HelpActivity
import com.codebrew.clikat.module.home_screen.HomeProvider
import com.codebrew.clikat.module.home_screen.resturant_home.ResHomeProvider
import com.codebrew.clikat.module.home_screen.resturant_home.pickup.PickUpProvider
import com.codebrew.clikat.module.home_screen.suppliers.SuppliersMapFragment
import com.codebrew.clikat.module.instruction_page.InstructionActivity
import com.codebrew.clikat.module.jumaHome.JumaHomeProvider
import com.codebrew.clikat.module.location.LocationActivity
import com.codebrew.clikat.module.login.LoginActivity
import com.codebrew.clikat.module.login.LoginActivityNew
import com.codebrew.clikat.module.loyaltyPoints.LoyaltyPointsProvider
import com.codebrew.clikat.module.main_screen.MainProvider
import com.codebrew.clikat.module.manage_order.ManageOrderProvider
import com.codebrew.clikat.module.more_setting.MoreProvider
import com.codebrew.clikat.module.new_signup.SigninActivity
import com.codebrew.clikat.module.new_signup.create_account.CreateAccActivity
import com.codebrew.clikat.module.new_signup.create_account.CreateAccProvider
import com.codebrew.clikat.module.new_signup.enter_phone.EnterPhoneProvider
import com.codebrew.clikat.module.new_signup.login.LoginProvider
import com.codebrew.clikat.module.new_signup.otp_verify.OtpProvider
import com.codebrew.clikat.module.new_signup.signup.RegisterProvider
import com.codebrew.clikat.module.new_signup.signup.SignInActivityNew
import com.codebrew.clikat.module.notification.NotificationListProvider
import com.codebrew.clikat.module.order_detail.OrderDetailActivity
import com.codebrew.clikat.module.order_detail.rate_product.RateProductActivity
import com.codebrew.clikat.module.order_detail_new.OrderDetailNew
import com.codebrew.clikat.module.order_detail_new.OrderStatusProvider
import com.codebrew.clikat.module.payment_gateway.PaymentListActivity
import com.codebrew.clikat.module.payment_gateway.PaymentWebViewActivity
import com.codebrew.clikat.module.payment_gateway.addCard.AddNewCard
import com.codebrew.clikat.module.payment_gateway.dialog_card.CardDialogProvider
import com.codebrew.clikat.module.payment_gateway.savedcards.SaveCardsActivity
import com.codebrew.clikat.module.pre_delivery.PreBookMenuProvider
import com.codebrew.clikat.module.product.ProdListProvider
import com.codebrew.clikat.module.product_addon.AddonProvider
import com.codebrew.clikat.module.product_detail.ProductDetailProvider
import com.codebrew.clikat.module.questions.QuestionFragProvider
import com.codebrew.clikat.module.rating.RatingFragment
import com.codebrew.clikat.module.refer_user.ReferralUserProvider
import com.codebrew.clikat.module.referral_list.ReferralProvider
import com.codebrew.clikat.module.rental.HomeRentalProvider
import com.codebrew.clikat.module.rental.carDetail.CarDetailProvider
import com.codebrew.clikat.module.rental.carList.ProductListProvider
import com.codebrew.clikat.module.rental.rental_checkout.CheckoutProvider
import com.codebrew.clikat.module.requestsLists.RequestsListProvider
import com.codebrew.clikat.module.restaurant_detail.RestDetailNewProvider
import com.codebrew.clikat.module.restaurant_detail.RestDetailProvider
import com.codebrew.clikat.module.restaurant_detail.reviewRating.ReviewsListActivity
import com.codebrew.clikat.module.roadmap.RoadMapActivity
import com.codebrew.clikat.module.roadmap.RoadMapProvider
import com.codebrew.clikat.module.searchProduct.SearchProvider
import com.codebrew.clikat.module.selectAgent.SelectAgentProvider
import com.codebrew.clikat.module.service_selection.ServSelectionActivity
import com.codebrew.clikat.module.setting.SettingProvider
import com.codebrew.clikat.module.signature.SignatureDetailProvider
import com.codebrew.clikat.module.signature.SignatureHomeProvider
import com.codebrew.clikat.module.signup.SignUpProvider
import com.codebrew.clikat.module.signup.SignupActivity
import com.codebrew.clikat.module.social_post.PostsModuleProvider
import com.codebrew.clikat.module.splash.SplashActivity
import com.codebrew.clikat.module.subcategory.SubCategoryProvider
import com.codebrew.clikat.module.subscription.SubscriptionProvider
import com.codebrew.clikat.module.subscription.subscrip_detail.SubscripDetailProvider
import com.codebrew.clikat.module.supplier_all.SupplierProvider
import com.codebrew.clikat.module.supplier_detail.SupplierDetailProvider
import com.codebrew.clikat.module.tables.BookedTableListProvider
import com.codebrew.clikat.module.user_tracking.OrderDetailProvider
import com.codebrew.clikat.module.user_tracking.UserTracking
import com.codebrew.clikat.module.wallet.WalletProvider
import com.codebrew.clikat.module.webview.WebViewActivity
import com.codebrew.clikat.module.webview.WebViewProvider
import com.codebrew.clikat.module.wishlist_prod.WishListProvider
import com.codebrew.clikat.services.MyFirebaseMessagingService
import com.codebrew.clikat.user_chat.UserChatActivity
import dagger.Module
import dagger.android.ContributesAndroidInjector


@Module
abstract class ActivityBuilder {


    @ContributesAndroidInjector(modules = [SupplierDetailProvider::class, HomeProvider::class,/*BookTableDetailProvider::class,*/
        SupplierProvider::class, SubCategoryProvider::class, OfferProdProvider::class,
        ProductDetailProvider::class, FilterProvider::class, SearchProvider::class,
        PreBookMenuProvider::class,        ProdListProvider::class, BottomSheetProvider::class,
        RestDetailProvider::class, RestDetailNewProvider::class, AddressDialogProvider::class,
        NotificationListProvider::class, RequestsListProvider::class,

        MoreProvider::class, CartProvider::class, MainProvider::class, PickUpProvider::class,
        ResHomeProvider::class, SavedAddonProvider::class, AddonProvider::class, HomeRentalProvider::class
        , ProductListProvider::class, CarDetailProvider::class, CheckoutProvider::class, SettingProvider::class
        , BaseOrderProvider::class, CategoryProvider::class, WishListProvider::class, CardDialogProvider::class,
        ReferralUserProvider::class, WalletProvider::class, QuestionFragProvider::class, ReferralProvider::class,
        FeedbackProvider::class, LoyaltyPointsProvider::class,
        ChangeLangProvider::class, CusHomeProvider::class, ManageOrderProvider::class, WebViewProvider::class, JumaHomeProvider::class,
        SubscriptionProvider::class, SubscripDetailProvider::class, TableFragmentProvider::class, BannersProvider::class,
        BookedTableListProvider::class, PostsModuleProvider::class,SignatureHomeProvider::class, SignatureDetailProvider::class, CraveManiaProvider::class])

    internal abstract fun bindHomeActivity(): MainScreenActivity

    @ContributesAndroidInjector
    internal abstract fun bindFragmentCraveMania(): FragmentCraveMania

    @ContributesAndroidInjector
    internal abstract fun bindFragmentCraveManiaAll(): FragmentCraveManiaAll1


    @ContributesAndroidInjector
    internal abstract fun bindSplashActivity(): SplashActivity

    @ContributesAndroidInjector
    internal abstract fun bindVehicleListActivity(): VehicleListingActivity


    @ContributesAndroidInjector
    internal abstract fun bindSelectSlots(): SelectSlots

    @ContributesAndroidInjector
    internal abstract fun bindRatingActivity(): RatingFragment

    @ContributesAndroidInjector
    internal abstract fun bindCraveScheduleActivity(): CraveScheduleActivity

    @ContributesAndroidInjector(modules = [TableSelectProvider::class])
    internal abstract fun bindTableSelectActivity(): TableSelectActivity

    @ContributesAndroidInjector
    internal abstract fun bindBookTableSlotsActivity(): BookTableSlotsActivity


    @ContributesAndroidInjector
    internal abstract fun bindCreateAccActivity(): CreateAccActivity

    @ContributesAndroidInjector
    internal abstract fun bindSuppliersActivity(): SuppliersMapFragment

    @ContributesAndroidInjector
    internal abstract fun bindRateActivity(): RateProductActivity

    @ContributesAndroidInjector(modules = [CardDialogProvider::class, RestDetailProvider::class, RestDetailNewProvider::class, MainProvider::class])
    internal abstract fun bindOrderDetailActivity(): OrderDetailActivity

    @ContributesAndroidInjector
    internal abstract fun bindVehicleActivity(): AddVehicleActivity

    @ContributesAndroidInjector
    internal abstract fun bindLocationActivity(): SelectlocActivity

    @ContributesAndroidInjector
    internal abstract fun bindnewLocationActivity(): newlocActivity
    @ContributesAndroidInjector
    internal abstract fun bindaddressLocationActivity(): AddressDialogActivity

    @ContributesAndroidInjector
    internal abstract fun bindRoadMapActivity(): RoadMapActivity



    @ContributesAndroidInjector
    internal abstract fun bindLocationActivityV2(): SelectlocActivityV2

    @ContributesAndroidInjector(modules = [OrderDetailProvider::class])
    internal abstract fun bindUserTrackActivity(): UserTracking

    @ContributesAndroidInjector
    internal abstract fun bindhelpActivity(): HelpActivity

    @ContributesAndroidInjector
    internal abstract fun bindLoginActivity(): LoginActivity

    @ContributesAndroidInjector
    internal abstract fun bindLoginActivityNew(): LoginActivityNew

    @ContributesAndroidInjector(modules = [CreateAccProvider::class, LoginProvider::class, RegisterProvider::class,
        OtpProvider::class, EnterPhoneProvider::class])
    internal abstract fun bindSignInActivityNew(): SignInActivityNew


    @ContributesAndroidInjector(modules = [SignUpProvider::class])
    internal abstract fun bindSignupActivity(): SignupActivity


    @ContributesAndroidInjector(modules = [AddressDialogProvider::class])
    internal abstract fun bindLocActivity(): LocationActivity


    @ContributesAndroidInjector
    internal abstract fun contributeMyFirebaseMessagingService(): MyFirebaseMessagingService?

    @ContributesAndroidInjector
    internal abstract fun bindWebActivity(): WebViewActivity

    @ContributesAndroidInjector
    internal abstract fun bindWebPaymentActivity(): PaymentWebViewActivity

    @ContributesAndroidInjector
    internal abstract fun bindInstructActivity(): InstructionActivity


    @ContributesAndroidInjector(modules = [CardDialogProvider::class])
    internal abstract fun bindPaymentActivity(): PaymentListActivity

    @ContributesAndroidInjector
    internal abstract fun bindUserChatActivity(): UserChatActivity


    @ContributesAndroidInjector(modules = [AgentListProvider::class, AgentSlotProvider::class, SelectAgentProvider::class])
    internal abstract fun bindSerSelcActivity(): ServSelectionActivity


    @ContributesAndroidInjector(modules = [CreateAccProvider::class, LoginProvider::class, RegisterProvider::class,
        OtpProvider::class, EnterPhoneProvider::class])
    internal abstract fun bindSignInActivity(): SigninActivity


    @ContributesAndroidInjector(modules = [CardDialogProvider::class])
    internal abstract fun bindSaveCardActivity(): SaveCardsActivity

    @ContributesAndroidInjector
    internal abstract fun bindScheduleOrderActivity(): ScheduleOrder


/*
    @ContributesAndroidInjector(modules = [CardDialogProvider::class])
    internal abstract fun bindWalletAddMoney(): WalletAddMoneyActivity
*/


    @ContributesAndroidInjector(modules = [AddressDialogProvider::class])
    internal abstract fun bindServListActivity(): ServiceListActivity

    @ContributesAndroidInjector
    internal abstract fun addCardActivity(): AddNewCard

    @ContributesAndroidInjector
    internal abstract fun promoCodeActivity(): PromoCodeListActivity

    @ContributesAndroidInjector
    internal abstract fun reviewsActivity(): ReviewsListActivity

    @ContributesAndroidInjector(modules = [SavedAddonProvider::class, AddonProvider::class, AddressDialogProvider::class])
    internal abstract fun dialogChat(): DialogChat

    @ContributesAndroidInjector(modules = [OrderStatusProvider::class])
    internal abstract fun orderDetailNewActivity(): OrderDetailNew

}





