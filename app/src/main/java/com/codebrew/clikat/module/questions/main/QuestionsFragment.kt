package com.codebrew.clikat.module.questions.main

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.NavOptions
import androidx.viewpager2.widget.ViewPager2
import com.codebrew.clikat.BR
import com.codebrew.clikat.R
import com.codebrew.clikat.app_utils.AppUtils
import com.codebrew.clikat.app_utils.ProdUtils
import com.codebrew.clikat.app_utils.extension.launchActivity
import com.codebrew.clikat.app_utils.extension.onSnackbar
import com.codebrew.clikat.app_utils.navController
import com.codebrew.clikat.base.BaseFragment
import com.codebrew.clikat.base.BaseInterface
import com.codebrew.clikat.data.AppDataType
import com.codebrew.clikat.data.VendorAppType
import com.codebrew.clikat.data.constants.AppConstants
import com.codebrew.clikat.data.model.api.QuestionList
import com.codebrew.clikat.data.model.others.AgentCustomParam
import com.codebrew.clikat.data.preferences.PreferenceHelper
import com.codebrew.clikat.databinding.FragmentQuestioneriesLayoutBinding
import com.codebrew.clikat.di.ViewModelProviderFactory
import com.codebrew.clikat.modal.other.ProductDataBean
import com.codebrew.clikat.modal.other.SettingModel
import com.codebrew.clikat.module.questions.adapters.PagerAdapterQuestioneries
import com.codebrew.clikat.module.questions.adapters.questionAnswer.QuestionAnswerFragment
import com.codebrew.clikat.module.service_selection.ServSelectionActivity
import com.codebrew.clikat.preferences.DataNames
import com.codebrew.clikat.utils.StaticFunction
import com.codebrew.clikat.utils.configurations.Configurations
import kotlinx.android.synthetic.main.fragment_questioneries_layout.*
import javax.inject.Inject

class QuestionsFragment : BaseFragment<FragmentQuestioneriesLayoutBinding, QuestionsViewModel>(), BaseInterface {


    private lateinit var viewModel: QuestionsViewModel

    @Inject
    lateinit var factory: ViewModelProviderFactory

    @Inject
    lateinit var prodUtils: ProdUtils

    @Inject
    lateinit var prefHelper: PreferenceHelper

    private var mBinding: FragmentQuestioneriesLayoutBinding? = null

    @Inject
    lateinit var appUtils: AppUtils

    var list = arrayListOf<Fragment>()
    var lastPosition = 0

    var question_Data = arrayListOf<QuestionList>()

    var subCategoryId = 0

    lateinit var adapter: PagerAdapterQuestioneries

    var productBean: ProductDataBean? = null

    var isCategory = false

    private var screenFlowBean: SettingModel.DataBean.ScreenFlowBean? = null
    private var settingData:SettingModel.DataBean.SettingData?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.navigator = this


        screenFlowBean = prefHelper.getGsonValue(DataNames.SCREEN_FLOW, SettingModel.DataBean.ScreenFlowBean::class.java)
        settingData = prefHelper.getGsonValue(DataNames.SETTING_DATA, SettingModel.DataBean.SettingData::class.java)
        val bundle = arguments
        if (bundle != null) {
            subCategoryId = bundle.getInt("subCategoryId", 0)

            isCategory = bundle.getBoolean("is_Category", false)

            if (bundle.containsKey("productBean")) {
                productBean = arguments?.getParcelable("productBean")
            }

        }

        questionsObserver()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mBinding?.color = Configurations.colors

        adapter = PagerAdapterQuestioneries(this, requireActivity(), list)

        pager.adapter = adapter

        pager.isUserInputEnabled = false
        pager.isSaveFromParentEnabled = false

        btn_next.setOnClickListener {
            if (answerValidations()) {
                if (lastPosition < list.count() - 1) {
                    lastPosition += 1
                    pager.setCurrentItem(lastPosition, true)
                } else {
                    nextPage()
                }
            } else Toast.makeText(activity, getString(R.string.select_answer), Toast.LENGTH_SHORT).show()

        }

        btn_back.setOnClickListener {
            lastPosition -= 1
            pager.setCurrentItem(lastPosition, true)
        }

        // if (question_Data.size == 0)
        getListOfQuestions(subCategoryId)


        pager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {

            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                // Log.e("Selected_Page", position.toString())

                btn_next.visibility = View.VISIBLE

                val currentFragment = list[position] as QuestionAnswerFragment
                currentFragment.setQuestion(question_Data[position])
                tv_page_no.text = "${position + 1} / ${list.count()}"
                if (position == 0) {
                    btn_back.visibility = View.GONE
                } else {
                    btn_back.visibility = View.VISIBLE
                }

                lastPosition = position
            }

        })


    }


    private fun questionsObserver() {
        // Create the observer which updates the UI.
        val catObserver = Observer<MutableList<QuestionList>> { resource ->
            setQuestions(resource)
        }

        // Observe the LiveData, passing in this activity as the LifecycleOwner and the observer.viewModel.questionsLiveData.observe(this, catObserver)
        viewModel.questionsLiveData.observe(this, catObserver)
    }

    private fun answerValidations(): Boolean {
        return question_Data[lastPosition].optionsList.any { it.isChecked }
    }

    private fun nextPage() {

        val bundle = arguments

        if (isCategory) {

            bundle?.putBoolean("isFromQuestion", true)
            bundle?.putParcelableArrayList("question_list", question_Data)


            val navOptions = NavOptions.Builder()
                    .setPopUpTo(R.id.subCategory, false)
                    .build()

            if (screenFlowBean?.is_single_vendor == VendorAppType.Multiple.appType && arguments?.getBoolean("is_supplier") == false) {
                bundle?.putInt("categoryId", bundle.getInt("categoryId", 0))
                bundle?.putInt("subCatId", subCategoryId)

                navController(this@QuestionsFragment).navigate(R.id.action_questionFrag_to_supplierAll, bundle, navOptions)
            } else {
                bundle?.putBoolean("has_subcat", true)
                navController(this@QuestionsFragment).navigate(R.id.action_questionFrag_to_productTabListing, bundle, navOptions)
            }
        } else {

            productBean?.type = screenFlowBean?.app_type
            productBean?.selectQuestAns = question_Data
            if(screenFlowBean?.app_type==AppDataType.HomeServ.type && settingData?.enable_freelancer_flow=="1"){
                if(productBean?.prod_quantity==0f)
                    productBean.apply { prodUtils.addItemToCart(productBean,false) }
                bookNow(productBean?: ProductDataBean())
            }else {
                productBean.apply { prodUtils.addItemToCart(productBean) }

                val navOptions = NavOptions.Builder()
                        .setPopUpTo(R.id.homeFragment, false)
                        .build()

                navController(this@QuestionsFragment).navigate(R.id.action_questionFrag_to_cart, bundle, navOptions)
            }
        }
    }

    private fun bookNow(bean: ProductDataBean) {
        if (bean.prod_quantity ?: 0f > 0f) {
            val productIds = ArrayList<String>()
            productIds.add(bean.product_id.toString())
            activity?.launchActivity<ServSelectionActivity>(AppConstants.REQUEST_AGENT_DETAIL)
            {
                putExtra("screenType", "productList")
                putExtra("serviceData", bean)
                putExtra("productIds", productIds.toTypedArray())
            }

        } else {
            mBinding?.root?.onSnackbar(getString(R.string.add_quantity))
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == AppConstants.REQUEST_AGENT_DETAIL && resultCode == Activity.RESULT_OK) {
            val dataAgent = data?.getParcelableExtra<AgentCustomParam>("agentData")

            val productBean=data?.getParcelableExtra<ProductDataBean>("serviceData")

            if (productBean != null) {
                productBean.agentDetail=dataAgent
                if(productBean.agentBufferPrice!=null)
                    productBean.netPrice=(productBean.netPrice ?: 0.0f).plus(productBean.agentBufferPrice?.toFloat()?:0f)

                if (!appUtils.checkProdExistance(productBean.product_id))
                    appUtils.addProductDb(requireContext(), screenFlowBean?.app_type
                            ?: 0,productBean)
                else
                    StaticFunction.updateCart(requireContext(), productBean.product_id, productBean.prod_quantity,
                            productBean.netPrice ?: 0f)


                navController(this@QuestionsFragment).navigate(R.id.action_questionFrag_to_cart)


            }

        }
    }
    private fun setQuestions(resource: MutableList<QuestionList>?) {
        list.clear()

        question_Data.clear()
        question_Data.addAll(resource ?: mutableListOf())

        btn_next.visibility = View.INVISIBLE

        if (list.isEmpty()) {
            for (i in question_Data.indices) {
                list.add(QuestionAnswerFragment())
            }
        }

        adapter.notifyDataSetChanged()

        if (list.size > 0) {
            val currentFragment = list.first() as QuestionAnswerFragment
            currentFragment.setQuestion(question_Data.first())
            tv_page_no.text = "${1} / ${list.count()}"
            btn_next.visibility = View.VISIBLE
        } else mBinding?.root?.onSnackbar(getString(R.string.no_quetion_found))


        btn_back.visibility = View.INVISIBLE

    }


    private fun getListOfQuestions(categoryId: Int) {

        if (viewModel.questionsLiveData.value == null) {

            viewModel.validateQuestion(categoryId.toString())
        } else {

            setQuestions(viewModel.questionsLiveData.value)
        }
    }

    override fun getBindingVariable(): Int {
        return BR.viewModel
    }

    override fun getLayoutId(): Int {
        return R.layout.fragment_questioneries_layout

    }

    override fun getViewModel(): QuestionsViewModel {
        viewModel = ViewModelProviders.of(this, factory).get(QuestionsViewModel::class.java)
        return viewModel
    }

    override fun onErrorOccur(message: String) {
        mBinding?.root?.onSnackbar(message)
    }

    override fun onSessionExpire() {
        openActivityOnTokenExpire()
    }

}