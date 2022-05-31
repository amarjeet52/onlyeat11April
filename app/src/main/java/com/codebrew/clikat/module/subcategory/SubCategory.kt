package com.codebrew.clikat.module.subcategory

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BlendMode
import android.graphics.BlendModeColorFilter
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Parcelable
import android.view.MenuItem
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.PopupMenu
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.Navigation
import com.codebrew.clikat.BR
import com.codebrew.clikat.BuildConfig
import com.codebrew.clikat.R
import com.codebrew.clikat.app_utils.*
import com.codebrew.clikat.app_utils.extension.onSnackbar
import com.codebrew.clikat.base.BaseFragment
import com.codebrew.clikat.data.AppDataType
import com.codebrew.clikat.data.DataManager
import com.codebrew.clikat.data.FoodAppType
import com.codebrew.clikat.data.VendorAppType
import com.codebrew.clikat.data.constants.AppConstants
import com.codebrew.clikat.data.constants.PrefenceConstants
import com.codebrew.clikat.data.model.api.Currency
import com.codebrew.clikat.data.model.api.supplier_detail.DataSupplierDetail
import com.codebrew.clikat.databinding.FragmentSubCategoryBinding
import com.codebrew.clikat.di.ViewModelProviderFactory
import com.codebrew.clikat.modal.AppGlobal
import com.codebrew.clikat.modal.CartList
import com.codebrew.clikat.modal.ExampleAllSupplier
import com.codebrew.clikat.modal.other.*
import com.codebrew.clikat.module.addon_quant.SavedAddon
import com.codebrew.clikat.module.home_screen.adapter.BrandsListAdapter
import com.codebrew.clikat.module.home_screen.adapter.SponsorListAdapter
import com.codebrew.clikat.module.product.product_listing.DialogListener
import com.codebrew.clikat.module.product.product_listing.adapter.ProductListingAdapter
import com.codebrew.clikat.module.product_addon.AddonFragment
import com.codebrew.clikat.module.product_detail.ProductDetails
import com.codebrew.clikat.module.subcategory.adapter.SubCatAdapter
import com.codebrew.clikat.module.subcategory.adapter.SubCategorySelectAdapter
import com.codebrew.clikat.preferences.DataNames
import com.codebrew.clikat.utils.StaticFunction
import com.codebrew.clikat.utils.Utils
import com.codebrew.clikat.utils.configurations.Configurations
import kotlinx.android.synthetic.main.fragment_sub_category.*
import kotlinx.android.synthetic.main.layout_bottom_cart.*
import kotlinx.android.synthetic.main.toolbar_app.*
import okhttp3.internal.filterList
import pub.devrel.easypermissions.AppSettingsDialog
import pub.devrel.easypermissions.EasyPermissions
import javax.inject.Inject

/*
 * Created by Harman Sandhu on 20/4/16.
 */

class SubCategory : BaseFragment<FragmentSubCategoryBinding, SubCategoryViewModel>(),
        SubCategorySelectAdapter.SubCategoryCallback, BrandsListAdapter.BrandCallback,
        ProductListingAdapter.ProductCallback, DialogListener, SubCatAdapter.SubCatCallback,
        SubCategoryNavigator, AddonFragment.AddonCallback, SponsorListAdapter.SponsorDetail, EasyPermissions.PermissionCallbacks {


    private var selectedCurrency: Currency?=null
    private var clientInfom: SettingModel.DataBean.SettingData? = null
    private var supplierId: Int = 0
    private var categoryId: Int = 0
    private var subCategoryList = ArrayList<com.codebrew.clikat.modal.other.SubCategory>()
    private var subCategoryId: Int = 0
    private var isSuplier = false
    private var mDeliveryType: Int = 0
    private var madapter: SubCatAdapter? = null
    private var searchType = 0

    private var mPoductAdapter: ProductListingAdapter? = null

    private var mSubCatLists = mutableListOf<SubCatList>()

    private var bookingFlowBean: SettingModel.DataBean.BookingFlowBean? = null

    private var screenFlowBean: SettingModel.DataBean.ScreenFlowBean? = null

    private var supplerDetail: DataSupplierDetail? = null

    var category: ArrayList<SubCategoryData>? = null


    private var list = mutableListOf<ProductDataBean>()

    @Inject
    lateinit var permissionUtil: PermissionFile

    @Inject
    lateinit var dataManager: DataManager

    @Inject
    lateinit var appUtils: AppUtils

    @Inject
    lateinit var prodUtils: ProdUtils

    @Inject
    lateinit var mDialogsUtil: DialogsUtil

    @Inject
    lateinit var factory: ViewModelProviderFactory

    private var mBinding: FragmentSubCategoryBinding? = null

    private lateinit var mViewModel: SubCategoryViewModel

    private val textConfig by lazy { appUtils.loadAppConfig(0).strings }
    private val colorConfig by lazy { Configurations.colors }

    private var supplierBean: SupplierInArabicBean? = null

    private var parentPos: Int = 0

    override fun getBindingVariable(): Int {
        return BR.viewModel
    }

    override fun getLayoutId(): Int {
        return R.layout.fragment_sub_category
    }

    override fun getViewModel(): SubCategoryViewModel {
        mViewModel = ViewModelProviders.of(this, factory).get(SubCategoryViewModel::class.java)
        return mViewModel
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel.navigator = this

        bookingFlowBean = dataManager.getGsonValue(DataNames.BOOKING_FLOW, SettingModel.DataBean.BookingFlowBean::class.java)
        screenFlowBean = dataManager.getGsonValue(DataNames.SCREEN_FLOW, SettingModel.DataBean.ScreenFlowBean::class.java)
        selectedCurrency = dataManager.getGsonValue(PrefenceConstants.CURRENCY_INF, Currency::class.java)
        clientInfom = dataManager.getGsonValue(DataNames.SETTING_DATA, SettingModel.DataBean.SettingData::class.java)
        subCatObserver()
        supplierDetailObserver()
        suppliersListObserver()

        arguments?.let {
            categoryId = it.getInt("categoryId", 0)
            subCategoryList = (it.getParcelableArrayList("subCategoryList") ?: ArrayList())
            subCategoryId = it.getInt("subCategoryId", 0)
            supplierId = it.getInt("supplierId", 0)
            isSuplier = it.getBoolean("is_supplier", false)
            category = it.getParcelableArrayList("subcategory")
            mDeliveryType = it.getInt("deliveryType", 0)
            supplierBean = it.getParcelable("supplierData")

            if (supplierBean != null) {
                categoryId = category?.get(0)?.category_id ?: 0
            }
        }
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mBinding = viewDataBinding
        mBinding?.colors = colorConfig
        mBinding?.strings = textConfig

        searchView.typeface = AppGlobal.regular
        mSubCatLists.clear()
        list.clear()

        settingToolbar()

        madapter = SubCatAdapter(mSubCatLists, appUtils, clientInfom?.show_ecom_v2_theme, clientInfom, textConfig,selectedCurrency)
        madapter?.setFragCallback(this, this)
        recyclerview.adapter = madapter


        if (isSuplier && supplierBean != null) {
            if (mViewModel.supllierLiveData.value == null) {
                callSuplierApi()
            } else {
                loadSupplieCategory(mViewModel.supllierLiveData.value)
            }

        } else {
            if (mViewModel.subCatLiveData.value == null) {
                apiGetSubCategories(categoryId, supplierId)
            } else {
                if (mViewModel.subCatLiveData.value?.sub_category_data?.firstOrNull()?.menu_type != 0)
                    setdata(mViewModel.subCatLiveData.value)
                else
                    Navigation.findNavController(mBinding?.root!!).popBackStack()
            }
        }

        setPriceLayout()

        if (BuildConfig.CLIENT_CODE == "dailyooz_0544" || BuildConfig.CLIENT_CODE == "dailyoozz_0865") {
            subCategorySearchContainer?.visibility = View.VISIBLE

            viewModel.supplierViaGetLiveData.observe(this, Observer {
                if (it?.isNotEmpty() == true) {

                    it.map { supplierData ->
                        supplierData.isOpen = appUtils.checkResturntTiming(supplierData.timing)
                    }

                    mSubCatLists.add(SubCatList("suppliers", it.sortedBy { !it.isOpen }))
                }
                viewModel.setSubCat(mSubCatLists.size)
                madapter?.notifyDataSetChanged()
            })
            viewModel.getSupplierListViaGet(searchView?.text.toString(), categoryId)



            viewModel.categoriesSearchLiveData.observe(this, Observer {
                if (it?.isNotEmpty() == true) {
                    mSubCatLists.add(SubCatList("subcategory", it))
                }
                viewModel.setSubCat(mSubCatLists.size)
                madapter?.notifyDataSetChanged()
            })



            viewModel.productLiveData.observe(viewLifecycleOwner, Observer {
                if (it?.product?.isNotEmpty() == true) {

                    list.addAll(it.product ?: listOf())

                    list.map { prod ->
                        prod.prod_quantity = StaticFunction.getCartQuantity(activity, prod.product_id)

                        prod.let {
                            prodUtils.changeProductList(true, prod)
                        }
                    }

                    mSubCatLists.add(SubCatList("products", list))
                } else {
                    val index = mSubCatLists.indexOfFirst { item -> item.name == "products" }
                    if (index >= 0) {
                        mSubCatLists.removeAt(index)
                    }
                }
                viewModel.setSubCat(mSubCatLists.size)
                madapter?.notifyDataSetChanged()
            })


            searchView?.setOnEditorActionListener { _, actionId, _ ->
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    mSubCatLists.clear()
                    if (searchView?.text?.trim()?.isNotEmpty() == true) {
                        doSearch()
                    }
                    true
                } else false
            }


            tvSearchType?.setOnClickListener {
                showCategoriesPopUp()
            }
        }
    }

    private fun doSearch() {
        when (searchType) {
            0 -> {
//                viewModel.getCategoriesSearch(searchView?.text.toString())
                val listHolder = mViewModel.subCatLiveData.value?.sub_category_data as MutableList<SubCategoryData>
                val filteredList = listHolder.filterList {
                    this.name?.toLowerCase()?.contains(searchView?.text.toString().toLowerCase())
                            ?: false
                }
                mSubCatLists.clear()
                if (filteredList.isNotEmpty()) {
                    mSubCatLists.add(SubCatList("subcategory", filteredList))
                }
                viewModel.setSubCat(mSubCatLists.size)
                madapter?.notifyDataSetChanged()
            }
            1 -> {
                val subCategoryIdList = subCategoryList.map { it.id ?: 0 } as ArrayList
                viewModel.getProductList(searchView?.text.toString(), requireActivity(), categoryId, subCategoryIdList)
            }
            2 -> {
                viewModel.getSupplierListViaGet(searchView?.text.toString(), categoryId)
            }
        }
    }

    private fun showCategoriesPopUp() {
        val popup = PopupMenu(requireContext(), tvSearchType)
        popup.inflate(R.menu.popup_search_type)

        popup.setOnMenuItemClickListener { item: MenuItem? ->
            tvSearchType.text = item?.title

            mSubCatLists.clear()
            searchType = when (item?.itemId) {
                R.id.search_by_category -> {
                    0
                }
                R.id.search_by_product -> {
                    1
                }
                R.id.search_by_restaurant -> {
                    2
                }
                else -> {
                    -1
                }
            }
            if (searchView?.text?.trim()?.isNotEmpty() == true) {
                doSearch()
            }
            true
        }
        popup.show()
    }


    private fun callSuplierApi() {
        mViewModel.fetchSuplierDetail(supplierBean?.supplier_branch_id
                ?: 0, category?.get(0)?.category_id ?: 0, supplierBean?.id ?: 0)
    }

    private fun settingToolbar() {
        tb_title.text = getString(R.string.select_category)

        tb_back.setOnClickListener {
            Navigation.findNavController(mBinding?.root!!).popBackStack()
        }

        if (clientInfom?.show_ecom_v2_theme == "1") {
            toolbar?.background = ContextCompat.getDrawable(requireActivity(), R.drawable.background_toolbar_bottom_radius)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                toolbar?.background?.colorFilter = BlendModeColorFilter(Color.parseColor(colorConfig.toolbarColor), BlendMode.SRC_ATOP)
            }
            toolbar?.elevation = 0f
        }
    }


    private fun subCatObserver() {
        // Create the observer which updates the UI.
        val catObserver = Observer<SubCatData> { resource ->
            /*  if (AppDataType.Food.type == screenFlowBean?.app_type) {
                  if (resource?.sub_category_data?.firstOrNull()?.menu_type == 1) {
                      setdata(resource)
                  } else {
                      val bundle = Bundle()
                      bundle.putInt("categoryId", categoryId ?: 0)
                      bundle.putInt("subCategoryId", subCategoryId)

                      navController(this@SubCategory)
                              .navigate(R.id.action_supplierAll, bundle)

                  }
              } else*/
            setdata(resource)
        }

        // Observe the LiveData, passing in this activity as the LifecycleOwner and the observer.
        viewModel.subCatLiveData.observe(this, catObserver)
    }

    private fun supplierDetailObserver() {
        // Create the observer which updates the UI.
        val catObserver = Observer<DataSupplierDetail> { resource ->
            // Update the UI, in this case, a TextView.
            loadSupplieCategory(resource)

        }

        // Observe the LiveData, passing in this activity as the LifecycleOwner and the observer.
        mViewModel.supllierLiveData.observe(this, catObserver)

    }

    private fun loadSupplieCategory(resource: DataSupplierDetail?) {
        mSubCatLists.clear()

        supplerDetail = resource
        mSubCatLists.add(SubCatList("subcategory", supplerDetail?.category as ArrayList<SubCategoryData>))
        viewModel.setSubCat(mSubCatLists.size)
        madapter?.notifyDataSetChanged()
    }


    private fun apiGetSubCategories(categoryId: Int, supplierId: Int) {

        val hashMap = dataManager.updateUserInf()
        if (supplierId != 0) {
            hashMap["supplier_id"] = "" + supplierId
        }
        hashMap["category_id"] = (if (subCategoryId > 0) subCategoryId else categoryId).toString()
        if (isNetworkConnected) {
            mViewModel.getSubCategory(hashMap)
        }
    }

    override fun onErrorOccur(message: String) {
        mBinding?.root?.onSnackbar(message)
    }

    override fun onSessionExpire() {
        openActivityOnTokenExpire()
    }

    private fun setdata(exampleSubCategories: SubCatData?) {

        if ((BuildConfig.CLIENT_CODE == "dailyooz_0544" || BuildConfig.CLIENT_CODE == "dailyoozz_0865") && viewModel.supplierViaGetLiveData.value?.isEmpty() == true) {
            mSubCatLists.clear()
            list.clear()
        }

        if (exampleSubCategories?.sub_category_data?.isNotEmpty() == true) {
            mSubCatLists.add(0, SubCatList("subcategory", exampleSubCategories.sub_category_data))
        }

        if (exampleSubCategories?.offer?.isNotEmpty() == true) {
            changeProductList(exampleSubCategories.offer)

            list.addAll(exampleSubCategories.offer)
            mSubCatLists.add(SubCatList("offer", exampleSubCategories.offer))
        }

        if (exampleSubCategories?.brand?.isNotEmpty() == true) {
            mSubCatLists.add(SubCatList("brand", exampleSubCategories.brand))
        }
        if (clientInfom?.is_custom_category_template == "1")
            updateSubCategorySuppliersData(exampleSubCategories?.supplier_with_subcategory)

        viewModel.setSubCat(mSubCatLists.size)
        madapter?.notifyDataSetChanged()

    }

    private fun updateSubCategorySuppliersData(resource: ArrayList<SuppliersList>?) {
        if (resource?.isEmpty() == true) return
        for (i in 0 until (resource?.size ?: 0)) {
            if (!resource?.get(i)?.suppliers.isNullOrEmpty()) {
                mSubCatLists.add(SubCatList(resource?.get(i)?.sub_category_name, resource?.get(i)?.suppliers as ArrayList<SupplierInArabicBean>, true))
            }
        }
    }

    override fun onSubCategoryDtail(dataBean: SubCategoryData) {
        val bundle = bundleOf("title" to dataBean.name,
                "supplierId" to supplierId,
                "categoryId" to categoryId,
                "subCategoryId" to dataBean.category_id,
                "deliveryType" to mDeliveryType,
                "is_supplier" to isSuplier)

        if ((dataBean.is_cub_category == 1 && screenFlowBean?.app_type != AppDataType.Food.type ) ||
                (dataBean.menu_type == 1 && screenFlowBean?.app_type == AppDataType.Food.type && dataBean.is_cub_category == 1)
                && (clientInfom?.show_supplier_detail != "1" && clientInfom?.is_expactor != "1")) {
            bundle.putInt("parent_category", arguments?.getInt("parent_category") ?: 0)
            navController(this@SubCategory)
                    .navigate(R.id.action_subCategory_self, bundle)
        } else {
            if (dataBean.is_question == 1) {
                bundle.putBoolean("is_Category", true)
                navController(this@SubCategory)
                        .navigate(R.id.action_subCategory_to_questionFrag, bundle)
            } else {
                if (arguments?.getBoolean("is_supplier") == false
                        && screenFlowBean?.is_single_vendor == VendorAppType.Multiple.appType) {

                    bundle.putInt("categoryId", categoryId)
                    bundle.putInt("subCatId", dataBean.category_id ?: 0)

                    getAllSuppliers(dataBean.category_id ?: 0, bundle, false)

                } else {
                    bundle.putBoolean("has_subcat", true)
                    navController(this@SubCategory)
                            .navigate(R.id.action_productListing, bundle)
                }
            }
        }

    }

    private var bundleAllSuppliers: Bundle? = null
    private fun getAllSuppliers(subCategory: Int, bundle: Bundle, isSubCat: Boolean) {
        bundleAllSuppliers = bundle
        if (clientInfom?.skip_suppliers_list == "1") {
            val hashMap = dataManager.updateUserInf()
            if (isSubCat)
                hashMap["subCat"] = subCategory.toString()
            hashMap["categoryId"] = categoryId.toString()

            if (isNetworkConnected) {
                mViewModel.getSuppliers(hashMap)
            }
        } else {
            navController(this@SubCategory)
                    .navigate(R.id.action_supplierAll, bundle)
        }
    }

    private fun suppliersListObserver() {
        // Create the observer which updates the UI.
        val supplierObserver = Observer<ExampleAllSupplier> { resource ->
            if (resource?.data?.supplierList?.isNotEmpty() == true && resource.data?.supplierList?.size == 1) {
                bundleAllSuppliers?.putBoolean("has_subcat", true)
                bundleAllSuppliers?.putInt("supplierId", resource.data?.supplierList?.firstOrNull()?.id
                        ?: 0)
                if (bundleAllSuppliers?.containsKey("subCatId") == true)
                    bundleAllSuppliers?.putInt("cat_id", bundleAllSuppliers?.getInt("subCatId", 0)
                            ?: 0)
                navController(this).navigate(R.id.action_productListing, bundleAllSuppliers)
            } else {
                navController(this@SubCategory)
                        .navigate(R.id.action_supplierAll, bundleAllSuppliers)
            }
        }

        // Observe the LiveData, passing in this activity as the LifecycleOwner and the observer.
        viewModel.suppliersList.observe(this, supplierObserver)
    }


    override fun onBrandSelect(brandsBean: Brand) {

        val bundle = bundleOf("brand_id" to brandsBean.id,
                "has_brands" to true,
                "title" to brandsBean.name)

        if (supplierId > 0) {
            bundle.putInt("supplierId", supplierId)
        }

        navController(this@SubCategory)
                .navigate(R.id.action_productListing, bundle)
    }

    override fun addToCart(position: Int, agentType: Boolean) {

        parentPos = position

        if (bookingFlowBean?.vendor_status == 0 && appUtils.checkVendorStatus(list[position].supplier_id, vendorBranchId = list[position].supplier_branch_id, branchFlow = clientInfom?.branch_flow)) {

            mDialogsUtil.openAlertDialog(activity
                    ?: requireContext(), getString(R.string.clearCart, textConfig?.supplier
                    ?: "", textConfig?.proceed ?: ""), "Yes", "No", this)
        } else {
            if (list[position].adds_on?.isNotEmpty() == true) {
                val cartList: CartList? = dataManager.getGsonValue(DataNames.CART, CartList::class.java)

                if (appUtils.checkProdExistance(list[position].product_id)) {
                    val savedProduct = cartList?.cartInfos?.filter {
                        it.supplierId == list[position].supplier_id && it.productId == list[position].product_id
                    } ?: emptyList()

                    SavedAddon.newInstance(list[position], list[position].self_pickup
                            ?: -1, savedProduct, this).show(childFragmentManager, "savedAddon")
                } else {
                    AddonFragment.newInstance(list[position], list[position].self_pickup
                            ?: -1, this).show(childFragmentManager, "addOn")
                }

            } else {
                if (appUtils.checkBookingFlow(requireContext(), list[position].product_id
                                ?: 0, this)) {
                    addCart(position)
                }
            }
        }
    }

    override fun bookNow(adapterPosition: Int, bean: ProductDataBean) {
        //do nothing
    }

    override fun removeToCart(position: Int) {

        parentPos = position

        val mProduct = list[position]

        if (mProduct.adds_on?.isNotEmpty() == true) {

            val savedProduct = appUtils.getCartList().cartInfos?.filter {
                it.supplierId == mProduct.supplier_id && it.productId == mProduct.product_id
            } ?: emptyList()

            SavedAddon.newInstance(mProduct, list[position].self_pickup
                    ?: -1, savedProduct, this).show(childFragmentManager, "savedAddon")

        } else {
            list[position] = mProduct.apply { prodUtils.removeItemToCart(mProduct) }
            mPoductAdapter?.notifyItemChanged(position)
        }

        updateOfferList(position)

        updateProductList(position)
    }

    override fun productDetail(bean: ProductDataBean?) {

        if (clientInfom?.isProductDetailCheck != null && clientInfom?.isProductDetailCheck == "1") return

        bean?.self_pickup = mDeliveryType

        if (clientInfom?.show_ecom_v2_theme == "1") {
            navController(this@SubCategory)
                    .navigate(R.id.actionProductDetailV2, ProductDetails.newInstance(bean, 0, false))
        } else {
            navController(this@SubCategory)
                    .navigate(R.id.action_productDetail, ProductDetails.newInstance(bean, 0, false))
        }


    }

    override fun publishResult(count: Int) {

    }

    override fun addtoWishList(adapterPosition: Int, status: Int?, productId: Int?) {

    }

    private fun addCart(position: Int) {

        parentPos = position

        val mProduct = list[position]

        mProduct.type = screenFlowBean?.app_type
        mProduct.apply { prodUtils.addItemToCart(mProduct) }

        updateOfferList(position)

        updateProductList(position)

    }

    override fun onSucessListner() {

        list.map {
            it.quantity == 0f
        }
        updateOfferList(-1)

        updateProductList(-1)
    }

    override fun onErrorListener() {

    }


    private fun changeProductList(product: List<ProductDataBean>) {
        product.map {
            it.quantity = StaticFunction.getCartQuantity(activity
                    ?: requireContext(), it.product_id)
            it.netPrice = it.fixed_price?.toFloatOrNull() ?: 0f

            it.let {
                prodUtils.changeProductList(false, it)
            }
        }
    }

    private fun updateOfferList(position: Int) {

        if (position != -1 && mSubCatLists.any { it.name == "offer" }) {
            mSubCatLists[position] = mSubCatLists.filter { it.name == "offer" }[position]
            mPoductAdapter?.notifyItemChanged(position)
        } else {
            mPoductAdapter?.notifyDataSetChanged()
        }
    }

    private fun updateProductList(position: Int) {

        /* if (position != -1) {
             mSubCatLists[position] = mSubCatLists.filter { it.name == "products" }[position]
             mPoductAdapter?.notifyItemChanged(position)
         } else {*/
        mPoductAdapter?.notifyDataSetChanged()
        //  }
    }


    private fun setPriceLayout() {
        val appCartModel = appUtils.getCartData(clientInfom,selectedCurrency)

        bottom_cart.visibility = View.VISIBLE

        if (appCartModel.cartAvail) {
            tv_total_price.text = activity?.getString(R.string.total).plus(" ").plus(AppConstants.CURRENCY_SYMBOL).plus(" ").plus(appCartModel.totalPrice)
            tv_total_product.text = getString(R.string.total_item_tag, Utils.getDecimalPointValue(clientInfom, appCartModel.totalCount))


            if (appCartModel.supplierName.isNotEmpty() && screenFlowBean?.is_single_vendor == VendorAppType.Multiple.appType) {
                tv_supplier_name.visibility = View.VISIBLE
                tv_supplier_name.text = getString(R.string.supplier_tag, appCartModel.supplierName)
            } else {
                tv_supplier_name.visibility = View.GONE
            }

            bottom_cart.setOnClickListener {
                if (clientInfom?.show_ecom_v2_theme == "1") {
                    navController(this@SubCategory).navigate(R.id.action_subCategory_to_cartV2)
                } else {
                    navController(this@SubCategory).navigate(R.id.action_subCategory_to_cart)
                }

            }
        } else {
            bottom_cart.visibility = View.GONE
        }
    }

    override fun onProdItemUpdate(adpater: ProductListingAdapter) {
        mPoductAdapter = adpater
    }

    override fun onSupplierDetail(supplierBean: SupplierDataBean?) {

        val bundle = bundleOf("supplierId" to supplierBean?.id)

        if (bookingFlowBean?.is_pickup_order == FoodAppType.Pickup.foodType) {
            bundle.putString("deliveryType", "pickup")
        }
        if (clientInfom?.app_selected_template != null && clientInfom?.app_selected_template == "1") {
            navController(this@SubCategory).navigate(R.id.action_restaurantDetailNew, bundle)
        } else {
            navController(this@SubCategory).navigate(R.id.action_restaurantDetail, bundle)
        }
    }

    override fun onAddonAdded(productModel: ProductDataBean) {

        if (productModel.adds_on?.isNullOrEmpty() == false && appUtils.checkProdExistance(productModel.product_id)) {
            val cartList: CartList? = appUtils.getCartList()
            productModel.prod_quantity = (cartList?.cartInfos?.filter { productModel.product_id == it.productId }?.sumByDouble { it.quantity.toDouble() }
                    ?: 0.0).toFloat()
        }

        setPriceLayout()

        updateProductList(parentPos)
    }

    override fun onSponsorDetail(supplier: SupplierInArabicBean?) {

        val bundle = bundleOf("supplierId" to supplier?.id,
                "title" to supplier?.name, "deliveryType" to mDeliveryType)

        if (supplier?.type == AppDataType.Food.type && clientInfom?.show_supplier_detail != "1") {
            bundle.putString("deliveryType", Utils.getDeliveryType(mDeliveryType ?: 0))

            if (clientInfom?.app_selected_template != null && clientInfom?.app_selected_template == "1") {
                navController(this@SubCategory).navigate(R.id.action_restaurantDetailNew, bundle)
            } else {
                navController(this@SubCategory).navigate(R.id.action_restaurantDetail, bundle)
            }
        } else {
            if (!supplier?.category.isNullOrEmpty()) {
                bundle.putBoolean("is_supplier", true)
                bundle.putParcelable("supplierData", supplier)
                bundle.putParcelableArrayList("subcategory", ArrayList<Parcelable>(supplier?.category
                        ?: mutableListOf()))
                navController(this@SubCategory)
                        .navigate(R.id.action_subCategory_self, bundle)
            } else {
                navController(this@SubCategory).navigate(R.id.action_productListing, bundle)
            }

        }
    }

    override fun onSupplierCall(supplier: SupplierInArabicBean?) {
        callSupplier(supplier?.supplierPhoneNumber ?: "")
    }

    private var phoneNum = ""

    private fun callSupplier(phone: String) {
        if (permissionUtil.hasCallPermissions(activity ?: requireContext())) {
            this.phoneNum = phone
            callPhone(phone)
        } else
            permissionUtil.phoneCallTask(activity ?: requireContext())

    }

    private fun callPhone(number: String) {

        val intent = Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", number, null))

        if (ContextCompat.checkSelfPermission(activity
                        ?: requireContext(), Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED) {
            Manifest.permission.CALL_PHONE
            startActivity(intent)
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(arrayOf(Manifest.permission.CALL_PHONE), 1)
            }
        }
    }

    override fun onSponsorWishList(supplier: SupplierInArabicBean?, parentPos: Int?, isChecked: Boolean) {
    }

    override fun onBookNow(supplierData: SupplierInArabicBean) {

    }

    override fun onPermissionsGranted(requestCode: Int, perms: MutableList<String>) {
        if (requestCode == AppConstants.REQUEST_CALL) {
            if (isNetworkConnected) {
                callPhone(phoneNum)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        searchView.text.clear()
        viewModel.productLiveData.value = null
    }

    override fun onPermissionsDenied(requestCode: Int, perms: MutableList<kotlin.String>) {
        if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
            AppSettingsDialog.Builder(this).build().show()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out kotlin.String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        // EasyPermissions handles the request result.
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this)
    }

}