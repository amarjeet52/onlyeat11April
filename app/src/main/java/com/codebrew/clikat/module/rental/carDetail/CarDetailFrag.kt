package com.codebrew.clikat.module.rental.carDetail

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.codebrew.clikat.BR
import com.codebrew.clikat.R
import com.codebrew.clikat.adapters.PagerImageAdapter
import com.codebrew.clikat.app_utils.AppUtils
import com.codebrew.clikat.app_utils.CommonUtils
import com.codebrew.clikat.app_utils.extension.launchActivity
import com.codebrew.clikat.app_utils.extension.onSnackbar
import com.codebrew.clikat.app_utils.navController
import com.codebrew.clikat.base.BaseFragment
import com.codebrew.clikat.data.constants.AppConstants
import com.codebrew.clikat.data.constants.PrefenceConstants
import com.codebrew.clikat.data.model.others.HomeRentalParam
import com.codebrew.clikat.data.preferences.PreferenceHelper
import com.codebrew.clikat.databinding.FragmentCarDetailBinding
import com.codebrew.clikat.di.ViewModelProviderFactory
import com.codebrew.clikat.modal.CartInfoServer
import com.codebrew.clikat.modal.other.AddtoCartModel
import com.codebrew.clikat.modal.other.ProductDataBean
import com.codebrew.clikat.modal.other.SupplierServiceModel
import com.codebrew.clikat.module.login.LoginActivity
import com.codebrew.clikat.preferences.DataNames
import com.codebrew.clikat.utils.StaticFunction
import com.codebrew.clikat.utils.configurations.Configurations
import kotlinx.android.synthetic.main.content_car_detail.*
import kotlinx.android.synthetic.main.fragment_car_detail.*
import javax.inject.Inject

class CarDetailFrag : BaseFragment<FragmentCarDetailBinding, CarDetailViewModel>(), CarDetailNavigator {

    private var mServiceList = mutableListOf<SupplierServiceModel>()

    @Inject
    lateinit var factory: ViewModelProviderFactory

    @Inject
    lateinit var prefHelper: PreferenceHelper

    @Inject
    lateinit var appUtil: AppUtils


    private lateinit var viewModel: CarDetailViewModel
    private var mBinding: FragmentCarDetailBinding? = null

    // lateinit var serviceAdapter: SupplierServiceAdapter

    private var intervalType: String = ""
    private var intervalTime: Int = 0

    var price: Float = 0.0f

    var favStatus: String = ""

    lateinit var mHomeParam: HomeRentalParam

    override fun getBindingVariable(): Int {
        return BR.viewModel
    }

    override fun getLayoutId(): Int {
        return R.layout.fragment_car_detail
    }

    override fun getViewModel(): CarDetailViewModel {
        viewModel = ViewModelProviders.of(this, factory).get(CarDetailViewModel::class.java)
        return viewModel
    }

    override fun updateCart() {

        if (!::mHomeParam.isInitialized) return


        val bundle = bundleOf("rentalParam" to mHomeParam)
        navController(this@CarDetailFrag)
                .navigate(R.id.action_carDetailFrag_to_rentalCheckoutFrag, bundle)
    }

    override fun addCart(cartdata: AddtoCartModel.CartdataBean) {
        if (isNetworkConnected) {
            if (!::mHomeParam.isInitialized) return

            mHomeParam.totalAmt = price.toString()
            mHomeParam.cartId = cartdata.cartId ?: ""
            viewModel.updateCartInfo(mHomeParam)
        }
    }

    override fun onFavStatus() {

        if (favStatus == "0") {
            tb_favourite.setImageResource(R.drawable.ic_unfavorite)
            mBinding?.root?.onSnackbar(getString(R.string.successUnFavourite))
        } else {
            tb_favourite.setImageResource(R.drawable.ic_favourite)
            mBinding?.root?.onSnackbar(getString(R.string.successFavourite))
        }
    }

    override fun onErrorOccur(message: String) {
        mBinding?.root?.onSnackbar(message)
    }

    override fun onSessionExpire() {
        openActivityOnTokenExpire()
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel.navigator = this
        productDetailObserver()
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mBinding = viewDataBinding
        viewDataBinding.color = Configurations.colors

        /*       serviceAdapter = SupplierServiceAdapter(mServiceList)
               rv_features.layoutManager = LinearLayoutManager(activity, RecyclerView.HORIZONTAL, false)

               rv_features.adapter = serviceAdapter

               val animationAdapter = AlphaInAnimationAdapter(serviceAdapter)
               animationAdapter.setDuration(1000)
               rv_features.adapter = animationAdapter*/

        loadProductDetail(arguments)

        btn_continue.setOnClickListener {

            prefHelper.setkeyValue(PrefenceConstants.NET_TOTAL, price.toFloat())

            if (prefHelper.getCurrentUserLoggedIn()) {
                addIntoCart()
            } else {
                activity?.launchActivity<LoginActivity>(DataNames.REQUEST_CART_LOGIN)
            }
        }

        tb_back.setOnClickListener {
            navController(this@CarDetailFrag).popBackStack()
        }


        tb_favourite.setOnClickListener {
            if (prefHelper.getCurrentUserLoggedIn()) {
                addFavUnFavProd()
            } else {
                activity?.launchActivity<LoginActivity>(AppConstants.REQUEST_PRODUCT_FAV)
            }
        }
    }

    private fun addFavUnFavProd() {

        if (viewDataBinding.productdata?.id ?: 0 < 0 && price.toInt() > 0) return

        if (isNetworkConnected) {
            favStatus = if (checkFavouriteImage(tb_favourite)) {
                "0"
            } else {
                "1"
            }
            viewModel.markFavProduct(viewDataBinding.productdata?.id.toString(), favStatus)
        }
    }

    private fun addIntoCart() {
        if (viewDataBinding.productdata?.id ?: 0 < 0 && price.toInt() > 0) return


        val productList = mutableListOf<CartInfoServer>()
        val productdata = viewDataBinding.productdata
        val cartInfoServer = CartInfoServer()

        cartInfoServer.quantity = 1f
        cartInfoServer.pricetype = productdata?.price_type ?: 0
        cartInfoServer.productId = productdata?.id.toString()
        cartInfoServer.category_id = productdata?.category_id ?: 0
        cartInfoServer.handlingAdmin = productdata?.handling_admin ?: 0.0f
        cartInfoServer.supplier_branch_id = productdata?.supplier_branch_id ?: 0
        cartInfoServer.handlingSupplier = productdata?.handling_supplier ?: 0.0f
        cartInfoServer.supplier_id = productdata?.supplier_id ?: 0
        cartInfoServer.agent_type = productdata?.is_agent ?: 0
        cartInfoServer.agent_list = productdata?.agent_list ?: 0
        cartInfoServer.deliveryType = DataNames.DELIVERY_TYPE_STANDARD
        cartInfoServer.name = productdata?.name ?: ""
        // cartInfoServer.subCatName = cartlist!!.cartInfos!![i].subCategoryName
        cartInfoServer.deliveryCharges = 0.0f
        cartInfoServer.price = price.toFloat()



        productList.add(cartInfoServer)

        if (isNetworkConnected)
            viewModel.addCart(viewDataBinding.productdata?.supplier_branch_id
                    ?: 0, productList)
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == DataNames.REQUEST_CART_LOGIN && resultCode == Activity.RESULT_OK) {
            val pojoLoginData = StaticFunction.isLoginProperly(activity)
            if (pojoLoginData.data != null) {

                addIntoCart()
            }

        } else if (requestCode == AppConstants.REQUEST_PRODUCT_FAV && resultCode == Activity.RESULT_OK) {
            val pojoLoginData = StaticFunction.isLoginProperly(activity)
            if (pojoLoginData.data != null) {
                addFavUnFavProd()
            }
        }
    }


    private fun loadProductDetail(arguments: Bundle?) {

        if (arguments == null) return

        mHomeParam = arguments.getParcelable("rentalparam") ?: HomeRentalParam()
        val mListDetail = arguments.getParcelable<ProductDataBean>("prodData")

        val mProductDetail = ProductDataBean()
        viewDataBinding.productdata = mProductDetail

        if (isNetworkConnected) {
            viewModel.getProductDetail(mListDetail?.product_id.toString(), mListDetail?.supplier_branch_id.toString())

        }

    }

    private fun intializePagerAdapter(imagePath: List<String>?) {

        viewPager.adapter = PagerImageAdapter(activity, imagePath, viewPager)
        cpiIndicator.setViewPager(viewPager)
        cpiIndicator.setSnap(false)
        cpiIndicator.setFillColor(ContextCompat.getColor(activity
                ?: requireContext(), R.color.white))
        cpiIndicator.setUnFillColor(ContextCompat.getColor(activity
                ?: requireContext(), R.color.black))
    }


    private fun productDetailObserver() {
        // Create the observer which updates the UI.
        val catObserver = Observer<ProductDataBean> { resource ->

            updateProductData(resource)
        }

        // Observe the LiveData, passing in this activity as the LifecycleOwner and the observer.
        viewModel.productDetailLiveData.observe(this, catObserver)
    }

    private fun updateProductData(resource: ProductDataBean?) {


        viewDataBinding.productdata = resource


        if (resource?.is_favourite == 1) {
            tb_favourite.setImageResource(R.drawable.ic_favourite)
        } else {
            tb_favourite.setImageResource(R.drawable.ic_unfavorite)
        }

        intializePagerAdapter(resource?.image_path as MutableList<String>?)
        webview_prodDesc.loadData(getString(R.string.long_desc), "text/html; charset=UTF-8", null)

        /*   mServiceList.clear()
           mServiceList.add(SupplierServiceModel(R.drawable.ic_payment_methods, "Cash", ""))
           mServiceList.add(SupplierServiceModel(R.drawable.ic_payment_methods, "Credit", ""))
           mServiceList.add(SupplierServiceModel(R.drawable.ic_payment_methods, "Paypal", ""))

           serviceAdapter.notifyDataSetChanged()*/


        intervalType = when (resource?.interval_flag) {
            0 -> "Hourly"
            1 -> "Day"
            2 -> "Week"
            3 -> "Month"
            4 -> "Year"
            5 -> "Km"
            else -> ""
        }



        if (intervalType == "Day") {
            intervalTime = resource?.required_day ?: 0
        } else if (intervalType == "Hourly") {
            intervalTime = resource?.required_hour ?: 0
        }

        resource?.hourly_price?.forEachIndexed { index, hourlyPrice ->
            (intervalTime >= hourlyPrice.min_hour ?: 0 && intervalTime <= hourlyPrice.max_hour ?: 0)
            run {
                price = hourlyPrice.price_per_hour ?: 0.0f
            }
        }

        tv_prod_price.text = getString(R.string.currency_tag, AppConstants.CURRENCY_SYMBOL, price)

    }

    private fun checkFavouriteImage(tbFavourite: ImageView): Boolean {
        return CommonUtils.areDrawablesIdentical(tbFavourite.drawable, ContextCompat.getDrawable(activity
                ?: requireContext(), R.drawable.ic_unfavorite)!!)

    }

}
