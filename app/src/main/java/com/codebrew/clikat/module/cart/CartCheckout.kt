package com.codebrew.clikat.module.cart

import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatEditText
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.FileProvider
import androidx.core.os.bundleOf
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.NavOptions
import androidx.navigation.Navigation
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import ch.datatrans.payment.PaymentProcessState
import ch.datatrans.payment.android.IPaymentProcessStateListener
import ch.datatrans.payment.android.PaymentProcessAndroid
import com.braintreepayments.api.BraintreeFragment
import com.braintreepayments.api.dropin.DropInActivity
import com.braintreepayments.api.dropin.DropInRequest
import com.braintreepayments.api.dropin.DropInResult
import com.braintreepayments.api.dropin.DropInResult.DropInResultListener
import com.braintreepayments.api.exceptions.InvalidArgumentException
import com.braintreepayments.api.models.ClientToken
import com.braintreepayments.api.models.GooglePaymentRequest
import com.braintreepayments.api.models.PaymentMethodNonce
import com.bumptech.glide.Glide
import com.clevertap.android.sdk.CleverTapAPI
import com.codebrew.clikat.BR
import com.codebrew.clikat.BuildConfig
import com.codebrew.clikat.R
import com.codebrew.clikat.app_utils.*
import com.codebrew.clikat.app_utils.dialogintrface.ImageDialgFragment
import com.codebrew.clikat.app_utils.extension.*
import com.codebrew.clikat.base.BaseFragment
import com.codebrew.clikat.data.*
import com.codebrew.clikat.data.constants.AppConstants
import com.codebrew.clikat.data.constants.NetworkConstants
import com.codebrew.clikat.data.constants.PrefenceConstants
import com.codebrew.clikat.data.model.api.*
import com.codebrew.clikat.data.model.api.Currency
import com.codebrew.clikat.data.model.api.distance_matrix.DistanceMatrix
import com.codebrew.clikat.data.model.api.tap_payment.Transaction
import com.codebrew.clikat.data.model.api.vehicleDetails.VehicleData
import com.codebrew.clikat.data.model.others.*
import com.codebrew.clikat.data.preferences.PreferenceHelper
import com.codebrew.clikat.databinding.*
import com.codebrew.clikat.di.ViewModelProviderFactory
import com.codebrew.clikat.intrface.ImageCallback
import com.codebrew.clikat.modal.*
import com.codebrew.clikat.modal.other.*
import com.codebrew.clikat.modal.slots.SupplierSlots
import com.codebrew.clikat.module.bottom_navigation.OnNavigationMenuClicked
import com.codebrew.clikat.module.cart.adapter.*
import com.codebrew.clikat.module.cart.braintree.BrainTreeSettings
import com.codebrew.clikat.module.cart.schedule_order.CraveScheduleActivity
import com.codebrew.clikat.module.cart.schedule_order.CraveScheduleModel
import com.codebrew.clikat.module.cart.schedule_order.ScheduleOrder
import com.codebrew.clikat.module.dialog_adress.AddressDialogActivity
import com.codebrew.clikat.module.dialog_adress.AddressDialogFragment
import com.codebrew.clikat.module.dialog_adress.interfaces.AddressDialogListener
import com.codebrew.clikat.module.dialog_adress.newlocActivity
import com.codebrew.clikat.module.login.LoginActivity
import com.codebrew.clikat.module.new_signup.SigninActivity
import com.codebrew.clikat.module.payment_gateway.PaymentListActivity
import com.codebrew.clikat.module.payment_gateway.PaymentWebViewActivity
import com.codebrew.clikat.module.payment_gateway.dialog_card.CardDialogFrag
import com.codebrew.clikat.module.payment_gateway.savedcards.adapters.SavedCardsAdapter
import com.codebrew.clikat.module.product.product_listing.DialogListener
import com.codebrew.clikat.module.service_selection.ServSelectionActivity
import com.codebrew.clikat.module.signup.DeclarationDialog
import com.codebrew.clikat.module.signup.declaration.DeclarationDialogListener
import com.codebrew.clikat.module.user_tracking.UserTracking
import com.codebrew.clikat.preferences.DataNames
import com.codebrew.clikat.utils.DialogIntrface
import com.codebrew.clikat.utils.StaticFunction
import com.codebrew.clikat.utils.StaticFunction.onChange
import com.codebrew.clikat.utils.Utils
import com.codebrew.clikat.utils.configurations.Configurations
import com.google.android.gms.wallet.TransactionInfo
import com.google.android.gms.wallet.WalletConstants
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.paytabs.paytabs_sdk.utils.PaymentParams
import com.segment.analytics.Analytics
import com.segment.analytics.Properties
import kotlinx.android.synthetic.main.dialog_both_delivery.*
import kotlinx.android.synthetic.main.dialog_order_placed.*
import kotlinx.android.synthetic.main.fragment_cart_new.*
import kotlinx.android.synthetic.main.item_agent_list.*
import kotlinx.android.synthetic.main.layout_adrs_time.*
import kotlinx.android.synthetic.main.layout_instructions.*
import kotlinx.android.synthetic.main.layout_saved_cards.*
import kotlinx.android.synthetic.main.table_manual_bottom_dialog.view.*
import kotlinx.android.synthetic.main.toolbar_app.*
import kotlinx.android.synthetic.main.web_view.*
import okhttp3.RequestBody
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import pub.devrel.easypermissions.AppSettingsDialog
import pub.devrel.easypermissions.EasyPermissions
import retrofit2.Retrofit
import java.io.BufferedOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.lang.reflect.Type
import java.math.RoundingMode
import java.net.URLEncoder
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.text.SimpleDateFormat
import java.util.*
import java.util.Date
import javax.inject.Inject
import kotlin.collections.ArrayList
import kotlin.toString as toString1


const val CARD_PAYMENT_REQUEST = 600
const val SADDED_PAYMENT_REQUEST = 588
const val THAWANI_PAYMENT_REQUEST = 587
const val TELR_PAYMENT_REQUEST = 581
const val URWAY_PAYMENT_REQUEST = 2
const val HYPERPAY_PAYMENT_REQUEST = 582
const val MY_FATOORAH_PAYMENT_REQUEST = 589
const val TAP_PAYMENT_REQUEST = 590
const val EVALON_PAYMENT_REQUEST = 591
const val SCHEDULE_REQUEST = 593
const val REQUEST_CODE_BRAINTREE = 594
const val PAYPALREQUEST = 595
const val PAYHERE_REQUEST = 11010
const val AAMAR_PAY_PAYMENT_REQUEST = 589
const val SCHEDULE_REQUEST_DINE_IN = 596
const val RAZOR_REQUEST = 1578
const val REQUEST_PROMO_CODE = 597
const val SADAD_PAYMENT_REQUEST = 583
const val TRANS_BANK_PAYMENT_REQUEST = 584
const val PAY_MAYA_REQUEST = 585
const val CRAVE_SCHEDULE_REQUEST = 999
const val SLOT_TIME_REQUEST = 111

class CartCheckout : BaseFragment<FragmentCartNewBinding, CartViewModel>(),
        CartAdapter.CartCallback, CartNavigator, AddressDialogListener,
        SavedCardsAdapter.OnCardClickListener,
        DialogIntrface, DialogListener,
        CardDialogFrag.onPaymentListener, ImageCallback,
        EasyPermissions.PermissionCallbacks, TipAdapter.TipCallback,
        DropInResultListener, IPaymentProcessStateListener, DeclarationDialogListener {

var update_address=""
    var delivery_note = ""
    private var radioOptionButton: RadioButton? = null
    private var selectedCurrency: Currency? = null
    private var ipAddress: String = ""
    private var minimumOrderArray: ArrayList<MinimumOrderDistance>? = null
    private var weightWiseArray: ArrayList<WeightWiseData>? = null
    private var mFirebaseAnalytics: FirebaseAnalytics? = null
    private var terminologyBean: SettingModel.DataBean.Terminology? = null
    private var cartList: MutableList<CartInfo>? = null
    private var cartAdapter: CartAdapter? = null
    private var questAdapter: SelectedQuestAdapter? = null
    private var supplierBranchId: Int? = null
    private var mAuthorization: String? = null
    private var isDineInWithFood = "0"
    private var tableLoadedFromScanner = "0"
    private var agentType: Int = 0
    private var mReferralAmt: Float = 0.0f
    private var redeemedAmt: Double = 0.0
    private var isReferrale = false
    var alertDialog: AlertDialog? = null
    private var mAgentParam: AgentCustomParam? = null
    private var mDropOffDataParam: AgentCustomParam? = null

    lateinit var tipList: ArrayList<Int>
    private var adapter: SavedCardsAdapter? = null
    private var tipAdapter: TipAdapter? = null
    private var tipType: Int? = null
private var isApply=true
    //Ready Chef
    private var havePets = 0
    private var cleaner_in = 0


    @Inject
    lateinit var dataManager: DataManager

    @Inject
    lateinit var paymentUtil: PaymentUtil

    @Inject
    lateinit var appUtils: AppUtils

    @Inject
    lateinit var cartUtils: CartUtils

    @Inject
    lateinit var mDialogsUtil: DialogsUtil

    @Inject
    lateinit var mDateTime: DateTimeUtils

    @Inject
    lateinit var retrofit: Retrofit


    @Inject
    lateinit var factory: ViewModelProviderFactory

    private val productList = mutableListOf<CartInfoServer>()

    @Inject
    lateinit var prefHelper: PreferenceHelper

    private var mViewModel: CartViewModel? = null
    private var mBinding: FragmentCartNewBinding? = null
    private var paymentType = ""
    private lateinit var adrsData: AddressBean

    private var totalAmt = 0.0
    private var totalAmtCopy = 0.0

    private var mDeliveryCharge = 0.0f
    private var mFreeDelivery = 0
    private var deliveryChargesFromDistance = 0.0f

    private var mDeliveryChargeArray = mutableListOf<SuplierDeliveryCharge>()
private var is_realAddress=0
    private var deliveryId: String = ""
    private var mTipCharges = 0.0f
    private var mTipChargesCopy = 0.0f
    private var mSubTotalCopy = 0.0
    private var questionAddonPrice = 0.0f
    private var maxHandlingAdminCharges = 0.0f
    private var mQuestionList = listOf<QuestionList>()
    private var scheduleData: SupplierSlots? = null
    private var orderId = arrayListOf<Int>()

    private var bookingFlowBean: SettingModel.DataBean.BookingFlowBean? = null
    private var screenFlowBean: SettingModel.DataBean.ScreenFlowBean? = null
var resturant_lat=0.0
    var resturant_long=0.0
    private var productData: CartInfo? = null
    var user_id = ""
    var self_pickup = ""
    private var mumyBeneDialog: Dialog? = null
    var wallet_amount = ""
    var from_signature_plate = ""
    var from_signature_menu = ""
    var signature_menu_id = ""
    var signature_plate_id = ""
    var type = ""
    var is_schedule = ""
    var Schedule_time = ""

    var Schedule_time_without = ""

    /*    Pickup(1),
        Delivery(0),
        Both(2),
        DineIn(3) */
    private var mDeliveryType: Int? = null

    private var mSelectedPayment: CustomPayModel? = null
    var timings: ArrayList<TimeDataBean> = ArrayList()
    //  var mTotalAmt: Float? = null

    private var settingData: SettingModel.DataBean.SettingData? = null
    private var mMinimumAmountForDelivery = 0
    private var is_auto_apply=0
    private var promoCodes:List<PromoCodee> = ArrayList()
    @Inject
    lateinit var imageUtils: ImageUtility

    @Inject
    lateinit var permissionFile: PermissionFile

    private var photoFile: File? = null
    private val imageDialog by lazy { ImageDialgFragment() }

    private var imageList: MutableList<ImageListModel>? = null


    private var mAdapter: ImageListAdapter? = null

    private var restServiceTax = 0.0
    private var minOrder: Float? = null
    private var baseDeliveryCharges = 0.0f
    private var baseDeliveryChargesArray: ArrayList<BaseDeliveryCharges>? = null
    private var regionDeliveryCharges = 0.0f

    private var isPaymentConfirm: Boolean = false

    private var payment_gateways: ArrayList<String>? = null

    private val decimalFormat: DecimalFormat = DecimalFormat("0.00", DecimalFormatSymbols.getInstance(Locale.ENGLISH))

    private var isDonate = false

    private var phoneNumber: String? = null
    var user_car_id = ""
    private var isLoyaltyPointsApplied: Boolean? = null
    private var userSubscription: SubcripModel? = null
    private var loyaltyLevelDiscount: Float? = null
    private var loyalitPointDiscountAmount: Float? = null

    private var selectedTableData: ListItem? = null
    private var currentSupplierId: Int? = null
    private val textConfig by lazy { appUtils.loadAppConfig(0).strings }
    private val colorConfig by lazy { Configurations.colors }
    private var isDineIn = 0
    var save_card=0
    var card_empty=0
    var save_card_token=""
    var cleverTapDefaultInstance: CleverTapAPI? = null
    private var distanceMinAmt: Double? = null
    private var isMinimumAmountApplied: Boolean? = false
    private var bufferTime: Int? = 0
    private var supplierDeliveryCharges: ArrayList<SupplierDeliveryTypes>? = null
    private var supplierPerKmPrice: Float? = null
    private var order_delivery_type: Int? = null
    private var navigationListeners: OnNavigationMenuClicked? = null
    private var userLoyaltyPoints: Float? = null
    private var supplierDeliveryCompanies: ArrayList<SuppliersDeliveryCompaniesItem>? = arrayListOf()
    private var selectedDeliveryCompany: SuppliersDeliveryCompaniesItem? = null
    private var userDataModel: PojoSignUp? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.navigator = this
        EventBus.getDefault().register(this)
        imageObserver()
        cardsObserver()
        userVehicleObserver()
        cleverTapDefaultInstance = CleverTapAPI.getDefaultInstance(requireActivity())
        decimalFormat.roundingMode = RoundingMode.HALF_DOWN
        userDataModel = prefHelper.getGsonValue(DataNames.USER_DATA, PojoSignUp::class.java)

        screenFlowBean = dataManager.getGsonValue(DataNames.SCREEN_FLOW, SettingModel.DataBean.ScreenFlowBean::class.java)
        bookingFlowBean = dataManager.getGsonValue(DataNames.BOOKING_FLOW, SettingModel.DataBean.BookingFlowBean::class.java)
        terminologyBean = prefHelper.getGsonValue(PrefenceConstants.APP_TERMINOLOGY, SettingModel.DataBean.Terminology::class.java)
        settingData = dataManager.getGsonValue(DataNames.SETTING_DATA, SettingModel.DataBean.SettingData::class.java)
        selectedCurrency = dataManager.getGsonValue(PrefenceConstants.CURRENCY_INF, Currency::class.java)

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onMessageEvent(event: PaymentEvent?) {
        if (event?.resultCode == RAZOR_REQUEST) {
            if (isNetworkConnected) {
                mSelectedPayment?.keyId = event.gateway_unique_id
                mSelectedPayment?.payment_token = "razorpay"
                onlinePayment(mSelectedPayment)
            }
        } else {
            mBinding?.root?.onSnackbar(event?.message ?: "")
        }
    }


    private fun cardsObserver() {
        // Create the observer which updates the UI.
        val catObserver = Observer<List<CardData>> { resource ->
            Log.e("saveCard","SavedCards")
if(resource.size==0)
{
    card_empty=0
    mBinding!!.tvSavedCard.visibility=View.GONE
}else{
    card_empty=1
//    mBinding!!.tvSavedCard.visibility=View.VISIBLE
}
            adapter = SavedCardsAdapter(resource, appUtils)
            mBinding?.rvSavecards?.adapter = adapter
            adapter?.setCardListener(this)
        }

        // Observe the LiveData, passing in this activity as the LifecycleOwner and the observer.
        viewModel.savedCardLiveData.observe(this, catObserver)
    }
    private fun imageObserver() {
        // Create the observer which updates the UI.
        val catObserver = Observer<String> { resource ->

            imageList?.add(ImageListModel(is_imageLoad = true, image = resource, _id = ""))

            mAdapter?.submitMessageList(imageList, "cart")
        }

        // Observe the LiveData, passing in this activity as the LifecycleOwner and the observer.
        viewModel.imageLiveData.observe(this, catObserver)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnNavigationMenuClicked)
            navigationListeners = context
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mBinding = viewDataBinding
        dataManager.removeValue(DataNames.DISCOUNT_AMOUNT)
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(requireContext())
        Glide.with(this).asGif().load(R.raw.loading_gif).into(mBinding!!.imageLoader);
        tb_add.visibility = View.GONE
        tb_title.text = getString(R.string.your_vehicle)
        tb_back.setOnClickListener {
            requireActivity().finish()
        }
        mBinding?.color = colorConfig
        mBinding?.strings = textConfig
        mBinding?.currency = AppConstants.CURRENCY_SYMBOL
        mBinding?.isAgentRating = settingData?.is_agent_rating == "1"

        if (arguments?.containsKey("type") == true) {
            if (arguments?.getString("type") == "2") {

            } else if (arguments?.getString("type") == "3") {
                if (arguments?.containsKey("from_supplier") == true) {
                    signature_plate_id = arguments?.getString("from_supplier")!!
                }
                from_signature_plate = "1"
            } else {
                if (arguments?.containsKey("from_supplier") == true) {
                    signature_menu_id = arguments?.getString("from_supplier")!!
                }
                from_signature_menu = "1"
            }

        }
        imageList = mutableListOf()

        if (settingData?.extra_instructions == "1") {
            settingInstructionLayout()
        }
        if (settingData?.full_view_supplier_theme == "1" && settingData?.is_user_subscription == "1") {
            btnSubscription?.text = textConfig?.my_subscription
            tvSubscriptionText?.text = getString(R.string.subscription_text, textConfig?.my_subscription)
            groupSubscription?.visibility = View.GONE
        } else
            groupSubscription?.visibility = View.GONE

        mumyBeneDialog = mDialogsUtil.openDialogMumybene(activity ?: requireContext()) {
            mViewModel?.cancelGenerateOrder()
        }

        // tvViewAllPromos?.visibility = if (settingData?.enable_promo_code_list == "1") View.GONE else View.GONE

        arguments?.let {
            if (it.containsKey("paymentType") && it.getString("paymentType") == "cod") {
                mSelectedPayment = CustomPayModel()
                mSelectedPayment?.payName = textConfig?.cod
                mSelectedPayment?.mumybenePay = textConfig?.cod
                mSelectedPayment?.payment_token = "cod"
                tv_pay_option.text = textConfig?.cod
                mSelectedPayment?.keyId = DataNames.PAYMENT_CASH.toString()
                //  cbRequestExchange?.visibility = if (settingData?.is_coin_exchange == "1") View.VISIBLE else View.GONE
            }
        }

        btnBookTable?.setOnClickListener {
            BookATable()
        }
        tvAddExpectedTime?.setOnClickListener {
            val intent = Intent(requireActivity(), SelectSlots::class.java)
            intent.putExtra("array", timings)
            requireActivity().startActivityForResult(intent, SLOT_TIME_REQUEST)

        }

        tvEditBooking?.setOnClickListener {
            BookATable()
        }

        tvScheduleMessage?.setOnClickListener {
            val selectedId: Int = lytPaymentSelection.getCheckedRadioButtonId()
            if (selectedId == R.id.rbCreditCard) {
                paymentType = "1"
            } else if (selectedId == R.id.rbWallet) {
                paymentType = "4"
            } else if (selectedId == R.id.rbCOD) {
                paymentType = "0"
            }
            if (paymentType.equals("")) {
                mBinding?.root?.onSnackbar("Select Payment Method")
            } else {
                val intent = Intent(requireActivity(), CraveScheduleActivity::class.java)
                requireActivity().startActivityForResult(intent, CRAVE_SCHEDULE_REQUEST)
            }

        }

        btnBookTableManually?.setOnClickListener {
            if (dataManager.getCurrentUserLoggedIn()) {
                showBottomSheetDialogForManualTable()
            } else {
                appUtils.checkLoginFlow(requireActivity(), DataNames.REQUEST_CART_LOGIN)
            }
        }

        tvClearTable?.setOnClickListener {
            prefHelper.removeValue(DataNames.INVITATTON_DATA)
            prefHelper.removeValue(DataNames.SAVED_TABLE_DATA)
            group_selected_table?.visibility = View.GONE
            group_table_buttons?.visibility = View.VISIBLE
            groupSchedule?.visibility = View.GONE
            selectedTableData = null
            setTableBookingData()
            calculateDelivery()
            calculateCartCharges(cartList)
            settingToolbar()
        }

        tableRelatedContainer?.visibility = if (settingData?.is_table_booking == "1") {
            View.VISIBLE
        } else {
            View.GONE
        }

        toolbar_layout?.visibility = if (settingData?.is_skip_theme == "1" || settingData?.is_juman_flow_enable == "1") View.VISIBLE else View.GONE
        //  groupCutlery?.visibility = if (settingData?.enable_cutlery_option == "1") View.VISIBLE else View.GONE

        if (settingData?.is_juman_flow_enable == "1") {
            ivNav?.setOnClickListener {
                navigationListeners?.onNavigationMenuChanged()
            }
            ivNav?.visibility = View.VISIBLE
            tb_back?.visibility = View.GONE
//            imgNav?.setOnClickListener {
//                ivNav?.callOnClick()
//            }
        }

        tb_back?.setOnClickListener {
            Navigation.findNavController(requireView()).popBackStack()
        }
        tb_title?.text = getString(R.string.cart)

//        etPromoCode.afterTextChanged {
//            if (it.trim().isNotEmpty()) {
//                tvRedeem.isEnabled = true
//                tvRedeem.setTextColor(Color.parseColor(colorConfig.primaryColor))
//            } else {
//                tvRedeem.isEnabled = false
//                tvRedeem.setTextColor(Color.parseColor(colorConfig.textSubhead))
//            }
//        }

        tvTipCurrency?.text = getString(R.string.currency, AppConstants.CURRENCY_SYMBOL)
//
        btn_promo.setOnClickListener {
            if (totalAmt > 0.0) {
                if (btn_promo.text.toString() == getString(R.string.remove)) {

                    btn_promo.isEnabled = true
                    btn_promo.text = getString(R.string.apply)
                    etPromoCode.setText("")
                    etPromoCode.isEnabled = true
                    dataManager.removeValue(DataNames.DISCOUNT_AMOUNT)
                    mBinding?.root?.onSnackbar(getString(R.string.promo_remove))
                    calculateCartCharges(cartList)
                } else {
                    isApply=false
                    if (dataManager.getCurrentUserLoggedIn())
                        if(self_pickup.equals("9")) {
                            checkPromoApi(etPromoCode.text.toString().trim(), "1")
                        }else{
                            checkPromoApi(etPromoCode.text.toString().trim(), "")
                        }
                    else {
                        appUtils.checkLoginFlow(requireActivity(), DataNames.REQUEST_CART_LOGIN_PROMO)
                    }
                }
            }
        }
//
//        tvViewAllPromos?.setOnClickListener {
//            if (dataManager.getCurrentUserLoggedIn()) {
//                val suplierIds = ArrayList<String>()
//                cartList?.forEach {
//                    if (suplierIds.isEmpty() || suplierIds.find { it1 -> it1 != it.supplierId.toString() } == null)
//                        suplierIds.add(it.supplierId.toString())
//                }
//                startActivityForResult(Intent(requireContext(), PromoCodeListActivity::class.java).putExtra("supplierIds", suplierIds), REQUEST_PROMO_CODE)
//
//            } else {
//                appUtils.checkLoginFlow(requireActivity(), DataNames.REQUEST_CART_LOGIN_PROMO)
//            }
//        }

        tvClearTip.setOnClickListener {
            mTipCharges = 0.0f
            mTipChargesCopy = 0.0f
            etCustomTip.setText("")

            tipAdapter?.sekectedTip(-1)
            tipAdapter?.notifyDataSetChanged()

            tvTipAmount.text = getString(R.string.currency_tag, AppConstants.CURRENCY_SYMBOL, Utils.getPriceFormat(mTipCharges, settingData, selectedCurrency))
            tvTipCharges.text = activity?.getString(R.string.currency_tag, AppConstants.CURRENCY_SYMBOL, Utils.getPriceFormat(mTipCharges, settingData, selectedCurrency))
            calculateCartCharges(cartList)
        }

        cardView2.setOnClickListener {
            if (dataManager.getCurrentUserLoggedIn()) {
                dataManager.getKeyValue(DataNames.FEATURE_DATA, PrefenceConstants.TYPE_STRING).toString1().let {
                    val listType: Type = object : TypeToken<ArrayList<SettingModel.DataBean.FeatureData?>?>() {}.type
                    val featureList: ArrayList<SettingModel.DataBean.FeatureData> = Gson().fromJson(it, listType)

                    activity?.launchActivity<PaymentListActivity>(AppConstants.REQUEST_PAYMENT_OPTION) {
                        putParcelableArrayListExtra("feature_data", featureList)
                        putExtra("mSelectPayment", payment_gateways)
                        putExtra("mTotalAmt", totalAmt.toFloat())
                    }
                }
            } else {
                appUtils.checkLoginFlow(requireActivity(), DataNames.REQUEST_CART_LOGIN)
            }
        }

        tvRedeemed.setOnClickListener {
            if (tvRedeemed.text.toString() == getString(R.string.remove)) {
                isReferrale = false
                redeemedAmt = 0.0
                group_referral.visibility = View.GONE
                tvRedeemed.text = getString(R.string.apply)
                calculateCartCharges(cartList)
            } else {
                isReferrale = true
                tvRedeemed.text = getString(R.string.remove)
                calculateCartCharges(cartList)
                group_referral.visibility = View.VISIBLE
            }
        }
        btnSubscription?.setOnClickListener {
            if (dataManager.getCurrentUserLoggedIn()) {
                navController(this@CartCheckout).navigate(R.id.action_cart_to_subscriptionFrag)
            } else {
                appUtils.checkLoginFlow(requireActivity(), DataNames.REQUEST_CART_LOGIN)
            }
        }


        tvApplyLoyalty.setOnClickListener {
            if (tvApplyLoyalty.text.toString() == getString(R.string.remove)) {
                isLoyaltyPointsApplied = false
                tvApplyLoyalty.text = getString(R.string.apply)
                loyaltyLevelDiscount = loyaltyLevelDiscount?.minus(loyalitPointDiscountAmount
                        ?: 0f)
                calculateCartCharges(cartList)
            } else {
                if (totalAmt > 0.0) {
                    if (settingData?.enable_min_loyality_points == "1" && settingData?.min_loyalty_points_to_redeem != null && userLoyaltyPoints != null
                            && settingData?.min_loyalty_points_to_redeem ?: 0f > userLoyaltyPoints ?: 0f) {
                        AppToasty.error(requireContext(), getString(R.string.min_loyaty_points, settingData?.min_loyalty_points_to_redeem?.toString()))
                    } else {
                        isLoyaltyPointsApplied = true
                        loyaltyLevelDiscount = loyaltyLevelDiscount?.plus(loyalitPointDiscountAmount
                                ?: 0f)
                        tvApplyLoyalty.text = getString(R.string.remove)
                        calculateCartCharges(cartList)
                    }

                }
            }
        }
        cartList = appUtils.getCartList().cartInfos

        if (prefHelper.getKeyValue(AppConstants.ROAD_MAP_TYPE, PrefenceConstants.TYPE_STRING) == AppConstants.ROAD_MAP_TYPE_PICK_UP)
            mDeliveryType = 1
        else
            mDeliveryType = 0

        if (cartList?.isNotEmpty() == true) {
            productData = cartList?.first()
        }

        setTableData()
        setTableBookingData()

        supplierBranchId = productData?.suplierBranchId ?: 0
        currentSupplierId = productData?.supplierId ?: 0

        if (isNetworkConnected) {
            if (mDateTime.checkWeekOfMonday() && !dataManager.isSubcriptionEnded() && dataManager.getCurrentUserLoggedIn()) {
                // viewModel.checkUserSubsc(dataManager.getUserSubscData())
                viewFlipper.displayedChild = 0
                settingToolbar()
            } else if (dataManager.isSubcriptionEnded()) {
                viewFlipper.displayedChild = 1
            } else {
                viewFlipper.displayedChild = 0
                settingToolbar()
            }
        }

        settingAdrs()
        initTipAdapter()
        extraFunctionality()

        if (productData?.appType == AppDataType.HomeServ.type) {

            if (settingData?.enable_freelancer_flow == "1" && productData?.serviceAgentDetail != null) {
                mAgentParam = productData?.serviceAgentDetail
                settingAgentData(mAgentParam)
            }

            if (cartList?.any { it.is_appointment == "1" }!!)
                change_time_slot.visibility = View.GONE

            cnst_service_selc.visibility = View.VISIBLE
            if (settingData?.is_laundry_theme == "1")
                change_time_slot?.text = getString(R.string.choose_booking_pickup_time_slot)

            gp_action?.visibility = View.GONE
            if (mAgentParam != null) {
                tv_change_agent.visibility = View.GONE
                group_mainlyt.visibility = View.VISIBLE
            } else {
                tv_change_agent.visibility = View.VISIBLE
                group_mainlyt.visibility = View.GONE
            }
        } else {
            cnst_service_selc.visibility = View.GONE
        }


        change_time_slot.setOnClickListener {
            openBookingDateTime(isAgent = false, isDeliveryDateTime = false)
        }

        tv_change_agent.setOnClickListener {
            openBookingDateTime(isAgent = true, isDeliveryDateTime = false)
        }

        change_delivery_time_slot.setOnClickListener {
            openBookingDateTime(isAgent = false, isDeliveryDateTime = true)
        }

        tv_change_delivery_agent?.setOnClickListener {
            openBookingDateTime(isAgent = true, isDeliveryDateTime = true)
        }

        tv_checkout?.text = textConfig?.order_now
        tv_pay_option?.text = textConfig?.choose_payment

        //      btn_donate_someone?.text = if (!textConfig?.donate_to_someone.isNullOrEmpty()) textConfig?.donate_to_someone else getString(R.string.donate_to_someone)

        if (settingData?.payment_method != "null" && settingData?.payment_method?.toInt() ?: 0 == PaymentType.CASH.payType) {
            tv_pay_option.text = textConfig?.cod
            cardView2.isEnabled = false
            group_tip.visibility = View.GONE
            //          cbRequestExchange?.visibility = if (settingData?.is_coin_exchange == "1") View.VISIBLE else View.GONE
        } else {
            cardView2.isEnabled = true
        }

        if (AppConstants.APP_SUB_TYPE > AppDataType.Custom.type) {
            //  tv_deliver_adrs?.setTextColor(ContextCompat.getColor(requireContext(), R.color.grey))
            //  deliver_text?.setTextColor(ContextCompat.getColor(requireContext(), R.color.grey))
            //    viewBackground.setBackgroundColor(ContextCompat.getColor(requireActivity(), R.color.white))
            //  ivChange?.visibility = View.VISIBLE
            //    tv_change_adrs?.visibility = View.GONE
            //   viewBottom?.visibility = View.VISIBLE
            //   ivLocation?.visibility = View.VISIBLE
            //          etPromoCode?.setHintTextColor(ContextCompat.getColor(requireContext(), R.color.black_80))
            //          etPromoCode?.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_coupon, 0, 0, 0)
        } else {
            //   viewBottom?.visibility = View.GONE
//            viewBackground.setBackgroundColor(Color.parseColor(colorConfig.toolbarColor))
            //   tv_deliver_adrs?.setTextColor(Color.parseColor(colorConfig.toolbarText))
            // deliver_text?.setTextColor(Color.parseColor(colorConfig.toolbarText))
            //        etPromoCode?.setHintTextColor(Color.parseColor(colorConfig.primaryColor))
        }

        btn_schedule_order.setOnClickListener {
            bookOrder(true)
        }

//        btn_edit_schedule_order.setOnClickListener {
//            activity?.launchActivity<ScheduleOrder>(SCHEDULE_REQUEST) {
//                putExtra("supplierId", cartList?.firstOrNull()?.supplierId.toString1())
//                if (::adrsData.isInitialized) {
//                    putExtra("latitude", adrsData.latitude)
//                    putExtra("longitude", adrsData.longitude)
//                }
//            }
//        }

//        tvSelectDeliveryCompany?.setOnClickListener {
//            showDeliveryCompaniesPopup()
//        }
        btn_retry.setOnClickListener {
            if (isNetworkConnected) {
                viewFlipper.displayedChild = 0
                settingToolbar()
                // viewModel.checkUserSubsc(dataManager.getUserSubscData())
            }
        }

        scrollView.setOnTouchListener(object : View.OnTouchListener {
            override fun onTouch(v: View?, event: MotionEvent?): Boolean {
                hideKeyboard()
                return false
            }
        })
        //    groupVehicleNumber?.visibility = if (settingData?.enable_user_vehicle_number == "1" && mDeliveryType == FoodAppType.Pickup.foodType) View.VISIBLE else View.GONE
        groupLoyalty?.visibility = if (settingData?.is_loyality_enable == "1") View.GONE else View.GONE

        /*      if (BuildConfig.CLIENT_CODE == "skipp_0631")
                  txtRestService.text = getString(R.string.service_charge)*/

        val address = dataManager.getGsonValue(PrefenceConstants.ADRS_DATA, AddressBean::class.java)
        adrsData=address!!
        address.let {
            dataManager.setkeyValue(DataNames.PICKUP_ID, it!!.id.toString())
            //dataManager.addGsonValue(PrefenceConstants.ADRS_DATA, Gson().toJson(it))
            deliveryId = it!!.id.toString()
            is_realAddress=address!!.is_real_address
            tvAddress.text = appUtils.getAddressFormat(address)
        }

    }

    private fun showDeliveryCompaniesPopup() {
        // setup the alert builder
        val builder = AlertDialog.Builder(context)
        builder.setTitle(getString(R.string.select_delivery_company))
        var position: Int? = 0
        val arrayAdapter = ArrayAdapter<SuppliersDeliveryCompaniesItem?>(requireContext(), android.R.layout.simple_list_item_single_choice)
        arrayAdapter.addAll(supplierDeliveryCompanies ?: arrayListOf())

        builder.setSingleChoiceItems(arrayAdapter, 0) { dialog, which ->
            position = which
        }
        builder.setPositiveButton(getString(R.string.ok)) { dialog, which ->
            selectedDeliveryCompany = arrayAdapter.getItem(position ?: 0)
            //    clSelectedDeliveryCompany?.visibility = View.VISIBLE
            //   tvCompanyName?.text = getString(R.string.delivery_company, selectedDeliveryCompany?.name)
            baseDeliveryCharges = selectedDeliveryCompany?.baseDeliveryCharges ?: 0f

            calculateDelivery()
            dialog?.dismiss()
        }
        builder.setNegativeButton(getString(R.string.cancel), null)
        val dialog = builder.create()
        dialog.show()
    }

    private fun checkDropOffButton() {
        if (settingData?.is_laundry_theme == "1") {
            txtSerDate?.text = getString(R.string.pickup_date)

            cnst_service_delivery_selc.visibility = View.VISIBLE
            if (mDropOffDataParam != null) {
                tv_change_delivery_agent.visibility = View.GONE
                group_mainlyt_delivery.visibility = View.VISIBLE
            } else {
                tv_change_delivery_agent.visibility = View.VISIBLE
                group_mainlyt_delivery.visibility = View.GONE
            }
        } else {
            cnst_service_delivery_selc.visibility = View.GONE
        }
    }

    override fun onViewProductSpecialInstruction(position: Int) {
        if (position == -1) return

        val binding = DataBindingUtil.inflate<SpecialInstructionDialogBinding>(LayoutInflater.from(requireActivity()), R.layout.special_instruction_dialog, null, false)
        binding.color = colorConfig
        val dialog = mDialogsUtil.showDialog(requireActivity(), binding.root)

        val tvHeader = dialog.findViewById<TextView>(R.id.tvHeader)
        val btnSave = dialog.findViewById<MaterialButton>(R.id.btnSave)
        val etInstruction = dialog.findViewById<AppCompatEditText>(R.id.etInstruction)
        val tvRemainingLength = dialog.findViewById<AppCompatTextView>(R.id.tvRemainingLength)

        val mCartProd = cartList?.get(position)?.copy()


        if (mCartProd?.productSpecialInstructions.isNullOrEmpty())
            tvHeader.text = getString(R.string.add_instructions_)
        else {
            etInstruction.setText(mCartProd?.productSpecialInstructions)
            tvHeader.text = getString(R.string.edit_instructions)
        }

        etInstruction.onChange {
            if (it.isNotEmpty()) {
                val remainingLength = 500 - it.length
                tvRemainingLength.text = "$remainingLength"
            } else
                tvRemainingLength.text = getString(R.string.five_zero_zero)
        }

        btnSave.setOnClickListener {
            mCartProd?.let {

                mCartProd.productSpecialInstructions = etInstruction.text!!.toString().trim()
                cartList?.set(position, mCartProd.copy())
                cartAdapter?.notifyItemChanged(position)
                cartAdapter?.notifyDataSetChanged()
                StaticFunction.updateCartInstructions(requireContext(), mCartProd.productId, mCartProd.productSpecialInstructions)

            }


            dialog.dismiss()
        }

        dialog.show()

    }


    private fun BookATable() {
        if (dataManager.getCurrentUserLoggedIn()) {
            activity?.launchActivity<ScheduleOrder>(SCHEDULE_REQUEST_DINE_IN) {
                putExtra("supplierId", cartList?.firstOrNull()?.supplierId.toString1())
                putExtra("dineIn", true)
                if (::adrsData.isInitialized) {
                    putExtra("latitude", adrsData.latitude)
                    putExtra("longitude", adrsData.longitude)
                }
            }
        } else {
            appUtils.checkLoginFlow(requireActivity(), DataNames.REQUEST_CART_LOGIN)
        }
    }

    private fun setTableData() {
        val slotsData = prefHelper.getGsonValue(DataNames.SAVED_TABLE_DATA, SupplierSlots::class.java)
        if (slotsData != null && isDineIn == 1) {
            scheduleData = slotsData
            selectedTableData = slotsData.selectedTable

            mBinding?.tvPayNow?.text = getString(R.string.order_food_and_book_table)
            mBinding?.btnScheduleOrder?.visibility = View.GONE
        } else
            prefHelper.removeValue(DataNames.SAVED_TABLE_DATA)
    }

    private fun setTableBookingData() {
        if (settingData?.is_table_booking == "0" || settingData?.is_table_booking.isNullOrEmpty() || isDineIn == 0) {
            group_table_buttons?.visibility = View.GONE
            group_selected_table?.visibility = View.GONE
            return
        }

        var haveTableData = false
        if (scheduleData != null) {
            /** this section for selected date and time of slot */
            val formattedDate = appUtils.convertDateOneToAnother(scheduleData?.startDateTime.toString1(),
                    "yyyy-MM-dd HH:mm:ss",
                    "EEE, dd MMMM hh:mm aaa") ?: ""
            tvCartTableDateTime?.text = formattedDate
            haveTableData = true
        }

        if (selectedTableData != null) {
            /** this section for selected table if table is not bypassed */
            val tableInfo = "${selectedTableData?.tableName} \n${
                getString(R.string.seating_capacity,
                        selectedTableData?.seatingCapacity.toString1())
            }"
            tvCartTableNameCapacity?.text = tableInfo
            tvCartTableNumber?.text = getString(R.string.table_number, selectedTableData?.tableNumber.toString1())
            haveTableData = true
        } else {
            val tableInfo = "Table Name: NA \n${
                getString(R.string.seating_capacity, "NA")
            }"
            tvCartTableNameCapacity?.text = tableInfo
            tvCartTableNumber?.text = getString(R.string.table_number, "NA")
        }

        if (!haveTableData) {
            /** this section for "scan" / "invitation" / "add item from booked table list" */
            val currentTableData = prefHelper.getCurrentTableData()
            if (currentTableData != null) {
                haveTableData = true
                if (currentTableData.isInvitation == "1" && currentTableData.isScanned == "0") {
                    val tableInfo = "${currentTableData.table_name} \n${
                        getString(R.string.seating_capacity, currentTableData.capacity.toString1())
                    }"

                    tvCartTableNameCapacity?.text = tableInfo
                    tvCartTableNumber?.text = getString(R.string.table_number, currentTableData.table_number.toString1())
                    val formattedDate = appUtils.convertDateOneToAnother(currentTableData.date.toString1(),
                            "yyyy-MM-dd HH:mm:ss",
                            "EEE, dd MMMM hh:mm aaa") ?: ""
                    tvCartTableDateTime?.text = formattedDate
                    selectedTableData = ListItem()
                    selectedTableData?.id = currentTableData.table_id?.toInt()
                }

                if (currentTableData.isInvitation == "0" && currentTableData.isScanned == "1") {
                    val tableInfo = "${currentTableData.table_name} \n${
                        getString(R.string.seating_capacity,
                                currentTableData.capacity.toString1())
                    }"
                    tvCartTableNameCapacity?.text = tableInfo
                    tvCartTableNumber?.text = getString(R.string.table_number, currentTableData.table_number.toString1())
                    selectedTableData = ListItem()
                    selectedTableData?.id = currentTableData.table_id?.toInt()
                }


                if (currentTableData.isInvitation == "0" && currentTableData.isScanned == "0") {
                    /** check if data is loaded from booking list page */

                    val formattedDate = appUtils.convertDateOneToAnother(currentTableData.date.toString1(),
                            "yyyy-MM-dd HH:mm:ss", "EEE, dd MMMM hh:mm aaa") ?: ""
                    tvCartTableDateTime?.text = formattedDate

                    if ((currentTableData.table_id != null && currentTableData.table_id != "null" &&
                                    currentTableData.table_number != null && currentTableData.table_number != "null" &&
                                    currentTableData.table_name != null && currentTableData.table_name != "null")) {
                        /** check if booking was made without table */
                        selectedTableData = ListItem()
                        selectedTableData?.id = currentTableData.table_id?.toInt()

                        val tableInfo = "${currentTableData.table_name} \n${
                            getString(R.string.seating_capacity,
                                    currentTableData.capacity.toString1())
                        }"
                        tvCartTableNameCapacity?.text = tableInfo
                        tvCartTableNumber?.text = getString(R.string.table_number, currentTableData.table_number.toString1())
                        haveTableData = true
                    }
                }
                tableLoadedFromScanner = currentTableData.isScanned ?: "0"
            }
        }

        if (haveTableData) {
            isDineInWithFood = "1"
            mDeliveryType = FoodAppType.DineIn.foodType
            //     adrsLyt?.visibility = View.GONE
            group_selected_table?.visibility = View.VISIBLE
            group_table_buttons?.visibility = View.GONE
        } else {
//            mDeliveryType = productData?.deliveryType
            //   adrsLyt?.visibility = View.VISIBLE
            group_selected_table?.visibility = View.GONE
            group_table_buttons?.visibility = View.GONE
        }

    }

    private fun showBottomSheetDialogForManualTable() {
        val mBottomSheetDialog = BottomSheetDialog(requireActivity())
        val sheetView: View = layoutInflater.inflate(R.layout.table_manual_bottom_dialog, null)
        mBottomSheetDialog.setContentView(sheetView)

        mViewModel?.tableNumberObserver?.observe(requireActivity(), Observer {
            if (it.isNotEmpty()) {
                val itemModel = it[0]
                selectedTableData = ListItem()
                selectedTableData?.id = itemModel?.id
                selectedTableData?.seatingCapacity = itemModel?.seatingCapacity
                selectedTableData?.tableName = itemModel?.tableName
                selectedTableData?.tableNumber = itemModel?.tableNumber
                mBottomSheetDialog.dismiss()
            } else {
                sheetView.verifyTable?.isEnabled = true
                Handler().postDelayed({
                    sheetView.etTableNumber?.text?.clear()
                    sheetView.verifyTable?.isEnabled = true
                    sheetView.verifyTable?.text = "Verify"
                }, 2000)
                sheetView.verifyTable?.text = "Please try another table number."
            }
            setTableBookingData()
            calculateDelivery()
            calculateCartCharges(cartList)
            settingToolbar()
        })

        sheetView.verifyTable?.setOnClickListener {
            val tableNumber = sheetView.etTableNumber?.text.toString1().trim()
            if (tableNumber.isEmpty()) {

                return@setOnClickListener
            }
            val requestHolder = hashMapOf(
                    "table_number" to tableNumber,
                    "supplier_id" to currentSupplierId?.toString()
            )
            sheetView.verifyTable?.isEnabled = false
            sheetView.verifyTable?.text = "Please wait. Checking..."

            mViewModel?.verifyTableNumber(requestHolder)
        }

        mBottomSheetDialog.show()
    }

    private fun openBookingDateTime(isAgent: Boolean, isDeliveryDateTime: Boolean) {

        if (dataManager.getCurrentUserLoggedIn())
            activity?.launchActivity<ServSelectionActivity>(AppConstants.REQUEST_AGENT_DETAIL)
            {
                putExtra(DataNames.SUPPLIER_BRANCH_ID, supplierBranchId)
                putExtra("productIds", cartList?.map { it.productId.toString() }?.toTypedArray())
                if (isAgent) {
                    if (isDeliveryDateTime)
                        putExtra("mAgentData", mDropOffDataParam)
                    else
                        putExtra("mAgentData", mAgentParam)
                    putExtra("isAgent", isAgent)
                }
                putExtra("duration", cartList?.filter { it.serviceType == 0 }?.sumBy {
                    it.serviceDuration.times(it.quantity.toInt())
                })

                putExtra("screenType", "order")
                if (settingData?.is_laundry_theme == "1" && isDeliveryDateTime) {
                    putExtra("isDeliveryDateTime", isDeliveryDateTime)
                }
                if (settingData?.enable_freelancer_flow == "1" && productData?.appType == AppDataType.HomeServ.type) {
                    putExtra("serviceData", ProductDataBean(netPrice = productData?.price))
                }
            }
        else {
            appUtils.checkLoginFlow(requireActivity(), AppConstants.REQUEST_CART_LOGIN_BOOKING)
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onResultReceived(result: ListItem?) {
        selectedTableData = result
        isDineInWithFood = "1"
        setTableBookingData()
        settingToolbar()
    }


    override fun onDestroy() {
        super.onDestroy()
        EventBus.getDefault().unregister(this)
        viewModel.compositeDisposable.clear()
    }


    private fun settingToolbar() {

        if (cartList?.any { it.is_appointment == "1" }!!) {
            //  clChange?.visibility = View.GONE
            mDeliveryType = FoodAppType.Pickup.foodType
        }


        if (dataManager.getCurrentUserLoggedIn() && cartList?.isNotEmpty() == true) {
            CommonUtils.setBaseUrl(BuildConfig.BASE_URL, retrofit)
            user_id = prefHelper.getKeyValue(PrefenceConstants.USER_ID, PrefenceConstants.TYPE_STRING)?.toString()!!
            self_pickup = if (prefHelper.getKeyValue(AppConstants.ROAD_MAP_TYPE, PrefenceConstants.TYPE_STRING)!!.equals("Delivery") || prefHelper.getKeyValue(AppConstants.ROAD_MAP_TYPE, PrefenceConstants.TYPE_STRING)!!.equals("Crave Mania") || prefHelper.getKeyValue(AppConstants.ROAD_MAP_TYPE, PrefenceConstants.TYPE_STRING)!!.equals("")) {
                "8"
            } else {
                "9"

            }
            if (self_pickup.equals("8") || self_pickup.equals("")) {
                rbCOD.visibility = View.VISIBLE
                viewModel.getAddressList(cartList?.firstOrNull()?.suplierBranchId ?: 0)
            } else {
                rbCOD.visibility = View.GONE
                viewModel.getAddressList(cartList?.firstOrNull()?.suplierBranchId ?: 0)

            }
        } else {
            setData()
        }


    }

    private fun userVehicleObserver() {

        // Create the observer which updates the UI.
        val vehicleObserver = Observer<List<VehicleData>> { resource ->
            tvVechicle.text = "Name : " + resource.get(0).name.toString() + "\nVehicle Color :" + resource.get(0).color + "\nVehicle Number :" + resource.get(0).number_plate.toString()
            user_car_id = resource.get(0).id.toString()

        }

        // Observe the LiveData, passing in this activity as the LifecycleOwner and the observer.
        viewModel.liveUserDeatil.observe(this, vehicleObserver)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        EventBus.getDefault().unregister(this)
    }

    private fun bookOrder(isSchedule: Boolean) {
        if (isNetworkConnected) {
            if (dataManager.getCurrentUserLoggedIn()) {
                if (mSelectedPayment?.payName == getString(R.string.mumybene) && phoneNumber.isNullOrEmpty()) {
                    mDialogsUtil.showMumyBenePhone(activity ?: requireContext(), phoneNumber
                            ?: "", payName = mSelectedPayment?.mumybenePay ?: "") {
                        phoneNumber = it
                    }

                }/*else if (mSelectedPayment?.payName == getString(R.string.text_braintree)) {
                    if (mAuthorization.isNullOrEmpty()) {
                        AppToasty.error(requireContext(), getString(R.string.braintree_token_error))
                        return
                    }
                    launchDropIn()
                }*/ else {
                    val orderCharges = appUtils.calculateCartTotal(cartList)
                    if (settingData?.enable_min_order_distance_wise == "1" && distanceMinAmt != null && distanceMinAmt ?: 0.0 > orderCharges) {
                        isMinimumAmountApplied = true
                        showMinOrderAlert(distanceMinAmt?.toFloat() ?: 0f)
                    } else if (isMinimumAmountApplied == false && minOrder != null && ((minOrder?.toDouble()
                                    ?: 0.0) > orderCharges))
                        showMinOrderAlert(minOrder ?: 0f)
                    else
                        handleBothDelivery(isSchedule)
                }
            } else {
                if (settingData?.user_register_flow != null && settingData?.user_register_flow == "1") {
                    startActivityForResult(Intent(requireContext(), SigninActivity::class.java), DataNames.REQUEST_CART_LOGIN)
                } else {
                    startActivityForResult(Intent(requireContext(), LoginActivity::class.java), DataNames.REQUEST_CART_LOGIN)
                }
            }
        }
    }

    private fun settingAdrs() {
        tvAddVechicle.setOnClickListener {
            if (dataManager.getCurrentUserLoggedIn()) {
                AddressDialogFragment.newInstance(productData?.suplierBranchId
                        ?: 0, user_id?.toInt() ?: 0).show(childFragmentManager, "dialog")
            } else {
                appUtils.checkLoginFlow(requireActivity(), AppConstants.REQUEST_ADDRESS_ADD)
            }
        }

        tvAddAddress.setOnClickListener {
            if (dataManager.getCurrentUserLoggedIn()) {
                val intent = Intent(requireActivity(), AddressDialogActivity::class.java)
                intent.putExtra("user_id", 0)
                    intent.putExtra("branchId", supplierBranchId)
                intent.putExtra("resturant_lat", resturant_lat)
                intent.putExtra("resturant_long", resturant_long)
                startActivityForResult(intent, 157)
            } else {
                appUtils.checkLoginFlow(requireActivity(), AppConstants.REQUEST_ADDRESS_ADD)
            }
        }

        tvPayNow.visibility = if (productData?.isOpen == true) View.VISIBLE else View.GONE

//        ivChange.setOnClickListener {
//        //    tv_change_adrs.callOnClick()
//        }

        lytPaymentSelection.setOnCheckedChangeListener(object : RadioGroup.OnCheckedChangeListener {
          override  fun onCheckedChanged(group: RadioGroup?, checkedId: Int) {
             if(checkedId== R.id.rbCreditCard)
             {
                 if(card_empty==1) {
//                     mBinding!!.tvSavedCard.visibility = View.VISIBLE
//                     mBinding!!.rvSavecards.visibility = View.VISIBLE
                 }
                 }else if (checkedId == R.id.rbWallet) {
                 mBinding!!.tvSavedCard.visibility=View.GONE
                 mBinding!!.rvSavecards.visibility=View.GONE
             } else if (checkedId == R.id.rbCOD) {
                 mBinding!!.tvSavedCard.visibility=View.GONE
                 mBinding!!.rvSavecards.visibility=View.GONE
             }
            }
        })


        tvPayNow.setOnClickListener {

                delivery_note = etNote.text.toString()
                val selectedId: Int = lytPaymentSelection.getCheckedRadioButtonId()
                if (selectedId == R.id.rbCreditCard) {
//                save_card=1
                    paymentType = "1"
                } else if (selectedId == R.id.rbWallet) {
                    save_card = 0
                    paymentType = "4"
                } else if (selectedId == R.id.rbCOD) {
                    save_card = 0
                    paymentType = "0"
                }
                if (paymentType.equals("1") && card_empty == 1) {
//                    if (save_card == 1) {
                        paymentType = "1"
                        tv_checkout.callOnClick()
//                    } else {
//                        mBinding?.root?.onSnackbar("Choose card first")
//                    }
                } else {
                    if (paymentType.equals("")) {
                        mBinding?.root?.onSnackbar("Select Payment Method")
                    } else {
                        tv_checkout.callOnClick()
                    }
                }


        }
        tv_checkout.setOnClickListener {
            if (BuildConfig.CLIENT_CODE == "skipp_0631") {
//                if (edAdditionalRemarks.text.toString().trim().isEmpty()) {
//                    mBinding?.root?.onSnackbar(getString(R.string.please_enter_car_number))
//                    return@setOnClickListener
//                }
            }
            bookOrder(false)
        }
//        btn_donate_someone.setOnClickListener {
//            if (::adrsData.isInitialized) {
//                openDonateDialog()
//            }
//        }
        val idForInvoice = prefHelper.getKeyValue(PrefenceConstants.ID_FOR_INVOICE, PrefenceConstants.TYPE_STRING)?.toString()
//        groupIdForInvoice?.visibility=if(settingData?.enable_id_for_invoice_in_profile=="1" && idForInvoice.isNullOrEmpty()) View.VISIBLE else View.GONE
//
//        etIdForInvoice?.afterTextChanged {
//            tvDone?.visibility =if(it.isEmpty())
//               View.GONE else View.VISIBLE
//        }
//        tvDone?.setOnClickListener {
//            hideKeyboard()
//            editProfile()
//        }

//        cbSkipPayment?.setOnCheckedChangeListener{ compoundButton: CompoundButton, b: Boolean ->
//            if(b){
//                cardView2?.visibility=View.GONE
//                cbRequestExchange?.visibility=View.GONE
//                tv_pay_option?.text=getString(R.string.choose_payment)
//                mSelectedPayment=null
//            }else{
//                cardView2?.visibility=View.VISIBLE
//            }
//        }
    }

    private fun editProfile() {
        val userInfo = dataManager.getGsonValue(DataNames.USER_DATA, PojoSignUp::class.java)
        val hashMap = HashMap<String, RequestBody>()
        hashMap["accessToken"] = CommonUtils.convrtReqBdy(prefHelper.getKeyValue(PrefenceConstants.ACCESS_TOKEN, PrefenceConstants.TYPE_STRING)?.toString()
                ?: "")

//        if (etIdForInvoice?.text?.toString()?.trim()?.isNotEmpty()==true)
//            hashMap["id_for_invoice"] = CommonUtils.convrtReqBdy(etIdForInvoice?.text?.toString()?:"".trim())
//
//        hashMap["name"] = CommonUtils.convrtReqBdy(userInfo?.data?.firstname?:"")
        if (isNetworkConnected)
            viewModel.editProfile(hashMap)
    }

    private fun showMinOrderAlert(amount: Float) {

        AlertDialog.Builder(requireContext())
                .setMessage(getString(R.string.net_total_greater_than, AppConstants.CURRENCY_SYMBOL, Utils.getPriceFormat(amount, settingData, selectedCurrency)))
                .setPositiveButton(R.string.Ok, null)
                .show()
    }


    private var walletDiscount: Double? = 0.0

    @SuppressLint("SetTextI18n")
    private fun settingLayout(maxDeliveryCharge: Float) {
        Log.e("enter", "enteryyyyy")
        maxHandlingAdminCharges = mDeliveryChargeArray.filter { chargeArray ->
            cartList?.any {
                chargeArray.supplierId == it.supplierId
            } == true
        }.sumByDouble { it.tax?.toDouble() ?: 0.0 }.toFloat()



        mSubTotalCopy = appUtils.calculateCartTotal(cartList)
        totalAmt = appUtils.calculateCartTotal(cartList)

        if (tipType == TIP_PERCEN) {
            mTipCharges = totalAmt.times(mTipChargesCopy.div(100)).toFloat()
        }

        Log.e("enter", "enteryyyyy123")
        tvTipCharges.text = activity?.getString(R.string.currency_tag, AppConstants.CURRENCY_SYMBOL, Utils.getPriceFormat(mTipCharges, settingData, selectedCurrency))

        tvSubTotal.text = getString(R.string.currency_tag, AppConstants.CURRENCY_SYMBOL, Utils.getPriceFormat(totalAmt.toFloat(), settingData, selectedCurrency))
        tvSubTotal1.text = getString(R.string.currency_tag, AppConstants.CURRENCY_SYMBOL, Utils.getPriceFormat(totalAmt.toFloat(), settingData, selectedCurrency))
var subTotal= totalAmt.toDouble()
        settingData?.user_service_fee.let {
            if (it == "1") {
                restServiceTax = 0.0
                val differSupplierProducts = ArrayList<CartInfo>()

                val supplierProd = cartList?.groupBy { it.supplierId }

                supplierProd?.values?.forEach { list ->

                    val serviceFee = if (list.firstOrNull()?.is_user_service_charge_flat ?: "" == "1") {
                        list.firstOrNull()?.user_service_charge
                                ?: 0.0
                    } else {
                        list.sumByDouble { it.price.times(it.quantity).toDouble() }.div(100f).times(list.firstOrNull()?.user_service_charge
                                ?: 0.0)
                    }

                    cartList?.map { cart ->
                        if (cart.supplierId == list.firstOrNull()?.supplierId) {
                            cart.handlingSupplier = serviceFee.toFloat()
                        }
                    }
                    restServiceTax += decimalFormat.format(serviceFee).toDoubleOrNull() ?: 0.0
                }





                group_service.visibility = View.VISIBLE
                tvRestService.text = getString(R.string.currency_tag, AppConstants.CURRENCY_SYMBOL,
                        Utils.getPriceFormat(restServiceTax.toFloat(), settingData, selectedCurrency))
            }
        }

        var discount = 0f

        if (settingData?.wallet_module == "1" && mSelectedPayment?.payName == getString(R.string.wallet)) {
            val amt = ((settingData?.payment_through_wallet_discount?.toDoubleOrNull()
                    ?: 0.0).div(100)).times(totalAmt)
            walletDiscount = amt
            tvWalletCharges?.text = getString(R.string.currency_tag, AppConstants.CURRENCY_SYMBOL,
                    Utils.getPriceFormat(amt.toFloat(), settingData, selectedCurrency))
            totalAmt -= amt
        }

        if (dataManager.getGsonValue(DataNames.DISCOUNT_AMOUNT, PromoCodeModel.DataBean::class.java) != null) {
            val promoData = dataManager.getGsonValue(DataNames.DISCOUNT_AMOUNT, PromoCodeModel.DataBean::class.java)
            group_discount.visibility = View.VISIBLE

//            //   totalAmt = totalAmt.minus(promoData?.discountPrice ?: 0f)
//            etPromoCode.setText(promoData?.promoCode)
//            tvRedeem.text = getString(R.string.remove)
//            etPromoCode.isEnabled = false
            discount = if (totalAmt <= promoData?.discountPrice ?: 0f) totalAmt.toFloat() else promoData?.discountPrice
                    ?: 0f

            totalAmt = if (totalAmt < 0 || totalAmt.toFloat() == discount)
                maxDeliveryCharge + maxHandlingAdminCharges + mTipCharges + restServiceTax + questionAddonPrice
            else
                totalAmt.minus(discount.toDouble()) + maxDeliveryCharge + maxHandlingAdminCharges + mTipCharges + restServiceTax + questionAddonPrice


            tvDiscount.text = "-" + getString(R.string.currency_tag, AppConstants.CURRENCY_SYMBOL, Utils.getPriceFormat(discount, settingData, selectedCurrency))
        } else {
//            etPromoCode.setText("")
//            etPromoCode.isEnabled = true
//            tvRedeem.text = getString(R.string.apply)
            group_discount.visibility = View.GONE
            tvDiscount.text = getString(R.string.currency_tag, AppConstants.CURRENCY_SYMBOL, Utils.getPriceFormat(0.0f, settingData, selectedCurrency))

            totalAmt = if (totalAmt < 0)
                maxDeliveryCharge + maxHandlingAdminCharges + mTipCharges + restServiceTax + questionAddonPrice
            else
                totalAmt + maxHandlingAdminCharges + maxDeliveryCharge + mTipCharges + restServiceTax + questionAddonPrice
        }


        group_tax.visibility = if (maxHandlingAdminCharges == 0.0f) View.GONE else View.VISIBLE


        tvTaxCharges.text = getString(R.string.currency_tag, AppConstants.CURRENCY_SYMBOL, Utils.getPriceFormat(maxHandlingAdminCharges, settingData, selectedCurrency))

        totalAmtCopy = totalAmt.plus(discount)
        if (mReferralAmt > 0 && isReferrale) {

            redeemedAmt = if (totalAmt >= mReferralAmt) {
                mReferralAmt.toDouble()
            } else {
                totalAmt
            }

            totalAmt -= redeemedAmt

            tvReferralCode.text = "-" + getString(R.string.currency_tag, AppConstants.CURRENCY_SYMBOL,
                    Utils.getPriceFormat(redeemedAmt.toFloat(), settingData, selectedCurrency))

            if (totalAmt == 0.0) {
                tv_pay_option.text = getString(R.string.referral_applied)
            }
        }

        if (scheduleData != null && scheduleData?.supplierTimings?.isNotEmpty() == true && scheduleData?.selectedTable == null)
            totalAmt += (scheduleData?.supplierTimings?.firstOrNull()?.price?.toDouble() ?: 0.0)


        if (settingData?.table_book_mac_theme == "1" && scheduleData != null && scheduleData?.selectedTable != null &&
                scheduleData?.selectedTable?.table_booking_price != null) {
            val tablePrice = if (cartList?.firstOrNull()?.table_booking_discount != null && cartList?.firstOrNull()?.table_booking_discount != 0f)
                cartList?.firstOrNull()?.table_booking_discount?.times(mSubTotalCopy.toFloat())?.div(100)
                        ?: 0f else 0f

            //   groupBookingCharges?.visibility=View.VISIBLE
            val tableTotalPrice = if (tablePrice > (scheduleData?.selectedTable?.table_booking_price
                            ?: 0f)) tablePrice
            else scheduleData?.selectedTable?.table_booking_price ?: 0f

            /*  tvBookingCharges?.text=activity?.getString(R.string.currency_tag, AppConstants.CURRENCY_SYMBOL,
                      Utils.getPriceFormat(tableTotalPrice , settingData, selectedCurrency))
  */
            scheduleData?.selectedTable?.calculated_table_price = tableTotalPrice
            //  totalAmt+=tableTotalPrice

        } else groupBookingCharges?.visibility = View.GONE


        totalAmt = deductLoyaltyAmt(totalAmt)
        mDeliveryCharge = baseDeliveryCharges
        for(i in 0 until promoCodes.size)
        {
            if(promoCodes.get(i).is_auto_apply==1) {
                is_auto_apply = 1
                break
            }
        }
        if (self_pickup.equals("8") || self_pickup.equals("")) {

            if (mFreeDelivery == 0) {

                    if (mDeliveryCharge == 0f) {
                        group_delivery.visibility = View.GONE
                        totalAmt = totalAmt
                    } else {
                        group_delivery.visibility = View.VISIBLE
                        totalAmt = totalAmt + baseDeliveryCharges
                    }
                if (promoCodes.size == 0) {

                }
                else {

                    if(is_auto_apply==1) {
                        if (promoCodes.get(0).minPrice.toDouble() > subTotal.toDouble()) {

                        } else {
                            if (isApply) {

                                isApply = false
                                etPromoCode.setText(promoCodes?.get(0).promoCode.toString())
                                btn_promo.callOnClick()
                            }
                        }
                    }
                }
            }else {
                if(mMinimumAmountForDelivery.toFloat()>totalAmt)
                {
                    group_delivery.visibility = View.VISIBLE
                    totalAmt = totalAmt + baseDeliveryCharges
                    if (promoCodes.size == 0) {

                    }
                    else {
                        if(is_auto_apply==1) {
                            if (promoCodes.get(0).minPrice.toDouble() > subTotal.toDouble()) {

                            } else {
                                if (isApply) {
                                    isApply = false
                                    etPromoCode.setText(promoCodes?.get(0).promoCode.toString())
                                    btn_promo.callOnClick()
                                }
                            }
                        }
                    }
                }else {
                    mDeliveryCharge=0f
                    baseDeliveryCharges=0f
                    totalAmt = totalAmt
                    group_delivery.visibility = View.GONE
                    if (promoCodes.size == 0) {

                    }
                    else {
                        if(is_auto_apply==1) {
                            if (promoCodes.get(0).minPrice.toDouble() > subTotal.toDouble()) {

                            } else {
                                if (isApply) {
                                    isApply = false
                                    etPromoCode.setText(promoCodes?.get(0).promoCode.toString())
                                    btn_promo.callOnClick()
                                }
                            }
                        }
                    }
                }
            }

        } else {
            totalAmt = totalAmt
        }
        totalAmtCopy = totalAmt

        tvNetTotal.text = AppConstants.CURRENCY_SYMBOL + Utils.getPriceFormat(decimalFormat.format(totalAmt).toFloat(), settingData, selectedCurrency)
        tvNetTotal1.text = AppConstants.CURRENCY_SYMBOL + Utils.getPriceFormat(decimalFormat.format(totalAmt).toFloat(), settingData, selectedCurrency)
        //    tvNetTotal.text = getString(R.string.currency_tag, AppCo
        //
        //        nstants.CURRENCY_SYMBOL, Utils.getDecimalPointValue(settingData, totalAmt.toFloat()))
        tvLoyaltyPoints?.text = getString(R.string.currency_tag, AppConstants.CURRENCY_SYMBOL, Utils.getPriceFormat(loyaltyLevelDiscount
                ?: 0f, settingData, selectedCurrency))

        if (settingData?.is_currency_exchange_rate == "1" && cartList?.firstOrNull()?.currency_exchange_rate != null &&
                cartList?.firstOrNull()?.currency_exchange_rate != 0.0
                && cartList?.firstOrNull()?.local_currency != null) {
            val amt = cartList?.firstOrNull()?.currency_exchange_rate?.times(totalAmt)

            val string = getString(R.string.net_total_exchange, cartList?.firstOrNull()?.local_currency, decimalFormat.format(amt),
                    AppConstants.CURRENCY_SYMBOL, cartList?.firstOrNull()?.currency_exchange_rate?.toString())

            tvNetTotal.text = string
            tvNetTotal1.text = string
        }


        if (settingData?.enable_tax_on_total_amt == "1") {
            totalAmt -= maxHandlingAdminCharges
            val amt = cartList?.firstOrNull()?.handlingAdmin
            val taxOnTotal = (totalAmt.times(amt?.toDouble() ?: 0.0)).div(100)
            totalAmt += taxOnTotal
            this.maxHandlingAdminCharges = taxOnTotal.toFloat()
            group_tax?.visibility = if (taxOnTotal != 0.0) View.VISIBLE else View.GONE
            tvNetTotal.text = AppConstants.CURRENCY_SYMBOL + Utils.getPriceFormat(decimalFormat.format(totalAmt).toFloat(), settingData, selectedCurrency)
            tvNetTotal1.text = AppConstants.CURRENCY_SYMBOL + Utils.getPriceFormat(decimalFormat.format(totalAmt).toFloat(), settingData, selectedCurrency)
            tvTaxCharges.text = getString(R.string.currency_tag, AppConstants.CURRENCY_SYMBOL, Utils.getPriceFormat(taxOnTotal.toFloat(), settingData, selectedCurrency))
        }

    }

    private fun deductLoyaltyAmt(totalAmt: Double): Double {
        val amount = if (settingData?.is_loyality_enable == "1")
            totalAmt.minus(loyaltyLevelDiscount?.toDouble() ?: 0.0)
        else totalAmt

        return if (amount < 0.0) 0.0 else amount
    }

    private fun initTipAdapter() {
        tvTipCharges.text = activity?.getString(R.string.currency_tag, AppConstants.CURRENCY_SYMBOL, decimalFormat.format(mTipCharges))
        tipList = ArrayList()
        val mLayoutManager = LinearLayoutManager(activity, RecyclerView.HORIZONTAL, false)
        rvTip.layoutManager = mLayoutManager
        tipAdapter = TipAdapter(activity
                ?: requireContext(), tipList, screenFlowBean, appUtils, settingData, selectedCurrency)
        rvTip.adapter = tipAdapter
        tipAdapter?.tipCallback(this)
    }


    private fun setData() {
        rl_contact.setOnClickListener {
            if (cbContact.isChecked) {
                cbContact.isChecked = false
                rbCOD.visibility = View.VISIBLE
            } else {
                cbContact.isChecked = true
                rbCOD.visibility = View.GONE
            }
        }
        cartList = appUtils.getCartList().cartInfos

        isPaymentConfirm = (cartList?.any { it.isPaymentConfirm == 1 } ?: false && settingData?.payment_after_confirmation == "1")
        // isPaymentConfirm=true

        setCartVisibility(cartList)


        cartList.takeIf { it?.isNotEmpty() == true }?.let { cartInfo ->

            cartAdapter = CartAdapter(activity
                    ?: requireContext(), cartInfo, screenFlowBean, appUtils, settingData, selectedCurrency)
            cartAdapter?.settingCallback(this)
            recyclerview.adapter = cartAdapter


            refreshDeliveryAdrs(mDeliveryType ?: 0)

            /* val list=ArrayList<CartItem>()
             cartInfo.forEach{
                list.add(CartItem(product_ids = it.productId,supplier_id = it.supplierId))
             }*/

            if (isNetworkConnected) {
                if (::adrsData.isInitialized) {
                    viewModel.refreshCart(CartReviewParam(product_ids = cartInfo.map { it.productId },
                            latitude = adrsData.latitude ?: "0.0", longitude = adrsData.longitude
                            ?: "0.0"), isLoginFromCart = dataManager.getCurrentUserLoggedIn())
                } else {
                    dataManager.getGsonValue(PrefenceConstants.ADRS_DATA, AddressBean::class.java)?.also {
                        viewModel.refreshCart(CartReviewParam(
                                product_ids = cartInfo.map { it.productId }, latitude = it.latitude
                                ?: "0.0",
                                longitude = it.longitude
                                        ?: "0.0"), dataManager.getCurrentUserLoggedIn())
                    }
                }
            }

            if (isNetworkConnected) {
                if (dataManager.getCurrentUserLoggedIn() && settingData?.referral_feature != null && settingData?.referral_feature == "1") {
                    viewModel.referralAmount()
                }

                if (dataManager.getCurrentUserLoggedIn() ) {
                    viewModel.getSaveCardNew()
                }
            }
        }

        setQuestAdapter()


        // init swipe to dismiss logic
        val swipeToDismissTouchHelper = ItemTouchHelper(object : ItemTouchHelper.SimpleCallback(
                ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {
            override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder): Boolean {
                // callback for drag-n-drop, false to skip this feature
                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                // callback for swipe to dismiss, removing item from data and adapter

                val mCartProd = cartList?.get(viewHolder.adapterPosition)


                if (mCartProd?.productAddonId ?: 0 > 0) {
                    StaticFunction.removeFromCart(activity, mCartProd?.productId, mCartProd?.productAddonId
                            ?: 0)
                } else {
                    StaticFunction.removeFromCart(activity, mCartProd?.productId, 0)
                }

                dataManager.removeValue(DataNames.DISCOUNT_AMOUNT)
                cartList?.removeAt(viewHolder.adapterPosition)
                cartAdapter?.notifyItemRemoved(viewHolder.adapterPosition)
                setCartVisibility(cartList)
                calculateCartCharges(cartList)
            }

        })
        swipeToDismissTouchHelper.attachToRecyclerView(recyclerview)

        if (mDeliveryType == FoodAppType.Both.foodType && productData?.appType == AppDataType.Food.type) {
            openDeliveryDialog()
        }

        if ((settingData?.cart_image_upload == "1" || settingData?.product_prescription == "1") &&
                cartList?.any { it.cart_image_upload == 1 } == true) {

            mAdapter = ImageListAdapter(ImageListAdapter.UserChatListener({

                if (it.is_imageLoad == false) {
                    if (imageList?.count() ?: 0 >= 4) {
                        mBinding?.root?.onSnackbar(getString(R.string.max_limit_reached))

                    } else {
                        if (dataManager.getCurrentUserLoggedIn())
                            uploadImage()
                        else {
                            appUtils.checkLoginFlow(requireActivity(), DataNames.REQUEST_CART_LOGIN_PRECRIPTION)
                        }
                    }
                }

            }, { it1 ->
                if (it1 < 0) return@UserChatListener
                imageList?.removeAt(it1)
                mAdapter?.submitMessageList(imageList, "cart")
            }, { it, pos ->

            }), false)

            mAdapter?.submitMessageList(imageList, "cart")

            //  rvPhotoList?.adapter = mAdapter
        } else
        //  group_presc?.visibility = View.GONE
/*
        settingData?.cart_image_upload?.let {
            if (it == "1") {


            } else {
                group_presc.visibility = View.GONE
            }
        }
*/

            if (cartList?.any { it.order_instructions == 1 } == true || settingData?.order_instructions == "1") {
                //   edAdditionalRemarks?.visibility = View.VISIBLE
            } else {
                //   edAdditionalRemarks?.visibility = View.GONE
            }
    }

    private fun setCartVisibility(cartList: MutableList<CartInfo>?) {
        viewModel.setIsCartList(cartList?.size ?: 0)

//        imgNav?.visibility = if ((cartList?.size
//                        ?: 0) == 0 && settingData?.is_juman_flow_enable == "1") View.VISIBLE else View.GONE

        Utils.loadAppPlaceholder(settingData?.cart ?: "")?.let {
            if (it.app?.isNotEmpty() == true) {
                ivCrat.loadPlaceHolder(it.app)
            }
            if (it.message?.isNotEmpty() == true) {
                tv_no_cart.text = it.message
            }
        }


        if (isPaymentConfirm || cartList?.count() == 0 || settingData?.isCash ?: "" == "1") {
            cardView2?.visibility = View.GONE
            //   btn_donate_someone?.visibility = View.GONE
            group_tip?.visibility = View.GONE
            //     groupExpectedDelivery?.visibility = View.GONE
        } else {
            cardView2.visibility = View.VISIBLE
            settingData?.show_donate_popup?.let {
                if (it == "1" && mDeliveryType == FoodAppType.Delivery.foodType) {
                    //          btn_donate_someone.visibility = View.VISIBLE
                } else
                    Log.d("", "")
                //        btn_donate_someone.visibility = View.GONE
            }
        }
        //   cbSkipPayment?.visibility=if(settingData?.skip_payment_option=="1" && !cartList.isNullOrEmpty()) View.VISIBLE else View.GONE
    }

    private fun setQuestAdapter() {

        mQuestionList = cartList?.distinctBy {
            it.question_list?.distinctBy { it1 -> it1.questionId }
        }?.flatMap { it2 ->
            it2.question_list ?: mutableListOf()
        } ?: listOf()


        if (mQuestionList.isEmpty()) return
        group_question.visibility = View.VISIBLE


        val filterLIst = mQuestionList.flatMap { it.optionsList }

        questionAddonPrice = filterLIst.sumByDouble {
            if (it.flatValue > 0) {
                it.flatValue.toDouble()
            } else {
                it.productPrice.div(100.0f).times(it.percentageValue).toDouble()
            }
        }.toFloat()

        tvAddonCharges.text = getString(R.string.currency_tag, AppConstants.CURRENCY_SYMBOL, Utils.getPriceFormat(questionAddonPrice, settingData, selectedCurrency))
        questAdapter = SelectedQuestAdapter(activity
                ?: requireContext(), mQuestionList, settingData, selectedCurrency)
        recyclerviewQuest.layoutManager = LinearLayoutManager(activity
                ?: requireContext(), RecyclerView.VERTICAL, false)
        recyclerviewQuest.adapter = questAdapter
        questAdapter?.notifyDataSetChanged()

        calculateCartCharges(cartList)
    }


    private fun settingInstructionLayout() {
        //  parkLyt.visibility = View.VISIBLE

        rButtonPetYes.setOnClickListener {
            rButtonPetYes.isChecked = true
            rButtonPetNo.isChecked = false
            havePets = 1
            cleaner_in = 0
        }

        rButtonPetNo.setOnClickListener {
            rButtonPetNo.isChecked = true
            rButtonPetYes.isChecked = false
            havePets = 0
            cleaner_in = 1

        }

        rButtonCleanInYes.setOnClickListener {
            rButtonCleanInYes.isChecked = true
            rButtonCleanInNo.isChecked = false
            cleaner_in = 1
            havePets = 0
        }

        rButtonCleanInNo.setOnClickListener {
            rButtonCleanInYes.isChecked = false
            rButtonCleanInNo.isChecked = true
            cleaner_in = 0
            havePets = 1
        }

        // havePets = rButtonPetYes.isChecked
        // cleaner_in=rButtonCleanInYes.isChecked

    }


    private fun showInstructionDialog() {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle(R.string.things_to_remember)
                .setMessage(settingData?.things_to_remember)
                .setPositiveButton(R.string.Ok) { dialog, id ->
                    if (isNetworkConnected) {
                        mViewModel?.addCart(productList, appUtils, questionAddonPrice, mQuestionList, mDeliveryChargeArray, decimalFormat, is_schedule)
                    }
                }

        builder.show()
    }


    private fun calculateDelivery() {

        val checkEcomApp = productData?.appType == AppDataType.Ecom.type && settingData?.ecom_agent_module == "1"
        val checkFoodApps = (productData?.appType == AppDataType.Food.type &&
                screenFlowBean?.is_single_vendor == VendorAppType.Single.appType)

        if (BuildConfig.CLIENT_CODE == "skipp_0631") {
            if (!(this::adrsData.isInitialized))
                adrsData = AddressBean()
        }
        Log.e("fhfhfhfgh", "jhgjhgjhgjh")
        calculateCartCharges(cartList)
    }

    private fun refreshDeliveryAdrs(deliveryType: Int) {
        //   adrsLyt.visibility = View.VISIBLE

        when (deliveryType) {
            FoodAppType.Pickup.foodType -> {
                Log.e("delviry", "4")
                group_delivery.visibility = View.GONE
                lytAddAddress?.visibility = View.GONE
                lytAddVechicle?.visibility = View.VISIBLE
                cbContact?.visibility = View.GONE
                tv_cbContact?.visibility = View.GONE
                //  tv_change_adrs.visibility = View.GONE
                // ivChange.visibility = View.GONE
                //  deliver_text.text = getString(R.string.pickup_from)
            }
            FoodAppType.Delivery.foodType, FoodAppType.Both.foodType -> {
                Log.e("delviry", "3")
                group_delivery.visibility = View.VISIBLE
                //  tv_change_adrs.visibility = View.VISIBLE
                if (AppConstants.APP_SUB_TYPE > AppDataType.Custom.type) {
                    //     ivChange.visibility = View.VISIBLE
                }

//                if (productData?.is_appointment == "1")
//                    deliver_text.text = getString(R.string.appointment_at)
//                else {
//                    deliver_text.text = if (productData?.appType == AppDataType.HomeServ.type) {
//                        getString(R.string.service_at)
//                    } else {
//                        getString(R.string.delivery_to)
//                    }
//
//                }

            }
            FoodAppType.DineIn.foodType -> {
                Log.e("delviry", "2")
                group_delivery.visibility = View.GONE
                //  tv_change_adrs.visibility = View.GONE
                //  ivChange.visibility = View.GONE
                //  deliver_text.text = getString(R.string.dine_in_tag)
            }
            else -> {
                //    adrsLyt.visibility = View.GONE
            }
        }


        val currentTableData = prefHelper.getCurrentTableData()
        if ((currentTableData != null || selectedTableData != null) && settingData?.is_table_booking != "1") {
            //  adrsLyt.visibility = View.GONE
        }

        /*   if (deliveryId.isEmpty()) {

           tv_deliver_adrs.text=  if (mDeliveryType == FoodAppType.Pickup.foodType && dataManager.getGsonValue(PrefenceConstants.RESTAURANT_INF, LocationUser::class.java) != null) {
                   val mLocUser = dataManager.getGsonValue(PrefenceConstants.RESTAURANT_INF, LocationUser::class.java)
                           ?: return
                  mLocUser.address
               }else if( dataManager.getGsonValue(DataNames.LocationUser, LocationUser::class.java)!=null)
               {
                   dataManager.getGsonValue(DataNames.LocationUser, LocationUser::class.java)?.let {
                       it.address
                   }
               }else {
                     getString(R.string.delivery_location)
               }
           }*/


        if (settingData?.is_juman_flow_enable == "1") {
            //     adrsLyt.visibility = View.VISIBLE
            //  viewBackground?.visibility = if (cartList.isNullOrEmpty()) View.GONE else View.VISIBLE
        }

    }


    private fun handleBothDelivery(isSchedule: Boolean) {
        if (mDeliveryType == FoodAppType.Both.foodType && productData?.appType == AppDataType.Food.type) {
            openDeliveryDialog()
        } else {
            placeOrder(isSchedule)
        }
    }

    private fun openDonateDialog() {
        val binding = DataBindingUtil.inflate<DialogDonateBinding>(LayoutInflater.from(activity), R.layout.dialog_donate, null, false)
        binding.color = colorConfig
        binding.strings = textConfig

        val mDialog = mDialogsUtil.showDialog(activity ?: requireContext(), binding.root)
        mDialog.show()

        val edDelivery = mDialog.findViewById<TextInputEditText>(R.id.edDelivery)
        val btnDonate = mDialog.findViewById<MaterialButton>(R.id.btn_donate)
        val btnSkip = mDialog.findViewById<MaterialButton>(R.id.btn_skip)
        val address = if (adrsData.address_line_1 != null)
            "${adrsData.customer_address ?: ""},${adrsData.address_line_1 ?: ""}"
        else
            adrsData.customer_address

        edDelivery.setText(address)

        btnDonate.setOnClickListener {
            isDonate = true
            mDialog.dismiss()
        }

        btnSkip.setOnClickListener {
            mDialog.dismiss()
        }

        mDialog.setOnDismissListener {
            tv_checkout.callOnClick()
        }

    }


    private fun openDeliveryDialog() {

        val binding = DataBindingUtil.inflate<DialogBothDeliveryBinding>(LayoutInflater.from(activity), R.layout.dialog_both_delivery, null, false)
        binding.color = colorConfig
        binding.strings = textConfig

        val mDialog = mDialogsUtil.showDialogFix(activity ?: requireContext(), binding.root)

        if (settingData?.is_table_booking != "1") {
            mDialog.btn_dine_in.visibility = View.GONE
            mDialog.divider_2.visibility = View.GONE
        }


        mDialog.show()

        val btn_pickup = mDialog.findViewById<MaterialButton>(R.id.btn_pickup)
        val btn_delivery = mDialog.findViewById<MaterialButton>(R.id.btn_delivery)
        val btn_dine_in = mDialog.findViewById<MaterialButton>(R.id.btn_dine_in)

        btn_pickup.setOnClickListener {
            mDeliveryType = FoodAppType.Pickup.foodType
            if (mDialog.isShowing) {
                mDialog.dismiss()
            }
        }

        btn_delivery.setOnClickListener {
            mDeliveryType = FoodAppType.Delivery.foodType
            if (mDialog.isShowing) {
                mDialog.dismiss()
            }
        }

        btn_dine_in.setOnClickListener {
            mDeliveryType = FoodAppType.DineIn.foodType
            if (mDialog.isShowing) {
                mDialog.dismiss()
            }
        }

        mDialog.setOnDismissListener {
            val cart = CartList()

            cartList?.map {
                it.deliveryType = mDeliveryType ?: 0
            }

            if (mDeliveryType == FoodAppType.DineIn.foodType || mDeliveryType == FoodAppType.Pickup.foodType) {
                mDeliveryCharge = 0.0f
                group_tip.visibility = View.GONE
            }

            cart.cartInfos = cartList

            dataManager.addGsonValue(DataNames.CART, Gson().toJson(cart))
            // calculateCartCharges(cartList.cartInfos)
            refreshDeliveryAdrs(productData?.deliveryType ?: 0)
            calculateDelivery()
        }
    }

    private fun placeOrder(isSchedule: Boolean) {
        productList.clear()
        productList.addAll(StaticFunction.covertCartToArray(activity))
        productList.map { product ->
            val item = cartList?.find { it.productId.toString() == product.productId }
            if (item != null) {
                if (settingData?.is_loyality_enable == "1")
                    product.perProductLoyalityDiscount = item.perProductLoyalityDiscount
                product.handlingSupplier = item.handlingSupplier
            }
        }

        if (productList.isNotEmpty()) {
            agentType = productList.first().agent_type ?: 0
        }

        val currentTableData = prefHelper.getCurrentTableData()
        if (settingData?.is_table_booking == "1" && mDeliveryType == FoodAppType.DineIn.foodType) {
            if (scheduleData == null && selectedTableData == null) {
                /** this means time and date slot is not selected */
                mBinding?.root?.onSnackbar(getString(R.string.please_select_table))
                return
            }

            if (settingData?.by_pass_tables_selection == "1" && selectedTableData == null) {
                /** this means that scheduling is selected but user also need to select a table */
                mBinding?.root?.onSnackbar(getString(R.string.please_select_table))
                return
            }
        }

//        if ((deliveryId.isEmpty() || deliveryId == "0") && mDeliveryType != FoodAppType.Pickup.foodType &&
//                mDeliveryType != FoodAppType.DineIn.foodType) {
//             mBinding?.root?.onSnackbar("Please select address")
//            tvAddAddress.callOnClick()
//        } else
        if (mAgentParam == null && productData?.appType == AppDataType.HomeServ.type && productData?.is_appointment != "1")
            mBinding?.root?.onSnackbar(getString(R.string.please_select_slot))
        else if (mDropOffDataParam == null && productData?.appType == AppDataType.HomeServ.type && settingData?.is_laundry_theme == "1")
            mBinding?.root?.onSnackbar(getString(R.string.please_select_delivery_slot))
        else if (productData?.appType == AppDataType.HomeServ.type && settingData?.is_laundry_theme == "1" && checkDateTime()) {
            mBinding?.root?.onSnackbar(getString(R.string.drop_off_date_time_greater, settingData?.dropoff_buffer))
        } else if (validatePaymentOption(mSelectedPayment)) {
            mBinding?.root?.onSnackbar(getString(R.string.choose_payment))
        } else if (imageList?.isEmpty() == true && (settingData?.cart_image_upload == "1" || settingData?.product_prescription == "1") &&
                (cartList?.any { it.cart_image_upload == 1 } == true)) {
            mBinding?.root?.onSnackbar(getString(R.string.please_select_precription))
        } else if (settingData?.enable_delivery_companies == "1" && !supplierDeliveryCompanies.isNullOrEmpty() && mDeliveryType
                == FoodAppType.Delivery.foodType && selectedDeliveryCompany == null) {
            mBinding?.root?.onSnackbar(getString(R.string.please_select_delivery_company))
        } else if (settingData?.extra_instructions == "1") {
            showInstructionDialog()
        } else {
            if (isSchedule) {
                if (scheduleData != null) {
                    if (settingData?.signup_declaration == "1")
                        DeclarationDialog.newInstance().show(childFragmentManager, "dialog")
                    else {
                        if (isNetworkConnected) {
                            mViewModel?.addCart(productList, appUtils, questionAddonPrice, mQuestionList, mDeliveryChargeArray, decimalFormat, is_schedule)
                        }
                    }
                } else {
                    activity?.launchActivity<ScheduleOrder>(SCHEDULE_REQUEST) {
                        putExtra("supplierId", cartList?.firstOrNull()?.supplierId.toString1())
                        if (::adrsData.isInitialized) {
                            putExtra("latitude", adrsData.latitude)
                            putExtra("longitude", adrsData.longitude)
                        }
                    }
                }
            } else {
                if (selectedTableData == null) {
                    //  scheduleData = null
                }
                if (settingData?.signup_declaration == "1")
                    DeclarationDialog.newInstance().show(childFragmentManager, "dialog")
                else {
                    if (isNetworkConnected) {
                        mViewModel?.addCart(productList, appUtils, questionAddonPrice, mQuestionList, mDeliveryChargeArray, decimalFormat, is_schedule)
                    }
                }
            }
        }
    }

    override fun onSubmitDeclaration() {
        if (isNetworkConnected) {
            mViewModel?.addCart(productList, appUtils, questionAddonPrice, mQuestionList, mDeliveryChargeArray, decimalFormat, is_schedule)
        }
    }

    private fun checkDateTime(): Boolean {
        val formatter = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
        val serviceDate = mAgentParam?.serviceDate + " " + mAgentParam?.serviceTime
        val dropOffTime = mDropOffDataParam?.serviceDate + " " + mDropOffDataParam?.serviceTime
        var pickupDate = formatter.parse(serviceDate)
        val dropOffDate = formatter.parse(dropOffTime)
        val calender = Calendar.getInstance()
        calender.time = pickupDate ?: Date()
        calender.add(Calendar.DATE, settingData?.dropoff_buffer?.toInt() ?: 0)
        pickupDate = calender.time

        return pickupDate?.after(dropOffDate) == true
    }

    private fun validatePaymentOption(mSelectedPayment: CustomPayModel?): Boolean {
        return if (settingData?.skip_payment_option == "1")
            false
        else if (mSelectedPayment == null && (settingData?.payment_method != "null" && settingData?.payment_method?.toInt() ?: 0 != PaymentType.CASH.payType) && !isPaymentConfirm && totalAmt != 0.0) {
            true
        } else if (settingData?.payment_method != "null" && settingData?.payment_method?.toInt() ?: 0 == PaymentType.ONLINE.payType || settingData?.payment_method?.toInt() ?: 0 == PaymentType.BOTH.payType) {
            false
        } else {
            false
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == DataNames.REQUEST_CART_LOGIN && resultCode == Activity.RESULT_OK) {
            if (dataManager.getCurrentUserLoggedIn()) {
                if (isNetworkConnected) {
                    if (::adrsData.isInitialized) {
                        viewModel.refreshCart(CartReviewParam(product_ids = cartList?.map { it.productId },
                                latitude = adrsData.latitude
                                        ?: "0.0", longitude = adrsData.longitude
                                ?: "0.0"), true)
                    } else {
                        viewModel.getAddressList(cartList?.firstOrNull()?.suplierBranchId ?: 0)
                    }
                }
                if (settingData?.referral_feature != null && settingData?.referral_feature == "1") {
                    viewModel.referralAmount()
                }
            }
        } else if (requestCode == AppConstants.REQUEST_AGENT_DETAIL && resultCode == Activity.RESULT_OK) {
            if (data == null) return
            val isDeliveryDateTime = data.hasExtra("isDeliveryDateTime")
            if (settingData?.is_laundry_theme == "1" && isDeliveryDateTime) {
                mDropOffDataParam = data.getParcelableExtra("agentData")
                settingDeliveryTimeData(mDropOffDataParam)
            } else {
                mAgentParam = data.getParcelableExtra("agentData")!!
                settingAgentData(mAgentParam)
                checkDropOffButton()
            }
        } else if (requestCode == SCHEDULE_REQUEST && resultCode == Activity.RESULT_OK) {
            if (data?.hasExtra("slotDetail") == true) {
                scheduleData = data.getParcelableExtra("slotDetail")
                groupSchedule?.visibility = View.VISIBLE
                btn_edit_schedule_order?.visibility = View.VISIBLE
            } else {
                btn_edit_schedule_order?.visibility = View.GONE
                groupSchedule?.visibility = View.GONE
                scheduleData = null
            }

            tvScheduleCharges?.text = getString(R.string.currency_tag, AppConstants.CURRENCY_SYMBOL, (scheduleData?.supplierTimings?.firstOrNull()?.price
                    ?: 0f).toString())

            calculateCartCharges(cartList)
            /* totalAmt += (scheduleData?.supplierTimings?.firstOrNull()?.price?.toFloat() ?: 0f)

             decimalFormat.roundingMode = RoundingMode.UP
             tvNetTotal.text = AppConstants.CURRENCY_SYMBOL + decimalFormat.format(totalAmt)*/
            tvNetTotal1.text = AppConstants.CURRENCY_SYMBOL + decimalFormat.format(totalAmt)
            /* btn_schedule_order.text = "Change Slot ${scheduleData?.supplierTimings.firstOrNull()}-${scheduleDate?.endDate}"*/
        } else if (requestCode == AppConstants.REQUEST_CART_LOGIN_BOOKING && resultCode == Activity.RESULT_OK) {
            openBookingDateTime(false, false)
        } else if (requestCode == DataNames.REQUEST_CART_LOGIN_PROMO && resultCode == Activity.RESULT_OK) {
            //   checkPromoApi(etPromoCode.text.toString().trim())
        } else if (requestCode == DataNames.REQUEST_CART_LOGIN_PRECRIPTION && resultCode == Activity.RESULT_OK) {
            uploadImage()
        } else if (requestCode == AppConstants.REQUEST_PAYMENT_OPTION && resultCode == Activity.RESULT_OK) {
            mSelectedPayment = data?.getParcelableExtra("payItem")
            //  cbRequestExchange?.visibility = View.GONE
            groupWallet?.visibility = View.GONE
            if (mSelectedPayment?.mumybenePay ?: mSelectedPayment?.payName == textConfig?.braintree) {
                tv_pay_option.text = textConfig?.braintree
            } else {
                tv_pay_option.text = mSelectedPayment?.mumybenePay ?: mSelectedPayment?.payName
            }
            if (tipList.isNotEmpty()) {
                group_tip.visibility = View.VISIBLE
            }

            when (mSelectedPayment?.payName) {
                getString(R.string.zelle), getString(R.string.pipol_pay) -> {
                    //  Glide.with(activity ?: requireActivity()).load(mSelectedPayment?.keyId
                    //        ?: "").into(iv_doc)
                    //group_zelle.visibility = View.VISIBLE
                }
                getString(R.string.mumybene) -> mDialogsUtil.showMumyBenePhone(activity
                        ?: requireContext(), phoneNumber
                        ?: "", payName = mSelectedPayment?.mumybenePay ?: "") {
                    phoneNumber = it
                }
                textConfig?.cod -> {
                    group_tip.visibility = View.GONE
                    tvClearTip.callOnClick()
                    //     cbRequestExchange?.visibility = if (settingData?.is_coin_exchange == "1") View.VISIBLE else View.GONE
                }
                textConfig?.braintree -> {
                    if (isNetworkConnected) {
                        viewModel.getBrainTreeClientToken()
                    }
                }
                getString(R.string.wallet) -> {
                    groupWallet?.visibility = View.VISIBLE
                    //      group_zelle.visibility = View.GONE
                    calculateCartCharges(cartList)
                }
                //    else -> group_zelle.visibility = View.GONE
            }

        } else if (requestCode == AppSettingsDialog.DEFAULT_SETTINGS_REQ_CODE) {
            // Do something after user returned from app settings screen, like showing a Toast.
            mBinding?.root?.onSnackbar(getString(R.string.returned_from_app_settings_to_activity))
        } else if (resultCode == AppCompatActivity.RESULT_OK && requestCode == AppConstants.CameraPicker) {
            if (isNetworkConnected) {
                if (photoFile?.isRooted == true) {
                    viewModel.uploadImage(imageUtils.compressImage(photoFile?.absolutePath
                            ?: ""))
                }
            }
        } else if (resultCode == AppCompatActivity.RESULT_OK && requestCode == AppConstants.GalleyPicker) {
            if (data != null) {
                if (isNetworkConnected) {
                    //data.getData return the content URI for the selected Image
                    val selectedImage = data.data
                    val filePathColumn = arrayOf(MediaStore.Images.Media.DATA)
                    // Get the cursor
                    val cursor = activity?.contentResolver?.query(selectedImage!!, filePathColumn, null, null, null)
                    // Move to first row
                    cursor?.moveToFirst()
                    //Get the column index of MediaStore.Images.Media.DATA
                    val columnIndex = cursor?.getColumnIndex(filePathColumn[0])
                    //Gets the String value in the column
                    val imgDecodableString = cursor?.getString(columnIndex ?: 0)
                    cursor?.close()

                    if (imgDecodableString?.isNotEmpty() == true) {
                        viewModel.uploadImage(imageUtils.compressImage(imgDecodableString))
                    }
                }
            }
        } else if (requestCode == AppConstants.REQUEST_CODE_PDF && resultCode == Activity.RESULT_OK) {
            val dir = File(Environment.getExternalStorageDirectory(), "/app")
            if (!dir.exists()) dir.mkdirs()
            val uri = data?.data
            val uriString = uri?.toString()
            val pdfFile: File?
            val myFile = File(uriString ?: "")
            if (uriString?.startsWith("content://") == true) {
                try {
                    val v = myFile.name.replace("[^a-zA-Z]+".toRegex(), "_") + SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date()) + ".pdf"
                    pdfFile = File(dir, v)
                    val fos = FileOutputStream(pdfFile)
                    val out = BufferedOutputStream(fos)
                    val inputStream = context?.contentResolver?.openInputStream(uri)
                    try {
                        val buffer = ByteArray(8192)
                        var len: Int
                        while (inputStream?.read(buffer).also { len = it ?: 0 } ?: 0 >= 0) {
                            out.write(buffer, 0, len)
                        }
                        out.flush()
                    } finally {
                        fos.fd.sync()
                        out.close()
                        inputStream?.close()
                    }
                    photoFile = pdfFile
                    if (photoFile?.isRooted == true) {
                        viewModel.uploadImage(photoFile?.absolutePath ?: "")
                    }
                } catch (e: Exception) {
                }
            } else if (uriString?.startsWith("file://") == true) {
                photoFile = myFile
                if (photoFile?.isRooted == true) {
                    viewModel.uploadImage(photoFile?.absolutePath ?: "")
                }
            }
        } else if (resultCode == Activity.RESULT_OK && requestCode == PaymentParams.PAYMENT_REQUEST_CODE) {
            mSelectedPayment?.keyId = data?.getStringExtra(PaymentParams.TRANSACTION_ID) ?: ""
            onlinePayment(mSelectedPayment)
        } else if (requestCode == REQUEST_CODE_BRAINTREE) {
            when (resultCode) {
                AppCompatActivity.RESULT_OK -> {
                    val result: DropInResult? = data?.getParcelableExtra(DropInResult.EXTRA_DROP_IN_RESULT)
                    result?.paymentMethodNonce?.let { displayResult(it, result.deviceData) }
                }
                AppCompatActivity.RESULT_CANCELED -> {
                    //show error message
                    val x = (data?.getSerializableExtra(DropInActivity.EXTRA_ERROR) as java.lang.Exception?)?.message
                }
                else -> {
                    val error = data!!.getSerializableExtra(DropInActivity.EXTRA_ERROR) as java.lang.Exception?
                }
            }
        } else if (requestCode == SCHEDULE_REQUEST_DINE_IN && resultCode == Activity.RESULT_OK) {
            scheduleData = data?.getParcelableExtra("slotDetail")
            setTableBookingData()
            settingToolbar()
            groupSchedule?.visibility = View.VISIBLE
            tvScheduleCharges?.text = getString(R.string.currency_tag, AppConstants.CURRENCY_SYMBOL, (scheduleData?.supplierTimings?.firstOrNull()?.price
                    ?: 0f).toString())

            if (settingData?.by_pass_tables_selection == "1") {
                selectedTableData = ListItem()
                selectedTableData?.id = 0
            } else {
                getListOfRestaurantsAccordingToSlot()
            }
        } else if (requestCode == REQUEST_PROMO_CODE && resultCode == Activity.RESULT_OK) {
            val promoCode = data?.getParcelableExtra<PromoCodeItem>("promoCode")
            //    etPromoCode?.setText(promoCode?.promoCode)
            //  tvRedeem?.callOnClick()
        } else if (requestCode == CRAVE_SCHEDULE_REQUEST && resultCode == Activity.RESULT_OK) {
            viewModel.craveScheduleModel = data?.getParcelableExtra<CraveScheduleModel>("crave_schedule")
            Log.d("cart", viewModel.craveScheduleModel.toString1())
            delivery_note = etNote.text.toString()
            val selectedId: Int = lytPaymentSelection.getCheckedRadioButtonId()
            if (selectedId == R.id.rbCreditCard) {
                paymentType = "1"

            } else if (selectedId == R.id.rbWallet) {
                paymentType = "4"
            } else if (selectedId == R.id.rbCOD) {
                paymentType = "0"
            }
            if (paymentType.equals("")) {

            } else {
                is_schedule = "1"
                tv_checkout.callOnClick()
            }
        } else if (requestCode == SLOT_TIME_REQUEST && resultCode == Activity.RESULT_OK) {
            var timeee = data?.getStringExtra("slot_time_n")!!.toString()
            Schedule_time = timeee.toString()
            Schedule_time_without = data?.getStringExtra("slot_time")!!.toString()

            Log.e("timeee", Schedule_time.toString())
            tvExpectedTime.text = Schedule_time_without

        } else if (requestCode == CARD_PAYMENT_REQUEST && resultCode == Activity.RESULT_OK) {
            when (resultCode) {
                AppCompatActivity.RESULT_OK -> {

                    var transactionId = data?.getStringExtra("transactionId")
                    var type = data?.getStringExtra("type")
                    var status = data?.getStringExtra("status")
                    var ending = data?.getStringExtra("cardEndingDigits")
                    var token = data?.getStringExtra("token")
                    var scheme = data?.getStringExtra("scheme")

                    if (scheme != null && token != null && ending != null) {
                        checkUrl(orderId[0].toString()!!, transactionId!!, type!!, status!!, ending, token, scheme, user_id)
                    } else {
                        checkUrl(orderId[0].toString()!!, transactionId!!, type!!, status!!, "", "", "", user_id)
                    }
                }
                AppCompatActivity.RESULT_CANCELED -> {
                    //show error message
                    val x = (data?.getSerializableExtra(DropInActivity.EXTRA_ERROR) as java.lang.Exception?)?.message
                }
                else -> {
                    val error = data!!.getSerializableExtra(DropInActivity.EXTRA_ERROR) as java.lang.Exception?
                }
            }

        }else if(requestCode == 157 && resultCode == Activity.RESULT_OK)
        {
            if(data?.hasExtra("addressBean")==true)
            {
                val addressBean=data.getParcelableExtra<AddressBean>("addressBean")
                update_address="1"
                addressBean?.let { onAddressSelect(it) }
            }

            if(data?.hasExtra("vehicleBean")==true)
            {
                val vehicleData=data?.getParcelableExtra<MapVehicleInputParam>("vehicleBean")
                onVehicleSelect(vehicleData?.id ?: "", vehicleData?.modelNo
                        ?: "", vehicleData?.color ?: "", vehicleData?.plateNo ?: "")
            }
        }
        paymentUtil.activityResult(requestCode, resultCode, data)?.let {
            mSelectedPayment?.keyId = it
            onlinePayment(mSelectedPayment)
        }
    }


    /*   fun tipText(){
           tvRiderTipText.text=



       }*/

    private fun getListOfRestaurantsAccordingToSlot() {
        val bundle = bundleOf("slot_id" to scheduleData?.supplierTimings?.get(0)?.id?.toString(),
                "supplierId" to currentSupplierId,
                "branchId" to supplierBranchId,
                "requestFromCart" to "1"
        )
        navController(this)
                .navigate(R.id.cart_to_tableSelection, bundle)

    }

    private fun settingAgentData(mAgentParam: AgentCustomParam?) {
        group_mainlyt.visibility = View.VISIBLE
        change_time_slot.visibility = View.GONE
        gp_action.visibility = View.GONE

        tvSerDate.text = mDateTime.convertDateOneToAnother(mAgentParam?.serviceDate + " " + mAgentParam?.serviceTime,
                "yyyy-MM-dd HH:mm", "EEE, dd MMM hh:mm aaa")


        if (mAgentParam?.agentData != null) {
            txtAgentBook?.visibility = View.VISIBLE
            agentDetail?.visibility = View.VISIBLE
            // if (mAgentParam?.agentData?.image != null) {
            iv_userImage.loadUserImage(mAgentParam.agentData?.image ?: "")
            // }

            if (mAgentParam.agentData?.name != null) {
                tv_name.text = mAgentParam.agentData?.name ?: ""
            }

            if (mAgentParam.agentData?.occupation != null) {
                tv_occupation.text = mAgentParam.agentData?.occupation ?: ""
            }

            tv_total_reviews.text = getString(R.string.agent_reviews_tag, mAgentParam.agentData?.avg_rating?.toFloat()
                    ?: 0f)

        } else {
            txtAgentBook?.visibility = View.GONE
            agentDetail?.visibility = View.GONE
        }
    }

    private fun settingDeliveryTimeData(mAgentParam: AgentCustomParam?) {
        group_mainlyt_delivery.visibility = View.VISIBLE
        change_delivery_time_slot.visibility = View.GONE

        tvSerDeliveryDate.text = mDateTime.convertDateOneToAnother(mAgentParam?.serviceDate + " " + mAgentParam?.serviceTime,
                "yyyy-MM-dd HH:mm", "EEE, dd MMM hh:mm aaa")

    }

    override fun onDeleteCart(position: Int) {

        if (position == -1) return

        val mCartProd = cartList?.get(position)
        StaticFunction.removeFromCart(activity, mCartProd?.productId, mCartProd?.productAddonId
                ?: 0)
        dataManager.removeValue(DataNames.DISCOUNT_AMOUNT)

        cartList?.removeAt(position)
        cartAdapter?.notifyItemRemoved(position)

        setCartVisibility(cartList)

        manageStock(cartList)
        calculateCartCharges(cartList)
    }

    override fun onEditQuantity(updatedQuantity: Float?, position: Int) {
        hideKeyboard()
        addItemToCart(updatedQuantity, position)
    }

    override fun addItem(position: Int) {
        if (position == -1) return
        addItemToCart(null, position)
    }

    private fun addItemToCart(updatedQuantity: Float?, position: Int) {
        if (position != -1) {

            dataManager.removeValue(DataNames.DISCOUNT_AMOUNT)
            val mCartProd = cartList?.get(position)?.copy()

            cartUtils.addItemToCart(mCartProd, quantityFromEditText = updatedQuantity).let {
                if (it != null) {

                    cartList?.set(position, it.copy())

                    cartAdapter?.notifyItemChanged(position)
                    cartAdapter?.notifyDataSetChanged()
                }
            }
            Log.e("hghghghg", "sjgfhg")
            calculateCartCharges(cartList)

            cartAdapter?.notifyItemChanged(position)
            cartAdapter?.notifyDataSetChanged()
        }
    }

    override fun removeItem(position: Int) {
        if (position == -1) return

        val mCartProd = cartList?.get(position)?.copy()

        mCartProd.let {
            cartUtils.removeItemToCart(mCartProd).let {
                cartList?.set(position, it.copy())
            }
        }

        if (mCartProd?.quantity == 0f) {
            cartList?.removeAt(position)
        }

        cartAdapter?.notifyItemChanged(position)
        cartAdapter?.notifyDataSetChanged()

        setCartVisibility(cartList)
        manageStock(cartList)

        calculateCartCharges(cartList)
    }

    override fun onClickProdDesc(position: Int) {

    }


    private fun extraFunctionality() {
//        edAdditionalRemarks.hint = if (BuildConfig.CLIENT_CODE == "cannadash_0180") {
//            "Enter your medical id"
//        } else if (BuildConfig.CLIENT_CODE == "keenathlive_0505") {
//            "${getString(R.string.select_a_pier)}"
//        } else if (BuildConfig.CLIENT_CODE == "skipp_0631") {
//            getString(R.string.enter_car_number)
//        } else {
//            getString(R.string.enter_instructionc)
//        }
    }


    private fun calculateCartCharges(cartList: MutableList<CartInfo>?, supplierId: Int? = 0) {

        val totalWeight = cartList?.sumByDouble {
            (it.duration?.toDouble() ?: 0.0).times(it.quantity)
        }

        var deliveryChargesWeight: Float? = null
        if (settingData?.is_delivery_charge_weight_wise_enable == "1") {
            for (i in ((weightWiseArray?.size ?: 0) - 1) downTo 0) {
                if (totalWeight ?: 0.0 >= weightWiseArray?.get(i)?.weight?.toDouble() ?: 0.0) {
                    deliveryChargesWeight = weightWiseArray?.get(i)?.delivery_charge
                    break
                }
            }
        }

        mDeliveryCharge = if (settingData?.is_delivery_charge_weight_wise_enable == "1" && deliveryChargesWeight != null) {
            deliveryChargesWeight
        } else if (productData?.appType == AppDataType.Ecom.type) {
//            if (settingData?.ecom_agent_module == "1")
            deliveryChargesFromDistance
//            else
//                cartList?.maxByOrNull {
//                    it.deliveryCharges
//                }?.deliveryCharges
//                        ?: 0.0f
        } else if (settingData?.delivery_charge_type == "1" && mDeliveryType != FoodAppType.Pickup.foodType) {
            productData?.radius_price ?: 0.0f.plus(baseDeliveryCharges)
        } else if (regionDeliveryCharges > 0f) {
            regionDeliveryCharges.plus(baseDeliveryCharges)
        } else {
            deliveryChargesFromDistance
        }
        if (productData?.appType == AppDataType.Food.type && mDeliveryType != FoodAppType.Delivery.foodType) {
            mDeliveryCharge = 0.0f
        }

        val total = appUtils.calculateCartTotal(cartList)

        if (userSubscription?.benefits?.any { it.benefit_type == "FD" } == true && userSubscription?.min_order_amount ?: 0.0 > 0.0 &&
                total >= userSubscription?.min_order_amount ?: 0.0) {
            mDeliveryCharge = 0f
        }

        mDeliveryCharge = baseDeliveryCharges
//        if (mFreeDelivery == 0) {
//            if (mDeliveryCharge == 0f) {
//                Log.e("delviry", "1")
//                group_delivery.visibility = View.GONE
//            }
//        } else {
//            group_delivery.visibility = View.GONE
//        }

        mDeliveryChargeArray.filter { chargeArray ->
            cartList?.any {
                chargeArray.supplierId == it.supplierId
            } == true
        }.map { supplierDelivery ->
            supplierDelivery.tax = if (settingData?.disable_tax == "1") 0.0f else cartList?.find { it.supplierId == supplierDelivery.supplierId }?.let { cartInfo ->
                cartInfo.price.times(cartInfo.quantity.toDouble()).plus(questionAddonPrice).times(cartInfo.handlingAdmin.div(100))
            }?.toFloat()

            if (supplierDelivery.supplierId == supplierId) {
                supplierDelivery.deliveryCharge = mDeliveryCharge
            }
        }


        mDeliveryCharge = if (settingData?.enable_delivery_companies == "1" && mDeliveryType
                == FoodAppType.Delivery.foodType && !supplierDeliveryCompanies.isNullOrEmpty())
            if (selectedDeliveryCompany == null) 0f else deliveryChargesFromDistance
        else mDeliveryChargeArray.filter { chargeArray ->
            cartList?.any {
                (chargeArray.supplierId == it.supplierId && it.is_free_delivery != 1)
            } == true
        }.sumByDouble { it.deliveryCharge?.toDouble() ?: 0.0 }.toFloat()

        tvDeliveryCharges.text = activity?.getString(R.string.currency_tag, AppConstants.CURRENCY_SYMBOL,
                Utils.getPriceFormat(baseDeliveryCharges, settingData, selectedCurrency))


        settingLayout(mDeliveryCharge)
//        if (mFreeDelivery == 0) {
//            if (mDeliveryCharge == 0f) {
//                Log.e("delviry", "1")
//                group_delivery.visibility = View.GONE
//            }
//        } else {
//            if(mMinimumAmountForDelivery.toFloat()>totalAmt)
//            {
//                group_delivery.visibility = View.VISIBLE
//            }else {
//                mDeliveryCharge=0f
//                baseDeliveryCharges=0f
//                group_delivery.visibility = View.GONE
//            }
//        }

    }


    private fun checkPromoApi(length: String, self_pick: String? = null) {

        val amount = appUtils.calculateCartTotal(cartList).plus(questionAddonPrice)

        val checkPromo = CheckPromoCodeParam(length, StaticFunction.getAccesstoken(activity), amount.toString(), StaticFunction.getLanguage(activity).toString(),
                cartList?.map { it.supplierId.toString() }, cartList?.map { it.categoryId.toString() }, self_pick)

        if (isNetworkConnected) {
            hideKeyboard()
            mViewModel?.validatePromo(checkPromo)
        }
    }

    private fun calculatePromo(promoData: PromoCodeModel.DataBean, cartList: MutableList<CartInfo>?, promoCode: String) {

        hideKeyboard()
        var filter_cart_total = 0f

        cartList?.mapIndexed { index, cartInfo ->

            filter_cart_total += if (promoData.supplierIds.isNotEmpty()) {
                promoData.supplierIds.filter { it == cartInfo.suplierBranchId }.sumByDouble { cartInfo.price.times(cartInfo.quantity).toDouble() }.toFloat()
            } else {
                promoData.categoryIds.filter { it == cartInfo.categoryId }.sumByDouble { cartInfo.price.times(cartInfo.quantity).toDouble() }.toFloat()
            }
        }

        promoData.discountPrice = if (filter_cart_total > 0 && promoData.discountType == 0) {
            promoData.discountPrice
        } else {
            val value = filter_cart_total / 100.0f * promoData.discountPrice
            if (promoData.max_discount_value != 0f && promoData.max_discount_value < value) {
                promoData.max_discount_value
            } else {
                value
            }
        }

//        if (promoData.discountPrice > 0) {
        if (promoData.discountPrice > 0) {

            promoData.promoCode = promoCode

//            etPromoCode.setText(promoCode)
//            etPromoCode.isEnabled = false
//            tvRedeem.text = getString(R.string.remove)

            dataManager.addGsonValue(DataNames.DISCOUNT_AMOUNT, Gson().toJson(promoData))
            calculateCartCharges(cartList)
        } else {
            mBinding?.root?.onSnackbar(getString(R.string.no_promocode_cart))
        }

    }

    override fun getBindingVariable(): Int {
        return BR.viewModel
    }

    override fun getLayoutId(): Int {
        return R.layout.fragment_cart_new
    }

    override fun getViewModel(): CartViewModel {
        mViewModel = ViewModelProviders.of(this, factory).get(CartViewModel::class.java)
        return mViewModel as CartViewModel
    }

    override fun onUpdateCart() {
        if (isNetworkConnected) {
            when {
                settingData?.skip_payment_option == "1" -> {
                    bookingForCash(DataNames.SKIP_PAYMENT)
                }
                mSelectedPayment?.keyId == DataNames.PAYMENT_CASH.toString() || (settingData?.payment_method != "null" &&
                        settingData?.payment_method?.toInt() ?: 0 == PaymentType.CASH.payType) || isPaymentConfirm || totalAmt == 0.0 -> {

                    val paymentOption = if (isPaymentConfirm) DataNames.PAYMENT_AFTER_CONFIRM else if (totalAmt == 0.0) DataNames.PAYMENT_REFERRAL else DataNames.PAYMENT_CASH
                    bookingForCash(paymentOption)
                }
                mSelectedPayment?.payName == getString(R.string.zelle) || mSelectedPayment?.payName == getString(R.string.creds_movil) ||
                        mSelectedPayment?.payName == getString(R.string.pipol_pay) -> {
                    onlinePayment(mSelectedPayment)
                }

                mSelectedPayment?.payName == getString(R.string.mumybene) -> {
                    onlinePayment(mSelectedPayment)
                    mumyBeneDialog?.show()
                }
                mSelectedPayment?.payName == textConfig?.braintree -> {
                    if (mAuthorization.isNullOrEmpty()) {
                        AppToasty.error(requireContext(), getString(R.string.braintree_token_error))
                        return
                    }
                    launchDropIn()
                }
                mSelectedPayment?.payName == getString(R.string.datatrans) -> {
                    appUtils.startTransaction(totalAmt, this, requireActivity())
                }
                mSelectedPayment?.payName == getString(R.string.wallet) -> {
                    mViewModel?.generateOrder(Schedule_time, user_car_id, appUtils,
                            mDeliveryType,
                            paymentType!!.toInt(),
                            mAgentParam,

                            "",
                            "",
                            redeemedAmt,
                            imageList ?: mutableListOf(),
                            delivery_note,
                            mTipCharges,
                            restServiceTax,
                            mQuestionList,
                            questionAddonPrice,
                            productData?.appType ?: 0,
                            mSelectedPayment,
                            havePets,
                            cleaner_in,
                            "",
                            "",
                            isPaymentConfirm,
                            isDonate,
                            phoneNumber,
                            scheduleData,
                            walletDiscount,
                            isLoyaltyPointsApplied,
                            "",
                            null,
                            isDineInWithFood,
                            selectedTableData?.id.toString1(),
                            tableLoadedFromScanner, mSubTotalCopy, mDropOffTimeParam = mDropOffDataParam,
                            order_delivery_type = order_delivery_type,
                            vehicle_number = "",
                            isCutleryRequired = false, ipAddress = ipAddress, signature_menu_id = signature_menu_id,
                            signature_plate_id = signature_plate_id, from_signature_menu = from_signature_menu,
                            from_signature_plate = from_signature_plate, deliveryCompany = selectedDeliveryCompany)
                }
                else -> {
                    if (mSelectedPayment?.addCard == true) {
                        if (isNetworkConnected) {
                            onlinePayment(mSelectedPayment)
                        }
                    } else {
                        paymentUtil.checkPaymentMethod(this, mViewModel, mSelectedPayment, totalAmt, adrsData)
                    }
                }
            }
        }
    }

    private fun bookingForCash(paymentOption: Int) {
        mViewModel?.generateOrder(Schedule_time, user_car_id, appUtils,
                mDeliveryType,
                paymentType!!.toInt(),
                mAgentParam,
                "",
                "",
                redeemedAmt,
                imageList ?: mutableListOf(),
                delivery_note,
                mTipCharges,
                restServiceTax,
                mQuestionList,
                questionAddonPrice,
                productData?.appType ?: 0,
                mSelectedPayment, havePets,
                cleaner_in,
                "",
                "",
                isPaymentConfirm,
                isDonate,
                phoneNumber,
                scheduleData,
                null,
                isLoyaltyPointsApplied,
                "",
                "0",
                isDineInWithFood,
                selectedTableData?.id.toString1(),
                tableLoadedFromScanner,
                mSubTotalCopy, mDropOffTimeParam = mDropOffDataParam, order_delivery_type = order_delivery_type,
                vehicle_number = "", isCutleryRequired = false,
                ipAddress = ipAddress,
                signature_menu_id = signature_menu_id,
                signature_plate_id = signature_plate_id, from_signature_menu = from_signature_menu,
                from_signature_plate = from_signature_plate, deliveryCompany = selectedDeliveryCompany)
    }

    fun add_event() {
        for (i in 0 until cartList?.size!!) {
            var prodViewedAction = mapOf(
                    "Product Name" to cartList?.get(i)!!.productName,
                    "Price" to cartList?.get(i)!!.productName,
                    "Quantity" to cartList?.get(i)!!.quantity)


            cleverTapDefaultInstance?.pushEvent("CheckOut_event", prodViewedAction)
        }
        var prodViewedAction1 = mapOf(

                "Total Price" to totalAmt)
        cleverTapDefaultInstance?.pushEvent("CheckOut_event", prodViewedAction1)
    }

    override fun paymentProcessStateChanged(p0: PaymentProcessAndroid?) {
        when (p0?.state) {
            PaymentProcessState.BEFORE_COMPLETION -> {
                //do nothing
            }
            PaymentProcessState.COMPLETED -> {
                mSelectedPayment?.keyId = p0.transactionId
                onlinePayment(mSelectedPayment)
            }
            PaymentProcessState.CANCELED -> {
                activity?.runOnUiThread {
                    AppToasty.error(requireContext(), getString(R.string.payment_cancelled))
                }
            }
            PaymentProcessState.ERROR -> {
                activity?.runOnUiThread {
                    AppToasty.error(requireContext(), p0.exception.toString())
                }
            }
            else -> {
            }
        }
    }


    override fun getSaddedPaymentSuccess(data: AddCardResponseData?) {
        mSelectedPayment?.keyId = data?.transaction_reference
        val intent = Intent(requireContext(), PaymentWebViewActivity::class.java).putExtra("paymentData", data)
        startActivityForResult(intent, SADDED_PAYMENT_REQUEST)
    }

    override fun getThawaniPaymentSuccess(data: AddCardResponseData?) {
        val intent = Intent(requireContext(), PaymentWebViewActivity::class.java).putExtra("paymentData", data)
                .putExtra("payment_gateway", getString(R.string.thawani))
        startActivityForResult(intent, THAWANI_PAYMENT_REQUEST)
    }

    override fun getTelrPaymentSuccess(data: AddCardResponseData?) {
        mSelectedPayment?.keyId = data?.order?.ref
        val intent = Intent(requireContext(), PaymentWebViewActivity::class.java).putExtra("paymentData", data)
                .putExtra("payment_gateway", getString(R.string.teller))
        startActivityForResult(intent, TELR_PAYMENT_REQUEST)
    }

    override fun getAamarPayPaymentSuccess(data: AddCardResponseData?) {
        val intent = Intent(requireContext(), PaymentWebViewActivity::class.java).putExtra("paymentData", data)
                .putExtra("payment_gateway", getString(R.string.aamar_pay))
        startActivityForResult(intent, AAMAR_PAY_PAYMENT_REQUEST)
    }

    override fun getmPaisaPaymentSuccess(data: AddCardResponseData?) {
        mSelectedPayment?.keyId = data?.rID
        val intent = Intent(requireContext(), PaymentWebViewActivity::class.java).putExtra("paymentData", data)
                .putExtra("payment_gateway", getString(R.string.mpaisa))
        startActivityForResult(intent, SADDED_PAYMENT_REQUEST)
    }

    override fun onBrainTreeTokenSuccess(clientToken: String?) {
        mAuthorization = clientToken

        try {
            val mBraintreeFragment = BraintreeFragment.newInstance(this, mAuthorization)
            if (ClientToken.fromString(mAuthorization) is ClientToken) {
                DropInResult.fetchDropInResult(requireActivity() as AppCompatActivity?, mAuthorization, this)
            } else {
                launchDropIn()
            }
        } catch (e: InvalidArgumentException) {
            AppToasty.error(requireContext(), e.message.toString1())
        }
    }

    override fun userBlocked() {

        viewFlipper.displayedChild = 1
        tv_error.text = getString(R.string.admin_block_msg)

    }

    override fun userUnBlock() {
        viewFlipper.displayedChild = 0
        settingToolbar()
    }

    @SuppressLint("StringFormatInvalid")
    override fun onProfileUpdate() {
//        tvDone?.visibility=View.GONE
//        etIdForInvoice?.clearFocus()
        AppToasty.success(requireContext(), getString(R.string.id_updated_success, getString(R.string.hint_id_number)))
    }


    override fun getMyFatoorahPaymentSuccess(data: AddCardResponseData?) {
        val intent = Intent(requireContext(), PaymentWebViewActivity::class.java).putExtra("paymentData", data)
                .putExtra("payment_gateway", textConfig?.my_fatoorah)
        startActivityForResult(intent, MY_FATOORAH_PAYMENT_REQUEST)
    }

    override fun onTapPayment(transaction: Transaction) {
        activity?.launchActivity<PaymentWebViewActivity>(TAP_PAYMENT_REQUEST) {
            putExtra("payment_url", transaction.url)
            putExtra("payment_gateway", getString(R.string.tap))
        }
    }

    override fun getPayMayaUrl(data: AddCardResponseData?) {
        activity?.launchActivity<PaymentWebViewActivity>(PAY_MAYA_REQUEST) {
            putExtra("paymentData", data)
            putExtra("payment_gateway", getString(R.string.pay_maya))
        }
    }

    override fun onEvalonPayment(data: String) {

        val paymentUrl = if (BuildConfig.DEBUG) {
            NetworkConstants.EVALON_TEST_LINK
        } else {
            NetworkConstants.EVALON_PROD_LINK
        }
        activity?.launchActivity<PaymentWebViewActivity>(EVALON_PAYMENT_REQUEST)
        {
            putExtra("payment_url", "${paymentUrl}${URLEncoder.encode(data, "utf-8")}")
            putExtra("payment_gateway", getString(R.string.tap))
        }
    }

    override fun onRefreshCartError() {
        calculateDelivery()
    }

    private fun onlinePayment(mSelectedPayment: CustomPayModel?) {
        mViewModel?.generateOrder(Schedule_time, user_car_id, appUtils,
                mDeliveryType,
                paymentType!!.toInt(),
                mAgentParam,
                mSelectedPayment?.keyId ?: "",
                mSelectedPayment?.payment_token ?: "",
                redeemedAmt,
                imageList ?: mutableListOf(),
                delivery_note,
                mTipCharges,
                restServiceTax,
                mQuestionList,
                questionAddonPrice,
                productData?.appType ?: 0,
                mSelectedPayment,
                havePets,
                cleaner_in,
                "",
                "",
                isPaymentConfirm,
                isDonate,
                phoneNumber,
                scheduleData,
                walletDiscount,
                isLoyaltyPointsApplied,
                "",
                null,
                isDineInWithFood,
                selectedTableData?.id.toString1(),
                tableLoadedFromScanner,
                mSubTotalCopy, mDropOffTimeParam = mDropOffDataParam, order_delivery_type = order_delivery_type,
                vehicle_number = "",
                isCutleryRequired = false, ipAddress = ipAddress,
                signature_menu_id = signature_menu_id,
                signature_plate_id = signature_plate_id, from_signature_menu = from_signature_menu,
                from_signature_plate = from_signature_plate, deliveryCompany = selectedDeliveryCompany)

    }


    override fun getWindCavePaymentSuccess(data: com.codebrew.clikat.data.model.api.Result?) {
        val intent = Intent(requireContext(), PaymentWebViewActivity::class.java).putExtra("paymentData", data?.Request)
                .putExtra("payment_gateway", getString(R.string.windcave))
        startActivityForResult(intent, SADDED_PAYMENT_REQUEST)
    }


    override fun onCartAdded(cartdata: AddtoCartModel.CartdataBean?) {

        /*   val adminCharges = cartList?.sumByDouble {
        (it.fixed_price?.times(it.quantity.toDouble()))?.times(it.handlingAdmin.div(100))
                ?: 0.0
    }?.toFloat() ?: 0.0f*/

        // mTotalAmt = appUtils.calculateCartTotal().toFloat().plus(mDeliveryCharge).plus(adminCharges).plus(questionAddonPrice)

        if (isNetworkConnected) {
            mViewModel?.updateCartInfo(cartList, appUtils, mDeliveryType, mDeliveryCharge, Utils.getPriceFormatWithoutConversion(totalAmt.toFloat(), settingData).toDoubleOrNull()
                    ?: 0.0, maxHandlingAdminCharges, productData?.deliveryMax, mQuestionList, questionAddonPrice)
        }
    }

    override fun onOrderPlaced(data: ArrayList<Int>) {
        orderId = data
        add_event()
        getAnalytic(orderId.toString())
        if (BuildConfig.CLIENT_CODE == "yummy_0122" || BuildConfig.CLIENT_CODE == "readychef_0309") {
            Analytics.with(context).track("New Order Placed", Properties().putValue("OrderId", orderId[0].toString()))
        }


        if (!paymentType.equals("1")) {
            mumyBeneDialog?.dismiss()
            dataManager.removeValue(DataNames.DISCOUNT_AMOUNT)
            StaticFunction.clearCart(activity)
            prefHelper.removeValue(DataNames.INVITATTON_DATA)
            prefHelper.removeValue(DataNames.SAVED_TABLE_DATA)

            val binding = DataBindingUtil.inflate<DialogOrderPlacedBinding>(LayoutInflater.from(context), R.layout.dialog_order_placed, null, false)
            val mDialog = mDialogsUtil.showDialogFix(activity ?: requireContext(), binding.root)
            mDialog.show()
            mDialog.tvPoint.text = getString(R.string.points_earned_text, totalAmtCopy.toInt().toString())
            mDialog.tvPoint.setOnClickListener {

                mDialog.dismiss()
                AppConstants.DELIVERY_OPTIONS = DeliveryType.DeliveryOrder.type

                navController(this@CartCheckout).navigate(CartCheckoutDirections.actionCartToLoyalityPoints())

            }

            mDialog.icCross.setOnClickListener {

                mDialog.dismiss()
                AppConstants.DELIVERY_OPTIONS = DeliveryType.DeliveryOrder.type

                val action = CartCheckoutDirections.actionCartToMainFragment()

                val navOptions = NavOptions.Builder()
                        .setPopUpTo(R.id.home, true)
                        .build()


                navController(this@CartCheckout).navigate(action, navOptions)
                activity?.startActivity(Intent(activity, UserTracking::class.java)
                        .putExtra(DataNames.REORDER_BUTTON, false).putIntegerArrayListExtra("orderId", arrayListOf(orderId[0].toInt())))
            }

        } else {
            //  viewModel.pay(totalAmtCopy.toString(), orderId[0].toString())
            val form = DecimalFormat("0.00")
            var url = ""
            if(prefHelper.getLangCode().equals("14"))
            {
                if (save_card == 0)
                {
                    url = BuildConfig.BASE_URL + "pay_q_pay?order_id=" + orderId[0].toString() + "&amount=" + form.format(totalAmtCopy) + "&user_name=" + userDataModel?.data?.firstname + "&mobile=" + userDataModel?.data?.mobile_no + "&email=" + userDataModel?.data?.email
                }else{
                    url = BuildConfig.BASE_URL + "pay_q_pay_saved_card?order_id=" + orderId[0].toString() + "&amount=" +  form.format(totalAmtCopy)+ "&token=" + save_card_token

                }
            }else{
                if (save_card == 0)
                {
                    url = BuildConfig.BASE_URL + "pay_q_pay?order_id=" + orderId[0].toString() + "&amount=" + decimalFormat.format(totalAmtCopy) + "&user_name=" + userDataModel?.data?.firstname + "&mobile=" + userDataModel?.data?.mobile_no + "&email=" + userDataModel?.data?.email
                }else{
                    url = BuildConfig.BASE_URL + "pay_q_pay_saved_card?order_id=" + orderId[0].toString() + "&amount=" + decimalFormat.format(totalAmtCopy) + "&token=" + save_card_token

                }
            }

            Log.e("urll", url)

            var newUrl = url.replace(" ", "%20");
            val intent = Intent(requireContext(), PaymentWebViewActivity::class.java).putExtra("url", newUrl)
            startActivityForResult(intent, CARD_PAYMENT_REQUEST)

            for (i in 0..6 / 2) {

            }
        }
    }

    override fun onFinalResponse(order_id: String?) {
//        Toast.makeText(requireActivity(), order_id, Toast.LENGTH_SHORT).show()
        getloyalityPoints(orderId.get(0).toString())
    }

    override fun onPay(sessionId: String, successIndicator: String) {
        //  var url = BuildConfig.BASE_URL + "secure_payment?sessionId=" + sessionId + "&successIndicator=" + successIndicator
//    Show_Dialog(url)

        var url = BuildConfig.BASE_URL + "pay_q_pay?order_id=" + sessionId + "&amount=" + successIndicator + "&user_name=" + "" + "&mobile=" + "" + "&email=" + ""

        val intent = Intent(requireContext(), PaymentWebViewActivity::class.java).putExtra("url", url)
        startActivityForResult(intent, CARD_PAYMENT_REQUEST)
    }


    fun checkUrl(order_id: String, transaction_id: String, type: String, status: String, cardEndingDigits: String, token: String, scheme: String, user_id: String) {


//        Toast.makeText(requireActivity(), "final order " + orderId.get(0).toString(), Toast.LENGTH_SHORT).show()
        mViewModel?.setIsLoading(true)
        viewModel.final_order(order_id, transaction_id!!, type, status, cardEndingDigits, token, scheme, user_id)


    }


    private fun getloyalityPoints(order_id: String?) {

        mViewModel?.setIsLoading(false)
        Toast.makeText(requireActivity(), "Order_id" + order_id, Toast.LENGTH_SHORT).show()
        mumyBeneDialog?.dismiss()
        dataManager.removeValue(DataNames.DISCOUNT_AMOUNT)
        StaticFunction.clearCart(activity)
        prefHelper.removeValue(DataNames.INVITATTON_DATA)
        prefHelper.removeValue(DataNames.SAVED_TABLE_DATA)
        Toast.makeText(requireActivity(), "Entered", Toast.LENGTH_SHORT).show()
        val binding = DataBindingUtil.inflate<DialogOrderPlacedBinding>(LayoutInflater.from(context), R.layout.dialog_order_placed, null, false)
        val mDialog = mDialogsUtil.showDialogFix(activity ?: requireContext(), binding.root)
        mDialog.show()
        mDialog.tvPoint.text = getString(R.string.points_earned_text, totalAmtCopy.toInt().toString())
        mDialog.tvPoint.setOnClickListener {

            mDialog.dismiss()
            AppConstants.DELIVERY_OPTIONS = DeliveryType.DeliveryOrder.type

            navController(this@CartCheckout).navigate(CartCheckoutDirections.actionCartToLoyalityPoints())

        }

        mDialog.icCross.setOnClickListener {

            mDialog.dismiss()
            AppConstants.DELIVERY_OPTIONS = DeliveryType.DeliveryOrder.type

            val action = CartCheckoutDirections.actionCartToMainFragment()

            val navOptions = NavOptions.Builder()
                    .setPopUpTo(R.id.home, true)
                    .build()


            navController(this@CartCheckout).navigate(action, navOptions)
            activity?.startActivity(Intent(activity, UserTracking::class.java)
                    .putExtra(DataNames.REORDER_BUTTON, false).putIntegerArrayListExtra("orderId", arrayListOf(orderId[0].toInt())))

        }

    }

    private fun getAnalytic(id: String) {
        val bundle = Bundle()
        bundle.putString("OrderId", id)
        mFirebaseAnalytics?.logEvent("Order_Placed", bundle)
    }

    override fun onValidatePromo(data: PromoCodeModel.DataBean, param: CheckPromoCodeParam?) {
        btn_promo.text = getString(R.string.remove)
        hideKeyboard()
        if (cartList?.sumByDouble {
                    it.price.times(it.quantity).toDouble()
                } ?: 0.0 >= data.minOrder) {
            calculatePromo(data, cartList, param?.promoCode ?: "")
        } else {
            mBinding?.root?.onSnackbar(getString(R.string.sorry_cart_value, "${AppConstants.CURRENCY_SYMBOL}${data.minOrder}"))
        }
    }

    override fun onRefreshCart(mCartData: CartData?) {
        recyclerview.visibility = View.GONE
        mDeliveryChargeArray.clear()
        productData = cartList?.first()
        promoCodes=mCartData!!.promoCodes!!
        resturant_lat=mCartData!!.result!!.get(0).latitude!!
        resturant_long=mCartData!!.result!!.get(0).longitude!!
        wallet_amount = mCartData?.wallet_amount?.toString()!!
        rbWallet.setText("Wallet( QAR " + wallet_amount + " )")
        isDineIn = mCartData?.is_dine_in ?: 0
        setTableBookingData()
        timings = mCartData.timings
        if (mCartData?.result?.firstOrNull()?.is_scheduled == 1)
            mBinding?.tvScheduleMessage?.visibility = View.VISIBLE
        else
            mBinding?.tvScheduleMessage?.visibility = View.GONE

        baseDeliveryCharges = mCartData?.result?.get(0)?.base_delivery_charges!!
        Log.e("delviry", baseDeliveryCharges.toString())
        mDeliveryCharge = baseDeliveryCharges
        mFreeDelivery = mCartData?.result?.get(0)?.is_free_delivery!!
        mMinimumAmountForDelivery=mCartData?.result?.get(0)?.minimum_order_amount_for_free_delivery!!
        if (mFreeDelivery == 0) {
            if (mDeliveryCharge == 0f) {
                Log.e("delviry", "1")
                group_delivery.visibility = View.GONE
            }
        } else {
            if(mMinimumAmountForDelivery.toFloat()>totalAmt)
            {
                group_delivery.visibility = View.VISIBLE
            }else {
//                mDeliveryCharge=0f
//                baseDeliveryCharges=0f
                group_delivery.visibility = View.GONE
            }
        }

        loyaltyLevelDiscount = mCartData?.loyalityLevelDiscountAmount!!.toFloat()

        loyalitPointDiscountAmount = mCartData?.loyalitPointDiscountAmount!!.toFloat()

        userLoyaltyPoints = mCartData?.loyalty_points!!.toFloat()

        supplierDeliveryCompanies = mCartData?.suppliers_delivery_companies

//        tvSelectDeliveryCompany?.visibility = if (settingData?.enable_delivery_companies == "1" && !supplierDeliveryCompanies.isNullOrEmpty() && mDeliveryType
//                ==FoodAppType.Delivery.foodType)
//            View.VISIBLE else View.GONE

        tvShowLoyaltyPoints?.text = getString(R.string.your_loyality_points, AppConstants.CURRENCY_SYMBOL, Utils.getPriceFormat(loyalitPointDiscountAmount
                ?: 0f, settingData, selectedCurrency))

        tvApplyLoyalty.isEnabled = loyalitPointDiscountAmount ?: 0f > 0

        tvApplyLoyalty.setTextColor(Color.parseColor(if (loyalitPointDiscountAmount ?: 0f > 0) colorConfig.textHead else colorConfig.textSubhead))

        userSubscription = mCartData?.userSubscriptionData

        mCartData?.isOpen = appUtils.checkResturntTiming(mCartData?.timings)

        if (self_pickup.equals("8") || self_pickup.equals("")) {
            lytAddtime.visibility = View.GONE
        } else {

            if (mCartData.isOpen) {
                val sdf = SimpleDateFormat("yyyy,MM:dd:HH:mm")
                val currentDateandTime = sdf.format(Date())

                val date = sdf.parse(currentDateandTime)
                val calendar = Calendar.getInstance()
                calendar.time = date
                calendar.add(Calendar.MINUTE, mCartData?.result.get(0).delivery_min_time!!)
                val formatter = SimpleDateFormat("MMM, d EE hh:mm a")

                val formatter_n = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")

                var date_time = formatter.format(calendar.time)
                var date_time_n = formatter_n.format(calendar.time)
                System.out.println("Time here " + date_time)


                lytAddtime.visibility = View.VISIBLE

                tvExpectedTime.text = date_time.toString()

                Schedule_time = date_time_n?.toString()!!
            } else {
                lytAddtime.visibility = View.GONE
            }
        }

        regionDeliveryCharges = mCartData?.region_delivery_charge.toFloat()
        //  minOrder = mCartData?.min_order

        val deliveryType = if (mDeliveryType == FoodAppType.Pickup.foodType) 0 else if (mDeliveryType == FoodAppType.Delivery.foodType) 1 else 2

        payment_gateways = if (settingData?.is_enable_orderwise_gateways == "1" && mCartData?.order_type_wise_gateways?.isNotEmpty() == true
                && mCartData.order_type_wise_gateways.find { it.order_type == deliveryType } != null) {
            val data = mCartData.order_type_wise_gateways.find { it.order_type == deliveryType }
            val list = ArrayList<String>()
            list.addAll(data?.payment_gateways?.split("#") ?: emptyList())
            list
        } else mCartData?.payment_gateways

        calculateTipCharges(mCartData?.tips ?: arrayListOf())

        val prodList = mCartData?.result ?: listOf()

        cartList?.map { cart ->

            var productDta = prodList.filter {
                it.product_id == cart.productId
            }

            if (productDta.size > 1) {

                val prodlist = productDta.filter {
                    it.discount == cart.isDiscount
                }
                productDta = prodlist
            }

            if (productDta.isNotEmpty()) {

                productData?.distance_value = productDta.firstOrNull()?.distance_value
                cart.distance_value = productDta.firstOrNull()?.distance_value
                cart.avgRating = productDta.first().avg_rating
                cart.radius_price = productDta.first().radius_price
                cart.purchasedQuant = productDta.first().purchased_quantity!!.toFloat()
                cart.prodQuant = productDta.first().quantity!!.toFloat()
                cart.categoryId = productDta.first().parent_category_id ?: 0
                cart.perProductLoyalityDiscount = productDta.firstOrNull()?.perProductLoyalityDiscount
                cart.is_free_delivery = productDta.firstOrNull()?.is_free_delivery
                /*if (productData?.appType == AppDataType.Ecom.type) {
                    cart.deliveryCharges = productDta.first().delivery_charges ?: 0.0f

                    productData?.latitude = productDta.first().latitude
                    productData?.longitude = productDta.first().longitude
                    productData?.radius_price = productDta.first().radius_price
                }*/
                cart.table_booking_discount = productDta.firstOrNull()?.table_booking_discount

                cart.handlingAdmin = productDta.first().handling_admin ?: 0.0f

                cart.add_ons?.mapIndexed { _, productAddon ->

                    val mFilterAddon = productDta.first().adds_on?.findLast { it?.name == productAddon?.name }?.value?.findLast {
                        it.type_id == productAddon?.type_id
                    }
                    if (mFilterAddon != null) {
                        productAddon?.price = mFilterAddon.price ?: 0f
                    }
                }

                cart.price = if (cart.add_ons?.isNotEmpty() == true) {
                    cart.add_ons?.sumByDouble {
                        it?.price?.toDouble() ?: 0.0
                    }?.toFloat()?.plus(productDta.first().fixed_price?.toFloatOrNull() ?: 0.0f)
                            ?: 0f
                } else {
                    if (cart.priceType == 1) {
                        cart.price
                    } else {
                        productDta.first().fixed_price?.toFloatOrNull() ?: 0f
                    }

                }
                // cartInfo.gstTotal = productDta[0].gst_price

                takeIf { productDta.first().purchased_quantity == productDta.first().quantity || productDta.first().quantity == 0f }.let {
                    cart.isQuantity = 0
                }

            } else {
                cart.isStock = true
                /*           StaticFunction.removeFromCart(activity, cartList?.get(index)?.productId, 0, false)
                   cartList?.removeAt(index)*/
            }
            cart.is_user_service_charge_flat = productDta.firstOrNull()?.is_user_service_charge_flat
            cart.user_service_charge = productDta.firstOrNull()?.user_service_charge
        }

        if (mCartData?.result?.firstOrNull()?.local_currency != null && mCartData.result.firstOrNull()?.currency_exchange_rate != null) {
            cartList?.firstOrNull()?.local_currency = mCartData.result.firstOrNull()?.local_currency
            cartList?.firstOrNull()?.currency_exchange_rate = mCartData.result.firstOrNull()?.currency_exchange_rate
            for (i in 0 until cartList!!.size) {
                for (j in 0 until mCartData.result!!.size) {
                    if (cartList?.get(i)?.productId == mCartData.result.get(j)?.product_id) {
                        cartList?.get(i)?.discount = mCartData.result.get(j)?.discount!!
                        cartList?.get(i)?.display_price = mCartData.result.get(j)?.display_price!!.toFloat()
                    }
                }
            }

        }


        if (dataManager.getCurrentUserLoggedIn()) {
            val cartListData = appUtils.getCartList()
            cartListData.cartInfos = cartList
            prefHelper.addGsonValue(DataNames.CART, Gson().toJson(cartListData))
            calculateCartCharges(cartList)
            cartAdapter?.notifyDataSetChanged()

        }
        minimumOrderArray = mCartData?.min_order_distance_wise
        weightWiseArray = mCartData?.supplier_weight_wise_delivery_charge
        weightWiseArray?.sort()
        supplierDeliveryCharges = mCartData?.supplier_delivery_types

        var supplierid = 0
        cartList?.map {
            if (it.supplierId != supplierid) {
                mDeliveryChargeArray.add(SuplierDeliveryCharge(supplierId = it.supplierId, latitude = it.latitude, longitude = it.longitude))
                supplierid = it.supplierId
            }
        }

        if (supplierDeliveryCharges?.isNotEmpty() == true && settingData?.is_enable_delivery_type == "1" && mDeliveryType == FoodAppType.Delivery.foodType) {
            //    clDelivery?.visibility = View.VISIBLE
//            tvExpressDelivery?.visibility = if (supplierDeliveryCharges?.any { it.type == 1 } == true) View.VISIBLE else View.GONE
//            tvNormalDelivery?.visibility = if (supplierDeliveryCharges?.any { it.type == 0 } == true) View.VISIBLE else View.GONE
//            if (tvExpressDelivery?.isVisible == true)
//                tvExpressDelivery?.callOnClick()
//            else
//                tvNormalDelivery?.callOnClick()
        } else
            calculateDelivery()


    }

    private fun calculateTipCharges(data: ArrayList<Int>) {
        if (mDeliveryType == FoodAppType.Pickup.foodType || mDeliveryType == FoodAppType.DineIn.foodType || data.isEmpty() || settingData?.hide_agent_tip == "1") {
            return
        }

        if (mSelectedPayment?.payName == textConfig?.cod) {
            data.clear()
            group_tip.visibility = View.GONE
        } else {
            group_tip.visibility = View.VISIBLE
        }

        tipType = TIP_FIXED
        tipAdapter?.tipType(TIP_FIXED)
        tvTipAmount.visibility = View.VISIBLE
        rlTip.visibility = View.GONE

        settingData?.agentTipPercentage?.let {
            if (it == "1") {
                tipType = TIP_PERCEN
                tipAdapter?.tipType(TIP_PERCEN)
                tvTipAmount.visibility = View.GONE
                rlTip.visibility = View.VISIBLE

                etCustomTip.afterTextChanged {


                    if (it.trim().isNotEmpty()) {
                        tvTipCurrency?.visibility = View.VISIBLE
                        tipType = TIP_FIXED
                        mTipCharges = it.toFloat()

                        // tipAdapter?.tipType(TIP_FIXED)
                        tipAdapter?.sekectedTip(-1)
                        tipAdapter?.notifyDataSetChanged()
                        calculateCartCharges(cartList)
                    } else
                        tvTipCurrency?.visibility = View.INVISIBLE
                }
            }
        }

        tipList.clear()
        tipList.addAll(data)
        tipAdapter?.notifyDataSetChanged()
        if (tipList.size > 0) {
            layoutTip.visibility = View.VISIBLE
        } else {
            layoutTip.visibility = View.GONE
        }
    }


    private fun manageStock(cartInfos: MutableList<CartInfo>?) {
        tv_checkout.isEnabled = cartInfos?.any { it.isStock == false } != true
    }

    override fun onCalculateDistance(value: DistanceMatrix?, supplierId: Int?) {
        group_delivery.visibility = View.VISIBLE
        var radiusPrice = 0f
        var distanceValue = 0f
        val valueInKm = value?.distance?.toFloat() ?: 0f

        if (value?.distance ?: 0.0 <= 0.0 && mDeliveryCharge == 0f) {
            calculateCartCharges(cartList, supplierId)
            return
        }

        if (settingData?.enable_delivery_companies == "1" && !supplierDeliveryCompanies.isNullOrEmpty() && selectedDeliveryCompany != null) {
            radiusPrice = selectedDeliveryCompany?.radiusPrice ?: 0f
            distanceValue = selectedDeliveryCompany?.distanceValue ?: 0f
        } else if (cartList?.any { it.supplierId == supplierId } == true) {
            productData?.radius_price = cartList?.find { it.supplierId == supplierId }?.radius_price
            radiusPrice = productData?.radius_price ?: 0f
            distanceValue = productData?.distance_value ?: 0f
        }

        if (regionDeliveryCharges != 0f)
            calculateMinOrderDistanceWise(valueInKm)
        else {
            bufferTime = value?.duration?.split(" ")?.firstOrNull()?.toInt()
            setExpressDelivery()
            //km to miles or miles to km ..distance conversion from backend side

            calculateMinOrderDistanceWise(valueInKm)

            /* if (settingData?.delivery_distance_unit == "1") {
                     valueInKm = valueInKm?.times(AppConstants.MILES_TO_KM)?.toFloat()
                 }*/

            var charges: BaseDeliveryCharges? = null

            if (baseDeliveryChargesArray?.isNotEmpty() == true) {
                for (i in 0 until (baseDeliveryChargesArray?.size ?: 0)) {
                    if (baseDeliveryChargesArray?.get(i)?.distance_value ?: 0f >= valueInKm) {
                        charges = baseDeliveryChargesArray?.get(i)
                        break
                    }
                }

                if (charges == null)
                    charges = baseDeliveryChargesArray?.last()
                baseDeliveryCharges = charges?.base_delivery_charges ?: 0f
                calculateDeliveryCharges(valueInKm, charges?.distance_value
                        ?: 0f, productData?.radius_price ?: 0f)
            } else {
                calculateDeliveryCharges(valueInKm, distanceValue, radiusPrice)
            }
            //  tvDeliveryCharges.text = getString(R.string.currency_tag, AppConstants.CURRENCY_SYMBOL, mDeliveryCharge)
        }
        calculateCartCharges(cartList, supplierId)
    }

    private fun setExpressDelivery() {
        if (supplierDeliveryCharges?.isNotEmpty() == true && settingData?.is_enable_delivery_type == "1" && mDeliveryType == FoodAppType.Delivery.foodType) {
            val time = supplierDeliveryCharges?.find { it.type == order_delivery_type }
            supplierPerKmPrice = time?.price ?: 0f
//            tvExpectedDeliveryTime?.text = getString(R.string.delivery_in_time, (time?.buffer_time?.plus(bufferTime
//                    ?: 0)))

        }
    }

    private fun calculateMinOrderDistanceWise(valueInKm: Float) {
        isMinimumAmountApplied = false
        distanceMinAmt = null
        if (!minimumOrderArray.isNullOrEmpty()) {
            minimumOrderArray?.sort()
            for (i in ((minimumOrderArray?.size ?: 0) - 1) downTo 0) {
                if (minimumOrderArray?.get(i)?.distance ?: 0f <= valueInKm) {
                    distanceMinAmt = minimumOrderArray?.get(i)?.min_amount?.toDouble()
                    break
                }
            }
        }
    }

    private fun calculateDeliveryCharges(valueInKm: Float?, distance_value: Float, radiusPrice: Float?) {
        val perKmPrice = if (supplierPerKmPrice != null && settingData?.is_enable_delivery_type == "1") supplierPerKmPrice
                ?: 0f else radiusPrice ?: 0f
        mDeliveryCharge = if (valueInKm?.minus(distance_value) ?: 0f > 0f) {
            (valueInKm?.minus(distance_value)?.times(perKmPrice) ?: 0f)
                    .plus(baseDeliveryCharges)
        } else {
            baseDeliveryCharges
            /*valueInKm?.times(productData?.radius_price ?: 0.0f)?.plus(baseDeliveryCharges)
                ?: 0.0f*/
        }
        deliveryChargesFromDistance = mDeliveryCharge
    }

    override fun onAddress(data: com.codebrew.clikat.data.model.api.DataBean?) {
        if (self_pickup.equals("8") || self_pickup.equals("")) {

        } else {
            viewModel.getUserVehicle(user_id ?: "0")
        }
        if (data?.address?.isNotEmpty() == true) {

//            adrsData = (if (deliveryId.isEmpty() || deliveryId == "0") {
//                data.address?.first()
//            }
//            else {
//                data.address?.filter { it.id.toString() == deliveryId }?.get(0)
//            }) ?: AddressBean()

//            adrsData.user_service_charge = data.user_service_charge
//            adrsData.preparation_time = data.preparation_time

//            updateAddress(adrsData)
        }

        baseDeliveryChargesArray = data?.base_delivery_charges_array
        baseDeliveryChargesArray?.sort()
        minOrder = data?.min_order

        setData()
    }

    override fun onReferralAmt(value: Float?) {
        if (value ?: 0.0f > 0) {
            mReferralAmt = if (settingData?.enable_referral_bal_limit == "1" && !settingData?.referral_bal_limit_per_order.isNullOrEmpty()
                    && (value ?: 0f > settingData?.referral_bal_limit_per_order?.toFloat() ?: 0f))
                settingData?.referral_bal_limit_per_order?.toFloat() ?: 0f
            else
                value ?: 0.0f

            llReferral.visibility = View.VISIBLE

            etReferralPoint.text = getString(R.string.referral_amt_tag, "${AppConstants.CURRENCY_SYMBOL} ${mReferralAmt.toString1()}")
        } else {
            llReferral.visibility = View.GONE
            group_referral.visibility = View.GONE
        }
    }

    private fun updateAddress(adrsData: AddressBean) {
        dataManager.addGsonValue(PrefenceConstants.ADRS_DATA, Gson().toJson(adrsData))
        if (mDeliveryType != FoodAppType.Pickup.foodType && mDeliveryType != FoodAppType.DineIn.foodType) {
            this.adrsData=adrsData!!
            adrsData.let {
                dataManager.setkeyValue(DataNames.PICKUP_ID, it.id.toString())
                //dataManager.addGsonValue(PrefenceConstants.ADRS_DATA, Gson().toJson(it))
                deliveryId = it.id.toString()
                is_realAddress=it.is_real_address
                tvAddress.text = appUtils.getAddressFormat(it)
            }
        }
//        if(update_address.equals("1"))
//        {
//            tvPayNow.callOnClick()
//        }
    }

    override fun onErrorOccur(message: String) {
        if (mumyBeneDialog?.isShowing == true || BuildConfig.CLIENT_CODE == "lettta_0293") {
            mumyBeneDialog?.dismiss()

            mDialogsUtil.openAlertDialog(activity
                    ?: requireContext(), message, "Okay", "", this)
        } else {

            mBinding?.root?.onSnackbar(message)
        }
    }

    override fun onSessionExpire() {
        openActivityOnTokenExpire()
    }

    override fun onAddressSelect(adrsBean: AddressBean) {
        adrsData = adrsBean

        //baseDeliveryCharges = adrsData.base_delivery_charges ?: 0.0f
        minOrder = adrsData.min_order

        adrsData.let {
            appUtils.setUserLocale(it)
        }

        if (isNetworkConnected) {
            if (::adrsData.isInitialized)
                viewModel.refreshCart(CartReviewParam(product_ids = cartList?.map { it.productId },
                        latitude = adrsData.latitude ?: "0.0", longitude = adrsData.longitude
                        ?: "0.0"), dataManager.getCurrentUserLoggedIn())
        }

        //   adrsData.user_service_charge= mAddressBean.user_service_charge
        // adrsData.preparation_time= mAddressBean.preparation_time
        updateAddress(adrsData)
    }

    override fun onDestroyDialog() {

    }

    override fun onVehicleSelect(id: String, model: String, color: String, plate_no: String) {
        tvVechicle.text = "Name : " + model + "\nVehicle Color :" + color + "\nVehicle Number :" + plate_no
        user_car_id = id;
    }

    override fun onSuccessListener() {

        AppConstants.DELIVERY_OPTIONS = DeliveryType.DeliveryOrder.type

        //   if (productData?.appType ?: 0 > 0) {
        // screenFlowBean?.app_type = AppConstants.APP_SAVED_SUB_TYPE
        //dataManager.setkeyValue(DataNames.SCREEN_FLOW, Gson().toJson(screenFlowBean))
        // }

        //  AppConstants.APP_SUB_TYPE=0

        val action = CartCheckoutDirections.actionCartToMainFragment()

        val navOptions = if (settingData?.show_food_groc == "1") {
            NavOptions.Builder()
                    .setPopUpTo(R.id.cart, false)
                    .build()
        } else {
            NavOptions.Builder()
                    .setPopUpTo(R.id.home, true)
                    .build()
        }

        navController(this@CartCheckout).navigate(action, navOptions)
        activity?.startActivity(Intent(activity, UserTracking::class.java)
                .putExtra(DataNames.REORDER_BUTTON, false).putIntegerArrayListExtra("orderId", arrayListOf(orderId[0].toInt())))
    }


    override fun onSucessListner() {

    }

    override fun onErrorListener() {

    }

    override fun paymentToken(token: String, paymentMethod: String, savedCard: SaveCardInputModel?) {
        if (isNetworkConnected) {
            mViewModel?.generateOrder(Schedule_time, user_car_id, appUtils, mDeliveryType, paymentType!!.toInt(), mAgentParam, token, paymentMethod,
                    redeemedAmt, imageList
                    ?: mutableListOf(), delivery_note, mTipCharges, restServiceTax, mQuestionList, questionAddonPrice,
                    productData?.appType
                            ?: 0, mSelectedPayment,
                    havePets, cleaner_in, "", "",
                    isPaymentConfirm, isDonate, phoneNumber, scheduleData, null, isLoyaltyPointsApplied,
                    "", null,
                    isDineInWithFood, selectedTableData?.id.toString1(), tableLoadedFromScanner, mSubTotalCopy, savedCard,
                    mDropOffTimeParam = mDropOffDataParam, order_delivery_type = order_delivery_type,
                    vehicle_number = "", isCutleryRequired = false, ipAddress = ipAddress,
                    signature_menu_id = signature_menu_id,
                    signature_plate_id = signature_plate_id, from_signature_menu = from_signature_menu,
                    from_signature_plate = from_signature_plate, deliveryCompany = selectedDeliveryCompany)
        }
    }


    private fun uploadImage() {
        if (permissionFile.hasCameraPermissions(activity ?: requireContext())) {
            if (isNetworkConnected) {
                showImagePicker()
            }
        } else {
            permissionFile.cameraAndGalleryTask(this)
        }
    }


    override fun onRequestPermissionsResult(
            requestCode: Int,
            permissions: Array<String>,
            grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        // EasyPermissions handles the request result.
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this)
    }

    private fun showImagePicker() {
        imageDialog.setPdfUpload(true)
        imageDialog.settingCallback(this)
        imageDialog.show(
                childFragmentManager,
                "image_picker"
        )
    }

    override fun onPdf() {
        val intent = Intent()
        intent.type = "application/pdf"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(Intent.createChooser(intent, "Select Pdf"),
                AppConstants.REQUEST_CODE_PDF)
    }


    override fun onGallery() {
        val pickIntent = Intent(Intent.ACTION_PICK)
        pickIntent.type = "image/*"
        startActivityForResult(pickIntent, AppConstants.GalleyPicker)
    }

    override fun onCamera() {
        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
            // Ensure that there's a camera activity to handle the intent
            takePictureIntent.resolveActivity(activity?.packageManager!!)?.also {
                // Create the File where the photo should go
                photoFile = try {
                    ImageUtility.filename(imageUtils)
                } catch (ex: IOException) {
                    // Error occurred while creating the File
                    null
                }
                // Continue only if the File was successfully created
                photoFile?.also {
                    val photoURI: Uri = FileProvider.getUriForFile(
                            requireContext(),
                            activity?.packageName ?: "",
                            it)

                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                    startActivityForResult(takePictureIntent, AppConstants.CameraPicker)
                }
            }
        }
    }

    override fun onPermissionsDenied(requestCode: Int, perms: MutableList<String>) {
        if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
            AppSettingsDialog.Builder(this).build().show()
        }
    }

    override fun onPermissionsGranted(requestCode: Int, perms: MutableList<String>) {
        if (requestCode == AppConstants.CameraGalleryPicker) {
            if (isNetworkConnected) {
                showImagePicker()
            }
        }
    }

    override fun onTipSelected(position: Int) {

        if (position == -1) return

        if (settingData?.agentTipPercentage == "1") {
            tipType = TIP_PERCEN
            rlTip.visibility = View.VISIBLE
            etCustomTip.setText("")
        } else {
            rlTip.visibility = View.GONE
        }

        if (tipType == TIP_PERCEN) {
            tipAdapter?.sekectedTip(position)
        } else {
            tipAdapter?.sekectedTip(-1)
        }

        tipAdapter?.notifyDataSetChanged()
        mTipCharges = if (tipType == TIP_PERCEN) {
            tipList[position].toFloat()
        } else {
            mTipCharges + tipList[position].toFloat()
        }

        mTipChargesCopy = mTipCharges

        tvTipAmount.text = getString(R.string.currency_tag, AppConstants.CURRENCY_SYMBOL,
                Utils.getPriceFormat(mTipCharges, settingData, selectedCurrency))

        calculateCartCharges(cartList)
    }


    private fun launchDropIn() {
        val dropInRequest = DropInRequest()
                .clientToken(mAuthorization)
                .requestThreeDSecureVerification(BrainTreeSettings.isThreeDSecureEnabled())
                .collectDeviceData(BrainTreeSettings.shouldCollectDeviceData())
                .googlePaymentRequest(getGooglePaymentRequest())
                .maskCardNumber(true)
                .maskSecurityCode(true)
                .allowVaultCardOverride(BrainTreeSettings.isSaveCardCheckBoxVisible())
                .vaultCard(BrainTreeSettings.defaultVaultSetting())
                .vaultManager(BrainTreeSettings.isVaultManagerEnabled())
                .cardholderNameStatus(BrainTreeSettings.getCardholderNameStatus())

        startActivityForResult(dropInRequest.getIntent(requireActivity()), REQUEST_CODE_BRAINTREE)
    }

    private fun getGooglePaymentRequest(): GooglePaymentRequest? {
        val currency = prefHelper.getGsonValue(PrefenceConstants.CURRENCY_INF, Currency::class.java)

        /**Currency code from server*/
        val amt = String.format("%.2f", totalAmt)
        /**final amount to be charged*/
        return GooglePaymentRequest()
                .transactionInfo(TransactionInfo.newBuilder()
                        .setTotalPrice(amt)
                        .setCurrencyCode(currency?.currency_name.toString1())
                        .setTotalPriceStatus(WalletConstants.TOTAL_PRICE_STATUS_FINAL)
                        .build())
                .billingAddressRequired(true)
                .emailRequired(true)
                .googleMerchantId("merchant-id-from-google")
    }

    private fun displayResult(paymentMethodNonce: PaymentMethodNonce, deviceData: String?) {
        val detailedNonce = BrainTreeSettings.getReadableDetailFromNonce(paymentMethodNonce)
        mViewModel?.generateOrder(Schedule_time, user_car_id, appUtils,
                mDeliveryType,
                paymentType!!.toInt(),
                mAgentParam,
                paymentMethodNonce.nonce,
                textConfig?.braintree?.toLowerCase(Locale.getDefault()) ?: "",
                redeemedAmt,
                imageList ?: mutableListOf(),
                delivery_note,
                mTipCharges,
                restServiceTax,
                mQuestionList,
                questionAddonPrice,
                productData?.appType ?: 0,
                mSelectedPayment,
                havePets,
                cleaner_in,
                "",
                "",
                isPaymentConfirm,
                isDonate,
                phoneNumber,
                scheduleData,
                null,
                isLoyaltyPointsApplied,
                "",
                null,
                isDineInWithFood,
                selectedTableData?.id.toString1(),
                tableLoadedFromScanner, mSubTotalCopy, mDropOffTimeParam = mDropOffDataParam,
                order_delivery_type = order_delivery_type,
                vehicle_number = "", isCutleryRequired = false,
                ipAddress = ipAddress,
                signature_menu_id = signature_menu_id,
                signature_plate_id = signature_plate_id, from_signature_menu = from_signature_menu,
                from_signature_plate = from_signature_plate, deliveryCompany = selectedDeliveryCompany)
    }


    override fun onResult(result: DropInResult?) {

    }

    override fun onError(exception: java.lang.Exception?) {

    }
    override fun onDeleteCard(savedCard: CardData, position: Int) {

    }

    override fun onCardClick(savedCard: CardData, position: Int) {
        save_card=1
        save_card_token=savedCard.card_token
       
    }
}