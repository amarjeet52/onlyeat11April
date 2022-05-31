package com.codebrew.clikat.utils.customviews;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;

import androidx.core.graphics.drawable.DrawableCompat;
import androidx.appcompat.widget.AppCompatImageView;
import android.util.AttributeSet;

import com.codebrew.clikat.R;
import com.codebrew.clikat.utils.StaticFunction;
import com.codebrew.clikat.utils.configurations.Configurations;

public class ClikatImageView extends AppCompatImageView {

    public ClikatImageView(Context context) {
        super(context);
    }

    public ClikatImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setCustomisation(getType(attrs));
    }

    public ClikatImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setCustomisation(getType(attrs));
    }

    private void setCustomisation(String type) {
        switch (type) {
            case "0": {
                /* handle side navigation icons*/
               // setTint(Configurations.colors.navSubTextColor);
                setTint(Configurations.colors.appBackground);
                break;
            }
            case "1": {
                setTint(Configurations.colors.appBackground);
                break;
            }
            case "2": {
                /* handle toolbar icons*/
                break;
            }
            case "3":
            {
                /*handle product images*/
                break;
            }
            case "4":
            {
                setTint(Configurations.colors.textHead);
                break;
            }

            default: {
                setTint();
            }
        }
    }

    private void setTint() {
        if (StaticFunction.INSTANCE.isValidColorHex(Configurations.colors.primaryColor)) {
            DrawableCompat.setTint(getDrawable(), Color.parseColor(Configurations.colors.primaryColor));
        }
    }
    private void setTint(String color) {
        if (StaticFunction.INSTANCE.isValidColorHex(color)) {
            DrawableCompat.setTint(getDrawable(), Color.parseColor(color));
        }
    }

    private String getType(AttributeSet attrs) {
        try {
            TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.ClikatImageView);
            String type = a.getText(R.styleable.ClikatImageView_imageType).toString();
            a.recycle();
            return type;
        } catch (Exception e) {
            return "-1";
        }
    }

//    public void setCustomSrc(String src, Drawable, Drawable defaultDrawable) {
//        setBackgroundColor(Color.parseColor(src));
//    }

}
