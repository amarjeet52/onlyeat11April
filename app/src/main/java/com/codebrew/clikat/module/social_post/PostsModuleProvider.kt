package com.codebrew.clikat.module.social_post

import com.codebrew.clikat.module.dialog_adress.AddressDialogFragment
import com.codebrew.clikat.module.social_post.bottom_sheet.BottomSocialSheetFrag
import com.codebrew.clikat.module.social_post.home.PostsHomeFragment
import com.codebrew.clikat.module.social_post.other.*
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class PostsModuleProvider {

    @ContributesAndroidInjector
    abstract fun providePostsHomeFactory(): PostsHomeFragment

    @ContributesAndroidInjector
    abstract fun provideSocialPostFactory(): SocialPostDescFrag

    @ContributesAndroidInjector
    abstract fun provideSocialSelectSuplierFactory(): SocialSelectSuplierFrag

    @ContributesAndroidInjector
    abstract fun provideBottomSocialFactory(): BottomSocialSheetFrag

    @ContributesAndroidInjector
    abstract fun provideSelectImagesFactory(): SocialSelectImagesFrag

    @ContributesAndroidInjector
    abstract fun provideConfirmPostFactory(): SocialConfirmPostFrag

    @ContributesAndroidInjector
    abstract fun provideSocialEnterQuesFactory(): SocialEnterQuesFrag

}
