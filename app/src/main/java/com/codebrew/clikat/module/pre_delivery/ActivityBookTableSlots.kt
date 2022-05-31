//package com.codebrew.clikat.module.pre_delivery
//
//import android.os.Bundle
//import androidx.appcompat.app.AppCompatActivity
//import androidx.databinding.DataBindingUtil
//import androidx.lifecycle.ViewModelProviders
//import androidx.recyclerview.widget.LinearLayoutManager
//import com.codebrew.clikat.BR
//import com.codebrew.clikat.R
//import com.codebrew.clikat.base.BaseActivity
//import com.codebrew.clikat.databinding.ActivityBookTableBinding
//import com.codebrew.clikat.databinding.FragmentPreBookMenuBinding
//import com.codebrew.clikat.di.ViewModelProviderFactory
//import com.codebrew.clikat.modal.other.SettingModel
//import com.codebrew.clikat.module.pre_delivery.Model.ListItem
//import com.codebrew.clikat.module.pre_delivery.adapter.BookAdapterHeaderSlots
//import com.michalsvec.singlerowcalendar.calendar.CalendarChangesObserver
//import com.michalsvec.singlerowcalendar.calendar.CalendarViewManager
//import com.michalsvec.singlerowcalendar.calendar.SingleRowCalendarAdapter
//import com.michalsvec.singlerowcalendar.selection.CalendarSelectionManager
//import kotlinx.android.synthetic.main.activity_book_table.*
//import java.util.*
//import kotlin.collections.ArrayList
//import com.michalsvec.singlerowcalendar.utils.DateUtils
//import kotlinx.android.synthetic.main.calendar_item.view.*
//import javax.inject.Inject
//
//class ActivityBookTableSlots : BaseActivity<ActivityBookTableBinding,SlotesViewModel>() {
//    private val calendar = Calendar.getInstance()
//
//    private var currentMonth = 0
//
//    private lateinit var mBinding: ActivityBookTableBinding
//
//    var header_list: ArrayList<ListItem> = ArrayList()
//
//    lateinit var slotesViewModel:SlotesViewModel
//
//    var item_list: ArrayList<ListItem> = ArrayList()
//
//    lateinit var bookAdapterSlots: BookAdapterHeaderSlots
//
//    @Inject
//    lateinit var factory: ViewModelProviderFactory
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//
//        calendar.time = Date()
//        currentMonth = calendar[Calendar.MONTH]
//        calenderView()
//        setHeader()
//        bookAdapterSlots = BookAdapterHeaderSlots(this,header_list)
//        val linearLayoutManager =
//                LinearLayoutManager(this)
//        mBinding.rvSlots.layoutManager = linearLayoutManager
//        mBinding.rvSlots.adapter = bookAdapterSlots
//    }
//
//    fun calenderView()
//    {
//        val myCalendarViewManager = object :
//                CalendarViewManager {
//           override  fun setCalendarViewResourceId(
//                    position: Int,
//                    date: Date,
//                    isSelected: Boolean
//            ): Int {
//                // set date to calendar according to position where we are
//                val cal = Calendar.getInstance()
//                cal.time = date
//                // if item is selected we return this layout items
//                // in this example. monday, wednesday and friday will have special item views and other days
//                // will be using basic item view
//                return if (isSelected)
//                    when (cal[Calendar.DATE]) {
//                        Calendar.MONDAY -> R.layout.selected_calendar_item
//                        Calendar.WEDNESDAY -> R.layout.selected_calendar_item
//                        Calendar.FRIDAY -> R.layout.selected_calendar_item
//                        else -> R.layout.selected_calendar_item
//                    }
//                else
//                // here we return items which are not selected
//                    when (cal[Calendar.DATE]) {
//                        Calendar.MONDAY -> R.layout.calendar_item
//                        Calendar.WEDNESDAY -> R.layout.calendar_item
//                        Calendar.FRIDAY -> R.layout.calendar_item
//                        else -> R.layout.calendar_item
//                    }
//
//                // NOTE: if we don't want to do it this way, we can simply change color of background
//                // in bindDataToCalendarView method
//            }
//
//            override fun bindDataToCalendarView(
//                    holder: SingleRowCalendarAdapter.CalendarViewHolder,
//                    date: Date,
//                    position: Int,
//                    isSelected: Boolean
//            ) {
//                // using this method we can bind data to calendar view
//                // good practice is if all views in layout have same IDs in all item views
//                holder.itemView.tv_date_calendar_item.text = DateUtils.getDayNumber(date)+" "+DateUtils.getMonthName(date)
//                holder.itemView.tv_day_calendar_item.text = DateUtils.getDay3LettersName(date)+", "
//
//            }
//        }
//
//        // using calendar changes observer we can track changes in calendar
//        val myCalendarChangesObserver = object :
//                CalendarChangesObserver {
//            // you can override more methods, in this example we need only this one
//            override fun whenSelectionChanged(isSelected: Boolean, position: Int, date: Date) {
//             //implement api here
//                super.whenSelectionChanged(isSelected, position, date)
//            }
//
//
//        }
//
//        // selection manager is responsible for managing selection
//        val mySelectionManager = object : CalendarSelectionManager {
//            override fun canBeItemSelected(position: Int, date: Date): Boolean {
//                // set date to calendar according to position
//                val cal = Calendar.getInstance()
//                cal.time = date
//                // in this example sunday and saturday can't be selected, others can
//                return when (cal[Calendar.DAY_OF_WEEK]) {
//                    Calendar.SATURDAY -> true
//                    Calendar.SUNDAY -> true
//                    else -> true
//                }
//            }
//        }
//
//        // here we init our calendar, also you can set more properties if you haven't specified in XML layout
//        val singleRowCalendar = main_single_row_calendar.apply {
//            calendarViewManager = myCalendarViewManager
//            calendarChangesObserver = myCalendarChangesObserver
//            calendarSelectionManager = mySelectionManager
//            setDates(getFutureDatesOfCurrentMonth())
//            init()
//        }
//    }
//
//
//    private fun getFutureDatesOfCurrentMonth(): List<Date> {
//        // get all next dates of current month
//        currentMonth = calendar[Calendar.MONTH]
//        return getDates(mutableListOf())
//    }
//
//
//    private fun getDates(list: MutableList<Date>): List<Date> {
//        // load dates of whole month
//        calendar.set(Calendar.MONTH, currentMonth)
//        calendar.set(Calendar.DAY_OF_MONTH, 1)
//        list.add(calendar.time)
//        while (currentMonth == calendar[Calendar.MONTH]) {
//            calendar.add(Calendar.DATE, +1)
//            if (calendar[Calendar.MONTH] == currentMonth)
//                list.add(calendar.time)
//        }
//        calendar.add(Calendar.DATE, -1)
//        return list
//    }
//   fun setHeader(){
//        var listItem:ListItem= ListItem("Morning",R.drawable.morning)
//       header_list.add(listItem)
//       listItem= ListItem("Afternoon",R.drawable.afternoon)
//       header_list.add(listItem)
//       listItem= ListItem("Night",R.drawable.night)
//       header_list.add(listItem)
//    }
//
//    override fun getBindingVariable(): Int {
//        return BR.viewModel
//    }
//
//    override fun getLayoutId(): Int {
//        return R.layout.activity_book_table
//    }
//
//    override fun getViewModel(): SlotesViewModel {
//        slotesViewModel= ViewModelProviders.of(this,factory).get(SlotesViewModel::class.java)
//        return slotesViewModel as SlotesViewModel
//    }
//
//}