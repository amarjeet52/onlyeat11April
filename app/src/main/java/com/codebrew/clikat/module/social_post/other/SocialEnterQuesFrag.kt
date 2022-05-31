package com.codebrew.clikat.module.social_post.other

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.navArgs
import com.codebrew.clikat.BR
import com.codebrew.clikat.R
import com.codebrew.clikat.app_utils.AppUtils
import com.codebrew.clikat.app_utils.extension.onSnackbar
import com.codebrew.clikat.app_utils.navController
import com.codebrew.clikat.base.BaseFragment
import com.codebrew.clikat.databinding.FragSocialEnterQuesBinding
import com.codebrew.clikat.databinding.FragSocialPostDescripBinding
import com.codebrew.clikat.di.ViewModelProviderFactory
import com.codebrew.clikat.module.social_post.SocialPostViewModel
import com.codebrew.clikat.module.social_post.custom_model.SocialPostInput
import com.codebrew.clikat.module.social_post.home.PostsHomeFragmentDirections
import com.codebrew.clikat.module.social_post.interfaces.SocialPostNavigator
import com.codebrew.clikat.utils.configurations.Configurations
import kotlinx.android.synthetic.main.toolbar_subscription.*
import javax.inject.Inject


/**
 * A simple [Fragment] subclass.
 * Use the [SocialEnterQuesFrag.newInstance] factory method to
 * create an instance of this fragment.
 */
class SocialEnterQuesFrag : BaseFragment<FragSocialEnterQuesBinding, SocialPostViewModel>(), SocialPostNavigator {


    @Inject
    lateinit var factory: ViewModelProviderFactory
    @Inject
    lateinit var appUtils: AppUtils

    private val argument: SocialEnterQuesFragArgs by navArgs()

    private var mBinding: FragSocialEnterQuesBinding? = null
    private val textConfig by lazy { appUtils.loadAppConfig(0).strings }
    private lateinit var viewModel: SocialPostViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel.navigator = this

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mBinding = viewDataBinding
        mBinding?.color = Configurations.colors
        mBinding?.strings = textConfig
        mBinding?.socialPost = if(argument.postDescData!=null)argument.postDescData else SocialPostInput()


        tb_name.text = getString(R.string.enter_qurstion)

        tb_back.setOnClickListener {
            navController(this@SocialEnterQuesFrag).popBackStack()
        }
    }

    override fun getBindingVariable(): Int {
        return BR.viewModel
    }

    override fun getLayoutId(): Int {
        return R.layout.frag_social_enter_ques
    }

    override fun getViewModel(): SocialPostViewModel {
        viewModel = ViewModelProviders.of(this, factory).get(SocialPostViewModel::class.java)
        return viewModel
    }

    override fun onErrorOccur(message: String) {
        hideKeyboard()
        mBinding?.root?.onSnackbar(message)
    }

    override fun onSessionExpire() {
        openActivityOnTokenExpire()
    }

    override fun onSocialPost(socialPostInput: SocialPostInput) {
        hideKeyboard()
        if (isNetworkConnected) {
            viewModel.createUserPost(socialPostInput)
        }

    }

    override fun onPostCreated() {
        val action = SocialEnterQuesFragDirections.actionSocialEnterQuesFragToSocialPost()
        navController(this@SocialEnterQuesFrag).navigate(action)
    }
}