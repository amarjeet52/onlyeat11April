package com.codebrew.clikat.module.banners

import android.os.Bundle
import android.os.Parcelable
import android.view.View
import androidx.core.os.bundleOf
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.codebrew.clikat.BR
import com.codebrew.clikat.R
import com.codebrew.clikat.app_utils.AppUtils
import com.codebrew.clikat.app_utils.ProdUtils
import com.codebrew.clikat.app_utils.extension.onSnackbar
import com.codebrew.clikat.app_utils.navController
import com.codebrew.clikat.base.BaseFragment
import com.codebrew.clikat.data.AppDataType
import com.codebrew.clikat.data.BannerRedirection
import com.codebrew.clikat.data.DataManager
import com.codebrew.clikat.data.VendorAppType
import com.codebrew.clikat.data.model.api.Value
import com.codebrew.clikat.data.model.api.supplier_detail.DataSupplierDetail
import com.codebrew.clikat.databinding.FragmentOffersBinding
import com.codebrew.clikat.di.ViewModelProviderFactory
import com.codebrew.clikat.modal.other.SettingModel
import com.codebrew.clikat.modal.other.SupplierDataBean
import com.codebrew.clikat.modal.other.TopBanner
import com.codebrew.clikat.module.banners.adapter.BannersAdapter
import com.codebrew.clikat.module.banners.adapter.ItemListener
import com.codebrew.clikat.preferences.DataNames
import com.codebrew.clikat.utils.configurations.Configurations
import kotlinx.android.synthetic.main.fragment_offers.*
import javax.inject.Inject


class BannersListingFragment : BaseFragment<FragmentOffersBinding, BannersListViewModel>(), BannersListNavigator {


    private lateinit var adapter: BannersAdapter

    @Inject
    lateinit var dataManager: DataManager

    @Inject
    lateinit var appUtils: AppUtils


    @Inject
    lateinit var prodUtils: ProdUtils

    @Inject
    lateinit var factory: ViewModelProviderFactory

    private var mBinding: FragmentOffersBinding? = null

    private lateinit var viewModel: BannersListViewModel

    private var screenFlowBean: SettingModel.DataBean.ScreenFlowBean? = null
    private var bookingFlowBean: SettingModel.DataBean.BookingFlowBean? = null

    private val textConfig by lazy { appUtils.loadAppConfig(0).strings }
    private var clientInform: SettingModel.DataBean.SettingData? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.navigator = this

        screenFlowBean = dataManager.getGsonValue(DataNames.SCREEN_FLOW, SettingModel.DataBean.ScreenFlowBean::class.java)
        bookingFlowBean = dataManager.getGsonValue(DataNames.BOOKING_FLOW, SettingModel.DataBean.BookingFlowBean::class.java)
        clientInform = dataManager.getGsonValue(DataNames.SETTING_DATA, SettingModel.DataBean.SettingData::class.java)
        bannersObserver()
    }


    override fun getBindingVariable(): Int {
        return BR.viewModel
    }

    override fun getLayoutId(): Int {
        return R.layout.fragment_offers
    }

    override fun getViewModel(): BannersListViewModel {
        viewModel = ViewModelProviders.of(this, factory).get(BannersListViewModel::class.java)
        return viewModel
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mBinding = viewDataBinding
        mBinding?.color= Configurations.colors
        mBinding?.drawables=Configurations.drawables

        listeners()
        setAdapter()
        hitApi()
    }

    private fun hitApi() {
        if(isNetworkConnected)
            viewModel.getBannersList()
    }

    private fun listeners() {
        ivBack?.setOnClickListener {
            navController(this).popBackStack()
        }
    }

    private fun setAdapter() {
        adapter = BannersAdapter()
        recyclerView?.adapter = adapter
        adapter.settingCallback(ItemListener {
             onBannerDetail(it)
        })
    }

    private fun onBannerDetail(bannerBean: TopBanner?) {
        if (bannerBean?.flow_banner_type == BannerRedirection.NoRedirection.type || (screenFlowBean?.is_single_vendor == VendorAppType.Single.appType
                        && screenFlowBean?.app_type != AppDataType.HomeServ.type)) return

        val bundle = bundleOf("supplierId" to bannerBean?.supplier_id,
                "title" to bannerBean?.name,
                "parent_category" to bannerBean?.category_id,
                "subCategoryId" to 0,
                "categoryId" to bannerBean?.category_id)

        when (bannerBean?.flow_banner_type) {
            BannerRedirection.ForSubscription.type -> {
                if (clientInform?.is_user_subscription == "1")
                    navController(this@BannersListingFragment).navigate(R.id.action_home_to_subscriptionFrag)
            }

            BannerRedirection.ForCategory.type -> {
                if (bannerBean.is_subcategory == 1 && screenFlowBean?.app_type != AppDataType.Ecom.type) {

                    if (screenFlowBean?.app_type == AppDataType.Food.type) {
                         if (clientInform?.is_skip_theme == "1") {
                            navController(this@BannersListingFragment).navigate(R.id.action_bannersFrag_to_supplierListFragment, bundle)
                        }else
                        navController(this@BannersListingFragment)
                                .navigate(R.id.action_supplierAll, bundle)
                    } else {
                        navController(this@BannersListingFragment).navigate(R.id.actionSubCategory, bundle)
                    }
                } else {
                    navController(this@BannersListingFragment).navigate(R.id.action_productListing, bundle)
                }
            }
            else -> {
                if (isNetworkConnected) {
                    viewModel.fetchSupplierDetail(bannerBean?.branch_id
                            ?: 0, bannerBean?.category_id
                            ?: 0, bannerBean?.supplier_id)
                }
            }
        }
    }
    override fun supplierDetailSuccess(data: DataSupplierDetail) {

        val bundle = bundleOf("supplierId" to data.supplier_id,
                "title" to data.name,
                "branchId" to data.branchId,
                "categoryId" to data.category_id)

        if (screenFlowBean?.app_type == AppDataType.Food.type) {
            if (clientInform?.app_selected_template != null && clientInform?.app_selected_template == "1") {
                navController(this@BannersListingFragment).navigate(R.id.action_restaurantDetailNew, bundle)
            } else {
                navController(this@BannersListingFragment).navigate(R.id.action_restaurantDetail, bundle)
            }
        } else {
            if (!data.category.isNullOrEmpty()) {
                /*bundle.putBoolean("is_supplier", true)
                bundle.putParcelable("supplierData", data)*/
                bundle.putParcelableArrayList("subcategory", ArrayList<Parcelable>(data.category
                        ?: mutableListOf()))

                if (clientInform?.is_supplier_detail == "1") {
                    if (clientInform?.show_ecom_v2_theme == "1") {
                        navController(this@BannersListingFragment)
                                .navigate(R.id.action_supplierDetail_v2, bundle)
                    } else {
                        navController(this@BannersListingFragment)
                                .navigate(R.id.action_supplierDetail, bundle)
                    }
                } else {
                    navController(this@BannersListingFragment)
                            .navigate(R.id.actionSubCategory, bundle)
                }

            } else {
                navController(this@BannersListingFragment).navigate(R.id.action_productListing, bundle)
            }

        }
    }
    private fun bannersObserver() {
        // Create the observer which updates the UI.
        val catObserver = Observer<List<TopBanner>> { resource ->
            adapter.submitItemList(resource)
        }

        // Observe the LiveData, passing in this activity as the LifecycleOwner and the observer.
        viewModel.bannersLiveData.observe(this, catObserver)
    }

    override fun onErrorOccur(message: String) {
        mBinding?.root?.onSnackbar(message)
    }

    override fun onSessionExpire() {
        openActivityOnTokenExpire()
    }

}