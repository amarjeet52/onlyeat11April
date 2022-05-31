package com.codebrew.clikat.utils.Dialogs;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;

import com.codebrew.clikat.R;
import com.codebrew.clikat.modal.AppGlobal;
import com.codebrew.clikat.utils.GeneralFunctions;
import com.codebrew.clikat.utils.StaticFunction;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

/*
 * Created by cbl80 on 14/6/16.
 */
public class ForgotPasswordDialouge extends Dialog {


    public OnOkClickListener mListener;
    private Boolean mIsDismiss;
    private Context context;


    public ForgotPasswordDialouge(Context context
            , boolean isDismiss, OnOkClickListener onClick) {
        super(context, R.style.TransparentDilaog);
        mListener = onClick;
        mIsDismiss = isDismiss;
        this.context = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialouge_forgot_pass);
        setCancelable(mIsDismiss);
        ConstraintLayout llContainer = findViewById(R.id.llContainer);
        llContainer.getLayoutParams().width = GeneralFunctions.getScreenWidth(context) - 40;
        ImageView ivCross = findViewById(R.id.ivCross);
        TextView tvTitle = findViewById(R.id.tvTitle);
        tvTitle.setTypeface(AppGlobal.regular);

        assert ivCross != null;
        ivCross.setOnClickListener(v -> {
            View view = this.getCurrentFocus();
            if (view != null) {
                InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
                if (imm != null) {
                    imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                }
            }
            dismiss();
        });
        TextView tvGo = findViewById(R.id.tvGo);

         TextInputEditText etSearch = findViewById(R.id.etSearch);
        TextInputLayout inputLayout = findViewById(R.id.tlForgot);


        assert tvGo != null;
        tvGo.setOnClickListener(v -> {
            GeneralFunctions.hideKeyboard(etSearch, context);

            if (mListener != null) {

                if(etSearch.getText().toString().isEmpty())
                {
                    inputLayout.requestFocus();
                    inputLayout.setError(context.getString(R.string.valid_email));
                } else if (GeneralFunctions.isValidEmail(etSearch.getText().toString().trim().toLowerCase())) {
                    inputLayout.setError(null);
                    mListener.onButtonClick(etSearch.getText().toString().trim());
                    dismiss();
                } else {
                    inputLayout.requestFocus();
                    inputLayout.setError(context.getString(R.string.invalid_email));
                }

            }
        });

        setOnDismissListener(dialog -> GeneralFunctions.hideKeyboard(etSearch, context));

    }

    @Override
    public void setOnKeyListener(OnKeyListener onKeyListener) {
        super.setOnKeyListener(onKeyListener);
    }


    public interface OnOkClickListener {
        void onButtonClick(String emailId);
    }
}