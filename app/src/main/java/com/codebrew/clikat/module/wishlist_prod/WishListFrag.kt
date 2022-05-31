package com.codebrew.clikat.module.wishlist_prod


import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.GridLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.codebrew.clikat.BR
import com.codebrew.clikat.R
import com.codebrew.clikat.app_utils.AppToasty
import com.codebrew.clikat.app_utils.extension.loadPlaceHolder
import com.codebrew.clikat.app_utils.navController
import com.codebrew.clikat.base.BaseFragment
import com.codebrew.clikat.data.constants.PrefenceConstants
import com.codebrew.clikat.data.model.api.Currency
import com.codebrew.clikat.data.preferences.PreferenceHelper
import com.codebrew.clikat.databinding.FragmentWishListBinding
import com.codebrew.clikat.di.ViewModelProviderFactory
import com.codebrew.clikat.modal.other.ProductDataBean
import com.codebrew.clikat.modal.other.SettingModel
import com.codebrew.clikat.module.product_detail.ProductDetails
import com.codebrew.clikat.module.wishlist_prod.adapter.WishlistAdapter
import com.codebrew.clikat.preferences.DataNames
import com.codebrew.clikat.utils.Utils
import com.codebrew.clikat.utils.configurations.Configurations
import kotlinx.android.synthetic.main.fragment_wish_list.*
import kotlinx.android.synthetic.main.nothing_found.*
import javax.inject.Inject


class WishListFrag : BaseFragment<FragmentWishListBinding, WishListViewModel>(),
        WishListNavigator, WishlistAdapter.WishCallback, SwipeRefreshLayout.OnRefreshListener {


    companion object {
        fun newInstance() = WishListFrag()
    }

    private lateinit var viewModel: WishListViewModel
    private var selectedCurrency: Currency?=null
    @Inject
    lateinit var factory: ViewModelProviderFactory

    private var mBinding: FragmentWishListBinding? = null

    @Inject
    lateinit var perferenceHelper: PreferenceHelper

    private var mAdapter: WishlistAdapter? = null
    private val textConfig = Configurations.strings

    private var clientInform: SettingModel.DataBean.SettingData? = null

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

        clientInform = perferenceHelper.getGsonValue(DataNames.SETTING_DATA, SettingModel.DataBean.SettingData::class.java)
        selectedCurrency = perferenceHelper.getGsonValue(PrefenceConstants.CURRENCY_INF, Currency::class.java)
        wishlistObserver()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mBinding = viewDataBinding
        mBinding?.color = Configurations.colors
        mBinding?.strings = Configurations.strings

        settingLayout()

        Utils.loadAppPlaceholder(clientInform?.favourite_product_listing ?: "")?.let {

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

        val bookingFlowBean = perferenceHelper.getGsonValue(DataNames.BOOKING_FLOW, SettingModel.DataBean.BookingFlowBean::class.java)
        val screenFlowBean = perferenceHelper.getGsonValue(DataNames.SCREEN_FLOW, SettingModel.DataBean.ScreenFlowBean::class.java)

        mAdapter = WishlistAdapter(bookingFlowBean, screenFlowBean,clientInform,selectedCurrency)
        mAdapter?.settingCallback(this)

        rv_wishlist.layoutManager = GridLayoutManager(activity, 2)

        rv_wishlist.adapter = mAdapter

        if (viewModel.wishListLiveData.value == null) {
            updateWishlist()
        } else {
            mAdapter?.updateList(viewModel.wishListLiveData.value)
        }
    }

    private fun updateWishlist() {
        if (isNetworkConnected) {
            viewModel.getWishlist()
        }
    }

    private fun wishlistObserver() {
        // Create the observer which updates the UI.
        val catObserver = Observer<MutableList<ProductDataBean>> { resource ->
            mAdapter?.updateList(resource)
        }

        // Observe the LiveData, passing in this activity as the LifecycleOwner and the observer.
        viewModel.wishListLiveData.observe(this, catObserver)
    }


    override fun onErrorOccur(message: String) {
        tvText.text = message
    }


    override fun productDetail(bean: ProductDataBean?) {

        if (clientInform?.isProductDetailCheck != null && clientInform?.isProductDetailCheck == "1") return


        if(clientInform?.show_ecom_v2_theme == "1"){
            navController(this@WishListFrag).navigate(R.id.action_wishListFrag_to_productDetailsV2,
                    ProductDetails.newInstance(bean, 0, false))
        } else {
            navController(this@WishListFrag).navigate(R.id.action_wishListFrag_to_productDetails,
                    ProductDetails.newInstance(bean, 0, false))
        }


    }

    override fun removeProduct(productId: Int?, favStatus: Int?, position: Int?) {
        if (isNetworkConnected)
            viewModel.markFavProduct(productId, favStatus, position ?: 0)
    }

    override fun onRefresh() {
        viewModel.setIsLoading(true)
        refreshLayout.isRefreshing = false
        updateWishlist()
    }

    override fun onSessionExpire() {
        openActivityOnTokenExpire()
    }

    override fun onFavStatus(position: Int?) {
        AppToasty.success(requireContext(), getString(R.string.removed_wishlist, textConfig.product, textConfig.wishlist))
        mAdapter?.removedProduct(position)

        if (mAdapter?.getList()?.isEmpty() == true) {
            viewModel.wishListLiveData.value = mutableListOf()
            viewModel.setWishList(0)
        }
    }

}
