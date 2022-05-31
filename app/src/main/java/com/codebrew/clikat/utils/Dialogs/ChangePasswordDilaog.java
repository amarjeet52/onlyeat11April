package com.codebrew.clikat.utils.Dialogs;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;

import com.codebrew.clikat.R;
import com.codebrew.clikat.app_utils.AppToasty;
import com.codebrew.clikat.modal.AppGlobal;
import com.codebrew.clikat.modal.ExampleCommon;
import com.codebrew.clikat.modal.PojoSignUp;
import com.codebrew.clikat.modal.other.SettingModel;
import com.codebrew.clikat.preferences.DataNames;
import com.codebrew.clikat.preferences.Prefs;
import com.codebrew.clikat.retrofit.RestClient;
import com.codebrew.clikat.utils.ClikatConstants;
import com.codebrew.clikat.utils.GeneralFunctions;
import com.codebrew.clikat.utils.ProgressBarDialog;
import com.codebrew.clikat.utils.StaticFunction;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.HashMap;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/*
 * Created by cbl80 on 18/5/16.
 */
public class ChangePasswordDilaog extends Dialog {


    public OnOkClickListener mListener;
    private Boolean mIsDismiss;
    private Context context;
    private TextInputEditText etOldpassword;
    private TextInputEditText etnewpassword;
    private TextInputEditText etConfirmpassword;
    private TextInputLayout inputOldpassword, inputnewpassword, inputConfirmpassword;
    private SettingModel.DataBean.SettingData clientInform = null;

    public ChangePasswordDilaog(Context context
            , boolean isDismiss, OnOkClickListener onClick) {
        super(context, R.style.TransparentDilaog);

        mListener = onClick;
        mIsDismiss = isDismiss;
        this.context = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_change_password);


        setCancelable(mIsDismiss);
        ImageView ivCross = findViewById(R.id.ivCross);
        TextView tvTitle = findViewById(R.id.tvTitle);
        tvTitle.setTypeface(AppGlobal.regular);
        CardView cvTop = findViewById(R.id.cvTop);

        CardView cvContainer = findViewById(R.id.cvContainer);
        etOldpassword = findViewById(R.id.etOldPassword);
        etnewpassword = findViewById(R.id.etNewPassword);
        etConfirmpassword = findViewById(R.id.etConfirmPassword);

        inputOldpassword = findViewById(R.id.inputOldpassword);
        inputnewpassword = findViewById(R.id.inputnewpassword);
        inputConfirmpassword = findViewById(R.id.inputConfirmpassword);
        TextView tvGo = findViewById(R.id.tvGo);

        assert tvGo != null;

        MaterialButton tvSave = findViewById(R.id.tvSave);
        assert tvSave != null;

        clientInform = Prefs.getPrefs().getObject(DataNames.SETTING_DATA, SettingModel.DataBean.SettingData.class);
        if (clientInform != null && clientInform.is_skip_theme() != null && clientInform.is_skip_theme().equals("1")) {
            tvSave.setVisibility(View.VISIBLE);
            tvGo.setVisibility(View.INVISIBLE);


            tvTitle.setTextColor(ContextCompat.getColor(getContext(), R.color.white));
            cvTop.setCardBackgroundColor(ContextCompat.getColor(getContext(), R.color.colorPrimary));
            cvContainer.setCardBackgroundColor(ContextCompat.getColor(getContext(), R.color.colorPrimary_10));
        }
        assert ivCross != null;
        ivCross.setOnClickListener(v -> dismiss());


        tvGo.setOnClickListener(v -> {
            if (StaticFunction.INSTANCE.isInternetConnected(context)) {
                GeneralFunctions.hideKeyboard(etOldpassword, context);
                if (etOldpassword.getText().toString().trim().equals("")) {
                    inputnewpassword.setError(null);
                    inputConfirmpassword.setError(null);
                    inputOldpassword.requestFocus();
                    inputOldpassword.setError(context.getString(R.string.empty));
                } else if (etnewpassword.getText().toString().trim().equals("")) {
                    inputOldpassword.setError(null);
                    inputConfirmpassword.setError(null);
                    inputnewpassword.requestFocus();
                    inputnewpassword.setError(context.getString(R.string.empty));
                } else if (etnewpassword.getText().toString().trim().length() < 6) {
                    inputOldpassword.setError(null);
                    inputConfirmpassword.setError(null);
                    inputnewpassword.requestFocus();
                    inputnewpassword.setError(context.getString(R.string.passwrd_lenght));
                } else if (etConfirmpassword.getText().toString().trim().equals("")) {
                    inputOldpassword.setError(null);
                    inputnewpassword.setError(null);
                    inputConfirmpassword.requestFocus();
                    inputConfirmpassword.setError(context.getString(R.string.empty));
                } else if (!etConfirmpassword.getText().toString().trim().equals(etnewpassword.getText().toString().trim())) {
                    inputOldpassword.setError(null);
                    inputnewpassword.setError(null);
                    inputConfirmpassword.requestFocus();
                    inputConfirmpassword.setError(context.getString(R.string.password_match_error));
                } else {
                    inputConfirmpassword.setError(null);
                    change_passwordApi();
                    if (mListener != null) {
                        mListener.onButtonClick();
                    }
                }


            } else {
                StaticFunction.INSTANCE.showNoInternetDialog(context);
            }
        });
        tvSave.setOnClickListener(view -> tvGo.performClick());

        setOnDismissListener(dialog -> GeneralFunctions.hideKeyboard(etOldpassword, context));

    }

    private void change_passwordApi() {
        final ProgressBarDialog barDialog = new ProgressBarDialog(context);
        barDialog.show();
        PojoSignUp signUp = Prefs.with(context).getObject(DataNames.USER_DATA, PojoSignUp.class);
        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put("accessToken", signUp.data.getAccess_token());
        hashMap.put("oldPassword", etOldpassword.getText().toString().trim());
        hashMap.put("newPassword", etnewpassword.getText().toString().trim());
        hashMap.put("languageId", "" + StaticFunction.INSTANCE.getLanguage(context));
        Call<ExampleCommon> changepassword = RestClient.getModalApiService(context).changePassword(hashMap);

        changepassword.enqueue(new Callback<ExampleCommon>() {
            @Override
            public void onResponse(Call<ExampleCommon> call, Response<ExampleCommon> response) {
                barDialog.dismiss();
                if (response.code() == 200) {
                    ExampleCommon pojoSignUp = response.body();
                    if (pojoSignUp.getStatus() == ClikatConstants.STATUS_SUCCESS) {
                        if (clientInform != null && clientInform.is_skip_theme() != null && clientInform.is_skip_theme().equals("1")) {
                            showDialog(getContext());
                        } else {
                            AppToasty.INSTANCE.success(getContext(), pojoSignUp.getMessage());
                            GeneralFunctions.showSnackBar(getCurrentFocus(), pojoSignUp.getMessage(), context);
                            dismiss();
                        }

                    } else
                        GeneralFunctions.showSnackBar(getCurrentFocus(), pojoSignUp.getMessage(), context);
                }
            }

            @Override
            public void onFailure(Call<ExampleCommon> call, Throwable t) {
                GeneralFunctions.showSnackBar(getCurrentFocus(), t.getMessage(), context);
                barDialog.dismiss();
            }
        });
    }


    public void showDialog(Context activity) {
        final Dialog dialog = new Dialog(activity, android.R.style.Theme_Black_NoTitleBar_Fullscreen);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.change_pass_success_dialo);

        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                dismiss();
                dialog.dismiss();
            }
        }, 2000L);
        dialog.show();
    }

    @Override
    public void setOnKeyListener(OnKeyListener onKeyListener) {
        super.setOnKeyListener(onKeyListener);
    }


    public interface OnOkClickListener {
        void onButtonClick();
    }
}
