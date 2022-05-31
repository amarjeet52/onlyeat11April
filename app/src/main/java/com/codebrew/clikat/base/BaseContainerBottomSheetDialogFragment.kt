package com.codebrew.clikat.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import com.codebrew.clikat.app_utils.PermissionFile
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.android.support.AndroidSupportInjection
import javax.inject.Inject

abstract class BaseContainerBottomSheetDialogFragment<T : ViewDataBinding> : BottomSheetDialogFragment() {


    private var mViewDataBinding: T? = null
    private var mRootView: View? = null
@Inject
lateinit var permissionFile: PermissionFile

    @get:LayoutRes
    protected abstract val layoutResourceId: Int

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        mViewDataBinding = DataBindingUtil.inflate(inflater, layoutResourceId, container, false)
        mRootView = mViewDataBinding?.root

        return mRootView
    }


    override fun onDestroyView() {
        super.onDestroyView()
        mRootView = null
        mViewDataBinding?.lifecycleOwner = null
        mViewDataBinding = null

        System.gc()
    }

    override fun dismiss() {
        super.dismiss()

    }


    override fun onResume() {
        super.onResume()

        dialog!!.window!!.setBackgroundDrawableResource(android.R.color.transparent)

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mViewDataBinding!!.lifecycleOwner = this
        mViewDataBinding!!.executePendingBindings()
    }

    open fun getViewDataBinding(): T {
        return mViewDataBinding!!
    }


}