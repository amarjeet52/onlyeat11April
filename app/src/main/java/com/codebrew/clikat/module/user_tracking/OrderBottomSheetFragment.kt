package com.codebrew.clikat.module.user_tracking

import android.Manifest
import android.annotation.SuppressLint
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.codebrew.clikat.R
import com.codebrew.clikat.app_utils.AppUtils
import com.codebrew.clikat.app_utils.CommonUtils.Companion.isNetworkConnected
import com.codebrew.clikat.app_utils.extension.loadImage
import com.codebrew.clikat.base.BaseContainerBottomSheetDialogFragment
import com.codebrew.clikat.data.AppDataType
import com.codebrew.clikat.data.OrderStatus
import com.codebrew.clikat.data.constants.AppConstants
import com.codebrew.clikat.data.constants.PrefenceConstants
import com.codebrew.clikat.data.model.api.Currency
import com.codebrew.clikat.data.model.api.orderDetail.OrderHistory
import com.codebrew.clikat.data.model.others.OrderStatusModel
import com.codebrew.clikat.data.preferences.PreferenceHelper
import com.codebrew.clikat.databinding.FragmentOrderBottomSheetBinding
import com.codebrew.clikat.modal.other.SettingModel
import com.codebrew.clikat.module.order_detail_new.adapter.StatusOrderAdapter
import com.codebrew.clikat.preferences.DataNames
import com.codebrew.clikat.utils.Utils
import com.codebrew.clikat.utils.configurations.TextConfig
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import dagger.android.support.AndroidSupportInjection
import pub.devrel.easypermissions.EasyPermissions
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.*


class OrderBottomSheetFragment : BaseContainerBottomSheetDialogFragment<FragmentOrderBottomSheetBinding>(), EasyPermissions.PermissionCallbacks {

    private var binding: FragmentOrderBottomSheetBinding? = null
    private var orderDetail: OrderHistory? = null
    private var selectedCurrency: Currency? = null
    private val decimalFormat: DecimalFormat = DecimalFormat("0.00", DecimalFormatSymbols.getInstance(Locale.ENGLISH))
    private var settingData: SettingModel.DataBean.SettingData? = null

    lateinit var dataManager: PreferenceHelper


    lateinit var appUtil: AppUtils
    private var textConfig: TextConfig? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (activity is UserTracking) {
            val actiivty = activity as UserTracking
            dataManager = actiivty.prefHelper
            appUtil = actiivty.appUtils
        }

        textConfig = appUtil.loadAppConfig(0).strings

        arguments?.let {
            orderDetail = it.getParcelable(ARG_PARAM1)
        }
    }

    private fun performDependencyInjection() {
        AndroidSupportInjection.inject(this@OrderBottomSheetFragment)
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        //   requireActivity().finish()
    }

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (dialog as? BottomSheetDialog)?.behavior?.apply {
            isFitToContents = true
            peekHeight = 600
            isDraggable = true
            state = BottomSheetBehavior.STATE_HALF_EXPANDED
        }

        (dialog as? BottomSheetDialog)?.setCancelable(false)
        (dialog as? BottomSheetDialog)?.setCanceledOnTouchOutside(false)

        settingData = dataManager.getGsonValue(DataNames.SETTING_DATA, SettingModel.DataBean.SettingData::class.java)
        selectedCurrency = dataManager.getGsonValue(PrefenceConstants.CURRENCY_INF, Currency::class.java)

        binding = getViewDataBinding()

        orderDetail?.let { orderDetail ->

            binding?.tvDeliveryPlace?.text = "${
                orderDetail.delivery_address?.customer_address
                        ?: ""
            } ,${orderDetail.delivery_address?.address_line_1 ?: ""}"


            binding?.tvSupplierName?.text = orderDetail.supplier_name

            val items = StringBuilder()
            orderDetail.product?.forEach {
                items.append("${it?.quantity}*${it?.name}")
                items.append("\n")
            }

            binding?.clSupplier?.setOnClickListener {

                if (permissionFile.hasCallPermissions(activity ?: requireContext())) {

                } else {
                    permissionFile.phoneCallTask(activity ?: requireContext())
                }

            }

            binding?.tvOrderPlaceHeader?.text = items.toString()

            binding?.ivSupplierImage?.loadImage(orderDetail.logo ?: "")

            orderDetail.agent?.let { agents ->
                binding?.tvDriverName?.text = agents.firstOrNull()?.name
                binding?.gpDriver?.visibility = View.VISIBLE

                binding?.tvDriverName?.setOnClickListener {
                    if (binding?.tvDriverName?.text.toString().equals("Driver not assigned")) {

                    } else {
                        if (permissionFile.hasCallPermissions(activity ?: requireContext())) {
                            callPhone(agents.firstOrNull()?.phone_number ?: "")
                        } else {
                            permissionFile.phoneCallTask(activity ?: requireContext())
                        }
                    }
                }
            }

            if (orderDetail.agent.isNullOrEmpty())
                binding?.tvDriverName?.text = getString(R.string.driver_not_assigned)



            binding?.tvOrderId?.text = orderDetail.order_id?.toString()

            binding?.tvOrderAmount?.text = getString(R.string.currency_tag, AppConstants.CURRENCY_SYMBOL, Utils.getPriceFormat(decimalFormat.format(orderDetail.net_amount)?.toFloatOrNull()
                    ?: 0f, settingData, selectedCurrency))

            binding?.tvPaymentMode?.text = if (orderDetail.payment_type == DataNames.SKIP_PAYMENT)
                getString(R.string.out_of_app)
            else if (orderDetail.payment_type == 3 && orderDetail.payment_status == 0) {
                "None"
            } else if (DataNames.DELIVERY_WALLET == orderDetail.payment_type) {
                getString(R.string.wallet)
            } else if (DataNames.DELIVERY_CASH == orderDetail.payment_type) {
                getString(R.string.cash)
            } else if (orderDetail.payment_type == DataNames.DELIVERY_CARD) {
                getString(R.string.online_pay_tag, orderDetail.payment_source)
            } else {
                when (orderDetail.payment_source) {
                    "zelle" -> {
                        getString(R.string.zelle)
                    }
                    "paypal" -> {
                        getString(R.string.pay_pal)
                    }
                    else -> {
                        getString(R.string.online_payment)
                    }
                }
            }

            val mStatusList = mutableListOf<OrderStatusModel>()

            when (1) {
                AppDataType.Food.type -> {
                    mStatusList.add(OrderStatusModel(convertDate(orderDetail.confirmed_on
                            ?: "", orderDetail.status, OrderStatus.Approved.orderStatus), OrderStatus.Approved, orderDetail.status
                            ?: 0.0))
                    mStatusList.add(OrderStatusModel(convertDate(orderDetail.progress_on
                            ?: "", orderDetail.status, OrderStatus.In_Kitchen.orderStatus), OrderStatus.In_Kitchen, orderDetail.status
                            ?: 0.0))
                    if (orderDetail.self_pickup == 0) {
                        mStatusList.add(OrderStatusModel(convertDate(orderDetail.shipped_on
                                ?: "", orderDetail.status, OrderStatus.On_The_Way.orderStatus), OrderStatus.On_The_Way, orderDetail.status
                                ?: 0.0))
                    } else {
                        mStatusList.add(OrderStatusModel(convertDate(orderDetail.shipped_on
                                ?: "", orderDetail.status, OrderStatus.Ready_to_be_picked.orderStatus), OrderStatus.Ready_to_be_picked, orderDetail.status
                                ?: 0.0))
                    }
                    if (orderDetail.self_pickup == 0) {
                        mStatusList.add(OrderStatusModel(convertDate(checkDeliverDate(orderDetail), orderDetail.status, OrderStatus.Delivered.orderStatus), OrderStatus.Delivered, orderDetail.status
                                ?: 0.0))
                    }
                }

                AppDataType.Ecom.type -> {
                    mStatusList.add(OrderStatusModel(convertDate(orderDetail.confirmed_on
                            ?: "", orderDetail.status, OrderStatus.Confirmed.orderStatus), OrderStatus.Confirmed, orderDetail.status
                            ?: 0.0))
                    mStatusList.add(OrderStatusModel(convertDate(orderDetail.progress_on
                            ?: "", orderDetail.status, OrderStatus.Packed.orderStatus), OrderStatus.Packed, orderDetail.status
                            ?: 0.0))
                    mStatusList.add(OrderStatusModel(convertDate(orderDetail.near_on
                            ?: "", orderDetail.status, OrderStatus.Shipped.orderStatus), OrderStatus.Shipped, orderDetail.status
                            ?: 0.0))
                    mStatusList.add(OrderStatusModel(convertDate(orderDetail.shipped_on
                            ?: "", orderDetail.status, OrderStatus.On_The_Way.orderStatus), OrderStatus.On_The_Way, orderDetail.status
                            ?: 0.0))
                    mStatusList.add(OrderStatusModel(convertDate(checkDeliverDate(orderDetail), orderDetail.status, OrderStatus.Delivered.orderStatus), OrderStatus.Delivered, orderDetail.status
                            ?: 0.0))
                }

                AppDataType.HomeServ.type -> {
                    mStatusList.add(OrderStatusModel(convertDate(orderDetail.confirmed_on
                            ?: "", orderDetail.status, OrderStatus.Confirmed.orderStatus), OrderStatus.Confirmed, orderDetail.status
                            ?: 0.0))

                    mStatusList.add(OrderStatusModel(convertDate(orderDetail.progress_on
                            ?: "", orderDetail.status, OrderStatus.In_Kitchen.orderStatus), OrderStatus.In_Kitchen, orderDetail.status
                            ?: 0.0))

                    mStatusList.add(OrderStatusModel(convertDate(orderDetail.near_on
                            ?: "", orderDetail.status, OrderStatus.Reached.orderStatus), OrderStatus.Reached, orderDetail.status
                            ?: 0.0))

                    mStatusList.add(OrderStatusModel(convertDate(orderDetail.shipped_on
                            ?: "", orderDetail.status, OrderStatus.Started.orderStatus), OrderStatus.Started, orderDetail.status
                            ?: 0.0))

                    mStatusList.add(OrderStatusModel(convertDate(checkDeliverDate(orderDetail), orderDetail.status, OrderStatus.Ended.orderStatus), OrderStatus.Ended, orderDetail.status
                            ?: 0.0))

                }
            }

            val mStatusPos = mStatusList.indexOfFirst {
                it.status.orderStatus == orderDetail.status
            }

            binding?.rvStatus?.layoutManager = LinearLayoutManager(requireContext(), RecyclerView.HORIZONTAL, false)
            binding?.rvStatus?.adapter = StatusOrderAdapter(mStatusList, orderDetail.type, orderDetail.self_pickup, orderDetail.terminology
                    ?: "")

            binding?.rvStatus?.layoutManager?.scrollToPosition(mStatusPos)

        }

        val status = orderDetail?.status ?: 0.0


        /*       val cancelHidden = (listOf(OrderStatus.Rejected.orderStatus, OrderStatus.Rating_Given.orderStatus,
                       OrderStatus.Customer_Canceled.orderStatus, OrderStatus.Delivered.orderStatus).contains(status))
                       || settingData?.disable_order_cancel ?: "0" == "0"
                       && (status != OrderStatus.Pending.orderStatus || settingData?.disbale_user_cancel_pending_order ?: "0" == "0")
                       || (settingData?.disable_user_cancel_after_confirm == "1" && status != OrderStatus.Pending.orderStatus)*/

        val cancelHidden = status != OrderStatus.Pending.orderStatus

        binding?.tvCancelOrder?.visibility = if (!cancelHidden) {
            View.VISIBLE
        } else {
            View.GONE
        }

        binding?.tvCancelOrder?.setOnClickListener {
            if (orderDetail == null) return@setOnClickListener
            if (orderDetail?.payment_type == DataNames.DELIVERY_CARD && settingData?.wallet_module == "1") {
                cancelOrderWallet()
            } else {
                if (orderDetail?.status == OrderStatus.Scheduled.orderStatus) sweetDialog() else sweetDialog()
            }
        }

        if (orderDetail?.self_pickup == 1) {
            binding?.groupAddress?.visibility = View.GONE
            binding?.groupDriver?.visibility = View.GONE
            binding?.gpDriver?.visibility = View.GONE
        }
    }

    private fun cancelOrderWallet() {

        if (activity is UserTracking) {
            (activity as UserTracking).cancelOrder(orderDetail?.order_id.toString(), 1)
        }

    }

    private fun sweetDialog() {

        val sweetAlertDialog = AlertDialog.Builder(requireContext())
        sweetAlertDialog.setTitle(getString(R.string.cancel_order, textConfig?.order))
        sweetAlertDialog.setMessage(getString(R.string.doYouCancel, textConfig?.order))

        sweetAlertDialog.setPositiveButton(getString(R.string.ok)) { dialog, which ->
            if (isNetworkConnected(requireContext())) {
                val cancelWallet = if (settingData?.wallet_module == "1") 1 else 0
                if (activity is UserTracking) {
                    (activity as UserTracking).cancelOrder(orderDetail?.order_id.toString(), cancelWallet)
                }
            }
            dialog.dismiss()
        }

        sweetAlertDialog.setNegativeButton(getString(R.string.cancel)) { dialog, which ->
            dialog.dismiss()
        }
        sweetAlertDialog.show()
    }

    private fun convertDate(dateToConvert: String, orderStatus: Double?, currentStatus: Double): String {

        return appUtil.convertDateOneToAnother(replaceInvalid(dateToConvert).replace("T", " ").replace("+00:00", ""),
                "yyyy-MM-dd HH:mm:ss", "EEE, dd\nhh:mm aa") ?: ""
    }

    private fun convertDateNew(dateToConvert: String): String {
        return appUtil.convertDateOneToAnother(replaceInvalid(dateToConvert).replace("T", " ").replace("+00:00", ""),
                "yyyy-MM-dd HH:mm:ss", "MMM dd EEE hh:mm aa") ?: ""
    }

    private fun checkDeliverDate(orderDetail: OrderHistory): String {
        return if (orderDetail.status != OrderStatus.Delivered.orderStatus) "" else orderDetail.delivered_on
                ?: ""
    }

    private fun replaceInvalid(dateToConvert: String): String {
        return if (dateToConvert.contains("Invalid date")) {
            dateToConvert.replace("Invalid date", "")
        } else {
            dateToConvert
        }
    }

    private fun callPhone(number: String) {

        val intent = Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", number, null))
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED) {
            Manifest.permission.CALL_PHONE
            startActivity(intent)
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(arrayOf(Manifest.permission.CALL_PHONE), 1)
            }
        }
    }

    companion object {
        const val ARG_PARAM1 = "param1"

        @JvmStatic
        fun newInstance(orderDetail: OrderHistory) =
                OrderBottomSheetFragment().apply {
                    arguments = Bundle().apply {
                        putParcelable(ARG_PARAM1, orderDetail)
                    }
                }
    }

    override val layoutResourceId: Int
        get() = R.layout.fragment_order_bottom_sheet

    override fun onPermissionsGranted(requestCode: Int, perms: MutableList<String>) {
        TODO("Not yet implemented")
    }

    override fun onPermissionsDenied(requestCode: Int, perms: MutableList<String>) {
        TODO("Not yet implemented")
    }


}