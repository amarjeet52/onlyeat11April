package com.codebrew.clikat.module.addon_quant


import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.codebrew.clikat.R
import com.codebrew.clikat.app_utils.AppUtils
import com.codebrew.clikat.app_utils.DialogsUtil
import com.codebrew.clikat.data.AppDataType
import com.codebrew.clikat.data.DataManager
import com.codebrew.clikat.data.constants.AppConstants
import com.codebrew.clikat.data.constants.PrefenceConstants
import com.codebrew.clikat.data.model.api.Currency
import com.codebrew.clikat.databinding.FragmentAddonSavedBinding
import com.codebrew.clikat.modal.CartInfo
import com.codebrew.clikat.modal.CartList
import com.codebrew.clikat.modal.other.ProductDataBean
import com.codebrew.clikat.modal.other.SettingModel
import com.codebrew.clikat.module.addon_quant.adpater.ItemListener
import com.codebrew.clikat.module.addon_quant.adpater.ItemQuantAdapter
import com.codebrew.clikat.module.product.product_listing.DialogListener
import com.codebrew.clikat.module.product_addon.AddonFragment
import com.codebrew.clikat.preferences.DataNames
import com.codebrew.clikat.utils.Utils
import com.codebrew.clikat.utils.configurations.Configurations
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.android.support.AndroidSupportInjection
import kotlinx.android.synthetic.main.fragment_addon_saved.*
import java.text.DecimalFormat
import javax.inject.Inject

/**
 * A simple [Fragment] subclass.
 */

private const val ARG_PARAM1 = "addonDetail"
private const val ARG_PARAM2 = "productDetail"
private const val ARG_PARAM3 = "deliveryType"

class SavedAddon : BottomSheetDialogFragment(), DialogListener {


    private var selectedCurrency: Currency?=null

    @Inject
    lateinit var appUtils: AppUtils

    @Inject
    lateinit var dataManager: DataManager

    @Inject
    lateinit var mDialogsUtil: DialogsUtil

    private var param1: ArrayList<CartInfo>? = null

    private var productDetail: ProductDataBean? = null

    private lateinit var adapter: ItemQuantAdapter

    private var bookingFlowBean: SettingModel.DataBean.BookingFlowBean? = null

    private var screenFlowBean: SettingModel.DataBean.ScreenFlowBean? = null
    private var settingsData: SettingModel.DataBean.SettingData? = null
    private var currentItem: Int = 0

    private var mDeliveryType: Int = 0


    private var mListener: AddonFragment.AddonCallback? = null

    private var mBinding: FragmentAddonSavedBinding? = null
    private val decimalFormat: DecimalFormat = DecimalFormat("0.00")

    private val textConfig by lazy { appUtils.loadAppConfig(0).strings}

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment

        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_addon_saved, container, false)
        mBinding?.color = Configurations.colors
        mBinding?.textConfig = textConfig
        return mBinding?.root
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidSupportInjection.inject(this)
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getParcelableArrayList(ARG_PARAM1)
            productDetail = it.getParcelable(ARG_PARAM2)
            mDeliveryType = it.getInt(ARG_PARAM3)
        }
        settingsData = dataManager.getGsonValue(DataNames.SETTING_DATA, SettingModel.DataBean.SettingData::class.java)
        screenFlowBean = dataManager.getGsonValue(DataNames.SCREEN_FLOW, SettingModel.DataBean.ScreenFlowBean::class.java)
        bookingFlowBean = dataManager.getGsonValue(DataNames.BOOKING_FLOW, SettingModel.DataBean.BookingFlowBean::class.java)
        selectedCurrency = dataManager.getGsonValue(PrefenceConstants.CURRENCY_INF, Currency::class.java)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = ItemQuantAdapter(settingsData)

        adapter.settingCallback(
                ItemListener(
                        //add item
                        {
                            currentItem = param1?.indexOf(it) ?: 0
                            addCart(it)
                        }
                        //remove item
                        , {
                    currentItem = param1?.indexOf(it) ?: 0
                    minusCart(it)
                }))

        val lytManager = LinearLayoutManager(activity, RecyclerView.VERTICAL, false)
        rv_saved_addon.layoutManager = lytManager

        rv_saved_addon.adapter = adapter

        param1?.map {
            it.price=Utils.getPriceFormat(it.price, settingsData,selectedCurrency).toFloatOrNull()?:0f                }

        adapter.submitItemList(param1)


        tv_prod_name.text = productDetail?.name


        iv_cross.setOnClickListener {
            dismiss()
        }


        tv_add_customize.setOnClickListener {
            AddonFragment.newInstance(productDetail!!, mDeliveryType, mListener!!).show(requireFragmentManager(), "addOn")
            dismiss()
        }

    }


    fun updateCallback(mListener: AddonFragment.AddonCallback) {
        this.mListener = mListener
    }

    private fun minusCart(it: CartInfo) {

        var quantity = it.quantity

        if (quantity > 0f) {

            if (settingsData?.is_decimal_quantity_allowed == "1")
                quantity = decimalFormat.format((quantity - AppConstants.DECIMAL_INTERVAL)).toFloat()
            else
                quantity--

            it.quantity = quantity
            productDetail?.prod_quantity = quantity
            productDetail?.fixed_quantity = quantity
            productDetail?.productAddonId = it.productAddonId

            if (quantity == 0f) {
                param1?.removeAt(currentItem)

                productDetail?.let { it1 -> appUtils.removeCartItem(it1) }

                param1?.map {
                    it.price=Utils.getPriceFormat(it.price, settingsData,selectedCurrency).toFloatOrNull()?:0f                }

                // adapter.notifyItemRemoved(currentItem)
                adapter.submitItemList(param1)
            } else {
                param1?.set(currentItem, it)

                appUtils.updateItem(productDetail!!)
                adapter.notifyItemChanged(currentItem)
            }
            if (param1?.size == 0) {
                dismiss()
            }
        }
    }

    private fun addCart(cartInfo: CartInfo) {

        if (bookingFlowBean?.vendor_status == 0 && appUtils.checkVendorStatus(cartInfo.supplierId, vendorBranchId = cartInfo.suplierBranchId, branchFlow = settingsData?.branch_flow)) {
            mDialogsUtil.openAlertDialog(activity
                    ?: requireContext(), getString(R.string.clearCart, textConfig?.supplier,textConfig?.proceed
                    ?: ""), "Yes", "No", this)
        } else {
            if (appUtils.checkBookingFlow(requireContext(), cartInfo.productId, this)) {
                val quantity = (appUtils.getCartList().cartInfos?.filter { it.productId == cartInfo.productId }?.sumByDouble { it.quantity.toDouble() }
                        ?: 0.0).toFloat()
                val remaingProd = cartInfo.prodQuant?.minus(cartInfo.purchasedQuant ?: 0f) ?: 0f

                if (quantity < remaingProd) {
                    updateToCart(cartInfo)
                } else {
                    Toast.makeText(activity, getString(R.string.maximum_limit_cart), Toast.LENGTH_SHORT).show()
                    //dialog?.currentFocus?.onSnackbar(getString(R.string.maximum_limit_cart))
                }
            }

        }
    }

    private fun updateToCart(it: CartInfo) {
        if (screenFlowBean?.app_type == AppDataType.Food.type) {
            val cartList: CartList? = dataManager.getGsonValue(DataNames.CART, CartList::class.java)

            if (cartList?.cartInfos?.size?:0 > 0) {
                if (cartList?.cartInfos?.any { cartList.cartInfos?.get(0)?.deliveryType != it.deliveryType } == true) {
                    appUtils.clearCart()
                }
            }
        }

        var quantity = it.quantity
        if (settingsData?.is_decimal_quantity_allowed == "1")
            quantity = decimalFormat.format((quantity+ AppConstants.DECIMAL_INTERVAL)).toFloat()
        else
            quantity++

        it.quantity = quantity
        productDetail?.prod_quantity = quantity
        productDetail?.fixed_quantity = quantity
        productDetail?.productAddonId = it.productAddonId

        //for adapter count value
        param1?.set(currentItem, it)
        adapter.notifyItemChanged(currentItem)

        productDetail?.let { it1 -> appUtils.updateItem(it1) }
    }


    override fun onSucessListner() {
        appUtils.clearCart()
    }

    override fun onErrorListener() {

    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)

        mListener?.onAddonAdded(productDetail!!)
    }


    companion object {

        @JvmStatic
        fun newInstance(productBean: ProductDataBean?, deliveryType: Int, param1: List<CartInfo>,
                        mListener: AddonFragment.AddonCallback) =
                SavedAddon().apply {
                    arguments = Bundle().apply {
                        putParcelable(ARG_PARAM2, productBean)
                        putInt(ARG_PARAM3, deliveryType)

                        if (!param1.isNullOrEmpty()) {
                            putParcelableArrayList(ARG_PARAM1, param1 as ArrayList<CartInfo>)
                        }
                    }
                    updateCallback(mListener)
                }
    }
}
