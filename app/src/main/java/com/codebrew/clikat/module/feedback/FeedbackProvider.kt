package com.codebrew.clikat.module.feedback

import com.codebrew.clikat.module.feedback.feedback_new.FeedbackNewFragment
import com.codebrew.clikat.module.feedback.feedback_new.SubmitFeedbackNewFragment
import dagger.Module
import dagger.android.ContributesAndroidInjector


@Module
abstract class FeedbackProvider {

    @ContributesAndroidInjector
    abstract fun feedbackFactory(): FeedbackFragment


    @ContributesAndroidInjector
    abstract fun feedbackNewFactory(): FeedbackNewFragment

    @ContributesAndroidInjector
    abstract fun feedbackSubmitFactory(): SubmitFeedbackNewFragment

}
