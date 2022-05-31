package com.codebrew.clikat.module.feedback

import com.codebrew.clikat.base.BaseInterface
import com.codebrew.clikat.data.model.api.SuggestionData

interface FeedbackNavigator : BaseInterface {
    fun feedbackSuccess()
    fun suggestionListSuccess(data: SuggestionData?)
}
