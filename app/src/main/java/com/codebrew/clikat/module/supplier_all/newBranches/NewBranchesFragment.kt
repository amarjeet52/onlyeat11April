package com.codebrew.clikat.module.supplier_all.newBranches


import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.codebrew.clikat.BR
import com.codebrew.clikat.R
import com.codebrew.clikat.app_utils.AppUtils
import com.codebrew.clikat.app_utils.PermissionFile
import com.codebrew.clikat.app_utils.extension.onSnackbar
import com.codebrew.clikat.app_utils.navController
import com.codebrew.clikat.base.BaseFragment
import com.codebrew.clikat.base.EmptyListListener
import com.codebrew.clikat.data.AppDataType
import com.codebrew.clikat.data.DataManager
import com.codebrew.clikat.data.constants.AppConstants
import com.codebrew.clikat.databinding.FragmentSupplierBranchesBinding
import com.codebrew.clikat.di.ViewModelProviderFactory
import com.codebrew.clikat.modal.SupplierList
import com.codebrew.clikat.modal.other.SettingModel
import com.codebrew.clikat.module.supplier_all.SupplierListNavigator
import com.codebrew.clikat.module.supplier_all.SupplierListViewModel
import com.codebrew.clikat.module.supplier_all.adapter.SupplierAllAdapter
import com.codebrew.clikat.preferences.DataNames
import com.codebrew.clikat.utils.StaticFunction
import com.codebrew.clikat.utils.configurations.Configurations
import kotlinx.android.synthetic.main.fragment_supplier_branches.*
import pub.devrel.easypermissions.AppSettingsDialog
import pub.devrel.easypermissions.EasyPermissions
import javax.inject.Inject


/*
 * Created by Ankit Jindal on 20/4/16.
 */

class NewBranchesFragment : BaseFragment<FragmentSupplierBranchesBinding, SupplierListViewModel>(),
        SupplierListNavigator, SupplierAllAdapter.SupplierListCallback, EasyPermissions.PermissionCallbacks,EmptyListListener {

    private var categoryId: Int = 0
    private var supplierId: Int = 0
    private var mAdapter: SupplierAllAdapter? = null
    private var supplierList = mutableListOf<SupplierList>()


    private val textConfig by lazy { appUtils.loadAppConfig(0).strings }
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

    private var mBinding: FragmentSupplierBranchesBinding? = null

    private var phoneNum = ""

    private val colorConfig by lazy { Configurations.colors }

    override fun getBindingVariable(): Int {
        return BR.viewModel
    }

    override fun getLayoutId(): Int {
        return R.layout.fragment_supplier_branches
    }

    override fun getViewModel(): SupplierListViewModel {
        viewModel = ViewModelProviders.of(this, factory).get(SupplierListViewModel::class.java)
        return viewModel
    }



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.navigator = this
        screenFlowBean = dataManager.getGsonValue(DataNames.SCREEN_FLOW, SettingModel.DataBean.ScreenFlowBean::class.java)
        clientInform = dataManager.getGsonValue(DataNames.SETTING_DATA, SettingModel.DataBean.SettingData::class.java)

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mBinding = viewDataBinding
        mBinding?.color = colorConfig
        initialise()
        setData()
        listeners()
        setAdapter()

        if (isNetworkConnected) {
            getAllSupplierBranches(supplierId)
        }


    }



    private fun initialise() {
        arguments?.let {
            categoryId = it.getInt("categoryId", 0)
            supplierId = it.getInt("supplierId", 0)
            isFromBranch = it.getBoolean("isFromBranch", false)
        }
    }
    private fun setData() {
        arguments?.let {
            val supplierDetail=it.getParcelable<SupplierList>("supplierDetail")
            tvName?.text=supplierDetail?.name
            tvRating?.text=supplierDetail?.rating.toString()
            tvDistance?.text=getString(R.string.km,supplierDetail?.distance)
            tvTime?.text=getString(R.string.deliver_time_min,supplierDetail?.deliveryMinTime?.toString())
            StaticFunction.loadImage(supplierDetail?.logo,ivImage, false)
        }
    }

    private fun listeners() {
        ivBack?.setOnClickListener {
            hideKeyboard()
            navController(this).popBackStack()
        }
    }
    private fun getAllSupplierBranches(supplierId: Int?) {
        val hashMap = dataManager.updateUserInf()

        if (arguments?.getInt("subCategoryId", 0) ?: 0 > 0) {
            hashMap["subCat"] = arguments?.getInt("subCategoryId", 0).toString()
        }
        if (supplierId ?: 0 > 0) {
            hashMap["supplierId"] = "" + supplierId
        }
        viewModel.supplierList(hashMap, true)
    }

    private fun setAdapter() {
        mAdapter = SupplierAllAdapter(supplierList, appUtils,this)
        mAdapter?.settingCallback(this)
        mAdapter?.settingClientInf(clientInform)
        recyclerView.adapter = mAdapter
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
                "isFromBranch" to isFromBranch)

        checkResturant(supplierBundle)
    }

    override fun onBranchList(supplierList: MutableList<SupplierList>) {
        this.supplierList.clear()
        this.supplierList.addAll(supplierList)

        mAdapter?.notifyDataSetChanged()
    }

    override fun onErrorOccur(message: String) {
        mBinding?.root?.onSnackbar(message)
    }

    override fun onSessionExpire() {
        openActivityOnTokenExpire()
    }


    override fun onCallSupplier(supplierBean: SupplierList?) {
        callSupplier(supplierBean?.supplierPhoneNumber ?: "")
    }

    override fun onBookNow(supplierBean: SupplierList?) {

    }

    override fun onSupplierWishList(supplier: SupplierList?, pos: Int?) {

    }

    private fun checkResturant(bundle: Bundle?) {
        if (clientInform?.app_selected_template != null
                && clientInform?.app_selected_template == "1")
            navController(this@NewBranchesFragment)
                    .navigate(R.id.action_resturantDetailNew, bundle)
        else
            navController(this@NewBranchesFragment)
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
    override fun unFavSupplierResponse(data: SupplierList?) {

    }

    override fun favSupplierResponse(supplierId: SupplierList?) {
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
        viewModel.setSupplierList(count)
    }

}
