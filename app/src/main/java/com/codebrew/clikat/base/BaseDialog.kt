package com.codebrew.clikat.base

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.RelativeLayout
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import com.codebrew.clikat.R
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment


abstract class BaseDialog : BottomSheetDialogFragment() {

    var baseActivity: BaseActivity<*, *>? = null
        private set

    val isNetworkConnected: Boolean
        get() = baseActivity != null && baseActivity!!.isNetworkConnected

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is BaseActivity<*, *>) {
            this.baseActivity = context
            context.onFragmentAttached()
        }
    }

    override fun onStart() {
        super.onStart()

        val displayMetrics = DisplayMetrics()
        (context as Activity)
                .windowManager
                .defaultDisplay
                .getMetrics(displayMetrics)
        val windowHeight = displayMetrics.heightPixels


        dialog.also {
            val bottomSheet = dialog?.findViewById<View>(R.id.design_bottom_sheet)
            bottomSheet?.layoutParams?.height = windowHeight * 4 / 5
            val behavior = BottomSheetBehavior.from(bottomSheet!!)
            behavior.peekHeight = windowHeight * 4 / 5
            behavior.state = BottomSheetBehavior.STATE_EXPANDED
            behavior.isDraggable = false
            view?.requestLayout()
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        if (dialog is BottomSheetDialog) {
            dialog.behavior.skipCollapsed = true
            dialog.behavior.state = BottomSheetBehavior.STATE_EXPANDED
        }
        return dialog
    }


    override fun onDetach() {
        baseActivity = null
        super.onDetach()
    }


    fun dismissDialog(tag: String) {
        dismiss()
        baseActivity?.onFragmentDetached(tag)
    }

    fun hideKeyboard() {
        if (baseActivity != null) {
            baseActivity?.hideKeyboard()
        }
    }

    fun hideLoading() {
        if (baseActivity != null) {
            baseActivity?.hideLoading()
        }
    }

    fun openActivityOnTokenExpire() {
        if (baseActivity != null) {
            baseActivity?.openActivityOnTokenExpire()
        }
    }

    fun showLoading() {
        if (baseActivity != null) {
            baseActivity?.showLoading()
        }
    }


}
