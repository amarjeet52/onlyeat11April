package com.codebrew.clikat.module.questions.main

import androidx.lifecycle.MutableLiveData
import com.codebrew.clikat.base.BaseInterface
import com.codebrew.clikat.base.BaseViewModel
import com.codebrew.clikat.data.DataManager
import com.codebrew.clikat.data.constants.NetworkConstants
import com.codebrew.clikat.data.model.api.QuestionList
import com.codebrew.clikat.data.model.api.QuestionResponse
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

class QuestionsViewModel(dataManager: DataManager) : BaseViewModel<BaseInterface>(dataManager) {

    val questionsLiveData by lazy { MutableLiveData<MutableList<QuestionList>>() }


    fun validateQuestion(categoryId: String) {


        val hashMap = hashMapOf("languageId" to dataManager.getLangCode(),
                "categoryId" to categoryId)


        setIsLoading(true)
        dataManager.getListOfQuestions(hashMap)
                ?.observeOn(AndroidSchedulers.mainThread())
                ?.subscribeOn(Schedulers.io())
                ?.subscribe({ this.codeResponse(it) }, { this.handleError(it) })?.let {
                    compositeDisposable.add(it)
                }
    }


    private fun codeResponse(it: QuestionResponse?) {
        setIsLoading(false)
        when (it?.status) {
            NetworkConstants.SUCCESS, 4 -> {

                questionsLiveData.value = it.data.questionList
            }

        }

    }


    private fun handleError(e: Throwable) {
        setIsLoading(false)

        handleErrorMsg(e).let {
            if (it == NetworkConstants.AUTH_MSG) {
                dataManager.setUserAsLoggedOut()
                navigator.onSessionExpire()
            } else {
                navigator.onErrorOccur(it)
            }
        }
    }

}