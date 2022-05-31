package com.codebrew.clikat.module.home_screen


import android.app.Activity
import android.content.Intent
import android.graphics.BlendMode
import android.graphics.BlendModeColorFilter
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.os.StrictMode
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.NavOptions
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.codebrew.clikat.BR
import com.codebrew.clikat.R
import com.codebrew.clikat.app_utils.AppUtils
import com.codebrew.clikat.app_utils.DateTimeUtils
import com.codebrew.clikat.app_utils.PermissionFile
import com.codebrew.clikat.app_utils.extension.afterTextChanged
import com.codebrew.clikat.app_utils.extension.loadPlaceHolder
import com.codebrew.clikat.app_utils.navController
import com.codebrew.clikat.base.BaseFragment
import com.codebrew.clikat.base.EmptyListListener
import com.codebrew.clikat.base.PagingResult
import com.codebrew.clikat.data.*
import com.codebrew.clikat.data.constants.AppConstants
import com.codebrew.clikat.data.constants.PrefenceConstants
import com.codebrew.clikat.data.model.api.Currency
import com.codebrew.clikat.data.preferences.PreferenceHelper
import com.codebrew.clikat.databinding.FragmentAllSupplierBinding
import com.codebrew.clikat.di.ViewModelProviderFactory
import com.codebrew.clikat.modal.AppGlobal
import com.codebrew.clikat.modal.SupplierList
import com.codebrew.clikat.modal.other.SettingModel
import com.codebrew.clikat.modal.other.SupplierDataBean
import com.codebrew.clikat.modal.other.TimeDataBean
import com.codebrew.clikat.modal.slots.SupplierSlots
import com.codebrew.clikat.module.cart.SCHEDULE_REQUEST
import com.codebrew.clikat.module.home_screen.adapter.BranchAdapter
import com.codebrew.clikat.module.supplier_all.SupplierListNavigator
import com.codebrew.clikat.module.supplier_all.SupplierListViewModel
import com.codebrew.clikat.preferences.DataNames
import com.codebrew.clikat.utils.Utils
import com.codebrew.clikat.utils.configurations.Configurations
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.android.synthetic.main.fragment_all_supplier.*
import kotlinx.android.synthetic.main.layout_bottom_cart.*
import kotlinx.android.synthetic.main.nothing_found.*
import kotlinx.android.synthetic.main.toolbar_app.tb_back
import kotlinx.android.synthetic.main.toolbar_supplier.*
import javax.inject.Inject


/*
 */

class
BranchResturant : BaseFragment<FragmentAllSupplierBinding, SupplierListViewModel>(),
        SupplierListNavigator, BranchAdapter.SupplierListCallback, EmptyListListener {

    private var selectedCurrency: Currency? = null
    private var categoryId: Int = 0
    private var supplierId: Int = 0
    private var mAdapter: BranchAdapter? = null
    private var supplierList = mutableListOf<SupplierDataBean>()
    private var isFilterApplied = false


    private val textConfig by lazy { appUtils.loadAppConfig(0).strings }
    private var clientInform: SettingModel.DataBean.SettingData? = null
    var isFromBranch: Boolean = false

    var supplierBundle: Bundle? = null
    var name = ""

    @Inject
    lateinit var dataManager: DataManager

    @Inject
    lateinit var appUtils: AppUtils

    @Inject
    lateinit var permissionUtil: PermissionFile


    @Inject
    lateinit var factory: ViewModelProviderFactory

    @Inject
    lateinit var prefHelper: PreferenceHelper

    private var screenFlowBean: SettingModel.DataBean.ScreenFlowBean? = null

    private lateinit var viewModel: SupplierListViewModel

    private var mBinding: FragmentAllSupplierBinding? = null

    private var phoneNum = ""

    private val colorConfig by lazy { Configurations.colors }
    private var selectedDateTimeForScheduling: SupplierSlots? = null

    override fun getBindingVariable(): Int {
        return BR.viewModel
    }

    override fun getLayoutId(): Int {
        return R.layout.fragment_all_supplier
    }

    override fun getViewModel(): SupplierListViewModel {
        viewModel = ViewModelProviders.of(this, factory).get(SupplierListViewModel::class.java)
        return viewModel
    }

    override fun onErrorOccur(message: String) {
//        mBinding?.root?.onSnackbar(message)
    }

    override fun onSessionExpire() {
        openActivityOnTokenExpire()
    }

    override fun onBranchList(supplierList: MutableList<SupplierList>) {

    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mBinding = viewDataBinding
        val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
        StrictMode.setThreadPolicy(policy)
        mBinding?.color = colorConfig
        mBinding?.strings = textConfig
        Glide.with(this).asGif().load(R.raw.loading_gif).into(mBinding!!.imageLoader);
        tvText.typeface = AppGlobal.semi_bold

        arguments?.let {
            categoryId = it.getInt("supplierId", 0)
            supplierId = it.getInt("supplierId", 0)
            isFromBranch = it.getBoolean("isFromBranch", false)
        }


        mAdapter = BranchAdapter(supplierList, FoodAppType.Both.foodType, appUtils, this, prefHelper.getLangCode())
        mAdapter?.settingCallback(this)
        var layout_man = LinearLayoutManager(requireActivity())
        recyclerview.layoutManager = layout_man
        recyclerview.adapter = mAdapter

        searchView.afterTextChanged {
            if (it.isEmpty()) {
                hideKeyboard()
            }
            mAdapter?.filter?.filter(it.toLowerCase(DateTimeUtils.timeLocale))
        }


        apiAllSupplier(categoryId)

        tb_back.setOnClickListener {
            hideKeyboard()
            Navigation.findNavController(view).popBackStack()
        }

        if (clientInform?.show_ecom_v2_theme == "1") {
            searchView?.background = ContextCompat.getDrawable(requireActivity(), R.drawable.search_home_radius_background)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                searchView.background.colorFilter = BlendModeColorFilter(Color.parseColor(colorConfig.search_background), BlendMode.SRC_ATOP)
            }
            searchView.setTextColor(Color.parseColor(colorConfig.search_textcolor))
            searchView.setHintTextColor(Color.parseColor(colorConfig.search_textcolor))

            toolbar_layout?.elevation = 0f
            toolbar_layout?.background = ContextCompat.getDrawable(requireActivity(), R.drawable.background_toolbar_bottom_radius)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                toolbar_layout?.background?.colorFilter = BlendModeColorFilter(Color.parseColor(colorConfig.toolbarColor), BlendMode.SRC_ATOP)
            }
        } else {
            searchView?.background = ContextCompat.getDrawable(requireActivity(), R.drawable.shape_supplier_search)
        }

        Utils.loadAppPlaceholder(clientInform?.supplier_listing ?: "")?.let {

            if (it.app?.isNotEmpty() == true) {
                ivPlaceholder.loadPlaceHolder(it.app)
            }

            if (it.message?.isNotEmpty() == true) {
                tvText.text = it.message
            }

        }

        showBottomCart()
    }

    private fun showBottomCart() {

        val appCartModel = appUtils.getCartData(clientInform, selectedCurrency)

        if (screenFlowBean?.app_type == AppDataType.Ecom.type) {
            bottom_cart.visibility = View.GONE
        } else {
            bottom_cart.visibility = View.VISIBLE
        }


        if (appCartModel.cartAvail) {
            tv_total_price.text = String.format("%s %s %s", getString(R.string.total), AppConstants.CURRENCY_SYMBOL, appCartModel.totalPrice)

            tv_total_product.text = getString(R.string.total_item_tag, Utils.getDecimalPointValue(clientInform, appCartModel.totalCount))


            if (appCartModel.supplierName.isNotEmpty() && screenFlowBean?.is_single_vendor == VendorAppType.Multiple.appType) {
                tv_supplier_name.visibility = View.VISIBLE
                tv_supplier_name.text = getString(R.string.supplier_tag, appCartModel.supplierName)
            } else {
                tv_supplier_name.visibility = View.GONE
            }

            bottom_cart.setOnClickListener { v ->

                val navOptions: NavOptions = if (screenFlowBean?.app_type == AppDataType.Food.type) {
                    NavOptions.Builder()
                            .setPopUpTo(R.id.resturantHomeFrag, false)
                            .build()
                } else {
                    NavOptions.Builder()
                            .setPopUpTo(R.id.homeFragment, false)
                            .build()
                }

                if (clientInform?.show_ecom_v2_theme == "") {
                    navController(this@BranchResturant).navigate(R.id.action_supplierAll_to_cartV2, null, navOptions)
                } else {
                    navController(this@BranchResturant).navigate(R.id.action_branch_to_cart, null, navOptions)
                }
            }
        } else {
            bottom_cart.visibility = View.GONE
        }

    }


    override fun onResume() {
        super.onResume()

        if (!isFilterApplied) {
            dataManager.removeValue(DataNames.FILTER)
            isFilterApplied = false
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel.navigator = this

        screenFlowBean = dataManager.getGsonValue(DataNames.SCREEN_FLOW, SettingModel.DataBean.ScreenFlowBean::class.java)
        clientInform = dataManager.getGsonValue(DataNames.SETTING_DATA, SettingModel.DataBean.SettingData::class.java)
        selectedCurrency = dataManager.getGsonValue(PrefenceConstants.CURRENCY_INF, Currency::class.java)
        suppplierListObserver()


    }

    private fun suppplierListObserver() {
        // Create the observer which updates the UI.
        val catObserver = Observer<PagingResult<List<SupplierDataBean>>> { resource ->
            if (resource.result!!.size > 0) {
                noData.visibility = View.GONE
                recyclerview.visibility = View.VISIBLE
            } else {
                noData.visibility = View.VISIBLE
                recyclerview.visibility = View.GONE
            }
            updateSupplierList(resource.result)
        }

        // Observe the LiveData, passing in this activity as the LifecycleOwner and the observer.
        viewModel.branchLiveDataby.observe(this, catObserver)
    }

    private fun updateSupplierList(resource: List<SupplierDataBean>?) {

        supplierList.clear()
        supplierList.addAll(resource ?: mutableListOf())
        mAdapter = BranchAdapter(supplierList, FoodAppType.Both.foodType, appUtils, this, prefHelper.getLangCode())
        mAdapter?.settingCallback(this)
        var layout_man = LinearLayoutManager(requireActivity())
        recyclerview.layoutManager = layout_man
        recyclerview.adapter = mAdapter

        resource?.forEachIndexed { index, supplierList ->
            val type = object : TypeToken<List<TimeDataBean>>() {}.type
            val listOfMap: List<TimeDataBean> = Gson().fromJson(supplierList.timings, type)
            supplierList.isOpen = appUtils.checkResturntTiming(listOfMap)
        }


        supplierList.sortBy { !it.isOpen }
//
        mAdapter?.settingClientInf(clientInform)
//        mAdapter?.notifyDataSetChanged()

    }


    private fun apiAllSupplier(categoryId: Int) {
        val hashMap = HashMap<String, String>()
        hashMap["supplierId"] = "" + supplierId
        viewModel.branchList(hashMap, isFromBranch)
    }


    private fun getAllSupplierBranches(supplierId: Int?) {
        val hashMap = dataManager.updateUserInf()

        if (arguments?.getInt("subCategoryId", 0) ?: 0 > 0) {
            hashMap["subCat"] = arguments?.getInt("subCategoryId", 0).toString()
        }
        if (supplierId ?: 0 > 0) {
            hashMap["supplierId"] = "" + supplierId
        }
        viewModel.supplierList(hashMap, isFromBranch)
    }

    override fun onSupplierListDetail(supplierBean: SupplierDataBean?) {

        if (prefHelper.getLangCode().toString().equals("15")) {
            name = supplierBean?.branch_name_arabic!!
        } else {
            name = supplierBean?.name!!
        }

        supplierBundle = bundleOf("categoryId" to categoryId,
                "title" to name,
                "status" to supplierBean?.status,
                "supplierId" to supplierBean?.supplier_id,
                "branchId" to supplierBean?.id,
                "supplierBranchName" to name,
                "isFromBranch" to isFromBranch,
                "supplierBannerImage" to supplierBean?.supplier_image,
                "supplierLogo" to supplierBean?.supplier_image,
                "supplierTime" to "")

        checkResturant(supplierBundle)


    }


    private var selectedSupplierId: Int? = null
    private var supplierBranchId: Int? = null


    private fun checkResturant(bundle: Bundle?) {

        navController(this@BranchResturant)
                .navigate(R.id.action_resturantDetailFrag, bundle)

    }


    override fun unFavSupplierResponse(data: SupplierList?) {

    }

    override fun favSupplierResponse(supplierId: SupplierList?) {
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == SCHEDULE_REQUEST && resultCode == Activity.RESULT_OK) {
            selectedDateTimeForScheduling = data?.getParcelableExtra("slotDetail")
            if (clientInform?.table_book_mac_theme == "1" && selectedDateTimeForScheduling?.selectedTable != null) {
                AlertDialog.Builder(requireContext()).setMessage(getString(R.string.select_items_to_continue))
                        .setCancelable(false)
                        .setPositiveButton(getString(R.string.ok)) { _, _ ->
                            dataManager.setkeyValue(DataNames.SAVED_TABLE_DATA, Gson().toJson(selectedDateTimeForScheduling))
                            showSupplierDetailScreen()
                        }.show()
            }
        }
    }

    private fun showSupplierDetailScreen() {
        val bundle = Bundle()
        bundle.putInt("supplierId", selectedSupplierId ?: 0)
        bundle.putInt("branchId", supplierBranchId ?: 0)
        bundle.putInt("categoryId", 0)

        if (clientInform?.show_supplier_detail == "1") {
            bundle.putInt("categoryId", categoryId)
            navController(this@BranchResturant)
                    .navigate(R.id.supplierDetailFragment, bundle)
        } else if (clientInform?.app_selected_template != null
                && clientInform?.app_selected_template == "1")
            navController(this@BranchResturant)
                    .navigate(R.id.action_resturantDetailNew, bundle)
        else
            navController(this@BranchResturant)
                    .navigate(R.id.action_resturantDetail, bundle)
    }


    override fun onEmptyList(count: Int) {

    }

}
