package com.codebrew.clikat.app_utils

import android.content.Context
import android.widget.Toast
import com.codebrew.clikat.R
import com.codebrew.clikat.data.AppDataType
import com.codebrew.clikat.data.constants.AppConstants
import com.codebrew.clikat.data.preferences.PreferenceHelper
import com.codebrew.clikat.modal.CartInfo
import com.codebrew.clikat.modal.other.ProductDataBean
import com.codebrew.clikat.modal.other.SettingModel
import com.codebrew.clikat.preferences.DataNames
import com.codebrew.clikat.utils.StaticFunction
import java.text.DecimalFormat
import javax.inject.Inject

class CartUtils @Inject constructor(private val mContext: Context) {

    @Inject
    lateinit var mPreferenceHelper: PreferenceHelper

    @Inject
    lateinit var mDialogsUtil: DialogsUtil

    @Inject
    lateinit var appUtils: AppUtils


    var screenFlowBean: SettingModel.DataBean.ScreenFlowBean? = null
    var bookingFlowBean: SettingModel.DataBean.BookingFlowBean? = null
    var settingsData: SettingModel.DataBean.SettingData? = null
    private val decimalFormat: DecimalFormat = DecimalFormat("0.00")

    fun addItemToCart(mProduct: CartInfo?, quantityFromEditText: Float? = null): CartInfo? {
        if (screenFlowBean == null) {
            screenFlowBean = mPreferenceHelper.getGsonValue(DataNames.SCREEN_FLOW, SettingModel.DataBean.ScreenFlowBean::class.java)
        }

        if (bookingFlowBean == null) {
            bookingFlowBean = mPreferenceHelper.getGsonValue(DataNames.BOOKING_FLOW, SettingModel.DataBean.BookingFlowBean::class.java)
        }

        if (settingsData == null)
            settingsData = mPreferenceHelper.getGsonValue(DataNames.SETTING_DATA, SettingModel.DataBean.SettingData::class.java)

        var quantity: Float?
        if (quantityFromEditText == null) {
            quantity = mProduct?.quantity ?: 0f
            if (mProduct?.appType == AppDataType.Food.type && settingsData?.is_decimal_quantity_allowed == "1")
                quantity = decimalFormat.format((quantity + AppConstants.DECIMAL_INTERVAL)).toFloat()
            else
                quantity++
        } else
            quantity = quantityFromEditText

        if (mProduct?.priceType == 1) {

            var minHour: Int
            var maxHour = 0

            val totalDuration = mProduct.serviceDuration.times(quantity)

            mProduct.hourlyPrice.forEachIndexed { index, hourlyPrice ->

                minHour = hourlyPrice.min_hour ?: 0
                maxHour = hourlyPrice.max_hour ?: 0

                if ((totalDuration.toInt()) in minHour.rangeTo(maxHour)) {
                    mProduct.price = hourlyPrice.price_per_hour ?: 0f
                }
            }

            mProduct.add_ons?.map { it?.quantity == quantity }

            if (totalDuration < maxHour) {
                mProduct.quantity = quantity
                mProduct.serviceDurationSum = totalDuration
                StaticFunction.updateCart(mContext, mProduct.productId, quantity, mProduct.price)
            } else {
                Toast.makeText(mContext, "Max Limit Reached", Toast.LENGTH_SHORT).show()
                return null
            }

        } else {

            // fixed price 1 for product 0 for service

            if (mProduct?.serviceType == 0) {
                mProduct.serviceDurationSum = bookingFlowBean?.interval?.times(quantity) ?: 0f

                mProduct.quantity = quantity

                updateCart(mProduct)
            } else {

                val actualQuantity = if (mProduct?.productAddonId ?: 0 > 0) {
                    (appUtils.getCartList().cartInfos?.filter { it.productId == mProduct?.productId }?.sumByDouble { it.quantity.toDouble() }?.plus(1)
                            ?: 0.0).toFloat()
                } else {
                    quantity
                }

                val remaingProd = mProduct?.prodQuant?.minus(mProduct.purchasedQuant ?: 0f) ?: 0f

                val checkPurchaseLimit=if(settingsData?.enable_item_purchase_limit=="1" &&
                        mProduct?.purchase_limit!=null && mProduct.purchase_limit!=0f) {
                    quantity <= mProduct.purchase_limit?:0f
                }
                else true
                if (actualQuantity <= remaingProd && checkPurchaseLimit) {
                    mProduct?.quantity = quantity
                    updateCart(mProduct)
                } else {
                    Toast.makeText(mContext, R.string.maximum_limit_cart, Toast.LENGTH_SHORT).show()
                    return null
                }
            }
        }


        return mProduct
    }


    fun removeItemToCart(mProduct: CartInfo?): CartInfo {

        if (bookingFlowBean == null) {
            bookingFlowBean = mPreferenceHelper.getGsonValue(DataNames.BOOKING_FLOW, SettingModel.DataBean.BookingFlowBean::class.java)
        }

        if (settingsData == null)
            settingsData = mPreferenceHelper.getGsonValue(DataNames.SETTING_DATA, SettingModel.DataBean.SettingData::class.java)

        if (mProduct?.quantity != 0f) {
            var quantity = mProduct?.quantity ?: 0f

            if (quantity > 0f) {
                if (mProduct?.appType == AppDataType.Food.type && settingsData?.is_decimal_quantity_allowed == "1") {
                    quantity = if (quantity > AppConstants.DECIMAL_INTERVAL)
                        decimalFormat.format((quantity - AppConstants.DECIMAL_INTERVAL)).toFloat()
                    else
                        quantity
                } else
                    quantity--

                if (mProduct?.priceType == 1) {

                    var minHour: Int
                    var maxHour: Int
                    val totalDuration = mProduct.serviceDuration.times(quantity)

                    mProduct.hourlyPrice.forEachIndexed { index, hourlyPrice ->

                        minHour = hourlyPrice.min_hour ?: 0
                        maxHour = hourlyPrice.max_hour ?: 0

                        if (totalDuration.toInt() in minHour.rangeTo(maxHour)) {
                            mProduct.price = hourlyPrice.price_per_hour ?: 0f
                        }
                    }

                    mProduct.add_ons?.map { it?.quantity == quantity }

                    mProduct.serviceDurationSum = totalDuration

                } else {
                    if (mProduct?.serviceType == 0) {
                        mProduct.serviceDurationSum = bookingFlowBean?.interval?.times(quantity)
                                ?: 0f
                    }
                }

                mProduct?.quantity = quantity


                if (quantity == 0f) {
                    StaticFunction.removeFromCart(mContext, mProduct?.productId, 0)
                } else {
                    updateCart(mProduct)
                }

            }
        }

        return mProduct!!
    }

    private fun updateCart(mProduct: CartInfo?) {
        if (mProduct?.add_ons?.isNotEmpty() == true) {
            val mProductDataBean = ProductDataBean(productAddonId = mProduct.productAddonId, fixed_quantity = mProduct.quantity)
            appUtils.updateItem(mProductDataBean)
        } else {
            StaticFunction.updateCart(mContext, mProduct?.productId, mProduct?.quantity, mProduct?.price
                    ?: 0f)
        }
    }

}