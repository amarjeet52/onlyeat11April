package com.codebrew.clikat.activities;

import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.codebrew.clikat.R;
import com.codebrew.clikat.databinding.ActivityNoInternetBinding;
import com.codebrew.clikat.utils.StaticFunction;
import com.codebrew.clikat.utils.configurations.Configurations;

/*
 * Created by cbl80 on 26/5/16.
 */
public class NoInternetActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ActivityNoInternetBinding binding= DataBindingUtil.setContentView(this, R.layout.activity_no_internet);
        binding.setColor(Configurations.colors);
        binding.setDrawables(Configurations.drawables);
        binding.setStrings(Configurations.strings);

        TextView bRetry= findViewById(R.id.bRetry);
        assert bRetry != null;
        bRetry.setOnClickListener(v -> {
            if (StaticFunction.INSTANCE.isInternetConnected(NoInternetActivity.this))
            {
                finish();
            }
        });
    }

    @Override
    public void onBackPressed() {

    }
    @Override
    protected void onStop() {
        super.onStop();
        onTrimMemory(TRIM_MEMORY_UI_HIDDEN);
    }
}
