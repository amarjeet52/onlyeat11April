package com.codebrew.clikat.app_utils

import android.content.Context
import com.codebrew.clikat.data.AppDataType
import com.codebrew.clikat.data.OrderPayment
import com.codebrew.clikat.data.OrderStatus
import com.codebrew.clikat.data.RequestsStatus
import com.codebrew.clikat.data.model.api.orderDetail.OrderHistory
import com.codebrew.clikat.data.model.others.OrderStatusModel
import com.codebrew.clikat.data.preferences.PreferenceHelper
import com.codebrew.clikat.preferences.DataNames
import javax.inject.Inject

class OrderUtils @Inject constructor(private val mContext: Context) {

    @Inject
    lateinit var mPreferenceHelper: PreferenceHelper

    @Inject
    lateinit var appUtil: AppUtils


    fun checkPaymtFlow(orderHistory: OrderHistory): Int {


        with(orderHistory)
        {


            ((payment_status == 0 && created_by ?: 0 > 0 && status == RequestsStatus.Approved.status) ||
                    (remaining_amount ?: 0.0f > 0.0f && payment_type == 1 && is_edit == 1) ||
                    (payment_status == 0 && payment_type == DataNames.PAYMENT_AFTER_CONFIRM && payment_after_confirmation == 1 && status == RequestsStatus.Approved.status))


            if (payment_status == 0 && payment_type == DataNames.PAYMENT_AFTER_CONFIRM && payment_after_confirmation == 1 && status == RequestsStatus.Approved.status) {
                return OrderPayment.PaymentAfterConfirm.payment

            }


            if (payment_status == 0 && created_by ?: 0 > 0 && status == RequestsStatus.Approved.status) {
                return OrderPayment.ReceiptOrder.payment
            }

            if (remaining_amount ?: 0.0f > 0.0f && payment_type == 1 && is_edit == 1) {
                return OrderPayment.EditOrder.payment
            }

        }

        return 0
    }


    fun checkOrderListFlow(orderHistory: OrderHistory): Boolean {

        with(orderHistory)
        {

            return  ((payment_status == 0 && created_by ?: 0 > 0 && status == RequestsStatus.Approved.status) ||
                    (remaining_amount ?: 0.0f > 0.0f && payment_type == 1 && is_edit == 1) ||
                    (payment_status == 0 && payment_type == DataNames.PAYMENT_AFTER_CONFIRM && payment_after_confirmation == 1 && status == RequestsStatus.Approved.status))
        }
    }

    fun getAllStatus(orderData: OrderHistory?):MutableList<OrderStatusModel> {

        val mStatusList = mutableListOf<OrderStatusModel>()

        when (orderData?.type) {
            AppDataType.Food.type -> {
                mStatusList.add(OrderStatusModel(convertDate(orderData.confirmed_on
                        ?: "", orderData.status, OrderStatus.Approved.orderStatus), OrderStatus.Approved, orderData.status
                        ?: 0.0))
                mStatusList.add(OrderStatusModel(convertDate(orderData.progress_on
                        ?: "", orderData.status, OrderStatus.In_Kitchen.orderStatus), OrderStatus.In_Kitchen, orderData.status
                        ?: 0.0))
                if (orderData.self_pickup == 0) {
                    mStatusList.add(OrderStatusModel(convertDate(orderData.shipped_on
                            ?: "", orderData.status, OrderStatus.On_The_Way.orderStatus), OrderStatus.On_The_Way, orderData.status
                            ?: 0.0))
                } else {
                    mStatusList.add(OrderStatusModel(convertDate(orderData.shipped_on
                            ?: "", orderData.status, OrderStatus.Ready_to_be_picked.orderStatus), OrderStatus.Ready_to_be_picked, orderData.status
                            ?: 0.0))
                }
                mStatusList.add(OrderStatusModel(convertDate(checkDeliverDate(orderData), orderData.status, OrderStatus.Delivered.orderStatus), OrderStatus.Delivered, orderData.status
                        ?: 0.0))
            }

            AppDataType.Ecom.type -> {
                mStatusList.add(OrderStatusModel(convertDate(orderData.confirmed_on
                        ?: "", orderData.status, OrderStatus.Confirmed.orderStatus), OrderStatus.Confirmed, orderData.status
                        ?: 0.0))
                mStatusList.add(OrderStatusModel(convertDate(orderData.progress_on
                        ?: "", orderData.status, OrderStatus.Packed.orderStatus), OrderStatus.Packed, orderData.status
                        ?: 0.0))
                mStatusList.add(OrderStatusModel(convertDate(orderData.near_on
                        ?: "", orderData.status, OrderStatus.Shipped.orderStatus), OrderStatus.Shipped, orderData.status
                        ?: 0.0))
                mStatusList.add(OrderStatusModel(convertDate(orderData.shipped_on
                        ?: "", orderData.status, OrderStatus.On_The_Way.orderStatus), OrderStatus.On_The_Way, orderData.status
                        ?: 0.0))
                mStatusList.add(OrderStatusModel(convertDate(checkDeliverDate(orderData), orderData.status, OrderStatus.Delivered.orderStatus), OrderStatus.Delivered, orderData.status
                        ?: 0.0))
            }

            AppDataType.HomeServ.type -> {
                mStatusList.add(OrderStatusModel(convertDate(orderData.confirmed_on
                        ?: "", orderData.status, OrderStatus.Confirmed.orderStatus), OrderStatus.Confirmed, orderData.status
                        ?: 0.0))

                mStatusList.add(OrderStatusModel(convertDate(orderData.progress_on
                        ?: "", orderData.status, OrderStatus.In_Kitchen.orderStatus), OrderStatus.In_Kitchen, orderData.status
                        ?: 0.0))

                mStatusList.add(OrderStatusModel(convertDate(orderData.near_on
                        ?: "", orderData.status, OrderStatus.Reached.orderStatus), OrderStatus.Reached, orderData.status
                        ?: 0.0))

                mStatusList.add(OrderStatusModel(convertDate(orderData.shipped_on
                        ?: "", orderData.status, OrderStatus.Started.orderStatus), OrderStatus.Started, orderData.status
                        ?: 0.0))

                mStatusList.add(OrderStatusModel(convertDate(checkDeliverDate(orderData), orderData.status, OrderStatus.Ended.orderStatus), OrderStatus.Ended, orderData.status
                        ?: 0.0))

            }
        }

        return mStatusList
    }

    private fun convertDate(dateToConvert: String, orderStatus: Double?, currentStatus: Double): String {

        return appUtil.convertDateOneToAnother(dateToConvert,"yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", "EEE, dd\nhh:mm aa") ?: ""
    }

    private fun checkDeliverDate(orderDetail: OrderHistory): String {
        return if (orderDetail.status != OrderStatus.Delivered.orderStatus) "" else orderDetail.delivered_on
                ?: ""
    }


}