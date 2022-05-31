package com.codebrew.clikat.app_utils.extension

import android.os.SystemClock
import android.view.View
import com.codebrew.clikat.module.home_screen.adapter.SpecialListAdapter

fun View.setSafeOnClickListener(onSafeClick: SpecialListAdapter.ViewHolder) {

    var lastTimeClicked: Long = 0

    setOnClickListener {
        if (SystemClock.elapsedRealtime() - lastTimeClicked < 500) {
            return@setOnClickListener
        }
        lastTimeClicked = SystemClock.elapsedRealtime()
//        onSafeClick.onClick(this)
    }
}