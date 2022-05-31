package com.codebrew.clikat.module.cart

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.lifecycle.observe
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.codebrew.clikat.BR
import com.codebrew.clikat.R
import com.codebrew.clikat.base.BaseActivity
import com.codebrew.clikat.data.model.api.vehicleDetails.VehicleData
import com.codebrew.clikat.databinding.ActivitySelectSlotsBinding
import com.codebrew.clikat.di.ViewModelProviderFactory
import com.codebrew.clikat.modal.other.TimeDataBean
import com.codebrew.clikat.module.cart.adapter.SlotsAdapter
import com.codebrew.clikat.module.dialog_adress.adapter.VehicleDEtailAdapter
import com.codebrew.clikat.module.dialog_adress.interfaces.VehicleDialogListener
import kotlinx.android.synthetic.main.fragment_cart_new.*
import kotlinx.android.synthetic.main.fragment_dialog_address.*
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

class SelectSlots : BaseActivity<ActivitySelectSlotsBinding, CartViewModel>() {
    private var mCartViewModel: CartViewModel? = null
    private var binding: ActivitySelectSlotsBinding? = null
    lateinit var slotModel: SlotModel

    @Inject
    lateinit var factory: ViewModelProviderFactory
    var time = 15
    var last_time = ""
    var last_time_n = ""
    var time_end = ""
    var timings: ArrayList<TimeDataBean> = ArrayList()
    var arrayTimeSlot: List<String> = ArrayList()
    lateinit var slotAdaptor: SlotsAdapter
    private var mListener1: VehicleDialogListener? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = viewDataBinding
        timings = intent.getParcelableArrayListExtra<TimeDataBean>("array")!!
        // displayTimeSlots()

        checkResturntSlots(timings)
        subscribeObserver()
        viewModel.checkTimeSlots(time_end)



        binding?.ivBack?.setOnClickListener { finish() }
        binding?.tvBook?.setOnClickListener {
            val intent = Intent()
            if (last_time.equals("")) {
                last_time = arrayTimeSlot.get(0).toString()
            }
            val sdf = SimpleDateFormat("yyyy,MM:dd:HH:mm")
            val currentDateandTime = sdf.format(Date())
            val date = sdf.parse(currentDateandTime)
            val calendar = Calendar.getInstance()
            calendar.time = date

            val formatter_n = SimpleDateFormat("MMM, d EE")

            var date_time_n = formatter_n.format(calendar.time)

            val formatter = SimpleDateFormat("yyyy-MM-dd")
            var date_time = formatter.format(calendar.time)

            last_time_n = date_time?.toString()!! + " " + last_time
            last_time = date_time_n?.toString() + " " + last_time

            intent.putExtra("slot_time", last_time)
            intent.putExtra("slot_time_n", last_time_n)
            setResult(Activity.RESULT_OK, intent)
            finish()
        }
    }

    private fun subscribeObserver() {

        // Create the observer which updates the UI.
        val slotObserver = Observer<List<String>> { resources ->
            arrayTimeSlot = resources
            slotAdaptor = SlotsAdapter(this,
                    arrayTimeSlot,
                    object : SlotsAdapter.Callback {
                        override fun onItemClicked(time: String) {
                            last_time = time

                        }
                    })
            binding?.rvSlots?.layoutManager = GridLayoutManager(this, 3, GridLayoutManager.VERTICAL, false)
            binding?.rvSlots?.adapter = slotAdaptor
        }
        viewModel.slotLiveData.observe(this, slotObserver)
    }

    override fun getBindingVariable(): Int {
        return BR.viewModel
    }

    override fun getLayoutId(): Int = R.layout.activity_select_slots

    override fun getViewModel(): CartViewModel {
        mCartViewModel = ViewModelProviders.of(this, factory).get(CartViewModel::class.java)
        return mCartViewModel as CartViewModel
    }


    @SuppressLint("SimpleDateFormat", "LogNotTimber")
    private fun displayTimeSlots() {

        val timeValue = System.currentTimeMillis()
        val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS")

        val date = Date(timeValue)
        val time = simpleDateFormat.format(date)

        val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS")
        try {
            val startCalendar = Calendar.getInstance()
            startCalendar.time = sdf.parse(time)
            if (startCalendar[Calendar.MINUTE] < 30) {
                startCalendar[Calendar.MINUTE] = 45
            } else {
                startCalendar.add(Calendar.MINUTE, 45) // overstep hour and clear minutes
                startCalendar.clear(Calendar.MINUTE)
            }
            val endCalendar = Calendar.getInstance()
            endCalendar.time = startCalendar.time

            // if you want dates for whole next day, uncomment next line
            //endCalendar.add(Calendar.DAY_OF_YEAR, 1);
            endCalendar.add(Calendar.HOUR_OF_DAY, 24 - startCalendar[Calendar.HOUR_OF_DAY])
            endCalendar.clear(Calendar.MINUTE)
            endCalendar.clear(Calendar.SECOND)
            endCalendar.clear(Calendar.MILLISECOND)
            val slotTime = SimpleDateFormat("hh:mma")
            val slotTimeLong = SimpleDateFormat("MMM, d EE hh:mm a")
            val slotTimeLongnew = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
            val slotDate = SimpleDateFormat(", dd/MM/yy")
            while (endCalendar.after(startCalendar)) {
                val slotStartTime = slotTimeLongnew.format(startCalendar.time)
                val slotStartDate = slotDate.format(startCalendar.time)
                startCalendar.add(Calendar.MINUTE, 15)
                val slotLongTime = slotTimeLong.format(startCalendar.time)
                val slotEndTime = slotTime.format(startCalendar.time)
                Log.e("datae", slotStartTime.toString())
                slotModel = SlotModel(slotEndTime.toString(), slotLongTime.toString(), slotStartTime.toString())

            }
        } catch (e: ParseException) {
            // date in wrong format
        }
    }

    fun checkResturntSlots(timing: List<TimeDataBean>?) {
        // val calendar = Calendar.getInstance()
        val startDate = Calendar.getInstance(Locale.getDefault())
        val endDate = Calendar.getInstance(Locale.getDefault())
        val currentDate = Calendar.getInstance(Locale.getDefault())

        var isCheckStatus = false

        val sdf = SimpleDateFormat("HH:mm:ss", Locale.getDefault())

        run loop@{
            timing?.forEach {
                if (it.week_id == (getDayId(currentDate.get(Calendar.DAY_OF_WEEK))
                                ?: "-1").toInt()) {
                    isCheckStatus = it.is_open == 1

                    startDate.time = sdf.parse(it.start_time ?: "")!!
                    startDate.set(Calendar.YEAR, currentDate.get(Calendar.YEAR))
                    startDate.set(Calendar.MONTH, currentDate.get(Calendar.MONTH))
                    startDate.set(Calendar.DAY_OF_MONTH, currentDate.get(Calendar.DAY_OF_MONTH))

                    endDate.time = sdf.parse(it.end_time ?: "")!!
                    endDate.set(Calendar.YEAR, currentDate.get(Calendar.YEAR))
                    endDate.set(Calendar.MONTH, currentDate.get(Calendar.MONTH))
                    endDate.set(Calendar.DAY_OF_MONTH, currentDate.get(Calendar.DAY_OF_MONTH))

                    if (currentDate.time.after(startDate.time) && currentDate.time.before(endDate.time)) {
                        time_end = it.end_time.toString()
                        Log.e("end_date", time_end)
                    }
                }
            }
        }
    }

    fun getDayId(dayId: Int): String? {
        return when (dayId) {
            Calendar.SUNDAY -> "6"
            Calendar.MONDAY -> "0"
            Calendar.TUESDAY -> "1"
            Calendar.WEDNESDAY -> "2"
            Calendar.THURSDAY -> "3"
            Calendar.FRIDAY -> "4"
            Calendar.SATURDAY -> "5"
            else -> "-1"
        }
    }

    fun checkDayId(dayId: Int): Int? {
        return when (dayId) {
            Calendar.SUNDAY -> 0
            Calendar.MONDAY -> 1
            Calendar.TUESDAY -> 2
            Calendar.WEDNESDAY -> 3
            Calendar.THURSDAY -> 4
            Calendar.FRIDAY -> 5
            Calendar.SATURDAY -> 6
            else -> -1
        }
    }
}