package com.codebrew.clikat.module.requestsLists

import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.RecyclerView
import com.codebrew.clikat.BR
import com.codebrew.clikat.R
import com.codebrew.clikat.app_utils.AppUtils
import com.codebrew.clikat.app_utils.DateTimeUtils
import com.codebrew.clikat.app_utils.DialogsUtil
import com.codebrew.clikat.app_utils.extension.loadPlaceHolder
import com.codebrew.clikat.app_utils.extension.onSnackbar
import com.codebrew.clikat.app_utils.navController
import com.codebrew.clikat.base.BaseFragment
import com.codebrew.clikat.base.PagingResult
import com.codebrew.clikat.data.DataManager
import com.codebrew.clikat.data.RequestsStatus
import com.codebrew.clikat.data.model.api.RequestData
import com.codebrew.clikat.data.model.api.RequestItem
import com.codebrew.clikat.databinding.FragmentRequestsListBinding
import com.codebrew.clikat.di.ViewModelProviderFactory
import com.codebrew.clikat.modal.other.SettingModel
import com.codebrew.clikat.module.product.product_listing.DialogListener
import com.codebrew.clikat.module.requestsLists.adapter.RequestsListAdapter
import com.codebrew.clikat.preferences.DataNames
import com.codebrew.clikat.utils.Utils
import com.codebrew.clikat.utils.configurations.Configurations
import kotlinx.android.synthetic.main.fragment_notification.refreshLayout
import kotlinx.android.synthetic.main.fragment_requests_list.*
import kotlinx.android.synthetic.main.nothing_found.*
import kotlinx.android.synthetic.main.toolbar_app.*
import javax.inject.Inject

class RequestListFragment : BaseFragment<FragmentRequestsListBinding, RequestsViewModel>(), RequestsNavigator, RequestsListAdapter.OnRequestClicked, DialogListener {


    private lateinit var adapter: RequestsListAdapter
    private lateinit var viewModel: RequestsViewModel
    private lateinit var mBinding: FragmentRequestsListBinding


    private var requestItem: RequestData? = null
    private var currentPos: Int? = null

    @Inject
    lateinit var dateTimeUtils: DateTimeUtils

    @Inject
    lateinit var factory: ViewModelProviderFactory


    @Inject
    lateinit var mDialogUtils: DialogsUtil

    @Inject
    lateinit var appUtils: AppUtils

    @Inject
    lateinit var dataManager: DataManager

    val textConfig by lazy { appUtils.loadAppConfig(0).strings }

    private var clientInform: SettingModel.DataBean.SettingData? = null

    override fun getBindingVariable(): Int {
        return BR.viewModel
    }

    override fun getLayoutId(): Int {
        return R.layout.fragment_requests_list
    }

    override fun getViewModel(): RequestsViewModel {
        viewModel = ViewModelProviders.of(this, factory).get(RequestsViewModel::class.java)
        return viewModel
    }

    override fun onErrorOccur(message: String) {
        mBinding.root.onSnackbar(message)
    }

    override fun onSessionExpire() {
        openActivityOnTokenExpire()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.navigator = this

        clientInform = dataManager.getGsonValue(DataNames.SETTING_DATA, SettingModel.DataBean.SettingData::class.java)

        requestsObserver()
        cancelObserver()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initialise()
        listeners()
        setAdapter()
        hitApi(true)

        Utils.loadAppPlaceholder(clientInform?.prescription_requests_listing ?: "")?.let {

            if (it.app?.isNotEmpty() == true)
            {
                ivPlaceholder.loadPlaceHolder(it.app ?: "")
            }

            if (it.message?.isNotEmpty() == true) {
                tvText.text = it.message
            }

        }
    }


    private fun initialise() {
        mBinding = viewDataBinding
        mBinding.color = Configurations.colors
        mBinding.drawables = Configurations.drawables
        mBinding.strings = textConfig

        tb_title.text = getString(R.string.requests, textConfig?.order)
        tb_back.setOnClickListener { navController(this@RequestListFragment).popBackStack() }
    }

    private fun listeners() {
        refreshLayout.setOnRefreshListener {
            refreshLayout.isRefreshing = false
            hitApi(true)
        }
    }

    private fun hitApi(firstPage: Boolean) {
        if (isNetworkConnected) {
            viewModel.getRequestsList(firstPage)
        }
    }

    private fun setAdapter() {
        adapter = RequestsListAdapter(this)
        rvRequests?.adapter = adapter
        onRecyclerViewScrolled()
    }

    private fun requestsObserver() {
        // Create the observer which updates the UI.
        val listObserver = Observer<PagingResult<RequestItem>> { resource ->

            if (resource?.result?.data?.isNotEmpty() == true) {
                resource.result.data.map {
                    it.created_at = dateTimeUtils.convertDateOneToAnother(it.created_at
                            , "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", "MMM dd EEE hh:mm a") ?: ""

                    it.updated_status = when (it.status) {
                        RequestsStatus.Approved.status -> "Approved"
                        RequestsStatus.AdminRejected.status -> "Admin Rejected"
                        RequestsStatus.UserCancelled.status -> "Cancelled by User"
                        RequestsStatus.Cancelled.status -> "Cancelled"
                        else -> "Pending"
                    }

                    it.cancelBtn = it.status == RequestsStatus.Pending.status || it.status == RequestsStatus.Approved.status
                }
            }


            val isFirstPage = resource.isFirstPage
            adapter.addList(isFirstPage, resource?.result?.data ?: arrayListOf())
        }

        // Observe the LiveData, passing in this activity as the LifecycleOwner and the observer.
        viewModel.requestsList.observe(this, listObserver)
    }

    private fun cancelObserver() {
        val cancelObserver = Observer<Int> { resource ->
            // adapter.changeStatus(resource)
            if (isNetworkConnected) {
                hitApi(true)
            }
        }
        viewModel.cancelRequest.observe(this, cancelObserver)
    }

    override fun onItemCancelled(item: RequestData?, adapterPos: Int) {

        requestItem = item
        currentPos = adapterPos

        mDialogUtils.openAlertDialog(activity ?: requireContext(),
                activity?.getString(R.string.are_you_sure_cancel_request) ?: "", "Yes", "No", this)
    }

    private fun onRecyclerViewScrolled() {
        rvRequests.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val isPagingActive = viewModel.validForPaging()
                if (!recyclerView.canScrollVertically(1) && isPagingActive) {
                    hitApi(false)
                }
            }
        })
    }

    override fun onSucessListner() {
        if (isNetworkConnected)
            viewModel.cancelRequest(requestItem?.id.toString(), "", currentPos ?: 0)
    }

    override fun onErrorListener() {

    }

}