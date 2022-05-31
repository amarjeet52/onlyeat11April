package com.codebrew.clikat.module.cart.promocode

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.RecyclerView
import com.codebrew.clikat.BR
import com.codebrew.clikat.R
import com.codebrew.clikat.app_utils.DateTimeUtils
import com.codebrew.clikat.app_utils.extension.loadPlaceHolder
import com.codebrew.clikat.app_utils.extension.onSnackbar
import com.codebrew.clikat.base.BaseActivity
import com.codebrew.clikat.base.PagingResult
import com.codebrew.clikat.data.DataManager
import com.codebrew.clikat.data.model.others.PromoCodeItem
import com.codebrew.clikat.data.model.others.PromoCodeResponse
import com.codebrew.clikat.databinding.FragmentPromoCodesBinding
import com.codebrew.clikat.di.ViewModelProviderFactory
import com.codebrew.clikat.modal.other.SettingModel
import com.codebrew.clikat.preferences.DataNames
import com.codebrew.clikat.utils.Utils
import com.codebrew.clikat.utils.configurations.Configurations
import kotlinx.android.synthetic.main.fragment_notification.*
import kotlinx.android.synthetic.main.nothing_found.*
import kotlinx.android.synthetic.main.toolbar_app.*
import javax.inject.Inject

class PromoCodeListActivity : BaseActivity<FragmentPromoCodesBinding, PromoCodeViewModel>(),
        PromoCodeNavigator, PromoCodeAdapter.OnPromoCodeClicked {


    private lateinit var adapter: PromoCodeAdapter
    private lateinit var viewModel: PromoCodeViewModel
    private lateinit var mBinding: FragmentPromoCodesBinding
    private var supplierIds = ArrayList<String>()

    @Inject
    lateinit var factory: ViewModelProviderFactory

    @Inject
    lateinit var dateTime: DateTimeUtils

    @Inject
    lateinit var dataManager: DataManager

    private var settingData: SettingModel.DataBean.SettingData? = null

    override fun getBindingVariable(): Int {
        return BR.viewModel
    }

    override fun getLayoutId(): Int {
        return R.layout.fragment_promo_codes
    }

    override fun getViewModel(): PromoCodeViewModel {
        viewModel = ViewModelProviders.of(this, factory).get(PromoCodeViewModel::class.java)
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

        settingData = dataManager.getGsonValue(DataNames.SETTING_DATA, SettingModel.DataBean.SettingData::class.java)

        initialise()
        listeners()
        setAdapter()
        agentListObserver()
        hitApi(true)
    }


    private fun listeners() {
        refreshLayout.setOnRefreshListener {
            refreshLayout.isRefreshing = false
            hitApi(true)
        }
    }

    private fun hitApi(isFirstPage: Boolean) {
        if (isNetworkConnected) {
            viewModel.getPromoCodesList(isFirstPage, supplierIds)
        }
    }

    private fun initialise() {
        mBinding = viewDataBinding
        mBinding.color = Configurations.colors
        mBinding.drawables = Configurations.drawables
        mBinding.strings = Configurations.strings

        tb_title.text = getString(R.string.promo_codes)
        tb_back.setOnClickListener { v: View? -> onBackPressed() }
        if (intent != null && intent?.hasExtra("supplierIds") == true)
            supplierIds = intent?.getStringArrayListExtra("supplierIds") ?: arrayListOf()


        Utils.loadAppPlaceholder(settingData?.promo_code_listing ?: "")?.let {

            if (it.app?.isNotEmpty() == true)
            {
                ivPlaceholder.loadPlaceHolder(it.app)
            }

            if (it.message?.isNotEmpty() == true) {
                tvText.text = it.message
            }

        }
    }

    private fun setAdapter() {
        val showApplyButton= intent?.hasExtra("showApply") != true
        adapter = PromoCodeAdapter(this,dateTime = dateTime, showApply = showApplyButton)
        rvNotification?.adapter = adapter
        onRecyclerViewScrolled()

    }

    private fun agentListObserver() {
        // Create the observer which updates the UI.
        val catObserver = Observer<PagingResult<PromoCodeResponse>> { resource ->
            val isFirstPage = resource?.isFirstPage
            adapter.addList(isFirstPage, resource?.result?.data?.list ?: arrayListOf())
        }
        // Observe the LiveData, passing in this activity as the LifecycleOwner and the observer.
        viewModel.promoCodes.observe(this, catObserver)
    }


    override fun onItemClicked(item: PromoCodeItem?) {
        val intent = Intent()
        intent.putExtra("promoCode", item)
        setResult(Activity.RESULT_OK, intent)
        finish()
    }


    private fun onRecyclerViewScrolled() {
        rvNotification.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val isPagingActive = viewModel.validForPaging()
                if (!recyclerView.canScrollVertically(1) && isPagingActive) {
                    hitApi(false)
                }
            }
        })
    }
}