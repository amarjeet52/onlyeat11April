package com.codebrew.clikat.module.payment_gateway.addCard

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.KeyEvent
import android.view.View
import androidx.lifecycle.ViewModelProviders
import com.codebrew.clikat.BR
import com.codebrew.clikat.BuildConfig
import com.codebrew.clikat.R
import com.codebrew.clikat.app_utils.extension.onSnackbar
import com.codebrew.clikat.base.BaseActivity
import com.codebrew.clikat.data.constants.PrefenceConstants
import com.codebrew.clikat.data.model.others.SaveCardInputModel
import com.codebrew.clikat.data.preferences.PreferenceHelper
import com.codebrew.clikat.databinding.ActivityAddNewCardBinding
import com.codebrew.clikat.di.ViewModelProviderFactory
import com.codebrew.clikat.modal.PojoSignUp
import com.codebrew.clikat.module.payment_gateway.savedcards.SavedCardsNavigator
import com.codebrew.clikat.module.payment_gateway.savedcards.SavedCardsViewModel
import com.codebrew.clikat.preferences.DataNames
import dagger.android.DispatchingAndroidInjector
import kotlinx.android.synthetic.main.activity_add_new_card.*
import java.util.regex.Matcher
import java.util.regex.Pattern
import javax.inject.Inject

class AddNewCard : BaseActivity<ActivityAddNewCardBinding, SavedCardsViewModel>(), SavedCardsNavigator, CardInterface {


    private var listOfPattern = ArrayList<String>()
    private var keyDel = 0
    private var isDefault = false
    private var isDefaultStatus = ""
    @Inject
    lateinit var prefHelper: PreferenceHelper

    @Inject
    lateinit var androidInjector: DispatchingAndroidInjector<Any>

    @Inject
    lateinit var factory: ViewModelProviderFactory

    private var mBinding: ActivityAddNewCardBinding? = null

    private lateinit var mViewModel: SavedCardsViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = viewDataBinding
        viewModel.navigator = this

        ivBack.setOnClickListener {
            setResult(Activity.RESULT_CANCELED)
            finish()
        }
        cardExpiresFormatting()
        setCardRegxList()
        etCardNo.addTextChangedListener(FourDigitCardFormatWatcher(etCardNo, listOfPattern, this))
        tvSetAsDefault.setOnClickListener {
            if (!isDefault) {
                isDefault = true
                isDefaultStatus = "1"
                tvSetAsDefault.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_radio_active, 0, 0, 0)
            } else {
                isDefault = false
                isDefaultStatus = "0"
                tvSetAsDefault.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_radio_unactive, 0, 0, 0)
            }
        }

        tvSaveCard.setOnClickListener {
            savebtnClick()
        }
    }

    override fun getBindingVariable(): Int {
        return BR.viewModel
    }

    override fun getLayoutId(): Int {
        return R.layout.activity_add_new_card
    }

    override fun getViewModel(): SavedCardsViewModel {
        mViewModel = ViewModelProviders.of(this, factory).get(SavedCardsViewModel::class.java)
        return mViewModel
    }

    private fun savebtnClick() {
        val cardNo = etCardNo.text.toString()
        val cardExpires = etExpires.text.toString().trim()
        val cvv = etCvv.text.toString().trim()
        val firstName = etFirstName.text.toString().trim()
        val LastName = etLastName.text.toString().trim()

        if (cardExpires.length < 7) {
            mBinding?.root?.onSnackbar("Invalid expiry date")
            return
        }

        if (isNetworkConnected) {
            if (validateData(cardNo, cardExpires, cvv, firstName, LastName)) {

                    p_bar.visibility = View.VISIBLE
                    val intent = Intent()
                    intent.putExtra("card_number", cardNo)
                    intent.putExtra("card_holder_name", firstName)
                    intent.putExtra("email", LastName)
                    intent.putExtra("exp_month", cardExpires.split("/")[0])
                    intent.putExtra("exp_year", cardExpires.split("/")[1])
                    intent.putExtra("cvc", cvv)
                    setResult(Activity.RESULT_OK, intent)
                    finish()

            }
        }
    }

    override fun getSelectedCardData(position: Int) {
        when (position) {
            0 -> {
                ivCard.setImageResource(R.drawable.ic_visa_new)
            }
            1 -> {
                ivCard.setImageResource(R.drawable.ic_mastercard)
            }
            2 -> {
                ivCard.setImageResource(R.drawable.ic_amex)
            }
            3 -> {
                ivCard.setImageResource(R.drawable.ic_diners)
            }
            4 -> {
                ivCard.setImageResource(R.drawable.ic_discover)
            }
            5 -> {
                ivCard.setImageResource(R.drawable.ic_jcb)
            }
            6 -> {
                ivCard.setImageResource(R.drawable.ic_up)
            }
            7 -> {
                ivCard.setImageResource(R.drawable.ic_elo)
            }
            8 -> {
                ivCard.setImageResource(R.drawable.ic_hiper)
            }
            10 -> {
                ivCard.setImageDrawable(null)
            }
        }
    }

    private fun cardExpiresFormatting() {
        etExpires.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                etExpires.setOnKeyListener(View.OnKeyListener { v, keyCode, event ->
                    if (keyCode == KeyEvent.KEYCODE_DEL)
                        keyDel = 1
                    false
                })

                if (keyDel == 0) {
                    val len = etExpires.text?.length
                    val input = etExpires.text.toString()
                    Log.e("TAG", " length : $len")
                    if (len == 2) {
                        etExpires.setText(String.format("%s", "${input.substring(0, 2)}${"/"}${input.substring(2)}"))
                        etExpires.setSelection(etExpires.text?.length ?: 0)
                    }
                } else if (keyDel == 1) {
                    val len = etExpires.text?.length
                    if (len == 4) {
                        etExpires.text = etExpires.text?.delete(etExpires.text?.length?.minus(1)
                                ?: 0, etExpires.text?.length ?: 0)
                        etExpires.setSelection(etExpires.text?.length ?: 0)
                    }
                    keyDel = 0
                }
            }
        })
    }

    private fun validateData(cardNumber: String, cardExpires: String, cvv: String, firstName: String, lastName: String): Boolean {
        return when {
            cardNumber.isEmpty() -> {
                mBinding?.root?.onSnackbar(getString(R.string.error_enter_cardNumber))
                false
            }

            ValidationUtils.isPhoneNumberFormatAllZeros(cardNumber.replace(" ", "")) -> {
                mBinding?.root?.onSnackbar(getString(R.string.error_invalid_card))
                false
            }

            cardNumber.length < 16 -> {
                mBinding?.root?.onSnackbar(getString(R.string.error_invalid_card))
                false
            }

            cardExpires.isEmpty() -> {
                mBinding?.root?.onSnackbar(getString(R.string.error_empty_cardExpires))
                false
            }
            cvv.isEmpty() -> {
                mBinding?.root?.onSnackbar(getString(R.string.error_empty_cvv))
                false
            }
            cvv.length < 3 -> {
                mBinding?.root?.onSnackbar(getString(R.string.error_valid_cvv))
                false
            }

            firstName.isEmpty() -> {
                mBinding?.root?.onSnackbar(getString(R.string.error_empty_firstName))
                false
            }

            !ValidationUtils.checkName(firstName) -> {
                mBinding?.root?.onSnackbar(getString(R.string.specialCharactersnot_first))
                false
            }

            lastName.isEmpty() -> {
                mBinding?.root?.onSnackbar(getString(R.string.enter_email))
                false
            }

            lastName.isNotEmpty() -> {
                if (!isValidEmail(lastName)) {
                    mBinding?.root?.onSnackbar(getString(R.string.invalid_email))
                    false
                } else {
                    true
                }
            }
            else -> true
        }
    }

    fun isValidEmail(target: CharSequence): Boolean {
        val check: Boolean
        val p: Pattern
        val m: Matcher

        val EMAIL_STRING = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@" + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$"

        p = Pattern.compile(EMAIL_STRING)

        m = p.matcher(target)
        check = m.matches()

        if (!check) {
            return false
        }
        return check
    }

    private fun setCardRegxList() {
        listOfPattern = ArrayList<String>()
        val ptVisa = "^4[0-9]{3,}$"
        listOfPattern.add(ptVisa)
        val ptMasterCard = "^5[1-5][0-9]{3,}$"
        listOfPattern.add(ptMasterCard)
        val ptAmeExp = "^3[47][0-9]{5,}$"
        listOfPattern.add(ptAmeExp)
        val ptDinClb = "^3(?:0[0-5]|[68][0-9])[0-9]{4,}$"
        listOfPattern.add(ptDinClb)
        val ptDiscover = "^6(?:011|5[0-9]{2})[0-9]{3,}$"
        listOfPattern.add(ptDiscover)
        val ptJcb = "^(?:2131|1800|35[0-9]{3})[0-9]{3,}$"
        listOfPattern.add(ptJcb)
        val ptUnionPay = "^(62|88)[0-9]{5,}$"
        listOfPattern.add(ptUnionPay)
        val ptElo = "^((((636368)|(438935)|(504175)|(451416)|(636297))[0-9]{0,10})|((5067)|(4576)|(4011))[0-9]{0,12})$"
        listOfPattern.add(ptElo)
        val ptHiperCard = "^(606282|3841)[0-9]{5,}$"
        listOfPattern.add(ptHiperCard)
    }

    override fun onDeleteCard() {
        //do nothing
    }

    override fun onErrorOccur(message: String) {
        mBinding?.root?.onSnackbar(message)
    }

    override fun onSessionExpire() {
        openActivityOnTokenExpire()
    }

}