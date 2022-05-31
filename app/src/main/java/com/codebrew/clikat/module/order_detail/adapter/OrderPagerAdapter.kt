package com.codebrew.clikat.module.order_detail.adapter

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.constraintlayout.widget.Group
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.codebrew.clikat.BuildConfig
import com.codebrew.clikat.R
import com.codebrew.clikat.app_utils.AppUtils
import com.codebrew.clikat.app_utils.OrderUtils
import com.codebrew.clikat.app_utils.extension.loadUserImage
import com.codebrew.clikat.data.AppDataType
import com.codebrew.clikat.data.FoodAppType
import com.codebrew.clikat.data.OrderStatus
import com.codebrew.clikat.data.constants.AppConstants
import com.codebrew.clikat.data.model.api.Currency
import com.codebrew.clikat.data.model.api.GeofenceData
import com.codebrew.clikat.data.model.api.QuestionList
import com.codebrew.clikat.data.model.api.orderDetail.Agent
import com.codebrew.clikat.data.model.api.orderDetail.OrderHistory
import com.codebrew.clikat.data.model.others.CustomPayModel
import com.codebrew.clikat.data.model.others.EditProductsItem
import com.codebrew.clikat.data.model.others.ImageListModel
import com.codebrew.clikat.data.model.others.OrderStatusModel
import com.codebrew.clikat.databinding.PagerProductItemBinding
import com.codebrew.clikat.modal.other.ProductDataBean
import com.codebrew.clikat.modal.other.SettingModel
import com.codebrew.clikat.module.cart.adapter.ImageListAdapter
import com.codebrew.clikat.module.cart.adapter.ImageListAdapter.UserChatListener
import com.codebrew.clikat.module.cart.adapter.SelectedQuestAdapter
import com.codebrew.clikat.module.user_tracking.UserTracking
import com.codebrew.clikat.preferences.DataNames
import com.codebrew.clikat.utils.StaticFunction
import com.codebrew.clikat.utils.StaticFunction.colorStatusProduct
import com.codebrew.clikat.utils.StaticFunction.loadImage
import com.codebrew.clikat.utils.StaticFunction.statusProduct
import com.codebrew.clikat.utils.Utils
import com.codebrew.clikat.utils.configurations.Configurations
import com.google.android.material.button.MaterialButton
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.android.synthetic.main.item_agent_list.view.*
import kotlinx.android.synthetic.main.pager_product_item.view.*
import java.text.DecimalFormat
import java.text.ParseException

class OrderPagerAdapter(private val mContext: Context, private val orderHistoryBeans: MutableList<OrderHistory>,
                        private val appUtil: AppUtils,
                        private val callBackReturn: OrderDetailProductAdapter.OnReturnClicked,
                        private val orderUtils: OrderUtils,
                        private val decimalFormat: DecimalFormat,
private val selectedCurrency:Currency?) : RecyclerView.Adapter<OrderPagerAdapter.View_holder>() {


    private var bookingFlowBean: SettingModel.DataBean.BookingFlowBean? = null
    private var screenFlowBean: SettingModel.DataBean.ScreenFlowBean? = null

    private var userDta: String? = null

    private var mCallback: OrderListener? = null

    private var appTerminology: SettingModel.DataBean.AppTerminology? = null

    private var settingData: SettingModel.DataBean.SettingData? = null

    private var gson = Gson()

    private var mSelectedPayment: CustomPayModel? = null
    private var mAppType:Int?=null

    private val textConfig by lazy { appUtil.loadAppConfig(0).strings }

    fun setUserId(userDta: String?, bookingFlowBean: SettingModel.DataBean.BookingFlowBean?,
                  screenFlowBean: SettingModel.DataBean.ScreenFlowBean?,

                  settingData: SettingModel.DataBean.SettingData?) {
        this.userDta = userDta
        this.screenFlowBean = screenFlowBean
        this.bookingFlowBean = bookingFlowBean

        this.settingData = settingData
    }

    fun settingTerminology(appTerminology: SettingModel.DataBean.AppTerminology?,mAppType:Int) {
        this.appTerminology = appTerminology
        this.mAppType=mAppType
    }

    fun settingCallback(mCallback: OrderListener) {
        this.mCallback = mCallback
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): View_holder {
        val binding: PagerProductItemBinding = DataBindingUtil.inflate(LayoutInflater.from(mContext), R.layout.pager_product_item, parent, false)
        binding.color = Configurations.colors
        binding.strings = textConfig
        binding.isAgentRating = settingData?.is_agent_rating == "1"
        return View_holder(binding.root)
    }

    override fun getItemCount(): Int {
        return orderHistoryBeans.size
    }

    override fun onBindViewHolder(holder: View_holder, position: Int) {

        holder.gpAction?.visibility = View.GONE
        try {
            initailize(holder, orderHistoryBeans[position], position)
        } catch (e: ParseException) {
            e.printStackTrace()
        }
    }

    fun addRemoveItem(item: ProductDataBean?, adapterPosition: Int,
                      parentPosition: Int, selectedItems: ArrayList<EditProductsItem>?, geoFenceItem: GeofenceData?) {

        var handlingAdmin: Float? = 0f
        var fixedPrice: Float? = 0f

        if (adapterPosition != -1 && parentPosition != -1) {
            orderHistoryBeans[parentPosition].product?.get(adapterPosition)?.quantity = item?.quantity

            orderHistoryBeans[parentPosition].product?.forEach {
                val taxCharges = geoFenceItem?.taxData?.firstOrNull()?.tax?.toFloat()
                        ?: (it?.handling_admin)
                handlingAdmin = handlingAdmin?.plus(
                        (it?.quantity?.toFloat() ?: 0f).times(it?.price?.toFloat()
                                ?: 0f).times(taxCharges?.div(100)
                                ?: 0f)
                )
                fixedPrice = fixedPrice?.plus((it?.quantity?.toFloat()
                        ?: 0f).times(it?.price?.toFloat()
                        ?: 0f))
            }

            selectedItems?.forEach {
                val taxCharges = geoFenceItem?.taxData?.firstOrNull()?.tax?.toFloat()
                        ?: (it.handling_admin?.toFloat())

                handlingAdmin = handlingAdmin?.plus(
                        (it.quantity?.toFloat() ?: 0f).times(it.price
                                ?: 0f).times(taxCharges?.div(100)
                                ?: 0f)
                )
                fixedPrice = fixedPrice?.plus((it.quantity?.toFloat() ?: 0f).times(it.price ?: 0f))
            }


            orderHistoryBeans[parentPosition].total_order_price = fixedPrice
            orderHistoryBeans[parentPosition].net_amount = null

            orderHistoryBeans[parentPosition].handling_admin = handlingAdmin
            notifyItemChanged(parentPosition)
        }
    }


    fun addRemoveItem(selectedItems: ArrayList<EditProductsItem>?, geoFenceItem: GeofenceData?) {
        var handlingAdmin: Float? = 0f
        var fixedPrice: Float? = 0f

        orderHistoryBeans.firstOrNull()?.product?.forEach {
            val taxCharges = geoFenceItem?.taxData?.firstOrNull()?.tax?.toFloat()
                    ?: (it?.handling_admin)

            handlingAdmin = handlingAdmin?.plus(
                    (it?.quantity?.toFloat() ?: 0f).times(it?.price?.toFloat()
                            ?: 0f).times(taxCharges?.div(100)
                            ?: 0f)
            )
            fixedPrice = fixedPrice?.plus((it?.quantity?.toFloat() ?: 0f).times(it?.price?.toFloat()
                    ?: 0f))
        }

        selectedItems?.forEach {
            val taxCharges = geoFenceItem?.taxData?.firstOrNull()?.tax?.toFloat()
                    ?: (it.handling_admin?.toFloat())

            handlingAdmin = handlingAdmin?.plus(
                    (it.quantity?.toFloat() ?: 0f).times(it.price ?: 0f).times(taxCharges?.div(100)
                            ?: 0f)
            )
            fixedPrice = fixedPrice?.plus((it.quantity?.toFloat() ?: 0f).times(it.price ?: 0f))
        }

        orderHistoryBeans.firstOrNull()?.handling_admin = handlingAdmin
        orderHistoryBeans.firstOrNull()?.total_order_price = fixedPrice
        orderHistoryBeans.firstOrNull()?.net_amount = null


        notifyDataSetChanged()
    }

    private fun initailize(mHolder: View_holder, orderDetail: OrderHistory, position: Int) {

        orderDetail.type =mAppType

        val conversionRate=Utils.getConversionRate(settingData,selectedCurrency)
        val mStatusList = mutableListOf<OrderStatusModel>()

        if (orderDetail.agent != null && orderDetail.agent.isNotEmpty()) {
            mHolder.agentLayout?.visibility = View.VISIBLE
            mHolder.tvAgentDetail?.visibility = View.VISIBLE
            val agentBean = orderDetail.agent[0]
            // if (agentBean?.image != null) {
            mHolder.ivUserImage?.loadUserImage(agentBean?.image ?: "")
            //Glide.with(mContext).load(agentBean.getImage()).apply(new RequestOptions().placeholder(R.drawable.placeholder_product)).into(ivUserImage);
            // }
            if (agentBean?.name != null) {
                mHolder.tvName?.text = if(agentBean.assign_id.isNullOrEmpty()) agentBean.name else
                    mContext.getString(R.string.agent_name_with_id,agentBean.name,agentBean.assign_id)
            }
            if (agentBean?.occupation != null) {
                mHolder.tvOccupation?.text = agentBean.occupation
            }

            mHolder.tvTotalReviews?.text = mContext.getString(R.string.agent_reviews_tag, agentBean?.total_review)
            mHolder.rbAgent?.text = agentBean?.avg_rating.toString()

        } else {
            mHolder.agentLayout?.visibility = View.GONE
            mHolder.tvAgentDetail?.visibility = View.GONE
        }

        if (settingData?.extra_instructions == "1" && screenFlowBean?.app_type == AppDataType.HomeServ.type) {
            mHolder.grpExtInst?.visibility = View.VISIBLE
            if (orderDetail.have_pet == 1) {
                mHolder.tvHavePets?.text = mContext.getString(R.string.yes)
            } else {
                mHolder.tvHavePets?.text = mContext.getString(R.string.no)
            }

            if (orderDetail.cleaner_in == 1) {
                mHolder.tvCleanerIn?.text = mContext.getString(R.string.yes)
            } else {
                mHolder.tvCleanerIn?.text = mContext.getString(R.string.no)
            }
        } else {
            mHolder.grpExtInst?.visibility = View.GONE
        }

        mHolder.tvSos?.visibility = if (settingData?.is_sos_enable == "1") View.VISIBLE else View.GONE
        mHolder.tvLiveChat?.visibility = if (BuildConfig.CLIENT_CODE == "zipeats_0417") View.VISIBLE else View.GONE

        /* mHolder.tvHavePets?.text=orderDetail.have_pet.toString()
         mHolder.tvCleanerIn?.text=orderDetail.cleaner_in.toString()*/

        mHolder.tvParkingInstructions?.text = orderDetail.parking_instructions
        mHolder.tvAreaToFoucus?.text = orderDetail.area_to_focus

        mHolder.rvStatus?.layoutManager = LinearLayoutManager(mContext, RecyclerView.HORIZONTAL, false)
        mHolder.rvStatus?.adapter = OrderStatusAdapter(mStatusList, orderDetail.type, orderDetail.self_pickup, orderDetail.terminology
                ?: "")


        loadImage(orderDetail.logo, mHolder.ivSupplierIcon!!, false)
        mHolder.rvProduct?.layoutManager = LinearLayoutManager(mContext)

        mHolder.tvOrderNo?.text = orderDetail.order_id.toString()

        mHolder.tvTax?.text = mContext.getString(R.string.currency_tag, AppConstants.CURRENCY_SYMBOL, Utils.getPriceFormat(orderDetail.handling_admin
                ?: 0.0f, settingData,selectedCurrency))

        mHolder.tvDelivery?.text = mContext.getString(R.string.currency_tag, AppConstants.CURRENCY_SYMBOL, Utils.getPriceFormat(orderDetail.delivery_charges
                ?: 0.0f, settingData,selectedCurrency))

        colorStatusProduct(mHolder.tvStatus1,
                orderDetail.status, mContext, false)

        if (orderDetail.status == 11.0) {
            orderDetail.status = OrderStatus.In_Kitchen.orderStatus
        }

        if (orderDetail.status == 10.0) {
            orderDetail.status = OrderStatus.Shipped.orderStatus
        }

        mHolder.txtOrderSucessMsg?.visibility = if (orderDetail.status == OrderStatus.Delivered.orderStatus && BuildConfig.CLIENT_CODE == "freshfarmandlocal_0443") {
            View.VISIBLE
        } else {
            View.GONE
        }

        val paymentFlow = orderUtils.checkOrderListFlow(orderDetail)

        mHolder.tvStatus1?.text = if (paymentFlow) {
            mContext.getString(R.string.payment_pending)
        } else {
            statusProduct(orderDetail.status, orderDetail.type
                    ?: 0, orderDetail.self_pickup ?: 0, mContext, orderDetail.terminology ?: "")
        }


        if (orderDetail.is_schedule != null && orderDetail.is_schedule == "1") {
            mHolder.groupSchedule?.visibility = View.VISIBLE
            mHolder.tvDelivered?.visibility = View.GONE
            mHolder.txtDeliver?.visibility = View.GONE
            mHolder.tvScheduleEnd?.text = convertDateNew(orderDetail.schedule_end_date ?: "")
            mHolder.tvScheduleStart?.text = convertDateNew(orderDetail.service_date ?: "")
            mHolder.tvScheduleOrder?.text = mContext.getString(R.string.currency_tag,
                    AppConstants.CURRENCY_SYMBOL, Utils.getPriceFormat(orderDetail.slot_price?.toFloat()
                    ?: 0f, settingData,selectedCurrency))
        } else {
            mHolder.groupSchedule?.visibility = View.GONE
            mHolder.tvDelivered?.visibility = View.VISIBLE
            mHolder.txtDeliver?.visibility = View.VISIBLE
            if (orderDetail.type == AppDataType.HomeServ.type) {
                mHolder.txtPlaced?.text = mContext.getString(R.string.start_time)

                if (settingData?.is_laundry_theme == "1" && !orderDetail.drop_off_date.isNullOrEmpty()) {
                    mHolder.tvDelivered?.text = convertDateNew(orderDetail.drop_off_date ?: "")
                } else {
                    if (orderDetail.status != OrderStatus.Delivered.orderStatus) {
                        mHolder.tvDelivered?.text = convertAddDate(orderDetail.delivered_on
                                ?: "", orderDetail.duration ?: 0)
                    } else {
                        mHolder.tvDelivered?.text = convertDateNew(orderDetail.delivered_on ?: "")
                    }
                }
            } else {

                if (orderDetail.status != OrderStatus.Delivered.orderStatus && settingData?.show_expected_delivery_between == "1") {
                    mHolder.tvDelivered?.text = mContext.getString(R.string.min_max_time, orderDetail.delivery_min_time
                            ?: 0, orderDetail.delivery_max_time ?: 0)
                } else {
                    mHolder.tvDelivered?.text = convertDateNew(orderDetail.delivered_on ?: "")
                }
            }
        }
        checkDeliverDate(orderDetail)

        mHolder.txtDelivered?.text = if (orderDetail.status != OrderStatus.Delivered.orderStatus) {
            when {
                orderDetail.type == AppDataType.HomeServ.type -> {
                    if (settingData?.is_laundry_theme == "1")
                        mContext.getString(R.string.drop_date_time)
                    else mContext.getString(R.string.end_time)
                }
                orderDetail.self_pickup == FoodAppType.Pickup.foodType -> {
                    mContext.getString(R.string.pickup_on)
                }
                else -> {
                    mContext.getString(R.string.expected_delivery_time)
                }
            }
        } else {
            when {
                orderDetail.type == AppDataType.HomeServ.type -> {
                    if (settingData?.is_laundry_theme == "1")
                        mContext.getString(R.string.drop_date_time)
                    else
                        mContext.getString(R.string.end_time)
                }
                orderDetail.self_pickup == FoodAppType.Pickup.foodType -> {
                    mContext.getString(R.string.pickup_on)
                }
                else -> {
                    mContext.getString(R.string.delivered_on)
                }
            }
        }

        mStatusList.add(OrderStatusModel(convertDate(orderDetail.created_on
                ?: "", orderDetail.status, OrderStatus.Pending.orderStatus), OrderStatus.Pending, orderDetail.status
                ?: 0.0))
        when (orderDetail.type) {
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
                mStatusList.add(OrderStatusModel(convertDate(checkDeliverDate(orderDetail), orderDetail.status, OrderStatus.Delivered.orderStatus), OrderStatus.Delivered, orderDetail.status
                        ?: 0.0))
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

        mHolder.rvStatus?.layoutManager?.scrollToPosition(mStatusPos)

        mHolder.checkChatVisiblity()

        mHolder.checkCallVisibility()

        //        var gpPrescription: Group? = null
        //        var additionRemark: TextView? = null
        //        var rvPhoto: RecyclerView? = null

        if (orderDetail.pres_description.isNullOrEmpty()) {
            mHolder.additionRemark?.visibility = View.GONE
        } else {
            mHolder.additionRemark?.visibility = View.VISIBLE
            mHolder.additionRemark?.text = orderDetail.pres_description
        }

        val imageList = productPreciption(orderDetail)

        if (!imageList.isNullOrEmpty()) {

            mHolder.gpPrescription?.visibility = View.VISIBLE

            val mAdapter = ImageListAdapter(UserChatListener(
                    {

                    }, {

            },{ it,pos->
                if(!it.image.isNullOrEmpty() && it.image?.endsWith(".pdf")==true){
                    StaticFunction.openCustomChrome(mContext, it.image ?: "")
                }
            }), false)

            mHolder.rvPhoto?.layoutManager = LinearLayoutManager(mContext, RecyclerView.HORIZONTAL, false)
            mHolder.rvPhoto?.adapter = mAdapter
            mAdapter.submitMessageList(imageList, "order")
        } else {
            mHolder.gpPrescription?.visibility = View.GONE
        }

        mHolder.tvPlaced?.text = if (orderDetail.type == AppDataType.HomeServ.type) {
            convertDateNew(orderDetail.service_date ?: "")
        } else {
            convertDateNew(orderDetail.created_on ?: "")
        }

        when (orderDetail.status) {
            OrderStatus.Rejected.orderStatus, OrderStatus.Customer_Canceled.orderStatus -> {
                mHolder.rvStatus?.visibility = View.GONE
            }
            else -> {
                mHolder.rvStatus?.visibility = View.VISIBLE
                mHolder.rvStatus?.adapter?.notifyDataSetChanged()
            }
        }

        if (orderDetail.promoCode != null) {
            mHolder.gpDiscount?.visibility = View.VISIBLE
            mHolder.tvPromoCode?.text = orderDetail.promoCode
            mHolder.tvDiscount?.text = "-" + mContext.getString(R.string.currency_tag, AppConstants.CURRENCY_SYMBOL,
                    Utils.getPriceFormat(orderDetail.discountAmount ?: 0.0f, settingData,selectedCurrency))
        } else {
            mHolder.gpDiscount?.visibility = View.GONE
        }

        if (settingData?.is_loyality_enable == "1") {
            mHolder.grpLoyalty?.visibility = View.VISIBLE
            mHolder.tvLoyaltyPoint?.text = mContext.getString(R.string.currency_tag, AppConstants.CURRENCY_SYMBOL, orderDetail.loyality_point_discount.toString())
        } else
            mHolder.grpLoyalty?.visibility = View.GONE

        mHolder.tvPament_method?.text = if(orderDetail.payment_type==DataNames.SKIP_PAYMENT)
            mContext.getString(R.string.out_of_app)
        else if (orderDetail.payment_type == 3 && orderDetail.payment_status == 0) {
            "None"
        } else if (DataNames.DELIVERY_WALLET == orderDetail.payment_type) {
            mContext.getString(R.string.wallet)
        } else if (DataNames.DELIVERY_CASH == orderDetail.payment_type) {
            textConfig?.cod
        } else if (orderDetail.payment_type == DataNames.DELIVERY_CARD) {
            mContext.getString(R.string.online_pay_tag, orderDetail.payment_source)
        } else {
            when (orderDetail.payment_source) {
                "zelle" -> {
                    mContext.getString(R.string.zelle)
                }
                "paypal" -> {
                    mContext.getString(R.string.pay_pal)
                }
                else -> {
                    mContext.getString(R.string.online_payment)
                }
            }
        }
        if (orderDetail.admin_updated_charge ?: 0f > 0f && orderDetail.is_subtotal_add != null) {
            mHolder.groupUpdatedAmt?.visibility = View.VISIBLE
            mHolder.tvUpdatedAmt?.text = if (orderDetail.is_subtotal_add == "1") mContext.getString(R.string.plus_amount,
                    AppConstants.CURRENCY_SYMBOL, Utils.getPriceFormat(orderDetail.admin_updated_charge ?: 0f, settingData,selectedCurrency))
            else mContext.getString(R.string.minus_amount,
                    AppConstants.CURRENCY_SYMBOL, Utils.getPriceFormat(orderDetail.admin_updated_charge
                    ?: 0f, settingData,selectedCurrency))
        } else
            mHolder.groupUpdatedAmt?.visibility = View.GONE


        mHolder.btnDownloadReceipt?.visibility = if (!orderDetail.admin_price_update_receipt.isNullOrEmpty() && orderDetail.is_edit == 1) View.VISIBLE else View.GONE

        mHolder.tvTrackDhlStatus?.visibility = if (orderDetail.dhlData?.isNotEmpty() == true) View.VISIBLE else View.GONE
        mHolder.tvTrackShipRocketStatus?.visibility = if (orderDetail.is_shiprocket_assigned == "1") View.VISIBLE else View.GONE
        settingData?.disable_tax?.let {
            if (it == "1") {
                mHolder.grpTax?.visibility = View.GONE
            }
        }

        mHolder.txtTax?.text = if (appTerminology?.tax?.isNotEmpty() == true) appTerminology?.tax else mContext.getString(R.string.tax)

        if (settingData?.is_coin_exchange == "1") {
            mHolder.groupExchangeMoney?.visibility = View.VISIBLE
            mHolder.tvExchangeMoney?.text = if (orderDetail.have_coin_change == "1")
                mContext.getString(R.string.yes) else mContext.getString(R.string.no)
        } else
            mHolder.groupExchangeMoney?.visibility = View.GONE

        if (orderDetail.status ?: 0.0 > OrderStatus.In_Kitchen.orderStatus || orderDetail.type != AppDataType.Food.type || orderDetail.preparation_time == "00:00:00" ||
                listOf(OrderStatus.Pending.orderStatus, OrderStatus.Delivered.orderStatus,
                        OrderStatus.Customer_Canceled.orderStatus, OrderStatus.Rejected.orderStatus).contains(orderDetail.status)) {
            mHolder.groupPrepTime?.visibility = View.GONE
        } else {
            mHolder.tvPrepTime?.text = mContext.getString(R.string.hrs, orderDetail.preparation_time)
            mHolder.groupPrepTime?.visibility = View.VISIBLE
        }

        if (orderDetail.self_pickup == FoodAppType.Pickup.foodType || orderDetail.self_pickup == FoodAppType.DineIn.foodType) {
            if (orderDetail.delivery_address == null) return
            mHolder.gpAddress?.visibility = View.VISIBLE
            mHolder.gpDelivery?.visibility = View.GONE


            if (orderDetail.self_pickup == FoodAppType.DineIn.foodType) {
                mHolder.tvAddress_t?.text = mContext.getString(R.string.dine_in_tag)
            } else {
                mHolder.tvAddress_t?.text = mContext.getString(R.string.pick_up)
            }

            if(orderDetail.is_appointment =="1")
                mHolder.tvAddress_t?.text = mContext.getString(R.string.appointment_at)


            val address = if (orderDetail.product?.firstOrNull()?.branch_name != null && orderDetail.branch_address != null)
                "${orderDetail.product?.firstOrNull()?.branch_name ?: ""} ,${orderDetail.branch_address}"
            else
                "${orderDetail.supplier_name ?: ""} ,${orderDetail.supplier_address ?: ""}"

            mHolder.tvAddress?.text = address
            mHolder.ivRestLocation?.visibility = View.VISIBLE

        } else {
            if (orderDetail.delivery_address == null) return

            mHolder.gpDelivery?.visibility = if (orderDetail.type == AppDataType.HomeServ.type) {
                View.GONE
            } else {
                View.VISIBLE
            }
            mHolder.gpAddress?.visibility = View.VISIBLE
            mHolder.tvAddress_t?.text = mContext.getString(R.string.address_detail)
            if (!orderDetail.delivery_address.user_name.isNullOrEmpty()) {
                mHolder.tvAddressName?.visibility = View.VISIBLE
                mHolder.tvAddressName?.text = mContext.getString(R.string.user_name, orderDetail.delivery_address.user_name)
            } else mHolder.tvAddressName?.visibility = View.GONE

            if (!orderDetail.delivery_address.reference_address.isNullOrEmpty()) {

                mHolder.tvReferenceAddress?.visibility = View.VISIBLE
                mHolder.tvReferenceAddress?.text = mContext.getString(R.string.reference_address, orderDetail.delivery_address.reference_address)
            } else mHolder.tvReferenceAddress?.visibility = View.GONE

            if (!orderDetail.delivery_address.phone_number.isNullOrEmpty()) {
                mHolder.tvAddressPhone?.visibility = View.VISIBLE
                mHolder.tvAddressPhone?.text = mContext.getString(R.string.contact_number_with_code, orderDetail.delivery_address.country_code?:"", orderDetail.delivery_address.phone_number?:"")
            } else mHolder.tvAddressPhone?.visibility = View.GONE


            mHolder.tvAddress?.text = "${
            orderDetail.delivery_address.customer_address
                    ?: ""
            } ,${orderDetail.delivery_address.address_line_1 ?: ""}"
            mHolder.ivRestLocation?.visibility = View.GONE
        }

        if (orderDetail.self_pickup == FoodAppType.DineIn.foodType) {
            mHolder.txtDelivered?.visibility = View.GONE
            mHolder.tvDelivered?.visibility = View.GONE
        }

        if (settingData?.is_skip_theme=="1" && !listOf(OrderStatus.Delivered.orderStatus,
                        OrderStatus.Customer_Canceled.orderStatus, OrderStatus.Rejected.orderStatus).contains(orderDetail.status)) {
            mHolder.txtDelivered?.visibility = View.GONE
            mHolder.tvDelivered?.visibility = View.GONE
        }

        val totalProdList = calculateProdAddon(orderDetail.product)



        if (orderDetail.questions?.isNotEmpty() == true && orderDetail.questions != "[]") {
            val myType = object : TypeToken<List<QuestionList>>() {}.type
            val questionList = gson.fromJson<List<QuestionList>>(orderDetail.questions, myType)


            if (questionList.isEmpty()) return
            mHolder.gpQuestion?.visibility = View.VISIBLE


            mHolder.tvAddonPrice?.text = mContext.getString(R.string.currency_tag, AppConstants.CURRENCY_SYMBOL, Utils.getPriceFormat(orderDetail.addOn
                    ?: 0.0f, settingData,selectedCurrency))
            val questAdapter = SelectedQuestAdapter(mContext, questionList, settingData,selectedCurrency)
            mHolder.rvQuestion?.adapter = questAdapter
            questAdapter.notifyDataSetChanged()
        }


        var subTotal = orderDetail.total_order_price ?: totalProdList?.sumByDouble {
            (it?.fixed_price?.toFloatOrNull()?.times(it.prod_quantity ?: 0f))?.toDouble()
                    ?: 0.0
        }?.toFloat()


        if (orderDetail.referral_amount ?: 0.0f > 0) {

            if (orderDetail.total_order_price == null && orderDetail.total_order_price == 0.0f) {
                subTotal = (subTotal?.minus(orderDetail.referral_amount ?: 0.0f))
            }

            mHolder.gpReferral?.visibility = View.VISIBLE
            mHolder.tvReferralAmt?.text = "-" + mContext.getString(R.string.currency_tag, AppConstants.CURRENCY_SYMBOL,
                    Utils.getPriceFormat(orderDetail.referral_amount
                    ?: 0.0f, settingData,selectedCurrency))
        } else {
            mHolder.gpReferral?.visibility = View.GONE
        }

        val totalAmt = orderDetail.net_amount ?: subTotal?.plus(orderDetail.handling_admin ?: 0.0f)
                ?.plus(orderDetail.delivery_charges ?: 0.0f)
                ?.minus(orderDetail.discountAmount ?: 0.0f)?.plus(orderDetail.tip_agent
                        ?: 0.0f)?.plus(orderDetail.user_service_charge
                        ?: 0.0)?.plus(orderDetail.addOn ?: 0.0f)?.toFloat()

        if (orderDetail.user_service_charge ?: 0.0 > 0) {
            mHolder.groupSupplierCharge?.visibility = View.VISIBLE
            mHolder.tvSupplierCharge?.text = mContext.getString(R.string.currency_tag,
                    AppConstants.CURRENCY_SYMBOL, Utils.getPriceFormat(orderDetail.user_service_charge?.toFloat()
                    ?: 0.0f, settingData,selectedCurrency))
        }

        mHolder.tvSubTotal?.text = mContext.getString(R.string.currency_tag, AppConstants.CURRENCY_SYMBOL, Utils.getPriceFormat(subTotal
                ?: 0.0f, settingData,selectedCurrency))

        val tipCharges = orderDetail.tip_agent ?: 0.0f


        mHolder.tvPayment?.text = mContext.getString(R.string.currency_tag, AppConstants.CURRENCY_SYMBOL, Utils.getPriceFormat(totalAmt
                ?: 0.0f, settingData,selectedCurrency))

        mHolder.tvAmount?.text = mContext.getString(R.string.currency_tag, AppConstants.CURRENCY_SYMBOL, Utils.getPriceFormat(decimalFormat.format(totalAmt)?.toFloatOrNull()?:0f
                ?: 0.0f, settingData,selectedCurrency))

        if (settingData?.is_currency_exchange_rate == "1" && orderDetail.currency_exchange_rate != null &&
                orderDetail.currency_exchange_rate != 0f
                && orderDetail.local_currency != null) {
            val amt = orderDetail.currency_exchange_rate?.times(totalAmt ?: 0f)

            val string = mContext.getString(R.string.net_total_exchange, orderDetail.local_currency,
                    Utils.getPriceFormat(decimalFormat.format(amt)?.toFloatOrNull()?:0f
                            ?: 0f, settingData,selectedCurrency), AppConstants.CURRENCY_SYMBOL,
                    orderDetail.currency_exchange_rate?.toString())
            mHolder.tvAmount?.text = string
        }

        if (orderDetail.remaining_amount ?: 0.0f > 0.0f) {
            mHolder.groupRemaining?.visibility = View.VISIBLE
            mHolder.tvRemaining?.text = mContext.getString(R.string.currency_tag, AppConstants.CURRENCY_SYMBOL, Utils.getPriceFormat(orderDetail.remaining_amount
                    ?: 0.0f, settingData,selectedCurrency))
        }

        if (orderDetail.refund_amount ?: 0f > 0f) {
            mHolder.groupRefund?.visibility = View.VISIBLE
            mHolder.tvRefund?.text = mContext.getString(R.string.currency_tag, AppConstants.CURRENCY_SYMBOL,
                    Utils.getPriceFormat(orderDetail.refund_amount
                            ?: 0.0f, settingData,selectedCurrency))
        }
        val tableDetail = orderDetail.table_details
        if (tableDetail != null) {
            mHolder.grp_table?.visibility = View.VISIBLE
            mHolder.txTableName?.text = tableDetail.table_name
            mHolder.txTableNumber?.text = " ${tableDetail.table_number.toString()}, ${mHolder.itemView.context.getString(R.string.seating_capacity, tableDetail.seating_capacity?.toString())}"
            val formattedDate = StaticFunction.convertDateOneToAnother(
                    orderDetail.service_date.toString(), "yyyy-MM-dd'T'HH:mm:ss", "EEE, dd MMMM hh:mm aaa")
            mHolder.txTableDate?.text = formattedDate
        } else {
            mHolder.grp_table?.visibility = View.GONE
        }


        if (mSelectedPayment?.payName == mContext.getString(R.string.zelle)) {
            mHolder.zelleDoc?.let {
                Glide.with(mContext).load(mSelectedPayment?.keyId
                        ?: "").into(it)
            }
            mHolder.groupZelle?.visibility = View.VISIBLE
        } else {
            mHolder.groupZelle?.visibility = View.GONE
        }

        if (DataNames.DELIVERY_WALLET == orderDetail.payment_type && orderDetail.wallet_discount_amount != null) {
            mHolder.tvWalletDiscount?.visibility = View.VISIBLE
            mHolder.txtWalletDiscount?.visibility = View.VISIBLE
            mHolder.tvWalletDiscount?.text = mContext.getString(R.string.currency_tag, AppConstants.CURRENCY_SYMBOL,
                    Utils.getPriceFormat(orderDetail.wallet_discount_amount
                            ?: 0f ,settingData,selectedCurrency))
        } else {
            mHolder.tvWalletDiscount?.visibility = View.GONE
            mHolder.txtWalletDiscount?.visibility = View.GONE
        }

        if (tipCharges > 0) {
            mHolder.grouptipCharges?.visibility = View.VISIBLE
            mHolder.tvTipCharges?.text = mContext.getString(R.string.currency_tag, AppConstants.CURRENCY_SYMBOL, Utils.getPriceFormat(tipCharges, settingData,selectedCurrency))
        } else {
            mHolder.grouptipCharges?.visibility = View.GONE

        }

        mHolder.groupAgentCode?.visibility = if (orderDetail.agent_verification_code?.isNotEmpty() == true &&
                settingData?.agent_verification_code_enable == "1" && orderDetail.self_pickup != FoodAppType.Pickup.foodType) View.VISIBLE else View.GONE
        mHolder.tvAgentCode?.text = orderDetail.agent_verification_code ?: ""
        mHolder.tvItemsCount?.text = totalProdList?.size.toString()

        mHolder.rvProduct?.adapter = OrderDetailProductAdapter(mContext, totalProdList
                ?: emptyList(), screenFlowBean, settingData, callBackReturn, orderDetail.status, appUtil, orderDetail, position,selectedCurrency)


        var isTrackBtn = false

        mHolder.btnTrackOrder?.visibility = if (orderDetail.type == AppDataType.HomeServ.type && orderDetail.status == OrderStatus.In_Kitchen.orderStatus) {
            isTrackBtn = true
            View.VISIBLE
        } else if (orderDetail.type != AppDataType.Ecom.type &&
                orderDetail.status != OrderStatus.Shipped.orderStatus && orderDetail.status == OrderStatus.On_The_Way.orderStatus
                && orderDetail.self_pickup != FoodAppType.Pickup.foodType) {
            isTrackBtn = true
            View.VISIBLE
        } else {
            isTrackBtn = false
            View.GONE
        }

        if (isTrackBtn && orderDetail.donate_to_someone == 1 || orderDetail.agent?.isEmpty() == true) {
            mHolder.btnTrackOrder?.visibility = View.GONE
        }
        val strings = textConfig
        if ((orderDetail.status == OrderStatus.Delivered.orderStatus || orderDetail.status == OrderStatus.Rating_Given.orderStatus)
                && orderDetail.is_supplier_rated == 0 && settingData?.is_supplier_rating == "1") {
            mHolder.btnRateSupplier?.visibility = View.VISIBLE
            mHolder.btnRateSupplier?.text = mContext.getString(R.string.rate_user_tag, strings?.supplier)
        } else {
            mHolder.btnRateSupplier?.visibility = View.GONE
        }
        mHolder.groupCutleryRequired?.visibility = if (settingData?.enable_cutlery_option == "1" && !orderDetail.is_cutlery_required.isNullOrEmpty()) View.VISIBLE else View.GONE
        mHolder.tvCutlery?.text = if (orderDetail.is_cutlery_required == "1") mContext.getString(R.string.yes) else mContext.getString(R.string.no)

        if (orderDetail.status == OrderStatus.Delivered.orderStatus && settingData?.is_agent_rating == "1" && !orderHistoryBeans[position].agent.isNullOrEmpty()) {
            mHolder.btnRateAgent?.text = mContext.getString(R.string.rate_user_tag, strings?.agent)
            mHolder.btnRateAgent?.visibility = View.VISIBLE
        } else {
            mHolder.btnRateAgent?.visibility = View.GONE
        }

        mHolder.groupVehicleNumber?.visibility = if (settingData?.enable_user_vehicle_number == "1" && !orderDetail.vehicle_number.isNullOrEmpty())
            View.VISIBLE else View.GONE
        mHolder.tvVehicleNumber?.text = orderDetail.vehicle_number ?: ""

        mHolder.rbAgent?.visibility = if (settingData?.is_agent_rating == "1") View.VISIBLE else View.GONE
        mHolder.tvTotalReviews?.visibility = if (settingData?.is_agent_rating == "1") View.VISIBLE else View.GONE

/*        mHolder.btnTrackOrder?.text = if (orderDetail.status == OrderStatus.On_The_Way.orderStatus)
            mContext.getString(R.string.track_order, Configurations.strings.order) else mContext.getString(R.string.rate_product, Configurations.strings.products)*/

        mHolder.tvZoomCall?.visibility = if (settingData?.is_zoom_call_enabled == "1" && (orderDetail.status == OrderStatus.Ready_to_be_picked.orderStatus ||
                orderDetail.status == OrderStatus.In_Kitchen.orderStatus)) View.VISIBLE else View.GONE


        mHolder.groupDeliveryType?.visibility = if (settingData?.is_enable_delivery_type == "1" && orderDetail.order_delivery_type != null
                && orderDetail.self_pickup == FoodAppType.Delivery.foodType) View.VISIBLE else View.GONE
        mHolder.tvDeliveryType?.text = if (orderDetail.order_delivery_type != null && orderDetail.order_delivery_type == "1")
            mContext.getString(R.string.express_delivery)
        else mContext.getString(R.string.normal_delivery)

        mHolder.btnTrackOrder?.text = mContext.getString(R.string.track_order, appUtil.loadAppConfig(orderDetail.type
                ?: 0).strings?.order)

        mHolder.tvViewBio?.visibility = if (!orderDetail.agent.isNullOrEmpty() &&
                !orderDetail.agent.firstOrNull()?.agent_bio_url.isNullOrEmpty() && settingData?.enable_audio_video=="1") View.VISIBLE else View.GONE

        mHolder.groupPaymentConfiramtion?.visibility=if(settingData?.enable_payment_confirmed_status=="1" && orderDetail.is_payment_confirmed!=null)
            View.VISIBLE else View.GONE
        mHolder.tvPaymentConfirmation?.text=if(orderDetail.is_payment_confirmed=="1") mContext.getString(R.string.confirmed) else mContext.getString(R.string.pending)
        mHolder.btnTrackOrder?.setOnClickListener { v: View? ->
            /*    Intent intent = new Intent(mContext, RateProductActivity.class);
            intent.putExtra("rateProducts", calculateRateProduct(orderDetail.getProduct()));
            mContext.startActivity(intent);*/
            //  val signUp = Prefs.with(mContext).getObject(DataNames.USER_DATA, PojoSignUp::class.java)
            val intent = Intent(mContext, UserTracking::class.java)
            intent.putExtra("userId", userDta)
            intent.putExtra("orderData", orderDetail)
            mContext.startActivity(intent)
        }

        settingData?.email?.let {
            if (it.isNotEmpty()) {
                mHolder.supportEmail?.visibility = View.VISIBLE
            }
        }

        mHolder.tvSos?.setOnClickListener {
            mCallback?.sosListener(orderDetail.order_id.toString())
        }

        mHolder.tvLiveChat?.setOnClickListener {
            mCallback?.liveChatListener(orderDetail.order_id.toString())
        }


        mHolder.supportEmail?.setOnClickListener {
            val i = Intent(Intent.ACTION_SEND)
            val adminEmail= settingData?.email?:""


            i.type = "message/rfc822"
            i.putExtra(Intent.EXTRA_EMAIL, arrayOf(adminEmail))
            i.putExtra(Intent.EXTRA_SUBJECT, mContext.getString(R.string.support_email_text,
                    orderDetail.order_id.toString()))
            try {
                mContext.startActivity(Intent.createChooser(i, "Send mail..."))
            } catch (ex: ActivityNotFoundException) {
                Toast.makeText(mContext, "There are no email clients installed.", Toast.LENGTH_SHORT).show()
            }
        }

    }

    private fun checkDeliverDate(orderDetail: OrderHistory): String {
        return if (orderDetail.status != OrderStatus.Delivered.orderStatus) "" else orderDetail.delivered_on
                ?: ""
    }

    private fun productPreciption(orderDetail: OrderHistory): MutableList<ImageListModel>? {

        val imageList: MutableList<ImageListModel>? = mutableListOf()
        orderDetail.pres_image1.let {
            if (it?.isNotEmpty() == true) {
                imageList?.add(ImageListModel(is_imageLoad = true, image = it, _id = ""))
            }
        }

        orderDetail.pres_image2.let {
            if (it?.isNotEmpty() == true) {
                imageList?.add(ImageListModel(is_imageLoad = true, image = it, _id = ""))
            }
        }

        orderDetail.pres_image3.let {
            if (it?.isNotEmpty() == true) {
                imageList?.add(ImageListModel(is_imageLoad = true, image = it, _id = ""))
            }
        }


        orderDetail.pres_image4.let {
            if (it?.isNotEmpty() == true) {
                imageList?.add(ImageListModel(is_imageLoad = true, image = it, _id = ""))
            }
        }

        orderDetail.pres_image5.let {
            if (it?.isNotEmpty() == true) {
                imageList?.add(ImageListModel(is_imageLoad = true, image = it, _id = ""))
            }
        }
        return imageList
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

    private fun convertDate(dateToConvert: String, orderStatus: Double?, currentStatus: Double): String {

        return appUtil.convertDateOneToAnother(replaceInvalid(dateToConvert).replace("T", " ").replace("+00:00", ""),
                "yyyy-MM-dd HH:mm:ss", "EEE, dd\nhh:mm aa") ?: ""
    }

    private fun convertDateNew(dateToConvert: String): String {
        return appUtil.convertDateOneToAnother(replaceInvalid(dateToConvert).replace("T", " ").replace("+00:00", ""),
                "yyyy-MM-dd HH:mm:ss", "MMM dd EEE hh:mm aa") ?: ""
    }

    private fun convertAddDate(dateToConvert: String, duration: Int): String {

        return appUtil.convertDateToAddDate(replaceInvalid(dateToConvert).replace("T", " ").replace("+00:00", ""),
                "yyyy-MM-dd HH:mm:ss", "MMM dd EEE hh:mm aa", duration) ?: ""
    }

    private fun replaceInvalid(dateToConvert: String): String {
        return if (dateToConvert.contains("Invalid date")) {
            dateToConvert.replace("Invalid date", "")
        } else {
            dateToConvert
        }
    }

    fun setZelleDoc(mSelectedPayment: CustomPayModel) {
        this.mSelectedPayment = mSelectedPayment
        notifyDataSetChanged()
    }

    class OrderListener(val clickListener: (model: Agent) -> Unit, val chatListner: (orderHist: OrderHistory) -> Unit, val supplierListener: (orderHist: OrderHistory) -> Unit,
                        val agentListener: (orderHist: OrderHistory) -> Unit,
                        val supplierNavigation: (orderHist: OrderHistory) -> Unit, val trackStatus: () -> Unit, val sosClick: (orderId: String) -> Unit,
                        val trackShipRocket: (orderId: String) -> Unit, val liveChat: (orderId: String) -> Unit,
                        val zoomCall: (orderHist: OrderHistory) -> Unit, val viewAgentBio: (agentBioUrl: String) -> Unit) {
        fun callDriver(agentBean: Agent) = clickListener(agentBean)

        fun chatDriver(orderData: OrderHistory) = chatListner(orderData)
        fun rateSupplier(orderData: OrderHistory) = supplierListener(orderData)
        fun rateAgent(agentBean: OrderHistory) = agentListener(agentBean)
        fun redirectToSupplier(orderData: OrderHistory) = supplierNavigation(orderData)
        fun trackDhlStatus() = trackStatus()

        fun sosListener(orderId: String) = sosClick(orderId)
        fun trackShipRocketStatus(orderId: String) = trackShipRocket(orderId)
        fun liveChatListener(orderId: String) = liveChat(orderId)
        fun joinZoomCall(orderHist: OrderHistory) = zoomCall(orderHist)

        fun agentBio(agentBioUrl: String) = viewAgentBio(agentBioUrl)
    }


    inner class View_holder(root: View) : RecyclerView.ViewHolder(root) {

        var tvStatus1: TextView? = root.tvStatus1
        var tvPayment: TextView? = root.tvPayment
        var tvItemsCount: TextView? = root.tvItemsCount
        var rvProduct: RecyclerView? = root.rvProduct
        var rvStatus: RecyclerView? = root.rvStatus
        var rvQuestion: RecyclerView? = root.recyclerviewQuest

        var tvOrder: TextView? = root.tvOrder
        var tvPlaced: TextView? = root.tvPlaced
        var tvOrderNo: TextView? = root.tvOrderNo
        var tvTax: TextView? = root.tvTax
        var tvSubTotal: TextView? = root.tvSubTotal
        var tvDelivery: TextView? = root.tvDelivery
        var tvDelivered: TextView? = root.tvDelivered
        var tvAmount: TextView? = root.tvAmount
        var tvAddress_t: TextView? = root.tvAddress_t
        var tvAddress: TextView? = root.tvAddress
        var tvPament_method_t: TextView? = root.txtPayMtd
        var tvPament_method: TextView? = root.tvPament_method

        // var txtDeliver: TextView? = null
        var ivSupplierIcon: CircleImageView? = root.ivSupplierIcon
        var zelleDoc: ImageView? = root.iv_doc
        var tvPromoCode: TextView? = root.tvPromoCode
        var tvDiscount: TextView? = root.tvDiscount
        var btnTrackOrder: MaterialButton? = root.btnTrackOrder
        var btnRateSupplier: MaterialButton? = root.btnRateSupplier
        var btnRateAgent: MaterialButton? = root.btnRateAgent
        var agentLayout: View? = root.lyt_agent
        var tvViewBio: View? = root.tvViewBio
        var tvAgentDetail: TextView? = root.tvAgentDetail
        var ivUserImage: CircleImageView? = root.iv_userImage
        var tvName: TextView? = root.tv_name
        var tvOccupation: TextView? = root.tv_occupation
        var tvAddonPrice: TextView? = root.tvAddonCharges


        var rbAgent: TextView? = root.rb_agent
        var tvTotalReviews: TextView? = root.tv_total_reviews
        var tvReferralAmt: TextView? = root.tvReferral
        var gpAction: Group? = root.gp_action
        var gpDiscount: Group? = root.grp_discount
        var gpAddress: Group? = root.grp_address
        var gpReferral: Group? = root.group_referral
        var gpDelivery: Group? = root.grpDelivery
        var gpQuestion: Group? = root.grp_question
        var grouptipCharges: Group? = root.grptipCharges
        var groupRemaining: Group? = root.grp_remaining
        var groupRefund: Group? = root.grp_refund
        var groupZelle: Group? = root.group_zelle

        var tvTipCharges: TextView? = root.tvTipChargesOrder
        var tvRemaining: TextView? = root.tvRemaining
        var tvRefund: TextView? = root.tvRefund

        var txtPlaced: TextView? = root.txtPlaced
        var txtDelivered: TextView? = root.txtDeliver
        var gpPrescription: Group? = root.grp_preciption
        var additionRemark: TextView? = root.edAdditionalRemarks
        var callDriver: ImageView? = root.ic_call
        var chatDriver: ImageView? = root.iv_chat_agent
        var rvPhoto: RecyclerView? = root.rvPhotoList
        var groupSupplierCharge: Group? = root.group_service
        var tvSupplierCharge: TextView? = root.tvServiceFee
        var tvSos: TextView? = root.tvSos
        var tvLiveChat: TextView? = root.tv_live_chat
        var supportEmail: TextView? = root.tvSupportEmail
        var tvHavePets: TextView? = root.textHavePets
        var tvCleanerIn: TextView? = root.textCleanerIn
        var tvParkingInstructions: TextView? = root.tvParkingInstructions
        var tvAreaToFoucus: TextView? = root.tvAreasToFocusOn
        var grpExtInst: Group? = root.grpExtInst
        var grpTax: Group? = root.grp_tax
        var txtTax: TextView? = root.txtTax

        var tvShippingStatus: TextView? = root.tvShippingStatus
        var tvShippingStatusTag: TextView? = root.tvShippingStatusTag
        var txtWalletDiscount: TextView? = root.txtWalletDiscount
        var tvWalletDiscount: TextView? = root.tvWalletDiscount
        var tvScheduleStart: TextView? = root.tvScheduleStart
        var tvScheduleEnd: TextView? = root.tvScheduleEnd
        var groupSchedule: Group? = root.groupSchedule
        var txtDeliver: TextView? = root.txtDeliver

        var tvScheduleOrder: TextView? = root.tvSchedule
        var tvPrepTime: TextView? = root.tvPrepTime
        var groupPrepTime: Group? = root.groupPrepTime
        var tvLoyaltyPoint: TextView? = root.tvLoyaltyPoint
        var ivRestLocation: ImageView? = root.ivRestLocation
        var grpLoyalty: Group? = root.group_loyalty
        var txTableName: TextView? = root.txTableName
        var txTableNumber: TextView? = root.txTableNumber
        var txTableDate: TextView? = root.txTableDate
        var grp_table: Group? = root.grp_table
        var groupExchangeMoney: Group? = root.groupExchangeMoney
        var tvExchangeMoney: TextView? = root.tvExchangeMoney
        var tvTrackDhlStatus: TextView? = root.tvTrackDhlStatus
        var groupAgentCode: LinearLayout? = root.lyt_agent_code
        var tvAgentCode: TextView? = root.tvAgentCode
        var tvAddressName: TextView? = root.tvAddressName
        var tvAddressPhone: TextView? = root.tvAddressPhone
        var btnDownloadReceipt: MaterialButton? = root.btnDownloadReceipt
        var tvTrackShipRocketStatus: MaterialButton? = root.tvTrackShipRocketStatus
        var groupUpdatedAmt: Group? = root.groupUpdatedPrice
        var tvUpdatedAmt: TextView? = root.tvUpdatedAmt
        var tvReferenceAddress: TextView? = root.tvReferenceAddress
        var tvZoomCall: TextView? = root.tvZoomCall
        var txtOrderSucessMsg: TextView? = root.txtOrderSucessMsg
        var groupDeliveryType: Group? = root.groupDeliveryType
        var tvDeliveryType: TextView? = root.tvDeliveryType
        var groupVehicleNumber: Group? = root.groupVehicleNumber
        var tvVehicleNumber: TextView? = root.tvVehicleNumber
        var groupCutleryRequired: Group? = root.groupCutleryRequired
        var tvCutlery: TextView? = root.tvCutleryRequired
        var groupPaymentConfiramtion:Group?=root.groupPaymentConfiramtion
        var tvPaymentConfirmation:TextView?=root.tvPaymentConfirmation
        init {
            tvZoomCall?.setOnClickListener {
                mCallback?.joinZoomCall(orderHistoryBeans[adapterPosition])
            }
            callDriver?.setOnClickListener {
                if (!orderHistoryBeans[adapterPosition].agent.isNullOrEmpty()) {
                    orderHistoryBeans[adapterPosition].agent?.get(0)?.let { it1 ->
                        it1.proxy_phone_number=orderHistoryBeans[adapterPosition].proxy_phone_number
                        mCallback?.callDriver(it1)
                    }
                }
            }
            btnDownloadReceipt?.setOnClickListener {
                if (!orderHistoryBeans[adapterPosition].admin_price_update_receipt.isNullOrEmpty()) {
                    StaticFunction.openCustomChrome(mContext, orderHistoryBeans[adapterPosition].admin_price_update_receipt
                            ?: "")
                }
            }
            chatDriver?.setOnClickListener {
                mCallback?.chatDriver(orderHistoryBeans[adapterPosition])
            }

            btnRateSupplier?.setOnClickListener {
                mCallback?.rateSupplier(orderHistoryBeans[adapterPosition])
            }

            tvViewBio?.setOnClickListener {
                mCallback?.agentBio(orderHistoryBeans[adapterPosition].agent?.firstOrNull()?.agent_bio_url.toString())
            }
            ivRestLocation?.setOnClickListener {
                mCallback?.redirectToSupplier(orderHistoryBeans[adapterPosition])
            }
            btnRateAgent?.setOnClickListener {
                if (!orderHistoryBeans[adapterPosition].agent.isNullOrEmpty()) {
                    mCallback?.rateAgent(orderHistoryBeans[adapterPosition])
                }
            }
            tvTrackDhlStatus?.setOnClickListener {
                mCallback?.trackDhlStatus()
            }
            tvTrackShipRocketStatus?.setOnClickListener {
                mCallback?.trackShipRocketStatus(orderHistoryBeans[adapterPosition].order_id.toString())
            }
        }

        fun checkChatVisiblity() {
            settingData?.chat_enable.let {
                if (it == "1") {
                    chatDriver?.checkVisibility()
                } else {
                    View.GONE
                }
            }
        }

        private fun View.checkVisibility() {
            val status = orderHistoryBeans[adapterPosition].status ?: 0.0

            visibility = if (orderHistoryBeans[adapterPosition].type == AppDataType.HomeServ.type) {

                if (status != OrderStatus.Pending.orderStatus && status != OrderStatus.Rejected.orderStatus && status != OrderStatus.Delivered.orderStatus
                        && status != OrderStatus.Customer_Canceled.orderStatus) {
                    View.VISIBLE
                } else {
                    View.GONE
                }
            } else {
                if (status >= OrderStatus.In_Kitchen.orderStatus && status != OrderStatus.Delivered.orderStatus
                        && status != OrderStatus.Rating_Given.orderStatus && status != OrderStatus.Customer_Canceled.orderStatus
                        && status != OrderStatus.Scheduled.orderStatus
                        && status != OrderStatus.Rejected.orderStatus) {
                    View.VISIBLE
                } else {
                    View.GONE
                }

            }

        }

        fun checkCallVisibility() {

            if (!orderHistoryBeans[adapterPosition].agent.isNullOrEmpty() &&
                    orderHistoryBeans[adapterPosition].agent?.firstOrNull()?.phone_number?.isNotEmpty() == true) {
                callDriver?.checkVisibility()
            } else {
                callDriver?.visibility = View.GONE
            }
        }
    }


}