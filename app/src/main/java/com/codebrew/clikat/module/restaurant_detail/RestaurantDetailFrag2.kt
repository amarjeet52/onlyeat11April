package com.codebrew.clikat.module.restaurant_detail


import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.provider.MediaStore
import android.util.Log
import android.view.Gravity
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.NavOptions
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSmoothScroller
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ItemDecoration
import androidx.recyclerview.widget.RecyclerView.SmoothScroller
import com.codebrew.clikat.BR
import com.codebrew.clikat.BuildConfig
import com.codebrew.clikat.R
import com.codebrew.clikat.app_utils.*
import com.codebrew.clikat.app_utils.dialogintrface.ImageDialgFragment
import com.codebrew.clikat.app_utils.extension.afterTextChanged
import com.codebrew.clikat.app_utils.extension.launchActivity
import com.codebrew.clikat.app_utils.extension.onSnackbar
import com.codebrew.clikat.base.BaseFragment
import com.codebrew.clikat.base.EmptyListListener
import com.codebrew.clikat.base.PagingResult
import com.codebrew.clikat.data.*
import com.codebrew.clikat.data.constants.AppConstants
import com.codebrew.clikat.data.constants.PrefenceConstants
import com.codebrew.clikat.data.model.api.AddressBean
import com.codebrew.clikat.data.model.api.Currency
import com.codebrew.clikat.data.model.api.ListItem
import com.codebrew.clikat.data.preferences.PreferenceHelper
import com.codebrew.clikat.databinding.FragmentRestaurantDetail2Binding
import com.codebrew.clikat.databinding.PopupRestaurantMenuBinding
import com.codebrew.clikat.di.ViewModelProviderFactory
import com.codebrew.clikat.modal.CartList
import com.codebrew.clikat.modal.LocationUser
import com.codebrew.clikat.modal.PojoSignUp
import com.codebrew.clikat.modal.other.*
import com.codebrew.clikat.modal.slots.SupplierSlots
import com.codebrew.clikat.module.addon_quant.SavedAddon
import com.codebrew.clikat.module.cart.SCHEDULE_REQUEST
import com.codebrew.clikat.module.cart.addproduct.AddProductDialog
import com.codebrew.clikat.module.cart.schedule_order.ScheduleOrder
import com.codebrew.clikat.module.dialog_adress.AddressDialogFragment
import com.codebrew.clikat.module.dialog_adress.interfaces.AddressDialogListener
import com.codebrew.clikat.module.dialog_adress.v2.AddressDialogFragmentV2
import com.codebrew.clikat.module.product.product_listing.DialogListener
import com.codebrew.clikat.module.product_addon.AddonFragment
import com.codebrew.clikat.module.product_detail.ProductDetails
import com.codebrew.clikat.module.restaurant_detail.adapter.MenuCategoryAdapter
import com.codebrew.clikat.module.restaurant_detail.adapter.ProdListAdapter
import com.codebrew.clikat.module.restaurant_detail.adapter.RestImagesViewPagerAdapter
import com.codebrew.clikat.module.restaurant_detail.adapter.SupplierProdListAdapter
import com.codebrew.clikat.module.restaurant_detail.dialog.MenuCategoryDialog
import com.codebrew.clikat.preferences.DataNames
import com.codebrew.clikat.utils.DialogIntrface
import com.codebrew.clikat.utils.StaticFunction
import com.codebrew.clikat.utils.Utils
import com.codebrew.clikat.utils.configurations.Configurations
import com.google.android.material.tabs.TabLayout
import com.google.gson.Gson
import com.codebrew.clikat.intrface.ImageCallback
import io.github.douglasjunior.androidSimpleTooltip.SimpleTooltip
import kotlinx.android.synthetic.main.fragment_restaurant_detail.*
import kotlinx.android.synthetic.main.fragment_restaurant_detail2.*
import kotlinx.android.synthetic.main.fragment_restaurant_detail2.bottom_cart
import kotlinx.android.synthetic.main.fragment_restaurant_detail2.btnBookTable
import kotlinx.android.synthetic.main.fragment_restaurant_detail2.btn_menu_custom
import kotlinx.android.synthetic.main.fragment_restaurant_detail2.group_presc
import kotlinx.android.synthetic.main.fragment_restaurant_detail2.group_presc_doc
import kotlinx.android.synthetic.main.fragment_restaurant_detail2.grp_yummy_view
import kotlinx.android.synthetic.main.fragment_restaurant_detail2.ivMenu
import kotlinx.android.synthetic.main.fragment_restaurant_detail2.ivSupplierBanner1
import kotlinx.android.synthetic.main.fragment_restaurant_detail2.ivUploadPresc
import kotlinx.android.synthetic.main.fragment_restaurant_detail2.iv_cross_pres
import kotlinx.android.synthetic.main.fragment_restaurant_detail2.iv_search_prod
import kotlinx.android.synthetic.main.fragment_restaurant_detail2.iv_search_prod_custom
import kotlinx.android.synthetic.main.fragment_restaurant_detail2.mainmenu
import kotlinx.android.synthetic.main.fragment_restaurant_detail2.nested_scrollview
import kotlinx.android.synthetic.main.fragment_restaurant_detail2.noData
import kotlinx.android.synthetic.main.fragment_restaurant_detail2.presc_image
import kotlinx.android.synthetic.main.fragment_restaurant_detail2.rvproductList
import kotlinx.android.synthetic.main.fragment_restaurant_detail2.tab_layout
import kotlinx.android.synthetic.main.fragment_restaurant_detail2.tvUnServiceAble
import kotlinx.android.synthetic.main.layout_bottom_cart.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import pub.devrel.easypermissions.AppSettingsDialog
import pub.devrel.easypermissions.EasyPermissions
import java.io.File
import java.io.IOException
import java.text.DecimalFormat
import javax.inject.Inject

/**
 * A simple [Fragment] subclass.
 */
class RestaurantDetailFrag2 : BaseFragment<FragmentRestaurantDetail2Binding, RestDetailViewModel>(),
        ProdListAdapter.ProdCallback, DialogListener, MenuCategoryAdapter.MenuCategoryCallback,
        RestDetailNavigator, AddonFragment.AddonCallback, ImageCallback, EasyPermissions.PermissionCallbacks,
        AddressDialogListener, DialogIntrface, TabLayout.OnTabSelectedListener, OnMenuCategoryListener, RestImagesViewPagerAdapter.OnImageClicked, AddProductDialog.OnAddProductListener, EmptyListListener {


    private var selectedCurrency: Currency? = null
    private var parentPos: Int = 0
    private var childPos: Int = 0

    private var adapter: SupplierProdListAdapter? = null
    private var productBeans = mutableListOf<ProductBean>()
    private var categoryList = mutableListOf<ProductBean>()
    private var bookingFlowBean: SettingModel.DataBean.BookingFlowBean? = null

    private var prodlytManager: LinearLayoutManager? = null

    private var deliveryType = 0

    private var supplierId = 0
    private var supplierBranchId = 0

    @Inject
    lateinit var dataManager: DataManager
    var bottomCartNew: View? = null

    @Inject
    lateinit var factory: ViewModelProviderFactory

    @Inject
    lateinit var appUtils: AppUtils

    @Inject
    lateinit var mDialogsUtil: DialogsUtil

    @Inject
    lateinit var imageUtils: ImageUtility

    @Inject
    lateinit var permissionFile: PermissionFile

    @Inject
    lateinit var mDataManager: PreferenceHelper

    private var photoFile: File? = null

    private val imageDialog by lazy { ImageDialgFragment() }

    private var screenFlowBean: SettingModel.DataBean.ScreenFlowBean? = null
    private var settingBean: SettingModel.DataBean.SettingData? = null

    private var mBinding: FragmentRestaurantDetail2Binding? = null
    private lateinit var viewModel: RestDetailViewModel


    private var isResutantOpen: Boolean = false

    private var colorConfig = Configurations.colors

    var tooltip: SimpleTooltip? = null
    private var supplierDetail: SupplierDetailBean? = null
    var isFromBranch: Boolean = false
    var supplierBranchName = ""
    private val decimalFormat: DecimalFormat = DecimalFormat("0.00")
    private val textConfig by lazy { appUtils.loadAppConfig(0).strings }

    private var adrsBean: AddressBean? = null
    private var selectedDateTimeForScheduling: SupplierSlots? = null

    private var smoothScroller: SmoothScroller? = null

    private var viewPagerAdapter: RestImagesViewPagerAdapter? = null

    private var isNewCategory = false

    private var previousCatId = ""

    override fun getBindingVariable(): Int {
        return BR.viewModel
    }

    override fun getLayoutId(): Int {
        return R.layout.fragment_restaurant_detail2
    }

    override fun getViewModel(): RestDetailViewModel {
        viewModel = ViewModelProviders.of(this, factory).get(RestDetailViewModel::class.java)
        return viewModel
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        EventBus.getDefault().register(this)
        viewModel.navigator = this
        settingBean = dataManager.getGsonValue(DataNames.SETTING_DATA, SettingModel.DataBean.SettingData::class.java)
        screenFlowBean = dataManager.getGsonValue(DataNames.SCREEN_FLOW, SettingModel.DataBean.ScreenFlowBean::class.java)
        selectedCurrency = dataManager.getGsonValue(PrefenceConstants.CURRENCY_INF, Currency::class.java)

        restDetailObserver()
        restCategoryObserver()
        imageObserver()
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onResultReceived(result: ListItem?) {
        if (result?.requestedFrom == "1") {
            return
        }
        bookTableWithSchedule(result?.id.toString(), supplierId)
    }

    override fun onDestroy() {
        super.onDestroy()
        EventBus.getDefault().unregister(this)
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        bottomCartNew = view.findViewById(R.id.bottom_cartNew)

        mBinding = viewDataBinding

        viewDataBinding.color = Configurations.colors
        viewDataBinding.drawables = Configurations.drawables
        viewDataBinding.strings = textConfig
        viewDataBinding.settingData = settingBean
        viewDataBinding.isSupplierRating = settingBean?.is_supplier_rating == "1"

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            //   mainmenu.foreground.alpha = 0
        }

        getValues()
        settingLayout()
        setclickListener()
        showBottomCart()
        setPrescListener()

//        settingBean?.zipDesc?.let {
//            if (it == "1") {
//                val lp: CoordinatorLayout.LayoutParams = btn_menu.layoutParams as CoordinatorLayout.LayoutParams
//                lp.anchorGravity = Gravity.BOTTOM or GravityCompat.END
//                btn_menu.layoutParams = lp
//            }
//        }

        //  onRecyclerViewScrolled()
    }


    @RequiresApi(Build.VERSION_CODES.M)
    private fun restDetailObserver() {
        // Create the observer which updates the UI.
        val catObserver = Observer<PagingResult<SuplierProdListModel>> { resource ->
            refreshAdapter(resource?.result?.data)
            if (resource.isFirstPage) {
                resource?.result?.data?.supplier_detail?.let { settingSupplierDetail(it) }
            }


        }

        // Observe the LiveData, passing in this activity as the LifecycleOwner and the observer.
        viewModel.supplierLiveData.observe(this, catObserver)
    }

    private fun imageObserver() {
        // Create the observer which updates the UI.
        val catObserver = Observer<String> { resource ->
            hideLoading()
            group_presc.visibility = View.VISIBLE
            presc_image.text = photoFile?.absoluteFile?.name.toString()
            mBinding?.root?.onSnackbar(resource)
        }

        // Observe the LiveData, passing in this activity as the LifecycleOwner and the observer.
        viewModel.prescLiveData.observe(this, catObserver)
    }


    private fun setPrescListener() {
        ivUploadPresc.setOnClickListener {
            if (dataManager.getCurrentUserLoggedIn()) {
                if (settingBean?.show_ecom_v2_theme == "1") {
                    AddressDialogFragmentV2.newInstance(0).show(childFragmentManager, "dialog")
                } else {
                    AddressDialogFragment.newInstance(0,0).show(childFragmentManager, "dialog")
                }

            } else {
                appUtils.checkLoginFlow(requireActivity(), AppConstants.REQUEST_PRES_UPLOAD)
            }
        }

        iv_cross_pres.setOnClickListener {
            group_presc.visibility = View.GONE
        }
    }


    @RequiresApi(Build.VERSION_CODES.M)
    private fun setclickListener() {

        iv_search_prod.afterTextChanged {
            if (it.isNotEmpty()) {
                adapter?.filter?.filter(it.toLowerCase())
            } else {
                adapter?.filter?.filter("")
            }
        }
        iv_search_prod?.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                hideKeyboard()
            }
            true
        }
        // if (dataManager.isBranchFlow()) {

//        btn_branches.setOnClickListener {
//            if (supplierDetail != null) {
//                val bundle = bundleOf("title" to supplierDetail?.name,
//                        "supplierId" to supplierDetail?.id)
//                navController(this@RestaurantDetailFrag2).navigate(R.id.action_restaurantDetailFrag_to_supplierAll, bundle)
//            }
//        }
//        // }

        ivBack.setOnClickListener {
            navController(this@RestaurantDetailFrag2).popBackStack()
        }



        ivMenu?.setOnClickListener {
            val catList = if (settingBean?.enable_rest_pagination_category_wise == "1") categoryList else productBeans
            val list = ArrayList<ProductBean>()
            list.addAll(catList)
            MenuCategoryDialog.newInstance(list).show(childFragmentManager, "dialog")
        }
//        btn_menu.setOnClickListener {
//            //displayPopupWindow(it)
//            showPopUp(it)
//        }

        btnBookTable?.setOnClickListener {
            if (dataManager.getCurrentUserLoggedIn()) {
                activity?.launchActivity<ScheduleOrder>(SCHEDULE_REQUEST) {
                    putExtra("supplierId", supplierId.toString())
                    putExtra("dineIn", true)
                }
            } else {
                appUtils.checkLoginFlow(requireActivity(), DataNames.REQUEST_RESTAURANT_LOGIN)
            }

        }
    }

    private fun getAllSupplierCategories() {
        if (isNetworkConnected)
            viewModel.getSupplierCategoryList(supplierId.toString())
    }

    private fun restCategoryObserver() {
        // Create the observer which updates the UI.
        val catObserver = Observer<ArrayList<ProductBean>> { resource ->
            categoryList.clear()
            categoryList.addAll(resource)
            categoryList.forEachIndexed { index, productBean ->
                tab_layout.addTab(tab_layout.newTab().setText(productBean.sub_cat_name))
            }
        }

        // Observe the LiveData, passing in this activity as the LifecycleOwner and the observer.
        viewModel.categoryLiveData.observe(this, catObserver)
    }


    @RequiresApi(Build.VERSION_CODES.M)
    private fun showPopUp(view: View) {

        val binding = PopupRestaurantMenuBinding.inflate(layoutInflater)
        binding.color = Configurations.colors

        tooltip = SimpleTooltip.Builder(activity)
                .anchorView(view)
                .text(textConfig?.catalogue)
                .gravity(if (settingBean?.yummyTheme == "1") Gravity.CENTER else Gravity.TOP)
                .dismissOnOutsideTouch(true)
                .dismissOnInsideTouch(false)
                .modal(true)
                .showArrow(false)
                .animated(false)
                .showArrow(true)
                .arrowDrawable(R.drawable.ic_popup)
                .textColor(Color.parseColor(colorConfig.primaryColor))
                .arrowColor(ContextCompat.getColor(requireContext(), R.color.white))
                .transparentOverlay(true)
                .overlayOffset(0f)
                // .highlightShape(OverlayView.INVISIBLE)
                //.overlayMatchParent(true)
                .padding(0.0f)
                .margin(0.0f)
                //.animationPadding(SimpleTooltipUtils.pxFromDp(50f))
                .onDismissListener {
                    mainmenu.foreground.alpha = 0
                }
                .onShowListener {
                    mainmenu.foreground.alpha = 120
                }
                .contentView(binding.root, R.id.menu)
                .focusable(true)
                .build()


        val stringList = ArrayList<String>()

        val list = if (settingBean?.enable_rest_pagination_category_wise == "1") categoryList else productBeans
        if (list.isNotEmpty()) {
            for (productBean in list) {
                stringList.add(productBean.sub_cat_name ?: "")
            }
        }
        // tooltip?.color = Configurations.colors

        val rvCategory = tooltip?.findViewById<RecyclerView>(R.id.rvmenu_category)
        val layoutManager = LinearLayoutManager(activity, RecyclerView.VERTICAL, false)
        rvCategory?.layoutManager = layoutManager
        val adapter = MenuCategoryAdapter(stringList)
        rvCategory?.adapter = adapter
        adapter.settingCallback(this)

        val itemDecoration: ItemDecoration = DividerItemDecoration(activity, DividerItemDecoration.VERTICAL)
        rvCategory?.addItemDecoration(itemDecoration)

        tooltip?.show()

    }


    private fun settingLayout() {

        smoothScroller = object : LinearSmoothScroller(requireContext()) {
            override fun getVerticalSnapPreference(): Int {
                return SNAP_TO_START
            }
        }

        productBeans.clear()

        viewPagerAdapter = RestImagesViewPagerAdapter(requireContext(), this)
        ivSupplierBanner1.adapter = viewPagerAdapter

        adapter = SupplierProdListAdapter(this, settingBean, appUtils, selectedCurrency, this, "new")
        prodlytManager = LinearLayoutManager(activity, RecyclerView.VERTICAL, false)
        rvproductList.layoutManager = prodlytManager
        rvproductList?.isNestedScrollingEnabled = false

        rvproductList.adapter = adapter

        rvproductList?.setHasFixedSize(true)
        rvproductList?.itemAnimator = null

        if (settingBean?.enable_rest_pagination_category_wise == "1") {
            tab_layout.visibility = View.VISIBLE
            //  btn_menu?.visibility = View.GONE
            ivMenu?.visibility = View.VISIBLE
            tab_layout.addOnTabSelectedListener(this)
            onRecyclerScrolled()
        } else if (settingBean?.is_new_menu_theme == "1") {
            tab_layout.visibility = View.VISIBLE
            //  btn_menu?.visibility = View.GONE
            ivMenu.visibility = View.VISIBLE
            tab_layout.addOnTabSelectedListener(this)
            onRecyclerViewScrolled()
        } else {
            if (settingBean?.yummyTheme != "1")
            //   btn_menu?.visibility = View.VISIBLE

                ivMenu.visibility = View.GONE
            tab_layout.visibility = View.GONE

            if (settingBean?.rest_detail_pagin == "1") {
                onRecyclerScrolled()
            }
        }

        if (arguments != null) {
            if (arguments?.containsKey("deliveryType") == true) {
                val typeHolder = arguments?.getString("deliveryType")
                deliveryType = when (typeHolder?.toLowerCase()) {
                    "pickup" -> 1
                    "both" -> 2
                    "dinein" -> 3
                    else -> 0
                }
            }
            deliveryType = if (settingBean?.is_skip_theme == "1" || BuildConfig.CLIENT_CODE == "skipp_0631") 1 else deliveryType

            if (arguments?.containsKey("isFromBranch") == true) {
                if (arguments?.getBoolean("isFromBranch") == true) {
                    // btn_branches.visibility = View.GONE
                    isFromBranch = true
                }

            }

            if (arguments?.containsKey("supplierName") == true) {
                supplierBranchName = arguments?.getString("supplierName", "") ?: ""

                //    tv_name.text = supplierBranchName

            }

            if (arguments?.containsKey("supplierLogo") == true) {
//                StaticFunction.loadImage(arguments?.getString("supplierLogo", "")?: "", ivSupplierIcon, false)
//                StaticFunction.loadImage(arguments?.getString("supplierLogo", "") ?: "", ivSupplierIcon21, false)

            }

            if (arguments?.containsKey("supplierBannerImage") == true) {

                val image = arrayListOf<String>()
                image.add(arguments?.getString("supplierBannerImage", "")
                        ?: "")

                viewPagerAdapter?.addImages(image)
//                circleIndicator?.setViewPager(ivSupplierBanner1)
//                circleIndicator.visibility = if (image.size == 1) View.GONE else View.VISIBLE

            }



            if (arguments?.containsKey("supplierId") == true) {
                supplierId = arguments?.getInt("supplierId") ?: 0

                if (arguments?.containsKey("branchId") == true) {
                    supplierBranchId = arguments?.getInt("branchId") ?: 0
                }
                if (settingBean?.enable_rest_pagination_category_wise == "1")
                    getAllSupplierCategories()
                else
                    getProductList(supplierId, supplierBranchId, true, null)
            }
        }

        val currentTableData = mDataManager.getCurrentTableData()
        if (currentTableData != null) {
            supplierId = currentTableData.supplier_id?.toIntOrNull() ?: 0
            supplierBranchId = currentTableData.branch_id?.toIntOrNull() ?: 0
            btnBookTable?.visibility = View.GONE
            getProductList(supplierId, supplierBranchId, true, null)
        }
    }

    private fun showBottomCart() {

        val appCartModel = appUtils.getCartData(settingBean, selectedCurrency)

        if (appCartModel.cartAvail) {
            if (Utils.checkApp(activity?.applicationContext!!)) {
                // bottom_cartNew.visibility = View.VISIBLE
                bottomCartNew?.visibility = View.VISIBLE
                tv_total_price.text = getString(R.string.total).plus(" ").plus(AppConstants.CURRENCY_SYMBOL).plus(" ").plus(appCartModel.totalPrice)

                tv_total_product.text = getString(R.string.total_item_tag, Utils.getDecimalPointValue(settingBean, appCartModel.totalCount)) + " items"


                if (appCartModel.supplierName.isNotEmpty() && screenFlowBean?.is_single_vendor == VendorAppType.Multiple.appType) {
                    tv_supplier_name.visibility = View.VISIBLE
                    tv_supplier_name.text = getString(R.string.supplier_tag, appCartModel.supplierName)
                } else {
                    tv_supplier_name.visibility = View.GONE
                }
            } else {
                bottom_cart.visibility = View.VISIBLE
                tv_total_price.text = getString(R.string.total).plus(" ").plus(AppConstants.CURRENCY_SYMBOL).plus(" ").plus(appCartModel.totalPrice)

                tv_total_product.text = getString(R.string.total_item_tag, Utils.getDecimalPointValue(settingBean, appCartModel.totalCount)) + " items"


                if (appCartModel.supplierName.isNotEmpty() && screenFlowBean?.is_single_vendor == VendorAppType.Multiple.appType) {
                    tv_supplier_name.visibility = View.VISIBLE
                    tv_supplier_name.text = getString(R.string.supplier_tag, appCartModel.supplierName)
                } else {
                    tv_supplier_name.visibility = View.GONE
                }
            }
//         x
            bottomCartNew?.setOnClickListener {
                bottom_cart.callOnClick()
            }
            bottom_cart.setOnClickListener {

                val navOptions: NavOptions = NavOptions.Builder()
                        .setPopUpTo(R.id.restaurantDetailFrag, false)
                        .build()


                navController(this@RestaurantDetailFrag2).navigate(R.id.action_restaurantDetailFrag_to_cart, null, navOptions)

            }
        } else {
            if (Utils.checkApp(activity?.applicationContext!!)) {
                bottomCartNew?.visibility = View.GONE
            } else {
                bottom_cart.visibility = View.GONE
            }
        }

    }

    private fun getValues() {
        bookingFlowBean = dataManager.getGsonValue(DataNames.BOOKING_FLOW, SettingModel.DataBean.BookingFlowBean::class.java)
        iv_search_prod?.hint = activity?.getString(R.string.search_for_a_specific_item)
    }


    private fun getProductList(supplierId: Int, supplierBranchId: Int = 0, isFirstPage: Boolean, categoryId: String?) {

        if (isNetworkConnected)
            if (isFromBranch) {
                viewModel.getBranchProductList(supplierId.toString(), supplierBranchId.toString(), isFirstPage)
            } else {

                if (viewModel.supplierLiveData.value != null && viewModel.isCategoryId.get() == categoryId?.toInt()) {
                    refreshAdapter(viewModel.supplierLiveData.value?.result?.data)
                    if (viewModel.supplierLiveData.value?.isFirstPage == true) {
                        viewModel.supplierLiveData.value?.result?.data?.supplier_detail.let { settingSupplierDetail(it) }
                    }
                } else {
                    viewModel.setIsCategory(categoryId?.toInt() ?: 0)

                    viewModel.showWhiteScreen.set(supplierDetail == null)
                    viewModel.isContentProgressBarLoading.set(supplierDetail != null)
                    viewModel.getProductList(supplierId.toString(), isFirstPage, settingBean, categoryId)
                }


            }
    }

    private fun refreshAdapter(data: DataBean?) {

        noDataVisibility(data?.product?.count() == 0)

        viewDataBinding.supplierModel = data?.supplier_detail

        if (data?.product?.size ?: 0 > 0 && settingBean?.is_carveQatar_home_theme == "1") {
            //  tvDescCrave.text = data?.product?.get(0)?.sub_cat_name
        }

        var subCatName: String? = null
        val previousCount = productBeans.count()

        if (viewModel.skip == 0 || isNewCategory)
            productBeans.clear()

        if (data?.product?.isNotEmpty() == true) {

            isNewCategory = false

            // productBeans.addAll(data.product ?: listOf())

            data.product?.map { prod ->

                prod.is_SubCat_visible = true

                prod.value?.map {
                    it.prod_quantity = StaticFunction.getCartQuantity(requireActivity(), it.product_id)
                    it.netPrice = if (it.fixed_price?.toFloatOrNull() ?: 0.0f > 0) it.fixed_price?.toFloatOrNull() else 0f
                }

                if (prod.detailed_category_name?.count() ?: 0 > 0) {
                    prod.detailed_category_name?.distinctBy { it.detailed_sub_category_id }?.forEach { detailProd ->
                        val prodBean = prod.copy()
                        prodBean.detailed_sub_category = detailProd.name
                        prod.value?.map {
                            it.detailed_sub_name = detailProd.name
                        }

                        prodBean.value = prod.value?.filter { it.detailed_sub_category_id == detailProd.detailed_sub_category_id }?.toMutableList()

                        if (prodBean.value?.isEmpty() == true) return@forEach

                        if (subCatName == prodBean.sub_cat_name) {
                            prodBean.is_SubCat_visible = false
                        }
                        subCatName = prodBean.sub_cat_name


                        if (prodBean.value?.isNotEmpty() == true) {
                            productBeans.add(prodBean.copy())
                        }
                    }
                } else {
                    if (productBeans.any { it.sub_cat_name == prod.sub_cat_name }) {
                        prod.is_SubCat_visible = false
                        productBeans.add(prod.copy())
                    } else {
                        productBeans.add(prod.copy())
                    }
                }
            }


            if (settingBean?.is_new_menu_theme == "1" && settingBean?.enable_rest_pagination_category_wise != "1" && productBeans.isNotEmpty()) {
                productBeans.forEachIndexed { index, productBean ->
                    tab_layout.addTab(tab_layout.newTab().setText(productBean.sub_cat_name))
                }
            } else {
                adapter?.settingList(productBeans)
                adapter?.filter?.filter("")

            }

        }
    }


    @RequiresApi(Build.VERSION_CODES.M)
    private fun settingSupplierDetail(supplier_detail: SupplierDetailBean?) {

        supplierDetail = supplier_detail
        isResutantOpen = appUtils.checkResturntTiming(supplier_detail?.timing)

        deliveryType = if (supplier_detail?.is_out_network == 1) FoodAppType.Delivery.foodType else deliveryType

        btnBookTable?.visibility = if (supplierDetail?.is_dine_in == 1 && settingBean?.is_table_booking == "1") {
            View.GONE
        } else {
            View.GONE
        }

//        if (!supplier_detail?.supplier_tags.isNullOrEmpty()) {
//            tvTags.visibility = View.VISIBLE
//            tvTags.text = supplier_detail?.supplier_tags?.joinToString(",")
//        } else {
//            tvTags.visibility = View.GONE
//            tvTags.text = ""
//        }


//        groupTags.visibility = if (BuildConfig.CLIENT_CODE == "foodydoo_0590") {
//            View.VISIBLE
//        } else {
//            View.GONE
//        }

        val currentTableData = mDataManager.getCurrentTableData()
        if (currentTableData != null) {
            btnBookTable?.visibility = View.GONE
        }

        if (adapter != null) {
            adapter?.checkResturantOpen(isResutantOpen)
        }

//        if (supplier_detail?.is_multi_branch == 1 && settingBean?.branch_flow == "1")
//            btn_branches.visibility = View.VISIBLE
//        else
//            btn_branches.visibility = View.GONE

//
//        StaticFunction.loadImage(supplier_detail?.logo, ivSupplierIcon, false)
//        StaticFunction.loadImage(supplier_detail?.logo, ivSupplierIcon21, false)


        if (supplier_detail?.supplier_image?.isNotEmpty() == true) {
            viewPagerAdapter?.addImages(supplier_detail?.supplier_image ?: emptyList())
//            circleIndicator?.setViewPager(ivSupplierBanner1)
//            circleIndicator.visibility = if (supplier_detail?.supplier_image?.size == 1) View.GONE else View.VISIBLE
        }


//        if (isFromBranch) {
//            tv_name.text = supplierBranchName
//        } else {
//            tv_name.text = supplier_detail?.name
//        }
//
//        tv_rating_crave.text = if (supplier_detail?.rating == null || supplier_detail?.rating == 0f) {
//            "New"
//        } else supplier_detail?.rating.toString()

//        iv_favourite?.visibility = if (settingBean?.is_supplier_wishlist == "1" || settingBean?.is_skip_theme == "1") View.VISIBLE else View.GONE
//
//        iv_favourite.setOnCheckedChangeListener(null)
//        iv_favourite.isChecked = supplier_detail?.Favourite == 1
//
//        iv_favourite.setOnCheckedChangeListener { checkBox, isChecked ->
//            if (isNetworkConnected) {
//                if (dataManager.getCurrentUserLoggedIn()) {
//                    if (isChecked) {
//                        viewModel.markFavSupplier(supplierId.toString())
//                    } else {
//                        viewModel.unFavSupplier(supplierId.toString())
//                    }
//                } else {
//                    appUtils.checkLoginFlow(requireActivity(), -1)
//                }
//            }
//        }


        settingBean?.yummyTheme?.let {
            if (it == "1") {
                setYummyLayout()

                if (!isResutantOpen) {
                    tvUnServiceAble.text = textConfig?.unserviceable
                    tvUnServiceAble.visibility = View.VISIBLE
                } else {
                    tvUnServiceAble.visibility = View.GONE
                }
            }
        }


        if (settingBean?.show_prescription_requests != null && settingBean?.show_prescription_requests == "1") {
            supplier_detail?.user_request_flag.let {
                if (it == 1) {
                    group_presc_doc.visibility = View.VISIBLE
                    group_presc.visibility = View.GONE
                } else {
                    group_presc_doc.visibility = View.GONE
                    group_presc.visibility = View.GONE
                }
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun setYummyLayout() {

        grp_yummy_view.visibility = View.VISIBLE
        iv_search_prod.visibility = View.GONE
        btn_menu_custom.visibility = View.VISIBLE
        //  btn_menu.visibility = View.GONE


        iv_search_prod_custom.setOnSearchClickListener { v: View? ->
            btn_menu_custom.visibility = View.INVISIBLE
            //   appbar.setExpanded(false)
        }

        btn_menu_custom.setOnClickListener {
            //displayPopupWindow(it)
            showPopUp(it)
        }


        iv_search_prod_custom.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(s: String): Boolean {
                adapter?.filter?.filter(s)
                //adapter?.notifyDataSetChanged()
                Log.e("search_view", "setOnQueryTextListener$s")
                return true
            }

            override fun onQueryTextChange(s: String): Boolean {
                //when the text change
                adapter?.filter?.filter(s)
                //  adapter?.notifyDataSetChanged()
                Log.e("search_view", "onQueryTextChange$s")
                return true
            }
        })
        iv_search_prod_custom.setOnCloseListener {

            //when canceling the search
            //  appbar.setExpanded(true)
            hideKeyboard()
            btn_menu_custom.visibility = View.VISIBLE
            false
        }
    }


    override fun onProdAdded(productBean: ProductDataBean?, parentPosition: Int, childPosition: Int, isOpen: Boolean) {
        if (parentPosition == -1 && childPosition == -1) return

        val cartList: CartList? = dataManager.getGsonValue(DataNames.CART, CartList::class.java)

        val userData = mDataManager.getGsonValue(DataNames.USER_DATA, PojoSignUp::class.java)
        if (productBean?.is_subscription_required == 1 && userData?.data?.is_subscribed != 1) {
            mBinding?.root?.onSnackbar(getString(R.string.subcription_required))
            return
        } else if ((cartList?.cartInfos?.any { !(it.product_owner_name.isNullOrEmpty()) } == true || supplierDetail?.is_out_network == 1)
                && cartList?.cartInfos?.size ?: 0 >= 4 && cartList?.cartInfos?.any { it.productId == productBean?.product_id } == false) {
            /*only 4 max products with out network product are allowed*/
            mBinding?.root?.onSnackbar(getString(R.string.max_products_allowed_with_out))
            return
        } else if (supplierDetail?.is_out_network == 1) {
            val dialog = AddProductDialog.newInstance(productBean, parentPosition, childPosition, isOpen)
            dialog.setListeners(this)
            dialog.show(childFragmentManager, "dialog")
        } else
            checkEditQuantity(null, productBean, parentPosition, childPosition, isOpen)

    }

    override fun onProductAdded(productBean: ProductDataBean?, parentPosition: Int, childPosition: Int, isOpen: Boolean) {
        checkEditQuantity(null, productBean, parentPosition, childPosition, isOpen)
    }

    private fun checkEditQuantity(updatedQuantity: Float?, productBean: ProductDataBean?, parentPosition: Int, childPosition: Int, isOpen: Boolean) {
        hideKeyboard()

        if (isOpen) {
            mDialogsUtil.openAlertDialog(activity
                    ?: requireContext(), getString(R.string.offline_supplier_tag, textConfig?.supplier),
                    getString(R.string.ok), "", this)
            return
        }


        productBean?.type = screenFlowBean?.app_type

        this.parentPos = parentPosition
        this.childPos = childPosition

        if (deliveryType == FoodAppType.Pickup.foodType || deliveryType == FoodAppType.DineIn.foodType) {
            if (viewModel.supplierLiveData.value == null) return

            val mRestUser = LocationUser()
            mRestUser.address = "${viewModel.supplierLiveData.value?.result?.data?.supplier_detail?.name ?: ""} , ${viewModel.supplierLiveData.value?.result?.data?.supplier_detail?.address}"
            dataManager.addGsonValue(PrefenceConstants.RESTAURANT_INF, Gson().toJson(mRestUser))
        }


        if (screenFlowBean?.is_single_vendor == VendorAppType.Multiple.appType) {
            val cartList: CartList? = dataManager.getGsonValue(DataNames.CART, CartList::class.java)

            if (cartList != null && cartList.cartInfos?.isNotEmpty() == true) {
                if (cartList.cartInfos?.any { it.deliveryType != deliveryType } == true) {
                    appUtils.clearCart()

                    refreshAdapter(viewModel.supplierLiveData.value?.result?.data)
                }
            }
        }



        if (productBean?.adds_on?.isNotEmpty() == true) {
            val cartList: CartList? = dataManager.getGsonValue(DataNames.CART, CartList::class.java)

            if (appUtils.checkProdExistance(productBean.product_id)) {
                val savedProduct = cartList?.cartInfos?.filter {
                    it.supplierId == supplierId && it.productId == productBean.product_id
                } ?: emptyList()
                SavedAddon.newInstance(productBean, deliveryType, savedProduct, this).show(childFragmentManager, "savedAddon")
            } else {
                AddonFragment.newInstance(productBean, deliveryType, this).show(childFragmentManager, "addOn")
            }

        } else {
            if (bookingFlowBean?.vendor_status == 0 && appUtils.checkVendorStatus(productBean?.supplier_id, vendorBranchId = productBean?.supplier_branch_id, branchFlow = settingBean?.branch_flow)) {
                mDialogsUtil.openAlertDialog(activity
                        ?: requireContext(), getString(R.string.clearCart, textConfig?.supplier
                        ?: "", textConfig?.proceed ?: ""), "Yes", "No", this)
            } else {
                if (appUtils.checkBookingFlow(requireContext(), productBean?.product_id, this)) {
                    addCart(updatedQuantity, productBean)
                }
            }

            showBottomCart()
        }
    }

    override fun onProdDelete(productBean: ProductDataBean?, parentPosition: Int, childPosition: Int, isOpen: Boolean) {

        hideKeyboard()

        if (parentPosition == -1 && childPosition == -1 && productBean?.prod_quantity ?: 0f < 0) return

        this.parentPos = parentPosition
        this.childPos = childPosition

        if (productBean?.adds_on?.isNotEmpty() == true) {
            val cartList: CartList? = dataManager.getGsonValue(DataNames.CART, CartList::class.java)

            val savedProduct = cartList?.cartInfos?.filter {
                it.supplierId == supplierId && it.productId == productBean.product_id
            } ?: emptyList()

            SavedAddon.newInstance(productBean, deliveryType, savedProduct, this).show(childFragmentManager, "savedAddon")
        } else {
            var quantity = productBean?.prod_quantity ?: 0f

            if (quantity > 0) {
                if (settingBean?.is_decimal_quantity_allowed == "1") {
                    quantity = if (quantity > AppConstants.DECIMAL_INTERVAL)
                        decimalFormat.format((quantity - AppConstants.DECIMAL_INTERVAL)).toFloat()
                    else
                        0f
                } else
                    quantity--

                productBean?.prod_quantity = quantity

                if (quantity == 0f) {
                    StaticFunction.removeFromCart(activity, productBean?.product_id, 0)

                } else {
                    StaticFunction.updateCart(activity, productBean?.product_id, quantity, productBean?.price?.toFloat()
                            ?: 0.0f)
                }

                productBean?.let { productBeans[parentPosition].value?.set(childPosition, it) }

                if (adapter != null) {
                    adapter?.notifyItemChanged(parentPos)
                }

            }

            showBottomCart()
        }

    }

    override fun onProdDetail(productBean: ProductDataBean?) {

        if (settingBean?.isProductDetailCheck != null && settingBean?.isProductDetailCheck == "1") return

        if (isFromBranch) {
            //   productBean?.supplier_id=supplierBranchId
            productBean?.supplier_name = supplierBranchName
        }
        productBean?.is_out_network = supplierDetail?.is_out_network

        val bundle = Bundle()
        bundle.putInt("productId", productBean?.product_id ?: 0)
        bundle.putString("title", productBean?.name)
        bundle.putInt("categoryId", productBean?.category_id ?: 0)
        bundle.putInt("supplier_id", productBean?.supplier_id ?: 0)
        bundle.putInt("supplier_branch_id", productBean?.supplier_branch_id ?: 0)
        bundle.putInt("mDeliveryType", deliveryType)
        // ProductDetails productDetails = new ProductDetails();
        bundle.putInt("offerType", 0)

        productBean?.self_pickup = deliveryType
        productBean?.type = screenFlowBean?.app_type
        if (AppConstants.APP_SUB_TYPE > AppDataType.Custom.type) {
            productBean?.payment_after_confirmation = settingBean?.payment_after_confirmation?.toIntOrNull()
                    ?: 0
        }
        navController(this).navigate(R.id.action_restaurantDetailFrag_to_productDetails,
                ProductDetails.newInstance(productBean, 0, false, isResutantOpen))
    }
    override fun onVehicleSelect(id:String,model: String, color: String, plate_no: String) {
        TODO("Not yet implemented")
    }
    override fun onDescExpand(tvDesc: TextView?, productBean: ProductDataBean?, childPosition: Int) {
        /*   if (productBean?.isExpand==true) {
               CommonUtils.collapseTextView(tvDesc, tvDesc?.lineCount)
           } else {
               productBean?.isExpand==true*/
        CommonUtils.expandTextView(tvDesc)
        // }
    }

    override fun onProdDesc(productDesc: String) {
        StaticFunction.sweetDialogueSuccess11(activity, getString(R.string.description), Utils.getHtmlData(productDesc).toString(), false, 1002, this)
    }

    override fun onProdDialog(bean: ProductDataBean?) {
        mDialogsUtil.showProductDialog(requireContext(), bean)
    }

    override fun onEditQuantity(quantity: Float, productBean: ProductDataBean?, parentPosition: Int, childPosition: Int, isOpen: Boolean) {
        if (parentPosition == -1 && childPosition == -1) return

        hideKeyboard()
        if (settingBean?.is_decimal_quantity_allowed == "1") {
            checkEditQuantity(quantity, productBean, parentPosition, childPosition, isOpen)
        }
    }

    override fun onProdAllergiesClicked(bean: ProductDataBean) {
        appUtils.showAllergiesDialog(requireContext(), bean.allergy_description ?: "")
    }

    private fun addCart(updatedQuantity: Float?, productModel: ProductDataBean?) {
        val cartList: CartList? = dataManager.getGsonValue(DataNames.CART, CartList::class.java)

        if (screenFlowBean?.app_type == AppDataType.Food.type) {
            if (cartList != null && cartList.cartInfos?.size!! > 0) {
                if (cartList.cartInfos?.any { cartList.cartInfos?.get(0)?.deliveryType != it.deliveryType } == true) {
                    appUtils.clearCart()

                    mBinding?.root?.onSnackbar("Cart Clear Successfully")
                }
            }
        }

        if (isFromBranch) {
            productModel?.supplier_branch_id = supplierBranchId
            productModel?.supplier_name = supplierBranchName
        }


        if (productModel?.prod_quantity == 0f) {

            productModel.prod_quantity = if (settingBean?.is_decimal_quantity_allowed == "1") AppConstants.DECIMAL_INTERVAL else 1f

            productModel.self_pickup = deliveryType

            productModel.type = screenFlowBean?.app_type

            if (AppConstants.APP_SUB_TYPE > AppDataType.Custom.type) {
                productModel.payment_after_confirmation = settingBean?.payment_after_confirmation?.toIntOrNull()
                        ?: 0
            }
            appUtils.addProductDb(activity ?: requireContext(), screenFlowBean?.app_type
                    ?: 0, productModel)

        } else {
            var quantity: Float
            if (updatedQuantity == null) {
                quantity = productModel?.prod_quantity ?: 0f

                if (settingBean?.is_decimal_quantity_allowed == "1")
                    quantity = decimalFormat.format((quantity + AppConstants.DECIMAL_INTERVAL)).toFloat()
                else
                    quantity++
            } else
                quantity = updatedQuantity


            val remaingProd = productModel?.quantity?.minus(productModel.purchased_quantity ?: 0f)
                    ?: 0f

            val checkPurchaseLimit = if (settingBean?.enable_item_purchase_limit == "1" &&
                    productModel?.purchase_limit != null && productModel.purchase_limit != 0f) {
                quantity <= productModel.purchase_limit ?: 0f
            } else true




            if (quantity <= remaingProd && checkPurchaseLimit) {
                productModel?.prod_quantity = quantity

                StaticFunction.updateCart(activity, productModel?.product_id, quantity, productModel?.netPrice
                        ?: 0.0f)
            } else {
                mBinding?.root?.onSnackbar(getString(R.string.maximum_limit_cart))
            }
        }


        val productBean = productBeans[parentPos]

        productModel?.let { productBean.value?.set(childPos, it) }


        if (adapter != null) {
            adapter?.notifyItemChanged(parentPos)
        }

    }

    private fun clearCart() {

        productBeans.map {
            it.value?.map { itt ->
                itt.prod_quantity = 0f
            }
        }

        /*     for (i in productBeans.indices) {

                 productBeans[i].value?.mapIndexed { index, valueBean ->
                     productBeans[i].value?.get(index)?.prod_quantity = 0
                 }
             }*/


        if (adapter != null)
            adapter?.notifyDataSetChanged()

        showBottomCart()
    }

    override fun onSucessListner() {

        if (isResutantOpen) {
            appUtils.clearCart()
            clearCart()
        }
    }

    override fun onSuccessListener() {
        navController(this)
                .navigate(R.id.action_restaurantDetailFrag_to_bookedTables, null)
    }


    override fun onErrorListener() {

    }

    override fun getMenuCategory(position: Int) {

        if (tooltip != null && tooltip?.isShowing == true) {
            tooltip?.dismiss()
        }
        if (settingBean?.is_new_menu_theme == "1" || settingBean?.enable_rest_pagination_category_wise == "1") {
            tab_layout.getTabAt(position)?.select()
        } else {
            // appbar.setExpanded(false)
            val y = rvproductList.getChildAt(position).y

            nested_scrollview.post {
                nested_scrollview.fling(0)
                nested_scrollview.smoothScrollTo(0, y.toInt())
            }
        }
    }

    override fun onMenuSelected(position: Int) {
        //  appbar.setExpanded(false)
        if (settingBean?.is_new_menu_theme == "1" || settingBean?.enable_rest_pagination_category_wise == "1") {
            tab_layout.getTabAt(position)?.select()
        } else {
            val y = rvproductList.getChildAt(position).y

            nested_scrollview.post {
                nested_scrollview.fling(0)
                nested_scrollview.smoothScrollTo(0, y.toInt())
            }
        }
    }


    override fun favResponse() {
        //   iv_favourite.isChecked = true
        mBinding?.root?.onSnackbar(getString(R.string.successFavourite))
    }

    override fun unFavResponse() {

        //  iv_favourite.isChecked = false
        mBinding?.root?.onSnackbar(getString(R.string.successUnFavourite))
    }

    override fun onTableSuccessfullyBooked() {
        selectedDateTimeForScheduling = null
        StaticFunction.sweetDialogueSuccess11(activity, getString(R.string.table_book_message_title),
                getString(R.string.table_book_message),
                false, 1001, this)
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


        productBeans.mapIndexed { index, productBean ->

            if (productBean.sub_cat_name == productModel.sub_category_name && productBean.value?.indexOf(productModel) != -1) {
                productModel.let {
                    productBean.value?.set(productBean.value?.indexOf(productModel) ?: 0, it)
                }
            }

        }

        if (adapter != null) {
            if (iv_search_prod.text.toString().isNotEmpty()) {
                adapter?.notifyDataSetChanged()
            } else {
                adapter?.notifyItemChanged(parentPos)
            }
        }
    }

    private fun uploadImage() {
        if (permissionFile.hasCameraPermissions(activity ?: requireContext())) {
            if (isNetworkConnected) {
                showImagePicker()
            }
        } else {
            permissionFile.cameraAndGalleryTask(this)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)


        if (requestCode == AppSettingsDialog.DEFAULT_SETTINGS_REQ_CODE) {
            // Do something after user returned from app settings screen, like showing a Toast.
            mBinding?.root?.onSnackbar(getString(R.string.returned_from_app_settings_to_activity))
        }

        if (resultCode == AppCompatActivity.RESULT_OK && requestCode == AppConstants.CameraPicker) {

            if (isNetworkConnected) {
                if (photoFile?.isRooted == true) {

                    uploadPrescImage(imageUtils.compressImage(photoFile?.absolutePath
                            ?: ""), screenFlowBean?.app_type.toString())
                }
            }
        } else if (resultCode == AppCompatActivity.RESULT_OK && requestCode == AppConstants.GalleyPicker) {
            if (data != null) {
                if (isNetworkConnected) {
                    //data.getData return the content URI for the selected Image
                    val selectedImage = data.data
                    val filePathColumn = arrayOf(MediaStore.Images.Media.DATA)
                    // Get the cursor
                    val cursor = activity?.contentResolver?.query(selectedImage!!, filePathColumn, null, null, null)
                    // Move to first row
                    cursor?.moveToFirst()
                    //Get the column index of MediaStore.Images.Media.DATA
                    val columnIndex = cursor?.getColumnIndex(filePathColumn[0])
                    //Gets the String value in the column
                    val imgDecodableString = cursor?.getString(columnIndex ?: 0)
                    cursor?.close()


                    if (imgDecodableString?.isNotEmpty() == true) {
                        photoFile = File(imgDecodableString)
                        uploadPrescImage(imageUtils.compressImage(imgDecodableString), screenFlowBean?.app_type.toString())
                    }
                }
            }
        } else if (requestCode == AppConstants.REQUEST_PRES_UPLOAD && resultCode == Activity.RESULT_OK) {
            if (settingBean?.show_ecom_v2_theme == "1") {
                AddressDialogFragmentV2.newInstance(0).show(childFragmentManager, "dialog")
            } else {
                AddressDialogFragment.newInstance(0,0).show(childFragmentManager, "dialog")
            }

        } else if (requestCode == SCHEDULE_REQUEST && resultCode == Activity.RESULT_OK) {
            selectedDateTimeForScheduling = data?.getParcelableExtra<SupplierSlots>("slotDetail")
            if (settingBean?.by_pass_tables_selection == "1") {
                bookTableWithSchedule("0", supplierId)
            } else {
                getListOfRestaurantsAccordingToSlot()
            }
        }
    }

    private fun getListOfRestaurantsAccordingToSlot() {

        val bundle = bundleOf("slot_id" to selectedDateTimeForScheduling?.supplierTimings?.get(0)?.id.toString(),
                "supplierId" to supplierId,
                "branchId" to if (supplierBranchId == 0) {
                    viewModel.supplierLiveData.value?.result?.data?.supplier_detail?.supplier_branch_id
                } else {
                    supplierBranchId
                },
                "requestFromCart" to "0"
        )
        navController(this)
                .navigate(R.id.action_restaurantDetailFrag_to_tableSelection, bundle)

    }

    private fun bookTableWithSchedule(tableId: String?, supplierId: Int?) {
        val tempRequestHolder = hashMapOf(
                "user_id" to mDataManager.getKeyValue(PrefenceConstants.USER_ID, PrefenceConstants.TYPE_STRING).toString(),
                "table_id" to tableId,
                "slot_id" to selectedDateTimeForScheduling?.supplierTimings?.get(0)?.id.toString(),
                "schedule_date" to selectedDateTimeForScheduling?.startDateTime,
                "schedule_end_date" to selectedDateTimeForScheduling?.endDateTime,
                "supplier_id" to supplierId.toString(),
                "branch_id" to if (supplierBranchId == 0) {
                    viewModel.supplierLiveData.value?.result?.data?.supplier_detail?.supplier_branch_id.toString()
                } else {
                    supplierBranchId.toString()
                }
        )
        viewModel.makeBookingAccordingToSlot(tempRequestHolder, null, null)
    }

    private fun uploadPrescImage(compressImage: String, appType: String) {

        if (isNetworkConnected) {
            showLoading()
            viewModel.uploadPresImage(compressImage,
                    viewModel.supplierLiveData.value?.result?.data?.supplier_detail?.supplier_branch_id.toString(), adrsBean?.id, appType)
        }
    }

    override fun onPdf() {

    }

    override fun onGallery() {
        val pickIntent = Intent(Intent.ACTION_PICK)
        pickIntent.type = "image/*"
        startActivityForResult(pickIntent, AppConstants.GalleyPicker)
    }

    override fun onCamera() {
        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
            // Ensure that there's a camera activity to handle the intent
            takePictureIntent.resolveActivity(activity?.packageManager!!)?.also {
                // Create the File where the photo should go
                photoFile = try {
                    ImageUtility.filename(imageUtils)
                } catch (ex: IOException) {
                    // Error occurred while creating the File
                    null
                }
                // Continue only if the File was successfully created
                photoFile?.also {
                    val photoURI: Uri = FileProvider.getUriForFile(
                            activity ?: requireContext(),
                            activity?.packageName ?: "",
                            it)

                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                    startActivityForResult(takePictureIntent, AppConstants.CameraPicker)
                }
            }
        }
    }

    override fun onPermissionsDenied(requestCode: Int, perms: MutableList<String>) {
        if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
            AppSettingsDialog.Builder(this).build().show()
        }
    }

    override fun onPermissionsGranted(requestCode: Int, perms: MutableList<String>) {
        if (requestCode == AppConstants.CameraGalleryPicker) {

            if (isNetworkConnected) {
                showImagePicker()
            }
        }
    }

    override fun onRequestPermissionsResult(
            requestCode: Int,
            permissions: Array<String>,
            grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        // EasyPermissions handles the request result.
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this)
    }

    private fun showImagePicker() {
        imageDialog.settingCallback(this)
        imageDialog.setPdfUpload(true)
        imageDialog.show(
                childFragmentManager,
                "image_picker"
        )
    }

    override fun onAddressSelect(adrsBean: AddressBean) {

        this.adrsBean = adrsBean

        adrsBean.let {
            dataManager.addGsonValue(PrefenceConstants.ADRS_DATA, Gson().toJson(it))
        }

        uploadImage()

    }

    override fun onDestroyDialog() {

    }

    /**
     * Called when a tab that is already selected is chosen again by the user. Some applications may
     * use this action to return to the top level of a category.
     *
     * @param tab The tab that was reselected.
     */
    override fun onTabReselected(tab: TabLayout.Tab?) {

    }

    /**
     * Called when a tab exits the selected state.
     *
     * @param tab The tab that was unselected
     */
    override fun onTabUnselected(tab: TabLayout.Tab?) {

    }

    /**
     * Called when a tab enters the selected state.
     *
     * @param tab The tab that was selected
     */
    override fun onTabSelected(tab: TabLayout.Tab?) {
        if (settingBean?.is_new_menu_theme == "1" && settingBean?.enable_rest_pagination_category_wise != "1") {
            adapter?.settingList(productBeans)
            adapter?.filter?.filter(iv_search_prod?.text.toString())
            val firstVisiblePos = prodlytManager?.findFirstVisibleItemPosition()
            if (firstVisiblePos != tab?.position)
                loadApp(tab?.position)
        } else if (settingBean?.enable_rest_pagination_category_wise == "1" && !categoryList.isNullOrEmpty()) {
            isNewCategory = true
            val categoryId = categoryList[tab?.position ?: 0].category_id
            getProductList(supplierId, supplierBranchId, true, categoryId)
        }
    }


    private fun loadApp(position: Int?) {
        showLoading()
        Handler().postDelayed(
                {
                    // After the screen duration, route to the right activities
                    hideLoading()
                    smoothScroller?.targetPosition = position ?: 0
                    prodlytManager?.startSmoothScroll(smoothScroller)

                },
                2000
        )
    }

    private fun onRecyclerViewScrolled() {

        rvproductList.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val pos = prodlytManager?.findFirstVisibleItemPosition()
                val lastVisiblePos = prodlytManager?.findLastCompletelyVisibleItemPosition()
                if (pos != null && pos != -1 && pos != tab_layout?.selectedTabPosition &&
                        lastVisiblePos != tab_layout?.selectedTabPosition && iv_search_prod?.text?.isEmpty() == true) {
                    tab_layout.getTabAt(pos)?.select()
                }

            }
        })
    }

    override fun onImageClicked(images: String) {

    }


    private fun onRecyclerScrolled() {

        rvproductList.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val isPagingActive = viewModel.validForPaging()
                //lytManager
                // val position = (recyclerView.layoutManager as LinearLayoutManager?)?.findFirstVisibleItemPosition()
                // Log.e("ss", "" + position)
                if (!recyclerView.canScrollVertically(1) && isPagingActive) {
                    val categoryId = if (settingBean?.enable_rest_pagination_category_wise == "1" && !categoryList.isNullOrEmpty())
                        categoryList[tab_layout?.selectedTabPosition ?: 0].category_id
                    else null
                    getProductList(supplierId, supplierBranchId, false, categoryId)
                }
            }
        })
    }

    override fun onEmptyList(count: Int) {
        noDataVisibility(count == 0)
    }

    private fun noDataVisibility(isNoData: Boolean) {
        if (isNoData) {
            noData.visibility = View.VISIBLE
            rvproductList.visibility = View.GONE
        } else {
            noData.visibility = View.GONE
            rvproductList.visibility = View.VISIBLE
        }
    }


}
