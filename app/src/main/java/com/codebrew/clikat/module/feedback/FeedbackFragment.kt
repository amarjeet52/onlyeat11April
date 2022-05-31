package com.codebrew.clikat.module.feedback

import android.os.Bundle
import android.view.View
import android.view.WindowManager
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.Navigation
import com.codebrew.clikat.BR
import com.codebrew.clikat.R
import com.codebrew.clikat.app_utils.AppToasty
import com.codebrew.clikat.app_utils.extension.onSnackbar
import com.codebrew.clikat.base.BaseFragment
import com.codebrew.clikat.data.constants.PrefenceConstants
import com.codebrew.clikat.data.model.api.SuggestionData
import com.codebrew.clikat.data.model.api.SuggestionDataItem
import com.codebrew.clikat.data.preferences.PreferenceHelper
import com.codebrew.clikat.databinding.FragmentFeedbackFormBinding
import com.codebrew.clikat.di.ViewModelProviderFactory
import com.codebrew.clikat.utils.GeneralFunctions
import com.codebrew.clikat.utils.configurations.Configurations
import com.google.android.material.chip.Chip
import kotlinx.android.synthetic.main.fragment_feedback_form.*
import kotlinx.android.synthetic.main.fragment_feedback_form.etEmail
import kotlinx.android.synthetic.main.fragment_signup_1.*
import javax.inject.Inject


class FeedbackFragment : BaseFragment<FragmentFeedbackFormBinding, FeedbackViewModel>(), FeedbackNavigator {

    @Inject
    lateinit var factory: ViewModelProviderFactory

    @Inject
    lateinit var mDataManager: PreferenceHelper


    private lateinit var viewModel: FeedbackViewModel
    private var mBinding: FragmentFeedbackFormBinding? = null
    private var suggestionData: SuggestionData? = null
    override fun getViewModel(): FeedbackViewModel {
        viewModel = ViewModelProviders.of(this, factory).get(FeedbackViewModel::class.java)
        return viewModel
    }

    override fun getBindingVariable(): Int {
        return BR.viewModel
    }

    override fun getLayoutId(): Int {
        return R.layout.fragment_feedback_form
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.navigator = this

        mBinding = viewDataBinding
        viewDataBinding.color = Configurations.colors
        listeners()
        hitApi()
    }

    private fun hitApi() {
        if (isNetworkConnected)
            viewModel.apiGetSuggestions()
    }

    private fun listeners() {
        ivBack?.setOnClickListener {
            activity?.onBackPressed()
        }
        btnSubmit?.setOnClickListener {
            validateData()
        }
    }

    private fun validateData() {
        val title = etTitle?.text.toString().trim()
        val description = etDescription?.text.toString().trim()
        when {
            title.isEmpty() -> mBinding?.root?.onSnackbar(getString(R.string.enter_title))
            description.isEmpty() -> mBinding?.root?.onSnackbar(getString(R.string.enter_description))
            etPhone.text.isNotEmpty() || etPhone.text.isNotEmpty() || etName.text.isNotEmpty()->{
                when {
                    etName.text.toString().trim().isEmpty() -> {
                        mBinding?.root?.onSnackbar(getString(R.string.enter_name))
                    }
                    etEmail.text.toString().trim().isEmpty() -> {
                        mBinding?.root?.onSnackbar(getString(R.string.empty_email))
                    }
                    !GeneralFunctions.isValidEmail(etEmail.text.toString().trim()) -> {
                        mBinding?.root?.onSnackbar(getString(R.string.invalid_email))
                    }
                    etPhone.text.toString().trim().isEmpty() -> {
                        mBinding?.root?.onSnackbar(getString(R.string.empty_phone_number))
                    }
                    etPhone.text.length<10 -> {
                        mBinding?.root?.onSnackbar(getString(R.string.enter_valid_number))
                    }
                    else -> {
                        hitAddFeedbackApi(title, description)
                    }
                }
            }

            else -> hitAddFeedbackApi(title, description)
        }

    }

    private fun hitAddFeedbackApi(title: String, description: String) {
        val email = etEmail?.text.toString().trim()
        val phone = etPhone?.text.toString().trim()
        val name = etName?.text.toString().trim()
        val hashMap = HashMap<String, String>()

        if (email.isNotEmpty())
            hashMap["email_id"] = email

        if (phone.isNotEmpty())
            hashMap["phone"] = phone

        if (name.isNotEmpty())
            hashMap["name"] = name

        hashMap["new_suggestion_description"] = description
        hashMap["new_suggestions"] = title

        if (mDataManager.getCurrentUserLoggedIn()) {
            hashMap["from_user_type"] = "USER"
            hashMap["from_user_id"] = mDataManager.getKeyValue(PrefenceConstants.USER_ID, PrefenceConstants.TYPE_STRING).toString()
        } else {
            hashMap["from_user_type"] = "GUEST"
            hashMap["from_user_id"] = "0"
        }
        if (suggestionData != null && suggestionData?.data?.isNotEmpty() == true) {
            val stringList = ArrayList<String>()
            val list = chipGroupSuggestions?.checkedChipIds
            for (i in 0 until (suggestionData?.data?.size ?: 0)) {
                val item = list?.find { it == suggestionData?.data?.get(i)?.id }
                if (item != null)
                    stringList.add(suggestionData?.data?.get(i)?.name ?: "")
            }

            if (stringList.isNotEmpty())
                hashMap["suggestions_assigned"] = stringList.joinToString(",")
        }

        if (isNetworkConnected)
            viewModel.apiAddFeedback(hashMap)
    }


    private fun addChip(suggestion: SuggestionDataItem) {
        val chip = Chip(context)
        chip.text = suggestion.name
        chip.id = suggestion.id ?: 0
        chip.setChipBackgroundColorResource(R.color.greyE8)

        // necessary to get single selection working
        chip.isClickable = true
        chip.isCheckable = true
        chip.isCheckedIconVisible = false
        chip.setOnCheckedChangeListener { _, b ->
            if (b) {
                chip.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
                chip.setChipBackgroundColorResource(R.color.colorPrimary)
            } else {
                chip.setTextColor(ContextCompat.getColor(requireContext(), R.color.black))
                chip.setChipBackgroundColorResource(R.color.greyE8)
            }
        }
        chipGroupSuggestions.addView(chip as View)
    }

    override fun feedbackSuccess() {
        AppToasty.success(requireContext(), getString(R.string.feedback_added_successfully))
        Navigation.findNavController(requireView()).popBackStack()
    }

    override fun suggestionListSuccess(data: SuggestionData?) {
        suggestionData = data
        if (data?.data?.isNotEmpty() == true) {
            for (i in 0 until data.data.size) {
                addChip(data.data[i])
            }
        }
    }

    override fun onErrorOccur(message: String) {
        mBinding?.root?.onSnackbar(message)
    }

    override fun onSessionExpire() {
        openActivityOnTokenExpire()
    }

}