package com.codebrew.clikat.module.completed_order.adapter

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.Adapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.codebrew.clikat.R
import com.codebrew.clikat.adapters.ImagesAdapter
import com.codebrew.clikat.app_utils.AppUtils
import com.codebrew.clikat.data.AppDataType
import com.codebrew.clikat.data.OrderStatus
import com.codebrew.clikat.data.constants.AppConstants
import com.codebrew.clikat.data.constants.AppConstants.Companion.CURRENCY_SYMBOL
import com.codebrew.clikat.data.constants.PrefenceConstants
import com.codebrew.clikat.data.model.api.Currency
import com.codebrew.clikat.data.model.api.orderDetail.OrderHistory
import com.codebrew.clikat.databinding.*
import com.codebrew.clikat.modal.AppGlobal
import com.codebrew.clikat.modal.PojoSignUp
import com.codebrew.clikat.modal.other.SettingModel
import com.codebrew.clikat.module.rating.RatingFragment
import com.codebrew.clikat.preferences.DataNames
import com.codebrew.clikat.preferences.Prefs
import com.codebrew.clikat.utils.GeneralFunctions
import com.codebrew.clikat.utils.StaticFunction
import com.codebrew.clikat.utils.StaticFunction.colorStatusProduct
import com.codebrew.clikat.utils.StaticFunction.isInternetConnected
import com.codebrew.clikat.utils.StaticFunction.showNoInternetDialog
import com.codebrew.clikat.utils.StaticFunction.statusProduct
import com.codebrew.clikat.utils.Utils
import com.codebrew.clikat.utils.configurations.Configurations
import com.codebrew.clikat.utils.configurations.TextConfig
import com.makeramen.roundedimageview.RoundedImageView
import java.text.ParseException

/*
 * Created by cbl80 on 20/4/16.
 */
class OrderHistoryAdapter(private val mContext: Context, private val list: MutableList<OrderHistory>,
                          private val appUtils: AppUtils, private val clientInform: SettingModel.DataBean.SettingData?,
                          private val screenFlowBean: SettingModel.DataBean.ScreenFlowBean?,
                          private val selectedCurrency:Currency?) : Adapter<ViewHolder>() {

    private var mCallback: OrderHisCallback? = null
    private val listTextHead = Color.parseColor(Configurations.colors.textListHead)
    private var textConfig: TextConfig? = null
    private var mAppType: Int? = null
    private var isFromHomeOrders: Boolean = false
    fun settingCallback(mCallback: OrderHisCallback?) {
        this.mCallback = mCallback
 }

    fun setViewTypeUi(isFromHomeOrders: Boolean) {
        this.isFromHomeOrders = isFromHomeOrders
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return when (viewType) {
            HOME_RECENT_ORDERS -> {
                val binding: ItemOrderHistoryBinding = DataBindingUtil.inflate(LayoutInflater.from(mContext),
                        R.layout.item_order_history, parent, false)
                binding.color = Configurations.colors
                binding.settingData=clientInform
                SupplierViewHolder(binding)
            }
            SKIP_THEME_ORDERS -> {
                val binding: ItemSupplierBranchBinding = DataBindingUtil.inflate(LayoutInflater.from(mContext),
                        R.layout.item_supplier_branch, parent, false)
                binding.color = Configurations.colors
                SkipSupplierViewHolder(binding)
            }
            else -> {
                val binding: ItemOrderBinding = DataBindingUtil.inflate(LayoutInflater.from(mContext),
                        R.layout.item_order, parent, false)
                binding.color = Configurations.colors
                View_holder(binding.root)
            }
        }

    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val orderHistory = list[position]
        val conversionRate=Utils.getConversionRate(clientInform,selectedCurrency)
        if (orderHistory.type ?: 0 == 0) {
            mAppType = screenFlowBean?.app_type
        }

        textConfig = appUtils.loadAppConfig(orderHistory.type ?: 0).strings

        if (holder is View_holder) {

            holder.tv_rate_order.setOnClickListener{

                var intent= Intent(mContext, RatingFragment::class.java)
                intent.putExtra("image",list[position].product!!.get(0)!!.image_path.toString())
                intent.putExtra("name",list[position]?.product!![0]?.supplier_name)
                intent.putExtra("orderId",list[position].order_id.toString())
                mContext!!.startActivity(intent)

            }

            holder.rvImages.adapter = ImagesAdapter(mContext, list[position].product)
            val s = (mContext.getString(R.string.currency_tag, CURRENCY_SYMBOL, Utils.getPriceFormat(orderHistory.net_amount
                    ?: 0.0f, clientInform,selectedCurrency))
                    + " / " + orderHistory?.product_count + " " + mContext.getString(R.string.items))
            holder.tvPrice.text = s


            var dd = orderHistory.created_on?.replace("T", " ")
            dd = dd?.replace("Z", "")
            var deliveryDate = SpannableString.valueOf("")
            try {
                deliveryDate = setColor(mContext.getString(R.string.placed_on) + "\n"
                        + GeneralFunctions.getFormattedDateSide(dd))
            } catch (e: ParseException) {
                e.printStackTrace()
            }
            holder.tvPlaced.text = deliveryDate
            val order = setColor(mContext.getString(R.string.order_no, textConfig?.order) + "\n" + orderHistory.order_id)
            holder.tvOrderNo.text = order
            if (orderHistory.delivered_on == null) {
                orderHistory.delivered_on = orderHistory.service_date ?: ""
            }
            dd = orderHistory.delivered_on?.replace("T", " ") ?: ""
            dd = dd.replace("Z", "")
            deliveryDate = SpannableString.valueOf("")
            try {
                deliveryDate = setColor((if (mAppType == AppDataType.HomeServ.type) mContext.getString(R.string.completed_on) else mContext.getString(R.string.delivered_on)) + "\n"
                        + GeneralFunctions.getFormattedDateSide(dd))
            } catch (e: ParseException) {
                e.printStackTrace()
            }
            holder.tvDeliveryDate.text = deliveryDate
            holder.tv_restaurant.text= list[position]?.product!![0]?.supplier_name
            colorStatusProduct(holder.tvStatus,
                    list[position].status, mContext, false)
            colorStatusProduct(holder.tvOrder,
                    list[position].status, mContext, true)
            holder.tvStatus.text = statusProduct(list[position].status, mAppType
                    ?: 0, orderHistory.self_pickup, mContext, orderHistory.terminology ?: "")

            when (list[position].status) {
                OrderStatus.Rejected.orderStatus->
                {
                    holder.tvDeliveryDate.visibility = View.GONE
                    holder.tv_rate_order.visibility = View.GONE
                }
                OrderStatus.Customer_Canceled.orderStatus -> {
                    holder.tvDeliveryDate.visibility = View.GONE
                    holder.tv_rate_order.visibility = View.GONE
                }
                OrderStatus.Admin_Canceled.orderStatus -> {

                    holder.tvDeliveryDate.visibility = View.GONE
                    holder.tv_rate_order.visibility = View.GONE
                }
                OrderStatus.Delivered.orderStatus -> {
                    if(list[position].is_rated==1) {
                        holder.tv_rate_order.visibility = View.GONE
                    }else{
                        holder.tv_rate_order.visibility = View.VISIBLE
                    }
                }
            }

        }else if(holder is SupplierViewHolder){
            holder.updateSupplierData(orderHistory)

            holder.tvReOrder?.setOnClickListener {
                val history2 = list[position]
                if (history2.delivered_on == null) {
                    history2.delivered_on = history2.service_date ?: ""
                }
                Prefs.with(mContext).save(DataNames.ORDER_DETAIL, history2)

                if (AppConstants.APP_SUB_TYPE > AppDataType.Custom.type) {
                    Prefs.with(mContext).save(PrefenceConstants.APP_TERMINOLOGY, history2.terminology)
                }

                val orderId = ArrayList<Int>()
                orderId.add(history2.order_id ?: 0)
                if (list[position].product?.get(0)?.order == 2 ||
                        list[position].product?.get(0)?.order == 13) {
                    mCallback?.reOrder(orderId)
                } else {
                    mCallback?.reOrder(orderId)
                }
            }
        }
        else if (holder is SkipSupplierViewHolder) {
            holder.updateSupplierData(orderHistory)
            holder.itemView.setOnClickListener {
                val history2 = list[position]
                if (history2.delivered_on == null) {
                    history2.delivered_on = history2.service_date ?: ""
                }
                Prefs.with(mContext).save(DataNames.ORDER_DETAIL, history2)

                if (AppConstants.APP_SUB_TYPE > AppDataType.Custom.type) {
                    Prefs.with(mContext).save(PrefenceConstants.APP_TERMINOLOGY, history2.terminology)
                }

                val orderId = ArrayList<Int>()
                orderId.add(history2.order_id ?: 0)
                if (list[position].product?.get(0)?.order == 2 ||
                        list[position].product?.get(0)?.order == 13) {
                    mCallback?.reOrder(orderId)
                } else {
                    mCallback?.reOrder(orderId)
                }
            }
        }
    }

    private fun setColor(string: String): SpannableString {
        val newString = SpannableString(string)
        val index = string.indexOf("\n") + 1
        newString.setSpan(ForegroundColorSpan(listTextHead), index, string.length,
                0)
        return newString
    }

    override fun getItemCount(): Int {
        return list.size
    }

    inner class View_holder(itemView: View) : ViewHolder(itemView) {
        internal var rvImages: RecyclerView
        internal var tvStatus: TextView
        internal var tvPrice: TextView
        internal var tvPlaced: TextView
        internal var tvOrderNo: TextView
        internal var tvDeliveryDate: TextView
        internal var tvHelp: TextView = itemView.findViewById(R.id.tvHelp)
        internal var tvOrder: TextView
        internal var cvContainer: CardView
        internal var tv_rate_order:TextView
        internal var tv_restaurant:TextView

        init {
            rvImages = itemView.findViewById(R.id.rvImages)
            tvStatus = itemView.findViewById(R.id.tvStatus)
            tvPrice = itemView.findViewById(R.id.tv_total_prod)
            tvPlaced = itemView.findViewById(R.id.tvPlaced)
            tv_rate_order = itemView.findViewById(R.id.tv_rate_order)
            tvOrderNo = itemView.findViewById(R.id.tvOrderNo)
            tvDeliveryDate = itemView.findViewById(R.id.tvDeliveryDate)
            tvOrder = itemView.findViewById(R.id.tvOrder)
            tv_restaurant = itemView.findViewById(R.id.tv_restaurant)
            cvContainer = itemView.findViewById(R.id.cvContainer)
            rvImages.layoutManager = LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false)
            tvStatus.typeface = AppGlobal.semi_bold
            tvPrice.typeface = AppGlobal.semi_bold
            tvPlaced.typeface = AppGlobal.regular
            tvOrderNo.typeface = AppGlobal.regular
            tvDeliveryDate.typeface = AppGlobal.regular
            tvOrder.typeface = AppGlobal.semi_bold
            cvContainer.setOnClickListener { v: View? ->
                val history2 = list[adapterPosition]
                if (history2.delivered_on == null) {
                    history2.delivered_on = history2.service_date ?: ""
                }
                Prefs.with(mContext).save(DataNames.ORDER_DETAIL, history2)

                if (AppConstants.APP_SUB_TYPE > AppDataType.Custom.type) {
                    Prefs.with(mContext).save(PrefenceConstants.APP_TERMINOLOGY, history2.terminology)
                }

                val orderId = ArrayList<Int>()
                orderId.add(history2.order_id ?: 0)
                if (list[adapterPosition].product?.get(0)?.order == 2 ||
                        list[adapterPosition].product?.get(0)?.order == 13) {
                    mCallback!!.reOrder(orderId)
                } else {
                    mCallback!!.reOrder(orderId)
                }
            }
            tvHelp.setOnClickListener { mCallback?.onHelpClick(list[adapterPosition]) }

            tvOrder.setOnClickListener { v: View? ->
                if (isInternetConnected(mContext)) {
                    val dataLogin = Prefs.with(mContext).getObject(DataNames.USER_DATA, PojoSignUp::class.java)
                    Prefs.with(mContext).save(DataNames.CATEGORY_ID, "" + list[adapterPosition].product?.get(0)?.category_id)
                    Prefs.with(mContext).save(DataNames.SUPPLIERBRANCHID, "" + list[adapterPosition]
                            .supplier_branch_id)
                    if (dataLogin?.data != null && dataLogin.data.access_token != null && dataLogin.data?.access_token?.trim()?.isNotEmpty() == true) {

                        mCallback!!.addToCart()
                        // apiAddToCart(list.get(getAdapterPosition()).getSupplier_branch_id(), itemView, (ArrayList<CartInfoServer>) productList);
                    }
                } else {
                    showNoInternetDialog(mContext)
                }
            }
        }
    }

    inner class SkipSupplierViewHolder(private val binding: ItemSupplierBranchBinding) : RecyclerView.ViewHolder(binding.root) {

        fun updateSupplierData(data: OrderHistory?) {

            binding.tvName.text = data?.product?.firstOrNull()?.name ?: ""

            binding.tvDistance.text = itemView.context?.getString(R.string.id, data?.order_id ?: "")
            binding.tvRating.text = statusProduct(data?.status, mAppType
                    ?: 0, data?.self_pickup, mContext, data?.terminology ?: "")
            binding.tvRating.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.ic_clock, 0, 0, 0)
            binding.tvDistance.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.ic_pin, 0, 0, 0)
            var deliveryDate: SpannableString
            if (data?.delivered_on == null) {
                data?.delivered_on = data?.service_date ?: ""
            }
            var dd = data?.delivered_on?.replace("T", " ") ?: ""
            dd = dd.replace("Z", "")
            deliveryDate = SpannableString.valueOf("")
            try {
                deliveryDate = setColor(GeneralFunctions.getFormattedDateSkip(dd))
            } catch (e: ParseException) {
                e.printStackTrace()
            }
            binding.tvTime.text = deliveryDate

            StaticFunction.loadImage(data?.product?.firstOrNull()?.image_path.toString(), binding.ivImage, false)
        }
    }

    inner class SupplierViewHolder(private val binding: ItemOrderHistoryBinding) : RecyclerView.ViewHolder(binding.root) {
        val sdvImage:RoundedImageView?=binding.sdvImage
        val tvReOrder:TextView?=binding.tvReOrder
        fun updateSupplierData(data: OrderHistory?) {

            binding.tvSupplierloc.text = data?.product?.joinToString(" ,"){it?.supplier_name?:""}
            binding.tvName.text = data?.product?.joinToString(" ,"){it?.name?:""}
            var deliveryDate: SpannableString
            if (data?.delivered_on == null) {
                data?.delivered_on = data?.service_date ?: ""
            }
            var dd = data?.delivered_on?.replace("T", " ") ?: ""
            dd = dd.replace("Z", "")
            deliveryDate = SpannableString.valueOf("")
            try {
                deliveryDate = setColor(GeneralFunctions.getFormattedDateSkip(dd))
            } catch (e: ParseException) {
                e.printStackTrace()
            }
            binding.tvDeliveryTime.text = deliveryDate

            StaticFunction.loadImage(data?.product?.firstOrNull()?.image_path.toString(), binding.sdvImage, false)
        }
    }

    interface OrderHisCallback {
        fun addToCart()
        fun reOrder(orderId: ArrayList<Int>?)
        fun onHelpClick(historyBean: OrderHistory?){}
    }

    override fun getItemViewType(position: Int): Int {
        return when {
            isFromHomeOrders -> HOME_RECENT_ORDERS
            clientInform?.is_skip_theme == "1" -> SKIP_THEME_ORDERS
            else -> ORDERS_LIST
        }
    }

    companion object {
        const val SKIP_THEME_ORDERS = 1
        const val ORDERS_LIST = 2
        const val HOME_RECENT_ORDERS = 3
    }

}