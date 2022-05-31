//package com.codebrew.clikat.module.cart.v2
//
//import android.app.Activity
//import android.app.AlertDialog
//import android.app.Dialog
//import android.content.Intent
//import android.graphics.BlendMode
//import android.graphics.BlendModeColorFilter
//import android.graphics.Color
//import android.net.Uri
//import android.os.Bundle
//import android.os.CountDownTimer
//import android.provider.MediaStore
//import android.view.LayoutInflater
//import android.view.View
//import android.view.Window
//import android.view.WindowManager
//import android.widget.EditText
//import android.widget.ImageView
//import android.widget.TextView
//import androidx.appcompat.app.AppCompatActivity
//import androidx.core.content.ContextCompat
//import androidx.core.content.FileProvider
//import androidx.databinding.DataBindingUtil
//import androidx.lifecycle.Observer
//import androidx.lifecycle.ViewModelProviders
//import androidx.navigation.Navigation
//import androidx.recyclerview.widget.ItemTouchHelper
//import androidx.recyclerview.widget.LinearLayoutManager
//import androidx.recyclerview.widget.RecyclerView
//import com.braintreepayments.api.BraintreeFragment
//import com.braintreepayments.api.dropin.DropInActivity
//import com.braintreepayments.api.dropin.DropInRequest
//import com.braintreepayments.api.dropin.DropInResult
//import com.braintreepayments.api.dropin.utils.PaymentMethodType
//import com.braintreepayments.api.exceptions.InvalidArgumentException
//import com.braintreepayments.api.models.*
//import com.braintreepayments.cardform.view.CardForm
//import com.bumptech.glide.Glide
//import com.codebrew.clikat.BR
//import com.codebrew.clikat.BuildConfig
//import com.codebrew.clikat.R
//import com.codebrew.clikat.app_utils.*
//import com.codebrew.clikat.app_utils.extension.afterTextChanged
//import com.codebrew.clikat.app_utils.extension.launchActivity
//import com.codebrew.clikat.app_utils.extension.loadUserImage
//import com.codebrew.clikat.app_utils.extension.onSnackbar
//import com.codebrew.clikat.base.BaseFragment
//import com.codebrew.clikat.data.*
//import com.codebrew.clikat.data.constants.AppConstants
//import com.codebrew.clikat.data.constants.NetworkConstants
//import com.codebrew.clikat.data.constants.PrefenceConstants
//import com.codebrew.clikat.data.model.api.*
//import com.codebrew.clikat.data.model.api.Currency
//import com.codebrew.clikat.data.model.api.Result
//import com.codebrew.clikat.data.model.api.tap_payment.Transaction
//import com.codebrew.clikat.data.model.others.*
//import com.codebrew.clikat.data.preferences.PreferenceHelper
//import com.codebrew.clikat.databinding.DialogBothDeliveryBinding
//import com.codebrew.clikat.databinding.DialogDonateBinding
//import com.codebrew.clikat.databinding.FragmentCartV2Binding
//import com.codebrew.clikat.di.ViewModelProviderFactory
//import com.codebrew.clikat.modal.*
//import com.codebrew.clikat.modal.other.AddtoCartModel
//import com.codebrew.clikat.modal.other.CheckPromoCodeParam
//import com.codebrew.clikat.modal.other.PromoCodeModel
//import com.codebrew.clikat.modal.other.SettingModel
//import com.codebrew.clikat.modal.slots.SupplierSlots
//import com.codebrew.clikat.module.cart.CartNavigator
//import com.codebrew.clikat.module.cart.CartViewModel
//import com.codebrew.clikat.module.cart.REQUEST_CODE_BRAINTREE
//import com.codebrew.clikat.module.cart.SCHEDULE_REQUEST_DINE_IN
//import com.codebrew.clikat.module.cart.adapter.CartAdapter
//import com.codebrew.clikat.module.cart.adapter.ImageListAdapter
//import com.codebrew.clikat.module.cart.adapter.SelectedQuestAdapter
//import com.codebrew.clikat.module.cart.adapter.TipAdapter
//import com.codebrew.clikat.module.cart.schedule_order.ScheduleOrder
//import com.codebrew.clikat.module.dialog_adress.interfaces.AddressDialogListener
//import com.codebrew.clikat.module.dialog_adress.v2.AddressDialogFragmentV2
//import com.codebrew.clikat.module.login.LoginActivity
//import com.codebrew.clikat.module.new_signup.SigninActivity
//import com.codebrew.clikat.module.payment_gateway.PaymentListActivity
//import com.codebrew.clikat.module.payment_gateway.PaymentWebViewActivity
//import com.codebrew.clikat.module.payment_gateway.dialog_card.CardDialogFrag
//import com.codebrew.clikat.module.product.product_listing.DialogListener
//import com.codebrew.clikat.module.service_selection.ServSelectionActivity
//import com.codebrew.clikat.preferences.DataNames
//import com.codebrew.clikat.utils.ClikatConstants
//import com.codebrew.clikat.utils.DialogIntrface
//import com.codebrew.clikat.utils.StaticFunction
//import com.codebrew.clikat.utils.Utils
//import com.codebrew.clikat.utils.configurations.Configurations
//import com.facebook.FacebookSdk.getApplicationContext
//import com.google.android.gms.maps.model.LatLng
//import com.google.android.gms.wallet.TransactionInfo
//import com.google.android.gms.wallet.WalletConstants
//import com.google.android.material.button.MaterialButton
//import com.google.android.material.textfield.TextInputEditText
//import com.google.gson.Gson
//import com.google.gson.reflect.TypeToken
//import com.codebrew.clikat.intrface.ImageCallback
//import com.codebrew.clikat.app_utils.dialogintrface.ImageDialgFragment
//import com.codebrew.clikat.data.model.api.distance_matrix.DistanceMatrix
//import com.codebrew.clikat.module.user_tracking.UserTracking
////import com.razorpay.Checkout
//import kotlinx.android.synthetic.main.fragment_cart.*
//import kotlinx.android.synthetic.main.fragment_cart_v2.*
//import kotlinx.android.synthetic.main.fragment_cart_v2.adrsLyt
//import kotlinx.android.synthetic.main.fragment_cart_v2.btnBookTable
//import kotlinx.android.synthetic.main.fragment_cart_v2.btn_donate_someone
//import kotlinx.android.synthetic.main.fragment_cart_v2.btn_schedule_order
//import kotlinx.android.synthetic.main.fragment_cart_v2.cardView2
//import kotlinx.android.synthetic.main.fragment_cart_v2.change_time_slot
//import kotlinx.android.synthetic.main.fragment_cart_v2.cnst_service_selc
//import kotlinx.android.synthetic.main.fragment_cart_v2.edAdditionalRemarks
//import kotlinx.android.synthetic.main.fragment_cart_v2.etPromoCode
//import kotlinx.android.synthetic.main.fragment_cart_v2.etReferralPoint
//import kotlinx.android.synthetic.main.fragment_cart_v2.group_delivery
//import kotlinx.android.synthetic.main.fragment_cart_v2.group_discount
//import kotlinx.android.synthetic.main.fragment_cart_v2.group_mainlyt
//import kotlinx.android.synthetic.main.fragment_cart_v2.group_presc
//import kotlinx.android.synthetic.main.fragment_cart_v2.group_question
//import kotlinx.android.synthetic.main.fragment_cart_v2.group_referral
//import kotlinx.android.synthetic.main.fragment_cart_v2.group_service
//import kotlinx.android.synthetic.main.fragment_cart_v2.group_tax
//import kotlinx.android.synthetic.main.fragment_cart_v2.group_tip
//import kotlinx.android.synthetic.main.fragment_cart_v2.group_zelle
//import kotlinx.android.synthetic.main.fragment_cart_v2.iv_doc
//import kotlinx.android.synthetic.main.fragment_cart_v2.layoutTip
//import kotlinx.android.synthetic.main.fragment_cart_v2.llReferral
//import kotlinx.android.synthetic.main.fragment_cart_v2.parkLyt
//import kotlinx.android.synthetic.main.fragment_cart_v2.price_text
//import kotlinx.android.synthetic.main.fragment_cart_v2.recyclerview
//import kotlinx.android.synthetic.main.fragment_cart_v2.recyclerviewQuest
//import kotlinx.android.synthetic.main.fragment_cart_v2.rvPhotoList
//import kotlinx.android.synthetic.main.fragment_cart_v2.rvTip
//import kotlinx.android.synthetic.main.fragment_cart_v2.tvAddonCharges
//import kotlinx.android.synthetic.main.fragment_cart_v2.tvClearTip
//import kotlinx.android.synthetic.main.fragment_cart_v2.tvDeliveryCharges
//import kotlinx.android.synthetic.main.fragment_cart_v2.tvDiscount
//import kotlinx.android.synthetic.main.fragment_cart_v2.tvNetTotal
//import kotlinx.android.synthetic.main.fragment_cart_v2.tvRedeem
//import kotlinx.android.synthetic.main.fragment_cart_v2.tvRedeemed
//import kotlinx.android.synthetic.main.fragment_cart_v2.tvReferralCode
//import kotlinx.android.synthetic.main.fragment_cart_v2.tvRestService
//import kotlinx.android.synthetic.main.fragment_cart_v2.tvSerDate
//import kotlinx.android.synthetic.main.fragment_cart_v2.tvSerTime
//import kotlinx.android.synthetic.main.fragment_cart_v2.tvSubTotal
//import kotlinx.android.synthetic.main.fragment_cart_v2.tvTaxCharges
//import kotlinx.android.synthetic.main.fragment_cart_v2.tvTipAmount
//import kotlinx.android.synthetic.main.fragment_cart_v2.tvTipCharges
//import kotlinx.android.synthetic.main.fragment_cart_v2.tv_change_agent
//import kotlinx.android.synthetic.main.fragment_cart_v2.tv_checkout
//import kotlinx.android.synthetic.main.fragment_cart_v2.tv_pay_option
//import kotlinx.android.synthetic.main.item_agent_list.*
//import kotlinx.android.synthetic.main.layout_adrs_time.*
//import kotlinx.android.synthetic.main.layout_adrs_time.toolbar_layout
//import kotlinx.android.synthetic.main.layout_instructions.*
//import kotlinx.android.synthetic.main.toolbar_app.*
//import lk.payhere.androidsdk.PHConfigs
//import lk.payhere.androidsdk.PHConstants
//import lk.payhere.androidsdk.PHMainActivity
//import lk.payhere.androidsdk.PHResponse
//import lk.payhere.androidsdk.model.InitRequest
//import lk.payhere.androidsdk.model.Item
//import lk.payhere.androidsdk.model.StatusResponse
//import org.greenrobot.eventbus.EventBus
//import org.greenrobot.eventbus.Subscribe
//import org.greenrobot.eventbus.ThreadMode
//import org.json.JSONObject
//import pub.devrel.easypermissions.AppSettingsDialog
//import pub.devrel.easypermissions.EasyPermissions
//import retrofit2.Retrofit
//import java.io.File
//import java.io.IOException
//import java.lang.reflect.Type
//import java.math.RoundingMode
//import java.net.URLEncoder
//import java.text.DecimalFormat
//import java.util.*
//import javax.inject.Inject
//import kotlin.collections.ArrayList
//
//const val PAYPALREQUEST = 587
//const val SADDED_PAYMENT_REQUEST = 588
//const val MY_FATOORAH_PAYMENT_REQUEST = 589
//const val TAP_PAYMENT_REQUEST = 590
//const val EVALON_PAYMENT_REQUEST = 591
//const val SCHEDULE_REQUEST = 593
//const val PAYHERE_REQUEST = 11010
//
//class CartV2 : BaseFragment<FragmentCartV2Binding, CartViewModel>(),
//        CartAdapter.CartCallback, CartNavigator, AddressDialogListener,
//        DialogIntrface, DialogListener,
//        CardDialogFrag.onPaymentListener, ImageCallback,
//        EasyPermissions.PermissionCallbacks, TipAdapter.TipCallback,
//        DropInResult.DropInResultListener {
//
//
//    private var terminologyBean: SettingModel.DataBean.Terminology? = null
//    private var cartList: MutableList<CartInfo>? = null
//    private var cartAdapter: CartAdapter? = null
//    private var questAdapter: SelectedQuestAdapter? = null
//    private var supplierBranchId: Int? = null
//
//    private var scheduleData: SupplierSlots? = null
//    private var agentType: Int = 0
//    private var mReferralAmt: Float = 0.0f
//    private var redeemedAmt: Double = 0.0
//    private var isReferrale = false
//
//    private var mAgentParam: AgentCustomParam? = null
//
//
//    lateinit var tipList: ArrayList<Int>
//
//    private var tipAdapter: TipAdapter? = null
//
//    //Ready Chef
//    private var havePets = 0
//    private var cleaner_in = 0
//
//    // 0 for delivery
//    private val deliveryType = 0
//
//    @Inject
//    lateinit var dataManager: DataManager
//
//    @Inject
//    lateinit var appUtils: AppUtils
//
//    @Inject
//    lateinit var cartUtils: CartUtils
//
//    @Inject
//    lateinit var mDialogsUtil: DialogsUtil
//
//    @Inject
//    lateinit var mDateTime: DateTimeUtils
//
//    @Inject
//    lateinit var retrofit: Retrofit
//
//
//    @Inject
//    lateinit var factory: ViewModelProviderFactory
//
//    private val productList = mutableListOf<CartInfoServer>()
//
//    @Inject
//    lateinit var prefHelper: PreferenceHelper
//
//    private var mViewModel: CartViewModel? = null
//    private var mBinding: FragmentCartV2Binding? = null
//
//    private lateinit var adrsData: AddressBean
//
//    private var totalAmt = 0.0
//
//    private var mDeliveryCharge = 0.0f
//
//    private var deliveryId: String = ""
//    private var mTipCharges = 0.0f
//    private var questionAddonPrice = 0.0f
//    private var maxHandlingAdminCharges = 0.0f
//    private var mQuestionList = listOf<QuestionList>()
//    private var totalAmtCopy = 0.0
//
//    var orderId = arrayListOf<Int>()
//
//    private var bookingFlowBean: SettingModel.DataBean.BookingFlowBean? = null
//    private var screenFlowBean: SettingModel.DataBean.ScreenFlowBean? = null
//
//    var productData: CartInfo? = null
//
//    /*    Pickup(1),
//        Delivery(0),
//        Both(2)*/
//    var mDeliveryType: Int? = null
//
//    var mSelectedPayment: CustomPayModel? = null
//
//    //  var mTotalAmt: Float? = null
//
//    var settingData: SettingModel.DataBean.SettingData? = null
//
//    @Inject
//    lateinit var imageUtils: ImageUtility
//
//    @Inject
//    lateinit var permissionFile: PermissionFile
//
//    private var photoFile: File? = null
//    private val imageDialog by lazy { ImageDialgFragment() }
//
//    private var imageList: MutableList<ImageListModel>? = null
//
//
//    private var mAdapter: ImageListAdapter? = null
//
//    private var restServiceTax = 0.0
//    private var minOrder: Float? = null
//    private var baseDeliveryCharges = 0.0f
//    private var regionDeliveryCharges = 0.0f
//
//    private var isPaymentConfirm: Boolean = false
//
//    private var payment_gateways: ArrayList<String>? = null
//
//    private val decimalFormat: DecimalFormat = DecimalFormat("0.00")
//
//    private var isDonate = false
//
//    private var mAuthorization: String? = null
//
//    var from_signature_plate=""
//    var from_signature_menu= ""
//    var signature_menu_id= ""
//    var signature_plate_id= ""
//    private val textConfig by lazy { appUtils.loadAppConfig(0).strings }
//
//    private val colorConfig by lazy { Configurations.colors}
//    private var selectedCurrency: Currency?=null
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        viewModel.navigator = this
//
//        imageObserver()
//
//        selectedCurrency = dataManager.getGsonValue(PrefenceConstants.CURRENCY_INF, Currency::class.java)
//        screenFlowBean = dataManager.getGsonValue(DataNames.SCREEN_FLOW, SettingModel.DataBean.ScreenFlowBean::class.java)
//        bookingFlowBean = dataManager.getGsonValue(DataNames.BOOKING_FLOW, SettingModel.DataBean.BookingFlowBean::class.java)
//        terminologyBean = prefHelper.getGsonValue(PrefenceConstants.APP_TERMINOLOGY, SettingModel.DataBean.Terminology::class.java)
//        settingData = dataManager.getGsonValue(DataNames.SETTING_DATA, SettingModel.DataBean.SettingData::class.java)
//    }
//
//
//    @Subscribe(threadMode = ThreadMode.MAIN)
//    fun onMessageEvent(event: PaymentEvent?) {
//        if (event?.resultCode == 0) {
//            if (isNetworkConnected) {
//                mSelectedPayment?.keyId = event.gateway_unique_id
//                mSelectedPayment?.payment_token = "razorpay"
//                onlinePayment(mSelectedPayment)
//            }
//        } else {
//            mBinding?.root?.onSnackbar(event?.message ?: "")
//        }
//    }
//
//
//    private fun imageObserver() {
//        // Create the observer which updates the UI.
//        val catObserver = Observer<String> { resource ->
//
//            imageList?.add(ImageListModel(is_imageLoad = true, image = resource, _id = ""))
//
//            mAdapter?.submitMessageList(imageList, "cart")
//        }
//
//        // Observe the LiveData, passing in this activity as the LifecycleOwner and the observer.
//        viewModel.imageLiveData.observe(this, catObserver)
//    }
//
//
//    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
//        super.onViewCreated(view, savedInstanceState)
//
//        mBinding = viewDataBinding
//
//        EventBus.getDefault().register(this)
//
//        mBinding?.color = colorConfig
//        mBinding?.strings = textConfig
//        mBinding?.currency = AppConstants.CURRENCY_SYMBOL
//        mBinding?.isAgentRating = settingData?.is_agent_rating=="1"
//
//        imageList = mutableListOf()
//
//        Glide.with(requireContext()).load(R.raw.loadergif).into(ivImage)
//
//        if (settingData?.extra_instructions == "1") {
//            settingInstructionLayout()
//        }
//
//        etPromoCode.afterTextChanged {
//            if (it.trim().isNotEmpty()) {
//                tvRedeem.isEnabled = true
//                tvRedeem.setTextColor(Color.parseColor(colorConfig.primaryColor))
//            } else {
//                tvRedeem.isEnabled = false
//                tvRedeem.setTextColor(Color.parseColor(colorConfig.textSubhead))
//            }
//        }
//
//
//        tvRedeem.setOnClickListener {
//            if (tvRedeem.text.toString() == getString(R.string.remove)) {
//                tvRedeem.isEnabled = true
//                tvRedeem.text = getString(R.string.apply)
//                etPromoCode.setText("")
//                etPromoCode.isEnabled = true
//                dataManager.removeValue(DataNames.DISCOUNT_AMOUNT)
//                mBinding?.root?.onSnackbar(getString(R.string.promo_remove))
//                calculateCartCharges(cartList)
//            } else {
//                if (dataManager.getCurrentUserLoggedIn())
//                    checkPromoApi(etPromoCode.text.toString().trim())
//                else {
//
//                    appUtils.checkLoginFlow(requireActivity(),DataNames.REQUEST_CART_LOGIN_PROMO)
//                }
//            }
//        }
//
//        tvClearTip.setOnClickListener {
//            mTipCharges = 0.0f
//            tvTipAmount.text = getString(R.string.currency_tag, AppConstants.CURRENCY_SYMBOL, Utils.getPriceFormat(mTipCharges,settingData,selectedCurrency))
//            tvTipCharges.text = activity?.getString(R.string.currency_tag, AppConstants.CURRENCY_SYMBOL, Utils.getPriceFormat(mTipCharges,settingData,selectedCurrency))
//            calculateCartCharges(cartList)
//        }
//
//        cardView2.setOnClickListener {
//            dataManager.getKeyValue(DataNames.FEATURE_DATA, PrefenceConstants.TYPE_STRING).toString().let {
//                val listType: Type = object : TypeToken<ArrayList<SettingModel.DataBean.FeatureData?>?>() {}.type
//                val featureList: ArrayList<SettingModel.DataBean.FeatureData> = Gson().fromJson(it, listType)
//
//                activity?.launchActivity<PaymentListActivity>(AppConstants.REQUEST_PAYMENT_OPTION) {
//                    putParcelableArrayListExtra("feature_data", featureList)
//                    putExtra("mSelectPayment", payment_gateways)
//                    putExtra("mTotalAmt", totalAmt.toFloat())
//                }
//            }
//        }
//
//
//        btnBookTable?.setOnClickListener {
//            activity?.launchActivity<ScheduleOrder>(SCHEDULE_REQUEST_DINE_IN) {
//                putExtra("supplierId", cartList?.firstOrNull()?.supplierId.toString())
//                putExtra("dineIn", true)
//                if (::adrsData.isInitialized) {
//                    putExtra("latitude", adrsData.latitude)
//                    putExtra("longitude", adrsData.longitude)
//                }
//            }
//        }
//
//        btnBookTable?.visibility = if(settingData?.is_table_booking == "1"){
//            View.VISIBLE
//        } else{
//            View.GONE
//        }
//
//
//        tvRedeemed.setOnClickListener {
//            if (tvRedeemed.text.toString() == getString(R.string.remove)) {
//                isReferrale = false
//                redeemedAmt = 0.0
//                group_referral.visibility = View.GONE
//                tvRedeemed.text = getString(R.string.apply)
//                calculateCartCharges(cartList)
//            } else {
//                isReferrale = true
//                tvRedeemed.text = getString(R.string.remove)
//                calculateCartCharges(cartList)
//                group_referral.visibility = View.VISIBLE
//            }
//        }
//
//        tvCancel?.setOnClickListener {
//            AppToasty.error(requireContext(), getString(R.string.request_cancelled))
//            mViewModel?.cancelGenerateOrder()
//        }
//
//        cartList = appUtils.getCartList().cartInfos
//
//        mDeliveryType = cartList?.firstOrNull()?.deliveryType
//
//        if (cartList?.isNotEmpty() == true) {
//            productData = cartList?.first()
//        }
//
//        supplierBranchId = productData?.suplierBranchId ?: 0
//
//        settingToolbar()
//
//        settingAdrs()
//        initTipAdapter()
//        extraFunctionality()
//
//        if (productData?.appType == AppDataType.HomeServ.type) {
//            cnst_service_selc.visibility = View.VISIBLE
//            gp_action.visibility = View.GONE
//            if (mAgentParam != null) {
//                tv_change_agent.visibility = View.GONE
//                group_mainlyt.visibility = View.VISIBLE
//            } else {
//                tv_change_agent.visibility = View.VISIBLE
//                group_mainlyt.visibility = View.GONE
//            }
//        } else {
//            cnst_service_selc.visibility = View.GONE
//        }
//
//        change_time_slot.setOnClickListener {
//
//            openBookingDateTime(false)
//        }
//
//        tv_change_agent.setOnClickListener {
//            openBookingDateTime(true)
//        }
//
//
//        if (productData?.appType == AppDataType.HomeServ.type) {
//            deliver_text.text = getString(R.string.service_at)
//        }
//
//        val term = Gson().fromJson(settingData?.terminology, SettingModel.DataBean.Terminology::class.java)
//        val languageId = prefHelper.getKeyValue(DataNames.SELECTED_LANGUAGE, PrefenceConstants.TYPE_STRING).toString()
//        val appTerminology = if (languageId == ClikatConstants.ENGLISH_FULL || languageId == ClikatConstants.ENGLISH_SHORT) {
//            term.english
//        } else {
//            term.other
//        }
//        if (appTerminology?.order_now?.isNotEmpty() == true)
//            tv_checkout?.text = term.english?.order_now
//        if (appTerminology?.choose_payment?.isNotEmpty() == true)
//            tv_pay_option?.text = term.english?.choose_payment
//
//        if (term?.english?.choose_payment?.isNotEmpty() == true)
//            tv_pay_option?.text = term.english.choose_payment
//
//
//        btn_donate_someone?.text = if (!appTerminology?.donate_to_someone.isNullOrEmpty()) appTerminology?.donate_to_someone else getString(R.string.donate_to_someone)
//        if (settingData?.payment_method != "null" && settingData?.payment_method?.toInt() ?: 0 == PaymentType.CASH.payType) {
//            tv_pay_option.text = textConfig?.cod
//            cardView2.isEnabled = false
//            group_tip.visibility = View.GONE
//        } else {
//            cardView2.isEnabled = true
//        }
//
//        if (AppConstants.APP_SUB_TYPE > AppDataType.Custom.type) {
//            tv_deliver_adrs?.setTextColor(ContextCompat.getColor(requireContext(), R.color.grey))
//            deliver_text?.setTextColor(ContextCompat.getColor(requireContext(), R.color.grey))
//            viewBackground.setBackgroundColor(ContextCompat.getColor(requireActivity(), R.color.white))
//            ivChange?.visibility = View.VISIBLE
//            tv_change_adrs?.visibility = View.GONE
//            viewBottom?.visibility = View.VISIBLE
//            ivLocation?.visibility = View.VISIBLE
//            etPromoCode?.setHintTextColor(ContextCompat.getColor(requireContext(), R.color.black_80))
//            etPromoCode?.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_coupon, 0, 0, 0)
//        } else {
//            viewBottom?.visibility = View.GONE
//            viewBackground.setBackgroundColor(Color.parseColor(colorConfig.toolbarColor))
//            tv_deliver_adrs?.setTextColor(Color.parseColor(colorConfig.toolbarText))
//            deliver_text?.setTextColor(Color.parseColor(colorConfig.toolbarText))
//            etPromoCode?.setHintTextColor(Color.parseColor(colorConfig.primaryColor))
//        }
//
//
//        settingData?.is_schdule_order?.let {
//            if (it == "1") {
//                btn_schedule_order.visibility = View.VISIBLE
//            }
//        }
//
//        btn_schedule_order.setOnClickListener {
//            bookOrder(true)
//        }
//
//
//    }
//
//    private fun openBookingDateTime(isAgent: Boolean) {
//
//        if (dataManager.getCurrentUserLoggedIn())
//            activity?.launchActivity<ServSelectionActivity>(AppConstants.REQUEST_AGENT_DETAIL)
//            {
//                putExtra(DataNames.SUPPLIER_BRANCH_ID, supplierBranchId)
//                putExtra("productIds", cartList?.map { it.productId.toString() }?.toTypedArray())
//                if (isAgent) {
//                    putExtra("mAgentData", mAgentParam)
//                    putExtra("isAgent", isAgent)
//                }
//                putExtra("duration", cartList?.filter { it.serviceType == 0 }?.sumByDouble {
//                    it.serviceDuration.times(it.quantity).toDouble()
//                })
//                putExtra("screenType", "order")
//            }
//        else {
//
//            appUtils.checkLoginFlow(requireActivity(),AppConstants.REQUEST_CART_LOGIN_BOOKING)
//        }
//    }
//
//    override fun onDestroyView() {
//        super.onDestroyView()
//        EventBus.getDefault().unregister(this)
//        viewModel.compositeDisposable.clear()
//    }
//
//
//    private fun settingToolbar() {
//        tv_deliver_adrs.text = if (mDeliveryType == FoodAppType.Pickup.foodType && dataManager.getGsonValue(PrefenceConstants.RESTAURANT_INF, LocationUser::class.java) != null) {
//            dataManager.getGsonValue(PrefenceConstants.RESTAURANT_INF, LocationUser::class.java)?.address
//                    ?: ""
//        } else if (dataManager.getGsonValue(PrefenceConstants.ADRS_DATA, AddressBean::class.java) != null) {
//            dataManager.getGsonValue(PrefenceConstants.ADRS_DATA, AddressBean::class.java)?.also {
//                adrsData = it
//                deliveryId = it.id.toString()
//            }.let {
//                if (it?.address_line_1 != null)
//                    "${it.customer_address} , ${it.address_line_1}"
//                else
//                    it?.customer_address
//            }
//        } else {
//            dataManager.getGsonValue(DataNames.LocationUser, LocationUser::class.java)?.also {
//                adrsData = AddressBean(latitude = it.latitude, longitude = it.longitude, customer_address = it.address)
//            }.let { it?.address }
//        }
//
//        if (dataManager.getCurrentUserLoggedIn() && cartList?.isNotEmpty() == true) {
//            CommonUtils.setBaseUrl(BuildConfig.BASE_URL, retrofit)
//            viewModel.getAddressList(cartList?.firstOrNull()?.suplierBranchId ?: 0)
//        } else {
//            setData()
//        }
//    }
//    override fun onVehicleSelect(id:String,model: String, color: String, plate_no: String) {
//        TODO("Not yet implemented")
//    }
//
//    private fun settingAdrs() {
//
//        tv_change_adrs.setOnClickListener {
//            if (dataManager.getCurrentUserLoggedIn()) {
//                AddressDialogFragmentV2.newInstance(productData?.suplierBranchId
//                        ?: 0).show(childFragmentManager, "dialog")
//
//
//            } else {
//
//                appUtils.checkLoginFlow(requireActivity(),AppConstants.REQUEST_ADDRESS_ADD)
//            }
//        }
//        ivChange.setOnClickListener {
//            tv_change_adrs.callOnClick()
//        }
//        tv_checkout.setOnClickListener {
//
//            bookOrder(false)
//        }
//
//        btn_donate_someone.setOnClickListener {
//            if (::adrsData.isInitialized) {
//                openDonateDialog()
//            }
//        }
//
//    }
//
//    private fun showMinOrderAlert() {
//        AlertDialog.Builder(requireContext())
//                .setMessage(getString(R.string.net_total_greater_than, AppConstants.CURRENCY_SYMBOL, Utils.getPriceFormat(minOrder?.toFloat()
//                        ?: 0.0f,settingData,selectedCurrency)))
//                .setPositiveButton(R.string.Ok, null)
//                .show()
//    }
//
//    private var walletDiscount: Double? = 0.0
//
//    private fun settingLayout(maxHandlingAdminCharges: Float, maxDeliveryCharge: Float) {
//
//        totalAmt = appUtils.calculateCartTotal(cartList)
//
//        tvSubTotal.text = getString(R.string.currency_tag, AppConstants.CURRENCY_SYMBOL, Utils.getPriceFormat(totalAmt.toFloat(),settingData,selectedCurrency))
//
//        settingData?.user_service_fee.let {
//
//            if (it == "1" && ::adrsData.isInitialized) {
//                restServiceTax = totalAmt.div(100f).times(adrsData.user_service_charge ?: 0.0)
//                group_service.visibility = View.VISIBLE
//                tvRestService.text = getString(R.string.currency_tag, AppConstants.CURRENCY_SYMBOL, Utils.getPriceFormat(restServiceTax.toFloat(),settingData,selectedCurrency))
//            }
//        }
//
//
//        //  totalAmt = totalAmt.plus(maxHandlingAdminCharges).plus(maxDeliveryCharge)
//        if (settingData?.wallet_module == "1" && mSelectedPayment?.payName == getString(R.string.wallet)) {
//            val amt = ((settingData?.payment_through_wallet_discount?.toDoubleOrNull()
//                    ?: 0.0).div(100)).times(totalAmt)
//            walletDiscount = amt
//            tvWalletCharges?.text = getString(R.string.currency_tag, AppConstants.CURRENCY_SYMBOL, Utils.getPriceFormat(
//                    amt.toFloat(),settingData,selectedCurrency))
//            totalAmt -= amt
//
//            totalAmt = if (totalAmt < 0)
//                maxDeliveryCharge + maxHandlingAdminCharges + mTipCharges + restServiceTax + questionAddonPrice
//            else
//                totalAmt + maxDeliveryCharge + maxHandlingAdminCharges + mTipCharges + restServiceTax + questionAddonPrice
//        } else if (dataManager.getGsonValue(DataNames.DISCOUNT_AMOUNT, PromoCodeModel.DataBean::class.java) != null) {
//            val promoData = dataManager.getGsonValue(DataNames.DISCOUNT_AMOUNT, PromoCodeModel.DataBean::class.java)
//            group_discount.visibility = View.VISIBLE
//            totalAmt = totalAmt.minus(promoData?.discountPrice ?: 0f)
//            etPromoCode.setText(promoData?.promoCode)
//            tvRedeem.text = getString(R.string.remove)
//            etPromoCode.isEnabled = false
//            val discount = if (totalAmt < 0) totalAmt.toFloat() else promoData?.discountPrice
//
//            totalAmt = if (totalAmt < 0)
//                maxDeliveryCharge + maxHandlingAdminCharges + mTipCharges + restServiceTax + questionAddonPrice
//            else
//                totalAmt + maxDeliveryCharge + maxHandlingAdminCharges + mTipCharges + restServiceTax + questionAddonPrice
//
//
//            tvDiscount.text = "-" + getString(R.string.currency_tag, AppConstants.CURRENCY_SYMBOL, Utils.getPriceFormat(discount
//                    ?: 0f,settingData,selectedCurrency))
//        } else {
//            etPromoCode.setText("")
//            etPromoCode.isEnabled = true
//            tvRedeem.text = getString(R.string.apply)
//            group_discount.visibility = View.GONE
//            tvDiscount.text = getString(R.string.currency_tag, AppConstants.CURRENCY_SYMBOL, Utils.getPriceFormat(0.0f,settingData,selectedCurrency))
//            totalAmt = appUtils.calculateCartTotal(cartList) + maxHandlingAdminCharges + maxDeliveryCharge + mTipCharges + restServiceTax + questionAddonPrice
//        }
//
//        if (maxHandlingAdminCharges == 0.0f) {
//            group_tax.visibility = View.GONE
//        }
//
//        tvTaxCharges.text = getString(R.string.currency_tag, AppConstants.CURRENCY_SYMBOL, Utils.getPriceFormat(maxHandlingAdminCharges,settingData,selectedCurrency))
//
//        if (mReferralAmt > 0 && isReferrale) {
//
//            redeemedAmt = if (totalAmt >= mReferralAmt) {
//                mReferralAmt.toDouble()
//            } else {
//                totalAmt
//            }
//
//            totalAmt -= mReferralAmt
//
//            tvReferralCode.text = "-" + getString(R.string.currency_tag, AppConstants.CURRENCY_SYMBOL, Utils.getPriceFormat(redeemedAmt.toFloat(),settingData,selectedCurrency))
//        }
//
//        if (scheduleData != null && scheduleData?.supplierTimings?.isNotEmpty() == true)
//            totalAmt += (scheduleData?.supplierTimings?.firstOrNull()?.price?.toDouble() ?: 0.0)
//
//
//        decimalFormat.roundingMode = RoundingMode.DOWN
//        tvNetTotal.text = AppConstants.CURRENCY_SYMBOL + decimalFormat.format(totalAmt)
//        // tvNetTotal.text = getString(R.string.currency_tag, AppConstants.CURRENCY_SYMBOL, totalAmt)
//
//
//        price_text?.setBackgroundColor(ContextCompat.getColor(requireActivity(), R.color.color_light_grey))
//        subtotalContainer?.setBackgroundColor(ContextCompat.getColor(requireActivity(), R.color.color_light_grey))
//        addOnContainer?.setBackgroundColor(ContextCompat.getColor(requireActivity(), R.color.color_light_grey))
//        deliveryContainer?.setBackgroundColor(ContextCompat.getColor(requireActivity(), R.color.color_light_grey))
//        tipsContainer?.setBackgroundColor(ContextCompat.getColor(requireActivity(), R.color.color_light_grey))
//        taxContainer?.setBackgroundColor(ContextCompat.getColor(requireActivity(), R.color.color_light_grey))
//        discountContainer?.setBackgroundColor(ContextCompat.getColor(requireActivity(), R.color.color_light_grey))
//        nettAmountContainer?.setBackgroundColor(ContextCompat.getColor(requireActivity(), R.color.color_light_grey))
//
//        tb_back.setOnClickListener { v -> Navigation.findNavController(v).popBackStack() }
//        tb_icon?.visibility = View.GONE
//
//        if (screenFlowBean?.app_type == AppDataType.Ecom.type) {
//            tb_title.text = getString(R.string.ecom_my_cart)
//        }
//        toolbar_layout?.visibility = View.VISIBLE
//        toolbar_layout?.elevation = 0f
//        viewBackground.background = ContextCompat.getDrawable(requireActivity(), R.drawable.background_toolbar_bottom_radius)
//        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
//            viewBackground?.background?.colorFilter = BlendModeColorFilter(Color.parseColor(colorConfig.toolbarColor), BlendMode.SRC_ATOP)
//        }
//
//    }
//
//    private fun initTipAdapter() {
//        tvTipCharges.text = activity?.getString(R.string.currency_tag, AppConstants.CURRENCY_SYMBOL, Utils.getPriceFormat(mTipCharges,settingData,selectedCurrency))
//        tipList = ArrayList<Int>()
//        val mLayoutManager = LinearLayoutManager(activity, RecyclerView.HORIZONTAL, false)
//        rvTip.layoutManager = mLayoutManager
//        tipAdapter = TipAdapter(activity ?: requireContext(), tipList, screenFlowBean, appUtils,settingData,selectedCurrency)
//        rvTip.adapter = tipAdapter
//        tipAdapter?.tipCallback(this)
//    }
//
//
//    private fun setData() {
//
//        cartList = appUtils.getCartList().cartInfos
//
//        isPaymentConfirm = cartList?.any { it.isPaymentConfirm == 1 } ?: false
//        // isPaymentConfirm=true
//
//        setCartVisibility(cartList)
//
//        cartList.takeIf { it?.isNotEmpty() == true }?.let { cartInfo ->
//
//            cartAdapter = CartAdapter(activity
//                    ?: requireContext(), cartInfo, screenFlowBean, appUtils, settingData,selectedCurrency)
//            cartAdapter?.settingCallback(this)
//            recyclerview.adapter = cartAdapter
//
//            refreshDeliveryAdrs(productData?.deliveryType ?: 0)
//
//            /* val list=ArrayList<CartItem>()
//             cartInfo.forEach{
//                list.add(CartItem(product_ids = it.productId,supplier_id = it.supplierId))
//             }*/
//
//            if (isNetworkConnected) {
//                if (::adrsData.isInitialized) {
//                    viewModel.refreshCart(CartReviewParam(product_ids = cartInfo.map { it.productId },
//                            latitude = adrsData.latitude ?: "", longitude = adrsData.longitude
//                            ?: ""),dataManager.getCurrentUserLoggedIn())
//                } else {
//                    dataManager.getGsonValue(DataNames.LocationUser, LocationUser::class.java)?.also {
//                        viewModel.refreshCart(CartReviewParam(
//                                product_ids = cartInfo.map { it.productId }, latitude = it.latitude
//                                ?: "0.0",
//                                longitude = it.longitude ?: "0.0"),dataManager.getCurrentUserLoggedIn())
//                    }
//                }
//            }
//
//            if (isNetworkConnected) {
//                if (dataManager.getCurrentUserLoggedIn() && settingData?.referral_feature != null && settingData?.referral_feature == "1") {
//                    viewModel.referralAmount()
//                }
//            }
//        }
//
//        setQuestAdapter()
//
//
//        // init swipe to dismiss logic
//        val swipeToDismissTouchHelper = ItemTouchHelper(object : ItemTouchHelper.SimpleCallback(
//                ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {
//            override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder): Boolean {
//                // callback for drag-n-drop, false to skip this feature
//                return false
//            }
//
//            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
//                // callback for swipe to dismiss, removing item from data and adapter
//
//                val mCartProd = cartList?.get(viewHolder.adapterPosition)
//
//
//                if (mCartProd?.productAddonId ?: 0 > 0) {
//                    StaticFunction.removeFromCart(activity, mCartProd?.productId, mCartProd?.productAddonId
//                            ?: 0)
//                } else {
//                    StaticFunction.removeFromCart(activity, mCartProd?.productId, 0)
//                }
//
//                dataManager.removeValue(DataNames.DISCOUNT_AMOUNT)
//                cartList?.removeAt(viewHolder.adapterPosition)
//                cartAdapter?.notifyItemRemoved(viewHolder.adapterPosition)
//                setCartVisibility(cartList)
//                calculateCartCharges(cartList)
//            }
//        })
//        swipeToDismissTouchHelper.attachToRecyclerView(recyclerview)
//
//        if (mDeliveryType == FoodAppType.Both.foodType && productData?.appType == AppDataType.Food.type) {
//            openDeliveryDialog()
//        }
//
//        if ((settingData?.cart_image_upload == "1" || settingData?.product_prescription == "1") &&
//                cartList?.any { it.cart_image_upload == 1 } == true) {
//            group_presc.visibility = View.VISIBLE
//
//            rvPhotoList.layoutManager = LinearLayoutManager(activity, RecyclerView.HORIZONTAL, false)
//
//            mAdapter = ImageListAdapter(ImageListAdapter.UserChatListener({
//
//                if (it.is_imageLoad == false) {
//                    if (imageList?.count() ?: 0 >= 4) {
//                        mBinding?.root?.onSnackbar(getString(R.string.max_limit_reached))
//
//                    } else {
//                        if (dataManager.getCurrentUserLoggedIn())
//                            uploadImage()
//                        else {
//
//                            appUtils.checkLoginFlow(requireActivity(),DataNames.REQUEST_CART_LOGIN_PRECRIPTION)
//
//                        }
//                    }
//                }
//
//            }, { it1 ->
//                if (it1 < 0) return@UserChatListener
//                imageList?.removeAt(it1)
//                mAdapter?.submitMessageList(imageList, "cart")
//            },{it,pos->
//
//            }), false)
//
//            mAdapter?.submitMessageList(imageList, "cart")
//            rvPhotoList.adapter = mAdapter
//        } else
//            group_presc.visibility = View.GONE
///*
//        settingData?.cart_image_upload?.let {
//            if (it == "1") {
//
//
//            } else {
//                group_presc.visibility = View.GONE
//            }
//        }
//*/
//
//        cartList?.any { it.order_instructions == 1 }?.let {
//            if (it == true) {
//                edAdditionalRemarks.visibility = View.VISIBLE
//            } else {
//                edAdditionalRemarks.visibility = View.GONE
//            }
//        }
//    }
//
//    private fun setCartVisibility(cartList: MutableList<CartInfo>?) {
//        viewModel.setIsCartList(cartList?.size ?: 0)
//
//        if (isPaymentConfirm || cartList?.count() == 0 || settingData?.isCash ?: "" == "1") {
//            cardView2.visibility = View.GONE
//            btn_donate_someone.visibility = View.GONE
//            group_tip.visibility = View.GONE
//        } else {
//            cardView2.visibility = View.VISIBLE
//            settingData?.show_donate_popup?.let {
//                if (it == "1" && mDeliveryType == FoodAppType.Delivery.foodType) {
//                    btn_donate_someone.visibility = View.VISIBLE
//                } else
//                    btn_donate_someone.visibility = View.GONE
//            }
//        }
//    }
//
//    private fun setQuestAdapter() {
//
//        mQuestionList = cartList?.distinctBy {
//            it.question_list?.distinctBy { it1 -> it1.questionId }
//        }?.flatMap { it2 ->
//            it2.question_list ?: mutableListOf()
//        } ?: listOf()
//
//
//        if (mQuestionList.isEmpty()) return
//        group_question.visibility = View.VISIBLE
//
//
//        val filterLIst = mQuestionList.flatMap { it.optionsList }
//
//        questionAddonPrice = filterLIst.sumByDouble {
//
//            if (it.flatValue > 0) {
//                it.flatValue.toDouble()
//            } else {
//                it.productPrice.div(100.0f).times(it.percentageValue).toDouble()
//            }
//        }.toFloat()
//
//        tvAddonCharges.text = getString(R.string.currency_tag, AppConstants.CURRENCY_SYMBOL, Utils.getPriceFormat(questionAddonPrice,settingData,selectedCurrency))
//        questAdapter = SelectedQuestAdapter(activity ?: requireContext(), mQuestionList, settingData,selectedCurrency)
//        recyclerviewQuest.layoutManager = LinearLayoutManager(activity
//                ?: requireContext(), RecyclerView.VERTICAL, false)
//        recyclerviewQuest.adapter = questAdapter
//        questAdapter?.notifyDataSetChanged()
//
//        calculateCartCharges(cartList)
//    }
//
//
//    private fun settingInstructionLayout() {
//        parkLyt.visibility = View.VISIBLE
//
//        rButtonPetYes.setOnClickListener {
//            rButtonPetYes.isChecked = true
//            rButtonPetNo.isChecked = false
//            havePets = 1
//            cleaner_in = 0
//        }
//
//        rButtonPetNo.setOnClickListener {
//            rButtonPetNo.isChecked = true
//            rButtonPetYes.isChecked = false
//            havePets = 0
//            cleaner_in = 1
//
//        }
//
//        rButtonCleanInYes.setOnClickListener {
//            rButtonCleanInYes.isChecked = true
//            rButtonCleanInNo.isChecked = false
//            cleaner_in = 1
//            havePets = 0
//        }
//
//        rButtonCleanInNo.setOnClickListener {
//            rButtonCleanInYes.isChecked = false
//            rButtonCleanInNo.isChecked = true
//            cleaner_in = 0
//            havePets = 1
//
//        }
//
//        // havePets = rButtonPetYes.isChecked
//        // cleaner_in=rButtonCleanInYes.isChecked
//
//    }
//
//
//    private fun showInstructionDialog() {
//        val builder = AlertDialog.Builder(requireContext())
//        builder.setTitle(R.string.things_to_remember)
//                .setMessage(settingData?.things_to_remember)
//                .setPositiveButton(R.string.Ok) { dialog, id ->
//                    if (isNetworkConnected) {
//                        mViewModel?.addCart(productList, appUtils, questionAddonPrice, mQuestionList, mutableListOf(), decimalFormat)
//                    }
//                }
//
//        builder.show()
//    }
//
//
//    private fun calculateDelivery() {
//
//        val checkEcomApp = productData?.appType == AppDataType.Ecom.type && settingData?.ecom_agent_module == "1"
//        val checkFoodApps = (productData?.appType == AppDataType.Food.type && regionDeliveryCharges == 0f ||
//                screenFlowBean?.is_single_vendor == VendorAppType.Single.appType)
//
//        if ((checkFoodApps || checkEcomApp) && ::adrsData.isInitialized && mDeliveryType == 0 && settingData?.delivery_charge_type != "1") {
//            if (isNetworkConnected) {
//                viewModel.getDistance(LatLng(adrsData.latitude?.toDouble()
//                        ?: 0.0, adrsData.longitude?.toDouble() ?: 0.0)
//                        , LatLng(productData?.latitude ?: 0.0, productData?.longitude ?: 0.0), 0)
//            }
//        } else {
//            calculateCartCharges(cartList)
//        }
//    }
//
//    private fun refreshDeliveryAdrs(deliveryType: Int) {
//
//        adrsLyt.visibility = View.VISIBLE
//
//
//
//        when (deliveryType) {
//            FoodAppType.Pickup.foodType -> {
//                group_delivery.visibility = View.GONE
//                tv_change_adrs.visibility = View.GONE
//                deliver_text.text = getString(R.string.pickup_from)
//            }
//            FoodAppType.Delivery.foodType -> {
//                group_delivery.visibility = View.VISIBLE
//                tv_change_adrs.visibility = View.VISIBLE
//                deliver_text.text = getString(R.string.delivery_to)
//            }
//            else -> {
//                adrsLyt.visibility = View.GONE
//            }
//        }
//
//        if (productData?.appType == AppDataType.HomeServ.type) {
//            group_delivery.visibility = View.GONE
//        }
//
//
//        /*   if (deliveryId.isEmpty()) {
//
//           tv_deliver_adrs.text=  if (mDeliveryType == FoodAppType.Pickup.foodType && dataManager.getGsonValue(PrefenceConstants.RESTAURANT_INF, LocationUser::class.java) != null) {
//                   val mLocUser = dataManager.getGsonValue(PrefenceConstants.RESTAURANT_INF, LocationUser::class.java)
//                           ?: return
//                  mLocUser.address
//               }else if( dataManager.getGsonValue(DataNames.LocationUser, LocationUser::class.java)!=null)
//               {
//                   dataManager.getGsonValue(DataNames.LocationUser, LocationUser::class.java)?.let {
//                       it.address
//                   }
//               }else {
//                     getString(R.string.delivery_location)
//               }
//           }*/
//    }
//
//    private fun bookOrder(isSchedule: Boolean) {
//        if (isNetworkConnected) {
//            if (dataManager.getCurrentUserLoggedIn()) {
//                if (mSelectedPayment?.payName == getString(R.string.mumybene) && phoneNumber.isNullOrEmpty()) {
//                    mDialogsUtil.showMumyBenePhone(activity ?: requireContext(), phoneNumber
//                            ?: "",payName = mSelectedPayment?.mumybenePay?:"") {
//                        phoneNumber = it
//                    }
//
//                } else if (mSelectedPayment?.payName == textConfig?.braintree) {
//                    if (mAuthorization.isNullOrEmpty()) {
//                        AppToasty.error(requireContext(), getString(R.string.braintree_token_error))
//                        return
//                    }
//                    launchDropIn()
//                } else {
//                    val orderCharges = appUtils.calculateCartTotal(cartList)
//                    if (minOrder != null && ((minOrder?.toDouble() ?: 0.0) > orderCharges))
//                        showMinOrderAlert()
//                    else
//                        handleBothDelivery(isSchedule)
//                }
//
//            } else {
//                if (settingData?.user_register_flow != null && settingData?.user_register_flow == "1") {
//                    startActivityForResult(Intent(requireContext(), SigninActivity::class.java), DataNames.REQUEST_CART_LOGIN)
//                } else {
//                    startActivityForResult(Intent(requireContext(), LoginActivity::class.java), DataNames.REQUEST_CART_LOGIN)
//                }
//
//            }
//        }
//    }
//
//
//    private fun handleBothDelivery(isSchedule: Boolean) {
//        if (mDeliveryType == FoodAppType.Both.foodType && productData?.appType == AppDataType.Food.type) {
//            openDeliveryDialog()
//        } else {
//            placeOrder(isSchedule)
//        }
//    }
//
//    private fun openDonateDialog() {
//        val binding = DataBindingUtil.inflate<DialogDonateBinding>(LayoutInflater.from(activity), R.layout.dialog_donate, null, false)
//        binding.color = colorConfig
//        binding.strings = textConfig
//
//        val mDialog = mDialogsUtil.showDialog(activity ?: requireContext(), binding.root)
//        mDialog.show()
//
//        val edDelivery = mDialog.findViewById<TextInputEditText>(R.id.edDelivery)
//        val btnDonate = mDialog.findViewById<MaterialButton>(R.id.btn_donate)
//        val btnSkip = mDialog.findViewById<MaterialButton>(R.id.btn_skip)
//        val address = if (adrsData.address_line_1 != null)
//            "${adrsData.customer_address ?: ""},${adrsData.address_line_1 ?: ""}"
//        else
//            adrsData.customer_address
//
//        edDelivery.setText(address)
//
//        btnDonate.setOnClickListener {
//            isDonate = true
//            mDialog.dismiss()
//        }
//
//        btnSkip.setOnClickListener {
//            mDialog.dismiss()
//        }
//
//        mDialog.setOnDismissListener {
//            tv_checkout.callOnClick()
//        }
//
//    }
//
//    private fun openDeliveryDialog() {
//
//        val binding = DataBindingUtil.inflate<DialogBothDeliveryBinding>(LayoutInflater.from(activity), R.layout.dialog_both_delivery, null, false)
//        binding.color = colorConfig
//        binding.strings = textConfig
//
//        val mDialog = mDialogsUtil.showDialogFix(activity ?: requireContext(), binding.root)
//        mDialog.show()
//
//        val btn_pickup = mDialog.findViewById<MaterialButton>(R.id.btn_pickup)
//        val btn_delivery = mDialog.findViewById<MaterialButton>(R.id.btn_delivery)
//
//        btn_pickup.setOnClickListener {
//            mDeliveryType = FoodAppType.Pickup.foodType
//            if (mDialog.isShowing) {
//                mDialog.dismiss()
//            }
//        }
//
//        btn_delivery.setOnClickListener {
//            mDeliveryType = FoodAppType.Delivery.foodType
//            if (mDialog.isShowing) {
//                mDialog.dismiss()
//            }
//        }
//
//        mDialog.setOnDismissListener {
//            val cart = CartList()
//
//            cartList?.map {
//                it.deliveryType = mDeliveryType ?: 0
//            }
//
//            if (mDeliveryType == 1) {
//                mDeliveryCharge = 0.0f
//                group_tip.visibility = View.GONE
//            }
//
//            cart.cartInfos = cartList
//
//            dataManager.addGsonValue(DataNames.CART, Gson().toJson(cart))
//            // calculateCartCharges(cartList.cartInfos)
//            refreshDeliveryAdrs(productData?.deliveryType ?: 0)
//            calculateDelivery()
//        }
//    }
//
//    private fun placeOrder(isSchedule: Boolean) {
//        productList.clear()
//        productList.addAll(StaticFunction.covertCartToArray(activity))
//
//        if (productList.isNotEmpty()) {
//            agentType = productList.first().agent_type ?: 0
//        }
//
//        if (deliveryId.isEmpty() || deliveryId == "0" && mDeliveryType != FoodAppType.Pickup.foodType) {
//            // mBinding?.root?.onSnackbar("Please select address")
//            tv_change_adrs.callOnClick()
//        } else if (mAgentParam == null && productData?.appType == AppDataType.HomeServ.type) {
//            mBinding?.root?.onSnackbar(getString(R.string.please_select_slot))
//        } else if (validatePaymentOption(mSelectedPayment)) {
//            mBinding?.root?.onSnackbar(getString(R.string.choose_payment))
//        } else if (imageList?.isEmpty() == true && (settingData?.cart_image_upload == "1" || settingData?.product_prescription == "1") &&
//                (cartList?.any { it.cart_image_upload == 1 } == true)) {
//            mBinding?.root?.onSnackbar(getString(R.string.please_select_precription))
//        } else if (settingData?.extra_instructions == "1") {
//            showInstructionDialog()
//        } else {
//            if (isSchedule) {
//                if (scheduleData != null) {
//                    if (isNetworkConnected) {
//                        mViewModel?.addCart(productList, appUtils, questionAddonPrice, mQuestionList, mutableListOf(), decimalFormat)
//                    }
//                } else {
//                    activity?.launchActivity<ScheduleOrder>(com.codebrew.clikat.module.cart.SCHEDULE_REQUEST) {
//                        putExtra("supplierId", cartList?.firstOrNull()?.supplierId.toString())
//                        if (::adrsData.isInitialized) {
//                            putExtra("latitude", adrsData.latitude)
//                            putExtra("longitude", adrsData.longitude)
//                        }
//                    }
//                }
//            } else {
//                scheduleData = null
//                if (isNetworkConnected) {
//                    mViewModel?.addCart(productList, appUtils, questionAddonPrice, mQuestionList, mutableListOf(), decimalFormat)
//                }
//            }
//
//        }
//    }
//
//    private fun validatePaymentOption(mSelectedPayment: CustomPayModel?): Boolean {
//        return if (mSelectedPayment == null && (settingData?.payment_method != "null" && settingData?.payment_method?.toInt() ?: 0 != PaymentType.CASH.payType) && !isPaymentConfirm) {
//            true
//        } else if (settingData?.payment_method != "null" && settingData?.payment_method?.toInt() ?: 0 == PaymentType.ONLINE.payType || settingData?.payment_method?.toInt() ?: 0 == PaymentType.BOTH.payType) {
//            return false
//        } else {
//            return false
//        }
//
//    }
//
//    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
//        super.onActivityResult(requestCode, resultCode, data)
//
//        if (requestCode == DataNames.REQUEST_CART_LOGIN && resultCode == Activity.RESULT_OK) {
//            if (dataManager.getCurrentUserLoggedIn()) {
//                if (isNetworkConnected) {
//                    if (::adrsData.isInitialized)
//                        viewModel.refreshCart(CartReviewParam(product_ids = cartList?.map { it.productId },
//                                latitude = adrsData.latitude ?: "", longitude = adrsData.longitude
//                                ?: ""), true)
//                }
//                if (settingData?.referral_feature != null && settingData?.referral_feature == "1") {
//                    viewModel.referralAmount()
//                }
//            }
//        } else if (requestCode == PAYHERE_REQUEST) {
//            if (data != null && data.hasExtra(PHConstants.INTENT_EXTRA_RESULT)) {
//                val response: PHResponse<StatusResponse> = data.getSerializableExtra(PHConstants.INTENT_EXTRA_RESULT) as PHResponse<StatusResponse>
//                if (response.isSuccess) {
//                    val msg = "Activity result:" + response.data.toString()
//                    mSelectedPayment?.keyId = response.data?.paymentNo.toString()
//                    onlinePayment(mSelectedPayment)
//                } else {
//                    val msg = "Result:$response"
//
//                    AppToasty.error(requireContext(), getString(R.string.payment_failed))
//                }
//            }
//        } else if (requestCode == PAYPALREQUEST) {
//            when (resultCode) {
//                Activity.RESULT_OK -> {
//                    val result: DropInResult = data?.getParcelableExtra(DropInResult.EXTRA_DROP_IN_RESULT)!!
//
//                    mSelectedPayment?.keyId = result.paymentMethodNonce?.nonce
//                    mSelectedPayment?.payment_token = "venmo"
//                    onlinePayment(mSelectedPayment)
//                    // use the result to update your UI and send the payment method nonce to your server
//                }
//                Activity.RESULT_CANCELED -> {
//                    mBinding?.root?.onSnackbar("User Cancelled")
//                }
//                else -> {
//                    // handle errors here, an exception may be available in
//                    val error = data!!.getSerializableExtra(DropInActivity.EXTRA_ERROR) as java.lang.Exception
//                    mBinding?.root?.onSnackbar(error.stackTrace.toString())
//                }
//            }
//
//        } else if (requestCode == SADDED_PAYMENT_REQUEST) {
//            when (resultCode) {
//                Activity.RESULT_OK -> {
//                    AppToasty.success(requireContext(), getString(R.string.payment_done_successful))
//                    if (mSelectedPayment?.payName == getString(R.string.windcave) || mSelectedPayment?.payName == getString(R.string.mpaisa)) {
//                        if (data != null && data.hasExtra("paymentId"))
//                            mSelectedPayment?.keyId = data.getStringExtra("paymentId")
//                    }
//                    onlinePayment(mSelectedPayment)
//                    // use the result to update your UI and send the payment method nonce to your server
//                }
//                else -> {
//                    val message = if (data?.hasExtra("showError") == true)
//                        getString(R.string.payment_failed)
//                    else
//                        getString(R.string.payment_unsuccessful)
//                    AppToasty.error(requireContext(), message)
//                }
//            }
//
//        } else if (requestCode == MY_FATOORAH_PAYMENT_REQUEST || requestCode == TAP_PAYMENT_REQUEST || requestCode == EVALON_PAYMENT_REQUEST) {
//            when (resultCode) {
//                Activity.RESULT_OK -> {
//                    AppToasty.success(requireContext(), getString(R.string.payment_done_successful))
//                    if (data != null && data.hasExtra("paymentId"))
//                        mSelectedPayment?.keyId = data.getStringExtra("paymentId")
//
//                    onlinePayment(mSelectedPayment)
//                    // use the result to update your UI and send the payment method nonce to your server
//                }
//                else -> {
//                    val message = if (data?.hasExtra("showError") == true)
//                        getString(R.string.payment_failed)
//                    else
//                        getString(R.string.payment_unsuccessful)
//                    AppToasty.error(requireContext(), message)
//                }
//            }
//
//        } else if (requestCode == AppConstants.REQUEST_AGENT_DETAIL && resultCode == Activity.RESULT_OK) {
//            if (data == null) return
//            mAgentParam = data.getParcelableExtra("agentData")!!
//            settingAgentData(mAgentParam)
//        } else if (requestCode == SCHEDULE_REQUEST && resultCode == Activity.RESULT_OK) {
//            if (data?.hasExtra("slotDetail") == true) {
//                scheduleData = data.getParcelableExtra("slotDetail")
//                groupSchedule?.visibility = View.VISIBLE
//                btn_edit_schedule_order?.visibility = View.VISIBLE
//            } else {
//                btn_edit_schedule_order?.visibility = View.GONE
//                groupSchedule?.visibility = View.GONE
//                scheduleData = null
//            }
//
//            tvScheduleCharges?.text = getString(R.string.currency_tag, AppConstants.CURRENCY_SYMBOL, scheduleData?.supplierTimings?.firstOrNull()?.price?.toString())
//
//            calculateCartCharges(cartList)
//            /* totalAmt += (scheduleData?.supplierTimings?.firstOrNull()?.price?.toFloat() ?: 0f)
//
//             decimalFormat.roundingMode = RoundingMode.UP
//             tvNetTotal.text = AppConstants.CURRENCY_SYMBOL + decimalFormat.format(totalAmt)*/
//            /* btn_schedule_order.text = "Change Slot ${scheduleData?.supplierTimings.firstOrNull()}-${scheduleDate?.endDate}"*/
//
////            btn_schedule_order.text = "Change Slot ${scheduleDate?.startDate}-${scheduleDate?.endDate}"
//        } else if (requestCode == AppConstants.REQUEST_CART_LOGIN_BOOKING && resultCode == Activity.RESULT_OK) {
//            openBookingDateTime(false)
//        } else if (requestCode == DataNames.REQUEST_CART_LOGIN_PROMO && resultCode == Activity.RESULT_OK) {
//            checkPromoApi(etPromoCode.text.toString().trim())
//        } else if (requestCode == DataNames.REQUEST_CART_LOGIN_PRECRIPTION && resultCode == Activity.RESULT_OK) {
//            uploadImage()
//        } else if (requestCode == AppConstants.REQUEST_PAYMENT_OPTION && resultCode == Activity.RESULT_OK) {
//            mSelectedPayment = data?.getParcelableExtra("payItem")
//
//            tv_pay_option.text = mSelectedPayment?.mumybenePay ?: mSelectedPayment?.payName
//
//            if (tipList.isNotEmpty()) {
//                group_tip.visibility = View.VISIBLE
//            }
//
//            when (mSelectedPayment?.payName) {
//                getString(R.string.zelle) -> {
//                    Glide.with(activity ?: requireActivity()).load(mSelectedPayment?.keyId
//                            ?: "").into(iv_doc)
//                    group_zelle.visibility = View.VISIBLE
//                }
//                textConfig?.braintree -> {
//                    if (isNetworkConnected) {
//                        mSelectedPayment = data?.getParcelableExtra("payItem")
//                        viewModel.getBrainTreeClientToken()
//                    }
//                }
//                getString(R.string.mumybene) -> showMumyBenePhoneDialog()
//                textConfig?.cod-> {
//                    group_tip.visibility = View.GONE
//                    tvClearTip.callOnClick()
//                }
//                else -> group_zelle.visibility = View.GONE
//            }
//
//        } else if (requestCode == AppSettingsDialog.DEFAULT_SETTINGS_REQ_CODE) {
//            // Do something after user returned from app settings screen, like showing a Toast.
//            mBinding?.root?.onSnackbar(getString(R.string.returned_from_app_settings_to_activity))
//        } else if (resultCode == AppCompatActivity.RESULT_OK && requestCode == AppConstants.CameraPicker) {
//
//            if (isNetworkConnected) {
//                if (photoFile?.isRooted == true) {
//                    viewModel.uploadImage(imageUtils.compressImage(photoFile?.absolutePath
//                            ?: ""))
//                }
//            }
//        } else if (resultCode == AppCompatActivity.RESULT_OK && requestCode == AppConstants.GalleyPicker) {
//            if (data != null) {
//                if (isNetworkConnected) {
//                    //data.getData return the content URI for the selected Image
//                    val selectedImage = data.data
//                    val filePathColumn = arrayOf(MediaStore.Images.Media.DATA)
//                    // Get the cursor
//                    val cursor = activity?.contentResolver?.query(selectedImage!!, filePathColumn, null, null, null)
//                    // Move to first row
//                    cursor?.moveToFirst()
//                    //Get the column index of MediaStore.Images.Media.DATA
//                    val columnIndex = cursor?.getColumnIndex(filePathColumn[0])
//                    //Gets the String value in the column
//                    val imgDecodableString = cursor?.getString(columnIndex ?: 0)
//                    cursor?.close()
//
//                    if (imgDecodableString?.isNotEmpty() == true) {
//                        viewModel.uploadImage(imageUtils.compressImage(imgDecodableString))
//                    }
//                }
//            }
//        }
//    }
//
//    private fun settingAgentData(mAgentParam: AgentCustomParam?) {
//
//
//        tvSerDate.text = mDateTime.convertDateOneToAnother(mAgentParam?.serviceDate
//                ?: "", "yyyy-MM-dd", "EEE, dd MMM")
//        tvSerTime.text = mDateTime.convertDateOneToAnother(mAgentParam?.serviceTime
//                ?: "", "HH:mm", "hh:mm aaa")
//
//        // if (mAgentParam?.agentData?.image != null) {
//        iv_userImage.loadUserImage(mAgentParam?.agentData?.image ?: "")
//        // }
//
//        if (mAgentParam?.agentData?.name != null) {
//            tv_name.text = mAgentParam.agentData?.name ?: ""
//        }
//
//        if (mAgentParam?.agentData?.occupation != null) {
//            tv_occupation.text = mAgentParam.agentData?.occupation ?: ""
//        }
//
//        tv_total_reviews.text = getString(R.string.agent_reviews_tag, mAgentParam?.agentData?.avg_rating?.toFloat()?:0f)
//
//        group_mainlyt.visibility = View.VISIBLE
//        change_time_slot.visibility = View.GONE
//        gp_action.visibility = View.GONE
//
//    }
//
//    override fun onDeleteCart(position: Int) {
//
//        if (position == -1) return
//
//        val mCartProd = cartList?.get(position)
//        StaticFunction.removeFromCart(activity, mCartProd?.productId, mCartProd?.productAddonId
//                ?: 0)
//        dataManager.removeValue(DataNames.DISCOUNT_AMOUNT)
//
//        cartList?.removeAt(position)
//        cartAdapter?.notifyItemRemoved(position)
//
//        setCartVisibility(cartList)
//
//        manageStock(cartList)
//        calculateCartCharges(cartList)
//    }
//
//    override fun onViewProductSpecialInstruction(position: Int) {
//
//    }
//
//    override fun addItem(position: Int) {
//        if (position != -1) {
//
//            dataManager.removeValue(DataNames.DISCOUNT_AMOUNT)
//            val mCartProd = cartList?.get(position)
//
//            cartUtils.addItemToCart(mCartProd).let {
//                if (it != null) {
//                    cartAdapter?.notifyItemChanged(position)
//                    cartAdapter?.notifyDataSetChanged()
//                }
//            }
//
//            calculateCartCharges(cartList)
//        }
//    }
//
//    override fun removeItem(position: Int) {
//
//        val mCartProd = cartList?.get(position)
//
//        mCartProd.let {
//            cartUtils.removeItemToCart(mCartProd)
//        }
//
//        if (mCartProd?.quantity == 0f) {
//            cartList?.removeAt(position)
//        }
//
//        cartAdapter?.notifyItemChanged(position)
//        cartAdapter?.notifyDataSetChanged()
//
//        setCartVisibility(cartList)
//        manageStock(cartList)
//
//        calculateCartCharges(cartList)
//
//    }
//
//    override fun onClickProdDesc(position: Int) {
//
//    }
//
//    override fun onEditQuantity(updatedQuantity: Float?,position: Int) {
//
//    }
//
//    private fun extraFunctionality() {
//        if (settingData?.extra_functionality == "1") {
//            edAdditionalRemarks.hint = activity?.getString(R.string.enter_instructionc).toString()
//        }
//    }
//
//
//    private fun calculateCartCharges(cartList: MutableList<CartInfo>?) {
//
//
//        maxHandlingAdminCharges = cartList?.sumByDouble {
//            (it.price.times(it.quantity.toDouble())).plus(questionAddonPrice).times(it.handlingAdmin.div(100))
//        }?.toFloat() ?: 0.0f
//
//
//        if (settingData?.disable_tax == "1") {
//
//            maxHandlingAdminCharges = 0.0f
//        }
//
//
//        mDeliveryCharge = (if (productData?.appType == AppDataType.Ecom.type) {
//            if (settingData?.ecom_agent_module == "1")
//                this.mDeliveryCharge
//            else
//                cartList?.maxBy {
//                    it.deliveryCharges
//                }?.deliveryCharges
//                        ?: 0.0f
//        } else if (settingData?.delivery_charge_type == "1" && mDeliveryType != FoodAppType.Pickup.foodType) {
//            productData?.radius_price ?: 0.0f.plus(baseDeliveryCharges)
//        } else if (regionDeliveryCharges > 0f) {
//            regionDeliveryCharges.plus(baseDeliveryCharges)
//        } else {
//            this.mDeliveryCharge
//        })
//
//        if (productData?.appType == AppDataType.Food.type && mDeliveryType != FoodAppType.Delivery.foodType) {
//            mDeliveryCharge = 0.0f
//        }
//
//        tvDeliveryCharges.text = activity?.getString(R.string.currency_tag, AppConstants.CURRENCY_SYMBOL, Utils.getPriceFormat(mDeliveryCharge,settingData,selectedCurrency))
//
//        settingLayout(maxHandlingAdminCharges, mDeliveryCharge)
//
//    }
//
//
//    private fun checkPromoApi(length: String) {
//
//        val amount = appUtils.calculateCartTotal(cartList).plus(questionAddonPrice)
//
//        val checkPromo = CheckPromoCodeParam(length, StaticFunction.getAccesstoken(activity), amount.toString(), StaticFunction.getLanguage(activity).toString(),
//                cartList?.map { it.supplierId.toString() }, cartList?.map { it.categoryId.toString() },"")
//
//        if (isNetworkConnected) {
//            hideKeyboard()
//            mViewModel?.validatePromo(checkPromo)
//        }
//    }
//
//    private fun calculatePromo(promoData: PromoCodeModel.DataBean, cartList: MutableList<CartInfo>?, promoCode: String) {
//
//        hideKeyboard()
//        var filter_cart_total = 0f
//
//        cartList?.mapIndexed { index, cartInfo ->
//
//            filter_cart_total += if (promoData.supplierIds.isNotEmpty()) {
//                promoData.supplierIds.filter { it == cartInfo.supplierId }.sumByDouble { cartInfo.price.times(cartInfo.quantity).toDouble() }.toFloat()
//            } else {
//                promoData.categoryIds.filter { it == cartInfo.categoryId }.sumByDouble { cartInfo.price.times(cartInfo.quantity).toDouble() }.toFloat()
//            }
//        }
//
//        promoData.discountPrice = if (filter_cart_total > 0 && promoData.discountType == 0) {
//            promoData.discountPrice
//        } else {
//            filter_cart_total / 100.0f * promoData.discountPrice
//        }
//
//        if (promoData.discountPrice > 0) {
//            promoData.promoCode = promoCode
//
//            etPromoCode.setText(promoCode)
//            etPromoCode.isEnabled = false
//            tvRedeem.text = getString(R.string.remove)
//
//            dataManager.addGsonValue(DataNames.DISCOUNT_AMOUNT, Gson().toJson(promoData))
//            calculateCartCharges(cartList)
//        } else {
//            mBinding?.root?.onSnackbar(getString(R.string.no_promocode_cart))
//        }
//
//    }
//
//    override fun getBindingVariable(): Int {
//        return BR.viewModel
//    }
//
//    override fun getLayoutId(): Int {
//        return R.layout.fragment_cart_v2
//    }
//
//    override fun getViewModel(): CartViewModel {
//        mViewModel = ViewModelProviders.of(this, factory).get(CartViewModel::class.java)
//        return mViewModel as CartViewModel
//    }
//
//    override fun onUpdateCart() {
//        if (isNetworkConnected) {
//            when {
//                mSelectedPayment?.keyId == DataNames.PAYMENT_CASH.toString() || (settingData?.payment_method != "null" &&
//                        settingData?.payment_method?.toInt() ?: 0 == PaymentType.CASH.payType) || isPaymentConfirm -> {
//                    mViewModel?.generateOrder("","",appUtils,
//                            mDeliveryType,
//                            if (isPaymentConfirm) DataNames.PAYMENT_AFTER_CONFIRM else DataNames.PAYMENT_CASH,
//                            mAgentParam,
//                            "",
//                            "",
//                            redeemedAmt,
//                            imageList ?: mutableListOf(),
//                            edAdditionalRemarks.text.toString().trim(),
//                            mTipCharges,
//                            restServiceTax,
//                            mQuestionList,
//                            questionAddonPrice,
//                            productData?.appType ?: 0,
//                            mSelectedPayment
//                            , havePets,
//                            cleaner_in,
//                            etParkingInstruction.text.toString().trim(),
//                            etAreaFocus.text.toString().trim(),
//                            isPaymentConfirm,
//                            isDonate,
//                            phoneNumber,
//                            scheduleData,
//                            null, false,
//                            etClickAtNo.text.toString().trim(), "", "",
//                            "", "", totalAmtCopy,isCutleryRequired = switchCutlery?.isChecked,ipAddress = "",signature_menu_id = signature_menu_id,
//                            signature_plate_id = signature_plate_id,from_signature_menu = from_signature_menu,
//                            from_signature_plate = from_signature_plate)
//                }
//
//                mSelectedPayment?.payName == getString(R.string.razor_pay) -> {
//                    initRazorPay(mSelectedPayment)
//                }
//                mSelectedPayment?.payName == getString(R.string.paypal) -> {
//                    initPaypal(mSelectedPayment)
//                }
//                mSelectedPayment?.payName == getString(R.string.zelle) -> {
//                    onlinePayment(mSelectedPayment)
//                }
//                mSelectedPayment?.payName == getString(R.string.paystack) -> {
//                    onlinePayStack(mSelectedPayment)
//                }
//                mSelectedPayment?.payName == getString(R.string.saded) -> {
//                    getSadedPayment()
//                }
//                mSelectedPayment?.payName == getString(R.string.myFatoora) -> {
//                    initMyFatooraGateway()
//                }
//                mSelectedPayment?.payName == getString(R.string.mumybene) -> {
//                    onlinePayment(mSelectedPayment)
//                    object : CountDownTimer(60000, 1000) {
//                        override fun onTick(millisUntilFinished: Long) {
//                        }
//
//                        override fun onFinish() {
//                            tvCancel?.isEnabled = true
//                            tvCancel?.alpha = 1f
//                            tvCancel?.visibility = View.VISIBLE
//                        }
//                    }.start()
//                }
//                mSelectedPayment?.payName == getString(R.string.windcave) -> {
//                    getWinCavePaymentUrl()
//                }
//                mSelectedPayment?.payName == getString(R.string.mpaisa) -> {
//                    getMpaisaUrl()
//                }
//                mSelectedPayment?.payName == getString(R.string.tap) -> {
//                    initTapGateway()
//                }
//                mSelectedPayment?.payName == getString(R.string.elavon) -> {
//                    initEvalonGateway()
//                }
//                mSelectedPayment?.payName == getString(R.string.payhere) -> {
//                    payHerePayment()
//                }
//                mSelectedPayment?.payName == getString(R.string.wallet) -> {
//                    mViewModel?.generateOrder("","",appUtils,
//                            mDeliveryType,
//                            DataNames.PAYMENT_WALLET,
//                            mAgentParam,
//                            "",
//                            "",
//                            redeemedAmt,
//                            imageList?: mutableListOf(),
//                            edAdditionalRemarks.text.toString().trim(),
//                            mTipCharges,
//                            restServiceTax,
//                            mQuestionList,
//                            questionAddonPrice,
//                            productData?.appType ?: 0,
//                            mSelectedPayment,
//                            havePets,
//                            cleaner_in,
//                            etParkingInstruction.text.toString().trim(),
//                            etAreaFocus.text.toString().trim(),
//                            isPaymentConfirm,
//                            isDonate,
//                            phoneNumber,
//                            scheduleData,
//                            walletDiscount, false, etClickAtNo.text.toString().trim(),
//                            etClickAtNo.text.toString().trim(),
//                            "", "", "", totalAmtCopy,isCutleryRequired = switchCutlery?.isChecked,ipAddress = "",signature_menu_id = signature_menu_id,
//                            signature_plate_id = signature_plate_id,from_signature_menu = from_signature_menu,
//                            from_signature_plate = from_signature_plate)
//                }
//                else -> {
//                    if (mSelectedPayment?.addCard == true) {
//                        onlinePayment(mSelectedPayment)
//                    } else {
//                        val promoData = dataManager.getGsonValue(DataNames.DISCOUNT_AMOUNT, PromoCodeModel.DataBean::class.java)
//                        totalAmt.minus(promoData?.discountPrice ?: 0f)
//                        CardDialogFrag.newInstance(mSelectedPayment, totalAmt.toFloat()).show(childFragmentManager, "paymentDialog")
//                    }
//                }
//            }
//        }
//    }
//
//    private fun initEvalonGateway() {
//        if (isNetworkConnected) {
//            mViewModel?.evalonPayemntApi(totalAmt.toString())
//        }
//    }
//
//    private fun initTapGateway() {
//        if (isNetworkConnected) {
//
//            val signUp = prefHelper.getGsonValue(DataNames.USER_DATA, PojoSignUp::class.java)
//
//            val currency = prefHelper.getGsonValue(PrefenceConstants.CURRENCY_INF, Currency::class.java)
//
//            getApplicationContext().resources.configuration.locale.displayCountry
//
//            val hashMap = hashMapOf<String, String>("email" to signUp?.data?.email.toString(),
//                    "phone" to signUp?.data?.mobile_no.toString(),
//                    "name" to signUp?.data?.firstname.toString(),
//                    "currency" to currency?.currency_name.toString(),
//                    "country_code" to Locale.getDefault().displayCountry,
//                    "post_url" to "${prefHelper.getKeyValue(PrefenceConstants.WEBSITE_LINK, PrefenceConstants.TYPE_STRING)}/success",
//                    "redirect_url" to "${prefHelper.getKeyValue(PrefenceConstants.WEBSITE_LINK, PrefenceConstants.TYPE_STRING)}/success",
//                    "amount" to totalAmt.toString())
//
//            mViewModel?.tapPayemntApi(hashMap)
//        }
//    }
//
//    private fun onlinePayStack(mSelectedPayment: CustomPayModel?) {
//        CardDialogFrag.newInstance(mSelectedPayment, totalAmt.toFloat()).show(childFragmentManager, "paymentDialog")
//
//    }
//
//    private fun getSadedPayment() {
//        val userInfo = dataManager.getGsonValue(DataNames.USER_DATA, PojoSignUp::class.java)
//        if (isNetworkConnected) {
//            mViewModel?.getSaddedPaymentUrl(userInfo?.data?.email ?: "", userInfo?.data?.firstname
//                    ?: "", totalAmt.toString())
//        }
//    }
//
//    private fun initMyFatooraGateway() {
//        val currency = dataManager.getGsonValue(PrefenceConstants.CURRENCY_INF, Currency::class.java)
//        if (isNetworkConnected) {
//            mViewModel?.getMyFatoorahPaymentUrl(currency?.currency_name ?: "", totalAmt.toString())
//        }
//    }
//
//    override fun getSaddedPaymentSuccess(data: AddCardResponseData?) {
//        mSelectedPayment?.keyId = data?.transaction_reference
//        val intent = Intent(requireContext(), PaymentWebViewActivity::class.java).putExtra("paymentData", data)
//        startActivityForResult(intent, SADDED_PAYMENT_REQUEST)
//    }
//
//    override fun getmPaisaPaymentSuccess(data: AddCardResponseData?) {
//        mSelectedPayment?.keyId = data?.rID
//        val intent = Intent(requireContext(), PaymentWebViewActivity::class.java).putExtra("paymentData", data)
//                .putExtra("payment_gateway", getString(R.string.mpaisa))
//        startActivityForResult(intent, SADDED_PAYMENT_REQUEST)
//    }
//
//    override fun onBrainTreeTokenSuccess(clientToken: String?) {
//        mAuthorization = clientToken
//
//        try {
//            val mBraintreeFragment = BraintreeFragment.newInstance(this, mAuthorization)
//            if (ClientToken.fromString(mAuthorization) is ClientToken) {
//                DropInResult.fetchDropInResult(requireActivity() as AppCompatActivity?, mAuthorization, this)
//            }
//        } catch (e: InvalidArgumentException) {
//            AppToasty.error(requireContext(), e.message.toString())
//        }
//    }
//
//    override fun onProfileUpdate() {
//
//    }
//
//
//    override fun getMyFatoorahPaymentSuccess(data: AddCardResponseData?) {
//        val intent = Intent(requireContext(), PaymentWebViewActivity::class.java).putExtra("paymentData", data)
//                .putExtra("payment_gateway", getString(R.string.myFatoora))
//        startActivityForResult(intent, MY_FATOORAH_PAYMENT_REQUEST)
//    }
//
//    override fun onTapPayment(transaction: Transaction) {
//        activity?.launchActivity<PaymentWebViewActivity>(TAP_PAYMENT_REQUEST)
//        {
//            putExtra("payment_url", transaction.url)
//            putExtra("payment_gateway", getString(R.string.tap))
//        }
//    }
//
//    override fun onEvalonPayment(data: String) {
//
//
//        val paymentUrl = if (BuildConfig.DEBUG) {
//            NetworkConstants.EVALON_TEST_LINK
//        } else {
//            NetworkConstants.EVALON_PROD_LINK
//        }
//        activity?.launchActivity<PaymentWebViewActivity>(EVALON_PAYMENT_REQUEST)
//        {
//            putExtra("payment_url", "${paymentUrl}${URLEncoder.encode(data, "utf-8")}")
//            putExtra("payment_gateway", getString(R.string.tap))
//        }
//    }
//
//    override fun onRefreshCartError() {
//        calculateDelivery()
//    }
//
//    private fun onlinePayment(mSelectedPayment: CustomPayModel?) {
//        mViewModel?.generateOrder("","",appUtils,
//                mDeliveryType,
//                DataNames.PAYMENT_CARD,
//                mAgentParam,
//                mSelectedPayment?.keyId ?: "",
//                mSelectedPayment?.payment_token ?: "",
//                redeemedAmt,
//                imageList ?: mutableListOf(),
//                edAdditionalRemarks.text.toString().trim(),
//                mTipCharges,
//                restServiceTax,
//                mQuestionList,
//                questionAddonPrice,
//                productData?.appType ?: 0,
//                mSelectedPayment,
//                havePets,
//                cleaner_in,
//                etParkingInstruction.text.toString().trim(),
//                etAreaFocus.text.toString().trim(),
//                isPaymentConfirm,
//                isDonate,
//                phoneNumber,
//                scheduleData,
//                walletDiscount, false, "", "",
//                etClickAtNo.text.toString().trim(), "", "", totalAmtCopy,isCutleryRequired = switchCutlery?.isChecked,ipAddress = "",signature_menu_id = signature_menu_id,
//                signature_plate_id = signature_plate_id,from_signature_menu = from_signature_menu,
//                from_signature_plate = from_signature_plate)
//    }
//
//    private fun initPaypal(mSelectedPayment: CustomPayModel?) {
//        val dropInRequest: DropInRequest = DropInRequest()
//                .clientToken(mSelectedPayment?.keyId)
//        startActivityForResult(dropInRequest.getIntent(requireContext()), PAYPALREQUEST)
//    }
//
//    override fun getWindCavePaymentSuccess(data: Result?) {
//        val intent = Intent(requireContext(), PaymentWebViewActivity::class.java).putExtra("paymentData", data?.Request)
//                .putExtra("payment_gateway", getString(R.string.windcave))
//        startActivityForResult(intent, SADDED_PAYMENT_REQUEST)
//    }
//
//    private fun initRazorPay(mSelectedPayment: CustomPayModel?) {
//
//        val userInfo = dataManager.getGsonValue(DataNames.USER_DATA, PojoSignUp::class.java)
//
//        Checkout.preload(activity ?: requireContext())
//        val co = Checkout()
//
//        co.setKeyID(mSelectedPayment?.keyId)
//        co.setImage(R.mipmap.ic_launcher_new)
//
//        try {
//            val options = JSONObject()
//            // options.put("name", "JnJ's Cafe")
//            //  options.put("description", "Food Order")
//            //You can omit the image option to fetch the image from dashboard
//            // options.put("image", "https://cafejj-api.royoapps.com/clikat-buckettest/jnj.png")
//            options.put("currency", "INR")
//            options.put("amount", totalAmt.times(100))
//            options.put("payment_capture", true)
//            // options.put("order_id", orderId)
//
//            val preFill = JSONObject()
//            preFill.put("email", userInfo?.data?.email)
//            preFill.put("contact", userInfo?.data?.mobile_no)
//
//            options.put("prefill", preFill)
//
//            co.open(activity, options)
//
//        } catch (e: Exception) {
//            e.printStackTrace()
//        }
//    }
//
//
//    override fun onCartAdded(cartdata: AddtoCartModel.CartdataBean?) {
//
//        /*   val adminCharges = cartList?.sumByDouble {
//            (it.fixed_price?.times(it.quantity.toDouble()))?.times(it.handlingAdmin.div(100))
//                    ?: 0.0
//        }?.toFloat() ?: 0.0f*/
//
//        // mTotalAmt = appUtils.calculateCartTotal().toFloat().plus(mDeliveryCharge).plus(adminCharges).plus(questionAddonPrice)
//
//        if (isNetworkConnected) {
//            mViewModel?.updateCartInfo(cartList, appUtils, mDeliveryType, mDeliveryCharge, totalAmt, maxHandlingAdminCharges,
//                    productData?.deliveryMax, mQuestionList, questionAddonPrice)
//        }
//    }
//
//    override fun onOrderPlaced(data: ArrayList<Int>) {
//
//        orderId = data
//
//        dataManager.removeValue(DataNames.DISCOUNT_AMOUNT)
//        StaticFunction.clearCart(activity)
//
//        StaticFunction.sweetDialogueSuccess11(activity, getString(R.string.success), getString(R.string.succesfully_order, textConfig?.order), false, 1001
//                , this)
//    }
//
//    override fun onValidatePromo(data: PromoCodeModel.DataBean, param: CheckPromoCodeParam?) {
//
//        if (cartList?.sumByDouble {
//                    it.price.times(it.quantity).toDouble()
//                } ?: 0.0 >= data.minOrder) {
//            calculatePromo(data, cartList, etPromoCode.text.toString().trim())
//        } else {
//            mBinding?.root?.onSnackbar(getString(R.string.sorry_cart_value, "${AppConstants.CURRENCY_SYMBOL}${data.minOrder}"))
//        }
//    }
//
//
//    override fun onRefreshCart(mCartData: CartData?) {
//        regionDeliveryCharges = mCartData?.region_delivery_charge ?: 0.0f
//        //  minOrder = mCartData?.min_order
//
//        payment_gateways = mCartData?.payment_gateways
//
//        calculateTipCharges(mCartData?.tips ?: arrayListOf())
//
//        val prodList = mCartData?.result ?: listOf()
//
//        cartList?.map { cart ->
//
//            var productDta = prodList.filter {
//                it.product_id == cart.productId
//            }
//
//            if (productDta.size > 1) {
//
//                val prodlist = productDta.filter {
//                    it.discount == cart.isDiscount
//                }
//
//                productDta = prodlist
//
//            }
//
//            if (productDta.isNotEmpty()) {
//
//                productData?.distance_value = productDta.firstOrNull()?.distance_value
//                cart.distance_value = productDta.firstOrNull()?.distance_value
//                cart.avgRating = productDta.first().avg_rating
//                cart.radius_price = productDta.first().radius_price
//                cart.purchasedQuant = productDta.first().purchased_quantity
//                cart.prodQuant = productDta.first().quantity
//
//                if (productData?.appType == AppDataType.Ecom.type) {
//                    cart.deliveryCharges = productDta.first().delivery_charges ?: 0.0f
//
//                    productData?.latitude = productDta.first().latitude
//                    productData?.longitude = productDta.first().longitude
//                    productData?.radius_price = productDta.first().radius_price
//                }
//
//                cart.handlingAdmin = productDta.first().handling_admin ?: 0.0f
//
//
//                cart.add_ons?.mapIndexed { _, productAddon ->
//
//                    val mFilterAddon = productDta.first().adds_on?.findLast { it?.name == productAddon?.name }?.value?.findLast {
//                        it.type_id == productAddon?.type_id
//                    }
//                    if (mFilterAddon != null) {
//                        productAddon?.price = mFilterAddon.price ?: 0f
//                    }
//                }
//
//                cart.price = if (cart.add_ons?.isNotEmpty() == true) {
//                    cart.add_ons?.sumByDouble {
//                        it?.price?.toDouble() ?: 0.0
//                    }?.toFloat()?.plus(productDta.first().fixed_price?.toFloatOrNull() ?: 0.0f)
//                            ?: 0f
//                } else {
//                    if (cart.priceType == 1) {
//                        cart.price
//                    } else {
//                        productDta.first().fixed_price?.toFloatOrNull() ?: 0f
//                    }
//
//                }
//
//                // cartInfo.gstTotal = productDta[0].gst_price
//
//                takeIf { productDta.first().purchased_quantity == productDta.first().quantity || productDta.first().quantity == 0f }.let {
//                    cart.isQuantity = 0
//                }
//
//            } else {
//                cart.isStock = false
//                /*           StaticFunction.removeFromCart(activity, cartList?.get(index)?.productId, 0, false)
//                       cartList?.removeAt(index)*/
//            }
//        }
//
//        if (dataManager.getCurrentUserLoggedIn()) {
//            val cartListData = appUtils.getCartList()
//            cartListData.cartInfos = cartList
//            prefHelper.addGsonValue(DataNames.CART, Gson().toJson(cartListData))
//            calculateCartCharges(cartList)
//            cartAdapter?.notifyDataSetChanged()
//        }
//
//
//        calculateDelivery()
//
//    }
//
//    private fun calculateTipCharges(data: ArrayList<Int>) {
//        if (mDeliveryType == FoodAppType.Pickup.foodType || data.isEmpty()) return
//
//        group_tip.visibility = View.VISIBLE
//
//        tipList.clear()
//        tipList.addAll(data)
//        tipAdapter?.notifyDataSetChanged()
//        if (tipList.size > 0) {
//            layoutTip.visibility = View.VISIBLE
//        } else {
//            layoutTip.visibility = View.GONE
//        }
//    }
//
//
//    private fun manageStock(cartInfos: MutableList<CartInfo>?) {
//        tv_checkout.isEnabled = cartInfos?.any { it.isStock == false } != true
//    }
//
//    override fun onCalculateDistance(value: DistanceMatrix?, supplierId: Int?) {
//        val valueInKm = (value?.distance?.toFloat())?.div(1000f)
//        mDeliveryCharge = if (valueInKm?.minus(productData?.distance_value ?: 0f) ?: 0f > 0f) {
//            ((valueInKm?.minus(productData?.distance_value ?: 0f))?.times(productData?.radius_price
//                    ?: 0f) ?: 0f)
//                    .plus(baseDeliveryCharges)
//        } else {
//            valueInKm?.times(productData?.radius_price ?: 0.0f)?.plus(baseDeliveryCharges) ?: 0.0f
//        }
//
//
//        //  tvDeliveryCharges.text = getString(R.string.currency_tag, AppConstants.CURRENCY_SYMBOL, mDeliveryCharge)
//        calculateCartCharges(cartList)
//    }
//
//    override fun onAddress(data: DataBean?) {
//
//        if (data?.address?.isNotEmpty() == true) {
//
//            adrsData = (if (deliveryId.isEmpty() || deliveryId == "0") {
//                data.address?.first()
//            } else {
//                data.address?.filter { it.id.toString() == deliveryId }?.get(0)
//            }) ?: AddressBean()
//
//            adrsData.user_service_charge = data.user_service_charge
//            adrsData.preparation_time = data.preparation_time
//
//            updateAddress(adrsData)
//
//        }
//
//        baseDeliveryCharges = data?.base_delivery_charges ?: 0.0f
//        minOrder = data?.min_order
//
//        setData()
//
//    }
//
//    override fun onReferralAmt(value: Float?) {
//
//        if (value ?: 0.0f > 0) {
//            mReferralAmt = value ?: 0.0f
//            llReferral.visibility = View.VISIBLE
//
//            etReferralPoint.text = getString(R.string.referral_amt_tag, "${AppConstants.CURRENCY_SYMBOL} ${value.toString()}")
//        } else {
//            llReferral.visibility = View.GONE
//            group_referral.visibility = View.GONE
//        }
//
//    }
//
//    private fun updateAddress(adrsData: AddressBean) {
//
//        if (mDeliveryType != FoodAppType.Pickup.foodType) {
//            val addressBean = if (adrsData.address_line_1 != null)
//                "${adrsData.customer_address} , ${adrsData.address_line_1 ?: ""}"
//            else
//                "${adrsData.customer_address}"
//
//            val locUser = LocationUser((adrsData.latitude
//                    ?: 0.0).toString(), (adrsData.longitude
//                    ?: 0.0).toString(), addressBean)
//
//            dataManager.addGsonValue(DataNames.LocationUser, Gson().toJson(locUser))
//
//            dataManager.setkeyValue(DataNames.PICKUP_ID, adrsData.id.toString())
//            dataManager.addGsonValue(PrefenceConstants.ADRS_DATA, Gson().toJson(adrsData))
//            deliveryId = adrsData.id.toString()
//            tv_deliver_adrs.text = addressBean
//        }
//    }
//
//    override fun onErrorOccur(message: String) {
//        mBinding?.root?.onSnackbar(message)
//    }
//
//    override fun onSessionExpire() {
//        openActivityOnTokenExpire()
//    }
//
//    override fun onAddressSelect(adrsBean: AddressBean) {
//        adrsData = adrsBean
//
//        baseDeliveryCharges = adrsData.base_delivery_charges ?: 0.0f
//        minOrder = adrsData.min_order
//
//        if (isNetworkConnected) {
//            if (::adrsData.isInitialized)
//                viewModel.refreshCart(CartReviewParam(product_ids = cartList?.map { it.productId },
//                        latitude = adrsData.latitude ?: "", longitude = adrsData.longitude
//                        ?: ""),dataManager.getCurrentUserLoggedIn())
//        }
//
//        //   adrsData.user_service_charge= mAddressBean.user_service_charge
//        // adrsData.preparation_time= mAddressBean.preparation_time
//        updateAddress(adrsData)
//    }
//
//    override fun onDestroyDialog() {
//
//    }
//
//    override fun onSuccessListener() {
//
//        AppConstants.DELIVERY_OPTIONS = DeliveryType.DeliveryOrder.type
//
//        //   if (productData?.appType ?: 0 > 0) {
//        screenFlowBean?.app_type = AppConstants.APP_SAVED_SUB_TYPE
//        dataManager.setkeyValue(DataNames.SCREEN_FLOW, Gson().toJson(screenFlowBean))
//        // }
//
//        //  AppConstants.APP_SUB_TYPE=0
//
//        val action = CartV2Directions.actionCartv2ToMainFragment()
//        navController(this@CartV2).navigate(action)
//        activity?.startActivity(Intent(activity, UserTracking::class.java)
//                .putExtra(DataNames.REORDER_BUTTON, false).putIntegerArrayListExtra("orderId", orderId))
//    }
//
//
//    override fun onSucessListner() {
//
//    }
//
//    override fun onErrorListener() {
//
//    }
//
//    override fun paymentToken(token: String, paymentMethod: String, savedCard: SaveCardInputModel?) {
//        if (isNetworkConnected) {
//            mViewModel?.generateOrder("","",appUtils, mDeliveryType, DataNames.PAYMENT_CARD, mAgentParam, token, paymentMethod,
//                    redeemedAmt, imageList
//                    ?: mutableListOf(), edAdditionalRemarks.text.toString().trim(), mTipCharges, restServiceTax, mQuestionList, questionAddonPrice,
//                    productData?.appType
//                            ?: 0, mSelectedPayment,
//                    havePets, cleaner_in, etParkingInstruction.text.toString().trim(), etAreaFocus.text.toString().trim(),
//                    isPaymentConfirm, isDonate, phoneNumber, scheduleData, null, false, "",
//                    etClickAtNo.text.toString().trim(), "", "", "", totalAmtCopy,isCutleryRequired = switchCutlery?.isChecked,ipAddress = "",
//                    signature_menu_id = signature_menu_id,
//                    signature_plate_id = signature_plate_id,from_signature_menu = from_signature_menu,
//                    from_signature_plate = from_signature_plate)
//        }
//    }
//
//    private fun uploadImage() {
//        if (permissionFile.hasCameraPermissions(activity ?: requireContext())) {
//            if (isNetworkConnected) {
//                showImagePicker()
//            }
//        } else {
//            permissionFile.cameraAndGalleryTask(this)
//        }
//    }
//
//    override fun onRequestPermissionsResult(
//            requestCode: Int,
//            permissions: Array<String>,
//            grantResults: IntArray
//    ) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
//
//        // EasyPermissions handles the request result.
//        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this)
//    }
//
//    private fun showImagePicker() {
//        imageDialog.settingCallback(this)
//        imageDialog.show(
//                childFragmentManager,
//                "image_picker"
//        )
//    }
//
//    override fun onPdf() {
//
//    }
//
//
//    override fun onGallery() {
//        val pickIntent = Intent(Intent.ACTION_PICK)
//        pickIntent.type = "image/*"
//        startActivityForResult(pickIntent, AppConstants.GalleyPicker)
//    }
//
//    override fun onCamera() {
//        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
//            // Ensure that there's a camera activity to handle the intent
//            takePictureIntent.resolveActivity(activity?.packageManager!!)?.also {
//                // Create the File where the photo should go
//                photoFile = try {
//                    ImageUtility.filename(imageUtils)
//                } catch (ex: IOException) {
//                    // Error occurred while creating the File
//                    null
//                }
//                // Continue only if the File was successfully created
//                photoFile?.also {
//                    val photoURI: Uri = FileProvider.getUriForFile(
//                            requireContext(),
//                            activity?.packageName ?: "",
//                            it)
//
//                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
//                    startActivityForResult(takePictureIntent, AppConstants.CameraPicker)
//                }
//            }
//        }
//    }
//
//    override fun onPermissionsDenied(requestCode: Int, perms: MutableList<String>) {
//        if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
//            AppSettingsDialog.Builder(this).build().show()
//        }
//    }
//
//    override fun onPermissionsGranted(requestCode: Int, perms: MutableList<String>) {
//        if (requestCode == AppConstants.CameraGalleryPicker) {
//            if (isNetworkConnected) {
//                showImagePicker()
//            }
//        }
//    }
//
//    override fun onTipSelected(position: Int) {
//        mTipCharges = mTipCharges + tipList[position].toFloat()
//        tvTipAmount.text = getString(R.string.currency_tag, AppConstants.CURRENCY_SYMBOL, Utils.getPriceFormat(mTipCharges,settingData,selectedCurrency))
//        tvTipCharges.text = activity?.getString(R.string.currency_tag, AppConstants.CURRENCY_SYMBOL, Utils.getPriceFormat(mTipCharges,settingData,selectedCurrency))
//        calculateCartCharges(cartList)
//    }
//
//    private fun getWinCavePaymentUrl() {
//        val currency = dataManager.getGsonValue(PrefenceConstants.CURRENCY_INF, Currency::class.java)
//        val userInfo = dataManager.getGsonValue(DataNames.USER_DATA, PojoSignUp::class.java)
//        val address = if (::adrsData.isInitialized) adrsData.customer_address else null
//        if (isNetworkConnected) {
//            mViewModel?.getWindCaveUrl(String.format("%.2f", totalAmt), currency?.currency_name
//                    ?: "",
//                    userInfo?.data?.email ?: "", address)
//        }
//    }
//
//    private fun getMpaisaUrl() {
//        if (isNetworkConnected) {
//            mViewModel?.getMPaisaPaymentUrl(String.format("%.2f", totalAmt))
//        }
//    }
//
//    var phoneNumber: String? = null
//    private fun showMumyBenePhoneDialog() {
//        val dialog = Dialog(requireContext(), android.R.style.Theme_Material_Dialog_Alert)
//        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
//        dialog.setContentView(R.layout.dialog_phone_number)
//        dialog.setCancelable(false)
//        dialog.window?.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT)
//        val ivCross = dialog.findViewById<ImageView>(R.id.ivCross)
//        val editText = dialog.findViewById<EditText>(R.id.etPhoneNumber)
//        val tvSubmit = dialog.findViewById<TextView>(R.id.tvSubmit)
//        editText?.setText(phoneNumber ?: "")
//        tvSubmit?.setOnClickListener {
//            phoneNumber = editText?.text.toString()
//            if (editText?.text.toString().isNotEmpty())
//                dialog.dismiss()
//            else
//                AppToasty.error(requireContext(), getString(R.string.enter_phone))
//        }
//        ivCross?.setOnClickListener {
//            dialog.dismiss()
//        }
//        dialog.show()
//    }
//
//    private fun payHerePayment() {
//        val amt = String.format("%.2f", totalAmt)
//        val userInfo = dataManager.getGsonValue(DataNames.USER_DATA, PojoSignUp::class.java)
//        val currency = dataManager.getGsonValue(PrefenceConstants.CURRENCY_INF, Currency::class.java)
//        val req = InitRequest()
//        req.merchantId = settingData?.payhere_merchantID ?: "1214669" // Your Merchant PayHere ID
//
//        req.merchantSecret = "4fXnVC1H5Gb4TrI3duTVkS4q6fKQKxijd8MLhr17oRxi" // Your Merchant secret (Add your app at Settings > Domains & Credentials, to get this))
//
//        req.currency = currency?.currency_name // Currency code LKR/USD/GBP/EUR/AUD
//
//        req.amount = amt.toDouble() // Final Amount to be charged
//
//        req.orderId = System.currentTimeMillis().toString() // Unique Reference ID
//
//        req.itemsDescription = "Order" // Item description title
//
//        req.custom1 = "This is the custom message 1"
//        req.custom2 = "This is the custom message 2"
//        req.customer.firstName = userInfo?.data?.firstname
//        req.customer.lastName = userInfo?.data?.lastname ?: "last name"
//        req.customer.email = userInfo?.data?.email
//        req.customer.phone = userInfo?.data?.mobile_no
//        val address = if (::adrsData.isInitialized) adrsData else AddressBean()
//
//        req.customer.address.address = address.customer_address ?: ""
//        req.customer.address.city = address.address_line_1 ?: ""
//        req.customer.address.country = address.customer_address ?: ""
//        req.items.add(Item(null, "Items", 1, amt.toDouble()))
//
//        /*//Optional Params
//        req.getCustomer().getDeliveryAddress().setAddress("No.2, Kandy Road")
//        req.getCustomer().getDeliveryAddress().setCity("Kadawatha")
//        req.getCustomer().getDeliveryAddress().setCountry("Sri Lanka")
//        req.getItems().add(Item(null, "Door bell wireless", 1, totalAmt))*/
//
//        val intent = Intent(requireContext(), PHMainActivity::class.java)
//        intent.putExtra(PHConstants.INTENT_EXTRA_DATA, req)
//        PHConfigs.setBaseUrl(PHConfigs.SANDBOX_URL)
//        startActivityForResult(intent, PAYHERE_REQUEST) //unique request ID like private final static int PAYHERE_REQUEST = 11010;
//
//    }
//
//    override fun onResult(result: DropInResult?) {
//
//    }
//
//    override fun onError(exception: java.lang.Exception?) {
//    }
//
//
//    private fun launchDropIn() {
//        val dropInRequest = DropInRequest()
//                .clientToken(mAuthorization)
//                .requestThreeDSecureVerification(true)
//                .collectDeviceData(true)
//                .googlePaymentRequest(getGooglePaymentRequest())
//                .maskCardNumber(true)
//                .maskSecurityCode(true)
//                .allowVaultCardOverride(true)
//                .vaultCard(true)
//                .vaultManager(true)
//                .cardholderNameStatus(CardForm.FIELD_OPTIONAL)
//
//        startActivityForResult(dropInRequest.getIntent(requireActivity()), REQUEST_CODE_BRAINTREE)
//    }
//
//    private fun getGooglePaymentRequest(): GooglePaymentRequest? {
//        return GooglePaymentRequest()
//                .transactionInfo(TransactionInfo.newBuilder()
//                        .setTotalPrice("1.00")
//                        .setCurrencyCode("QAR")
//                        .setTotalPriceStatus(WalletConstants.TOTAL_PRICE_STATUS_FINAL)
//                        .build())
//                .emailRequired(true)
//    }
//
//
//    private fun displayResult(paymentMethodNonce: PaymentMethodNonce?, deviceData: String?) {
//
//        val mPaymentMethodType = PaymentMethodType.forType(paymentMethodNonce)
////        mPaymentMethodIcon.setImageResource(PaymentMethodType.forType(mNonce).getDrawable())
////        mPaymentMethodTitle.setText(paymentMethodNonce.typeLabel)
////        mPaymentMethodDescription.setText(paymentMethodNonce.description)
////        mPaymentMethod.setVisibility(View.VISIBLE)
////        mNonceString.setText(getString(R.string.nonce) + ": " + mNonce.getNonce())
////        mNonceString.setVisibility(View.VISIBLE)
//        var details = ""
//        when (paymentMethodNonce) {
//            is CardNonce -> {
//                val cardNonce = paymentMethodNonce
//                details = """
//                Card Last Two: ${cardNonce.lastTwo}
//
//                """.trimIndent()
//                details += """
//                3DS isLiabilityShifted: ${cardNonce.threeDSecureInfo.isLiabilityShifted}
//
//                """.trimIndent()
//                details += "3DS isLiabilityShiftPossible: " + cardNonce.threeDSecureInfo.isLiabilityShiftPossible
//            }
//            is PayPalAccountNonce -> {
//
//                details = """
//                First name: ${paymentMethodNonce.firstName}
//
//                """.trimIndent()
//                details += """
//                Last name: ${paymentMethodNonce.lastName}
//
//                """.trimIndent()
//                details += """
//                Email: ${paymentMethodNonce.email}
//
//                """.trimIndent()
//                details += """
//                Phone: ${paymentMethodNonce.phone}
//
//                """.trimIndent()
//                details += """
//                Payer id: ${paymentMethodNonce.payerId}
//
//                """.trimIndent()
//                details += """
//                Client metadata id: ${paymentMethodNonce.clientMetadataId}
//
//                """.trimIndent()
//                details += """
//                Billing address: ${formatAddress(paymentMethodNonce.billingAddress)}
//
//                """.trimIndent()
//                details += "Shipping address: " + formatAddress(paymentMethodNonce.shippingAddress)
//            }
//            is VenmoAccountNonce -> {
//
//                details = "Username: " + paymentMethodNonce.username
//            }
//            is GooglePaymentCardNonce -> {
//
//                details = """
//                Underlying Card Last Two: ${paymentMethodNonce.lastTwo}
//
//                """.trimIndent()
//                details += """
//                Email: ${paymentMethodNonce.email}
//
//                """.trimIndent()
//                details += """
//                Billing address: ${formatAddress(paymentMethodNonce.billingAddress)}
//
//                """.trimIndent()
//                details += "Shipping address: " + formatAddress(paymentMethodNonce.shippingAddress)
//            }
//        }
////        mNonceDetails.setText(details)
////        mNonceDetails.setVisibility(View.VISIBLE)
////        mDeviceData.setText("Device Data: $deviceData")
////        mDeviceData.setVisibility(View.VISIBLE)
////        mAddPaymentMethodButton.setVisibility(View.GONE)
////        mPurchaseButton.setEnabled(true)
//
////        val intent: Intent = Intent(this, CreateTransactionActivity::class.java)
////                .putExtra(CreateTransactionActivity.EXTRA_PAYMENT_METHOD_NONCE, mNonce)
////        startActivity(intent)
//
//    }
//
//
//    private fun formatAddress(address: PostalAddress?): String? {
//        return address?.recipientName + " " + address?.streetAddress + " " +
//                address?.extendedAddress + " " + address?.locality + " " + address?.region +
//                " " + address?.postalCode + " " + address?.countryCodeAlpha2
//    }
//}