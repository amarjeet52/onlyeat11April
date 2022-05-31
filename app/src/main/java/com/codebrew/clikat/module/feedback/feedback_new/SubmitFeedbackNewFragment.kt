package com.codebrew.clikat.module.feedback.feedback_new

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.view.WindowManager
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProviders
import com.codebrew.clikat.R
import com.codebrew.clikat.app_utils.AppUtils
import com.codebrew.clikat.app_utils.extension.onSnackbar
import com.codebrew.clikat.base.BaseFragment
import com.codebrew.clikat.data.constants.PrefenceConstants
import com.codebrew.clikat.data.model.api.SuggestionData
import com.codebrew.clikat.data.preferences.PreferenceHelper
import com.codebrew.clikat.databinding.FragmentAddFeedbackBinding
import com.codebrew.clikat.di.ViewModelProviderFactory
import com.codebrew.clikat.module.feedback.FeedbackNavigator
import com.codebrew.clikat.module.feedback.FeedbackViewModel
import com.codebrew.clikat.utils.configurations.Configurations
import com.codebrew.clikat.BR

import kotlinx.android.synthetic.main.fragment_add_feedback.*
import javax.inject.Inject


class SubmitFeedbackNewFragment : BaseFragment<FragmentAddFeedbackBinding, FeedbackViewModel>(),
        FeedbackNavigator {

    @Inject
    lateinit var factory: ViewModelProviderFactory

    @Inject
    lateinit var mDataManager: PreferenceHelper

    @Inject
    lateinit var appUtils: AppUtils


    private lateinit var viewModel: FeedbackViewModel
    private var mBinding: FragmentAddFeedbackBinding? = null

    override fun getViewModel(): FeedbackViewModel {
        viewModel = ViewModelProviders.of(this, factory).get(FeedbackViewModel::class.java)
        return viewModel
    }

    override fun getBindingVariable(): Int {
        return BR.viewModel
    }

    override fun getLayoutId(): Int {
        return R.layout.fragment_add_feedback
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.navigator = this
        mBinding = viewDataBinding
        viewDataBinding.color = Configurations.colors

        activity?.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN)
        listeners()
    }


    private fun listeners() {
        ivBack?.setOnClickListener {
            activity?.onBackPressed()
        }
        btnSubmit?.setOnClickListener {
            validateData()
        }
        checkBox?.setOnCheckedChangeListener { _, b ->
            groupBottom?.visibility = if (b) View.VISIBLE else View.GONE

        }
    }

    private fun validateData() {
        val title = etFeature?.text.toString().trim()
        val description = etDescription?.text.toString().trim()
        when {
            title.isEmpty() ->
                mBinding?.root?.onSnackbar(getString(R.string.enter_feature))
            description.isEmpty() -> mBinding?.root?.onSnackbar(getString(R.string.enter_description))
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

        if(mDataManager.getCurrentUserLoggedIn()){
            hashMap["from_user_type"] = "USER"
            hashMap["from_user_id"] =
                    mDataManager.getKeyValue(PrefenceConstants.USER_ID, PrefenceConstants.TYPE_STRING)
                            .toString()
        }else{
            hashMap["from_user_type"] = "GUEST"
            hashMap["from_user_id"] = "0"
        }
        if (isNetworkConnected)
            viewModel.apiAddFeedback(hashMap)
    }


    override fun feedbackSuccess() {
        layoutFeedback?.visibility = View.VISIBLE
        nsLayout?.visibility = View.GONE
        clMain?.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.white))
        Handler(Looper.getMainLooper()).postDelayed({
            activity?.onBackPressed()
        }, 2000)

    }

    override fun suggestionListSuccess(data: SuggestionData?) {
        // do nothing
    }


    override fun onErrorOccur(message: String) {
        mBinding?.root?.onSnackbar(message)
    }

    override fun onSessionExpire() {
        openActivityOnTokenExpire()
    }

}