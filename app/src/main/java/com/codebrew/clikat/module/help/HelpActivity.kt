package com.codebrew.clikat.module.help

import android.os.Bundle
import androidx.lifecycle.ViewModelProviders
import com.codebrew.clikat.BR
import com.codebrew.clikat.R
import com.codebrew.clikat.app_utils.extension.launchActivity
import com.codebrew.clikat.base.BaseActivity
import com.codebrew.clikat.data.ReceiverType
import com.codebrew.clikat.data.model.api.orderDetail.Agent
import com.codebrew.clikat.data.preferences.PreferenceHelper
import com.codebrew.clikat.databinding.FragmentHelpBinding
import com.codebrew.clikat.di.ViewModelProviderFactory
import com.codebrew.clikat.modal.PojoSignUp
import com.codebrew.clikat.module.feedback.FeedbackViewModel
import com.codebrew.clikat.preferences.DataNames
import com.codebrew.clikat.user_chat.UserChatActivity
import javax.inject.Inject

class HelpActivity : BaseActivity<FragmentHelpBinding, FeedbackViewModel>(),HelpAdapter.HelpListener {
    @Inject
    lateinit var factory: ViewModelProviderFactory

    @Inject
    lateinit var mDataManager: PreferenceHelper

    private var mBinding: FragmentHelpBinding? = null

    private lateinit var viewModel: FeedbackViewModel
    var helpList: ArrayList<HelpModel> = ArrayList()
    override fun getBindingVariable(): Int {
        return BR.viewModel

    }

    override fun getLayoutId(): Int {
        return R.layout.fragment_help
    }

    override fun getViewModel(): FeedbackViewModel {
        viewModel = ViewModelProviders.of(this, factory).get(FeedbackViewModel::class.java)
        return viewModel
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mBinding = viewDataBinding
        setHelpList()
        var helpAdapter=HelpAdapter(helpList,this,this@HelpActivity)
        mBinding!!.rvHelp.adapter=helpAdapter
    }

    fun setHelpList() {
        var helpModel = HelpModel(1, "I haven't received this order")
        helpList.add(helpModel)
        helpModel = HelpModel(2, "I have payment, refund and bill related queries for this order")
        helpList.add(helpModel)
        helpModel = HelpModel(3, "The quantity of food is not adequate")
        helpList.add(helpModel)
        helpModel = HelpModel(4, "I have coupon related queries for this order")
        helpList.add(helpModel)
        helpModel = HelpModel(5, "Report missing items in my order")
        helpList.add(helpModel)
        helpModel = HelpModel(6, "Items are different from what I ordered")
        helpList.add(helpModel)
        helpModel = HelpModel(7, "Report incorrectly cooked items in my order")
        helpList.add(helpModel)
        helpModel = HelpModel(8, "I have packaging or spillage issue with this order")
        helpList.add(helpModel)
        helpModel = HelpModel(9, "I want to report an issue related to my Delivery Partner")
        helpList.add(helpModel)
    }

    override fun onClickListen() {
        val userInfo = mDataManager.getGsonValue(DataNames.USER_DATA, PojoSignUp::class.java)
        val data = Agent()
        data.message_id = userInfo?.data?.message_id
        data.name = getString(R.string.admin)
        launchActivity<UserChatActivity> {
            putExtra("userType", ReceiverType.ADMIN.type)
            putExtra("userData", data)
        }
    }
}