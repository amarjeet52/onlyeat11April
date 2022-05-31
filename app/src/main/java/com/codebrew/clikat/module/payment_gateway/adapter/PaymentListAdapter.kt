package com.codebrew.clikat.module.payment_gateway.adapter


import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.codebrew.clikat.R
import com.codebrew.clikat.data.constants.AppConstants
import com.codebrew.clikat.data.model.api.Currency
import com.codebrew.clikat.data.model.others.CustomPayModel
import com.codebrew.clikat.databinding.ItemPaymentListBinding
import com.codebrew.clikat.modal.other.SettingModel
import com.codebrew.clikat.utils.Utils
import com.codebrew.clikat.utils.configurations.TextConfig
import kotlinx.android.synthetic.main.item_payment_list.view.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class PaymentListAdapter(private val isFromWallet: Boolean = false,
                         val clientInform: SettingModel.DataBean.SettingData?,
                         private val textConfig: TextConfig?,private val selectedCurrency:Currency?) :
        ListAdapter<PaymentItem, RecyclerView.ViewHolder>(ItemListDiffCallback()) {

    private val adapterScope = CoroutineScope(Dispatchers.Default)
    private lateinit var mCallback: PayListener

    var prevPos: Int = -1

    fun settingCallback(mCallback: PayListener) {
        this.mCallback = mCallback
    }


    fun changeIsSelected(position: Int) {
        if (prevPos != -1 && prevPos != position) {
            val item = getItem(prevPos)
            item.payItem.isSelected = false
            notifyItemChanged(prevPos)
        }
        prevPos = position

        getItem(position).payItem.isSelected = true
        notifyItemChanged(position)

    }

    fun submitItemList(list: List<CustomPayModel>?) {
        adapterScope.launch {
            val items = list?.map {
                if (it.payName.equals("Stripe Pay"))
                    it.payName = "Debit/Credit Card"
                PaymentItem.ProductDataItem(it)
            }

            withContext(Dispatchers.Main) {
                submitList(items)
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is ImageViewHolder -> {
                val modelItem = getItem(position) as PaymentItem.ProductDataItem
                holder.bind(modelItem.payItem, mCallback, isFromWallet, clientInform,textConfig,selectedCurrency)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return ImageViewHolder.from(parent)
    }

    fun settingLyt(context: Context, mSettingData: ArrayList<SettingModel.DataBean.FeatureData>?,
                   textConfig: TextConfig?): MutableList<CustomPayModel> {

        val mPaymentList: MutableList<CustomPayModel>? = mutableListOf()

        mSettingData?.forEachIndexed { index, featureData ->

            if (featureData.type_name == "payment_gateway" && featureData.is_active == 1 && featureData.key_value_front?.isNotEmpty() == true) {
                when (featureData.name) {
                    "Stripe" -> {
                        if (featureData.key_value_front[0]?.key == "stripe_publish_key") {
                            mPaymentList?.add(CustomPayModel(context.getString(R.string.online_payment), R.drawable.ic_payment_card,
                                    featureData.key_value_front[0]?.value, "stripe", addCard = true,payment_name = featureData.name))
                        }
                    }
                    "payuLatam" -> {
                        if (featureData.key_value_front[0]?.key == "basic_auth") {
                            mPaymentList?.add(CustomPayModel(context.getString(R.string.online_payment), R.drawable.ic_payment_card,
                                    featureData.key_value_front[0]?.value, "payuLatam", addCard = true,payment_name = featureData.name))
                        }
                    }

                    "Conekta" -> {
                        if (featureData.key_value_front[0]?.key == "conekta_publish_key") {
                            mPaymentList?.add(CustomPayModel(context.getString(R.string.conekta), R.drawable.ic_payment_card,
                                    featureData.key_value_front[0]?.value, "conekta",payment_name = featureData.name))
                        }
                    }

                    "Zelle" -> {
                        mPaymentList?.add(CustomPayModel(context.getString(R.string.zelle), R.drawable.ic_payment_card,
                                featureData.key_value_front[0]?.value,
                                "zelle", featureData.key_value_front,payment_name = featureData.name))
                    }

                    "PipolPay" -> {
                        mPaymentList?.add(CustomPayModel(context.getString(R.string.pipol_pay), R.drawable.ic_payment_card,
                                featureData.key_value_front[0]?.value,
                                "PipolPay", featureData.key_value_front,payment_name = featureData.name))
                    }

                    "Cashapp" -> {
                        mPaymentList?.add(CustomPayModel(context.getString(R.string.cashapp), R.drawable.ic_payment_card,
                                featureData.key_value_front[0]?.value,
                                "cashapp", featureData.key_value_front,payment_name = featureData.name))
                    }

                    "Razor Pay" -> {
                        if (featureData.key_value_front[0]?.key == "razorpay_key_id") {
                            mPaymentList?.add(CustomPayModel(textConfig?.razor_pay, R.drawable.ic_payment_card,
                                    featureData.key_value_front[0]?.value, "razorpay",payment_name = featureData.name))
                        }
                    }

                    "Braintree" -> {
                        if (featureData.key_value_front[0]?.key == "venmo_braintree_public_key") {
                            mPaymentList?.add(CustomPayModel(textConfig?.braintree, R.drawable.ic_payment_card,
                                    featureData.key_value_front[0]?.value, "braintree",payment_name = featureData.name))
                        }
                    }

                    "paystack" -> {
                        if (featureData.key_value_front[0]?.key == "paystack_publish_key") {
                            mPaymentList?.add(CustomPayModel(context.getString(R.string.paystack), R.drawable.ic_payment_card,
                                    featureData.key_value_front[0]?.value, "paystack",payment_name = featureData.name))
                        }
                    }

                    "Square" -> {
                        if (featureData.key_value_front[0]?.key == "square_publish_key") {
                            mPaymentList?.add(CustomPayModel(context.getString(R.string.square_pay),
                                    R.drawable.ic_payment_card, featureData.key_value_front[0]?.value, "squareup", addCard = true,payment_name = featureData.name))
                        }
                    }

                    "Peach" -> {
                        mPaymentList?.add(CustomPayModel(context.getString(R.string.peach),
                                R.drawable.ic_payment_card, featureData.key_value_front[0]?.value, "peach", addCard = true,
                                payment_name = featureData.name))
                    }
                    "SADAD" -> {
                        mPaymentList?.add(CustomPayModel(context.getString(R.string.saded),
                                R.drawable.ic_payment_card, featureData.key_value_front.firstOrNull()?.value, "sadded", addCard = false,
                                payment_name = featureData.name))
                    }

                    "Tap" -> {
                        if (featureData.key_value_front[0]?.key == "tap_publish_key") {
                            mPaymentList?.add(CustomPayModel(context.getString(R.string.tap),
                                    R.drawable.ic_payment_card, featureData.key_value_front.firstOrNull()?.value, "tap", addCard = false,
                                    payment_name = featureData.name))
                        }
                    }

                    "elavon-converge" -> {
                        mPaymentList?.add(CustomPayModel(context.getString(R.string.elavon),
                                R.drawable.ic_payment_card, featureData.key_value_front.firstOrNull()?.value, "converge", addCard = false,
                                payment_name = featureData.name))
                    }

                    "MyFatoorah" -> {
                        mPaymentList?.add(CustomPayModel(textConfig?.my_fatoorah,
                                R.drawable.ic_payment_card, featureData.key_value_front.firstOrNull()?.value, "myfatoorah", addCard = false,
                                payment_name = featureData.name))
                    }

                    "Windcave" -> {
                        mPaymentList?.add(CustomPayModel(context.getString(R.string.windcave),
                                R.drawable.ic_payment_card, featureData.key_value_front.firstOrNull()?.value, "windcave", addCard = false,
                                payment_name = featureData.name))
                    }
                    "Aamarpay" -> {
                        mPaymentList?.add(CustomPayModel(context.getString(R.string.aamar_pay),
                                R.drawable.ic_payment_card, featureData.key_value_front.firstOrNull()?.value, "aamarpay", addCard = false,
                                payment_name = featureData.name))
                    }
                    "mPaisa" -> {
                        mPaymentList?.add(CustomPayModel(context.getString(R.string.mpaisa),
                                R.drawable.ic_payment_card, featureData.key_value_front.firstOrNull()?.value, "mpaisa", addCard = false
                        , payment_name = featureData.name))
                    }
                    "Datatrans" -> {
                        mPaymentList?.add(CustomPayModel(context.getString(R.string.datatrans),
                                R.drawable.ic_payment_card, featureData.key_value_front.firstOrNull()?.value, "datatrans", addCard = false,
                                payment_name = featureData.name))
                    }
                    /*"Paypal" -> {
                        if (featureData.key_value_front[0]?.key == "paypal_client_key") {
                            mPaymentList?.add(CustomPayModel(context.getString(R.string.pay_pal), R.drawable.ic_payment_card, featureData.key_value_front[0]?.value, "paypal"))
                        }
                    }*/
                    "Payhere" -> {
                        mPaymentList?.add(CustomPayModel(context.getString(R.string.payhere),
                                R.drawable.ic_payment_card, featureData.key_value_front.firstOrNull()?.value, "payhere", addCard = false,
                                payment_name = featureData.name))
                    }
                    "Mumybene" -> {
                        addMumyBenePayment(context, "Airtel Money", R.drawable.opertor_airtel_money, mPaymentList, featureData)
                        addMumyBenePayment(context, "MTN Mobile Money", R.drawable.opertor_mtn_logo_cmyk, mPaymentList, featureData)
                        addMumyBenePayment(context, "Zamtel Pay", R.drawable.opertor_zamtel_kwacha_logo, mPaymentList, featureData)
                        addMumyBenePayment(context, "Indo Bank", R.drawable.opertor_indo_zambia_bank, mPaymentList, featureData)
                        addMumyBenePayment(context, "Investrust", R.drawable.opertor_investrust, mPaymentList, featureData)
                        addMumyBenePayment(context, "543", R.drawable.ic_543, mPaymentList, featureData)
                    }
                    "Paytabs" -> {
                        mPaymentList?.add(CustomPayModel(context.getString(R.string.pay_tabs),
                                R.drawable.ic_payment_card, featureData.key_value_front.firstOrNull()?.value, "paytab", payement_front = featureData.key_value_front,
                                addCard = false, payment_name = featureData.name))
                    }
                    "cred movil" -> {
                        mPaymentList?.add(CustomPayModel(context.getString(R.string.creds_movil), R.drawable.ic_payment_card,
                                featureData.key_value_front.firstOrNull()?.value,
                                "cred_movil", featureData.key_value_front, payment_name = featureData.name))
                    }

                    "Authorize.Net" -> {
                        mPaymentList?.add(CustomPayModel(context.getString(R.string.authorise_net), R.drawable.ic_payment_card,
                                featureData.key_value_front[0]?.value, "authorize_net", addCard = true/*featureData.type_id == 0*/ ,payment_name = featureData.name))
                    }
                    "Pago Facil" -> {
                        mPaymentList?.add(CustomPayModel(context.getString(R.string.pagofacil), R.drawable.ic_payment_card,
                                featureData.key_value_front[0]?.value, "pago_facil", addCard = false/*featureData.type_id == 0*/, payment_name = featureData.name))
                    }
                    "Thawani"->{
                        mPaymentList?.add(CustomPayModel(context.getString(R.string.thawani),
                                R.drawable.ic_payment_card, featureData.key_value_front.firstOrNull()?.value, "thawani",
                                payement_front = featureData.key_value_front,
                                addCard = false, payment_name = featureData.name))
                    }
                    "Telr"->{
                        mPaymentList?.add(CustomPayModel(context.getString(R.string.teller),
                                R.drawable.ic_payment_card, featureData.key_value_front.firstOrNull()?.value, "telr",
                                payement_front = featureData.key_value_front,
                                addCard = false, payment_name = featureData.name))
                    }
                    "Hyperpay"->{
                        mPaymentList?.add(CustomPayModel(context.getString(R.string.hyper_pay),
                                R.drawable.ic_payment_card, featureData.key_value_front.firstOrNull()?.value, "hyperpay",
                                payement_front = featureData.key_value_front,
                                addCard = false, payment_name = featureData.name))
                    }
                    "SADADQA"->{
                        mPaymentList?.add(CustomPayModel(context.getString(R.string.sadad),
                                R.drawable.ic_payment_card, featureData.key_value_front.firstOrNull()?.value, "sadadqa",
                                payement_front = featureData.key_value_front,
                                addCard = false, payment_name = featureData.name))
                    }
                    "transbank"->{
                        mPaymentList?.add(CustomPayModel(context.getString(R.string.trans_bank),
                                R.drawable.ic_payment_card, featureData.key_value_front.firstOrNull()?.value, "transbank",
                                payement_front = featureData.key_value_front,
                                addCard = false, payment_name = featureData.name))
                    }
                    "paymaya"->{
                        mPaymentList?.add(CustomPayModel(context.getString(R.string.pay_maya),
                                R.drawable.ic_payment_card, featureData.key_value_front.firstOrNull()?.value, "paymaya",
                                payement_front = featureData.key_value_front,
                                addCard = false, payment_name = featureData.name))
                    }
                    "urway"->{
                        mPaymentList?.add(CustomPayModel(context.getString(R.string.ur_way),
                                R.drawable.ic_payment_card, featureData.key_value_front.firstOrNull()?.value, "urway",
                                payement_front = featureData.key_value_front,
                                addCard = false, payment_name = featureData.name))
                    }
                    "safe2pay" -> {
                        if (featureData.key_value_front[0]?.key == "safe2pay_apikey") {
                            mPaymentList?.add(CustomPayModel(context.getString(R.string.safe_2_pay), R.drawable.ic_payment_card,
                                    featureData.key_value_front[0]?.value, "safe2pay", addCard = false,payment_name = featureData.name))
                        }
                    }
                }
            }
        }

        //mPaymentList?.dropWhile { cusPay->
        //            payment_gateways?.any{cusPay.payment_token==it}==true
        //        }?.toMutableList()


        return mPaymentList ?: mutableListOf()
    }

    private fun addMumyBenePayment(context: Context, name: String, drawable: Int, mPaymentList: MutableList<CustomPayModel>?, featureData: SettingModel.DataBean.FeatureData) {
        mPaymentList?.add(CustomPayModel(context.getString(R.string.mumybene),
                drawable, featureData.key_value_front?.firstOrNull()?.value, "mumybene", addCard = false, mumybenePay = name, payment_name = featureData.name))
    }

    fun filterList(paymentGateways: ArrayList<String>?, mPaymentList: MutableList<CustomPayModel>?): MutableList<CustomPayModel>? {

        if (paymentGateways?.isEmpty() == true || paymentGateways == null) return mPaymentList

        val mFilterList: MutableList<CustomPayModel>? = mutableListOf()
        mPaymentList?.forEach { cusPay ->
            paymentGateways.forEach {
                if (cusPay.payment_name?.toLowerCase() == it) {
                    mFilterList?.add(cusPay)
                }
            }
        }

        return mFilterList
    }

}


class ImageViewHolder private constructor(val binding: ItemPaymentListBinding) :
        RecyclerView.ViewHolder(binding.root) {

    val context = itemView.context
    fun bind(item: CustomPayModel, listener: PayListener, isFromWallet: Boolean, clientInform: SettingModel.DataBean.SettingData?, textConfig: TextConfig?,selectedCurrency:Currency?) {
        binding.payModel = item
        binding.listener = listener
        binding.isFromWallet = isFromWallet
        binding.position = adapterPosition
        binding.executePendingBindings()


        if (item.isSelected == true)
            itemView.ivSelection?.setImageResource(R.drawable.ic_radio_active)
        else
            itemView.ivSelection?.setImageResource(R.drawable.ic_radio_unactive)

        if (item.payName == context.getString(R.string.wallet)) {
            itemView.rb_choose_payment?.text = context.getString(R.string.wallet_with_amt, AppConstants.CURRENCY_SYMBOL, Utils.getPriceFormat(item.walletAmount
                    ?: 0f,clientInform,selectedCurrency))
            itemView.tvWalletDiscountText?.visibility = View.VISIBLE
            if (item.showAddMoney) {
                itemView.tvAddMoney?.visibility = View.VISIBLE
                itemView.tvWalletDiscountText?.text = context.getString(R.string.insufficient_balance)
                itemView.tvWalletDiscountText?.setTextColor(ContextCompat.getColor(context, R.color.red))
            } else {
                itemView.tvAddMoney?.visibility = View.GONE
                itemView.tvWalletDiscountText?.setTextColor(ContextCompat.getColor(context, R.color.username4))
                itemView.tvWalletDiscountText?.text = context.getString(R.string.pay_by_wallet, (clientInform?.payment_through_wallet_discount?.toFloatOrNull()
                        ?: 0f).toString())
            }
        } else if (item.payName == textConfig?.braintree) {
            itemView.rb_choose_payment?.text = textConfig?.braintree
        } else {
            itemView.tvAddMoney?.visibility = View.GONE
            itemView.tvWalletDiscountText?.visibility = View.GONE
        }
    }


    companion object {
        fun from(parent: ViewGroup): ImageViewHolder {
            val layoutInflater = LayoutInflater.from(parent.context)
            val binding = ItemPaymentListBinding.inflate(layoutInflater, parent, false)
            return ImageViewHolder(binding)
        }
    }
}


class ItemListDiffCallback : DiffUtil.ItemCallback<PaymentItem>() {
    override fun areItemsTheSame(oldItem: PaymentItem, newItem: PaymentItem): Boolean {
        return oldItem.payItem == newItem.payItem
    }

    @SuppressLint("DiffUtilEquals")
    override fun areContentsTheSame(oldItem: PaymentItem, newItem: PaymentItem): Boolean {
        return oldItem.payItem == newItem.payItem
    }
}

class PayListener(val payListener: (imageModel: CustomPayModel, adapterPos: Int) -> Unit, val addMoneyListener: (item: CustomPayModel) -> Unit) {
    fun imageClick(image: CustomPayModel, adapterPos: Int) = payListener(image, adapterPos)
    fun addMoneyClicked(item: CustomPayModel) = addMoneyListener(item)
}


sealed class PaymentItem {
    data class ProductDataItem(val mPaymentData: CustomPayModel) : PaymentItem() {
        override val payItem = mPaymentData
    }

    abstract val payItem: CustomPayModel
}





