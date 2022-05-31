package com.codebrew.clikat.module.selectAgent

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.Navigation
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout.OnRefreshListener
import com.codebrew.clikat.BR
import com.codebrew.clikat.BuildConfig
import com.codebrew.clikat.R
import com.codebrew.clikat.app_utils.AppUtils
import com.codebrew.clikat.app_utils.CommonUtils.Companion.setBaseUrl
import com.codebrew.clikat.app_utils.DateTimeUtils
import com.codebrew.clikat.app_utils.extension.loadPlaceHolder
import com.codebrew.clikat.app_utils.extension.loadUserImage
import com.codebrew.clikat.app_utils.extension.onSnackbar
import com.codebrew.clikat.base.BaseFragment
import com.codebrew.clikat.data.DataManager
import com.codebrew.clikat.data.model.others.AgentCustomParam
import com.codebrew.clikat.databinding.FragmentSelectAgentBinding
import com.codebrew.clikat.di.ViewModelProviderFactory
import com.codebrew.clikat.modal.agent.AgentAvailabilityDate
import com.codebrew.clikat.modal.agent.AgentDataBean
import com.codebrew.clikat.modal.agent.CblUserBean
import com.codebrew.clikat.modal.agent.DataBean
import com.codebrew.clikat.modal.other.ProductDataBean
import com.codebrew.clikat.modal.other.SettingModel
import com.codebrew.clikat.modal.other.TimeSlot
import com.codebrew.clikat.module.selectAgent.adapter.TimeSlotAdapter
import com.codebrew.clikat.module.selectAgent.adapter.TimeSlotAdapter.TimeSlotCallback
import com.codebrew.clikat.preferences.DataNames
import com.codebrew.clikat.utils.StaticFunction.loadImage
import com.codebrew.clikat.utils.Utils
import com.codebrew.clikat.utils.configurations.Configurations
import com.google.android.material.tabs.TabLayout.OnTabSelectedListener
import com.google.android.material.tabs.TabLayout.Tab
import kotlinx.android.synthetic.main.fragment_select_agent.*
import kotlinx.android.synthetic.main.item_agent_list.*
import kotlinx.android.synthetic.main.nothing_found.*
import kotlinx.android.synthetic.main.toolbar_app.*
import retrofit2.Retrofit
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class SelectAgent : BaseFragment<FragmentSelectAgentBinding, SelectAgentViewModel>(), SelectAgentNavigator, OnTabSelectedListener, OnRefreshListener, TimeSlotCallback {


    private var slotAdapter: TimeSlotAdapter? = null
    private var timeSlots: MutableList<TimeSlot>? = null
    private var userBean: CblUserBean? = null

    private var duration = 0

    @Inject
    lateinit var dataManager: DataManager

    @Inject
    lateinit var retrofit: Retrofit

    @Inject
    lateinit var appUtil: AppUtils

    @Inject
    lateinit var factory: ViewModelProviderFactory

    @Inject
    lateinit var mDateTime: DateTimeUtils

    private var bookingTimeSlot = ""

    private var mBinding: FragmentSelectAgentBinding? = null

    private lateinit var viewModel: SelectAgentViewModel


    val argument: SelectAgentArgs by navArgs()


    private val tempDate = ArrayList<AgentAvailabilityDate>()

    private val textConfig by lazy { appUtil.loadAppConfig(0).strings }

    private var selectedDate = ""
    private var selectedTime = ""

    val dateList: MutableList<AgentAvailabilityDate> = mutableListOf()

    private val calendar by lazy { Calendar.getInstance() }

    private var settingData: SettingModel.DataBean.SettingData? = null

    override fun getBindingVariable(): Int {
        return BR.viewModel
    }

    override fun getLayoutId(): Int {
        return R.layout.fragment_select_agent
    }

    override fun getViewModel(): SelectAgentViewModel {
        viewModel = ViewModelProviders.of(this, factory).get(SelectAgentViewModel::class.java)
        return viewModel
    }

    override fun onValidTimeSlot() {
        setBaseUrl(BuildConfig.BASE_URL, retrofit)
        val agentSlot = AgentCustomParam()
        agentSlot.agentData = userBean
        agentSlot.serviceDate = selectedDate
        agentSlot.serviceTime = selectedTime
        agentSlot.duration = duration

        val returnIntent = Intent()
        returnIntent.putExtra("agentData", agentSlot)
        if (settingData?.enable_freelancer_flow == "1") {
            returnIntent.putExtra("serviceData", arguments?.getParcelable<ProductDataBean>("serviceData"))
        }
        activity?.setResult(Activity.RESULT_OK, returnIntent)
        activity?.finish()

    }

    override fun onErrorOccur(message: String) {
        mBinding?.root?.onSnackbar(message)
    }

    override fun onSessionExpire() {
        openActivityOnTokenExpire()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel.navigator = this

        settingData = dataManager.getGsonValue(DataNames.SETTING_DATA, SettingModel.DataBean.SettingData::class.java)

        userBean = argument.agentData
        duration = argument.duration
        selectedDate = argument.selectedDate ?: ""
        selectedTime = argument.selectedTime ?: ""

        agentAvailObserver()
        agentSlotObserver()
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mBinding = viewDataBinding
        mBinding?.color = Configurations.colors
        mBinding?.drawables = Configurations.drawables
        mBinding?.strings = textConfig
        mBinding?.isAgentRating = settingData?.is_agent_rating=="1"


        intializeLayout()
        settingLayout()
        hideKeyboard()

        tb_title.text = getString(R.string.select_agent, textConfig?.agent)
        tb_back.setOnClickListener { v: View? -> activity?.onBackPressed() }

        book_slot.setOnClickListener {
            // Prefs.with(activity).save(DataNames.AGENT_ID, userBean.id)

            if (selectedDate.isNotEmpty() && selectedTime.isNotEmpty()) {
                if (isNetworkConnected) {
                    //yyyy-MM-dd HH:mm:ss
                    val hashMap = hashMapOf<String, String>("datetime" to "${selectedDate} ${selectedTime}",
                            "duration" to argument.duration.toString(), "offset" to SimpleDateFormat("ZZZZZ", DateTimeUtils.timeLocale).format(System.currentTimeMillis()),
                            "id" to argument.agentData?.id.toString())
                    setBaseUrl(BuildConfig.ONBOARD_AGENT_URL, retrofit)
                    viewModel.validAgentSlot(hashMap)
                }
            } else {
                viewDataBinding.root.onSnackbar(getString(R.string.date_time_msg))
            }

        }
    }


    private fun agentAvailObserver() {
        // Create the observer which updates the UI.
        val catObserver = Observer<MutableList<AgentDataBean>> { resource ->

            setBaseUrl(BuildConfig.BASE_URL, retrofit)
            setAgentDates(resource)
        }

        // Observe the LiveData, passing in this activity as the LifecycleOwner and the observer.
        viewModel.agentAvailList.observe(this, catObserver)
    }


    private fun agentSlotObserver() {
        // Create the observer which updates the UI.
        val catObserver = Observer<List<String>> { resource ->

            setBaseUrl(BuildConfig.BASE_URL, retrofit)
            settingTimeSlot(resource)

            ivPlaceholder.visibility = if (resource.isEmpty()) View.VISIBLE else View.GONE
            tvText.visibility = if (resource.isEmpty()) View.VISIBLE else View.GONE
            rv_timeslot.visibility = if (resource.isNotEmpty()) View.VISIBLE else View.GONE
            slotAdapter?.refreshAdapter("", -1)
            slotAdapter?.notifyDataSetChanged()

        }

        // Observe the LiveData, passing in this activity as the LifecycleOwner and the observer.
        viewModel.agentSlotsData.observe(this, catObserver)
    }


    private fun settingLayout() {
        if(userBean!=null) {
            agentDetail?.visibility=View.VISIBLE
            gp_action.visibility = View.GONE

            iv_userImage.loadUserImage(userBean?.image ?: "")
            if (userBean?.name != null) {
                tv_name.text = userBean?.name
            }
            if (userBean?.occupation != null) {
                tv_occupation.text = userBean?.occupation
            }
                tv_total_reviews.text = getString(R.string.agent_reviews_tag, userBean?.avg_rating?.toFloat()?:0f)

        }else
            agentDetail?.visibility=View.GONE
    }


    private fun intializeLayout() {
        timeSlots = ArrayList()
        slotAdapter = TimeSlotAdapter(timeSlots)
        slotAdapter?.settingCallback(this)
        rv_timeslot.layoutManager = LinearLayoutManager(activity, RecyclerView.VERTICAL, false)
        rv_timeslot.adapter = slotAdapter
        refreshLayout.setOnRefreshListener(this)
        tabLayout.addOnTabSelectedListener(this)
        assert(arguments != null)
        if (arguments?.containsKey("agentDetail") == true) settingUserData(arguments?.getParcelable("agentDetail"))
        agentAvailability()

        Utils.loadAppPlaceholder(settingData?.select_agent_lsiting ?: "")?.let {

            if (it.app?.isNotEmpty() == true)
            {
                ivPlaceholder.loadPlaceHolder(it.app ?: "")
            }

            if (it.message?.isNotEmpty() == true) {
                tvText.text = it.message
            }

        }
    }

    private fun settingUserData(agentDetail: DataBean?) {
        val userBean = agentDetail?.cbl_user
        if (userBean?.image != null) {
            loadImage(userBean.image, iv_userImage, false)
            //Glide.with(getActivity()).load(userBean.getImage()).apply(new RequestOptions().placeholder(R.drawable.placeholder_product)).into(iv_userImage);
        }
        if (userBean?.name != null) {
            tv_name.text = userBean.name
        }
        if (userBean?.occupation != null) {
            tv_occupation.text = userBean.occupation
        }

    }

    override fun onRefresh() {
        refreshLayout.isRefreshing = false

        selectedDate = appUtil.convertDateOneToAnother(tabLayout?.selectedTabPosition?.let {
            dateList[it].date.toString() }
                ?: "", "EEE MMM dd HH:mm:ss zzz yyyy", "yyyy-MM-dd") ?: ""

        val date = appUtil.convertDateOneToAnother(tempDate[tabLayout.selectedTabPosition].date.toString(), "EEE MMM dd HH:mm:ss zzz yyyy", "yyyy-MM-dd")
                ?: ""
        getAgentSlots(date)
    }

    override fun onTabSelected(tab: Tab) {

        selectedDate = appUtil.convertDateOneToAnother(tab.position.let { dateList[it].date.toString() }, "EEE MMM dd HH:mm:ss zzz yyyy", "yyyy-MM-dd")
                ?: ""

        val date = appUtil.convertDateOneToAnother(tempDate[tab.position].date.toString(), "EEE MMM dd HH:mm:ss zzz yyyy", "yyyy-MM-dd")
                ?: ""
        getAgentSlots(date)
    }

    override fun onTabUnselected(tab: Tab) {}

    override fun onTabReselected(tab: Tab) {}

    override fun selectTimeSlot(slot: String) {

        selectedTime = appUtil.convertDateOneToAnother(slot, "hh:mm aaa", "HH:mm") ?: ""
    }

    override fun refreshAdapter(type: String, adapterPosition: Int) {
        slotAdapter?.refreshAdapter(type, adapterPosition)
    }

    private fun agentAvailability() {


        if (isNetworkConnected) {

            val param = HashMap<String, String>()
            if (userBean != null) param["id"] = userBean?.id.toString()

            setBaseUrl(BuildConfig.ONBOARD_AGENT_URL, retrofit)
            viewModel.getAgentAvailability(param)
        }

    }

    private fun setAgentDates(dataBeans: MutableList<AgentDataBean>) {

        dateList.clear()

        val cal = Calendar.getInstance()
        val date = Date()
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", DateTimeUtils.timeLocale)
        val outputFormat = SimpleDateFormat("EEE, dd MMM", DateTimeUtils.timeLocale)

            if (dataBeans[0].cbl_user_avail_dates?.count() ?: 0 > 0) {
                for (userAvailDatesBean in dataBeans[0].cbl_user_avail_dates ?: listOf()) {
                    try {
                        if(userAvailDatesBean.status==1)
                        dateList.add(AgentAvailabilityDate(dateFormat.parse(userAvailDatesBean.from_date
                                ?: ""),   appUtil.convertDateOneToAnother(userAvailDatesBean.from_date?:"", "yyyy-MM-dd", "EEE, dd MMM")))

                    } catch (e: ParseException) {
                        e.printStackTrace()
                    }
                }
            }

        if (dataBeans[0].cbl_user_availablities?.count() ?: 0 > 0) {
            for (userWeekAvailBean in dataBeans[0].cbl_user_availablities ?: listOf()) {
                if (userWeekAvailBean.status == 1) {
                    cal.clear()
                    cal.time = date
                    for (i in 0..4) {
                        cal[Calendar.DAY_OF_WEEK] = userWeekAvailBean.day_id?.plus(1) ?: 0
                        if (date.before(cal.time) || date == cal.time) {
                            dateList.add(AgentAvailabilityDate(cal.time, outputFormat.format(cal.time)))
                        }
                        cal.add(Calendar.DAY_OF_MONTH, 7)
                    }
                }
            }
        }

        dateList.sort()
        for (availabilityDate in dateList) {
            tempDate.add(AgentAvailabilityDate(availabilityDate.date, availabilityDate.format_date))
            tabLayout.addTab(tabLayout.newTab().setText(availabilityDate.format_date))
        }
    }

    private fun getAgentSlots(date: String) {

        bookingTimeSlot = date

        val param = HashMap<String, String>()
        if (userBean != null) param["id"] = userBean?.id.toString()
        param["date"] = date
        param["offset"] = SimpleDateFormat("ZZZZZ", DateTimeUtils.timeLocale).format(System.currentTimeMillis())

        if (isNetworkConnected) {
            setBaseUrl(BuildConfig.ONBOARD_AGENT_URL, retrofit)
            viewModel.getAgentSlotsList(param)
        }
    }

    private fun settingTimeSlot(data: List<String>) {

        timeSlots?.clear()
        var dayofWeek = ""
        var timeSlotPeriod: TimeSlot
        val listHashMap = mutableMapOf<String, List<String>?>()
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
            val timeOfDay = startCalendar[Calendar.HOUR_OF_DAY]
            when (timeOfDay) {
                in 0..11 -> {
                    dayofWeek = "Morning"
                }
                in 12..15 -> {
                    dayofWeek = "Afternoon"
                }
                in 16..20 -> {
                    dayofWeek = "Evening"
                }
                in 21..23 -> {
                    dayofWeek = "Night"
                }
            }
            timeSlotsList = ArrayList()
            if (currentCalendar.time.before(startCalendar.time)) {
                if (listHashMap.containsKey(dayofWeek)) {
                    timeSlotsList.addAll(listHashMap[dayofWeek] ?: emptyList())
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
            //appUtil.convertDateOneToAnother(tabLayout.getTabAt(0)?.text.toString(), "EEE, dd MMM", "yyyy-MM-dd")

            /*getAgentSlots(appUtil.convertDateOneToAnother(tabLayout.getTabAt(0)?.text.toString() + "," + calendar.get(Calendar.YEAR)
                    , "EEE, dd MMM,yyyy", "yyyy-MM-dd") ?: "")*/

        } else {
            for ((key, value) in listHashMap) {
                timeSlotPeriod = TimeSlot(key, value, true)
                timeSlots?.add(timeSlotPeriod)
            }
        }
    }


    companion object {
        private const val ARG_PARAM1 = "agentData"
        private val ARG_PARAM4 = DataNames.DURATION
        fun newInstance(userBean: CblUserBean?, duration: Int): Bundle {
            val args = Bundle()
            args.putParcelable(ARG_PARAM1, userBean)
            args.putInt(ARG_PARAM4, duration)
            return args
        }
    }


}