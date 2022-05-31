package com.codebrew.clikat.app_utils

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import com.codebrew.clikat.BuildConfig
import com.codebrew.clikat.R
import com.codebrew.clikat.data.DataManager
import com.codebrew.clikat.data.constants.AppConstants
import com.codebrew.clikat.data.constants.PrefenceConstants
import com.codebrew.clikat.data.model.api.AddressBean
import com.codebrew.clikat.data.model.api.Currency
import com.codebrew.clikat.data.model.others.CustomPayModel
import com.codebrew.clikat.data.preferences.PreferenceHelper
import com.codebrew.clikat.modal.PojoSignUp
import com.codebrew.clikat.modal.other.BillingAddress
import com.codebrew.clikat.modal.other.PromoCodeModel
import com.codebrew.clikat.module.cart.*
import com.codebrew.clikat.module.payment_gateway.PaymentWebViewActivity
import com.codebrew.clikat.module.payment_gateway.dialog_card.CardDialogFrag
import com.codebrew.clikat.preferences.DataNames
import com.google.gson.Gson
import com.paytabs.paytabs_sdk.utils.PaymentParams
//import com.razorpay.Checkout
import com.urway.paymentlib.UrwayPayment
import lk.payhere.androidsdk.PHConfigs
import lk.payhere.androidsdk.PHConstants
import lk.payhere.androidsdk.PHMainActivity
import lk.payhere.androidsdk.PHResponse
import lk.payhere.androidsdk.model.InitRequest
import lk.payhere.androidsdk.model.Item
import lk.payhere.androidsdk.model.StatusResponse
import org.json.JSONObject
import javax.inject.Inject
import kotlin.math.roundToInt


class PaymentUtil @Inject constructor(private val mContext: Context) : AppUtils.OnHyperPayChanges {

    @Inject
    lateinit var mPreferenceHelper: PreferenceHelper

    @Inject
    lateinit var appUtils: AppUtils

    @Inject
    lateinit var dataManager: DataManager

    private var mTotalAmt: Double? = null
    private var mViewModel: CartViewModel? = null
    private var mFragContext: Fragment? = null
    private var mAcitivityContext: FragmentActivity? = null
    private var mSelectedPayment: CustomPayModel? = null
    private lateinit var adrsData: AddressBean
    private var context: Context? = null

    private val textConfig by lazy { appUtils.loadAppConfig(0).strings }

    fun checkPaymentMethodActivity(mFragContext: FragmentActivity, mCartModel: CartViewModel?, mSelectedPayment: CustomPayModel?, mTotalAmt: Double, adrsData: AddressBean) {
        this.mAcitivityContext = mFragContext
        paymentGateways(mCartModel, mSelectedPayment, mTotalAmt, adrsData)
    }

    fun checkPaymentMethod(mFragContext: Fragment, mCartModel: CartViewModel?, mSelectedPayment: CustomPayModel?, mTotalAmt: Double, adrsData: AddressBean) {
        this.mFragContext = mFragContext
        paymentGateways(mCartModel, mSelectedPayment, mTotalAmt, adrsData)
    }

    private fun paymentGateways(mCartModel: CartViewModel?, mSelectedPayment: CustomPayModel?, mTotalAmt: Double, adrsData: AddressBean) {
        context = if (mFragContext != null) mFragContext?.activity else mAcitivityContext
        this.mViewModel = mCartModel
        this.mTotalAmt = mTotalAmt
        this.adrsData = adrsData
        this.mSelectedPayment = mSelectedPayment

        when {
            mSelectedPayment?.payName == textConfig?.razor_pay -> {
//                initRazorPay(mSelectedPayment)
            }

            mSelectedPayment?.payName == mContext.getString(R.string.paystack) -> {
                onlinePayStack(mSelectedPayment)
            }
            mSelectedPayment?.payName == mContext.getString(R.string.saded) -> {
                getSadedPayment()
            }
            mSelectedPayment?.payName == textConfig?.my_fatoorah -> {
                initMyFatooraGateway()
            }
            mSelectedPayment?.payName == mContext.getString(R.string.windcave) -> {
                getWinCavePaymentUrl()
            }
            mSelectedPayment?.payName == mContext.getString(R.string.mpaisa) -> {
                getMpaisaUrl()
            }
            mSelectedPayment?.payName == mContext.getString(R.string.tap) -> {
                initTapGateway()
            }
            mSelectedPayment?.payName == mContext.getString(R.string.elavon) -> {
                initEvalonGateway()
            }
            mSelectedPayment?.payName == mContext.getString(R.string.payhere) -> {
                payHerePayment()
            }
            mSelectedPayment?.payName == textConfig?.braintree -> {
                mViewModel?.getBrainTreeClientToken()
            }
            mSelectedPayment?.payName == mContext.getString(R.string.pay_tabs) -> {
                if (mFragContext != null)
                    mFragContext?.activity?.let { appUtils.showBillingData(it, mSelectedPayment, mTotalAmt) }
                else
                    mAcitivityContext?.let { appUtils.showBillingData(it, mSelectedPayment, mTotalAmt) }
            }
            mSelectedPayment?.payName == mContext.getString(R.string.thawani) -> {
                getThawaniPaymentUrl()
            }
            mSelectedPayment?.payName == mContext.getString(R.string.teller) -> {
                getTelrPaymentUrl()
            }
            mSelectedPayment?.payName == mContext.getString(R.string.aamar_pay) -> {
                getAamarPaymentUrl()
            }
            mSelectedPayment?.payName == mContext.getString(R.string.hyper_pay) -> {
                getHyperPayUrl()
            }
            mSelectedPayment?.payName == mContext.getString(R.string.sadad) -> {
                getSadadQaUrl()
            }

            mSelectedPayment?.payName == mContext.getString(R.string.trans_bank) -> {
                getTransBankUrl()
            }

            mSelectedPayment?.payName == mContext.getString(R.string.pay_maya) -> {
                getPayMayaUrl()
            }
            mSelectedPayment?.payName == mContext.getString(R.string.ur_way) -> {
                initialseUrWay()
            }


            mSelectedPayment?.addCard == false -> {
                val promoData = dataManager.getGsonValue(DataNames.DISCOUNT_AMOUNT, PromoCodeModel.DataBean::class.java)
                mTotalAmt.minus(promoData?.discountPrice ?: 0f)

                mSelectedPayment.let {
                    val fragmentManager = if (mFragContext != null) mFragContext?.childFragmentManager
                    else mAcitivityContext?.supportFragmentManager
                    if (fragmentManager != null) {
                        CardDialogFrag.newInstance(it, mTotalAmt.toFloat()).show(
                                fragmentManager, "paymentDialog")
                    }
                }
            }
        }
    }

    private fun initialseUrWay() {
        val context = if (mFragContext != null) mFragContext?.activity else mAcitivityContext
        val currency = dataManager.getGsonValue(PrefenceConstants.DEFAULT_CURRENCY_INF, Currency::class.java)
        val userInfo = dataManager.getGsonValue(DataNames.USER_DATA, PojoSignUp::class.java)
        val actionCode: String = "1"

        val urWayPayment = UrwayPayment()
        val address = dataManager.getGsonValue(PrefenceConstants.ADRS_DATA, AddressBean::class.java)
       val countryCode= appUtils.getAddress(address?.latitude?.toDouble()
                ?: 0.0, address?.longitude?.toDouble() ?: 0.0)?.countryCode ?: ""

        urWayPayment.makepaymentService(mTotalAmt.toString(), context, actionCode, "SAR",
                "", "", "", "", "", "",
                userInfo?.data?.email ?: "", "address", "", "", "", countryCode,
                System.currentTimeMillis().toString(), "", "", "0")
    }


    override fun onHyperPayChanges(data: BillingAddress) {

    }

    private fun getSadadQaUrl() {
        val websiteUrl = dataManager.getKeyValue(PrefenceConstants.WEBSITE_LINK, PrefenceConstants.TYPE_STRING).toString()

        val userInfo = dataManager.getGsonValue(DataNames.USER_DATA, PojoSignUp::class.java)
        val merchantId = mSelectedPayment?.payement_front?.find { it?.key == "merchent_id" }?.value
        val secretKey = mSelectedPayment?.payement_front?.find { it?.key == "secret_key" }?.value

        val amount = String.format("%.2f", mTotalAmt)
        val url = "$websiteUrl/sadad/index.php?${"order_id="}${System.currentTimeMillis()}${"&item_name=IPHONE&email="}${userInfo?.data?.email}${"&phone_number="}${userInfo?.data?.mobile_no}${"&MERCHANT_ID="}${merchantId}${"&SECRET_KEY="}${secretKey}${"&amount="}$amount"
        val intent = Intent(mContext, PaymentWebViewActivity::class.java).putExtra("payment_url", url)
                .putExtra("payment_gateway", mContext.getString(R.string.sadad))
        startActivity(intent, SADAD_PAYMENT_REQUEST)
    }

    private fun getTransBankUrl() {
        val websiteUrl = dataManager.getKeyValue(PrefenceConstants.WEBSITE_LINK, PrefenceConstants.TYPE_STRING).toString()
        val userInfo = dataManager.getGsonValue(DataNames.USER_DATA, PojoSignUp::class.java)

        val amount = String.format("%.2f", mTotalAmt)
        val url = "$websiteUrl/transbank/index.php?${"amount="}${amount}${"&order="}${System.currentTimeMillis()}${"&session="}${System.currentTimeMillis()}${"&return_url="}${websiteUrl}${"/transbank/result.php&final_url="}${websiteUrl}${"/transbank/result.php"}${"&unique_id"}${BuildConfig.CLIENT_CODE}"
        val intent = Intent(mContext, PaymentWebViewActivity::class.java).putExtra("payment_url", url)
                .putExtra("payment_gateway", mContext.getString(R.string.trans_bank))
        startActivity(intent, TRANS_BANK_PAYMENT_REQUEST)

    }

//    private fun initRazorPay(mSelectedPayment: CustomPayModel?) {
//
//        val userInfo = dataManager.getGsonValue(DataNames.USER_DATA, PojoSignUp::class.java)
//        val currency = dataManager.getGsonValue(PrefenceConstants.DEFAULT_CURRENCY_INF, Currency::class.java)
//
//        Checkout.preload(mContext)
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
//            options.put("currency", currency?.currency_name ?: "INR")
//            options.put("amount", mTotalAmt?.times(100)?.roundToInt())
//            options.put("payment_capture", true)
//            // options.put("order_id", orderId)
//
//            val preFill = JSONObject()
//            preFill.put("email", userInfo?.data?.email)
//            preFill.put("contact", userInfo?.data?.mobile_no)
//
//            options.put("prefill", preFill)
//
//            if (mFragContext != null)
//                co.open(mFragContext?.activity, options)
//            else co.open(mAcitivityContext, options)
//
//
//        } catch (e: Exception) {
//            e.printStackTrace()
//        }
//    }

    private fun onlinePayStack(mSelectedPayment: CustomPayModel?) {
        val fragmentManager = if (mFragContext != null) mFragContext?.childFragmentManager
        else mAcitivityContext?.supportFragmentManager

        if (fragmentManager != null) {
            CardDialogFrag.newInstance(mSelectedPayment, mTotalAmt?.toFloat()
                    ?: 0.0f).show(fragmentManager, "paymentDialog")
        }
    }

    private fun getSadedPayment() {
        val userInfo = dataManager.getGsonValue(DataNames.USER_DATA, PojoSignUp::class.java)
        if (CommonUtils.isNetworkConnected(mContext)) {
            mViewModel?.getSaddedPaymentUrl(userInfo?.data?.email ?: "", userInfo?.data?.firstname
                    ?: "", mTotalAmt.toString())
        }
    }

    private fun getThawaniPaymentUrl() {
        val currency = dataManager.getGsonValue(PrefenceConstants.DEFAULT_CURRENCY_INF, Currency::class.java)
        val userInfo = dataManager.getGsonValue(DataNames.USER_DATA, PojoSignUp::class.java)
        if (CommonUtils.isNetworkConnected(mContext)) {
            mViewModel?.getThawaniPaymentUrl(mTotalAmt.toString(), currency?.currency_name
                    ?: "", userInfo)
        }
    }

    private fun getTelrPaymentUrl() {
        val currency = dataManager.getGsonValue(PrefenceConstants.DEFAULT_CURRENCY_INF, Currency::class.java)
        if (CommonUtils.isNetworkConnected(mContext)) {
            mViewModel?.getTelrPaymentUrl(mTotalAmt.toString(), currency?.currency_name ?: "")
        }
    }

    private fun getAamarPaymentUrl() {
        val userInfo = dataManager.getGsonValue(DataNames.USER_DATA, PojoSignUp::class.java)
        val currency = dataManager.getGsonValue(PrefenceConstants.DEFAULT_CURRENCY_INF, Currency::class.java)
        val hashMap = HashMap<String, String>()
        hashMap["currency"] = currency?.currency_name ?: ""
        hashMap["email"] = userInfo?.data?.email ?: ""
        hashMap["phone"] = userInfo?.data?.mobile_no ?: ""
        hashMap["name"] = userInfo?.data?.firstname ?: ""
        hashMap["amount"] = mTotalAmt.toString()
        hashMap["desc"] = "Order Detail"
        hashMap["address"] = if (::adrsData.isInitialized) adrsData.customer_address
                ?: "" else "Customer Address"
        if (CommonUtils.isNetworkConnected(mContext)) {
            mViewModel?.getAamarPayUrl(hashMap)
        }
    }

    private fun getPayMayaUrl() {

        val currency = dataManager.getGsonValue(PrefenceConstants.DEFAULT_CURRENCY_INF, Currency::class.java)
        val hashMap = HashMap<String, String>()
        hashMap["currency"] = currency?.currency_name ?: ""
        hashMap["amount"] = String.format("%.2f", mTotalAmt)
        hashMap["userId"] = dataManager.getKeyValue(PrefenceConstants.USER_ID, PrefenceConstants.TYPE_STRING).toString()
        hashMap["successUrl"] = "https://billing.royoapps.com/payment-success"
        hashMap["failureUrl"] = "https://billing.royoapps.com/payment-error"

        if (CommonUtils.isNetworkConnected(mContext)) {
            mViewModel?.getPaymayaUrl(hashMap)
        }
    }

    private fun getHyperPayUrl() {
        val websiteUrl = dataManager.getKeyValue(PrefenceConstants.WEBSITE_LINK, PrefenceConstants.TYPE_STRING).toString()
        val address = if (::adrsData.isInitialized) adrsData else null

        val url = "$websiteUrl/hyper-pay-webview?amount=${mTotalAmt.toString()}&is_web_view=1&address=${Gson().toJson(address)}"
        val intent = Intent(mContext, PaymentWebViewActivity::class.java).putExtra("payment_url", url)
                .putExtra("payment_gateway", mContext.getString(R.string.hyper_pay))

        startActivity(intent, HYPERPAY_PAYMENT_REQUEST)
    }

    private fun initMyFatooraGateway() {
        val currency = dataManager.getGsonValue(PrefenceConstants.DEFAULT_CURRENCY_INF, Currency::class.java)
        if (CommonUtils.isNetworkConnected(mContext)) {
            mViewModel?.getMyFatoorahPaymentUrl(currency?.currency_name ?: "", mTotalAmt.toString())
        }
    }

    private fun getWinCavePaymentUrl() {
        val currency = dataManager.getGsonValue(PrefenceConstants.DEFAULT_CURRENCY_INF, Currency::class.java)
        val userInfo = dataManager.getGsonValue(DataNames.USER_DATA, PojoSignUp::class.java)
        val address = if (::adrsData.isInitialized) adrsData.customer_address else null
        if (CommonUtils.isNetworkConnected(mContext)) {
            mViewModel?.getWindCaveUrl(String.format("%.2f", mTotalAmt), currency?.currency_name
                    ?: "",
                    userInfo?.data?.email ?: "", address)
        }
    }

    private fun getMpaisaUrl() {
        if (CommonUtils.isNetworkConnected(mContext)) {
            mViewModel?.getMPaisaPaymentUrl(String.format("%.2f", mTotalAmt))
        }
    }

    private fun initTapGateway() {
        if (CommonUtils.isNetworkConnected(mContext)) {

            val signUp = dataManager.getGsonValue(DataNames.USER_DATA, PojoSignUp::class.java)
            val currency = dataManager.getGsonValue(PrefenceConstants.DEFAULT_CURRENCY_INF, Currency::class.java)

            // FacebookSdk.getApplicationContext().resources.configuration.locale.displayCountry

            val hashMap = hashMapOf("email" to signUp?.data?.email.toString(),
                    "phone" to signUp?.data?.mobile_no.toString(),
                    "name" to signUp?.data?.firstname.toString(),
                    "currency" to currency?.currency_name.toString(),
                    "country_code" to AppConstants.COUNTRY_ISO,
                    "post_url" to "${dataManager.getKeyValue(PrefenceConstants.WEBSITE_LINK, PrefenceConstants.TYPE_STRING)}/success",
                    "redirect_url" to "${dataManager.getKeyValue(PrefenceConstants.WEBSITE_LINK, PrefenceConstants.TYPE_STRING)}/success",
                    "amount" to mTotalAmt.toString())

            mViewModel?.tapPayemntApi(hashMap)
        }
    }

    private fun initEvalonGateway() {
        if (CommonUtils.isNetworkConnected(mContext)) {
            mViewModel?.evalonPayemntApi(mTotalAmt.toString())
        }
    }

    private fun payHerePayment() {
        val amt = String.format("%.2f", mTotalAmt)
        val userInfo = dataManager.getGsonValue(DataNames.USER_DATA, PojoSignUp::class.java)
        val currency = dataManager.getGsonValue(PrefenceConstants.DEFAULT_CURRENCY_INF, Currency::class.java)
        val req = InitRequest()
        req.merchantId = "214128"/*settingData?.payhere_merchantID ?: "1214669"*/ // Your Merchant PayHere ID

        req.merchantSecret = "4JBvaPMca8X49aaB9Q1VQv8RjoJyExdkn48WbrckTmxp" // Your Merchant secret (Add your app at Settings > Domains & Credentials, to get this))

        req.currency = currency?.currency_name // Currency code LKR/USD/GBP/EUR/AUD

        req.amount = amt.toDouble() // Final Amount to be charged

        req.orderId = System.currentTimeMillis().toString() // Unique Reference ID

        req.itemsDescription = "Order" // Item description title

        req.custom1 = "This is the custom message 1"
        req.custom2 = "This is the custom message 2"
        req.customer.firstName = userInfo?.data?.firstname
        req.customer.lastName = userInfo?.data?.lastname ?: "last name"
        req.customer.email = userInfo?.data?.email
        req.customer.phone = userInfo?.data?.mobile_no
        val address = if (::adrsData.isInitialized) adrsData else AddressBean()

        req.customer.address.address = address.customer_address ?: ""
        req.customer.address.city = address.address_line_1 ?: ""
        req.customer.address.country = address.customer_address ?: ""
        req.items.add(Item(null, "Items", 1, amt.toDouble()))

        /*//Optional Params
        req.getCustomer().getDeliveryAddress().setAddress("No.2, Kandy Road")
        req.getCustomer().getDeliveryAddress().setCity("Kadawatha")
        req.getCustomer().getDeliveryAddress().setCountry("Sri Lanka")
        req.getItems().add(Item(null, "Door bell wireless", 1, totalAmt))*/

        val intent = Intent(mContext, PHMainActivity::class.java)
        intent.putExtra(PHConstants.INTENT_EXTRA_DATA, req)
        PHConfigs.setBaseUrl(PHConfigs.LIVE_URL)

        startActivity(intent, PAYHERE_REQUEST)
        //unique request ID like private final static int PAYHERE_REQUEST = 11010;

    }


    fun activityResult(requestCode: Int, resultCode: Int, data: Intent?): String? {

        if (requestCode == URWAY_PAYMENT_REQUEST) {

            if (data != null) {
                val  jsonObj = JSONObject(data.getStringExtra("MESSAGE")?:"")
                if(jsonObj.get("ResponseCode").toString()=="000")
                    return jsonObj.get("TranId").toString()
                 else
                    AppToasty.error(mContext,  mContext.getString(R.string.payment_failed))
            }
        } else if (requestCode == SADDED_PAYMENT_REQUEST) {
            when (resultCode) {
                Activity.RESULT_OK -> {
                    AppToasty.success(mContext, mContext.getString(R.string.payment_done_successful))
                    if (mSelectedPayment?.payName == mContext.getString(R.string.windcave) || mSelectedPayment?.payName == mContext.getString(R.string.mpaisa)) {
                        if (data != null && data.hasExtra("paymentId"))
                            return data.getStringExtra("paymentId")
                    }
                    // use the result to update your UI and send the payment method nonce to your server
                }
                else -> {
                    val message = if (data?.hasExtra("showError") == true)
                        mContext.getString(R.string.payment_failed)
                    else
                        mContext.getString(R.string.payment_unsuccessful)
                    AppToasty.error(mContext, message)
                }
            }
        } else if (requestCode == MY_FATOORAH_PAYMENT_REQUEST || requestCode == TAP_PAYMENT_REQUEST ||
                requestCode == EVALON_PAYMENT_REQUEST || requestCode == AAMAR_PAY_PAYMENT_REQUEST || requestCode == TELR_PAYMENT_REQUEST
                || requestCode == HYPERPAY_PAYMENT_REQUEST || requestCode == THAWANI_PAYMENT_REQUEST || requestCode == SADAD_PAYMENT_REQUEST
                || requestCode == TRANS_BANK_PAYMENT_REQUEST || requestCode == PAY_MAYA_REQUEST) {
            when (resultCode) {
                Activity.RESULT_OK -> {
                    AppToasty.success(mContext, mContext.getString(R.string.payment_done_successful))
                    if (data != null && data.hasExtra("paymentId"))
                        return data.getStringExtra("paymentId")
                    // use the result to update your UI and send the payment method nonce to your server
                }
                else -> {
                    val message = if (data?.hasExtra("showError") == true)
                        mContext.getString(R.string.payment_failed)
                    else
                        mContext.getString(R.string.payment_unsuccessful)
                    AppToasty.error(mContext, message)
                }
            }
        } else if (requestCode == PAYHERE_REQUEST) {
            if (data != null && data.hasExtra(PHConstants.INTENT_EXTRA_RESULT)) {
                val response: PHResponse<StatusResponse> = data.getSerializableExtra(PHConstants.INTENT_EXTRA_RESULT) as PHResponse<StatusResponse>
                if (response.isSuccess) {
                    val msg = "Activity result:" + response.data.toString()
                    return response.data?.paymentNo.toString()
                } else {
                    val msg = "Result:$response"

                    AppToasty.error(mContext, mContext.getString(R.string.payment_failed))
                }
            }
        } else if (resultCode == Activity.RESULT_OK && requestCode == PaymentParams.PAYMENT_REQUEST_CODE) {
            return data?.getStringExtra(PaymentParams.TRANSACTION_ID) ?: ""
        }

        return null
    }


    private fun startActivity(intent: Intent, requestCode: Int) {
        if (mFragContext != null)
            mFragContext?.startActivityForResult(intent, requestCode)
        else mAcitivityContext?.startActivityForResult(intent, requestCode)
    }
}