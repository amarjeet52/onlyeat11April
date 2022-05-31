package com.codebrew.clikat.module.agent_listing

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.navArgs
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout.OnRefreshListener
import com.codebrew.clikat.BR
import com.codebrew.clikat.BuildConfig
import com.codebrew.clikat.R
import com.codebrew.clikat.app_utils.AppUtils
import com.codebrew.clikat.app_utils.CommonUtils.Companion.setBaseUrl
import com.codebrew.clikat.app_utils.DateTimeUtils
import com.codebrew.clikat.app_utils.extension.afterTextChanged
import com.codebrew.clikat.app_utils.extension.onSnackbar
import com.codebrew.clikat.app_utils.navController
import com.codebrew.clikat.base.BaseFragment
import com.codebrew.clikat.data.DataManager
import com.codebrew.clikat.data.constants.PrefenceConstants
import com.codebrew.clikat.data.constants.PrefenceConstants.Companion.TYPE_STRING
import com.codebrew.clikat.data.model.api.Currency
import com.codebrew.clikat.data.model.others.AgentCustomParam
import com.codebrew.clikat.databinding.FragmentAgentListBinding
import com.codebrew.clikat.di.ViewModelProviderFactory
import com.codebrew.clikat.modal.agent.DataBean
import com.codebrew.clikat.modal.agent.GetAgentListParam
import com.codebrew.clikat.modal.other.GetAgentListKey
import com.codebrew.clikat.modal.other.ProductDataBean
import com.codebrew.clikat.modal.other.SettingModel
import com.codebrew.clikat.module.agent_listing.AgentListAdapter.AgentCallback
import com.codebrew.clikat.module.restaurant_detail.VideoPlayer
import com.codebrew.clikat.preferences.DataNames
import com.codebrew.clikat.utils.StaticFunction
import com.codebrew.clikat.utils.configurations.Configurations
import kotlinx.android.synthetic.main.fragment_agent_list.*
import kotlinx.android.synthetic.main.nothing_found.*
import kotlinx.android.synthetic.main.toolbar_app.*
import retrofit2.Retrofit
import java.text.SimpleDateFormat
import javax.inject.Inject


class AgentListFragment : BaseFragment<FragmentAgentListBinding, AgentListViewModel>(), AgentListNavigator, OnRefreshListener, AgentCallback {

    private var selectedCurrency: Currency?=null
    private var adapter: AgentListAdapter? = null
    private var agentList = mutableListOf<DataBean>()
    private val productIds: MutableList<String> = ArrayList()
    private var duration = 0

    private lateinit var viewModel: AgentListViewModel

    private var mBinding: FragmentAgentListBinding? = null

    private var settingData: SettingModel.DataBean.SettingData? = null

    @Inject
    lateinit var factory: ViewModelProviderFactory

    @Inject
    lateinit var dataManager: DataManager

    @Inject
    lateinit var appUtil: AppUtils


    @Inject
    lateinit var retrofit: Retrofit

    val textConfig by lazy { appUtil.loadAppConfig(0).strings }

    val argument: AgentListFragmentArgs by navArgs()

    private lateinit var agentData: DataBean
    private var serviceData: ProductDataBean? = null

    override fun getBindingVariable(): Int {
        return BR.viewModel
    }

    override fun getLayoutId(): Int {
        return R.layout.fragment_agent_list
    }

    override fun getViewModel(): AgentListViewModel {
        viewModel = ViewModelProviders.of(this, factory).get(AgentListViewModel::class.java)
        return viewModel
    }

    override fun onValidTimeSlot() {
        if (argument.selectedDate != null && argument.selectedTime != null) {
            val agentSlot = AgentCustomParam()
            agentSlot.agentData = agentData.cbl_user
            agentSlot.serviceDate = argument.selectedDate
            agentSlot.serviceTime = argument.selectedTime
            agentSlot.duration = duration


            val returnIntent = Intent()
            returnIntent.putExtra("agentData", agentSlot)
            returnIntent.putExtra("serviceData", serviceData)
            activity?.setResult(Activity.RESULT_OK, returnIntent)
            activity?.finish()

        } else {
            viewDataBinding.root.onSnackbar(getString(R.string.date_time_msg))
        }
    }

    override fun onErrorOccur(message: String) {
        mBinding?.root?.onSnackbar(message)
    }

    override fun onSessionExpire() {
        openActivityOnTokenExpire()
    }


    //private Calendar calendar = Calendar.getInstance();
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.navigator = this

        settingData = dataManager.getGsonValue(DataNames.SETTING_DATA, SettingModel.DataBean.SettingData::class.java)
        selectedCurrency = dataManager.getGsonValue(PrefenceConstants.CURRENCY_INF, Currency::class.java)
        agentListObserver()

        agentKeyObserver()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mBinding = viewDataBinding
        mBinding?.color = Configurations.colors
        mBinding?.drawables = Configurations.drawables
        mBinding?.strings = textConfig

        intializeLayout()
        hideKeyboard()
        tb_title.text = getString(R.string.select_agent, textConfig?.agents)
        tb_back.setOnClickListener { activity?.finish() }

        tb_icon.visibility = View.VISIBLE
        tb_icon.setImageDrawable(ContextCompat.getDrawable(activity
                ?: requireContext(), R.drawable.ic_calendar))
        tb_icon.mutateBackground(true)
        tb_icon.setBackgroundColor(Color.parseColor("#888B9B"))

        searchView.afterTextChanged {
            adapter?.filter?.filter(it)
        }


        // AgentListFragmentDirections.actionAgentListFragment2ToTimeSlotFragment()

        tb_icon.setOnClickListener {

            val action = AgentListFragmentDirections.actionAgentListFragment2ToTimeSlotFragment(argument.duration, argument.screenType, argument.selectedDate
                    , argument.selectedTime, argument.productIds,serviceData)

            navController(this@AgentListFragment).navigate(action)
        }
    }

    private fun agentKeyObserver() {
        // Create the observer which updates the UI.
        val catObserver = Observer<List<GetAgentListKey.DataBean>> { resource ->

            updateAgentKey(resource)
        }

        // Observe the LiveData, passing in this activity as the LifecycleOwner and the observer.
        viewModel.agentKeyData.observe(this, catObserver)
    }

    private fun agentListObserver() {
        // Create the observer which updates the UI.
        val catObserver = Observer<List<DataBean>> { resource ->

            updateAgentList(resource)
        }

        // Observe the LiveData, passing in this activity as the LifecycleOwner and the observer.
        viewModel.agentList.observe(this, catObserver)
    }

    private fun updateAgentKey(resource: List<GetAgentListKey.DataBean>?) {

        for (dataBean in resource!!) {
            if (dataBean.key == "api_key") {
                dataManager.setkeyValue(DataNames.AGENT_API_KEY, dataBean.value)
            } else if (dataBean.key == "secret_key") {
                dataManager.setkeyValue(DataNames.AGENT_DB_SECRET, dataBean.value)
            }
        }

        if (viewModel.agentList.value != null) {
            updateAgentList(viewModel.agentList.value)
        } else {
            if (isNetworkConnected) getAgentList()
        }
    }


    private fun intializeLayout() {
        if (argument.productIds?.isNotEmpty() == true) {
            productIds.clear()
            productIds.addAll(argument.productIds?.toMutableList() ?: mutableListOf())
        }
        if (argument.duration > 0) {
            duration = argument.duration
        }

        refreshLayout.setOnRefreshListener(this)

        if (settingData?.enable_freelancer_flow == "1" && arguments?.containsKey("serviceData") == true)
            serviceData = arguments?.getParcelable("serviceData")

        //agentList = ArrayList()
        adapter = AgentListAdapter(agentList, settingData,serviceData,selectedCurrency)
        adapter?.settingCallback(this)
        rvAgentList.adapter = adapter



        if (isNetworkConnected) {
            if (viewModel.agentKeyData.value != null) {
                updateAgentKey(viewModel.agentKeyData.value)
            } else {
                viewModel.getAgentKeyList()
            }
        } else {
            StaticFunction.showNoInternetDialog(requireActivity())
        }


    }

    private fun getAgentList() {

        val param = GetAgentListParam()
        param.serviceIds.addAll(productIds)
        param.duration = duration

        if (argument.selectedDate != null && argument.selectedTime != null) {
            param.datetime = appUtil.convertDateOneToAnother(argument.selectedDate + " " + argument.selectedTime,
                    "yyyy-MM-dd HH:mm", "yyyy-MM-dd HH:mm:ss")
        }

        param.offset=SimpleDateFormat("ZZZZZ", DateTimeUtils.timeLocale).format(System.currentTimeMillis())
        //dataManager.setkeyValue(DataNames.DURATION,duration)

        setBaseUrl(BuildConfig.ONBOARD_AGENT_URL, retrofit)
        val headers = HashMap<String, String>()
        headers["api_key"] = dataManager.getKeyValue(DataNames.AGENT_API_KEY, TYPE_STRING).toString()
        headers["secret_key"] = dataManager.getKeyValue(DataNames.AGENT_DB_SECRET, TYPE_STRING).toString()

        if (isNetworkConnected) {
            viewModel.getAgentList(headers, param)
        }
    }

    private fun updateAgentList(resource: List<DataBean>?) {
        setBaseUrl(BuildConfig.BASE_URL, retrofit)
        agentList.clear()

        if (resource?.isNotEmpty() == true) {
            agentList.addAll(resource)
            adapter?.notifyDataSetChanged()
        } else {
            tvText.text = getString(R.string.artist_not_available, textConfig?.agent)
        }
    }

    override fun onRefresh() {
        refreshLayout.isRefreshing = false
        getAgentList()
    }

    override fun getAgentDetail(agentDetail: DataBean?) {
        if(serviceData!=null && settingData?.enable_freelancer_flow=="1")
            serviceData?.agentBufferPrice=agentDetail?.cbl_user_service_pricing?.firstOrNull()?.agentBufferPrice

        val action = AgentListFragmentDirections.actionAgentListFragment2ToSelectAgent2(argument.duration,
                argument.selectedDate, argument.selectedTime, agentDetail?.cbl_user,serviceData)

        navController(this@AgentListFragment).navigate(action)
    }

    override fun onBookAgent(agentDetail: DataBean?) {
        agentDetail.let {
            agentData = it ?: DataBean()
        }
        if(serviceData!=null && settingData?.enable_freelancer_flow=="1")
            serviceData?.agentBufferPrice=agentDetail?.cbl_user_service_pricing?.firstOrNull()?.agentBufferPrice

        if (argument.selectedDate != null && argument.selectedTime != null) {
            if (isNetworkConnected) {
                //yyyy-MM-dd HH:mm:ss
                val hashMap = hashMapOf<String, String>("datetime" to "${argument.selectedDate} ${argument.selectedTime}",
                        "duration" to argument.duration.toString(), "offset" to SimpleDateFormat("ZZZZZ", DateTimeUtils.timeLocale).format(System.currentTimeMillis()),
                        "id" to agentData.cbl_user?.id.toString())
                setBaseUrl(BuildConfig.ONBOARD_AGENT_URL, retrofit)
                viewModel.validAgentSlot(hashMap)
            }
        } else {
            viewDataBinding.root.onSnackbar(getString(R.string.date_time_msg))
        }

    }

    override fun onViewBioUrl(agentDetail: DataBean?) {
        startActivity(Intent(requireContext(), VideoPlayer::class.java).putExtra("link", agentDetail?.cbl_user?.agent_bio_url))
    }

}