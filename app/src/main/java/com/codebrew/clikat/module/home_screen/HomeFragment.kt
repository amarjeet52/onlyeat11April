package com.codebrew.clikat.module.home_screen


import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.Typeface
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Parcelable
import android.util.Log
import android.view.*
import android.view.inputmethod.EditorInfo
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavOptions
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.bumptech.glide.Glide
import com.clevertap.android.sdk.CleverTapAPI
import com.codebrew.clikat.BR
import com.codebrew.clikat.BuildConfig
import com.codebrew.clikat.R
import com.codebrew.clikat.app_utils.*
import com.codebrew.clikat.app_utils.extension.*
import com.codebrew.clikat.base.BaseFragment
import com.codebrew.clikat.base.PagingResult
import com.codebrew.clikat.data.*
import com.codebrew.clikat.data.constants.AppConstants
import com.codebrew.clikat.data.constants.AppConstants.Companion.DELIVERY_OPTIONS
import com.codebrew.clikat.data.constants.PrefenceConstants
import com.codebrew.clikat.data.model.api.AddressBean
import com.codebrew.clikat.data.model.api.Currency
import com.codebrew.clikat.data.model.api.ListItem
import com.codebrew.clikat.data.model.api.orderDetail.OrderHistory
import com.codebrew.clikat.data.model.api.supplier_detail.DataSupplierDetail
import com.codebrew.clikat.data.model.others.AgentCustomParam
import com.codebrew.clikat.data.model.others.CustomPayModel
import com.codebrew.clikat.data.network.HostSelectionInterceptor
import com.codebrew.clikat.data.preferences.PreferenceHelper
import com.codebrew.clikat.databinding.FragmentEcommerceBinding
import com.codebrew.clikat.databinding.PopupRestaurantMenuBinding
import com.codebrew.clikat.di.ViewModelProviderFactory
import com.codebrew.clikat.modal.*
import com.codebrew.clikat.modal.other.*
import com.codebrew.clikat.modal.slots.SupplierSlots
import com.codebrew.clikat.module.addon_quant.SavedAddon
import com.codebrew.clikat.module.cart.SCHEDULE_REQUEST
import com.codebrew.clikat.module.cart.schedule_order.ScheduleOrder
import com.codebrew.clikat.module.completed_order.adapter.OrderHistoryAdapter
import com.codebrew.clikat.module.dialog_adress.AddressDialogFragment
import com.codebrew.clikat.module.dialog_adress.interfaces.AddressDialogListener
import com.codebrew.clikat.module.dialog_adress.v2.AddressDialogFragmentV2
import com.codebrew.clikat.module.filter.BottomSheetFragment
import com.codebrew.clikat.module.home_screen.adapter.*
import com.codebrew.clikat.module.home_screen.listeners.OnSortByListenerClicked
import com.codebrew.clikat.module.home_screen.resturant_home.WALLET_REQUEST
import com.codebrew.clikat.module.home_screen.suppliers.SuppliersMapFragment
import com.codebrew.clikat.module.payment_gateway.PaymentListActivity
import com.codebrew.clikat.module.product.product_listing.DialogListener
import com.codebrew.clikat.module.product_addon.AddonFragment
import com.codebrew.clikat.module.product_detail.ProductDetails
import com.codebrew.clikat.module.restaurant_detail.OnTableCapacityListener
import com.codebrew.clikat.module.restaurant_detail.adapter.MenuCategoryAdapter
import com.codebrew.clikat.module.restaurant_detail.adapter.ProdListAdapter
import com.codebrew.clikat.module.service_selection.ServSelectionActivity
import com.codebrew.clikat.module.user_tracking.UserTracking
import com.codebrew.clikat.preferences.DataNames
import com.codebrew.clikat.utils.DialogIntrface
import com.codebrew.clikat.utils.StaticFunction
import com.codebrew.clikat.utils.Utils
import com.codebrew.clikat.utils.Utils.getDecimalPointValue
import com.codebrew.clikat.utils.configurations.Configurations
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.android.synthetic.main.fragment_ecommerce.*
import kotlinx.android.synthetic.main.fragment_ecommerce.bottom_cart
import kotlinx.android.synthetic.main.fragment_ecommerce.toolbar_layout
import kotlinx.android.synthetic.main.item_search_view.*
import kotlinx.android.synthetic.main.layout_bottom_cart.*
import kotlinx.android.synthetic.main.toolbar_home.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import pub.devrel.easypermissions.AppSettingsDialog
import pub.devrel.easypermissions.EasyPermissions
import retrofit2.Retrofit
import ru.nikartm.support.BadgePosition
import java.lang.reflect.Type
import java.util.*
import javax.inject.Inject
import kotlin.collections.ArrayList
import kotlin.String as String1
import com.codebrew.clikat.modal.other.Data
import kotlinx.android.synthetic.main.layout_home_updated.*

/**
 * A simple [Fragment  ] subclass.
 */


class HomeFragment : BaseFragment<FragmentEcommerceBinding, HomeViewModel>(),
    SpecialListAdapter.OnProductDetail, CategoryListAdapter.CategoryDetail,
    SponsorListAdapter.SponsorDetail, DialogListener, BannerListAdapter.BannerCallback,
    HomeItemAdapter.SupplierListCallback, SwipeRefreshLayout.OnRefreshListener,
    BrandsListAdapter.BrandCallback, HomeNavigator, View.OnClickListener,
    AddressDialogListener, ProdListAdapter.ProdCallback, MenuCategoryAdapter.MenuCategoryCallback,
    View.OnAttachStateChangeListener, AddonFragment.AddonCallback, DialogIntrface,
    EasyPermissions.PermissionCallbacks, OnSortByListenerClicked,
    NearBySuppliersAdapter.SponsorDetail, AdapterView.OnItemSelectedListener,
    OrderHistoryAdapter.OrderHisCallback, OnTableCapacityListener {
    var cleverTapDefaultInstance: CleverTapAPI? = null
    private var firstHomeSection: SettingModel.DataBean.DynamicScreenSections? = null

    private lateinit var viewModel: HomeViewModel

    private var mHomeScreenBinding: FragmentEcommerceBinding? = null

    private var catId = 0

    private var self_Pickup = ""

    @Inject
    lateinit var factory: ViewModelProviderFactory

    @Inject
    lateinit var prefHelper: PreferenceHelper

    @Inject
    lateinit var dataManager: DataManager

    @Inject
    lateinit var retrofit: Retrofit

    @Inject
    lateinit var interceptor: HostSelectionInterceptor

    @Inject
    lateinit var permissionUtil: PermissionFile


    @Inject
    lateinit var appUtils: AppUtils

    @Inject
    lateinit var prodUtils: ProdUtils

    private val bannerHandler = Handler()

    private var bannerRunnable: Runnable? = null

    private var phoneNum = ""

    private var parentPos: Int = 0
    private var childPos: Int = 0
    private var fetchHighestRatingSuppliers = false
    private var fetchNewRestaurantSuppliers = false
    private var fetchRecentOrders = false
    private var fetchCategoryWiseSuppliers = false
    private var fetchPopularSuppliers = false
    private var speProductId = 0
    private var prodStatus = 0
    var name = ""
    private var supplierBundle: Bundle? = null

    private val textConfig by lazy { appUtils.loadAppConfig(0).strings }
    private val colorConfig by lazy { Configurations.colors }
    private var selectedCurrency: Currency? = null
    // val compositeDisposable: CompositeDisposable = CompositeDisposable()

    override fun getBindingVariable(): Int {
        return BR.viewModel
    }

    override fun getLayoutId(): Int = R.layout.fragment_ecommerce


    override fun getViewModel(): HomeViewModel {
        activity?.let {
            viewModel = ViewModelProviders.of(it, factory).get(HomeViewModel::class.java)
        }

        return viewModel
    }


    private var specialOffers: MutableList<ProductDataBean>? = null
    private var mSupplierList: MutableList<SupplierDataBean>? = null
    private var mPopularList: MutableList<ProductDataBean>? = null

    private var homeItemAdapter: HomeItemAdapter? = null

    private var specialListAdapter: SpecialListAdapter? = null

    private var homelytManager: LinearLayoutManager? = null

    private var categoryCount = 0

    private var specialCount = 0

    private var screenFlowBean: SettingModel.DataBean.ScreenFlowBean? = null

    private var bookingFlowBean: SettingModel.DataBean.BookingFlowBean? = null

    private var clientInform: SettingModel.DataBean.SettingData? = null

    private var categoryModel: Data? = null

    private var sectionsList: SettingModel.DataBean.DynamicSectionsData? = null

    private var offerListModel: OfferDataBean? = null
    private val sharedPrefFile = "kotlinsharedpreference"
    private var popup: PopupWindow? = null
    private val currenyList: MutableList<Currency>? = mutableListOf()
    lateinit var editor: SharedPreferences.Editor
    lateinit var sharedPreferences: SharedPreferences
    lateinit var imageView: ImageView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.navigator = this
        cleverTapDefaultInstance = CleverTapAPI.getDefaultInstance(requireActivity())
        cleverTapDefaultInstance?.enableDeviceNetworkInfoReporting(true);
        sharedPreferences =
            requireActivity().getSharedPreferences(sharedPrefFile, Context.MODE_PRIVATE)
        editor = sharedPreferences.edit()
        // EventBus.getDefault().register(this)
        catId =
            if (prefHelper.getKeyValue(PrefenceConstants.CATEGORY_ID, PrefenceConstants.TYPE_STRING)
                    .toString().isNotEmpty()
            ) {
                prefHelper.getKeyValue(PrefenceConstants.CATEGORY_ID, PrefenceConstants.TYPE_STRING)
                    .toString().toInt()
            } else {
                0
            }

        screenFlowBean = prefHelper.getGsonValue(
            DataNames.SCREEN_FLOW,
            SettingModel.DataBean.ScreenFlowBean::class.java
        )
        bookingFlowBean = prefHelper.getGsonValue(
            DataNames.BOOKING_FLOW,
            SettingModel.DataBean.BookingFlowBean::class.java
        )
        clientInform = prefHelper.getGsonValue(
            DataNames.SETTING_DATA,
            SettingModel.DataBean.SettingData::class.java
        )
        firstHomeSection = prefHelper.getGsonValue(
            PrefenceConstants.FIRST_HOME_SECTION,
            SettingModel.DataBean.DynamicScreenSections::class.java
        )
        selectedCurrency =
            dataManager.getGsonValue(PrefenceConstants.CURRENCY_INF, Currency::class.java)
        /*  if (clientInform?.full_view_supplier_theme == null) {
              clientInform?.full_view_supplier_theme = "1"
              prefHelper.setkeyValue(DataNames.SETTING_DATA, Gson().toJson(clientInform))

          }*/



        categoryObserver()
        productObserver()
        offersObserver()
        supplierObserver()
        dynamicSupplierListCheck()
        highestRatingSupplierObserver()
        popularObserver()
        branchSupplierObserver()
        categoryWiseObserver()
        lastOrdersObserver()
        // tableCapacityObserver()

    }


    private fun dynamicSupplierListCheck() {
        lifecycleScope.launch {
            withContext(Dispatchers.Default) {

                if (sectionsList == null)
                    sectionsList = dataManager.getGsonValue(
                        DataNames.HOME_DYNAMIC_SECTIONS,
                        SettingModel.DataBean.DynamicSectionsData::class.java
                    )

                if (clientInform?.dynamic_home_screen_sections == "1" && sectionsList != null) {
                    val newRestuarentSection = sectionsList?.list?.filter {
                        it.code == "new_resturant"
                    }

                    val categoryWiseSection = sectionsList?.list?.filter {
                        it.code == "category_wise_rest"
                    }

                    val popularSection = sectionsList?.list?.filter {
                        it.code == "top_selling"
                    }
                    val recentOrders = sectionsList?.list?.filter {
                        it.code == "recent_orders"
                    }

                    if (!popularSection.isNullOrEmpty())
                        fetchPopularSuppliers = true

                    if (!categoryWiseSection.isNullOrEmpty()) {
                        fetchCategoryWiseSuppliers = true
                    }
                    if (!newRestuarentSection.isNullOrEmpty()) {
                        fetchNewRestaurantSuppliers = true
                    }
                    if (!recentOrders.isNullOrEmpty()) {
                        fetchRecentOrders = true
                    }
                }
            }
        }
    }


    private fun highestRatingSupplierObserver() {

        lifecycleScope.launch {
            val ratingSection = withContext(Dispatchers.Default) {
                checkHighestRatingSection()
            }

            if (ratingSection) {

                fetchHighestRatingSuppliers = true
                val supplierObserver = Observer<PagingResult<List<SupplierDataBean>>> { resource ->

                    if (viewModel.supplierLiveData.value != null && viewModel.supplierLiveData.value?.isFirstPage == true) {
                        updateSupplierData(viewModel.supplierLiveData.value?.result)
                    } else
                        fetchSupplierList(SortBy.SortByDistance.sortBy.toString(), true)
                }

                // Observe the LiveData, passing in this activity as the LifecycleOwner and the observer.
                viewModel.highestRatingSupplierLiveData.observe(this@HomeFragment, supplierObserver)
            }
        }
    }

    private fun checkHighestRatingSection(): Boolean {
        if (sectionsList == null)
            sectionsList = dataManager.getGsonValue(
                DataNames.HOME_DYNAMIC_SECTIONS,
                SettingModel.DataBean.DynamicSectionsData::class.java
            )

        return if (clientInform?.dynamic_home_screen_sections == "1" && sectionsList != null) {
            val ratingSection = sectionsList?.list?.filter {
                it.code == "highest_rating_resturant"
            }
            !ratingSection.isNullOrEmpty()
        } else
            false
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mHomeScreenBinding = viewDataBinding

        viewDataBinding.color = colorConfig
        viewDataBinding.drawables = Configurations.drawables
        viewDataBinding.strings = textConfig
        tvArea?.compoundDrawablesRelative?.getOrNull(0)
            ?.setTint(Color.parseColor(colorConfig.toolbarText))
        location_txt?.text = textConfig?.location_text

        HomeItemAdapter.SORT_POPUP = getString(R.string.sort_by_distance)

        Glide.with(this).asGif().load(R.raw.loading_gif).into(mHomeScreenBinding!!.imageLoader);

        if (screenFlowBean?.is_single_vendor == VendorAppType.Single.appType && screenFlowBean?.app_type == AppDataType.Food.type) {
            lyt_search.visibility = View.VISIBLE
            btn_menu.visibility = View.VISIBLE
            space.visibility = View.VISIBLE

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                rv_homeItem?.setOnScrollChangeListener { v, scrollX, scrollY, oldScrollX, oldScrollY ->
                    btn_menu?.visibility = if (scrollY < oldScrollY) {
                        View.INVISIBLE
                    } else {
                        View.VISIBLE
                    }
                }
            }

        } else {
            lyt_search.visibility = View.GONE
            btn_menu.visibility = View.GONE
            space.visibility = View.GONE
        }

        updateWallet()

        StaticFunction.removeAllCartLaundry(requireActivity())
        StaticFunction.clearCartLaundry(activity)
        if (prefHelper.getCurrentUserLoggedIn()) {
            try {
                val allCategories =
                    prefHelper.getGsonValue(DataNames.ORDERS_COUNT, PojoPendingOrders::class.java)
                if (allCategories != null && allCategories.data != null) {
                    if (allCategories.data.pendingOrder == 1 && prefHelper.getKeyValue(
                            DataNames.IS_DIALOG,
                            PrefenceConstants.TYPE_INT
                        ) == 0
                    ) {
                        //sweetDialogueFailure(activity, getString(R.string.rate_order), getString(R.string.rate_order_title, textConfig.order), true)

                        //rate order screen

                        prefHelper.setkeyValue(DataNames.IS_DIALOG, 1)
                    }
                }
            } catch (e: NullPointerException) {
                e.printStackTrace()
            }
        }

        if (screenFlowBean?.app_type == AppDataType.Food.type && bookingFlowBean?.is_pickup_order == FoodAppType.Both.foodType &&
            screenFlowBean?.is_single_vendor == VendorAppType.Multiple.appType
        ) {
            toolbar_layout.visibility = View.GONE
        } else {
            toolbar_layout.visibility = View.VISIBLE
        }

        if (clientInform?.show_ecom_v2_theme == "1") {
            toolbar_layout?.elevation = 0f
        }

        settingLayout()

        if ((screenFlowBean?.app_type == AppDataType.Ecom.type || screenFlowBean?.app_type == AppDataType.HomeServ.type ||
                    clientInform?.yummyTheme == "1" || clientInform?.app_selected_theme == "3" || clientInform?.app_selected_theme == "1"
                    || clientInform?.dynamic_home_screen_sections == "1")
        ) {

            if ((clientInform?.dynamic_home_screen_sections == "1" && fetchPopularSuppliers) || clientInform?.dynamic_home_screen_sections != "1")
                fetchPopular()
        }

        if (catId == 0) {
            fetchCategories()
        } else {
            clearViewModelApi()
        }

        // showBottomCart()

        settingToolbar()

        tvCountry?.setOnClickListener {
            spinnerCountry?.performClick()
        }
        iv_search.setOnClickListener {
            if (clientInform?.show_ecom_v2_theme == "1") {
                navController(this@HomeFragment)
                    .navigate(R.id.action_homeFrag_to_other)
                return@setOnClickListener
            }

            if (clientInform?.is_unify_search == "1")
                navController(this@HomeFragment)
                    .navigate(R.id.action_homeFragment_to_unifySearchFragment)
            else {
                if (BuildConfig.CLIENT_CODE == "skipp_0631") {
                    navController(this@HomeFragment)
                        .navigate(R.id.action_homeFragment_to_unifySearchFragment)
                } else
                    navController(this@HomeFragment)
                        .navigate(R.id.action_homeFragment_to_searchFragment)
            }
        }

        btn_menu.setOnClickListener {
            displayPopupWindow(btn_menu)
        }
        ivFilter?.setOnClickListener {
            if (clientInform?.is_skip_theme == "1")
                SortByFilterDialog.newInstance().show(childFragmentManager, "dialog")
            else
                clSearch?.callOnClick()
        }
        checkSingleVendor()

        Utils.loadAppPlaceholder(clientInform?.home ?: "")?.let {
            if (it.app?.isNotEmpty() == true) {
                iv_no_store.loadPlaceHolder(it.app)
            }
        }

        spinnerCountry?.onItemSelectedListener = null
        groupCountry?.visibility =
            if (clientInform?.is_multicurrency_enable == "1") View.VISIBLE else View.GONE
        setSpinnerAdapter()
    }

    private fun setSpinnerAdapter() {
        currenyList?.clear()

        val list =
            dataManager.getKeyValue(PrefenceConstants.CURRENCY_LIST, PrefenceConstants.TYPE_STRING)
                .toString()


        tvCountry?.text = selectedCurrency?.country_name
        if (clientInform?.is_multicurrency_enable == "1" && list.isNotEmpty()) {
            val groupListType = object : TypeToken<ArrayList<Currency?>?>() {}.type
            currenyList?.addAll(Gson().fromJson<ArrayList<Currency>>(list, groupListType))
            val dataAdapter: ArrayAdapter<Currency> = ArrayAdapter<Currency>(
                requireContext(), android.R.layout.simple_spinner_item, currenyList
                    ?: mutableListOf()
            )
            dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinnerCountry?.adapter = dataAdapter
            spinnerCountry?.onItemSelectedListener = this
            val selectedCurrencyIndex =
                currenyList?.indexOfFirst { it.currency_symbol == selectedCurrency?.currency_symbol }
            if (selectedCurrencyIndex != null && selectedCurrencyIndex != -1) {
                spinnerCountry?.setSelection(selectedCurrencyIndex)
            }
        }
    }

    override fun onNothingSelected(p0: AdapterView<*>?) {
        //do nothing
    }

    override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
        tvCountry?.text = spinnerCountry?.selectedItem.toString()

        if (p2 != -1 && AppConstants.CURRENCY_SYMBOL != currenyList?.get(p2)?.currency_symbol ?: "") {
            dataManager.addGsonValue(
                PrefenceConstants.CURRENCY_INF,
                Gson().toJson(currenyList?.get(p2))
            )
            AppConstants.CURRENCY_SYMBOL = currenyList?.get(p2)?.currency_symbol ?: ""
            selectedCurrency = currenyList?.get(p2)
            homeItemAdapter?.changeCurrency(selectedCurrency)
            homeItemAdapter?.notifyDataSetChanged()
        }
    }

    private fun updateWallet() {
        tvWalletTag?.text = textConfig?.wallet
        cvWallet?.visibility =
            if (prefHelper.getCurrentUserLoggedIn() && clientInform?.wallet_module == "1" && clientInform?.show_wallet_on_home == "1") View.VISIBLE else View.GONE

        cvWallet?.setOnClickListener {
            cnst_wallet.callOnClick()
        }

        cnst_wallet.setOnClickListener {
            if (prefHelper.getCurrentUserLoggedIn()) {
                navController(this@HomeFragment).navigate(R.id.action_resturantHomeFrag_to_walletFragment)
            } else {
                appUtils.checkLoginFlow(requireActivity(), WALLET_REQUEST)
            }
        }

        if (clientInform?.app_selected_theme == "3" || clientInform?.is_skip_theme == "1") {
            iv_search?.visibility = View.GONE
            clSearch?.visibility = View.VISIBLE
            iv_notification?.visibility = View.GONE
            if (clientInform?.is_skip_theme == "1") {
                ivCart?.visibility = View.VISIBLE
                viewSearch?.visibility = View.VISIBLE
                etText?.isEnabled = false
                etText?.hint = getString(R.string.search_your_fav_res)
                etText?.setHintTextColor(Color.parseColor(colorConfig.primaryColor))
                clSearch?.background = ContextCompat.getDrawable(
                    requireContext(),
                    R.drawable.drawable_color_primary_light
                )

                ivFilter?.rotation = 90f
                ivFilter?.setImageResource(R.drawable.ic_filter_white)
                ivFilter?.background =
                    ContextCompat.getDrawable(requireContext(), R.drawable.background_themebottom)
            } else {
                etText?.isEnabled = true

                etText.setOnEditorActionListener(object : TextView.OnEditorActionListener {
                    override fun onEditorAction(
                        v: TextView?,
                        actionId: Int,
                        event: KeyEvent?
                    ): Boolean {
                        if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                            if (etText.text.toString().trim().isNotEmpty())
                                clSearch?.callOnClick()
                            return true
                        }
                        return false
                    }
                })
            }
        } else clSearch?.visibility = View.GONE



        clSearch?.setOnClickListener {
            val bundle = bundleOf("searchText" to etText.text.toString().trim())
            if (clientInform?.is_unify_search == "1")
                navController(this@HomeFragment).navigate(R.id.action_resturantHomeFrag_to_unify_SearchFragment)
            else
                navController(this@HomeFragment).navigate(
                    R.id.action_resturantHomeFrag_to_searchFragment,
                    bundle
                )
        }
        clSearch?.setOnClickListener {

                navController(this@HomeFragment).navigate(
                    R.id.action_resturantHomeFrag_to_recordinFragment
                )
        }


    }

    private fun fetchPopular() {
        if (viewModel.popularLiveData.value != null) {
            popularOffer(viewModel.popularLiveData.value)
        } else {
            viewModel.getPopularProduct(catId, clientInform)
        }
    }

    private fun fetchRecentOrdersApi() {
        if (isNetworkConnected) {
            if (prefHelper.getCurrentUserLoggedIn() && clientInform?.dynamic_home_screen_sections == "1") {
                viewModel.getOrderHistory()
            }
        }
    }

    private fun lastOrdersObserver() {
        val catObserver = Observer<PagingResult<MutableList<OrderHistory>>> { resource ->
            val pos = mSupplierList?.indexOfFirst { it.viewType == HomeItemAdapter.RECENT_ORDERS }
            if (pos != null && pos != -1) {
                mSupplierList?.get(pos)?.itemModel?.recentOrdersList = resource?.result
                homeItemAdapter?.notifyItemChanged(pos)
            }
        }

        // Observe the LiveData, passing in this activity as the LifecycleOwner and the observer.
        viewModel.historyLiveData.observe(this, catObserver)
    }

    override fun onFilterClicked(ivFilter: ImageView) {
        openPrepTimeDialog()
    }

    override fun isLargeClicked(isLarge: Boolean) {

        if (viewModel.supplierLiveData.value?.result.isNullOrEmpty()) return

        rv_homeItem.adapter = null
        homeItemAdapter = null
        val mLocUser = prefHelper.getGsonValue(PrefenceConstants.ADRS_DATA, AddressBean::class.java)

        if (isLarge) {

            mSupplierList?.clear()
            setViewType(HomeItemAdapter.BANNER_TYPE, 0)
            setViewType(HomeItemAdapter.CATEGORY_TYPE, 0)

            setViewType(HomeItemAdapter.FILTER_TYPE, viewModel.supplierListCount.get())
            setViewType(HomeItemAdapter.SUPL_TYPE, 0)
            homeItemAdapter = HomeItemAdapter(
                mSupplierList,
                FoodAppType.Both.foodType,
                appUtils,
                clientInform,
                mLocUser,
                screenFlowBean,
                selectedCurrency,
                prefHelper.getLangCode()
            )
            homeItemAdapter?.largeView = true
            homeItemAdapter?.settingCallback(this)

        } else {
            mSupplierList?.clear()
            setViewType(HomeItemAdapter.BANNER_TYPE, 0)
            setViewType(HomeItemAdapter.CATEGORY_TYPE, 0)

            setViewType(HomeItemAdapter.FILTER_TYPE, viewModel.supplierListCount.get())
            setViewType(HomeItemAdapter.SUPL_TYPE, 0)
            homeItemAdapter = HomeItemAdapter(
                mSupplierList,
                FoodAppType.Both.foodType,
                appUtils,
                clientInform,
                mLocUser,
                screenFlowBean,
                selectedCurrency,
                prefHelper.getLangCode()
            )
            homeItemAdapter?.largeView = false
            homeItemAdapter?.settingCallback(this)

        }
//        homeItemAdapter?.notifyDataSetChanged()
        rv_homeItem.adapter = homeItemAdapter
    }

    override fun onSortByClicked(tvSortBy: TextView) {
        showPopup(tvSortBy)
    }


    private fun openPrepTimeDialog() {
        appUtils.showPrepTimeDialog(requireContext(), this)
    }

    private fun showPopup(view: TextView) {
        val popup = PopupMenu(requireContext(), view)
        popup.inflate(R.menu.popup_sort_by)
        popup.setOnMenuItemClickListener { item: MenuItem? ->
            when (item?.itemId) {
                R.id.sort_by_new -> {
                    mSupplierList?.clear()
                    fetchSupplierList(null, true)
                }
                R.id.sort_by_rating -> {
                    mSupplierList?.clear()
                    fetchSupplierList(SortBy.SortByRating.sortBy.toString(), true)
                }
                R.id.sort_by_distance -> {
                    mSupplierList?.clear()
                    fetchSupplierList(SortBy.SortByDistance.sortBy.toString(), true)
                }
                R.id.sort_by_atz -> {
                    mSupplierList?.clear()
                    fetchSupplierList(SortBy.SortByATZ.sortBy.toString(), true)
                }
                /*  R.id.sort_by_zta -> {
                      mSupplierList?.clear()
                      fetchSupplierList(SortBy.SortByZTA.sortBy.toString())
                  }*/
                R.id.sort_by_open -> {
                    mSupplierList?.clear()
                    fetchSupplierList(SortBy.SortByOpen.sortBy.toString(), true)
                }
                R.id.sort_by_close -> {
                    mSupplierList?.clear()
                    fetchSupplierList(SortBy.SortByClose.sortBy.toString(), true)
                }
            }

            HomeItemAdapter.SORT_POPUP = item?.title.toString()

            true
        }

        popup.show()
    }


    override fun onItemSelected(type: Int) {
        mSupplierList?.clear()
        fetchSupplierList(type.toString(), true)
    }

    override fun onPrepTimeSelected(filters: FiltersSupplierList) {
        mSupplierList?.clear()
        fetchSupplierList(
            StaticFunction.getSortByValue(HomeItemAdapter.SORT_POPUP),
            true, filters = filters
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()

        viewModel.compositeDisposable.clear()

    }

    private fun checkSingleVendor() {

        if (screenFlowBean?.app_type == AppDataType.Food.type && screenFlowBean?.is_single_vendor == VendorAppType.Single.appType) {
            group_deliver_option.visibility = View.VISIBLE

            if (DELIVERY_OPTIONS == DeliveryType.PickupOrder.type) {
                group_deliver_option.check(R.id.rb_pickup)
                changePickAdrs()

            } else {
                group_deliver_option.check(R.id.rb_delivery)
                location_txt.text = getString(R.string.location)
                settingToolbar()
            }

            if (prefHelper.getKeyValue(PrefenceConstants.SELF_PICKUP, PrefenceConstants.TYPE_STRING)
                    .toString() == FoodAppType.Both.foodType.toString()
            ) {
                group_deliver_option.visibility = View.VISIBLE
            } else {
                group_deliver_option.visibility = View.GONE
            }

            iv_search.visibility = View.GONE

            rb_pickup.setOnCheckedChangeListener { _, b ->
                if (b) {
                    rb_delivery.isChecked = false
                    changePickAdrs()
                    DELIVERY_OPTIONS = DeliveryType.PickupOrder.type
                    StaticFunction.clearCart(activity)
                    checkSavedProd()
                    showBottomCart()
                }
            }

            rb_delivery.setOnCheckedChangeListener { _, b ->
                if (b) {
                    rb_pickup.isChecked = false
                    rb_delivery.isChecked = true
                    DELIVERY_OPTIONS = DeliveryType.DeliveryOrder.type
                    if (clientInform?.show_ecom_v2_theme == "1") {
                        AddressDialogFragmentV2.newInstance(0).show(childFragmentManager, "dialog")
                    } else {
                        AddressDialogFragment.newInstance(0, 0).show(childFragmentManager, "dialog")
                    }
                }
            }

            btn_show_branches.setOnClickListener {

                viewModel.productLiveData.value?.result?.data?.supplier_detail?.let {
                    val supplierDetail = SupplierDataBean()
                    supplierDetail.name = it.name
                    supplierDetail.id = it.id ?: 0
                    supplierDetail.is_multi_branch = if (it.branchCount ?: 0 > 1) 1 else 0
                    onSupplierDetail(supplierDetail)
                }


            }

        } else {
            iv_search.visibility = View.VISIBLE
            group_deliver_option.visibility = View.GONE
            btn_show_branches.visibility = View.GONE
        }


        //if (screenFlowBean?.is_single_vendor == VendorAppType.Single.appType) {

        if (AppConstants.APP_SUB_TYPE > AppDataType.Custom.type) {
            iv_supplier_logo.setImageResource(R.drawable.ic_back_home)

            iv_supplier_logo.setOnClickListener {
                if (clientInform?.clikat_theme == "1")
                    navController(this@HomeFragment).navigate(R.id.action_clikathomeFragment_to_mainFragment)
                else
                    navController(this@HomeFragment).navigate(R.id.action_homeFragment_to_mainFragment)
            }
        } else {
            if (clientInform?.app_selected_template == null || clientInform?.app_selected_template == "0") {
                iv_supplier_logo.visibility = View.VISIBLE

                iv_supplier_logo.setImageDrawable(
                    ContextCompat.getDrawable(
                        activity
                            ?: requireContext(), R.mipmap.ic_launcher_new
                    )
                )

            } else {
                iv_supplier_logo.visibility = View.GONE
            }
        }

    }

    private fun changePickAdrs() {
        location_txt.text = getString(R.string.pickup_from)
        val locUser =
            prefHelper.getGsonValue(PrefenceConstants.RESTAURANT_INF, LocationUser::class.java)
        updateToolbar(locUser?.address ?: "")
    }

    private fun settingToolbar() {
        val mLocUser = prefHelper.getGsonValue(PrefenceConstants.ADRS_DATA, AddressBean::class.java)


        updateToolbar(appUtils.getAddressFormat(mLocUser) ?: "")
    }


    private fun updateToolbar(mLocation: String1) {
        tvArea.text = mLocation
    }


    private fun showBottomCart() {

        val appCartModel = appUtils.getCartData(clientInform, selectedCurrency)


        if (appCartModel.cartAvail) {

            bottom_cart.visibility = View.VISIBLE

            tv_total_price.text = getString(R.string.total).plus(" ").plus(
                getString(
                    R.string.currency_tag,
                    AppConstants.CURRENCY_SYMBOL,
                    appCartModel.totalPrice
                )
            )

            tv_total_product.text = getString(
                R.string.total_item_tag,
                getDecimalPointValue(clientInform, appCartModel.totalCount)
            )

            if (appCartModel.supplierName.isNotEmpty() && screenFlowBean?.is_single_vendor == VendorAppType.Multiple.appType) {
                tv_supplier_name.text = getString(R.string.supplier_tag, appCartModel.supplierName)
                tv_supplier_name.visibility = View.VISIBLE
            }


            bottom_cart.setOnClickListener {
                val navOptions: NavOptions = if (screenFlowBean?.app_type == AppDataType.Food.type
                    && screenFlowBean?.is_single_vendor == VendorAppType.Multiple.appType
                ) {
                    NavOptions.Builder()
                        //.setPopUpTo(R.id.resturantHomeFrag, false)
                        .build()
                } else {
                    NavOptions.Builder()
                        //.setPopUpTo(R.id.homeFragment, false)
                        .build()
                }

                if (bookingFlowBean?.is_pickup_order == FoodAppType.Both.foodType &&
                    screenFlowBean?.app_type == AppDataType.Food.type
                    && screenFlowBean?.is_single_vendor == VendorAppType.Multiple.appType
                ) {
                    navController(this@HomeFragment).navigate(
                        R.id.action_resturantHomeFrag_to_cart,
                        null,
                        navOptions
                    )
                } else {
                    navController(this@HomeFragment).navigate(
                        R.id.action_homeFragment_to_cart,
                        null,
                        navOptions
                    )
                }
            }
        } else {
            bottom_cart.visibility = View.GONE
        }


    }

    private fun fetchCategories() {
        if (isNetworkConnected) {
            categoryCount = 0
            specialCount = 0

//            if (viewModel.homeDataLiveData.value != null) {
//                updateCategoryData(viewModel.homeDataLiveData.value)
//                setWalletData(viewModel.homeDataLiveData.value?.user_wallet_amount)
//            } else {
                CommonUtils.checkAppDBKey(
                    prefHelper.getKeyValue(
                        PrefenceConstants.DB_SECRET,
                        PrefenceConstants.TYPE_STRING
                    )!!.toString(), interceptor
                )
                viewModel.getCategories(catId)
//            }
        }
    }


    private fun fetchOffers() {
        if (isNetworkConnected) {
            val filterBy = if (firstHomeSection != null) StaticFunction.getDeliveryType(
                firstHomeSection?.code
                    ?: ""
            ) else FoodAppType.Delivery.foodType
            viewModel.getOfferList(catId, clientInform, filterBy)
        }
    }

    private fun fetchSupplierList(
        sort_by: kotlin.String?,
        isFirstPage: Boolean,
        otherSuppliers: kotlin.String? = "0",
        categoryId: String1? = null,
        filters: FiltersSupplierList? = null
    ) {
        self_Pickup =
            if (prefHelper.getKeyValue(AppConstants.ROAD_MAP_TYPE, PrefenceConstants.TYPE_STRING)!!
                    .equals("Delivery")
            ) {
                "8"
            } else {
                "9"
            }
        if (isNetworkConnected) {
            if (firstHomeSection != null && clientInform?.is_order_types_screen_dynamic == "1") {

                viewModel.getSupplierList(
                    self_Pickup,
                    sort_by,
                    clientInform,
                    isFirstPage,
                    otherSuppliers,
                    categoryId,
                    filters
                )
            } else {
                if (screenFlowBean?.app_type == AppDataType.Food.type && bookingFlowBean?.is_pickup_order == FoodAppType.Pickup.foodType
                    || clientInform?.is_skip_theme == "1"
                ) {
                    viewModel.getSupplierList(
                        self_Pickup,
                        sort_by,
                        clientInform,
                        isFirstPage,
                        otherSuppliers,
                        categoryId,
                        filters
                    )
                } else {
                    viewModel.getSupplierList(
                        self_Pickup,
                        sort_by,
                        clientInform,
                        isFirstPage,
                        otherSuppliers,
                        categoryId,
                        filters
                    )
                }
            }
        }
    }

    private fun fetchProductList(isFirstPage: Boolean) {
        if (isNetworkConnected) {
            if (prefHelper.getKeyValue(
                    PrefenceConstants.GENRIC_SUPPLIERID,
                    PrefenceConstants.TYPE_INT
                ) != 0
            )
                viewModel.getProductList(
                    prefHelper.getKeyValue(
                        PrefenceConstants.GENRIC_SUPPLIERID,
                        PrefenceConstants.TYPE_INT
                    ).toString(), isFirstPage
                )
        }
    }


    private fun categoryObserver() {
        // Create the observer which updates the UI.
        val catObserver = Observer<Data> { resource ->

            updateCategoryData(resource)
            setWalletData(resource?.user_wallet_amount)
        }
        // Observe the LiveData, passing in this activity as the LifecycleOwner and the observer.
        viewModel.homeDataLiveData.observe(this, catObserver)
    }

    private fun setWalletData(wallet_amount: kotlin.String?) {
        tvWalletAmt?.text = getString(
            R.string.currency_tag, AppConstants.CURRENCY_SYMBOL, wallet_amount
                ?: "0.0"
        )
    }

    private fun productObserver() {
        // Create the observer which updates the UI.
        val catObserver = Observer<PagingResult<SuplierProdListModel>> { resource ->

            if (resource?.result?.data?.supplier_detail?.branchCount ?: 0 > 1) {
                btn_show_branches.visibility = View.VISIBLE
            }

            setViewType(HomeItemAdapter.SINGLE_PROD_TYPE, 0)
            homeItemAdapter?.notifyDataSetChanged()
        }

        // Observe the LiveData, passing in this activity as the LifecycleOwner and the observer.
        viewModel.productLiveData.observe(this, catObserver)
    }

    private fun updateCategoryData(resource: Data?) {

        categoryModel = resource

        categoryCount = 0
        specialCount = 0

//        if (viewModel.offersLiveData.value != null) {
//            updateOfferData(viewModel.offersLiveData.value)
        updateOfferData()
//        } else {
//         //   fetchOffers()
//        }

    }


    private fun supplierObserver() {
        // Create the observer which updates the UI.
        val supplierObserver = Observer<PagingResult<List<SupplierDataBean>>> { resource ->
            rv_homeItem.visibility = View.VISIBLE
            resource?.result?.map { supplierList ->
                val type = object : TypeToken<List<TimeDataBean>>() {}.type
                val listOfMap: List<TimeDataBean> = Gson().fromJson(supplierList.timings, type)
                supplierList.isOpen = appUtils.checkResturntTiming(listOfMap)
            }

            val restaurantList = resource?.result

            if (resource?.isFirstPage == true)
                updateSupplierData(restaurantList)
            else {

                /* if(mSupplierList?.isNotEmpty() == true)
                 {
                     mSupplierList?.sortBy { !it.isOpen }
                 }*/

                if (restaurantList != null && !restaurantList.isNullOrEmpty()) {
                    val posStart = mSupplierList?.size
                    for (i in restaurantList.indices) {
                        restaurantList[i].viewType = HomeItemAdapter.SUPL_TYPE
                        mSupplierList?.add(restaurantList[i])
                    }
                    homeItemAdapter?.notifyItemRangeInserted(
                        posStart
                            ?: 0, restaurantList.size
                    )
                }
            }
        }

        // Observe the LiveData, passing in this activity as the LifecycleOwner and the observer.
        viewModel.supplierLiveData.observe(this, supplierObserver)

    }

    private fun categoryWiseObserver() {
        val supplierObserver = Observer<ArrayList<CategoryWiseSuppliers>> { resource ->
            val pos =
                mSupplierList?.indexOfFirst { it.viewType == HomeItemAdapter.CATEGORY_WISE_SUPPLIERS }
            if (pos != null && pos != -1) {
                mSupplierList?.get(pos)?.itemModel?.categoryWiseSuppliers = resource
                homeItemAdapter?.notifyItemChanged(pos)
            }

        }
        viewModel.categoryWiseSupplierLiveData.observe(this, supplierObserver)
    }

    private fun updateSupplierData(resource: List<SupplierDataBean>?) {

        if (resource?.isNotEmpty() == true) {


            if (sectionsList == null)
                sectionsList = dataManager.getGsonValue(
                    DataNames.HOME_DYNAMIC_SECTIONS,
                    SettingModel.DataBean.DynamicSectionsData::class.java
                )

            sectionsList?.list?.sort()

            iv_no_store.visibility = View.GONE
            if (clientInform?.yummyTheme == "1" || BuildConfig.CLIENT_CODE == "alaruxpress_0549") {
                setViewType(HomeItemAdapter.BANNER_TYPE, 0)
                setViewType(HomeItemAdapter.CATEGORY_TYPE, 0)
                setViewType(HomeItemAdapter.SPL_PROD_TYPE, 0)
                setViewType(HomeItemAdapter.RECOMEND_TYPE, 0)
                setViewType(HomeItemAdapter.FILTER_TYPE, viewModel.supplierListCount.get())
                setViewType(HomeItemAdapter.SUPL_TYPE, 0)
            } else if (clientInform?.is_carveQatar_home_theme == "1") {

                setViewType(HomeItemAdapter.BANNER_TYPE, 0)
                setViewType(HomeItemAdapter.CATEGORY_TYPE, 0)
                setViewType(HomeItemAdapter.FILTER_TYPE, viewModel.supplierListCount.get())
                setViewType(HomeItemAdapter.SUPL_TYPE, 0)
            } else if (clientInform?.dynamic_home_screen_sections == "1" && !sectionsList?.list.isNullOrEmpty()) {
                for (i in sectionsList?.list?.indices!!) {
                    val section = StaticFunction.getSectionsName(
                        sectionsList?.list?.get(i)?.code
                            ?: ""
                    )
                    setViewType(section, 0, sectionsList?.list?.get(i)?.section_name)
                }
            } else if (clientInform?.is_skip_theme == "1") {

                setViewType(HomeItemAdapter.BANNER_TYPE, 0)
                setViewType(HomeItemAdapter.CATEGORY_TYPE, 0)
                setViewType(HomeItemAdapter.NEAR_BY_SPPLIERS, 0)
                setViewType(HomeItemAdapter.RECOMEND_TYPE, 0)
            } else if (clientInform?.app_selected_theme == "3" || clientInform?.app_selected_theme == "1") {
                setViewType(HomeItemAdapter.BANNER_TYPE, 0)
                if (screenFlowBean?.app_type == AppDataType.Food.type && clientInform?.show_tags_for_suppliers == "1")
                    setViewType(HomeItemAdapter.TAGS_SUPPLIER_TYPE, 0)
                else
                    setViewType(HomeItemAdapter.CATEGORY_TYPE, 0)
                setViewType(HomeItemAdapter.HORIZONTAL_SUPPLIERS, 0)

                setViewType(HomeItemAdapter.POPULAR_TYPE, 0)
                setViewType(HomeItemAdapter.SPL_PROD_TYPE, 0)
                setViewType(HomeItemAdapter.FASTEST_DELIVERY_SUPPLIERS, 0)
                setViewType(HomeItemAdapter.RECOMMENDED_FOOD, 0)
                setViewType(HomeItemAdapter.CLIKAT_THEME_SUPPLIERS, 0)

            } else {
                if (resource.size >= 3) {
                    for (i in resource.indices) {
                        when (i) {
                            0 -> {
                                setViewType(HomeItemAdapter.BANNER_TYPE, 0)
                                // setViewType(HomeItemAdapter.SEARCH_TYPE, 0)
                                if (screenFlowBean?.app_type == AppDataType.Food.type && clientInform?.show_tags_for_suppliers == "1")
                                    setViewType(HomeItemAdapter.TAGS_SUPPLIER_TYPE, 0)
                                else {
                                    if (AppConstants.APP_SUB_TYPE < AppDataType.Custom.type && clientInform?.show_food_groc == "0") {
                                        setViewType(HomeItemAdapter.CATEGORY_TYPE, 0)
                                    }
                                }
                                setViewType(
                                    HomeItemAdapter.FILTER_TYPE,
                                    viewModel.supplierListCount.get()
                                )
                            }
                            1 -> {
                                setViewType(HomeItemAdapter.RECOMEND_TYPE, 0)
                                if (clientInform?.enable_best_sellers == "1")
                                    setViewType(HomeItemAdapter.BEST_SELLERS, 0)
                            }
                            2 -> setViewType(HomeItemAdapter.SPL_PROD_TYPE, 0)
                        }
                        resource[i].viewType = HomeItemAdapter.SUPL_TYPE
                        mSupplierList?.add(resource[i])
                    }

                } else {
                    setViewType(HomeItemAdapter.BANNER_TYPE, 0)
                    // setViewType(HomeItemAdapter.SEARCH_TYPE, 0)
                    if (screenFlowBean?.app_type == AppDataType.Food.type && clientInform?.show_tags_for_suppliers == "1")
                        setViewType(HomeItemAdapter.TAGS_SUPPLIER_TYPE, 0)
                    else {
                        if (AppConstants.APP_SUB_TYPE < AppDataType.Custom.type && clientInform?.show_food_groc == "0") {
                            setViewType(HomeItemAdapter.CATEGORY_TYPE, 0)
                        }
                    }
                    setViewType(HomeItemAdapter.FILTER_TYPE, viewModel.supplierListCount.get())

                    resource.mapIndexed { _, dataBean ->
                        dataBean.viewType = HomeItemAdapter.SUPL_TYPE
                    }
                    mSupplierList?.addAll(resource)
                    setViewType(HomeItemAdapter.RECOMEND_TYPE, 0)
                    if (clientInform?.enable_best_sellers == "1")
                        setViewType(HomeItemAdapter.BEST_SELLERS, 0)
                    setViewType(HomeItemAdapter.SPL_PROD_TYPE, 0)
                }
            }
        } else if (clientInform?.is_carveQatar_home_theme == "1") {

            setViewType(HomeItemAdapter.BANNER_TYPE, 0)
            setViewType(HomeItemAdapter.CATEGORY_TYPE, 0)
        } else {
            setViewType(HomeItemAdapter.BANNER_TYPE, 0)
            //   setViewType(HomeItemAdapter.SEARCH_TYPE, 0)
            if (screenFlowBean?.app_type == AppDataType.Food.type && clientInform?.show_tags_for_suppliers == "1")
                setViewType(HomeItemAdapter.TAGS_SUPPLIER_TYPE, 0)
            else {
                if (AppConstants.APP_SUB_TYPE < AppDataType.Custom.type && clientInform?.show_food_groc == "0") {
                    setViewType(HomeItemAdapter.CATEGORY_TYPE, 0)
                }
            }
            if (clientInform?.is_restaurant_sort == "1") {
                setViewType(HomeItemAdapter.FILTER_TYPE, viewModel.supplierListCount.get())
            }

            if (viewModel.offersLiveData.value?.supplierInArabic.isNullOrEmpty() && viewModel.supplierLiveData.value?.result?.isNullOrEmpty() == true
                && screenFlowBean?.is_single_vendor == VendorAppType.Multiple.appType
            ) {
                iv_no_store.visibility = View.VISIBLE
                // swiprRefresh.visibility = View.GONE
            } else {
                iv_no_store.visibility = View.GONE
                //swiprRefresh.visibility = View.VISIBLE
            }

        }

        homeItemAdapter?.notifyDataSetChanged()
    }


    private fun offersObserver() {
        // Create the observer which updates the UI.
        val offerObserver = Observer<OfferDataBean> { resource ->
            updateOfferData()
        }
        // Observe the LiveData, passing in this activity as the LifecycleOwner and the observer.
        viewModel.offersLiveData.observe(this, offerObserver)

    }


    private fun popularObserver() {

        // Create the observer which updates the UI.
        val offerObserver = Observer<List<ProductDataBean>> { resource ->
            popularOffer(resource)
        }

        // Observe the LiveData, passing in this activity as the LifecycleOwner and the observer.
        viewModel.popularLiveData.observe(this, offerObserver)
    }

    private fun branchSupplierObserver() {
        // Create the observer which updates the UI.
        val catObserver = Observer<MutableList<SupplierList>> { resource ->

            hideLoading()
            updateBranchList(resource)
        }

        // Observe the LiveData, passing in this activity as the LifecycleOwner and the observer.
        viewModel.branchListData.observe(this, catObserver)
    }

    private fun updateBranchList(resource: MutableList<SupplierList>?) {

        if (resource?.isNullOrEmpty() == true) return

        if (resource.count() == 1) {
            supplierBundle?.putBoolean("isFromBranch", true)
            supplierBundle?.putInt("branchId", resource.firstOrNull()?.supplierBranchId ?: 0)
            supplierBundle?.putString("supplierBranchName", resource.firstOrNull()?.name ?: "")
            supplierBundle?.putString("supplierLogo", resource.firstOrNull()?.logo ?: "")
            supplierBundle?.putString("supplierBanner", resource.firstOrNull()?.supplierImage ?: "")
            chooseResturant(supplierBundle)
        } else {
            navController(this@HomeFragment).navigate(R.id.action_supplierAll, supplierBundle)
        }
    }

    private fun popularOffer(resource: List<ProductDataBean>?) {
        mPopularList?.clear()

        mPopularList?.addAll(resource ?: emptyList())
        mPopularList?.map {

            it.prod_quantity = StaticFunction.getCartQuantity(
                activity
                    ?: requireContext(), it.product_id
            )
            it.netPrice = it.fixed_price?.toFloatOrNull() ?: 0f

            it.let {
                prodUtils.changeProductList(false, it)
            }
        }
    }


    private fun updateOfferData() {

//        offerListModel = resource

        mSupplierList?.clear()
//        if (resource?.offerEnglish?.isNotEmpty() == true) {
//
//            specialOffers?.clear()
//
//            resource.offerEnglish?.let { specialOffers?.addAll(it) }


            // if (screenFlowBean?.app_type == AppDataType.Food.type) {
//
//            specialOffers?.map {
//
//                it.prod_quantity = StaticFunction.getCartQuantity(
//                    activity
//                        ?: requireContext(), it.product_id
//                )
//                it.netPrice = it.fixed_price?.toFloatOrNull() ?: 0f
//
//                it.let {
//                    prodUtils.changeProductList(false, it)
//                }
//            }
            //}
//        }

//        if (!resource?.offersSuppliers.isNullOrEmpty()) {
//            resource?.offersSuppliers?.map { it.isSupplier = true }
//        }
        if (screenFlowBean?.app_type == AppDataType.Food.type) {

            if (screenFlowBean?.is_single_vendor == VendorAppType.Multiple.appType) {
                if (viewModel.supplierLiveData.value != null && viewModel.supplierLiveData.value?.isFirstPage == true) {
                    updateSupplierData(viewModel.supplierLiveData.value?.result)
                } else {
                    if (fetchCategoryWiseSuppliers) {
                        fetchCategoryWiseSuppliersApi()
                    }
                    if (fetchRecentOrders) {
                        fetchRecentOrdersApi()
                    }
                    if (fetchHighestRatingSuppliers)
                        fetchSupplierList(SortBy.SortByRating.sortBy.toString(), true, "1")
                    else
                        fetchSupplierList(SortBy.SortByDistance.sortBy.toString(), true)

                    if (fetchNewRestaurantSuppliers) {
                        fetchSupplierList(SortBy.SortByNew.sortBy.toString(), true, "2")
                    }


                }
            } else {

                onRecyclerViewScrolled()

                setViewType(HomeItemAdapter.BANNER_TYPE, 0)
                // setViewType(HomeItemAdapter.SEARCH_TYPE, 0)
                setViewType(HomeItemAdapter.SPL_PROD_TYPE, 0)

                if (viewModel.productLiveData.value != null) {
                    setViewType(HomeItemAdapter.SINGLE_PROD_TYPE, 0)
                    homeItemAdapter?.notifyDataSetChanged()
                } else {
                    fetchProductList(true)
                }
            }


        } else if (screenFlowBean?.app_type == AppDataType.HomeServ.type) {

            setViewType(HomeItemAdapter.BANNER_TYPE, 0)
            setViewType(HomeItemAdapter.FILTER_TYPE, 0)
            if (AppConstants.APP_SUB_TYPE < AppDataType.Custom.type && clientInform?.show_food_groc == "0") {
                setViewType(HomeItemAdapter.CATEGORY_TYPE, 0)
            }
            setViewType(HomeItemAdapter.SPL_PROD_TYPE, 0)

            if (screenFlowBean?.is_single_vendor == VendorAppType.Multiple.appType) {
                setViewType(HomeItemAdapter.RECOMEND_TYPE, 0)
            }

            setViewType(HomeItemAdapter.POPULAR_TYPE, 0)
            setViewType(HomeItemAdapter.RECENT_TYPE, 0)

        } else {
            setViewType(HomeItemAdapter.BANNER_TYPE, 0)

            if (clientInform?.show_ecom_v2_theme == "1") {
                setViewType(HomeItemAdapter.SEARCH_TYPE, 0)
            }


            if (clientInform?.show_ecom_v2_theme == "1" || clientInform?.is_lubanah_theme == "1") {
                setViewType(HomeItemAdapter.CATEGORY_TYPE, 0)
            }
            setViewType(HomeItemAdapter.SPL_PROD_TYPE, 0)
//            setViewType(HomeItemAdapter.BRAND_TYPE, 0)
            setViewType(HomeItemAdapter.RECOMEND_TYPE, 0)
            setViewType(HomeItemAdapter.POPULAR_TYPE, 0)
            setViewType(HomeItemAdapter.RECENT_TYPE, 0)

            homeItemAdapter?.notifyDataSetChanged()
        }
    }

    private fun fetchCategoryWiseSuppliersApi() {
        val categoriesList = ArrayList<Int>()
        categoryModel?.english?.forEach { categoriesList.add(it.id ?: 0) }
        if (isNetworkConnected && !categoriesList.isNullOrEmpty()) {
            viewModel.getCategoryViseSupplier(categoriesList)
        }
    }

    private fun onRecyclerViewScrolled() {

        rv_homeItem?.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                if (clientInform?.enable_rest_pagination_category_wise == "1") {
                    if (mSupplierList?.any { it.viewType == HomeItemAdapter.SUPL_TYPE } == true) {
                        val isSuppliersPagingActive = viewModel.validForSuppliersPaging()
                        if (!recyclerView.canScrollVertically(1) && isSuppliersPagingActive) {
                            fetchSupplierList(null, false)
                        }
                    }
                } else {
                    val isPagingActive = viewModel.validForPaging()
                    if (!recyclerView.canScrollVertically(1) && isPagingActive) {
                        fetchProductList(false)
                    }
                }
            }
        })
    }


    override fun onResume() {
        super.onResume()

        checkSavedProd()

        showBottomCart()

//        if(!EventBus.getDefault().isRegistered(this))
//            EventBus.getDefault().register(this)
    }

    private fun checkSavedProd() {
        if (specialOffers != null && specialOffers?.isNotEmpty() == true) {

            specialOffers?.map {
                it.prod_quantity = StaticFunction.getCartQuantity(
                    activity
                        ?: requireContext(), it.product_id
                )
                if (it.price_type != 1) {
                    it.netPrice = it.fixed_price?.toFloatOrNull() ?: 0f
                }
            }
            homeItemAdapter?.notifyDataSetChanged()
        }

        //Update Product Quantity for Single Vendor List
        if (viewModel.productLiveData.value != null && viewModel.productLiveData.value?.result?.data?.product?.isNotEmpty() == true) {
            mSupplierList?.map { supplierData ->
                if (supplierData.viewType == HomeItemAdapter.SINGLE_PROD_TYPE) {
                    supplierData.itemModel?.vendorProdList?.value?.map {
                        it.prod_quantity = StaticFunction.getCartQuantity(
                            activity
                                ?: requireContext(), it.product_id
                        )
                    }
                }
            }
            homeItemAdapter?.notifyDataSetChanged()
        }
    }


    private fun setViewType(
        viewType: String1,
        supplierCount: Int,
        dynamicSectionName: String1? = null
    ) {
        val dataBean = SupplierDataBean()

        val itemModel = HomeItemModel()
        itemModel.screenType = screenFlowBean?.app_type ?: -1
        itemModel.isSingleVendor = screenFlowBean?.is_single_vendor ?: -1

        dataBean.viewType = viewType
        dataBean.dynamicSectionName = dynamicSectionName

        if (viewType == HomeItemAdapter.BANNER_TYPE) {
            val bannerList = ArrayList<TopBanner>()

            if (categoryModel?.topBanner?.isNotEmpty() == true) {
                bannerList.addAll(categoryModel?.topBanner ?: emptyList())
            } else if (!clientInform?.banner_url.isNullOrEmpty()) {
                if (BuildConfig.CLIENT_CODE == "lastminute_0382") return

                val bannerBean = TopBanner()
                bannerBean.isEnabled = true
                bannerBean.phone_image = clientInform?.banner_url ?: ""
                bannerList.add(bannerBean)
            } else if (!clientInform?.banner_one.isNullOrEmpty() || !clientInform?.banner_two.isNullOrEmpty() || !clientInform?.banner_three.isNullOrEmpty() || !clientInform?.banner_four.isNullOrEmpty()) {
                if (BuildConfig.CLIENT_CODE == "lastminute_0382") return

                val bannerBean = TopBanner()
                bannerBean.isEnabled = false
                bannerBean.phone_image = clientInform?.banner_one ?: ""
                bannerList.add(bannerBean)
                val bannerBean2 = TopBanner()
                bannerBean2.isEnabled = false
                bannerBean2.phone_image = clientInform?.banner_two ?: ""
                bannerList.add(bannerBean2)
                val bannerBean3 = TopBanner()
                bannerBean3.isEnabled = false
                bannerBean3.phone_image = clientInform?.banner_three ?: ""
                bannerList.add(bannerBean3)
                val bannerBean4 = TopBanner()
                bannerBean4.isEnabled = false
                bannerBean4.phone_image = clientInform?.banner_four ?: ""
                bannerList.add(bannerBean4)
            }
            itemModel.bannerWidth = clientInform?.app_banner_width?.toInt() ?: 0

            //  if (bannerList.size() > 0) {
            itemModel.bannerList =
                if (AppConstants.APP_SUB_TYPE < AppDataType.Custom.type) bannerList else bannerList.filter { it.category_id == catId }

            dataBean.itemModel = itemModel
            mSupplierList?.add(dataBean)
            //}
        }
        else if (viewType == HomeItemAdapter.CATEGORY_TYPE) {
            val mainCat = categoryModel?.english

            if (mainCat?.isNotEmpty() == true) {

                val beanList = ArrayList<English>()

                beanList.addAll(mainCat)
                beanList.add(0,English(name = getString(R.string.all)))

                val listAdapter = CategoryListAdapter(beanList, screenFlowBean?.app_type?:0, clientInform)
                rv_category_list.adapter=listAdapter
                rv_category_list.layoutManager=LinearLayoutManager(activity, RecyclerView.HORIZONTAL, false)
rv_category_list.visibility=View.GONE
                listAdapter.settingCallback(this)

                // if (beanList.size() > 0) {
                itemModel.categoryList = beanList
                //}

                dataBean.itemModel = itemModel
                mSupplierList?.add(dataBean)
            }
        }
        else if (viewType == HomeItemAdapter.TAGS_SUPPLIER_TYPE) {
            if (!categoryModel?.supplier_tags.isNullOrEmpty()) {
                itemModel.categoryList = categoryModel?.supplier_tags

                dataBean.itemModel = itemModel
                mSupplierList?.add(dataBean)
            }
        }
        else if (viewType == HomeItemAdapter.FILTER_TYPE) {

            if (supplierCount > 0 || screenFlowBean?.app_type == AppDataType.Ecom.type || clientInform?.is_restaurant_sort == "1") {
                itemModel.supplierCount = supplierCount

                dataBean.itemModel = itemModel
                mSupplierList?.add(dataBean)
            }
        }
        else if (viewType == HomeItemAdapter.RECOMEND_TYPE) {

            if (!offerListModel?.supplierInArabic.isNullOrEmpty()) {
                itemModel.sponserList = offerListModel?.supplierInArabic

                dataBean.itemModel = itemModel
                mSupplierList?.add(dataBean)
            }
        } else if (viewType == HomeItemAdapter.BEST_SELLERS || viewType == HomeItemAdapter.HORIZONTAL_SUPPLIERS) {

            if (!offerListModel?.bestSellersSuppliers.isNullOrEmpty()) {
                itemModel.sponserList = offerListModel?.bestSellersSuppliers

                dataBean.itemModel = itemModel
                mSupplierList?.add(dataBean)
            }
        } else if (viewType == HomeItemAdapter.FASTEST_DELIVERY_SUPPLIERS) {

            if (!offerListModel?.fastestDeliverySuppliers.isNullOrEmpty()) {
                itemModel.sponserList = offerListModel?.fastestDeliverySuppliers

                dataBean.itemModel = itemModel
                mSupplierList?.add(dataBean)
            }
        } else if (viewType == HomeItemAdapter.POPULAR_TYPE) {
            if (mPopularList?.isNotEmpty() == true) {
                itemModel.popularProdList = mPopularList
                itemModel.mSpecialType = if (clientInform?.laundary_service_flow == "1") 0 else 1
                dataBean.itemModel = itemModel
                mSupplierList?.add(dataBean)
            }
        } else if (viewType == HomeItemAdapter.RECOMMENDED_FOOD) {
            if (offerListModel?.recentViewHistory?.isNotEmpty() == true) {
                itemModel.recentViewHistory = offerListModel?.recentViewHistory
                dataBean.itemModel = itemModel
                mSupplierList?.add(dataBean)
            }
        } else if (viewType == HomeItemAdapter.SINGLE_PROD_TYPE) {
            if (viewModel.productLiveData.value != null && viewModel.productLiveData.value?.result?.data?.product?.isNotEmpty() == true) {
                viewModel.productLiveData.value?.result?.data?.product?.forEachIndexed { index, productBean ->

                    productBean.value?.map {
                        it.netPrice = it.fixed_price?.toFloatOrNull() ?: 0f
                        it.prod_quantity = StaticFunction.getCartQuantity(
                            activity
                                ?: requireContext(), it.product_id
                        )
                    }

                    val model = HomeItemModel()
                    model.vendorProdList = productBean
                    model.screenType = screenFlowBean?.app_type ?: -1
                    dataBean.itemModel = model
                    mSupplierList?.add(dataBean.copy())
                }

            }
        } else if (viewType == HomeItemAdapter.SPL_PROD_TYPE) {

            if (specialOffers?.isEmpty() == true) return

            val beanList = ArrayList<ProductDataBean>()

            if (!offerListModel?.offersSuppliers.isNullOrEmpty() && clientInform?.dynamic_home_screen_sections == "1"
                && clientInform?.enable_supplier_in_special_offer == "1"
            ) {
                beanList.addAll(offerListModel?.offersSuppliers ?: emptyList())
            } else {
                beanList.addAll(specialOffers ?: emptyList())
            }

            val updatedList = beanList.map {
                prodUtils.changeProductList(false, it)
            }

            itemModel.mSpecialType = 1
            itemModel.specialOffers = updatedList.toMutableList()
            //}

            if (beanList.isNotEmpty()) {
                dataBean.itemModel = itemModel
                mSupplierList?.add(dataBean)
            }
        } else if (viewType == HomeItemAdapter.BRAND_TYPE) {

            if (!categoryModel?.brands.isNullOrEmpty()) {
                itemModel.brandsList = categoryModel?.brands

                dataBean.itemModel = itemModel
                mSupplierList?.add(dataBean)
            }
        } else if (viewType == HomeItemAdapter.RECENT_TYPE) {

            if (!categoryModel?.recentActivity.isNullOrEmpty()) {
                itemModel.recentProdList = categoryModel?.recentActivity
                itemModel.mSpecialType = 1
                dataBean.itemModel = itemModel
                mSupplierList?.add(dataBean)
            }
        } else if (viewType == HomeItemAdapter.CLIKAT_THEME_SUPPLIERS) {
            if (viewModel.supplierLiveData.value?.result.isNullOrEmpty()) return

            itemModel.suppliersList = viewModel.supplierLiveData.value?.result

            dataBean.itemModel = itemModel
            mSupplierList?.add(dataBean)

        } else if (viewType == HomeItemAdapter.HIGEST_RATING_SUPPLIERS) {
            if (viewModel.highestRatingSupplierLiveData.value?.result.isNullOrEmpty()) return

            itemModel.highestRatingSuppliersList =
                viewModel.highestRatingSupplierLiveData.value?.result

            dataBean.itemModel = itemModel
            mSupplierList?.add(dataBean)

        } else if (viewType == HomeItemAdapter.NEW_RESTUARENT_SUPPLIERS) {
            if (viewModel.newRestaurantSupplierLiveData.value?.result.isNullOrEmpty()) return

            itemModel.newSuppliersList = viewModel.newRestaurantSupplierLiveData.value?.result

            dataBean.itemModel = itemModel
            mSupplierList?.add(dataBean)

        } else if (viewType == HomeItemAdapter.CATEGORY_WISE_SUPPLIERS) {
            itemModel.categoryWiseSuppliers = viewModel.categoryWiseSupplierLiveData.value
                ?: arrayListOf()
            dataBean.itemModel = itemModel
            mSupplierList?.add(dataBean)
        } else if (viewType == HomeItemAdapter.RECENT_ORDERS) {
            itemModel.recentOrdersList = viewModel.historyLiveData.value?.result
                ?: mutableListOf()
            dataBean.itemModel = itemModel
            mSupplierList?.add(dataBean)
        } else if (viewType == HomeItemAdapter.NEAR_BY_SPPLIERS) {

            if (viewModel.supplierLiveData.value?.result.isNullOrEmpty()) return
            val list = ArrayList<SupplierDataBean>()
            list.addAll(viewModel.supplierLiveData.value?.result ?: emptyList())
            itemModel.suppliersList = list
            val userAddress =
                prefHelper.getGsonValue(PrefenceConstants.ADRS_DATA, AddressBean::class.java)
            itemModel.userAddress = userAddress?.latitude + "," + userAddress?.longitude
            dataBean.itemModel = itemModel
            mSupplierList?.add(dataBean)

        } else if (viewType == HomeItemAdapter.SUPL_TYPE || clientInform?.yummyTheme == "1") {
            if (viewModel.supplierLiveData.value?.result.isNullOrEmpty()) return

            viewModel.supplierLiveData.value?.result?.mapIndexed { index, supplierDataBean ->
                supplierDataBean.viewType = HomeItemAdapter.SUPL_TYPE
                mSupplierList?.add(supplierDataBean)
            }
        } else {
            dataBean.itemModel = itemModel
            mSupplierList?.add(dataBean)
        }

    }


    private fun settingLayout() {
        specialOffers = ArrayList()
        mSupplierList = ArrayList()
        mPopularList = ArrayList()

        val mLocUser = prefHelper.getGsonValue(PrefenceConstants.ADRS_DATA, AddressBean::class.java)
        homeItemAdapter = HomeItemAdapter(
            mSupplierList,
            bookingFlowBean?.is_pickup_order
                ?: 1,
            appUtils,
            clientInform,
            mLocUser,
            screenFlowBean,
            selectedCurrency,
            prefHelper.getLangCode()
        )
        homeItemAdapter?.setFragCallback(this)
        homeItemAdapter?.settingCallback(this)

        if (clientInform?.dynamic_home_screen_sections == "1" && firstHomeSection != null)
            homeItemAdapter?.setDeliveryType(
                StaticFunction.getDeliveryType(
                    firstHomeSection?.code
                        ?: ""
                )
            )

        homelytManager = LinearLayoutManager(activity, RecyclerView.VERTICAL, false)

        rv_homeItem.layoutManager = homelytManager
        rv_homeItem.adapter = homeItemAdapter

        rv_homeItem.setHasFixedSize(true)

        rv_homeItem.itemAnimator = null

        swiprRefresh.setOnRefreshListener(this)
        tvArea.setOnClickListener(this)

        if (clientInform?.enable_rest_pagination_category_wise == "1")
            onRecyclerViewScrolled()

        ed_search?.afterTextChanged {
            if (it.isEmpty()) {
                homeItemAdapter?.filter?.filter("")
                homelytManager?.scrollToPositionWithOffset(0, mSupplierList?.size ?: 0)
            } else {
                homelytManager?.scrollToPositionWithOffset(2, mSupplierList?.size ?: 0)
                homeItemAdapter?.filter?.filter(it)
            }
        }

        iv_notification.setBadgeValue(
            prefHelper.getKeyValue(PrefenceConstants.BADGE_COUNT, PrefenceConstants.TYPE_STRING)
                .toString().toIntOrNull()
                ?: 0
        )
            .setBadgeOvalAfterFirst(true)
            .setBadgeTextSize(16f)
            .setMaxBadgeValue(999)
            .setBadgeBackground(
                ContextCompat.getDrawable(
                    activity
                        ?: requireContext(), R.drawable.rectangle_rounded
                )
            )
            .setBadgePosition(BadgePosition.TOP_RIGHT)
            .setBadgeTextStyle(Typeface.NORMAL)
            .setShowCounter(true)
            .setBadgePadding(4)

        iv_notification.setOnClickListener {
            if (!prefHelper.getCurrentUserLoggedIn()) {
                AppToasty.error(requireActivity(), getString(R.string.login_first))
                return@setOnClickListener
            }

            if (clientInform?.show_ecom_v2_theme == "1") {
                navController(this@HomeFragment).navigate(R.id.action_homeFragment_to_cartV2)
                return@setOnClickListener
            }

            navController(this@HomeFragment).navigate(R.id.action_home_to_notificationFrag)

        }


        if (clientInform?.show_ecom_v2_theme == "1") {
            iv_notification.setImageResource(R.drawable.ic_cart)
            iv_search.setImageResource(R.drawable.placeholder_user)
        }
    }

    private fun displayPopupWindow(anchorView: View) {

        popup = PopupWindow()

        val binding = DataBindingUtil.inflate<PopupRestaurantMenuBinding>(
            LayoutInflater.from(activity),
            R.layout.popup_restaurant_menu,
            null,
            false
        )
        binding.color = colorConfig

        popup?.contentView = binding.root

        if (viewModel.productLiveData.value?.result?.data?.product?.isNotEmpty() == true) {
            val rvCategory = binding.root.findViewById<RecyclerView>(R.id.rvmenu_category)
            val layoutManager = LinearLayoutManager(activity, RecyclerView.VERTICAL, false)
            rvCategory.layoutManager = layoutManager
            val adapter =
                MenuCategoryAdapter(viewModel.productLiveData.value?.result?.data?.product?.map {
                    it.sub_cat_name ?: ""
                })
            rvCategory.adapter = adapter
            adapter.settingCallback(this)
        }

        popup?.animationStyle = R.style.MyAlertDialogStyle


        popup?.height = WindowManager.LayoutParams.WRAP_CONTENT
        popup?.width = WindowManager.LayoutParams.WRAP_CONTENT
        popup?.isOutsideTouchable = true
        popup?.isFocusable = true

        if (Build.VERSION.SDK_INT >= 24) {
            val a = IntArray(2) //getLocationInWindow required array of size 2
            anchorView.getLocationInWindow(a)
            popup?.showAtLocation(activity?.window?.decorView, Gravity.CENTER, 0, 0)
        } else {
            popup?.showAsDropDown(anchorView)
        }
        popup?.update()

        popup?.dimBehind()

        anchorView.addOnAttachStateChangeListener(this)
    }


    override fun onViewDetachedFromWindow(v: View?) {
        //appUtils.revealShow(v,btn_menu, false,null)
    }

    override fun onViewAttachedToWindow(v: View?) {
        if (v == null) return

        appUtils.revealShow(v, btn_menu, true, null)
    }


    override fun onProductDetail(bean: ProductDataBean?) {
        val bundle = Bundle()

        //if(screenFlowBean?.is_single_vendor==VendorAppType.Single.appType)

        if ((clientInform?.app_selected_theme == "3" || clientInform?.app_selected_theme == "1") && bean?.isSupplier == false) {
            navController(this@HomeFragment).navigate(
                R.id.action_productDetail,
                ProductDetails.newInstance(bean, 1, false)
            )
        } else if (screenFlowBean?.app_type == AppDataType.Food.type) {
            val supplierId = if (bean?.isSupplier == true) bean.id else bean?.supplier_id ?: 0
            bundle.putInt("supplierId", supplierId ?: 0)
            bundle.putInt("branchId", bean?.supplier_branch_id ?: 0)
            bundle.putString("title", bean?.name ?: "")
            bundle.putInt("categoryId", bean?.category_id ?: 0)

            if (screenFlowBean?.app_type == AppDataType.Food.type) {
                if (bookingFlowBean?.is_pickup_order == FoodAppType.Pickup.foodType || clientInform?.is_skip_theme == "1") {
                    bundle.putString("deliveryType", "pickup")
                } else if (clientInform?.dynamic_home_screen_sections == "1" && firstHomeSection != null &&
                    StaticFunction.getFilterByType(firstHomeSection?.code ?: "")
                    != FilterByData.Delivery.type
                ) {
                    when (StaticFunction.getFilterByType(firstHomeSection?.code ?: "")) {
                        FilterByData.DineIn.type -> bundle.putString("deliveryType", "dineIn")
                        FilterByData.Pickup.type -> bundle.putString("deliveryType", "pickup")
                    }
                }

                if (clientInform?.app_selected_template != null
                    && clientInform?.app_selected_template == "1"
                )
                    navController(this@HomeFragment)
                        .navigate(R.id.action_restaurantDetailNew, bundle)
                else
                    navController(this@HomeFragment)
                        .navigate(R.id.action_restaurantDetail, bundle)
            } else {
                navController(this@HomeFragment)
                    .navigate(R.id.action_supplierDetail, bundle)
            }
        } else {
            if (bean?.parent_id != null && bean.parent_id != 0) {
                bean.product_id = bean.parent_id
            }
            if (clientInform?.show_ecom_v2_theme == "1") {
                navController(this@HomeFragment).navigate(
                    R.id.actionProductDetailV2,
                    ProductDetails.newInstance(bean, 1, false)
                )
            } else {
                navController(this@HomeFragment).navigate(
                    R.id.action_productDetail,
                    ProductDetails.newInstance(bean, 1, false)
                )
            }
        }
    }

    private fun tableCapacityObserver() {
        // Create the observer which updates the UI.
        val observer = Observer<ArrayList<kotlin.String>> { resource ->
            //  StaticFunction.tableCapacityDialog(requireContext(), this)
        }

        // Observe the LiveData, passing in this activity as the LifecycleOwner and the observer.
        viewModel.tableCapacityLiveData.observe(this, observer)
    }

    private fun getTableCapacityApi(supplierId: Int, branchId: Int) {
        if (isNetworkConnected)
            viewModel.getTableCapacity(supplierId.toString(), branchId.toString())
    }

    override fun onTableCapacitySelected(capacity: Int) {
        activity?.launchActivity<ScheduleOrder>(SCHEDULE_REQUEST) {
            putExtra("supplierId", supplierId.toString())
            putExtra("dineIn", true)
            putExtra("seating_capacity", capacity)
            putExtra("supplierBranchId", supplierBranchId)
        }
    }

    override fun onSupplierDetail(supplierBean: SupplierDataBean?) {
        if (prefHelper.getKeyValue(AppConstants.ROAD_MAP_TYPE, PrefenceConstants.TYPE_STRING)!!
                .equals("Delivery")
        ) {
            self_Pickup = "8"
        } else {
            if (clientInform?.is_table_booking == "1") {
                self_Pickup = ""
            } else {
                self_Pickup = "9"
            }

        }

        if (self_Pickup.equals("8") || self_Pickup.equals("")) {


            supplierBundle = bundleOf("supplierId" to supplierBean?.id)
            supplierBundle?.putInt("branchId", supplierBean?.supplier_branch_id ?: 0)
            supplierBundle?.putInt("categoryId", supplierBean?.category_id ?: 0)
            supplierBundle?.putString("supplierBannerImage", supplierBean?.supplier_image)
            supplierBundle?.putString("supplierLogo", supplierBean?.logo)
            supplierBundle?.putString("supplierRating", supplierBean?.rating.toString())
            if (prefHelper.getLangCode().toString().equals("15")) {
                name = supplierBean?.name_arabic!!
            } else {
                name = supplierBean?.name!!
            }
//        }
            supplierBundle?.putString("supplierName", name)
            supplierBundle?.putString(
                "supplierTime",
                supplierBean?.delivery_min_time.toString() + " - " + supplierBean?.delivery_max_time.toString() + getString(
                    R.string.min
                )
            )
            if (clientInform?.is_skip_theme == "1" || BuildConfig.CLIENT_CODE == "skipp_0631") {
                supplierBundle?.putString("deliveryType", "pickup")
            }

            if (supplierBean?.is_multi_branch == 1 && clientInform?.branch_flow == "1") {

                supplierBundle?.putString("title", supplierBean?.name)
                supplierBundle?.putBoolean("isFromBranch", true)
                supplierBundle?.putInt("subCategoryId", 0)

                if (isNetworkConnected) {
                    val hashMap = dataManager.updateUserInf()
                    hashMap["supplierId"] = supplierBean?.id.toString()

                    showLoading()

                    viewModel.supplierBranchList(hashMap)
                }
            } else {
                chooseResturant(supplierBundle)
            }
        } else {
            if (supplierBean?.count_supplier_branch == 1) {
                supplierBundle = bundleOf("supplierId" to supplierBean?.id)
                supplierBundle?.putInt("branchId", supplierBean?.supplier_branch_id ?: 0)
                supplierBundle?.putInt("categoryId", supplierBean?.category_id ?: 0)
                supplierBundle?.putString("supplierBannerImage", supplierBean?.supplier_image)
                supplierBundle?.putString("supplierLogo", supplierBean?.logo)
                supplierBundle?.putString("supplierRating", supplierBean?.rating.toString())
                if (prefHelper.getLangCode().toString().equals("15")) {
                    name = supplierBean?.name_arabic!!
                } else {
                    name = supplierBean?.name!!
                }
//        }
                supplierBundle?.putString("supplierName", name)
                supplierBundle?.putString(
                    "supplierTime",
                    supplierBean?.delivery_min_time.toString() + " - " + supplierBean?.delivery_max_time.toString() + getString(
                        R.string.min
                    )
                )
                if (clientInform?.is_skip_theme == "1" || BuildConfig.CLIENT_CODE == "skipp_0631") {
                    supplierBundle?.putString("deliveryType", "pickup")
                }

                chooseResturant(supplierBundle)
            } else {
                val bundle = bundleOf(
                    "title" to supplierBean?.name,
                    "supplierId" to (supplierBean?.id ?: 0),
                    "subCategoryId" to 0
                )
                navController(this@HomeFragment).navigate(R.id.branchListFragment, bundle)
            }
        }
    }

    override fun viewAllNearBy() {
        requireActivity().launchActivity<SuppliersMapFragment> {}
    }

    private fun chooseResturant(bundle: Bundle?) {
        if (sharedPreferences.getString("is_table", "default_name")!!.toString() == "1") {
            navController(this@HomeFragment).navigate(R.id.action_home_to_book_table_detail, bundle)
        } else {
            if (bookingFlowBean?.is_pickup_order == FoodAppType.Pickup.foodType) {
                bundle?.putString("deliveryType", "pickup")
            } else if (clientInform?.dynamic_home_screen_sections == "1" && firstHomeSection != null && StaticFunction.getFilterByType(
                    firstHomeSection?.code
                        ?: ""
                )
                != FilterByData.Delivery.type
            ) {
                when (StaticFunction.getFilterByType(firstHomeSection?.code ?: "")) {
                    FilterByData.DineIn.type -> bundle?.putString("deliveryType", "dineIn")
                    FilterByData.Pickup.type -> bundle?.putString("deliveryType", "pickup")
                }
            }


            if (clientInform?.show_supplier_detail == "1") {
                navController(this@HomeFragment)
                    .navigate(R.id.action_supplierDetail, bundle)
            } else if (clientInform?.app_selected_template != null && clientInform?.app_selected_template == "1") {
                navController(this@HomeFragment)
                    .navigate(R.id.restaurantDetailFragNew, bundle)
            } else {
                if (Utils.checkApp(activity?.applicationContext!!)) {
                    val roadMapType = dataManager.getKeyValue(
                        AppConstants.ROAD_MAP_TYPE,
                        PrefenceConstants.TYPE_STRING
                    )
                    /* if (roadMapType == AppConstants.ROAD_MAP_TYPE_DELIVERY) {
                         navController(this@HomeFragment).navigate(R.id.restaurantDetailFrag2, bundle)
                     }else{*/
                    navController(this@HomeFragment).navigate(R.id.restaurantDetailFrag, bundle)
                    // }
                } else {
                    navController(this@HomeFragment).navigate(R.id.restaurantDetailFrag, bundle)
                }
            }
        }
    }


    override fun addToCart(position: Int, productBean: ProductDataBean?) {

        val cartList = appUtils.getCartList()
        val userData = dataManager.getGsonValue(DataNames.USER_DATA, PojoSignUp::class.java)
        if (productBean?.is_subscription_required == 1 && userData?.data?.is_subscribed != 1) {
            mHomeScreenBinding?.root?.onSnackbar(getString(R.string.subcription_required))
            return
        } else if (cartList.cartInfos?.any { !(it.product_owner_name.isNullOrEmpty()) } == true && cartList.cartInfos?.size ?: 0 >= 4 &&
            cartList.cartInfos?.any { it.productId == productBean?.product_id } == false) {
            /*only 4 max products with out network product are allowed*/
            mHomeScreenBinding?.root?.onSnackbar(getString(R.string.max_products_allowed_with_out))
            return
        }

        if (screenFlowBean?.is_single_vendor == VendorAppType.Single.appType) {
            if (group_deliver_option.checkedRadioButtonId == R.id.rb_pickup) {
                productBean?.self_pickup = FoodAppType.Pickup.foodType
            } else {
                productBean?.self_pickup = FoodAppType.Delivery.foodType
            }
        } else {
            productBean?.self_pickup = FoodAppType.Delivery.foodType
        }



        if (cartList.cartInfos?.any { it.deliveryType == FoodAppType.Both.foodType } == true && productBean?.self_pickup != FoodAppType.Both.foodType) {
            cartList.cartInfos?.mapIndexed { index, cartInfo ->
                cartInfo.deliveryType = productBean?.self_pickup ?: 0
            }

            prefHelper.addGsonValue(DataNames.CART, Gson().toJson(cartList))

            productBean?.self_pickup = productBean?.self_pickup

        } else if (cartList.cartInfos?.any { it.deliveryType != FoodAppType.Both.foodType } == true && productBean?.self_pickup == FoodAppType.Both.foodType) {
            val cartInfo = cartList.cartInfos?.get(0)

            productBean.self_pickup = cartInfo?.deliveryType
        }


        if (productBean?.self_pickup == FoodAppType.Pickup.foodType || productBean?.self_pickup == FoodAppType.Both.foodType) {
            val mRestUser = LocationUser()
            mRestUser.address = "${productBean.supplier_name} , ${
                productBean.supplier_address
                    ?: ""
            }"
            prefHelper.addGsonValue(PrefenceConstants.RESTAURANT_INF, Gson().toJson(mRestUser))
        }


        if (screenFlowBean?.app_type == AppDataType.Food.type && screenFlowBean?.is_single_vendor == VendorAppType.Multiple.appType) {
            // val cartList: CartList? = prefHelper.getGsonValue(DataNames.CART, CartList::class.java)

            if (cartList.cartInfos?.isNotEmpty() == true) {
                if (cartList.cartInfos?.any { it.deliveryType != productBean?.self_pickup } == true) {
                    appUtils.clearCart()
                }
            }
        }



        if (productBean?.adds_on?.isNotEmpty() == true) {
            //    val cartList: CartList? = prefHelper.getGsonValue(DataNames.CART, CartList::class.java)

            if (appUtils.checkProdExistance(productBean.product_id)) {
                val savedProduct = cartList.cartInfos?.filter {
                    it.supplierId == productBean.supplier_id ?: 0 && it.productId == productBean.product_id
                } ?: emptyList()

                SavedAddon.newInstance(
                    productBean,
                    FoodAppType.Delivery.foodType,
                    savedProduct,
                    this
                ).show(childFragmentManager, "savedAddon")
            } else {
                AddonFragment.newInstance(productBean, FoodAppType.Delivery.foodType, this)
                    .show(childFragmentManager, "addOn")
            }

        } else {
            val appointmentAdded = cartList.cartInfos?.any { it.is_appointment == "1" }!!

            if (bookingFlowBean?.vendor_status == 0 && appUtils.checkVendorStatus(
                    productBean?.supplier_id,
                    vendorBranchId = productBean?.supplier_branch_id,
                    branchFlow = clientInform?.branch_flow
                )
            ) {
                appUtils.mDialogsUtil.openAlertDialog(
                    activity
                        ?: requireContext(), getString(
                        R.string.clearCart, textConfig?.supplier
                            ?: "", textConfig?.proceed ?: ""
                    ), "Yes", "No", this
                )
            } else if (appointmentAdded)
                appUtils.mDialogsUtil.openAlertDialog(
                    activity
                        ?: requireContext(),
                    getString(R.string.appointment_multiple_service_message),
                    "Okay",
                    "",
                    null
                )
            else if (!cartList.cartInfos.isNullOrEmpty() && productBean?.is_appointment == "1")
                appUtils.mDialogsUtil.openAlertDialog(
                    activity
                        ?: requireContext(),
                    getString(R.string.appointment_multiple_service_message),
                    "Okay",
                    "",
                    null
                )
            else {
                if (appUtils.checkBookingFlow(requireContext(), productBean?.product_id, this)) {

                    // FOR SINGLE TYPE ADDRESS IS ALREADY SAVED IN SPLASH SCREEN
                    if (screenFlowBean?.is_single_vendor != VendorAppType.Single.appType) {
                        if (productBean?.is_appointment == "1") {
                            val mRestUser = LocationUser()
                            mRestUser.address = "${productBean.supplier_name} , ${
                                productBean.supplier_address
                                    ?: ""
                            }"
                            prefHelper.addGsonValue(
                                PrefenceConstants.RESTAURANT_INF,
                                Gson().toJson(mRestUser)
                            )
                        }
                    }
                    addCart(productBean)
                }
            }
        }

    }


    override fun onPagerScroll(listAdapter: BannerListAdapter, rvBannerList: RecyclerView) {

        val NUM_PAGES =
            mSupplierList?.find { it.viewType == HomeItemAdapter.BANNER_TYPE }?.itemModel?.bannerList?.count()
                ?: 0
        var currentPage = 0

        if (bannerHandler != null && bannerRunnable == null) {
            bannerRunnable = Runnable {
                if (currentPage == NUM_PAGES) {
                    currentPage = 0
                }

                rvBannerList.scrollToPosition(currentPage++)
            }
            val swipeTimer = Timer()
            swipeTimer.schedule(object : TimerTask() {
                override fun run() {
                    bannerRunnable?.let { bannerHandler.post(it) }


                }
            }, 3000, 3000)
        } else {
            bannerRunnable?.let { bannerHandler.post(it) }
        }
    }

    override fun onSearchClickedForV2Theme() {
        navController(this@HomeFragment)
            .navigate(R.id.action_homeFragment_to_searchFragment)
    }

    override fun supplierViewMoreCliked(data: SupplierDataBean?, listType: Int, title: String1) {
        val bundle = bundleOf(
            "listType" to listType,
            "title" to title
        )
        navController(this@HomeFragment)
            .navigate(R.id.viewAllSuppliersFragment, bundle)

    }

    override fun onDestroy() {
        super.onDestroy()

        if (bannerHandler == null && bannerRunnable == null) return

        bannerRunnable?.let { bannerHandler.removeCallbacks(it) }
        //  EventBus.getDefault().unregister(this)
    }

    private fun addCart(productBean: ProductDataBean?) {
        val addInDb: Boolean =
            !(clientInform?.enable_freelancer_flow == "1" && screenFlowBean?.app_type == AppDataType.HomeServ.type && productBean?.is_appointment != "1")

        if (productBean?.is_question == 1 && prodUtils.addItemToCart(
                productBean,
                addInDb
            ) != null
        ) {
            productBean.prod_quantity = 0f
            val bundle = bundleOf(
                "productBean" to productBean,
                "is_Category" to false,
                "subCategoryId" to productBean.detailed_sub_category_id
            )
            navController(this@HomeFragment).navigate(
                R.id.action_homeFragment_to_questionFrag,
                bundle
            )
        } else {
            productBean?.type = screenFlowBean?.app_type

            val productDataBean = prodUtils.addItemToCart(productBean, isAddInDb = addInDb)
            if (productDataBean != null) {
                if (clientInform?.enable_freelancer_flow == "1" && productBean?.is_appointment != "1")
                    onBookNow(productBean)
                else
                    refreshProductList(productBean)

            }
        }

    }

    override fun onBookNow(bean: ProductDataBean?) {
        if (bean?.isSupplier == true) {
            bookDineIn(bean.id, bean.supplier_branch_id, bean.is_dine_in)
        } else {
            if (bean?.prod_quantity ?: 0f > 0f) {
                if (dataManager.getCurrentUserLoggedIn()) {
                    val productIds = ArrayList<kotlin.String>()
                    productIds.add(bean?.product_id.toString())
                    activity?.launchActivity<ServSelectionActivity>(AppConstants.REQUEST_AGENT_DETAIL)
                    {
                        putExtra(DataNames.SUPPLIER_BRANCH_ID, bean?.supplier_branch_id)
                        putExtra("screenType", "productList")
                        putExtra("serviceData", bean)
                        putExtra("productIds", productIds.toTypedArray())
                    }
                } else {
                    appUtils.checkLoginFlow(
                        requireActivity(),
                        AppConstants.REQUEST_CART_LOGIN_BOOKING
                    )
                }
            } else {
                mHomeScreenBinding?.root?.onSnackbar(getString(R.string.add_quantity))
            }
        }
    }

    private fun refreshProductList(productBean: ProductDataBean?) {


        specialOffers?.map {
            it.prod_quantity = StaticFunction.getCartQuantity(
                activity
                    ?: requireContext(), it.product_id
            )
        }

        mPopularList?.map {
            it.prod_quantity = StaticFunction.getCartQuantity(
                activity
                    ?: requireContext(), it.product_id
            )
        }

        mSupplierList?.mapIndexed { index, supplierDataBean ->
            if (supplierDataBean.itemModel?.specialOffers?.any { it.product_id == productBean?.product_id } == true) {
                homeItemAdapter?.notifyItemRangeChanged(index, mSupplierList?.size ?: 0)
            }

            if (supplierDataBean.itemModel?.popularProdList?.any { it.product_id == productBean?.product_id } == true) {
                homeItemAdapter?.notifyItemRangeChanged(index, mSupplierList?.size ?: 0)
            }

            if (supplierDataBean.itemModel?.recentProdList?.any { it.product_id == productBean?.product_id } == true) {
                homeItemAdapter?.notifyItemRangeChanged(index, mSupplierList?.size ?: 0)
            }
        }

        // homeItemAdapter?.notifyItemRangeChanged(2,mSupplierList?.size?:0)
        // homeItemAdapter?.notifyItemRangeChanged(4,mSupplierList?.size?:0)


        /*  val specialItem = specialOffers?.find { it.product_id == productBean?.product_id }

          if (specialItem != null) {
              specialItem.prod_quantity = StaticFunction.getCartQuantity(activity
                      ?: requireContext(), specialItem.product_id)

              specialOffers?.set(specialOffers?.indexOf(specialItem) ?: -1, specialItem)
             // specialListAdapter?.notifyItemChanged(specialOffers?.indexOf(specialItem) ?: -1)

          }*/


        if (screenFlowBean?.app_type == AppDataType.Food.type) {
            mSupplierList?.mapIndexed { index, supplierDataBean ->

                if (supplierDataBean.viewType == HomeItemAdapter.SINGLE_PROD_TYPE &&
                    supplierDataBean.itemModel?.vendorProdList?.value?.any { it.product_id == productBean?.product_id } == true
                ) {

                    supplierDataBean.itemModel?.vendorProdList?.value?.map { it1 ->
                        it1.prod_quantity = StaticFunction.getCartQuantity(
                            activity
                                ?: requireContext(), it1.product_id
                        )
                    }
                    if (homeItemAdapter != null) {
                        homeItemAdapter?.notifyItemChanged(index)
                    }
                }

            }
        } else {
            mSupplierList?.mapIndexed { index, supplierDataBean ->
                if (supplierDataBean.viewType == HomeItemAdapter.SINGLE_PROD_TYPE &&
                    supplierDataBean.itemModel?.specialOffers?.any { it.product_id == productBean?.product_id } == true
                ) {

                    supplierDataBean.itemModel?.specialOffers?.map { productBean ->
                        productBean.prod_quantity = StaticFunction.getCartQuantity(
                            activity
                                ?: requireContext(), productBean.product_id
                        )
                    }

                    if (homeItemAdapter != null) {
                        homeItemAdapter?.notifyItemChanged(index)
                    }
                }
            }
        }
        showBottomCart()
    }

    override fun removeToCart(position: Int, productBean: ProductDataBean?) {

        if (productBean?.adds_on?.isNotEmpty() == true) {
            val cartList: CartList? = prefHelper.getGsonValue(DataNames.CART, CartList::class.java)

            val savedProduct = cartList?.cartInfos?.filter {
                it.supplierId == productBean.supplier_id && it.productId == productBean.product_id
            } ?: emptyList()

            SavedAddon.newInstance(
                productBean, productBean.self_pickup
                    ?: -1, savedProduct, this
            ).show(childFragmentManager, "savedAddon")

        } else {
            productBean.apply { prodUtils.removeItemToCart(productBean) }
            refreshProductList(productBean)
        }
    }

    override fun addtoWishList(adapterPosition: Int, status: Int?, productId: Int?) {

        speProductId = productId ?: 0
        prodStatus = status ?: 0

        if (prefHelper.getCurrentUserLoggedIn()) {
            if (isNetworkConnected) {
                viewModel.markFavProduct(productId, status)
            }
        } else {
            startActivityForResult(
                Intent(activity, appUtils.checkLoginActivity()),
                AppConstants.REQUEST_WISH_PROD
            )
        }
    }

    override fun onProdDesc(productDesc: kotlin.String) {
        StaticFunction.sweetDialogueSuccess11(
            activity,
            getString(R.string.description),
            productDesc,
            false,
            1002,
            this
        )
    }

    override fun onProdAllergies(bean: ProductDataBean?) {
        appUtils.showAllergiesDialog(requireContext(), bean?.allergy_description ?: "")
    }

    override fun onProdDialog(bean: ProductDataBean?) {
        appUtils.mDialogsUtil.showProductDialog(requireContext(), bean)
    }


    override fun onEditQuantity(
        quantity: Float,
        productBean: ProductDataBean?,
        parentPosition: Int,
        childPosition: Int,
        isOpen: Boolean
    ) {
        //do nothing
    }

    override fun onProdAllergiesClicked(bean: ProductDataBean) {
        appUtils.showAllergiesDialog(requireContext(), bean.allergy_description ?: "")
    }

    override fun onSponsorWishList(
        supplier: SupplierInArabicBean?,
        parentPos: Int?,
        isChecked: Boolean
    ) {
        if (prefHelper.getCurrentUserLoggedIn()) {
            if (isNetworkConnected) {
                supplier?.parentPosition = parentPos
                if (isChecked)
                    viewModel.markFavSupplier(supplier)
                else
                    viewModel.unFavSupplier(supplier)
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == AppConstants.REQUEST_WISH_PROD && resultCode == Activity.RESULT_OK) {
            val pojoLoginData = StaticFunction.isLoginProperly(activity)
            if (pojoLoginData.data != null) {
                viewModel.markFavProduct(speProductId, prodStatus)
            }
        } else if (requestCode == WALLET_REQUEST && resultCode == Activity.RESULT_OK) {
            cnst_wallet.performClick()
        } else if (requestCode == AppConstants.REQUEST_PAYMENT_OPTION && resultCode == Activity.RESULT_OK) {
            val payItem = data?.getParcelableExtra<CustomPayModel>("payItem")
            bookTableWithSchedule(
                selectedDateTimeForScheduling?.selectedTable?.id?.toString(),
                supplierId
            )
        } else if (requestCode == SCHEDULE_REQUEST && resultCode == Activity.RESULT_OK) {
            selectedDateTimeForScheduling = data?.getParcelableExtra("slotDetail")
            if (clientInform?.table_book_mac_theme == "1" && selectedDateTimeForScheduling?.selectedTable != null) {
                AlertDialog.Builder(requireContext())
                    .setMessage(getString(R.string.select_items_to_continue))
                    .setCancelable(false)
                    .setPositiveButton(getString(R.string.yes)) { _, _ ->
                        prefHelper.setkeyValue(
                            DataNames.SAVED_TABLE_DATA,
                            Gson().toJson(selectedDateTimeForScheduling)
                        )
                        showSupplierDetailScreen()
                    }
                    .setNegativeButton(getString(R.string.no)) { _, _ ->
                        onPaymentSelection()
                    }
                    .show()

            } else {
                if (clientInform?.by_pass_tables_selection == "1") {
                    bookTableWithSchedule("0", supplierId)
                } else {
                    getListOfRestaurantsAccordingToSlot()
                }
            }
        } else if (requestCode == AppConstants.REQUEST_AGENT_DETAIL && resultCode == Activity.RESULT_OK) {
            val dataAgent = data?.getParcelableExtra<AgentCustomParam>("agentData")

            val productBean = data?.getParcelableExtra<ProductDataBean>("serviceData")

            if (productBean != null) {
                productBean.agentDetail = dataAgent
                if (productBean.agentBufferPrice != null)
                    productBean.netPrice = (productBean.netPrice
                        ?: 0.0f).plus(productBean.agentBufferPrice?.toFloat() ?: 0f)
                if (!appUtils.checkProdExistance(productBean.product_id))
                    appUtils.addProductDb(
                        requireContext(), screenFlowBean?.app_type
                            ?: 0, productBean
                    )
                else
                    StaticFunction.updateCart(
                        requireContext(), productBean.product_id, productBean.prod_quantity,
                        productBean.netPrice ?: 0f
                    )

                navController(this@HomeFragment).navigate(R.id.action_homeFragment_to_cart, null)


            }

        }
    }

    private fun onPaymentSelection() {
        dataManager.getKeyValue(DataNames.FEATURE_DATA, PrefenceConstants.TYPE_STRING).toString()
            .let {
                val listType: Type =
                    object : TypeToken<ArrayList<SettingModel.DataBean.FeatureData?>?>() {}.type
                val featureList: ArrayList<SettingModel.DataBean.FeatureData> =
                    Gson().fromJson(it, listType)

                activity?.launchActivity<PaymentListActivity>(AppConstants.REQUEST_PAYMENT_OPTION) {
                    putParcelableArrayListExtra("feature_data", featureList)
                    putExtra("enablePayment", true)
                    putExtra(
                        "totalAmount",
                        selectedDateTimeForScheduling?.selectedTable?.table_booking_price
                    )
                }
            }
    }

    private fun showSupplierDetailScreen() {
        val bundle = Bundle()
        bundle.putInt("supplierId", supplierId ?: 0)
        bundle.putInt("branchId", supplierBranchId ?: 0)
        bundle.putInt("categoryId", 0)

        if (screenFlowBean?.app_type == AppDataType.Food.type) {
            if (bookingFlowBean?.is_pickup_order == FoodAppType.Pickup.foodType || clientInform?.is_skip_theme == "1") {
                bundle.putString("deliveryType", "pickup")
            } else if (clientInform?.dynamic_home_screen_sections == "1" && firstHomeSection != null && StaticFunction.getFilterByType(
                    firstHomeSection?.code
                        ?: ""
                )
                != FilterByData.Delivery.type
            ) {
                when (StaticFunction.getFilterByType(firstHomeSection?.code ?: "")) {
                    FilterByData.DineIn.type -> bundle.putString("deliveryType", "dineIn")
                    FilterByData.Pickup.type -> bundle.putString("deliveryType", "pickup")
                }
            }

            if (clientInform?.app_selected_template != null
                && clientInform?.app_selected_template == "1"
            )
                navController(this@HomeFragment)
                    .navigate(R.id.action_restaurantDetailNew, bundle)
            else
                navController(this@HomeFragment)
                    .navigate(R.id.action_restaurantDetail, bundle)
        } else {
            navController(this@HomeFragment)
                .navigate(R.id.action_supplierDetail, bundle)
        }
    }

    private var selectedDateTimeForScheduling: SupplierSlots? = null
    private var supplierId: Int? = null
    private var supplierBranchId: Int? = null

    override fun onBookNow(supplierData: SupplierDataBean) {
        bookDineIn(supplierData.id, supplierData.supplier_branch_id, supplierData.is_dine_in)
    }

    override fun onBookNow(supplierData: SupplierInArabicBean) {
        bookDineIn(supplierData.id, supplierData.supplier_branch_id, supplierData.is_dine_in)
    }

    private fun bookDineIn(supplier_id: Int?, supplier_branch_d: Int?, isDineIn: Int?) {
        if (dataManager.getCurrentUserLoggedIn()) {
            supplierId = supplier_id
            supplierBranchId = supplier_branch_d
            selectedDateTimeForScheduling = null

            if (isDineIn == 1 && clientInform?.is_table_booking == "1" && clientInform?.table_book_mac_theme == "1") {
                StaticFunction.tableCapacityDialog(requireContext(), this)
                //  getTableCapacityApi(supplier_id?:0, supplier_branch_d?:0)
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

    override fun onListViewChanges(adapterPosition: Int, isGrid: Boolean) {
        if (adapterPosition != -1) {
            mSupplierList?.get(adapterPosition)?.itemModel?.nearBySupplierView =
                if (isGrid) NearBySupplierType.GRID.type else NearBySupplierType.LIST.type
            homeItemAdapter?.notifyItemChanged(adapterPosition)
        }
    }


    private fun bookTableWithSchedule(tableId: kotlin.String?, supplierId: Int?) {

        val tempRequestHolder = hashMapOf(
            "user_id" to dataManager.getKeyValue(
                PrefenceConstants.USER_ID,
                PrefenceConstants.TYPE_STRING
            ).toString(),
            "table_id" to tableId,
            "slot_id" to selectedDateTimeForScheduling?.supplierTimings?.get(0)?.id.toString(),
            "schedule_date" to selectedDateTimeForScheduling?.startDateTime,
            "schedule_end_date" to selectedDateTimeForScheduling?.endDateTime,
            "supplier_id" to (supplierId ?: 0).toString(),
            "branch_id" to (supplierBranchId ?: 0).toString()
        )

        viewModel.makeBookingAccordingToSlot(tempRequestHolder)
    }

    private fun getListOfRestaurantsAccordingToSlot() {
        Log.e("nextFrag", "....")
        val bundle = bundleOf(
            "slot_id" to selectedDateTimeForScheduling?.supplierTimings?.get(0)?.id.toString(),
            "supplierId" to (supplierId ?: 0),
            "branchId" to (supplierBranchId ?: 0),
            "requestFromCart" to "0"
        )

        if (activity != null) {
            navController(this@HomeFragment)
                .navigate(R.id.action_home_to_tableSelection, bundle)
        }

    }

    //    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onResultReceived(result: ListItem?) {
        if (result?.requestedFrom == "1") {
            return
        }
        Log.e("home event", "....")
        bookTableWithSchedule(result?.id.toString(), supplierId)
    }

    override fun onTableSuccessfullyBooked() {
        selectedDateTimeForScheduling = null
        StaticFunction.sweetDialogueSuccess11(
            activity, getString(R.string.table_book_message_title),
            getString(R.string.table_book_message),
            false, 1001, this
        )
    }

    override fun onCategoryDetail(bean: English?) {


        if (clientInform?.show_tags_for_suppliers == "1") {
            val bundle = bundleOf("tag_id" to bean?.id.toString())
            if (clientInform?.is_skip_theme == "1") {
                navController(this@HomeFragment).navigate(
                    R.id.action_resturantHomeFrag_to_supplierListFragment,
                    bundle
                )
            } else
                navController(this@HomeFragment)
                    .navigate(R.id.action_supplierAll, bundle)
        } else {
            val bundle = bundleOf(
                "title" to bean?.name,
                "categoryId" to (bean?.id ?: 0),
                "subCategoryId" to 0
            )

            var prodViewedAction = mapOf(
                "Category Id" to bean?.name
            )

            cleverTapDefaultInstance?.pushEvent("Category_viewd_event", prodViewedAction)

            if (clientInform?.show_ecom_v2_theme == "1" && bean?.id == null) {
                navController(this@HomeFragment)
                    .navigate(R.id.action_homeFrag_to_categories)
                return
            }
            if (screenFlowBean?.is_single_vendor == VendorAppType.Multiple.appType) {
                if (bean?.sub_category?.count() ?: 0 > 0 && screenFlowBean?.app_type != AppDataType.Food.type) {
                    navController(this@HomeFragment).navigate(R.id.actionSubCategory, bundle)
                } else if (clientInform?.is_skip_theme == "1") {
                    navController(this@HomeFragment).navigate(
                        R.id.action_resturantHomeFrag_to_supplierListFragment,
                        bundle
                    )
                } else {
                    navController(this@HomeFragment)
                        .navigate(R.id.action_supplierAll, bundle)
                }
            } else {
                if (screenFlowBean?.app_type != AppDataType.Food.type) {
                    if (bean?.sub_category?.count() ?: 0 > 0) {
                        navController(this@HomeFragment).navigate(R.id.actionSubCategory, bundle)
                    } else {
                        bundle.putBoolean("has_subcat", true)
                        bundle.putInt("supplierId", bean?.supplier_branch_id ?: 0)
                        navController(this@HomeFragment).navigate(
                            R.id.action_productListing,
                            bundle
                        )
                    }
                } else {
                    bundle.putInt("supplierId", bean?.id ?: 0)
                    if (clientInform?.app_selected_template != null && clientInform?.app_selected_template == "1") {
                        navController(this@HomeFragment)
                            .navigate(R.id.action_restaurantDetailNew, bundle)
                    } else {
                        navController(this@HomeFragment)
                            .navigate(R.id.action_restaurantDetail, bundle)
                    }
                }
            }
        }
    }

    override fun onSponsorDetail(supplier: SupplierInArabicBean?) {

        val bundle = bundleOf(
            "supplierId" to supplier?.id,
            "title" to supplier?.name,
            "branchId" to supplier?.supplier_branch_id,
            "categoryId" to supplier?.category_id
        )

        if (clientInform?.show_supplier_detail != null && clientInform?.show_supplier_detail == "1") {
            navController(this@HomeFragment)
                .navigate(R.id.action_supplierDetail, bundle)
        } else {
            if (screenFlowBean?.app_type == AppDataType.Food.type) {
                if (clientInform?.dynamic_home_screen_sections == "1" && firstHomeSection != null &&
                    StaticFunction.getFilterByType(firstHomeSection?.code ?: "")
                    != FilterByData.Delivery.type
                ) {
                    when (StaticFunction.getFilterByType(firstHomeSection?.code ?: "")) {
                        FilterByData.DineIn.type -> bundle.putString("deliveryType", "dineIn")
                        FilterByData.Pickup.type -> bundle.putString("deliveryType", "pickup")
                    }
                }
                if (clientInform?.app_selected_template != null && clientInform?.app_selected_template == "1") {
                    navController(this@HomeFragment).navigate(
                        R.id.action_restaurantDetailNew,
                        bundle
                    )
                } else {
                    navController(this@HomeFragment).navigate(R.id.action_restaurantDetail, bundle)
                }
            } else {
                if (!supplier?.category.isNullOrEmpty()) {
                    bundle.putBoolean("is_supplier", true)
                    bundle.putParcelable("supplierData", supplier)
                    bundle.putParcelableArrayList(
                        "subcategory", ArrayList<Parcelable>(
                            supplier?.category
                                ?: mutableListOf()
                        )
                    )

                    if (clientInform?.is_supplier_detail == "1") {
                        if (clientInform?.show_ecom_v2_theme == "1") {
                            navController(this@HomeFragment)
                                .navigate(R.id.action_supplierDetail_v2, bundle)
                        } else {
                            navController(this@HomeFragment)
                                .navigate(R.id.action_supplierDetail, bundle)
                        }
                    } else {
                        navController(this@HomeFragment)
                            .navigate(R.id.actionSubCategory, bundle)
                    }

                } else {
                    navController(this@HomeFragment).navigate(R.id.action_productListing, bundle)
                }

            }
        }
    }

    override fun onSponsorDetail(supplier: SupplierDataBean?) {

        val bundle = bundleOf(
            "supplierId" to supplier?.id,
            "title" to supplier?.name,
            "branchId" to supplier?.supplier_branch_id,
            "categoryId" to supplier?.category_id
        )

        if (clientInform?.show_supplier_detail != null && clientInform?.show_supplier_detail == "1") {

            navController(this@HomeFragment)
                .navigate(R.id.action_supplierDetail, bundle)
        } else {
            if (screenFlowBean?.app_type == AppDataType.Food.type) {
                if (clientInform?.dynamic_home_screen_sections == "1" && firstHomeSection != null &&
                    StaticFunction.getFilterByType(firstHomeSection?.code ?: "")
                    != FilterByData.Delivery.type
                ) {
                    when (StaticFunction.getFilterByType(firstHomeSection?.code ?: "")) {
                        FilterByData.DineIn.type -> bundle.putString("deliveryType", "dineIn")
                        FilterByData.Pickup.type -> bundle.putString("deliveryType", "pickup")
                    }
                }
                if (clientInform?.app_selected_template != null && clientInform?.app_selected_template == "1") {
                    navController(this@HomeFragment).navigate(
                        R.id.action_restaurantDetailNew,
                        bundle
                    )
                } else {
                    navController(this@HomeFragment).navigate(R.id.action_restaurantDetail, bundle)
                }
            } else {
                if (!supplier?.category.isNullOrEmpty()) {
                    bundle.putBoolean("is_supplier", true)
                    bundle.putParcelable("supplierData", supplier)
                    bundle.putParcelableArrayList(
                        "subcategory", ArrayList<Parcelable>(
                            supplier?.category
                                ?: mutableListOf()
                        )
                    )


                    if (clientInform?.is_supplier_detail == "1") {
                        if (clientInform?.show_ecom_v2_theme == "1") {
                            navController(this@HomeFragment)
                                .navigate(R.id.action_supplierDetail_v2, bundle)
                        } else {
                            navController(this@HomeFragment)
                                .navigate(R.id.action_supplierDetail, bundle)
                        }
                    } else {
                        navController(this@HomeFragment)
                            .navigate(R.id.actionSubCategory, bundle)
                    }

                } else {
                    navController(this@HomeFragment).navigate(R.id.action_productListing, bundle)
                }

            }
        }
    }


    override fun onSupplierCall(supplier: SupplierInArabicBean?) {
        callSupplier(supplier?.supplierPhoneNumber ?: "")
    }

    override fun onSucessListner() {

        appUtils.clearCart()

        refreshProductList(null)
    }

    override fun onSuccessListener() {

    }

    override fun onErrorListener() {

    }

    override fun onBannerDetail(bannerBean: TopBanner?) {

        if (bannerBean?.flow_banner_type == BannerRedirection.NoRedirection.type || (screenFlowBean?.is_single_vendor == VendorAppType.Single.appType
                    && screenFlowBean?.app_type != AppDataType.HomeServ.type)
        ) return

        val bundle = bundleOf(
            "supplierId" to bannerBean?.supplier_id,
            "title" to bannerBean?.name,
            "parent_category" to bannerBean?.category_id,
            "subCategoryId" to 0,
            "categoryId" to bannerBean?.category_id
        )

        when (bannerBean?.flow_banner_type) {
            BannerRedirection.ForSubscription.type -> {
                if (clientInform?.is_user_subscription == "1")
                    navController(this@HomeFragment).navigate(R.id.action_home_to_subscriptionFrag)
            }

            BannerRedirection.ForCategory.type -> {
                if (bannerBean.is_subcategory == 1 && screenFlowBean?.app_type != AppDataType.Ecom.type) {

                    if (screenFlowBean?.app_type == AppDataType.Food.type) {

                        navController(this@HomeFragment)
                            .navigate(R.id.action_supplierAll, bundle)
                    } else {
                        navController(this@HomeFragment).navigate(R.id.actionSubCategory, bundle)
                    }
                } else {
                    navController(this@HomeFragment).navigate(R.id.action_productListing, bundle)
                }
            }
            else -> {
                if (isNetworkConnected) {
                    viewModel.fetchSupplierDetail(
                        bannerBean?.branch_id
                            ?: 0, bannerBean?.category_id
                            ?: 0, bannerBean?.supplier_id
                    )
                }
            }
        }
    }

    override fun supplierDetailSuccess(data: DataSupplierDetail) {

        val bundle = bundleOf(
            "supplierId" to data.supplier_id,
            "title" to data.name,
            "branchId" to data.branchId,
            "categoryId" to data.category_id
        )

        if (screenFlowBean?.app_type == AppDataType.Food.type) {
            if (clientInform?.app_selected_template != null && clientInform?.app_selected_template == "1") {
                navController(this@HomeFragment).navigate(R.id.action_restaurantDetailNew, bundle)
            } else {
                navController(this@HomeFragment).navigate(R.id.action_restaurantDetail, bundle)
            }
        } else {
            if (!data.category.isNullOrEmpty()) {
                /*bundle.putBoolean("is_supplier", true)
                bundle.putParcelable("supplierData", data)*/
                bundle.putParcelableArrayList(
                    "subcategory", ArrayList<Parcelable>(
                        data.category
                            ?: mutableListOf()
                    )
                )

                if (clientInform?.is_supplier_detail == "1") {
                    if (clientInform?.show_ecom_v2_theme == "1") {
                        navController(this@HomeFragment)
                            .navigate(R.id.action_supplierDetail_v2, bundle)
                    } else {
                        navController(this@HomeFragment)
                            .navigate(R.id.action_supplierDetail, bundle)
                    }
                } else {
                    navController(this@HomeFragment)
                        .navigate(R.id.actionSubCategory, bundle)
                }

            } else {
                navController(this@HomeFragment).navigate(R.id.action_productListing, bundle)
            }

        }
    }

    override fun onSearchItem(text: kotlin.String?) {

    }

    override fun onHomeCategory(position: Int) {

        when (position) {
            0 -> homelytManager?.scrollToPositionWithOffset(4, mSupplierList?.size ?: 0)
            1 -> homelytManager?.scrollToPositionWithOffset(5, mSupplierList?.size ?: 0)
            2 -> homelytManager?.scrollToPositionWithOffset(7, mSupplierList?.size ?: 0)
        }


    }

    override fun onViewMore(title: String1?) {
        val bundle = bundleOf(
            "cat_id" to catId,
            "title" to title
        )
        navController(this@HomeFragment)
            .navigate(R.id.action_OfferListing, bundle)
    }

    override fun onViewAllCategories(list: List<English>) {
        val array = ArrayList<English>()
        array.addAll(list)
        val bundle = bundleOf("categoryList" to array)
        navController(this@HomeFragment)
            .navigate(R.id.action_frag_to_categoriesListFragment, bundle)
    }


    override fun onSpclView(specialListAdapter: SpecialListAdapter?) {

        this.specialListAdapter = specialListAdapter
    }

    override fun onFilterScreen() {
        val bottomSheetFragment = BottomSheetFragment()
        bottomSheetFragment.show(childFragmentManager, "assasa")
    }

    override fun onRefresh() {

        swiprRefresh.isRefreshing = false
        iv_no_store?.visibility = View.GONE
        HomeItemAdapter.SORT_POPUP = getString(R.string.sort_by_new)
        clearViewModelApi()
    }

    public fun clearViewModelApi(): Boolean {

/*        specialOffers?.clear()
        mSupplierList?.clear()*/

        if (::viewModel.isInitialized) {

            viewModel.homeDataLiveData.value = null
            viewModel.supplierLiveData.value = null
            viewModel.offersLiveData.value = null
            viewModel.productLiveData.value = null
            viewModel.highestRatingSupplierLiveData.value = null
            viewModel.newRestaurantSupplierLiveData.value = null

            fetchCategories()

            return false
        } else {
            return true
        }

    }

    override fun onBrandSelect(brandsBean: Brand) {

        val bundle = Bundle()
        bundle.putInt("brand_id", brandsBean.id ?: -1)
        bundle.putBoolean("has_brands", true)
        bundle.putString("title", brandsBean.name)

        navController(this@HomeFragment).navigate(R.id.action_productListing, bundle)
    }

    override fun onFavStatus() {
        mSupplierList?.mapIndexed { index, supplierDataBean ->

            if (supplierDataBean.viewType == HomeItemAdapter.SPL_PROD_TYPE && supplierDataBean.itemModel?.specialOffers?.any { it.product_id == speProductId } == true) {
                supplierDataBean.itemModel?.specialOffers?.filter { it.product_id == speProductId }
                    ?.map {
                        it.is_favourite = prodStatus
                    }

                homeItemAdapter?.notifyItemChanged(index)
            }


            if (supplierDataBean.viewType == HomeItemAdapter.POPULAR_TYPE && supplierDataBean.itemModel?.popularProdList?.any { it.product_id == speProductId } == true) {
                supplierDataBean.itemModel?.popularProdList?.filter { it.product_id == speProductId }
                    ?.map {
                        it.is_favourite = prodStatus
                    }

                homeItemAdapter?.notifyItemChanged(index)
            }
        }
    }

    override fun favSupplierResponse(supplierId: SupplierInArabicBean?) {
        supplierId?.Favourite = 1
        if (supplierId?.parentPosition != null && supplierId.parentPosition != -1)
            homeItemAdapter?.notifyItemChanged(supplierId.parentPosition ?: 0)
    }

    override fun unFavSupplierResponse(data: SupplierInArabicBean?) {
        data?.Favourite = 0
        if (data?.parentPosition != null && data.parentPosition != -1)
            homeItemAdapter?.notifyItemChanged(data.parentPosition ?: 0)
    }

    override fun onErrorOccur(message: String1) {

        tvArea.isEnabled = true

        cnst_home_lyt.onSnackbar(message)
    }

    override fun onSessionExpire() {
        openActivityOnTokenExpire()
    }


    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.tvArea -> {
                if (DELIVERY_OPTIONS == DeliveryType.DeliveryOrder.type) {
                    if (clientInform?.show_ecom_v2_theme == "1") {
                        AddressDialogFragmentV2.newInstance(0).show(childFragmentManager, "dialog")
                    } else {
                        AddressDialogFragment.newInstance(0, 0).show(childFragmentManager, "dialog")
                    }
                }
            }
        }
    }

    override fun onAddressSelect(adrsBean: AddressBean) {
        rv_homeItem.visibility = View.GONE
        location_txt.text = getString(R.string.location)

        adrsBean.let {
            appUtils.setUserLocale(it)

            prefHelper.addGsonValue(PrefenceConstants.ADRS_DATA, Gson().toJson(it))

            tvArea.text = appUtils.getAddressFormat(it)
        }

        clearViewModelApi()
    }

    override fun onDestroyDialog() {


        if (screenFlowBean?.app_type == AppDataType.Food.type) {
            rb_pickup.isChecked = true
            DELIVERY_OPTIONS = DeliveryType.PickupOrder.type

            //StaticFunction.clearCart(activity)
            checkSavedProd()
            showBottomCart()
        }
    }

    override fun onVehicleSelect(
        id: kotlin.String,
        model: kotlin.String,
        color: kotlin.String,
        plate_no: kotlin.String
    ) {
        TODO("Not yet implemented")
    }


    companion object {
        fun newInstance() = HomeFragment()
    }

    override fun onProdAdded(
        productBean: ProductDataBean?,
        parentPosition: Int,
        childPosition: Int,
        isOpen: Boolean
    ) {
        this.parentPos = parentPosition
        this.childPos = childPosition

        val prodViewedAction = mapOf(
            "Product Name" to productBean?.name,
            "Product ID" to productBean?.id,
            "Category" to productBean?.category_id,
            "Product Price" to productBean?.price,
            "Quantity" to productBean?.quantity,
            "Stock Availability" to productBean?.availability
        )

        cleverTapDefaultInstance?.pushEvent("card_added_event", prodViewedAction)

        addToCart(childPosition, productBean)
    }

    override fun onProdDelete(
        productBean: ProductDataBean?,
        parentPosition: Int,
        childPosition: Int,
        isOpen: Boolean
    ) {
        this.parentPos = parentPosition
        this.childPos = childPosition

        removeToCart(-1, productBean)
    }

    override fun onProdDetail(productBean: ProductDataBean?) {

        if (clientInform?.isProductDetailCheck != null && clientInform?.isProductDetailCheck == "1") return

        if (clientInform?.show_ecom_v2_theme == "1") {
            navController(this).navigate(
                R.id.actionProductDetailV2,
                ProductDetails.newInstance(productBean, 0, false)
            )
        } else {
            navController(this).navigate(
                R.id.action_productDetail,
                ProductDetails.newInstance(productBean, 0, false)
            )
        }
    }


    override fun onDescExpand(
        tvDesc: TextView?,
        productBean: ProductDataBean?,
        childPosition: Int
    ) {
        /*    if (productBean?.isExpand==true) {
                CommonUtils.collapseTextView(tvDesc, tvDesc?.lineCount)
            } else {*/
        CommonUtils.expandTextView(tvDesc)
        //  }
    }

    override fun getMenuCategory(position: Int) {

        homelytManager?.scrollToPositionWithOffset(position + 2, mSupplierList?.size ?: 0)

        if (popup?.isShowing == true) {
            popup?.dismiss()
        }
    }

    override fun onAddonAdded(productModel: ProductDataBean) {
        if (productModel.adds_on?.isNullOrEmpty() == false && appUtils.checkProdExistance(
                productModel.product_id
            )
        ) {
            val cartList: CartList? = appUtils.getCartList()
            productModel.prod_quantity =
                (cartList?.cartInfos?.filter { productModel.product_id == it.productId }
                    ?.sumByDouble { it.quantity.toDouble() }
                    ?: 0.0).toFloat()
        }


        // productModel.let { mSupplierList?.get(parentPos)?.itemModel?.vendorProdList?.value?.set(childPos, it) }
        refreshProductList(productModel)
    }

    private fun callSupplier(phone: kotlin.String) {
        if (permissionUtil.hasCallPermissions(activity ?: requireContext())) {

            this.phoneNum = phone
            callPhone(phone)

        } else {
            permissionUtil.phoneCallTask(activity ?: requireContext())
        }
    }

    private fun callPhone(number: kotlin.String) {

        val intent = Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", number, null))

        if (ContextCompat.checkSelfPermission(
                activity
                    ?: requireContext(), Manifest.permission.CALL_PHONE
            ) == PackageManager.PERMISSION_GRANTED
        ) {
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

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out kotlin.String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        // EasyPermissions handles the request result.
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this)
    }

    override fun addToCart() {
    }


    override fun reOrder(orderId: ArrayList<Int>?) {
        startActivityForResult(
            Intent(activity, UserTracking::class.java)
                .putExtra(DataNames.REORDER_BUTTON, false)
                .putIntegerArrayListExtra("orderId", orderId), DataNames.REQUEST_REORDER
        )
    }


}
