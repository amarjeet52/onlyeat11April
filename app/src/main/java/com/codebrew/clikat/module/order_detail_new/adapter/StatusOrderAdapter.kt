package com.codebrew.clikat.module.order_detail_new.adapter

import android.content.Context
import android.graphics.Color
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.codebrew.clikat.R
import com.codebrew.clikat.data.OrderStatus
import com.codebrew.clikat.data.model.others.OrderStatusModel
import com.codebrew.clikat.module.order_detail.adapter.OrderStatusAdapter
import com.codebrew.clikat.utils.StaticFunction
import com.codebrew.clikat.utils.configurations.Configurations
import com.github.vipulasri.timelineview.TimelineView
import com.github.vipulasri.timelineview.sample.utils.VectorDrawableUtils
import kotlinx.android.synthetic.main.item_status_order.view.*


class StatusOrderAdapter(private val mStatusList: List<OrderStatusModel>, private val appType: Int?,
                         private val selfPickup: Int?, private val orderTerminology: String) : RecyclerView.Adapter<StatusOrderAdapter.StatusViewHolder>() {

    private lateinit var mLayoutInflater: LayoutInflater

    private var mContext: Context? = null

    val colorConfig by lazy { Configurations.colors }

    override fun getItemViewType(position: Int): Int {
        return TimelineView.getTimeLineViewType(position, itemCount)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StatusViewHolder {

        mContext = parent.context

        if (!::mLayoutInflater.isInitialized) {
            mLayoutInflater = LayoutInflater.from(parent.context)
        }

        return StatusViewHolder(mLayoutInflater.inflate(R.layout.item_status_order, parent, false), viewType)
    }

    override fun onBindViewHolder(holder: StatusViewHolder, position: Int) {

        val timeLineModel = mStatusList[position]

        if (timeLineModel.orderStatus == 11.0) {
            timeLineModel.orderStatus = OrderStatus.In_Kitchen.orderStatus
        }

        if (timeLineModel.status.toString().equals("Approved")) {
            holder.ivStatus.setImageResource(R.drawable.accepted)
        } else if (timeLineModel.status.toString().equals("In_Kitchen")) {
            holder.ivStatus.setImageResource(R.drawable.in_kitchen)
        } else if (timeLineModel.status.toString().equals("On_The_Way")) {
            holder.ivStatus.setImageResource(R.drawable.on_the_way)
        } else {
            holder.ivStatus.setImageResource(R.drawable.delivered)
        }
//        holder.ivStatus.setImageResource( when(timeLineModel.orderStatus)
//        {
//            OrderStatus.Approved.orderStatus,OrderStatus.Confirmed.orderStatus->
//                R.drawable.accepted
//
//            OrderStatus.In_Kitchen.orderStatus->
//                R.drawable.in_kitchen
//            OrderStatus.On_The_Way.orderStatus->
//                R.drawable.on_the_way
//            OrderStatus.Ended.orderStatus, OrderStatus.Delivered.orderStatus->
//                R.drawable.delivered
//            else->R.drawable.accepted
//        })

        when {
            timeLineModel.status.orderStatus <= timeLineModel.orderStatus -> {
                setMarker(holder, R.drawable.radio_on, R.color.colorPrimary, true)
                holder.timeline.setEndLineColor(Color.parseColor(colorConfig.primaryColor), 4)
                holder.timeline.setStartLineColor(Color.parseColor(colorConfig.primaryColor), 4)
                if (position == 1 || position == 2|| position == 3) {
                    if (position == 2 &&  (OrderStatus.Ready_to_be_picked.orderStatus == timeLineModel.orderStatus))
                        holder.ivMarker.visibility = View.GONE
                    else
                        holder.ivMarker.visibility = View.VISIBLE
                }
            }
            else -> {
                setMarker(holder, R.drawable.radio_off, R.color.black_10, false)
                holder.timeline.setEndLineColor(Color.parseColor(colorConfig.lightGrey), 4)
                holder.timeline.setStartLineColor(Color.parseColor(colorConfig.lightGrey), 4)
                holder.timeline.setMarkerColor(Color.parseColor(colorConfig.lightGrey))
                holder.ivStatus.setColorFilter(ContextCompat.getColor(holder.ivStatus.context, R.color.lightGrey), android.graphics.PorterDuff.Mode.MULTIPLY);

                holder.ivMarker.visibility = View.GONE
            }
        }

/*        holder.timeline.setEndLineColor(Color.parseColor(colorConfig.primaryColor), 4)
        holder.timeline.setStartLineColor(Color.parseColor(colorConfig.primaryColor), 4)
        holder.timeline.setMarkerColor(Color.parseColor(colorConfig.primaryColor))*/

        holder.message.text = StaticFunction.statusProduct1(timeLineModel.status.orderStatus, appType
                ?: 0, selfPickup ?: 0, mContext, orderTerminology)
    }

    private fun setMarker(holder: StatusOrderAdapter.StatusViewHolder, drawableResId: Int, colorFilter: Int, status: Boolean) {
        if (Build.VERSION.SDK_INT >= 24) {
            holder.timeline.marker = VectorDrawableUtils.getDrawable(holder.itemView.context, drawableResId, ContextCompat.getColor(holder.itemView.context, colorFilter))
        } else {
            if (status) {
                holder.timeline.marker = ContextCompat.getDrawable(holder.itemView.context, R.drawable.radio_on)
            } else {
                holder.timeline.marker = ContextCompat.getDrawable(holder.itemView.context, R.drawable.radio_off)
            }
        }
        /*   if (status){
               holder.timeline.setMarker(holder.timeline.context.getDrawable(R.drawable.radio_on),holder.timeline.context.getColor(R.color.color_main))
           }else{
               holder.timeline.setMarker(holder.timeline.context.getDrawable(R.drawable.radio_off),holder.timeline.context.getColor(R.color.colorPrimary))
           }*/
    }

    override fun getItemCount() = mStatusList.size

    inner class StatusViewHolder(itemView: View, viewType: Int) : RecyclerView.ViewHolder(itemView) {

        val ivStatus = itemView.iv_status
        val message = itemView.tv_status
        val timeline = itemView.timeline
        val ivMarker = itemView.ivMarker

        init {
            timeline.initLine(viewType)
        }
    }

}
