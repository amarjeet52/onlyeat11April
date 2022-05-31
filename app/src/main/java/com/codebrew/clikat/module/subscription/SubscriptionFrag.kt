package com.codebrew.clikat.module.subscription

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.codebrew.clikat.BR
import com.codebrew.clikat.R
import com.codebrew.clikat.app_utils.AppUtils
import com.codebrew.clikat.app_utils.extension.onSnackbar
import com.codebrew.clikat.app_utils.navController
import com.codebrew.clikat.base.BaseFragment
import com.codebrew.clikat.base.BaseInterface
import com.codebrew.clikat.base.PagingResult
import com.codebrew.clikat.data.model.api.SubcripModel
import com.codebrew.clikat.data.preferences.PreferenceHelper
import com.codebrew.clikat.databinding.SubscriptionFragmentBinding
import com.codebrew.clikat.di.ViewModelProviderFactory
import com.codebrew.clikat.modal.other.SettingModel
import com.codebrew.clikat.module.subscription.adapter.BenefitAdapter
import com.codebrew.clikat.module.subscription.adapter.SubcripListAdapter
import com.codebrew.clikat.module.subscription.adapter.SubscripListener
import com.codebrew.clikat.preferences.DataNames
import com.codebrew.clikat.utils.configurations.Configurations
import kotlinx.android.synthetic.main.subscription_fragment.*
import kotlinx.android.synthetic.main.toolbar_subscription.*
import javax.inject.Inject

class SubscriptionFrag : BaseFragment<SubscriptionFragmentBinding, SubscriptionViewModel>(),
        BaseInterface {

    companion object {
        fun newInstance() = SubscriptionFrag()
    }

    private lateinit var viewModel: SubscriptionViewModel

    @Inject
    lateinit var factory: ViewModelProviderFactory

    private var mBinding: SubscriptionFragmentBinding? = null

    @Inject
    lateinit var perferenceHelper: PreferenceHelper

    @Inject
    lateinit var appUtils: AppUtils

    private val textConfig by lazy { appUtils.loadAppConfig(0).strings }

    private lateinit var adapter: SubcripListAdapter

    private lateinit var benefitAdapter: BenefitAdapter

    private var lytManager: RecyclerView.LayoutManager? = null

    private var currentItem: SubcripModel? = null
    private var settingBean: SettingModel.DataBean.SettingData? = null

    override fun getBindingVariable(): Int {
        return BR.viewModel
    }

    override fun getLayoutId(): Int {
        return R.layout.subscription_fragment
    }

    override fun getViewModel(): SubscriptionViewModel {
        viewModel = ViewModelProviders.of(this, factory).get(SubscriptionViewModel::class.java)
        return viewModel
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.navigator = this

        subscripObserver()
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mBinding = viewDataBinding
        Glide.with(this).asGif().load(R.raw.loading_gif).into(mBinding!!.imageLoader);
        mBinding?.color = Configurations.colors
        mBinding?.strings = textConfig
        settingBean = perferenceHelper.getGsonValue(DataNames.SETTING_DATA, SettingModel.DataBean.SettingData::class.java)

        tb_name.text = textConfig?.my_subscription ?: getString(R.string.subscriptions)
        tb_back.setOnClickListener { Navigation.findNavController(requireView()).popBackStack() }

        setAdapter()

        hitApi(true)

        btn_purchase_subcrip.setOnClickListener {
            if(currentItem==null)
            {
                Toast.makeText(context, "No Plan Choosen", Toast.LENGTH_SHORT).show()

            }else {
                val action = SubscriptionFragDirections.actionSubscriptionFragToSubscripDetailFrag(currentItem)
                navController(this@SubscriptionFrag).navigate(action)
            }
        }

        btn_user_subcrip.setOnClickListener {
            val action = SubscriptionFragDirections.actionSubscriptionFragToSubscripDetailFrag(null)
            navController(this@SubscriptionFrag).navigate(action)
        }
    }

    private fun subscripObserver() {
        // Create the observer which updates the UI.
        val catObserver = Observer<PagingResult<MutableList<SubcripModel>>> { resource ->
            updateItemList(resource)
        }

        // Observe the LiveData, passing in this activity as the LifecycleOwner and the observer.
        viewModel.subscripLiveData.observe(this, catObserver)
    }

    private fun updateItemList(resource: PagingResult<MutableList<SubcripModel>>?) {

        //here is a "type": "1" parameter in list 1- weekly, 2 - monthly, 3 - yearly

        resource?.result?.map {
             when (it.type) {
                 1 -> {
                     it.subscription_plan = getString(R.string.subcription_plan_tag, getString(R.string.weekly))
                     it.subscription_plan_short = getString(R.string.per_time, getString(R.string.week))
                 }
                 2 -> {
                     it.subscription_plan = getString(R.string.subcription_plan_tag, getString(R.string.monthly))
                     it.subscription_plan_short = getString(R.string.per_time, getString(R.string.month))
                 }
                else -> {
                    it.subscription_plan = getString(R.string.subcription_plan_tag, getString(R.string.yearly))
                    it.subscription_plan_short=getString(R.string.per_time, getString(R.string.year))
                }
            }
        }

        mBinding?.planActive = resource?.result?.any { it.is_subscribed == 1 }

        adapter.submitItemList(setSelectedItem(0, resource?.result) ?: mutableListOf())
        tvPlanSub?.text=resource?.result?.firstOrNull()?.description
        benefitAdapter.submitItemList(resource?.result?.firstOrNull()?.benefits ?: mutableListOf())
    }


    override fun onErrorOccur(message: String) {
        mBinding?.root?.onSnackbar(message)
    }

    override fun onSessionExpire() {
        openActivityOnTokenExpire()
    }

    private fun onRecyclerViewScrolled() {
        rv_subcrip_list.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val isPagingActive = viewModel.validForPaging()
                //lytManager
                if (!recyclerView.canScrollHorizontally(1) && isPagingActive) {
                    hitApi(false)
                }
            }
        })
    }

    private fun hitApi(isFirstPage: Boolean) {
        if (isNetworkConnected) {
            viewModel.getSubscrpList(isFirstPage)
        }
    }

    private fun setAdapter() {
        adapter = SubcripListAdapter(settingBean)
        adapter.settingCallback(SubscripListener { model, pos ->
            run {
                adapter.submitItemList(setSelectedItem(pos, viewModel.subscripLiveData.value?.result))
                adapter.notifyDataSetChanged()
                benefitAdapter.submitItemList(model.benefits)
                tvPlanSub?.text = model.description
            }
        })
        lytManager = LinearLayoutManager(activity, RecyclerView.HORIZONTAL, false)
        rv_subcrip_list.layoutManager = lytManager
        rv_subcrip_list?.adapter = adapter

        benefitAdapter = BenefitAdapter()
        rv_benefit_list?.adapter = benefitAdapter

        onRecyclerViewScrolled()

    }

    private fun setSelectedItem(pos: Int, result: MutableList<SubcripModel>?): MutableList<SubcripModel>? {
        result?.map {
            it.benefitStatus = false
        }

        val resultItem = result?.getOrNull(pos)
        resultItem?.benefitStatus = true
        resultItem?.let { result.set(pos, it) }
        currentItem = resultItem

        if (currentItem?.is_subscribed == 1) {
            btn_purchase_subcrip.visibility = View.INVISIBLE
        } else {
            btn_purchase_subcrip.visibility = View.VISIBLE
        }

        return result
    }

}