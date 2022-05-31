package com.codebrew.clikat.module.supplier_all


import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BlendMode
import android.graphics.BlendModeColorFilter
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.NavOptions
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.codebrew.clikat.BR
import com.codebrew.clikat.BuildConfig
import com.codebrew.clikat.R
import com.codebrew.clikat.app_utils.AppUtils
import com.codebrew.clikat.app_utils.DateTimeUtils
import com.codebrew.clikat.app_utils.PermissionFile
import com.codebrew.clikat.app_utils.extension.afterTextChanged
import com.codebrew.clikat.app_utils.extension.launchActivity
import com.codebrew.clikat.app_utils.extension.loadPlaceHolder
import com.codebrew.clikat.app_utils.extension.onSnackbar
import com.codebrew.clikat.app_utils.navController
import com.codebrew.clikat.base.BaseFragment
import com.codebrew.clikat.data.*
import com.codebrew.clikat.base.EmptyListListener
import com.codebrew.clikat.data.AppDataType
import com.codebrew.clikat.data.DataManager
import com.codebrew.clikat.data.SortBy
import com.codebrew.clikat.data.VendorAppType
import com.codebrew.clikat.data.constants.AppConstants
import com.codebrew.clikat.data.constants.PrefenceConstants
import com.codebrew.clikat.data.model.api.Currency
import com.codebrew.clikat.data.preferences.PreferenceHelper
import com.codebrew.clikat.databinding.FragmentAllSupplierBinding
import com.codebrew.clikat.di.ViewModelProviderFactory
import com.codebrew.clikat.modal.AppGlobal
import com.codebrew.clikat.modal.SupplierList
import com.codebrew.clikat.modal.other.SettingModel
import com.codebrew.clikat.modal.other.SortByData
import com.codebrew.clikat.modal.other.TimeDataBean
import com.codebrew.clikat.modal.slots.SupplierSlots
import com.codebrew.clikat.module.cart.SCHEDULE_REQUEST
import com.codebrew.clikat.module.cart.schedule_order.ScheduleOrder
import com.codebrew.clikat.module.dialog_adress.AddressDialogFragment
import com.codebrew.clikat.module.dialog_adress.v2.AddressDialogFragmentV2
import com.codebrew.clikat.module.restaurant_detail.OnTableCapacityListener
import com.codebrew.clikat.module.supplier_all.adapter.SupplierAllAdapter
import com.codebrew.clikat.preferences.DataNames
import com.codebrew.clikat.utils.StaticFunction
import com.codebrew.clikat.utils.Utils
import com.codebrew.clikat.utils.configurations.Configurations
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.android.synthetic.main.fragment_all_supplier.*
import kotlinx.android.synthetic.main.layout_bottom_cart.*
import kotlinx.android.synthetic.main.nothing_found.*
import kotlinx.android.synthetic.main.toolbar_app.tb_back
import kotlinx.android.synthetic.main.toolbar_supplier.*
import pub.devrel.easypermissions.AppSettingsDialog
import pub.devrel.easypermissions.EasyPermissions
import java.io.File
import javax.inject.Inject


/*
 */

class SupplierAll : BaseFragment<FragmentAllSupplierBinding, SupplierListViewModel>(),
    SupplierListNavigator, SupplierAllAdapter.SupplierListCallback,
    EasyPermissions.PermissionCallbacks, OnTableCapacityListener, EmptyListListener {

    private var selectedCurrency: Currency? = null
    private var categoryId: Int = 0
    private var supplierId: Int = 0
    private var mAdapter: SupplierAllAdapter? = null
    private var supplierList = mutableListOf<SupplierList>()
    private var isFilterApplied = false


    private val textConfig by lazy { appUtils.loadAppConfig(0).strings }
    private var clientInform: SettingModel.DataBean.SettingData? = null
    var isFromBranch: Boolean = false
    var self_Pickup = ""
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
        mBinding?.root?.onSnackbar(message)
    }

    override fun onSessionExpire() {
        openActivityOnTokenExpire()
    }

    override fun onBranchList(supplierList: MutableList<SupplierList>) {

        this.supplierList.clear()
        this.supplierList.addAll(supplierList)

        if (this.supplierList.count() == 1) {
            supplierBundle?.putString(
                "supplierBranchName", this.supplierList.firstOrNull()?.name
                    ?: ""
            )
            supplierBundle?.putInt(
                "branchId", this.supplierList.firstOrNull()?.supplierBranchId
                    ?: 0
            )

            checkResturant(supplierBundle)

        } else {
            mAdapter?.settingClientInf(clientInform)
            mAdapter?.notifyDataSetChanged()

        }
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mBinding = viewDataBinding
        mBinding?.color = colorConfig
        mBinding?.strings = textConfig
        Glide.with(this).asGif().load(R.raw.loading_gif).into(mBinding!!.imageLoader);
        tvText.typeface = AppGlobal.semi_bold

        arguments?.let {
            categoryId = it.getInt("categoryId", 0)
            supplierId = it.getInt("supplierId", 0)
            isFromBranch = it.getBoolean("isFromBranch", false)
        }


        mAdapter = SupplierAllAdapter(supplierList, appUtils, this, prefHelper.getLangCode())
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


        if (viewModel.supplierLiveData.value != null) {
            updateSupplierList(viewModel.supplierLiveData.value)
        } else {
            if (isNetworkConnected) {
                apiAllSupplier(categoryId)
            }
        }

        tb_back.setOnClickListener {
            hideKeyboard()
            Navigation.findNavController(view).popBackStack()
        }

        if (clientInform?.show_ecom_v2_theme == "1") {
            searchView?.background = ContextCompat.getDrawable(
                requireActivity(),
                R.drawable.search_home_radius_background
            )
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                searchView.background.colorFilter = BlendModeColorFilter(
                    Color.parseColor(colorConfig.search_background),
                    BlendMode.SRC_ATOP
                )
            }
            searchView.setTextColor(Color.parseColor(colorConfig.search_textcolor))
            searchView.setHintTextColor(Color.parseColor(colorConfig.search_textcolor))

            toolbar_layout?.elevation = 0f
            toolbar_layout?.background = ContextCompat.getDrawable(
                requireActivity(),
                R.drawable.background_toolbar_bottom_radius
            )
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                toolbar_layout?.background?.colorFilter = BlendModeColorFilter(
                    Color.parseColor(colorConfig.toolbarColor),
                    BlendMode.SRC_ATOP
                )
            }
        } else {
            searchView?.background =
                ContextCompat.getDrawable(requireActivity(), R.drawable.shape_supplier_search)
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
            tv_total_price.text = String.format(
                "%s %s %s",
                getString(R.string.total),
                AppConstants.CURRENCY_SYMBOL,
                appCartModel.totalPrice
            )

            tv_total_product.text = getString(
                R.string.total_item_tag,
                Utils.getDecimalPointValue(clientInform, appCartModel.totalCount)
            )


            if (appCartModel.supplierName.isNotEmpty() && screenFlowBean?.is_single_vendor == VendorAppType.Multiple.appType) {
                tv_supplier_name.visibility = View.VISIBLE
                tv_supplier_name.text = getString(R.string.supplier_tag, appCartModel.supplierName)
            } else {
                tv_supplier_name.visibility = View.GONE
            }

            bottom_cart.setOnClickListener { v ->

                val navOptions: NavOptions =
                    if (screenFlowBean?.app_type == AppDataType.Food.type) {
                        NavOptions.Builder()
                            .setPopUpTo(R.id.resturantHomeFrag, false)
                            .build()
                    } else {
                        NavOptions.Builder()
                            .setPopUpTo(R.id.homeFragment, false)
                            .build()
                    }

                if (clientInform?.show_ecom_v2_theme == "") {
                    navController(this@SupplierAll).navigate(
                        R.id.action_supplierAll_to_cartV2,
                        null,
                        navOptions
                    )
                } else {
                    navController(this@SupplierAll).navigate(
                        R.id.action_supplierAll_to_cart,
                        null,
                        navOptions
                    )
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

        screenFlowBean = dataManager.getGsonValue(
            DataNames.SCREEN_FLOW,
            SettingModel.DataBean.ScreenFlowBean::class.java
        )
        clientInform = dataManager.getGsonValue(
            DataNames.SETTING_DATA,
            SettingModel.DataBean.SettingData::class.java
        )
        selectedCurrency =
            dataManager.getGsonValue(PrefenceConstants.CURRENCY_INF, Currency::class.java)
        self_Pickup =
            if (prefHelper.getKeyValue(AppConstants.ROAD_MAP_TYPE, PrefenceConstants.TYPE_STRING)!!
                    .equals("Delivery")
            ) {
                "8"
            } else {
                "9"
            }
        suppplierListObserver()
        tableCapacityObserver()

    }

    private fun suppplierListObserver() {
        // Create the observer which updates the UI.
        val catObserver = Observer<MutableList<SupplierList>> { resource ->
            if (resource.size > 0) {
                noData.visibility = View.GONE
                recyclerview.visibility = View.VISIBLE
            } else {
                noData.visibility = View.VISIBLE
                recyclerview.visibility = View.GONE
            }
            updateSupplierList(resource)
        }

        // Observe the LiveData, passing in this activity as the LifecycleOwner and the observer.
        viewModel.supplierLiveData.observe(this, catObserver)
    }

    private fun updateSupplierList(resource: MutableList<SupplierList>?) {

        resource?.forEachIndexed { index, supplierList ->
            val type = object : TypeToken<List<TimeDataBean>>() {}.type
            val listOfMap: List<TimeDataBean> = Gson().fromJson(supplierList.timings, type)
            supplierList.isOpen = appUtils.checkResturntTiming(listOfMap)
        }

        supplierList.clear()
        supplierList.addAll(resource ?: mutableListOf())
        mAdapter = SupplierAllAdapter(supplierList, appUtils, this, prefHelper.getLangCode())
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
        val hashMap = dataManager.updateUserInf()

        if (clientInform?.show_tags_for_suppliers == "1" && arguments?.containsKey("tag_id") == true) {
            hashMap["tag_id"] = arguments?.getString("tag_id") ?: ""
        } else {
            if (arguments?.getInt("subCategoryId", 0) ?: 0 > 0) {
                hashMap["subCat"] = arguments?.getInt("subCategoryId", 0).toString()
            }

            hashMap["categoryId"] = categoryId.toString()

            if (supplierId > 0) {
                hashMap["supplierId"] = "" + supplierId
            }
            hashMap["sort_by"] = SortBy.SortByRating.sortBy.toString()
            hashMap["skip"] = "0"
            hashMap["limit"] = "1000"
            hashMap["offset"] = ""
            hashMap["search"] = ""
            hashMap["access_token"] = dataManager.getKeyValue(PrefenceConstants.ACCESS_TOKEN, PrefenceConstants.TYPE_STRING)
                    .toString()
        }
        if(self_Pickup.equals("9")) {
            hashMap["self_pickup"] = ""
        }
        /*   if (!(hashMap.containsKey("categoryId"))) {
               viewModel.getAllSupplierList()
           } else*/
        viewModel.supplierList(hashMap, isFromBranch, self_Pickup)
    }


    private fun getAllSupplierBranches(supplierId: Int?) {
        val hashMap = dataManager.updateUserInf()

        if (arguments?.getInt("subCategoryId", 0) ?: 0 > 0) {
            hashMap["subCat"] = arguments?.getInt("subCategoryId", 0).toString()
        }
        if (supplierId ?: 0 > 0) {
            hashMap["supplierId"] = "" + supplierId
        }
        viewModel.supplierList(hashMap, isFromBranch, self_Pickup)
    }

    override fun onSupplierListDetail(supplierBean: SupplierList?) {
        if (supplierBean?.is_multi_branch ?: 0 == 1 && clientInform?.branch_flow == "1") {
            isFromBranch = true
        }
        if (prefHelper.getLangCode().toString().equals("15")) {
            name = supplierBean?.name!!
        } else {
            name = supplierBean?.name!!
        }

        supplierBundle = bundleOf(
            "categoryId" to categoryId,
            "title" to name,
            "status" to supplierBean?.status,
            "supplierId" to supplierBean?.id,
            "branchId" to supplierBean?.supplierBranchId,
            "supplierBranchName" to supplierBean?.supplierBranchName,
            "isFromBranch" to isFromBranch,
            "supplierBannerImage" to supplierBean?.supplierImage,
            "supplierLogo" to supplierBean?.logo,
            "supplierRating" to supplierBean?.rating.toString(),

            "supplierTime" to supplierBean?.deliveryMinTime.toString() + " - " + supplierBean?.deliveryMaxTime.toString() + getString(
                R.string.min
            )
        )

        if (self_Pickup.equals("8")) {
            if (screenFlowBean?.app_type == AppDataType.Food.type) {

                if (supplierBean?.is_multi_branch == 1 && clientInform?.branch_flow == "1") {
                    supplierId = supplierBean.id ?: 0
                    getAllSupplierBranches(supplierBean.id)
                } else {


                    checkResturant(supplierBundle)
                }
            } else {

                if (screenFlowBean?.app_type == AppDataType.HomeServ.type && categoryId != 0 && isFromBranch && arguments?.containsKey(
                        "branchId"
                    ) == false
                ) {
                    supplierBundle?.putBoolean("isFromBranch", true)

                    navController(this@SupplierAll).navigate(
                        R.id.action_supplierAll_self,
                        supplierBundle
                    )
                } else {
                    supplierBundle?.putInt("subCategoryId", arguments?.getInt("subCatId", 0) ?: 0)
                    supplierBundle?.putBoolean("has_subcat", true)
                    supplierBundle?.putParcelableArrayList(
                        "question_list",
                        arguments?.getParcelableArrayList("question_list")
                    )
                    navController(this@SupplierAll).navigate(
                        R.id.action_productListing,
                        supplierBundle
                    )
                }
            }
        } else {
            if (supplierBean?.count_supplier_branch == 1) {
                supplierBundle = bundleOf("supplierId" to supplierBean?.id)
                supplierBundle?.putInt("branchId", supplierBean?.supplierBranchId ?: 0)
                supplierBundle?.putInt("categoryId", categoryId ?: 0)
                supplierBundle?.putString("supplierBannerImage", supplierBean?.supplierImage)
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
                    supplierBean?.deliveryMinTime.toString() + " - " + supplierBean?.deliveryMaxTime.toString() + getString(
                        R.string.min
                    )

                )

                if (clientInform?.is_skip_theme == "1" || BuildConfig.CLIENT_CODE == "skipp_0631") {
                    supplierBundle?.putString("deliveryType", "pickup")
                }

                checkResturant(supplierBundle)
            } else {
                val bundle = bundleOf(
                    "title" to supplierBean?.name,
                    "supplierId" to (supplierBean?.id ?: 0),
                    "subCategoryId" to 0
                )
                navController(this@SupplierAll).navigate(R.id.branchListFragment, bundle)

            }
        }
        /*
                      "SubCategory" -> {
                          bundle.putString("title", list[position].name)
                          bundle.putStringArrayList("categoryFlow", categoryFlow)
                          bundle.putInt("categoryId", categoryId)
                          bundle.putInt("supplierId", list[position].id ?: 0)
                          navController(this@SupplierAll).navigate(R.id.action_supplierAll_to_subCategory, bundle)

                      }
      */
    }

    override fun onCallSupplier(supplierBean: SupplierList?) {
        callSupplier(supplierBean?.supplierPhoneNumber ?: "")
    }

    private var selectedSupplierId: Int? = null
    private var supplierBranchId: Int? = null

    override fun onBookNow(supplierBean: SupplierList?) {
        if (dataManager.getCurrentUserLoggedIn()) {
            selectedSupplierId = supplierBean?.id
            supplierBranchId = supplierBean?.supplierBranchId
            selectedDateTimeForScheduling = null

            if (supplierBean?.is_dine_in == 1 && clientInform?.is_table_booking == "1" && clientInform?.table_book_mac_theme == "1") {
                //   getTableCapacityApi(supplierBean.id?:0,supplierBean.supplierBranchId?:0)
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

    private fun getTableCapacityApi(supplierId: Int, branchId: Int) {
        if (isNetworkConnected)
            viewModel.getTableCapacity(supplierId.toString(), branchId.toString())
    }

    private fun tableCapacityObserver() {
        // Create the observer which updates the UI.
        val observer = Observer<ArrayList<kotlin.String>> { resource ->
            //   StaticFunction.tableCapacityDialog(requireContext(), this)
        }

        // Observe the LiveData, passing in this activity as the LifecycleOwner and the observer.
        viewModel.tableCapacityLiveData.observe(this, observer)
    }

    override fun onTableCapacitySelected(capacity: Int) {
        activity?.launchActivity<ScheduleOrder>(SCHEDULE_REQUEST) {
            putExtra("supplierId", selectedSupplierId.toString())
            putExtra("dineIn", true)
            putExtra("seating_capacity", capacity)
            putExtra("supplierBranchId", supplierBranchId)
        }
    }

    override fun onSupplierWishList(supplier: SupplierList?, pos: Int?) {

    }

    private fun checkResturant(bundle: Bundle?) {
        if (clientInform?.show_supplier_detail == "1") {
            bundle?.putInt("categoryId", categoryId)
            navController(this@SupplierAll)
                .navigate(R.id.supplierDetailFragment, bundle)
        } else if (clientInform?.app_selected_template != null
            && clientInform?.app_selected_template == "1"
        )
            navController(this@SupplierAll)
                .navigate(R.id.action_resturantDetailNew, bundle)
        else {
            //for delivery
            //  navController(this@SupplierAll).navigate(R.id.action_resturantDetail2, bundle)
            //for table
            navController(this@SupplierAll).navigate(R.id.action_resturantDetail, bundle)
        }
    }

    private fun callSupplier(phone: String) {
        if (permissionUtil.hasCallPermissions(activity ?: requireContext())) {

            this.phoneNum = phone
            callPhone(phone)

        } else {
            permissionUtil.phoneCallTask(activity ?: requireContext())
        }
    }

    private fun callPhone(number: String) {

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

    override fun unFavSupplierResponse(data: SupplierList?) {

    }

    override fun favSupplierResponse(supplierId: SupplierList?) {
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == SCHEDULE_REQUEST && resultCode == Activity.RESULT_OK) {
            selectedDateTimeForScheduling = data?.getParcelableExtra("slotDetail")
            if (clientInform?.table_book_mac_theme == "1" && selectedDateTimeForScheduling?.selectedTable != null) {
                AlertDialog.Builder(requireContext())
                    .setMessage(getString(R.string.select_items_to_continue))
                    .setCancelable(false)
                    .setPositiveButton(getString(R.string.ok)) { _, _ ->
                        dataManager.setkeyValue(
                            DataNames.SAVED_TABLE_DATA,
                            Gson().toJson(selectedDateTimeForScheduling)
                        )
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
            navController(this@SupplierAll)
                .navigate(R.id.supplierDetailFragment, bundle)
        } else if (clientInform?.app_selected_template != null
            && clientInform?.app_selected_template == "1"
        )
            navController(this@SupplierAll)
                .navigate(R.id.action_resturantDetailNew, bundle)
        else
            navController(this@SupplierAll)
                .navigate(R.id.action_resturantDetail, bundle)
    }

    override fun onPermissionsDenied(requestCode: Int, perms: MutableList<String>) {
        if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
            AppSettingsDialog.Builder(this).build().show()
        }
    }

    override fun onPermissionsGranted(requestCode: Int, perms: MutableList<String>) {
        if (requestCode == AppConstants.REQUEST_CALL) {
            if (isNetworkConnected) {
                callPhone(phoneNum)
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        // EasyPermissions handles the request result.
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this)
    }

    override fun onEmptyList(count: Int) {
//        viewModel.setSupplierList(count)
    }

}
