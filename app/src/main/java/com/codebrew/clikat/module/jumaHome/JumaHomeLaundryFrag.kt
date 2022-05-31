package com.codebrew.clikat.module.jumaHome

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.codebrew.clikat.BR
import com.codebrew.clikat.R
import com.codebrew.clikat.app_utils.AppUtils
import com.codebrew.clikat.app_utils.PermissionFile
import com.codebrew.clikat.app_utils.extension.loadImage
import com.codebrew.clikat.app_utils.extension.onSnackbar
import com.codebrew.clikat.app_utils.navController
import com.codebrew.clikat.base.BaseFragment
import com.codebrew.clikat.data.AppDataManager
import com.codebrew.clikat.data.constants.AppConstants
import com.codebrew.clikat.data.model.api.supplier_detail.DataSupplierDetail
import com.codebrew.clikat.databinding.FragmentJumaHomeBinding
import com.codebrew.clikat.di.ViewModelProviderFactory
import com.codebrew.clikat.modal.other.SettingModel
import com.codebrew.clikat.modal.other.SettingModel.DataBean.ScreenFlowBean
import com.codebrew.clikat.modal.other.SupplierInArabicBean
import com.codebrew.clikat.module.bottom_navigation.OnNavigationMenuClicked
import com.codebrew.clikat.module.home_screen.HomeNavigator
import com.codebrew.clikat.module.home_screen.HomeViewModel
import com.codebrew.clikat.preferences.DataNames
import com.codebrew.clikat.utils.configurations.Configurations
import kotlinx.android.synthetic.main.fragment_juma_home.*
import pub.devrel.easypermissions.AppSettingsDialog
import pub.devrel.easypermissions.EasyPermissions
import javax.inject.Inject


/**
 * A simple [Fragment] subclass.
 */
class JumaHomeLaundryFrag : BaseFragment<FragmentJumaHomeBinding, HomeViewModel>(), HomeNavigator , EasyPermissions.PermissionCallbacks{


    private var phoneNumber: String=""

    @Inject
    lateinit var dataManager: AppDataManager

    @Inject
    lateinit var factory: ViewModelProviderFactory

    @Inject
    lateinit var appUtils: AppUtils

    private lateinit var viewModel: HomeViewModel

    @Inject
    lateinit var permissionUtil: PermissionFile

    private var mBinding: FragmentJumaHomeBinding? = null
    private var screenFlowBean: ScreenFlowBean? = null
    private var bookingFlowBean: SettingModel.DataBean.BookingFlowBean? = null
    private var settingData: SettingModel.DataBean.SettingData? = null
    private var navigationListeners: OnNavigationMenuClicked? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.navigator = this

    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mBinding = viewDataBinding

        mBinding?.color = Configurations.colors
        mBinding?.drawables = Configurations.drawables

        screenFlowBean = dataManager.getGsonValue(DataNames.SCREEN_FLOW, ScreenFlowBean::class.java)
        bookingFlowBean = dataManager.getGsonValue(DataNames.BOOKING_FLOW, SettingModel.DataBean.BookingFlowBean::class.java)
        settingData = dataManager.getGsonValue(DataNames.SETTING_DATA, SettingModel.DataBean.SettingData::class.java)
        initialise()
        listeners()
    }

    private fun initialise() {
        if(!settingData?.cleanfax_home_title.isNullOrEmpty())
            tvTitle?.text=settingData?.cleanfax_home_title?:""

        if(!settingData?.cleanfax_home_heading.isNullOrEmpty())
            tvSubTitle?.text=settingData?.cleanfax_home_heading?:""

        if(!settingData?.cleanfax_home_image_url.isNullOrEmpty())
            ivSplashLogo?.loadImage(settingData?.cleanfax_home_image_url?:"")
    }

    private fun listeners() {
        phoneNumber=if(!settingData?.clean_fax_phone_number.isNullOrEmpty()) settingData?.clean_fax_phone_number?:"" else "+254797802320"

        ivMenu?.setOnClickListener {
            navigationListeners?.onNavigationMenuChanged()
        }

        ivWhatsApp?.setOnClickListener {
            if (permissionUtil.hasCallPermissions(requireContext())) {
                try {
                    val url = "https://api.whatsapp.com/send?phone=$phoneNumber"
                    val i = Intent(Intent.ACTION_VIEW)
                    i.data = Uri.parse(url)
                    startActivity(i)
                } catch (ex: Exception) {
                    ex.printStackTrace()
                }
            } else {
                permissionUtil.phoneCallTask(requireContext())
            }

        }
        ivCall?.setOnClickListener {
            if (permissionUtil.hasCallPermissions(requireContext())) {
                callPhone()
            } else {
                permissionUtil.phoneCallTask(requireContext())
            }

        }
        tvScheduleText?.setOnClickListener {
            tvSchedule?.callOnClick()
        }

        tvSchedule?.setOnClickListener {
            navController(this).navigate(R.id.action_frag_to_categories)
        }
    }

    private fun callPhone() {

        val intent = Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", phoneNumber, null))
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED) {
            Manifest.permission.CALL_PHONE
            startActivity(intent)
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(arrayOf(Manifest.permission.CALL_PHONE), 1)
            }
        }
    }
    companion object {
        fun newInstance() = JumaHomeLaundryFrag()
    }

    override fun getBindingVariable(): Int {
        return BR.viewModel
    }

    override fun getLayoutId(): Int {
        return R.layout.fragment_juma_home
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnNavigationMenuClicked)
            navigationListeners = context
    }

    override fun getViewModel(): HomeViewModel {
        viewModel = ViewModelProviders.of(this, factory).get(HomeViewModel::class.java)
        return viewModel
    }

    override fun onFavStatus() {
    }

    override fun unFavSupplierResponse(data: SupplierInArabicBean?) {
    }

    override fun favSupplierResponse(supplierId: SupplierInArabicBean?) {
    }

    override fun supplierDetailSuccess(data: DataSupplierDetail) {
    }

    override fun onTableSuccessfullyBooked() {
    }


    override fun onErrorOccur(message: String) {
        mBinding?.root?.onSnackbar(message)
    }

    override fun onSessionExpire() {
        openActivityOnTokenExpire()
    }


    override fun onPermissionsDenied(requestCode: Int, perms: MutableList<String>) {
        if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
            AppSettingsDialog.Builder(this).build().show()
        }
    }

    override fun onPermissionsGranted(requestCode: Int, perms: MutableList<String>) {
        if (requestCode == AppConstants.REQUEST_CODE_LOCATION) {

        }
    }
}