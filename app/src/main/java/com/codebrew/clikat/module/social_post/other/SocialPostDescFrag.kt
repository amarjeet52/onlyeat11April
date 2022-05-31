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
import com.codebrew.clikat.base.BaseInterface
import com.codebrew.clikat.data.constants.AppConstants
import com.codebrew.clikat.databinding.FragSocialPostDescripBinding
import com.codebrew.clikat.databinding.SubscripDetailFragmentBinding
import com.codebrew.clikat.di.ViewModelProviderFactory
import com.codebrew.clikat.module.cart.CartViewModel
import com.codebrew.clikat.module.social_post.SocialPostViewModel
import com.codebrew.clikat.module.social_post.custom_model.SocialPostInput
import com.codebrew.clikat.module.social_post.home.PostsHomeFragmentDirections
import com.codebrew.clikat.module.social_post.interfaces.SocialPostNavigator
import com.codebrew.clikat.utils.configurations.Configurations
import kotlinx.android.synthetic.main.toolbar_subscription.*
import javax.inject.Inject

/**
 * A simple [Fragment] subclass.
 * Use the [SocialPostDescFrag.newInstance] factory method to
 * create an instance of this fragment.
 */
class SocialPostDescFrag : BaseFragment<FragSocialPostDescripBinding, SocialPostViewModel>(), SocialPostNavigator {


    private lateinit var viewModel: SocialPostViewModel

    @Inject
    lateinit var factory: ViewModelProviderFactory

    @Inject
    lateinit var appUtils: AppUtils

    private var mBinding: FragSocialPostDescripBinding? = null

    private val argument: SocialPostDescFragArgs by navArgs()

    private val textConfig by lazy { appUtils.loadAppConfig(0).strings }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel.navigator = this
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mBinding = viewDataBinding
        mBinding?.color = Configurations.colors
        mBinding?.strings = textConfig
        mBinding?.socialPost = if (argument.postDescData?.post_desc == null && argument.postDescData?.post_desc == null) SocialPostInput() else argument.postDescData

        tb_name.text = getString(R.string.post_desc)

        tb_back.setOnClickListener {
            navController(this@SocialPostDescFrag).popBackStack()
        }
    }


    /**
     * Override for set binding variable
     *
     * @return variable id
     */
    override fun getBindingVariable(): Int {
        return BR.viewModel
    }

    /**
     * @return layout resource id
     */
    override fun getLayoutId(): Int {
        return R.layout.frag_social_post_descrip
    }

    /**
     * Override for set view model
     *
     * @return view model instance
     */
    override fun getViewModel(): SocialPostViewModel {
        viewModel = ViewModelProviders.of(this, factory).get(SocialPostViewModel::class.java)
        return viewModel
    }

    override fun onSocialPost(socialPostInput: SocialPostInput) {
        hideKeyboard()
        if (isNetworkConnected) {
            val action = SocialPostDescFragDirections.actionSocialPostDescFragToSocialSelectSuplierFrag(socialPostInput)
            navController(this@SocialPostDescFrag).navigate(action)
        }

    }

    override fun onErrorOccur(message: String) {
        hideKeyboard()
        mBinding?.root?.onSnackbar(message)
    }

    override fun onSessionExpire() {
        openActivityOnTokenExpire()
    }
}