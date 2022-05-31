package com.codebrew.clikat.app_utils


import android.annotation.SuppressLint
import com.codebrew.clikat.data.constants.AppConstants
import java.text.DateFormatSymbols
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

/**
 * contains commonly used methods related to date & time conversion
 */
class DateTimeUtils {


    /**
     * get difference between current time and provided timezone
     *
     * @return differrence in time between two timestamp
     */
    val timeOffset: Long
        get() {
            val currentTime = System.currentTimeMillis()
            val edtOffset = TimeZone.getTimeZone("Your Time Zone").getOffset(currentTime)
            val current = TimeZone.getDefault().getOffset(currentTime)
            return (current - edtOffset).toLong()
        }

    fun getTimeOffset(): String {
        return SimpleDateFormat(
                "ZZZZZ",
                timeLocale
        ).format(System.currentTimeMillis())
    }

    val currentDate: String
        get() {
            val cDate = Date()
            return SimpleDateFormat("yyyy-MM-dd HH:mm:ss", timeLocale).format(cDate)
        }

    val dayName: String
        get() {
            val calendar = Calendar.getInstance()

            calendar.time = Date()

            val days = arrayOf("SUNDAY", "MONDAY", "TUESDAY", "WEDNESDAY", "THURSDAY", "FRIDAY", "SATURDAY")

            return days[calendar.get(Calendar.DAY_OF_WEEK)]
        }

    /**
     * get date form timestamp
     *
     * @param timestamp time to be converter
     * @param format    for date conversion eg(dd MMMM yyyy)
     * @return date in string
     */

    fun getDateFromTimestamp(timestamp: String, format: String): String {


        val calendar = Calendar.getInstance()
        calendar.timeInMillis = (timestamp).toLong()

        return try {
            val sdf = SimpleDateFormat(format, timeLocale)
            val netDate = calendar.time
            sdf.format(netDate)
        } catch (ex: Exception) {
            "xx xxxx xxxx"
        }

    }

    /**
     * To convert a date to timestamp
     *
     * @param dateToConvert date to be converted
     * @param dateFormat    format of date entered
     * @return timestamp in milliseconds
     */

    fun convertDateToTimeStamp(dateToConvert: String, dateFormat: String): Long {
        val formatter = SimpleDateFormat(dateFormat, timeLocale)
        var date: Date? = null
        try {
            date = formatter.parse(dateToConvert)
            return date!!.time
        } catch (e: ParseException) {
            e.printStackTrace()
            return 0
        }

    }

    /**
     * Convert date from one format to another
     *
     * @param dateToConvert date to be converted
     * @param formatFrom    the format of the date to be converted
     * @param formatTo      the format of date you want the output
     * @return date in string as per the entered formats
     */
    @SuppressLint("SimpleDateFormat")
    fun convertDateOneToAnother(dateToConvert: String, formatFrom: String, formatTo: String): String? {
        var outputDateStr = ""

        if (dateToConvert.isNotEmpty()) {
            val inputFormat = SimpleDateFormat(formatFrom, Locale(if (AppConstants.LANG_CODE == "ar") "en" else AppConstants.LANG_CODE))
            val outputFormat = SimpleDateFormat(formatTo, Locale(if (AppConstants.LANG_CODE == "ar") "en" else AppConstants.LANG_CODE))
            val date: Date
            try {
                date = inputFormat.parse(dateToConvert)
                outputDateStr = outputFormat.format(date)
            } catch (e: ParseException) {
                e.printStackTrace()
            }
        }

        return outputDateStr
    }


    fun getCalendarFormat(dateToConvert: String, dateFormat: String): Calendar? {
        val inputFormat = SimpleDateFormat(dateFormat, timeLocale)
        var date: Date? = null
        try {
            date = inputFormat.parse(dateToConvert)
        } catch (e: ParseException) {
            e.printStackTrace()
        }

        val calendar = Calendar.getInstance(Locale.getDefault())
        calendar.time = date ?: Date()
        return calendar
    }

    fun getDateFormat(dateToConvert: String, dateFormat: String): Date? {
        val inputFormat = SimpleDateFormat(dateFormat, Locale.getDefault())
        inputFormat.timeZone = TimeZone.getTimeZone("UTC")
        var date: Date? = null
        try {
            date = inputFormat.parse(dateToConvert)
        } catch (e: ParseException) {
            e.printStackTrace()
        }

        return date
    }

    @SuppressLint("SimpleDateFormat")
    fun getDatePlusTwo(date: String): String? {
        try {
            val format = SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH)
            val dat = format.parse(date)
            val calendar = Calendar.getInstance()
            calendar.time = dat
            calendar.add(Calendar.DAY_OF_YEAR, +2)
            val newDate = calendar.time
            return SimpleDateFormat("EEEE yyyy-MM-dd").format(newDate)
        } catch (e: ParseException) {
            e.printStackTrace()
        }

        return null
    }

    @SuppressLint("SimpleDateFormat")
    fun getDatePlusOne(date: String): String? {
        try {
            val format = SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH)
            val dat = format.parse(date)
            val calendar = Calendar.getInstance()
            calendar.time = dat
            calendar.add(Calendar.DAY_OF_YEAR, +1)
            val newDate = calendar.time
            return SimpleDateFormat("EEEE yyyy-MM-dd").format(newDate)
        } catch (e: ParseException) {
            e.printStackTrace()
        }

        return null
    }

    @SuppressLint("SimpleDateFormat")
    fun sevenDayBackDate(): String {
        val cDate = Date()
        val calendar = Calendar.getInstance()
        calendar.time = cDate
        calendar.add(Calendar.DAY_OF_YEAR, -7)
        val newDate = calendar.time
        return SimpleDateFormat("yyyy-MM-dd").format(newDate)
    }

    @SuppressLint("SimpleDateFormat")
    fun thirtyDayBackDate(): String {
        val cDate = Date()
        val calendar = Calendar.getInstance()
        calendar.time = cDate
        calendar.add(Calendar.DAY_OF_YEAR, -30)
        val newDate = calendar.time
        return SimpleDateFormat("yyyy-MM-dd").format(newDate)
    }

    @SuppressLint("SimpleDateFormat")
    fun toDate(): String {
        val cDate = Date()
        val calendar = Calendar.getInstance()
        calendar.time = cDate
        calendar.add(Calendar.YEAR, -10)
        val newDate = calendar.time
        return SimpleDateFormat("yyyy-MM-dd").format(newDate)
    }

    fun nextDayOfWeek(dow: Int): Calendar {
        val date = Calendar.getInstance()
        var diff = dow - date.get(Calendar.DAY_OF_WEEK)
        if (diff <= 0) {
            diff += 7
        }
        date.add(Calendar.DAY_OF_MONTH, diff)
        return date
    }

    @SuppressLint("SimpleDateFormat")
    fun Format12to24(time: String): String {

        val displayFormat = SimpleDateFormat("HH:mm")
        val parseFormat = SimpleDateFormat("hh:mm aa")
        var date: Date? = null
        try {
            date = parseFormat.parse(time)
            return displayFormat.format(date)
        } catch (e: ParseException) {
            e.printStackTrace()
        }

        return ""
    }

    @SuppressLint("SimpleDateFormat")
    fun Format24to12(time: String): String {

        val displayFormat = SimpleDateFormat("HH:mm")
        val parseFormat = SimpleDateFormat("hh:mm aa")
        var date: Date? = null
        try {
            date = displayFormat.parse(time)
            return parseFormat.format(date)
        } catch (e: ParseException) {
            e.printStackTrace()
        }

        return ""
    }

    @SuppressLint("SimpleDateFormat")
    fun convertDate(dat: String): String? {
        var outputDateStr: String? = null
        try {
            val inputFormat = SimpleDateFormat("yyyy-MM-dd")
            val outputFormat = SimpleDateFormat("dd-MMM-yyyy")
            val date = inputFormat.parse(dat)
            outputDateStr = outputFormat.format(date)
        } catch (e: ParseException) {
            e.printStackTrace()
        }

        return outputDateStr
    }

    fun getMonth(month: Int): String {
        return DateFormatSymbols().months[month]
    }

    @SuppressLint("SimpleDateFormat")
    fun getMonthNumber(month: String): Int {
        var date: Date? = null
        try {
            date = SimpleDateFormat("MMMM").parse(month)
        } catch (e: ParseException) {
            e.printStackTrace()
        }

        val cal = Calendar.getInstance()
        cal.time = date
        return cal.get(Calendar.MONTH)
    }

    @SuppressLint("SimpleDateFormat")
    fun getMonthNumberInc(month: String): Int {
        var date: Date? = null
        try {
            date = SimpleDateFormat("MMMM").parse(month)
        } catch (e: ParseException) {
            e.printStackTrace()
        }

        val cal = Calendar.getInstance()
        cal.time = date
        return cal.get(Calendar.MONTH) + 1
    }

    fun checkWeekOfMonday(): Boolean {
        val calendar = Calendar.getInstance()
        val dayOfWeek = calendar[Calendar.DAY_OF_WEEK]

        return Calendar.MONDAY == dayOfWeek
    }

    companion object {

        /**
         * Get current timestamp in seconds
         *
         * @return current device time in seconds
         */

        val currentDate: Date
            get() = Calendar.getInstance().time

        val timeStampInSeconds: Long
            get() = System.currentTimeMillis() / 1000

        val timeOffset: String
            get() = SimpleDateFormat("ZZZZZ", timeLocale).format(System.currentTimeMillis())

        val timeLocale: Locale
            get() = Locale(if (AppConstants.LANG_CODE == "ar") "en" else AppConstants.LANG_CODE)
    }

}
