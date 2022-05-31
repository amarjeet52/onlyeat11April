package com.codebrew.clikat.module.cart.schedule_order

import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.codebrew.clikat.BR
import com.codebrew.clikat.R
import com.codebrew.clikat.app_utils.AppUtils
import com.codebrew.clikat.app_utils.DateTimeUtils
import com.codebrew.clikat.app_utils.extension.onSnackbar
import com.codebrew.clikat.app_utils.navController
import com.codebrew.clikat.base.BaseFragment
import com.codebrew.clikat.base.BaseInterface
import com.codebrew.clikat.data.AppDataManager
import com.codebrew.clikat.data.constants.AppConstants
import com.codebrew.clikat.data.constants.PrefenceConstants
import com.codebrew.clikat.data.model.api.Currency
import com.codebrew.clikat.data.model.api.ListItem
import com.codebrew.clikat.databinding.FragmentBookTableBinding
import com.codebrew.clikat.di.ViewModelProviderFactory
import com.codebrew.clikat.modal.other.SettingModel
import com.codebrew.clikat.modal.other.TimeSlot
import com.codebrew.clikat.modal.slots.SupplierAvailableDatesItem
import com.codebrew.clikat.modal.slots.SupplierSlots
import com.codebrew.clikat.modal.slots.SupplierTimingsItem
import com.codebrew.clikat.modal.slots.TableSlotsItem
import com.codebrew.clikat.module.agent_time_slot.AgentViewModel
import com.codebrew.clikat.module.selectAgent.adapter.TimeSlotAdapter
import com.codebrew.clikat.preferences.DataNames
import com.codebrew.clikat.utils.Utils
import com.codebrew.clikat.utils.configurations.Configurations
import com.google.android.material.tabs.TabLayout
import kotlinx.android.synthetic.main.fragment_agent_time_slot.*
import kotlinx.android.synthetic.main.fragment_agent_time_slot.groupTableFee
import kotlinx.android.synthetic.main.fragment_agent_time_slot.noData
import kotlinx.android.synthetic.main.fragment_agent_time_slot.rbDelivery
import kotlinx.android.synthetic.main.fragment_agent_time_slot.rbPickup
import kotlinx.android.synthetic.main.fragment_agent_time_slot.rbTableBooking
import kotlinx.android.synthetic.main.fragment_agent_time_slot.refreshLayout
import kotlinx.android.synthetic.main.fragment_agent_time_slot.rgGroup
import kotlinx.android.synthetic.main.fragment_agent_time_slot.rv_timeslot
import kotlinx.android.synthetic.main.fragment_agent_time_slot.tabLayout
import kotlinx.android.synthetic.main.fragment_agent_time_slot.tvBookingFee
import kotlinx.android.synthetic.main.fragment_book_table.*
import kotlinx.android.synthetic.main.nothing_found.*
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class BookTableSlots : BaseFragment<FragmentBookTableBinding, AgentViewModel>(), TabLayout.OnTabSelectedListener,
        SwipeRefreshLayout.OnRefreshListener, BaseInterface, TimeSlotAdapter.TimeSlotCallback {


    @Inject
    lateinit var factory: ViewModelProviderFactory

    private var mBinding: FragmentBookTableBinding? = null

    private lateinit var viewModel: AgentViewModel

    @Inject
    lateinit var dateTimeUtils: DateTimeUtils

    @Inject
    lateinit var dataManger: AppDataManager

    var settingData: SettingModel.DataBean.SettingData? = null

    var startDateTime = ""
    var endDateTime = ""

    private var time24Format = ""

    private var dateYYMMDD = ""

    private var timeSlots: MutableList<TimeSlot>? = null

    private var bookingTimeSlot = ""

    private var slotAdapter: TimeSlotAdapter? = null

    private val colorConfig by lazy { Configurations.colors }

    private val tempList = ArrayList<SupplierAvailableDatesItem>()

    @Inject
    lateinit var mDateTime: DateTimeUtils

    @Inject
    lateinit var appUtil: AppUtils

    private var latitude: String? = null
    private var longitude: String? = null
    private var supplierId: String? = "0"
    private val dateList: MutableList<SupplierAvailableDatesItem> = mutableListOf()

    private var supplierAvailableData: SupplierSlots? = null
    private var tableItem: ListItem? = null
    private var selectedCurrency: Currency? = null


    override fun getBindingVariable(): Int {
        return BR.viewModel
    }

    override fun getLayoutId(): Int {
        return R.layout.fragment_book_table
    }

    override fun getViewModel(): AgentViewModel {
        viewModel = ViewModelProviders.of(this, factory).get(AgentViewModel::class.java)
        return viewModel
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        settingData = dataManger.getGsonValue(DataNames.SETTING_DATA, SettingModel.DataBean.SettingData::class.java)
        selectedCurrency = dataManger.getGsonValue(PrefenceConstants.CURRENCY_INF, Currency::class.java)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

//        colorConfig.toolbarColor = colorConfig.appBackground
//        colorConfig.toolbarText = colorConfig.textAppTitle
//
//        mBinding?.color = colorConfig
//        mBinding?.drawables = Configurations.drawables
//        mBinding?.strings = appUtil.loadAppConfig(0).strings
        mBinding = viewDataBinding
        viewModel.navigator = this



        refreshLayout.setOnRefreshListener(this)
        tabLayout.addOnTabSelectedListener(this)
        //calculateUserDate()
        initialise()
        setAdapter()
        listeners()

        timeSlotObserver()
        suppliersAvailabilityObserver()
        tableTimeSlotObserver()
        holdSlotsObserver()
        getSupplierAvailabilities()
    }

    private fun initialise() {

        if (arguments != null) {
            if (arguments?.containsKey("supplierId") == true)
                supplierId = arguments?.getString("supplierId")
            if (arguments?.containsKey("latitude") == true)
                latitude = arguments?.getString("latitude")
            if (arguments?.containsKey("longitude") == true)
                longitude = arguments?.getString("longitude")
        }

        //  tb_title.text = getString(R.string.choose_date_time)
        clScheduleOrder?.visibility = View.VISIBLE
        btn_book_agent?.visibility = View.GONE
        rgGroup?.visibility = View.VISIBLE

        time24Format = ""
        dateYYMMDD = ""
        timeSlots = ArrayList()

        if (arguments?.containsKey("dineIn") == true) {
            rgGroup?.visibility = View.GONE
            rbTableBooking?.isChecked = true
            /** neet values are added according to params checked into web inspect mode */
            latitude = "0"
            longitude = "0"
        }

    }

    private fun setAdapter() {
        rv_timeslot.layoutManager = LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)
        slotAdapter = TimeSlotAdapter(timeSlots)
        slotAdapter?.settingCallback(this)
        rv_timeslot.adapter = slotAdapter
    }

    private fun listeners() {

        tv_book.setOnClickListener {
            if (dateYYMMDD.isEmpty() || time24Format.isEmpty()) {
                viewDataBinding.root.onSnackbar(getString(R.string.date_time_msg))
            } else if (settingData?.table_book_mac_theme == "1" && arguments?.containsKey("seating_capacity") == true && arguments?.containsKey("supplierBranchId") == true) {
                holdSlotsApi()
            } else {
                screenSelection()
                val datePosition: Int = tabLayout?.selectedTabPosition ?: 0
                val supplierTimingsItem = getSelectedSlot(datePosition)

                val tempRequestHolder = hashMapOf(
                        "slot_id" to supplierTimingsItem?.id,
                        "offset" to 0,
                        "limit" to 20,
                        "supplier_id" to supplierId?.toInt(),
                        "branch_id" to arguments?.getInt("supplierBranchId", 0)
                )

                val map2 = hashMapOf("startTime" to startDateTime, "endTime" to endDateTime)

                val bundle = bundleOf("map" to tempRequestHolder, "map2" to map2)

                navController(this@BookTableSlots)
                        .navigate(R.id.action_book_table_to_Table_selection, bundle)
//                val intent = Intent(this,TableSelectActivity::class.java)
//                intent.putExtra("map",tempRequestHolder)
//                intent.putExtra("map2",map2)
//                startActivity(intent)
                // viewModel.getListOfTablesAccordingToSlot(tempRequestHolder,true)
                //  holdSlotsApi()
            }
        }

        tv_restaurant.setOnClickListener {

        }

        iv_back.setOnClickListener {
            // finish()
        }

        rgGroup?.setOnCheckedChangeListener { _, _ ->
            getSupplierAvailabilities()
        }
    }

    private fun holdSlotsApi() {
        val hashMap = HashMap<String, String>()
        hashMap["supplier_id"] = supplierId ?: ""
        hashMap["slotDate"] = dateYYMMDD
        hashMap["slotTime"] = appUtil.convertDateOneToAnother(time24Format, "hh:mm aa", "HH:mm:ss")
                ?: ""
        hashMap["offset"] = SimpleDateFormat("ZZZZZ", DateTimeUtils.timeLocale).format(System.currentTimeMillis())
        hashMap["branch_id"] = arguments?.getInt("supplierBranchId", 0).toString()
        if (isNetworkConnected)
            viewModel.holdSlotApi(hashMap)
    }


    override fun onTabReselected(tab: TabLayout.Tab?) {

    }

    override fun onTabUnselected(tab: TabLayout.Tab?) {

    }

    override fun onTabSelected(tab: TabLayout.Tab?) {
        if (tempList.isNotEmpty()) {
            dateYYMMDD = tab?.position?.let { tempList[it].fromDate.toString() } ?: ""
            getAgentSlots(dateYYMMDD)
        }
    }

    override fun onRefresh() {
        refreshLayout.isRefreshing = false
        if (tempList.isNotEmpty()) {
            dateYYMMDD = tabLayout.selectedTabPosition.let { tempList[it].fromDate.toString() }
            getAgentSlots(dateYYMMDD)
        }
    }


    override fun refreshAdapter(type: String?, adapterPosition: Int) {
        slotAdapter?.refreshAdapter(type, adapterPosition)
    }


    private fun screenSelection() {
        val datePosition = tabLayout?.selectedTabPosition
        if (datePosition != null && datePosition != -1 && supplierAvailableData != null) {

            val supplierSlotsData = SupplierSlots()
            supplierSlotsData.supplierSlotsInterval = supplierAvailableData?.supplierSlotsInterval
            supplierSlotsData.supplierTimings = arrayListOf(getSelectedSlot(datePosition))
            supplierSlotsData.startDateTime = mDateTime.convertDateOneToAnother("$bookingTimeSlot $time24Format",
                    "yyyy-MM-dd hh:mm aa", "yyyy-MM-dd HH:mm:ss")
            startDateTime = mDateTime.convertDateOneToAnother("$bookingTimeSlot $time24Format",
                    "yyyy-MM-dd hh:mm aa", "yyyy-MM-dd HH:mm:ss").toString()
            endDateTime = getEndDate()
            supplierSlotsData.endDateTime = getEndDate()
            supplierSlotsData.selectedTable = tableItem

//            val intent = Intent()
//            intent.putExtra("slotDetail", supplierSlotsData)
//            setResult(Activity.RESULT_OK, intent)
//            finish()
        }
    }

    private fun getSelectedSlot(datePosition: Int): SupplierTimingsItem? {
        val timingList = if (tempList[datePosition].isWeek == true && tempList[datePosition].dayId != null) {
            supplierAvailableData?.supplierTimings?.filter { it?.dayId == tempList[datePosition].dayId }
        } else
            supplierAvailableData?.supplierTimings?.filter { it?.dateId == tempList[datePosition].id }

        timingList?.forEach {
            val startTime = mDateTime.getDateFormat("$bookingTimeSlot ${it?.startTime}",
                    "yyyy-MM-dd HH:mm:ss")
            val endTime = mDateTime.getDateFormat("$bookingTimeSlot ${it?.endTime}",
                    "yyyy-MM-dd HH:mm:ss")
            val selectedSlot = mDateTime.getDateFormat("$bookingTimeSlot $time24Format",
                    "yyyy-MM-dd hh:mm aa")
            if (endTime?.time ?: 0L >= selectedSlot?.time ?: 0L && startTime?.time ?: 0L <= selectedSlot?.time ?: 0L)
                return it
        }
        return null
    }

    private fun getEndDate(): String {
        val startDate = mDateTime.getDateFormat("$bookingTimeSlot $time24Format", "yyyy-MM-dd hh:mm aa")
                ?: Date()
        startDate.time = startDate.time.plus(supplierAvailableData?.supplierSlotsInterval?.times(60000)
                ?: 0)
        val format = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        return format.format(startDate)
    }

    private fun getSupplierAvailabilities() {
        val type = if (rbDelivery?.isChecked == true) 1 else if (rbPickup?.isChecked == true) 2 else 3
        val param = HashMap<String, String>()
        param["date_order_type"] = type.toString()
        param["supplier_id"] = supplierId ?: ""
        param["longitude"] = longitude ?: ""
        param["latitude"] = latitude ?: ""

        if (isNetworkConnected)
            viewModel.getSupplierAvailabilities(param)
    }

    private fun suppliersAvailabilityObserver() {
        // Create the observer which updates the UI.
        val catObserver = Observer<SupplierSlots> { resource ->
            supplierAvailableData = resource
            clearData()
            setAgentDates(resource)
            //  setAvailableDates(resource.supplierAvailableDates)
        }
        // Observe the LiveData, passing in this activity as the LifecycleOwner and the observer.
        viewModel.supplierAvailabilities.observe(this, catObserver)
    }

    private fun setAvailableDates(supplierAvailableDates: ArrayList<SupplierAvailableDatesItem>?) {
        /*add future dates and status ==1 */
        supplierAvailableDates?.forEach {
            val validDate = dateTimeUtils.getDateFormat(it.fromDate ?: "", "yyyy-MM-dd")

            val date = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
            if (it.status == 1 && (validDate?.after(Date()) == true || it.fromDate?.equals(date) == true)) {
                tempList.add(it)
                val text = appUtil.convertDateOneToAnother(it.fromDate.toString(), "yyyy-MM-dd", "EEE, dd MMMM")
                        ?: ""
                tabLayout.addTab(tabLayout.newTab().setText(text))
            }

        }
    }

    private fun calculateUserDate() {
        val actualFormat = SimpleDateFormat("yyyy-MM-dd", DateTimeUtils.timeLocale)
        val outputFormat = SimpleDateFormat("EEE, dd MMMM", DateTimeUtils.timeLocale)
        val cal = Calendar.getInstance()

        for (i in 0..30) {
            tempList.add(SupplierAvailableDatesItem(fromDate = actualFormat.format(cal.time)))
            tabLayout.addTab(tabLayout.newTab().setText(outputFormat.format(cal.time)))

            cal.add(Calendar.DATE, 1)
        }
    }

    private fun setAgentDates(dataBeans: SupplierSlots) {

        dateList.clear()

        val cal = Calendar.getInstance()

        val dateFormat = SimpleDateFormat("yyyy-MM-dd", DateTimeUtils.timeLocale)

        if (dataBeans.supplierAvailableDates?.count() ?: 0 > 0) {
            for (userAvailDatesBean in dataBeans.supplierAvailableDates ?: arrayListOf()) {
                val validDate = dateTimeUtils.getDateFormat(userAvailDatesBean.fromDate
                        ?: "", "yyyy-MM-dd")
                val date = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
                if (userAvailDatesBean.status == 1 && (validDate?.after(Date()) == true || userAvailDatesBean.fromDate?.equals(date) == true)) {
                    userAvailDatesBean.date = dateFormat.parse(userAvailDatesBean.fromDate
                            ?: "")
                    dateList.add(userAvailDatesBean)
                }
            }
        }

        val date = Date()
        if (dataBeans.weeksData?.count() ?: 0 > 0) {
            for (userWeekAvailBean in dataBeans.weeksData ?: arrayListOf()) {
                if (userWeekAvailBean.status == 1) {
                    cal.clear()
                    cal.time = date
                    for (i in 0..4) {
                        cal[Calendar.DAY_OF_WEEK] = userWeekAvailBean.dayId?.plus(1) ?: 0
                        if (date.before(cal.time) || date == cal.time) {
                            userWeekAvailBean.date = cal.time
                            userWeekAvailBean.fromDate = dateFormat.format(cal.time)
                            dateList.add(SupplierAvailableDatesItem(date = cal.time, fromDate = dateFormat.format(cal.time),
                                    dayId = userWeekAvailBean.dayId, id = userWeekAvailBean.id, isWeek = true))
                        }
                        cal.add(Calendar.DAY_OF_MONTH, 7)
                    }
                }
            }
        }

        dateList.sort()
        for (availabilityDate in dateList) {
            tempList.add(availabilityDate)
            tabLayout.addTab(tabLayout.newTab().setText(appUtil.convertDateOneToAnother(availabilityDate.fromDate
                    ?: "", "yyyy-MM-dd", "EEE, dd MMM")))
        }
    }

    private fun clearData() {
        tempList.clear()
        timeSlots?.clear()
        slotAdapter?.notifyDataSetChanged()
        time24Format = ""
        dateYYMMDD = ""
        bookingTimeSlot = ""
        tabLayout?.removeAllTabs()
    }


    private fun getAgentSlots(date: String) {
        bookingTimeSlot = date
        val type = if (rbDelivery?.isChecked == true) 1 else if (rbPickup?.isChecked == true) 2 else 3
        val param = HashMap<String, String>()
        param["date"] = date
        param["date_order_type"] = type.toString()
        param["supplier_id"] = supplierId ?: ""
        param["longitude"] = longitude ?: ""
        param["latitude"] = latitude ?: ""

        if (isNetworkConnected) {
            if (settingData?.table_book_mac_theme == "1" && arguments?.containsKey("seating_capacity") == true && arguments?.containsKey("supplierBranchId") == true) {
                param["seating_capacity"] = arguments?.getInt("seating_capacity", 0).toString()
                param["branch_id"] = arguments?.getInt("supplierBranchId", 0).toString()
                viewModel.getTableSlotsList(param)
            } else
                viewModel.getSlotsList(param)
        }
    }


    private fun timeSlotObserver() {
        // Create the observer which updates the UI.
        val catObserver = Observer<List<String>> { resource ->
            updateAgentSlots(resource)

        }
        // Observe the LiveData, passing in this activity as the LifecycleOwner and the observer.
        viewModel.slotsData.observe(this, catObserver)
    }

    private fun tableTimeSlotObserver() {
        // Create the observer which updates the UI.
        val catObserver = Observer<TableSlotsItem> { resource ->
            tableItem = resource?.availableTables?.firstOrNull()
            if (tableItem != null) {
                groupTableFee?.visibility = View.VISIBLE
                tvBookingFee?.text = getString(R.string.currency_tag, AppConstants.CURRENCY_SYMBOL,
                        Utils.getPriceFormat(tableItem?.table_booking_price
                                ?: 0f, settingData, selectedCurrency))
            } else
                groupTableFee?.visibility = View.GONE

            updateAgentSlots(resource?.slots)

        }
        // Observe the LiveData, passing in this activity as the LifecycleOwner and the observer.
        viewModel.tableSlotsData.observe(this, catObserver)
    }

    private fun holdSlotsObserver() {
        // Create the observer which updates the UI.
        val observer = Observer<Any> { resource ->
            screenSelection()
        }
        // Observe the LiveData, passing in this activity as the LifecycleOwner and the observer.
        viewModel.holdSlotsLiveData.observe(this, observer)
    }

    private fun updateAgentSlots(resource: List<String>?) {
        timeSlots?.clear()

        resource?.let {
            settingTimeSlot(it)
        }

        tvText?.text = getString(R.string.no_slots_available)
        noData?.visibility = if (timeSlots?.size == 0) View.VISIBLE else View.GONE
        // rv_timeslot.visibility = if ((timeSlots?.size ?: 0) > 0) View.VISIBLE else View.GONE
        slotAdapter?.refreshAdapter("", -1)
        slotAdapter?.notifyDataSetChanged()
        rv_timeslot.adapter?.notifyDataSetChanged()


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

            if (settingData?.table_book_mac_theme == "1") {
                when (startCalendar.get(Calendar.HOUR_OF_DAY)) {
                    in 0..12 -> dayofWeek = getString(R.string.breakfast)
                    in 13..20 -> dayofWeek = getString(R.string.lunch)
                    in 21..23 -> dayofWeek = getString(R.string.dinner)
                }
            } else {
                when (startCalendar.get(Calendar.HOUR_OF_DAY)) {
                    in 0..11 -> dayofWeek = getString(R.string.morning)
                    in 12..15 -> dayofWeek = getString(R.string.aftenoon)
                    in 16..20 -> dayofWeek = getString(R.string.evening)
                    in 21..23 -> dayofWeek = getString(R.string.night)
                }
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

//        if (listHashMap.isEmpty()) {
//            if (appUtil.convertDateOneToAnother(bookingTimeSlot, "yyyy-MM-dd", "EEE, dd MMM") == tabLayout.getTabAt(0)?.text) {
//                tabLayout.removeTabAt(0)
//            }
//
//            getAgentSlots(appUtil.convertDateOneToAnother(tabLayout.getTabAt(0)?.text.toString() + "," + calendar.get(Calendar.YEAR)
//                    , "EEE, dd MMM,yyyy", "yyyy-MM-dd") ?: "")
//        }


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
