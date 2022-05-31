package com.codebrew.clikat.module.pending_orders.adapter

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.Adapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.codebrew.clikat.BuildConfig
import com.codebrew.clikat.R
import com.codebrew.clikat.adapters.ImagesAdapter
import com.codebrew.clikat.app_utils.AppUtils
import com.codebrew.clikat.app_utils.OrderUtils
import com.codebrew.clikat.data.AppDataType
import com.codebrew.clikat.data.FoodAppType
import com.codebrew.clikat.data.OrderStatus
import com.codebrew.clikat.data.constants.AppConstants.Companion.CURRENCY_SYMBOL
import com.codebrew.clikat.data.model.api.Currency
import com.codebrew.clikat.data.model.api.orderDetail.OrderHistory
import com.codebrew.clikat.databinding.ItemOrderBinding
import com.codebrew.clikat.databinding.ItemOrderV2Binding
import com.codebrew.clikat.databinding.ItemSupplierBranchBinding
import com.codebrew.clikat.modal.AppGlobal
import com.codebrew.clikat.modal.other.SettingModel
import com.codebrew.clikat.module.completed_order.adapter.OrderHistoryAdapter
import com.codebrew.clikat.module.rating.RatingFragment
import com.codebrew.clikat.preferences.DataNames
import com.codebrew.clikat.preferences.Prefs
import com.codebrew.clikat.utils.GeneralFunctions
import com.codebrew.clikat.utils.StaticFunction
import com.codebrew.clikat.utils.StaticFunction.cancelOrderWallet
import com.codebrew.clikat.utils.StaticFunction.changeBorderColor
import com.codebrew.clikat.utils.StaticFunction.colorStatusProduct
import com.codebrew.clikat.utils.StaticFunction.statusProduct
import com.codebrew.clikat.utils.StaticFunction.sweetDialogueFailure
import com.codebrew.clikat.utils.Utils
import com.codebrew.clikat.utils.configurations.Configurations
import com.codebrew.clikat.utils.configurations.TextConfig
import com.google.android.material.button.MaterialButton
import java.text.ParseException
import kotlin.collections.ArrayList
import java.util.*

/*
 * Created by cbl80 on 20/4/16.
 */
class UpcomingAdapter(private val mContext: Context?, list1: List<OrderHistory>?,
                      private val settingData: SettingModel.DataBean.SettingData?,
                      private val appUtils: AppUtils? = null,
                      private val orderUtils: OrderUtils? = null,
                      private val screenFlowBean: SettingModel.DataBean.ScreenFlowBean?,
private val selectedCurrency: Currency?) : Adapter<ViewHolder>() {
    private val list: ArrayList<OrderHistory> = list1 as ArrayList<OrderHistory>
    var selectedOrderId = 0
    var positionSelected = 0
    private val tabUnselected = Color.parseColor(Configurations.colors.tabUnSelected)
    private val tabSelected = Color.parseColor(Configurations.colors.tabSelected)
    private val appBackground = Color.parseColor(Configurations.colors.appBackground)
    private var textConfig: TextConfig? = null
    private var mCallback: OrderCallback? = null
    private var appType: Int? = null

    fun settingCallback(mCallback: OrderCallback?) {
        this.mCallback = mCallback
    }

    fun remove() {
        list.removeAt(positionSelected)
        notifyItemRemoved(positionSelected)
        notifyItemRangeChanged(positionSelected, list.size)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return when (viewType) {
            SKIP_THEME_ORDERS -> {
                val binding: ItemSupplierBranchBinding = DataBindingUtil.inflate(LayoutInflater.from(mContext),
                        R.layout.item_supplier_branch, parent, false)
                binding.color = Configurations.colors
                SkipSupplierViewHolder(binding)
            }
            else -> {
                val binding = if (settingData?.show_ecom_v2_theme == "1") {
                    DataBindingUtil.inflate(LayoutInflater.from(mContext), R.layout.item_order_v2, parent, false) as ItemOrderV2Binding
                } else {
                    DataBindingUtil.inflate(LayoutInflater.from(mContext), R.layout.item_order, parent, false) as ItemOrderBinding
                }
                when (binding) {
                    is ItemOrderV2Binding -> {
                        binding.color = Configurations.colors
                    }

                    is ItemOrderBinding -> {
                        binding.color = Configurations.colors
                    }
                }
                View_holder(binding.root)
            }
        }

    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val orderHistory = list[position]
        textConfig = appUtils?.loadAppConfig(orderHistory.type ?: 0)?.strings

        if (orderHistory.type ?: 0 == 0) {
            appType = screenFlowBean?.app_type
        }

        if (orderHistory.status == 11.0) {
            orderHistory.status = OrderStatus.In_Kitchen.orderStatus
        }
        if (orderHistory.status == 10.0) {
            orderHistory.status = OrderStatus.Shipped.orderStatus
        }
        val cancelOrderString = mContext?.resources?.getString(R.string.cancel_order, textConfig?.order)
        val cancelHidden = (listOf(OrderStatus.Rejected.orderStatus, OrderStatus.Rating_Given.orderStatus,
                OrderStatus.Customer_Canceled.orderStatus, OrderStatus.Delivered.orderStatus).contains(list[position].status))
                && settingData?.disable_order_cancel ?: "0" == "0"
                && (list[position].status != OrderStatus.Pending.orderStatus || settingData?.disbale_user_cancel_pending_order ?: "0" == "0")

        val paymentFlow = orderUtils?.checkOrderListFlow(orderHistory)
        val status = if (paymentFlow == true) {
            "Payment Pending"
        } else {
            statusProduct(list[position].status, appType ?: 0, orderHistory.self_pickup
                    ?: 0, mContext, orderHistory.terminology ?: "")
        }

        if (holder is View_holder) {
            if (settingData?.show_ecom_v2_theme == "1") {
                holder.cvContainer.cardElevation = 0f
                holder.cvContainer.setCardBackgroundColor(ContextCompat.getColor(holder.itemView.context, R.color.lightGrey))
            }

            holder.tvOrderNo.text = mContext?.getString(R.string.order_no, textConfig?.order) + "\n" + orderHistory.order_id
            holder.rvImages.adapter = ImagesAdapter(mContext, list[position].product)

            val s = (mContext?.getString(R.string.currency_tag, CURRENCY_SYMBOL, Utils.getPriceFormat(orderHistory.net_amount
                    ?: 0.0f, settingData,selectedCurrency))
                    + " / " + orderHistory.product_count + " " + mContext?.getString(R.string.items))

/*
        val s = (mContext?.getString(R.string.currency_tag, CURRENCY_SYMBOL, orderHistory.net_amount - orderHistory.discountAmount)
*/
            holder.tvPrice.text = s

            var dd = orderHistory.created_on?.replace("T", " ")
            dd = dd?.replace("Z", "")
            var deliveryDate = SpannableString.valueOf("")
            try {
                deliveryDate = setColor(mContext?.getString(R.string.placed_on) + "\n"
                        + GeneralFunctions.getFormattedDateSide(dd))
            } catch (e: ParseException) {
                e.printStackTrace()
            }
            holder.tv_restaurant.text= list[position]?.product!![0]?.supplier_name
            holder.tv_rate_order.visibility=View.GONE
            holder.tvPlaced.text = deliveryDate
            val order = setColor(mContext?.getString(R.string.order_no, textConfig?.order) + "\n" + orderHistory.order_id)
            holder.tvOrderNo.text = order
            if(orderHistory?.is_schedule?.toString().equals("1"))
            {
                dd = orderHistory.schedule_end_date?.replace("T", " ")
            }else{
                dd = orderHistory.service_date?.replace("T", " ")
            }

            dd = dd?.replace("Z", "")
            deliveryDate = SpannableString.valueOf("")
            try {

                // orderHistory.sel

                val deliverMsg = if (appType == AppDataType.HomeServ.type) {
                    mContext?.getString(R.string.expected_delivered_on, mContext.getString(R.string.service))
                } else if (orderHistory.self_pickup == FoodAppType.Pickup.foodType) {
                    mContext?.getString(R.string.expected_delivered_on, mContext.getString(R.string.pickkup))
                } else {
                    mContext?.getString(R.string.expected_delivered_on, textConfig?.delivery_tab)
                }

                deliveryDate = setColor(deliverMsg + "\n"
                        + GeneralFunctions.getFormattedDateSide(dd))
            } catch (e: ParseException) {
                e.printStackTrace()
            }

            if (appType == AppDataType.HomeServ.type || appType == AppDataType.Beauty.type || BuildConfig.CLIENT_CODE == "bodyformula_0497") {
                holder.tvDeliveryDate.visibility = View.GONE
            } else {
                holder.tvDeliveryDate.visibility = View.VISIBLE
                holder.tvDeliveryDate.text = deliveryDate
//                holder.tvDeliveryDate.text = "${mContext?.getString(R.string.expected_delivered_time)} \n${Utils.convertMinute(list[position].delivery_min_time?.toLong())} - ${Utils.convertMinute(list[position].delivery_min_time?.toLong())}"
            }


            holder.tvStatus.text = status

            colorStatusProduct(holder.tvStatus, list[position].status, mContext, false)


            holder.tvOrder.text = cancelOrderString
            //  holder.tvOrder.background = changeBorderColor(Configurations.colors.tabSelected?:"", "", GradientDrawable.RECTANGLE)
            //holder.tvOrder.setTextColor(appBackground)

            val cancelHidden = (listOf(OrderStatus.Rejected.orderStatus, OrderStatus.Rating_Given.orderStatus,
                    OrderStatus.Customer_Canceled.orderStatus, OrderStatus.Delivered.orderStatus).contains(list[position].status))

                    && (list[position].status != OrderStatus.Pending.orderStatus)
                    || ( list[position].status != OrderStatus.Pending.orderStatus)

            holder.tvOrder.visibility = if (!cancelHidden) {
                View.VISIBLE
            } else {
                View.GONE
            }

            //        list.get(position).gets
        } else if (holder is SkipSupplierViewHolder) {
            holder.tvViewMenu.text = cancelOrderString
            holder.tvViewMenu.visibility = if (!cancelHidden) {
                View.VISIBLE
            } else {
                View.GONE
            }
            holder.tvStatus.text = status
            holder.updateSupplierData(orderHistory)

        }
    }

    private fun setColor(string: String): SpannableString {
        val newString = SpannableString(string)
        val index = string.indexOf("\n") + 1
        newString.setSpan(ForegroundColorSpan(tabUnselected), index, string.length,
                0)
        return newString
    }

    override fun getItemCount(): Int {
        return list.size
    }

    inner class View_holder(itemView: View) : ViewHolder(itemView) {
        internal var rvImages: RecyclerView = itemView.findViewById(R.id.rvImages)
        internal var tvStatus: TextView = itemView.findViewById(R.id.tvStatus)
        internal var tvHelp: TextView = itemView.findViewById(R.id.tvHelp)
        internal var tvPrice: TextView = itemView.findViewById(R.id.tv_total_prod)
        internal var tvPlaced: TextView = itemView.findViewById(R.id.tvPlaced)
        internal var tvOrderNo: TextView = itemView.findViewById(R.id.tvOrderNo)
        internal var tvDeliveryDate: TextView = itemView.findViewById(R.id.tvDeliveryDate)
        internal var tvOrder: MaterialButton = itemView.findViewById(R.id.tvOrder)
        internal var tv_rate_order: TextView = itemView.findViewById(R.id.tv_rate_order)
        internal var cvContainer: CardView = itemView.findViewById(R.id.cvContainer)
        internal var mainLayout: LinearLayout = itemView.findViewById(R.id.mainLayout)
        internal var tv_restaurant:TextView = itemView.findViewById(R.id.tv_restaurant)

        init {

            rvImages.layoutManager = LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false)
            tvStatus.typeface = AppGlobal.semi_bold
            tvPrice.typeface = AppGlobal.semi_bold
            tvPlaced.typeface = AppGlobal.regular
            tvOrderNo.typeface = AppGlobal.regular
            tvDeliveryDate.typeface = AppGlobal.regular
            tvOrder.typeface = AppGlobal.semi_bold
            tvOrder.background = changeBorderColor(Configurations.colors.tabSelected
                    ?: "", "", GradientDrawable.RECTANGLE)
            tvOrder.setTextColor(appBackground)
            tvOrder.setOnClickListener { v: View? ->
                // CASE Confirm order
                if (list[adapterPosition].status == OrderStatus.Scheduled.orderStatus) {
                    Prefs.with(mContext).save(DataNames.ORDER_DETAIL, list[adapterPosition])
                    //  Select Payment Gateway
// Show Order Summary Screen
/*     if (list.get(getAdapterPosition()).getProduct().get(0).getOrder() == 13) {
                        Prefs.with(mContext).save(DataNames.FLOW_STROE, DataNames.PACKAGES);
                    } else {
                        Prefs.with(mContext).save(DataNames.FLOW_STROE, DataNames.GROCERY);
                    }*/Prefs.with(mContext).save(DataNames.SUPPLIER_LOGO_BRANCH_ID, "" + list[adapterPosition].supplier_branch_id)
                    Prefs.with(mContext).save(DataNames.SUPPLIER_LOGO_ID, "" + list[adapterPosition].supplier_id)
                    Prefs.with(mContext).getString(DataNames.SUPPLIER_LOGO, "" + list[adapterPosition].logo)
                    Prefs.with(mContext).save(DataNames.CATEGORY_ID, "" + (list[adapterPosition].product?.get(0)?.category_id
                            ?: 0))
                    /*             val paymentMethod = PaymentMethod()
                                 val bundle = Bundle()
                                 bundle.putBoolean("confirmOrder", true)
                                 paymentMethod.arguments = bundle
                                 (mContext as MainActivity).pushFragments(DataNames.TAB1, paymentMethod
                                         ,
                                         true, true, "", true)*/
                } else {
                    selectedOrderId = list[adapterPosition].order_id ?: 0
                    positionSelected = adapterPosition
                    if (settingData?.wallet_module == "1" && list[adapterPosition].payment_type == DataNames.DELIVERY_CARD) {
                        mContext?.let { cancelOrderWallet(it, textConfig?.order ?: "") }
                    } else {
                        sweetDialogueFailure(mContext,
                                mContext?.getString(R.string.cancel_order, textConfig?.order) ?: "",
                                mContext?.getString(R.string.doYouCancel, textConfig?.order)
                                        ?: "", true, 101, settingData?.wallet_module ?: "")
                    }
                }
            }
            mainLayout.setOnClickListener { mCallback?.onOrderDetail(list[adapterPosition]) }
            rvImages.setOnClickListener { mCallback?.onOrderDetail(list[adapterPosition]) }
            tvHelp.setOnClickListener { mCallback?.onHelpClick(list[adapterPosition]) }

        }
    }

    inner class SkipSupplierViewHolder(private val binding: ItemSupplierBranchBinding) : RecyclerView.ViewHolder(binding.root) {
        internal var tvStatus: TextView = binding.tvRating
        internal var tvViewMenu: TextView = binding.tvViewMenu

        init {
            tvViewMenu.setOnClickListener {
                // CASE Confirm order
                if (list[adapterPosition].status == OrderStatus.Scheduled.orderStatus) {
                    Prefs.with(mContext).save(DataNames.ORDER_DETAIL, list[adapterPosition])
                    //  Select Payment Gateway
// Show Order Summary Screen
/*     if (list.get(getAdapterPosition()).getProduct().get(0).getOrder() == 13) {
                        Prefs.with(mContext).save(DataNames.FLOW_STROE, DataNames.PACKAGES);
                    } else {
                        Prefs.with(mContext).save(DataNames.FLOW_STROE, DataNames.GROCERY);
                    }*/Prefs.with(mContext).save(DataNames.SUPPLIER_LOGO_BRANCH_ID, "" + list[adapterPosition].supplier_branch_id)
                    Prefs.with(mContext).save(DataNames.SUPPLIER_LOGO_ID, "" + list[adapterPosition].supplier_id)
                    Prefs.with(mContext).getString(DataNames.SUPPLIER_LOGO, "" + list[adapterPosition].logo)
                    Prefs.with(mContext).save(DataNames.CATEGORY_ID, "" + (list[adapterPosition].product?.get(0)?.category_id
                            ?: 0))
                    /*             val paymentMethod = PaymentMethod()
                                 val bundle = Bundle()
                                 bundle.putBoolean("confirmOrder", true)
                                 paymentMethod.arguments = bundle
                                 (mContext as MainActivity).pushFragments(DataNames.TAB1, paymentMethod
                                         ,
                                         true, true, "", true)*/
                } else {
                    selectedOrderId = list[adapterPosition].order_id ?: 0
                    positionSelected = adapterPosition
                    if (settingData?.wallet_module == "1" && list[adapterPosition].payment_type == DataNames.DELIVERY_CARD) {
                        mContext?.let { cancelOrderWallet(it, textConfig?.order ?: "") }
                    } else {
                        sweetDialogueFailure(mContext,
                                mContext?.getString(R.string.cancel_order, textConfig?.order) ?: "",
                                mContext?.getString(R.string.doYouCancel, textConfig?.order)
                                        ?: "", true, 101, settingData?.wallet_module ?: "")
                    }
                }
            }

            itemView.setOnClickListener { mCallback?.onOrderDetail(list[adapterPosition]) }
        }

        fun updateSupplierData(data: OrderHistory?) {

            binding.tvName.text = data?.product?.firstOrNull()?.name ?: ""


            binding.tvDistance.text = itemView.context?.getString(R.string.id, data?.order_id ?: "")

            binding.tvRating.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.ic_clock, 0, 0, 0)
            binding.tvDistance.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.ic_pin, 0, 0, 0)

            var dd = data?.created_on?.replace("T", " ")
            dd = dd?.replace("Z", "")
            var deliveryDate = SpannableString.valueOf("")
            try {
                deliveryDate = setColor(GeneralFunctions.getFormattedDateSkip(dd))
            } catch (e: ParseException) {
                e.printStackTrace()
            }
            binding.tvTime.text = deliveryDate

            StaticFunction.loadImage(data?.product?.firstOrNull()?.image_path.toString(), binding.ivImage, false)
        }
    }

    companion object {
        const val SKIP_THEME_ORDERS = 1
        const val ORDERS_LIST = 2
    }

    override fun getItemViewType(position: Int): Int {
        return if (settingData?.is_skip_theme == "1") OrderHistoryAdapter.SKIP_THEME_ORDERS else OrderHistoryAdapter.ORDERS_LIST
    }

    interface OrderCallback {
        fun onOrderDetail(historyBean: OrderHistory?)
        fun onHelpClick(historyBean: OrderHistory?){}
    }

}