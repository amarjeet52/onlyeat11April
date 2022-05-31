package com.codebrew.clikat.module.searchProduct

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.TypedValue
import android.view.View
import android.view.inputmethod.EditorInfo
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.Navigation
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.codebrew.clikat.BR
import com.codebrew.clikat.R
import com.codebrew.clikat.app_utils.AppUtils
import com.codebrew.clikat.app_utils.DialogsUtil
import com.codebrew.clikat.app_utils.ProdUtils
import com.codebrew.clikat.app_utils.extension.afterTextChanged
import com.codebrew.clikat.app_utils.extension.onSnackbar
import com.codebrew.clikat.app_utils.navController
import com.codebrew.clikat.base.BaseFragment
import com.codebrew.clikat.base.PagingResult
import com.codebrew.clikat.data.AppDataType
import com.codebrew.clikat.data.DataManager
import com.codebrew.clikat.data.VendorAppType
import com.codebrew.clikat.data.constants.AppConstants
import com.codebrew.clikat.data.constants.PrefenceConstants
import com.codebrew.clikat.data.model.api.AddressBean
import com.codebrew.clikat.data.model.api.Currency
import com.codebrew.clikat.data.model.api.ProductData
import com.codebrew.clikat.data.model.others.FilterInputModel
import com.codebrew.clikat.databinding.FragmentUnifySearchBinding
import com.codebrew.clikat.di.ViewModelProviderFactory
import com.codebrew.clikat.modal.CartList
import com.codebrew.clikat.modal.database.SearchCatListModel
import com.codebrew.clikat.modal.database.SearchCategoryModel
import com.codebrew.clikat.modal.other.*
import com.codebrew.clikat.modal.other.SettingModel.DataBean.BookingFlowBean
import com.codebrew.clikat.modal.other.SettingModel.DataBean.ScreenFlowBean
import com.codebrew.clikat.module.addon_quant.SavedAddon
import com.codebrew.clikat.module.login.LoginActivity
import com.codebrew.clikat.module.product.product_listing.DialogListener
import com.codebrew.clikat.module.product.product_listing.adapter.ProductListingAdapter
import com.codebrew.clikat.module.product.product_listing.adapter.ProductListingAdapter.ProductCallback
import com.codebrew.clikat.module.product_addon.AddonFragment
import com.codebrew.clikat.module.product_detail.ProductDetails
import com.codebrew.clikat.module.searchProduct.adapter.ResturantListAdapter
import com.codebrew.clikat.module.searchProduct.adapter.ResturantListener
import com.codebrew.clikat.module.searchProduct.adapter.SuggestionListAdapter.Searchcallback
import com.codebrew.clikat.preferences.DataNames
import com.codebrew.clikat.preferences.Prefs
import com.codebrew.clikat.utils.StaticFunction
import com.codebrew.clikat.utils.StaticFunction.getCartQuantity
import com.codebrew.clikat.utils.Utils
import com.codebrew.clikat.utils.configurations.Configurations
import io.realm.OrderedRealmCollection
import io.realm.Realm
import kotlinx.android.synthetic.main.fragment_unify_search.*
import kotlinx.android.synthetic.main.layout_bottom_cart.*
import kotlinx.android.synthetic.main.nothing_found.*
import kotlinx.android.synthetic.main.toolbar_app.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import javax.inject.Inject

/*
 * Created by cbl80 on 8/7/16.
 */
class UnifySearchFragment : BaseFragment<FragmentUnifySearchBinding, SearchViewModel>(), Searchcallback,
        ProductCallback, DialogListener, View.OnClickListener, SearchNavigator, AddonFragment.AddonCallback {


    private var selectedCurrency: Currency?=null

    @Inject
    lateinit var factory: ViewModelProviderFactory

    @Inject
    lateinit var appUtils: AppUtils

    @Inject
    lateinit var prodUtils: ProdUtils


    private lateinit var mBinding: FragmentUnifySearchBinding


    private lateinit var viewModel: SearchViewModel

    private val listAllData = mutableListOf<ProductDataBean>()
    private var productAdapter: ProductListingAdapter? = null
    private var resturantListAdapter: ResturantListAdapter? = null
    private var varientData: FilterVarientData? = null
    private var realm: Realm? = null
    private var bookingFlowBean: BookingFlowBean? = null
    private var parentPos: Int = 0
    private var screenFlowBean: ScreenFlowBean? = null
    private var clientInform: SettingModel.DataBean.SettingData? = null

    @Inject
    lateinit var dataManager: DataManager

    @Inject
    lateinit var mDialogsUtil: DialogsUtil

    private val inputModel by lazy { FilterInputModel() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel.navigator = this

        productListObserver()

        supplierObserver()
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mBinding = viewDataBinding
        mBinding.color = Configurations.colors
        mBinding.drawables = Configurations.drawables
        mBinding.strings = appUtils.loadAppConfig(0).strings


        EventBus.getDefault().register(this)

        screenFlowBean = dataManager.getGsonValue(DataNames.SCREEN_FLOW, ScreenFlowBean::class.java)
        bookingFlowBean = dataManager.getGsonValue(DataNames.BOOKING_FLOW, BookingFlowBean::class.java)
        clientInform = dataManager.getGsonValue(DataNames.SETTING_DATA, SettingModel.DataBean.SettingData::class.java)
        selectedCurrency = dataManager.getGsonValue(PrefenceConstants.CURRENCY_INF, Currency::class.java)

        initialise()
        clickListner()
        setData()
        uniSearchEditText.afterTextChanged {
            if (it.trim().isNotEmpty()) {
                iv_search.isEnabled = true
            } else {
                viewModel.setSupplierCount(0)
                viewModel.setProductCount(0)
                listAllData.clear()
                iv_search.isEnabled = false
                productAdapter?.notifyDataSetChanged()
                resturantListAdapter?.submitMessageList(emptyList())
            }
        }

        uniSearchEditText.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                // Your piece of code on keyboard search click
                if (uniSearchEditText.text.trim().isNotEmpty())
                    iv_search.callOnClick()
                true
            } else false
        }
        showBottomCart()

        onRecyclerViewScrolled()
    }

    private fun setData() {
        if (arguments != null && arguments?.containsKey("productList") == true) {
            tb_title.text = getString(R.string.search, appUtils.loadAppConfig(0).strings?.product)
            tvRestaurant?.visibility = View.GONE
            tvProducts?.visibility = View.VISIBLE
            noData?.visibility = View.GONE
            val list = arguments?.getParcelableArrayList<ProductDataBean>("productList")
            setProductAdapter()
            handleProductList(list ?: arrayListOf())

        } else if (arguments != null && arguments?.containsKey("supplierList") == true) {
            tb_title.text = getString(R.string.search, appUtils.loadAppConfig(0).strings?.supplier)
            tvProducts?.visibility = View.GONE
            tvRestaurant?.visibility = View.VISIBLE
            noData?.visibility = View.GONE
            val list = arguments?.getParcelableArrayList<SupplierDataBean>("supplierList")
            setSupplierAdapter()
            updateSupplierData(list)

        } else {
            iv_search?.visibility = View.VISIBLE
            uniSearchEditText?.visibility = View.VISIBLE
            tb_title.text = getString(R.string.search, appUtils.loadAppConfig(0).strings?.product)
            setSupplierAdapter()
            setProductAdapter()
        }
    }

    private fun initialise() {
        // Create the Realm instance
        realm = Realm.getDefaultInstance()
        uniSearchEditText?.hint = getString(R.string.search_for_food_item_rest,
                appUtils.loadAppConfig(0).strings?.product,appUtils.loadAppConfig(0).strings?.supplier)
        tvRestaurant?.text = appUtils.loadAppConfig(0).strings?.supplier
        tvProducts?.text = appUtils.loadAppConfig(0).strings?.product

        if(clientInform?.is_skip_theme=="1"){
            tvTitleOoPs?.visibility=View.VISIBLE
            ivPlaceholder?.setImageResource(R.drawable.ic_graphic)
            tvText?.text=getString(R.string.nothing_found_try_again)
            tvText?.setTextColor(ContextCompat.getColor(requireContext(),R.color.colorPrimary))
            tvText?.setTextSize(TypedValue.COMPLEX_UNIT_SP,16f)
        }
    }

    private fun productListObserver() {
        // Create the observer which updates the UI.
        val catObserver = Observer<PagingResult<ProductData>> { resource ->
            // Update the UI, in this case, a TextView.
            val productList = ArrayList<ProductDataBean>()
            if (resource?.result?.product?.size ?: 0 > 5)
                productList.addAll(resource?.result?.product?.subList(0, 4) ?: emptyList())
            else
                productList.addAll(resource?.result?.product ?: arrayListOf())

            handleProductList(productList)
        }

        viewModel.productLiveData.observe(this, catObserver)

    }


    private fun supplierObserver() {
        // Create the observer which updates the UI.
        val supplierObserver = Observer<List<SupplierDataBean>> { resource ->
            setSupplierList(resource)
        }
        // Observe the LiveData, passing in this activity as the LifecycleOwner and the observer.
        viewModel.supplierLiveData.observe(this, supplierObserver)

    }


    private fun setSupplierList(resource: List<SupplierDataBean>?) {
        if (resource?.size ?: 0 > 5)
            updateSupplierData(resource?.subList(0, 4))
        else updateSupplierData(resource)
    }

    private fun updateSupplierData(resource: List<SupplierDataBean>?) {
        resturantListAdapter?.submitMessageList(resource)
    }


    private fun handleProductList(resource: ArrayList<ProductDataBean>) {
        hideKeyboard()

        listAllData.clear()
        changeProductList(resource)
        listAllData.addAll(resource)
        viewModel.setIsList(listAllData.size)

        productAdapter?.settingLayout(false)
        productAdapter?.notifyDataSetChanged()
    }

    private fun setSupplierAdapter() {
        resturantListAdapter = ResturantListAdapter(ResturantListener {
            val bundle = bundleOf("supplierId" to it.id)

            if (clientInform?.app_selected_template != null && clientInform?.app_selected_template == "1") {
                navController(this@UnifySearchFragment)
                        .navigate(R.id.action_searchFragment_to_restaurantDetailNewFrag, bundle)
            } else {
                navController(this@UnifySearchFragment)
                        .navigate(R.id.action_searchFragment_to_restaurantDetailFrag, bundle)
            }
        }, appUtils)
        recyclerViewRest.adapter = resturantListAdapter
    }

    private fun setProductAdapter() {
        productAdapter = ProductListingAdapter(activity
                ?: requireContext(), listAllData, appUtils, clientInform?.show_ecom_v2_theme,selectedCurrency)
        recyclerViewProd.adapter = productAdapter
        productAdapter?.settingLayout(false)
        productAdapter?.settingCallback(this)
    }

    private fun clickListner() {
        tb_back.setOnClickListener {
            Navigation.findNavController(requireView()).popBackStack()
        }
        iv_search?.setOnClickListener(this)
        tvViewMoreProducts?.setOnClickListener(this)
        tvViewMoreSupplier?.setOnClickListener(this)
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == AppConstants.REQUEST_WISH_PROD && resultCode == Activity.RESULT_OK) {

            if (dataManager.getCurrentUserLoggedIn()) {
                viewModel.markFavProduct(listAllData.get(parentPos).product_id, if (listAllData.get(parentPos).is_favourite == 0) 1 else 0)
            }
        }

    }

    override fun onResume() {
        super.onResume()

        productAdapter?.settingLayout(false)
        changeProductList(listAllData)
        if (productAdapter != null) {
            productAdapter?.notifyDataSetChanged()
        }

        if (viewModel.supplierLiveData.value != null) {
            setSupplierList(viewModel.supplierLiveData.value)
        }


    }

    override fun onDestroyView() {
        super.onDestroyView()
        realm?.close()

        //  varientData = FilterVarientData()
        EventBus.getDefault().unregister(this)
    }

    override fun getSearch(keyword: String) {
        uniSearchEditText?.setText(keyword)
        if (isNetworkConnected) {
            viewModel.getSupplierList(uniSearchEditText.text.toString().trim())
            setFilterData(uniSearchEditText?.text.toString())
        }
    }

    override fun clearHistory() {
        realm?.executeTransactionAsync { realm: Realm ->
            // realm.deleteAll();
            val data: OrderedRealmCollection<SearchCategoryModel>? = realm.where(SearchCatListModel::class.java).findFirst()!!.itemList
            // Otherwise it has been deleted already.
            data?.deleteAllFromRealm()
        }
    }


    override fun addToCart(position: Int, agentType: Boolean) {

        parentPos = position
        /*check service existance*/


        // if vendor status ==0 single vendor && vendor status==1 multiple vendor
        if (bookingFlowBean?.vendor_status == 0 && appUtils.checkVendorStatus(listAllData[position].supplier_id
                        , vendorBranchId = listAllData[position].supplier_branch_id, branchFlow = clientInform?.branch_flow)) {

            mDialogsUtil.openAlertDialog(activity
                    ?: requireContext(), getString(R.string.clearCart, appUtils.loadAppConfig(0).strings?.supplier
                    ?: "",appUtils.loadAppConfig(0).strings?.proceed ?: ""), "Yes", "No", this)
        } else {
            if (listAllData[position].adds_on?.isNotEmpty() == true) {
                val cartList: CartList? = dataManager.getGsonValue(DataNames.CART, CartList::class.java)

                if (appUtils.checkProdExistance(listAllData[position].product_id)) {
                    val savedProduct = cartList?.cartInfos?.filter {
                        it.supplierId == listAllData[position].supplier_id && it.productId == listAllData[position].product_id
                    } ?: emptyList()

                    SavedAddon.newInstance(listAllData[position], listAllData[position].self_pickup
                            ?: -1, savedProduct, this).show(childFragmentManager, "savedAddon")
                } else {
                    AddonFragment.newInstance(listAllData[position], listAllData[position].self_pickup
                            ?: -1, this).show(childFragmentManager, "addOn")
                }

            } else {
                if (appUtils.checkBookingFlow(requireContext(), listAllData[position].product_id
                                ?: 0, this)) {
                    addCart(position)
                }
            }
        }
    }

    override fun bookNow(adapterPosition: Int, bean: ProductDataBean) {
        //do nothing
    }

    private fun addCart(position: Int) {

        val mProduct = listAllData[position]

        if (mProduct.is_question == 1 && prodUtils.addItemToCart(mProduct) != null) {
            //  mProduct.prod_quantity==0
            val bundle = bundleOf("productBean" to mProduct, "is_Category" to false
                    , "categoryId" to mProduct.detailed_sub_category_id)
            navController(this@UnifySearchFragment).navigate(R.id.action_searchFragment_to_questionFrag, bundle)
        } else {
            mProduct.type = screenFlowBean?.app_type
            mProduct.apply { prodUtils.addItemToCart(mProduct) }
            productAdapter?.notifyItemChanged(position)
            productAdapter?.notifyDataSetChanged()

            showBottomCart()
        }
    }


    override fun removeToCart(position: Int) {

        parentPos = position

        val mProduct = listAllData[position]

        if (mProduct.adds_on?.isNotEmpty() == true) {
            val cartList: CartList? = dataManager.getGsonValue(DataNames.CART, CartList::class.java)

            val savedProduct = cartList?.cartInfos?.filter {
                it.supplierId == mProduct.supplier_id && it.productId == mProduct.product_id
            } ?: emptyList()

            SavedAddon.newInstance(mProduct, mProduct.self_pickup
                    ?: -1, savedProduct, this).show(childFragmentManager, "savedAddon")

        } else {

            listAllData[position] = mProduct.apply { prodUtils.removeItemToCart(mProduct) }
            productAdapter?.notifyItemChanged(position)
        }

        showBottomCart()
    }

    override fun productDetail(bean: ProductDataBean?) {

        val bundle = Bundle()
        bundle.putInt("productId", bean?.product_id ?: 0)
        bundle.putString("title", bean?.name)
        bundle.putInt("categoryId", bean?.category_id ?: 0)

        val screenFlowBean = Prefs.with(activity).getObject(DataNames.SCREEN_FLOW, ScreenFlowBean::class.java)

        if (screenFlowBean!!.app_type == AppDataType.Food.type && clientInform?.isProductDetailCheck == "1") {

            bundle.putInt("categoryId", bean?.category_id ?: 0)
            bundle.putInt("supplierId", bean?.supplier_id ?: 0)
            bundle.putInt("branchId", bean?.supplier_branch_id ?: 0)

            if (clientInform?.app_selected_template != null && clientInform?.app_selected_template == "1") {
                navController(this@UnifySearchFragment)
                        .navigate(R.id.action_searchFragment_to_restaurantDetailNewFrag, bundle)
            } else {
                navController(this@UnifySearchFragment)
                        .navigate(R.id.action_searchFragment_to_restaurantDetailFrag, bundle)
            }
        } else {
            if (clientInform?.show_ecom_v2_theme == "1") {
                navController(this@UnifySearchFragment)
                        .navigate(R.id.action_searchFragment_to_productDetailsV2, ProductDetails.newInstance(bean, 0, false))
            } else {
                navController(this@UnifySearchFragment)
                        .navigate(R.id.action_searchFragment_to_productDetails, ProductDetails.newInstance(bean, 0, false))
            }
        }

    }

    private fun changeProductList(product: List<ProductDataBean>?) {

        product?.mapIndexed { index, productDataBean ->
            productDataBean.prod_quantity = getCartQuantity(activity, productDataBean.product_id)

            //for fixed price

            productDataBean.let {
                prodUtils.changeProductList(false, productDataBean)
            }
        }
    }

    override fun publishResult(count: Int) {
        viewModel.setIsList(count)
    }

    override fun addtoWishList(adapterPosition: Int, status: Int?, productId: Int?) {

        parentPos = adapterPosition

        if (dataManager.getCurrentUserLoggedIn()) {
            if (isNetworkConnected) {

                viewModel.markFavProduct(listAllData[parentPos].product_id, if (listAllData[parentPos].is_favourite == 0) 1 else 0)
            }
        } else {
            startActivityForResult(Intent(activity, LoginActivity::class.java), AppConstants.REQUEST_WISH_PROD)
        }
    }

    override fun onSucessListner() {

        appUtils.clearCart()

        listAllData.map {
            it.prod_quantity = 0f
        }
        productAdapter?.notifyDataSetChanged()

        showBottomCart()
    }

    override fun onErrorListener() {}

    companion object {
        private const val FILTERDATA = 788
    }

    override fun onClick(p0: View?) {
        when (p0?.id) {
            R.id.iv_search -> {
                hideKeyboard()
                if (isNetworkConnected) {
                    viewModel.getSupplierList(uniSearchEditText.text.toString().trim())
                    setFilterData(uniSearchEditText?.text.toString())
                }

                realm?.executeTransaction { realm: Realm? -> SearchCategoryModel.create(realm, uniSearchEditText?.text.toString()) }
            }
            R.id.tvViewMoreProducts -> {
                val bundle = bundleOf("productList" to viewModel.productLiveData.value?.result?.product)
                navController(this@UnifySearchFragment)
                        .navigate(R.id.action_searchFragment_to_unify
                                , bundle)
            }
            R.id.tvViewMoreSupplier -> {
                val bundle = bundleOf("supplierList" to viewModel.supplierLiveData.value)
                navController(this@UnifySearchFragment)
                        .navigate(R.id.action_searchFragment_to_unify
                                , bundle)
            }
        }
    }

    private fun setFilterData(keyword: String) {
        var low_to_high = 1
        var popularity = 0
        if (varientData != null) {
            when (varientData!!.sortBy) {
                "Price: Low to High" -> low_to_high = 1
                "Price: High to Low" -> low_to_high = 0
                "Popularity" -> popularity = 1
            }
        }

        inputModel.languageId = StaticFunction.getLanguage(activity).toString()
        if (varientData != null) {
            inputModel.subCategoryId?.addAll(varientData!!.subCatId)
            inputModel.max_price_range = varientData!!.maxPrice.toString()
            inputModel.min_price_range = varientData!!.minPrice.toString()
            inputModel.variant_ids?.addAll(varientData!!.varientID)
            // inputModel.setSupplier_ids("");
            inputModel.brand_ids?.addAll(varientData!!.brandId)
            inputModel.is_availability = (if (varientData!!.isAvailability) "1" else "0")
            inputModel.is_discount = (if (varientData!!.isDiscount) "1" else "0")
        } else {
            inputModel.is_availability = 0.toString()
            inputModel.is_discount = 0.toString()
            inputModel.max_price_range = 100000.toString()
            inputModel.min_price_range = 0.toString()
        }

        val mLocUser = dataManager.getGsonValue(PrefenceConstants.ADRS_DATA, AddressBean::class.java)

        if (mLocUser != null) {
            inputModel.latitude = mLocUser.latitude ?: ""
            inputModel.longitude = mLocUser.longitude ?: ""
        }
        inputModel.low_to_high = low_to_high.toString()
        inputModel.is_popularity = popularity
        if (!keyword.isEmpty()) {
            inputModel.product_name = keyword
        }

        callApi(inputModel,true)
    }

    private fun showBottomCart() {

        val appCartModel = appUtils.getCartData(clientInform,selectedCurrency)

        bottom_cart.visibility = View.VISIBLE

        if (appCartModel.cartAvail) {

            tv_total_product.text =getString(R.string.total_item_tag, Utils.getDecimalPointValue(clientInform, appCartModel.totalCount))
            tv_total_price.text = getString(R.string.total).plus(" ").plus(AppConstants.CURRENCY_SYMBOL).plus(" ").plus(appCartModel.totalPrice)


            if (appCartModel.supplierName.isNotEmpty() && screenFlowBean?.is_single_vendor == VendorAppType.Multiple.appType) {
                tv_supplier_name.visibility = View.VISIBLE
                tv_supplier_name.text = getString(R.string.supplier_tag, appCartModel.supplierName)
            } else {
                tv_supplier_name.visibility = View.GONE
            }

            bottom_cart.setOnClickListener {
                if (clientInform?.show_ecom_v2_theme == "1") {
                    navController(this@UnifySearchFragment).navigate(R.id.action_searchFragment_to_cartV2)
                } else {
                    navController(this@UnifySearchFragment).navigate(R.id.action_searchFragment_to_cart)
                }
            }
        } else {
            bottom_cart.visibility = View.GONE
        }
    }


    override fun getBindingVariable(): Int {
        return BR.viewModel
    }

    override fun getLayoutId(): Int {
        return R.layout.fragment_unify_search


    }

    override fun getViewModel(): SearchViewModel {
        viewModel = ViewModelProviders.of(this, factory).get(SearchViewModel::class.java)
        return viewModel
    }

    override fun onFavStatus() {

        listAllData.map {

            if (it.product_id == listAllData[parentPos].product_id) {
                it.is_favourite = if (it.is_favourite == 0) 1 else 0
            }
        }

        productAdapter?.notifyItemChanged(parentPos)
    }

    override fun onErrorOccur(message: String) {
        mBinding.root.onSnackbar(message)
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

        listAllData[parentPos] = productModel

        productAdapter?.notifyItemChanged(parentPos)
        productAdapter?.notifyDataSetChanged()

        showBottomCart()
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onMessageEvent(filterVarientData: FilterResponseEvent) {
        varientData = filterVarientData.filterModel
//        listAllData.clear()
//        viewModel.setIsSearchHist(false)
//        AppConstants.SEARCH_OPTION = SearchType.TYPE_PROD.type
//        viewModel.setIsList(filterVarientData.productlist.size)
//        if (filterVarientData.productlist.isNotEmpty()) {
//            listAllData.addAll(filterVarientData.productlist)
//            changeProductList(listAllData)
//
//            if (listAllData.isNotEmpty()) {
//                if (recyclerview.adapter !== productAdapter) {
//                    recyclerview.layoutManager = setLayoutManager(true)
//                    recyclerview.adapter = productAdapter
//                }
//            }
//        }
//        productAdapter?.notifyDataSetChanged()
    }

    private fun callApi(inputModel: FilterInputModel, isFirstPage: Boolean) {
        if (isNetworkConnected) {
            viewModel.getProductList(inputModel, isFirstPage)
        }
    }

    private fun onRecyclerViewScrolled() {
//        recyclerview.addOnScrollListener(object : RecyclerView.OnScrollListener() {
//            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
//                super.onScrolled(recyclerView, dx, dy)
//
//                val isPagingActive = viewModel.validForPaging()
//
//                if ( recyclerview.adapter is ResturantListAdapter) return
//
//                if (recyclerView.layoutManager is LinearLayoutManager && !recyclerView.canScrollVertically(1) && isPagingActive) {
//                    callApi(inputModel, false)
//                } else if (recyclerView.layoutManager is GridLayoutManager) {
//
//                    val layoutManager = recyclerView.layoutManager as GridLayoutManager
//                    val visibleItemCount = layoutManager.findLastCompletelyVisibleItemPosition() + 1
//
//                    if (visibleItemCount == layoutManager.itemCount && isPagingActive)
//                        callApi(inputModel, false)
//                }
//            }
//        })
    }
}