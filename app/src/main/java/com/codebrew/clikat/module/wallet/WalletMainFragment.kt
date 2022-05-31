package com.codebrew.clikat.module.wallet

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.bumptech.glide.Glide
import com.codebrew.clikat.BR
import com.codebrew.clikat.R
import com.codebrew.clikat.app_utils.AppUtils
import com.codebrew.clikat.app_utils.DateTimeUtils
import com.codebrew.clikat.app_utils.extension.loadPlaceHolder
import com.codebrew.clikat.app_utils.extension.onSnackbar
import com.codebrew.clikat.app_utils.navController
import com.codebrew.clikat.base.BaseFragment
import com.codebrew.clikat.base.PagingResult
import com.codebrew.clikat.data.DataManager
import com.codebrew.clikat.data.constants.AppConstants
import com.codebrew.clikat.data.constants.PrefenceConstants
import com.codebrew.clikat.data.model.api.Currency
import com.codebrew.clikat.databinding.FragmentWalletMainBinding
import com.codebrew.clikat.di.ViewModelProviderFactory
import com.codebrew.clikat.modal.other.SettingModel
import com.codebrew.clikat.modal.wallet.UserDetails
import com.codebrew.clikat.modal.wallet.WalletTransactionsResonse
import com.codebrew.clikat.module.wallet.adapter.WalletHistoryAdapter
import com.codebrew.clikat.preferences.DataNames
import com.codebrew.clikat.utils.Utils
import com.codebrew.clikat.utils.configurations.Configurations
import kotlinx.android.synthetic.main.fragment_wallet_main.*
import kotlinx.android.synthetic.main.nothing_found.*
import javax.inject.Inject


const val ADD_MONEY = 1101

class WalletMainFragment : BaseFragment<FragmentWalletMainBinding, WalletViewModel>(), WalletNavigator, View.OnClickListener {

    private var selectedCurrency: Currency?=null

    @Inject
    lateinit var factory: ViewModelProviderFactory

    private var mBinding: FragmentWalletMainBinding? = null

    private lateinit var viewModel: WalletViewModel
    private lateinit var adapter: WalletHistoryAdapter

    @Inject
    lateinit var appUtils: AppUtils

    @Inject
    lateinit var dateTimeUtils: DateTimeUtils

    @Inject
    lateinit var dataManager: DataManager
    var settingData: SettingModel.DataBean.SettingData? = null

    private val textConfig by lazy { appUtils.loadAppConfig(0).strings }

    override fun getBindingVariable(): Int {
        return BR.viewModel
    }

    override fun getViewModel(): WalletViewModel {
        viewModel = ViewModelProviders.of(this, factory).get(WalletViewModel::class.java)
        return viewModel
    }

    override fun getLayoutId(): Int {
        return R.layout.fragment_wallet_main
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.navigator = this
        settingData = dataManager.getGsonValue(DataNames.SETTING_DATA, SettingModel.DataBean.SettingData::class.java)
        selectedCurrency = dataManager.getGsonValue(PrefenceConstants.CURRENCY_INF, Currency::class.java)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mBinding = viewDataBinding
        Glide.with(this).asGif().load(R.raw.loading_gif).into(mBinding!!.imageLoader);
        viewDataBinding.color = Configurations.colors
        viewDataBinding.strings = Configurations.strings
        viewDataBinding.drawables = Configurations.drawables

        setAdapter()
        listeners()
        if (viewModel.transactions.value == null || arguments?.containsKey("isRefresh") == true)
            hitGetTransactionsApi(true)
        else
            apiResponse(viewModel.transactions.value)

        transactionsObserver()
        recyclerViewScrolling()

        tvTitle?.text = textConfig?.wallet ?: getString(R.string.wallet)

        Utils.loadAppPlaceholder(settingData?.wallet_history ?: "")?.let {

            if (it.app?.isNotEmpty() == true)
            {
                ivPlaceholder.loadPlaceHolder(it.app ?: "")
            }

            if (it.message?.isNotEmpty() == true) {
                tvText.text = it.message
            }

        }
    }

    private fun listeners() {
        ivBack?.setOnClickListener(this)
        tvSendMoneyToSomeone?.setOnClickListener(this)
        ivAddMoney?.setOnClickListener(this)
    }

    private fun hitGetTransactionsApi(isFirstPage: Boolean) {
        if (isNetworkConnected)
            viewModel.getTransactionsList(isFirstPage)
    }

    private fun setAdapter() {
        adapter = WalletHistoryAdapter(settingData,selectedCurrency,dateTimeUtils)
        rvHistory?.adapter = adapter
    }


    private fun transactionsObserver() {
        // Create the observer which updates the UI.
        val catObserver = Observer<PagingResult<WalletTransactionsResonse>> { resource ->
            apiResponse(resource)
        }
        // Observe the LiveData, passing in this activity as the LifecycleOwner and the observer.
        viewModel.transactions.observe(this, catObserver)
    }

    private fun apiResponse(resource: PagingResult<WalletTransactionsResonse>?) {
        val isFirstPage = resource?.isFirstPage

        resource?.result?.data?.transactions?.map {
            val endDate = dateTimeUtils.getDateFormat(it.createdAt
                    ?: "", "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")

            it.formattedDate = Utils.printDifference(DateTimeUtils.currentDate, endDate)
        }

        adapter.addList(isFirstPage, resource?.result?.data?.transactions ?: arrayListOf())

        if (isFirstPage == true)
            setData(resource.result?.data?.userDetails)
    }

    private fun setData(userDetail: UserDetails?) {
        tvTotalBalance?.text = getString(R.string.currency_tag, AppConstants.CURRENCY_SYMBOL, Utils.getPriceFormat(userDetail?.walletAmount
                ?: 0f,settingData,selectedCurrency))
    }

    override fun onMoneySent() {
        //do nothing
    }

    override fun onAddMoneyToWallet() {

    }

    override fun onErrorOccur(message: String) {
        mBinding?.root?.onSnackbar(message)
    }

    override fun onSessionExpire() {
        openActivityOnTokenExpire()
    }


    override fun onClick(p0: View?) {
        when (p0?.id) {
            R.id.ivBack -> navController(this).popBackStack()
            R.id.tvSendMoneyToSomeone -> {
                navController(this@WalletMainFragment).navigate(R.id.action_wallet_send_money)
            }
            R.id.ivAddMoney -> {
              //  startActivityForResult(Intent(requireContext(), WalletAddMoneyActivity::class.java), ADD_MONEY)
            }
        }
    }

    private fun recyclerViewScrolling() {
        mScrollView?.viewTreeObserver?.addOnScrollChangedListener {
            if (mScrollView != null) {
                val view = mScrollView.getChildAt(mScrollView.childCount - 1) as View
                val diff = view.bottom - (mScrollView.height + mScrollView
                        .scrollY)
                if (diff == 0 && viewModel.validForPaging()) {
                    // your pagination code
                    hitGetTransactionsApi(false)
                }
            }

        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && requestCode == ADD_MONEY) {
            hitGetTransactionsApi(true)
        }
    }
}