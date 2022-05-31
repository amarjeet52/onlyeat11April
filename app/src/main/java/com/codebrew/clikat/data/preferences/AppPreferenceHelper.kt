package com.codebrew.clikat.data.preferences


import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import com.codebrew.clikat.data.constants.PrefenceConstants
import com.codebrew.clikat.data.model.others.GlobalTableDataHolder
import com.codebrew.clikat.di.PreferenceInfo
import com.codebrew.clikat.modal.other.SettingModel
import com.codebrew.clikat.preferences.DataNames
import com.codebrew.clikat.utils.ClikatConstants
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import javax.inject.Inject

class AppPreferenceHelper @Inject constructor(
        private val context: Context,
        @PreferenceInfo private val prefFileName: String
) : PreferenceHelper {


    private val mPrefs: SharedPreferences = context.getSharedPreferences(prefFileName, Context.MODE_PRIVATE)


    override fun setkeyValue(key: String, value: Any) {

        if (value is Int) {
            mPrefs.edit().putInt(key, value).apply()
        }
        else if (value is Boolean) {
            mPrefs.edit().putBoolean(key, value).apply()
        }
        else if (value is String) {
            mPrefs.edit().putString(key, value).apply()
        }
    }

    override fun addGsonValue(key: String, value: String) {
        mPrefs.edit().putString(key, value).apply()
    }

    override fun <T> getGsonValue(key: String, type: Class<T>): T? {
        val gson = mPrefs.getString(key, null)
        return if (gson == null) {
            null
        } else {
            try {

                Gson().fromJson(gson, type)
            } catch (e: Exception) {
                throw IllegalArgumentException(
                        "Object storaged with key "
                                + key + " is instanceof other class"
                )
            }

        }
    }


    override fun getKeyValue(key: String, type: String): Any? {


        return when (type) {
            PrefenceConstants.TYPE_STRING -> mPrefs.getString(key, "")
            PrefenceConstants.TYPE_BOOLEAN -> mPrefs.getBoolean(key, false)
            PrefenceConstants.TYPE_INT -> mPrefs.getInt(key, 0)
            else -> null
        }


    }


    override fun logout() {

        setkeyValue(PrefenceConstants.ACCESS_TOKEN, "")
        setkeyValue(PrefenceConstants.USER_LOGGED_IN, false)
        setkeyValue(PrefenceConstants.NET_TOTAL, "")
        setkeyValue(PrefenceConstants.USER_NAME, "")
        setkeyValue(PrefenceConstants.USER_ID, "")
        setkeyValue(DataNames.USER_DATA, "")
        setkeyValue(DataNames.CART, "")
        setkeyValue(DataNames.CART_ID, "")
        setkeyValue(DataNames.AGENT_API_KEY, "")
        setkeyValue(DataNames.AGENT_DB_SECRET, "")

        removeValue(DataNames.Pref_Cart_Quantity)
        removeValue(DataNames.Pref_Cart_Quantity_Sallon_Home)
        removeValue(DataNames.Pref_Cart_Quantity_Sallon_Place)
        removeValue(DataNames.Pref_Cart_Quantity_laundry)
        removeValue(DataNames.CATEGORY_ID_TEST)
        removeValue(DataNames.CART)
        removeValue("netTotalLaundry")
        //removeValue(PrefenceConstants.ADRS_DATA)
        removeValue(DataNames.LocationUser)
    }

    override fun removeValue(key: String) {
        mPrefs.edit {
            remove(key)?.apply()
        }
    }

    override fun onClear() {
        mPrefs.edit()?.clear()?.apply()
    }

    override fun onCartClear() {
        setkeyValue(PrefenceConstants.NET_TOTAL, "")
        setkeyValue(DataNames.CART_ID, "")
        removeValue(DataNames.Pref_Cart_Quantity)
        removeValue(DataNames.Pref_Cart_Quantity_Sallon_Home)
        removeValue(DataNames.Pref_Cart_Quantity_Sallon_Place)
        removeValue(DataNames.Pref_Cart_Quantity_laundry)
        removeValue(DataNames.CART)
        removeValue("netTotalLaundry")
    }

    override fun getCurrentUserLoggedIn(): Boolean {
        return mPrefs.getBoolean(PrefenceConstants.USER_LOGGED_IN, false)
    }

    override fun getCurrentTableData(): GlobalTableDataHolder? {
        val invitation: String? = mPrefs.getString(DataNames.INVITATTON_DATA, null)
        if (invitation?.isEmpty() == true) {
            return null
        }
        val type = object : TypeToken<GlobalTableDataHolder?>() {}.type
        return Gson().fromJson(invitation, type)
    }

    override fun getLangCode(): String {
        return when (getKeyValue(DataNames.SELECTED_LANGUAGE, PrefenceConstants.TYPE_STRING).toString()) {
            ClikatConstants.ENGLISH_SHORT, ClikatConstants.ENGLISH_FULL -> ClikatConstants.LANGUAGE_ENGLISH
            else -> ClikatConstants.LANGUAGE_OTHER
        }.toString()
    }

    override fun isBranchFlow(): Boolean {
        val settingData = getGsonValue(DataNames.SETTING_DATA, SettingModel.DataBean.SettingData::class.java)
        settingData?.branch_flow?.let {
            return it == "1"
        }
        return false
    }

    override fun isSubcriptionEnded(): Boolean {
        return mPrefs.getBoolean(PrefenceConstants.IS_BLOCK, false)
    }
}