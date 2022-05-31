package com.codebrew.clikat.module.referral_list

import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.RecyclerView.ItemDecoration
import com.codebrew.clikat.BR
import com.codebrew.clikat.R
import com.codebrew.clikat.app_utils.extension.onSnackbar
import com.codebrew.clikat.base.BaseFragment
import com.codebrew.clikat.base.BaseInterface
import com.codebrew.clikat.data.model.api.ReferalData
import com.codebrew.clikat.data.preferences.PreferenceHelper
import com.codebrew.clikat.databinding.ReferralListFragmentBinding
import com.codebrew.clikat.di.ViewModelProviderFactory
import com.codebrew.clikat.module.referral_list.adapter.ReferrListAdapter
import com.codebrew.clikat.utils.configurations.Configurations
import kotlinx.android.synthetic.main.referral_list_fragment.*
import javax.inject.Inject


class ReferralListFragment : BaseFragment<ReferralListFragmentBinding, ReferralListViewModel>(), BaseInterface {

    companion object {
        fun newInstance() = ReferralListFragment()
    }

    private lateinit var viewModel: ReferralListViewModel

    private var mAdapter: ReferrListAdapter? = null

    @Inject
    lateinit var factory: ViewModelProviderFactory

    private var mBinding: ReferralListFragmentBinding? = null

    @Inject
    lateinit var perferenceHelper: PreferenceHelper


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.navigator = this

        referrallistObserver()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mBinding = viewDataBinding
        mBinding?.color = Configurations.colors


        val itemDecoration: ItemDecoration = DividerItemDecoration(activity, DividerItemDecoration.VERTICAL)
        rv_referral_list.addItemDecoration(itemDecoration)

        mAdapter = ReferrListAdapter()

        rv_referral_list.adapter = mAdapter

    }


    override fun onResume() {
        super.onResume()

        if (isNetworkConnected) {
            viewModel.getReferralList()
        }
    }


    override fun getBindingVariable(): Int {
        return BR.viewModel
    }

    override fun getLayoutId(): Int {
        return R.layout.referral_list_fragment
    }

    override fun getViewModel(): ReferralListViewModel {
        viewModel = ViewModelProviders.of(this, factory).get(ReferralListViewModel::class.java)
        return viewModel
    }

    override fun onErrorOccur(message: String) {
        mBinding?.root?.onSnackbar(message)
    }

    override fun onSessionExpire() {
        openActivityOnTokenExpire()
    }

    private fun referrallistObserver() {
        // Create the observer which updates the UI.
        val catObserver = Observer<MutableList<ReferalData>> { resource ->

            mAdapter?.submitMessageList(resource)
        }

        // Observe the LiveData, passing in this activity as the LifecycleOwner and the observer.
        viewModel.referralListLiveData.observe(this, catObserver)

    }


}
