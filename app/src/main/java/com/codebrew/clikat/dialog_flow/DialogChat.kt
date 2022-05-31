package com.codebrew.clikat.dialog_flow

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.view.MotionEvent
import android.view.View
import androidx.lifecycle.ViewModelProviders
import com.codebrew.clikat.BR
import com.codebrew.clikat.R
import com.codebrew.clikat.app_utils.AppUtils
import com.codebrew.clikat.app_utils.DialogRequest
import com.codebrew.clikat.app_utils.DialogsUtil
import com.codebrew.clikat.app_utils.PermissionFile
import com.codebrew.clikat.app_utils.extension.onSnackbar
import com.codebrew.clikat.base.BaseActivity
import com.codebrew.clikat.data.AppDataType
import com.codebrew.clikat.data.DgFlow
import com.codebrew.clikat.data.FoodAppType
import com.codebrew.clikat.data.constants.AppConstants
import com.codebrew.clikat.data.constants.PrefenceConstants
import com.codebrew.clikat.data.model.api.AddressBean
import com.codebrew.clikat.data.model.api.Currency
import com.codebrew.clikat.data.model.api.DataBean
import com.codebrew.clikat.data.model.others.DialogDataItem
import com.codebrew.clikat.data.model.others.DialogItem
import com.codebrew.clikat.data.model.others.PopUpItem
import com.codebrew.clikat.data.preferences.PreferenceHelper
import com.codebrew.clikat.databinding.ActivityDialogChatBinding
import com.codebrew.clikat.di.ViewModelProviderFactory
import com.codebrew.clikat.dialog_flow.adapter.DialogChatAdapter
import com.codebrew.clikat.modal.CartList
import com.codebrew.clikat.modal.PojoSignUp
import com.codebrew.clikat.modal.other.ProductDataBean
import com.codebrew.clikat.modal.other.SettingModel
import com.codebrew.clikat.module.addon_quant.SavedAddon
import com.codebrew.clikat.module.cart.CartNavigator
import com.codebrew.clikat.module.cart.CartViewModel
import com.codebrew.clikat.module.dialog_adress.AddressDialogFragment
import com.codebrew.clikat.module.dialog_adress.interfaces.AddressDialogListener
import com.codebrew.clikat.module.dialog_adress.v2.AddressDialogFragmentV2
import com.codebrew.clikat.module.product.product_listing.DialogListener
import com.codebrew.clikat.module.product_addon.AddonFragment
import com.codebrew.clikat.preferences.DataNames
import com.codebrew.clikat.utils.StaticFunction
import com.codebrew.clikat.utils.configurations.Configurations
import com.google.api.gax.core.FixedCredentialsProvider
import com.google.auth.oauth2.AccessToken
import com.google.auth.oauth2.GoogleCredentials
import com.google.cloud.dialogflow.v2.*
import com.google.gson.Gson
import com.google.protobuf.util.JsonFormat
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.HasAndroidInjector
import kotlinx.android.synthetic.main.activity_dialog_chat.*
import pub.devrel.easypermissions.AppSettingsDialog
import pub.devrel.easypermissions.EasyPermissions
import java.util.*
import javax.inject.Inject

class DialogChat : BaseActivity<ActivityDialogChatBinding, CartViewModel>(), HasAndroidInjector, CartNavigator
        , AddonFragment.AddonCallback, AddressDialogListener, DialogListener, EasyPermissions.PermissionCallbacks {

    @Inject
    lateinit var factory: ViewModelProviderFactory

    @Inject
    lateinit var prefHelper: PreferenceHelper

    @Inject
    lateinit var androidInjector: DispatchingAndroidInjector<Any>

    @Inject
    lateinit var appUtils: AppUtils

    @Inject
    lateinit var mDialogsUtil: DialogsUtil

    @Inject
    lateinit var permissionManger: PermissionFile

    private var mViewModel: CartViewModel? = null

    private var sessionsClient: SessionsClient? = null
    private var session: SessionName? = null
    private val uuid = UUID.randomUUID().toString()

    private val gson = Gson()

    private var mDialogAdapter: DialogChatAdapter? = null
    private var mDialogList = mutableListOf<DialogDataItem>()

    private var userProfile: PojoSignUp? = null
    private var clientInform: SettingModel.DataBean.SettingData? = null
    private var bookingFlowBean: SettingModel.DataBean.BookingFlowBean? = null
    private var screenFlowBean: SettingModel.DataBean.ScreenFlowBean? = null

    private val textConfig by lazy { appUtils.loadAppConfig(0).strings }

    private var speechRecognizer: SpeechRecognizer? = null
    private var speechRecognizerIntent: Intent? = null

    var credentials: GoogleCredentials? = null
    private var selectedCurrency: Currency?=null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mViewModel?.navigator = this

        viewDataBinding?.color = Configurations.colors
        viewDataBinding?.strings = textConfig

        val statusColor = Color.parseColor(Configurations.colors.appBackground)
        StaticFunction.setStatusBarColor(this,getColor(R.color.dark_blue))

        userProfile = prefHelper.getGsonValue(DataNames.USER_DATA, PojoSignUp::class.java)
        bookingFlowBean = prefHelper.getGsonValue(DataNames.BOOKING_FLOW, SettingModel.DataBean.BookingFlowBean::class.java)
        clientInform = prefHelper.getGsonValue(DataNames.SETTING_DATA, SettingModel.DataBean.SettingData::class.java)
        screenFlowBean = prefHelper.getGsonValue(DataNames.SCREEN_FLOW, SettingModel.DataBean.ScreenFlowBean::class.java)
        selectedCurrency = prefHelper.getGsonValue(PrefenceConstants.CURRENCY_INF, Currency::class.java)
        initVoicePermission()

        speechMethod()

        mDialogAdapter = DialogChatAdapter(clientInform,selectedCurrency, DialogChatAdapter.ChatListener({

        }, {
            it.netPrice = it.fixed_price?.toFloat()

            if (it.adds_on?.isNotEmpty() == true) {

                hideKeyboard()
                val cartList: CartList? = prefHelper.getGsonValue(DataNames.CART, CartList::class.java)

                if (appUtils.checkProdExistance(it.id ?: -1)) {
                    val savedProduct = cartList?.cartInfos?.filter { cart ->
                        cart.supplierId == it.supplier_id && cart.productId == it.id
                    } ?: emptyList()

                    SavedAddon.newInstance(it, 0, savedProduct, this).show(supportFragmentManager, "savedAddon")
                } else {
                    AddonFragment.newInstance(it, 0, this).show(supportFragmentManager, "addOn")
                }

            } else {
                if (bookingFlowBean?.vendor_status == 0 && appUtils.checkVendorStatus(it.supplier_id
                                , vendorBranchId = it.supplier_branch_id, branchFlow = clientInform?.branch_flow)) {
                    mDialogsUtil.openAlertDialog(this, getString(R.string.clearCart, textConfig?.supplier,textConfig?.proceed
                            ?: ""), "Yes", "No", this)
                } else {
                    if (appUtils.checkBookingFlow(this, it.product_id, this)) {
                        addCart(it)
                    }
                }
                onProductAdded(it)
            }
        }, {

            onAddressAdded(it)

        }, { yes: String, no: String, type: Int, inputType: String ->

            when (type) {
                DgFlow.PopUpAdrsType.dgFlowType -> {
                    if (yes.isNotEmpty() && yes == "yes") {
                        mDialogList.add(DialogDataItem(DgFlow.TextRightType.dgFlowType, listOf(), listOf(), PopUpItem(), "yes"))
                        mDialogList.add(DialogDataItem(DgFlow.PopUpLeftType.dgFlowType, listOf(), listOf(), PopUpItem(msg = "How Would You Like To Pay?", acceptMsg = "Online",
                                rejectMsg = "COD", type = DgFlow.PopUpAmtType.dgFlowType), ""))

                        updateRecylerview(mDialogList)

                    } else {
                        appUtils.clearCart()
                        addDialogText(DgFlow.TextLeftType.dgFlowType, "What Else Can I Help You With?")
                    }
                }
                else -> {
                    hideKeyboard()
                    //redirect to cart
                    if (inputType == "COD") {
                        val intent = Intent()
                        intent.putExtra("paymentType", "cod")
                        setResult(Activity.RESULT_OK, intent)
                    } else {
                        setResult(Activity.RESULT_OK)
                    }
                    finish()
                }
            }
        }))

        rv_chat.adapter = mDialogAdapter

        addDialogText(DgFlow.TextLeftType.dgFlowType, "Welcome!")

        createToken("")

        btn_chat.setOnClickListener {
            sendMessage(ed_chat.text.toString())
        }

        iv_cross.setOnClickListener {
            finish()
        }

    }

    private fun createToken(message: String) {
        if (isNetworkConnected) {
            mViewModel?.generateToken(message)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        speechRecognizer?.destroy()
    }

    private fun speechMethod() {

        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(this)

        speechRecognizerIntent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
        speechRecognizerIntent?.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
        speechRecognizerIntent?.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())

        speechRecognizer?.setRecognitionListener(object : RecognitionListener {
            override fun onReadyForSpeech(bundle: Bundle) {}
            override fun onBeginningOfSpeech() {
                ed_chat.setText("")
                ed_chat.hint = "Listening..."
            }

            override fun onRmsChanged(v: Float) {}
            override fun onBufferReceived(bytes: ByteArray) {}
            override fun onEndOfSpeech() {}
            override fun onError(i: Int) {}
            override fun onResults(bundle: Bundle) {
                btn_mic.setImageResource(R.drawable.ic_dialog_mike)
                val data = bundle.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                sendMessage(data?.get(0) ?: "")
            }

            override fun onPartialResults(bundle: Bundle) {}
            override fun onEvent(i: Int, bundle: Bundle) {}
        })

    }

    private fun onAddressAdded(it: AddressBean) {

        appUtils.setUserLocale(it)
        prefHelper.addGsonValue(PrefenceConstants.ADRS_DATA, Gson().toJson(it))

        addDialogText(DgFlow.TextRightType.dgFlowType, "${it.customer_address} , ${it.address_line_1}")

        mDialogList.add(DialogDataItem(DgFlow.PopUpLeftType.dgFlowType, listOf(), listOf(), PopUpItem(msg = "Do You Wish To Proceed With This Order?", acceptMsg = "yes",
                rejectMsg = "no", type = DgFlow.PopUpAdrsType.dgFlowType), ""))

        updateRecylerview(mDialogList)

    }

    private fun onProductAdded(it: ProductDataBean) {

        addDialogText(DgFlow.TextRightType.dgFlowType, "Selected: " + it.name)

        addDialogText(DgFlow.TextLeftType.dgFlowType, "${userProfile?.data?.firstname} as, Please Select Location")

        if (isNetworkConnected) {
            viewModel.getAddressList(0)
        }
    }

    private fun addCart(productModel: ProductDataBean) {

        if (screenFlowBean?.app_type == AppDataType.Food.type) {
            val cartList: CartList? = prefHelper.getGsonValue(DataNames.CART, CartList::class.java)

            if (cartList != null && cartList.cartInfos?.size!! > 0) {
                if (cartList.cartInfos?.any { cartList.cartInfos?.get(0)?.deliveryType != it.deliveryType } == true) {
                    appUtils.clearCart()

                    viewDataBinding?.root?.onSnackbar("Cart Clear Successfully")
                }
            }
        }

        if (productModel.prod_quantity == 0f) {

            productModel.prod_quantity = 1f

            productModel.self_pickup = FoodAppType.Delivery.foodType

            productModel.type = screenFlowBean?.app_type

            if (AppConstants.APP_SUB_TYPE > AppDataType.Custom.type) {
                productModel.payment_after_confirmation = clientInform?.payment_after_confirmation?.toInt()
                        ?: 0
            }
            appUtils.addProductDb(this, screenFlowBean?.app_type
                    ?: 0, productModel)


        } else {
            var quantity = productModel.prod_quantity ?: 0f

            quantity++

            val remaingProd = productModel.quantity?.minus(productModel.purchased_quantity ?: 0f)
                    ?: 0f

            if (quantity <= remaingProd) {
                productModel.prod_quantity = quantity

                StaticFunction.updateCart(this, productModel.product_id, quantity, productModel.netPrice
                        ?: 0.0f)
            } else {
                viewDataBinding?.root?.onSnackbar(getString(R.string.maximum_limit_cart))
            }
        }
    }

    /**
     * Override for set binding variable
     *
     * @return variable id
     */
    override fun getBindingVariable(): Int {
        return BR.viewModel
    }

    /**
     * @return layout resource id
     */
    override fun getLayoutId(): Int {
        return R.layout.activity_dialog_chat
    }

    /**
     * Override for set view model
     *
     * @return view model instance
     */
    override fun getViewModel(): CartViewModel {
        mViewModel = ViewModelProviders.of(this, factory).get(CartViewModel::class.java)
        return mViewModel as CartViewModel
    }

    override fun onTokenGenerate(token: String, message: String) {


        val calendar = Calendar.getInstance()
        calendar.add(Calendar.DAY_OF_WEEK, 1)
        credentials = GoogleCredentials.create(AccessToken(token, calendar.time))

        initV2Chatbot()

        dialogMsgRequest(message)
    }

    override fun onProfileUpdate() {

    }


    override fun onErrorOccur(message: String) {
        viewDataBinding.root.onSnackbar(message)
    }

    override fun onSessionExpire() {
        openActivityOnTokenExpire()
    }

    private fun initV2Chatbot() {

        val projectId = clientInform?.dialog_project_id

        if (projectId.isNullOrEmpty()) return

        try {

            val settingsBuilder = SessionsSettings.newBuilder()
            val sessionsSettings = settingsBuilder.setCredentialsProvider(FixedCredentialsProvider.create(credentials)).build()
            sessionsClient = SessionsClient.create(sessionsSettings)
            session = SessionName.of(projectId, uuid)

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun dialogMsgRequest(message:String)
    {
        if (message.isNotEmpty()) {

            if (session == null && sessionsClient == null) return

            ed_chat.hint = ""
            ed_chat.setText("")
            addDialogText(DgFlow.TextRightType.dgFlowType, message)
            val queryInput = QueryInput.newBuilder().setText(TextInput.newBuilder().setText(message).setLanguageCode("en-US")).build()
            DialogRequest(this@DialogChat, session, sessionsClient, queryInput).execute()

        }
    }

    private fun initVoicePermission() {
        if (permissionManger.hasAudioPermission(this)) {
            intializeVoiceListener()
        } else {
            permissionManger.audioCallTask(this)
        }
    }

    private fun intializeVoiceListener() {

        btn_mic.setOnTouchListener(object : View.OnTouchListener {
            override fun onTouch(v: View?, motionEvent: MotionEvent?): Boolean {
                if (motionEvent?.action == MotionEvent.ACTION_UP) {
                    speechRecognizer?.stopListening()
                }
                if (motionEvent?.action == MotionEvent.ACTION_DOWN) {
                    btn_mic.setImageResource(R.drawable.ic_dialog_mike)
                    speechRecognizer?.startListening(speechRecognizerIntent)
                }
                return false
            }
        })
    }

    private fun sendMessage(msg: String) {

        if (credentials?.createScopedRequired() == true || credentials?.accessToken?.expirationTime == null) {
            createToken(msg)
        }else
        {
            dialogMsgRequest(msg)
        }
    }

    /** Returns an [AndroidInjector].  */
    override fun androidInjector(): AndroidInjector<Any> {
        return androidInjector
    }

    fun callbackV2(response: DetectIntentResponse?) {
        if (response != null) {
            // process aiResponse here
            addDialogText(DgFlow.TextLeftType.dgFlowType, response.queryResult.fulfillmentText)

            response.queryResult.webhookPayload.fieldsMap.values.forEachIndexed { index, any ->

                val jsonFormat = JsonFormat.printer().print(any)

                if (jsonFormat != "true") {
                    val productBean = gson.fromJson<DialogItem>(jsonFormat, DialogItem::class.java)
                    if (productBean != null) {

                        mDialogList.add(DialogDataItem(DgFlow.ListLeftType.dgFlowType, productBean.items, listOf(), PopUpItem(), ""))
                        updateRecylerview(mDialogList)
                    }
                }
            }
        }
    }

    override fun onAddress(data: DataBean?) {
        if (data?.address?.isNotEmpty() == true) {
            mDialogList.add(DialogDataItem(DgFlow.ListLeftType.dgFlowType, listOf(), data.address?.toList()
                    ?: listOf(), PopUpItem(), ""))
            updateRecylerview(mDialogList)
        } else {
            if (clientInform?.show_ecom_v2_theme == "1") {
                AddressDialogFragmentV2.newInstance(0).show(supportFragmentManager, "dialog")
            } else {
                AddressDialogFragment.newInstance(0,0).show(supportFragmentManager, "dialog")
            }
        }
    }

    private fun addDialogText(chatType: Int, msg: String) {
        mDialogList.add(DialogDataItem(chatType, listOf(), listOf(), PopUpItem(), msg))
        updateRecylerview(mDialogList)

    }

    private fun updateRecylerview(mDialogList: MutableList<DialogDataItem>) {
        rv_chat.smoothScrollToPosition(mDialogList.size - 1)
        mDialogAdapter?.addItmSubmitList(mDialogList)
    }

    override fun onAddonAdded(productModel: ProductDataBean) {
        if (productModel.adds_on?.isNullOrEmpty() == false && appUtils.checkProdExistance(productModel.product_id)) {
            val cartList: CartList? = appUtils.getCartList()
            productModel.prod_quantity = (cartList?.cartInfos?.filter { productModel.product_id == it.productId }?.sumByDouble { it.quantity.toDouble() }
                    ?: 0.0).toFloat()
        }

        onProductAdded(productModel)
    }

    override fun onAddressSelect(adrsBean: AddressBean) {
        adrsBean.let {
            appUtils.setUserLocale(it)

            prefHelper.addGsonValue(PrefenceConstants.ADRS_DATA, Gson().toJson(it))
            onAddressAdded(it)
        }
    }

    override fun onDestroyDialog() {

    }

    override fun onVehicleSelect(id: String, model: String, color: String, plate_no: String) {
        TODO("Not yet implemented")
    }



    override fun onSucessListner() {
        appUtils.clearCart()
    }

    override fun onErrorListener() {

    }

    override fun onPermissionsDenied(requestCode: Int, perms: MutableList<String>) {
        if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
            AppSettingsDialog.Builder(this).build().show()
        }
    }


    override fun onPermissionsGranted(requestCode: Int, perms: MutableList<String>) {
        if (requestCode == AppConstants.REQUEST_AUDIO) {
            intializeVoiceListener()
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
}