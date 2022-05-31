package com.codebrew.clikat.module.payment_gateway

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import android.widget.Button
import android.widget.ImageView
import android.widget.PopupWindow
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.DividerItemDecoration
import com.bumptech.glide.Glide
import com.codebrew.clikat.BR
import com.codebrew.clikat.BuildConfig
import com.codebrew.clikat.R
import com.codebrew.clikat.app_utils.*
import com.codebrew.clikat.app_utils.dialogintrface.ImageDialgFragment
import com.codebrew.clikat.app_utils.extension.dimBehind
import com.codebrew.clikat.app_utils.extension.launchActivity
import com.codebrew.clikat.app_utils.extension.onSnackbar
import com.codebrew.clikat.base.BaseActivity
import com.codebrew.clikat.data.DataManager
import com.codebrew.clikat.data.PaymentType
import com.codebrew.clikat.data.constants.AppConstants
import com.codebrew.clikat.data.constants.NetworkConstants
import com.codebrew.clikat.data.constants.PrefenceConstants
import com.codebrew.clikat.data.model.api.AddCardResponseData
import com.codebrew.clikat.data.model.api.AddressBean
import com.codebrew.clikat.data.model.api.Currency
import com.codebrew.clikat.data.model.api.Result
import com.codebrew.clikat.data.model.api.orderDetail.OrderHistory
import com.codebrew.clikat.data.model.api.tap_payment.Transaction
import com.codebrew.clikat.data.model.others.CustomPayModel
import com.codebrew.clikat.data.model.others.SaveCardInputModel
import com.codebrew.clikat.data.preferences.PreferenceHelper
import com.codebrew.clikat.databinding.ActivityPaymentBinding
import com.codebrew.clikat.databinding.PopupZellePaymentBinding
import com.codebrew.clikat.di.ViewModelProviderFactory
import com.codebrew.clikat.modal.other.SettingModel
import com.codebrew.clikat.modal.wallet.Data
import com.codebrew.clikat.module.cart.*
import com.codebrew.clikat.module.payment_gateway.adapter.PayListener
import com.codebrew.clikat.module.payment_gateway.adapter.PaymentListAdapter
import com.codebrew.clikat.module.payment_gateway.dialog_card.CardDialogFrag
import com.codebrew.clikat.module.payment_gateway.savedcards.SaveCardsActivity
import com.codebrew.clikat.preferences.DataNames
import com.codebrew.clikat.utils.configurations.Configurations
import com.codebrew.clikat.intrface.ImageCallback
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.HasAndroidInjector
import kotlinx.android.synthetic.main.activity_payment.*
import pub.devrel.easypermissions.AppSettingsDialog
import pub.devrel.easypermissions.EasyPermissions
import java.io.File
import java.io.IOException
import java.net.URLEncoder
import javax.inject.Inject


class PaymentListActivity : BaseActivity<ActivityPaymentBinding, CartViewModel>(),
        ImageCallback, EasyPermissions.PermissionCallbacks, CartNavigator ,
        CardDialogFrag.onPaymentListener, HasAndroidInjector {


    @Inject
    lateinit var dataManager: DataManager

    @Inject
    lateinit var imageUtils: ImageUtility

    @Inject
    lateinit var permissionFile: PermissionFile

    @Inject
    lateinit var factory: ViewModelProviderFactory

    @Inject
    lateinit var prefHelper: PreferenceHelper

    @Inject
    lateinit var appUtils: AppUtils

    private var mViewModel: CartViewModel? = null

    private var photoFile: File? = null

    private val imageDialog by lazy { ImageDialgFragment() }

    private var mAdapter: PaymentListAdapter? = null

    private var mBinding: ActivityPaymentBinding? = null

    private var featureList: ArrayList<SettingModel.DataBean.FeatureData>? = null
    private var amount: Float? = null
    private var orderHist: OrderHistory? = null
    private var settingData: SettingModel.DataBean.SettingData? = null

    private var payItem: CustomPayModel? = null

    private var popup: PopupWindow? = null

    private var ivImage: ImageView? = null

    private var payment_gateways: ArrayList<String>? = arrayListOf()

    private val textConfig by lazy { appUtils.loadAppConfig(0).strings }

    private var mSubscrptionType = false

    private var enablePayment: Boolean = false

    @Inject
    lateinit var paymentUtil: PaymentUtil

    private var totalAmount: Float? = 0f

    private var adrsData: AddressBean? = null
    private var selectedCurrency: Currency?=null
    @Inject
    lateinit var androidInjector: DispatchingAndroidInjector<Any>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel.navigator = this
        viewDataBinding?.color = Configurations.colors

        initialise()
        imageObserver()
        transactionsObserver()
        setAdapter()

        if (settingData?.wallet_module == "1")
            hitWalletApi()
        else {
            setPaymentOptions(0f)
        }
        listeners()
    }

    private fun initialise() {
        settingData = dataManager.getGsonValue(DataNames.SETTING_DATA, SettingModel.DataBean.SettingData::class.java)
        adrsData = dataManager.getGsonValue(PrefenceConstants.ADRS_DATA, AddressBean::class.java)
        selectedCurrency = dataManager.getGsonValue(PrefenceConstants.CURRENCY_INF, Currency::class.java)
        featureList = intent.getParcelableArrayListExtra("feature_data")
        amount = intent.getFloatExtra("mTotalAmt", 0.0f)

        if (intent.hasExtra("mSelectPayment")) {
            payment_gateways = intent.getStringArrayListExtra("mSelectPayment")
        }

        mSubscrptionType = intent.getBooleanExtra("mSubscribeType", false)

        if (intent.hasExtra("orderData")) {
            orderHist = intent.getParcelableExtra("orderData")
        }
        if (intent.hasExtra("enablePayment"))
            enablePayment = intent.getBooleanExtra("enablePayment", false)

        if (intent.hasExtra("totalAmount")) {
            totalAmount = intent.getFloatExtra("totalAmount", 0f)
        }
    }

    private fun listeners() {
        iv_back.setOnClickListener {
            finish()
        }

        mAdapter?.settingCallback(PayListener({ it: CustomPayModel, i: Int ->
            when (it.payName) {
                getString(R.string.zelle) ,getString(R.string.pipol_pay) -> {
                    payItem = it
                    displayPopupWindow(window.decorView.findViewById(android.R.id.content), it.payement_front)
                }

                getString(R.string.creds_movil) -> {
                    payItem = it
                    displayPopupWindow(window.decorView.findViewById(android.R.id.content), it.payement_front)
                }

                getString(R.string.cashapp) -> {
                    payItem = it
                    displayPopupWindow(window.decorView.findViewById(android.R.id.content), it.payement_front)
                }
                getString(R.string.wallet) -> {
                    if (!it.showAddMoney) {
                        val intent = Intent()
                        intent.putExtra("payItem", it)
                        setResult(Activity.RESULT_OK, intent)
                        finish()
                    }
                }

                /*     getString(R.string.mumybene) ->{
                         val fragment = MymybeneOperatorFragment()
                         fragment.setActionListener(object: OperatorsRecyclerAdapter.OnItemSelectionListener{
                             override fun onItemSelected() {
                                 val intent = Intent()
                                 intent.putExtra("payItem", it)
                                 setResult(Activity.RESULT_OK, intent)
                                 finish()
                             }

                         })
                         val transaction = supportFragmentManager.beginTransaction()
                         transaction.add(R.id.paymentListParent,fragment , "Operator")
                         transaction.addToBackStack(null)
                         transaction.commit()
                     }*/

                else -> {
                    if (it.addCard == true) {
                        if (prefHelper.getCurrentUserLoggedIn()) {
                            payItem = it
                            openPayment(it)
                        } else {
                            val intent = Intent(this, appUtils.checkLoginActivity())
                            startActivityForResult(intent, AppConstants.REQUEST_CARD_ADD)
                        }
                    } else {
                        if (enablePayment) {
                            payItem=it
                            paymentUtil.checkPaymentMethodActivity(this, mViewModel, payItem, totalAmount?.toDouble()
                                    ?: 0.0, adrsData ?: AddressBean())
                        } else {
                            val intent = Intent()
                            intent.putExtra("payItem", it)
                            setResult(Activity.RESULT_OK, intent)
                            finish()
                        }
                    }
                }
            }
        }, {
           // launchActivity<WalletAddMoneyActivity>(AppConstants.REQUEST_ADD_MONEY)
        }))
    }

    private fun setAdapter() {
        mAdapter = PaymentListAdapter(false, settingData, textConfig,selectedCurrency)
        rv_payment_option.adapter = mAdapter
    }

    private fun hitWalletApi() {
        if (isNetworkConnected)
            viewModel.getTransactionsList()
    }

    private fun setPaymentOptions(walletAmount: Float) {
        var mPaymentList = mAdapter?.settingLyt(this, featureList, textConfig)

        if (settingData?.wallet_module == "1") {
            val showAddMoney = amount ?: 0f > walletAmount
            mPaymentList?.add(0, CustomPayModel(getString(R.string.wallet), R.drawable.ic_cash_on, DataNames.PAYMENT_WALLET.toString(),
                    payment_token = "wallet", walletAmount = walletAmount, showAddMoney = showAddMoney, payment_name = "wallet"))
        }
        /*add cash on delivery method*/
        if (settingData?.payment_method != "null" && settingData?.payment_method?.toInt() ?: 0 != PaymentType.ONLINE.payType && !enablePayment) {
            val mumybenePay = if (mPaymentList?.any { it.payName == "Mumybene" } == true) textConfig?.cod else null
            mPaymentList?.add(CustomPayModel(textConfig?.cod, R.drawable.ic_cash_on, DataNames.PAYMENT_CASH.toString(),
                    payment_token = "cod", mumybenePay = mumybenePay, payment_name = "cod"))
        }

        mPaymentList = mAdapter?.filterList(payment_gateways, mPaymentList)

        val itemDecoration = DividerItemDecoration(this, DividerItemDecoration.VERTICAL)
        rv_payment_option.addItemDecoration(itemDecoration)


        val mRefreshList = if (orderHist != null && orderHist?.payment_type != DataNames.PAYMENT_AFTER_CONFIRM) {
            paymentName(mPaymentList, orderHist)
        } else if (mSubscrptionType) {
            mPaymentList?.filter { it.payName != "Zelle" && it.payName != textConfig?.braintree && it.payName != textConfig?.cod && it.payName != getString(R.string.wallet) }
        } else {
            mPaymentList
        }

        mAdapter?.submitItemList(mRefreshList)
    }

    private fun transactionsObserver() {
        // Create the observer which updates the UI.
        val catObserver = Observer<Data> { resource ->
            setPaymentOptions(resource?.userDetails?.walletAmount ?: 0f)
        }
        // Observe the LiveData, passing in this activity as the LifecycleOwner and the observer.
        viewModel.wallet.observe(this, catObserver)
    }

    private fun openPayment(it: CustomPayModel?) {
        val intent = Intent(this, SaveCardsActivity::class.java)
        intent.putExtra("amount", amount)
        intent.putExtra("payItem", it)
        startActivityForResult(intent, AppConstants.REQUEST_PAYMENT_DEBIT_CARD)
    }


    private fun imageObserver() {
        // Create the observer which updates the UI.
        val catObserver = Observer<String> { resource ->

            payItem?.keyId = resource

            if (popup?.isShowing == true) {
                popup?.dismiss()
            }
            val intent = Intent()
            intent.putExtra("payItem", payItem)
            setResult(Activity.RESULT_OK, intent)
            finish()
        }

        // Observe the LiveData, passing in this activity as the LifecycleOwner and the observer.
        viewModel.imageLiveData.observe(this, catObserver)
    }


    private fun displayPopupWindow(anchorView: View, payementFront: List<SettingModel.DataBean.KeyValueFront?>?) {

        val textConfig by lazy { Configurations.strings }

        popup = PopupWindow(anchorView)
        val binding = DataBindingUtil.inflate<PopupZellePaymentBinding>(LayoutInflater.from(this), R.layout.popup_zelle_payment, null, false)
        binding.color = Configurations.colors
        binding.textConfig = textConfig
        popup?.contentView = binding.root


        val tvEmail = binding.root.findViewById<TextView>(R.id.tv_email)
        val tvPhone = binding.root.findViewById<TextView>(R.id.tv_phone)
        val choosePay = binding.root.findViewById<Button>(R.id.btn_choose_doc)
        ivImage = binding.root.findViewById(R.id.iv_doc)

        tvEmail.text = "1. ${textConfig.email?:getString(R.string.email)} ${payementFront?.find { it?.key == "email" }?.value ?: ""}"
        tvPhone.text = "2. ${textConfig.shareScreenshot ?: getString(R.string.phoneno_tag, payementFront?.find { it?.key == "phone_number" }?.value ?: "")}"

        choosePay.setOnClickListener {
            uploadImage()
        }

        // Set content width and height
        popup?.height = WindowManager.LayoutParams.WRAP_CONTENT
        popup?.width = WindowManager.LayoutParams.MATCH_PARENT
        popup?.isOutsideTouchable = true
        popup?.isFocusable = true



        popup?.showAtLocation(anchorView, Gravity.CENTER, 0, 0)

        popup?.dimBehind()

    }


    private fun uploadImage() {
        if (permissionFile.hasCameraPermissions(this)) {
            if (isNetworkConnected) {
                showImagePicker()
            }
        } else {
            permissionFile.cameraAndGalleryActivity(this)
        }
    }

    private fun paymentName(mPaymentList: MutableList<CustomPayModel>?, orderHist: OrderHistory?): List<CustomPayModel>? {

        return when (orderHist?.payment_type) {
            DataNames.PAYMENT_CASH -> mPaymentList?.filter { it.payName == textConfig?.cod }

            DataNames.PAYMENT_CARD ->
                mPaymentList?.filter { it.payName != textConfig?.cod }


            else -> mPaymentList
        }

    }


    override fun getBindingVariable(): Int {
        return BR.viewModel
    }

    override fun getLayoutId(): Int {
        return R.layout.activity_payment
    }

    override fun getViewModel(): CartViewModel {
        mViewModel = ViewModelProviders.of(this, factory).get(CartViewModel::class.java)
        return mViewModel as CartViewModel
    }

    override fun onPdf() {

    }

    override fun onGallery() {
        val pickIntent = Intent(Intent.ACTION_PICK)
        pickIntent.type = "image/*"
        startActivityForResult(pickIntent, AppConstants.GalleyPicker)
    }

    override fun onCamera() {
        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
            // Ensure that there's a camera activity to handle the intent
            takePictureIntent.resolveActivity(packageManager!!)?.also {
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
                            this,
                            packageName ?: "",
                            it)

                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                    startActivityForResult(takePictureIntent, AppConstants.CameraPicker)
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)


        if (requestCode == AppSettingsDialog.DEFAULT_SETTINGS_REQ_CODE) {
            // Do something after user returned from app settings screen, like showing a Toast.
            mBinding?.root?.onSnackbar(getString(R.string.returned_from_app_settings_to_activity))
        }

        if (resultCode == AppCompatActivity.RESULT_OK && requestCode == AppConstants.CameraPicker) {

            if (isNetworkConnected) {
                if (photoFile?.isRooted == true) {
                    loadDocImage(photoFile?.absolutePath
                            ?: "")

                    viewModel.validateZelleImage(imageUtils.compressImage(photoFile?.absolutePath
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
                    val cursor = contentResolver?.query(selectedImage!!, filePathColumn, null, null, null)
                    // Move to first row
                    cursor?.moveToFirst()
                    //Get the column index of MediaStore.Images.Media.DATA
                    val columnIndex = cursor?.getColumnIndex(filePathColumn[0])
                    //Gets the String value in the column
                    val imgDecodableString = cursor?.getString(columnIndex ?: 0)
                    cursor?.close()

                    loadDocImage(imgDecodableString ?: "")

                    if (imgDecodableString?.isNotEmpty() == true) {
                        viewModel.validateZelleImage(imageUtils.compressImage(imgDecodableString))
                    }
                }
            }
        } else if (resultCode == AppCompatActivity.RESULT_OK && requestCode == AppConstants.REQUEST_PAYMENT_DEBIT_CARD) {
            if (data != null) {
                val savedCard = data.getParcelableExtra<CustomPayModel>("payItem")
                val intent = Intent()
                intent.putExtra("payItem", savedCard)
                setResult(Activity.RESULT_OK, intent)
                finish()
            }
        } else if (requestCode == AppConstants.REQUEST_CARD_ADD && resultCode == Activity.RESULT_OK && prefHelper.getCurrentUserLoggedIn()) {
            openPayment(payItem)
        } else if (requestCode == AppConstants.REQUEST_ADD_MONEY && resultCode == Activity.RESULT_OK) {
            hitWalletApi()
        } else if (requestCode == MY_FATOORAH_PAYMENT_REQUEST || requestCode == TAP_PAYMENT_REQUEST || requestCode == EVALON_PAYMENT_REQUEST ||
                requestCode == AAMAR_PAY_PAYMENT_REQUEST || requestCode == TELR_PAYMENT_REQUEST || requestCode == HYPERPAY_PAYMENT_REQUEST) {
            when (resultCode) {
                Activity.RESULT_OK -> {
                    AppToasty.success(this, getString(R.string.payment_done_successful))
                    if (data != null && data.hasExtra("paymentId"))
                        payItem?.keyId = data.getStringExtra("paymentId")

                    // use the result to update your UI and send the payment method nonce to your server
                    val intent = Intent()
                    intent.putExtra("payItem", payItem)
                    setResult(Activity.RESULT_OK, intent)
                    finish()
                }
                else -> {
                    val message = if (data?.hasExtra("showError") == true)
                        getString(R.string.payment_failed)
                    else
                        getString(R.string.payment_unsuccessful)
                    AppToasty.error(this, message)
                }
            }
        } else {

        }

    }

    private fun loadDocImage(image: String) {

        if (ivImage != null && image.isNotEmpty()) {
            Glide.with(this).load(image).into(ivImage!!)
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
        imageDialog.settingCallback(this)
        imageDialog.show(
                supportFragmentManager,
                "image_picker"
        )
    }

    override fun onErrorOccur(message: String) {
        window.decorView.findViewById<View>(android.R.id.content)?.onSnackbar(message)
    }

    override fun onSessionExpire() {
        openActivityOnTokenExpire()
    }


    override fun getSaddedPaymentSuccess(data: AddCardResponseData?) {
        payItem?.keyId = data?.transaction_reference
        val intent = Intent(this, PaymentWebViewActivity::class.java).putExtra("paymentData", data)
        startActivityForResult(intent, SADDED_PAYMENT_REQUEST)
    }

    override fun getWindCavePaymentSuccess(data: Result?) {
        val intent = Intent(this, PaymentWebViewActivity::class.java).putExtra("paymentData", data?.Request)
                .putExtra("payment_gateway", getString(R.string.windcave))
        startActivityForResult(intent, SADDED_PAYMENT_REQUEST)
    }

    override fun getMyFatoorahPaymentSuccess(data: AddCardResponseData?) {
        val intent = Intent(this, PaymentWebViewActivity::class.java).putExtra("paymentData", data)
                .putExtra("payment_gateway", textConfig?.my_fatoorah)
        startActivityForResult(intent, MY_FATOORAH_PAYMENT_REQUEST)
    }

    override fun onTapPayment(transaction: Transaction) {
        launchActivity<PaymentWebViewActivity>(TAP_PAYMENT_REQUEST) {
            putExtra("payment_url", transaction.url)
            putExtra("payment_gateway", getString(R.string.tap))
        }
    }

    override fun getPayMayaUrl(data: AddCardResponseData?) {
        launchActivity<PaymentWebViewActivity>(PAY_MAYA_REQUEST) {
            putExtra("paymentData", data)
            putExtra("payment_gateway", getString(R.string.pay_maya))
        }
    }

    override fun onProfileUpdate() {

    }

    override fun getTelrPaymentSuccess(data: AddCardResponseData?) {
        payItem?.keyId = data?.order?.ref
        val intent = Intent(this, PaymentWebViewActivity::class.java).putExtra("paymentData", data)
                .putExtra("payment_gateway", getString(R.string.teller))
        startActivityForResult(intent, TELR_PAYMENT_REQUEST)
    }


    override fun onEvalonPayment(data: String) {
        val paymentUrl = if (BuildConfig.DEBUG) {
            NetworkConstants.EVALON_TEST_LINK
        } else {
            NetworkConstants.EVALON_PROD_LINK
        }

        launchActivity<PaymentWebViewActivity>(EVALON_PAYMENT_REQUEST)
        {
            putExtra("payment_url", "${paymentUrl}${URLEncoder.encode(data, "utf-8")}")
            putExtra("payment_gateway", getString(R.string.tap))
        }
    }

    override fun getmPaisaPaymentSuccess(data: AddCardResponseData?) {
        payItem?.keyId = data?.rID
        val intent = Intent(this, PaymentWebViewActivity::class.java).putExtra("paymentData", data)
                .putExtra("payment_gateway", getString(R.string.mpaisa))
        startActivityForResult(intent, SADDED_PAYMENT_REQUEST)
    }

    override fun getAamarPayPaymentSuccess(data: AddCardResponseData?) {
        val intent = Intent(this, PaymentWebViewActivity::class.java).putExtra("paymentData", data)
                .putExtra("payment_gateway", getString(R.string.aamar_pay))
        startActivityForResult(intent, AAMAR_PAY_PAYMENT_REQUEST)
    }

    override fun paymentToken(token: String, paymentMethod: String, savedCard: SaveCardInputModel?) {
        token.let {
                payItem?.keyId = it
                payItem?.payment_token = paymentMethod
                val intent = Intent()
                intent.putExtra("payItem", it)
                intent.putExtra("savedCard",savedCard)
                setResult(Activity.RESULT_OK, intent)
                finish()
            }
    }

    override fun androidInjector(): AndroidInjector<Any> {
        return androidInjector
    }
}


