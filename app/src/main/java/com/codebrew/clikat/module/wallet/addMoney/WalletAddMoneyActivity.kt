//package com.codebrew.clikat.module.wallet.addMoney
//
//import android.app.Activity
//import android.app.Dialog
//import android.content.Intent
//import android.net.Uri
//import android.os.Bundle
//import android.provider.MediaStore
//import android.view.*
//import android.widget.*
//import androidx.appcompat.app.AppCompatActivity
//import androidx.core.content.FileProvider
//import androidx.databinding.DataBindingUtil
//import androidx.lifecycle.Observer
//import androidx.lifecycle.ViewModelProviders
//import ch.datatrans.payment.PaymentProcessState
//import ch.datatrans.payment.android.IPaymentProcessStateListener
//import ch.datatrans.payment.android.PaymentProcessAndroid
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
//import com.codebrew.clikat.app_utils.dialogintrface.ImageDialgFragment
//import com.codebrew.clikat.app_utils.extension.dimBehind
//import com.codebrew.clikat.app_utils.extension.launchActivity
//import com.codebrew.clikat.app_utils.extension.onSnackbar
//import com.codebrew.clikat.base.BaseActivity
//import com.codebrew.clikat.data.DataManager
//import com.codebrew.clikat.data.constants.AppConstants
//import com.codebrew.clikat.data.constants.NetworkConstants
//import com.codebrew.clikat.data.constants.PrefenceConstants
//import com.codebrew.clikat.data.model.api.*
//import com.codebrew.clikat.data.model.api.Currency
//import com.codebrew.clikat.data.model.api.distance_matrix.DistanceMatrix
//import com.codebrew.clikat.data.model.api.tap_payment.Transaction
//import com.codebrew.clikat.data.model.others.CustomPayModel
//import com.codebrew.clikat.data.model.others.PaymentEvent
//import com.codebrew.clikat.data.model.others.SaveCardInputModel
//import com.codebrew.clikat.data.preferences.PreferenceHelper
//import com.codebrew.clikat.databinding.FragmentAddMoneyBinding
//import com.codebrew.clikat.databinding.PopupZellePaymentBinding
//import com.codebrew.clikat.di.ViewModelProviderFactory
//import com.codebrew.clikat.modal.PojoSignUp
//import com.codebrew.clikat.modal.other.AddtoCartModel
//import com.codebrew.clikat.modal.other.PromoCodeModel
//import com.codebrew.clikat.modal.other.SettingModel
//import com.codebrew.clikat.module.cart.*
//import com.codebrew.clikat.module.payment_gateway.PaymentWebViewActivity
//import com.codebrew.clikat.module.payment_gateway.adapter.PayListener
//import com.codebrew.clikat.module.payment_gateway.adapter.PaymentListAdapter
//import com.codebrew.clikat.module.payment_gateway.dialog_card.CardDialogFrag
//import com.codebrew.clikat.module.payment_gateway.savedcards.SaveCardsActivity
//import com.codebrew.clikat.preferences.DataNames
//import com.codebrew.clikat.utils.configurations.Configurations
//import com.facebook.FacebookSdk
//import com.google.android.gms.wallet.TransactionInfo
//import com.google.android.gms.wallet.WalletConstants
//import com.google.gson.Gson
//import com.google.gson.reflect.TypeToken
//import com.paytabs.paytabs_sdk.utils.PaymentParams
//import com.codebrew.clikat.intrface.ImageCallback
//import com.codebrew.clikat.modal.other.CheckPromoCodeParam
////import com.razorpay.Checkout
//import com.urway.paymentlib.UrwayPayment
//import dagger.android.AndroidInjector
//import dagger.android.DispatchingAndroidInjector
//import dagger.android.HasAndroidInjector
//import kotlinx.android.synthetic.main.fragment_add_money.*
//import lk.payhere.androidsdk.PHConfigs
//import lk.payhere.androidsdk.PHConstants
//import lk.payhere.androidsdk.PHMainActivity
//import lk.payhere.androidsdk.PHResponse
//import lk.payhere.androidsdk.model.InitRequest
//import lk.payhere.androidsdk.model.Item
//import lk.payhere.androidsdk.model.StatusResponse
//import org.greenrobot.eventbus.Subscribe
//import org.greenrobot.eventbus.ThreadMode
//import org.json.JSONObject
//import pub.devrel.easypermissions.AppSettingsDialog
//import pub.devrel.easypermissions.EasyPermissions
//import java.io.File
//import java.io.IOException
//import java.lang.reflect.Type
//import java.net.URLEncoder
//import java.util.*
//import javax.inject.Inject
//
//class WalletAddMoneyActivity : BaseActivity<FragmentAddMoneyBinding, CartViewModel>(), CartNavigator, View.OnClickListener,
//        ImageCallback, EasyPermissions.PermissionCallbacks, CardDialogFrag.onPaymentListener, HasAndroidInjector,
//        DropInResult.DropInResultListener, IPaymentProcessStateListener {
//
//    @Inject
//    lateinit var androidInjector: DispatchingAndroidInjector<Any>
//
//    private var mSelectedPayment: CustomPayModel? = null
//
//    @Inject
//    lateinit var imageUtils: ImageUtility
//
//    @Inject
//    lateinit var permissionFile: PermissionFile
//
//    @Inject
//    lateinit var factory: ViewModelProviderFactory
//    private var popup: PopupWindow? = null
//    private var mBinding: FragmentAddMoneyBinding? = null
//    private var mAdapter: PaymentListAdapter? = null
//    private var payment_gateways: ArrayList<String>? = arrayListOf()
//    private val imageDialog by lazy { ImageDialgFragment() }
//    private var featureList: ArrayList<SettingModel.DataBean.FeatureData>? = null
//    private var settingData: SettingModel.DataBean.SettingData? = null
//    private var ivImage: ImageView? = null
//
//    private val textConfig by lazy { appUtils.loadAppConfig(0).strings }
//    private var selectedCurrency: Currency?=null
//
//    @Inject
//    lateinit var dataManager: DataManager
//    private var photoFile: File? = null
//
//    @Inject
//    lateinit var prefHelper: PreferenceHelper
//
//    @Inject
//    lateinit var appUtils: AppUtils
//
//    private lateinit var mViewModel: CartViewModel
//    private var totalAmt: Float? = 0f
//
//    override fun getBindingVariable(): Int {
//        return BR.viewModel
//    }
//
//    override fun getViewModel(): CartViewModel {
//        mViewModel = ViewModelProviders.of(this, factory).get(CartViewModel::class.java)
//        return mViewModel
//    }
//
//    override fun getLayoutId(): Int {
//        return R.layout.fragment_add_money
//    }
//
//    private var mAuthorization: String? = null
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        viewModel.navigator = this
//        settingData = dataManager.getGsonValue(DataNames.SETTING_DATA, SettingModel.DataBean.SettingData::class.java)
//        selectedCurrency = dataManager.getGsonValue(PrefenceConstants.DEFAULT_CURRENCY_INF, Currency::class.java)
//        mBinding = viewDataBinding
//        viewDataBinding.color = Configurations.colors
//        viewDataBinding.strings = Configurations.strings
//
//        initialise()
//        setAdapter()
//        listeners()
//        walletApiObserver()
//        imageObserver()
//    }
//
//    private fun initialise() {
//        featureList = dataManager.getKeyValue(DataNames.FEATURE_DATA, PrefenceConstants.TYPE_STRING).toString().let {
//            val listType: Type = object : TypeToken<ArrayList<SettingModel.DataBean.FeatureData?>?>() {}.type
//            Gson().fromJson(it, listType)
//        }
//    }
//
//    private fun setAdapter() {
//        mAdapter = PaymentListAdapter(true, null, textConfig,selectedCurrency)
//        rvPaymentMethods.adapter = mAdapter
//
//        var mPaymentList = mAdapter?.settingLyt(this, featureList, textConfig)
//        mPaymentList = mAdapter?.filterList(payment_gateways, mPaymentList)
//        mAdapter?.submitItemList(mPaymentList)
//    }
//
//    private fun listeners() {
//        tvContinue?.setOnClickListener(this)
//        ivBack?.setOnClickListener(this)
//        mAdapter?.settingCallback(PayListener(
//                { customPayModel: CustomPayModel, i: Int ->
//                    if (customPayModel.addCard == true) {
//                        if (prefHelper.getCurrentUserLoggedIn()) {
//                            mSelectedPayment = customPayModel
//                            if (etAmount?.text?.toString()?.trim()?.isEmpty() == true)
//                                AppToasty.error(this, getString(R.string.enter_amount))
//                            else {
//                                totalAmt = etAmount.text.toString().toFloat()
//                                openPayment(customPayModel, i)
//                            }
//                        } else {
//                            val intent = Intent(this, appUtils.checkLoginActivity())
//                            startActivityForResult(intent, AppConstants.REQUEST_CARD_ADD)
//                        }
//                    } else {
//                        mAdapter?.changeIsSelected(i)
//                        mSelectedPayment = customPayModel
//                    }
//
//
//                }, {
//        }))
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
//    override fun onClick(p0: View?) {
//        when (p0?.id) {
//            R.id.ivBack -> onBackPressed()
//            R.id.tvContinue -> {
//                val amt = etAmount?.text.toString().trim()
//                if (amt.isEmpty()) {
//                    mBinding?.root?.onSnackbar(getString(R.string.enter_amount))
//                } else {
//                    totalAmt = amt.toFloat()
//                    fetchPaymentGateway()
//                }
//            }
//        }
//    }
//
//    @Subscribe(threadMode = ThreadMode.MAIN)
//    fun onMessageEvent(event: PaymentEvent?) {
//        if (event?.resultCode == 0) {
//            if (isNetworkConnected) {
//                mSelectedPayment?.keyId = event.gateway_unique_id
//                mSelectedPayment?.payment_token = "razorpay"
//                hitAddMoneyToWalletApi(null)
//            }
//        } else {
//            mBinding?.root?.onSnackbar(event?.message ?: "")
//        }
//    }
//
//
//    private fun fetchPaymentGateway() {
//        if (isNetworkConnected) {
//            when (mSelectedPayment?.payName) {
//                textConfig?.razor_pay -> {
//                    initRazorPay(mSelectedPayment)
//                }
//                getString(R.string.pay_pal) -> {
//                    initPaypal(mSelectedPayment)
//                }
//                getString(R.string.zelle), getString(R.string.creds_movil) ,getString(R.string.pipol_pay)-> {
//                    displayPopupWindow(window.decorView.findViewById(android.R.id.content), mSelectedPayment?.payement_front)
//                }
//                getString(R.string.paystack) -> {
//                    onlinePayStack(mSelectedPayment)
//                }
//                getString(R.string.saded) -> {
//                    getSadedPayment()
//                }
//                textConfig?.my_fatoorah -> {
//                    initMyFatooraGateway()
//                }
//                getString(R.string.mumybene) -> {
//                    showMumyBenePhoneDialog()
//                }
//                getString(R.string.windcave) -> {
//                    getWinCavePaymentUrl()
//                }
//                getString(R.string.mpaisa) -> {
//                    getMpaisaUrl()
//                }
//                getString(R.string.tap) -> {
//                    initTapGateway()
//                }
//                getString(R.string.elavon) -> {
//                    initEvalonGateway()
//                }
//                getString(R.string.payhere) -> {
//                    payHerePayment()
//                }
//                getString(R.string.cashapp) -> {
//                    displayPopupWindow(window.decorView.findViewById(android.R.id.content), mSelectedPayment?.payement_front)
//                }
//                textConfig?.braintree -> {
//                    launchDropIn()
//                }
//                getString(R.string.aamar_pay) -> {
//                    getAamarPaymentUrl()
//                }
//                getString(R.string.datatrans) -> {
//                    appUtils.startTransaction(totalAmt?.toDouble() ?: 0.0, this, this)
//                }
//                getString(R.string.pay_tabs) -> {
//                    getHyperPayUrl()
//                }
//                getString(R.string.teller) -> {
//                    getTelrPaymentUrl()
//                }
//                getString(R.string.hyper_pay) -> {
//                    getHyperPayUrl()
//                }
//                getString(R.string.sadad)->{
//                    getSadadQaUrl()
//                }
//                getString(R.string.pay_maya)->{
//                    getPayMayaUrl()
//                }
//                 getString(R.string.ur_way) -> {
//                    initialseUrWay()
//                }
//                else -> {
//                    if (mSelectedPayment?.addCard == true) {
//                        hitAddMoneyToWalletApi(null)
//                    } else {
//                        val promoData = dataManager.getGsonValue(DataNames.DISCOUNT_AMOUNT, PromoCodeModel.DataBean::class.java)
//                        totalAmt?.minus(promoData?.discountPrice ?: 0f)
//                        CardDialogFrag.newInstance(mSelectedPayment, totalAmt
//                                ?: 0f).show(supportFragmentManager, "paymentDialog")
//                    }
//                }
//            }
//        }
//    }
//    private fun getHyperPayUrl() {
//        val websiteUrl = dataManager.getKeyValue(PrefenceConstants.WEBSITE_LINK, PrefenceConstants.TYPE_STRING).toString()
//
//        val address = dataManager.getGsonValue(PrefenceConstants.ADRS_DATA, AddressBean::class.java)
//        val url= "$websiteUrl/hyper-pay-webview?amount=${totalAmt.toString()}&is_web_view=1&address=${Gson().toJson(address)}"
//
//        val intent = Intent(this, PaymentWebViewActivity::class.java).putExtra("payment_url", url)
//                .putExtra("payment_gateway", getString(R.string.hyper_pay))
//        startActivityForResult(intent, HYPERPAY_PAYMENT_REQUEST)
//    }
//
//    private fun initialseUrWay() {
//
//        val currency = dataManager.getGsonValue(PrefenceConstants.DEFAULT_CURRENCY_INF, Currency::class.java)
//        val userInfo = dataManager.getGsonValue(DataNames.USER_DATA, PojoSignUp::class.java)
//        val actionCode: String = "1"
//
//        val urWayPayment = UrwayPayment()
//
//        urWayPayment.makepaymentService(totalAmt.toString(), this, actionCode, "SAR",
//                "", "", "", "", "", "",
//                userInfo?.data?.email ?: "", "address", "", "", "", AppConstants.COUNTRY_ISO,
//                System.currentTimeMillis().toString(), "", "", "0")
//    }
//
//    private fun getSadadQaUrl() {
//        val websiteUrl = dataManager.getKeyValue(PrefenceConstants.WEBSITE_LINK, PrefenceConstants.TYPE_STRING).toString()
//
//        val userInfo = dataManager.getGsonValue(DataNames.USER_DATA, PojoSignUp::class.java)
//        val merchantId=mSelectedPayment?.payement_front?.find { it?.key=="merchent_id" }?.value
//        val secretKey=mSelectedPayment?.payement_front?.find { it?.key=="secret_key" }?.value
//
//        val amount= String.format("%.2f",totalAmt)
//        val url= "$websiteUrl/sadad/index.php?${"order_id="}${System.currentTimeMillis()}${"&item_name=IPHONE&email="}${userInfo?.data?.email}${"&phone_number="}${userInfo?.data?.mobile_no}${"&MERCHANT_ID="}${merchantId}${"&SECRET_KEY="}${secretKey}${"&amount="}$amount"
//        val intent = Intent(this, PaymentWebViewActivity::class.java).putExtra("payment_url", url)
//                .putExtra("payment_gateway",getString(R.string.sadad))
//        startActivityForResult(intent, SADAD_PAYMENT_REQUEST)
//    }
//
//    private fun getPayMayaUrl() {
//
//        val currency = dataManager.getGsonValue(PrefenceConstants.DEFAULT_CURRENCY_INF, Currency::class.java)
//        val hashMap = HashMap<String, String>()
//        hashMap["currency"] = currency?.currency_name ?: ""
//        hashMap["amount"] =  String.format("%.2f",totalAmt)
//        hashMap["userId"]=dataManager.getKeyValue(PrefenceConstants.USER_ID, PrefenceConstants.TYPE_STRING).toString()
//        hashMap["successUrl"] = "https://billing.royoapps.com/payment-success"
//        hashMap["failureUrl"] = "https://billing.royoapps.com/payment-error"
//
//        if (isNetworkConnected) {
//            mViewModel.getPaymayaUrl(hashMap)
//        }
//    }
//    private fun hitAddMoneyToWalletApi(savedCard: SaveCardInputModel?) {
//        if (isNetworkConnected) {
//            mViewModel.apiAddMoneyToWallet(mSelectedPayment, totalAmt.toString(), phoneNumber, savedCard)
//        }
//    }
//
//    private fun walletApiObserver() {
//        // Create the observer which updates the UI.
//        val observer = Observer<Any> { resource ->
//            AppToasty.success(this, getString(R.string.amount_added_success))
//            setResult(Activity.RESULT_OK, Intent())
//            finish()
//        }
//        // Observe the LiveData, passing in this activity as the LifecycleOwner and the observer.
//        viewModel.addMoneyToWallet.observe(this, observer)
//    }
//
//    private fun initEvalonGateway() {
//        if (isNetworkConnected) {
//            mViewModel.evalonPayemntApi(totalAmt.toString())
//        }
//    }
//
//    private fun initTapGateway() {
//        if (isNetworkConnected) {
//
//            val signUp = prefHelper.getGsonValue(DataNames.USER_DATA, PojoSignUp::class.java)
//
//            val currency = prefHelper.getGsonValue(PrefenceConstants.DEFAULT_CURRENCY_INF, Currency::class.java)
//
//            FacebookSdk.getApplicationContext().resources.configuration.locale.displayCountry
//            val hashMap = hashMapOf<String, String>("email" to signUp?.data?.email.toString(),
//                    "phone" to signUp?.data?.mobile_no.toString(),
//                    "name" to signUp?.data?.firstname.toString(),
//                    "currency" to currency?.currency_name.toString(),
//                    "country_code" to DateTimeUtils.timeLocale.displayCountry,
//                    "post_url" to "${prefHelper.getKeyValue(PrefenceConstants.WEBSITE_LINK, PrefenceConstants.TYPE_STRING)}/success",
//                    "redirect_url" to "${prefHelper.getKeyValue(PrefenceConstants.WEBSITE_LINK, PrefenceConstants.TYPE_STRING)}/success",
//                    "amount" to totalAmt.toString())
//
//            mViewModel.tapPayemntApi(hashMap)
//        }
//    }
//
//    private fun onlinePayStack(mSelectedPayment: CustomPayModel?) {
//        CardDialogFrag.newInstance(mSelectedPayment, totalAmt
//                ?: 0f).show(supportFragmentManager, "paymentDialog")
//
//    }
//
//    override fun paymentToken(token: String, paymentMethod: String, savedCard: SaveCardInputModel?) {
//        mSelectedPayment?.keyId = token
//        hitAddMoneyToWalletApi(savedCard)
//    }
//
//    private fun getSadedPayment() {
//        val userInfo = dataManager.getGsonValue(DataNames.USER_DATA, PojoSignUp::class.java)
//        if (isNetworkConnected) {
//            mViewModel.getSaddedPaymentUrl(userInfo?.data?.email ?: "", userInfo?.data?.firstname
//                    ?: "", totalAmt.toString())
//        }
//    }
//
//    override fun getAamarPayPaymentSuccess(data: AddCardResponseData?) {
//        val intent = Intent(this, PaymentWebViewActivity::class.java).putExtra("paymentData", data)
//                .putExtra("payment_gateway", getString(R.string.aamar_pay))
//        startActivityForResult(intent, AAMAR_PAY_PAYMENT_REQUEST)
//    }
//
//    override fun getTelrPaymentSuccess(data: AddCardResponseData?) {
//        mSelectedPayment?.keyId = data?.order?.ref
//        val intent = Intent(this, PaymentWebViewActivity::class.java).putExtra("paymentData", data)
//                .putExtra("payment_gateway", getString(R.string.teller))
//        startActivityForResult(intent, TELR_PAYMENT_REQUEST)
//    }
//
//    override fun getPayMayaUrl(data: AddCardResponseData?) {
//        launchActivity<PaymentWebViewActivity>(PAY_MAYA_REQUEST) {
//            putExtra("paymentData", data)
//            putExtra("payment_gateway", getString(R.string.pay_maya))
//        }
//    }
//
//    private fun getTelrPaymentUrl() {
//        val currency = dataManager.getGsonValue(PrefenceConstants.DEFAULT_CURRENCY_INF, Currency::class.java)
//        if (isNetworkConnected) {
//            mViewModel.getTelrPaymentUrl(totalAmt.toString(),currency?.currency_name?:"")
//        }
//    }
//
//    private fun getAamarPaymentUrl() {
//        val userInfo = dataManager.getGsonValue(DataNames.USER_DATA, PojoSignUp::class.java)
//        val currency = dataManager.getGsonValue(PrefenceConstants.DEFAULT_CURRENCY_INF, Currency::class.java)
//        val hashMap = HashMap<String, String>()
//        hashMap["currency"] = currency?.currency_name ?: ""
//        hashMap["email"] = userInfo?.data?.email ?: ""
//        hashMap["phone"] = userInfo?.data?.mobile_no ?: ""
//        hashMap["name"] = userInfo?.data?.firstname ?: ""
//        hashMap["amount"] = totalAmt.toString()
//        hashMap["desc"] = "Order Detail"
//        val address = dataManager.getGsonValue(PrefenceConstants.ADRS_DATA, AddressBean::class.java)
//        hashMap["address"] = appUtils.getAddressFormat(address) ?: ""
//
//        if (isNetworkConnected) {
//            mViewModel.getAamarPayUrl(hashMap)
//        }
//    }
//
//    private fun initMyFatooraGateway() {
//        val currency = dataManager.getGsonValue(PrefenceConstants.DEFAULT_CURRENCY_INF, Currency::class.java)
//        if (isNetworkConnected) {
//            mViewModel.getMyFatoorahPaymentUrl(currency?.currency_name ?: "", totalAmt.toString())
//        }
//    }
//
//
//    override fun getSaddedPaymentSuccess(data: AddCardResponseData?) {
//        mSelectedPayment?.keyId = data?.transaction_reference
//        val intent = Intent(this, PaymentWebViewActivity::class.java).putExtra("paymentData", data)
//        startActivityForResult(intent, SADDED_PAYMENT_REQUEST)
//    }
//
//    override fun getmPaisaPaymentSuccess(data: AddCardResponseData?) {
//        mSelectedPayment?.keyId = data?.rID
//        val intent = Intent(this, PaymentWebViewActivity::class.java).putExtra("paymentData", data)
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
//                DropInResult.fetchDropInResult(this, mAuthorization, this)
//            }
//        } catch (e: InvalidArgumentException) {
//            AppToasty.error(this, e.message.toString())
//        }
//    }
//
//    override fun userBlocked() {
//
//    }
//
//    override fun userUnBlock() {
//
//    }
//
//    override fun onProfileUpdate() {
//
//    }
//
//
//    override fun getMyFatoorahPaymentSuccess(data: AddCardResponseData?) {
//        val intent = Intent(this, PaymentWebViewActivity::class.java).putExtra("paymentData", data)
//                .putExtra("payment_gateway", textConfig?.my_fatoorah)
//        startActivityForResult(intent, MY_FATOORAH_PAYMENT_REQUEST)
//    }
//
//    override fun onTapPayment(transaction: Transaction) {
//        launchActivity<PaymentWebViewActivity>(TAP_PAYMENT_REQUEST)
//        {
//            putExtra("payment_url", transaction.url)
//            putExtra("payment_gateway", getString(R.string.tap))
//        }
//    }
//
//    override fun onEvalonPayment(data: String) {
//
//        val paymentUrl = if (BuildConfig.DEBUG) {
//            NetworkConstants.EVALON_TEST_LINK
//        } else {
//            NetworkConstants.EVALON_PROD_LINK
//        }
//        launchActivity<PaymentWebViewActivity>(EVALON_PAYMENT_REQUEST)
//        {
//            putExtra("payment_url", "${paymentUrl}${URLEncoder.encode(data, "utf-8")}")
//            putExtra("payment_gateway", getString(R.string.tap))
//        }
//    }
//
//
//    private fun initPaypal(mSelectedPayment: CustomPayModel?) {
//        val dropInRequest: DropInRequest = DropInRequest()
//                .clientToken(mSelectedPayment?.keyId)
//        startActivityForResult(dropInRequest.getIntent(this), PAYPALREQUEST)
//    }
//
//    override fun getWindCavePaymentSuccess(data: Result?) {
//        val intent = Intent(this, PaymentWebViewActivity::class.java).putExtra("paymentData", data?.Request)
//                .putExtra("payment_gateway", getString(R.string.windcave))
//        startActivityForResult(intent, SADDED_PAYMENT_REQUEST)
//    }
//
//
//
//    private fun initRazorPay(mSelectedPayment: CustomPayModel?) {
//
//        val userInfo = dataManager.getGsonValue(DataNames.USER_DATA, PojoSignUp::class.java)
//
//        Checkout.preload(this)
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
//            options.put("amount", totalAmt?.times(100))
//            options.put("payment_capture", true)
//            // options.put("order_id", orderId)
//
//            val preFill = JSONObject()
//            preFill.put("email", userInfo?.data?.email)
//            preFill.put("contact", userInfo?.data?.mobile_no)
//
//            options.put("prefill", preFill)
//
//            co.open(this, options)
//
//        } catch (e: Exception) {
//            e.printStackTrace()
//        }
//    }
//
//
//    private fun getWinCavePaymentUrl() {
//        val currency = dataManager.getGsonValue(PrefenceConstants.DEFAULT_CURRENCY_INF, Currency::class.java)
//        val userInfo = dataManager.getGsonValue(DataNames.USER_DATA, PojoSignUp::class.java)
//        val address = dataManager.getGsonValue(PrefenceConstants.ADRS_DATA, AddressBean::class.java)
//        if (isNetworkConnected) {
//            mViewModel.getWindCaveUrl(String.format("%.2f", totalAmt), currency?.currency_name
//                    ?: "",
//                    userInfo?.data?.email ?: "", appUtils.getAddressFormat(address))
//        }
//    }
//
//    private fun getMpaisaUrl() {
//        if (isNetworkConnected) {
//            mViewModel.getMPaisaPaymentUrl(String.format("%.2f", totalAmt))
//        }
//    }
//
//    var phoneNumber: String? = null
//    private fun showMumyBenePhoneDialog() {
//        val dialog = Dialog(this, android.R.style.Theme_Material_Dialog_Alert)
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
//                AppToasty.error(this, getString(R.string.enter_phone))
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
//        val currency = dataManager.getGsonValue(PrefenceConstants.DEFAULT_CURRENCY_INF, Currency::class.java)
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
//
//        val address = dataManager.getGsonValue(PrefenceConstants.ADRS_DATA, AddressBean::class.java)
//        req.customer.address.address = appUtils.getAddressFormat(address) ?: ""
//        req.customer.address.city = address?.address_line_1 ?: ""
//        req.customer.address.country = appUtils.getAddress(address?.latitude?.toDouble()
//                ?: 0.0, address?.longitude?.toDouble() ?: 0.0)?.countryName ?: ""
//        req.items.add(Item(null, "Items", 1, amt.toDouble()))
//
//        /*//Optional Params
//        req.getCustomer().getDeliveryAddress().setAddress("No.2, Kandy Road")
//        req.getCustomer().getDeliveryAddress().setCity("Kadawatha")
//        req.getCustomer().getDeliveryAddress().setCountry("Sri Lanka")
//        req.getItems().add(Item(null, "Door bell wireless", 1, totalAmt))*/
//
//        val intent = Intent(this, PHMainActivity::class.java)
//        intent.putExtra(PHConstants.INTENT_EXTRA_DATA, req)
//        PHConfigs.setBaseUrl(PHConfigs.SANDBOX_URL)
//        startActivityForResult(intent, PAYHERE_REQUEST) //unique request ID like private final static int PAYHERE_REQUEST = 11010;
//
//    }
//
//    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
//        super.onActivityResult(requestCode, resultCode, data)
//        if (requestCode == AppSettingsDialog.DEFAULT_SETTINGS_REQ_CODE) {
//            // Do something after user returned from app settings screen, like showing a Toast.
//            mBinding?.root?.onSnackbar(getString(R.string.returned_from_app_settings_to_activity))
//        }
//
//        if (resultCode == AppCompatActivity.RESULT_OK && requestCode == AppConstants.CameraPicker) {
//
//            if (isNetworkConnected) {
//                if (photoFile?.isRooted == true) {
//                    loadDocImage(photoFile?.absolutePath
//                            ?: "")
//                    popup?.dismiss()
//                    mViewModel.validateZelleImage(imageUtils.compressImage(photoFile?.absolutePath
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
//                    val cursor = contentResolver?.query(selectedImage!!, filePathColumn, null, null, null)
//                    // Move to first row
//                    cursor?.moveToFirst()
//                    //Get the column index of MediaStore.Images.Media.DATA
//                    val columnIndex = cursor?.getColumnIndex(filePathColumn[0])
//                    //Gets the String value in the column
//                    val imgDecodableString = cursor?.getString(columnIndex ?: 0)
//                    cursor?.close()
//
//                    loadDocImage(imgDecodableString ?: "")
//
//                    if (imgDecodableString?.isNotEmpty() == true) {
//                        popup?.dismiss()
//                        mViewModel.validateZelleImage(imageUtils.compressImage(imgDecodableString))
//                    }
//                }
//            }
//        }else  if (requestCode == URWAY_PAYMENT_REQUEST) {
//            if (data != null) {
//                val  jsonObj = JSONObject(data.getStringExtra("MESSAGE")?:"")
//                if(jsonObj.get("ResponseCode").toString()=="000")
//                {
//                    mSelectedPayment?.keyId= jsonObj.get("TranId").toString()
//                    hitAddMoneyToWalletApi(null)
//                }
//                else
//                    AppToasty.error(this,  getString(R.string.payment_failed))
//            }
//        }
//        else if (requestCode == PAYHERE_REQUEST) {
//            if (data != null && data.hasExtra(PHConstants.INTENT_EXTRA_RESULT)) {
//                val response: PHResponse<StatusResponse> = data.getSerializableExtra(PHConstants.INTENT_EXTRA_RESULT) as PHResponse<StatusResponse>
//                if (response.isSuccess) {
//                    val msg = "Activity result:" + response.data.toString()
//                    mSelectedPayment?.keyId = response.data?.paymentNo.toString()
//                    hitAddMoneyToWalletApi(null)
//                } else {
//                    val msg = "Result:$response"
//
//                    AppToasty.error(this, getString(R.string.payment_failed))
//                }
//            }
//        } else if (requestCode == PAYPALREQUEST) {
//            when (resultCode) {
//                Activity.RESULT_OK -> {
//                    val result: DropInResult = data?.getParcelableExtra(DropInResult.EXTRA_DROP_IN_RESULT)!!
//
//                    mSelectedPayment?.keyId = result.paymentMethodNonce?.nonce
//                    mSelectedPayment?.payment_token = "venmo"
//                    hitAddMoneyToWalletApi(null)
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
//                    AppToasty.success(this, getString(R.string.payment_done_successful))
//                    if (mSelectedPayment?.payName == getString(R.string.windcave) || mSelectedPayment?.payName == getString(R.string.mpaisa)) {
//                        if (data != null && data.hasExtra("paymentId"))
//                            mSelectedPayment?.keyId = data.getStringExtra("paymentId")
//                    }
//                    hitAddMoneyToWalletApi(null)
//                    // use the result to update your UI and send the payment method nonce to your server
//                }
//                else -> {
//                    val message = if (data?.hasExtra("showError") == true)
//                        getString(R.string.payment_failed)
//                    else
//                        getString(R.string.payment_unsuccessful)
//                    AppToasty.error(this, message)
//                }
//            }
//
//        } else if (requestCode == MY_FATOORAH_PAYMENT_REQUEST || requestCode == TAP_PAYMENT_REQUEST || requestCode == EVALON_PAYMENT_REQUEST ||
//                requestCode == AAMAR_PAY_PAYMENT_REQUEST || requestCode == TELR_PAYMENT_REQUEST || requestCode== HYPERPAY_PAYMENT_REQUEST||
//                requestCode==SADAD_PAYMENT_REQUEST || requestCode== PAY_MAYA_REQUEST) {
//            when (resultCode) {
//                Activity.RESULT_OK -> {
//                    AppToasty.success(this, getString(R.string.payment_done_successful))
//                    if (data != null && data.hasExtra("paymentId"))
//                        mSelectedPayment?.keyId = data.getStringExtra("paymentId")
//                    hitAddMoneyToWalletApi(null)
//
//                    // use the result to update your UI and send the payment method nonce to your server
//                }
//                else -> {
//                    val message = if (data?.hasExtra("showError") == true)
//                        getString(R.string.payment_failed)
//                    else
//                        getString(R.string.payment_unsuccessful)
//                    AppToasty.error(this, message)
//                }
//            }
//        } else if (resultCode == AppCompatActivity.RESULT_OK && requestCode == AppConstants.REQUEST_PAYMENT_DEBIT_CARD) {
//            if (data != null) {
//                mSelectedPayment = data.getParcelableExtra("payItem")
//                val pos = data.getIntExtra("pos", 0)
//                mAdapter?.changeIsSelected(pos)
//            }
//        } else if (requestCode == REQUEST_CODE_BRAINTREE) {
//            when (resultCode) {
//                AppCompatActivity.RESULT_OK -> {
//                    val result: DropInResult? = data?.getParcelableExtra(DropInResult.EXTRA_DROP_IN_RESULT)
//                    displayResult(result?.paymentMethodNonce, result?.deviceData)
//                }
//                AppCompatActivity.RESULT_CANCELED -> {
//                    //show error message
//                    val x = (data?.getSerializableExtra(DropInActivity.EXTRA_ERROR) as java.lang.Exception?)?.message
//                    AppToasty.error(this, x.toString())
//                }
//            }
//        } else if (resultCode == Activity.RESULT_OK && requestCode == PaymentParams.PAYMENT_REQUEST_CODE) {
//            mSelectedPayment?.keyId = data?.getStringExtra(PaymentParams.TRANSACTION_ID) ?: ""
//            hitAddMoneyToWalletApi(null)
//        }
//    }
//
//    private fun imageObserver() {
//        // Create the observer which updates the UI.
//        val catObserver = Observer<String> { resource ->
//            mSelectedPayment?.keyId = resource
//            if (popup?.isShowing == true) {
//                popup?.dismiss()
//            }
//            hitAddMoneyToWalletApi(null)
//        }
//
//        // Observe the LiveData, passing in this activity as the LifecycleOwner and the observer.
//        mViewModel.imageLiveData.observe(this, catObserver)
//    }
//
//    private fun openPayment(it: CustomPayModel?, pos: Int) {
//        val intent = Intent(this, SaveCardsActivity::class.java)
//        intent.putExtra("amount", totalAmt)
//        intent.putExtra("payItem", it)
//        intent.putExtra("pos", pos)
//        startActivityForResult(intent, AppConstants.REQUEST_PAYMENT_DEBIT_CARD)
//    }
//
//    private fun displayPopupWindow(anchorView: View, payementFront: List<SettingModel.DataBean.KeyValueFront?>?) {
//
//        val textConfig by lazy { Configurations.strings }
//
//        popup = PopupWindow(anchorView)
//        val binding = DataBindingUtil.inflate<PopupZellePaymentBinding>(LayoutInflater.from(this), R.layout.popup_zelle_payment, null, false)
//        binding.color = Configurations.colors
//        binding.textConfig = textConfig
//        popup?.contentView = binding.root
//
//
//        val tvEmail = binding.root.findViewById<TextView>(R.id.tv_email)
//        val tvPhone = binding.root.findViewById<TextView>(R.id.tv_phone)
//        val choosePay = binding.root.findViewById<Button>(R.id.btn_choose_doc)
//        ivImage = binding.root.findViewById(R.id.iv_doc)
//
//        tvEmail.text = "1. ${textConfig.email?:getString(R.string.email)} ${payementFront?.find { it?.key == "email" }?.value ?: ""}"
//        tvPhone.text = "2. ${textConfig.shareScreenshot ?: getString(R.string.phoneno_tag, payementFront?.find { it?.key == "phone_number" }?.value ?: "")}"
//
//        choosePay.setOnClickListener {
//            uploadImage()
//        }
//
//        // Set content width and height
//        popup?.height = WindowManager.LayoutParams.WRAP_CONTENT
//        popup?.width = WindowManager.LayoutParams.MATCH_PARENT
//        popup?.isOutsideTouchable = true
//        popup?.isFocusable = true
//
//
//
//        popup?.showAtLocation(anchorView, Gravity.CENTER, 0, 0)
//
//        popup?.dimBehind()
//
//    }
//
//    private fun uploadImage() {
//        if (permissionFile.hasCameraPermissions(this)) {
//            if (isNetworkConnected) {
//                showImagePicker()
//            }
//        } else {
//            permissionFile.cameraAndGalleryActivity(this)
//        }
//    }
//
//    private fun loadDocImage(image: String) {
//
//        if (ivImage != null && image.isNotEmpty()) {
//            Glide.with(this).load(image).into(ivImage!!)
//        }
//    }
//
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
//                supportFragmentManager,
//                "image_picker"
//        )
//    }
//
//    override fun onPdf() {
//
//    }
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
//            takePictureIntent.resolveActivity(packageManager!!)?.also {
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
//                            this,
//                            packageName ?: "",
//                            it)
//
//                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
//                    startActivityForResult(takePictureIntent, AppConstants.CameraPicker)
//                }
//            }
//        }
//    }
//
//
//    override fun onUpdateCart() { /*do nothing*/
//    }
//
//    override fun onRefreshCartError() { /*do nothing*/
//    }
//
//    override fun onCartAdded(cartdata: AddtoCartModel.CartdataBean?) { /*do nothing*/
//    }
//
//    override fun onOrderPlaced(data: ArrayList<Int>) { /*do nothing*/
//    }
//
//    override fun onValidatePromo(data: PromoCodeModel.DataBean, param: CheckPromoCodeParam?) { /*do nothing*/
//    }
//
//    override fun onRefreshCart(mCartData: CartData?) { /*do nothing*/
//    }
//
//    override fun onCalculateDistance(value: DistanceMatrix?, supplierId: Int?) { /*do nothing*/
//    }
//
//    override fun onAddress(data: DataBean?) { /*do nothing*/
//    }
//
//    override fun onReferralAmt(value: Float?) { /*do nothing*/
//    }
//
//    override fun androidInjector(): AndroidInjector<Any> {
//        return androidInjector
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
//
//    override fun onResult(result: DropInResult?) {
//
//    }
//
//    override fun onError(exception: java.lang.Exception?) {
//
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
//        startActivityForResult(dropInRequest.getIntent(this), REQUEST_CODE_BRAINTREE)
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
//    override fun paymentProcessStateChanged(p0: PaymentProcessAndroid?) {
//        when (p0?.state) {
//            PaymentProcessState.BEFORE_COMPLETION -> {
//                //do nothing
//            }
//            PaymentProcessState.COMPLETED -> {
//                mSelectedPayment?.keyId = p0.transactionId
//                hitAddMoneyToWalletApi(null)
//            }
//            PaymentProcessState.CANCELED -> {
//                runOnUiThread {
//                    AppToasty.error(this, getString(R.string.payment_cancelled))
//                }
//            }
//            PaymentProcessState.ERROR -> {
//                runOnUiThread {
//                    AppToasty.error(this, p0.exception.toString())
//                }
//            }
//            else -> {
//            }
//        }
//    }
//
//}