package com.codebrew.clikat.module.questions

import com.codebrew.clikat.module.questions.adapters.questionAnswer.QuestionAnswerFragment
import com.codebrew.clikat.module.questions.main.QuestionsFragment
import dagger.Module
import dagger.android.ContributesAndroidInjector


@Module
abstract class QuestionFragProvider {

    @ContributesAndroidInjector
    abstract fun provideQuestionFactory(): QuestionsFragment

    @ContributesAndroidInjector
    abstract fun provideAnswerFactory(): QuestionAnswerFragment
}
