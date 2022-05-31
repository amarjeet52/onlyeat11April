package com.codebrew.clikat.module.questions.adapters.questionAnswer

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.codebrew.clikat.R
import com.codebrew.clikat.data.DataManager
import com.codebrew.clikat.data.constants.PrefenceConstants
import com.codebrew.clikat.data.model.api.AnswersData
import com.codebrew.clikat.data.model.api.Currency
import com.codebrew.clikat.data.model.api.QuestionList
import com.codebrew.clikat.modal.other.SettingModel
import com.codebrew.clikat.module.questions.adapters.AnswersAdapter
import com.codebrew.clikat.preferences.DataNames
import dagger.android.support.AndroidSupportInjection
import kotlinx.android.synthetic.main.fragment_question_answer_layout.*
import javax.inject.Inject

class QuestionAnswerFragment : Fragment() {

    private var data: QuestionList? = null
    var adapter: AnswersAdapter? = null
    var listData = arrayListOf<AnswersData>()
    private var selectedCurrency: Currency?=null

    @Inject
    lateinit var dataManager: DataManager
    var settingData: SettingModel.DataBean.SettingData? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidSupportInjection.inject(this)
        super.onCreate(savedInstanceState)
    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_question_answer_layout, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        selectedCurrency = dataManager.getGsonValue(PrefenceConstants.CURRENCY_INF, Currency::class.java)
        settingData = dataManager.getGsonValue(DataNames.SETTING_DATA, SettingModel.DataBean.SettingData::class.java)
        rcycle_answers.layoutManager = LinearLayoutManager(activity)
        tv_question.text = data?.question

        var isMultipleSelection = false
        if (data?.questionTypeSelection == 2) isMultipleSelection = true

        adapter = AnswersAdapter(requireActivity(), listData, isMultipleSelection, settingData,selectedCurrency)
        rcycle_answers.adapter = adapter

    }

    fun setQuestion(questionData: QuestionList) {
        listData.clear()
        data = questionData
        listData.addAll(questionData.optionsList)
        adapter?.notifyDataSetChanged()


    }


}