package com.codebrew.clikat.utils.configurations

import android.graphics.Color
import com.codebrew.clikat.modal.other.SettingModel.DataBean.SettingData
import java.util.regex.Pattern

class ColorConfigNight(settingFlow: SettingData?) {

    private val pattern: Pattern
    //home services
//-->DE216C
    @kotlin.jvm.JvmField
    var statusColor: String? = "#ACB1AD"

    @kotlin.jvm.JvmField
    var offwhite:String?="#80FFFFFF"

    @kotlin.jvm.JvmField
    var primaryColor: String? = "#ACB1AD"
    @kotlin.jvm.JvmField
    var appBackground = "#000000"
    @kotlin.jvm.JvmField
    var listBackground = "#000000"
    @kotlin.jvm.JvmField
    var textAppTitle: String? = "#000000"
    @kotlin.jvm.JvmField
    var textHead = textAppTitle
    @kotlin.jvm.JvmField
    var textSubhead = "#000000"
    @kotlin.jvm.JvmField
    var textBody = "#000000"
    @kotlin.jvm.JvmField
    var radioSelected = "#58dc00"
    @kotlin.jvm.JvmField
    var yellowcolor = "#FFC947"
    @kotlin.jvm.JvmField
    var supplier_bg = "#f9f9f9"
    @kotlin.jvm.JvmField
    var app_light_bg = "#f9f9f9"
    @kotlin.jvm.JvmField
    var toolbarColor: String? = "#000000"  /*212B36*/
    @kotlin.jvm.JvmField
    var toolbarText: String? = "#000000"
    @kotlin.jvm.JvmField
    var rating_color = "#ACB1AD"
    @kotlin.jvm.JvmField
    var load_more_bg = "#F6F6F6"
    @kotlin.jvm.JvmField
    var load_more_text = "#373D3E"
    @kotlin.jvm.JvmField
    var primaryColor_trans = "#80ACB1AD"
    @kotlin.jvm.JvmField
    var category_color_trans = "#4D000000"
    @kotlin.jvm.JvmField
    var fb_button = "#FF435898"
    @kotlin.jvm.JvmField
    var google_button = "#4386F4"
    @kotlin.jvm.JvmField
    var open_now = "#5AA82B"
    @kotlin.jvm.JvmField
    var acceptColor = Color.GREEN
    @kotlin.jvm.JvmField
    var rejectColor = Color.RED
    @kotlin.jvm.JvmField
    var editHint = textHead
    @kotlin.jvm.JvmField
    var editBackground = appBackground
    @kotlin.jvm.JvmField
    var editStroke = textSubhead
    @kotlin.jvm.JvmField
    var titleBackground: String? = null
    @kotlin.jvm.JvmField
    var button_background = primaryColor
    @kotlin.jvm.JvmField
    var buttonStroke = primaryColor
    @kotlin.jvm.JvmField
    var listItemBackground: String? = null
    @kotlin.jvm.JvmField
    var cardBackground: String? = null
    @kotlin.jvm.JvmField
    var navBackground = textHead
    @kotlin.jvm.JvmField
    var navHeadBackground = "#0Dffffff"
    @kotlin.jvm.JvmField
    var gridBackground: String? = null
    @kotlin.jvm.JvmField
    var divider = "#000000"
    @kotlin.jvm.JvmField
    var productTitlBackground: String? = null
    @kotlin.jvm.JvmField
    var tabBackground: String? = null
    @kotlin.jvm.JvmField
    var tabSelected = primaryColor
    @kotlin.jvm.JvmField
    var tabUnSelected = "#909090"
    @kotlin.jvm.JvmField
    var homelistBackground = "#F5F7FB"
    @kotlin.jvm.JvmField
    var navTextColor = appBackground
    @kotlin.jvm.JvmField
    var navSubTextColor = appBackground
    @kotlin.jvm.JvmField
    var textListHead = textHead
    @kotlin.jvm.JvmField
    var textListSubhead = textSubhead
    @kotlin.jvm.JvmField
    var textListBody = textBody
    @kotlin.jvm.JvmField
    var icon_light = textAppTitle
    @kotlin.jvm.JvmField
    var view_pager = appBackground
    @kotlin.jvm.JvmField
    var nav_color = primaryColor
    @kotlin.jvm.JvmField
    var search_background = "#F4F4F4"
    @kotlin.jvm.JvmField
    var search_textcolor = "#80000000"
    @kotlin.jvm.JvmField
    var transparent_color = "#ffffff00"
    @kotlin.jvm.JvmField
    var rental_color = "#01AFFF"
    @kotlin.jvm.JvmField
    var food_rate_color = "#ED8A19"
    @kotlin.jvm.JvmField
    var image_background = "#1D000000"

    @kotlin.jvm.JvmField
    var toolbarTabIndicatorColor: String? = "#FFFFFF"

    private fun validate(hexColorCode: String?): Boolean {
        return if(hexColorCode.isNullOrEmpty())
        {
            false
        }else{
            val matcher = pattern.matcher(hexColorCode)
            matcher.matches()
        }
    }

    companion object {
        private const val HEX_PATTERN = "^#([A-Fa-f0-9]{6}|[A-Fa-f0-9]{3})$"
    }

    init {
        pattern = Pattern.compile(HEX_PATTERN)
        primaryColor = if (validate(settingFlow?.theme_color)) settingFlow?.theme_color else "#ACB1AD"
        textAppTitle = if (validate(settingFlow?.element_color)) settingFlow?.element_color else "#515151"
        toolbarColor = if (validate(settingFlow?.header_color)) settingFlow?.header_color else "#212B36"
        toolbarText = if (validate(settingFlow?.header_text_color)) settingFlow?.header_text_color else "#000000"

        if(settingFlow?.zipTheme=="1")
        {
            supplier_bg=StringBuilder(primaryColor?:"").insert(1,"33").toString()
        }
        app_light_bg=StringBuilder(primaryColor?:"").insert(1,"33").toString()
        primaryColor_trans=StringBuilder(primaryColor?:"").insert(1,"80").toString()
        navSubTextColor=StringBuilder(primaryColor?:"").insert(1,"A6").toString()
    }
}