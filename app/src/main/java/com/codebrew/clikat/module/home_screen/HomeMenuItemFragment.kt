package com.codebrew.clikat.module.home_screen

import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProviders
import com.codebrew.clikat.BR
import com.codebrew.clikat.R
import com.codebrew.clikat.app_utils.AppUtils
import com.codebrew.clikat.app_utils.PermissionFile
import com.codebrew.clikat.app_utils.navController
import com.codebrew.clikat.base.BaseFragment
import com.codebrew.clikat.data.model.api.supplier_detail.DataSupplierDetail
import com.codebrew.clikat.data.network.HostSelectionInterceptor
import com.codebrew.clikat.data.preferences.PreferenceHelper
import com.codebrew.clikat.databinding.FragmentHomeMenuItemBinding
import com.codebrew.clikat.di.ViewModelProviderFactory
import com.codebrew.clikat.modal.other.SupplierInArabicBean
import kotlinx.android.synthetic.main.fragment_home_menu_item.*
import javax.inject.Inject


class HomeMenuItemFragment : BaseFragment<FragmentHomeMenuItemBinding, HomeViewModel>(), HomeNavigator {

    private lateinit var viewModel: HomeViewModel

    @Inject
    lateinit var factory: ViewModelProviderFactory

    @Inject
    lateinit var prefHelper: PreferenceHelper

    @Inject
    lateinit var interceptor: HostSelectionInterceptor

    @Inject
    lateinit var permissionUtil: PermissionFile


    @Inject
    lateinit var appUtils: AppUtils

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.navigator = this
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        ivDel.setOnClickListener {

          //  navController(this@HomeMenuItemFragment).navigate(R.id.action_home_menu_item_to_resturantHomeFrag)
        }
    }

    override fun getBindingVariable(): Int {
        return BR.viewModel
    }

    override fun getLayoutId(): Int {
        return R.layout.fragment_home_menu_item
    }

    override fun getViewModel(): HomeViewModel {
        activity?.let {
            viewModel = ViewModelProviders.of(it, factory).get(HomeViewModel::class.java)
        }

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

    }

    override fun onSessionExpire() {

    }

}