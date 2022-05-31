package com.codebrew.clikat.utils.Dialogs;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.codebrew.clikat.R;
import com.codebrew.clikat.modal.AppGlobal;
import com.codebrew.clikat.data.model.api.supplier_detail.MyReview;
import com.codebrew.clikat.utils.ClikatConstants;
import com.codebrew.clikat.utils.GeneralFunctions;
import com.codebrew.clikat.utils.StaticFunction;

import androidx.appcompat.app.AppCompatActivity;

/*
 * Created by cbl80 on 2/6/16.
 */
public class RatingDialog extends Dialog {


    public OnOkClickListener mListener;
    private Boolean mIsDismiss;
    private Context context;
    private EditText edReview;
    private RatingBar ratingBar;
    private MyReview review;


    public RatingDialog(Context context
            , boolean isDismiss, OnOkClickListener onClick, MyReview review) {
        super(context, R.style.TransparentDilaog);
        mListener = onClick;
        mIsDismiss = isDismiss;
        this.context = context;
        this.review = review;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.item_rating_dilaog);
        setlanguage();
        setCancelable(mIsDismiss);
        ImageView ivCross = findViewById(R.id.ivCross);

        edReview = findViewById(R.id.edReview);
        ratingBar = findViewById(R.id.ratingBar);
        TextView tvTitle= findViewById(R.id.tvTitle);
        tvTitle.setTypeface(AppGlobal.regular);
        ImageView sdvImage = findViewById(R.id.sdvImage);
        Glide.with(context).load(Uri.parse(review.getUserImage()))
                .apply(new RequestOptions()
                        .placeholder(R.drawable.com_facebook_profile_picture_blank_portrait)
                        .centerCrop().override(100,100)).into(sdvImage);
        edReview.setText(review.getComment());
        ratingBar.setRating(review.getRating());
        TextView tvName = findViewById(R.id.tvName);
        tvName.setText(review.getFirstname());

        tvName.setTypeface(AppGlobal.regular);
        edReview.setTypeface(AppGlobal.regular);

        assert ivCross != null;
        ivCross.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        TextView tvGo = findViewById(R.id.tvGo);
        tvGo.setTypeface(AppGlobal.regular);
        assert tvGo != null;
        tvGo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mListener != null) {
                    mListener.onButtonClick(edReview.getText().toString().trim(), ratingBar.getRating());
                    dismiss();
                }
            }


        });

        setOnDismissListener(new OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                GeneralFunctions.hideKeyboard(edReview, context);
            }
        });

    }

    private void setlanguage() {
        if (StaticFunction.INSTANCE.getLanguage(context) == ClikatConstants.LANGUAGE_OTHER) {
            GeneralFunctions.force_layout_to_RTL((AppCompatActivity) context);
        } else {
            GeneralFunctions.force_layout_to_LTR((AppCompatActivity) context);
        }
    }
    @Override
    public void setOnKeyListener(OnKeyListener onKeyListener) {
        super.setOnKeyListener(onKeyListener);
    }


    public interface OnOkClickListener {
        void onButtonClick(String comment, float rating);
    }
}