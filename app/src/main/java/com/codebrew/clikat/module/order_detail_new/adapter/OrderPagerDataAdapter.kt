package com.codebrew.clikat.module.order_detail_new.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.codebrew.clikat.R
import com.codebrew.clikat.app_utils.AppUtils
import com.codebrew.clikat.app_utils.OrderUtils
import com.codebrew.clikat.data.OrderStatus
import com.codebrew.clikat.data.model.api.orderDetail.OrderHistory
import com.codebrew.clikat.data.model.others.OrderStatusModel
import com.codebrew.clikat.databinding.PagerProductNewItemBinding
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapsInitializer
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import kotlinx.android.synthetic.main.fragment_order_status.*
import kotlinx.android.synthetic.main.pager_product_new_item.view.*
import java.text.DecimalFormat

class OrderPagerDataAdapter(private val orderHistoryBeans: MutableList<OrderHistory>,
                            private val appUtil: AppUtils,
                            private val orderUtils: OrderUtils,
                            private val decimalFormat: DecimalFormat) : RecyclerView.Adapter<OrderPagerDataAdapter.ViewHolder>() {

     var mContext: Context? = null


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        mContext = parent.context

        val binding: PagerProductNewItemBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.pager_product_new_item, parent, false)
        return ViewHolder(binding.root,parent)
    }

    class ViewHolder(root: View, private val view: ViewGroup) : RecyclerView.ViewHolder(root), OnMapReadyCallback {

        private lateinit var map: GoogleMap
        private lateinit var latLng: LatLng

        init {
            with(itemView.mapView) {
                // Initialise the MapView
                onCreate(null)
                // Set the map ready callback to receive the GoogleMap object
                getMapAsync(this@ViewHolder)
            }
        }

        private fun setMapLocation() {
            if (!::map.isInitialized) return
            with(map) {
                moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 13f))
                addMarker(MarkerOptions().position(latLng))
                mapType = GoogleMap.MAP_TYPE_NORMAL
            }
        }


        override fun onMapReady(googleMap: GoogleMap?) {
            MapsInitializer.initialize(view.context)
            // If map is not initialised properly
            map = googleMap ?: return
            setMapLocation()
        }

        /** This function is called when the RecyclerView wants to bind the ViewHolder. */
        fun bindView(orderItem: OrderHistory, orderUtils: OrderUtils) {
            LatLng(orderItem.latitude ?: 0.0, orderItem.longitude ?: 0.0).let {
                latLng = it

                itemView.mapView.tag = this
                // We need to call setMapLocation from here because RecyclerView might use the
                // previously loaded maps
                setMapLocation()
            }

            itemView.rv_status.layoutManager=LinearLayoutManager(view.context,RecyclerView.HORIZONTAL,false)


            val mStatusList = mutableListOf<OrderStatusModel>()

            if (orderItem.status == 11.0) {
                orderItem.status = OrderStatus.In_Kitchen.orderStatus
            }

            if (orderItem.status == 10.0) {
                orderItem.status = OrderStatus.Shipped.orderStatus
            }

            mStatusList.addAll(orderUtils.getAllStatus(orderItem))

            itemView.rv_status?.adapter = StatusOrderAdapter(mStatusList, orderItem.type, orderItem.self_pickup, orderItem.terminology
                    ?: "")


            val mStatusPos = mStatusList.indexOfFirst {
                it.status.orderStatus == orderItem.status
            }

            itemView.rv_status.layoutManager?.scrollToPosition(mStatusPos)


        }

        /** This function is called by the recycleListener, when we need to clear the map. */
        fun clearView() {
            with(map) {
                // Clear the map and free up resources by changing the map type to none
                clear()
                mapType = GoogleMap.MAP_TYPE_NONE
            }
        }

    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val orderHistory=orderHistoryBeans.get(holder.adapterPosition)

        holder.bindView(orderHistory,orderUtils)
    }

    override fun getItemCount(): Int {
        return orderHistoryBeans.count()
    }
}