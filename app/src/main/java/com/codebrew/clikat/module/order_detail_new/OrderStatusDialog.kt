package com.codebrew.clikat.module.order_detail_new

import android.app.Activity
import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.codebrew.clikat.R
import com.codebrew.clikat.app_utils.AppUtils
import com.codebrew.clikat.app_utils.OrderUtils
import com.codebrew.clikat.app_utils.extension.loadUserImage
import com.codebrew.clikat.data.AppDataType
import com.codebrew.clikat.data.OrderStatus
import com.codebrew.clikat.data.constants.AppConstants
import com.codebrew.clikat.data.model.api.orderDetail.OrderHistory
import com.codebrew.clikat.data.model.others.OrderStatusModel
import com.codebrew.clikat.databinding.FragmentOrderStatusBinding
import com.codebrew.clikat.modal.other.ProductDataBean
import com.codebrew.clikat.modal.other.SettingModel
import com.codebrew.clikat.module.order_detail.adapter.OrderDetailProductAdapter
import com.codebrew.clikat.module.order_detail.adapter.OrderStatusAdapter
import com.codebrew.clikat.module.order_detail_new.adapter.StatusOrderAdapter
import com.codebrew.clikat.preferences.DataNames
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.android.support.AndroidSupportInjection
import kotlinx.android.synthetic.main.fragment_order_status.*
import kotlinx.android.synthetic.main.pager_product_new_item.view.*
import javax.inject.Inject


private const val ARG_PARAM = "OrderData"

class OrderStatusDialog : BottomSheetDialogFragment() {

    @Inject
    lateinit var appUtil: AppUtils

    @Inject
    lateinit var orderUtils: OrderUtils

    private var mBinding: FragmentOrderStatusBinding? = null

    private var listener: OrderShowListener? = null
    private var orderData: OrderHistory? = null

    private val textConfig by lazy { appUtil.loadAppConfig(0).strings }

    private var screenFlowBean: SettingModel.DataBean.ScreenFlowBean? = null

    private var settingData: SettingModel.DataBean.SettingData? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidSupportInjection.inject(this)
        super.onCreate(savedInstanceState)

        orderData = arguments?.getParcelable(ARG_PARAM)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        listener?.onShowDialog()

        settingOrderDetail(orderData)

    }

    private fun settingOrderDetail(orderData: OrderHistory?) {

        val mStatusList = mutableListOf<OrderStatusModel>()

        if (orderData?.status == 11.0) {
            orderData.status = OrderStatus.In_Kitchen.orderStatus
        }

        if (orderData?.status == 10.0) {
            orderData.status = OrderStatus.Shipped.orderStatus
        }

        mStatusList.addAll(orderUtils.getAllStatus(orderData))

        rv_status.layoutManager= LinearLayoutManager(activity, RecyclerView.HORIZONTAL,false)
        rv_status?.adapter = StatusOrderAdapter(mStatusList, orderData?.type, orderData?.self_pickup, orderData?.terminology
                ?: "")


        val mStatusPos = mStatusList.indexOfFirst {
            it.status.orderStatus == orderData?.status
        }

        rv_status.layoutManager?.scrollToPosition(mStatusPos)




        iv_supplier_logo?.loadUserImage("")
        tv_name.text = orderData?.supplier_name
        tv_call.setOnClickListener {
            //listener?.onCallSupplier(orderData?.product)
        }


        text_delivery_adrs.text = orderData?.delivery_address?.address_line_1
        tv_address.text = orderData?.delivery_address?.customer_address

        val totalProdList = calculateProdAddon(orderData?.product)
        val subTotal = orderData?.total_order_price ?: totalProdList?.sumByDouble {
            (it?.fixed_price?.toFloatOrNull()?.times(it.prod_quantity ?: 0f))?.toDouble()
                    ?: 0.0 }?.toFloat()

        tv_amount.text = getString(R.string.currency_tag, AppConstants.CURRENCY_SYMBOL, subTotal.toString())


        rv_orders.adapter = OrderDetailProductAdapter(activity?.applicationContext, totalProdList
                ?: emptyList(), screenFlowBean, settingData, status = orderData?.status,appUtil= appUtil,orderDetail =  orderData)


        driver_group.visibility = if (orderData?.agent?.isNotEmpty() == true) View.VISIBLE else View.GONE
        tv_driver_name.text = orderData?.agent?.firstOrNull()?.name?:""
        text_driver.setOnClickListener {

        }

        tv_order_id.text = orderData?.order_id.toString()
        tv_payment_type.text = when (orderData?.payment_type) {
            DataNames.DELIVERY_CASH -> textConfig?.cod
            else -> getString(R.string.online_pay_tag, orderData?.payment_source)
        }
    }

    private fun calculateProdAddon(productList: List<ProductDataBean?>?): List<ProductDataBean?>? {

        val prodList = arrayListOf<ProductDataBean?>()

        productList?.mapIndexed { index, product ->

            if (product?.adds_on.isNullOrEmpty()) {
                product?.prod_quantity = product?.quantity
                prodList += product?.copy()
            } else {
                product?.adds_on?.groupBy {
                    it?.serial_number
                }?.mapValues {
                    //product.adds_on = it.value
                    product.add_on_name = it.value.map { "${it?.adds_on_type_name} * ${it?.adds_on_type_quantity}" }.joinToString()
                    product.prod_quantity = it.value[0]?.quantity ?: 0f
                    product.fixed_price = product.price?.toFloatOrNull()?.plus(it.value.sumByDouble {
                        (it?.price ?: 0.0f).toDouble().times((it?.adds_on_type_quantity
                                ?: "0").toInt())
                    }.toFloat()).toString()
                    prodList += product.copy()
                }
            }
        }
        return prodList.takeIf { it.isNotEmpty() } ?: productList
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_order_status, container, false)
        return mBinding?.root
    }

    override fun onStart() {
        super.onStart()

        val displayMetrics = DisplayMetrics()
        (context as Activity)
                .windowManager
                .defaultDisplay
                .getMetrics(displayMetrics)
        val windowHeight = displayMetrics.heightPixels


        dialog.also {


            val bottomSheet = dialog?.findViewById<View>(R.id.design_bottom_sheet)
            val behavior = BottomSheetBehavior.from(bottomSheet!!)
            behavior.state = BottomSheetBehavior.STATE_HALF_EXPANDED

            view?.requestLayout()
        }
    }


    override fun onDismiss(dialog: DialogInterface) {
        listener?.onDismissDialog()
    }


    override fun onAttach(context: Context) {
        super.onAttach(context)
        val parent = parentFragment
        listener = if (parent != null) {
            parent as OrderShowListener
        } else {
            context as OrderShowListener
        }
    }

    override fun onDetach() {
        listener = null
        super.onDetach()
    }

    companion object {

        @JvmStatic
        fun newInstance(data: OrderHistory?) =
                OrderStatusDialog().apply {
                    arguments = Bundle().apply {
                        putParcelable(ARG_PARAM, data)
                    }
                }
    }
}