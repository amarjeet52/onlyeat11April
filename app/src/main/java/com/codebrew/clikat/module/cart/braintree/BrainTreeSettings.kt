package com.codebrew.clikat.module.cart.braintree

import android.content.Context
import com.braintreepayments.api.dropin.utils.PaymentMethodType
import com.braintreepayments.api.models.*
import com.braintreepayments.cardform.view.CardForm

object BrainTreeSettings {


    private val ENVIRONMENT = "environment"
    private val VERSION = "version"
    private val SANDBOX_TOKENIZATION_KEY = "sandbox_csknjscq_p79gcgck727bsf9g"
    private val PRODUCTION_TOKENIZATION_KEY = "production_ykzrbxk9_tfpvk58cvts5sk5w"

    fun getEnvironment(): Int = 0 /*0:Sandbox/1:Production*/

    fun getCustomerId(): String? = ""

    fun getMerchantAccountId(): String? = ""

    fun shouldCollectDeviceData(): Boolean = true

    fun useTokenizationKey(): Boolean = true

    fun isPayPalSignatureVerificationDisabled(): Boolean = true

    fun useHardcodedPayPalConfiguration(): Boolean = false

    fun isThreeDSecureEnabled(): Boolean = true

    fun isThreeDSecureRequired(): Boolean = true

    fun getThreeDSecureVersion(): String? = ThreeDSecureRequest.VERSION_2


    fun isVaultManagerEnabled(): Boolean = false

    fun getCardholderNameStatus(): Int = CardForm.FIELD_OPTIONAL
            /*  CardForm.FIELD_OPTIONAL / CardForm.FIELD_REQUIRED / CardForm.FIELD_DISABLED*/

    fun isSaveCardCheckBoxVisible( ): Boolean = true

    fun defaultVaultSetting( ): Boolean = true

    fun getEnvironmentTokenizationKey(): String? {
        return when (getEnvironment()) {
            0 -> SANDBOX_TOKENIZATION_KEY
            1 -> PRODUCTION_TOKENIZATION_KEY
            else -> ""
        }
    }

    fun getThreeDSecureMerchantAccountId(): String? {
        return if (isThreeDSecureEnabled() && getEnvironment() == 1) {
            "test_AIB"
        } else {
            null
        }
    }

    fun getUnionPayMerchantAccountId(context: Context): String? {
        return if (getEnvironment() == 0) {
            "fake_switch_usd"
        } else {
            null
        }
    }

    fun getReadableDetailFromNonce(paymentMethodNonce: PaymentMethodNonce?){
        var details = ""
        details += "PaymentMethodType: ${PaymentMethodType.forType(paymentMethodNonce)}"
        details += "Payment description: ${paymentMethodNonce?.description}"
        when (paymentMethodNonce) {
            is CardNonce -> {
                val cardNonce = paymentMethodNonce
                details += "Card Last Two: ${cardNonce.lastTwo}"
                details += "3DS isLiabilityShifted: ${cardNonce.threeDSecureInfo.isLiabilityShifted}"
                details += "3DS isLiabilityShiftPossible: ${cardNonce.threeDSecureInfo.isLiabilityShiftPossible}"
            }
            is PayPalAccountNonce -> {
                details += "First name: ${paymentMethodNonce.firstName}"
                details += "Last name: ${paymentMethodNonce.lastName}"
                details += "Email: ${paymentMethodNonce.email}"
                details += "Phone: ${paymentMethodNonce.phone}"
                details += "Payer id: ${paymentMethodNonce.payerId}"
                details += "Client metadata id: ${paymentMethodNonce.clientMetadataId}"
                details += "Billing address: ${formatAddress(paymentMethodNonce.billingAddress)}"
                details += "Shipping address: ${formatAddress(paymentMethodNonce.shippingAddress)}"
            }
            is VenmoAccountNonce -> {

                details += "Username: " + paymentMethodNonce.username
            }
            is GooglePaymentCardNonce -> {

                details += "Underlying Card Last Two: ${paymentMethodNonce.lastTwo}"
                details += "Email: ${paymentMethodNonce.email}"
                details += "Billing address: ${formatAddress(paymentMethodNonce.billingAddress)}"
                details += "Shipping address: ${formatAddress(paymentMethodNonce.shippingAddress)}"
            }
        }
    }


    private fun formatAddress(address: PostalAddress?): String? {
        return address?.recipientName + " " + address?.streetAddress + " " +
                address?.extendedAddress + " " + address?.locality + " " + address?.region +
                " " + address?.postalCode + " " + address?.countryCodeAlpha2
    }
}