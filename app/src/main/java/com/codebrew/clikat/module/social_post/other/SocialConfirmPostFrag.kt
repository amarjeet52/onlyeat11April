package com.codebrew.clikat.module.social_post.other

import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.codebrew.clikat.BR
import com.codebrew.clikat.R
import com.codebrew.clikat.app_utils.AppUtils
import com.codebrew.clikat.app_utils.extension.onSnackbar
import com.codebrew.clikat.app_utils.navController
import com.codebrew.clikat.base.BaseFragment
import com.codebrew.clikat.databinding.FragSocialConfirmPostBinding
import com.codebrew.clikat.di.ViewModelProviderFactory
import com.codebrew.clikat.module.social_post.SocialPostViewModel
import com.codebrew.clikat.module.social_post.interfaces.SocialPostNavigator
import com.codebrew.clikat.module.social_post.other.adapter.ImageListener
import com.codebrew.clikat.module.social_post.other.adapter.SelectedImageAdapter
import com.codebrew.clikat.utils.configurations.Configurations
import kotlinx.android.synthetic.main.frag_social_confirm_post.*
import kotlinx.android.synthetic.main.toolbar_subscription.*
import javax.inject.Inject

class SocialConfirmPostFrag : BaseFragment<FragSocialConfirmPostBinding, SocialPostViewModel>(), SocialPostNavigator {

    @Inject
    lateinit var factory: ViewModelProviderFactory

    @Inject
    lateinit var appUtils: AppUtils

    private var mBinding: FragSocialConfirmPostBinding? = null

    private lateinit var mViewModel: SocialPostViewModel

    private val argument: SocialConfirmPostFragArgs by navArgs()

    private val textConfig by lazy { appUtils.loadAppConfig(0).strings }

    private var mSelectedAdapter: SelectedImageAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.navigator = this
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mBinding = viewDataBinding
        mBinding?.color = Configurations.colors
        mBinding?.strings = textConfig
        mBinding?.socialPost = argument.postDescData

        tb_name.text=getString(R.string.confirm)

        tb_back.setOnClickListener {
            navController(this@SocialConfirmPostFrag).popBackStack()
        }

        btn_edit_post.setOnClickListener {
            val action = SocialConfirmPostFragDirections.actionSocialConfirmPostFragToSocialPostDescFrag(argument.postDescData)
            navController(this@SocialConfirmPostFrag).navigate(action)
        }

        btn_cancel.setOnClickListener {
            val action = SocialConfirmPostFragDirections.actionSocialConfirmPostFragToSocialPost()
            navController(this@SocialConfirmPostFrag).navigate(action)
        }

        if(argument.postDescData?.imageList?.isNotEmpty()==true)
        {
            mSelectedAdapter= SelectedImageAdapter(ImageListener { model, position->
            },false)

            rvImages.layoutManager= LinearLayoutManager(activity, RecyclerView.HORIZONTAL,false)
            rvImages.adapter=mSelectedAdapter

            mSelectedAdapter?.submitItemList(argument.postDescData?.imageList)
        }

    }

    override fun onPostCreated() {
        btn_cancel.callOnClick()
    }

    override fun getBindingVariable(): Int {
        return BR.viewModel
    }

    override fun getLayoutId(): Int {
        return R.layout.frag_social_confirm_post
    }

    override fun getViewModel(): SocialPostViewModel {
        mViewModel = ViewModelProviders.of(this, factory).get(SocialPostViewModel::class.java)
        return mViewModel
    }

    override fun onErrorOccur(message: String) {
        hideKeyboard()
        mBinding?.root?.onSnackbar(message)
    }

    override fun onSessionExpire() {
        openActivityOnTokenExpire()
    }
}