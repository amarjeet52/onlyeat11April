package com.codebrew.clikat.module.payment_gateway.savedcards

import android.app.Activity
import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.braintreepayments.api.dropin.AddCardActivity
import com.codebrew.clikat.BR
import com.codebrew.clikat.BuildConfig
import com.codebrew.clikat.R
import com.codebrew.clikat.app_utils.AppUtils
import com.codebrew.clikat.app_utils.extension.onSnackbar
import com.codebrew.clikat.base.BaseActivity
import com.codebrew.clikat.data.constants.AppConstants
import com.codebrew.clikat.data.constants.PrefenceConstants
import com.codebrew.clikat.data.model.api.AddCardResponseData
import com.codebrew.clikat.data.model.api.Data1
import com.codebrew.clikat.data.model.api.SavedCardList
import com.codebrew.clikat.data.model.others.CustomPayModel
import com.codebrew.clikat.data.model.others.SaveCardInputModel
import com.codebrew.clikat.data.preferences.PreferenceHelper
import com.codebrew.clikat.databinding.LayoutSavedCardsBinding
import com.codebrew.clikat.di.ViewModelProviderFactory
import com.codebrew.clikat.modal.PojoSignUp
import com.codebrew.clikat.module.cart.CardData
import com.codebrew.clikat.module.payment_gateway.addCard.AddNewCard
import com.codebrew.clikat.module.payment_gateway.dialog_card.CardDialogFrag
import com.codebrew.clikat.module.payment_gateway.savedcards.adapters.SavedCardsAdapter
import com.codebrew.clikat.preferences.DataNames
import com.codebrew.clikat.utils.configurations.Configurations
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.HasAndroidInjector
import kotlinx.android.synthetic.main.layout_saved_cards.*
import sqip.CardDetails
import sqip.CardEntry
import sqip.handleActivityResult
import javax.inject.Inject


class SaveCardsActivity : BaseActivity<LayoutSavedCardsBinding, SavedCardsViewModel>(), SavedCardsNavigator, SavedCardsAdapter.OnCardClickListener,
        HasAndroidInjector, CardDialogFrag.onPaymentListener {

    @Inject
    lateinit var androidInjector: DispatchingAndroidInjector<Any>

    @Inject
    lateinit var factory: ViewModelProviderFactory

    private lateinit var mViewModel: SavedCardsViewModel

    private var mBinding: LayoutSavedCardsBinding? = null

    private var adapter: SavedCardsAdapter? = null

    private var saveCardList = mutableListOf<CardData>()
    var mSelectedPayment: CustomPayModel? = null

    private var itemPosition = 0

    var amount = 0.0f
    // var gateway_unique_id = ""

    @Inject
    lateinit var prefHelper: PreferenceHelper

    @Inject
    lateinit var appUtils: AppUtils

    override fun getBindingVariable(): Int {
        return BR.viewModel
    }

    override fun getLayoutId(): Int {
        return R.layout.layout_saved_cards
    }

    override fun getViewModel(): SavedCardsViewModel {

        mViewModel = ViewModelProviders.of(this, factory).get(SavedCardsViewModel::class.java)
        return mViewModel
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewDataBinding?.colors = Configurations.colors
        viewDataBinding?.strings = appUtils.loadAppConfig(0).strings

        mBinding = viewDataBinding
        viewModel.navigator = this

        addCardObserver()

        amount = intent.getFloatExtra("amount", 0.0f)
        mSelectedPayment = intent?.getParcelableExtra("payItem")

        savedCardObserver()


        if (isNetworkConnected) {
            mViewModel.getSaveCardList(mSelectedPayment?.payment_token ?: "")
        }


        adapter = SavedCardsAdapter(saveCardList, appUtils)
        rvSavedCards.adapter = adapter
        adapter?.setCardListener(this)

        if (mSelectedPayment?.payment_token == "squareup") {
            sqip.InAppPaymentsSdk.squareApplicationId = mSelectedPayment?.keyId ?: ""
        }



        btnAddCard.setOnClickListener {
            if (!isNetworkConnected) return@setOnClickListener

            if (prefHelper.getCurrentUserLoggedIn()) {
                if (mSelectedPayment?.payment_token == "squareup") {
                    CardEntry.startCardEntryActivity(this, true,
                            AppConstants.REQUEST_SQUARE_PAY)
                } else if (mSelectedPayment?.payment_token == "peach") {
                    startActivityForResult(Intent(this, AddNewCard::class.java), AppConstants.PEACH_PAYMENT)
                } else if (mSelectedPayment?.payment_token == "payuLatam") {
                    openAddCardActivity()
                } else {
                    CardDialogFrag.newInstance(mSelectedPayment, amount).show(supportFragmentManager, "paymentDialog")
                }
            } else {
                mBinding?.root?.onSnackbar(getString(R.string.card_add_login_error))
                appUtils.checkLoginFlow(this, AppConstants.REQUEST_CARD_ADD)
            }
        }

        ivBack.setOnClickListener { onBackPressed() }

    }

    private fun openAddCardActivity() {
        val intent = Intent(this, AddNewCard::class.java)
        startActivityForResult(intent, 111)
    }


    private fun addCardObserver() {
        // Create the observer which updates the UI.
        val catObserver = Observer<AddCardResponseData> { resource ->
            // Update the UI, in this case, a TextView.

            prefHelper.setkeyValue(PrefenceConstants.CUSTOMER_PAYMENT_ID, resource.customer_payment_id
                    ?: "")
            mBinding?.root?.onSnackbar("Card saved successfully")
            mViewModel.getSaveCardList(mSelectedPayment?.payment_token ?: "")

        }

        mViewModel.addCardLiveData.observe(this, catObserver)

    }


    private fun savedCardObserver() {
        // Create the observer which updates the UI.
        val catObserver = Observer<List<SavedCardList>> { resource ->
            // Update the UI, in this case, a TextView.
            saveCardList.clear()
//            saveCardList.addAll(resource)
            adapter?.notifyDataSetChanged()
        }

        mViewModel.savedCardLiveData.observe(this, catObserver)

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == AppConstants.REQUEST_CARD_ADD && resultCode == Activity.RESULT_OK && prefHelper.getCurrentUserLoggedIn()) {

            if (mSelectedPayment?.payment_token == "squareup") {
                CardEntry.startCardEntryActivity(this, true,
                        AppConstants.REQUEST_SQUARE_PAY)
            } else if (mSelectedPayment?.payment_token == "peach") {
                startActivityForResult(Intent(this, AddNewCard::class.java), AppConstants.PEACH_PAYMENT)
            } else {
                CardDialogFrag.newInstance(mSelectedPayment, amount).show(supportFragmentManager, "paymentDialog")
            }
        } else if (requestCode == AppConstants.REQUEST_SQUARE_PAY && resultCode == Activity.RESULT_OK) {

            CardEntry.handleActivityResult(data) { result ->
                if (result.isSuccess()) {
                    val cardResult: CardDetails = result.getSuccessValue()
                    val card: sqip.Card = cardResult.card

                    val userInfo = prefHelper.getGsonValue(DataNames.USER_DATA, PojoSignUp::class.java)

                    val inputModel = SaveCardInputModel()

                    inputModel.user_id = prefHelper.getKeyValue(PrefenceConstants.USER_ID, PrefenceConstants.TYPE_STRING).toString()
                    inputModel.card_type = card.brand.name
                    inputModel.card_number = card.lastFourDigits
                    inputModel.exp_month = card.expirationMonth.toString()
                    inputModel.exp_year = card.expirationYear.toString()
                    inputModel.card_token = ""
                    inputModel.gateway_unique_id = mSelectedPayment?.payment_token
                    inputModel.cvc = "123"
                    inputModel.card_nonce = cardResult.nonce
                    inputModel.card_holder_name = "${userInfo?.data?.firstname ?: ""} ${userInfo?.data?.lastname ?: ""}".trim()

                    mViewModel.saveCard(inputModel)

                } else if (result.isCanceled()) {

                    mBinding?.root?.onSnackbar("Canceled")
                }
            }
        } else if (requestCode == AppConstants.PEACH_PAYMENT && resultCode == Activity.RESULT_OK) {
            if (resultCode == Activity.RESULT_OK) {
                val inputModel = SaveCardInputModel()
                inputModel.user_id = prefHelper.getKeyValue(PrefenceConstants.USER_ID, PrefenceConstants.TYPE_STRING).toString()
                if (data != null) {
                    inputModel.card_type = ""
                    inputModel.card_number = data.getStringExtra("card_number")
                    inputModel.exp_month = data.getStringExtra("exp_month")
                    inputModel.exp_year = data.getStringExtra("exp_year")
                    inputModel.card_token = ""
                    inputModel.gateway_unique_id = mSelectedPayment?.payment_token
                    inputModel.cvc = data.getStringExtra("cvc")
                    inputModel.card_nonce = ""
                    inputModel.card_holder_name = data.getStringExtra("card_holder_name")

                    mViewModel.saveCard(inputModel)
                }
            } else if (resultCode == Activity.RESULT_CANCELED) {
                mBinding?.root?.onSnackbar("Canceled")
            }
        } else if (requestCode == 111 && resultCode == Activity.RESULT_OK) {
            val userInfo = prefHelper.getGsonValue(DataNames.USER_DATA, PojoSignUp::class.java)

            val inputModel = SaveCardInputModel()

            inputModel.user_id = prefHelper.getKeyValue(PrefenceConstants.USER_ID, PrefenceConstants.TYPE_STRING).toString()
            inputModel.card_type = "VISA"
            inputModel.card_number = data?.getStringExtra("card_number")
            inputModel.exp_month = data?.getStringExtra("exp_month")
            inputModel.exp_year = data?.getStringExtra("exp_year")
            inputModel.card_token = null
            inputModel.gateway_unique_id = "payuLatam"
            inputModel.cvc = data?.getStringExtra("cvc")
            inputModel.card_nonce = ""
            inputModel.card_holder_name = data?.getStringExtra("card_holder_name")?.trim()

            mViewModel.saveCard(inputModel)
        }
    }

    override fun onDeleteCard() {
        saveCardList.removeAt(itemPosition)
        adapter?.notifyDataSetChanged()
    }

    override fun onErrorOccur(message: String) {
        mBinding?.root?.onSnackbar(message)
    }

    override fun onSessionExpire() {
        openActivityOnTokenExpire()
    }

    override fun onDeleteCard(savedCard: CardData, position: Int) {
//        itemPosition = position
//        if (mSelectedPayment?.payment_token == "authorize_net")
//            mViewModel.deleteSavedCard(savedCard.authnet_payment_profile_id, mSelectedPayment?.payment_token
//                    ?: "")
//        else
//            if (BuildConfig.CLIENT_CODE == "ulagula_0488") {
//                mViewModel.deleteSavedCard(savedCard.token, mSelectedPayment?.payment_token ?: "")
//            } else {
//                mViewModel.deleteSavedCard(savedCard.id, mSelectedPayment?.payment_token ?: "")
//            }
    }

    override fun onCardClick(savedCard: CardData, position: Int) {
//        val intentShare = Intent()
//
//        mSelectedPayment?.cardId = savedCard.id
//        mSelectedPayment?.customerId = prefHelper.getKeyValue(PrefenceConstants.CUSTOMER_PAYMENT_ID, PrefenceConstants.TYPE_STRING).toString()
//
//        mSelectedPayment?.authnet_payment_profile_id = savedCard.authnet_payment_profile_id
//        mSelectedPayment?.token = savedCard.token
//        mSelectedPayment?.authnet_profile_id = savedCard.authnet_profile_id
//        intentShare.putExtra("payItem", mSelectedPayment)
//
//        if (intent.hasExtra("pos"))
//            intentShare.putExtra("pos", intent.getIntExtra("pos", 0))
//
//        setResult(Activity.RESULT_OK, intentShare)
//        finish()
    }


    override fun androidInjector(): AndroidInjector<Any> {
        return androidInjector
    }

    override fun paymentToken(token: String, paymentMethod: String, savedCard: SaveCardInputModel?) {
        if (isNetworkConnected) {
            viewModel.saveCard(savedCard)
        }
    }


}