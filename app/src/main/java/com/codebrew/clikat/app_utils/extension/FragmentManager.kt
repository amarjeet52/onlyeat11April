package com.codebrew.clikat.app_utils.extension

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.codebrew.clikat.R


internal fun FragmentManager.removeFragment(
        tag: String,
        slideIn: Int = R.anim.slide_left,
        slideOut: Int = R.anim.slide_right
) {
    this.findFragmentByTag(tag)?.let {
        this.beginTransaction()
                .disallowAddToBackStack()
                .setCustomAnimations(slideIn, slideOut)
                .remove(it)
                .commitNow()
    }
}

internal fun FragmentManager.addFragment(
        containerViewId: Int,
        fragment: Fragment,
        tag: String,
        @SuppressLint("PrivateResource") slideIn: Int = R.anim.design_bottom_sheet_slide_in,
        @SuppressLint("PrivateResource") slideOut: Int = R.anim.design_bottom_sheet_slide_out
) {
    this.beginTransaction()
            .setCustomAnimations(slideIn, slideOut)
            .replace(containerViewId, fragment, tag)
            .commit()
}


internal fun FragmentManager.refreshFragment(
        fragment: Fragment?,
        @SuppressLint("PrivateResource") slideIn: Int = R.anim.design_bottom_sheet_slide_in,
        @SuppressLint("PrivateResource") slideOut: Int = R.anim.design_bottom_sheet_slide_out
) {

    fragment?.let {
        this.beginTransaction()
                .setCustomAnimations(slideIn, slideOut)
                .detach(it)
                .attach(it)
                .commit()
    }
}


inline fun <reified T : Any> Activity.launchActivity(
        requestCode: Int = -1,
        options: Bundle? = null,
        noinline init: Intent.() -> Unit = {}) {

    val intent = newIntent<T>(this)
    intent.init()
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
        startActivityForResult(intent, requestCode, options)
    } else {
        startActivityForResult(intent, requestCode)
    }
}

inline fun <reified T : Any> newIntent(context: Context): Intent =
        Intent(context, T::class.java)

