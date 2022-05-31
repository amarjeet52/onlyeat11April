package com.codebrew.clikat.module.custom_home

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Parcelable
import android.view.View
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.codebrew.clikat.BR
import com.codebrew.clikat.R
import com.codebrew.clikat.app_utils.*
import com.codebrew.clikat.app_utils.extension.onSnackbar
import com.codebrew.clikat.base.BaseFragment
import com.codebrew.clikat.data.AppDataType
import com.codebrew.clikat.data.FoodAppType
import com.codebrew.clikat.data.VendorAppType
import com.codebrew.clikat.data.constants.AppConstants
import com.codebrew.clikat.data.constants.PrefenceConstants
import com.codebrew.clikat.data.model.api.AddressBean
import com.codebrew.clikat.data.model.api.Currency
import com.codebrew.clikat.data.model.others.HomeDataModel
import com.codebrew.clikat.data.network.HostSelectionInterceptor
import com.codebrew.clikat.data.preferences.PreferenceHelper
import com.codebrew.clikat.databinding.CustomHomeFragmentBinding
import com.codebrew.clikat.di.ViewModelProviderFactory
import com.codebrew.clikat.modal.CartList
import com.codebrew.clikat.modal.other.*
import com.codebrew.clikat.module.addon_quant.SavedAddon
import com.codebrew.clikat.module.dialog_adress.interfaces.AddressDialogListener
import com.codebrew.clikat.module.home_screen.adapter.CategoryListAdapter
import com.codebrew.clikat.module.home_screen.adapter.HomeItemAdapter
import com.codebrew.clikat.module.home_screen.adapter.SpecialListAdapter
import com.codebrew.clikat.module.home_screen.adapter.SponsorListAdapter
import com.codebrew.clikat.module.product.product_listing.DialogListener
import com.codebrew.clikat.module.product_addon.AddonFragment
import com.codebrew.clikat.module.product_detail.ProductDetails
import com.codebrew.clikat.preferences.DataNames
import com.codebrew.clikat.utils.DialogIntrface
import com.codebrew.clikat.utils.StaticFunction
import com.codebrew.clikat.utils.Utils
import com.codebrew.clikat.utils.configurations.Configurations
import com.google.gson.Gson
import kotlinx.android.synthetic.main.custom_home_fragment.*
import kotlinx.android.synthetic.main.layout_bottom_cart.*
import kotlinx.android.synthetic.main.toolbar_home.*
import pub.devrel.easypermissions.AppSettingsDialog
import pub.devrel.easypermissions.EasyPermissions
import retrofit2.Retrofit
import javax.inject.Inject


class CustomHomeFrag : BaseFragment<CustomHomeFragmentBinding, CustomHomeViewModel>(),
        CusHomeNavigator, SwipeRefreshLayout.OnRefreshListener, CategoryListAdapter.CategoryDetail
        , SpecialListAdapter.OnProductDetail, SponsorListAdapter.SponsorDetail, AddonFragment.AddonCallback,
        DialogListener, AddressDialogListener, DialogIntrface, EasyPermissions.PermissionCallbacks {


    companion object {
        fun newInstance() = CustomHomeFrag()
    }

    private var selectedCurrency: Currency?=null
    private lateinit var viewModel: CustomHomeViewModel

    @Inject
    lateinit var factory: ViewModelProviderFactory

    @Inject
    lateinit var retrofit: Retrofit

    @Inject
    lateinit var appUtils: AppUtils

    @Inject
    lateinit var prodUtils: ProdUtils

    @Inject
    lateinit var interceptor: HostSelectionInterceptor

    @Inject
    lateinit var prefHelper: PreferenceHelper

    @Inject
    lateinit var permissionUtil: PermissionFile

    private val textConfig by lazy { appUtils.loadAppConfig(0).strings }

    private var phoneNum=""

    private var screenFlowBean: SettingModel.DataBean.ScreenFlowBean? = null
    private var clientInform: SettingModel.DataBean.SettingData? = null
    private var bookingFlowBean: SettingModel.DataBean.BookingFlowBean? = null

    val mHomeData by lazy { HomeDataModel() }



    private var mBinding: CustomHomeFragmentBinding? = null

    private var homeItemAdapter: HomeItemAdapter? = null

    private var mSupplierList = mutableListOf<SupplierDataBean>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel.navigator = this

        screenFlowBean = prefHelper.getGsonValue(DataNames.SCREEN_FLOW, SettingModel.DataBean.ScreenFlowBean::class.java)
        clientInform = prefHelper.getGsonValue(DataNames.SETTING_DATA, SettingModel.DataBean.SettingData::class.java)
         selectedCurrency = prefHelper.getGsonValue(PrefenceConstants.CURRENCY_INF, Currency::class.java)
        bookingFlowBean = prefHelper.getGsonValue(DataNames.BOOKING_FLOW, SettingModel.DataBean.BookingFlowBean::class.java)
        categoryObserver()
        offersObserver()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mBinding = viewDataBinding

        val color = Configurations.colors
        color.toolbarColor = color.primaryColor
        color.toolbarText = color.appBackground

        viewDataBinding.color = color
        viewDataBinding.strings = textConfig

        /*iv_search.setImageDrawable(ContextCompat.getDrawable(activity
                ?: requireContext(), R.drawable.ic_more_terms))*/
        // toolbar_lyt.setBackgroundColor(Color.parseColor(Configurations.colors.primaryColor))

        //    toolbar.setBackgroundColor(Color.parseColor(Configurations.colors.primaryColor))

        swiprRefresh.setOnRefreshListener(this)

        AppConstants.APP_SUB_TYPE = AppConstants.APP_SAVED_SUB_TYPE
        val mLocUser = prefHelper.getGsonValue(PrefenceConstants.ADRS_DATA, AddressBean::class.java)

        homeItemAdapter = HomeItemAdapter(mSupplierList, FoodAppType.Both.foodType, appUtils, clientInform,mLocUser,screenFlowBean,selectedCurrency)
        homeItemAdapter?.setFragCallback(this)
        //homeItemAdapter?.settingCallback(this)

        val homelytManager = LinearLayoutManager(activity, RecyclerView.VERTICAL, false)

        rv_homeItem.layoutManager = homelytManager
        rv_homeItem.adapter = homeItemAdapter

        if (isNetworkConnected) {
            fetchCategories()
        }

        settingToolbar()
        showBottomCart()

    }

    override fun onResume() {
        super.onResume()
        saveSubAppType(AppConstants.APP_SUB_TYPE)
    }

    private fun settingToolbar() {
        val mLocUser = prefHelper.getGsonValue(PrefenceConstants.ADRS_DATA, AddressBean::class.java)
                ?: return

        updateToolbar(appUtils.getAddressFormat(mLocUser) ?: "")
    }


    private fun updateToolbar(mLocation: String) {
        tvArea.text = mLocation
    }


    private fun categoryObserver() {
        // Create the observer which updates the UI.
        val catObserver = Observer<Data> { resource ->

            updateCategoryData(resource)
        }

        // Observe the LiveData, passing in this activity as the LifecycleOwner and the observer.
        viewModel.homeDataLiveData.observe(this, catObserver)
    }

    private fun offersObserver() {
        // Create the observer which updates the UI.
        val offerObserver = Observer<OfferDataBean> { resource ->
            updateOfferData(resource)
        }

        // Observe the LiveData, passing in this activity as the LifecycleOwner and the observer.
        viewModel.offersLiveData.observe(this, offerObserver)

    }

    private fun updateOfferData(resource: OfferDataBean?) {
        mHomeData.offerData = resource
        setViewType(HomeItemAdapter.SPL_PROD_TYPE)
        setViewType(HomeItemAdapter.RECOMEND_TYPE)
    }


    private fun fetchOffers() {
        if (isNetworkConnected) {
            viewModel.getOfferList()
        }
    }

    private fun fetchCategories() {
        if (isNetworkConnected) {

            if (viewModel.homeDataLiveData.value != null) {
                updateCategoryData(viewModel.homeDataLiveData.value)
            } else {
                CommonUtils.checkAppDBKey(prefHelper.getKeyValue(
                        PrefenceConstants.DB_SECRET,
                        PrefenceConstants.TYPE_STRING)!!.toString(), interceptor)
                viewModel.getCategories()
            }
        }
    }


    private fun updateCategoryData(resource: Data?) {

        mSupplierList.clear()

        mHomeData.data = resource
        // setViewType(HomeItemAdapter.BANNER_TYPE)
        setViewType(HomeItemAdapter.CATEGORY_TYPE)


        if (viewModel.offersLiveData.value != null) {
            updateOfferData(viewModel.offersLiveData.value)
        } else {
            fetchOffers()
        }

    }


    override fun getBindingVariable(): Int {
        return BR.viewModel
    }

    override fun getLayoutId(): Int {
        return R.layout.custom_home_fragment
    }

    override fun getViewModel(): CustomHomeViewModel {
        viewModel = ViewModelProviders.of(this, factory).get(CustomHomeViewModel::class.java)
        return viewModel
    }

    override fun onErrorOccur(message: String) {
        mBinding?.root?.onSnackbar(message)
    }

    override fun onSessionExpire() {
        openActivityOnTokenExpire()
    }


    private fun setViewType(viewType: String) {
        val dataBean = SupplierDataBean()

        val itemModel = HomeItemModel()
        itemModel.screenType = AppConstants.APP_SUB_TYPE
        itemModel.isSingleVendor = screenFlowBean?.is_single_vendor ?: -1

        dataBean.viewType = viewType

        if (viewType == HomeItemAdapter.BANNER_TYPE) {

            itemModel.bannerWidth = clientInform?.app_banner_width?.toInt() ?: 0

            if (mHomeData.data?.topBanner?.count() ?: 0 > 0) {
                itemModel.bannerList = mHomeData.data?.topBanner

                dataBean.itemModel = itemModel
                mSupplierList.add(dataBean)
            }

        } else if (viewType == HomeItemAdapter.CATEGORY_TYPE) {

            if (mHomeData.data?.english?.isNotEmpty() == true) {

                if (mHomeData.data?.english?.count() ?: 0 > 0) {
                    itemModel.categoryList = mHomeData.data?.english

                    dataBean.itemModel = itemModel
                    mSupplierList.add(dataBean)
                }
            }
        } else if (viewType == HomeItemAdapter.SPL_PROD_TYPE) {
            if (mHomeData.offerData?.getOfferByCategory?.isEmpty() == true) return


            mHomeData.offerData?.getOfferByCategory?.forEachIndexed { index, getOfferByCategory ->

                if (getOfferByCategory.value.isNotEmpty()) {
                    itemModel.mSpecialOfferName = getOfferByCategory.name

                    val mProdList = getOfferByCategory.value.map {
                        it.let {
                            prodUtils.changeProductList(false,it)
                        }


                    }

                    itemModel.specialOffers = mProdList.toMutableList()

                    dataBean.itemModel = itemModel.copy()
                    mSupplierList.add(dataBean.copy())
                }

            }


        } else if (viewType == HomeItemAdapter.RECOMEND_TYPE) {
            if (mHomeData.offerData?.supplierInArabic?.isEmpty() == true) return

            itemModel.sponserList = mHomeData.offerData?.supplierInArabic

            dataBean.itemModel = itemModel
            mSupplierList.add(dataBean)
        } else {
            dataBean.itemModel = itemModel
            mSupplierList.add(dataBean)
        }

    }

    override fun onRefresh() {

        swiprRefresh.isRefreshing = false

        clearViewModelApi()
    }

    private fun clearViewModelApi() {

        viewModel.homeDataLiveData.value = null
        viewModel.offersLiveData.value = null
        fetchCategories()
    }

    override fun onCategoryDetail(bean: English?) {

        saveSubAppType(bean?.type)

        saveCategoryInf(bean)

        prefHelper.setkeyValue(PrefenceConstants.CATEGORY_ID, bean?.id.toString())

        // navController(this@CustomHomeFrag).navigate(R.id.action_customHomeFrag_to_mainFragment)


        val bundle = bundleOf("title" to bean?.name,
                "categoryId" to bean?.id,
                "subCategoryId" to 0)

        if (screenFlowBean?.is_single_vendor == VendorAppType.Multiple.appType) {

            if (bean?.sub_category?.count() ?: 0 > 0 && screenFlowBean?.app_type != AppDataType.Food.type) {
                navController(this@CustomHomeFrag).navigate(R.id.action_customHomeFrag_to_subCategory, bundle)
            } else {
                navController(this@CustomHomeFrag)
                        .navigate(R.id.action_supplierAll, bundle)
            }
        } else {
            if (screenFlowBean?.app_type != AppDataType.Food.type) {
                if (bean?.sub_category?.count() ?: 0 > 0) {



                    navController(this@CustomHomeFrag).navigate(R.id.action_customHomeFrag_to_subCategory, bundle)
                } else {
                    bundle.putBoolean("has_subcat", true)
                    bundle.putInt("supplierId", bean?.supplier_branch_id ?: 0)
                    navController(this@CustomHomeFrag).navigate(R.id.action_customHomeFrag_to_productTabListing, bundle)
                }
            } else {
                bundle.putInt("supplierId", bean?.id ?: 0)
                if (clientInform?.app_selected_template != null && clientInform?.app_selected_template == "1") {
                    navController(this@CustomHomeFrag)
                            .navigate(R.id.action_customHomeFrag_to_restaurantDetailNewFrag, bundle)
                } else {
                    navController(this@CustomHomeFrag)
                            .navigate(R.id.action_customHomeFrag_to_restaurantDetailFrag, bundle)
                }
            }
        }

    }

    private fun saveCategoryInf(bean: English?) {
        clientInform?.payment_after_confirmation = (bean?.payment_after_confirmation
                ?: 0).toString()
        clientInform?.order_instructions = (bean?.order_instructions ?: 0).toString()
        clientInform?.cart_image_upload = (bean?.cart_image_upload ?: 0).toString()

        prefHelper.setkeyValue(DataNames.SETTING_DATA, Gson().toJson(clientInform))

        prefHelper.addGsonValue(PrefenceConstants.APP_TERMINOLOGY, bean?.terminology ?: "")
    }

    private fun saveSubAppType(type: Int?) {

        // AppConstants.APP_SUB_TYPE=type?:0

        val bookingFlow = prefHelper.getGsonValue(DataNames.BOOKING_FLOW, SettingModel.DataBean.BookingFlowBean::class.java)

        if (type == AppDataType.HomeServ.type) {
            bookingFlow?.vendor_status = 0
            bookingFlow?.cart_flow = 1
        } else {
            bookingFlow?.vendor_status = 0
            bookingFlow?.cart_flow = 3
        }

        prefHelper.setkeyValue(DataNames.BOOKING_FLOW, Gson().toJson(bookingFlow))

        screenFlowBean?.app_type = type ?: 0
        prefHelper.setkeyValue(DataNames.SCREEN_FLOW, Gson().toJson(screenFlowBean))
    }

    override fun onProductDetail(bean: ProductDataBean?) {

        saveSubAppType(bean?.type)

        val bundle = Bundle()

        //if(screenFlowBean?.is_single_vendor==VendorAppType.Single.appType)

        if (bean?.type == AppDataType.Food.type) {

            bundle.putInt("supplierId", bean.supplier_id ?: 0)
            bundle.putInt("branchId", bean.supplier_branch_id ?: 0)
            bundle.putString("title", bean.name ?: "")
            bundle.putInt("categoryId", bean.category_id ?: 0)

            //if (screenFlowBean?.app_type == AppDataType.Food.type) {
            /*  if (bookingFlowBean?.is_pickup_order == FoodAppType.Pickup.foodType) {
                  bundle.putString("deliveryType", "pickup")
              }*/
            if (clientInform?.app_selected_template != null
                    && clientInform?.app_selected_template == "1")
                navController(this@CustomHomeFrag)
                        .navigate(R.id.action_customHomeFrag_to_restaurantDetailNewFrag, bundle)
            else
                navController(this@CustomHomeFrag)
                        .navigate(R.id.action_customHomeFrag_to_restaurantDetailFrag, bundle)

            /* } else {
                 navController(this@HomeFragment)
                         .navigate(R.id.action_supplierDetail, bundle)
             }*/
        } else {
            if (bean?.parent_id != null && bean.parent_id != 0) {
                bean.product_id = bean.parent_id
            }
            navController(this@CustomHomeFrag).navigate(R.id.action_customHomeFrag_to_productDetails, ProductDetails.newInstance(bean, 1, false))
        }
    }

    override fun addToCart(position: Int, productBean: ProductDataBean?) {


        val cartList = appUtils.getCartList()

        if (productBean?.adds_on?.isNotEmpty() == true) {
            //    val cartList: CartList? = prefHelper.getGsonValue(DataNames.CART, CartList::class.java)

            if (appUtils.checkProdExistance(productBean.product_id)) {
                val savedProduct = cartList.cartInfos?.filter {
                    it.supplierId == productBean.supplier_id ?: 0 && it.productId == productBean.product_id
                } ?: emptyList()

                SavedAddon.newInstance(productBean, FoodAppType.Delivery.foodType, savedProduct, this).show(childFragmentManager, "savedAddon")
            } else {
                AddonFragment.newInstance(productBean, FoodAppType.Delivery.foodType, this).show(childFragmentManager, "addOn")
            }

        } else {
            if (appUtils.checkVendorStatus(productBean?.supplier_id,
                            vendorBranchId = productBean?.supplier_branch_id, branchFlow = clientInform?.branch_flow)) {
                appUtils.mDialogsUtil.openAlertDialog(activity
                        ?: requireContext(), getString(R.string.clearCart, textConfig?.supplier
                        ?: "",textConfig?.proceed), "Yes", "No", this)
            } else {

                if (appUtils.checkBookingFlow(requireContext(), productBean?.product_id, this)) {
                    addCart(productBean)
                }
            }
        }
    }

    override fun removeToCart(position: Int, productBean: ProductDataBean?) {

        if (productBean?.prod_quantity == 0f) return

        if (productBean?.adds_on?.isNotEmpty() == true) {
            val cartList: CartList? = prefHelper.getGsonValue(DataNames.CART, CartList::class.java)

            val savedProduct = cartList?.cartInfos?.filter {
                it.supplierId == productBean.supplier_id && it.productId == productBean.product_id
            } ?: emptyList()

            SavedAddon.newInstance(productBean, productBean.self_pickup
                    ?: -1, savedProduct, this).show(childFragmentManager, "savedAddon")

        } else {
            productBean.apply { prodUtils.removeItemToCart(productBean) }
            refreshProductList(productBean)
        }
    }

    override fun addtoWishList(adapterPosition: Int, status: Int?, productId: Int?) {

    }

    override fun onProdDesc(productDesc: String) {
        StaticFunction.sweetDialogueSuccess11(activity, getString(R.string.description), productDesc, false, 1002
                , this)
    }

    override fun onProdAllergies(bean: ProductDataBean?) {

    }

    override fun onProdDialog(bean: ProductDataBean?) {
        appUtils.mDialogsUtil.showProductDialog(requireContext(),bean)
    }

    override fun onBookNow(bean: ProductDataBean?) {

    }

    override fun onSponsorDetail(supplier: SupplierInArabicBean?) {

        saveSubAppType(supplier?.type)

        val bundle = bundleOf("supplierId" to supplier?.id,
                "title" to supplier?.name)

        if (supplier?.type == AppDataType.Food.type) {
            if (clientInform?.app_selected_template != null && clientInform?.app_selected_template == "1") {
                navController(this@CustomHomeFrag).navigate(R.id.action_customHomeFrag_to_restaurantDetailNewFrag, bundle)
            } else {
                navController(this@CustomHomeFrag).navigate(R.id.action_customHomeFrag_to_restaurantDetailFrag, bundle)
            }
        } else {
            if (!supplier?.category.isNullOrEmpty()) {
                bundle.putBoolean("is_supplier", true)
                bundle.putParcelable("supplierData", supplier)
                bundle.putParcelableArrayList("subcategory", ArrayList<Parcelable>(supplier?.category
                        ?: mutableListOf()))
                navController(this@CustomHomeFrag)
                        .navigate(R.id.action_customHomeFrag_to_subCategory, bundle)
            } else {
                navController(this@CustomHomeFrag).navigate(R.id.action_customHomeFrag_to_productTabListing, bundle)
            }

        }

    }

    override fun onSupplierCall(supplier: SupplierInArabicBean?) {
        callSupplier(supplier?.supplierPhoneNumber?:"")
    }

    private fun addCart(productBean: ProductDataBean?) {
        if (productBean?.is_question == 1 && prodUtils.addItemToCart(productBean) != null) {
            productBean.prod_quantity == 0f
            val bundle = bundleOf("productBean" to productBean, "is_Category" to false
                    , "categoryId" to productBean.detailed_sub_category_id)
            navController(this@CustomHomeFrag).navigate(R.id.action_homeFragment_to_questionFrag, bundle)
        } else {
            productBean.apply { prodUtils.addItemToCart(productBean) }
            refreshProductList(productBean)
        }
    }


    private fun refreshProductList(productBean: ProductDataBean?) {
        mHomeData.offerData?.getOfferByCategory?.mapIndexed { index, getOfferByCategory ->
            if (productBean == null) {
                getOfferByCategory.value.map {
                    it.prod_quantity = 0f
                }
            }

            if (getOfferByCategory.name == productBean?.cate_name) {
                getOfferByCategory.value.map {
                    if (productBean.id == it.id) {
                        it.prod_quantity = StaticFunction.getCartQuantity(activity
                                ?: requireContext(), it.product_id)
                    }

                }
            }
        }

        mSupplierList.mapIndexed { index, supplierDataBean ->

            if (supplierDataBean.viewType == HomeItemAdapter.SPL_PROD_TYPE && supplierDataBean.itemModel?.mSpecialOfferName == productBean?.cate_name) {
                homeItemAdapter?.notifyItemChanged(index)
            }
        }
        if (productBean == null) {
            homeItemAdapter?.notifyDataSetChanged()
        }

        // homeItemAdapter?.notifyItemRangeChanged(2,mSupplierList?.size?:0)

        showBottomCart()
    }


    private fun showBottomCart() {

        val appCartModel = appUtils.getCartData(clientInform,selectedCurrency)


        if (appCartModel.cartAvail) {

            bottom_cart.visibility = View.VISIBLE

            tv_total_price.text = getString(R.string.total).plus(" ").plus(AppConstants.CURRENCY_SYMBOL).plus(" ").plus(appCartModel.totalPrice)
            tv_total_product.text = getString(R.string.total_item_tag, Utils.getDecimalPointValue(clientInform, appCartModel.totalCount))

            if (appCartModel.supplierName.isNotEmpty() && screenFlowBean?.is_single_vendor == VendorAppType.Multiple.appType) {
                tv_supplier_name.text = getString(R.string.supplier_tag, appCartModel.supplierName)
                tv_supplier_name.visibility = View.VISIBLE
            }

            if (appUtils.getCartList().cartInfos?.count() ?: 0 > 0 && mHomeData.data?.english?.count() ?: 0 > 0) {
                val appType = appUtils.getCartList().cartInfos?.first()?.appType

                mHomeData.data?.english?.forEach {
                    if (it.type == appType) {
                        saveCategoryInf(it)
                    }
                }
            }

            bottom_cart.setOnClickListener {
                navController(this@CustomHomeFrag).navigate(R.id.action_customHomeFrag_to_cart, null)
            }
        } else {
            bottom_cart.visibility = View.GONE
        }


    }

    override fun onAddonAdded(productModel: ProductDataBean) {
        if (productModel.adds_on?.isNullOrEmpty() == false && appUtils.checkProdExistance(productModel.product_id)) {
            val cartList: CartList? = appUtils.getCartList()
            productModel.prod_quantity = (cartList?.cartInfos?.filter { productModel.product_id == it.productId }?.sumByDouble { it.quantity.toDouble() }
                    ?: 0.0).toFloat()
        }

        refreshProductList(productModel)
    }

    override fun onSucessListner() {

        appUtils.clearCart()

        refreshProductList(null)
    }

    override fun onSuccessListener() {

    }


    override fun onErrorListener() {

    }

    override fun onAddressSelect(adrsBean: AddressBean) {

        location_txt.text = getString(R.string.location)

        adrsBean.let {
            appUtils.setUserLocale(it)

            prefHelper.addGsonValue(PrefenceConstants.ADRS_DATA, Gson().toJson(it))
            tvArea.text = appUtils.getAddressFormat(it)
        }

        clearViewModelApi()
    }

    override fun onDestroyDialog() {

    }

    override fun onVehicleSelect(id:String,model: String, color: String, plate_no: String) {
        TODO("Not yet implemented")
    }
    override fun onSponsorWishList(supplier: SupplierInArabicBean?, parentPos: Int?, isChecked: Boolean) {

    }

    override fun onBookNow(supplierData: SupplierInArabicBean) {

    }

    private fun callSupplier(phone: kotlin.String)
    {
        if (permissionUtil.hasCallPermissions(activity?:requireContext())) {

            this.phoneNum = phone
            callPhone(phone)

        } else {
            permissionUtil.phoneCallTask(activity?:requireContext())
        }
    }

    private fun callPhone(number: kotlin.String) {

        val intent = Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", number, null))

        if (ContextCompat.checkSelfPermission(activity?:requireContext(), Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED) {
            Manifest.permission.CALL_PHONE
            startActivity(intent)
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(arrayOf(Manifest.permission.CALL_PHONE), 1)
            }
        }
    }

    override fun onPermissionsDenied(requestCode: Int, perms: MutableList<kotlin.String>) {
        if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
            AppSettingsDialog.Builder(this).build().show()
        }
    }

    override fun onPermissionsGranted(requestCode: Int, perms: MutableList<kotlin.String>) {
        if (requestCode == AppConstants.REQUEST_CALL) {
            if (isNetworkConnected) {
                callPhone(phoneNum)
            }
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out kotlin.String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        // EasyPermissions handles the request result.
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this)
    }
}
