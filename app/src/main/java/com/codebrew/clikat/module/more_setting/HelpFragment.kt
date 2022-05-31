package com.codebrew.clikat.module.more_setting

import android.os.Bundle
import androidx.lifecycle.ViewModelProviders
import com.codebrew.clikat.BR
import com.codebrew.clikat.R
import com.codebrew.clikat.app_utils.extension.onSnackbar
import com.codebrew.clikat.base.BaseFragment
import com.codebrew.clikat.databinding.FragmentMoreSettingBinding
import com.codebrew.clikat.di.ViewModelProviderFactory
import com.codebrew.clikat.module.product.product_listing.DialogListener
import javax.inject.Inject

class HelpFragment:BaseFragment<FragmentMoreSettingBinding, MoreSettingViewModel>(), DialogListener, MoreSettingNavigator{

    private lateinit var viewModel: MoreSettingViewModel
    private var mBinding: FragmentMoreSettingBinding? = null
    @Inject
    lateinit var factory: ViewModelProviderFactory

    override fun getBindingVariable(): Int {
        return BR.viewModel
    }

    override fun getLayoutId(): Int {
        return R.layout.fragment_help
    }

    override fun getViewModel(): MoreSettingViewModel {
        viewModel = ViewModelProviders.of(this, factory).get(MoreSettingViewModel::class.java)
        return viewModel
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.navigator = this
    }

    override fun onSosSuccess() {

    }

    override fun onLogoutSuccess() {

    }

    override fun onErrorOccur(message: String) {
        mBinding?.root?.onSnackbar(message)
    }

    override fun onSessionExpire() {
        openActivityOnTokenExpire()
    }

    override fun onSucessListner() {
        //do nothing
    }

    override fun onErrorListener() {
        //do nothing
    }
}