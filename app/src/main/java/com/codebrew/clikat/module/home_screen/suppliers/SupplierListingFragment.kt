package com.codebrew.clikat.module.home_screen.suppliers

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.TypedValue
import android.view.View
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.NavOptions
import androidx.recyclerview.widget.GridLayoutManager
import com.codebrew.clikat.BR
import com.codebrew.clikat.R
import com.codebrew.clikat.app_utils.AppUtils
import com.codebrew.clikat.app_utils.DateTimeUtils
import com.codebrew.clikat.app_utils.PermissionFile
import com.codebrew.clikat.app_utils.extension.afterTextChanged
import com.codebrew.clikat.app_utils.extension.onSnackbar
import com.codebrew.clikat.app_utils.navController
import com.codebrew.clikat.base.BaseFragment
import com.codebrew.clikat.base.EmptyListListener
import com.codebrew.clikat.data.AppDataType
import com.codebrew.clikat.data.DataManager
import com.codebrew.clikat.data.VendorAppType
import com.codebrew.clikat.data.constants.AppConstants
import com.codebrew.clikat.data.constants.PrefenceConstants
import com.codebrew.clikat.data.model.api.Currency
import com.codebrew.clikat.databinding.FragmentSupplierListBinding
import com.codebrew.clikat.di.ViewModelProviderFactory
import com.codebrew.clikat.modal.SupplierList
import com.codebrew.clikat.modal.other.SettingModel
import com.codebrew.clikat.module.supplier_all.SupplierListNavigator
import com.codebrew.clikat.module.supplier_all.SupplierListViewModel
import com.codebrew.clikat.module.supplier_all.adapter.SupplierAllAdapter
import com.codebrew.clikat.preferences.DataNames
import com.codebrew.clikat.utils.GridSpacingItemDecoration
import com.codebrew.clikat.utils.StaticFunction
import com.codebrew.clikat.utils.Utils
import com.codebrew.clikat.utils.configurations.Configurations
import kotlinx.android.synthetic.main.fragment_supplier_list.*
import kotlinx.android.synthetic.main.layout_bottom_cart.*
import kotlinx.android.synthetic.main.nothing_found.*
import pub.devrel.easypermissions.AppSettingsDialog
import pub.devrel.easypermissions.EasyPermissions
import javax.inject.Inject


class SupplierListingFragment : BaseFragment<FragmentSupplierListBinding, SupplierListViewModel>(),
        SupplierListNavigator, SupplierAllAdapter.SupplierListCallback, EasyPermissions.PermissionCallbacks,EmptyListListener {


    private var categoryId: Int = 0
    private var supplierId: Int = 0
    private var mAdapter: SupplierAllAdapter? = null
    private var supplierList = mutableListOf<SupplierList>()
    private var isFilterApplied = false



    private var clientInform: SettingModel.DataBean.SettingData? = null
    var isFromBranch: Boolean = false

    var supplierBundle: Bundle? = null

    @Inject
    lateinit var dataManager: DataManager

    @Inject
    lateinit var appUtils: AppUtils

    @Inject
    lateinit var permissionUtil: PermissionFile


    @Inject
    lateinit var factory: ViewModelProviderFactory

    private var screenFlowBean: SettingModel.DataBean.ScreenFlowBean? = null

    private lateinit var viewModel: SupplierListViewModel

    private var mBinding: FragmentSupplierListBinding? = null

    private var phoneNum = ""

    private val colorConfig by lazy { Configurations.colors }
    private var selectedCurrency: Currency?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel.navigator = this

        screenFlowBean = dataManager.getGsonValue(DataNames.SCREEN_FLOW, SettingModel.DataBean.ScreenFlowBean::class.java)
        clientInform = dataManager.getGsonValue(DataNames.SETTING_DATA, SettingModel.DataBean.SettingData::class.java)
        selectedCurrency = dataManager.getGsonValue(PrefenceConstants.CURRENCY_INF, Currency::class.java)
        suppplierListObserver()

    }


    override fun getBindingVariable(): Int {
        return BR.viewModel
    }

    override fun getLayoutId(): Int {
        return R.layout.fragment_supplier_list
    }

    override fun getViewModel(): SupplierListViewModel {
        viewModel = ViewModelProviders.of(this, factory).get(SupplierListViewModel::class.java)
        return viewModel
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mBinding = viewDataBinding

        initialise()
        listeners()
        setAdapter()


        if (viewModel.supplierLiveData.value != null) {
            updateSupplierList(viewModel.supplierLiveData.value)
        } else {
            if (isNetworkConnected) {
                apiAllSupplier(categoryId)
            }
        }

        showBottomCart()
    }

    private fun initialise() {
        tvTitle?.text = getString(R.string.burgers)

        arguments?.let {
            categoryId = it.getInt("categoryId", 0)
            supplierId = it.getInt("supplierId", 0)
            isFromBranch = it.getBoolean("isFromBranch", false)
        }

        if(clientInform?.is_skip_theme=="1"){
            tvTitleOoPs?.visibility=View.VISIBLE
            ivPlaceholder?.setImageResource(R.drawable.ic_graphic)
            tvText?.text=getString(R.string.nothing_found_try_again)
            tvText?.setTextColor(ContextCompat.getColor(requireContext(),R.color.colorPrimary))
            tvText?.setTextSize(TypedValue.COMPLEX_UNIT_SP,16f)
        }
    }

    private fun listeners() {
        ivBack?.setOnClickListener {
            hideKeyboard()
            navController(this).popBackStack()
        }
        searchView.afterTextChanged {
            if (it.isEmpty()) {
                hideKeyboard()
            }

            mAdapter?.filter?.filter(it.toLowerCase(DateTimeUtils.timeLocale))
        }

    }

    private fun setAdapter() {

        val layoutManager = GridLayoutManager(requireContext(), 2)

        recyclerView?.layoutManager = layoutManager
        recyclerView?.addItemDecoration(GridSpacingItemDecoration(false, 2, StaticFunction.pxFromDp(16, requireContext()), true))
        mAdapter = SupplierAllAdapter(supplierList, appUtils,this)
        mAdapter?.settingCallback(this)
        mAdapter?.setIsSkipTheme("GRID")
        recyclerView?.adapter = mAdapter
    }


    override fun onErrorOccur(message: String) {
        mBinding?.root?.onSnackbar(message)
    }

    override fun onSessionExpire() {
        openActivityOnTokenExpire()
    }

    override fun onBranchList(supplierList: MutableList<SupplierList>) {
        if (supplierList.count() == 1) {
            supplierBundle?.putString("supplierBranchName", supplierList.firstOrNull()?.name
                    ?: "")
            supplierBundle?.putInt("branchId", supplierList.firstOrNull()?.supplierBranchId
                    ?: 0)

            checkResturant(supplierBundle)
        } else {
            //redirect to new supplier screen
            val list = ArrayList<SupplierList>()
            list.addAll(supplierList)
            supplierBundle?.putParcelableArrayList("branchesList", list)
            navController(this@SupplierListingFragment).navigate(R.id.action_branches_fragment, supplierBundle)
        }
    }

    private fun showBottomCart() {

        val appCartModel = appUtils.getCartData(clientInform,selectedCurrency)

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
                    navController(this@SupplierListingFragment).navigate(R.id.action_supplierAll_to_cartV2, null, navOptions)
                } else {
                    navController(this@SupplierListingFragment).navigate(R.id.action_supplierAll_to_cart, null, navOptions)
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


    private fun suppplierListObserver() {
        // Create the observer which updates the UI.
        val catObserver = Observer<MutableList<SupplierList>> { resource ->

            updateSupplierList(resource)
        }

        // Observe the LiveData, passing in this activity as the LifecycleOwner and the observer.
        viewModel.supplierLiveData.observe(this, catObserver)
    }

    private fun updateSupplierList(resource: MutableList<SupplierList>?) {

        supplierList.clear()

        supplierList.addAll(resource?: mutableListOf())

        supplierList.map { supplierList->
            supplierList.isOpen=appUtils.checkResturntTiming(supplierList.timing)
        }

        supplierList.sortBy { !it.isOpen }
        mAdapter?.settingClientInf(clientInform)
        mAdapter?.notifyDataSetChanged()
    }


    private fun apiAllSupplier(categoryId: Int) {
        val hashMap = dataManager.updateUserInf()

        if (clientInform?.show_tags_for_suppliers == "1" && arguments?.containsKey("tag_id") == true) {
            hashMap["tag_id"] = arguments?.getString("tag_id") ?: ""
        } else {
            if (arguments?.getInt("subCategoryId", 0) ?: 0 > 0) {
                hashMap["subCat"] = arguments?.getInt("subCategoryId", 0).toString()
            }
            if (categoryId > 0) {
                hashMap["categoryId"] = categoryId.toString()
            }
            if (supplierId > 0) {
                hashMap["supplierId"] = "" + supplierId
            }

        }
        viewModel.supplierList(hashMap, isFromBranch)
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

    override fun onSupplierListDetail(supplierBean: SupplierList?) {

        if (supplierBean?.is_multi_branch ?: 0 == 1 && clientInform?.branch_flow == "1") {
            isFromBranch = true
        }

        supplierBundle = bundleOf("categoryId" to categoryId,
                "title" to supplierBean?.name,
                "status" to supplierBean?.status,
                "supplierId" to supplierBean?.id,
                "branchId" to supplierBean?.supplierBranchId,
                "supplierBranchName" to supplierBean?.supplierBranchName,
                "isFromBranch" to isFromBranch,
                "supplierDetail" to supplierBean)


        if (screenFlowBean?.app_type == AppDataType.Food.type) {

            if (supplierBean?.is_multi_branch == 1 && clientInform?.branch_flow == "1") {
                supplierId = supplierBean.id ?: 0
                getAllSupplierBranches(supplierBean.id)
            } else {
                checkResturant(supplierBundle)
            }
        } else {

            if (screenFlowBean?.app_type == AppDataType.HomeServ.type && categoryId != 0 && isFromBranch && arguments?.containsKey("branchId") == false) {
                supplierBundle?.putBoolean("isFromBranch", true)

                navController(this@SupplierListingFragment).navigate(R.id.action_supplierAll_self, supplierBundle)
            } else {

                supplierBundle?.putBoolean("has_subcat", true)
                supplierBundle?.putParcelableArrayList("question_list", arguments?.getParcelableArrayList("question_list"))
                supplierBundle?.putInt("cat_id", arguments?.getInt("subCatId", 0) ?: 0)
                navController(this@SupplierListingFragment).navigate(R.id.action_productListing, supplierBundle)
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

    override fun onBookNow(supplierBean: SupplierList?) {

    }

    override fun onSupplierWishList(supplier: SupplierList?, pos: Int?) {
        if (dataManager.getCurrentUserLoggedIn()) {
            if (isNetworkConnected) {
                supplier?.position = pos
                if (supplier?.Favourite == null || supplier.Favourite == 0)
                    viewModel.markFavSupplier(supplier)
                else
                    viewModel.unFavSupplier(supplier)
            }
        }else {
            appUtils.checkLoginFlow(requireActivity(), -1)
        }
    }

    override fun favSupplierResponse(supplierId: SupplierList?) {
        supplierId?.Favourite = 1
        mBinding?.root?.onSnackbar(getString(R.string.successFavourite))
        if (supplierId?.position != null && supplierId.position != -1)
            mAdapter?.notifyItemChanged(supplierId.position ?: 0)
    }

    override fun unFavSupplierResponse(data: SupplierList?) {
        data?.Favourite = 0
        mBinding?.root?.onSnackbar(getString(R.string.successUnFavourite))
        if (data?.position != null && data.position != -1)
            mAdapter?.notifyItemChanged(data.position ?: 0)
    }

    private fun checkResturant(bundle: Bundle?) {
        if (clientInform?.app_selected_template != null
                && clientInform?.app_selected_template == "1")
            navController(this@SupplierListingFragment)
                    .navigate(R.id.action_resturantDetailNew, bundle)
        else
            navController(this@SupplierListingFragment)
                    .navigate(R.id.action_resturantDetail, bundle)
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

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        // EasyPermissions handles the request result.
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this)
    }

    override fun onEmptyList(count: Int) {
//        viewModel.setSupplierList(count)
    }
}