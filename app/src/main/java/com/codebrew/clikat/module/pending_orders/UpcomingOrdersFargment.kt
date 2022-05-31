package com.codebrew.clikat.module.pending_orders

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.RecyclerView
import com.codebrew.clikat.R
import com.codebrew.clikat.base.PagingResult
import com.codebrew.clikat.data.AppDataType
import com.codebrew.clikat.data.constants.AppConstants
import com.codebrew.clikat.data.constants.PrefenceConstants
import com.codebrew.clikat.data.model.api.Currency
import com.codebrew.clikat.data.model.api.orderDetail.OrderHistory
import com.codebrew.clikat.data.model.others.OrderEvent
import com.codebrew.clikat.modal.DataCommon
import com.codebrew.clikat.modal.other.SettingModel
import com.codebrew.clikat.module.base_orders.BaseOrderFragment
import com.codebrew.clikat.module.help.HelpActivity
import com.codebrew.clikat.module.order_detail.OrderDetailActivity
import com.codebrew.clikat.module.pending_orders.adapter.UpcomingAdapter
import com.codebrew.clikat.module.user_tracking.UserTracking
import com.codebrew.clikat.preferences.DataNames
import com.codebrew.clikat.utils.StaticFunction
import com.google.gson.Gson
import kotlinx.android.synthetic.main.fragment_base_order.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.util.*

/*
 * Created by Ankit Jindal on 20/4/16.
 */

const val CancelOrder = 587

class UpcomingOrdersFargment : BaseOrderFragment(), UpcomingAdapter.OrderCallback {

    private var upcomingAdapter: UpcomingAdapter? = null

    private var screenFlowBean: SettingModel.DataBean.ScreenFlowBean? = null

    private var orderHistory2List: MutableList<OrderHistory> = mutableListOf()
    private var selectedCurrency: Currency?=null

    companion object {
        fun newInstance() = UpcomingOrdersFargment()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel.navigator = this

        pendingObserver()

        cancelObserver()

        EventBus.getDefault().register(this)
    }

    private fun cancelObserver() {

        // Create the observer which updates the UI.
        val catObserver = Observer<DataCommon> { resource ->

            updateCancelOrder(resource)
        }

        // Observe the LiveData, passing in this activity as the LifecycleOwner and the observer.
        viewModel.cancelLiveData.observe(this, catObserver)
    }

    private fun updateCancelOrder(resource: DataCommon?) {
        StaticFunction.sweetDialogueSuccess(activity, getString(R.string.success), getString(R.string.cancel_msg, textConfig?.order
                ?: ""), false, 101, null, null)
        orderHistory2List.removeAt(upcomingAdapter!!.positionSelected)
        upcomingAdapter!!.notifyItemRemoved(upcomingAdapter!!.positionSelected)
        upcomingAdapter!!.notifyDataSetChanged()
    }

    override fun onResume() {
        super.onResume()

        callApi(true)
    }

    private fun callApi(firstPage: Boolean) {
        if (isNetworkConnected) {
            /*           if (viewModel.pendingLiveData.value != null && firstPage) {
                           if (viewModel.pendingLiveData.value?.isFirstPage == true) {
                               orderHistory2List.clear()
                           }
                           updatePendingData(viewModel.pendingLiveData.value!!)
                       } else {*/
            if (prefHelper.getCurrentUserLoggedIn()) {
                viewModel.getUpcomingList(firstPage)
            }
            //         }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        screenFlowBean = prefHelper.getGsonValue(DataNames.SCREEN_FLOW, SettingModel.DataBean.ScreenFlowBean::class.java)
        selectedCurrency = prefHelper.getGsonValue(PrefenceConstants.CURRENCY_INF, Currency::class.java)
        upcomingAdapter = UpcomingAdapter(activity, orderHistory2List, settingData, appUtils, orderUtils,screenFlowBean,selectedCurrency)


        upcomingAdapter?.settingCallback(this)

        onRecyclerViewScrolled()
    }


    private fun pendingObserver() {
        // Create the observer which updates the UI.
        val catObserver = Observer<PagingResult<MutableList<OrderHistory>>> { resource ->
            if (resource != null) {
                if (resource.isFirstPage) {
                    orderHistory2List.clear()
                }
                updatePendingData(resource)
            }

        }

        // Observe the LiveData, passing in this activity as the LifecycleOwner and the observer.
        viewModel.pendingLiveData.observe(this, catObserver)
    }

    private fun updatePendingData(resource: PagingResult<MutableList<OrderHistory>>) {

        resource.result?.map {
            it.product_count=calculateProdAddon(it.product)?.sumBy { it?.prod_quantity?.toInt()?:0 }
        }

        orderHistory2List.addAll(resource.result ?: emptyList())

        if (resource.isFirstPage) {
            setAdapter(upcomingAdapter, getString(R.string.order_history, textConfig?.order), getString(R.string.no_order_found))
        } else {
            upcomingAdapter?.notifyDataSetChanged()
        }
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onMessageEvent(event: OrderEvent) {

        if (event.type == AppConstants.CANCEL_EVENT) {
            apiCancelOrder(event.cancelToWallet)
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onMessagEvent(event: OrderEvent) {
        if (event.type == AppConstants.NOTIFICATION_EVENT) {
            if (isNetworkConnected) {
                viewModel.pendingLiveData.value = null
                callApi(true)
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == CancelOrder && resultCode == Activity.RESULT_OK) {
            viewModel.pendingLiveData.value = null
            callApi(true)
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()

        viewModel.compositeDisposable.clear()

        EventBus.getDefault().unregister(this)
    }

    override fun refreshData() {
        callApi(true)
    }

    fun apiCancelOrder(cancelToWallet: Int) {
        if (isNetworkConnected) {
            viewModel.cancelOrder(upcomingAdapter?.selectedOrderId.toString(), cancelToWallet)
        }
    }

    override fun onOrderDetail(historyBean: OrderHistory?) {

        historyBean?.delivered_on = historyBean?.service_date ?: ""

        prefHelper.addGsonValue(DataNames.ORDER_DETAIL, Gson().toJson(historyBean))

        if (AppConstants.APP_SUB_TYPE > AppDataType.Custom.type) {
            prefHelper.setkeyValue(PrefenceConstants.APP_TERMINOLOGY, historyBean?.terminology
                    ?: "")
        }

        val orderId = ArrayList<Int>()

        orderId.add(historyBean?.order_id ?: 0)


        startActivityForResult(Intent(activity, UserTracking::class.java)
                .putExtra(DataNames.REORDER_BUTTON, false).putIntegerArrayListExtra("orderId", orderId), CancelOrder)

    }

    override fun onHelpClick(historyBean: OrderHistory?) {
        if (prefHelper.getCurrentUserLoggedIn()) {

            var intent= Intent(requireActivity(), HelpActivity::class.java)
            startActivity(intent)
        } else {
            appUtils.checkLoginFlow(requireActivity(), DataNames.REQUEST_RESTAURANT_LOGIN)
        }
    }

    private fun onRecyclerViewScrolled() {
        rvOrders.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val isPagingActive = viewModel.validForPaging()
                if (!recyclerView.canScrollVertically(1) && isPagingActive) {
                    callApi(false)
                }
            }
        })
    }

}
