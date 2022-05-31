package com.codebrew.clikat.module.wishlist_prod

import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.codebrew.clikat.BR
import com.codebrew.clikat.R
import com.codebrew.clikat.app_utils.AppToasty
import com.codebrew.clikat.app_utils.AppUtils
import com.codebrew.clikat.app_utils.extension.loadPlaceHolder
import com.codebrew.clikat.app_utils.navController
import com.codebrew.clikat.base.BaseFragment
import com.codebrew.clikat.data.preferences.PreferenceHelper
import com.codebrew.clikat.databinding.FragmentWishListBinding
import com.codebrew.clikat.di.ViewModelProviderFactory
import com.codebrew.clikat.modal.other.SettingModel
import com.codebrew.clikat.modal.other.SupplierDataBean
import com.codebrew.clikat.module.wishlist_prod.adapter.OnItemClicked
import com.codebrew.clikat.module.wishlist_prod.adapter.SuppliersWishListAdapter
import com.codebrew.clikat.preferences.DataNames
import com.codebrew.clikat.utils.Utils
import com.codebrew.clikat.utils.configurations.Configurations
import kotlinx.android.synthetic.main.fragment_wish_list.*
import kotlinx.android.synthetic.main.nothing_found.*
import javax.inject.Inject


class WishlistSuppliersFrag : BaseFragment<FragmentWishListBinding, WishListViewModel>(),
        WishListNavigator, SwipeRefreshLayout.OnRefreshListener, OnItemClicked {

    companion object {
        fun newInstance() = WishlistSuppliersFrag()
    }

    private lateinit var viewModel: WishListViewModel

    @Inject
    lateinit var factory: ViewModelProviderFactory


    private var mBinding: FragmentWishListBinding? = null

    @Inject
    lateinit var perferenceHelper: PreferenceHelper

    @Inject
    lateinit var appUtils: AppUtils

    private var mAdapter: SuppliersWishListAdapter? = null
    val textConfig = Configurations.strings
    var settingBean: SettingModel.DataBean.SettingData? = null
    override fun getBindingVariable(): Int {
        return BR.viewModel
    }

    override fun getLayoutId(): Int {
        return R.layout.fragment_wish_list
    }

    override fun getViewModel(): WishListViewModel {
        viewModel = ViewModelProviders.of(this, factory).get(WishListViewModel::class.java)
        return viewModel
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.navigator = this

        wishlistObserver()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mBinding = viewDataBinding
        mBinding?.color = Configurations.colors
        mBinding?.strings = Configurations.strings
        settingBean = perferenceHelper.getGsonValue(DataNames.SETTING_DATA, SettingModel.DataBean.SettingData::class.java)
        settingLayout()

        Utils.loadAppPlaceholder(settingBean?.favourite_suppliers ?: "")?.let {

            if (it.app?.isNotEmpty() == true)
            {
                ivPlaceholder.loadPlaceHolder(it.app ?: "")
            }

            if (it.message?.isNotEmpty() == true) {
                tvText.text = it.message
            }

        }
    }


    private fun settingLayout() {
        refreshLayout.setOnRefreshListener(this)
        mAdapter = SuppliersWishListAdapter(this, settingBean)
        rv_wishlist.layoutManager = LinearLayoutManager(requireActivity())
        rv_wishlist.adapter = mAdapter

        if (viewModel.supplierWishList.value == null) {
            updateWishList()
        } else {
            mAdapter?.addList(viewModel.supplierWishList.value ?: mutableListOf())
        }
    }


    private fun updateWishList() {
        if (isNetworkConnected) {
            viewModel.getSuppliersWishList()
        }
    }

    private fun wishlistObserver() {
        // Create the observer which updates the UI.
        val catObserver = Observer<MutableList<SupplierDataBean>> { resource ->

            resource?.map {
                it.isOpen = appUtils.checkResturntTiming(it.timing)
            }

            mAdapter?.addList(resource)
        }

        // Observe the LiveData, passing in this activity as the LifecycleOwner and the observer.
        viewModel.supplierWishList.observe(this, catObserver)
    }


    override fun onErrorOccur(message: String) {
        tvText.text = message
    }


    override fun removeProduct(dataItem: SupplierDataBean, position: Int) {
        if (isNetworkConnected)
            viewModel.unFavSupplier(dataItem.supplier_id.toString(), position)
    }


    override fun onRefresh() {
        viewModel.setIsLoading(true)
        refreshLayout.isRefreshing = false
        updateWishList()
    }

    override fun onSessionExpire() {
        openActivityOnTokenExpire()
    }

    override fun onFavStatus(position: Int?) {
        AppToasty.success(requireContext(), getString(R.string.removed_wishlist, textConfig.supplier, textConfig.wishlist))
        mAdapter?.removedProduct(position)

        if (mAdapter?.getList()?.isEmpty() == true) {
            viewModel.supplierWishList.value = mutableListOf()
            viewModel.setWishList(0)
        }
    }

    override fun onItemClicked(dataItem: SupplierDataBean) {

        val bundle = bundleOf("supplierId" to dataItem?.supplier_id,
                "deliveryType" to "pickup", "branchId" to dataItem?.supplier_branch_id,
                "categoryId" to dataItem.category_id,
                "title" to dataItem.name)

        if (settingBean?.show_supplier_detail != null && settingBean?.show_supplier_detail == "1") {
            navController(this@WishlistSuppliersFrag).navigate(R.id.action_supplierDetail, bundle)
        } else if (settingBean?.app_selected_template == "1")
            navController(this@WishlistSuppliersFrag)
                    .navigate(R.id.action_restaurantDetailNew, bundle)
        else
            navController(this@WishlistSuppliersFrag)
                    .navigate(R.id.action_restaurantDetail, bundle)
    }

}
