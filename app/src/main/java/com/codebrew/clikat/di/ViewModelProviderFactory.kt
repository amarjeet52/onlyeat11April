package com.codebrew.clikat.di

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.codebrew.clikat.data.DataManager
import com.codebrew.clikat.modal.crave_mania.CraveManiaViewModel
import com.codebrew.clikat.module.agent_listing.AgentListViewModel
import com.codebrew.clikat.module.agent_time_slot.AgentViewModel
import com.codebrew.clikat.module.all_categories.CategoryViewModel
import com.codebrew.clikat.module.all_offers.OfferProdListViewModel
import com.codebrew.clikat.module.banners.BannersListViewModel
import com.codebrew.clikat.module.base_orders.BaseOrderViewModel
import com.codebrew.clikat.module.bottom_navigation.MainScreenViewModel
import com.codebrew.clikat.module.cart.CartViewModel
import com.codebrew.clikat.module.cart.promocode.PromoCodeViewModel
import com.codebrew.clikat.module.cart.schedule_order.ScheduleOrderViewModel
import com.codebrew.clikat.module.cart.tables.TablesViewModel
import com.codebrew.clikat.module.crave_mania.CraveManiaViewModelNew
import com.codebrew.clikat.module.custom_home.CustomHomeViewModel
import com.codebrew.clikat.module.dialog_adress.AddressViewModel
import com.codebrew.clikat.module.essentialHome.ServiceViewModel
import com.codebrew.clikat.module.feedback.FeedbackViewModel
import com.codebrew.clikat.module.filter.FilterViewModel
import com.codebrew.clikat.module.home_screen.HomeViewModel
import com.codebrew.clikat.module.instruction_page.InstructionViewModel
import com.codebrew.clikat.module.login.LoginViewModel
import com.codebrew.clikat.module.loyaltyPoints.LoyaltyPointsViewModel
import com.codebrew.clikat.module.more_setting.MoreSettingViewModel
import com.codebrew.clikat.module.new_signup.SigninViewModel
import com.codebrew.clikat.module.new_signup.enter_phone.EnterPhoneViewModel
import com.codebrew.clikat.module.new_signup.otp_verify.OtpVerifyViewModel
import com.codebrew.clikat.module.new_signup.signup.RegisterViewModel
import com.codebrew.clikat.module.new_signup.signup.v2.RegisterViewModelV2
import com.codebrew.clikat.module.notification.NotificationViewModel
import com.codebrew.clikat.module.order_detail.OrderDetailViewModel
import com.codebrew.clikat.module.order_detail.rate_product.RateViewModel
import com.codebrew.clikat.module.payment_gateway.PaymentListViewModel
import com.codebrew.clikat.module.payment_gateway.dialog_card.CardViewModel
import com.codebrew.clikat.module.payment_gateway.savedcards.SavedCardsViewModel
import com.codebrew.clikat.module.product.product_listing.ProductTabViewModel
import com.codebrew.clikat.module.product_detail.ProdDetailViewModel
import com.codebrew.clikat.module.questions.main.QuestionsViewModel
import com.codebrew.clikat.module.recording.RecordingViewModel
import com.codebrew.clikat.module.referral_list.ReferralListViewModel
import com.codebrew.clikat.module.rental.HomeRentalViewModel
import com.codebrew.clikat.module.rental.carDetail.CarDetailViewModel
import com.codebrew.clikat.module.rental.carList.ProductListViewModel
import com.codebrew.clikat.module.rental.rental_checkout.CheckoutViewModel
import com.codebrew.clikat.module.requestsLists.RequestsViewModel
import com.codebrew.clikat.module.restaurant_detail.CraveSignatureDetailViewModel
import com.codebrew.clikat.module.restaurant_detail.RestDetailViewModel
import com.codebrew.clikat.module.searchProduct.SearchViewModel
import com.codebrew.clikat.module.selectAgent.SelectAgentViewModel
import com.codebrew.clikat.module.service_selection.SerSelectionViewModel
import com.codebrew.clikat.module.setting.SettingViewModel
import com.codebrew.clikat.module.signature.SignatureViewModel
import com.codebrew.clikat.module.social_post.SocialPostViewModel
import com.codebrew.clikat.module.splash.SplashViewModel
import com.codebrew.clikat.module.subcategory.SubCategoryViewModel
import com.codebrew.clikat.module.subscription.SubscriptionViewModel
import com.codebrew.clikat.module.supplier_all.SupplierListViewModel
import com.codebrew.clikat.module.supplier_detail.SupplierDetailViewModel
import com.codebrew.clikat.module.tables.TableBookingsViewModel
import com.codebrew.clikat.module.wallet.WalletViewModel
import com.codebrew.clikat.module.webview.WebViewModel
import com.codebrew.clikat.module.wishlist_prod.WishListViewModel
import com.codebrew.clikat.user_chat.UserChatViewModel
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class ViewModelProviderFactory @Inject
constructor(private val dataManager: DataManager) : ViewModelProvider.NewInstanceFactory() {


    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        when {
            modelClass.isAssignableFrom(WishListViewModel::class.java) -> return WishListViewModel(dataManager) as T

            modelClass.isAssignableFrom(CraveSignatureDetailViewModel::class.java) -> return CraveSignatureDetailViewModel(dataManager) as T

            modelClass.isAssignableFrom(CraveManiaViewModel::class.java) -> return CraveManiaViewModel(dataManager) as T

            modelClass.isAssignableFrom(MainScreenViewModel::class.java) -> return MainScreenViewModel(dataManager) as T

            modelClass.isAssignableFrom(SupplierDetailViewModel::class.java) -> return SupplierDetailViewModel(dataManager) as T

            modelClass.isAssignableFrom(SplashViewModel::class.java) -> return SplashViewModel(dataManager) as T

            modelClass.isAssignableFrom(HomeViewModel::class.java) -> return HomeViewModel(dataManager) as T

            modelClass.isAssignableFrom(ProductTabViewModel::class.java) -> return ProductTabViewModel(dataManager) as T

            modelClass.isAssignableFrom(AgentViewModel::class.java) -> return AgentViewModel(dataManager) as T

            modelClass.isAssignableFrom(AddressViewModel::class.java) -> return AddressViewModel(dataManager) as T

            modelClass.isAssignableFrom(RestDetailViewModel::class.java) -> return RestDetailViewModel(dataManager) as T

            modelClass.isAssignableFrom(HomeRentalViewModel::class.java) -> return HomeRentalViewModel(dataManager) as T

            modelClass.isAssignableFrom(ProductListViewModel::class.java) -> return ProductListViewModel(dataManager) as T

            modelClass.isAssignableFrom(CarDetailViewModel::class.java) -> return CarDetailViewModel(dataManager) as T

            modelClass.isAssignableFrom(CheckoutViewModel::class.java) -> return CheckoutViewModel(dataManager) as T

            modelClass.isAssignableFrom(SubCategoryViewModel::class.java) -> return SubCategoryViewModel(dataManager) as T

             modelClass.isAssignableFrom(CartViewModel::class.java) -> return CartViewModel(dataManager) as T

            modelClass.isAssignableFrom(SearchViewModel::class.java) -> return SearchViewModel(dataManager) as T

            modelClass.isAssignableFrom(OrderDetailViewModel::class.java) -> return OrderDetailViewModel(dataManager) as T

            modelClass.isAssignableFrom(SettingViewModel::class.java) -> return SettingViewModel(dataManager) as T

            modelClass.isAssignableFrom(LoginViewModel::class.java) -> return LoginViewModel(dataManager) as T

            modelClass.isAssignableFrom(OfferProdListViewModel::class.java) -> return OfferProdListViewModel(dataManager) as T

            modelClass.isAssignableFrom(BaseOrderViewModel::class.java) -> return BaseOrderViewModel(dataManager) as T

            modelClass.isAssignableFrom(CategoryViewModel::class.java) -> return CategoryViewModel(dataManager) as T

            modelClass.isAssignableFrom(ProdDetailViewModel::class.java) -> return ProdDetailViewModel(dataManager) as T

            modelClass.isAssignableFrom(FilterViewModel::class.java) -> return FilterViewModel(dataManager) as T

            modelClass.isAssignableFrom(SerSelectionViewModel::class.java) -> return SerSelectionViewModel(dataManager) as T

            modelClass.isAssignableFrom(AgentListViewModel::class.java) -> return AgentListViewModel(dataManager) as T

            modelClass.isAssignableFrom(SelectAgentViewModel::class.java) -> return SelectAgentViewModel(dataManager) as T

            modelClass.isAssignableFrom(WebViewModel::class.java) -> return WebViewModel(dataManager) as T

            modelClass.isAssignableFrom(CardViewModel::class.java) -> return CardViewModel(dataManager) as T

            modelClass.isAssignableFrom(UserChatViewModel::class.java) -> return UserChatViewModel(dataManager) as T

            modelClass.isAssignableFrom(ReferralListViewModel::class.java) -> return ReferralListViewModel(dataManager) as T

            modelClass.isAssignableFrom(PaymentListViewModel::class.java) -> return PaymentListViewModel(dataManager) as T

            modelClass.isAssignableFrom(QuestionsViewModel::class.java) -> return QuestionsViewModel(dataManager) as T

            modelClass.isAssignableFrom(CustomHomeViewModel::class.java) -> return CustomHomeViewModel(dataManager) as T

            modelClass.isAssignableFrom(SigninViewModel::class.java) -> return SigninViewModel(dataManager) as T

            modelClass.isAssignableFrom(RegisterViewModel::class.java) -> return RegisterViewModel(dataManager) as T

            modelClass.isAssignableFrom(OtpVerifyViewModel::class.java) -> return OtpVerifyViewModel(dataManager) as T

            modelClass.isAssignableFrom(EnterPhoneViewModel::class.java) -> return EnterPhoneViewModel(dataManager) as T

            modelClass.isAssignableFrom(SavedCardsViewModel::class.java) -> return SavedCardsViewModel(dataManager) as T

            modelClass.isAssignableFrom(NotificationViewModel::class.java) -> return NotificationViewModel(dataManager) as T

            modelClass.isAssignableFrom(RequestsViewModel::class.java)->return  RequestsViewModel(dataManager) as T

            modelClass.isAssignableFrom(RateViewModel::class.java)->return  RateViewModel(dataManager) as T

            modelClass.isAssignableFrom(SupplierListViewModel::class.java)->return  SupplierListViewModel(dataManager) as T

            modelClass.isAssignableFrom(WalletViewModel::class.java)->return  WalletViewModel(dataManager) as T

            modelClass.isAssignableFrom(InstructionViewModel::class.java)->return  InstructionViewModel(dataManager) as T

            modelClass.isAssignableFrom(RegisterViewModelV2::class.java)->return  RegisterViewModelV2(dataManager) as T

            modelClass.isAssignableFrom(ScheduleOrderViewModel::class.java)->return  ScheduleOrderViewModel(dataManager) as T

            modelClass.isAssignableFrom(FeedbackViewModel::class.java)->return  FeedbackViewModel(dataManager) as T

            modelClass.isAssignableFrom(SubscriptionViewModel::class.java)->return  SubscriptionViewModel(dataManager) as T

            modelClass.isAssignableFrom(ServiceViewModel::class.java)->return  ServiceViewModel(dataManager) as T

            modelClass.isAssignableFrom(LoyaltyPointsViewModel::class.java)->return  LoyaltyPointsViewModel(dataManager) as T

            modelClass.isAssignableFrom(TablesViewModel::class.java)->return  TablesViewModel(dataManager) as T

            modelClass.isAssignableFrom(TableBookingsViewModel::class.java)->return  TableBookingsViewModel(dataManager) as T

            modelClass.isAssignableFrom(SocialPostViewModel::class.java)->return  SocialPostViewModel(dataManager) as T

            modelClass.isAssignableFrom(MoreSettingViewModel::class.java)->return  MoreSettingViewModel(dataManager) as T
            modelClass.isAssignableFrom(PromoCodeViewModel::class.java)->return  PromoCodeViewModel(dataManager) as T

            modelClass.isAssignableFrom(CraveManiaViewModelNew::class.java)->return  CraveManiaViewModelNew(dataManager) as T

            modelClass.isAssignableFrom(RecordingViewModel::class.java)->return  RecordingViewModel(dataManager) as T

            modelClass.isAssignableFrom(BannersListViewModel::class.java)->return  BannersListViewModel(dataManager) as T
            modelClass.isAssignableFrom(SignatureViewModel::class.java)->return  SignatureViewModel(dataManager) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: " + modelClass.name)
    }
}


