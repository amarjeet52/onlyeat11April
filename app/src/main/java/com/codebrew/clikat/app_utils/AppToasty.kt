package com.codebrew.clikat.app_utils

import android.content.Context
import android.graphics.Color
import androidx.core.content.ContextCompat
import android.widget.Toast
import com.codebrew.clikat.R
import es.dmoral.toasty.Toasty

object AppToasty {


    fun int(mContext: Context)
    {
        Toasty.Config.getInstance()
                .tintIcon(true) // optional (apply textColor also to the icon)
                .allowQueue(false) // optional (prevents several Toastys from queuing)
                //.setToastTypeface(Typeface.createFromAsset(mContext.getAssets(),  "fonts/abc.ttf"))
                .setTextSize(18)
                .apply()
    }

    fun error(context: Context, message: String) {
        Toasty.error(context, message, Toast.LENGTH_SHORT, true).show();
    }

    fun success(context: Context, message: String) {
        Toasty.success(context, message, Toast.LENGTH_SHORT, true).show();
    }


    fun normal(context: Context, message: String) {
        Toasty.normal(context, message).show();
    }



}