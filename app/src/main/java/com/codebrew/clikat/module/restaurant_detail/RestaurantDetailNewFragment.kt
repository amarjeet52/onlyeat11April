package com.codebrew.clikat.module.restaurant_detail

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.provider.MediaStore
import android.view.Gravity
import android.view.View
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.NavOptions
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ItemDecoration
import com.codebrew.clikat.BR
import com.codebrew.clikat.R
import com.codebrew.clikat.app_utils.*
import com.codebrew.clikat.app_utils.dialogintrface.ImageDialgFragment
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
import com.codebrew.clikat.data.model.api.orderDetail.Agent
import com.codebrew.clikat.data.model.api.orderDetail.OrderHistory
import com.codebrew.clikat.data.model.others.CustomPayModel
import com.codebrew.clikat.data.model.others.EditProductsItem
import com.codebrew.clikat.data.model.others.SaveCardInputModel
import com.codebrew.clikat.data.preferences.PreferenceHelper
import com.codebrew.clikat.databinding.FragmentRestaurantDetailNewBinding
import com.codebrew.clikat.di.ViewModelProviderFactory
import com.codebrew.clikat.modal.CartList
import com.codebrew.clikat.modal.LocationUser
import com.codebrew.clikat.modal.PojoSignUp
import com.codebrew.clikat.modal.other.*
import com.codebrew.clikat.modal.slots.SupplierSlots
import com.codebrew.clikat.module.addon_quant.SavedAddon
import com.codebrew.clikat.module.cart.REQUEST_PROMO_CODE
import com.codebrew.clikat.module.cart.SCHEDULE_REQUEST
import com.codebrew.clikat.module.cart.addproduct.AddProductDialog
import com.codebrew.clikat.module.cart.promocode.PromoCodeListActivity
import com.codebrew.clikat.module.cart.schedule_order.ScheduleOrder
import com.codebrew.clikat.module.dialog_adress.AddressDialogFragment
import com.codebrew.clikat.module.dialog_adress.interfaces.AddressDialogListener
import com.codebrew.clikat.module.dialog_adress.v2.AddressDialogFragmentV2
import com.codebrew.clikat.module.login.LoginActivity
import com.codebrew.clikat.module.main_screen.OnOrderEdited
import com.codebrew.clikat.module.payment_gateway.PaymentListActivity
import com.codebrew.clikat.module.product.product_listing.DialogListener
import com.codebrew.clikat.module.product_addon.AddonFragment
import com.codebrew.clikat.module.product_detail.ProductDetails
import com.codebrew.clikat.module.restaurant_detail.adapter.MenuCategoryAdapter
import com.codebrew.clikat.module.restaurant_detail.adapter.ProdListAdapter
import com.codebrew.clikat.module.restaurant_detail.adapter.RestImagesViewPagerAdapter
import com.codebrew.clikat.module.restaurant_detail.adapter.SupplierProdListAdapter
import com.codebrew.clikat.module.restaurant_detail.reviewRating.ReviewsListActivity
import com.codebrew.clikat.preferences.DataNames
import com.codebrew.clikat.user_chat.UserChatActivity
import com.codebrew.clikat.utils.DialogIntrface
import com.codebrew.clikat.utils.StaticFunction
import com.codebrew.clikat.utils.StaticFunction.checkVisibility
import com.codebrew.clikat.utils.Utils
import com.codebrew.clikat.utils.configurations.Configurations
import com.google.android.material.tabs.TabLayout
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.codebrew.clikat.intrface.ImageCallback
import io.github.douglasjunior.androidSimpleTooltip.SimpleTooltip
import kotlinx.android.synthetic.main.fragment_restaurant_detail_new.*
import kotlinx.android.synthetic.main.layout_bottom_cart.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import pub.devrel.easypermissions.AppSettingsDialog
import pub.devrel.easypermissions.EasyPermissions
import java.io.File
import java.io.IOException
import java.lang.reflect.Type
import java.text.DecimalFormat
import javax.inject.Inject

/**
 * A simple [Fragment] subclass.
 */
class RestaurantDetailNewFragment : BaseFragment<FragmentRestaurantDetailNewBinding, RestDetailViewModel>(),
        ProdListAdapter.ProdCallback, DialogListener, MenuCategoryAdapter.MenuCategoryCallback,
        RestDetailNavigator, AddonFragment.AddonCallback, RestImagesViewPagerAdapter.OnImageClicked,
        ImageCallback, EasyPermissions.PermissionCallbacks, AddressDialogListener, DialogIntrface, TabLayout.OnTabSelectedListener, OnTableCapacityListener,
        AddProductDialog.OnAddProductListener, EmptyListListener {

    private var isNonVeg: String? = null
    private var selectedCurrency: Currency? = null
    private var viewPagerAdapter: RestImagesViewPagerAdapter? = null
    private var parentPos: Int = 0
    private var childPos: Int = 0
    private var adapter: RecyclerView.Adapter<RecyclerView.ViewHolder>? = null
    private var productBeans = mutableListOf<ProductBean>()
    private var categoryList = mutableListOf<ProductBean>()
    private var bookingFlowBean: SettingModel.DataBean.BookingFlowBean? = null
    private var prodlytManager: LinearLayoutManager? = null
    private var deliveryType = 0
    private var supplierId = 0
    private var supplierBranchId = 0
    private var supplierDetail: SupplierDetailBean? = null
    private val decimalFormat: DecimalFormat = DecimalFormat("0.00")

    @Inject
    lateinit var dataManager: DataManager

    @Inject
    lateinit var factory: ViewModelProviderFactory

    @Inject
    lateinit var mDataManager: PreferenceHelper

    @Inject
    lateinit var appUtils: AppUtils

    @Inject
    lateinit var mDialogsUtil: DialogsUtil

    @Inject
    lateinit var imageUtils: ImageUtility

    @Inject
    lateinit var permissionFile: PermissionFile

    private var photoFile: File? = null

    private val imageDialog by lazy { ImageDialgFragment() }

    private var screenFlowBean: SettingModel.DataBean.ScreenFlowBean? = null

    private var mBinding: FragmentRestaurantDetailNewBinding? = null

    private lateinit var viewModel: RestDetailViewModel

    private var isResutantOpen: Boolean = false

    private var colorConfig = Configurations.colors
    private val textConfig = Configurations.strings
    var tooltip: SimpleTooltip? = null
    private var terminologyBean: SettingModel.DataBean.Terminology? = null
    private var adrsBean: AddressBean? = null
    var settingBean: SettingModel.DataBean.SettingData? = null

    var isFromBranch: Boolean = false
    var supplierBranchName = ""
    private var orderDetail: OrderHistory? = null
    private var selectedProductsList: ArrayList<EditProductsItem>? = arrayListOf()
    private var selectedDateTimeForScheduling: SupplierSlots? = null

    private var onEditOrder: OnOrderEdited? = null

    private var mProdTabList = mutableListOf<ProductDataBean>()

    // private var tableCapacity = ArrayList<String>()
    private var isNewCategory = false

    override fun getBindingVariable(): Int {
        return BR.viewModel
    }

    override fun getLayoutId(): Int {
        return R.layout.fragment_restaurant_detail_new
    }

    override fun getViewModel(): RestDetailViewModel {
        viewModel = ViewModelProviders.of(this, factory).get(RestDetailViewModel::class.java)
        return viewModel
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        EventBus.getDefault().register(this)
        viewModel.navigator = this
        terminologyBean = dataManager.getGsonValue(PrefenceConstants.APP_TERMINOLOGY, SettingModel.DataBean.Terminology::class.java)

        screenFlowBean = dataManager.getGsonValue(DataNames.SCREEN_FLOW, SettingModel.DataBean.ScreenFlowBean::class.java)
        settingBean = dataManager.getGsonValue(DataNames.SETTING_DATA, SettingModel.DataBean.SettingData::class.java)
        selectedCurrency = dataManager.getGsonValue(PrefenceConstants.CURRENCY_INF, Currency::class.java)
        restDetailObserver()
        imageObserver()
        subCategoryObserver()
        //   tableCapacityObserver()
        restCategoryObserver()
    }

    private fun subCategoryObserver() {
        // Create the observer which updates the UI.
        val catObserver = Observer<SubCatData> { resource ->
            resource?.sub_category_data?.mapIndexed { index, subCategoryData ->
                tab_layout.addTab(tab_layout.newTab().setText(subCategoryData.name).setTag(subCategoryData.category_id
                        ?: 0))
            }
        }

        // Observe the LiveData, passing in this activity as the LifecycleOwner and the observer.
        viewModel.subCatLiveData.observe(this, catObserver)
    }

    private fun tableCapacityObserver() {
        // Create the observer which updates the UI.
        val observer = Observer<ArrayList<String>> { resource ->
            //  tableCapacity.clear()
            //  tableCapacity.addAll(resource)
        }

        // Observe the LiveData, passing in this activity as the LifecycleOwner and the observer.
        viewModel.tableCapacityLiveData.observe(this, observer)
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onResultReceived(result: ListItem?) {
        if (result?.requestedFrom == "1") {
            return
        }
        bookTableWithSchedule(result?.id.toString(), supplierId, null, null)
    }

    override fun onDestroy() {
        super.onDestroy()
        EventBus.getDefault().unregister(this)
    }

    @SuppressLint("NewApi")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mBinding = viewDataBinding

        viewDataBinding.color = Configurations.colors

        viewDataBinding.drawables = Configurations.drawables
        viewDataBinding.strings = Configurations.strings

        viewDataBinding.isSupplierRating = settingBean?.is_supplier_rating == "1"

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            mainmenu?.foreground?.alpha = 0
        }

        initialise()
        getValues()
        settingLayout()
        setclikListener()

        if (orderDetail == null || orderDetail?.editOrder == false)
            showBottomCart()
        else
            bottom_cart.visibility = View.GONE

        setPrescListener()

        //  onRecyclerViewScrolled()

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

    private fun initialise() {
        viewPagerAdapter = RestImagesViewPagerAdapter(requireContext(), this)

        ivSupplierBanner.adapter = viewPagerAdapter
        if (settingBean?.show_social_links != "1") {
            ivFacebook?.visibility = View.GONE
            ivInstagram?.visibility = View.GONE
        }

        if (settingBean?.supplier_to_user_chat == "1")
            ivChat?.visibility = View.VISIBLE
        else
            ivChat?.visibility = View.GONE


    }

    private fun restDetailObserver() {
        // Create the observer which updates the UI.
        val catObserver = Observer<PagingResult<SuplierProdListModel>> { resource ->
            refreshAdapter(resource?.result?.data)
            if (resource.isFirstPage)
                resource?.result?.data?.supplier_detail?.let {
                    settingSupplierDetail(it)
                    /* if (tableCapacity.isNullOrEmpty() && it.is_dine_in == 1 && settingBean?.is_table_booking == "1" && settingBean?.table_book_mac_theme == "1") {
                         getTableCapacityApi(it)
                     }*/
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


    @RequiresApi(Build.VERSION_CODES.M)
    private fun setclikListener() {

        ivSearch?.setOnClickListener {
            val list = ArrayList<ProductBean>()
            list.addAll(productBeans)
            val bundle = Bundle()
            bundle.putInt("supplierId", supplierId)
            bundle.putInt("deliveryType", deliveryType)
            if (orderDetail?.editOrder == true) {
                bundle.putParcelableArrayList("selectedList", selectedProductsList)
                bundle.putParcelable("orderItem", orderDetail)
            }
            navController(this@RestaurantDetailNewFragment).navigate(R.id.action_restaurantDetailFrag_to_search, bundle)
        }

        /*
        iv_search_prod.setOnSearchClickListener { v: View? ->
            btn_menu.visibility = View.INVISIBLE
            appbar.setExpanded(false)
        }


        iv_search_prod.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(s: String): Boolean {
                adapter!!.filter.filter(s)
                adapter!!.notifyDataSetChanged()
                Log.e("search_view", "setOnQueryTextListener$s")
                return true
            }

            override fun onQueryTextChange(s: String): Boolean {
                //when the text change
                adapter!!.filter.filter(s)
                adapter!!.notifyDataSetChanged()
                Log.e("search_view", "onQueryTextChange$s")
                return true
            }
        })
        iv_search_prod.setOnCloseListener {

            //when canceling the search
            appbar.setExpanded(true)
            hideKeyboard()
            btn_menu.setVisibility(View.VISIBLE)
            false
        }
*/

        ic_back.setOnClickListener {
            activity?.onBackPressed()
            //navController(this@RestaurantDetailNewFragment).popBackStack()
        }


        ivFacebook?.setOnClickListener {
            if (!fbLink.isNullOrEmpty()) {
                StaticFunction.openCustomChrome(requireActivity(), fbLink ?: "")
            }
        }
        ivInstagram?.setOnClickListener {
            if (!instaLink.isNullOrEmpty()) {
                StaticFunction.openCustomChrome(requireActivity(), instaLink ?: "")
            }
        }

        btn_menu.setOnClickListener {
            //displayPopupWindow(it)
            showPopUp(it)
        }

        btnEdit?.setOnClickListener {
            onEditOrder?.onRestDetailEdited(orderDetail, selectedProductsList)
        }

        ivChat?.setOnClickListener {
            if (dataManager.getCurrentUserLoggedIn()) {
                val data = Agent()
                data.image = supplierDetail?.supplier_image?.firstOrNull()
                data.name = supplierDetail?.name
                data.message_id = supplierDetail?.message_id
                data.user_created_id = supplierDetail?.user_created_id
                data.agent_created_id = supplierDetail?.id.toString()
                activity?.launchActivity<UserChatActivity> {
                    putExtra("userType", ReceiverType.SUPPLIER.type)
                    putExtra("userData", data)
                }
            } else {
                activity?.launchActivity<LoginActivity>()
            }
        }

        btnBookTable?.setOnClickListener {
            if (dataManager.getCurrentUserLoggedIn()) {
                if (settingBean?.table_book_mac_theme == "1") {
                    //   val array = ArrayList<String>()
                    //   array.addAll(tableCapacity)
                    StaticFunction.tableCapacityDialog(requireContext(), this)
                } else {
                    activity?.launchActivity<ScheduleOrder>(SCHEDULE_REQUEST) {
                        putExtra("supplierId", supplierId.toString())
                        putExtra("dineIn", true)
                    }
                }
            } else {
                appUtils.checkLoginFlow(requireActivity(), DataNames.REQUEST_RESTAURANT_LOGIN)
            }
        }
        tvPromoCodes?.setOnClickListener {
            if (dataManager.getCurrentUserLoggedIn()) {
                val suplierIds = ArrayList<String>()
                suplierIds.add(supplierId.toString())
                startActivityForResult(Intent(requireContext(), PromoCodeListActivity::class.java).putExtra("supplierIds", suplierIds)
                        .putExtra("showApply", false), REQUEST_PROMO_CODE)
            } else {
                appUtils.checkLoginFlow(requireActivity(), DataNames.REQUEST_RESTAURANT_LOGIN)
            }
        }
        tvReviewRatings?.setOnClickListener {
            startActivity(Intent(requireContext(), ReviewsListActivity::class.java).putExtra("supplierDetail", supplierDetail))
        }

        switchVeg?.setOnCheckedChangeListener { compoundButton, b ->
            isNonVeg = if (b) "2" else null
            productBeans.clear()
            tab_layout?.removeAllTabs()
            hitApi()
        }

    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnOrderEdited)
            onEditOrder = context
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun showPopUp(view: View) {
        tooltip = SimpleTooltip.Builder(activity)
                .anchorView(view)
                .text(R.string.menu)
                .gravity(Gravity.TOP)
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
                    mainmenu?.foreground?.alpha = 0
                }
                .onShowListener {
                    mainmenu?.foreground?.alpha = 120
                }
                .contentView(R.layout.popup_restaurant_menu, R.id.menu)
                .focusable(true)
                .build()


        // tooltip?.color = Configurations.colors
        val list = if (settingBean?.enable_rest_pagination_category_wise == "1") categoryList else productBeans

        val rvCategory = tooltip?.findViewById<RecyclerView>(R.id.rvmenu_category)
        val layoutManager = LinearLayoutManager(activity, RecyclerView.VERTICAL, false)
        rvCategory?.layoutManager = layoutManager
        val adapter = MenuCategoryAdapter(list.distinctBy { it.sub_cat_name }.map { it.sub_cat_name }.toList())
        rvCategory?.adapter = adapter
        adapter.settingCallback(this)

        val itemDecoration: ItemDecoration = DividerItemDecoration(activity, DividerItemDecoration.VERTICAL)
        rvCategory?.addItemDecoration(itemDecoration)

        tooltip?.show()

    }


    private fun settingLayout() {
        btnEdit?.visibility = View.GONE
        productBeans.clear()

        if (settingBean?.enable_rest_pagination_category_wise == "1") {
            tab_layout.visibility = View.VISIBLE
            btn_menu?.visibility = View.VISIBLE
            adapter = SupplierProdListAdapter(this@RestaurantDetailNewFragment, settingBean, appUtils, selectedCurrency, this, "old")

            tab_layout.addOnTabSelectedListener(this)
            onRecyclerScrolled()
        } else if (settingBean?.custom_rest_prod == "1") {
            tab_layout.visibility = View.VISIBLE
            adapter = ProdListAdapter(settingBean, selectedCurrency)
            (adapter as ProdListAdapter).settingCallback(this, appUtils)

            tab_layout.addOnTabSelectedListener(this)
        } else {
            tab_layout.visibility = View.GONE
            adapter = SupplierProdListAdapter(this@RestaurantDetailNewFragment, settingBean, appUtils, selectedCurrency, this, "old")

            if (settingBean?.rest_detail_pagin == "1") {
                onRecyclerScrolled()
            }
        }

        prodlytManager = LinearLayoutManager(activity, RecyclerView.VERTICAL, false)
        rvproductList.layoutManager = prodlytManager

        rvproductList.adapter = adapter


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

            if (arguments?.containsKey("isFromBranch") == true) {
                if (arguments?.getBoolean("isFromBranch") == true) {
                    //  btn_branches.visibility = View.GONE
                    isFromBranch = true
                }

                if (arguments?.containsKey("supplierBranchName") == true) {
                    supplierBranchName = arguments?.getString("supplierBranchName", "") ?: ""
                }
            }

            if (arguments?.containsKey("supplierId") == true) {
                supplierId = arguments?.getInt("supplierId") ?: 0
                if (arguments?.containsKey("branchId") == true) {
                    supplierBranchId = arguments?.getInt("branchId") ?: 0
                }
                hitApi()
            } else if (arguments?.containsKey("orderItem") == true) {
                if (orderDetail == null) {
                    orderDetail = arguments?.getParcelable("orderItem")
                    supplierId = orderDetail?.supplier_id ?: 0
                }
                btnEdit?.visibility = View.VISIBLE
                hitApi()
            }

            if (arguments?.containsKey("selectedList") == true) {
                selectedProductsList = arguments?.getParcelableArrayList("selectedList")
            }
        }

        deliveryType = if (settingBean?.is_skip_theme == "1") 1 else deliveryType

        val currentTableData = mDataManager.getCurrentTableData()
        if (currentTableData != null) {
            supplierId = currentTableData.supplier_id?.toInt() ?: 0
            supplierBranchId = currentTableData.branch_id?.toInt() ?: 0
            btnBookTable?.visibility = View.GONE
            hitApi()
        }
    }

    private fun hitApi() {
        if (settingBean?.enable_rest_pagination_category_wise == "1")
            getAllSupplierCategories()
        else
            getProductList(supplierId, supplierBranchId, true, null, is_non_veg = isNonVeg)
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


    private fun getTableCapacityApi(it: SupplierDetailBean) {
        if (isNetworkConnected)
            viewModel.getTableCapacity(it.id.toString(), it.supplier_branch_id.toString())
    }

    private fun showBottomCart() {

        val appCartModel = appUtils.getCartData(settingBean, selectedCurrency)

        if (screenFlowBean?.app_type == AppDataType.Ecom.type) {
            bottom_cart?.visibility = View.GONE
        } else {
            bottom_cart?.visibility = View.VISIBLE
        }
        if (appCartModel.cartAvail) {
            tv_total_price.text = getString(R.string.total).plus(" ").plus(getString(R.string.currency_tag, AppConstants.CURRENCY_SYMBOL, appCartModel.totalPrice))
/*
            tv_total_price.text = getString(R.string.total).plus(" ").plus(AppConstants.CURRENCY_SYMBOL).plus(" ").plus(appCartModel.totalPrice)
*/
            val totalCount = Utils.getDecimalPointValue(settingBean, appCartModel.totalCount)

            tv_total_product.text = getString(R.string.total_item_tag, totalCount)

            if (appCartModel.supplierName.isNotEmpty() && screenFlowBean?.is_single_vendor == VendorAppType.Multiple.appType) {
                tv_supplier_name.visibility = View.VISIBLE
                tv_supplier_name.text = getString(R.string.supplier_tag, appCartModel.supplierName)
            } else {
                tv_supplier_name.visibility = View.GONE
            }

            bottom_cart.setOnClickListener {

                val navOptions: NavOptions = NavOptions.Builder()
                        .setPopUpTo(R.id.restaurantDetailFragNew, false)
                        .build()

                navController(this@RestaurantDetailNewFragment).navigate(R.id.action_restaurantDetailFrag_to_cart, null, navOptions)
            }
        } else {
            bottom_cart.visibility = View.GONE
        }

    }

    private fun getValues() {
        bookingFlowBean = dataManager.getGsonValue(DataNames.BOOKING_FLOW, SettingModel.DataBean.BookingFlowBean::class.java)
    }


    private fun getProductList(supplierId: Int, supplierBranchId: Int = 0, isFirstPage: Boolean, categoryId: String?, is_non_veg: String? = null) {

        if (isNetworkConnected)
            if (isFromBranch) {
                viewModel.getBranchProductList(supplierId.toString(), supplierBranchId.toString(), isFirstPage)
            } else {
                viewModel.showWhiteScreen.set(supplierDetail == null)
                viewModel.getProductList(supplierId.toString(), isFirstPage, settingBean, categoryId, is_non_veg=is_non_veg)
            }
    }

    private fun refreshAdapter(data: DataBean?) {

        noDataVisibility(data?.product?.count() == 0)

        viewDataBinding.supplierModel = data?.supplier_detail

        var subCatName: String? = null
        val previousCount = productBeans.count()

        if (isNewCategory || viewModel.skip == 0) {
            productBeans.clear()
        }

        if (data?.product?.isNotEmpty() == true) {
            isNewCategory = false
            data.product?.map { prod ->

                prod.is_SubCat_visible = true
                if (orderDetail != null && orderDetail?.editOrder == true) {
                    prod.value = changeProductList(prod.value ?: mutableListOf())
                } else {
                    prod.value?.map {
                        it.netPrice = if (it.fixed_price?.toFloatOrNull() ?: 0.0f > 0) it.fixed_price?.toFloatOrNull() else 0f
                        it.prod_quantity = StaticFunction.getCartQuantity(requireActivity(), it.product_id)
                    }
                }

                if (prod.detailed_category_name?.isNotEmpty() == true) {
                    prod.detailed_category_name.distinctBy { it.detailed_sub_category_id }.forEach { detailProd ->
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

            if (settingBean?.custom_rest_prod == "1" && productBeans.isNotEmpty() && settingBean?.enable_rest_pagination_category_wise != "1") {
                productBeans.forEachIndexed { index, productBean ->
                    tab_layout.addTab(tab_layout.newTab().setText(productBean.sub_cat_name))
                }

                updateTabListProd(productBeans.firstOrNull()?.value ?: mutableListOf(), 0)
            } else {

                if (adapter != null && adapter is SupplierProdListAdapter) {
                    (adapter as SupplierProdListAdapter).settingList(productBeans)
                    (adapter as SupplierProdListAdapter).filter.filter("")
                }
            }
        }
    }

    private fun updateTabListProd(prodList: MutableList<ProductDataBean>, parentPosition: Int) {
        (adapter as ProdListAdapter).updateParentPos(parentPosition)

        mProdTabList.clear()
        mProdTabList.addAll(prodList)

        if (adapter is ProdListAdapter) {
            activity?.runOnUiThread {
                (adapter as ProdListAdapter).addItmSubmitList(mProdTabList)
            }
            /*{

                activity?.runOnUiThread {
                    if (it) {
                        hideLoading()
                    } else {
                        showLoading()
                    }
                }
            }*/
        } else {
            adapter?.notifyDataSetChanged()
        }

    }

    private fun changeProductList(product: MutableList<ProductDataBean>): ArrayList<ProductDataBean> {
        val values = ArrayList<ProductDataBean>()
        for (j in 0 until product.size) {
            val dataProduct = product[j]
            dataProduct.netPrice =
                    if (dataProduct.fixed_price?.toFloatOrNull() ?: 0.0f > 0) dataProduct.fixed_price?.toFloatOrNull() else 0f

            val index = orderDetail?.product?.indexOfFirst {
                it?.product_id == dataProduct.product_id
            }

            if (index == null || index == -1) {
                val selectedItemPos = selectedProductsList?.indexOfFirst { it.productId == dataProduct.product_id }
                if (selectedItemPos != null && selectedItemPos != -1)
                    dataProduct.prod_quantity = selectedProductsList?.get(selectedItemPos)?.quantity

                values.add(dataProduct)
            }

        }
        return values
    }

    private var fbLink: String? = null
    private var instaLink: String? = null
    private fun settingSupplierDetail(supplier_detail: SupplierDetailBean) {
        supplierDetail = supplier_detail
        fbLink = supplier_detail.facebook_link
        instaLink = supplier_detail.linkedin_link
        isResutantOpen = appUtils.checkResturntTiming(supplier_detail.timing)
        deliveryType = if (supplier_detail.is_out_network == 1) FoodAppType.Delivery.foodType else deliveryType

        if (adapter != null) {
            when (adapter) {
                is SupplierProdListAdapter -> {
                    (adapter as SupplierProdListAdapter).checkResturantOpen(isResutantOpen)
                }
                is ProdListAdapter -> {
                    (adapter as ProdListAdapter).updateRestTime(isResutantOpen)
                }
            }
        }

        StaticFunction.loadImage(supplier_detail.logo, ivSupplierIcon, false)

        if (supplier_detail.supplier_image?.isNotEmpty() == true) {
            viewPagerAdapter?.addImages(supplier_detail.supplier_image ?: emptyList())
        }


        btnBookTable?.visibility = if (settingBean?.table_book_mac_theme == "1") {
            if (deliveryType == FoodAppType.DineIn.foodType) View.VISIBLE else View.GONE
        } else if (supplier_detail.is_dine_in == 1 && settingBean?.is_table_booking == "1")
            View.VISIBLE
        else
            View.GONE


        tvFreeDelivery?.visibility = if (supplier_detail.is_free_delivery == 1) View.VISIBLE else View.GONE

        val currentTableData = mDataManager.getCurrentTableData()
        if (currentTableData != null) {
            btnBookTable?.visibility = View.GONE
        }
        switchVeg?.visibility = if (settingBean?.enable_non_veg_filter == "1") View.VISIBLE else View.GONE
        iv_favourite?.visibility = if (settingBean?.is_supplier_wishlist == "1") View.VISIBLE else View.GONE

        if (isFromBranch) {
            tv_name.text = supplierBranchName
        } else {
            tv_name.text = supplier_detail.name
        }

        if (supplier_detail.rating == 0f) {
            tvRating.text = "0.0"
            rbRating?.rating = 0f
        } else {
            tvRating.text = supplier_detail.rating.toString()
            rbRating?.rating = supplier_detail.rating ?: 0f
        }


        val defaultValue = settingBean?.show_supplier_info_settings

        if (!supplier_detail.address.isNullOrEmpty() && settingBean?.hide_supplier_address != "1") {
            tvAddress.visibility = View.VISIBLE
            tvAddress?.text = supplier_detail.address
        } else tvAddress.visibility = View.GONE


        if (checkVisibility(settingBean?.show_supplier_open_close, defaultValue)) {
            groupOpenClose.visibility = View.VISIBLE
            tvStatus?.text = if (isResutantOpen) getString(R.string.open_) else getString(R.string.close)
        } else groupOpenClose.visibility = View.GONE

        if (!supplier_detail.speciality.isNullOrEmpty() && checkVisibility(settingBean?.show_supplier_speciality, defaultValue)) {
            group_speciality.visibility = View.VISIBLE
            tvSpeciality?.text = supplier_detail.speciality
        } else group_speciality.visibility = View.GONE


        if (!supplier_detail.phone.isNullOrEmpty() && checkVisibility(settingBean?.show_supplier_phone, defaultValue)) {
            groupPhone?.visibility = View.VISIBLE
            tvPhone?.text = supplier_detail.phone
        } else groupPhone?.visibility = View.GONE

        if (!supplier_detail.email.isNullOrEmpty() && checkVisibility(settingBean?.show_supplier_email, defaultValue)) {
            groupEmail?.visibility = View.VISIBLE
            tvEmail?.text = supplier_detail.email
        } else groupEmail?.visibility = View.GONE

        if (!supplier_detail.nationality.isNullOrEmpty() && checkVisibility(settingBean?.show_supplier_nationality, defaultValue)) {
            tvNationality?.visibility = View.VISIBLE
            tvNationalityTag?.visibility = View.VISIBLE
            tvNationality?.text = supplier_detail.nationality
        } else {
            tvNationality?.visibility = View.GONE
            tvNationalityTag?.visibility = View.GONE
        }

        if (!supplier_detail.brand.isNullOrEmpty() && checkVisibility(settingBean?.show_supplier_brand_name, defaultValue)) {
            tvBrand?.visibility = View.VISIBLE
            tvBrandTag?.visibility = View.VISIBLE
            tvBrand?.text = supplier_detail.brand
        } else {
            tvBrand?.visibility = View.GONE
            tvBrandTag?.visibility = View.GONE
        }

        if (!supplier_detail.category.isNullOrEmpty() && checkVisibility(settingBean?.show_supplier_categories, defaultValue)) {
            groupTags?.visibility = View.VISIBLE
            tvTagText?.text = getString(R.string.categories_text, textConfig.categories)
            tvTags?.text = supplier_detail.category.joinToString(",") { it.category_name }
        } else
            groupTags?.visibility = View.GONE

        if (supplier_detail.delivery_max_time != null && checkVisibility(settingBean?.show_supplier_delivery_timing, defaultValue)) {

            groupTime?.visibility = if (settingBean?.table_book_mac_theme == "1") {
                if (deliveryType == FoodAppType.DineIn.foodType || deliveryType == FoodAppType.Pickup.foodType) View.GONE else View.VISIBLE
            } else View.VISIBLE

            tvTime?.text = getString(R.string.delivery_mins, supplier_detail.delivery_max_time.toString())
        } else groupTime?.visibility = View.GONE


        iv_favourite.setOnCheckedChangeListener(null)
        iv_favourite.isChecked = supplier_detail.Favourite == 1

        iv_favourite.setOnCheckedChangeListener { checkBox, isChecked ->
            if (isNetworkConnected) {
                if (dataManager.getCurrentUserLoggedIn()) {
                    if (isChecked) {
                        viewModel.markFavSupplier(supplierId.toString())
                    } else {
                        viewModel.unFavSupplier(supplierId.toString())
                    }
                } else {
                    activity?.launchActivity<LoginActivity>()
                }
            }
        }

        tvPromoCodes?.visibility = if (settingBean?.enable_supplier_promo_list == "1" && supplier_detail.is_promo_codes == 1) View.VISIBLE else View.GONE
        tvReviewRatings?.visibility = if (settingBean?.enable_supplier_review_list == "1" && !supplier_detail.ratingAndReviews.isNullOrEmpty()) View.VISIBLE else View.GONE

        if (settingBean?.show_prescription_requests != null && settingBean?.show_prescription_requests == "1") {
            supplier_detail.user_request_flag.let {
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

    override fun onImageClicked(images: String) {
        startActivity(Intent(requireContext(), VideoPlayer::class.java).putExtra("link", images))
    }

    private fun addInSelectedList(productDataBean: ProductDataBean?) {
        val item = EditProductsItem()
        item.productId = productDataBean?.product_id
        item.branchId = productDataBean?.supplier_branch_id
        item.imagePath = productDataBean?.image_path.toString()
        item.productDesc = productDataBean?.product_desc
        item.quantity = productDataBean?.prod_quantity
        item.price = productDataBean?.price?.toFloat()
        item.productName = productDataBean?.name
        item.handling_admin = productDataBean?.handling_admin?.toDouble()
        selectedProductsList?.add(item)
    }
    override fun onVehicleSelect(id:String,model: String, color: String, plate_no: String) {
        TODO("Not yet implemented")
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

    override fun onProdAdded(productBean: ProductDataBean?, parentPosition: Int, childPosition: Int, isOpen: Boolean) {
        if (parentPosition == -1 && childPosition == -1) return
        val cartList: CartList? = dataManager.getGsonValue(DataNames.CART, CartList::class.java)

        val userData = mDataManager.getGsonValue(DataNames.USER_DATA, PojoSignUp::class.java)
        if (settingBean?.table_book_mac_theme == "1" && deliveryType == FoodAppType.DineIn.foodType && selectedDateTimeForScheduling == null) {
            mBinding?.root?.onSnackbar(getString(R.string.please_book_table_first))
            return
        } else if (productBean?.is_subscription_required == 1 && userData?.data?.is_subscribed != 1) {
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

    private fun checkEditQuantity(updatedQuantity: Float?, productBean: ProductDataBean?, parentPosition: Int, childPosition: Int, isOpen: Boolean) {
        var isFromDifferentTYpe = false
        if (isOpen) {
            mDialogsUtil.openAlertDialog(activity
                    ?: requireContext(), getString(R.string.offline_supplier_tag, Configurations.strings.supplier), getString(R.string.ok), "", this)
            return
        }

        productBean?.type = screenFlowBean?.app_type

        this.parentPos = parentPosition
        this.childPos = childPosition
        /*without customization in case of edit order */
        if (orderDetail != null && orderDetail?.editOrder == true) {

            productBean?.prod_quantity = if (settingBean?.is_decimal_quantity_allowed == "1")
                decimalFormat.format(productBean?.prod_quantity?.plus(0.15)?.toFloat()).toFloat()
            else
                productBean?.prod_quantity?.plus(1f)

            val index =
                    selectedProductsList?.indexOfFirst { it.productId == productBean?.product_id }
            if (index != null && index != -1)
                selectedProductsList?.get(index)?.quantity = productBean?.prod_quantity
            else
                addInSelectedList(productBean)

            adapter?.notifyItemChanged(parentPosition)

        } else {
            if (deliveryType == FoodAppType.Pickup.foodType || deliveryType == FoodAppType.DineIn.foodType) {
                if (viewModel.supplierLiveData.value == null) return

                val mRestUser = LocationUser()
                mRestUser.address = "${
                    viewModel.supplierLiveData.value?.result?.data?.supplier_detail?.name
                            ?: ""
                } , ${viewModel.supplierLiveData.value?.result?.data?.supplier_detail?.address}"
                dataManager.addGsonValue(PrefenceConstants.RESTAURANT_INF, Gson().toJson(mRestUser))
            }

            if (screenFlowBean?.is_single_vendor == VendorAppType.Multiple.appType) {
                val cartList: CartList? = dataManager.getGsonValue(DataNames.CART, CartList::class.java)

                if (cartList != null && cartList.cartInfos?.isNotEmpty() == true) {

                    if (cartList.cartInfos?.any { it.deliveryType != deliveryType } == true) {
                        isFromDifferentTYpe = true
                        AlertDialog.Builder(requireContext())
                                .setMessage(getString(R.string.previous_cart_items_removed, textConfig.proceed))
                                .setCancelable(false)
                                .setPositiveButton(getString(R.string.yes)) { _, _ ->
                                    appUtils.clearCart()
                                    clearCart()

                                    viewModel.supplierLiveData.value?.result?.data?.product?.map {
                                        it.value?.map {
                                            it.prod_quantity = 0f
                                        }
                                    }

                                    refreshAdapter(viewModel.supplierLiveData.value?.result?.data)
                                    addToCart(updatedQuantity, productBean)
                                }
                                .show()

                    }
                }
            }

            if (!isFromDifferentTYpe) {
                addToCart(updatedQuantity, productBean)
            }
        }
    }

    private fun addToCart(updatedQuantity: Float?, productBean: ProductDataBean?) {
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
            if (bookingFlowBean?.vendor_status == 0 && appUtils.checkVendorStatus(productBean?.supplier_id,
                            vendorBranchId = productBean?.supplier_branch_id, branchFlow = settingBean?.branch_flow)) {
                openAlertDialog(updatedQuantity, getString(R.string.clearCart, Configurations.strings.supplier
                        ?: "", textConfig.proceed ?: ""), productBean)
            } else {
                if (appUtils.checkBookingFlow(requireContext(), productBean?.product_id, this)) {
                    addCart(updatedQuantity, productBean)
                }
                showBottomCart()
            }

        }
    }

    private fun openAlertDialog(updatedQuantity: Float?, message: String, productBean: ProductDataBean?) {
        val builder = AlertDialog.Builder(requireContext())
        builder.setPositiveButton(getString(R.string.yes)) { dialog, _ ->
            dialog.dismiss()
            if (isResutantOpen) {
                appUtils.clearCart()
                clearCart()
            }
            if (appUtils.checkBookingFlow(requireContext(), productBean?.product_id, this)) {
                addCart(updatedQuantity, productBean)
            }
            showBottomCart()
        }
        builder.setNegativeButton(getString(R.string.no)) { dialog, _ ->
            dialog.dismiss()
        }
        builder.setMessage(message)
        builder.setCancelable(false)
        builder.create().show()
    }

    override fun onProdDelete(productBean: ProductDataBean?, parentPosition: Int, childPosition: Int, isOpen: Boolean) {

        if (parentPosition == -1 && childPosition == -1 && productBean?.prod_quantity ?: 0f < 0) return

        this.parentPos = parentPosition
        this.childPos = childPosition

        if (orderDetail != null && orderDetail?.editOrder == true) {
            productBean?.prod_quantity = if (settingBean?.is_decimal_quantity_allowed == "1") {
                if (productBean?.prod_quantity ?: 0f > AppConstants.DECIMAL_INTERVAL)
                    decimalFormat.format(productBean?.prod_quantity?.minus(0.15f)).toFloat()
                else
                    0f
            } else
                productBean?.prod_quantity?.minus(1f)

            val index =
                    selectedProductsList?.indexOfFirst { it.productId == productBean?.product_id }
            if (index != null && index != -1)
                removeFromSelectedList(productBean, index)

            adapter?.notifyItemChanged(parentPosition)

        } else {
            if (productBean?.adds_on?.isNotEmpty() == true) {
                val cartList: CartList? = dataManager.getGsonValue(DataNames.CART, CartList::class.java)

                val savedProduct = cartList?.cartInfos?.filter {
                    it.supplierId == supplierId && it.productId == productBean.product_id
                } ?: emptyList()

                SavedAddon.newInstance(productBean, deliveryType, savedProduct, this).show(childFragmentManager, "savedAddon")

            } else {
                var quantity = productBean?.prod_quantity ?: 0f

                if (quantity > 0f) {
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
    }

    private fun removeFromSelectedList(
            productBean: ProductDataBean?,
            index: Int
    ) {
        if (productBean?.prod_quantity != 0f)
            selectedProductsList?.get(index)?.quantity = productBean?.prod_quantity
        else
            selectedProductsList?.removeAt(index)
    }

    override fun onProdDetail(productBean: ProductDataBean?) {

        if (settingBean?.isProductDetailCheck != null && settingBean?.isProductDetailCheck == "1") return

        if (isFromBranch) {
            // productBean?.supplier_id=supplierBranchId
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
            productBean?.payment_after_confirmation = settingBean?.payment_after_confirmation?.toInt()
                    ?: 0
        }


        if (activity == null) return
        navController(this).navigate(R.id.action_restaurantDetailFrag_to_productDetails,
                ProductDetails.newInstance(productBean, 0, false, isResutantOpen, orderDetail?.editOrder
                        ?: false))
//        Navigation.findNavController(requireView()).navigate(R.id.action_restaurantDetailFrag_to_productDetails, bundle)
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


    private fun addCart(updatedQuantity: Float?, productModel: ProductDataBean?) {

        if (screenFlowBean?.app_type == AppDataType.Food.type) {
            val cartList: CartList? = dataManager.getGsonValue(DataNames.CART, CartList::class.java)

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
            if (settingBean?.is_decimal_quantity_allowed == "1")
                productModel.prod_quantity = AppConstants.DECIMAL_INTERVAL
            else
                productModel.prod_quantity = 1f

            productModel.self_pickup = deliveryType
            productModel.type = screenFlowBean?.app_type
            if (AppConstants.APP_SUB_TYPE > AppDataType.Custom.type) {
                productModel.payment_after_confirmation = settingBean?.payment_after_confirmation?.toInt()
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

            if (quantity <= remaingProd) {
                productModel?.prod_quantity = quantity

                StaticFunction.updateCart(activity, productModel?.product_id, quantity, productModel?.netPrice
                        ?: 0.0f)
            } else {
                mBinding?.root?.onSnackbar(getString(R.string.maximum_limit_cart))
            }
        }


        val productBean = productBeans[parentPos]

        productModel?.let { productBean.value?.set(childPos, it) }

        productBeans[parentPos] = productBean

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
                .navigate(R.id.action_restaurantDetailFragNew_to_bookedTables, null)
    }


    override fun onErrorListener() {

    }

    override fun getMenuCategory(position: Int) {

        if (tooltip != null && tooltip?.isShowing == true) {
            tooltip?.dismiss()
        }


        if (settingBean?.enable_rest_pagination_category_wise == "1") {
            tab_layout.getTabAt(position)?.select()
        } else {
            appbar.setExpanded(false)
            if (settingBean?.custom_rest_prod == "1") {
                tab_layout.getTabAt(position)?.select()
            } else {
                prodlytManager?.scrollToPosition(position)
            }
        }
    }


    override fun favResponse() {
        iv_favourite.isChecked = true
        mBinding?.root?.onSnackbar(getString(R.string.success_wishlist, textConfig.supplier, textConfig.wishlist))
    }

    override fun unFavResponse() {
        iv_favourite.isChecked = false
        mBinding?.root?.onSnackbar(getString(R.string.removed_wishlist, textConfig.supplier, textConfig.wishlist))
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
            if (productBean.sub_cat_name == productModel.sub_category_name) {
                productModel.let {
                    if (productBean.value?.indexOf(productModel) != -1) {
                        productBean.value?.set(productBean.value?.indexOf(productModel) ?: 0, it)
                    }
                }
            }
        }

        if (adapter != null) {
            adapter?.notifyItemChanged(parentPos)
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

        } else if (requestCode == AppConstants.REQUEST_PAYMENT_OPTION && resultCode == Activity.RESULT_OK) {
            val payItem: CustomPayModel = data?.getParcelableExtra("payItem") ?: CustomPayModel()
            val savedCard = data?.getParcelableExtra<SaveCardInputModel>("savedCard")
            if (savedCard != null)
                payItem.payment_token = savedCard.gateway_unique_id

            bookTableWithSchedule(selectedDateTimeForScheduling?.selectedTable?.id?.toString(), supplierId, payItem, savedCard)
        } else if (requestCode == SCHEDULE_REQUEST && resultCode == Activity.RESULT_OK) {
            selectedDateTimeForScheduling = data?.getParcelableExtra("slotDetail")
            if (settingBean?.table_book_mac_theme == "1" && selectedDateTimeForScheduling?.selectedTable != null) {
                AlertDialog.Builder(requireContext()).setMessage(getString(R.string.select_items_to_continue))
                        .setCancelable(false)
                        .setPositiveButton(getString(R.string.yes)) { _, _ ->
                            mDataManager.setkeyValue(DataNames.SAVED_TABLE_DATA, Gson().toJson(selectedDateTimeForScheduling))
                        }
                        .setNegativeButton(getString(R.string.no)) { _, _ ->
                            if (selectedDateTimeForScheduling?.selectedTable?.table_booking_price != null &&
                                    selectedDateTimeForScheduling?.selectedTable?.table_booking_price == 0f) {
                                bookTableWithSchedule(selectedDateTimeForScheduling?.selectedTable?.id?.toString(), supplierId, null, null)
                            } else
                                onPaymentSelection()
                        }
                        .show()
            } else {
                if (settingBean?.by_pass_tables_selection == "1") {
                    bookTableWithSchedule("0", supplierId, null, null)
                } else {
                    getListOfRestaurantsAccordingToSlot()
                }
            }
        }
    }

    private fun onPaymentSelection() {
        dataManager.getKeyValue(DataNames.FEATURE_DATA, PrefenceConstants.TYPE_STRING).toString().let {
            val listType: Type = object : TypeToken<ArrayList<SettingModel.DataBean.FeatureData?>?>() {}.type
            val featureList: ArrayList<SettingModel.DataBean.FeatureData> = Gson().fromJson(it, listType)

            activity?.launchActivity<PaymentListActivity>(AppConstants.REQUEST_PAYMENT_OPTION) {
                putParcelableArrayListExtra("feature_data", featureList)
                putExtra("enablePayment", true)
                putExtra("totalAmount", selectedDateTimeForScheduling?.selectedTable?.table_booking_price)
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
                .navigate(R.id.action_restaurantDetailFragNew_to_tableSelection, bundle)

    }


    private fun bookTableWithSchedule(tableId: String?, supplierId: Int?, paymentItem: CustomPayModel?, savedCard: SaveCardInputModel?) {
        val languageId = dataManager.getKeyValue(DataNames.SELECTED_LANGUAGE, PrefenceConstants.TYPE_STRING).toString()
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
                },
                "amount" to if (settingBean?.table_book_mac_theme == "1" && paymentItem != null) selectedDateTimeForScheduling?.selectedTable?.table_booking_price.toString() else "",
                "currency" to selectedCurrency?.currency_name,
                "languageId" to languageId

        )

        viewModel.makeBookingAccordingToSlot(tempRequestHolder, paymentItem, savedCard)
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

    private fun apiGetSubCategories(categoryId: Int, supplierId: Int) {

        val hashMap = dataManager.updateUserInf()
        hashMap["supplier_id"] = supplierId.toString()
        hashMap["category_id"] = categoryId.toString()
        if (isNetworkConnected) {
            viewModel.getSubCategory(hashMap)
        }
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
                    getProductList(supplierId, supplierBranchId, false, categoryId, isNonVeg)
                }
            }
        })
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
        if (settingBean?.enable_rest_pagination_category_wise == "1" && !categoryList.isNullOrEmpty()) {
            isNewCategory = true

            val categoryId = categoryList[tab?.position ?: 0].category_id
            getProductList(supplierId, supplierBranchId, true, categoryId, isNonVeg)
        } else {
            loadApp()

            updateTabListProd(productBeans[tab?.position ?: 0].value
                    ?: mutableListOf(), tab?.position ?: 0)
        }
    }

    private fun loadApp() {
        showLoading()
        Handler().postDelayed(
                {
                    // After the screen duration, route to the right activities
                    hideLoading()
                },
                3000
        )
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

    override fun onTableCapacitySelected(capacity: Int) {
        activity?.launchActivity<ScheduleOrder>(SCHEDULE_REQUEST) {
            putExtra("supplierId", supplierId.toString())
            putExtra("dineIn", true)
            putExtra("seating_capacity", capacity)
            putExtra("supplierBranchId", supplierDetail?.supplier_branch_id)
        }
    }

    override fun onProductAdded(productBean: ProductDataBean?, parentPosition: Int, childPosition: Int, isOpen: Boolean) {
        checkEditQuantity(null, productBean, parentPosition, childPosition, isOpen)
    }


}
