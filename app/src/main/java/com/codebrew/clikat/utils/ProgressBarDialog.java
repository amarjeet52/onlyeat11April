package com.codebrew.clikat.utils;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import androidx.core.content.ContextCompat;
import android.view.Window;

import com.codebrew.clikat.R;
import com.codebrew.clikat.utils.configurations.Configurations;


public class ProgressBarDialog extends Dialog {


    private final ProgressDialog pDialog;

    public ProgressBarDialog(Context context) {
        super(context, R.style.TransparentProgressDialog);
        //Set Background of Dialog - Custom

        //Remove the Title
        //dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);


        //Set the View of the Dialog - Custom
        getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        setCanceledOnTouchOutside(false);
        setCancelable(false);
        pDialog = new ProgressDialog(context);
        pDialog.setTitle(context.getString(R.string.com_facebook_loading));
        pDialog.setCancelable(false);

        //Set the title of the Dialog
        //dialog.setTitle("Title...");

    }

    @Override
    public void show() {
        super.show();
        pDialog.show();
    }

    @Override
    public void dismiss() {
        super.dismiss();
        pDialog.dismiss();
    }

    @Override
    public void cancel() {
        super.cancel();
        pDialog.dismiss();
    }

}
