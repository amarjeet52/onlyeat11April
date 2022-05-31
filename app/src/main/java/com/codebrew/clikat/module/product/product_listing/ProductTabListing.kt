package com.codebrew.clikat.module.product.product_listing

import android.app.Activity
import android.content.Intent
import android.graphics.BlendMode
import android.graphics.BlendModeColorFilter
import android.graphics.Color
import android.graphics.PorterDuff
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.DividerItemDecoration.VERTICAL
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ItemDecoration
import com.codebrew.clikat.BR
import com.codebrew.clikat.R
import com.codebrew.clikat.app_utils.*
import com.codebrew.clikat.app_utils.extension.launchActivity
import com.codebrew.clikat.app_utils.extension.loadPlaceHolder
import com.codebrew.clikat.app_utils.extension.onSnackbar
import com.codebrew.clikat.base.BaseFragment
import com.codebrew.clikat.base.PagingResult
import com.codebrew.clikat.data.AppDataType
import com.codebrew.clikat.data.DataManager
import com.codebrew.clikat.data.VendorAppType
import com.codebrew.clikat.data.constants.AppConstants
import com.codebrew.clikat.data.constants.PrefenceConstants
import com.codebrew.clikat.data.model.api.*
import com.codebrew.clikat.data.model.api.Currency
import com.codebrew.clikat.data.model.api.orderDetail.Data
import com.codebrew.clikat.data.model.api.orderDetail.OrderHistory
import com.codebrew.clikat.data.model.others.AgentCustomParam
import com.codebrew.clikat.data.model.others.EditOrderRequest
import com.codebrew.clikat.data.model.others.EditProductsItem
import com.codebrew.clikat.data.model.others.FilterInputModel
import com.codebrew.clikat.data.preferences.PreferenceHelper
import com.codebrew.clikat.databinding.FragmentSubcategoryGroceryBinding
import com.codebrew.clikat.di.ViewModelProviderFactory
import com.codebrew.clikat.modal.CartList
import com.codebrew.clikat.modal.LocationUser
import com.codebrew.clikat.modal.other.FilterResponseEvent
import com.codebrew.clikat.modal.other.FilterVarientData
import com.codebrew.clikat.modal.other.ProductDataBean
import com.codebrew.clikat.modal.other.SettingModel
import com.codebrew.clikat.module.addon_quant.SavedAddon
import com.codebrew.clikat.module.filter.BottomSheetFragment
import com.codebrew.clikat.module.product.ProductNavigator
import com.codebrew.clikat.module.product.product_listing.adapter.ProductListingAdapter
import com.codebrew.clikat.module.product_addon.AddonFragment
import com.codebrew.clikat.module.product_detail.ProductDetails
import com.codebrew.clikat.module.service_selection.ServSelectionActivity
import com.codebrew.clikat.preferences.DataNames
import com.codebrew.clikat.utils.StaticFunction
import com.codebrew.clikat.utils.Utils
import com.codebrew.clikat.utils.configurations.Configurations
import com.google.gson.Gson
import kotlinx.android.synthetic.main.fragment_subcategory_grocery.*
import kotlinx.android.synthetic.main.layout_bottom_cart.*
import kotlinx.android.synthetic.main.nothing_found.*
import kotlinx.android.synthetic.main.toolbar_app.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.util.*
import javax.inject.Inject
import kotlin.collections.ArrayList

/*
 * Created by cbl45 on 15/4/16.
 */
private const val FILTERDATA = 788


class ProductTabListing : BaseFragment<FragmentSubcategoryGroceryBinding, ProductTabViewModel>(),
        ProductNavigator, ProductListingAdapter.ProductCallback, DialogListener, View.OnClickListener, AddonFragment.AddonCallback {


    private lateinit var viewModel: ProductTabViewModel

    @Inject
    lateinit var factory: ViewModelProviderFactory

    @Inject
    lateinit var mPreferenceHelper: PreferenceHelper

    private lateinit var mProductBinding: FragmentSubcategoryGroceryBinding

    private var subCategoryId: Int = 0
    private var categoryId: Int = 0

    private var exampleAllProduct: ProductListModel? = null
    private var supplierBranchId: Int? = 0
    private var mAdapter: ProductListingAdapter? = null


    private val list = mutableListOf<ProductDataBean>()
    private var mQuestionList = arrayListOf<QuestionList>()

    private var bookingFlowBean: SettingModel.DataBean.BookingFlowBean? = null
    private var screenFlowBean: SettingModel.DataBean.ScreenFlowBean? = null

    var varientData: FilterVarientData? = null

    private var isLoading = false

    private val selectedColor = Color.parseColor(Configurations.colors.tabSelected)
    private val unselectedColor = Color.parseColor(Configurations.colors.tabUnSelected)

    private var brandId: Int = 0

    private var viewType: Boolean = false
    private var hasSubcat: Boolean = false
    private var hasBrands: Boolean = false
    private var catName: String = ""
    private var mDeliveyType: Int = 0
    private var selectedCurrency: Currency?=null
    @Inject
    lateinit var dataManager: DataManager

    @Inject
    lateinit var appUtils: AppUtils

    @Inject
    lateinit var prodUtils: ProdUtils

    @Inject
    lateinit var prefHelper: PreferenceHelper

    @Inject
    lateinit var mDialogsUtil: DialogsUtil

    private var parentPos: Int = 0

    private val textConfig by lazy { appUtils.loadAppConfig(0).strings }
    private var settingBean: SettingModel.DataBean.SettingData? = null
    private var orderDetail: OrderHistory? = null

    private val colorConfig by lazy { Configurations.colors }

    private val inputModel by lazy { FilterInputModel() }

    override fun getBindingVariable(): Int = BR.viewModel

    override fun getLayoutId(): Int = R.layout.fragment_subcategory_grocery


    override fun getViewModel(): ProductTabViewModel {

        viewModel = ViewModelProviders.of(this, factory).get(ProductTabViewModel::class.java)
        return viewModel
    }

    override fun onFavStatus() {

        markFavList()
    }

    private fun markFavList() {
        when (list[parentPos].is_favourite) {
            1 -> {
                list[parentPos].is_favourite = 0
            }
            else -> {
                list[parentPos].is_favourite = 1
            }
        }
        mAdapter?.notifyItemChanged(parentPos)
    }

    override fun onErrorOccur(message: String) {
        llContainer.onSnackbar(message)
    }

    override fun onSessionExpire() {
        openActivityOnTokenExpire()
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mProductBinding = viewDataBinding
        viewDataBinding.colors = colorConfig
        viewDataBinding.drawables = Configurations.drawables
        viewDataBinding.strings = textConfig

        selectedCurrency = dataManager.getGsonValue(PrefenceConstants.CURRENCY_INF, Currency::class.java)
        screenFlowBean = mPreferenceHelper.getGsonValue(DataNames.SCREEN_FLOW, SettingModel.DataBean.ScreenFlowBean::class.java)
        bookingFlowBean = mPreferenceHelper.getGsonValue(DataNames.BOOKING_FLOW, SettingModel.DataBean.BookingFlowBean::class.java)
        settingBean = dataManager.getGsonValue(DataNames.SETTING_DATA, SettingModel.DataBean.SettingData::class.java)
        initialization()

        productListObserver()

        if (arguments != null) {

            if (arguments?.containsKey("title") == true) {
                val title = arguments?.getString("title", "Select")
                tb_title.text = title
            }

            if (arguments?.containsKey("has_subcat") == true) {
                hasSubcat = arguments?.getBoolean("has_subcat") ?: false
            }

            if (arguments?.containsKey("has_brands") == true) {
                hasBrands = arguments?.getBoolean("has_brands") ?: false
                brandId = arguments?.getInt("brand_id") ?: 0
            }

            mDeliveyType = arguments?.getInt("deliveryType") ?: 0


            if (arguments?.containsKey("varientData") == true) {
                list.clear()

                varientData = arguments?.getParcelable("varientData")

                if (arguments?.containsKey("result") == true) {
                    list.addAll(arguments?.getParcelableArrayList("result")!!)

                    isLoading = true
                    refreshData()
                }

                if (searchView.text.toString().isNotEmpty())
                    mAdapter?.filter?.filter(searchView.text.toString().toLowerCase(DateTimeUtils.timeLocale))
            }
            if (arguments?.containsKey("orderItem") == true) {
                orderDetail = arguments?.getParcelable("orderItem")
                supplierBranchId = orderDetail?.supplier_id

                tvEditOrder?.visibility = View.VISIBLE
            }
        }


        if (StaticFunction.isInternetConnected(activity)) {
            apiAllProducts(subCategoryId, supplierBranchId ?: 0)
        } else {
            StaticFunction.showNoInternetDialog(activity)
        }

        if (settingBean?.show_ecom_v2_theme == "1") {
            produtTabSearchContainer.background = ContextCompat.getDrawable(requireActivity(), R.drawable.background_toolbar_bottom_radius)
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
                produtTabSearchContainer?.background?.colorFilter = BlendModeColorFilter(Color.parseColor(colorConfig.toolbarColor), BlendMode.SRC_ATOP)
            }
            toolbar_layout.elevation = 0f


            searchView.background = ContextCompat.getDrawable(requireActivity(), R.drawable.search_home_radius_background)
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
                searchView.background.colorFilter = BlendModeColorFilter(Color.parseColor(colorConfig.search_background), BlendMode.SRC_ATOP)
            }
            searchView.setTextColor(Color.parseColor(colorConfig.search_textcolor))
            searchView.setHintTextColor(Color.parseColor(colorConfig.search_textcolor))
        }

        tb_back.setOnClickListener {

//            Navigation.findNavController(view).popBackStack()

            requireActivity().onBackPressed()

        }

        iv_grid_view.setOnClickListener(this)
        tv_filter.setOnClickListener(this)
        iv_list_view.setOnClickListener(this)
        tvEditOrder?.setOnClickListener(this)


        Utils.loadAppPlaceholder(settingBean?.product_listing ?: "")?.let {

            if (it.app?.isNotEmpty() == true) {
                ivPlaceholder.loadPlaceHolder(it.app ?: "")
            }

            if (it.message?.isNotEmpty() == true) {
                tvText.text = it.message
            }

        }

        if (orderDetail == null)
            setPriceLayout()
        else
            bottom_cart.visibility = View.GONE

        onRecyclerViewScrolled()
    }


    private fun refreshData() {
        if (isLoading) {
            if (list.size > 0) {
                if (orderDetail != null && arguments?.containsKey("isEditOrder") == true) changeProductListEditOrder(list) else changeProductList(list)
                recyclerview.layoutManager = setLayoutManager(viewType)
                mAdapter?.settingLayout(viewType)
                recyclerview.adapter = mAdapter
                mAdapter?.notifyDataSetChanged()
            }
            recyclerview.visibility = if (list.size > 0) View.VISIBLE else View.GONE
            noData.visibility = if (list.size == 0) View.VISIBLE else View.GONE

            if (searchView.text.toString().isNotEmpty())
                mAdapter?.filter?.filter(searchView.text.toString().toLowerCase(DateTimeUtils.timeLocale))

            view_product_rel?.visibility = if (list.size > 0) {
                View.VISIBLE
            } else {
                View.GONE
            }

            if (settingBean?.show_ecom_v2_theme == "1") {
                view_product_rel?.visibility = View.GONE
            }

            searchView.visibility = if (list.size > 0) View.VISIBLE else View.GONE
            tv_filter?.visibility = if (screenFlowBean?.app_type != AppDataType.Food.type && list.size > 0 &&
                    settingBean?.show_filters == "1") View.VISIBLE else View.GONE
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel.navigator = this

        val bundle = arguments


        EventBus.getDefault().register(this)


        if (bundle != null) {


            if (bundle.containsKey("subCategoryId")) {
                subCategoryId = bundle.getInt("subCategoryId", 0)
            }

            categoryId = if (bundle.containsKey("cat_id")) {
                bundle.getInt("cat_id", 0)
            } else {
                bundle.getInt("categoryId", 0)
            }


            if (bundle.containsKey("supplierId")) {
                supplierBranchId = bundle.getInt("supplierId", 0)
            }

            if (bundle.containsKey("question_list")) {
                mQuestionList = bundle.getParcelableArrayList("question_list")
                        ?: arrayListOf()
            }
        }
    }

    private fun initialization() {
        tvEditOrder?.text = getString(R.string.edit_order_detail, mProductBinding.strings?.order)
        mAdapter = ProductListingAdapter(activity
                ?: requireContext(), list, appUtils, settingBean?.show_ecom_v2_theme,selectedCurrency)
        mAdapter?.settingCallback(this)
        mAdapter?.settingLayout(viewModel.isViewType.get())


        if (settingBean?.show_ecom_v2_theme == "1") {
            val itemDecoration: ItemDecoration = DividerItemDecoration(activity, VERTICAL)
            recyclerview.addItemDecoration(itemDecoration)

            viewModel.isViewType.set(false)
        }

        recyclerview.layoutManager = setLayoutManager(viewModel.isViewType.get())

        recyclerview.adapter = mAdapter


        searchView.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {

            }

            override fun afterTextChanged(s: Editable) {

                mAdapter?.filter?.filter(s.toString().toLowerCase(DateTimeUtils.timeLocale))
                recyclerview.scrollToPosition(0)
                searchView.clearFocus()
                //  val  keyboard = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                //  keyboard.hideSoftInputFromWindow(view?.windowToken,0)

                if (mAdapter != null && mAdapter?.itemCount ?: 0 > 0) {
                    noData.visibility = View.GONE
                    recyclerview.scrollToPosition(0)
                } else
                    noData.visibility = View.VISIBLE
            }
        })


    }


    private fun productListObserver() {
        // Create the observer which updates the UI.
        val catObserver = Observer<PagingResult<ProductData>> { resource ->
            // Update the UI, in this case, a TextView.

            if (resource != null) {
                if (resource.isFirstPage) {
                    list.clear()
                }
                handleProductList(resource.result)
            }

        }

        viewModel.productLiveData.observe(viewLifecycleOwner, catObserver)

    }

    private fun handleProductList(resource: ProductData?) {
        if (resource?.product?.count() ?: 0 > 0) {
            if (orderDetail != null && arguments?.containsKey("isEditOrder") == true) changeProductListEditOrder(resource?.product)
            else changeProductList(resource?.product)
            setdata(resource)

            tv_search_count.text = getString(R.string.result_tag, resource?.product?.count() ?: 0)
            tv_search_count.visibility = if (resource?.product?.count() ?: 0 > 0) View.VISIBLE else View.GONE

        }

        recyclerview.visibility = if (resource?.product?.count() ?: 0 > 0) View.VISIBLE else View.GONE
        noData.visibility = if (resource?.product?.count() ?: 0 == 0) View.VISIBLE else View.GONE

        view_product_rel.visibility = if (resource?.product?.count() ?: 0 > 0) View.VISIBLE else View.GONE

        if (settingBean?.show_ecom_v2_theme == "1") {
            view_product_rel?.visibility = View.GONE
        }


        searchView.visibility = if (resource?.product?.count() ?: 0 > 0) View.VISIBLE else View.GONE
        tv_filter?.visibility = if (screenFlowBean?.app_type != AppDataType.Food.type && resource?.product?.count() ?: 0 > 0
        /*&& settingBean?.show_filters=="1"*/) View.VISIBLE else View.GONE

    }


    private fun apiAllProducts(subCategoryId: Int, supplierBranchId: Int) {


        inputModel.languageId = StaticFunction.getLanguage(activity).toString()

        if (subCategoryId != 0 && hasSubcat)
            inputModel.subCategoryId?.add(subCategoryId)


        if (categoryId != 0) {
            inputModel.subCategoryId?.add(categoryId)
        }


        val mLocUser = dataManager.getGsonValue(PrefenceConstants.ADRS_DATA, AddressBean::class.java)

        if (mLocUser != null) {
            inputModel.latitude = mLocUser.latitude ?: ""
            inputModel.longitude = mLocUser.longitude ?: ""
        }
        inputModel.low_to_high = 1.toString()
        inputModel.is_availability = 1.toString()
        inputModel.is_discount = 0.toString()
        inputModel.max_price_range = 100000.toString()
        inputModel.min_price_range = 0.toString()


        if (hasBrands) {
            inputModel.brand_ids?.add(brandId)
        }

        if (screenFlowBean?.app_type == AppDataType.HomeServ.type) {
            inputModel.need_agent = 1
        }


        if (supplierBranchId != 0) {
            inputModel.supplier_ids?.add(supplierBranchId.toString())
        }


        callApi(inputModel, true)
    }

    private fun callApi(inputModel: FilterInputModel, isFirstPage: Boolean) {
        if (isNetworkConnected) {
            viewModel.getProductList(inputModel, isFirstPage)
        }
    }

    private fun changeProductList(product: MutableList<ProductDataBean>?) {
        for (j in product?.indices!!) {
            val dataProduct = product[j]
            val productId = dataProduct.product_id

            dataProduct.prod_quantity = StaticFunction.getCartQuantity(activity, productId)
            dataProduct.self_pickup = mDeliveyType

            //for fixed price
            dataProduct.let {
                prodUtils.changeProductList(true, dataProduct)
            }
        }
    }

    private fun changeProductListEditOrder(product: MutableList<ProductDataBean>?) {
        if (product != null) {
            for (j in product.indices) {
                val dataProduct = product[j]
                val index = orderDetail?.product?.indexOfFirst {
                    it?.product_id == dataProduct.product_id
                }
                if (index != null && index != -1)
                    dataProduct.prod_quantity =
                            (orderDetail?.duration ?: 60).div(dataProduct.duration ?: 60).toFloat()

                //for fixed price
                dataProduct.let {
                    prodUtils.changeProductList(false, dataProduct)
                }
            }
        }
    }

    private fun setdata(dataAllProduct: ProductData?) {
        tv_filter.visibility = if (screenFlowBean?.app_type == AppDataType.Food.type || settingBean?.show_filters != "1") View.INVISIBLE else View.VISIBLE

        list.addAll(dataAllProduct?.product ?: emptyList())

        tv_search_count.text = getString(R.string.result_tag, list.size)
        tv_search_count.visibility = View.VISIBLE

        iv_list_view.setColorFilter(unselectedColor, PorterDuff.Mode.MULTIPLY)
        iv_list_view.setColorFilter(selectedColor, PorterDuff.Mode.MULTIPLY)

        exampleAllProduct?.data = dataAllProduct

        mAdapter?.notifyDataSetChanged()
    }

    private fun setLayoutManager(type: Boolean): RecyclerView.LayoutManager {

        viewType = type

        return if (type) GridLayoutManager(activity, 2) else LinearLayoutManager(activity)
    }


    override fun addToCart(position: Int, agentType: Boolean) {


        val cartList = appUtils.getCartList()

        parentPos = position

        val appointmentAdded = cartList.cartInfos?.any { it.is_appointment == "1" }!!

        // if vendor status ==0 single vendor && vendor status==1 multiple vendor
        if (bookingFlowBean?.vendor_status == 0 && appUtils.checkVendorStatus(list[position].supplier_id,
                        vendorBranchId = list[position].supplier_branch_id, branchFlow = settingBean?.branch_flow)) {

            mDialogsUtil.openAlertDialog(activity
                    ?: requireContext(), getString(R.string.clearCart, textConfig?.supplier
                    ?: "", textConfig?.proceed ?: ""), "Yes", "No", this)
        } else if (appointmentAdded)
            appUtils.mDialogsUtil.openAlertDialog(activity
                    ?: requireContext(), getString(R.string.appointment_multiple_service_message),
                    "Okay", "", null)
        else if (!cartList.cartInfos.isNullOrEmpty() && list[position].is_appointment == "1")
            appUtils.mDialogsUtil.openAlertDialog(activity
                    ?: requireContext(), getString(R.string.appointment_multiple_service_message),
                    "Okay", "", null)
        else {
            addNewData(position)
        }


    }

    override fun bookNow(adapterPosition: Int, bean: ProductDataBean) {
        if (bean.prod_quantity ?: 0f > 0f) {
            if (dataManager.getCurrentUserLoggedIn()) {
                val productIds = ArrayList<String>()
                productIds.add(bean.product_id.toString())
                activity?.launchActivity<ServSelectionActivity>(AppConstants.REQUEST_AGENT_DETAIL)
                {
                    putExtra(DataNames.SUPPLIER_BRANCH_ID, supplierBranchId)
                    putExtra("screenType", "productList")
                    putExtra("serviceData", bean)
                    putExtra("productIds", productIds.toTypedArray())
                }
            } else {
                appUtils.checkLoginFlow(requireActivity(), AppConstants.REQUEST_CART_LOGIN_BOOKING)
            }
        } else {
            mProductBinding.root.onSnackbar(getString(R.string.add_quantity))
        }
    }

    private fun addNewData(position: Int) {
        if (orderDetail != null) {
            if (appUtils.checkBookingStatus(requireContext(), list, list[position].product_id
                            ?: 0, this)) {
                addCart(position, false)
            }
        } else {
            if (list[position].adds_on?.isNotEmpty() == true) {
                val cartList: CartList? = dataManager.getGsonValue(DataNames.CART, CartList::class.java)

                if (appUtils.checkProdExistance(list[position].product_id)) {
                    val savedProduct = cartList?.cartInfos?.filter {
                        it.supplierId == list[position].supplier_id && it.productId == list[position].product_id
                    } ?: emptyList()

                    SavedAddon.newInstance(list[position], mDeliveyType, savedProduct, this).show(childFragmentManager, "savedAddon")
                } else {
                    AddonFragment.newInstance(list[position], mDeliveyType, this).show(childFragmentManager, "addOn")
                }

            } else {
                if (appUtils.checkBookingFlow(requireContext(), list[position].product_id
                                ?: 0, this)) {

                    val addInDb: Boolean = !(settingBean?.enable_freelancer_flow == "1"  && list[position].is_appointment != "1")

                    addCart(position, addInDb)
                }
            }
        }

    }

    override fun onDestroy() {
        super.onDestroy()

        varientData = FilterVarientData()

        EventBus.getDefault().unregister(this)
    }

    override fun removeToCart(position: Int) {

        parentPos = position

        val mProduct = list[position]
        if (orderDetail == null) {
            if (mProduct.adds_on?.isNotEmpty() == true) {
                val cartList: CartList? = dataManager.getGsonValue(DataNames.CART, CartList::class.java)

                val savedProduct = cartList?.cartInfos?.filter {
                    it.supplierId == mProduct.supplier_id && it.productId == mProduct.product_id
                } ?: emptyList()

                SavedAddon.newInstance(mProduct, mDeliveyType, savedProduct, this).show(childFragmentManager, "savedAddon")
            } else {
                list[position] = mProduct.apply { prodUtils.removeItemToCart(mProduct) }
                mAdapter?.notifyItemChanged(position)
            }

            setPriceLayout()
        } else {
            list[position] = mProduct.apply { prodUtils.removeItemToCart(mProduct, false) }
            mAdapter?.notifyItemChanged(position)
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onMessageEvent(filterVarientData: FilterResponseEvent) {

        varientData = filterVarientData.filterModel


        if (filterVarientData.status == "success") {

            list.clear()

            if (filterVarientData.productlist.size > 0) {

                list.addAll(filterVarientData.productlist)

                isLoading = true
                refreshData()


                if (searchView.text.toString().isNotEmpty())
                    mAdapter?.filter?.filter(searchView.text.toString().toLowerCase(DateTimeUtils.timeLocale))
            } else {
                refreshData()
            }

            tv_search_count.visibility = if (list.size > 0) View.VISIBLE else View.GONE
            tv_search_count.text = getString(R.string.result_tag, list.size)
            tv_filter?.visibility = if (screenFlowBean?.app_type != AppDataType.Food.type && list.size > 0 /*&& settingBean?.show_filters=="1"*/) View.VISIBLE else View.GONE

        }
        mAdapter?.notifyDataSetChanged()
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

        if (requestCode == FILTERDATA) {

            isLoading = false

            if (resultCode == Activity.RESULT_OK) {

                list.clear()
                if (data?.hasExtra("varientData") == true) {
                    varientData = data.getParcelableExtra("varientData")
                }

                if (data?.hasExtra("result") == true) {
                    list.addAll(data.getParcelableArrayListExtra("result")!!)

                    isLoading = true
                    refreshData()
                }

                if (list.size > 0)
                    mAdapter?.filter?.filter(searchView.text.toString().toLowerCase(DateTimeUtils.timeLocale))

            }
            if (resultCode == Activity.RESULT_CANCELED) {

                refreshData()
            }

            mAdapter?.notifyDataSetChanged()
        } else if (requestCode == AppConstants.REQUEST_WISH_PROD && resultCode == Activity.RESULT_OK) {
            markFavList()
        } else if (requestCode == AppConstants.REQUEST_AGENT_DETAIL && resultCode == Activity.RESULT_OK) {
            val dataAgent = data?.getParcelableExtra<AgentCustomParam>("agentData")

            val productBean = data?.getParcelableExtra<ProductDataBean>("serviceData")

            if (productBean != null) {
                productBean.agentDetail = dataAgent
                if (productBean.agentBufferPrice != null)
                    productBean.netPrice = (productBean.netPrice
                            ?: 0.0f).plus(productBean.agentBufferPrice?.toFloat() ?: 0f)
                if (!appUtils.checkProdExistance(productBean.product_id))
                    appUtils.addProductDb(requireContext(), screenFlowBean?.app_type
                            ?: 0, productBean)
                else
                    StaticFunction.updateCart(requireContext(), productBean.product_id, productBean.prod_quantity,
                            productBean.netPrice ?: 0f)

                if (settingBean?.show_ecom_v2_theme == "1") {
                    navController(this@ProductTabListing).navigate(R.id.action_productTabListing_to_cartV2)
                } else {
                    navController(this@ProductTabListing).navigate(R.id.action_productTabListing_to_cart)
                }

            }

        }
    }

    private fun setPriceLayout() {

        val appCartModel = appUtils.getCartData(settingBean,selectedCurrency)

        bottom_cart.visibility = View.VISIBLE


        if (appCartModel.cartAvail) {

            tv_total_price.text = getString(R.string.total).plus(" ").plus(AppConstants.CURRENCY_SYMBOL).plus(" ").plus(appCartModel.totalPrice)
            tv_total_product.text = getString(R.string.total_item_tag, Utils.getDecimalPointValue(settingBean, appCartModel.totalCount))

            if (appCartModel.supplierName.isNotEmpty() && screenFlowBean?.is_single_vendor == VendorAppType.Multiple.appType) {
                tv_supplier_name.visibility = View.VISIBLE
                tv_supplier_name.text = getString(R.string.supplier_tag, appCartModel.supplierName)
            } else {
                tv_supplier_name.visibility = View.GONE
            }

            bottom_cart.setOnClickListener {
                if (settingBean?.show_ecom_v2_theme == "1") {
                    navController(this@ProductTabListing).navigate(R.id.action_productTabListing_to_cartV2)
                } else {
                    navController(this@ProductTabListing).navigate(R.id.action_productTabListing_to_cart)
                }
            }
        } else {
            bottom_cart.visibility = View.GONE
        }
    }


    override fun productDetail(bean: ProductDataBean?) {
        viewModel.setViewType(viewType)

        if (screenFlowBean?.app_type != AppDataType.Food.type || (settingBean?.product_detail == "1" && screenFlowBean?.app_type == AppDataType.Food.type)) {
            val bundle = Bundle()
            bundle.putInt("productId", bean?.product_id ?: 0)
            bundle.putString("title", bean?.name)
            bundle.putInt("categoryId", bean?.category_id ?: 0)

            if (screenFlowBean?.app_type == AppDataType.Food.type && settingBean?.show_supplier_detail != "1") {

                bundle.putInt("categoryId", bean?.category_id ?: 0)
                bundle.putInt("supplierId", bean?.supplier_id ?: 0)
                bundle.putInt("branchId", bean?.supplier_branch_id ?: 0)

                navController(this@ProductTabListing)
                        .navigate(R.id.action_productTabListing_to_restaurantDetailFrag, bundle)
            } else {
                bean?.selectQuestAns = mQuestionList

                if (settingBean?.show_ecom_v2_theme == "1") {
                    navController(this@ProductTabListing)
                            .navigate(R.id.actionProductDetailV2, ProductDetails.newInstance(bean, 0, true))

                } else {
                    navController(this@ProductTabListing)
                            .navigate(R.id.actionProductDetail, ProductDetails.newInstance(bean, 0, true))
                }
            }
        }
    }

    override fun publishResult(count: Int) {
        if (count == 0) {
            view_product_rel?.visibility = View.GONE
            tv_search_count.visibility = View.GONE
            /*tv_filter?.visibility=View.GONE*/
        } else {
            view_product_rel?.visibility = View.VISIBLE
            tv_filter?.visibility = View.VISIBLE
            tv_search_count.visibility = View.VISIBLE
            tv_search_count.text = getString(R.string.result_tag, count)
        }

        if (settingBean?.show_ecom_v2_theme == "1") {
            view_product_rel?.visibility = View.GONE
        }

    }

    override fun addtoWishList(adapterPosition: Int, status: Int?, productId: Int?) {

        parentPos = adapterPosition

        if (dataManager.getCurrentUserLoggedIn()) {
            if (isNetworkConnected) {
                viewModel.markFavProduct(productId, status)
            }
        } else {

            appUtils.checkLoginFlow(requireActivity(), AppConstants.REQUEST_WISH_PROD)
        }
    }


    private fun addCart(position: Int, isAddInDb: Boolean) {

        val mProduct = list[position]

        // if (mProduct.is_question == 1) {
        mProduct.selectQuestAns = mQuestionList
        //}

        mProduct.type = screenFlowBean?.app_type
        prodUtils.addItemToCart(mProduct, isAddInDb).let {

            // FOR SINGLE TYPE ADDRESS IS ALREADY SAVED IN SPLASH SCREEN
            if(screenFlowBean?.is_single_vendor != VendorAppType.Single.appType){
                if (mProduct.is_appointment == "1") {
                    val mRestUser = LocationUser()
                    mRestUser.address = "${mProduct.supplier_name} , ${
                        mProduct.supplier_address
                                ?: ""
                    }"
                    prefHelper.addGsonValue(PrefenceConstants.RESTAURANT_INF, Gson().toJson(mRestUser))
                }
            }


            mAdapter?.notifyItemChanged(position)
        }

        if (isAddInDb && settingBean?.enable_freelancer_flow != "1")
            setPriceLayout()
    }


    override fun onSucessListner() {

        if (orderDetail == null) {
            appUtils.clearCart()
            list.map {
                it.prod_quantity = 0f
            }

            mAdapter?.notifyDataSetChanged()
            setPriceLayout()
        } else {
            list.map {
                it.prod_quantity = 0f
            }

            mAdapter?.notifyDataSetChanged()
        }
    }

    override fun onErrorListener() {

    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.iv_grid_view -> {
                iv_list_view.setColorFilter(unselectedColor, PorterDuff.Mode.MULTIPLY)
                iv_grid_view.setColorFilter(selectedColor, PorterDuff.Mode.MULTIPLY)

                recyclerview.layoutManager = setLayoutManager(true)
                mAdapter?.settingLayout(true)
                recyclerview.adapter = mAdapter
            }

            R.id.iv_list_view -> {
                iv_list_view.setColorFilter(selectedColor, PorterDuff.Mode.MULTIPLY)
                iv_grid_view.setColorFilter(unselectedColor, PorterDuff.Mode.MULTIPLY)
                recyclerview.layoutManager = setLayoutManager(false)
                mAdapter?.settingLayout(false)
                recyclerview.adapter = mAdapter
            }

            R.id.tv_filter -> {

                //Intent intent = new Intent(getActivity(), FilterScreenActivity.class);


                if (varientData == null) {
                    varientData = FilterVarientData()

                    varientData?.catId = categoryId
                    varientData?.isAvailability = true
                    varientData?.sortBy = "Price: Low to High"
                    varientData?.isDiscount = false
                    varientData?.maxPrice = 100000
                    varientData?.minPrice = 0
                    //  varientData?.catNames?.addAll(MainActivity.catNames)
                    varientData?.varientID?.clear()
                    varientData?.subCatId?.clear()
                    if (supplierBranchId != 0) {
                        varientData?.supplierId?.add(supplierBranchId.toString())
                    }
                    varientData?.subCatId?.add(subCategoryId)
                }

                //   intent.putExtra("varientData", varientData);

                // startActivityForResult(intent, FILTERDATA);

                val bottomSheetFragment = BottomSheetFragment()
                val bundle = Bundle()
                bundle.putParcelable("varientData", varientData)
                bottomSheetFragment.arguments = bundle
                bottomSheetFragment.show(childFragmentManager, "bottom_sheet")
            }

            R.id.tvEditOrder -> {
                val selectedList = getSelectedItem()
                if (selectedList != null) {
                    editOrder(selectedList)
                } else
                    tvEditOrder?.onSnackbar(getString(R.string.please_choose_atlease_one_service))
            }
        }
    }


    override fun onAddonAdded(productModel: ProductDataBean) {
        if (productModel.adds_on?.isNullOrEmpty() == false && appUtils.checkProdExistance(productModel.product_id)) {
            val cartList: CartList? = appUtils.getCartList()
            productModel.prod_quantity = (cartList?.cartInfos?.filter { productModel.product_id == it.productId }?.sumByDouble { it.quantity.toDouble() }
                    ?: 0.0).toFloat()
        }

        list[parentPos] = productModel

        mAdapter?.notifyItemChanged(parentPos)
        mAdapter?.notifyDataSetChanged()

        setPriceLayout()
    }

    private fun getSelectedItem(): ProductDataBean? {
        val index = list.indexOfFirst { it.prod_quantity ?: 0f > 0f }
        return if (index != -1)
            list[index]
        else null
    }

    private fun editOrder(selectedItem: ProductDataBean) {
        val orderRequest = EditOrderRequest()
        orderRequest.orderId = orderDetail?.order_id.toString()
        getSelectedList(selectedItem, orderRequest)
        /*any number need to remove from backend*/
        orderRequest.sectionId = 0
        orderRequest.duration = selectedItem.duration?.times(selectedItem.prod_quantity?.toInt()
                ?: 0)
        orderRequest.pricing_type = selectedItem.price_type
        orderRequest.removalItems = getRemovalItems(orderRequest.items ?: arrayListOf())

        if (isNetworkConnected)
            viewModel.editOrderProducts(orderRequest)
    }

    private fun getRemovalItems(selectedList: ArrayList<EditProductsItem>): ArrayList<String?>? {
        val removalItems = ArrayList<String?>()
        orderDetail?.product?.forEach {
            val index = selectedList.indexOfFirst { it1 -> it1.productId == it?.product_id }
            if (index == -1)
                removalItems.add(it?.order_price_id)
        }
        return removalItems
    }


    private fun getSelectedList(
            product: ProductDataBean,
            orderRequest: EditOrderRequest
    ) {
        val geofenceItem = if (arguments != null && arguments?.containsKey("geofenceData") == true)
            arguments?.getParcelable<GeofenceData>("geofenceData") else null

        val selectedItem = ArrayList<EditProductsItem>()
        val itemInOrderDetail =
                orderDetail?.product?.find { it?.product_id == product.product_id }
        val item = EditProductsItem()
        item.productId = product.product_id
        item.imagePath = product.image_path.toString()
        item.productDesc = product.product_desc
        /*use static quantity for hourly basis services*/
        if (product.price_type == 1) {
            item.quantity = 1f
            item.price = product.netPrice?.times(product.prod_quantity ?: 1f)
        } else {
            item.quantity = product.prod_quantity
            item.price = product.netPrice
        }
        item.productName = product.name
        if (itemInOrderDetail != null)
            item.orderPriceId = itemInOrderDetail.order_price_id
        item.branchId = product.supplier_branch_id
        selectedItem.add(item)

        orderRequest.items = selectedItem

        val handlingAdmin =
                if (geofenceItem != null && geofenceItem.taxData?.firstOrNull()?.tax != null) geofenceItem.taxData.firstOrNull()?.tax?.toDouble() else product.handling_admin?.toDouble()
        orderRequest.handlingAdmin =
                ((item.quantity?.toDouble() ?: 0.0).times(handlingAdmin ?: 0.0)
                        .times(item.price?.toDouble() ?: 0.0)).div(100)

        orderRequest.userServiceCharge =
                ((item.price ?: 0f).times(item.quantity?.toFloat() ?: 0f)
                        .times(orderDetail?.user_service_charge?.toFloat() ?: 0f)).div(100)
    }


    override fun editOrderResponse(data: Data?) {
        AppToasty.success(requireContext(), getString(R.string.order_edited_success))
        activity?.finish()
    }

    private fun onRecyclerViewScrolled() {
        recyclerview.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                val isPagingActive = viewModel.validForPaging()


                if (recyclerView.layoutManager is LinearLayoutManager && !recyclerView.canScrollVertically(1) && isPagingActive) {
                    callApi(inputModel, false)
                } else if (recyclerView.layoutManager is GridLayoutManager) {

                    val layoutManager = recyclerView.layoutManager as GridLayoutManager
                    val visibleItemCount = layoutManager.findLastCompletelyVisibleItemPosition() + 1

                    if (visibleItemCount == layoutManager.itemCount && isPagingActive)
                        callApi(inputModel, false)
                }
            }
        })
    }

}
