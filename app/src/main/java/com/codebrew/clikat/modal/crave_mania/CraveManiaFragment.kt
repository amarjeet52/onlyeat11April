package com.codebrew.clikat.modal.crave_mania

import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.codebrew.clikat.BR
import com.codebrew.clikat.R
import com.codebrew.clikat.app_utils.extension.onSnackbar
import com.codebrew.clikat.base.BaseFragment
import com.codebrew.clikat.databinding.FragmentCraveManiaBinding
import com.codebrew.clikat.di.ViewModelProviderFactory
import com.codebrew.clikat.module.restaurant_detail.Final
import kotlinx.android.synthetic.main.fragment_crave_mania.*
import javax.inject.Inject

class CraveManiaFragment : BaseFragment<FragmentCraveManiaBinding, CraveManiaViewModel>(), CraveManiaNavigator {
    private lateinit var viewModel: CraveManiaViewModel
    lateinit var craveManiaAdapter: CraveManiaAdapter
    private var craveManiaRestaurantList: ArrayList<Final>? = null

    @Inject
    lateinit var factory: ViewModelProviderFactory
    private var mBinding: FragmentCraveManiaBinding? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mBinding = viewDataBinding
        craveManiaAdapter = CraveManiaAdapter(requireActivity(), craveManiaRestaurantList!!)
        rvChief.layoutManager = LinearLayoutManager(requireActivity())
        rvChief.adapter = craveManiaAdapter
        getCraveManiaRestaurant()
    }

    override fun getBindingVariable(): Int {
        return BR.viewModel
    }

    override fun getLayoutId(): Int {
        return R.layout.fragment_crave_mania
    }

    override fun getViewModel(): CraveManiaViewModel {
        activity?.let {
            viewModel = ViewModelProviders.of(it, factory).get(CraveManiaViewModel::class.java)
        }
        return viewModel
    }


    private fun getCraveManiaRestaurant() {
        viewModel.getCraveManiaRestaurant()
    }

    override fun onSessionExpire() {
        openActivityOnTokenExpire()
    }

    override fun onErrorOccur(it: String) {
        mBinding?.root?.onSnackbar(it)
    }

    override fun onCraveManiaRestaurantResponse(it: CraveManiaResponseData?) {
        if (it?.data != null && it.data?.final != null && it.data?.final?.size!! > 0) {
            craveManiaRestaurantList?.addAll(it.data?.final!!)
            craveManiaAdapter.notifyDataSetChanged()
        }
    }
}