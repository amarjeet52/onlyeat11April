package com.codebrew.clikat.module.subscription.subscrip_detail

import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.navArgs
import com.codebrew.clikat.BR
import com.codebrew.clikat.BuildConfig
import com.codebrew.clikat.R
import com.codebrew.clikat.app_utils.*
import com.codebrew.clikat.app_utils.extension.launchActivity
import com.codebrew.clikat.app_utils.extension.onSnackbar
import com.codebrew.clikat.base.BaseFragment
import com.codebrew.clikat.data.DataManager
import com.codebrew.clikat.data.constants.AppConstants
import com.codebrew.clikat.data.constants.NetworkConstants
import com.codebrew.clikat.data.constants.PrefenceConstants
import com.codebrew.clikat.data.model.api.*
import com.codebrew.clikat.data.model.api.tap_payment.Transaction
import com.codebrew.clikat.data.model.others.BuySubcripInput
import com.codebrew.clikat.data.model.others.CustomPayModel
import com.codebrew.clikat.data.model.others.SaveCardInputModel
import com.codebrew.clikat.data.preferences.PreferenceHelper
import com.codebrew.clikat.databinding.SubscripDetailFragmentBinding
import com.codebrew.clikat.di.ViewModelProviderFactory
import com.codebrew.clikat.modal.PojoSignUp
import com.codebrew.clikat.modal.other.SettingModel
import com.codebrew.clikat.module.cart.*
import com.codebrew.clikat.module.payment_gateway.PaymentListActivity
import com.codebrew.clikat.module.payment_gateway.PaymentWebViewActivity
import com.codebrew.clikat.module.payment_gateway.dialog_card.CardDialogFrag
import com.codebrew.clikat.module.subscription.adapter.BenefitAdapter
import com.codebrew.clikat.preferences.DataNames
import com.codebrew.clikat.utils.configurations.Configurations
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.android.synthetic.main.subscrip_detail_fragment.*
import kotlinx.android.synthetic.main.toolbar_subscription.*
import java.lang.reflect.Type
import java.net.URLEncoder
import javax.inject.Inject


class SubscripDetailFrag : BaseFragment<SubscripDetailFragmentBinding, CartViewModel>(),
        CartNavigator, CardDialogFrag.onPaymentListener {

    companion object {
        fun newInstance() = SubscripDetailFrag()
    }

    private lateinit var viewModel: CartViewModel

    @Inject
    lateinit var factory: ViewModelProviderFactory

    private var mBinding: SubscripDetailFragmentBinding? = null

    @Inject
    lateinit var perferenceHelper: PreferenceHelper

    @Inject
    lateinit var appUtils: AppUtils

    @Inject
    lateinit var mDialogsUtil: DialogsUtil

    @Inject
    lateinit var dataManager: DataManager

    @Inject
    lateinit var dateTimeUtils: DateTimeUtils

    @Inject
    lateinit var paymentUtil: PaymentUtil

    val argument: SubscripDetailFragArgs by navArgs()

    private lateinit var benefitAdapter: BenefitAdapter

    private val textConfig by lazy { appUtils.loadAppConfig(0).strings }

    lateinit var mSelectedPayment: CustomPayModel

    private var mumyBeneDialog: Dialog? = null

    private var adrsData: AddressBean? = null
    private var phoneNumber: String? = null
    private var subscripData: SubcripModel? = null

    private var currency: Currency? = null

    override fun getBindingVariable(): Int {
        return BR.viewModel
    }

    override fun getLayoutId(): Int {
        return R.layout.subscrip_detail_fragment
    }

    override fun getViewModel(): CartViewModel {
        viewModel = ViewModelProviders.of(this, factory).get(CartViewModel::class.java)
        return viewModel
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.navigator = this

        adrsData = dataManager.getGsonValue(PrefenceConstants.ADRS_DATA, AddressBean::class.java)
        currency = dataManager.getGsonValue(PrefenceConstants.DEFAULT_CURRENCY_INF, Currency::class.java)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mBinding = viewDataBinding
        mBinding?.color = Configurations.colors
        mBinding?.currency = AppConstants.DEFAULT_CURRENCY_SYMBOL
        mBinding?.strings = textConfig

        mumyBeneDialog = mDialogsUtil.openDialogMumybene(activity ?: requireContext()) {
            viewModel.cancelGenerateOrder()
        }


        benefitAdapter = BenefitAdapter()
        rv_benefit_list?.adapter = benefitAdapter

        tb_name.text = textConfig?.my_subscription ?: getString(R.string.subscriptions)
        tb_back.setOnClickListener { navController(this@SubscripDetailFrag).popBackStack() }

        btn_renew_subcrip.setOnClickListener {
            if (isNetworkConnected) {
                viewModel.getPaymentGeofence()
            }
        }


        if (argument.subscripData != null) {
            subscripData = argument.subscripData
            argument.subscripData?.updated_start_date = updateDate(argument.subscripData?.created_at
                    ?: "")
            argument.subscripData?.update_end_date = updateDate(argument.subscripData?.updated_at
                    ?: "")
            mBinding?.subcripModel = argument.subscripData

            benefitAdapter.submitItemList(argument.subscripData?.benefits)
            tvPlanSub?.text = argument.subscripData?.description
            group_date.visibility = View.GONE
        } else {
            getSubscripDetail()
        }
    }


    override fun onGeofencePayment(data: GeofenceData?) {
        dataManager.getKeyValue(DataNames.FEATURE_DATA, PrefenceConstants.TYPE_STRING).toString().let {
            val listType: Type = object : TypeToken<ArrayList<SettingModel.DataBean.FeatureData?>?>() {}.type
            val featureList: ArrayList<SettingModel.DataBean.FeatureData> = Gson().fromJson(it, listType)

            activity?.launchActivity<PaymentListActivity>(AppConstants.REQUEST_PAYMENT_OPTION) {
                putParcelableArrayListExtra("feature_data", featureList)
                putExtra("mSelectPayment", data?.gateways as ArrayList<String>?)
                putExtra("mSubscribeType", true)
                putExtra("mTotalAmt", subscripData?.price?.toFloat())
            }
        }
    }


    override fun onBuySubscription(data: BuySubscription) {

        if (mumyBeneDialog != null && mumyBeneDialog?.isShowing == true) {
            mumyBeneDialog?.dismiss()
        }

        val userDetail=dataManager.getGsonValue(DataNames.USER_DATA,PojoSignUp::class.java)
        userDetail?.data?.is_subscribed=1
        dataManager.addGsonValue(DataNames.USER_DATA,Gson().toJson(userDetail))

        getSubscripDetail()
    }

    private fun getSubscripDetail() {
        if (isNetworkConnected) {
            viewModel.getSubscrpDetail()
        }
    }

    override fun onCancelSubscription(message: String) {
        val userDetail=dataManager.getGsonValue(DataNames.USER_DATA,PojoSignUp::class.java)
        userDetail?.data?.is_subscribed=0
        dataManager.addGsonValue(DataNames.USER_DATA,Gson().toJson(userDetail))

        mBinding?.root?.onSnackbar(message)

        navController(this@SubscripDetailFrag).popBackStack()
    }

    override fun onSubscripDetailList(data: MutableList<SubcripModel>) {

        group_date.visibility = View.VISIBLE

        subscripData = data.firstOrNull()

        subscripData?.subscription_plan = when (subscripData?.type) {
            1 -> {
                getString(R.string.subcription_plan_tag, getString(R.string.weekly))
            }
            2 -> {
                getString(R.string.subcription_plan_tag, getString(R.string.monthly))
            }
            else -> {
                getString(R.string.subcription_plan_tag, getString(R.string.yearly))
            }
        }

        subscripData?.updated_start_date = updateDate(subscripData?.start_date ?: "")
        subscripData?.update_end_date = updateDate(subscripData?.end_date ?: "")
        mBinding?.subcripModel = subscripData

        benefitAdapter.submitItemList(subscripData?.benefits)
    }

    override fun onProfileUpdate() {

    }


    override fun getSaddedPaymentSuccess(data: AddCardResponseData?) {
        mSelectedPayment.keyId = data?.transaction_reference
        val intent = Intent(requireContext(), PaymentWebViewActivity::class.java).putExtra("paymentData", data)
        startActivityForResult(intent, SADDED_PAYMENT_REQUEST)
    }

    override fun getWindCavePaymentSuccess(data: Result?) {
        val intent = Intent(requireContext(), PaymentWebViewActivity::class.java).putExtra("paymentData", data?.Request)
                .putExtra("payment_gateway", getString(R.string.windcave))
        startActivityForResult(intent, SADDED_PAYMENT_REQUEST)
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

    override fun getTelrPaymentSuccess(data: AddCardResponseData?) {
        mSelectedPayment.keyId = data?.order?.ref
        val intent = Intent(requireContext(), PaymentWebViewActivity::class.java).putExtra("paymentData", data)
                .putExtra("payment_gateway", getString(R.string.teller))
        startActivityForResult(intent, TELR_PAYMENT_REQUEST)
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

    override fun getmPaisaPaymentSuccess(data: AddCardResponseData?) {
        mSelectedPayment.keyId = data?.rID
        val intent = Intent(requireContext(), PaymentWebViewActivity::class.java).putExtra("paymentData", data)
                .putExtra("payment_gateway", getString(R.string.mpaisa))
        startActivityForResult(intent, SADDED_PAYMENT_REQUEST)
    }


    override fun onErrorOccur(message: String) {
        mBinding?.root?.onSnackbar(message)
    }

    override fun onSessionExpire() {
        openActivityOnTokenExpire()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == AppConstants.REQUEST_PAYMENT_OPTION && resultCode == Activity.RESULT_OK) {
            mSelectedPayment = data?.getParcelableExtra("payItem") ?: CustomPayModel()
            if (mSelectedPayment.payName == getString(R.string.mumybene)) {
                mDialogsUtil.showMumyBenePhone(activity ?: requireContext(), phoneNumber
                        ?: "", payName = mSelectedPayment.mumybenePay ?: "") {
                    phoneNumber = it
                    mumyBeneDialog?.show()
                    buySubscription(subscripData, mSelectedPayment)
                }
            }else if(mSelectedPayment.payName == getString(R.string.pipol_pay)){
                buySubscription(subscripData, mSelectedPayment)
            }
            else if (mSelectedPayment.addCard == true) {
                buySubscription(subscripData, mSelectedPayment)
            } else if (requestCode == MY_FATOORAH_PAYMENT_REQUEST || requestCode == TAP_PAYMENT_REQUEST || requestCode == EVALON_PAYMENT_REQUEST ||
                    requestCode == AAMAR_PAY_PAYMENT_REQUEST || requestCode == TELR_PAYMENT_REQUEST || requestCode== HYPERPAY_PAYMENT_REQUEST) {
                when (resultCode) {
                    Activity.RESULT_OK -> {
                        AppToasty.success(requireContext(), getString(R.string.payment_done_successful))
                        if (data != null && data.hasExtra("paymentId"))
                            mSelectedPayment.keyId = data.getStringExtra("paymentId")

                        buySubscription(subscripData, mSelectedPayment)

                        // use the result to update your UI and send the payment method nonce to your server
                    }
                    else -> {
                        val message = if (data?.hasExtra("showError") == true)
                            getString(R.string.payment_failed)
                        else
                            getString(R.string.payment_unsuccessful)
                        AppToasty.error(requireContext(), message)
                    }
                }
            } else {
                paymentUtil.checkPaymentMethod(this@SubscripDetailFrag, viewModel, mSelectedPayment, subscripData?.price?.toDoubleOrNull()
                        ?: 0.0,
                        adrsData ?: AddressBean())
            }

        }

        paymentUtil.activityResult(requestCode, resultCode, data).let {
            if (it != null) {
                mSelectedPayment.keyId = it
                buySubscription(subscripData, mSelectedPayment)
            }
        }
    }

    private fun updateDate(dateToFormat: String): String? {
        return dateTimeUtils.convertDateOneToAnother(
                dateToFormat,
                "yyyy-MM-dd'T'HH:mm:ss",
                "dd-MM-yyyy") ?: ""
    }

    private fun buySubscription(subscripData: SubcripModel?, mSelectedPayment: CustomPayModel?, savedCard: SaveCardInputModel? = null) {

        val param = BuySubcripInput(action = if (subscripData?.is_subscribed == 0) 1 else 2,
                benefit_type = subscripData?.benefits?.joinToString { it.benefit_type ?: "" }
                        ?: "".trim(),
                currency = currency?.currency_name ?: "",
                gateway_unique_id = mSelectedPayment?.payment_token ?: "",
                paymentType = DataNames.PAYMENT_CARD,
                payment_token = mSelectedPayment?.keyId ?: "",
                price = subscripData?.price?.toFloat() ?: 0.0f,
                subscription_plan_id = subscripData?.id ?: 0,
                type = subscripData?.type.toString()

        )

        if (dataManager.getKeyValue(PrefenceConstants.CUSTOMER_PAYMENT_ID, PrefenceConstants.TYPE_STRING).toString().isNotEmpty()) {
            param.customer_payment_id= dataManager.getKeyValue(PrefenceConstants.CUSTOMER_PAYMENT_ID, PrefenceConstants.TYPE_STRING).toString()
        }

        if (mSelectedPayment?.payment_token == "authorize_net") {
            param.authnet_payment_profile_id = mSelectedPayment.authnet_payment_profile_id
            param.authnet_profile_id = mSelectedPayment.authnet_profile_id
        } else if (mSelectedPayment?.payment_token == "pago_facil") {
            param.payment_token = savedCard?.card_number ?: ""
            param.cvt = savedCard?.cvc ?: ""
            param.cp = savedCard?.zipCode ?: ""
            param.expMonth = savedCard?.exp_month ?: ""
            param.expYear = savedCard?.exp_year?.takeLast(2) ?: ""
        }else if (mSelectedPayment?.payment_token=="safe2pay") {
            param.payment_token = savedCard?.card_number ?: ""
            param.cvc = savedCard?.cvc ?: ""
            param.expMonth = savedCard?.exp_month ?: ""
            param.expYear = savedCard?.exp_year
            param.cardHolderName=savedCard?.card_holder_name
        }


        if (subscripData?.subscription_id != null) {
            param.action = 2
            param.renew_id = subscripData.renew_id
            param.end_date = subscripData.end_date
        }

        if (isNetworkConnected) {
            viewModel.buySubcription(param)
        }
    }

    override fun paymentToken(token: String, paymentMethod: String, savedCard: SaveCardInputModel?) {
        token.let {
            if (it != null) {
                mSelectedPayment.keyId = it
                mSelectedPayment.payment_token = paymentMethod
                buySubscription(subscripData, mSelectedPayment, savedCard)
            }
        }
    }

}