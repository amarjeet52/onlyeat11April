package com.codebrew.clikat.module.tables

import android.annotation.SuppressLint
import android.os.Build
import android.text.format.DateUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.codebrew.clikat.R
import com.codebrew.clikat.app_utils.AppUtils
import com.codebrew.clikat.data.OrderStatus
import com.codebrew.clikat.data.TableBookingStatus
import com.codebrew.clikat.data.constants.AppConstants
import com.codebrew.clikat.data.model.api.Currency
import com.codebrew.clikat.databinding.BookedTableRecyclerItemBinding
import com.codebrew.clikat.modal.BookedTableItem
import com.codebrew.clikat.modal.other.SettingModel
import com.codebrew.clikat.preferences.DataNames
import com.codebrew.clikat.utils.StaticFunction
import com.codebrew.clikat.utils.Utils
import com.codebrew.clikat.utils.configurations.Configurations
import kotlinx.android.synthetic.main.booked_table_recycler_item.view.*

class BookedTablesRecyclerAdapter : RecyclerView.Adapter<BookedTablesRecyclerAdapter.ItemViewHolder>() {

    private var listCollection = mutableListOf<BookedTableItem?>()
    private lateinit var actionListener: TablesRecyclerActionListener
    private var dataType = 0
    private var clientInform: SettingModel.DataBean.SettingData? = null
    private var selectedCurrency: Currency? = null
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val binding: BookedTableRecyclerItemBinding = DataBindingUtil.inflate(
                LayoutInflater.from(parent.context),
                R.layout.booked_table_recycler_item,
                parent,
                false
        )

        binding.color = Configurations.colors
        return ItemViewHolder(binding)
    }

    override fun getItemCount(): Int = listCollection.size

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        holder.onBind(listCollection[position])
    }

    fun setListData(listCollection: List<BookedTableItem?>?) {
        this.listCollection = listCollection as MutableList<BookedTableItem?>
    }

    fun setActionListener(actionListener: TablesRecyclerActionListener) {
        this.actionListener = actionListener
    }

    fun setDataType(dataType: Int) {
        this.dataType = dataType
    }

    fun setSettingsData(clientInform: SettingModel.DataBean.SettingData?,currency:Currency?) {
        this.clientInform = clientInform
        selectedCurrency=currency
    }

    inner class ItemViewHolder(val binding: BookedTableRecyclerItemBinding) :
            RecyclerView.ViewHolder(binding.root) {


        @RequiresApi(Build.VERSION_CODES.O)
        @SuppressLint("SetTextI18n")
        fun onBind(itemModel: BookedTableItem?) {
            binding.dataItem = itemModel
            itemView.btnAddItems?.visibility = View.GONE
            itemView.btnOnMyWay?.visibility = View.GONE
            itemView.btnInviteFriends?.visibility = View.GONE
            itemView.tvTableLocation?.visibility = View.GONE

            val formattedDate = StaticFunction.convertDateOneToAnother(
                    itemModel?.scheduleDate.toString(), "yyyy-MM-dd'T'HH:mm:ss", "EEE, dd MMMM hh:mm aaa")
            itemView.tvBookingDate?.text = itemView.context.getString(R.string.table_date, formattedDate)
            val status = when (itemModel?.status?.toDouble()) {
                TableBookingStatus.Pending.tableStatus -> itemView.context.getString(R.string.pending)
                TableBookingStatus.Approved.tableStatus -> itemView.context.getString(R.string.confirmed)
                TableBookingStatus.Rejected.tableStatus -> itemView.context.getString(R.string.reject)
                TableBookingStatus.Completed.tableStatus -> itemView.context.getString(R.string.table_status_complete)
                else -> ""
            }

            itemView.tvBookingStatus?.text = "Status: $status"


            if (clientInform?.is_table_invite_allowed == "1") {
                if (dataType == 0 &&
                        itemModel?.status?.toDouble() == OrderStatus.Confirmed.orderStatus) {
                    itemView.btnInviteFriends?.visibility = View.VISIBLE
                }
            }

            if (itemModel?.userOnTheWay == 0 &&
                    itemModel.status?.toDouble() == OrderStatus.Confirmed.orderStatus) {
                itemView.btnOnMyWay?.visibility = View.VISIBLE
            }


            if(itemModel?.userOnTheWay == 1){
                itemView.tvTableLocation?.visibility = View.VISIBLE
            }

            if (clientInform?.table_booking_add_food_allow == "1") {
                if (itemModel?.status?.toDouble() == OrderStatus.Confirmed.orderStatus && itemModel.userInRange == 1) {
                    itemView.btnAddItems?.visibility = View.VISIBLE
                }
            }

            if (itemModel?.seatingCapacity == null && itemModel?.tableNumber == null && itemModel?.tableName == null) {
                itemView.tvTableName.visibility = View.GONE
                itemView.tvTableNumber.visibility = View.GONE
                itemView.tvSeatingCapacity.visibility = View.GONE
            } else {
                itemView.tvTableName.visibility = View.VISIBLE
                itemView.tvTableNumber.visibility = View.VISIBLE
                itemView.tvSeatingCapacity.visibility = View.VISIBLE
            }

            if(itemModel?.amount!=null){
                itemView.tvAmount.visibility=View.VISIBLE
                itemView.tvAmount.text=itemView.context.getString(R.string.amount_currency_tag, AppConstants.CURRENCY_SYMBOL,
                        Utils.getPriceFormat(itemModel.amount, clientInform, selectedCurrency))
            }else
                itemView.tvAmount.visibility=View.GONE

            if(!itemModel?.payment_source.isNullOrEmpty()){
                itemView.tvPaymentSource.visibility=View.VISIBLE
                itemView.tvPaymentSource?.text = itemView.context.getString(R.string.payment_source, when {
                    itemModel?.payment_type== DataNames.SKIP_PAYMENT -> itemView.context.getString(R.string.out_of_app)
                    DataNames.DELIVERY_WALLET == itemModel?.payment_type -> {
                        itemView.context.getString(R.string.wallet)
                    }
                    itemModel?.payment_type == DataNames.DELIVERY_CARD -> {
                        itemView.context.getString(R.string.online_pay_tag, itemModel?.payment_source)
                    }
                    else -> {
                        when (itemModel?.payment_source) {
                            "zelle" -> {
                                itemView.context.getString(R.string.zelle)
                            }
                            "paypal" -> {
                                itemView.context.getString(R.string.pay_pal)
                            }
                            else -> {
                                itemView.context.getString(R.string.online_payment)
                            }
                        }
                    }
                })
            }else
                itemView.tvPaymentSource.visibility=View.GONE

            itemView.btnAddItems?.setOnClickListener {
                actionListener.onAddItemsInCartClicked(itemModel)
            }

            itemView.btnInviteFriends?.setOnClickListener {
                actionListener.onInviteFriendClicked(itemModel)
            }

            itemView.btnOnMyWay.setOnClickListener {
                actionListener.onOnMyWayClicked(itemModel)
            }


        }
    }

}

