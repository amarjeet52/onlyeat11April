package com.codebrew.clikat.module.restaurant_detail


import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.ImageView
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.NavOptions
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.SmoothScroller
import com.bumptech.glide.Glide
import com.clevertap.android.sdk.CleverTapAPI
import com.codebrew.clikat.BR
import com.codebrew.clikat.BuildConfig
import com.codebrew.clikat.R
import com.codebrew.clikat.app_utils.*
import com.codebrew.clikat.app_utils.extension.afterTextChanged
import com.codebrew.clikat.app_utils.extension.launchActivity
import com.codebrew.clikat.app_utils.extension.onSnackbar
import com.codebrew.clikat.base.BaseFragment
import com.codebrew.clikat.base.EmptyListListener
import com.codebrew.clikat.base.PagingResult
import com.codebrew.clikat.data.*
import com.codebrew.clikat.data.constants.AppConstants
import com.codebrew.clikat.data.constants.PrefenceConstants
import com.codebrew.clikat.data.model.api.Currency
import com.codebrew.clikat.data.preferences.PreferenceHelper
import com.codebrew.clikat.databinding.FragmentRestaurantDetailBinding
import com.codebrew.clikat.di.ViewModelProviderFactory
import com.codebrew.clikat.modal.CartList
import com.codebrew.clikat.modal.other.*
import com.codebrew.clikat.modal.slots.SupplierSlots
import com.codebrew.clikat.module.addon_quant.SavedAddon
import com.codebrew.clikat.module.cart.SCHEDULE_REQUEST
import com.codebrew.clikat.module.cart.addproduct.AddProductDialog
import com.codebrew.clikat.module.cart.schedule_order.ScheduleOrder
import com.codebrew.clikat.module.dialog_adress.interfaces.AddressDialogListener
import com.codebrew.clikat.module.product.product_listing.DialogListener
import com.codebrew.clikat.module.product_addon.AddonFragment
import com.codebrew.clikat.module.product_detail.ProductDetails
import com.codebrew.clikat.module.restaurant_detail.adapter.ProdListAdapter
import com.codebrew.clikat.module.restaurant_detail.adapter.RestImagesViewPagerAdapter
import com.codebrew.clikat.module.restaurant_detail.adapter.expandable.ExapandableProdAdapter
import com.codebrew.clikat.module.restaurant_detail.dialog.MenuCategoryDialog
import com.codebrew.clikat.module.restaurant_detail.model.Dataa
import com.codebrew.clikat.module.restaurant_detail.model.Genre
import com.codebrew.clikat.module.restaurant_detail.model.ParentCategoryModel
import com.codebrew.clikat.preferences.DataNames
import com.codebrew.clikat.utils.DialogIntrface
import com.codebrew.clikat.utils.StaticFunction
import com.codebrew.clikat.utils.Utils
import com.codebrew.clikat.utils.configurations.Configurations
import com.google.android.material.tabs.TabLayout
import com.google.gson.Gson
import com.thoughtbot.expandablerecyclerview.listeners.GroupExpandCollapseListener
import com.thoughtbot.expandablerecyclerview.models.ExpandableGroup
import com.thoughtbot.expandablerecyclerview.models.ExpandableListPosition
import kotlinx.android.synthetic.main.fragment_restaurant_detail.*
import kotlinx.android.synthetic.main.fragment_restaurant_detail.bottom_cart
import kotlinx.android.synthetic.main.fragment_restaurant_detail.btnBookTable
import kotlinx.android.synthetic.main.fragment_restaurant_detail.ivMenu
import kotlinx.android.synthetic.main.fragment_restaurant_detail.ivSupplierBanner1
import kotlinx.android.synthetic.main.fragment_restaurant_detail.iv_search_prod
import kotlinx.android.synthetic.main.fragment_restaurant_detail.nested_scrollview
import kotlinx.android.synthetic.main.fragment_restaurant_detail.noData
import kotlinx.android.synthetic.main.fragment_restaurant_detail.rvproductList
import kotlinx.android.synthetic.main.fragment_restaurant_detail.tab_layout
import kotlinx.android.synthetic.main.fragment_restaurant_detail2.*
import kotlinx.android.synthetic.main.layout_bottom_cart.*
import java.text.DecimalFormat
import java.util.*
import javax.inject.Inject
import kotlin.collections.ArrayList


/**
 * A simple [Fragment] subclass.
 */
class RestaurantDetailFrag : BaseFragment<FragmentRestaurantDetailBinding, RestDetailViewModel>(),
        ProdListAdapter.ProdCallback,
        RestDetailNavigator, AddonFragment.AddonCallback,
        AddressDialogListener, DialogIntrface, TabLayout.OnTabSelectedListener,
        AddProductDialog.OnAddProductListener, EmptyListListener, OnMenuCategoryListener,
        ExapandableProdAdapter.ExpandCollapseListener {


    private var selectedCurrency: Currency? = null


    private var productBeans: List<ProductBean> = ArrayList()
    private var categoryList = mutableListOf<ProductBean>()
    private var bookingFlowBean: SettingModel.DataBean.BookingFlowBean? = null

    private var deliveryType = 0
    private var minimum_order_amount_for_free_delivery = 0
    private var promoCodes: List<PromoCode> = ArrayList()
    private var is_auto_apply = 0
    private var is_free_delivery = 0
    private var supplierId = 0
    private var supplierBranchId = 0
    lateinit var productBeanAddOn: ProductDataBean

    @Inject
    lateinit var dataManager: DataManager
    var total_amount = 0.0

    @Inject
    lateinit var factory: ViewModelProviderFactory

    @Inject
    lateinit var appUtils: AppUtils

    @Inject
    lateinit var mDialogsUtil: DialogsUtil


    @Inject
    lateinit var mDataManager: PreferenceHelper


    private var screenFlowBean: SettingModel.DataBean.ScreenFlowBean? = null
    private var settingBean: SettingModel.DataBean.SettingData? = null

    private var mBinding: FragmentRestaurantDetailBinding? = null
    private lateinit var viewModel: RestDetailViewModel

    var Maniaself_Pickup = ""
    private var isResutantOpen: Boolean = false

    private var colorConfig = Configurations.colors

    private var supplierDetail: SupplierDetailBean? = null
    var isFromBranch: Boolean = false
    var supplierBranchName = ""
    var supplier_time = ""
    private val decimalFormat: DecimalFormat = DecimalFormat("0.00")
    private val textConfig by lazy { appUtils.loadAppConfig(0).strings }

    private var selectedDateTimeForScheduling: SupplierSlots? = null

    private var smoothScroller: SmoothScroller? = null

    private var viewPagerAdapter: RestImagesViewPagerAdapter? = null

    private var mExpandAdapter: ExapandableProdAdapter? = null

    private var lytManager: RecyclerView.LayoutManager? = null

    lateinit var imageView: ImageView

    private var counter = 0

    override fun getBindingVariable(): Int {
        return BR.viewModel
    }

    override fun getLayoutId(): Int {
        return R.layout.fragment_restaurant_detail
    }

    override fun getViewModel(): RestDetailViewModel {
        viewModel = ViewModelProviders.of(this, factory).get(RestDetailViewModel::class.java)
        return viewModel
    }

    var cleverTapDefaultInstance: CleverTapAPI? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        cleverTapDefaultInstance = CleverTapAPI.getDefaultInstance(requireActivity())
        viewModel.navigator = this
        settingBean = dataManager.getGsonValue(
                DataNames.SETTING_DATA,
                SettingModel.DataBean.SettingData::class.java
        )
        screenFlowBean = dataManager.getGsonValue(
                DataNames.SCREEN_FLOW,
                SettingModel.DataBean.ScreenFlowBean::class.java
        )

        selectedCurrency =
                dataManager.getGsonValue(PrefenceConstants.CURRENCY_INF, Currency::class.java)

        restDetailObserver()
        restCategoryObserver()
        addOnObserver();


    }

    private fun addOnObserver() {
        // Create the observer which updates the UI.
        val catObserver = Observer<List<Dataa>> { resource ->
            productBeanAddOn.adds_on = resource.get(0).adds_on
            AddonFragment.newInstance(productBeanAddOn, deliveryType, this)
                    .show(childFragmentManager, "addOn")
        }

        viewModel.addOnsLiveData.observe(this, catObserver)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        mBinding = viewDataBinding
        Glide.with(this).asGif().load(R.raw.loading_gif).into(mBinding!!.imageLoader);
        viewDataBinding.color = Configurations.colors
        viewDataBinding.drawables = Configurations.drawables
        viewDataBinding.strings = textConfig
        viewDataBinding.settingData = settingBean
        viewDataBinding.isSupplierRating = settingBean?.is_supplier_rating == "1"

        mBinding?.ivCross?.setOnClickListener {
            rl_discount.visibility = View.GONE
        }
        getValues()
        settingLayout()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            setclickListener()
        }
        showBottomCart()
    }


    private fun restDetailObserver() {
        // Create the observer which updates the UI.
        val catObserver = Observer<PagingResult<SuplierProdListModel>> { resource ->
            resource?.result?.data?.product?.forEachIndexed { index, productBean ->
                this.productBeans = resource?.result?.data?.product!!
                for (i in 0 until productBeans.size) {
                    productBeans.get(i).category_id = i.toString()
                }
                tab_layout.addTab(tab_layout.newTab().setText(productBean.sub_cat_name))
            }
            is_free_delivery = resource?.result?.data?.supplier_detail?.is_free_delivery!!
            minimum_order_amount_for_free_delivery = resource?.result?.data?.supplier_detail?.minimum_order_amount_for_free_delivery!!
            promoCodes = resource?.result?.data?.supplier_detail?.promoCodes!!
            for(i in 0 until promoCodes.size)
            {
                if(promoCodes.get(i).is_auto_apply==1) {
                    is_auto_apply = 1
                    break
                }
            }
            if (Maniaself_Pickup.equals("9")) {
                tv_freeDElivery.visibility = View.GONE
                rl_discount.visibility = View.GONE
            } else {

                if (is_free_delivery == 0) {
                    if (resource?.result?.data?.supplier_detail?.promoCodes?.size == 0) {
                        tv_freeDElivery.visibility = View.GONE
                        rl_discount.visibility = View.GONE
                    } else {
                        var totalAmount = 0.0
                        if (tv_total_price.text.toString().equals("")) {
                            totalAmount = 0.0

                        } else {
                            totalAmount = total_amount

                        }
                        var remainingBalance = 0.0
                        if(is_auto_apply==1) {
                            if (resource?.result?.data?.supplier_detail?.promoCodes?.get(0)?.minPrice?.toDouble()!! > totalAmount) {
                                tv_freeDElivery.visibility = View.VISIBLE
                                rl_discount.visibility = View.VISIBLE
                                remainingBalance = resource?.result?.data?.supplier_detail?.promoCodes?.get(0)?.minPrice?.toDouble()!! - totalAmount
                                var percentage = ""
                                if (resource?.result.data?.supplier_detail?.promoCodes?.get(0)?.discountType == 1) {
                                    percentage = "%"
                                } else {
                                    percentage = ""
                                }
                                tv_freeDElivery.text = requireActivity().getString(R.string.discount_price, resource?.result?.data?.supplier_detail?.promoCodes?.get(0)?.minPrice?.toDouble().toString(),
                                        resource?.result?.data?.supplier_detail?.promoCodes?.get(0)?.discountPrice?.toString() + percentage)
                                tv_discount.text = requireActivity().getString(R.string.discount_price, remainingBalance.toString(),
                                        resource?.result?.data?.supplier_detail?.promoCodes?.get(0)?.discountPrice?.toString() + percentage)

                            } else {
                                tv_freeDElivery.visibility = View.GONE
                                rl_discount.visibility = View.GONE
                            }
                        }
                    }
                } else {
                    if (resource?.result?.data?.supplier_detail?.minimum_order_amount_for_free_delivery == 0) {
                        if (resource?.result?.data?.supplier_detail?.promoCodes?.size == 0) {
                            tv_freeDElivery.visibility = View.GONE
                            rl_discount.visibility = View.GONE
                        } else {
                            var totalAmount = 0.0
                            if (tv_total_price.text.toString().equals("")) {
                                totalAmount = 0.0

                            } else {
                                totalAmount = total_amount

                            }
                            var remainingBalance = 0.0
                            if(is_auto_apply==1) {
                                if (resource?.result?.data?.supplier_detail?.promoCodes?.get(0)?.minPrice?.toDouble()!! > totalAmount) {
                                    tv_freeDElivery.visibility = View.VISIBLE
                                    rl_discount.visibility = View.VISIBLE
                                    var percentage = ""
                                    if (resource?.result.data?.supplier_detail?.promoCodes?.get(0)?.discountType == 1) {
                                        percentage = "%"
                                    } else {
                                        percentage = ""
                                    }
                                    remainingBalance = resource?.result?.data?.supplier_detail?.promoCodes?.get(0)?.minPrice?.toDouble()!! - totalAmount
                                    tv_freeDElivery.text = requireActivity().getString(R.string.discount_price, resource?.result?.data?.supplier_detail?.promoCodes?.get(0)?.minPrice?.toDouble().toString(),
                                            resource?.result?.data?.supplier_detail?.promoCodes?.get(0)?.discountPrice?.toString() + percentage)
                                    tv_discount.text = requireActivity().getString(R.string.discount_price, remainingBalance.toString(),
                                            resource?.result?.data?.supplier_detail?.promoCodes?.get(0)?.discountPrice?.toString() + percentage)

                                } else {
                                    tv_freeDElivery.visibility = View.GONE
                                    rl_discount.visibility = View.GONE
                                }
                            }
                        }
                    }
                    else {
                        tv_freeDElivery.text = requireActivity().getString(R.string.free_del_text, resource?.result?.data?.supplier_detail?.minimum_order_amount_for_free_delivery.toString())
                        tv_freeDElivery.visibility = View.VISIBLE
                        if (resource?.result?.data?.supplier_detail?.promoCodes?.size == 0) {
                            tv_freeDElivery.visibility = View.VISIBLE
                            rl_discount.visibility = View.GONE
                        } else {
                            var totalAmount = 0.0
                            if (tv_total_price.text.toString().equals("")) {
                                totalAmount = 0.0

                            } else {
                                totalAmount = total_amount

                            }
                            var remainingBalance = 0.0
                            if(is_auto_apply==1) {
                                if (resource?.result?.data?.supplier_detail?.promoCodes?.get(0)?.minPrice?.toDouble()!! > totalAmount) {
                                    rl_discount.visibility = View.VISIBLE
                                    var percentage = ""
                                    if (resource?.result.data?.supplier_detail?.promoCodes?.get(0)?.discountType == 1) {
                                        percentage = "%"
                                    } else {
                                        percentage = ""
                                    }
                                    remainingBalance = resource?.result?.data?.supplier_detail?.promoCodes?.get(0)?.minPrice?.toDouble()!! - totalAmount
                                    tv_discount.text = requireActivity().getString(R.string.discount_price, remainingBalance.toString(),
                                            resource?.result?.data?.supplier_detail?.promoCodes?.get(0)?.discountPrice?.toString() + percentage)

                                } else {
                                    rl_discount.visibility = View.GONE
                                }
                            }
                        }
                    }
                }
            }

            validateSupplierData(
                    resource?.result?.data?.copy()?.product,
                    resource?.result?.data?.copy()
            )

            if (resource.isFirstPage) {
                resource?.result?.data?.supplier_detail?.let { settingSupplierDetail(it) }
            }
        }

        // Observe the LiveData, passing in this activity as the LifecycleOwner and the observer.
        viewModel.supplierLiveData.observe(this, catObserver)
    }

    private fun validateSupplierData(product: List<ProductBean>?, result: DataBean?) {
        val productList = mutableListOf<Genre>()


        product?.map { prod ->
            prod.is_SubCat_visible = true
            prod.value?.map {
                it.prod_quantity = StaticFunction.getCartQuantity(requireActivity(), it.product_id)
                it.netPrice =
                        if (it.fixed_price?.toFloatOrNull() ?: 0.0f > 0) it.fixed_price?.toFloatOrNull() else 0f
                it.productSpecialInstructions =
                        StaticFunction.getCartSpecialInstructions(requireActivity(), it.product_id)
            }
        }

        product?.forEachIndexed { index, productBean ->
            val categoryFormat = Gson().toJson(
                    ParentCategoryModel(
                            productBean.copy().sub_cat_name
                                    ?: "", productBean.copy().value?.get(0)?.cate_image ?: ""
                    )
            )

            productList.add(Genre(categoryFormat, productBean.copy().value?.toList()))
        }

        lytManager = LinearLayoutManager(activity, RecyclerView.VERTICAL, false)

        mExpandAdapter = ExapandableProdAdapter(
                this,
                appUtils.checkResturntTiming(result?.supplier_detail?.timing),
                productList,
                settingBean,
                selectedCurrency,
                textConfig,
                colorConfig,
                gson = Gson(),
                mFrag = this,
                langCode = dataManager.getLangCode()
        )

        rvproductList.layoutManager = lytManager

        mExpandAdapter?.setOnGroupExpandCollapseListener(object : GroupExpandCollapseListener {
            override fun onGroupExpanded(group: ExpandableGroup<*>?) {
                rvproductList.smoothScrollToPosition(
                        mExpandAdapter?.getGroupPositionWhenExpand(
                                group
                        ) ?: 0
                )
            }

            override fun onGroupCollapsed(group: ExpandableGroup<*>?) {
                rvproductList.smoothScrollToPosition(
                        mExpandAdapter?.getGroupPositionWhenCollapse(
                                group
                        ) ?: 0
                )
            }
        })

        rvproductList.adapter = mExpandAdapter

    }

    fun getFilterList(): List<ExpandableGroup<*>> {
        if (viewModel.supplierLiveData.value == null) return emptyList()

        val product = viewModel.supplierLiveData.value?.result?.data?.product
        val productList = mutableListOf<Genre>()

        product?.map { prod ->
            prod.is_SubCat_visible = true
            prod.value?.map {
                it.prod_quantity = StaticFunction.getCartQuantity(requireActivity(), it.product_id)
                it.netPrice =
                        if (it.fixed_price?.toFloatOrNull() ?: 0.0f > 0) it.fixed_price?.toFloatOrNull() else 0f
                it.productSpecialInstructions =
                        StaticFunction.getCartSpecialInstructions(requireActivity(), it.product_id)
            }
        }

        product?.forEachIndexed { index, productBean ->
            val categoryFormat = Gson().toJson(
                    ParentCategoryModel(
                            productBean.sub_cat_name
                                    ?: "", productBean.value?.get(0)?.cate_image ?: ""
                    )
            )

            productList.add(Genre(categoryFormat, productBean.copy().value?.toList()))
        }

        return productList
    }


    @RequiresApi(Build.VERSION_CODES.M)
    private fun setclickListener() {

        iv_search_prod.afterTextChanged {
            if (it.isNotEmpty()) {
                appbar.setExpanded(false)
                mExpandAdapter?.filter(it.toLowerCase(Locale.ENGLISH))
            } else {
                hideKeyboard()
                appbar.setExpanded(true)
                mExpandAdapter?.filter("")
            }
        }

        iv_search_prod?.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                appbar.setExpanded(false)
                hideKeyboard()
            }
            true
        }
        if (dataManager.getLangCode().equals("14")) {
            ic_back.setImageResource(R.drawable.backnew)
        } else {
            ic_back.setImageResource(R.drawable.backnew_ar)
        }
        ic_back.setOnClickListener {
            navController(this@RestaurantDetailFrag).popBackStack()
        }



        ivMenu?.setOnClickListener {
            val catList =
                    if (settingBean?.enable_rest_pagination_category_wise == "1") productBeans else productBeans
            val list = ArrayList<ProductBean>()
            list.addAll(catList)
            MenuCategoryDialog.newInstance(list).show(childFragmentManager, "dialog")
        }

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
//        if (isNetworkConnected)
//            viewModel.getSupplierCategoryList(supplierId.toString())
    }

    private fun restCategoryObserver() {
        // Create the observer which updates the UI.
        val catObserver = Observer<ArrayList<ProductBean>> { resource ->
            categoryList.clear()
            categoryList.addAll(resource)
            categoryList.forEachIndexed { index, productBean ->
//                tab_layout.addTab(tab_layout.newTab().setText(productBean.sub_cat_name))
            }
        }

        // Observe the LiveData, passing in this activity as the LifecycleOwner and the observer.
        viewModel.categoryLiveData.observe(this, catObserver)
    }


    private fun settingLayout() {

        tab_layout.addOnTabSelectedListener(this)

        productBeans = ArrayList()

//        viewPagerAdapter = RestImagesViewPagerAdapter(requireContext(), null)
//        ivSupplierBanner1.adapter = viewPagerAdapter

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
            deliveryType =
                    if (settingBean?.is_skip_theme == "1" || BuildConfig.CLIENT_CODE == "skipp_0631") 1 else deliveryType


            if (arguments?.containsKey("supplierName") == true) {
                supplierBranchName = arguments?.getString("supplierName", "") ?: ""
                tv_name.text = supplierBranchName
            }
            if (arguments?.containsKey("supplierTime") == true) {
                supplier_time = arguments?.getString("supplierTime", "") ?: ""
                if (supplier_time.equals("")) {
                    tv_supplier_inf_crave.visibility = View.GONE
                    txt_supplier_in.visibility = View.GONE
                } else {
                    tv_supplier_inf_crave.text = supplier_time
                }
            }
            if (arguments?.containsKey("supplierBannerImage") == true) {
//                ivSupplierBanner1.loadImage(arguments?.getString("supplierLogo", "")?: "")
//                ivSupplierIcon.loadImage(arguments?.getString("supplierLogo", "")?: "")

                StaticFunction.loadImage(
                        arguments?.getString("supplierBannerImage", "")
                                ?: "", ivSupplierBanner1, false
                )

            }
            if (arguments?.containsKey("supplierRating") == true) {
                if (arguments?.getString("supplierRating") == null || arguments?.getString("supplierRating")
                                .equals("") ||
                        arguments?.getString("supplierRating")
                                .equals("null") || arguments?.getString("supplierRating").equals("0")
                ) {
                    tv_rating_crave.text = "0.0"
                } else {
                    tv_rating_crave.text = arguments?.getString("supplierRating")
                }

            }

            if (arguments?.containsKey("supplierLogo") == true) {
//                ivSupplierBanner1.loadImage(arguments?.getString("supplierLogo", "")?: "")
//                ivSupplierIcon.loadImage(arguments?.getString("supplierLogo", "")?: "")
                StaticFunction.loadImage(
                        arguments?.getString("supplierLogo", "")
                                ?: "", ivSupplierIcon, false
                )

            }

            if (arguments?.containsKey("supplierId") == true) {
                supplierId = arguments?.getInt("supplierId") ?: 0

                if (arguments?.containsKey("branchId") == true) {
                    supplierBranchId = arguments?.getInt("branchId") ?: 0
                }

                getAllSupplierCategories()
                getProductList(supplierId, supplierBranchId, true, null)
            }
        }
    }

    private fun showBottomCart() {

        val appCartModel = appUtils.getCartData(settingBean, selectedCurrency)

        if (appCartModel.cartAvail) {
            bottom_cart.visibility = View.VISIBLE
            tv_total_price.text =
                    getString(R.string.total).plus(" ").plus(AppConstants.CURRENCY_SYMBOL).plus(" ")
                            .plus(appCartModel.totalPrice)
            total_amount = appCartModel.totalPrice.toDouble()
            tv_total_product.text = getString(
                    R.string.total_item_tag,
                    Utils.getDecimalPointValue(settingBean, appCartModel.totalCount)
            ) + " items"
            for(i in 0 until promoCodes.size)
            {
                if(promoCodes.get(i).is_auto_apply==1) {
                    is_auto_apply = 1
                    break
                }
            }

            if (Maniaself_Pickup.equals("9")) {
                tv_freeDElivery.visibility = View.GONE
                rl_discount.visibility = View.GONE
            } else {

                if (is_free_delivery == 0) {
                    if (promoCodes?.size == 0) {
                        tv_freeDElivery.visibility = View.GONE
                        rl_discount.visibility = View.GONE
                    } else {
                        var totalAmount = 0.0
                        if (tv_total_price.text.toString().equals("")) {
                            totalAmount = 0.0

                        } else {
                            totalAmount = appCartModel.totalPrice.toDouble()

                        }
                        var remainingBalance = 0.0
                        if(is_auto_apply==1) {
                            if (promoCodes?.get(0)?.minPrice?.toDouble()!! > totalAmount) {
                                tv_freeDElivery.visibility = View.VISIBLE
                                rl_discount.visibility = View.VISIBLE
                                var percentage = ""
                                if (promoCodes?.get(0)?.discountType == 1) {
                                    percentage = "%"
                                } else {
                                    percentage = ""
                                }
                                remainingBalance = promoCodes?.get(0)?.minPrice?.toDouble()!! - totalAmount
                                tv_freeDElivery.text = requireActivity().getString(R.string.discount_price, promoCodes?.get(0)?.minPrice?.toDouble()!!.toString(),
                                        promoCodes?.get(0)?.discountPrice?.toString() + percentage)
                                tv_discount.text = requireActivity().getString(R.string.discount_price, remainingBalance.toString(),
                                        promoCodes?.get(0)?.discountPrice?.toString() + percentage)

                            } else {
                                tv_freeDElivery.visibility = View.GONE
                                rl_discount.visibility = View.GONE
                            }
                        }
                    }
                } else {
                    if (minimum_order_amount_for_free_delivery == 0) {
                        if (promoCodes?.size == 0) {
                            tv_freeDElivery.visibility = View.VISIBLE
                            rl_discount.visibility = View.GONE
                        } else {
                            var totalAmount = 0.0
                            if (tv_total_price.text.toString().equals("")) {
                                totalAmount = 0.0

                            } else {
                                totalAmount = appCartModel.totalPrice.toDouble()

                            }
                            var remainingBalance = 0.0
                            if(is_auto_apply==1) {
                                if (promoCodes?.get(0)?.minPrice?.toDouble()!! > totalAmount) {
                                    tv_freeDElivery.visibility = View.VISIBLE
                                    rl_discount.visibility = View.VISIBLE
                                    var percentage = ""
                                    if (promoCodes?.get(0)?.discountType == 1) {
                                        percentage = "%"
                                    } else {
                                        percentage = ""
                                    }
                                    remainingBalance = promoCodes?.get(0)?.minPrice?.toDouble()!! - totalAmount
                                    tv_freeDElivery.text = requireActivity().getString(R.string.discount_price, promoCodes?.get(0)?.minPrice?.toDouble()!!.toString(),
                                            promoCodes?.get(0)?.discountPrice?.toString() + percentage)
                                    tv_discount.text = requireActivity().getString(R.string.discount_price, remainingBalance.toString(),
                                            promoCodes?.get(0)?.discountPrice?.toString() + percentage)

                                } else {
                                    tv_freeDElivery.visibility = View.GONE
                                    rl_discount.visibility = View.GONE
                                }
                            }
                        }
                    } else {
                        tv_freeDElivery.text = requireActivity().getString(R.string.free_del_text, minimum_order_amount_for_free_delivery.toString())
                        tv_freeDElivery.visibility = View.VISIBLE
                        if (promoCodes?.size == 0) {
                            rl_discount.visibility = View.GONE
                        } else {
                            var totalAmount = 0.0
                            if (tv_total_price.text.toString().equals("")) {
                                totalAmount = 0.0

                            } else {
                                totalAmount = appCartModel.totalPrice.toDouble()

                            }
                            var remainingBalance = 0.0
                            if(is_auto_apply==1) {
                                if (promoCodes?.get(0)?.minPrice?.toDouble()!! > totalAmount) {
                                    rl_discount.visibility = View.VISIBLE
                                    var percentage = ""
                                    if (promoCodes?.get(0)?.discountType == 1) {
                                        percentage = "%"
                                    } else {
                                        percentage = ""
                                    }
                                    remainingBalance = promoCodes?.get(0)?.minPrice?.toDouble()!! - totalAmount
                                    tv_discount.text = requireActivity().getString(R.string.discount_price, remainingBalance.toString(),
                                            promoCodes?.get(0)?.discountPrice?.toString() + percentage)

                                } else {
//                                tv_freeDElivery.visibility = View.GONE
                                    rl_discount.visibility = View.GONE
                                }
                            }
                        }
                    }
                }
            }

            if (appCartModel.supplierName.isNotEmpty() && screenFlowBean?.is_single_vendor == VendorAppType.Multiple.appType) {
                tv_supplier_name.visibility = View.VISIBLE
                tv_supplier_name.text = getString(
                        R.string.supplier_tag,
                        /*if (dataManager.getLangCode().toString().equals("15")) appCartModel.supplierName else*/
                        appCartModel.supplierName
                )
                //  tv_supplier_inf_crave.text=
            } else {
                tv_supplier_name.visibility = View.GONE
            }

            bottom_cart.setOnClickListener {

                val navOptions: NavOptions = NavOptions.Builder()
                        .setPopUpTo(R.id.restaurantDetailFrag, false)
                        .build()

                navController(this@RestaurantDetailFrag).navigate(
                        R.id.action_restaurantDetailFrag_to_cart,
                        null,
                        navOptions
                )
            }
        } else {
            bottom_cart.visibility = View.GONE
        }

    }

    private fun getValues() {
        bookingFlowBean = dataManager.getGsonValue(
                DataNames.BOOKING_FLOW,
                SettingModel.DataBean.BookingFlowBean::class.java
        )
        iv_search_prod?.hint = textConfig?.what_are_u_looking_for
    }


    private fun getProductList(
            supplierId: Int,
            supplierBranchId: Int = 0,
            isFirstPage: Boolean,
            categoryId: String?
    ) {


        viewModel.showWhiteScreen.set(supplierDetail == null)
        viewModel.isContentProgressBarLoading.set(supplierDetail != null)
        if (dataManager.getKeyValue(
                        AppConstants.ROAD_MAP_TYPE,
                        PrefenceConstants.TYPE_STRING
                ) == AppConstants.ROAD_MAP_TYPE_CRAVE_MANIA
        ) {

            Maniaself_Pickup = "10"
        } else {
            if (dataManager.getKeyValue(
                            AppConstants.ROAD_MAP_TYPE,
                            PrefenceConstants.TYPE_STRING
                    )!!.equals("Delivery")
            ) {
                Maniaself_Pickup = "8"
            } else {
                Maniaself_Pickup = "9"
            }

        }

        if (Maniaself_Pickup.equals("9")) {
            viewModel.getProductList(
                    supplierId.toString(),
                    isFirstPage,
                    settingBean,
                    categoryId,
                    supplier_branch_id = supplierBranchId,
                    is_non_veg = null,
                    showShimmerLoading = supplierDetail == null,
                    type = "1"
            )
        } else {
            viewModel.getProductList(
                    supplierId.toString(),
                    isFirstPage,
                    settingBean,
                    categoryId,
                    supplier_branch_id = supplierBranchId,
                    is_non_veg = null,
                    showShimmerLoading = supplierDetail == null
            )
        }
    }


    private fun settingSupplierDetail(supplier_detail: SupplierDetailBean?) {

        supplierDetail = supplier_detail
        isResutantOpen = appUtils.checkResturntTiming(supplier_detail?.timing)

        deliveryType =
                if (supplier_detail?.is_out_network == 1) FoodAppType.Delivery.foodType else deliveryType

//        StaticFunction.loadImage(supplier_detail?.logo, ivSupplierIcon, false)
//        StaticFunction.loadImage(supplier_detail?.logo, ivSupplierBanner1, false)

        if (isFromBranch) {
            tv_name.text = supplierBranchName
        } else {
            if (mDataManager.getLangCode().toString().equals("15")) {
                tv_name.text = supplier_detail?.name_arabic
            } else
                tv_name.text = supplier_detail?.name
        }


    }


    override fun onProdAdded(
            productBean: ProductDataBean?,
            parentPosition: Int,
            childPosition: Int,
            isOpen: Boolean
    ) {
        if (parentPosition == -1 && childPosition == -1) return
        val prodViewedAction = mapOf(
                "Product Name" to productBean?.name,
                "Product ID" to productBean?.id,
                "Category" to productBean?.category_id,
                "Product Price" to productBean?.price,
                "Quantity" to productBean?.prod_quantity,
                "Stock Availability" to productBean?.availability
        )

        cleverTapDefaultInstance?.pushEvent("card_added_event", prodViewedAction)

        checkEditQuantity(null, productBean, parentPosition, childPosition, isOpen)

    }

    override fun onProductAdded(
            productBean: ProductDataBean?,
            parentPosition: Int,
            childPosition: Int,
            isOpen: Boolean
    ) {
        checkEditQuantity(null, productBean, parentPosition, childPosition, isOpen)
    }

    private fun checkEditQuantity(
            updatedQuantity: Float?,
            productBean: ProductDataBean?,
            parentPosition: Int,
            childPosition: Int,
            isOpen: Boolean
    ) {
        hideKeyboard()

        if (isOpen) {
            productBean?.isOpen = false
            /*mDialogsUtil.openAlertDialog(activity
                    ?: requireContext(), getString(R.string.offline_supplier_tag, textConfig?.supplier),
                    getString(R.string.ok), "", null)
            return*/
        }


        productBean?.type = screenFlowBean?.app_type


        if (screenFlowBean?.is_single_vendor == VendorAppType.Multiple.appType) {
            val cartList: CartList? = dataManager.getGsonValue(DataNames.CART, CartList::class.java)

            if (cartList != null && cartList.cartInfos?.isNotEmpty() == true) {
                if (cartList.cartInfos?.any { it.deliveryType != deliveryType } == true) {
                    appUtils.clearCart()

                }
            }
        }

        if (productBean?.adds_on?.isNotEmpty() == true) {
            val cartList: CartList? = dataManager.getGsonValue(DataNames.CART, CartList::class.java)

            if (appUtils.checkProdExistance(productBean.product_id)) {
                val savedProduct = cartList?.cartInfos?.filter {
                    it.supplierId == supplierId && it.productId == productBean.product_id
                } ?: emptyList()
                SavedAddon.newInstance(productBean, deliveryType, savedProduct, this)
                        .show(childFragmentManager, "savedAddon")
            } else {
                if (dataManager.getLangCode().toString().equals("15")) {

                    productBeanAddOn = productBean
                    var hashMap: HashMap<String, String> = HashMap()
                    hashMap["languageId"] = dataManager.getLangCode()
                    hashMap["product_id"] = productBean?.product_id.toString()
                    viewModel?.getAddOns(hashMap);

                } else {
                    AddonFragment.newInstance(productBean, deliveryType, this)
                            .show(childFragmentManager, "addOn")
                }


            }

        } else {
            if (bookingFlowBean?.vendor_status == 0 && appUtils.checkVendorStatus(
                            productBean?.supplier_id,
                            vendorBranchId = productBean?.supplier_branch_id,
                            branchFlow = settingBean?.branch_flow
                    )
            ) {
                mDialogsUtil.openAlertDialog(activity
                        ?: requireContext(), getString(
                        R.string.clearCart, textConfig?.supplier
                        ?: "", textConfig?.proceed ?: ""
                ), "Yes", "No", object : DialogListener {
                    override fun onSucessListner() {
                        appUtils.clearCart()
                        showBottomCart()
                    }

                    override fun onErrorListener() {
                    }

                })
            } else {
                if (appUtils.checkBookingFlow(requireContext(), productBean?.product_id, null)) {
                    addCart(updatedQuantity, productBean)
                }
            }

            rvproductList.adapter = mExpandAdapter

            showBottomCart()
        }
    }

    override fun onVehicleSelect(id: String, model: String, color: String, plate_no: String) {
        TODO("Not yet implemented")
    }

    override fun onProdDelete(
            productBean: ProductDataBean?,
            parentPosition: Int,
            childPosition: Int,
            isOpen: Boolean
    ) {

        hideKeyboard()

        if (productBean?.adds_on?.isNotEmpty() == true) {
            val cartList: CartList? = dataManager.getGsonValue(DataNames.CART, CartList::class.java)

            val savedProduct = cartList?.cartInfos?.filter {
                it.supplierId == supplierId && it.productId == productBean.product_id
            } ?: emptyList()

            SavedAddon.newInstance(productBean, deliveryType, savedProduct, this)
                    .show(childFragmentManager, "savedAddon")
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
                    StaticFunction.updateCart(
                            activity, productBean?.product_id, quantity, productBean?.price?.toFloat()
                            ?: 0.0f
                    )
                }


            }

            rvproductList.adapter = mExpandAdapter

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
            productBean?.payment_after_confirmation =
                    settingBean?.payment_after_confirmation?.toIntOrNull()
                            ?: 0
        }
        navController(this).navigate(
                R.id.action_restaurantDetailFrag_to_productDetails,
                ProductDetails.newInstance(productBean, 0, false, isResutantOpen)
        )
    }


    override fun onEditQuantity(
            quantity: Float,
            productBean: ProductDataBean?,
            parentPosition: Int,
            childPosition: Int,
            isOpen: Boolean
    ) {
        if (parentPosition == -1 && childPosition == -1) return

        hideKeyboard()
        if (settingBean?.is_decimal_quantity_allowed == "1") {
            checkEditQuantity(quantity, productBean, parentPosition, childPosition, isOpen)
        }
    }


    private fun addCart(updatedQuantity: Float?, productModel: ProductDataBean?) {
        val cartList: CartList? = dataManager.getGsonValue(DataNames.CART, CartList::class.java)

        if (cartList != null && cartList.cartInfos?.size!! > 0) {
            if (cartList.cartInfos?.any { cartList.cartInfos?.get(0)?.deliveryType != it.deliveryType } == true) {
                appUtils.clearCart()

                mBinding?.root?.onSnackbar("Cart Clear Successfully")
            }
        }


        if (isFromBranch) {
            productModel?.supplier_branch_id = supplierBranchId
            productModel?.supplier_name = supplierBranchName
        }


        if (productModel?.prod_quantity == 0f) {

            productModel.prod_quantity =
                    if (settingBean?.is_decimal_quantity_allowed == "1") AppConstants.DECIMAL_INTERVAL else 1f

            productModel.self_pickup = deliveryType

            productModel.type = screenFlowBean?.app_type

            if (AppConstants.APP_SUB_TYPE > AppDataType.Custom.type) {
                productModel.payment_after_confirmation =
                        settingBean?.payment_after_confirmation?.toIntOrNull()
                                ?: 0
            }
            productModel.isOpen = isResutantOpen
            appUtils.addProductDb(
                    activity ?: requireContext(), screenFlowBean?.app_type
                    ?: 0, productModel
            )

        } else {
            var quantity: Float
            if (updatedQuantity == null) {
                quantity = productModel?.prod_quantity ?: 0f

                if (settingBean?.is_decimal_quantity_allowed == "1")
                    quantity =
                            decimalFormat.format((quantity + AppConstants.DECIMAL_INTERVAL)).toFloat()
                else
                    quantity++
            } else
                quantity = updatedQuantity


            val remaingProd = productModel?.quantity?.minus(productModel.purchased_quantity ?: 0f)
                    ?: 0f

            val checkPurchaseLimit = if (settingBean?.enable_item_purchase_limit == "1" &&
                    productModel?.purchase_limit != null && productModel.purchase_limit != 0f
            ) {
                quantity <= productModel.purchase_limit ?: 0f
            } else true




            if (quantity <= remaingProd && checkPurchaseLimit) {
                productModel?.prod_quantity = quantity

                StaticFunction.updateCart(
                        activity, productModel?.product_id, quantity, productModel?.netPrice
                        ?: 0.0f
                )
            } else {
                mBinding?.root?.onSnackbar(getString(R.string.maximum_limit_cart))
            }
        }
    }


    override fun onSuccessListener() {

    }


    override fun onErrorListener() {

    }


    override fun onErrorOccur(message: String) {
        mBinding?.root?.onSnackbar(message)
    }

    override fun onSessionExpire() {
        openActivityOnTokenExpire()
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

        showBottomCart()


        productBeans.mapIndexed { index, productBean ->

            if (productBean.sub_cat_name == productModel.sub_category_name && productBean.value?.indexOf(
                            productModel
                    ) != -1
            ) {
                productModel.let {
                    productBean.value?.set(productBean.value?.indexOf(productModel) ?: 0, it)
                }
            }

        }

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
        collapseMenu(tab?.position ?: 0)
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

    override fun onMenuSelected(position: Int) {
        collapseMenu(position)
        reSelectTabLayout(productBeans[position].category_id)
    }

    private fun collapseMenu(position: Int) {
        try {
            counter = counter++
            mExpandAdapter?.onCollapse()

            if (counter > 1) {
                appbar.setExpanded(false)
            }

            //mExpandAdapter?.onGroupExpanded(position,  viewModel.supplierLiveData.value?.result?.data?.product?.count()?:0)


            rvproductList?.scrollToPosition(mExpandAdapter?.getGroupItemRealPosition(position) ?: 0)
            mExpandAdapter?.toggleGroup(mExpandAdapter?.getAdapterList()?.get(position))
        } catch (e: Exception) {

        }
    }


    private fun reSelectTabLayout(categoryId: String?) {
        val selectedCategory = productBeans.filter { it.category_id == categoryId }

        if (selectedCategory.isNotEmpty()) {
            val selectedIndex = productBeans.indexOf(selectedCategory.first())
            val selectedTab = mBinding?.tabLayout?.getTabAt(selectedIndex)
            mBinding?.tabLayout?.selectTab(selectedTab, true)
        }

    }

    override fun onExpand(position: Int) {
        if (position < productBeans.size)
            reSelectTabLayout(productBeans[position].category_id)
    }

}
