package com.codebrew.clikat.module.cart.schedule_order

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.codebrew.clikat.BR
import com.codebrew.clikat.R
import com.codebrew.clikat.base.BaseActivity
import com.codebrew.clikat.databinding.ActivityCraveScheduleBinding
import com.codebrew.clikat.di.ViewModelProviderFactory
import com.codebrew.clikat.module.cart.CartViewModel
import com.codebrew.clikat.module.cart.schedule_order.adapter.CraveSlotAdaptor
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

class CraveScheduleActivity : BaseActivity<ActivityCraveScheduleBinding, CartViewModel>() {

    val arrayTimeSlot: ArrayList<String> = arrayListOf("05:00 PM - 06:00 PM", "06:00 PM - 07:00 PM", "07:00 PM - 08:00 PM", "08:00 PM - 09:00 PM", "09:00 PM - 10:00 PM",
            "10:00 PM - 11:00 PM", "11:00 PM - 12:00 PM", "12:00 AM - 01:00 AM", "01:00 AM - 02:00 AM", "02:00 AM - 03:00 AM", "03:00 AM - 04:00 AM",
            "04:00 AM - 05:00 AM", "05:00 AM - 06:00 AM", "06:00 AM - 07:00 AM",
            "07:00 AM - 08:00 AM", "08:00 AM - 09:00 AM")

    private var mCartViewModel: CartViewModel? = null
    private var binding: ActivityCraveScheduleBinding? = null
    private var adaptor: CraveSlotAdaptor? = null
    private val calendar: Calendar = Calendar.getInstance()

    private val formatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

    private var selectedDate = ""

    private var scheduleType = "0"

    @Inject
    lateinit var factory: ViewModelProviderFactory

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = viewDataBinding

        binding?.rvSlots?.layoutManager = LinearLayoutManager(this)
        adaptor = CraveSlotAdaptor(arrayTimeSlot)
        binding?.rvSlots?.adapter = adaptor
        binding?.rvSlots?.isNestedScrollingEnabled = false

        binding?.btnToday?.isSelected = true

        binding?.btnDaily?.setOnClickListener {
            binding?.btnDaily?.isSelected = true
            binding?.btnMonthly?.isSelected = false
            binding?.btnWeekly?.isSelected = false
            binding?.btnToday?.isSelected = false

            scheduleType = "1"
        }

        binding?.btnMonthly?.setOnClickListener {
            binding?.btnMonthly?.isSelected = true
            binding?.btnDaily?.isSelected = false
            binding?.btnWeekly?.isSelected = false
            binding?.btnToday?.isSelected = false

            scheduleType = "3"
        }

        binding?.btnWeekly?.setOnClickListener {
            binding?.btnMonthly?.isSelected = false
            binding?.btnDaily?.isSelected = false
            binding?.btnWeekly?.isSelected = true
            binding?.btnToday?.isSelected = false

            scheduleType = "2"
        }

        binding?.btnToday?.setOnClickListener {
            binding?.btnMonthly?.isSelected = false
            binding?.btnDaily?.isSelected = false
            binding?.btnWeekly?.isSelected = false
            binding?.btnToday?.isSelected = true

            scheduleType = "0"
        }

        binding?.btnCancel?.setOnClickListener { onBackPressed() }

        binding?.btnConfirm?.setOnClickListener {

            if (adaptor?.getSelectedPosition() == -1) {
                Toast.makeText(this, getString(R.string.choose_date_time), Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val selectedTime = arrayTimeSlot[adaptor?.getSelectedPosition() ?: 0]

            val times = selectedTime.split("-")

            val startDate = "$selectedDate ${times[0]}"
            val endDate = "$selectedDate ${times[1]}"

            val scheduleModel = CraveScheduleModel(
                    scheduleDate = startDate,
                    endDate = endDate,
                    type = scheduleType
            )

            val intent = Intent()
            intent.putExtra("crave_schedule", scheduleModel)
            setResult(Activity.RESULT_OK, intent)
            finish()
        }

        selectedDate = formatter.format(binding?.calender?.date)
        val date = calendar.time.time
        binding?.calender?.minDate= date
        binding?.calender?.setOnDateChangeListener { view, year, month, dayOfMonth ->

            calendar.set(year, month, dayOfMonth)

            selectedDate = formatter.format(calendar.time)
        }

    }

    override fun getBindingVariable(): Int {
        return BR.viewModel
    }

    override fun getLayoutId(): Int = R.layout.activity_crave_schedule

    override fun getViewModel(): CartViewModel {
        mCartViewModel = ViewModelProviders.of(this, factory).get(CartViewModel::class.java)
        return mCartViewModel as CartViewModel
    }
}