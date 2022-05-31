package com.codebrew.clikat.module.base_orders

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import com.codebrew.clikat.BR
import com.codebrew.clikat.R
import com.codebrew.clikat.app_utils.AppUtils
import com.codebrew.clikat.app_utils.extension.onSnackbar
import com.codebrew.clikat.base.BaseFragment
import com.codebrew.clikat.base.PagingResult
import com.codebrew.clikat.data.constants.PrefenceConstants
import com.codebrew.clikat.data.model.api.Currency
import com.codebrew.clikat.data.model.api.orderDetail.OrderHistory
import com.codebrew.clikat.data.preferences.PreferenceHelper
import com.codebrew.clikat.databinding.FragmentSkipMyOrdersBinding
import com.codebrew.clikat.di.ViewModelProviderFactory
import com.codebrew.clikat.modal.other.SettingModel
import com.codebrew.clikat.module.completed_order.adapter.OrderHistoryAdapter
import com.codebrew.clikat.module.order_detail.OrderDetailActivity
import com.codebrew.clikat.module.user_tracking.UserTracking
import com.codebrew.clikat.preferences.DataNames
import com.codebrew.clikat.utils.configurations.Configurations
import kotlinx.android.synthetic.main.fragment_skip_my_orders.*
import javax.inject.Inject

class SkipOrdersFragment : BaseFragment<FragmentSkipMyOrdersBinding, BaseOrderViewModel>(), BaseOrderNavigator, OrderHistoryAdapter.OrderHisCallback {

    private var selectedCurrency: Currency?=null
    private var screenFlowBean: SettingModel.DataBean.ScreenFlowBean? = null
    private var settingsData: SettingModel.DataBean.SettingData? = null

    @Inject
    lateinit var factory: ViewModelProviderFactory

    @Inject
    lateinit var prefHelper: PreferenceHelper

    private var mBinding: FragmentSkipMyOrdersBinding? = null
    private lateinit var viewModel: BaseOrderViewModel
    private var orderHistoryAdapter: OrderHistoryAdapter? = null
    private var orderHistory2List: MutableList<OrderHistory> = mutableListOf()

    @Inject
    lateinit var appUtils: AppUtils

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.navigator = this
        orderObserver()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mBinding = viewDataBinding

        mBinding?.color = Configurations.colors
        mBinding?.strings = Configurations.strings
        mBinding?.drawables = Configurations.drawables
        initialise()
        listeners()
        setAdapter()
        callApi(true)
    }

    private fun listeners() {
        ivBack?.setOnClickListener {
            Navigation.findNavController(requireView()).popBackStack()
        }
    }

    private fun initialise() {
        screenFlowBean = prefHelper.getGsonValue(DataNames.SCREEN_FLOW, SettingModel.DataBean.ScreenFlowBean::class.java)
        settingsData = prefHelper.getGsonValue(DataNames.SETTING_DATA, SettingModel.DataBean.SettingData::class.java)
        selectedCurrency = prefHelper.getGsonValue(PrefenceConstants.CURRENCY_INF, Currency::class.java)
    }

    private fun setAdapter() {
        orderHistoryAdapter = OrderHistoryAdapter(activity
                ?: requireContext(), orderHistory2List, appUtils, settingsData, screenFlowBean,selectedCurrency)
        recyclerView?.adapter = orderHistoryAdapter

        orderHistoryAdapter?.settingCallback(this)
        onRecyclerViewScrolled()
    }

    override fun getBindingVariable(): Int {
        return BR.viewModel
    }

    override fun getLayoutId(): Int {
        return R.layout.fragment_skip_my_orders
    }

    override fun refreshData() {
        //do nothing
    }

    override fun getViewModel(): BaseOrderViewModel {
        viewModel = ViewModelProviders.of(this, factory).get(BaseOrderViewModel::class.java)
        return viewModel
    }

    private fun callApi(firstPage: Boolean) {
        if (isNetworkConnected) {
            if (prefHelper.getCurrentUserLoggedIn()) {
                viewModel.getOrderHistory(firstPage)
            }
        }
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
        if (resource.isFirstPage)
            orderHistory2List.clear()

        orderHistory2List.addAll(resource.result ?: emptyList())
        orderHistoryAdapter?.notifyDataSetChanged()
    }

    private fun onRecyclerViewScrolled() {
        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val isPagingActive = viewModel.validForPaging()
                if (!recyclerView.canScrollVertically(1) && isPagingActive) {
                    callApi(false)
                }
            }
        })
    }

    override fun addToCart() {

    }


    override fun reOrder(orderId: ArrayList<Int>?) {
        startActivityForResult(Intent(activity, UserTracking::class.java)
                .putExtra(DataNames.REORDER_BUTTON, false).putIntegerArrayListExtra("orderId", orderId), DataNames.REQUEST_REORDER)
    }

    override fun onErrorOccur(message: String) {
        mBinding?.root?.onSnackbar(message)
    }


    override fun onSessionExpire() {
        openActivityOnTokenExpire()
    }
}