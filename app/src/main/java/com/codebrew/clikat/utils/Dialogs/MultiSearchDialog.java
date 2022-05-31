package com.codebrew.clikat.utils.Dialogs;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.codebrew.clikat.R;
import com.codebrew.clikat.modal.AppGlobal;
import com.codebrew.clikat.utils.GeneralFunctions;



import java.util.ArrayList;
import java.util.List;

/*
 * Created by cbl80 on 6/5/16.
 */
public class MultiSearchDialog extends Dialog {


    public OnOkClickListener mListener;
    private Boolean mIsDismiss;
    private Context context;
   // private FlowLayout flowLayout;
    private List<String> list = new ArrayList<>();


    public MultiSearchDialog(Context context
            , boolean isDismiss, OnOkClickListener onClick,List<String> list) {
        super(context, R.style.TransparentDilaog);
        mListener = onClick;
        mIsDismiss = isDismiss;
        this.context = context;
        this.list.addAll(list);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_multisearch);
        setCancelable(mIsDismiss);
        LinearLayout llContainer = findViewById(R.id.llContainer);
        llContainer.getLayoutParams().width=GeneralFunctions.getScreenWidth(context)-40;
        ImageView ivCross = findViewById(R.id.ivCross);
        TextView tvTitle= findViewById(R.id.tvTitle);
        tvTitle.setTypeface(AppGlobal.regular);

        assert ivCross != null;
        ivCross.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        TextView tvGo = findViewById(R.id.tvGo);

       // flowLayout = findViewById(R.id.flLayout);
        final EditText etSearch = findViewById(R.id.etSearch);


        TextView tvAdd = findViewById(R.id.tvAdd);
        tvAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              //  if (flowLayout.getChildCount() < 10) {
                    if (!etSearch.getText().toString().trim().equals(""))
                        setTag(etSearch.getText().toString().trim());
                    etSearch.clearComposingText();
                    etSearch.setText("");
                //} else {
                    etSearch.setText("");
                    etSearch.clearComposingText();
                    GeneralFunctions.hideKeyboard(etSearch, context);
                    GeneralFunctions.showSnackBar(etSearch, context.getString(R.string.max_limit_reached), context);
             //   }
            }
        });

    /*    etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.toString().trim().length()!=0){
                if (s.charAt(s.length() - 1) == '\n' || s.charAt(s.length() - 1) == ' ') {

                }
                }
            }
        });*/

        assert tvGo != null;
        tvGo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GeneralFunctions.hideKeyboard(etSearch,context);
                if (list.size() != 0) {
                    if (mListener != null) {
                        mListener.onButtonClick(list);
                    }
                    dismiss();
                }
            }
        });

        if (list.size()!=0) {
            List<String> strings = new ArrayList<>();
            strings.addAll(list);
            refreshTag(strings);
        }
        setOnDismissListener(new OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                GeneralFunctions.hideKeyboard(etSearch,context);
            }
        });

    }

    @Override
    public void setOnKeyListener(OnKeyListener onKeyListener) {
        super.setOnKeyListener(onKeyListener);
    }

    private void setTag(String tagText) {

        final View view = LayoutInflater.from(context).inflate(R.layout.item_tag_dummy, null);
        final TextView tvTitle = view.findViewById(R.id.tvTitle);
        ImageView ivDelete = view.findViewById(R.id.ivDelete);
        list.add(tagText);
        tvTitle.setTag(list.size() - 1);
        tvTitle.setText(tagText);
        ivDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                list.remove((int) tvTitle.getTag());
                List<String> strings = new ArrayList<>();
                strings.addAll(list);
                refreshTag(strings);
            }


        });
      //  flowLayout.addView(view);

    }

    private void refreshTag(List<String> list1) {
       // flowLayout.removeAllViews();
        list.clear();
        for (int i = 0; i < list1.size(); i++) {
            final View view = LayoutInflater.from(context).inflate(R.layout.item_tag_dummy, null);
            final TextView tvTitle = view.findViewById(R.id.tvTitle);
            ImageView ivDelete = view.findViewById(R.id.ivDelete);
            list.add(list1.get(i));
            tvTitle.setTag(list.size() - 1);
            tvTitle.setText(list1.get(i));
            ivDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    list.remove((int) tvTitle.getTag());
                    List<String> strings = new ArrayList<>();
                    strings.addAll(list);
                    refreshTag(strings);
                }


            });
          //  flowLayout.addView(view);
        }
    }

    public interface OnOkClickListener {
        void onButtonClick(List<String> strings);
    }
}
