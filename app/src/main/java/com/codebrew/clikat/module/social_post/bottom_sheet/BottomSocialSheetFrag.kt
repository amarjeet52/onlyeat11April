package com.codebrew.clikat.module.social_post.bottom_sheet

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.RecyclerView
import com.codebrew.clikat.R
import com.codebrew.clikat.app_utils.AppToasty
import com.codebrew.clikat.app_utils.AppUtils
import com.codebrew.clikat.app_utils.extension.afterTextChanged
import com.codebrew.clikat.base.BaseDialog
import com.codebrew.clikat.data.SPType
import com.codebrew.clikat.data.model.api.CommentBean
import com.codebrew.clikat.data.preferences.PreferenceHelper
import com.codebrew.clikat.databinding.FragmentBottomSocialSheetBinding
import com.codebrew.clikat.di.ViewModelProviderFactory
import com.codebrew.clikat.modal.PojoSignUp
import com.codebrew.clikat.module.social_post.SocialPostViewModel
import com.codebrew.clikat.module.social_post.bottom_sheet.adapter.BottomAdapter
import com.codebrew.clikat.module.social_post.custom_model.BottomDataItem
import com.codebrew.clikat.module.social_post.custom_model.SocialDataItem
import com.codebrew.clikat.module.social_post.interfaces.BottomActionListener
import com.codebrew.clikat.module.social_post.interfaces.SocialPostNavigator
import com.codebrew.clikat.preferences.DataNames
import com.codebrew.clikat.utils.configurations.Configurations
import dagger.android.support.AndroidSupportInjection
import kotlinx.android.synthetic.main.fragment_bottom_social_sheet.*
import java.util.*
import javax.inject.Inject


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "bottomData"

/**
 * A simple [Fragment] subclass.
 * Use the [BottomSocialSheetFrag.newInstance] factory method to
 * create an instance of this fragment.
 */
class BottomSocialSheetFrag : BaseDialog(), SocialPostNavigator {
    // TODO: Rename and change types of parameters
    private var param1: BottomDataItem? = null

    @Inject
    lateinit var factory: ViewModelProviderFactory

    @Inject
    lateinit var appUtils: AppUtils

    @Inject
    lateinit var prefHelper: PreferenceHelper

    private var mSocialViewModel: SocialPostViewModel? = null
    private val textConfig by lazy { appUtils.loadAppConfig(0).strings }
    private var mAdapter: BottomAdapter? = null
    private var mListener: BottomActionListener? = null
    private val mSocialDataItem = mutableListOf<SocialDataItem>()
    private var updatedComment: CommentBean? = null
    private var signup: PojoSignUp? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getParcelable(ARG_PARAM1)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment

        val binding = DataBindingUtil.inflate<FragmentBottomSocialSheetBinding>(inflater, R.layout.fragment_bottom_social_sheet, container, false)
        AndroidSupportInjection.inject(this)
        mSocialViewModel = ViewModelProviders.of(this, factory).get(SocialPostViewModel::class.java)
        binding.viewModel = mSocialViewModel
        binding.color = Configurations.colors
        binding.strings = textConfig

        mSocialViewModel?.navigator = this

        return binding.root
    }

    /**
     * Called immediately after [.onCreateView]
     * has returned, but before any saved state has been restored in to the view.
     * This gives subclasses a chance to initialize themselves once
     * they know their view hierarchy has been completely created.  The fragment's
     * view hierarchy is not however attached to its parent at this point.
     * @param view The View returned by [.onCreateView].
     * @param savedInstanceState If non-null, this fragment is being re-constructed
     * from a previous saved state as given here.
     */
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        signup = prefHelper.getGsonValue(DataNames.USER_DATA, PojoSignUp::class.java)

        ic_cross.setOnClickListener {
            dismissDialog("dialog")
        }

        tb_name.text = when {
            param1?.supList != null -> {
                "Select ${textConfig?.supplier}"
            }
            param1?.commentList != null -> {
                "Comments"
            }
            param1?.likeList != null -> {
                "Likes"
            }
            else -> {
                "Select ${textConfig?.product}"
            }
        }

        if (param1?.commentList != null) {
            group_comment.visibility = View.VISIBLE
            ed_search.visibility = View.GONE
        } else {
            group_comment.visibility = View.GONE
            ed_search.visibility = View.VISIBLE
        }


        mAdapter = BottomAdapter(BottomAdapter.SPListener({
            mListener?.onProductSelect(it)
            dismissDialog("dialog")
        }, {
            mListener?.onSupplierSelect(it)
            dismissDialog("dialog")
        }))

        val itemDecoration: RecyclerView.ItemDecoration = DividerItemDecoration(activity, DividerItemDecoration.VERTICAL)
        rv_list.addItemDecoration(itemDecoration)

        rv_list.adapter = mAdapter


        ed_search.afterTextChanged {
            mAdapter?.updateList(mSocialDataItem)

            if (it.isEmpty()) {
                mAdapter?.filter?.filter("")
            } else {
                mAdapter?.filter?.filter(it.toLowerCase(Locale.getDefault()))
            }
        }

        post_comment.setOnClickListener {
            if (ed_add_comment.text.trim().isNotEmpty() && prefHelper.getCurrentUserLoggedIn() && mSocialViewModel?.isLoading?.get() == false) {
                val commentBean = mSocialDataItem.lastOrNull()?.commentBean

                updatedComment = CommentBean(comment = ed_add_comment.text.trim().toString(),
                        user_id = signup?.data?.id ?: 0,
                        id = commentBean?.id?.plus(1)
                                ?: 0, user_name = "${signup?.data?.firstname}",
                        post_id = commentBean?.post_id ?: 0)

                if (isNetworkConnected) {
                    mSocialViewModel?.addComment(commentBean?.post_id
                            ?: 0, ed_add_comment.text.trim().toString())
                }
            }
        }

        handleSocialData(param1)
    }

    private fun handleSocialData(param1: BottomDataItem?) {


        when {
            param1?.prodList != null -> {
                param1.prodList.forEach {
                    mSocialDataItem.add(SocialDataItem(socialType = SPType.ProductType.type, productList = it))
                }
            }
            param1?.commentList != null -> {
                param1.commentList.forEach {
                    mSocialDataItem.add(SocialDataItem(socialType = SPType.CommentType.type, commentBean = it))
                }
            }
            param1?.likeList != null -> {
                param1.likeList.forEach {
                    mSocialDataItem.add(SocialDataItem(socialType = SPType.LikeType.type, likeBean = it))
                }
            }
            else -> {
                param1?.supList?.forEach {
                    mSocialDataItem.add(SocialDataItem(socialType = SPType.SupplierType.type, supplierList = it))
                }
            }
        }

        mAdapter?.addItmSubmitList(mSocialDataItem)

        rv_list.smoothScrollToPosition(mSocialDataItem.size)
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment BottomSocialSheetFrag.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: BottomDataItem?) =
                BottomSocialSheetFrag().apply {
                    arguments = Bundle().apply {
                        putParcelable(ARG_PARAM1, param1)
                    }
                }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        val parent = parentFragment
        mListener = if (parent != null) {
            parent as BottomActionListener
        } else {
            context as BottomActionListener
        }
    }

    override fun onDetach() {
        mListener = null
        super.onDetach()
    }

    override fun onErrorOccur(message: String) {
        AppToasty.error(activity ?: requireContext(), message)
    }

    override fun onSessionExpire() {
        openActivityOnTokenExpire()
    }

    override fun onPostComment() {
        ed_add_comment.setText("")
        mSocialDataItem.add(SocialDataItem(socialType = SPType.CommentType.type, commentBean = updatedComment))
        mAdapter?.addItmSubmitList(mSocialDataItem)

        updatedComment?.let {
            mListener?.onUpdateComment(it)
        }

        rv_list.smoothScrollToPosition(mSocialDataItem.size)
    }
}