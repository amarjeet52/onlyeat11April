package com.codebrew.clikat.module.completed_order

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.RecyclerView
import com.codebrew.clikat.R
import com.codebrew.clikat.base.PagingResult
import com.codebrew.clikat.data.constants.PrefenceConstants
import com.codebrew.clikat.data.model.api.Currency
import com.codebrew.clikat.data.model.api.orderDetail.OrderHistory
import com.codebrew.clikat.modal.other.ProductDataBean
import com.codebrew.clikat.modal.other.SettingModel
import com.codebrew.clikat.module.base_orders.BaseOrderFragment
import com.codebrew.clikat.module.completed_order.adapter.OrderHistoryAdapter
import com.codebrew.clikat.module.help.HelpActivity
import com.codebrew.clikat.module.order_detail.OrderDetailActivity
import com.codebrew.clikat.module.user_tracking.UserTracking
import com.codebrew.clikat.preferences.DataNames
import kotlinx.android.synthetic.main.fragment_base_order.*
import java.util.*

/*
 * Created by Ankit Jindal on 20/4/16.
 */
class OrderHistoryFargment : BaseOrderFragment(), OrderHistoryAdapter.OrderHisCallback {


    private var orderHistoryAdapter: OrderHistoryAdapter? = null
    private var screenFlowBean: SettingModel.DataBean.ScreenFlowBean? = null
    private var orderHistory2List: MutableList<OrderHistory> = mutableListOf()

    companion object {
        fun newInstance() = OrderHistoryFargment()
    }

    override fun onResume() {
        super.onResume()

        callApi(true)
    }

    private fun callApi(firstPage: Boolean) {

        if (isNetworkConnected) {
/*            if (viewModel.historyLiveData.value != null) {
                if (viewModel.historyLiveData.value?.isFirstPage == true) {
                    orderHistory2List.clear()
                }
                updateHistoryData(viewModel.historyLiveData.value?.result)
            } else {*/
            if (prefHelper.getCurrentUserLoggedIn()) {
                viewModel.getOrderHistory(firstPage)
            }
            //  }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.navigator = this

        orderObserver()
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
         val selectedCurrency = prefHelper.getGsonValue(PrefenceConstants.CURRENCY_INF, Currency::class.java)
         screenFlowBean = prefHelper.getGsonValue(DataNames.SCREEN_FLOW, SettingModel.DataBean.ScreenFlowBean::class.java)
        val settingsData = prefHelper.getGsonValue(DataNames.SETTING_DATA, SettingModel.DataBean.SettingData::class.java)

        orderHistoryAdapter = OrderHistoryAdapter(activity
                ?: requireContext(), orderHistory2List, appUtils,settingsData,screenFlowBean,selectedCurrency)

        onRecyclerViewScrolled()
    }

    private fun orderObserver() {
        // Create the observer which updates the UI.
        val catObserver = Observer<PagingResult<MutableList<OrderHistory>>> { resource ->

            if (resource.isFirstPage) {
                orderHistory2List.clear()
            }
            updateHistoryData(resource)
        }

        // Observe the LiveData, passing in this activity as the LifecycleOwner and the observer.
        viewModel.historyLiveData.observe(this, catObserver)
    }

    private fun updateHistoryData(resource: PagingResult<MutableList<OrderHistory>>) {

        orderHistory2List.addAll(resource.result ?: emptyList())

        resource.result?.map {
            it.product_count=calculateProdAddon(it.product)?.sumBy { it?.prod_quantity?.toInt()?:0 }
        }

        if (resource.isFirstPage) {
            orderHistoryAdapter?.settingCallback(this)
            setAdapter(orderHistoryAdapter, resources.getString(R.string.order_history, textConfig?.order), getString(R.string.no_order_found))
        } else {
            orderHistoryAdapter?.notifyDataSetChanged()
        }

    }


    override fun onDestroyView() {
        super.onDestroyView()
        viewModel.compositeDisposable.clear()
    }

    override fun refreshData() {
        callApi(true)
    }


    override fun addToCart() {
        //GeneralFunctions.showSnackBar(view, "Item Added into Cart", activity)
    }


    override fun reOrder(orderId: ArrayList<Int>?) {
        startActivityForResult(Intent(activity, UserTracking::class.java)
                .putExtra(DataNames.REORDER_BUTTON, false).putIntegerArrayListExtra("orderId", orderId), DataNames.REQUEST_REORDER)
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
