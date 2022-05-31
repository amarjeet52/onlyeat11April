package com.codebrew.clikat.module.all_offers

import android.content.Intent
import android.graphics.BlendMode
import android.graphics.BlendModeColorFilter
import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.NavOptions
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.codebrew.clikat.BR
import com.codebrew.clikat.R
import com.codebrew.clikat.app_utils.AppUtils
import com.codebrew.clikat.app_utils.ProdUtils
import com.codebrew.clikat.app_utils.extension.onSnackbar
import com.codebrew.clikat.app_utils.navController
import com.codebrew.clikat.base.BaseFragment
import com.codebrew.clikat.data.*
import com.codebrew.clikat.data.constants.AppConstants
import com.codebrew.clikat.data.constants.PrefenceConstants
import com.codebrew.clikat.data.model.api.Currency
import com.codebrew.clikat.databinding.FragmentPackagetListingBinding
import com.codebrew.clikat.di.ViewModelProviderFactory
import com.codebrew.clikat.modal.CartList
import com.codebrew.clikat.modal.other.ProductDataBean
import com.codebrew.clikat.modal.other.SettingModel
import com.codebrew.clikat.module.addon_quant.SavedAddon
import com.codebrew.clikat.module.home_screen.adapter.SpecialListAdapter
import com.codebrew.clikat.module.login.LoginActivity
import com.codebrew.clikat.module.product.product_listing.DialogListener
import com.codebrew.clikat.module.product_addon.AddonFragment
import com.codebrew.clikat.module.product_detail.ProductDetails
import com.codebrew.clikat.preferences.DataNames
import com.codebrew.clikat.utils.DialogIntrface
import com.codebrew.clikat.utils.StaticFunction
import com.codebrew.clikat.utils.StaticFunction.getCartQuantity
import com.codebrew.clikat.utils.Utils
import com.codebrew.clikat.utils.configurations.Configurations
import kotlinx.android.synthetic.main.fragment_packaget_listing.*
import kotlinx.android.synthetic.main.fragment_packaget_listing.toolbar
import kotlinx.android.synthetic.main.layout_bottom_cart.*
import kotlinx.android.synthetic.main.toolbar_app.*
import kotlinx.android.synthetic.main.toolbar_app.tb_back
import kotlinx.android.synthetic.main.toolbar_supplier.*
import javax.inject.Inject


/*
 * Created by cbl80 on 16/6/16.
 */
class OfferProductListingFragment : BaseFragment<FragmentPackagetListingBinding, OfferProdListViewModel>(),
        SpecialListAdapter.OnProductDetail, OfferProdListNavigator, AddonFragment.AddonCallback, DialogListener, DialogIntrface {

    private var selectedCurrency: Currency?=null
    private var mAdapter: SpecialListAdapter? = null

    @Inject
    lateinit var dataManager: DataManager

    @Inject
    lateinit var appUtils: AppUtils


    @Inject
    lateinit var prodUtils: ProdUtils

    @Inject
    lateinit var factory: ViewModelProviderFactory

    private var mBinding: FragmentPackagetListingBinding? = null

    private lateinit var viewModel: OfferProdListViewModel

    private var screenFlowBean: SettingModel.DataBean.ScreenFlowBean? = null
    private var bookingFlowBean: SettingModel.DataBean.BookingFlowBean? = null
    private var parentPos: Int = 0

    private var catId = 0

    private var similarProdList: MutableList<ProductDataBean>? = null

    private lateinit var mOfferList: MutableList<ProductDataBean>

    private var title: String? = null

    private val textConfig by lazy { appUtils.loadAppConfig(0).strings }
    private var clientInform: SettingModel.DataBean.SettingData? = null
    private val colorConfig by lazy { Configurations.colors }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.navigator = this

        screenFlowBean = dataManager.getGsonValue(DataNames.SCREEN_FLOW, SettingModel.DataBean.ScreenFlowBean::class.java)
        bookingFlowBean = dataManager.getGsonValue(DataNames.BOOKING_FLOW, SettingModel.DataBean.BookingFlowBean::class.java)
        clientInform = dataManager.getGsonValue(DataNames.SETTING_DATA, SettingModel.DataBean.SettingData::class.java)
        selectedCurrency = dataManager.getGsonValue(PrefenceConstants.CURRENCY_INF, Currency::class.java)
        offerListObserver()

        arguments?.let {
            catId = it.getInt("cat_id")
            similarProdList = it.getParcelableArrayList("similar_list")
            title = it.getString("title")
        }
    }

    private fun offerListObserver() {
        // Create the observer which updates the UI.
        val catObserver = Observer<MutableList<ProductDataBean>> { resource ->
            refreshList(resource)
        }
        // Observe the LiveData, passing in this activity as the LifecycleOwner and the observer.
        viewModel.offerLiveData.observe(this, catObserver)
    }

    private fun refreshList(resource: MutableList<ProductDataBean>?) {
        setdata(resource)
    }

    override fun getBindingVariable(): Int {
        return BR.viewModel
    }

    override fun getLayoutId(): Int {
        return R.layout.fragment_packaget_listing
    }

    override fun getViewModel(): OfferProdListViewModel {
        viewModel = ViewModelProviders.of(this, factory).get(OfferProdListViewModel::class.java)
        return viewModel
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mBinding = viewDataBinding
        viewDataBinding.color = colorConfig
        viewDataBinding.drawables = Configurations.drawables
        viewDataBinding.strings = textConfig
        settingLayout()
        apiAllOffer()

        showBottomCart()

        if (!title.isNullOrEmpty())
            tb_title.text = title
        else
            tb_title.text = getString(R.string.offers)

        tb_back.setOnClickListener { v: View? -> Navigation.findNavController(view).popBackStack() }

        if (clientInform?.show_ecom_v2_theme == "1") {
            toolbar?.elevation = 0f
            toolbar?.background = ContextCompat.getDrawable(requireActivity(), R.drawable.background_toolbar_bottom_radius)
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
                toolbar?.background?.colorFilter = BlendModeColorFilter(Color.parseColor(colorConfig.search_background), BlendMode.SRC_ATOP)
            }
        }
    }

    private fun settingLayout() {

        mOfferList = mutableListOf()

        val mLayoutManager = LinearLayoutManager(activity, RecyclerView.VERTICAL, false)
        recyclerview.layoutManager = mLayoutManager
        mAdapter = SpecialListAdapter(mOfferList, screenFlowBean?.app_type
                ?: 0, screenFlowBean?.is_single_vendor ?: 0, 0, clientInform,currency = selectedCurrency,appUtils = appUtils)


/*        val itemDecoration: ItemDecoration = DividerItemDecoration(activity, DividerItemDecoration.VERTICAL)
        recyclerview.addItemDecoration(itemDecoration)*/

        mAdapter?.settingCllback(this)
        recyclerview.adapter = mAdapter
    }

    override fun onResume() {
        super.onResume()

        if (viewModel.offerLiveData.value != null) {
            refreshList(viewModel.offerLiveData.value)
        }
    }

    private fun apiAllOffer() {

        if (viewModel.offerLiveData.value != null) {
            refreshList(viewModel.offerLiveData.value)
        } else {
            if (isNetworkConnected) {
                if (similarProdList?.isNotEmpty() == true) {
                    viewModel.setOfferList(similarProdList?.count() ?: 0)
                    setdata(similarProdList)
                } else {
                    viewModel.getOfferList(catId)
                }
            }
        }
    }

    private fun setdata(data: MutableList<ProductDataBean>?) {

        mOfferList.clear()

        data?.map {
            it.prod_quantity = getCartQuantity(activity, it.product_id)
            it.netPrice = it.price?.toFloatOrNull()

            it.let {
                prodUtils.changeProductList(false, it)
            }
        }
        mOfferList.addAll(data ?: emptyList())
        mAdapter?.notifyDataSetChanged()
    }

    override fun addToCart(position: Int, productBean: ProductDataBean?) {

        parentPos = position

        productBean?.discount = 1

        if (AppConstants.DELIVERY_OPTIONS == DeliveryType.PickupOrder.type) {
            productBean?.self_pickup = FoodAppType.Pickup.foodType
        } else {
            productBean?.self_pickup = FoodAppType.Delivery.foodType
        }


        if (productBean?.adds_on?.isNotEmpty() == true) {
            val cartList: CartList? = dataManager.getGsonValue(DataNames.CART, CartList::class.java)

            if (appUtils.checkProdExistance(productBean.product_id)) {
                val savedProduct = cartList?.cartInfos?.filter {
                    it.supplierId == productBean.supplier_id ?: 0 && it.productId == productBean.product_id
                } ?: emptyList()

                SavedAddon.newInstance(productBean, FoodAppType.Delivery.foodType, savedProduct, this).show(childFragmentManager, "savedAddon")
            } else {
                AddonFragment.newInstance(productBean, FoodAppType.Delivery.foodType, this).show(childFragmentManager, "addOn")
            }

        } else {
            if (bookingFlowBean?.vendor_status == 0 && appUtils.checkVendorStatus(productBean?.supplier_id, vendorBranchId = productBean?.supplier_branch_id, branchFlow = clientInform?.branch_flow)) {
                appUtils.mDialogsUtil.openAlertDialog(activity
                        ?: requireContext(), getString(R.string.clearCart, textConfig?.supplier, textConfig?.proceed
                        ?: ""), "Yes", "No", this)
            } else {

                if (appUtils.checkBookingFlow(requireContext(), productBean?.product_id, this)) {
                    addCart(position, productBean)
                }
            }
        }

    }


    private fun addCart(position: Int, productBean: ProductDataBean?) {

        if (productBean?.is_question == 1) {
            productBean.prod_quantity == 0f
            val bundle = bundleOf("productBean" to productBean, "is_Category" to false, "categoryId" to productBean.detailed_sub_category_id)
            navController(this@OfferProductListingFragment).navigate(R.id.action_offerProductListingFragment_to_questionFrag, bundle)
        } else {
            productBean?.type = screenFlowBean?.app_type
            productBean.apply { prodUtils.addItemToCart(productBean) }
            mAdapter?.notifyItemChanged(position)

            showBottomCart()
        }
    }

    private fun showBottomCart() {

        val appCartModel = appUtils.getCartData(clientInform,selectedCurrency)

        /* if (screenFlowBean?.app_type == AppDataType.Food.type) {
             bottom_cart.visibility = View.GONE
         } else {
             bottom_cart.visibility = View.VISIBLE
         }*/


        if (appCartModel.cartAvail) {

            bottom_cart.visibility = View.VISIBLE

            tv_total_price.text = getString(R.string.total).plus(" ").plus(AppConstants.CURRENCY_SYMBOL).plus(" ").plus(appCartModel.totalPrice)

            tv_total_product.text = getString(R.string.total_item_tag, Utils.getDecimalPointValue(clientInform, appCartModel.totalCount))


            if (appCartModel.supplierName.isNotEmpty() && screenFlowBean?.is_single_vendor == VendorAppType.Multiple.appType) {
                tv_supplier_name.visibility = View.VISIBLE
                tv_supplier_name.text = getString(R.string.supplier_tag, appCartModel.supplierName)
            } else {
                tv_supplier_name.visibility = View.GONE
            }

            bottom_cart.setOnClickListener {

                val navOptions: NavOptions = if (screenFlowBean?.app_type == AppDataType.Food.type) {
                    NavOptions.Builder()
                            .setPopUpTo(R.id.resturantHomeFrag, false)
                            .build()
                } else {
                    NavOptions.Builder()
                            .setPopUpTo(R.id.homeFragment, false)
                            .build()
                }

                navController(this@OfferProductListingFragment).navigate(R.id.action_offerProductListingFragment_to_cart, null, navOptions)

            }
        } else {
            bottom_cart.visibility = View.GONE
        }

    }


    override fun removeToCart(position: Int, productBean: ProductDataBean?) {

        parentPos = position
        if (productBean?.adds_on?.isNotEmpty() == true) {
            val cartList: CartList? = dataManager.getGsonValue(DataNames.CART, CartList::class.java)

            if (appUtils.checkProdExistance(productBean.product_id)) {
                val savedProduct = cartList?.cartInfos?.filter {
                    it.supplierId == productBean.supplier_id ?: 0 && it.productId == productBean.product_id
                } ?: emptyList()

                SavedAddon.newInstance(productBean, FoodAppType.Delivery.foodType, savedProduct, this).show(childFragmentManager, "savedAddon")
            } else {
                AddonFragment.newInstance(productBean, FoodAppType.Delivery.foodType, this).show(childFragmentManager, "addOn")
            }
        } else {

            productBean.apply { prodUtils.removeItemToCart(productBean) }
            mAdapter?.notifyItemChanged(position)

            showBottomCart()
        }
    }

    private fun clearCart() {
        mOfferList.map {
            it.prod_quantity = 0f
        }
        mAdapter?.notifyDataSetChanged()

        showBottomCart()
    }

    override fun onProductDetail(bean: ProductDataBean?) {

        if (screenFlowBean?.is_single_vendor == VendorAppType.Multiple.appType) {
            val bundle = Bundle()

            if (screenFlowBean?.app_type == AppDataType.Food.type) {

                bundle.putInt("supplierId", bean?.supplier_id ?: 0)
                bundle.putInt("branchId", bean?.supplier_branch_id ?: 0)
                bundle.putString("title", bean?.name)
                bundle.putInt("categoryId", bean?.category_id ?: 0)

                if (screenFlowBean?.app_type == AppDataType.Food.type) {
                    if (bookingFlowBean?.is_pickup_order == FoodAppType.Pickup.foodType) {
                        bundle.putString("deliveryType", "pickup")
                    }
                    navController(this@OfferProductListingFragment)
                            .navigate(R.id.action_offerProductListingFragment_to_restaurantDetailFrag, bundle)
                }
            } else {
                Navigation.findNavController(requireView()).navigate(R.id.action_offerProductListingFragment_to_productDetails, ProductDetails.newInstance(bean, 1, false))
            }
        }

    }

    override fun onFavStatus() {
        mOfferList.map {

            if (it.product_id == mOfferList[parentPos].product_id) {
                it.is_favourite = if (it.is_favourite == 0) 1 else 0
            }
        }

        mAdapter?.notifyItemChanged(parentPos)
    }

    override fun onErrorOccur(message: String) {
        mBinding?.root?.onSnackbar(message)
    }

    override fun onSessionExpire() {
        openActivityOnTokenExpire()
    }

    override fun onAddonAdded(productModel: ProductDataBean) {
        if (productModel.adds_on?.isNullOrEmpty() == false && appUtils.checkProdExistance(productModel.product_id)) {
            val cartList: CartList? = appUtils.getCartList()
            productModel.prod_quantity = (cartList?.cartInfos?.filter { productModel.product_id == it.productId }?.sumByDouble { it.quantity.toDouble() }
                    ?: 0.0).toFloat()
        }

        showBottomCart()

        if (mAdapter != null) {
            mOfferList[parentPos] = productModel
            mAdapter?.notifyItemChanged(parentPos)
            mAdapter?.notifyDataSetChanged()
        }
    }

    override fun onSucessListner() {
        appUtils.clearCart()
        clearCart()
    }

    override fun onSuccessListener() {

    }


    override fun onErrorListener() {

    }

    override fun addtoWishList(adapterPosition: Int, status: Int?, productId: Int?) {

        parentPos = adapterPosition

        if (dataManager.getCurrentUserLoggedIn()) {
            if (isNetworkConnected) {

                viewModel.markFavProduct(mOfferList.get(parentPos).product_id, if (mOfferList.get(parentPos).is_favourite == 0) 1 else 0)
            }
        } else {
            startActivityForResult(Intent(activity, LoginActivity::class.java), AppConstants.REQUEST_WISH_PROD)
        }

    }

    override fun onProdDesc(productDesc: String) {
        StaticFunction.sweetDialogueSuccess11(activity, getString(R.string.description), productDesc, false, 1002, this)
    }

    override fun onProdAllergies(bean: ProductDataBean?) {

    }

    override fun onProdDialog(bean: ProductDataBean?) {
        appUtils.mDialogsUtil.showProductDialog(requireContext(), bean)
    }

    override fun onBookNow(bean: ProductDataBean?) {
        //do nothing
    }


}