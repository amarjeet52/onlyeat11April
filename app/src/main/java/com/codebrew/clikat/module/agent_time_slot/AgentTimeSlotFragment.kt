package com.codebrew.clikat.module.agent_time_slot


import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.codebrew.clikat.BR
import com.codebrew.clikat.BuildConfig
import com.codebrew.clikat.R
import com.codebrew.clikat.app_utils.AppUtils
import com.codebrew.clikat.app_utils.CommonUtils
import com.codebrew.clikat.app_utils.DateTimeUtils
import com.codebrew.clikat.app_utils.extension.loadImage
import com.codebrew.clikat.app_utils.extension.loadPlaceHolder
import com.codebrew.clikat.app_utils.extension.onSnackbar
import com.codebrew.clikat.app_utils.navController
import com.codebrew.clikat.base.BaseFragment
import com.codebrew.clikat.base.BaseInterface
import com.codebrew.clikat.data.AppDataManager
import com.codebrew.clikat.data.DataManager
import com.codebrew.clikat.data.model.others.AgentCustomParam
import com.codebrew.clikat.data.network.HostSelectionInterceptor
import com.codebrew.clikat.databinding.FragmentAgentTimeSlotBinding
import com.codebrew.clikat.di.ViewModelProviderFactory
import com.codebrew.clikat.modal.agent.AgentAvailabilityDate
import com.codebrew.clikat.modal.other.GetAgentListKey
import com.codebrew.clikat.modal.other.ProductDataBean
import com.codebrew.clikat.modal.other.SettingModel
import com.codebrew.clikat.modal.other.TimeSlot
import com.codebrew.clikat.module.selectAgent.adapter.TimeSlotAdapter
import com.codebrew.clikat.preferences.DataNames
import com.codebrew.clikat.utils.Utils
import com.codebrew.clikat.utils.configurations.Configurations
import com.google.android.material.tabs.TabLayout
import kotlinx.android.synthetic.main.fragment_agent_time_slot.*
import kotlinx.android.synthetic.main.nothing_found.*
import kotlinx.android.synthetic.main.toolbar_app.*
import retrofit2.Retrofit
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject
import kotlin.collections.HashMap


class AgentTimeSlotFragment : BaseFragment<FragmentAgentTimeSlotBinding, AgentViewModel>(), View.OnClickListener, SwipeRefreshLayout.OnRefreshListener,
        TabLayout.OnTabSelectedListener, TimeSlotAdapter.TimeSlotCallback, BaseInterface {


    @Inject
    lateinit var factory: ViewModelProviderFactory


    private lateinit var viewModel: AgentViewModel

    private var screenType: String? = ""

    private var time24Format = ""
    private var dateYYMMDD = ""

    private var timeSlots: MutableList<TimeSlot>? = null

    private var slotAdapter: TimeSlotAdapter? = null

    private val tempList = ArrayList<AgentAvailabilityDate>()


    @Inject
    lateinit var dataManger: AppDataManager

    @Inject
    lateinit var appUtil: AppUtils


    @Inject
    lateinit var mDateTime: DateTimeUtils

    @Inject
    lateinit var dataManager: DataManager

    @Inject
    lateinit var interceptor: HostSelectionInterceptor

    private val calendar by lazy { Calendar.getInstance() }


    @Inject
    lateinit var retrofit: Retrofit

    private val colorConfig by lazy { Configurations.colors }

    val argument: AgentTimeSlotFragmentArgs by navArgs()

    var settingData: SettingModel.DataBean.SettingData? = null
    private var bookingTimeSlot = ""


    override fun getBindingVariable(): Int {
        return BR.viewModel
    }

    override fun getLayoutId(): Int {
        return R.layout.fragment_agent_time_slot
    }

    override fun getViewModel(): AgentViewModel {
        viewModel = ViewModelProviders.of(this, factory).get(AgentViewModel::class.java)
        return viewModel
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel.navigator = this
        settingData = dataManager.getGsonValue(DataNames.SETTING_DATA, SettingModel.DataBean.SettingData::class.java)
        timeSlotObserver()
        agentKeyObserver()
    }

    private fun timeSlotObserver() {
        // Create the observer which updates the UI.
        val catObserver = Observer<List<String>> { resource ->
            updateAgentSlots(resource)
        }
        // Observe the LiveData, passing in this activity as the LifecycleOwner and the observer.
        viewModel.agentSlotsData.observe(this, catObserver)
    }

    private fun updateAgentSlots(resource: List<String>?) {
        timeSlots?.clear()

        resource?.let { settingTimeSlot(it) }

        CommonUtils.setBaseUrl(BuildConfig.BASE_URL, retrofit)

        ivPlaceholder.visibility = if (resource?.size == 0) View.VISIBLE else View.GONE
        tvText.visibility = if (resource?.size == 0) View.VISIBLE else View.GONE
        rv_timeslot.visibility = if (resource?.size ?: 0 > 0) View.VISIBLE else View.GONE

        slotAdapter?.refreshAdapter("", -1)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        colorConfig.toolbarColor = colorConfig.appBackground
        colorConfig.toolbarText = colorConfig.textAppTitle

        viewDataBinding.color = colorConfig
        viewDataBinding.drawables = Configurations.drawables
        viewDataBinding.strings = appUtil.loadAppConfig(0).strings

        time24Format = ""
        dateYYMMDD = ""

        // barDialog = ProgressBarDialog(activity)

        timeSlots = ArrayList()

        slotAdapter = TimeSlotAdapter(timeSlots)

        slotAdapter?.settingCallback(this)

        rv_timeslot.layoutManager = LinearLayoutManager(activity, RecyclerView.VERTICAL, false)

        rv_timeslot.adapter = slotAdapter

        refreshLayout.setOnRefreshListener(this)
        tabLayout.addOnTabSelectedListener(this)

        btn_book_agent.setOnClickListener(this)

        screenType = argument.screenType

        settingToolbar(view)
        calculateUserDate()

        Utils.loadAppPlaceholder(settingData?.agent_slots_listing ?: "")?.let {

            if (it.app?.isNotEmpty() == true)
            {
                ivPlaceholder.loadPlaceHolder(it.app ?: "")
            }

            if (it.message?.isNotEmpty() == true) {
                tvText.text = it.message
            }

        }
    }


    private fun settingToolbar(view: View) {
        tb_back.setOnClickListener {
            activity?.finish()
        }

        tb_title.text = getString(R.string.pick_uptime)
    }

    override fun onTabReselected(tab: TabLayout.Tab?) {

    }

    override fun onTabUnselected(tab: TabLayout.Tab?) {

    }

    override fun onTabSelected(tab: TabLayout.Tab?) {
        dateYYMMDD = appUtil.convertDateOneToAnother(tab?.position?.let { tempList[it].date.toString() }
                ?: "", "EEE MMM dd HH:mm:ss zzz yyyy", "yyyy-MM-dd") ?: ""

        hitAgentKeyOrSlotsApi()
    }

    override fun onRefresh() {
        refreshLayout.isRefreshing = false

        dateYYMMDD = appUtil.convertDateOneToAnother(tabLayout.selectedTabPosition.let { tempList[it].date.toString() }, "EEE MMM dd HH:mm:ss zzz yyyy", "yyyy-MM-dd")
                ?: ""

        hitAgentKeyOrSlotsApi()
    }


    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.btn_book_agent -> {
                if (dateYYMMDD.isEmpty() || time24Format.isEmpty()) {
                    viewDataBinding.root.onSnackbar(getString(R.string.date_time_msg))
                } else {
                    screenSelection()
                }
            }
        }
    }

    override fun refreshAdapter(type: String?, adapterPosition: Int) {
        slotAdapter?.refreshAdapter(type, adapterPosition)
    }


    private fun screenSelection() {

        val sdf = SimpleDateFormat("yyyy-MM-dd hh:mm aaa", DateTimeUtils.timeLocale)
        val date = sdf.parse("$dateYYMMDD $time24Format")
        val calendar = Calendar.getInstance()
        calendar.time = date ?: Date()


        val cal = Calendar.getInstance()

        if (cal.time.after(calendar.time)) {
            viewDataBinding.root.onSnackbar(getString(R.string.selected_time_alert))
        } else {
            if (settingData?.hideAgentList != null && settingData?.hideAgentList == "1") {
                val agentSlot = AgentCustomParam()
                agentSlot.agentData = null
                agentSlot.serviceDate = dateYYMMDD
                agentSlot.serviceTime = appUtil.convertDateOneToAnother(time24Format, "hh:mm aaa", "HH:mm")
                agentSlot.duration = argument.duration

                val returnIntent = Intent()
                returnIntent.putExtra("agentData", agentSlot)
                if (settingData?.is_laundry_theme=="1" && arguments?.containsKey("isDeliveryDateTime") == true)
                    returnIntent.putExtra("isDeliveryDateTime",true)

                if(settingData?.enable_freelancer_flow=="1")
                    returnIntent.putExtra("serviceData",arguments?.getParcelable<ProductDataBean>("serviceData"))

                activity?.setResult(Activity.RESULT_OK, returnIntent)
                activity?.finish()
            } else {
                val action = AgentTimeSlotFragmentDirections.actionTimeSlotFragmentToAgentListFragment2(argument.duration, argument.screenType, argument.productIds,
                        dateYYMMDD, appUtil.convertDateOneToAnother(time24Format, "hh:mm aaa", "HH:mm")
                        ?: "")
                navController(this@AgentTimeSlotFragment)
                        .navigate(action)
            }
        }

    }

    private fun calculateUserDate() {
        val outputFormat = SimpleDateFormat("EEE, dd MMMM", DateTimeUtils.timeLocale)

        val cal = Calendar.getInstance()

        for (i in 0..30) {
            tempList.add(AgentAvailabilityDate(cal.time, outputFormat.format(cal.time)))
            tabLayout.addTab(tabLayout.newTab().setText(outputFormat.format(cal.time)))

            cal.add(Calendar.DATE, 1)
        }
    }


    private fun hitAgentKeyOrSlotsApi() {
        if (viewModel.agentKeyData.value == null && settingData?.hideAgentList != null && settingData?.hideAgentList == "1")
            viewModel.getAgentKeyList()
        else
            getAgentSlots(dateYYMMDD)
    }

    private fun getAgentSlots(date: String) {

        bookingTimeSlot = date

        CommonUtils.setBaseUrl(BuildConfig.ONBOARD_AGENT_URL, retrofit)

        val param = HashMap<String, String>()
        param["date"] = date
        param["offset"] = SimpleDateFormat("ZZZZZ", DateTimeUtils.timeLocale).format(System.currentTimeMillis())

        /* if(viewModel.agentSlotsData.value!=null)
         {
             updateAgentSlots(viewModel.agentSlotsData.value)
         }else
         {*/
        viewModel.getAgentSlotsList(param)
        //}
    }

    private fun agentKeyObserver() {
        // Create the observer which updates the UI.
        val catObserver = Observer<List<GetAgentListKey.DataBean>> { resource ->
            updateAgentKey(resource)
        }

        // Observe the LiveData, passing in this activity as the LifecycleOwner and the observer.
        viewModel.agentKeyData.observe(this, catObserver)
    }

    private fun updateAgentKey(resource: List<GetAgentListKey.DataBean>?) {
        for (dataBean in resource!!) {
            if (dataBean.key == "api_key") {
                dataManager.setkeyValue(DataNames.AGENT_API_KEY, dataBean.value)
            } else if (dataBean.key == "secret_key") {
                dataManager.setkeyValue(DataNames.AGENT_DB_SECRET, dataBean.value)
            }
        }
        getAgentSlots(dateYYMMDD)
    }

    private fun settingTimeSlot(data: List<String>) {

        var dayofWeek = ""

        var timeSlotPeriod: TimeSlot


        val listHashMap = mutableMapOf<String, List<String>>()

        var timeSlotsList: MutableList<String>

        val startCalendar = Calendar.getInstance()
        val currentCalendar = Calendar.getInstance()

        for (timeSlot in data) {
            startCalendar.clear()
            try {
                startCalendar.time = mDateTime.getCalendarFormat("$bookingTimeSlot $timeSlot", "yyyy-MM-dd HH:mm:ss")?.time
                        ?: Date()
            } catch (e: ParseException) {
                e.printStackTrace()
            }

            when (startCalendar.get(Calendar.HOUR_OF_DAY)) {
                in 0..11 -> dayofWeek = getString(R.string.morning)
                in 12..15 -> dayofWeek = getString(R.string.aftenoon)
                in 16..20 -> dayofWeek = getString(R.string.evening)
                in 21..23 -> dayofWeek = getString(R.string.night)
            }


            timeSlotsList = ArrayList()

            if (currentCalendar.time.before(startCalendar.time)) {

                if (listHashMap.containsKey(dayofWeek)) {

                    listHashMap[dayofWeek]?.let { timeSlotsList.addAll(it) }

                    timeSlotsList.add(appUtil.convertDateOneToAnother(timeSlot, "HH:mm:ss", "hh:mm aaa")
                            ?: "")

                    listHashMap[dayofWeek] = timeSlotsList
                } else {
                    timeSlotsList.add(appUtil.convertDateOneToAnother(timeSlot, "HH:mm:ss", "hh:mm aaa")
                            ?: "")

                    listHashMap[dayofWeek] = timeSlotsList
                }
            }
        }

        if (listHashMap.isEmpty()) {
            if (appUtil.convertDateOneToAnother(bookingTimeSlot, "yyyy-MM-dd", "EEE, dd MMM") == tabLayout.getTabAt(0)?.text) {
                tabLayout.removeTabAt(0)
            }

            /*  getAgentSlots(appUtil.convertDateOneToAnother(tabLayout.getTabAt(0)?.text.toString() + "," + calendar.get(Calendar.YEAR)
                      , "EEE, dd MMM,yyyy", "yyyy-MM-dd") ?: "")*/
        }


        for ((key, value) in listHashMap) {

            timeSlotPeriod = TimeSlot(key, value, true)

            timeSlots?.add(timeSlotPeriod)
        }


    }

    override fun selectTimeSlot(slot: String?) {
        time24Format = slot ?: ""
    }

    override fun onErrorOccur(message: String) {
        viewDataBinding.root.onSnackbar(message)
    }

    override fun onSessionExpire() {
        openActivityOnTokenExpire()
    }


}
