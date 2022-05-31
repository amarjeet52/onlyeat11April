package com.codebrew.clikat.module.change_language


import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.codebrew.clikat.R
import com.codebrew.clikat.app_utils.extension.launchActivity
import com.codebrew.clikat.base.BaseDialog
import com.codebrew.clikat.data.DataManager
import com.codebrew.clikat.databinding.LayoutSkipChangeLanguageBinding
import com.codebrew.clikat.modal.AppGlobal
import com.codebrew.clikat.modal.other.SettingModel
import com.codebrew.clikat.module.bottom_navigation.MainScreenActivity
import com.codebrew.clikat.preferences.DataNames
import com.codebrew.clikat.utils.GeneralFunctions
import com.codebrew.clikat.utils.configurations.Configurations
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import dagger.android.support.AndroidSupportInjection
import kotlinx.android.synthetic.main.layout_skip_change_language.*
import java.util.*
import javax.inject.Inject

/**
 * A simple [Fragment] subclass.
 */

class SkipChangeLanguage : BaseDialog() {

    @Inject
    lateinit var dataManager: DataManager

    private var mBinding: LayoutSkipChangeLanguageBinding? = null

    var settingData: SettingModel.DataBean.SettingData? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment

        mBinding = DataBindingUtil.inflate(inflater, R.layout.layout_skip_change_language, container, false)
        mBinding?.color = Configurations.colors
        return mBinding?.root
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidSupportInjection.inject(this)
        super.onCreate(savedInstanceState)

        settingData = dataManager.getGsonValue(DataNames.SETTING_DATA, SettingModel.DataBean.SettingData::class.java)
    }
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = BottomSheetDialog(requireContext(), theme)
        dialog.behavior.state = BottomSheetBehavior.STATE_EXPANDED
        return dialog
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        tv_first_lang.text = getString(R.string.english)

        settingData?.secondary_language?.let {
            if (it != "0") {
                val locale = Locale(it)
                tv_second_lang.text = //locale.displayLanguage.toLowerCase(DateTimeUtils.timeLocale)
                locale.displayLanguage
            }
        }



        tv_first_lang.setOnClickListener {
            selectLanguageId("en")
        }

        tv_second_lang.setOnClickListener {
            settingData?.secondary_language?.let {
                selectLanguageId(it)
            }
        }

        btnCancel.setOnClickListener {
            dismiss()
        }

    }


    private fun selectLanguageId(langCode: String) {
        AppGlobal.localeManager?.setNewLocale(activity?:requireContext(), langCode)

        when (langCode) {
            "arabic","ar" -> {
                GeneralFunctions.force_layout_to_RTL(activity as AppCompatActivity?)
            }
            "spanish","español","es" -> {
                GeneralFunctions.force_layout_to_LTR(activity as AppCompatActivity?)
            }
            else -> {
                GeneralFunctions.force_layout_to_LTR(activity as AppCompatActivity?)
            }
        }
        dismiss()
        activity?.finishAffinity()
        activity?.launchActivity<MainScreenActivity>()
    }

    companion object {

        @JvmStatic
        fun newInstance() =
                SkipChangeLanguage().apply {
                    arguments = Bundle()

                }

    }


}
