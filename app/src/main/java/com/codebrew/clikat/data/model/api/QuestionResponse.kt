package com.codebrew.clikat.data.model.api

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class QuestionResponse(var status: Int,
                            var message: String,
                            var data: QuestionData) : Parcelable




@Parcelize
data class QuestionData(var questionList: MutableList<QuestionList>) : Parcelable

@Parcelize
data class QuestionList(var questionId: Int,
                        var categoryId: Int,
                        var question: String,
                        var questionTypeSelection: Int,
                        var optionsList: List<AnswersData>) : Parcelable

@Parcelize
data class AnswersData(var questionId: Int,
                       var categoryId: Int,
                       var questionOptionId: Int,
                       var optionLabel: String,
                       var valueChargeType: Int,
                       var flatValue: Float,
                       var productPrice:Float,
                       var percentageValue: Int,
                       var isChecked: Boolean) : Parcelable


@Parcelize
data class QuestionDataSecond(var questionList: List<Questions>) : Parcelable

@Parcelize
data class Questions(var questionId: Int,
                     var question: String,
                     var questionTypeSelection: Int,
                     var optionsList: List<OptionsList>) : Parcelable

@Parcelize
data class OptionsList(var questionOptionId: Int,
                       var optionLabel: String,
                       var valueChargeType: Int,
                       var flatValue: Int,
                       var percentageValue: Int) : Parcelable