package com.codebrew.clikat.utils

import android.app.Activity
import android.content.Context
import android.content.res.Configuration
import android.content.res.Resources
import android.net.Uri
import android.os.Build
import android.text.Html
import android.text.Spanned
import android.util.DisplayMetrics
import android.view.View
import android.view.ViewTreeObserver
import android.view.inputmethod.InputMethodManager
import android.widget.ImageView
import androidx.annotation.StringRes
import androidx.browser.customtabs.CustomTabColorSchemeParams
import androidx.browser.customtabs.CustomTabsIntent
import androidx.browser.trusted.TrustedWebActivityDisplayMode
import androidx.browser.trusted.TrustedWebActivityIntentBuilder
import androidx.core.content.ContextCompat
import com.codebrew.clikat.R
import com.codebrew.clikat.data.FoodAppType
import com.codebrew.clikat.data.constants.AppConstants
import com.codebrew.clikat.data.model.api.Currency
import com.codebrew.clikat.data.model.others.ImageViewLength
import com.codebrew.clikat.modal.other.SettingModel
import com.codebrew.clikat.preferences.DataNames
import com.codebrew.clikat.preferences.Prefs
import com.google.android.libraries.places.api.model.RectangularBounds
import com.google.androidbrowserhelper.trusted.TwaLauncher
import com.google.gson.Gson
import com.google.maps.android.SphericalUtil
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.math.absoluteValue


object Utils {
    fun pxToDp(context: Context, px: Int): Int {
        val displayMetrics = context.resources.displayMetrics
        return Math.round(px / (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT))
    }

    fun dpToPx(dp: Int): Float {
        return (dp * Resources.getSystem().displayMetrics.density)
    }


    fun getHtmlData(text: String): Spanned {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            Html.fromHtml(text, Html.FROM_HTML_MODE_COMPACT)
        } else {
            Html.fromHtml(text)
        }
    }

    fun checkApp(context: Context): Boolean {
        val clientInform = Prefs.with(context).getObject(DataNames.SETTING_DATA, SettingModel.DataBean.SettingData::class.java)
        return clientInform?.is_carveQatar_home_theme == "1"
    }

    fun getScreenWidth(activity: Activity): Int {
        val displayMetrics = DisplayMetrics()
        activity.windowManager.defaultDisplay.getMetrics(displayMetrics)
        return displayMetrics.widthPixels
    }

    public fun getScreenHeight(activity: Activity): Int {
        val displayMetrics = DisplayMetrics()
        activity.windowManager.defaultDisplay.getMetrics(displayMetrics)
        return displayMetrics.heightPixels
    }

    fun hideKeyboard(activity: Activity) {
        val imm = activity.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        //Find the currently focused view, so we can grab the correct window token from it.
        var view = activity.currentFocus
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = View(activity)
        }
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }

//    fun getPriceFormat(price: Float): String {
//        return getPriceFormat(price,null)
//    }

    fun getPriceFormat(price: Float, clientInform: SettingModel.DataBean.SettingData?, selectedCurrency: Currency?): String {
        val conversionRate = getConversionRate(clientInform, selectedCurrency)
        if (clientInform?.is_round_off_disable == "1") {
            return String.format(Locale("en"), "%.0f", price.times(conversionRate))
        }

        return if (AppConstants.CURRENCY_SYMBOL == "BHD") {
            String.format(Locale("en"), "%.3f", price.times(conversionRate))
        } else {
            String.format(Locale("en"), "%.2f", price.times(conversionRate))
        }
    }

    fun getDiscountPrice(price: Float, loyaltyDiscount: Float?, clientInform: SettingModel.DataBean.SettingData?): Float {
        val fixedPrice = if (clientInform?.loyality_discount_on_product_listing == "1" && loyaltyDiscount != null && loyaltyDiscount != 0f)
            price.minus(loyaltyDiscount) else price
        return if (fixedPrice > 0) fixedPrice else 0f
    }

    fun getPriceFormatWithoutConversion(price: Float, clientInform: SettingModel.DataBean.SettingData?): String {
        if (clientInform?.is_round_off_disable == "1") {
            return String.format(Locale("en"), "%.0f", price)
        }

        return if (AppConstants.CURRENCY_SYMBOL == "BHD") {
            String.format(Locale("en"), "%.3f", price)
        } else {
            String.format(Locale("en"), "%.2f", price)
        }
    }

    fun getDecimalPointValue(clientInform: SettingModel.DataBean.SettingData?, total: Float?): String {
        return when (clientInform?.is_decimal_quantity_allowed) {
            "1" -> String.format(Locale("en"), "%.2f", total)
            else -> (total?.toInt()).toString()
        }
    }

    fun getConversionRate(clientInform: SettingModel.DataBean.SettingData?, selectedCurrency: Currency?): Float {
        return if (clientInform?.is_multicurrency_enable == "1" && selectedCurrency != null &&
                selectedCurrency.conversion_rate != null && selectedCurrency.conversion_rate != 0f)
            selectedCurrency.conversion_rate else 1f
    }

    fun isAtLeastVersion(version: Int): Boolean {
        return Build.VERSION.SDK_INT >= version
    }

    fun getRectangularBound(): RectangularBounds {
        // in meter
        val radius = 50000.0
// optional: to get distance to circle radius, not the edge
        val distance = radius * kotlin.math.sqrt(2.0)

        val ne = SphericalUtil.computeOffset(AppConstants.USER_LATLNG, distance, 45.0)
        val sw = SphericalUtil.computeOffset(AppConstants.USER_LATLNG, distance, 225.0)

        return RectangularBounds.newInstance(sw, ne)
    }

    fun Context.getStringByLocale(@StringRes stringRes: Int, locale: Locale, vararg formatArgs: Any): String {
        val configuration = Configuration(resources.configuration)
        configuration.setLocale(locale)
        return createConfigurationContext(configuration).resources.getString(stringRes, *formatArgs)
    }

    fun openWebView(context: Context, url: String) {
        val darkModeColorScheme = CustomTabColorSchemeParams.Builder()
                .setToolbarColor(ContextCompat.getColor(context, R.color.colorPrimaryDark))
                .setNavigationBarColor(ContextCompat.getColor(context, R.color.colorPrimaryDark))
                .build()

        val twaBuilder = TrustedWebActivityIntentBuilder(Uri.parse(url))
                .setToolbarColor(ContextCompat.getColor(context, R.color.colorPrimary))
                .setNavigationBarColor(ContextCompat.getColor(context, R.color.colorPrimaryDark))
                .setColorScheme(CustomTabsIntent.COLOR_SCHEME_SYSTEM)
                .setColorSchemeParams(
                        CustomTabsIntent.COLOR_SCHEME_DARK, darkModeColorScheme
                )
                .setDisplayMode(TrustedWebActivityDisplayMode.DefaultMode())

        /*val mTwaLauncher = TwaLauncher(context)
        mTwaLauncher.launch(
                twaBuilder,
                null,
                Runnable,
                TwaLauncher.CCT_FALLBACK_STRATEGY
        )*/
    }

    fun convertMinute(minute: Long?): String {
        val timeFormat = StringBuilder()

        if (TimeUnit.MINUTES.toDays(minute ?: 0) > 0) {
            timeFormat.append("${TimeUnit.MINUTES.toDays(minute ?: 0)} day")
        }

        if (TimeUnit.MINUTES.toHours(minute ?: 0) > 0) {
            timeFormat.append("${TimeUnit.MINUTES.toHours(minute ?: 0)} hour")
        }

        if (minute ?: 0 > 60) {
            if (minute?.rem(60) ?: 0 > 0)
                timeFormat.append("${minute?.rem(60)} minute")
        } else {
            timeFormat.append("$minute minute")
        }

        return timeFormat.toString()

    }

    fun getDeliveryType(mDeliveryType: Int): String {
        return when (mDeliveryType) {
            FoodAppType.Pickup.foodType -> "pickup"
            FoodAppType.DineIn.foodType -> "dineIn"
            FoodAppType.Both.foodType -> "both"
            else -> "delivery"
        }
    }

    fun loadAppPlaceholder(mData: String): SettingModel.DataBean.AppIcon? {
        val mGson = Gson()

        return if (mData.isBlank()) {
            null
        } else {
            mGson.fromJson(mData, SettingModel.DataBean.AppIcon::class.java)
        }
    }

    fun getImageHeightWidth(imageView: ImageView): ImageViewLength {
        val imageLength = ImageViewLength()
        imageView.viewTreeObserver.addOnPreDrawListener(object : ViewTreeObserver.OnPreDrawListener {
            override fun onPreDraw(): Boolean {
                imageView.viewTreeObserver.removeOnPreDrawListener(this)
                imageLength.height = imageView.measuredHeight.toDouble()
                imageLength.width = imageView.measuredWidth.toDouble()
                return true
            }
        })
        return imageLength
    }

    fun printDifference(startDate: Date?, endDate: Date?): String {
        //milliseconds
        var different = (endDate?.time ?: Long.MIN_VALUE) - (startDate?.time ?: Long.MIN_VALUE)
        println("startDate : $startDate")
        println("endDate : $endDate")
        println("different : $different")
        val secondsInMilli: Long = 1000
        val minutesInMilli = secondsInMilli * 60
        val hoursInMilli = minutesInMilli * 60
        val daysInMilli = hoursInMilli * 24
        val elapsedDays = different / daysInMilli
        different %= daysInMilli
        val elapsedHours = different / hoursInMilli
        different %= hoursInMilli
        val elapsedMinutes = different / minutesInMilli
        different %= minutesInMilli
        val elapsedSeconds = different / secondsInMilli

        return when {
            elapsedDays.absoluteValue > 0 -> {
                "${elapsedDays.absoluteValue} days ago"
            }
            elapsedHours.absoluteValue > 0 -> {
                "${elapsedHours.absoluteValue} hours ago"
            }
            elapsedMinutes.absoluteValue > 0 -> {
                "${elapsedMinutes.absoluteValue} minutes ago"
            }
            else -> {
                "${elapsedSeconds.absoluteValue} seconds ago"
            }
        }
    }


}



