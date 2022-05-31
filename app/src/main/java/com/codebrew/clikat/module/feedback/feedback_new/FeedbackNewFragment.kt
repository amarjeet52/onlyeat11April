package com.codebrew.clikat.module.feedback.feedback_new

import android.os.Bundle
import android.view.View
import android.view.WindowManager
import androidx.lifecycle.ViewModelProviders
import com.codebrew.clikat.BR
import com.codebrew.clikat.R
import com.codebrew.clikat.app_utils.AppToasty
import com.codebrew.clikat.app_utils.AppUtils
import com.codebrew.clikat.app_utils.extension.onSnackbar
import com.codebrew.clikat.app_utils.navController
import com.codebrew.clikat.base.BaseFragment
import com.codebrew.clikat.data.constants.PrefenceConstants
import com.codebrew.clikat.data.model.api.SuggestionData
import com.codebrew.clikat.data.model.api.SuggestionDataItem
import com.codebrew.clikat.data.preferences.PreferenceHelper
import com.codebrew.clikat.databinding.FragmentFeedbackSuggestionsBinding
import com.codebrew.clikat.di.ViewModelProviderFactory
import com.codebrew.clikat.module.feedback.FeedbackNavigator
import com.codebrew.clikat.module.feedback.FeedbackViewModel
import com.codebrew.clikat.utils.configurations.Configurations
import kotlinx.android.synthetic.main.fragment_feedback_suggestions.*
import javax.inject.Inject


class FeedbackNewFragment : BaseFragment<FragmentFeedbackSuggestionsBinding, FeedbackViewModel>(),
        FeedbackNavigator, FeedbackSuggestionsAdapter.OnSubmitSuggestion {

    @Inject
    lateinit var factory: ViewModelProviderFactory

    @Inject
    lateinit var mDataManager: PreferenceHelper

    @Inject
    lateinit var appUtils: AppUtils


    private lateinit var viewModel: FeedbackViewModel
    private var mBinding: FragmentFeedbackSuggestionsBinding? = null
    private var suggestionData: SuggestionData? = null
    private lateinit var adapter: FeedbackSuggestionsAdapter

    override fun getViewModel(): FeedbackViewModel {
        viewModel = ViewModelProviders.of(this, factory).get(FeedbackViewModel::class.java)
        return viewModel
    }

    override fun getBindingVariable(): Int {
        return BR.viewModel
    }

    override fun getLayoutId(): Int {
        return R.layout.fragment_feedback_suggestions
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.navigator = this
        mBinding = viewDataBinding
        viewDataBinding.color = Configurations.colors

        activity?.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN)
        listeners()
        setAdapter()
        hitApi()
    }

    private fun setAdapter() {
        adapter = FeedbackSuggestionsAdapter(this)
        rvSuggestions?.adapter = adapter
    }

    private fun hitApi() {
        if (isNetworkConnected)
            viewModel.apiGetSuggestions()
    }

    private fun listeners() {
        ivBack?.setOnClickListener {
            activity?.onBackPressed()
        }

        tvRequestFeature?.setOnClickListener {
            navController(this).navigate(R.id.action_add_feedback_fragment)
        }
    }


    override fun feedbackSuccess() {
        AppToasty.success(requireContext(), getString(R.string.suggestion_submitted_success))
    }

    override fun suggestionListSuccess(data: SuggestionData?) {
        suggestionData = data
        adapter.addList(data?.data ?: arrayListOf())
    }

    override fun onErrorOccur(message: String) {
        mBinding?.root?.onSnackbar(message)
    }

    override fun onSessionExpire() {
        openActivityOnTokenExpire()
    }

    override fun onSubmitSuggestion(item: SuggestionDataItem) {
        val hashMap = HashMap<String, String>()
        if (mDataManager.getCurrentUserLoggedIn()) {
            hashMap["from_user_type"] = "USER"
            hashMap["from_user_id"] =
                    mDataManager.getKeyValue(PrefenceConstants.USER_ID, PrefenceConstants.TYPE_STRING)
                            .toString()
        } else {
            hashMap["from_user_type"] = "GUEST"
            hashMap["from_user_id"] = "0"
        }

        hashMap["suggestions_assigned"] = item.name ?: ""

        if (isNetworkConnected)
            viewModel.apiAddFeedback(hashMap)
    }


}